package top.bearsof.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.bearsof.reggie.common.R;
import top.bearsof.reggie.common.SMSUtil;
import top.bearsof.reggie.dto.DishDto;
import top.bearsof.reggie.entity.Category;
import top.bearsof.reggie.entity.Dish;
import top.bearsof.reggie.entity.DishFlavor;
import top.bearsof.reggie.service.CategoryService;
import top.bearsof.reggie.service.DishFlavorService;
import top.bearsof.reggie.service.DishService;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private SMSUtil smsUtil;
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;




    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("新增菜品成功");
    }
    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize,String name){
        //分页构造器
        Page<Dish> iPage = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //过滤器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name!=null,Dish::getName,name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(iPage,lambdaQueryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(iPage,dishDtoPage,"records");
        List<Dish> records = iPage.getRecords();
        List<DishDto> dishDtoList = records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtoList);
        smsUtil.sendMailMessage("323357085@qq.com","这是你看起来好像很好吃的第一个邮箱项目","这是一个测试");
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> dishDtoR(@PathVariable Long id){
        DishDto dishDto = dishService.selectWithFlavor(id);
        return R.success(dishDto);
    }


    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        //修改所有的Key
        /*Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);*/
        //清理某个分类下面的缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("修改成功");
    }

    /*@GetMapping("/list")
    public R<List<Dish>> queryDish(Dish dish){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Dish::getCategoryId,dish.getCategoryId());
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        return R.success(list);
    }*/


    @GetMapping("/list")
    public R<List<DishDto>> queryDish(Dish dish){
        List<DishDto> dishDtoList =null;
        //redis缓存查询
        //动态构造
        String key ="dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if(dishDtoList!=null){
            return R.success(dishDtoList);
        }
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        dishDtoList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        //在这里，证明redis缓存中不存在数据
        //设置60分钟的缓存
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }

    /**
     * 删除菜品和菜品对应的图片文件
     * @param ids  需要删除的菜品id集合
     * @return  return 删除的结果    由事务进行监听
     */
    @DeleteMapping
    @Transactional
    public R<String> delete(Long[] ids){
        List<String> images = Arrays.stream(ids).map((item) -> {
            Dish dish = dishService.getById(item);
            return dish.getImage();
        }).toList();
        List<Long> longs = Arrays.stream(ids).toList();
        this.dishService.removeByIds(longs);
        images.forEach(s ->{
            log.info(s);
            String filePath = CommonController.path +"\\"+ s;
            File file = new File(filePath);
            boolean delete = file.delete();
            log.info("删除的结果是:" + delete);
        });           //Path + s 代表图片文件的路径
        return R.success("删除成功!");
    }

    /**
     * 修改菜品状态，并开启Mysql事务监听
     * @param status  需要修改成的状态值
     * @param ids  菜品的id
     * @return  修改的结果
     */
    @PostMapping("/status/{status}")
    @Transactional
    public R<String> changeDishStatus(@PathVariable Integer status,Long[] ids){
        //log.info((String) status);
        List<Long> idsList = Arrays.stream(ids).toList();
        List<Dish> dishes = idsList.stream().map((item)->{
            LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(status!=null&&item!=null,Dish::getId,item);
            Dish dish = dishService.getOne(lambdaQueryWrapper);
            dish.setStatus(status);
            return dish;
        }).collect(Collectors.toList());
        dishService.updateBatchById(dishes);
        return R.success("修改成功!");
    }

}
