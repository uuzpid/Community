package com.pyx.community;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommunityApplicationTests {

    public CommunityApplicationTests(){
        System.out.println("A");
    }
    class B{
        public B(){
            System.out.println("B");
        }
    }


    @Test
    public void contextLoads() {
        
        CommunityApplicationTests communityApplicationTests = new CommunityApplicationTests();
        CommunityApplicationTests.B b = new CommunityApplicationTests().new B();
        Chouxiang chouxiang = new Chouxiang() {
            String name = "neibu";
            @Override
            public void test1() {
                super.test1();
                System.out.println(name);
            }
        } ;
        chouxiang.test1();
        System.out.println("-----");
        System.out.println(chouxiang.name);
    }

}

abstract class Chouxiang{
    String name = "waibu";
    public void test1(){
        System.out.println("抽象类外部方法");
    }
}