package top.bearsof.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.bearsof.reggie.common.GlobalException;
import top.bearsof.reggie.common.R;
import top.bearsof.reggie.dto.DishDto;
import top.bearsof.reggie.dto.SetmealDto;
import top.bearsof.reggie.entity.Category;
import top.bearsof.reggie.entity.Setmeal;
import top.bearsof.reggie.entity.SetmealDish;
import top.bearsof.reggie.service.CategoryService;
import top.bearsof.reggie.service.SetMealDishService;
import top.bearsof.reggie.service.SetMealService;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {
    @Autowired
    private SetMealService setMealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetMealDishService setMealDishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增套餐功能
     * @param setMealDto 使用数据传输类替代 SetMeal和SetMealDish
     * @return 返回操作结果
     */
    @Cacheable(value = "setmealCache",key = "")
    @PostMapping
    public R<String> saveSetMeal(@RequestBody SetmealDto setMealDto){
        setMealDishService.saveSetMeal(setMealDto);
        return R.success("添加套餐成功");
    }

    /**
     * 套餐分页
     * @param page 当前页码
     * @param pageSize 当前页面的数据容纳最大数量
     * @return 返回SetMeal的套餐链表
     */
    @GetMapping("/page")
    public R<Page> selectSetMeal(Integer page, Integer pageSize){
        //创建分页构造器
        Page<Setmeal> pages = new Page<>(page,pageSize);
        //创建筛选构造器
        //LambdaQueryWrapper<SetMeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealService.page(pages);
        Page<SetmealDto> setMealDtoPage = new Page<>();
        BeanUtils.copyProperties(pages,setMealDtoPage,"records");
        List<Setmeal> records = pages.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            setmealDto.setCategoryName(categoryService.getById(item.getCategoryId()).getName());
            return setmealDto;
        }).collect(Collectors.toList());
        setMealDtoPage.setRecords(list);
        return R.success(setMealDtoPage);
    }


    /**
     * 获取指定套餐Id的内容
     * @return 返回套餐的菜信息
     */
    //@Cacheable(value = "setmealCache",key = "#setmeal.categoryId + '_' + #setmeal.status")
    @GetMapping("/list")
    public R<List<Setmeal>> getSetMeal(Setmeal setmeal){
        //log.info(categoryId.toString());
        //log.info(status.toString());
        //获取正常启用的套餐
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lambdaQueryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> setMealList = setMealService.list(lambdaQueryWrapper);
        return R.success(setMealList);
    }

    @GetMapping("/dish/{SetMealId}")
    public R<List<SetmealDish>> getSetMealDish(@PathVariable Long SetMealId){
        LambdaQueryWrapper<SetmealDish> setMealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealDishLambdaQueryWrapper.eq(SetMealId!=null,SetmealDish::getSetmealId,SetMealId);
        List<SetmealDish> setMealDishList = setMealDishService.list(setMealDishLambdaQueryWrapper);
        if(setMealDishList!=null){
            return R.success(setMealDishList);
        }else {
            throw new GlobalException("套餐为空");
        }
    }


    /**
     * 修改删除过套餐后，同时清理掉redis中的缓存
     * @param ids
     * @return
     */
    @CacheEvict(value = "setmealCache",allEntries = true)
    @DeleteMapping
    public R<String> deleteSetMeal(Long ids){
        setMealDishService.deleteSetMealWithSetMealDish(ids);
        return R.success("删除成功");
    }
}
