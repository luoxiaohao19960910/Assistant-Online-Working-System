package com.jzy.model.excel.template;

import com.jzy.manager.constant.ExcelConstants;
import com.jzy.manager.exception.ClassTooManyStudentsException;
import com.jzy.manager.exception.InvalidFileTypeException;
import com.jzy.model.dto.StudentAndClassDetailedWithSubjectsDto;
import com.jzy.model.excel.AbstractTemplateExcel;
import lombok.ToString;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.IOException;
import java.util.List;

/**
 * @author JinZhiyun
 * @version 1.0
 * @ClassName AssistantTutorialTemplate
 * @description 助教工作手册模板的模型类
 * @date 2019/11/1 15:28
 **/
@ToString(callSuper = true)
public class AssistantTutorialExcel extends AbstractTemplateExcel {
    private static final long serialVersionUID = 2416400649170324596L;

    private static final String CAMPUS_COLUMN = ExcelConstants.CAMPUS_COLUMN_2;
    private static final String CLASS_ID_COLUMN = ExcelConstants.CLASS_ID_COLUMN_3;
    private static final String TEACHER_NAME_COLUMN = ExcelConstants.TEACHER_NAME_COLUMN_3;
    private static final String ASSISTANT_NAME_COLUMN = ExcelConstants.ASSISTANT_NAME_COLUMN_3;
    private static final String STUDENT_ID_COLUMN = ExcelConstants.STUDENT_ID_COLUMN_2;
    private static final String STUDENT_NAME_COLUMN = ExcelConstants.STUDENT_NAME_COLUMN_2;
    private static final String STUDENT_PHONE_COLUMN = ExcelConstants.STUDENT_PHONE_COLUMN_2;
    private static final String IS_OLD_STUDENT = ExcelConstants.IS_OLD_STUDENT;
    private static final String TEACHER_REQUIREMENT_COLUMN = ExcelConstants.TEACHER_REQUIREMENT_COLUMN;
    private static final String STUDENT_SCHOOL_COLUMN = ExcelConstants.STUDENT_SCHOOL_COLUMN_2;
    private static final String SUBJECTS_COLUMN = ExcelConstants.SUBJECTS_COLUMN;

    /**
     * 班上默认最大人数上限
     */
    public static final int MAX_CLASS_STUDENTS_COUNT = 100;

    /**
     * 开班电话表sheet索引
     */
    private int startClassSheetIndex = getSheetIndex(ExcelConstants.START_CLASS_SHEET);

    /**
     * 签到表sheet索引
     */
    private int signSheetIndex = getSheetIndex(ExcelConstants.SIGN_SHEET);

    /**
     * 信息回访表sheet索引
     */
    private int callbackSheetIndex = getSheetIndex(ExcelConstants.CALLBACK_SHEET);

    public AssistantTutorialExcel(String inputFile) throws IOException, InvalidFileTypeException {
        super(inputFile);
    }

    /**
     * 根据学生在助教班上出现次数决定使用什么颜色填充单元格背景，返回对应颜色的index值
     *
     * @param count 学生在助教班上出现次数
     * @return 对应颜色的index值
     */
    private short getBackgroundColorIndexByStudentOccurCount(int count) {
        if (count == 2) {
            //出现2次，绿色
            return HSSFColor.LIGHT_GREEN.index;
        }
        if (count == 3) {
            //出现3次，蓝色
            return HSSFColor.LIGHT_BLUE.index;
        }
        if (count == 4) {
            //出现4次，粉色
            return HSSFColor.ROSE.index;
        }
        if (count >= 5) {
            //出现5次以上，红色
            return HSSFColor.RED.index;
        }

        return -1;
    }

    /**
     * 修改制作开班电话表
     *
     * @param data 从数据库中读取到的信息或手动输入的表格中读到的信息，以及用户输入的信息
     * @return 写入成功与否
     * @throws IOException                   写excel的io异常
     * @throws ClassTooManyStudentsException 班级的学生人数过多，不能写入模板表格。
     *                                       这里由于模板中只给了100条空行用来放置要写入的数据。因此如果入参行数超过这个阈值会抛出此异常
     */
    public boolean writeStartClassSheet(List<StudentAndClassDetailedWithSubjectsDto> data) throws ClassTooManyStudentsException {
        // 获得班上学生总人数
        int rowCountToSave = data.size();
        if (rowCountToSave > MAX_CLASS_STUDENTS_COUNT) {
            throw new ClassTooManyStudentsException("班上学生人数超过了" + MAX_CLASS_STUDENTS_COUNT + "！");
        }

        int startRow = 0;
        // 先扫描第startRow行找到"校区"、"班号"、"教师姓名"等信息所在列的位置
        int columnIndexOfCampus = -1, columnIndexOfClassId = -1, columnIndexOfTeacherName = -1, columnIndexOfAssistantName = -1, columnIndexOfStudentId = -1, columnIndexOfStudentName = -1, columnIndexOfStudentPhone = -1, columnIndexOfIsOldStudent = -1, columnIndexOfTeacherRequirement = -1, columnIndexOfStudentSchool = -1, columnIndexOfSubjects = -1;
        int row0ColumnCount = getColumnCount(startClassSheetIndex, startRow); // 第startRow行的列数
        for (int i = 0; i < row0ColumnCount; i++) {
            String value = getValueAt(startClassSheetIndex, startRow, i);
            if (value != null) {
                switch (value) {
                    case CAMPUS_COLUMN:
                        columnIndexOfCampus = i;
                        break;
                    case CLASS_ID_COLUMN:
                        columnIndexOfClassId = i;
                        break;
                    case TEACHER_NAME_COLUMN:
                        columnIndexOfTeacherName = i;
                        break;
                    case ASSISTANT_NAME_COLUMN:
                        columnIndexOfAssistantName = i;
                        break;
                    case STUDENT_ID_COLUMN:
                        columnIndexOfStudentId = i;
                        break;
                    case STUDENT_NAME_COLUMN:
                        columnIndexOfStudentName = i;
                        break;
                    case STUDENT_PHONE_COLUMN:
                        columnIndexOfStudentPhone = i;
                        break;
                    case IS_OLD_STUDENT:
                        columnIndexOfIsOldStudent = i;
                        break;
                    case TEACHER_REQUIREMENT_COLUMN:
                        columnIndexOfTeacherRequirement = i;
                        break;
                    case STUDENT_SCHOOL_COLUMN:
                        columnIndexOfStudentSchool = i;
                        break;
                    case SUBJECTS_COLUMN:
                        columnIndexOfSubjects = i;
                        break;
                    default:
                }
            }
        }

        if (columnIndexOfCampus < 0 || columnIndexOfClassId < 0 || columnIndexOfTeacherName < 0 || columnIndexOfAssistantName < 0
                || columnIndexOfStudentId < 0 || columnIndexOfStudentName < 0 || columnIndexOfStudentPhone < 0
                || columnIndexOfTeacherRequirement < 0 || columnIndexOfStudentSchool < 0) {
//                || columnIndexOfSubjects < 0) {
            //列属性中有未匹配的属性名

        }


        for (int i = startRow; i < rowCountToSave + startRow; i++) {
            StudentAndClassDetailedWithSubjectsDto object = data.get(i - startRow);
            //遍历每行要填的学生上课信息对象
            // 填校区
            if (columnIndexOfCampus >= 0) {
                write(startClassSheetIndex, i + 1, columnIndexOfCampus, object.getClassCampus());
            }
            // 填班号
            if (columnIndexOfClassId >= 0) {
                write(startClassSheetIndex, i + 1, columnIndexOfClassId, object.getClassId());
            }
            // 填教师姓名
            if (columnIndexOfTeacherName >= 0) {
                write(startClassSheetIndex, i + 1, columnIndexOfTeacherName, object.getTeacherName());
            }
            // 填助教
            if (columnIndexOfAssistantName >= 0) {
                write(startClassSheetIndex, i + 1, columnIndexOfAssistantName, object.getAssistantName());
            }
            // 填学员编号
            if (columnIndexOfStudentId >= 0) {
                write(startClassSheetIndex, i + 1, columnIndexOfStudentId, object.getStudentId());
            }
            // 填学员姓名
            if (columnIndexOfStudentName >= 0) {
                write(startClassSheetIndex, i + 1, columnIndexOfStudentName, object.getStudentName());
                // 姓名背景色
                int count = object.getCountOfSpecifiedAssistant();
                if (count > 1) {
                    //如果同一助教半夏出现次数大于1才改填充色
                    updateCellBackgroundColor(startClassSheetIndex, i + 1, columnIndexOfStudentName, getBackgroundColorIndexByStudentOccurCount(object.getCountOfSpecifiedAssistant()));
                }
            }
            // 填学员联系方式
            if (columnIndexOfStudentPhone >= 0) {
                write(startClassSheetIndex, i + 1, columnIndexOfStudentPhone, object.getStudentPhone());
            }
            // 填类别，是否老生
            if (columnIndexOfIsOldStudent >= 0) {
                String isOld = object.isOldStudent() ? "老生" : "新生";
                write(startClassSheetIndex, i + 1, columnIndexOfIsOldStudent, isOld);
            }
            // 填任课教师要求
            if (columnIndexOfTeacherRequirement >= 0) {
                write(startClassSheetIndex, i + 1, columnIndexOfTeacherRequirement, object.getClassTeacherRequirement());
            }
            // 填学校
            if (columnIndexOfStudentSchool >= 0) {
                write(startClassSheetIndex, i + 1, columnIndexOfStudentSchool, object.getStudentSchool());
            }
            // 填所有在读学科
            if (columnIndexOfSubjects >= 0) {
                String subjectsToString = object.getSubjects() == null ? "" : object.getSubjects().toString();
                write(startClassSheetIndex, i + 1, columnIndexOfSubjects, subjectsToString);
            }
        }

        // 删除多余行
        removeRows(startClassSheetIndex, rowCountToSave + 4, getRowCount(startClassSheetIndex));

        return true;
    }

    /**
     * 修改制作签到表
     *
     * @param data 从数据库中读取到的信息或手动输入的表格中读到的信息，以及用户输入的信息
     * @return 写入成功与否
     * @throws ClassTooManyStudentsException 班级的学生人数过多，不能写入模板表格。
     *                                       这里由于模板中只给了100条空行用来放置要写入的数据。因此如果入参行数超过这个阈值会抛出此异常
     */
    public boolean writeSignSheet(List<StudentAndClassDetailedWithSubjectsDto> data) throws ClassTooManyStudentsException {
        // 获得班上学生总人数
        int rowCountToSave = data.size();
        if (rowCountToSave > MAX_CLASS_STUDENTS_COUNT) {
            throw new ClassTooManyStudentsException("班上学生人数超过了" + MAX_CLASS_STUDENTS_COUNT + "！");
        }

        StudentAndClassDetailedWithSubjectsDto dto = new StudentAndClassDetailedWithSubjectsDto();
        if (rowCountToSave > 0) {
            //取第一个对象为例，填充表格第一行、第二行
            dto = data.get(0);
        }
        // 填表格第一行
        String str1 = "班号：" + dto.getClassId() + "                  班级名称：" + dto.getClassName();
        write(signSheetIndex, 0, 0, str1);
        // 填表格第二行
        String str2 = "上课时间：" + dto.getClassSimplifiedTime() + "         上课教室：" + dto.getClassroom() +
                "          教师：" + dto.getTeacherName() + "             助教：" + dto.getAssistantName();
        write(signSheetIndex, 1, 0, str2);

        int startRow = 2;
        // 先扫描第startRow行找到"学员编号"、"学员姓名"、"家长联系方式"等信息所在列的位置
        int columnIndexOfStudentId = -1, columnIndexOfStudentName = -1, columnIndexOfStudentPhone = -1;
        int row0ColumnCount = getColumnCount(signSheetIndex, startRow); // 第startRow行的列数
        for (int i = 0; i < row0ColumnCount; i++) {
            String value = getValueAt(signSheetIndex, startRow, i);
            if (value != null) {
                switch (value) {
                    case STUDENT_ID_COLUMN:
                        columnIndexOfStudentId = i;
                        break;
                    case STUDENT_NAME_COLUMN:
                        columnIndexOfStudentName = i;
                        break;
                    case STUDENT_PHONE_COLUMN:
                        columnIndexOfStudentPhone = i;
                        break;
                    default:
                }
            }
        }

        for (int i = startRow; i < rowCountToSave + startRow; i++) {
            StudentAndClassDetailedWithSubjectsDto object = data.get(i - startRow);
            //遍历每行要填的学生上课信息对象
            // 填学员编号
            if (columnIndexOfStudentId >= 0) {
                write(signSheetIndex, i + 1, columnIndexOfStudentId, object.getStudentId());
            }
            // 填学员姓名
            if (columnIndexOfStudentName >= 0) {
                write(signSheetIndex, i + 1, columnIndexOfStudentName, object.getStudentName());
            }
            // 填学员联系方式
            if (columnIndexOfStudentPhone >= 0) {
                write(signSheetIndex, i + 1, columnIndexOfStudentPhone, object.getStudentPhone());
            }
        }

        // 删除多余行
        removeRows(signSheetIndex, rowCountToSave + 4 + startRow + 1,
                getRowCount(signSheetIndex));

        //根据上课次数删除多余列
        //TODO

        return true;
    }

    /**
     * 修改制作信息回访表
     *
     * @param data 从数据库中读取到的信息或手动输入的表格中读到的信息，以及用户输入的信息
     * @return 写入成功与否
     * @throws ClassTooManyStudentsException 班级的学生人数过多，不能写入模板表格。
     *                                       这里由于模板中只给了100条空行用来放置要写入的数据。因此如果入参行数超过这个阈值会抛出此异常
     */
    public boolean writeCallbackSheet(List<StudentAndClassDetailedWithSubjectsDto> data) throws ClassTooManyStudentsException {
        // 获得班上学生总人数
        int rowCountToSave = data.size();
        if (rowCountToSave > MAX_CLASS_STUDENTS_COUNT) {
            throw new ClassTooManyStudentsException("班上学生人数超过了" + MAX_CLASS_STUDENTS_COUNT + "！");
        }

        int startRow = 0;
        // 先扫描第startRow行找到"校区"、"班号"、"教师姓名"等信息所在列的位置
        int columnIndexOfCampus = -1, columnIndexOfClassId = -1, columnIndexOfTeacherName = -1, columnIndexOfAssistantName = -1, columnIndexOfStudentId = -1, columnIndexOfStudentName = -1, columnIndexOfIsOldStudent = -1;
        int row0ColumnCount = getColumnCount(callbackSheetIndex, startRow); // 第startRow行的列数
        for (int i = 0; i < row0ColumnCount; i++) {
            String value = getValueAt(callbackSheetIndex, startRow, i);
            if (value != null) {
                switch (value) {
                    case CAMPUS_COLUMN:
                        columnIndexOfCampus = i;
                        break;
                    case CLASS_ID_COLUMN:
                        columnIndexOfClassId = i;
                        break;
                    case TEACHER_NAME_COLUMN:
                        columnIndexOfTeacherName = i;
                        break;
                    case ASSISTANT_NAME_COLUMN:
                        columnIndexOfAssistantName = i;
                        break;
                    case STUDENT_ID_COLUMN:
                        columnIndexOfStudentId = i;
                        break;
                    case STUDENT_NAME_COLUMN:
                        columnIndexOfStudentName = i;
                        break;
                    case IS_OLD_STUDENT:
                        columnIndexOfIsOldStudent = i;
                        break;
                    default:
                }
            }
        }

        for (int i = startRow; i < rowCountToSave + startRow; i++) {
            StudentAndClassDetailedWithSubjectsDto object = data.get(i - startRow);
            //遍历每行要填的学生上课信息对象
            // 填校区
            if (columnIndexOfCampus >= 0) {
                write(callbackSheetIndex, i + 1, columnIndexOfCampus, object.getClassCampus());
            }
            // 填班号
            if (columnIndexOfClassId >= 0) {
                write(callbackSheetIndex, i + 1, columnIndexOfClassId, object.getClassId());
            }
            // 填教师姓名
            if (columnIndexOfTeacherName >= 0) {
                write(callbackSheetIndex, i + 1, columnIndexOfTeacherName, object.getTeacherName());
            }
            // 填助教
            if (columnIndexOfAssistantName >= 0) {
                write(callbackSheetIndex, i + 1, columnIndexOfAssistantName, object.getAssistantName());
            }
            // 填学员编号
            if (columnIndexOfStudentId >= 0) {
                write(callbackSheetIndex, i + 1, columnIndexOfStudentId, object.getStudentId());
            }
            // 填学员姓名
            if (columnIndexOfStudentName >= 0) {
                write(callbackSheetIndex, i + 1, columnIndexOfStudentName, object.getStudentName());
            }
            // 填类别，是否老生
            if (columnIndexOfIsOldStudent > 0) {
                String isOld = object.isOldStudent() ? "老生" : "新生";
                write(callbackSheetIndex, i + 1, columnIndexOfIsOldStudent, isOld);
            }
        }

        // 删除多余行
        removeRows(callbackSheetIndex, rowCountToSave + 4, getRowCount(callbackSheetIndex));

        return true;
    }


    /**
     * 使用巴啦啦能量！完成对助教工作手册的所有处理（不含座位表）！
     *
     * @param data 从花名册或数据库中读取到的信息以及用户输入的信息
     * @return 写入成功与否
     * @throws ClassTooManyStudentsException 班级的学生人数过多，不能写入模板表格。
     *                                       这里由于模板中只给了100条空行用来放置要写入的数据。因此如果入参行数超过这个阈值会抛出此异常
     */
    public boolean writeAssistantTutorialWithoutSeatTable(List<StudentAndClassDetailedWithSubjectsDto> data) throws ClassTooManyStudentsException {
        boolean r = true;
        if (startClassSheetIndex >= 0) {
            r = r && writeStartClassSheet(data);
        }
        if (signSheetIndex >= 0) {
            r = r && writeSignSheet(data);
        }
        if (callbackSheetIndex >= 0) {
            r = r && writeCallbackSheet(data);
        }
        return r;
    }
}
