package top.bearsof.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.bearsof.reggie.common.BaseContext;
import top.bearsof.reggie.common.GlobalException;
import top.bearsof.reggie.common.R;
import top.bearsof.reggie.entity.AddressBook;
import top.bearsof.reggie.service.AddressBookService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;
    @PostMapping
    public R<String> addressBookR(@RequestBody AddressBook addressBook){
        Long currentId = BaseContext.getCurrentId();
        log.info(currentId.toString());
        log.info(addressBook.toString());
        //设置用户Id
        addressBook.setUserId(currentId);
        addressBookService.save(addressBook);
        return R.success("添加成功");
    }

    @GetMapping("/list")
    public R<List<AddressBook>> getAddressBookList(){
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        List<AddressBook> addressBookList = addressBookService.list(lambdaQueryWrapper);
        log.info(addressBookList.toString());
        return R.success(addressBookList);
    }


    @PutMapping("/default")
    public R<String> setDefaultAddress(@RequestBody AddressBook addressBooks){
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        lambdaUpdateWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(lambdaUpdateWrapper);
        AddressBook addressBook = addressBookService.getById(addressBooks.getId());
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

    /**
     * 支付页面获取默认地址
     * @return  返回默认地址的Json
     */
    @GetMapping("/default")
    public R<AddressBook> getDefaultAddress(){
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(BaseContext.getCurrentId()!=null,AddressBook::getUserId,BaseContext.getCurrentId()).eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(addressBookLambdaQueryWrapper);
        if (addressBook==null) {
            throw new GlobalException("您未设置默认地址");
        }
        return R.success(addressBook);
    }


}
