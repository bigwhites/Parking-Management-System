package com.wtu.parking.mapper;
import com.wtu.parking.entity.Goods;
import java.util.List;

public interface GoodsMapper {
    Goods findBygId(int gId);

    int insert(Goods goods);

    int update(Goods goods);
    int deleteById(int gId);

    int getCount();

    List<Goods> getByPage(int limit,int offset);

}
