package com.mysql;

import com.mysql.model.User;
import com.mysql.util.JedisAdapter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)
public class JedisTest {

    @Autowired
    JedisAdapter jedisAdapter;

    @Test
    public void testObject(){
        User user = new User();
        user.setHeadUrl("http://images.nowcoder.com/head/100t.png");
        user.setName("user1");
        user.setPassword("abc");
        user.setSalt("def");

        jedisAdapter.setObject("user111",user);

        User u = jedisAdapter.getObject("user111",User.class);
        System.out.println(ToStringBuilder.reflectionToString(u));
    }
}
