package com.mysql.async.handler;

import com.mysql.async.EventHander;
import com.mysql.async.EventModel;
import com.mysql.async.EventType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class LikeHandler implements EventHander {

    @Override
    public void doHandle(EventModel model) {
        System.out.println("Liked");
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
