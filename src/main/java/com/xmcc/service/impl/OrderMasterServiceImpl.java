package com.xmcc.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xmcc.common.*;
import com.xmcc.dto.OrderDetailDto;
import com.xmcc.dto.OrderMasterDto;
import com.xmcc.entity.OrderDetail;
import com.xmcc.entity.OrderMaster;
import com.xmcc.entity.ProductInfo;
import com.xmcc.exception.CustomException;
import com.xmcc.repository.OrderMasterRepository;
import com.xmcc.service.OrderDetailService;
import com.xmcc.service.OrderMasterService;
import com.xmcc.service.ProductInfoService;
import com.xmcc.utils.BigDecimalUtil;
import com.xmcc.utils.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderMasterServiceImpl implements OrderMasterService {
    
    @Autowired
    private OrderMasterRepository orderMasterRepository;
    @Autowired
    private ProductInfoService productInfoService;
    @Autowired
    private OrderDetailService orderDetailService;

    
    @Override
    @Transactional
    public ResultResponse insertOrder(OrderMasterDto orderMasterDto) {
        //取出订单
        List<OrderDetailDto> items = orderMasterDto.getItems();
        //创建订单集合,将符合的放入 之后批量插入
        List<OrderDetail> orderDetailList = Lists.newArrayList();
        //创建总金额
        BigDecimal totalPrice = new BigDecimal("0");

        //遍历订单项,获取商品详情
        for(OrderDetailDto item : items){
            //查询订单
            ResultResponse<ProductInfo> resultResponse = productInfoService.queryById(item.getProductId());
            //若未查到 生成订单失败
            if(resultResponse.getCode()== ResultEnums.FAIL.getCode()){
                throw new CustomException(resultResponse.getMsg());
            }
            //获取查询的商品
            ProductInfo productInfo = resultResponse.getData();
            //比较库存
            if(productInfo.getProductStock()<item.getProductQuantity()){
                throw new CustomException(ProductEnums.PRODUCT_NOT_ENOUGH.getMsg());
            }
            //创建订单项
            OrderDetail orderDetail = OrderDetail.builder().detailId(IDUtils.createIdbyUUID()).productIcon(productInfo.getProductIcon())
                    .productId(item.getProductId()).productName(productInfo.getProductName())
                    .productPrice(productInfo.getProductPrice()).productQuantity(item.getProductQuantity())
                    .build();
            orderDetailList.add(orderDetail);

            //减少库存
            productInfo.setProductStock(productInfo.getProductStock()-item.getProductQuantity());
            productInfoService.updateProduct(productInfo);

            //计算价格
            totalPrice = BigDecimalUtil.add(totalPrice, BigDecimalUtil.multi(productInfo.getProductPrice(), item.getProductQuantity()));
        }

            //生成订单id
            String orderId = IDUtils.createIdbyUUID();
            //构建订单信息
            OrderMaster orderMaster = OrderMaster.builder().buyerAddress(orderMasterDto.getAddress()).buyerName(orderMasterDto.getName())
                    .buyerOpenid(orderMasterDto.getOpenid()).orderStatus(OrderEnums.NEW.getCode())
                    .payStatus(PayEnums.WAIT.getCode()).buyerPhone(orderMasterDto.getPhone())
                    .orderId(orderId).orderAmount(totalPrice).build();

            //将生成的订单id，设置到订单项中
            List<OrderDetail> orderDetails = orderDetailList.stream().map(orderDetail -> {
                orderDetail.setOrderId(orderId);
                return orderDetail;
            }).collect(Collectors.toList());

            //插入订单项
            orderDetailService.batchInsert(orderDetails);
            //插入订单
            orderMasterRepository.save(orderMaster);
            HashMap<String,String> mapq= Maps.newHashMap();

            mapq.put("orderId",orderId);
            return ResultResponse.success(mapq);

    }

    @Override
    public Page<OrderMasterDto> findList(String openid, PageRequest request) {
        return null;
    }
}
