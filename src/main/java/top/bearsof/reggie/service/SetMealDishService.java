package top.bearsof.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.bearsof.reggie.common.R;
import top.bearsof.reggie.dto.SetmealDto;
import top.bearsof.reggie.entity.Setmeal;
import top.bearsof.reggie.entity.SetmealDish;

public interface SetMealDishService extends IService<SetmealDish> {
    public void saveSetMeal(SetmealDto setmealDto);
}
