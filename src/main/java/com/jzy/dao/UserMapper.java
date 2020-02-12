package com.jzy.dao;

import com.jzy.model.dto.search.UserSearchCondition;
import com.jzy.model.dto.search.UserSendToSearchCondition;
import com.jzy.model.entity.User;
import com.jzy.model.dto.UserSendTo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author JinZhiyun
 * @version 1.0
 * @IntefaceName UserMapper
 * @description 用户业务dao接口
 * @date 2019/11/13 16:17
 **/
public interface UserMapper {
    /**
     * 根据用户表自增主键id查询出用户信息
     *
     * @param id 用户表自增主键
     * @return 对应用户对象
     */
    User getUserById(@Param("id") Long id);

    /**
     * 根据用户名查询出用户信息
     *
     * @param userName 用户名
     * @return 对应用户对象
     */
    User getUserByName(@Param("userName") String userName);

    /**
     * 根据用户工号查询出用户信息
     *
     * @param userWorkId 用户工号
     * @return 对应用户对象
     */
    User getUserByWorkId(@Param("userWorkId") String userWorkId);

    /**
     * 根据用户身份证查询出用户信息
     *
     * @param userIdCard 用户身份证
     * @return 对应用户对象
     */
    User getUserByIdCard(@Param("userIdCard") String userIdCard);

    /**
     * 根据用户邮箱查询出用户信息
     *
     * @param userEmail 用户邮箱
     * @return 对应用户对象
     */
    User getUserByEmail(@Param("userEmail") String userEmail);

    /**
     * 根据用户电话查询出用户信息
     *
     * @param userPhone 用户电话
     * @return 对应用户对象
     */
    User getUserByPhone(@Param("userPhone") String userPhone);

    /**
     * 由邮箱更新对应的用户密码
     *
     * @param userEmail 用户邮箱
     * @param userPassword 用户新密码（密文），直接set到数据库
     * @return 更新记录数
     */
    long updatePasswordByEmail(@Param("userEmail") String userEmail, @Param("userPassword") String userPassword);

    /**
     * 用户自己更新用户信息，只有部分字段，注意不是用户管理中的更新
     *
     * @param user 用户信息
     * @return 更新记录数
     */
    long updateOwnInfo(User user);

    /**
     * 根据id修改用户邮箱
     *
     * @param id 用户id
     * @param userEmail 新安全邮箱
     * @return 更新记录数
     */
    long updateEmailById(@Param("id") Long id, @Param("userEmail") String userEmail);

    /**
     * 根据id修改用户手机
     *
     * @param id 用户id
     * @param userPhone 新安全手机
     * @return 更新记录数
     */
    long updatePhoneById(@Param("id") Long id,@Param("userPhone") String userPhone);

    /**
     * 根据id修改用户密码，这里密码是密文，加密操作在service层完成
     *
     * @param id 用户id
     * @param userPassword 用户密文密码
     * @return 更新记录数
     */
    long updatePasswordById(@Param("id") Long id,@Param("userPassword") String userPassword);

    /**
     * 查询所有符合条件的用户信息
     *
     * @param condition 查询条件封装
     * @return 符合条件的用户信息
     */
    List<User> listUsers(UserSearchCondition condition);

    /**
     * 根据用户id更新用户的其他字段
     *
     * @param user 修改后的用户信息
     * @return 更新记录数
     */
    long updateUserInfo(User user);

    /**
     * 插入新用户
     *
     * @param user 新用户信息
     * @return 更新记录数
     */
    long insertOneUser(User user);

    /**
     * 根据id删除一个用户
     *
     * @param id 用户id
     * @return 更新记录数
     */
    long deleteOneUserById(@Param("id") Long id);

    /**
     * 根据工号更新用户信息
     *
     * @param user 新的用户信息
     * @return 更新记录数
     */
    long updateUserByWorkId(User user);

    /**
     * 根据id删除多个用户
     *
     * @param ids 用户id的list
     * @return 更新记录数
     */
    long deleteManyUsersByIds(List<Long> ids);

    /**
     * 查询出所有的用户
     *
     * @return 所有用户集合
     */
    List<User> listAllUsers();

    /**
     * 发送消息页面，用户综合查询
     *
     * @param condition 查询条件入参
     * @return 符合条件的用户信息 {@link UserSendTo}
     */
    List<UserSendTo> listUsersSendTo(UserSendToSearchCondition condition);

    /**
     * 查询出所有的用户id
     *
     * @return 所有用户id集合
     */
    List<Long> listAllUserIds();

    /**
     * 根据指定的id集合查询出相应的用户
     *
     * @param userIds 指定的id集合
     * @return 符合条件用户集合
     */
    List<User> listUsersByIds(List<Long> userIds);
}
