package top.bearsof.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.bearsof.reggie.entity.ShoppingCart;
import top.bearsof.reggie.mapper.ShoppingCartMapper;
import top.bearsof.reggie.service.ShoppingCartService;
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
