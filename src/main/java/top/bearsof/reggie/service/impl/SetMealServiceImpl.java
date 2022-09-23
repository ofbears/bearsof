package top.bearsof.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.bearsof.reggie.common.GlobalException;
import top.bearsof.reggie.entity.Setmeal;
import top.bearsof.reggie.entity.SetmealDish;
import top.bearsof.reggie.mapper.SetMealMapper;
import top.bearsof.reggie.service.SetMealDishService;
import top.bearsof.reggie.service.SetMealService;

@Service
@Slf4j
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
}
