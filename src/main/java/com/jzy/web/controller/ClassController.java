package com.jzy.web.controller;

import com.jzy.model.entity.Class;
import com.jzy.model.vo.AutoCompleteSearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @ClassName ClassController
 * @Author JinZhiyun
 * @Description 班级业务控制器（所有角色通用，不涉及权限问题）
 * @Date 2019/11/22 12:23
 * @Version 1.0
 **/
@Controller
@RequestMapping("/class")
public class ClassController extends AbstractController {
    private final static Logger logger = LogManager.getLogger(ClassController.class);

    /**
     * 查询校区下的所有教室
     *
     * @param campusName 校区名称
     * @return
     */
    @GetMapping("/listClassroomsByCampus")
    @ResponseBody
    public List<String> listClassroomsByCampus(@RequestParam(value = "campusName", required = false) String campusName) {
        return campusAndClassroomService.listClassroomsByCampus(campusName);
    }

    /**
     * 根据输入班级编码解析班级校区季度班型等信息，返回解析后的class对象
     *
     * @param classId 班级编码
     * @return 解析后的class对象（作为json返回）
     */
    @GetMapping("/getParsedClassByParsingClassId")
    @ResponseBody
    public Class getParsedClassByParsingClassId(@RequestParam(value = "classId", required = false) String classId) {
        Class clazz = new Class();
        clazz.setParsedClassId(classId);
        return clazz;
    }

    /**
     * 获取模糊匹配的班级编码，且不区分大小写。
     * 即输入classId，先置为全部大写，在从数据库like模糊查询。
     * 再将查询到的所有结果按照"年份-季度-分期"降序排列，即越近期越靠前。
     * 将所有结果进一步封装成ClassIdSearchResult返回前端。
     *
     * @param classId 输入字符串作为关键字
     * @return 符合条件的数据 {@link AutoCompleteSearchResult}
     */
    @GetMapping("/getClassesLikeClassId")
    @ResponseBody
    public Map<String, Object> getClassesLikeClassId(@RequestParam(value = "keywords", required = false) String classId) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("code", 0);
        map.put("msg", "");

        classId = StringUtils.upperCase(classId);
        List<Class> classes = classService.listClassesLikeClassId(classId);
        List<AutoCompleteSearchResult> results = new ArrayList<>();
        //按常识上的开课时间有近至远排序
        Collections.sort(classes, Class.CLASS_YEAR_SEASON_SUB_SEASON_COMPARATOR_DESC);

        for (Class clazz : classes) {
            clazz.setParsedClassYear();
            if (clazz.getClassYear() == null) {
                clazz.setClassYear("");
            }
            if (clazz.getClassSeason() == null) {
                clazz.setClassSeason("");
            }
            if (clazz.getClassSubSeason() == null) {
                clazz.setClassSubSeason("");
            }

            String season = clazz.getClassYear() + clazz.getClassSeason() + clazz.getClassSubSeason();
            season = StringUtils.isEmpty(season) ? "" : season + "_";

            String campus = StringUtils.isEmpty(clazz.getClassCampus()) ? "" : clazz.getClassCampus() + "_";

            String name = season + campus + clazz.getClassName();
            AutoCompleteSearchResult result = new AutoCompleteSearchResult(clazz.getClassId(), name);
            results.add(result);
        }
        map.put("data", results);
        return map;
    }
}
