package com.jzy.model.excel.template;


import com.jzy.manager.exception.InvalidFileTypeException;
import com.jzy.model.dto.MissManyDaysLessonStudentDetailedDto;
import com.jzy.model.excel.AbstractTemplateExcel;
import lombok.ToString;

import java.io.IOException;

/**
 * @author JinZhiyun
 * @version 1.0
 * @ClassName MissedLessonTemplate
 * @description 补课单模板的模型类
 * @date 2019/11/3 20:26
 **/
@ToString(callSuper = true)
public class MissedLessonExcel extends AbstractTemplateExcel {
    private static final long serialVersionUID = -4232682893945575846L;

    public MissedLessonExcel(String inputFile) throws IOException, InvalidFileTypeException {
        super(inputFile);
    }

    /**
     * 填充补课单，缺1次课
     *
     * @param input         缺课的信息封装
     * @param currentCampus 补课所在校区
     * @return
     */
    public boolean writeMissLesson(MissManyDaysLessonStudentDetailedDto input, String currentCampus)  {
        int missLessonCount = input.getDaysBetween().size();
        return writeMissLesson(input, currentCampus, missLessonCount);
    }

    /**
     * 填充补课单
     *
     * @param input             缺课的信息封装
     * @param currentCampus     补课所在校区
     * @param missedLessonCount 缺课次数
     * @return 写入成功与否
     */
    public boolean writeMissLesson(MissManyDaysLessonStudentDetailedDto input, String currentCampus, int missedLessonCount)  {
        int sheetIx = 0;
        String title = "由于学员___" + input.getStudentName() + "___个人原因在上海新东方上课期间缺课___" + missedLessonCount + "___节，经证实情况属实，允许该生于规定时间内在上海新东方相同类型班级里补上所缺课时。";
        write(sheetIx, 3, 1, title);

        int targetRow = 14;//目标行
        //填所缺课程
        write(sheetIx, targetRow, 3, input.getCurrentClassGrade() + input.getCurrentClassSubject());
        //填补课班号
        write(sheetIx, targetRow, 4, input.getCurrentClassId());
        //填上课时间
        write(sheetIx, targetRow, 5, input.getDaysBetweenToString() + ", " + input.getCurrentClassSimplifiedTime());
        //填上课教室
        write(sheetIx, targetRow, 6, currentCampus + "/" + input.getCurrentClassroom());
        //填原班助教
        write(sheetIx, targetRow, 7, input.getOriginalAssistantName());
        //填补课班助教
        write(sheetIx, targetRow, 8, input.getCurrentAssistantName());

        return true;
    }

}
