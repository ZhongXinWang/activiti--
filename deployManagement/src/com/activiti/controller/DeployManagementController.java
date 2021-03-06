package com.activiti.controller;

import com.demo.util.R;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.MemberKey;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.cmd.AddCommentCmd;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.FilenameUtils;
import org.apache.ibatis.javassist.compiler.MemberResolver;
import org.codehaus.groovy.tools.shell.util.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * Created by @author 王钟鑫
 *
 * @create 2018/1/20.
 */
@Controller
public class DeployManagementController {

    private Logger log = Logger.create(DeployManagementController.class);

    //流程部署实例
    private RepositoryService repositoryService;

    @Resource
    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @RequestMapping(value = "/deployments",method = RequestMethod.GET)
    public String index(Model model){

       /* repositoryService.createDeployment().addClasspathResource("diagram/HelloWorld.bpmn")
                .addClasspathResource("diagram/HelloWorld.png").deploy();*/

        //获得流程定义对象
        List<ProcessDefinition> dataList = repositoryService.createProcessDefinitionQuery().list();

        model.addAttribute("dataList",dataList);

        return "deploy/index";
    }
    /*
    *
    * 通过上传的文件zip,bpmn,png文件来部署流程
    * @Param  前端form表单的file对象
    *
    * */

    @RequestMapping(value = "/deployment/files",method = RequestMethod.POST)
    public String deploy(@RequestParam("file")MultipartFile file){

        if(file != null){

            String originName = file.getOriginalFilename();
            try {

                InputStream inputStream = file.getInputStream();
                //获取到扩展名
                String type = FilenameUtils.getExtension(originName);

                //创建部署对象
                DeploymentBuilder deployment = repositoryService.createDeployment();

                //如果是zip，bar类型需要使用Zip对象来进行部署
                if(type.equals("zip") || type.equals("bar")){

                    ZipInputStream zip = new ZipInputStream(inputStream);
                    deployment.addZipInputStream(zip);

                    //其他文件直接部署
                }else{

                    deployment.addInputStream(originName,inputStream);

                }
                //部署
                deployment.deploy();

                return "redirect:deployments";

            } catch (IOException e) {

                e.printStackTrace();

            }

        }
        return "error";

    }
    /*
    *
    * 获取到部署的xml或者是png图片
    * @Param  deploymentId   部署Id
      @Param  resourceName 资源的名称
    *
    * */
    @RequestMapping(value = "/deployment/resources",method = RequestMethod.GET)
    public void getXmlResource(@RequestParam("id") String deploymentId,@RequestParam("name") String resourceName, HttpServletResponse response){

        //获取到查询对象
        InputStream resourceAsStream = repositoryService.getResourceAsStream(deploymentId, resourceName);

        byte[] b = new byte[1024];
        int len = -1;
        try {
            while ((len = resourceAsStream.read(b,0,1024)) != -1){

                response.getOutputStream().write(b,0,len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /*
    * 删除部署的流程
    * @Param deploymentId  部署Id
    *
    * */
    @ResponseBody
    @RequestMapping(value = "/deployment/{id}",method = RequestMethod.DELETE)
    public Object delete(@PathVariable("id")String deploymentId){

        try {
            repositoryService.deleteDeployment(deploymentId,true);
            return R.success();
        }catch (Exception e){

            e.printStackTrace();

        }

        return R.failure();


    }

    /*
    *
    * 跳转到新建模型对象，这里需要创建一个modelId
    * */
    @RequestMapping(value = "/deployment/model/management",method = RequestMethod.POST)
    public void toCreate(@RequestParam("name") String name,@RequestParam("description") String description,@RequestParam("key") String key,HttpServletRequest request, HttpServletResponse response){


        try {

            ObjectMapper mapper = new ObjectMapper();//创建jackson对象
            ObjectNode editorNode = mapper.createObjectNode();//创建节点
            editorNode.put("id", "canvas");                //添加数据
            editorNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = mapper.createObjectNode();
            stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.put("stencilset", stencilSetNode);
            org.activiti.engine.repository.Model modelData = repositoryService.newModel();  //使用repositoryService创建model

            ObjectNode modelObjectNode = mapper.createObjectNode();
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            modelData.setMetaInfo(modelObjectNode.toString());
            modelData.setName(name);
            modelData.setKey(key);

            //保存模型
            repositoryService.saveModel(modelData);
            repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
            //跳转到modeler.html并传递刚刚生成的modelId
            response.sendRedirect(request.getContextPath() + "/plugins/procerss-editor/modeler.html?modelId=" + modelData.getId());
        }catch (Exception e){

            System.err.println("出现了异常");
        }

    }
     /*
      * @Author:王钟鑫
      * @Description:获取到所有的模型
      * @Param:
      * @Date:2018/1/27_10:42
      *
      * */
    @RequestMapping(value="/deployment/model/management",method = RequestMethod.GET)
    public  String manage(Model model){

        List<org.activiti.engine.repository.Model> dataList = repositoryService.createModelQuery()
                                                              .orderByCreateTime().desc().list();

        model.addAttribute("dataList",dataList);

        return "deploy/model";

    }

     /*
      * @Author:王钟鑫
      * @Description:获取到模型的xml信息或者png的数据
      * @Param:modelId图片资源id,editorSourceValueId  获取xml，editorSourceExtraValueId 获取 png图片
      * @Date:2018/1/27_14:30
      *
      * */
     @RequestMapping(value = "/deployment/model/resources",method = RequestMethod.GET)
     public void getModelXmlResource(@RequestParam("id") String modelId,@RequestParam(value = "vid" ,defaultValue = "",required = false) String editorSourceValueId,@RequestParam(value = "evid" ,defaultValue = "",required = false) String editorSourceExtraValueId, HttpServletResponse response){

         //获取到对应的模型
         org.activiti.engine.repository.Model model = repositoryService.getModel(modelId);
         byte[] modelEditorSourceExtra = null;

         //查询模型的原数据 xml
         if(!editorSourceValueId.equals("")){


             modelEditorSourceExtra = repositoryService.getModelEditorSource(model.getId());
             JsonNode editNode = null;
             try {
                 editNode = new ObjectMapper().readTree(modelEditorSourceExtra); //读取节点
                 BpmnJsonConverter jsonConverter = new BpmnJsonConverter();//
                 BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editNode);
                 modelEditorSourceExtra = new BpmnXMLConverter().convertToXML(bpmnModel);//转化为对应的xml
             } catch (IOException e) {
                 e.printStackTrace();
             }

         }else{ //获取到png图片

             modelEditorSourceExtra = repositoryService.getModelEditorSourceExtra(model.getId());

         }

         //输出到界面
         InputStream put = new ByteArrayInputStream(modelEditorSourceExtra);

         byte[] b = new byte[1024];
         int len = -1;
         try {
             while ((len = put.read(b,0,1024)) != -1){

                 response.getOutputStream().write(b,0,len);
             }

         } catch (IOException e) {
             e.printStackTrace();
         }

     }
     /*
      * @Author:王钟鑫
      * @Description:部署流程
      * @Param:modelId 模型编号
      * @Date:2018/1/27_15:21
      *
      * */
    @RequestMapping(value = "/deployment/model/deployment/{id}",method = RequestMethod.PUT)
    public @ResponseBody  Object deployByModel(@PathVariable("id") String modelId){

        try {
            org.activiti.engine.repository.Model model = repositoryService.getModel(modelId);

            byte[] modelEditorSource = repositoryService.getModelEditorSource(model.getId());

            //把json进行转化为xml
            JsonNode editNode = null;
            editNode = new ObjectMapper().readTree(modelEditorSource); //读取节点
            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();//
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editNode);
            modelEditorSource = new BpmnXMLConverter().convertToXML(bpmnModel);//转化为对应的xml


            String processName = model.getName() + ".bpmn20.xml";
            //部署


            repositoryService.createDeployment().name(model.getName())
                                .addString(processName,new String(modelEditorSource,"utf-8"))
                                .enableDuplicateFiltering()
                                .deploy();
            return R.success();

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }catch (Exception e){

            e.printStackTrace();
        }

        return R.failure();

    }
    /*
  * 删除model
  * @Param deploymentId  部署Id
  *
  * */
    @ResponseBody
    @RequestMapping(value = "/deployment/model/{id}",method = RequestMethod.DELETE)
    public Object deleteModel(@PathVariable("id")String modelId){

        try {
            repositoryService.deleteModel(modelId);
            return R.success();
        }catch (Exception e){

            e.printStackTrace();

        }

        return R.failure();


    }


}
