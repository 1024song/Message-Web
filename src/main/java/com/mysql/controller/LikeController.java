package com.mysql.controller;

import com.mysql.async.EventHander;
import com.mysql.async.EventModel;
import com.mysql.async.EventProducer;
import com.mysql.async.EventType;
import com.mysql.model.EntityType;
import com.mysql.model.HostHolder;
import com.mysql.model.News;
import com.mysql.service.LikeService;
import com.mysql.service.NewsService;
import com.mysql.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"},method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String like(Model model, @RequestParam("newsId") int newsId){
        int userId = hostHolder.getUser().getId();

        long likeCount = likeService.like(userId, EntityType.ENTITY_NEWS,newsId);
        News news = newsService.getById(newsId);
        newsService.updateLikeCount(newsId,(int)likeCount);

        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                .setActorId(hostHolder.getUser().getId()).setEntityId(newsId)
                .setEntityType(EntityType.ENTITY_NEWS).setEntityOwnerId(news.getUserId()));

        return ToutiaoUtil.getJSONString(0,String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"},method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String dislike(Model model, @RequestParam("newsId") int newsId){
        int userId = hostHolder.getUser().getId();

        long likeCount = likeService.disLike(userId, EntityType.ENTITY_NEWS,newsId);
        newsService.updateLikeCount(newsId,(int)likeCount);

        return ToutiaoUtil.getJSONString(0,String.valueOf(likeCount));
    }
}
