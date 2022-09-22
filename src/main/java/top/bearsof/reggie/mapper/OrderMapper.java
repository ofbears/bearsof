package top.bearsof.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.bearsof.reggie.entity.Orders;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
