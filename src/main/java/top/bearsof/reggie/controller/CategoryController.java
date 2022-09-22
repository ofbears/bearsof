package top.bearsof.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.bearsof.reggie.common.GlobalException;
import top.bearsof.reggie.common.R;
import top.bearsof.reggie.entity.Category;
import top.bearsof.reggie.entity.Dish;
import top.bearsof.reggie.entity.Setmeal;
import top.bearsof.reggie.service.CategoryService;
import top.bearsof.reggie.service.DishService;
import top.bearsof.reggie.service.SetMealService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetMealService setMealService;
    @GetMapping("/page")
    public R<Page<Category>> page(Integer page, Integer pageSize){
        //分页构造器
        Page<Category> pages = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pages,lambdaQueryWrapper);
        return R.success(pages);
    }
    @PostMapping
    public R<String> addDish(@RequestBody Category category){
        categoryService.save(category);
        return R.success("添加成功");
    }
    @DeleteMapping
    public R<String> delete(Long ids){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        long count1 = dishService.count(lambdaQueryWrapper);
        if(count1>0){
            throw new GlobalException("删除失败，该菜系还关联菜品");
        }
        LambdaQueryWrapper<Setmeal> setMealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);
        long count2 = setMealService.count(setMealLambdaQueryWrapper);
        if(count2>0){
            throw new GlobalException("删除失败，当前分类下关联了套餐");
        }
        categoryService.removeById(ids);
        return R.success("删除成功");
    }
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息:{}",category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    /**
     * 用于添加菜品界面回显菜品分类
     */

    // TODO
    @GetMapping("/list")
    public R<List<Category>> select(Category category){
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(lambdaQueryWrapper);
        return R.success(list);
    }
}
