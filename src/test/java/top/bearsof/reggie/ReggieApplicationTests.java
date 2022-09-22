package top.bearsof.reggie;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.*;
import java.util.concurrent.TimeUnit;


@SpringBootTest
@Slf4j
class ReggieApplicationTests {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void contextLoad() {
        ValueOperations valueOperations = stringRedisTemplate.opsForValue();

        valueOperations.set("yyds", 123, 10L, TimeUnit.SECONDS);

        valueOperations.setIfAbsent("city123", "city123", 20L, TimeUnit.SECONDS);
    }

    @Test
    public void operationHash() {
        HashOperations hashOperations = stringRedisTemplate.opsForHash();
        Map<String, String> map = new HashMap<>();
        map.put("username", "admin");
        map.put("password", "admin");
        hashOperations.putAll("user", map);
        System.out.println(hashOperations.get("user", "username"));
        hashOperations.delete("user", "username", "password");
        Set user = hashOperations.keys("user");
        for (Object o : user) {
            System.out.println(o);
        }
        stringRedisTemplate.delete("user");
    }

    @Test
    public void operationList() {
        ListOperations listOperations = stringRedisTemplate.opsForList();
        listOperations.leftPush("mylist", "a");
        listOperations.leftPushAll("mylist", "b", "c", "d");
        List<String> mylist = listOperations.range("mylist", 0, -1);
        for (Object o : mylist) {
            System.out.println(o);
        }
    }

    @Test
    public void operationSet() {
        SetOperations setOperations = stringRedisTemplate.opsForSet();
        setOperations.add("mySet", 20, 30, 40, 50, 60);
        Set<Integer> mySet = setOperations.members("mySet");
        for (Integer o : mySet) {
            System.out.println(o);
            setOperations.remove("mySet", o);
        }

        mySet = setOperations.members("mySet");
        for (Integer o : mySet) {
            System.out.println(o);
        }
    }

    @Test
    public void operationZSet() {
        ZSetOperations zSetOperations = stringRedisTemplate.opsForZSet();
        zSetOperations.add("admin", "你看起来好像很好吃", 9.9f);   //优先保住小的
        zSetOperations.add("admin", "你看起来好像很好吃", 2.2f);
        zSetOperations.add("admin", "132456879", 3.3);
        Set<String> admin = zSetOperations.range("admin", 0, -1);
        for (String o : admin) {
            System.out.println(o);
        }
        zSetOperations.incrementScore("admin", "你看起来好像很好吃", 12.2f);
        admin = zSetOperations.range("admin", 0, -1);
        for (String o : admin) {
            System.out.println(o);
            zSetOperations.remove("admin",o);
        }
        stringRedisTemplate.delete("admin");
    }


}
