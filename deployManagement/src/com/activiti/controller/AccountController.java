package com.activiti.controller;

import com.alibaba.fastjson.JSON;
import com.demo.util.R;
import com.demo.vo.UserVo;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * Created by @author 王钟鑫
 *
 * @create 2018/1/29.
 */
@Controller
@RequestMapping("/accounts")
public class AccountController {

    private IdentityService identityService;


    @Resource
    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

     /*
      * @Author:王钟鑫
      * @Description:获取到用户的列表
      * @Param:
      * @Date:2018/1/29_14:13
      *
      * */

    @RequestMapping()
    public String index(Model model){

        List<User> dataList = identityService.createUserQuery().list();

        model.addAttribute("dataList",dataList);

        return "/account/user_list";

    }
    @RequestMapping(value = "/users",method = RequestMethod.GET)
    @ResponseBody
    public Object userList(){

        List<User> dataList = identityService.createUserQuery().list();

      //  Map<String,Object> map = new HashMap<String, Object>();
       // map.put("rows",dataList);
       // map.put("total",dataList.size());

        return dataList;

    }

    @RequestMapping("/users/{id}")
    public String toUserForm(@PathVariable(value = "id") String id,Model model){

        List<Group> dataList = identityService.createGroupQuery().list();
        //获取到user的值
        User user = identityService.createUserQuery().userId(id).singleResult();

        if(!"add".equals(id)) {
            //获得用户组id
            List<Group> list = identityService.createGroupQuery().groupMember(id).list();

            System.out.println(list.size());
            UserVo userVo = new UserVo();
            userVo.setId(user.getId());
            userVo.setName(user.getFirstName());
            userVo.setEmail(user.getEmail());
            userVo.setGroupId(list.get(0).getId());
            model.addAttribute("user", userVo);
        }
        model.addAttribute("operator",id);
        model.addAttribute("dataList",dataList);


        return "/account/user_form";

    }

     /*
      * @Author:王钟鑫
      * @Description:添加用户操作
      * @Param:用户Vo对象
      * @Date:2018/1/30_16:16
      *
      * */
    @RequestMapping(value = "/users",method = RequestMethod.POST)
    @ResponseBody
    public Object saveUser(UserVo userVo){

        //新增用户
        try{

            String uuid = UUID.randomUUID().toString();
            User user = identityService.newUser(uuid);
            user.setFirstName(userVo.getName());
            user.setEmail(userVo.getEmail());
            identityService.saveUser(user);
            //建立和组之间的关联
            try {

               identityService.createMembership(uuid, userVo.getGroupId());

            }catch (RuntimeException e){


            }
            return R.success();


        }catch (Exception e){

        }

      return R.failure();

    }
    /*
    * @Author:王钟鑫
    * @Description:添加用户操作
    * @Param:用户Vo对象
    * @Date:2018/1/30_16:16
    *
    * */
    @RequestMapping(value = "/users/{id}",method = RequestMethod.POST)
    @ResponseBody
    public Object updateUser(UserVo userVo){

        //更新用户
        try{
            //先要查询对应的用户
            User user = identityService.createUserQuery().userId(userVo.getId()).singleResult();
            user.setFirstName(userVo.getName());
            user.setEmail(userVo.getEmail());
            identityService.saveUser(user);
            //更新分组
            boolean isExist = false;
            String oldGroup = "";
            //获取用户的所属分组
            List<Group> group = identityService.createGroupQuery().groupMember(userVo.getId()).list();
            //若是找到分组相同的，不需要更改值
            for (Group item : group) {

                if(item.getId() != userVo.getGroupId()){

                    isExist = true;
                    oldGroup = item.getId();
                    break;

                }

            }
            if(isExist){

                //删除旧的关系
                identityService.deleteMembership(userVo.getId(),oldGroup);
                identityService.createMembership(userVo.getId(),userVo.getGroupId());

            }


            return R.success();


        }catch (Exception e){

        }

        return R.failure();

    }
    /*
   * @Author:王钟鑫
   * @Description:添加用户操作
   * @Param:用户Vo对象
   * @Date:2018/1/30_16:16
   *
   * */
    @RequestMapping(value = "/users",method = RequestMethod.DELETE)
    @ResponseBody
    public Object deleteUser(@RequestBody  String id){

        try {
            List<String> ids = JSON.parseArray(id, String.class);

            //批量删除

            for (String s : ids) {

                System.out.println(s);
                identityService.deleteUser(s);
                //删除关系
                List<Group> groups = identityService.createGroupQuery().groupMember(s).list();

                for (Group group : groups) {

                    identityService.deleteMembership(s,group.getId());

                }
            }

            return R.success();

        }catch (Exception e){


        }


        return R.failure();
    }


    @RequestMapping(value = "/groups",method = RequestMethod.GET)
    @ResponseBody
    public Object groupList(){

        List<Group> dataList = identityService.createGroupQuery().list();
        return dataList;

    }

}
