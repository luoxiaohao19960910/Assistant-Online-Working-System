package com.jzy.web.controller;

import com.jzy.config.FilePathProperties;
import com.jzy.manager.constant.Constants;
import com.jzy.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Controller;

/**
 * @author JinZhiyun
 * @ClassName BaseController
 * @Description 基础控制器，用来继承。
 * 有如下内容：
 * 1、自动注入所有所需服务层接口
 * 2、自动注入redis模板
 * 之后其他控制类只需继承此类，无需自行注入和设置
 * @Date 2019/6/4 22:39
 * @Version 1.0
 **/
@Controller
public abstract class AbstractController {
    protected static final String SUCCESS = Constants.SUCCESS;

    protected static final String FAILURE = Constants.FAILURE;

    protected static final String UNCHANGED = Constants.UNCHANGED;

    protected static final String UNKNOWN_ERROR = Constants.UNKNOWN_ERROR;

    @Autowired
    protected UserService userService;

    @Autowired
    protected StudentService studentService;

    @Autowired
    protected TeacherService teacherService;

    @Autowired
    protected ClassService classService;

    @Autowired
    protected StudentAndClassService studentAndClassService;

    @Autowired
    protected AssistantService assistantService;

    @Autowired
    protected CampusAndClassroomService campusAndClassroomService;

    @Autowired
    protected RoleAndPermissionService roleAndPermissionService;

    @Autowired
    protected MissLessonStudentService missLessonStudentService;

    @Autowired
    protected QuestionService questionService;

    @Autowired
    protected UsefulInformationService usefulInformationService;

    @Autowired
    protected UserMessageService userMessageService;

    @Autowired
    protected RedisOperation redisOperation;

    @Autowired
    protected ImportantLogService importantLogService;

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    @Autowired
    protected HashOperations<String, String, Object> hashOps;

    @Autowired
    protected ValueOperations<String, Object> valueOps;

    @Autowired
    protected ListOperations<String, Object> listOps;

    @Autowired
    protected SetOperations<String, Object> setOps;

    @Autowired
    protected ZSetOperations<String, Object> zSetOps;

    @Autowired
    protected FilePathProperties filePathProperties;
}
