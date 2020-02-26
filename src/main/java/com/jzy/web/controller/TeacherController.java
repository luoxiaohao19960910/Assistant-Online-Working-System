package com.jzy.web.controller;

import com.jzy.model.entity.Teacher;
import com.jzy.model.vo.AutoCompleteSearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName TeacherController
 * @Author JinZhiyun
 * @Description 教师业务控制器（所有角色通用，不涉及权限问题）
 * @Date 2020/2/26 11:11
 * @Version 1.0
 **/
@Controller
@RequestMapping("/teacher")
public class TeacherController extends AbstractController {
    /**
     * 获取模糊匹配的教师姓名，且不区分大小写。
     *
     * @param teacherName 输入字符串作为关键字
     * @return 符合条件的数据 {@link AutoCompleteSearchResult}
     */
    @RequestMapping("/getTeachersLikeTeacherName")
    @ResponseBody
    public Map<String, Object> getTeachersLikeTeacherName(@RequestParam(value = "keywords", required = false) String teacherName) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("code", 0);
        map.put("msg", "");


        List<Teacher> teachers = teacherService.listTeachersLikeTeacherName(teacherName);
        List<AutoCompleteSearchResult> results = new ArrayList<>();

        for (Teacher teacher : teachers) {
            if (teacher.getTeacherPhone() == null) {
                teacher.setTeacherPhone("");
            }

            AutoCompleteSearchResult result = new AutoCompleteSearchResult(teacher.getTeacherName(), teacher.getTeacherPhone());
            results.add(result);
        }
        map.put("data", results);
        return map;
    }
}
