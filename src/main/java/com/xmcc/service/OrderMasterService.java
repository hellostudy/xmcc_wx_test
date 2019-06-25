package com.xmcc.service;

import com.xmcc.common.ResultResponse;
import com.xmcc.dto.OrderMasterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface OrderMasterService {

    ResultResponse insertOrder(OrderMasterDto orderMasterDto);

    /*查询订单列表*/
    Page<OrderMasterDto> findList(String openid, PageRequest request);
}
