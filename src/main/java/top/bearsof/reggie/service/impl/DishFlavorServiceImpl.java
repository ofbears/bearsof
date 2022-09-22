package top.bearsof.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.bearsof.reggie.entity.DishFlavor;
import top.bearsof.reggie.mapper.DishFlavorMapper;
import top.bearsof.reggie.service.DishFlavorService;
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
