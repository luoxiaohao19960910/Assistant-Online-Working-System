package com.jzy.model.excel.template;

import com.jzy.manager.exception.InputFileTypeException;
import com.jzy.model.dto.StudentAndClassDetailedWithSubjectsDto;
import com.jzy.model.excel.Excel;
import com.jzy.model.excel.ExcelVersionEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * @author JinZhiyun
 * @version 1.0
 * @ClassName SeatTemplate
 * @description 座位表模板的模型类
 * @date 2019/10/30 14:21
 **/
public class SeatTableTemplateExcel extends Excel implements Serializable {
    private static final long serialVersionUID = -3764653590834120925L;

    public SeatTableTemplateExcel() {
    }

    public SeatTableTemplateExcel(String inputFile) throws IOException, InputFileTypeException {
        super(inputFile);
    }

    public SeatTableTemplateExcel(File file) throws IOException, InputFileTypeException {
        super(file);
    }

    public SeatTableTemplateExcel(InputStream inputStream, ExcelVersionEnum version) throws IOException, InputFileTypeException {
        super(inputStream, version);
    }

    public SeatTableTemplateExcel(Workbook workbook) {
        super(workbook);
    }

    /**
     * 删除除输入classroom外的其他教室的sheet
     *
     * @param classroom 教室号
     * @return
     * @throws IOException
     */
    private boolean deleteOtherSheets(String classroom) throws IOException {
        if (StringUtils.isEmpty(classroom)){
            return false;
        }
        int start = 0;
        int totalSheetCount = this.getSheetCount();
        for (int i = 0; i < totalSheetCount; i++) {
            if (this.getSheetName(start).equals(classroom)) {
                start++;
            } else {
                this.removeSheetAt(start);
            }
        }
        return true;
    }

    /**
     * 根据输入学生名单列表修改对应教室的座位
     *
     * @param data 从数据库中读取到的信息或手动输入的表格中读到的信息，以及用户输入的信息
     * @return
     * @throws IOException
     */
    public boolean writeSeatTable(List<StudentAndClassDetailedWithSubjectsDto> data) throws IOException {
        StudentAndClassDetailedWithSubjectsDto dto = new StudentAndClassDetailedWithSubjectsDto();
        if (data.size() > 0) {
            //取第一个对象为例，获得教室
            dto = data.get(0);
        }
        String classRoom = dto.getClassroom();

        //先把其他没用的教室删掉
        deleteOtherSheets(classRoom);

        //开始依序填座位表
        int targetSheetIndex = 0; //在第0张sheet找
        int rowCount = this.getRowCount(targetSheetIndex);
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < this.getColumnCount(targetSheetIndex, i); j++) { //遍历表格所有行
                String value = this.getValueAt(targetSheetIndex, i, j);
                if (StringUtils.isNumeric(value)) { //对所有为数字的单元格（即座位号）填充姓名
                    int index=Integer.parseInt(value) - 1;
                    if (index<data.size()) {
                        //座位号值大于学生数量的座位不填
                        this.setValueAt(targetSheetIndex, i, j, data.get(Integer.parseInt(value) - 1).getStudentName());
                    }
                }
            }
        }
        return true;
    }
}