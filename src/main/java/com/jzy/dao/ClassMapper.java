package com.jzy.dao;

import com.jzy.model.dto.ClassDetailedDto;
import com.jzy.model.dto.search.ClassSearchCondition;
import com.jzy.model.entity.Class;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author JinZhiyun
 * @version 1.0
 * @IntefaceName ClassMapper
 * @description 班级业务dao接口
 * @date 2019/11/13 16:18
 **/
public interface ClassMapper {
    /**
     * 根据班级id查询班级
     *
     * @param id 班级id
     * @return 对应班级对象
     */
    Class getClassById(@Param("id") Long id);

    /**
     * 根据班级编码查询班级,，注意这里classId不是主键id
     *
     * @param classId 班级编码
     * @return 对应班级对象
     */
    Class getClassByClassId(@Param("classId") String classId);

    /**
     * 修改班级信息由班级编码修改，注意这里classId不是主键id
     *
     * @param classDetailedDto 修改后的班级信息
     * @return 更新记录数
     */
    long updateClassByClassId(ClassDetailedDto classDetailedDto);

    /**
     * 添加班级
     *
     * @param classDetailedDto 新添加班级的信息
     * @return 更新记录数
     */
    long insertOneClass(ClassDetailedDto classDetailedDto);

    /**
     * 返回符合条件的班级信息分页结果
     *
     * @param condition  查询条件入参
     * @return 符合条件的班级详细信息集合
     */
    List<ClassDetailedDto> listClasses(ClassSearchCondition condition);

    /**
     * 返回所有的班级编码
     *
     * @return 所有班号的list
     */
    List<String> listAllClassIds();

    /**
     * 修改班级信息请求，由id修改
     *
     * @param classDetailedDto 修改后的班级信息
     * @return 更新记录数
     */
    long updateClassInfo(ClassDetailedDto classDetailedDto);

    /**
     * 删除一个班级
     *
     * @param id 被删除班级的id
     * @return 更新记录数
     */
    long deleteOneClassById(Long id);

    /**
     * 根据班级编码查询班级详细信息，注意这里classId不是主键id
     *
     * @param classId 班级编码
     * @return 班级的详细信息
     */
    ClassDetailedDto getClassDetailByClassId(@Param("classId") String classId);

    /**
     * 根据id删除多个班级
     *
     * @param ids 班级id的列表
     * @return 更新记录数
     */
    long deleteManyClassesByIds(List<Long> ids);

    /**
     * 根据输入条件删除指定的班级
     *
     * @param condition 输入条件封装
     * @return 更新记录数
     */
    long deleteClassesByCondition(ClassSearchCondition condition);

    /**
     * 根据班级编码模糊查询班级,注意这里classId不是主键id
     *
     * @param classId 班级编码
     * @return like入参班号的所有班级对象
     */
    List<Class> listClassesLikeClassId(@Param("classId") String classId);

    /**
     * 根据班级编码模糊查询返回匹配的班级编码,注意这里classId不是主键id
     *
     * @param classId 班级编码
     * @return like入参班号的所有班号
     */
    List<String> listClassIdsLikeClassId(@Param("classId") String classId);

    /**
     * 根据班级id查询班级详细信息，注意这里是主键id
     *
     * @param id 班级id
     * @return 对应id的班级详细信息
     */
    ClassDetailedDto getClassDetailById(@Param("id") Long id);

    /**
     * 返回所有的班级
     *
     * @return 所有的班级
     */
    List<Class> listAllClasses();
}
