package top.bearsof.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.bearsof.reggie.entity.Setmeal;
import top.bearsof.reggie.mapper.SetMealMapper;
import top.bearsof.reggie.service.SetMealService;

@Service
@Slf4j
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
}
