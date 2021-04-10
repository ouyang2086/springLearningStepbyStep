package com.yc.dao;

import com.yc.biz.StudentBizImpl;
import junit.framework.TestCase;

public class StudentDaoTest extends TestCase {

    private StudentDao studentDao;
    private StudentBizImpl studentBizImpl;

    public void setUp() throws Exception {
       //
        studentDao =  new StudentDaoMybatisImpl();
        //
        studentBizImpl = new StudentBizImpl();
        //
        studentBizImpl.setStudentDao(studentDao);

    }

    public void testAdd() {
        studentBizImpl.add("张三");
    }

    public void testUpdate() {
        studentBizImpl.update("张三");
    }
    public void testBizAdd(){
        studentBizImpl.add("张三");
    }
}