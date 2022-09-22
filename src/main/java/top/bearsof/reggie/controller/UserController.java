package top.bearsof.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import top.bearsof.reggie.common.*;
import top.bearsof.reggie.entity.AddressBook;
import top.bearsof.reggie.entity.User;
import top.bearsof.reggie.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private SMSUtil smsUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    /**
     * 用户自主获取登录验证码
     * @return  返回获取的状态
     */
    @GetMapping("/login")
    public R<String> getCode(String phone, HttpServletRequest httpServletRequest){
        log.info(phone);
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        log.info("验证码为:{}",code);
        smsUtil.sendMailMessage(phone,"注册验证码","您的验证码为" + code);
        //设置对话访问记录   记录时长20
        //httpServletRequest.getSession().setAttribute(phone,code);
        //redis缓存验证码
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(phone,code,5, TimeUnit.MINUTES);
        return R.success("验证码发送成功");
    }



    /**
     * 用户前端登录
     * @return  返回用户信息
     */
    @PostMapping("/login")
    public R<User> userLogin(@RequestBody Map map,HttpServletRequest httpServletRequest){
        log.info(map.toString());
        String phone = (String) map.get("phone");
        String code = (String) map.get("code");
        //System.out.println(phone);
        //System.out.println(code);
        log.info("用户的邮箱为:{}",phone);
        log.info("用户当前输入的验证码:{}",code);
        String codeInSession = redisTemplate.opsForValue().get(phone).toString();
        log.info("系统生成的验证码为:{}",codeInSession);
        if(codeInSession!=null&& code.equals(codeInSession)){
            //表示一切正常进入注册程序
            //需要判断用户是否是老用户
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(lambdaQueryWrapper);
            if (user==null){
                //证明这是一个新用户  需要优先注册再进入程序  采用用户无感注册形式
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);   //1表示启用   0表示禁用
                userService.save(user);
            }
            httpServletRequest.getSession().setAttribute("user",user.getId());

            //登录成功删除redis中的验证码key
            redisTemplate.delete(phone);
            return R.success(user);
        }else {
            throw new GlobalException("未知错误");
        }
    }

    /**
     *
     * @param httpServletRequest 去除session
     * @return  返回退出结果
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest httpServletRequest){
        httpServletRequest.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
