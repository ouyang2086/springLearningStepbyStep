package com.yc.dao;

import com.yc.AppConfig;
import com.yc.biz.StudentBizImpl;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class StudentImplTest extends TestCase {
    //容器
    ApplicationContext ac;
    private StudentBizImpl studentBiz;
    @Before
    public void setUp() throws Exception {
       // java.lang.String
        ac = new AnnotationConfigApplicationContext(AppConfig.class);
        studentBiz = (StudentBizImpl) ac.getBean("studentBizImpl");
    }

    @Test
    public void testAdd() {
        studentBiz.add("李四");
    }

    @Test
    public void testUpdate() {
        studentBiz.update("李四");
    }
}