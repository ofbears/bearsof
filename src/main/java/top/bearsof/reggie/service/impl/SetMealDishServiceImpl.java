package top.bearsof.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.bearsof.reggie.dto.SetmealDto;
import top.bearsof.reggie.entity.Setmeal;
import top.bearsof.reggie.entity.SetmealDish;
import top.bearsof.reggie.mapper.SetMealDishMapper;
import top.bearsof.reggie.service.SetMealDishService;
import top.bearsof.reggie.service.SetMealService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetMealDishServiceImpl extends ServiceImpl<SetMealDishMapper, SetmealDish> implements SetMealDishService {
    @Autowired
    private SetMealService setMealService;
    @Override
    public void saveSetMeal(SetmealDto setmealDto) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto,setmeal);
        setMealService.save(setmeal);
        List<SetmealDish> setMealDishes = setmealDto.getSetmealDishes();
        setMealDishes = setMealDishes.stream().map((item)->{
            item.setSetmealId(setmeal.getId());
            return item;
        }).collect(Collectors.toList());
        super.saveBatch(setMealDishes);
    }
}
