package com.jzy.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jzy.dao.StudentAndClassMapper;
import com.jzy.manager.constant.Constants;
import com.jzy.manager.exception.InvalidParameterException;
import com.jzy.manager.util.StudentAndClassUtils;
import com.jzy.model.dto.*;
import com.jzy.model.dto.echarts.*;
import com.jzy.model.entity.Class;
import com.jzy.model.entity.Student;
import com.jzy.model.entity.StudentAndClass;
import com.jzy.model.vo.echarts.NamesAndValues;
import com.jzy.service.StudentAndClassService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName StudentAndClassServiceImpl
 * @Author JinZhiyun
 * @Description 学生上课业务实现
 * @Date 2019/11/23 18:06
 * @Version 1.0
 **/
@Service
public class StudentAndClassServiceImpl extends AbstractServiceImpl implements StudentAndClassService {
    private final static Logger logger = LogManager.getLogger(StudentAndClassServiceImpl.class);

    @Autowired
    private StudentAndClassMapper studentAndClassMapper;

    @Override
    public StudentAndClass getStudentAndClassById(Long id) {
        return id == null ? null : studentAndClassMapper.getStudentAndClassById(id);
    }

    @Override
    public Long countStudentAndClassByStudentIdAndClassId(String studentId, String classId) {
        return (StringUtils.isEmpty(studentId) || StringUtils.isEmpty(classId)) ? 0 : studentAndClassMapper.countStudentAndClassByStudentIdAndClassId(studentId, classId);
    }

    @Override
    public UpdateResult insertStudentAndClass(StudentAndClassDetailedDto studentAndClassDetailedDto) {
        if (countStudentAndClassByStudentIdAndClassId(studentAndClassDetailedDto.getStudentId(), studentAndClassDetailedDto.getClassId()) > 0) {
            //重复报班
            return new UpdateResult("studentAndClassExist");
        }

        return insertNewStudentAndClass(studentAndClassDetailedDto);
    }

    /**
     * 插入学员号不重复的学员报班信息
     *
     * @param studentAndClassDetailedDto
     * @return
     */
    private UpdateResult insertNewStudentAndClass(StudentAndClassDetailedDto studentAndClassDetailedDto) {
        Student student = studentService.getStudentByStudentId(studentAndClassDetailedDto.getStudentId());
        if (student == null) {
            //学员号不存在
            return new UpdateResult("studentNotExist");
        }

        Class clazz = classService.getClassByClassId(studentAndClassDetailedDto.getClassId());
        if (clazz == null) {
            //班号不存在
            return new UpdateResult("classNotExist");
        }

        UpdateResult result = new UpdateResult(Constants.SUCCESS);
        long count = studentAndClassMapper.insertStudentAndClass(studentAndClassDetailedDto);
        result.setInsertCount(count);
        return result;
    }


    @Override
    public UpdateResult updateStudentAndClassByStudentIdAndClassId(StudentAndClassDetailedDto studentAndClassDetailedDto) {
        UpdateResult result = new UpdateResult(Constants.SUCCESS);
        long count = studentAndClassMapper.updateStudentAndClassByStudentIdAndClassId(studentAndClassDetailedDto);
        result.setUpdateCount(count);
        return result;
    }

    @Override
    public UpdateResult insertAndUpdateStudentAndClassesFromExcel(List<StudentAndClassDetailedDto> studentAndClassDetailedDtos) throws Exception {
        UpdateResult result = new UpdateResult();

        for (StudentAndClassDetailedDto studentAndClassDetailedDto : studentAndClassDetailedDtos) {
            if (StudentAndClassUtils.isValidStudentAndClassDetailedDtoInfo(studentAndClassDetailedDto)) {
                UpdateResult resultTmp = insertAndUpdateOneStudentAndClassFromExcel(studentAndClassDetailedDto);
                result.add(resultTmp);
            } else {
                String msg = "输入学生花名册表中读取到的studentAndClassDetailedDtos不合法!";
                logger.error(msg);
                throw new InvalidParameterException(msg);
            }
        }
        result.setResult(Constants.SUCCESS);
        return result;
    }

    @Override
    public UpdateResult insertAndUpdateOneStudentAndClassFromExcel(StudentAndClassDetailedDto studentAndClassDetailedDto) throws Exception {
        if (studentAndClassDetailedDto == null) {
            String msg = "insertAndUpdateOneStudentAndClassFromExcel方法输入studentAndClassDetailedDto为null!";
            logger.error(msg);
            throw new InvalidParameterException(msg);
        }

        UpdateResult result = new UpdateResult();

        Long count = countStudentAndClassByStudentIdAndClassId(studentAndClassDetailedDto.getStudentId(), studentAndClassDetailedDto.getClassId());
        if (count > 0) {
            //记录已存在，更新
            result.add(updateStudentAndClassByStudentIdAndClassId(studentAndClassDetailedDto));
        } else {
            //插入
            result.add(insertNewStudentAndClass(studentAndClassDetailedDto));
        }
        result.setResult(Constants.SUCCESS);
        return result;
    }

    @Override
    public PageInfo<StudentAndClassDetailedDto> listStudentAndClasses(MyPage myPage, StudentAndClassSearchCondition condition) {
        PageHelper.startPage(myPage.getPageNum(), myPage.getPageSize());
        List<StudentAndClassDetailedDto> studentAndClassDetailedDtos = studentAndClassMapper.listStudentAndClasses(condition);
        for (int i = 0; i < studentAndClassDetailedDtos.size(); i++) {
            StudentAndClassDetailedDto studentAndClassDetailedDto = studentAndClassDetailedDtos.get(i);
            if (!StringUtils.isEmpty(studentAndClassDetailedDto.getClassYear())) {
                studentAndClassDetailedDto.setClassYear(Class.parseYear(studentAndClassDetailedDto.getClassYear()));
                studentAndClassDetailedDtos.set(i, studentAndClassDetailedDto);
            }
        }
        return new PageInfo<>(studentAndClassDetailedDtos);
    }

    @Override
    public String updateStudentAndClassInfo(StudentAndClassDetailedDto studentAndClassDetailedDto) {
        StudentAndClass originalDto = getStudentAndClassById(studentAndClassDetailedDto.getId());

        //原来的学员编号
        String originalStudentId = studentService.getStudentById(originalDto.getStudentId()).getStudentId();
        String originalClassId = classService.getClassById(originalDto.getClassId()).getClassId();

        if (!studentAndClassDetailedDto.getStudentId().equals(originalStudentId)
                || !studentAndClassDetailedDto.getClassId().equals(originalClassId)) {
            //学员号和班号中的一个修改过了，判断是否与已存在的<学员号, 班号>冲突
            if (countStudentAndClassByStudentIdAndClassId(studentAndClassDetailedDto.getStudentId(), studentAndClassDetailedDto.getClassId()) > 0) {
                //修改后的上课记录已存在
                return "studentAndClassExist";
            }

            if (!studentAndClassDetailedDto.getStudentId().equals(originalStudentId)) {
                //学员号修改过了
                Student student = studentService.getStudentByStudentId(studentAndClassDetailedDto.getStudentId());
                if (student == null) {
                    //学员号不存在
                    return "studentNotExist";
                }
            }

            if (!studentAndClassDetailedDto.getClassId().equals(originalClassId)) {
                //班号修改过了
                Class clazz = classService.getClassByClassId(studentAndClassDetailedDto.getClassId());
                if (clazz == null) {
                    //班号不存在
                    return "classNotExist";
                }
            }
        }

        studentAndClassMapper.updateStudentAndClassInfo(studentAndClassDetailedDto);
        return Constants.SUCCESS;
    }

    @Override
    public long deleteOneStudentAndClassById(Long id) {
        if (id == null) {
            return 0;
        }
        return studentAndClassMapper.deleteOneStudentAndClassById(id);
    }

    @Override
    public long deleteManyStudentAndClassesByIds(List<Long> ids) {
        if (ids == null || ids.size() == 0) {
            return 0;
        }
        return studentAndClassMapper.deleteManyStudentAndClassesByIds(ids);
    }

    @Override
    public List<StudentAndClassDetailedWithSubjectsDto> listStudentAndClassesByClassId(String classId) {
        return StringUtils.isEmpty(classId) ? new ArrayList<>() : studentAndClassMapper.listStudentAndClassesByClassId(classId);
    }

    @Override
    public List<StudentAndClassDetailedWithSubjectsDto> listStudentAndClassesWithSubjectsByClassId(String classId) {
        List<StudentAndClassDetailedWithSubjectsDto> dtos = listStudentAndClassesByClassId(classId);
        //先查出该班级所有学生
        for (StudentAndClassDetailedWithSubjectsDto dto : dtos) {
            StudentAndClassSearchCondition condition = new StudentAndClassSearchCondition();
            condition.setStudentId(dto.getStudentId());
            condition.setClassYear(dto.getClassYear());
            condition.setClassSeason(dto.getClassSeason());
            //查出当前年份-季度下该学生的所有上课记录
            List<StudentAndClassDetailedDto> tmps = studentAndClassMapper.listStudentAndClassesWithSubjectsByStudentId(condition);

            List<String> subjects = new ArrayList<>();
            for (StudentAndClassDetailedDto tmp : tmps) {
                if (!StringUtils.isEmpty(tmp.getClassSubject())) {
                    subjects.add(tmp.getClassSubject());
                }
            }
            //读取设置该学生所有修读的学科
            dto.setSubjects(subjects);
        }
        return dtos;
    }

    @Override
    public UpdateResult deleteStudentAndClassesByCondition(StudentAndClassSearchCondition condition) {
        long count = studentAndClassMapper.deleteStudentAndClassesByCondition(condition);
        UpdateResult result = new UpdateResult(Constants.SUCCESS);
        result.setDeleteCount(count);
        return result;
    }

    @Override
    public NamesAndValues countStudentsGroupByClassGrade(StudentAndClassSearchCondition condition) {
        NamesAndValues namesAndValues = new NamesAndValues();
        List<GroupedByGradeObjectTotal> objectTotals = studentAndClassMapper.countStudentsGroupByClassGrade(condition);
        namesAndValues.addAll(objectTotals);
        return namesAndValues;
    }

    @Override
    public NamesAndValues countStudentsGroupByClassSubject(StudentAndClassSearchCondition condition) {
        NamesAndValues namesAndValues = new NamesAndValues();
        List<GroupedBySubjectObjectTotal> objectTotals = studentAndClassMapper.countStudentsGroupByClassSubject(condition);
        namesAndValues.addAll(objectTotals);
        return namesAndValues;
    }

    @Override
    public List<GroupedByTypeObjectTotal> countStudentsGroupByClassType(StudentAndClassSearchCondition condition) {
        return studentAndClassMapper.countStudentsGroupByClassType(condition);
    }

    @Override
    public List<GroupedByGradeAndTypeObjectTotal> countStudentsGroupByClassGradeAndType(StudentAndClassSearchCondition condition) {
        //先查出各年级对应人数
        List<GroupedByGradeObjectTotal> objectTotals = studentAndClassMapper.countStudentsGroupByClassGrade(condition);
        Collections.sort(objectTotals);

        //结果集
        List<GroupedByGradeAndTypeObjectTotal> byGradeAndTypeObjectTotals = new ArrayList<>();
        for (GroupedByGradeObjectTotal objectTotal : objectTotals) {
            //遍历各年级
            condition.setClassGrade(objectTotal.getName());
            //查出当前年级各班型对应人数
            List<GroupedByTypeObjectTotal> byTypeObjectTotals = studentAndClassMapper.countStudentsGroupByClassType(condition);
            Collections.sort(byTypeObjectTotals);

            //把含有当前年级对应人数，以及该年级下各班型对应人数信息的对象添加到结果集
            byGradeAndTypeObjectTotals.add(new GroupedByGradeAndTypeObjectTotal(objectTotal, byTypeObjectTotals));
        }

        return byGradeAndTypeObjectTotals;
    }

    @Override
    public List<GroupedBySubjectAndTypeObjectTotal> countStudentsGroupByClassSubjectAndType(StudentAndClassSearchCondition condition) {
        //先查出各学科对应人数
        List<GroupedBySubjectObjectTotal> objectTotals = studentAndClassMapper.countStudentsGroupByClassSubject(condition);
        Collections.sort(objectTotals);

        //结果集
        List<GroupedBySubjectAndTypeObjectTotal> bySubjectAndTypeObjectTotals = new ArrayList<>();
        for (GroupedBySubjectObjectTotal objectTotal : objectTotals) {
            //遍历各学科
            condition.setClassSubject(objectTotal.getName());
            //查出当前学科各班型对应人数
            List<GroupedByTypeObjectTotal> byTypeObjectTotals = studentAndClassMapper.countStudentsGroupByClassType(condition);
            Collections.sort(byTypeObjectTotals);

            //把含有当前学科对应人数，以及该学科下各班型对应人数信息的对象添加到结果集
            bySubjectAndTypeObjectTotals.add(new GroupedBySubjectAndTypeObjectTotal(objectTotal, byTypeObjectTotals));
        }

        return bySubjectAndTypeObjectTotals;
    }

}
