package org.dymbols.biz.service.client;

import com.alibaba.fastjson.JSONObject;
import org.dymbols.biz.service.client.model.ExecuteReq;
import org.dymbols.biz.service.client.model.ExecuteRes;

public interface CommonService {

    JSONObject execute(JSONObject req) ;

    JSONObject execute2(JSONObject req);

}
