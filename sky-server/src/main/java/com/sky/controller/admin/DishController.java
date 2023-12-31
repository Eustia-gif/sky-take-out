package com.sky.controller.admin;

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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate  redisTemplate;
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        //清理缓存数据
        cleanCache("dish_*");
        return Result.success();
    }
    @GetMapping("/page")
    @ApiOperation(value = "分页查询菜品")
    public  Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){//请求参数是query的方式通过？来传参，不是json格式，不需要注解
        log.info("分页查询菜品：{}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }
    @DeleteMapping
    @ApiOperation(value = "菜品的批量删除")
    public Result delete(@RequestParam List<Long> ids){// @RequestParam能够接收多个参数，由springmvc动态的解析出来，传入的参数实例1,2,3

        log.info("删除菜品：{}",ids);
        dishService.deleteBatch(ids);
        cleanCache("dish_*");
        return Result.success();
    }
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品：{}",id);
        DishVO dishVO= dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }
    @PutMapping
    @ApiOperation(value = "菜品修改")
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.updateWithFlavor(dishDTO);
        cleanCache("dish_*");
        return Result.success();
    }
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        dishService.startOrStop(status, id);

        cleanCache("dish_*");

        return Result.success();
    }
    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId) {
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 清理缓存
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set key=redisTemplate.keys(pattern);
        redisTemplate.delete(key);
    }
}
