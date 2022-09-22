package top.bearsof.reggie.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.bearsof.reggie.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
