package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;
//业务层接口CategoryService
public interface CategoryService extends IService<Category> {

    public void remove(Long id);

}
