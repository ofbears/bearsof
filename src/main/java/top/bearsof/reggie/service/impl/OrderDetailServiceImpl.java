package top.bearsof.reggie.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.bearsof.reggie.entity.OrderDetail;
import top.bearsof.reggie.mapper.OrderDetailMapper;
import top.bearsof.reggie.service.OrderDetailService;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}