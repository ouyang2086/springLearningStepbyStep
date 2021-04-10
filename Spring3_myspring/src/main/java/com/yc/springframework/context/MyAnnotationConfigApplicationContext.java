package com.yc.springframework.context;

import com.yc.springframework.stereotype.*;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.*;

/**
 * @program: TestSpring
 * @description:
 * @author: 作者
 * @create: 2021-04-05 15:26
 */
public class MyAnnotationConfigApplicationContext implements MyApplicationContext{

   private Map<String,Object> beanMap = new HashMap<String,Object>();

   public MyAnnotationConfigApplicationContext(Class<?>... componentClasses){
       try {
           register(componentClasses);
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

   public void  register(Class<?>[] componentClasses) throws Exception {
       if(componentClasses==null || componentClasses.length <=0){
           throw  new RuntimeException("没有指定配置文件");
       }

       for( Class cl:componentClasses ){
           //自主实现
           //源码1：实现IOC MyPostConStruct MyPreDestroy
           if(!cl.isAnnotationPresent(MyConfiguration.class)){
               continue;
           }
           String[] basePackages = getAppConfigBasePackages(cl);
           if(cl.isAnnotationPresent(MyComponentScan.class)){
               MyComponentScan mcs =(MyComponentScan) cl.getAnnotation(MyComponentScan.class);
                if(mcs.basePackages() !=null && mcs.basePackages().length>0){
                    basePackages = mcs.basePackages();
                }
           }
           //处理@Bean的情况
           //cl->MyAppConfig对象。class
           Object obj = cl.newInstance();  //obj 就是当前解析的 MyAppConfig 对象
           handleAtMyBean(cl  ,obj);
           //源码2：实现di
           //处理 basePackages
           for(String basePackage : basePackages){
               scanPackgeAndSubPackageClasses(basePackage);//扫描所有的包，及子包中的been包
           }
           //继续其他托管
           handleManagedBean();
           //版本2  循环 beanMap中的每个bean， 找到它们的每个类的每个由@Autowired @Resource 注解的方法以实现di
           handleDi(beanMap);
       }
     

   }
    //循环 beanMap中的每个bean， 找到它们的每个类的每个由@Autowired @Resource 注解的方法以实现di
    private void handleDi(Map<String, Object> beanMap) throws InvocationTargetException, IllegalAccessException {
       Collection<Object> objectCollection = beanMap.values();
       for(Object obj : objectCollection){
           Class cls = obj.getClass();
           Method[] ms = cls.getDeclaredMethods();
           for (Method m :ms){
               if(m.isAnnotationPresent(MyAutowired.class)&& m.getName().startsWith("set")){
                   invokeAutowiredMethod(m,obj);
               }else if(m.isAnnotationPresent(MyResource.class)&&m.getName().startsWith("set")){
                   invokeResourceMethod(m,obj);
               }
               Field[] fs = cls.getDeclaredFields();
               for (Field field : fs ){
                   if(field.isAnnotationPresent(MyAutowired.class)){

                   }else if(field.isAnnotationPresent(MyResource.class)){

                   }

               }

           }
       }
    }

    private void invokeResourceMethod(Method m, Object obj) throws InvocationTargetException, IllegalAccessException {
       //1. 取出 MyResource 中的的name的属性值 ， 当成 beanid
        MyResource mr = m.getAnnotation(MyResource.class);
        String beanId = mr.name();
        //2.如果没有，则取出 m方法中的参数的类型名，改成首字母小写 当成beanId
        if(beanId==null || beanId.equalsIgnoreCase("")    ){
            String pname = m.getParameterTypes()[0].getSimpleName();
            beanId = pname.substring(0,1).toLowerCase()+ pname.substring(1);

        }
        //3.从beanMap 取出
        Object o = beanMap.get(beanId);
        //4.invoke
        m.invoke(obj,o);
    }

    private void invokeAutowiredMethod(Method m, Object obj) throws InvocationTargetException, IllegalAccessException {
       //1.取出 m的参数的类型
        //studentdo 的接口类型
        Class typeClass = m.getParameterTypes()[0];
        //2.从beanMap 中循环所有的object
        Set<String> keys = beanMap.keySet();
        for (String key : keys){
            //判断这些object 是否为参数类型的实例 instanceof
            //如果是，则从beanMap取出
            Object o = beanMap.get(key);
            Class[] interfaces = o.getClass().getInterfaces();
            for (Class c : interfaces){
                System.out.println(c.getName() + "\t" +typeClass);
                if(c == typeClass){
                    //5.invoke
                    m.invoke(obj,o);
                    break;
                }
            }
        }
    }

    /*
    * 处理managedBeanClasses 所有的Class类，筛选出所的@Component @Service @Repository 的类，并实例化
    * */
    private void handleManagedBean() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        for(Class c:manageBeanClasses){
            if (c.isAnnotationPresent(MyComponent.class)){
                saveManagedBean(c);
            }else if(c.isAnnotationPresent(MyService.class)){
                saveManagedBean(c);
            }else if(c.isAnnotationPresent(MyRepository.class)){
                saveManagedBean(c);
            }else if(c.isAnnotationPresent(MyController.class)){
                saveManagedBean(c);
            }else{

            }
        }
    }
    //保存
    private void saveManagedBean(Class c) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Object  o = c.newInstance();
        handlePostConstruct(o,c);
        String beanId = c.getSimpleName().substring(0,1).toLowerCase() + c.getSimpleName().substring(1);
        beanMap.put(beanId,o);

    }

    //扫描包和子包
    private void scanPackgeAndSubPackageClasses(String basePackage) throws Exception{
       String packagePath = basePackage.replaceAll("\\.","/");
       //Class.forName("com.xx.xx")
        System.out.println("扫描包路径"+basePackage+",替换"+ packagePath);
        Enumeration<URL> files = Thread.currentThread().getContextClassLoader().getResources(packagePath);
        while(files.hasMoreElements()){
            URL url = files.nextElement();
            System.out.println("配置的扫描的路径为："+url.getFile());
            findClassesInPackages(url.getFile(),basePackage);  //第二个参数
        }
    }

    private Set<Class> manageBeanClasses = new HashSet<Class>();

   /* 查找 file下面的及子包下面的要托管的class，存到的Set ( managedBeanClass )中 */
    private void findClassesInPackages(String file, String basePackage) throws ClassCastException, ClassNotFoundException {
        File f = new File(file);
        //
        File[] classFiles = f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".class") || file.isDirectory();
            }
        });
        //
        for(File cf:classFiles){
            if(cf.isDirectory()){
                //如果有目录，则递归
                //拼接子目录
                basePackage += "." +cf.getName().substring(cf.getName().lastIndexOf("/")+1);
                findClassesInPackages(cf.getAbsolutePath(),basePackage);

            }else{
                //加载 cf 作为 class文件
                URL[] urls = new URL[]{};
                URLClassLoader ucl = new URLClassLoader(urls);
                //
                //
                Class c = ucl.loadClass(basePackage + "."+cf.getName().replace(".class",""));
                manageBeanClasses.add(c);
            }
        }


    }

    /*
   * 处理 MyAppConfig配置中的@Bean注解，完成IOC操作
   * */
    private void handleAtMyBean(Class cls, Object obj) throws InvocationTargetException, IllegalAccessException {
       //获取cls中的method
        Method[] ms = cls.getDeclaredMethods();
        //循环，判断是否有@MyBean注解
        for(Method m : ms){
            if (m.isAnnotationPresent(MyBean.class)){
                //有
                Object o = m.invoke(obj);
                //
                handlePostConstruct(o ,o.getClass());
                beanMap.put(m.getName(),o);
            }
        }
    }

    //处理一个Bean中的 @MyPostConstruct对应的方法
    private void handlePostConstruct(Object o, Class<?> cls) throws InvocationTargetException, IllegalAccessException {
     Method[] ms = cls.getDeclaredMethods();
     for (Method m : ms){
         if(m.isAnnotationPresent(MyPostConstruct.class)){
             m.invoke(o);
         }
     }
    }

    // 获取当前 AppConfig类所在的包路径
    private String[] getAppConfigBasePackages(Class cl) {
       String[] paths = new String[1];
       System.out.println(paths);
       paths[0] = cl.getPackage().getName();
       return paths;
    }

    @Override
    public Object getBean(String id) {
        return beanMap.get(id);
    }
}
