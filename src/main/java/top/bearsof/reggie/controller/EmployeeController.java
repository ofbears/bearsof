package top.bearsof.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import top.bearsof.reggie.common.R;
import top.bearsof.reggie.common.SMSUtil;
import top.bearsof.reggie.common.ValidateCodeUtils;
import top.bearsof.reggie.entity.Employee;
import top.bearsof.reggie.service.EmployeeService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private SMSUtil smsUtil;

    /**
     * 用户登录
     *
     * @param request  用户登录成功后存Session
     * @param employee 获取用户登录时信息
     * @return 返回登录结果
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        String password = employee.getPassword();
        //使用工具类将密码加密成md5
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        employeeLambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(employeeLambdaQueryWrapper);

        if (emp == null) {
            return R.error("登录失败");
        }
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }
        //登录成功
        //检查员工是否被经用
        if (emp.getStatus() == 0) {
            return R.error("账户已被禁用");
        }
        //员工状态正常，登录成功
        request.getSession().setAttribute("employee", emp.getId());
        //smsUtil.sendMailMessage("323357085@qq.com","系统登录","您的验证码为:" + ValidateCodeUtils.generateValidateCode4String(4));

        return R.success(emp);
    }


    /**
     * 用户登出
     *
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        String firstPassword = "123456";
        firstPassword = DigestUtils.md5DigestAsHex(firstPassword.getBytes());
        employee.setPassword(firstPassword);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        boolean save = employeeService.save(employee);
        return save ? R.success("注册成功") : R.error("注册失败");
    }

    @GetMapping("/page")
    public R<Page> page(int page , int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        //分页构造器
        Page iPage= new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<Employee>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(iPage,lambdaQueryWrapper);
        return R.success(iPage);
    }

    /**
     * 员工的状态的禁用和启用
     * @param httpServletRequest
     * @param employee
     * @return
     */

    @PutMapping
    public R<String> update(HttpServletRequest httpServletRequest,@RequestBody Employee employee){
        System.out.println(employee);
        Long updateEmployee = (Long) httpServletRequest.getSession().getAttribute("employee");
        employee.setUpdateUser(updateEmployee);
        employeeService.updateById(employee);
        long id = Thread.currentThread().getId();
        log.info("当前进程的Id {}",id);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<Employee>();
        wrapper.eq(Employee::getId,id);
        Employee emp = employeeService.getOne(wrapper);

        log.info("查询出Id为:" + id);
        return emp==null? R.error("未知异常"):R.success(emp);
    }

}
