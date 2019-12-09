package com.jzy.web.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.jzy.manager.constant.Constants;
import com.jzy.manager.constant.ModelConstants;
import com.jzy.manager.exception.ExcelColumnNotFoundException;
import com.jzy.manager.exception.InputFileTypeException;
import com.jzy.manager.exception.InvalidParameterException;
import com.jzy.manager.util.ClassUtils;
import com.jzy.manager.util.UserMessageUtils;
import com.jzy.model.CampusEnum;
import com.jzy.model.dto.ClassDetailedDto;
import com.jzy.model.dto.ClassSearchCondition;
import com.jzy.model.dto.MyPage;
import com.jzy.model.dto.UpdateResult;
import com.jzy.model.entity.Assistant;
import com.jzy.model.entity.Class;
import com.jzy.model.entity.User;
import com.jzy.model.entity.UserMessage;
import com.jzy.model.excel.Excel;
import com.jzy.model.excel.ExcelVersionEnum;
import com.jzy.model.excel.input.ClassArrangementExcel;
import com.jzy.model.vo.ResultMap;
import com.jzy.model.vo.Speed;
import com.jzy.model.vo.SqlProceedSpeed;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * @ClassName ClassAdminController
 * @Author JinZhiyun
 * @Description 班级管理的控制器
 * @Date 2019/11/24 13:11
 * @Version 1.0
 **/
@Controller
@RequestMapping("/class/admin")
public class ClassAdminController extends AbstractController {
    private final static Logger logger = LogManager.getLogger(ClassAdminController.class);

    /**
     * 导入排班表
     *
     * @param file                上传的文件
     * @param parseClassIdChecked 是否开启自动解析的开关
     * @param clazz               开班年份、校区、季度等信息的封装
     * @return
     */
    @RequestMapping("/import")
    @ResponseBody
    public Map<String, Object> importExcel(@RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("parseClassId") boolean parseClassIdChecked,
                                           @RequestParam("deleteFirst") boolean deleteFirstChecked, Class clazz) throws InvalidParameterException {
        Map<String, Object> map2 = new HashMap<>(1);
        Map<String, Object> map = new HashMap<>();
        //返回layui规定的文件上传模块JSON格式
        map.put("code", 0);
        map2.put("src", "");
        map.put("data", map2);

        if (clazz == null || StringUtils.isEmpty(clazz.getClassYear()) || !ClassUtils.isValidClassYear(clazz.getClassYear())) {
            map.put("msg", "yearInvalid");
            return map;
        }

        if (!ClassUtils.isValidClassSeason(clazz.getClassSeason()) || !ClassUtils.isValidClassCampus(clazz.getClassCampus())) {
            map.put("msg", Constants.FAILURE);
            return map;
        }

        if (file.isEmpty()) {
            String msg = "上传文件为空";
            logger.error(msg);
            throw new InvalidParameterException(msg);
        }


        if (!Excel.isExcel(file.getOriginalFilename())) {
            String msg = "上传文件不是excel";
            logger.error(msg);
            throw new InvalidParameterException(msg);
        }

        long startTime = System.currentTimeMillis();   //获取开始时间
        int excelEffectiveDataRowCount = 0; //表格有效数据行数
        int databaseUpdateRowCount = 0; //数据库更新记录数
        int databaseInsertRowCount = 0; //数据库插入记录数
        int databaseDeleteRowCount = 0; //数据库删除记录数

        ClassArrangementExcel excel = null;
        try {
            excel = new ClassArrangementExcel(file.getInputStream(), ExcelVersionEnum.getVersionByName(file.getOriginalFilename()));
            excelEffectiveDataRowCount = excel.readClassDetailFromExcel();
        } catch (IOException e) {
            e.printStackTrace();
            map.put("msg", Constants.FAILURE);
            return map;
        } catch (ExcelColumnNotFoundException e) {
            e.printStackTrace();
            map.put("msg", "excelColumnNotFound");
            return map;
        } catch (InputFileTypeException e) {
            e.printStackTrace();
            map.put("msg", Constants.FAILURE);
            return map;
        }

        try {
            UpdateResult teacherResult = teacherService.insertAndUpdateTeachersFromExcel(new ArrayList<>(excel.getTeachers()));
            databaseInsertRowCount += (int) teacherResult.getInsertCount();
            databaseUpdateRowCount += (int) teacherResult.getUpdateCount();

            List<ClassDetailedDto> classDetailedDtos = excel.getClassDetailedDtos();
            for (ClassDetailedDto classDetailedDto : classDetailedDtos) {
                classDetailedDto.setClassYear(clazz.getClassYear());
                if (!parseClassIdChecked) {
                    //未开启自动解析
                    if (!StringUtils.isEmpty(clazz.getClassSeason())) {
                        classDetailedDto.setClassSeason(clazz.getClassSeason());
                    }
                    if (!StringUtils.isEmpty(clazz.getClassCampus())) {
                        classDetailedDto.setClassCampus(clazz.getClassCampus());
                    }
                }
            }

            if (deleteFirstChecked) {
                //如果开启先导后删
                if (classDetailedDtos.size() > 0) {
                    ClassDetailedDto dto = classDetailedDtos.get(0);
                    ClassSearchCondition condition = new ClassSearchCondition();
                    condition.setClassYear(dto.getClassYear());
                    condition.setClassSeason(dto.getClassSeason());
                    condition.setClassCampus(dto.getClassCampus());
                    databaseDeleteRowCount += (int) classService.deleteClassesByCondition(condition).getDeleteCount();
                }
            }

            //插入&更新
            UpdateResult classResult = classService.insertAndUpdateClassesFromExcel(classDetailedDtos);
            databaseInsertRowCount += (int) classResult.getInsertCount();
            databaseUpdateRowCount += (int) classResult.getUpdateCount();


            long endTime = System.currentTimeMillis(); //获取结束时间
            Speed speedOfExcelImport = new Speed(excelEffectiveDataRowCount, endTime - startTime);
            SqlProceedSpeed speedOfDatabaseImport = new SqlProceedSpeed(databaseUpdateRowCount, databaseInsertRowCount, databaseDeleteRowCount, endTime - startTime);
            speedOfExcelImport.parseSpeed();
            speedOfDatabaseImport.parseSpeed();
            map.put("excelSpeed", speedOfExcelImport);
            map.put("databaseSpeed", speedOfDatabaseImport);

            //向对应校区的用户发送通知消息
            if (classDetailedDtos.size() > 0) {
                ClassDetailedDto classDetailedDto = classDetailedDtos.get(0);
                String campus = classDetailedDto.getClassCampus();
                if (!StringUtils.isEmpty(campus)) {
                    List<Assistant> assistants = assistantService.listAssistantsByCampus(campus);
                    List<Long> userIds = new ArrayList<>();
                    for (Assistant assistant : assistants) {
                        userIds.add(userService.getUserByWorkId(assistant.getAssistantWorkId()).getId());
                    }

                    for (Long userId : userIds) {
                        UserMessage message = new UserMessage();
                        message.setUserId(userId);
                        message.setUserFromId(userService.getSessionUserInfo().getId());
                        message.setMessageTitle("排班信息有变化");
                        StringBuffer messageContent = new StringBuffer();
                        messageContent.append("你的学管老师刚刚更新了" + classDetailedDto.getClassCampus() + "校区" + classDetailedDto.getClassYear() + "年" + classDetailedDto.getClassSeason() + "的排班表。")
                                .append("<br>点<a lay-href='/class/admin/page' lay-text='班级信息'>这里</a>前往查看。");
                        message.setMessageContent(messageContent.toString());
                        message.setMessageTime(new Date());
                        if (UserMessageUtils.isValidUserMessageUpdateInfo(message)) {
                            userMessageService.insertUserMessage(message);
                        }
                    }
                }
            }


            map.put("msg", Constants.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", Constants.FAILURE);
            return map;
        }

        return map;
    }

    /**
     * 跳转班级管理页面
     *
     * @return
     */
    @RequestMapping("/page")
    public String page(Model model) {
        model.addAttribute(ModelConstants.CURRENT_YEAR_MODEL_KEY, ClassUtils.getCurrentYear());
        model.addAttribute(ModelConstants.CURRENT_SEASON_MODEL_KEY, ClassUtils.getCurrentSeason());

        model.addAttribute(ModelConstants.CAMPUS_NAMES_MODEL_KEY, JSON.toJSONString(CampusEnum.getCampusNamesList()));
        model.addAttribute(ModelConstants.SEASONS_MODEL_KEY, JSON.toJSONString(Class.SEASONS));
        model.addAttribute(ModelConstants.CLASS_IDS_MODEL_KEY, JSON.toJSONString(classService.listAllClassIds()));
        model.addAttribute(ModelConstants.GRADES_MODEL_KEY, JSON.toJSONString(Class.GRADES));
        model.addAttribute(ModelConstants.SUBJECTS_MODEL_KEY, JSON.toJSONString(Class.SUBJECTS));
        model.addAttribute(ModelConstants.TYPES_MODEL_KEY, JSON.toJSONString(Class.TYPES));
        return "class/admin/page";
    }

    /**
     * 查询班级信息的ajax交互
     *
     * @param myPage    分页{页号，每页数量}
     * @param condition 查询条件入参
     * @return
     */
    @RequestMapping("/getClassInfo")
    @ResponseBody
    public ResultMap<List<ClassDetailedDto>> getClassInfo(MyPage myPage, ClassSearchCondition condition) {
        PageInfo<ClassDetailedDto> pageInfo = classService.listClasses(myPage, condition);
        return new ResultMap<>(0, "", (int) pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 查询助教自己的班级信息的ajax交互
     *
     * @param myPage    分页{页号，每页数量}
     * @param condition 查询条件入参
     * @return
     */
    @RequestMapping("/getOwnClassInfoByAssistant")
    @ResponseBody
    public ResultMap<List<ClassDetailedDto>> getOwnClassInfoByAssistant(MyPage myPage, ClassSearchCondition condition) {
        User user = userService.getSessionUserInfo();
        condition.setAssistantWorkId(user.getUserWorkId());
        PageInfo<ClassDetailedDto> pageInfo = classService.listClasses(myPage, condition);
        return new ResultMap<>(0, "", (int) pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 重定向到编辑班级iframe子页面并返回相应model
     *
     * @param model
     * @param clazz 当前要被编辑的班级信息
     * @return
     */
    @RequestMapping("/updateForm")
    public String updateForm(Model model, Class clazz) {
        model.addAttribute(ModelConstants.CAMPUS_NAMES_MODEL_KEY, JSON.toJSONString(CampusEnum.getCampusNamesList()));
        model.addAttribute(ModelConstants.SEASONS_MODEL_KEY, JSON.toJSONString(Class.SEASONS));
        model.addAttribute(ModelConstants.GRADES_MODEL_KEY, JSON.toJSONString(Class.GRADES));
        model.addAttribute(ModelConstants.SUBJECTS_MODEL_KEY, JSON.toJSONString(Class.SUBJECTS));
        model.addAttribute(ModelConstants.TYPES_MODEL_KEY, JSON.toJSONString(Class.TYPES));

        model.addAttribute(ModelConstants.CLASS_EDIT_MODEL_KEY, clazz);
        return "class/admin/classForm";
    }

    /**
     * 班级管理中的编辑班级请求，由id修改
     *
     * @param classDetailedDto 修改后的班级信息
     * @return
     */
    @RequestMapping("/updateById")
    @ResponseBody
    public Map<String, Object> updateById(ClassDetailedDto classDetailedDto) throws InvalidParameterException {
        Map<String, Object> map = new HashMap<>(1);

        if (!ClassUtils.isValidClassDetailedDtoInfo(classDetailedDto)) {
            String msg = "updateById方法错误入参";
            logger.error(msg);
            throw new InvalidParameterException(msg);
        }

        map.put("data", classService.updateClassInfo(classDetailedDto));

        return map;
    }

    /**
     * 班级管理中的添加班级请求
     *
     * @param classDetailedDto 新添加班级的信息
     * @return
     */
    @RequestMapping("/insert")
    @ResponseBody
    public Map<String, Object> insert(ClassDetailedDto classDetailedDto) throws InvalidParameterException {
        Map<String, Object> map = new HashMap<>(1);

        if (!ClassUtils.isValidClassUpdateInfo(classDetailedDto)) {
            String msg = "insert方法错误入参";
            logger.error(msg);
            throw new InvalidParameterException(msg);
        }
        map.put("data", classService.insertClass(classDetailedDto).getResult());

        return map;
    }

    /**
     * 删除一个班级ajax交互
     *
     * @param id 被删除班级的id
     * @return
     */
    @RequestMapping("/deleteOne")
    @ResponseBody
    public Map<String, Object> deleteOne(@RequestParam("id") Long id) {
        Map<String, Object> map = new HashMap(1);

        classService.deleteOneClassById(id);
        map.put("data", Constants.SUCCESS);
        return map;
    }

    /**
     * 删除多个班级ajax交互
     *
     * @param classes 多个班级的json串，用fastjson转换为list
     * @return
     */
    @RequestMapping("/deleteMany")
    @ResponseBody
    public Map<String, Object> deleteMany(@RequestParam("classes") String classes) {
        Map<String, Object> map = new HashMap(1);

        List<ClassDetailedDto> classesParsed = JSON.parseArray(classes, ClassDetailedDto.class);
        List<Long> ids = new ArrayList<>();
        for (ClassDetailedDto classDetailedDto : classesParsed) {
            ids.add(classDetailedDto.getId());
        }
        classService.deleteManyClassesByIds(ids);
        map.put("data", Constants.SUCCESS);
        return map;
    }

    /**
     * 条件删除多个助教ajax交互
     *
     * @param condition 输入的查询条件
     * @return
     */
    @RequestMapping("/deleteByCondition")
    @ResponseBody
    public Map<String, Object> deleteByCondition(ClassSearchCondition condition) {
        Map<String, Object> map = new HashMap(1);
        map.put("data", classService.deleteClassesByCondition(condition).getResult());
        return map;
    }


    /**
     * 重定向到预览班级信息iframe子页面并返回相应model
     *
     * @param model
     * @param clazz 当前输入的含班号的班级信息
     * @return
     */
    @RequestMapping("/getPreviewClassInfo")
    public String getPreviewClassInfo(Model model, Class clazz) {
        ClassDetailedDto classDetailedDto = classService.getClassDetailByClassId(clazz.getClassId());
        if (classDetailedDto == null) {
            classDetailedDto = new ClassDetailedDto();
        }

        model.addAttribute(ModelConstants.CLASS_PREVIEW_MODEL_KEY, classDetailedDto);
        return "class/admin/classPreviewForm";
    }
}
