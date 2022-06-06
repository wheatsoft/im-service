package org.dymbols.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ShardThreadPoolExecutor extends AbstractExecutorService {
    private final ReentrantLock mainLock = new ReentrantLock();
    private final Condition termination = mainLock.newCondition();

    private final Condition shutdown = mainLock.newCondition();

    private Class clazz = ArrayBlockingQueue.class;

    private Integer poolSize = 20;//Runtime.getRuntime().availableProcessors();
    private String poolName = "shard-thread-pool-executor";
    private List<BlockingQueue> commanders;
    private Map<Integer, Work> workers = new ConcurrentHashMap<>();
    private Map<Work, BlockingQueue> workQueueMap = new ConcurrentHashMap<>();
    private boolean shutDown;
    private boolean shutDownNow;

    private Integer queueSize = 200;

    public ShardThreadPoolExecutor(int poolSize, String poolName, Class clazz,

                                   Integer queueSize) {

        if (poolSize < 0 || poolName == null || poolName.trim() == "" || clazz == null || queueSize == null || queueSize < 0) {

            throw new IllegalArgumentException();

        }
        this.poolSize = poolSize;
        this.poolName = poolName;
        this.clazz = clazz;
        initCommanders();
    }

    public ShardThreadPoolExecutor() {
        initCommanders();
    }

    private void initCommanders() {

        commanders = new ArrayList<>(poolSize);

        for (int i = 0; i < poolSize; i++) {

            try {
                commanders.add((BlockingQueue) clazz.getConstructor(Integer.TYPE).newInstance(queueSize));
            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException(e);

            }
        }

    }

    @Override
    public void shutdown() {
        throw new RuntimeException("shutdown not supported");
    }

    @Override
    public List shutdownNow() {

        shutDownNow = true;

        shutDown = true;

        for (Work work : workers.values()) {

            work.thread.interrupt();

        }

        return new ArrayList(workers.values());

    }


    @Override
    public boolean isShutdown() {

        return shutDown;

    }

    @Override
    public boolean isTerminated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {

        long nanos = unit.toNanos(timeout);
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();

        try {

            for (; ; ) {
                if (workers.size() == 0) {
                    return true;
                }
                if (nanos <= 0) {
                    return false;
                }
                nanos = termination.awaitNanos(nanos);
            }

        } finally {

            mainLock.unlock();

        }

    }

    @Override
    public void execute(Runnable command) {

        if (!(command instanceof Command)) throw new IllegalArgumentException("param must be instance of ShardThreadPoolExecutor Command");

        Command cmd = ((Command) command);
        Object shardObject = cmd.getShardKey();

        if (shardObject == null) throw new IllegalStateException("shardObject is null");

        if (poolSize > workers.size() && !hasWorker(cmd)) {
            newWork(cmd);
        } else {
            try {
                getCommandQueue(cmd).put(cmd);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }

    }

    private boolean hasWorker(Command command) {
        return workers.containsKey(getBlockQueueIndex(command));
    }

    private BlockingQueue getCommandQueue(Command command) {

        return commanders.get(getBlockQueueIndex(command));

    }

    private Integer getBlockQueueIndex(Command command) {

        int hashCode = command.getShardKey().hashCode();

        // can do better
        return Math.abs(hashCode % poolSize);

    }

    private void newWork(Command command) {

        Work work = new Work(command);

        work.thread.start();

        workQueueMap.put(work, getCommandQueue(command));

        workers.put(getBlockQueueIndex(command), work);

    }

    private Command getCommand(BlockingQueue commandDeque) {

        if (shutDownNow) {

            return null;

        }

        try {

            return (Command) commandDeque.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;

        }

    }

    private void removeWork(Work work) {

        if (isShutdown()) {

            Integer key = null;

            for (Map.Entry<Integer, Work> workEntry : workers.entrySet()) {
                if (work == workEntry.getValue()) {
                    key = workEntry.getKey();
                }
            }
            workers.remove(key);
        }

    }

    public interface Command extends Runnable {

        String getShardKey();

    }

    private class Work extends Thread {

        private Command command;

        private Thread thread;

        private int state = 0;

        public Work(Command command) {

            thread = new Thread(this::run, poolName + "-" + workers.size());

            this.command = command;

        }

        @Override
        public void run() {
            execute(command);
        }

        private void execute(Command command) {

            BlockingQueue commandDeque = getCommandQueue(command);

            Command cmd = command;

            while (cmd != null || (cmd = getCommand(commandDeque)) != null) {

                try {
                    state = 0;
                    cmd.run();
                    state = 1;
                } finally {
                    cmd = null;
                }

            }

            removeWork(this);

        }

    }

}





