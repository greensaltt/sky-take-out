package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加菜品和对应口味
     * @param dishDTO
     */
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        // 复制原有属性
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 插入一条菜品
        dishMapper.insert(dish);

        // 获取插入后得到的id值
        Long dishId = dish.getId();
        // 插入多条flavor
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if(dishFlavors != null && dishFlavors.size() > 0) {
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insert(dishFlavors);
        }
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     * @param dishs
     */
    @Transactional
    public void removeDish(List<Long> dishs) {
        // 校验是否在售
        for(Long dishId: dishs) {
            Dish dish = dishMapper.select(dishId);
            if(dish.getStatus() == StatusConstant.ENABLE || dish == null) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 校验是否在套餐中
        List<Long> setMealId = setmealMapper.getIdByDish(dishs);
        if(setMealId != null && setMealId.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        for(Long dishId: dishs) {
            // 删除
            dishMapper.delete(dishId);

            // 删除对应口味
            dishFlavorMapper.delete(dishId);
        }
    }
}
