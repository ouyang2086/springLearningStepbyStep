package com.yc.biz;

import org.springframework.stereotype.Component;

@Component //只要加了这个注解 ，这个类就可以被Spring容器托管
public class HelloWorld {
    public void hello(){System.out.println("Hello World ");}

    public  HelloWorld(){System.out.println("无参构造方法 ");}
}
