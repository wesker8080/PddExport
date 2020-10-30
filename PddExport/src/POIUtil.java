import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class POIUtil {
    /**
     * 导入工具类
     *  file Excel :上传过来的文件
     * columnNames :为Excel 实体类属性集合一一对应
     * 比如我的实体类有 id,name,age,address
     * 那么这个集合就是List<String> list =new ArrayList<>(){id,name,age,address}
     */
    public static Map<String, Object> uploadExcel(File file, List<String> columnNames) {
        List<Line> lines = new LinkedList<>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        int state = 1;
        String msg = "读取成功！";
        if (!file.exists()) {
            state = 0;
            msg = "文件为空！";
        }
        try {
            //根据路径获取这个操作excel的实例
            Workbook wb = WorkbookFactory.create(file);
            //根据页面index 获取sheet页
            Sheet sheet = wb.getSheetAt(0);
            Row row = null;
            //循环sheet页中数据从第二行开始，第一行是标题
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Line line = new Line();
                List<Column> columns = new ArrayList<Column>();
                row = sheet.getRow(i);
                for (int j = 0; j < columnNames.size(); j++) {
                    Column clbo = new Column();
                    clbo.setName(columnNames.get(j));
                    if (row.getCell(j) != null) {
                        CellType cellType = row.getCell(j).getCellType();
                        String value = null;
                        if (CellType.NUMERIC.equals(cellType)) {
                            if ("General".equals(row.getCell(j).getCellStyle().getDataFormatString())) {
                                value = String.valueOf(row.getCell(j).getNumericCellValue());
                            } else if ("m/d/yy".equals(row.getCell(j).getCellStyle().getDataFormatString())) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                value = sdf.format(row.getCell(j).getDateCellValue());
                            } else {
                                value = String.valueOf(row.getCell(j).getNumericCellValue());
                            }
                            clbo.setValue(value);
                        } else {
                            row.getCell(j).setCellType(CellType.STRING);//设置成String
                            clbo.setValue(row.getCell(j).getStringCellValue());
                        }
                    } else {
                        clbo.setValue("");
                    }
                    columns.add(clbo);
                }
                line.setColumns(columns);
                lines.add(line);
            }
        } catch (Exception e) {
            //log.error(e.getMessage());
            e.printStackTrace();
            state = 0;
            msg = "读取失败，请稍后再试！";
        }
        resultMap.put("state", state);
        resultMap.put("msg", msg);
        resultMap.put("lines", lines);
        return resultMap;
    }
    public static void exportExcel(List<String> titles, List<Line> lines,
                                   String title) {
        // 创建Excel工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();
        workbook.createInformationProperties();//创建文档信息
        DocumentSummaryInformation dsi= workbook.getDocumentSummaryInformation();//摘要信息
        dsi.setCategory("类别:Excel文件");//类别
        dsi.setManager("管理者:张志坤");//管理者
        dsi.setCompany("公司:--");//公司
        SummaryInformation si = workbook.getSummaryInformation();//摘要信息
        si.setSubject("主题:--");//主题
        si.setTitle("标题:Pdd");//标题
        si.setAuthor("作者:张志坤");//作者
        si.setComments("备注");//备注
        // 创建一个工作表sheet 默认是表名是sheet0
        HSSFSheet sheet = workbook.createSheet();
        // 创建表的第一行
        HSSFRow row = sheet.createRow(0);
        sheet.setDefaultColumnWidth(20);
        sheet.setDefaultRowHeightInPoints(20);
        // 创建第0行 也就是标题
        row.setHeightInPoints(30);// 设备标题的高度
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, titles.size()));

        // 创建单元格
        HSSFCell cell = null;
        cell = row.createCell(0);
        cell.setCellStyle(titleStyle(workbook));
        cell.setCellValue(title);

        HSSFRow row1 = sheet.createRow(1);
        // 循环为第一行插入标题
        for (int i = 0; i < titles.size(); i++) {
            cell = row1.createCell(i);
            cell.setCellStyle(style(workbook));
            cell.setCellValue(titles.get(i));
        }
        // 追加数据 1是第二行
        for (int i = 2 ; i <= lines.size()+1;i++) {
            Line line = lines.get(i-2);
            List<Column> columns = line.getColumns();
            HSSFRow nextrow = sheet.createRow(i);
            for (int j = 0; j < columns.size(); j++) {
                HSSFCell cell2 = nextrow.createCell(j);
                cell2.setCellStyle(style1(workbook));
                if (columns.get(j).getValue() == null) {
                    cell2.setCellValue("");
                } else {
                    cell2.setCellValue(String.valueOf(columns.get(j).getValue()));
                }
            }
        }
        try {
            //.xls 是2003版本，excel2003、2007、2010都可以打开，兼容性最好
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern("YYYY年MM月dd日HH点mm分ss秒");
            String format = pattern.format(now);
            FileOutputStream fileOut = new FileOutputStream("备货单"+format+".xls");
            workbook.write(fileOut);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 标题第一行样式
    public static HSSFCellStyle titleStyle(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBottomBorderColor((short) 8);
        style.setBorderBottom(BorderStyle.THIN); // HSSFCellStyle.BORDER_THIN
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        HSSFFont headerFont = (HSSFFont) wb.createFont(); // 创建字体样式
        headerFont.setFontName("黑体"); // 设置字体类型
        headerFont.setFontHeightInPoints((short) 15); // 设置字体大小
        style.setFont(headerFont); // 为标题样式设置字体样式
        return style;
    }

    // 第二行样式
    public static HSSFCellStyle style(HSSFWorkbook wb) {
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBottomBorderColor((short) 8);
        style.setBorderBottom(BorderStyle.THIN); // HSSFCellStyle.BORDER_THIN
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        HSSFFont headerFont = (HSSFFont) wb.createFont(); // 创建字体样式
        headerFont.setFontName("黑体"); // 设置字体类型
        headerFont.setFontHeightInPoints((short) 12); // 设置字体大小
        style.setFont(headerFont); // 为标题样式设置字体样式
        return style;
    }
    // 数据行样式
    public static HSSFCellStyle style1(HSSFWorkbook wb) {
        HSSFCellStyle style1 = wb.createCellStyle();
        style1.setWrapText(true);// 设置自动换行
        style1.setAlignment(HorizontalAlignment.CENTER);    //HSSFCellStyle.ALIGN_CENTER
        style1.setVerticalAlignment(VerticalAlignment.CENTER); // HSSFCellStyle.VERTICAL_CENTER // 创建一个居中格式
        style1.setBottomBorderColor((short) 8);
        style1.setBorderBottom(BorderStyle.THIN); // HSSFCellStyle.BORDER_THIN
        style1.setBorderLeft(BorderStyle.THIN);
        style1.setBorderRight(BorderStyle.THIN);
        style1.setBorderTop(BorderStyle.THIN);
        HSSFFont headerFont1 = (HSSFFont) wb.createFont(); // 创建字体样式
        headerFont1.setFontName("黑体"); // 设置字体类型
        headerFont1.setFontHeightInPoints((short) 10); // 设置字体大小
        style1.setFont(headerFont1); // 为标题样式设置字体样式
        return style1;
    }
   /* public static void export(){
        HSSFWorkbook wb = new HSSFWorkbook();
        // 设置标题样式
        HSSFCellStyle titleStyle = wb.createCellStyle();
        // 设置单元格边框样式
        titleStyle.setBorderTop(BorderStyle.THIN);// 上边框 细边线
        titleStyle.setBorderBottom(BorderStyle.THIN);// 下边框 细边线
        titleStyle.setBorderLeft(BorderStyle.THIN);// 左边框 细边线
        titleStyle.setBorderRight(BorderStyle.THIN);// 右边框 细边线
        // 设置单元格对齐方式
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平居中
        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
        // 设置字体样式
        Font titleFont = wb.createFont();
        titleFont.setFontHeightInPoints((short)15);// 字体高度
        titleFont.setFontName("黑体");// 字体样式
        titleStyle.setFont(titleFont);
        // 在Workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(fileName);
        // 标题数组
        String[] titleArray = new String[excelHeader.length];
        // 字段名数组
        String[] fieldArray = new String[excelHeader.length];
        for(int i = 0; i < excelHeader.length; i++) {
            String[] tempArray = excelHeader[i].split("#");// 临时数组 分割#
            titleArray[i] = tempArray[0];
            fieldArray[i] = tempArray[1];
        }
        // 在sheet中添加标题行
        HSSFRow row = sheet.createRow((int)0);// 行数从0开始
        //需要序号才把下面注解打开
//   HSSFCell sequenceCell = row.createCell(0);// cell列 从0开始 第一列添加序号
//   sequenceCell.setCellValue("序号");
//   sequenceCell.setCellStyle(titleStyle);
        sheet.autoSizeColumn(0);// 自动设置宽度
        // 设置表格默认列宽度
        sheet.setDefaultColumnWidth(20);
        // 为标题行赋值
        for(int i = 0; i < titleArray.length; i++) {
            //需要序号就需要i+1     因为0号位被序号占用
            HSSFCell titleCell = row.createCell(i);
            titleCell.setCellValue(titleArray[i]);
            titleCell.setCellStyle(titleStyle);
            sheet.autoSizeColumn(i + 1);// 0号位被序号占用，所以需+1
        }
        // 数据样式 因为标题和数据样式不同 需要分开设置 不然会覆盖
        HSSFCellStyle dataStyle = wb.createCellStyle();
        // 设置数据边框
        dataStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        dataStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        // 设置居中样式
        dataStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平居中
        dataStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
        // 设置数据字体
        Font dataFont = wb.createFont();
        dataFont.setFontHeightInPoints((short)12);// 字体高度
        dataFont.setFontName("宋体");// 字体
        dataStyle.setFont(dataFont);
        // 遍历集合数据，产生数据行
        Iterator<T> it = dataList.iterator();
        int index =0;
        while(it.hasNext()) {
            index++;// 0号位被占用 所以+1
            row = sheet.createRow(index);
            T t = (T) it.next();
            // 利用反射，根据传过来的字段名数组，动态调用对应的getXxx()方法得到属性值
            for(int i = 0; i < fieldArray.length; i++) {
                //需要序号就需要i+1
                HSSFCell dataCell = row.createCell(i);
                dataCell.setCellStyle(dataStyle);
                //需要序号就需要i+1
                sheet.autoSizeColumn(i);
                String fieldName = fieldArray[i];
                String getMethodName = "get"+ fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);// 取得对应getXxx()方法
                Class<?extends Object> tCls = t.getClass();// 泛型为Object以及所有Object的子类
                Method getMethod = tCls.getMethod(getMethodName, new Class[] {});// 通过方法名得到对应的方法
                Object value = getMethod.invoke(t, new Object[] {});// 动态调用方,得到属性值
                if(value != null) {
                    dataCell.setCellValue(value.toString());// 为当前列赋值
                }
            }
        }
    }*/
}
