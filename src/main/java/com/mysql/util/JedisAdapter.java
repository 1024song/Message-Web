package com.mysql.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class JedisAdapter implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool = null;
    private Jedis jedis = null;

    public static void print(int index,Object obj){
        System.out.println(String.format(("%d,%s"),index,obj.toString()));
    }
    public static void main(String []argv){
        Jedis jedis = new Jedis();
        jedis.flushAll();

        jedis.set("hello","world");
        print(1,jedis.get("hello"));

        //jedis.rename("hello","newhello");
        //print(1,jedis.get("newhello"));
        //jedis.setex("hello2",15,"world2");
        //jedis.set("pv","100");

        jedis.incrBy("pv",5);
        print(2,jedis.get("pv"));

        //列表操作
        String listName = "list";
        for(int i = 0;i < 10;++i){
            jedis.lpush(listName,"a" + String.valueOf(i));
        }
        print(3,jedis.lrange(listName,0,12));

        print(4,jedis.llen(listName));

        print(5,jedis.lpop(listName));

        print(6,jedis.llen(listName));

        print(7,jedis.lindex(listName,3));

        print(8,jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER,"a4","xx"));

       // print(8,jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE,"a4","xx"));

        print(9,jedis.lrange(listName,0,12));

        String userKey = "userxx";
        jedis.hset(userKey,"name","jim");
        jedis.hset(userKey,"age","12");
        jedis.hset(userKey,"phone","1234455");

        print(10,jedis.hgetAll(userKey));
        jedis.hdel(userKey,"phone");
        print(11,jedis.hgetAll(userKey));

        jedis.hsetnx(userKey,"school","asdasd");
        jedis.hsetnx(userKey,"name","song");
        print(12,jedis.hgetAll(userKey));

        String likeKeys1 = "newsLike1";
        String likeKeys2 = "newsLike2";
        for(int i = 0;i < 10;++i){
            jedis.sadd(likeKeys1,String.valueOf(i));
            jedis.sadd(likeKeys2,String.valueOf(2*i));
        }
        print(13,jedis.smembers(likeKeys1));
        print(14,jedis.smembers(likeKeys2));
        print(15,jedis.sinter(likeKeys1,likeKeys2));
        print(16,jedis.sunion(likeKeys1,likeKeys2));
        print(17,jedis.sdiff(likeKeys1,likeKeys2));
        jedis.srem(likeKeys1,"5");
        print(18,jedis.smembers(likeKeys1));
        print(19,jedis.sismember(likeKeys1,"5"));

        print(20,jedis.scard(likeKeys1));


        String rankKey = "rankKey";
        jedis.zadd(rankKey,15,"jim");
        jedis.zadd(rankKey,60,"ben");
        jedis.zadd(rankKey,95,"lee");
        jedis.zadd(rankKey,85,"km");
        jedis.zadd(rankKey,74,"hg");
        print(21,jedis.zcard(rankKey));
        print(22,jedis.zcount(rankKey,61,100));
        print(23,jedis.zscore(rankKey,"lee"));


        print(24,jedis.zrange(rankKey,1,3));

        /*JedisPool pool = new JedisPool();
        for(int i = 0;i < 16;++i){
            Jedis j = pool.getResource();
            j.get("a");
            System.out.println("pool" + i);
            j.close();
        }*/
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost",6379);
    }

    private Jedis getJedis(){
        return pool.getResource();
    }

    public long sadd(String key,String value){
         jedis = null;
         try{
            jedis = pool.getResource();
            return jedis.sadd(key,value);
         }catch(Exception e){
             logger.error("发生异常" + e.getMessage());
             return 0;
         }finally {
             if(jedis != null){
                 jedis.close();
             }
         }
    }

    public long srem(String key,String value){
        jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.srem(key,value);
        }catch(Exception e){
            logger.error("发生异常" + e.getMessage());
            return 0;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public boolean sismember(String key,String value){
        jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key,value);
        }catch(Exception e){
            logger.error("发生异常" + e.getMessage());
            return false;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public long scard(String key){
        jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.scard(key);
        }catch(Exception e){
            logger.error("发生异常" + e.getMessage());
            return 0;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }
}
