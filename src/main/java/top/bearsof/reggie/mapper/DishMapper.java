package top.bearsof.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.bearsof.reggie.entity.Dish;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
