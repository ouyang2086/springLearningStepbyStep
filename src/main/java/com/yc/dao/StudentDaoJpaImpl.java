package com.yc.dao;

import org.springframework.stereotype.Repository;

import java.util.Random;

@Repository //异常转化： 从Exception 转化为RuntimeException
public class StudentDaoJpaImpl implements StudentDao{


    @Override
    public int add(String name) {
        System.out.println("jpa 添加学生" +name);
        Random r = new Random();
        return r.nextInt();
    }

    @Override
    public void update(String name) {
    System.out.println("jpa 更新学生" +name);
    }
}
