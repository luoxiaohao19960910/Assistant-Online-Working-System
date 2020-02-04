package com.jzy.model.excel.template;

import com.jzy.manager.exception.InvalidFileTypeException;
import com.jzy.model.entity.Student;
import com.jzy.model.excel.AbstractTemplateExcel;
import com.jzy.model.excel.ExcelVersionEnum;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName StudentPhoneOutputExcel
 * @Author JinZhiyun
 * @Description 导出的学生联系方式的表格
 * @Date 2020/1/30 16:15
 * @Version 1.0
 **/
@ToString(callSuper = true)
public class StudentPhoneOutputExcel extends AbstractTemplateExcel {
    private static final long serialVersionUID = -4679847794474338161L;

    private static final String NAME = "姓名";
    private static final int COLUMN_INDEX_OF_NAME = 0;
    private static final String PHONE = "联系方式";
    private static final int COLUMN_INDEX_OF_PHONE = 1;

    public StudentPhoneOutputExcel(ExcelVersionEnum version) throws InvalidFileTypeException {
        super(version);
    }

    /**
     * 将学生和联系方式写入表格
     *
     * @param students   所有学生信息
     * @param namePrefix 姓名前缀
     * @param nameSuffix 姓名后缀
     * @return
     */
    public boolean writeStudentPhone(List<Student> students, String namePrefix, String nameSuffix) {
        createSheet();
        int sheetIx = 0;
        int startRow = 0;
        write(sheetIx, startRow, COLUMN_INDEX_OF_NAME, NAME);
        write(sheetIx, startRow, COLUMN_INDEX_OF_PHONE, PHONE);
        for (int i = 0; i < students.size(); i++) {
            int rowIx = i + startRow + 1;
            Student student = students.get(i);
            write(sheetIx, rowIx, COLUMN_INDEX_OF_NAME, namePrefix + (student.getStudentName() == null ? "" : student.getStudentName()) + nameSuffix);
            write(sheetIx, rowIx, COLUMN_INDEX_OF_PHONE, student.getStudentPhone());
        }
        return true;
    }
}
