package com.jzy.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JinZhiyun
 * @version 1.0
 * @ClassName RoleEnum
 * @description 用户角色枚举
 * @date 2019/12/1 9:29
 **/
public enum RoleEnum {
    ADMINISTRATOR("管理员"),ASSISTANT_MANAGER("学管"),ASSISTANT_MASTER("助教长")
    , ASSISTANT("助教"), TEACHER("教师"), GUEST("游客");

    private String role;

    public String getRole() {
        return role;
    }

    RoleEnum(String role) {
        this.role=role;
    }

    /**
     * 获取所有角色列表
     *
     * @return ["管理员","学管",.....]
     */
    public static List<String> getRolesList() {
        List<String> list = new ArrayList<>();
        for (RoleEnum roleEnum : RoleEnum.values()) {
            list.add(roleEnum.getRole());
        }
        return list;
    }

    /**
     * 判断当前输入角色字串是否存在
     *
     * @param role 输入角色字串
     * @return
     */
    public static boolean hasRole(String role) {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleEnum.getRole().equals(role)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断当前枚举是否与角色（字符串）相同
     *
     * @param role 字符串类型的角色
     * @return
     */
    public boolean equals(String role){
        if (this.getRole().equals(role)){
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(RoleEnum.ADMINISTRATOR.equals("管理"));
    }
}
