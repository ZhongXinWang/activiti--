package com.test.HelloWorld;

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.util.List;

/**
 * Created by @author 王钟鑫
 *
 * @create 2018/1/18.
 */

public class Test {


    @org.junit.Test
    public void createProcessEngine(){


        //创建engine配置器
        ProcessEngine processEngine = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
                                  .setJdbcDriver("com.mysql.jdbc.Driver")
                                  .setJdbcUrl("jdbc:mysql://localhost:3306/activiti")
                                  .setJdbcUsername("root")
                                  .setJdbcPassword("mysqladmin")
                                  .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
                                  .buildProcessEngine();

        System.out.println("processEngine的值是：---" + processEngine + "，当前方法=Test.cteateProcessEngine()");

    }

    @org.junit.Test
    public void  createByXml(){

        //默认创建的  配置文件 是 activiti-cfg.xml
        //所以可以直接使用ProcessEngines来获得一个processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        System.out.println("processEngine的值是：---" + processEngine + "，当前方法=Test.cteateProcessEngine()");

    }

    ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

    //定义和部署流程
    @org.junit.Test
    public void testRepository(){

        //processEngine 获取到流程的RepositoryService
       RepositoryService repositoryService =  processEngine.getRepositoryService();
        //获得一个流程部署构造器
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();

        //开始部署 添加流程名字-》加载流程对应的资源文件 -》部署
        Deployment deployment = deploymentBuilder.name("helloWorld入门")
                              .addClasspathResource("diagram/HelloWorld.bpmn")
                              .addClasspathResource("diagram/HelloWorld.png").deploy();


        System.out.println("deployment的值是编号：---" + deployment.getId() + "，当前方法=Test.testRepository()");
        System.out.println("deployment的值是名称：---" + deployment.getName() + "，当前方法=Test.testRepository()");

    }

    //发布工作流
    @org.junit.Test
    public void testRuntime(){

        //获取到runRimeService的服务
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //根据Id启动流程
       ProcessInstance processInstance =  runtimeService.startProcessInstanceByKey("myProcess_1");

        System.out.println("processInstance的流程Id值是：---" + processInstance.getId()+ "，当前方法=Test.testRuntime()");
        System.out.println("processInstance的流程DeploymentId值是：---" + processInstance.getDeploymentId()+ "，当前方法=Test.testRuntime()");
        System.out.println("processInstance的流程name值是：---" + processInstance.getName()+ "，当前方法=Test.testRuntime()");

    }

    //获取任务
    @org.junit.Test
    public void testTask(){

        String user = "李四";   //任务执行人

       //获取任务的service
       TaskService taskService =  processEngine.getTaskService();
       //查看任务  获取到任务列表
       List<Task> taskList =  taskService.createTaskQuery().taskAssignee(user).list();

        //输出所有的任务
        for(Task task:taskList){

            System.out.println("task的Id值是：---" + task.getId() + "，当前方法=Test.testTask()");
            System.out.println("task的name值是：---" + task.getName()+ "，当前方法=Test.testTask()");
            System.out.println("task的任务执行者值是：---" + task.getAssignee()+ "，当前方法=Test.testTask()");
        }

    }

    @org.junit.Test
    public void testComplete(){

        //根据任务的ID完成相应的任务
        String taskId = "12502";    //任务编号，根据你的taskId不同自己改变
        //获取任务的service
        TaskService taskService =  processEngine.getTaskService();

        //完成任务
        taskService.complete(taskId);

        System.out.println("完成任务的Id=" + taskId);

    }

}
