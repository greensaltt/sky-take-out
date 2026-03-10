package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "菜品相关接口")
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 添加菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("添加菜品接口")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("添加菜品:{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询菜品接口")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询菜品:{}", dishPageQueryDTO);
        PageResult result = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(result);
    }

    /**
     * 删除菜品
     * @param dishs
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除菜品接口")
    public Result remove(@RequestParam List<Long> ids) {
        log.info("删除菜品:{}", ids);
        dishService.removeDish(ids);
        return Result.success();
    }
}
