package com.sky.controller.admin;


import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Api(tags = "菜品相关接口")
@RequestMapping("/admin/dish")
@Slf4j
public class DishController {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DishService dishService;

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

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

        // 删除对应分类的redis
        String pattern = "dish_" + dishDTO.getCategoryId();
        cleanCache(pattern);
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
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除菜品接口")
    public Result remove(@RequestParam List<Long> ids) {
        log.info("删除菜品:{}", ids);
        dishService.removeDish(ids);

        String pattern = "dish_*";
        cleanCache(pattern);

        return Result.success();
    }

    /**
     * 查询菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("查询菜品接口")
    public Result<DishVO> getById(@PathVariable Long id) {
        log.info("正在查询菜品:{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品接口")
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("正在修改菜品:{}", dishDTO);
        dishService.updateWithFlavors(dishDTO);

        String pattern = "dish_*";
        cleanCache(pattern);

        return Result.success();
    }

    /**
     * 修改菜品状态
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("设置菜品状态接口")
    public Result setStatus(@PathVariable Integer status, Long id) {
        log.info("修改菜品 {} 状态为：{}", id,
                status == StatusConstant.ENABLE? "起售": "停售");

        dishService.StartOrStop(status, id);

        cleanCache("dish_*");
        return Result.success();
    }

    private void cleanCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }

}
