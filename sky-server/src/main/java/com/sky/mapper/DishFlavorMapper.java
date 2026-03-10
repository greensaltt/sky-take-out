package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入dishFalvor
     * @param dishFlavors
     */
    void insert(List<DishFlavor> dishFlavors);

    /**
     * 根据dishid删除口味
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void delete(Long dishId);
}
