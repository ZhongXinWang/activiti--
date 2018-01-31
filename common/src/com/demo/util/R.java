package com.demo.util;

/**
 * Created by @author 王钟鑫
 *
 * @create 2018/1/24.
 */
public class R {

    private String msg;
    private Object data;
    private boolean success;

    public R(String msg){

        this.msg = msg;

    }
    public R(String msg,boolean success){

        this.msg = msg;
        this.success = success;

    }

    public static R success(){

        return new R("操作成功",true);

    }
    public static R failure(){

        return new R("操作失败",false);

    }
    public static R success(Object data){

        R r =  new R("操作成功",true);
        r.setData(data);
        return r;

    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
