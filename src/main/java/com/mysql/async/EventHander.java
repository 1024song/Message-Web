package com.mysql.async;

import java.util.List;

public interface EventHander {
    void doHandle(EventModel model);

    List<EventType> getSupportEventTypes();
}
