package com.yc.biz;

import com.yc.dao.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class StudentBizImpl {
    private StudentDao studentDao;

    public StudentBizImpl(StudentDao studentDao){
        this.studentDao=studentDao;
    }
    public StudentBizImpl(){}

    //
    //@Inject  //javax中的依赖注入 ， 如果有多个对象
    //
    @Autowired
    @Qualifier("studentDaoJpaImpl")
    //
    //
    public void setStudentDao(StudentDao studentDao){
        this.studentDao=studentDao;
    }
    public int add(String name){
        System.out.println("========业务层=======");
        System.out.println("用户名是否重名");
        int result = studentDao.add(name);
        System.out.println("========业务层结束=======");
        return result;
    }
    public void update(String name){
        System.out.println("========业务层=======");
        System.out.println("用户名是否重名");
        studentDao.update(name);
        System.out.println("========业务层结束=======");
    }
}
