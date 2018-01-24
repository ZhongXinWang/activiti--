package com.test.user;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.test.ActivitiRule;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

/**
 * Created by @author 王钟鑫
 *
 * @create 2018/1/20.
 */
public class TestUser {

    //获取到activiti测试类的ActivitiRule对象，可以来获得对应的Service
    @Rule
    public  ActivitiRule activitiRule = new ActivitiRule();

    //创建一个User
    @Test
    public void testUser(){

        //获取一个IdentityService，里面装了对用户，用户组，用户和用户组直接的操作
        IdentityService identityService = activitiRule.getIdentityService();

        //创建一个用户
        User user = new UserEntity();
        user.setId(UUID.randomUUID().toString());
        user.setFirstName("张三");
        user.setEmail("111111@qq.com");
        identityService.saveUser(user);
    }

    //查询所有的用户
    @Test
    public void testQueryUser(){


        ManagementService managementService = activitiRule.getManagementService();
        IdentityService identityService = activitiRule.getIdentityService();
        List<User> dataList = identityService.createUserQuery().list();
        for (User user:dataList){


            System.out.println("user的值是：---" + user.getId()+"--"+user.getFirstName() + "，当前方法=TestUser.testQueryUser()");
            
        }

        long count = identityService.createNativeUserQuery().sql("select count(*) from "+managementService.getTableName(User.class)).count();

        System.out.println("count的值是：---" + count + "，当前方法=TestUser.testQueryUser()");
    }
    //增加一个用户组，在把用户添加到用户组里边去
    @Test
    public void testUserAndGruop(){

        IdentityService identityService = activitiRule.getIdentityService();

        Group group = new GroupEntity();

        group.setId("groupId");
        group.setName("用户组");
        identityService.saveGroup(group);

        //创建用户
        User user = identityService.newUser("userId");
        User user1 = identityService.newUser("userId1");
        user.setFirstName("李四");
        identityService.saveUser(user);
        identityService.saveUser(user1);

        //把用户添加到用户组里面,通过两个UsreId 和 GroupId建立两者的关系
        identityService.createMembership("userId","groupId");
        identityService.createMembership("userId1","groupId");

        //查询一个组下的所有的用户,从用户的UserQuery出发

        List<User> userGroup =  identityService.createUserQuery().memberOfGroup("groupId").list();

        for (User item:userGroup){

            System.out.println("组下面的用户=" + item.getId()+ "，当前方法=TestUser.testGruop()");
        }

        //查询一个用户属于哪个组

        List<Group> userBelongGroup = identityService.createGroupQuery().groupMember("userId").list();

        for (Group item:userBelongGroup){

            System.out.println("用户属于的组编号是：---" + item.getId() + "，当前方法=TestUser.testGruop()");

        }

        //使用原始的sql查询数据
        List<User> list = identityService.createNativeUserQuery().sql("select *from act_id_user").list();

        for (User u : list){

            System.out.println("u的值是：---" + u.getFirstName()+u.getEmail()+ "，当前方法=TestUser.testGruop()");
        }


    }

    //释放对象
    @After
    public void destory(){

        if(activitiRule != null)
        {

            IdentityService identityService = activitiRule.getIdentityService();

            identityService.deleteUser("userId");
            identityService.deleteUser("userId1");
            identityService.deleteGroup("groupId");

            activitiRule = null;
        }
    }

}
