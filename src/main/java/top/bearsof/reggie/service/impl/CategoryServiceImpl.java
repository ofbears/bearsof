package top.bearsof.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.bearsof.reggie.entity.Category;
import top.bearsof.reggie.mapper.CategoryMapper;
import top.bearsof.reggie.service.CategoryService;


/**
 * @author bears
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
