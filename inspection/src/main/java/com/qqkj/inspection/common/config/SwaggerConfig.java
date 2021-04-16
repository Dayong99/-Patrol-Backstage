package com.qqkj.inspection.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2     // 开启 Swagger2
public class SwaggerConfig {

    // 配置了Swagger的Docket的bean实例
    @Bean
    public Docket docket(Environment environment) {

        // 设置要显示的swagger环境
        Profiles profiles = Profiles.of("dev", "test");
        // 通过environment.acceptsProfiles判断是否处在自己设定的环境下
        boolean flag = environment.acceptsProfiles(profiles);
        System.out.println(flag);

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(appInfo())
                .groupName("qqkj")
                //.enable(flag)   // enable 是否启动swagger
                .select()
                /*

                    RequestHandlerSelectors,配置要扫描接口的方式
                        basePackage:指定要扫描的包
                        any:扫描全部
                        none:不扫描
                        withClassAnnotation:扫描类上的注解，参数是一个注解的反射
                        withMethodAnnotation:扫描方法上的注解

                 */
                .apis(RequestHandlerSelectors.basePackage("com.qqkj.inspection.inspection.controller"))
                // paths()  过滤什么路径
               // .paths(PathSelectors.ant("/gaizka/**"))
                .build();
    }

    // 配置Swagger信息=apiInfo
    private ApiInfo appInfo() {
        // 作者信息
        Contact contact = new Contact("Joe", "", "");
        return  new ApiInfo("巡察办的SwaggerAPI文档",
                "即使再小的帆也能远航",
                "1.0",
                "urn:tos",
                contact,
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList());

    }
}
