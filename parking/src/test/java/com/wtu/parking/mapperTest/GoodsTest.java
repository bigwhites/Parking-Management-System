package com.wtu.parking.mapperTest;


import com.wtu.parking.entity.Goods;
import com.wtu.parking.mapper.GoodsMapper;
import com.wtu.parking.mapper.GoodsTradeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GoodsTest {

    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    GoodsTradeMapper goodsTradeMapper;
    @Test
    void t(){
        Goods goods = goodsMapper.findBygId(30001);
        System.out.println(goods.toString());
        goods.setGInfo("好东西");
        goodsMapper.update(goods);
        goods = goodsMapper.findBygId(30001);
        System.out.println(goods.toString());
    }

    @Test
    void deleteByID(){
        System.out.println(goodsMapper.deleteById(30001));
    }

    @Test
    void findById(){
        System.out.println(goodsTradeMapper.getByTrId(10001).toString());
        System.out.println(goodsTradeMapper.getByUId(202301));
    }
}
