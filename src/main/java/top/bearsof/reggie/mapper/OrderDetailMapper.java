package top.bearsof.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.bearsof.reggie.entity.OrderDetail;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
