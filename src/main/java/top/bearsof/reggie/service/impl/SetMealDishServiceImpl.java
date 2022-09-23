package top.bearsof.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.bearsof.reggie.common.GlobalException;
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

    /**
     * 使用事务监听处理结果，同更新同失败   启用异常处理机制
     * @param ids  SetMeal对应的ID
     */
    @Override
    @Transactional
    public void deleteSetMealWithSetMealDish(Long ids) {
        try {
            LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(ids!=null,SetmealDish::getSetmealId,ids);
            //删除setMealDishService中的菜品
            this.removeBatchByIds(this.list(lambdaQueryWrapper));
            setMealService.removeById(ids);
        } catch (Exception e) {
            throw new GlobalException("删除失败，未知异常");
        }
    }
}
