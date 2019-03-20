package com.mysql.service;

import com.mysql.dao.LoginTicketDAO;
import com.mysql.dao.UserDao;
import com.mysql.model.LoginTicket;
import com.mysql.model.User;
import com.mysql.util.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public Map<String,Object> resgister(String username,String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        if(StringUtils.isBlank(username)){
            map.put("msgname","用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("msgpwd","密码不能为空");
            return map;
        }

        User user = userDao.selectByName(username);

        if(user != null){
            map.put("msgname","用户名已经被注册");
            return map;
        }

        //密码强度
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setPassword(ToutiaoUtil.MD5(password + user.getSalt()));
        user.setHeadUrl(head);
        userDao.addUser(user);

        //登陆
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    public Map<String,Object> login(String username,String password){
        Map<String,Object> map = new HashMap<String, Object>();

        if(StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("msgpwd","密码不能为空");
            return map;

        }

        User user = userDao.selectByName(username);

        if(user == null){
            map.put("msg","用户名不存在");
            return map;
        }

        if(!ToutiaoUtil.MD5(password +user.getSalt()).equals(user.getPassword())){
            map.put("msgpwd","密码不正确");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);

        return map;
    }

    private String addLoginTicket(int userId){
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }

    public void addUser(User user){
        userDao.addUser(user);
    }

    public User getUser(int id){
        return userDao.selectById(id);
    }

    public User getUser(String username){
        return userDao.selectByName(username);
    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }
}
