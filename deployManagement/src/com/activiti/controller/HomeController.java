package com.activiti.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by @author 王钟鑫
 *
 * @create 2018/1/29.
 */
@Controller
@RequestMapping(value="/")
public class HomeController {


    @RequestMapping(method = RequestMethod.GET)
    public String index(){

        return "/index";

    }

}
