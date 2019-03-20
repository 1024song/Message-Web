package com.mysql.async;

import com.alibaba.fastjson.JSONObject;
import com.mysql.util.JedisAdapter;
import com.mysql.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    private static final Logger logger = LoggerFactory.getLogger(EventProducer.class);
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel model){
        try {
            String json = JSONObject.toJSONString(model);
            String key = RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key, json);
            return true;
        }catch (Exception e){
            logger.error("发生异常" + e.getMessage());
            return false;
        }
    }
}
