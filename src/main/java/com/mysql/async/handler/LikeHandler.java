package com.mysql.async.handler;

import com.mysql.async.EventHander;
import com.mysql.async.EventModel;
import com.mysql.async.EventType;
import com.mysql.model.Message;
import com.mysql.model.User;
import com.mysql.service.MessageService;
import com.mysql.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHander {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        //System.out.println("Liked");
        Message message = new Message();
        message.setFromId(model.getActorId());
        message.setToId(model.getEntityOwnerId());
        User user = userService.getUser(model.getActorId());
        message.setContent("用户" + user.getName()
                + "赞了你的资讯，http://127.0.0.1:8080/news/" + String.valueOf(model.getEntityId()));
        message.setCreatedDate(new Date());
        int fromId = message.getFromId();
        int toId = message.getToId();
        message.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) :
                String.format("%d_%d", toId, fromId));
        //message.setConversationId();
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
