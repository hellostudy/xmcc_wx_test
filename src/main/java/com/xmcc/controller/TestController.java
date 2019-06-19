package com.xmcc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    Logger logger= LoggerFactory.getLogger(TestController.class);

    @GetMapping("/hello")
    public String hello(){
        logger.info("hello logger info");
        return "hello spring_boot";
    }
}
