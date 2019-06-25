package com.xmcc.controller;

import com.google.common.collect.Maps;
import com.sun.media.jfxmedia.logging.Logger;
import com.xmcc.common.ResultResponse;
import com.xmcc.dto.OrderMasterDto;
import com.xmcc.entity.OrderMaster;
import com.xmcc.exception.CustomException;
import com.xmcc.service.OrderMasterService;
import com.xmcc.utils.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("buyer/order")
@Api(value = "订单相关接口",description = "完成订单的增删改查")
@Slf4j
public class OrderMasterController {
    Logger lod=null;
    @Autowired
    private OrderMasterService orderMasterService;

    @PostMapping("create")
    @ApiOperation(value = "创建订单接口", httpMethod = "POST", response =ResultResponse.class)
    public ResultResponse creat(@Valid @ApiParam(name="订单对象",value = "传入json格式",required = true)
                                        OrderMasterDto orderMasterDto, BindingResult bindingResult){
        Map<String,String> map= Maps.newHashMap();
        //判断参数校验
        if(bindingResult.hasErrors()){
            List<String> list = bindingResult.getFieldErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.toList());
            map.put("参数校验出错", JsonUtil.object2string(list));
            return ResultResponse.fail(map);
        }
        return orderMasterService.insertOrder(orderMasterDto);
    }


    //订单列表
    @PostMapping("list")
    @ApiOperation(value = "订单列表接口")
    public ResultResponse<List<OrderMasterDto>> list(@RequestParam("openid") String openid,
                                                    @RequestParam(value = "page",defaultValue = "0") Integer page,
                                                    @RequestParam(value = "size",defaultValue = "10") Integer size){
        if(StringUtils.isEmpty(openid)){
            log.info("cuowu");
            throw new CustomException("cw");
        }
        PageRequest request = new PageRequest(page, size);

        Page<OrderMasterDto> masterDtoPage = orderMasterService.findList(openid, request);
        return ResultResponse.success(masterDtoPage.getContent());
    }
    //订单详情
    @PostMapping("detail")
    @ApiOperation(value = "查询订单详情")
    public ResultResponse detail(String openid, String orderId){


        return null;
    }




}
