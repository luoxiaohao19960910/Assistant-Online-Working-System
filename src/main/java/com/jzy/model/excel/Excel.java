package com.jzy.model.excel;

import com.jzy.manager.exception.InvalidFileTypeException;
import com.jzy.manager.util.MyTimeUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Excel 包装类，对poi的二次封装
 *
 * @author zhangyi
 * @version 1.0 2016/01/27
 */
public abstract class Excel implements Serializable, Resettable, ExcelValidity {
    private static final long serialVersionUID = 5628415838137969509L;

    /**
     * 工作簿对象
     */
    @Getter
    @Setter
    protected Workbook workbook;

    /**
     * excel版本枚举对象
     */
    @Getter
    protected ExcelVersionEnum version;

    /**
     * 输入文件路径
     */
    @Getter
    private String inputFilePath;

    /**
     * 输出流
     */
    private OutputStream os;

    /**
     * 日期格式
     */
    @Getter
    @Setter
    private String datePattern = MyTimeUtils.FORMAT_YMDHMS_BACKUP;

    /**
     * 由输入文件路径构造excel对象
     *
     * @param inputFile 输入文件路径
     * @throws IOException
     * @throws InvalidFileTypeException
     */
    public Excel(String inputFile) throws IOException, InvalidFileTypeException {
        this(new File(inputFile));
    }

    /**
     * 由一个File构造excel对象
     *
     * @param file 输入文件对象
     * @throws IOException
     * @throws InvalidFileTypeException
     */
    public Excel(File file) throws IOException, InvalidFileTypeException {
        String inputFile = file.getAbsolutePath();
        if (inputFile.endsWith(ExcelVersionEnum.VERSION_2003.getSuffix())) {
            version = ExcelVersionEnum.VERSION_2003;
            workbook = new HSSFWorkbook(new FileInputStream(file));
        } else if (inputFile.endsWith(ExcelVersionEnum.VERSION_2007.getSuffix())) {
            version = ExcelVersionEnum.VERSION_2007;
            workbook = new XSSFWorkbook(new FileInputStream(file));
        } else {
            throw new InvalidFileTypeException("输入文件格式不是.xls或.xlsx");
        }
        this.inputFilePath = inputFile;
    }

    /**
     * 由一个输入流和版本枚举对象构造excel对象
     *
     * @param inputStream 输入流对象
     * @param version     excel版本的枚举对象
     * @throws IOException
     * @throws InvalidFileTypeException
     */
    public Excel(InputStream inputStream, ExcelVersionEnum version) throws IOException, InvalidFileTypeException {
        if (ExcelVersionEnum.VERSION_2003.equals(version)) {
            this.version = version;
            workbook = new HSSFWorkbook(inputStream);
        } else if (ExcelVersionEnum.VERSION_2007.equals(version)) {
            this.version = version;
            workbook = new XSSFWorkbook(inputStream);
        } else {
            throw new InvalidFileTypeException("输入文件格式不是.xls或.xlsx");
        }
    }

    /**
     * 由一个工作簿构造excel对象
     *
     * @param workbook 工作簿对象
     */
    public Excel(Workbook workbook) {
        this.workbook = workbook;
    }

    /**
     * 构建指定excel版本的新表格
     *
     * @param version excel版本的枚举对象
     * @throws InvalidFileTypeException 不合法的入参excel版本枚举异常
     */
    public Excel(ExcelVersionEnum version) throws InvalidFileTypeException {
        if (ExcelVersionEnum.VERSION_2003.equals(version)) {
            this.version = version;
            workbook = new HSSFWorkbook();
        } else if (ExcelVersionEnum.VERSION_2007.equals(version)) {
            this.version = version;
            workbook = new XSSFWorkbook();
        } else {
            throw new InvalidFileTypeException("不合法的入参excel版本枚举");
        }
    }

    @Override
    public String toString() {
        return "共有 " + getSheetCount() + "个sheet 页！";
    }

    public String toString(int sheetIx) {
        return "第 " + (sheetIx + 1) + "个sheet 页，名称： " + getSheetName(sheetIx) + "，共 " + getRowCount(sheetIx) + "行！";
    }

    /**
     * 根据后缀判断是否为 Excel 文件，后缀匹配xls和xlsx
     *
     * @param pathname 输入excel路径
     * @return
     */
    public static boolean isExcel(String pathname) {
        if (pathname == null) {
            return false;
        }
        return pathname.endsWith(ExcelVersionEnum.VERSION_2003.getSuffix()) || pathname.endsWith(ExcelVersionEnum.VERSION_2007.getSuffix());
    }

    /**
     * 读取 Excel 第一页所有数据
     *
     * @return
     */
    public List<List<String>> read() {
        return read(0, 0, getRowCount(0) - 1);
    }

    /**
     * 读取指定sheet 页所有数据
     *
     * @param sheetIx 指定 sheet 页，从 0 开始
     * @return
     */
    public List<List<String>> read(int sheetIx) {
        return read(sheetIx, 0, getRowCount(sheetIx) - 1);
    }

    /**
     * 读取指定sheet 页指定行数据
     *
     * @param sheetIx 指定 sheet 页，从 0 开始
     * @param start   指定开始行，从 0 开始
     * @param end     指定结束行，从 0 开始
     * @return
     */
    public List<List<String>> read(int sheetIx, int start, int end) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        List<List<String>> list = new ArrayList<List<String>>();

        if (end > getRowCount(sheetIx)) {
            end = getRowCount(sheetIx);
        }


        for (int i = start; i <= end; i++) {
            List<String> rowList = new ArrayList<String>();
            Row row = sheet.getRow(i);
            if (row != null) {
                int cols = row.getLastCellNum();
                for (int j = 0; j < cols; j++) {
                    rowList.add(getCellValueToString(row.getCell(j)));
                }
            }
            list.add(rowList);
        }

        return list;
    }

    /**
     * 将同一个值value设置在指定区域内的每一个单元格
     *
     * @param sheetIx     sheet号
     * @param value       值
     * @param startColumn 起始列（含）
     * @param endColumn   结束列（不含）
     * @param startRow    起始行（含）
     * @param endRow      结束行（不含）
     * @return
     */
    public boolean write(int sheetIx, int startColumn, int endColumn, int startRow, int endRow, String value) {
        for (int i = startColumn; i < endColumn; i++) {
            for (int j = startRow; j < endRow; j++) {
                write(sheetIx, j, i, value);
            }
        }
        return true;
    }

    /**
     * 设置cell 样式
     *
     * @param sheetIx  指定 Sheet 页，从 0 开始
     * @param rowIndex 指定行，从 0 开始
     * @param colIndex 指定列，从 0 开始
     * @param style    要设置样式
     * @return
     */
    public boolean setStyle(int sheetIx, int rowIndex, int colIndex, CellStyle style) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        // sheet.autoSizeColumn(colIndex, true);// 设置列宽度自适应
//        sheet.setColumnWidth(colIndex, 4000);
        if (isRowNull(sheetIx, rowIndex) || isCellNull(sheetIx, rowIndex, colIndex)) {
            return false;
        }
        Cell cell = sheet.getRow(rowIndex).getCell(colIndex);
        cell.setCellStyle(style);
        return true;
    }

    /**
     * 获得cell样式
     *
     * @param sheetIx  指定 Sheet 页，从 0 开始
     * @param rowIndex 行索引
     * @param colIndex 列索引
     * @return cell样式
     */
    public CellStyle getStyle(int sheetIx, int rowIndex, int colIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return null;
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            return null;
        }
        return cell.getCellStyle();
    }

    /**
     * 设置单元格背景颜色，但不改变单元格原有样式
     *
     * @param sheetIx    指定 Sheet 页，从 0 开始
     * @param rowIndex   行索引
     * @param colIndex   列索引
     * @param colorIndex 颜色的索引值
     * @return
     */
    public boolean updateCellBackgroundColor(int sheetIx, int rowIndex, int colIndex, short colorIndex) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.cloneStyleFrom(getStyle(sheetIx, rowIndex, colIndex));
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);  //填充单元格
        cellStyle.setFillForegroundColor(colorIndex);
        setStyle(sheetIx, rowIndex, colIndex, cellStyle);
        return true;
    }

    /**
     * 合并单元格
     *
     * @param sheetIx  指定 Sheet 页，从 0 开始
     * @param firstRow 开始行
     * @param lastRow  结束行
     * @param firstCol 开始列
     * @param lastCol  结束列
     */
    public void region(int sheetIx, int firstRow, int lastRow, int firstCol, int lastCol) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    /**
     * 指定行是否为空
     *
     * @param sheetIx  指定 Sheet 页，从 0 开始
     * @param rowIndex 指定开始行，从 0 开始
     * @return true 不为空，false 不行为空
     */
    public boolean isRowNull(int sheetIx, int rowIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        return sheet.getRow(rowIndex) == null;
    }

    /**
     * 创建行，若行存在，则清空
     *
     * @param sheetIx  指定 sheet 页，从 0 开始
     * @param rowIndex 指定创建行，从 0 开始
     * @return
     */
    public boolean createRow(int sheetIx, int rowIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        sheet.createRow(rowIndex);
        return true;
    }

    /**
     * 指定单元格是否为空
     *
     * @param sheetIx  指定 Sheet 页，从 0 开始
     * @param rowIndex 指定开始行，从 0 开始
     * @param colIndex 指定开始列，从 0 开始
     * @return true 行不为空，false 行为空
     */
    public boolean isCellNull(int sheetIx, int rowIndex, int colIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        if (isRowNull(sheetIx, rowIndex)) {
            return true;
        }
        Row row = sheet.getRow(rowIndex);
        return row.getCell(colIndex) == null;
    }

    /**
     * 创建单元格
     *
     * @param sheetIx  指定 sheet 页，从 0 开始
     * @param rowIndex 指定行，从 0 开始
     * @param colIndex 指定创建列，从 0 开始
     * @return true 列为空，false 行不为空
     */
    public boolean createCell(int sheetIx, int rowIndex, int colIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        if (isRowNull(sheetIx, rowIndex)) {
            createRow(sheetIx, rowIndex);
        }
        sheet.getRow(rowIndex).createCell(colIndex);
        return true;
    }

    /**
     * 返回sheet 中的行数
     *
     * @param sheetIx 指定 Sheet 页，从 0 开始
     * @return
     */
    public int getRowCount(int sheetIx) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        if (sheet.getPhysicalNumberOfRows() == 0) {
            return 0;
        }
        return sheet.getLastRowNum() + 1;

    }

    /**
     * 返回所在行的列数
     *
     * @param sheetIx  指定 Sheet 页，从 0 开始
     * @param rowIndex 指定行，从0开始
     * @return 返回-1 表示所在行为空
     */
    public int getColumnCount(int sheetIx, int rowIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        Row row = sheet.getRow(rowIndex);
        return row == null ? -1 : row.getLastCellNum();

    }

    /**
     * 设置row 和 column 位置的单元格值
     *
     * @param sheetIx  指定 Sheet 页，从 0 开始
     * @param rowIndex 指定行，从0开始
     * @param colIndex 指定列，从0开始
     * @param value    值
     * @return
     */
    public boolean write(int sheetIx, int rowIndex, int colIndex, String value) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        if (isRowNull(sheetIx, rowIndex)) {
            createRow(sheetIx, rowIndex);
        }
        if (isCellNull(sheetIx, rowIndex, colIndex)) {
            createCell(sheetIx, rowIndex, colIndex);
        }
        sheet.getRow(rowIndex).getCell(colIndex).setCellValue(value);
        return true;
    }

    /**
     * 返回 row 和 column 位置的单元格值
     *
     * @param sheetIx  指定 Sheet 页，从 0 开始
     * @param rowIndex 指定行，从0开始
     * @param colIndex 指定列，从0开始
     * @return
     */
    public String getValueAt(int sheetIx, int rowIndex, int colIndex) {
        if (rowIndex < 0 || colIndex < 0) {
            return null;
        }
        Sheet sheet = workbook.getSheetAt(sheetIx);
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return null;
        }
        return getCellValueToString(row.getCell(colIndex));
    }

    /**
     * 重置指定行的值。从第0列开始
     *
     * @param rowData  数据
     * @param sheetIx  指定 Sheet 页，从 0 开始
     * @param rowIndex 指定行，从0开始
     * @return
     */
    public boolean writeRow(int sheetIx, int rowIndex, List<String> rowData) {
        return writeRow(sheetIx, rowIndex, 0, rowData);
    }

    /**
     * 重置指定行的值
     *
     * @param sheetIx     指定 Sheet 页，从 0 开始
     * @param rowIndex    指定行，从0开始
     * @param startColumn 从第几列开始写
     * @param rowData     数据
     * @return
     */
    public boolean writeRow(int sheetIx, int rowIndex, int startColumn, List<String> rowData) {
        for (int i = startColumn; i < startColumn + rowData.size(); i++) {
            write(sheetIx, rowIndex, i, rowData.get(i - startColumn));
        }
        return true;
    }

    /**
     * 重置指定列的值，从第0行开始写
     *
     * @param columnData  数据
     * @param sheetIx     指定 Sheet 页，从 0 开始
     * @param columnIndex 指定行，从0开始
     * @return
     */
    public boolean writeColumn(int sheetIx, int columnIndex, List<String> columnData) {
        return writeRow(sheetIx, 0, columnIndex, columnData);
    }


    /**
     * 重置指定列的值
     *
     * @param sheetIx     指定 Sheet 页，从 0 开始
     * @param startRow    从第几行开始
     * @param columnIndex 指定行，从0开始
     * @param columnData  数据
     * @return
     */
    public boolean writeColumn(int sheetIx, int startRow, int columnIndex, List<String> columnData) {
        for (int i = startRow; i < startRow + columnData.size(); i++) {
            write(sheetIx, i, columnIndex, columnData.get(i - startRow));
        }
        return true;
    }


    /**
     * 返回指定行的值的集合
     *
     * @param sheetIx  指定 Sheet 页，从 0 开始
     * @param rowIndex 指定行，从0开始
     * @return
     */
    public List<String> getRowValue(int sheetIx, int rowIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        List<String> list = new ArrayList<String>();
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            list.add(null);
        } else {
            for (int i = 0; i < row.getLastCellNum(); i++) {
                list.add(getCellValueToString(row.getCell(i)));
            }
        }
        return list;
    }

    /**
     * 返回列的值的集合，从startRowIndex行开始
     *
     * @param sheetIx       指定 Sheet 页，从 0 开始
     * @param startRowIndex 指定行，从0开始
     * @param colIndex      指定列，从0开始
     * @return
     */
    public List<String> getColumnValue(int sheetIx, int startRowIndex, int colIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        List<String> list = new ArrayList<String>();
        if (sheet == null) {
            return list;
        }
        for (int i = startRowIndex; i < getRowCount(sheetIx); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                list.add(null);
                continue;
            }
            list.add(getCellValueToString(row.getCell(colIndex)));
        }
        return list;
    }

    /**
     * 获取excel 中sheet 总页数
     *
     * @return
     */
    public int getSheetCount() {
        return workbook.getNumberOfSheets();
    }

    public Sheet createSheet() {
        return workbook.createSheet();
    }

    public Sheet createSheet(String sheetName) {
        return workbook.createSheet(sheetName);
    }

    /**
     * 设置sheet名称，长度为1-31，不能包含后面任一字符: ：\ / ? * [ ]
     *
     * @param sheetIx 指定 Sheet 页，从 0 开始，//
     * @param name
     * @return
     */
    public boolean setSheetName(int sheetIx, String name) {
        workbook.setSheetName(sheetIx, name);
        return true;
    }

    /**
     * 获取 sheet名称
     *
     * @param sheetIx 指定 Sheet 页，从 0 开始
     * @return
     */
    public String getSheetName(int sheetIx) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        if (sheet == null) {
            return null;
        }
        return sheet.getSheetName();
    }

    /**
     * 获取sheet的索引，从0开始
     *
     * @param name sheet 名称
     * @return -1表示该未找到名称对应的sheet
     */
    public int getSheetIndex(String name) {
        return workbook.getSheetIndex(name);
    }

    /**
     * 删除指定sheet
     *
     * @param sheetIx 指定 Sheet 页，从 0 开始
     * @return
     */
    public boolean removeSheetAt(int sheetIx) {
        workbook.removeSheetAt(sheetIx);
        return true;
    }

    /**
     * 删除指定名称的sheet
     *
     * @param sheetName sheet名称
     * @return
     */
    public boolean removeSheetByName(String sheetName) {
        workbook.removeSheetAt(getSheetIndex(sheetName));
        return true;
    }

    /**
     * 删除指定sheet中行，改变该行之后行的索引
     *
     * @param sheetIx  指定 Sheet 页，从 0 开始
     * @param rowIndex 指定行，从0开始
     * @return
     */
    public boolean removeRow(int sheetIx, int rowIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIx);
        int lastRowNum = sheet.getLastRowNum();
        if (rowIndex >= 0 && rowIndex < lastRowNum) {
            sheet.shiftRows(rowIndex + 1, lastRowNum, -1);// 将行号为rowIndex+1一直到行号为lastRowNum的单元格全部上移一行，以便删除rowIndex行
        }
        if (rowIndex == lastRowNum) {
            Row removingRow = sheet.getRow(rowIndex);
            if (removingRow != null) {
                sheet.removeRow(removingRow);
            }
        }
        return true;
    }

    /**
     * 删除指定sheet中rowIndexStart（含）到rowIndexEnd（不含）的行，改变该行之后行的索引
     *
     * @param sheetIx       指定 Sheet 页，从 0 开始
     * @param rowIndexStart 起始行（含）
     * @param rowIndexEnd   结束行（不含）
     * @return
     */
    public boolean removeRows(int sheetIx, int rowIndexStart, int rowIndexEnd) {
        for (int i = rowIndexEnd - 1; i >= rowIndexStart; i--) {
            removeRow(sheetIx, i);
        }
        return true;
    }

    /**
     * 设置sheet 页的索引
     *
     * @param sheetName Sheet 名称
     * @param sheetIx   Sheet 索引，从0开始
     */
    public void setSheetOrder(String sheetName, int sheetIx) {
        workbook.setSheetOrder(sheetName, sheetIx);
    }

    /**
     * 清空指定sheet页（先删除后添加并指定sheetIx）
     *
     * @param sheetIx 指定 Sheet 页，从 0 开始
     * @return
     */
    public boolean clearSheet(int sheetIx) {
        String sheetName = getSheetName(sheetIx);
        removeSheetAt(sheetIx);
        workbook.createSheet(sheetName);
        setSheetOrder(sheetName, sheetIx);
        return true;
    }

    /**
     * 将当前修改保存覆盖至输入文件inputFilePath中
     *
     * @throws IOException
     */
    public void submitWrite() throws IOException {
        if (StringUtils.isNotEmpty(inputFilePath)) {
            os = new FileOutputStream(new File(inputFilePath));
            submitWrite(os);
        } else {
            throw new IOException("文件的默认路径（源文件路径）不存在");
        }
    }


    /**
     * 将当前修改保存到输出流
     *
     * @param outputStream 输出流
     * @throws IOException
     */
    public void submitWrite(OutputStream outputStream) throws IOException {
        this.workbook.write(outputStream);
    }

    /**
     * 将当前修改保存到outputPath对应的文件中
     *
     * @param outputPath 输出文件的路径
     * @throws IOException
     */
    public void submitWrite(String outputPath) throws IOException {
        os = new FileOutputStream(new File(outputPath));
        submitWrite(os);
    }

    /**
     * 关闭流
     *
     * @throws IOException
     */
    public void close() throws IOException {
        if (os != null) {
            os.close();
        }
        workbook.close();
    }

    /**
     * 转换单元格的类型为String 默认的 <br>
     * 默认的数据类型：CELL_TYPE_BLANK(3), CELL_TYPE_BOOLEAN(4), CELL_TYPE_ERROR(5),CELL_TYPE_FORMULA(2), CELL_TYPE_NUMERIC(0),
     * CELL_TYPE_STRING(1)
     *
     * @param cell
     * @return
     */
    private String getCellValueToString(Cell cell) {
        String strCell = "";
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                strCell = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    if (datePattern != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                        strCell = sdf.format(date);
                    } else {
                        strCell = date.toString();
                    }
                    break;
                }
                // 不是日期格式，则防止当数字过长时以科学计数法显示
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                strCell = cell.toString();
                break;
            case Cell.CELL_TYPE_STRING:
                strCell = cell.getStringCellValue();
                break;
            default:
                break;
        }
        return strCell;
    }
}
