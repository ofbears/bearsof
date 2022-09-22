package top.bearsof.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.bearsof.reggie.dto.DishDto;
import top.bearsof.reggie.entity.Category;
import top.bearsof.reggie.entity.Dish;
import top.bearsof.reggie.entity.DishFlavor;
import top.bearsof.reggie.mapper.DishMapper;
import top.bearsof.reggie.service.CategoryService;
import top.bearsof.reggie.service.DishFlavorService;
import top.bearsof.reggie.service.DishService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);  //返回id
        Long id = dishDto.getId();
        List<DishFlavor> dishFlavorList = dishDto.getFlavors();
        dishFlavorList = dishFlavorList.stream().map((dishFlavor) ->{
                dishFlavor.setDishId(id);
                return dishFlavor;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(dishFlavorList);
    }

    @Override
    public DishDto selectWithFlavor(Long id) {
//        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(Dish::getId,id);
        Dish dish = super.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Category category = categoryService.getById(dish.getCategoryId());
        dishDto.setCategoryName(category.getName());
        lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(list);
        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto,dish);
        //更新菜品信息
        super.updateById(dish);
        //清理掉旧的数据
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
