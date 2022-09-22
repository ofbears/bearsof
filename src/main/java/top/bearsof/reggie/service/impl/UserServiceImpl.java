package top.bearsof.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.bearsof.reggie.entity.User;
import top.bearsof.reggie.mapper.UserMapper;
import top.bearsof.reggie.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
