package com.wxn.miaosha;

import com.wxn.miaosha.dao.ItemDOMapper;
import com.wxn.miaosha.dao.PromoDOMapper;
import com.wxn.miaosha.dao.UserDOMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MiaoshaApplicationTests {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private PromoDOMapper promoDOMapper;

    @Test
    void contextLoads() {
        System.out.println(promoDOMapper.selectByItemId(3));
    }

}
