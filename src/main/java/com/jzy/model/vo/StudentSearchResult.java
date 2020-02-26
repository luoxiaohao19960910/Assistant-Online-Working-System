package com.jzy.model.vo;

import com.jzy.model.entity.Student;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName StudentSearchResult
 * @Author JinZhiyun
 * @Description 学生查询AutoComplete下拉input搜索结果的封装
 * @Date 2020/2/26 13:17
 * @Version 1.0
 **/
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class StudentSearchResult extends AutoCompleteSearchResult {
    private static final long serialVersionUID = 4307487776717095505L;

    private String studentName;

    private String studentId;

    private String studentPhone;

    public void setStudentProperties(Student student) {
        if (student != null) {
            studentName = student.getStudentName();
            studentId = student.getStudentId();
            studentPhone = student.getStudentPhone();
        }
    }
}
