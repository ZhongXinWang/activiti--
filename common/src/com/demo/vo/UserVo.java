package com.demo.vo;

import java.io.Serializable;

/**
 * Created by @author 王钟鑫
 *
 * @create 2018/1/30.
 */
public class UserVo implements Serializable {

    private String id;
    private String name;
    private String email;
    private String groupId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "UserVo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", groupId='" + groupId + '\'' +
                '}';
    }
}
