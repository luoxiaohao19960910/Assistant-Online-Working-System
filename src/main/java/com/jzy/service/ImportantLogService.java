package com.jzy.service;

import com.github.pagehelper.PageInfo;
import com.jzy.model.LogLevelEnum;
import com.jzy.model.dto.ImportantLogDetailedDto;
import com.jzy.model.dto.MyPage;
import com.jzy.model.dto.search.ImportantLogSearchCondition;
import com.jzy.model.entity.ImportantLog;
import com.jzy.model.entity.User;

import java.util.List;

/**
 * @InterfaceName ImportantLogService
 * @Author JinZhiyun
 * @Description 持久化到数据库的重要日志业务
 * @Date 2020/1/31 9:35
 * @Version 1.0
 **/
public interface ImportantLogService {
    /**
     * 插入一条重要日志
     *
     * @param importantLog 重要日志对象
     * @return 1."failure"：错误入参等异常
     * 2."success": 更新成功
     */
    String insertOneImportantLog(ImportantLog importantLog);

    /**
     * 查询符合条件日志信息
     *
     * @param myPage    分页{页号，每页数量}
     * @param condition 查询条件入参
     * @return 分页结果
     */
    PageInfo<ImportantLogDetailedDto> listImportantLog(MyPage myPage, ImportantLogSearchCondition condition);

    /**
     * 根据id的列表删除多条日志
     *
     * @param ids 日志id的列表
     * @return 更新记录数
     */
    long deleteManyImportantLogByIds(List<Long> ids);

    /**
     * 条件删除多条日志记录
     *
     * @param condition 输入的查询条件
     * @return 更新记录数
     */
    long deleteImportantLogByCondition(ImportantLogSearchCondition condition);

    /**
     * 持久化日志，日志级别默认——info，用户为当前会话用户，ip、备注为空
     *
     * @param msg  日志消息正文
     * @return 记录日志成功？
     */
    boolean saveImportantLogBySessionUser(String msg);

    /**
     * 持久化日志，日志级别默认——info，用户为当前会话用户，备注为空
     *
     * @param msg  日志消息正文
     * @param ip   触发日志事件的客户端ip
     * @return 记录日志成功？
     */
    boolean saveImportantLogBySessionUser(String msg, String ip);

    /**
     * 持久化日志，用户为当前会话用户，ip、备注为空
     *
     * @param msg  日志消息正文
     * @param level 日志级别
     * @param ip   触发日志事件的客户端ip
     * @return 记录日志成功？
     */
    boolean saveImportantLogBySessionUser(String msg, LogLevelEnum level, String ip);

    /**
     * 持久化日志，用户为当前会话用户
     *
     * @param msg    日志消息正文
     * @param level  日志级别
     * @param ip     触发日志事件的客户端ip
     * @param remark 备注
     * @return 记录日志成功？
     */
    boolean saveImportantLogBySessionUser(String msg, LogLevelEnum level, String ip, String remark);

    /**
     * 持久化日志，日志级别默认——info，备注为空
     *
     * @param msg  日志消息正文
     * @param user 触发日志事件的用户
     * @param ip   触发日志事件的客户端ip
     * @return 记录日志成功？
     */
    boolean saveImportantLog(String msg, User user, String ip);

    /**
     * 持久化日志，备注为空
     *
     * @param msg   日志消息正文
     * @param level 日志级别
     * @param user  触发日志事件的用户
     * @param ip    触发日志事件的客户端ip
     * @return 记录日志成功？
     */
    boolean saveImportantLog(String msg, LogLevelEnum level, User user, String ip);

    /**
     * 持久化日志
     *
     * @param msg    日志消息正文
     * @param level  日志级别
     * @param user   触发日志事件的用户
     * @param ip     触发日志事件的客户端ip
     * @param remark 备注
     * @return 记录日志成功？
     */
    boolean saveImportantLog(String msg, LogLevelEnum level, User user, String ip, String remark);
}
