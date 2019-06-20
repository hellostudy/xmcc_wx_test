package com.xmcc;


import com.google.common.collect.Lists;
import com.xmcc.entity.ProductCategory;
import com.xmcc.repository.ProductCategoryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@EnableSwagger2
public class WxTestApplicationTests {
    @Autowired
    private  ProductCategoryRepository productCategoryRepository;

    @Test
    public void findByCategoryTypeIn() {
        //List<ProductCategory>
        List<ProductCategory> byCategoryTypeIn = productCategoryRepository.findByCategoryTypeIn(Lists.newArrayList(1,2));
        byCategoryTypeIn.stream().forEach(System.out::println);
    }

}
