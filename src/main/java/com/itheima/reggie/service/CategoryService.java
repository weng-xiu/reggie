package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

/**
 * 菜品分类Service接口
 */
public interface CategoryService extends IService<Category> {

    public void remove(Long id);

}
