package com.jzy.web.controller;

import com.jzy.model.entity.Student;
import com.jzy.model.vo.AutoCompleteSearchResult;
import com.jzy.model.vo.StudentSearchResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName StudentController
 * @Author JinZhiyun
 * @Description 学生业务控制器（所有角色通用，不涉及权限问题）
 * @Date 2020/2/26 13:14
 * @Version 1.0
 **/
@Controller
@RequestMapping("/student")
public class StudentController extends AbstractController {
    /**
     * 获取模糊匹配的学生姓名，且不区分大小写。
     *
     * @param studentName 输入字符串作为关键字
     * @return 符合条件的数据 {@link AutoCompleteSearchResult}
     */
    @RequestMapping("/getStudentsLikeStudentName")
    @ResponseBody
    public Map<String, Object> getStudentsLikeStudentName(@RequestParam(value = "keywords", required = false) String studentName) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("code", 0);
        map.put("msg", "");


        List<Student> students = studentService.listStudentsLikeStudentName(studentName);
        List<StudentSearchResult> results = new ArrayList<>();

        for (Student student : students) {
            if (student.getStudentPhone() == null) {
                student.setStudentPhone("");
            }

            StudentSearchResult result = new StudentSearchResult();
            result.setValue(student.getStudentName());
            result.setSubValue(student.getStudentId());
            result.setStudentProperties(student);
            results.add(result);
        }
        map.put("data", results);
        return map;
    }


    /**
     * 获取模糊匹配的学生编号，且不区分大小写。
     *
     * @param studentId 输入字符串作为关键字
     * @return 符合条件的数据 {@link AutoCompleteSearchResult}
     */
    @RequestMapping("/getStudentsLikeStudentId")
    @ResponseBody
    public Map<String, Object> getStudentsLikeStudentId(@RequestParam(value = "keywords", required = false) String studentId) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("code", 0);
        map.put("msg", "");

        studentId = StringUtils.upperCase(studentId);
        List<Student> students = studentService.listStudentsLikeStudentId(studentId);
        List<StudentSearchResult> results = new ArrayList<>();

        for (Student student : students) {
            if (student.getStudentPhone() == null) {
                student.setStudentPhone("");
            }

            StudentSearchResult result = new StudentSearchResult();
            result.setValue(student.getStudentId());
            result.setSubValue(student.getStudentName());
            result.setStudentProperties(student);
            results.add(result);
        }
        map.put("data", results);
        return map;
    }
}
