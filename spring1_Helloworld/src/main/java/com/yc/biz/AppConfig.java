package com.yc.biz;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;

@Configurable //表示当前类是一个配置类
@ComponentScan(basePackages =  "com.yc") //将要托管的bean要扫描的包
public class AppConfig {
}
