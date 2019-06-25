package com.xmcc.service.impl;

import com.xmcc.common.ProductEnums;
import com.xmcc.common.ResultEnums;
import com.xmcc.common.ResultResponse;
import com.xmcc.dto.ProductCategoryDto;
import com.xmcc.dto.ProductInfoDto;
import com.xmcc.entity.ProductInfo;
import com.xmcc.repository.ProductInfoRepository;
import com.xmcc.service.ProductCategoryService;
import com.xmcc.service.ProductInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductInfoServiceImpl implements ProductInfoService {

    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Override
    public ResultResponse queryList() {
        //查询所有分类
        ResultResponse<List<ProductCategoryDto>> categoryServiceResult = productCategoryService.findAll();
        List<ProductCategoryDto> categoryDtoList = categoryServiceResult.getData();
        if (CollectionUtils.isEmpty(categoryDtoList)) {
            return categoryServiceResult;//列表为空,直接返回
        }

        //获得类目编号集合
       List<Integer> collect = categoryDtoList.stream().map(productCategoryDto -> productCategoryDto.getCategoryType())
                .collect(Collectors.toList());
        //查询商品列表,对商品上下架 用枚举 方便扩展
        List<ProductInfo> typeIn = productInfoRepository.findByProductStatusAndCategoryTypeIn(ResultEnums.PRODUCT_UP.getCode(), collect);
        //多线程遍历 取出每个商品类目编号对应的 商品列表 设置进入类目中
        //将productInfo设置到 foods中
        //过滤: 不同的type  进行不同的封装
        //将 productInfo 转换成Dto
        List<ProductCategoryDto> collect1 = categoryDtoList.parallelStream().map(productCategoryDto -> {
            productCategoryDto.setProductInfoDtoList(typeIn.stream()
                    //过滤: 不同的type  进行不同的封装
                    .filter(productInfo -> productInfo.getCategoryType() == productCategoryDto.getCategoryType())
                    //将 productInfo 转换成Dto
                    .map(productInfo -> ProductInfoDto.build(productInfo)).collect(Collectors.toList()));
            return productCategoryDto;
        }).collect(Collectors.toList());

        return ResultResponse.success(collect1);
    }

    //根据id查询商品
    @Override
    public ResultResponse<ProductInfo> queryById(String productId) {
        if(StringUtils.isBlank(productId)){
            return ResultResponse.fail(ResultEnums.PARAM_ERROR.getMsg());
        }
        Optional<ProductInfo> byId = productInfoRepository.findById(productId);
        if(!byId.isPresent()){
            return ResultResponse.fail(productId+":"+ResultEnums.NOT_EXITS.getMsg());
        }
        ProductInfo productInfo = byId.get();

        //判断商品是否下架
        if(productInfo.getProductStatus()== ProductEnums.PRODUCT_down.getCode()){
            return ResultResponse.fail(ProductEnums.PRODUCT_down.getMsg());
        }
        return ResultResponse.success(productInfo);
    }

    //修改商品库存
    @Override
    public void updateProduct(ProductInfo productInfo) {
        productInfoRepository.save(productInfo);
    }

}
