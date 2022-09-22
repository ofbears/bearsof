package top.bearsof.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;
import top.bearsof.reggie.entity.Category;

@Transactional(rollbackFor = Exception.class)
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
