package com;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Random;

@Configurable //表示当前类是一个配置类
@ComponentScan(basePackages =  {"com.huwei", "com.mimi"}) //将要托管的bean要扫描的包
public class AppConfig {

    @Bean
    public Random r(){
        return new Random();
    }
}
