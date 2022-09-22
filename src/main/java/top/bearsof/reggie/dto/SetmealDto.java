package top.bearsof.reggie.dto;

import lombok.Data;
import top.bearsof.reggie.entity.Setmeal;
import top.bearsof.reggie.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
