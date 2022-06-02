package org.dymbols.tool;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogTests {

    private static final Logger
            LOGGER = LogManager.getLogger(LogTests.class);
    public static void main(String[] args) {
        LOGGER.info("test........");
    }


}
