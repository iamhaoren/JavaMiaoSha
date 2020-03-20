package com.rt.miaosha.controller;

import com.rt.miaosha.controller.viewobject.ItemVO;
import com.rt.miaosha.error.BusinessException;
import com.rt.miaosha.response.CommonReturnType;
import com.rt.miaosha.service.impl.ItemServiceImpl;
import com.rt.miaosha.service.model.ItemModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.beans.Beans;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class ItemController extends BaseController {

    @Autowired
    ItemServiceImpl itemService;

    //创建商品
    @RequestMapping(value = "/create",method = {RequestMethod.POST},consumes ={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title")String title,
                                       @RequestParam(name = "description")String description,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock")Integer stock,
                                       @RequestParam(name = "imgUrl")String imgUrl) throws BusinessException {
        ItemModel itemModel=new ItemModel();
        itemModel.setTitle(title);
        itemModel.setStock(stock);
        itemModel.setPrice(price);
        itemModel.setDescription(description);
        itemModel.setImgUrl(imgUrl);
        ItemModel itemModelForReturn=itemService.createItem(itemModel);

        ItemVO itemVO=convertVOFromModel(itemModelForReturn);
        return CommonReturnType.create(itemVO);
    }

    //商品详情
    @RequestMapping(value = "/get",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItem(@RequestParam(name = "id")Integer id){
        ItemModel itemModel=itemService.getItemById(id);
        ItemVO itemVO=convertVOFromModel(itemModel);
        return CommonReturnType.create(itemVO);
    }

    //商品列表浏览

    @RequestMapping(value = "/list",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType listItem(){
        List<ItemModel> itemModelList=itemService.listItem();
        List<ItemVO> itemVOList=itemModelList.stream().map(itemModel -> {
            ItemVO itemVO=convertVOFromModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());
        return CommonReturnType.create(itemVOList);
    }

    private ItemVO convertVOFromModel(ItemModel itemModel){
        if (itemModel==null)
            return null;
        ItemVO itemVO=new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);
        return itemVO;
    }
}
