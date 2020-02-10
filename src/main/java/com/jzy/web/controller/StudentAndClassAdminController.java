package com.jzy.web.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.jzy.manager.constant.Constants;
import com.jzy.manager.constant.ModelConstants;
import com.jzy.manager.exception.InvalidParameterException;
import com.jzy.manager.util.ShiroUtils;
import com.jzy.manager.util.StudentAndClassUtils;
import com.jzy.model.CampusEnum;
import com.jzy.model.LogLevelEnum;
import com.jzy.model.dto.ClassSeasonDto;
import com.jzy.model.dto.MyPage;
import com.jzy.model.dto.StudentAndClassDetailedDto;
import com.jzy.model.dto.search.StudentAndClassSearchCondition;
import com.jzy.model.entity.Class;
import com.jzy.model.vo.ResultMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @ClassName StudentAndClassAdminController
 * @Author JinZhiyun
 * @Description 学生上课情况管理控制器
 * @Date 2019/11/25 17:54
 * @Version 1.0
 **/
@Controller
@RequestMapping("/studentAndClass/admin")
public class StudentAndClassAdminController extends AbstractController {
    private final static Logger logger = LogManager.getLogger(StudentAndClassAdminController.class);

    /**
     * 跳转学员上课信息管理页面。将select表单中需要渲染的选择内容添加至model
     *
     * @return
     */
    @RequestMapping("/page")
    public String page(Model model, Class clazz) {
        model.addAttribute(ModelConstants.CAMPUS_NAMES_MODEL_KEY, JSON.toJSONString(CampusEnum.getCampusNamesList()));
        model.addAttribute(ModelConstants.SEASONS_MODEL_KEY, JSON.toJSONString(Class.SEASONS));
        model.addAttribute(ModelConstants.SUB_SEASONS_MODEL_KEY, JSON.toJSONString(Class.SUB_SEASONS));
        model.addAttribute(ModelConstants.GRADES_MODEL_KEY, JSON.toJSONString(Class.GRADES));
        model.addAttribute(ModelConstants.SUBJECTS_MODEL_KEY, JSON.toJSONString(Class.SUBJECTS));
        model.addAttribute(ModelConstants.TYPES_MODEL_KEY, JSON.toJSONString(Class.TYPES));

        model.addAttribute(ModelConstants.CLASS_ID_MODEL_KEY, clazz.getClassId());

        ClassSeasonDto currentClassSeason = classService.getCurrentClassSeason();
        if (StringUtils.isNotEmpty(clazz.getClassId())) {
            //如果是从'查看该班学生'跳转而来的，手动将指定班级的'年份-季度-分期'set好
            currentClassSeason.setClassYear(clazz.getClassYear());
            currentClassSeason.setClassSeason(clazz.getClassSeason());
            currentClassSeason.setClassSubSeason(clazz.getClassSubSeason());
        }
        model.addAttribute(ModelConstants.CURRENT_ClASS_SEASON_MODEL_KEY, currentClassSeason);
        return "student/sc/admin/page";
    }

    /**
     * 查询学员上课信息的ajax交互。
     * 其中学员编号、班级编码字段查询不分大小写，因此将该字段upperCase置为全部大写（数据库中班级编码统一为全部大写）后传给服务层
     *
     * @param myPage    分页{页号，每页数量}
     * @param condition 查询条件入参
     * @return
     */
    @RequestMapping("/getStudentAndClassInfo")
    @ResponseBody
    public ResultMap<List<StudentAndClassDetailedDto>> getStudentAndClassInfo(MyPage myPage, StudentAndClassSearchCondition condition) {
        condition.setStudentId(StringUtils.upperCase(condition.getStudentId()));
        condition.setClassId(StringUtils.upperCase(condition.getClassId()));
        PageInfo<StudentAndClassDetailedDto> pageInfo = studentAndClassService.listStudentAndClasses(myPage, condition);
        return new ResultMap<>(0, "", (int) pageInfo.getTotal(), pageInfo.getList());
    }


    /**
     * 重定向到转班iframe子页面并返回相应model
     *
     * @return
     */
    @RequestMapping("/updateForm")
    public String updateForm() {
        return "student/sc/admin/studentAndClassFormEdit";
    }

    /**
     * 学员上课信息管理中的编辑学员上课信息请求，由id修改。
     *
     * @param studentAndClassDetailedDto 修改后的学员上课信息
     * @return
     */
    @RequestMapping("/updateById")
    @ResponseBody
    public Map<String, Object> updateById(@RequestParam(value = "currentTime", required = false) String currentTimeSwitch, StudentAndClassDetailedDto studentAndClassDetailedDto, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>(1);

        if (!StudentAndClassUtils.isValidStudentAndClassUpdateDtoInfo(studentAndClassDetailedDto)) {
            String msg = "更新学员上课信息updateById方法错误入参";
            logger.error(msg);
            importantLogService.saveImportantLogBySessionUser(msg, LogLevelEnum.ERROR, ShiroUtils.getClientIpAddress(request));
            throw new InvalidParameterException(msg);
        }

        if (Constants.ON.equals(currentTimeSwitch)) {
            //如果开启了使用当前时间作为进班时间
            studentAndClassDetailedDto.setRegisterTime(new Date());
        }

        map.put("data", studentAndClassService.updateStudentAndClassInfo(studentAndClassDetailedDto));
        return map;
    }

    /**
     * 重定向到报班iframe子页面并返回相应model
     *
     * @param model
     * @return
     */
    @RequestMapping("/insertForm")
    public String insertForm(Model model) {
        return "student/sc/admin/studentAndClassFormAdd";
    }

    /**
     * 学员上课信息管理中的报班请求
     *
     * @param studentAndClassDetailedDto 新添加de 报班信息
     * @return
     */
    @RequestMapping("/insert")
    @ResponseBody
    public Map<String, Object> insert(StudentAndClassDetailedDto studentAndClassDetailedDto, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>(1);

        if (!StudentAndClassUtils.isValidStudentAndClassUpdateDtoInfo(studentAndClassDetailedDto)) {
            String msg = "添加学员上课信息insert方法错误入参";
            logger.error(msg);
            importantLogService.saveImportantLogBySessionUser(msg, LogLevelEnum.ERROR, ShiroUtils.getClientIpAddress(request));
            throw new InvalidParameterException(msg);
        }

        map.put("data", studentAndClassService.insertOneStudentAndClass(studentAndClassDetailedDto).getResult());

        return map;
    }

    /**
     * 删除一个学员上课记录ajax交互
     *
     * @param id 被删除学员上课的id
     * @return
     */
    @RequestMapping("/deleteOne")
    @ResponseBody
    public Map<String, Object> deleteOne(@RequestParam("id") Long id) {
        Map<String, Object> map = new HashMap(1);

        studentAndClassService.deleteOneStudentAndClassById(id);
        map.put("data", SUCCESS);
        return map;
    }

    /**
     * 删除多个学员上课记录ajax交互
     *
     * @param studentAndClasses 多个学员上课记录的json串，用fastjson转换为list
     * @return
     */
    @RequestMapping("/deleteMany")
    @ResponseBody
    public Map<String, Object> deleteMany(@RequestParam("studentAndClasses") String studentAndClasses) {
        Map<String, Object> map = new HashMap(1);

        List<StudentAndClassDetailedDto> studentAndClassesParsed = JSON.parseArray(studentAndClasses, StudentAndClassDetailedDto.class);
        List<Long> ids = new ArrayList<>();
        for (StudentAndClassDetailedDto studentAndClassDetailedDto : studentAndClassesParsed) {
            ids.add(studentAndClassDetailedDto.getId());
        }
        studentAndClassService.deleteManyStudentAndClassesByIds(ids);
        map.put("data", SUCCESS);
        return map;
    }

    /**
     * 条件删除多个学生上课记录ajax交互
     *
     * @param condition 输入的查询条件
     * @return
     */
    @RequestMapping("/deleteByCondition")
    @ResponseBody
    public Map<String, Object> deleteByCondition(StudentAndClassSearchCondition condition) {
        Map<String, Object> map = new HashMap(1);
        map.put("data", studentAndClassService.deleteStudentAndClassesByCondition(condition).getResult());
        return map;
    }
}
