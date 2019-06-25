package com.xmcc.service;

import com.xmcc.entity.OrderDetail;
import org.springframework.stereotype.Service;

import java.util.List;


public interface OrderDetailService {

    //批量插入
    void batchInsert(List<OrderDetail> orderDetailList);
}
