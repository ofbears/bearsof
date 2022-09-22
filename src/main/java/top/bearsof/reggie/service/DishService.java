package top.bearsof.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.bearsof.reggie.dto.DishDto;
import top.bearsof.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);

    //多表查询
    public DishDto selectWithFlavor(Long id);


    //多表更新

    public void updateWithFlavor(DishDto dishDto);
}
