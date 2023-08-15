package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 购物车Mapper接口
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

}
