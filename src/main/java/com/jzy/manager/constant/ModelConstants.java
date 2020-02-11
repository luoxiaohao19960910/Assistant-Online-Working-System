package com.jzy.manager.constant;

/**
 * @author JinZhiyun
 * @version 1.0
 * @ClassName ModelConstants
 * @description springmvc的model的key
 * @date 2019/11/19 16:50
 **/
public final class ModelConstants {
    private ModelConstants() {
    }

    /**
     * 值为游客登录问题所对应的model键
     */
    public static final String QUESTION_MODEL_KEY = "question";

    /**
     * 值为所有用户角色所对应的model键
     */
    public static final String ROLES_MODEL_KEY = "roles";

    /**
     * 值为所有校区名称所对应的model键
     */
    public static final String CAMPUS_NAMES_MODEL_KEY = "campusNames";

    /**
     * 值为所有季度所对应的model键
     */
    public static final String SEASONS_MODEL_KEY = "seasons";

    /**
     * 值为所有季度分期所对应的model键
     */
    public static final String SUB_SEASONS_MODEL_KEY = "subSeasons";

    /**
     * 值为所有年级所对应的model键
     */
    public static final String GRADES_MODEL_KEY = "grades";

    /**
     * 值为所有学科所对应的model键
     */
    public static final String SUBJECTS_MODEL_KEY = "subjects";

    /**
     * 值为所有班型所对应的model键
     */
    public static final String TYPES_MODEL_KEY = "types";

    /**
     * 值为所有日志级别所对应的model键
     */
    public static final String LEVELS_MODEL_KEY = "levels";

    /**
     * 值为当前开课年份季度分期所对应的model键
     */
    public static final String CURRENT_ClASS_SEASON_MODEL_KEY = "currentClassSeason";

    /**
     * 通过"查看该班学生"功能跳转学生查询页面时，传递的班级编码的model键
     */
    public static final String CLASS_ID_MODEL_KEY = "classId";

    /**
     * 通过"开班做表"功能跳转做表页面时，传递的班级校区的model键
     */
    public static final String CLASS_CAMPUS_MODEL_KEY = "classCampus";

    /**
     * 通过"开补课单"功能跳转做表页面时，存放的学生上课信息的model键
     */
    public static final String STUDENT_AND_CLASS_MODEL_KEY = "studentAndClass";

    /**
     * 跳转编辑用户iframe页面时，存放的model键
     */
    public static final String USER_EDIT_MODEL_KEY = "userEdit";

    /**
     * 跳转编辑角色权限iframe页面时，存放的model键
     */
    public static final String PERMISSION_EDIT_MODEL_KEY = "permissionEdit";

    /**
     * 跳转编辑助教iframe页面时，存放的model键
     */
    public static final String ASSISTANT_EDIT_MODEL_KEY = "assistantEdit";

    /**
     * 跳转编辑班级iframe页面时，存放的model键
     */
    public static final String CLASS_EDIT_MODEL_KEY = "classEdit";

    /**
     * 跳转预览班级iframe页面时，存放的model键
     */
    public static final String CLASS_PREVIEW_MODEL_KEY = "classPreview";

    /**
     * 跳转系统公告推送页面时，存放的model键
     */
    public static final String ANNOUNCEMENT_EDIT_MODEL_KEY = "announcementEdit";

    /**
     * 跳转收费公告推送页面时，存放的model键
     */
    public static final String PAY_ANNOUNCEMENT_EDIT_MODEL_KEY = "payAnnouncementEdit";

    /**
     * 跳转主页面时，存放的关于公告信息的model键
     */
    public static final String ANNOUNCEMENT_MODEL_KEY = "announcement";

    /**
     * 跳转页面时，存放的关于收费公告信息的model键
     */
    public static final String PAY_ANNOUNCEMENT_MODEL_KEY = "payAnnouncement";

    /**
     * 跳转控制台页面时，存放的关于常用信息归属类别的model键
     */
    public static final String USEFUL_INFORMATION_BELONG_TO_MODEL_KEY = "belongTo";

    /**
     * 跳转编辑常用信息iframe页面时，存放的model键
     */
    public static final String USEFUL_INFORMATION_EDIT_MODEL_KEY = "usefulInformationEdit";

    /**
     * 跳转控制台时，存放的系统负载信息的model键
     */
    public static final String SYSTEM_LOAD_MODEL_KEY = "systemLoad";

    /**
     * 跳转用户消息的详细内容时，存放的消息对象的model键
     */
    public static final String USER_MESSAGE_DTO_MODEL_KEY = "userMessageDtoDetail";

    /**
     * 跳转用户消息中心页面时，存放的未读消息数量的model键
     */
    public static final String UNREAD_USER_MESSAGE_COUNT_MODEL_KEY = "countUnread";

    /**
     * 跳转发送消息页面时，存放的被发送方信息的model键
     */
    public static final String USER_SEND_TO_MODEL_KEY = "userSendTo";

    /**
     * 跳转发送消息页面时，存放的展示给用户看的被发送方信息的model键
     */
    public static final String USER_SEND_TO_SHOW_MODEL_KEY = "userSendToShow";

    /**
     * 跳转发送消息页面时，存放的回复标题的model键
     */
    public static final String REPLY_TITLE_MODEL_KEY = "replyTitle";

    /**
     * 跳转批量发送消息页面时，存放的被发送方id列表信息的model键
     */
    public static final String USER_SEND_TO_IDS_MODEL_KEY = "userSendToIds";

    /**
     * 跳转做表班号入参不存在提示页面时，错误班号的model键
     */
    public static final String WRONG_CLASS_ID = "wrongClassId";

    /**
     * 跳转导出学生联系方式页面时，助教姓名的model键
     */
    public static final String ASSISTANT_NAME = "assistantName";
}
