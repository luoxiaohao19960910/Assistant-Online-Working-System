package com.jzy.model.dto;

import lombok.Data;

/**
 * @ClassName UserWithPayStatus
 * @Author JinZhiyun
 * @Description 带支付状态的用户信息封装
 * @Date 2020/2/12 13:56
 * @Version 1.0
 **/
@Data
public class UserWithPayStatus {
    private static final long serialVersionUID = 4395754667461644045L;

    /**
     * 是否需要支付，否即表示已支付
     */
    private boolean needToPay;

    /**
     * 主键id
     */
    protected Long id;


    /**
     * 用户的工号，即助教的工号，唯一，长度不超过32可以为空
     */
    private String userWorkId;

    /**
     * 用户的身份证，可以为空
     */
    private String userIdCard;

    /**
     * 用户名，唯一，可以自定义，6~20位(数字、字母、下划线)以字母开头
     */
    private String userName;

    /**
     * 用户的真实姓名，非空，不超过50个字符
     */
    private String userRealName;

    /**
     * 用户身份,"管理员","学管", "助教长", "助教", "教师", "游客"
     */
    private String userRole;

    /**
     * 用户邮箱，唯一，空或者长度于等于100
     */
    private String userEmail;

    /**
     * 用户手机，唯一，空或者11位数字
     */
    private String userPhone;
}
