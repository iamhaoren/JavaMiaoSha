package com.rt.miaosha.service.impl;

import com.rt.miaosha.dao.ItemDOMapper;
import com.rt.miaosha.dao.ItemStockDOMapper;
import com.rt.miaosha.dataobject.ItemDO;
import com.rt.miaosha.dataobject.ItemStockDO;
import com.rt.miaosha.error.BusinessException;
import com.rt.miaosha.error.EmBusinessError;
import com.rt.miaosha.service.ItemService;
import com.rt.miaosha.service.model.ItemModel;
import com.rt.miaosha.validator.ValidationResult;
import com.rt.miaosha.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    ValidatorImpl validator;

    @Autowired
    ItemDOMapper itemDOMapper;

    @Autowired
    ItemStockDOMapper itemStockDOMapper;

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        //校验参数
        ValidationResult result=validator.validate(itemModel);
        if (result.isHasError()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }

        //itemModel -> itemDO
        ItemDO itemDO=convertItemDOFromItemModel(itemModel);

        //写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());

        //根据商品创建并存入其库存信息
        ItemStockDO itemStockDO=convertItemStockDOFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);

        //返回创建的对象
        return getItemById(itemModel.getId());
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOList=itemDOMapper.listItem();
        List<ItemModel> itemModelList=itemDOList.stream().map(itemDO -> {
            ItemStockDO itemStockDO=itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel=convertModelFromDataObject(itemDO,itemStockDO);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO=itemDOMapper.selectByPrimaryKey(id);
        if (itemDO==null)
            return null;
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());

        return convertModelFromDataObject(itemDO,itemStockDO);
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) {
        int affectedRow=itemStockDOMapper.decreaseStock(itemId,amount);
        //是否更新库存成功
        if (affectedRow>0)
            return true;
        return false;
    }

    private ItemDO convertItemDOFromItemModel(ItemModel itemModel){
        if (itemModel==null)
            return null;
        ItemDO itemDO=new ItemDO();
        BeanUtils.copyProperties(itemModel,itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }

    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel){
        if (itemModel==null)
            return null;
        ItemStockDO itemStockDO=new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());
        return itemStockDO;
    }
    private ItemModel convertModelFromDataObject(ItemDO itemDO,ItemStockDO itemStockDO){
        ItemModel itemModel=new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }
}
