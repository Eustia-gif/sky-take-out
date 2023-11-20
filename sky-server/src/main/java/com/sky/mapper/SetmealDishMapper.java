package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品ip查询套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealDishIds(List<Long>dishIds);
}
