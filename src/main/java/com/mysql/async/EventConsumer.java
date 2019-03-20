package com.mysql.async;

import com.alibaba.fastjson.JSON;
import com.mysql.util.JedisAdapter;
import com.mysql.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventConsumer implements InitializingBean , ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private ApplicationContext applicationContext;
    private Map<EventType, List<EventHander>> config = new HashMap<>();

    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String,EventHander> beans = applicationContext.getBeansOfType(EventHander.class);
        if(beans != null){
            for(Map.Entry<String,EventHander> entry : beans.entrySet()){
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                for(EventType type : eventTypes){
                    if(!config.containsKey(type)){
                        config.put(type,new ArrayList<EventHander>());
                    }
                    //注册每个事件的处理函数
                    config.get(type).add(entry.getValue());
                }
            }
        }

        //启动线程去消费事件
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //从队列一直消费
                while(true){
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> messages = jedisAdapter.brpop(0,key);
                    //第一个元素是队列名字
                    for(String message : messages){
                        if(message.equals(key)){
                            continue;
                        }

                        EventModel eventModel = JSON.parseObject(message,EventModel.class);
                        //找到这个事件的处理handler列表
                        if(!config.containsKey(eventModel.getType())){
                            logger.error("不能识别的事件");
                            continue;
                        }
                        for(EventHander hander : config.get(eventModel.getType())){
                            hander.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
