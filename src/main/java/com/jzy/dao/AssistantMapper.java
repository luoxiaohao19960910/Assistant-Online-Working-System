package com.jzy.dao;

import com.jzy.model.dto.search.AssistantSearchCondition;
import com.jzy.model.dto.ClassSeasonDto;
import com.jzy.model.entity.Assistant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author JinZhiyun
 * @version 1.0
 * @IntefaceName AssistantMapper
 * @description 助教业务dao接口
 * @date 2019/11/13 16:17
 **/
public interface AssistantMapper {
    /**
     * 根据助教id查询出助教信息
     *
     * @param id 助教id
     * @return 对应助教
     */
    Assistant getAssistantById(@Param("id") Long id);

    /**
     * 根据助教工号查询出助教信息
     *
     * @param assistantWorkId 助教工号
     * @return 对应助教
     */
    Assistant getAssistantByWorkId(@Param("assistantWorkId") String assistantWorkId);

    /**
     * 根据助教姓名查询出助教信息
     *
     * @param assistantName 助教姓名
     * @return 对应助教
     */
    Assistant getAssistantByName(@Param("assistantName") String assistantName);

    /**
     * 插入助教
     *
     * @param assistant 新添加助教的信息
     * @return 更新记录数
     */
    long insertOneAssistant(Assistant assistant);

    /**
     * 修改助教信息由id修改
     *
     * @param assistant 修改后的助教信息
     * @return 更新记录数
     */
    long updateAssistantInfo(Assistant assistant);

    /**
     * 修改助教信息由工号修改
     *
     * @param assistant 修改后的助教信息
     * @return 更新记录数
     */
    long updateAssistantByWorkId(Assistant assistant);

    /**
     * 查询符合条件的助教信息
     *
     * @param condition  查询条件入参
     * @return 结果集合
     */
    List<Assistant> listAssistants(AssistantSearchCondition condition);

    /**
     * 根据id删除一个助教
     *
     * @param id  被删除助教的id
     * @return 更新记录数
     */
    long deleteOneAssistantById(@Param("id") Long id);

    /**
     * 根据id删除多个助教
     *
     * @param ids 助教id的列表
     * @return 更新记录数
     */
    long deleteManyAssistantsByIds(List<Long> ids);

    /**
     * 根据输入条件删除指定的助教
     *
     * @param condition 输入条件封装
     * @return 更新记录数
     */
    long deleteAssistantsByCondition(AssistantSearchCondition condition);

    /**
     * 根据助教校区查询出助教信息
     *
     * @param campus 助教校区
     * @return 指定校区的全部助教
     */
    List<Assistant> listAssistantsByCampus(@Param("campus") String campus);

    /**
     * 根据开课的年份季度分期和助教校区查询出助教信息。如果某入参为空，该字段不作为sql查询约束
     *
     * @param classSeasonDto 开课的年份季度分期
     * @param campus 助教校区
     * @return 指定开课的年份季度分期和校区的全部助教
     */
    List<Assistant> listAssistantsByClassSeasonAndCampus(@Param("classSeasonDto") ClassSeasonDto classSeasonDto, @Param("campus") String campus);

    /**
     * 根据助教姓名模糊查询助教
     *
     * @param assistantName 助教姓名关键字
     * @return 符合条件的助教列表
     */
    List<Assistant> listAssistantsLikeAssistantName(@Param("assistantName") String assistantName);

    /**
     * 列出所有助教
     *
     * @return 所有助教列表
     */
    List<Assistant> listAllAssistants();
}
