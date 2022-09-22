package top.bearsof.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.bearsof.reggie.common.BaseContext;
import top.bearsof.reggie.common.GlobalException;
import top.bearsof.reggie.common.R;
import top.bearsof.reggie.entity.ShoppingCart;
import top.bearsof.reggie.service.ShoppingCartService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 获取当前用户的购物车
     *
     * @return 返回当前用户的购物车集合
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> getShoppingCartList() {

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BaseContext.getCurrentId() != null, ShoppingCart::getUserId, BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(lambdaQueryWrapper);
        return R.success(shoppingCartList);
    }

    @PostMapping("/add")
    public R<String> addToShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        shoppingCart.setNumber(1);
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        lambdaQueryWrapper
                .eq(ShoppingCart::getUserId, BaseContext.getCurrentId())
                .eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart userCart = shoppingCartService.getOne(lambdaQueryWrapper);
        if (userCart != null) {
            Integer cartNumber = userCart.getNumber();
            userCart.setNumber(cartNumber + 1);
            shoppingCartService.updateById(userCart);
        } else {
            shoppingCartService.save(shoppingCart);
        }

        log.info(shoppingCart.toString());
        return R.success("添加成功");
    }

    @Transactional
    @PostMapping("/sub")
    public R<String> deleteShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //两种情况    1、DishID=null   2、SetMealId=null
        lambdaQueryWrapper.eq(BaseContext.getCurrentId() != null, ShoppingCart::getUserId, BaseContext.getCurrentId());
        lambdaQueryWrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId()); //false
        lambdaQueryWrapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        ShoppingCart cart = shoppingCartService.getOne(lambdaQueryWrapper);
        //需要判断数量，如果GetNumber==1  证明这次删减后需要清除这一条数据
        Integer number = cart.getNumber();
        log.info("你看起来好像很好吃" + number.toString());
        if (number <= 1) {
            shoppingCartService.remove(lambdaQueryWrapper);
        } else {
            cart.setNumber(cart.getNumber() - 1);
            shoppingCartService.updateById(cart);
        }
        return R.success("操作成功");
    }

    @DeleteMapping("/clean")
    public R<String> deleteUserShoppingCart() {
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(BaseContext.getCurrentId() != null, ShoppingCart::getUserId, BaseContext.getCurrentId());
        boolean result = shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
        if(!result){
            throw new GlobalException("清空失败");
        }
        return R.success("删除成功");
    }
}


