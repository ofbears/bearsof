package top.bearsof.reggie.dto;

import lombok.Data;
import top.bearsof.reggie.entity.Dish;
import top.bearsof.reggie.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
