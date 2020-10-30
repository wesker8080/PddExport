import com.alibaba.fastjson.JSON;

import java.io.File;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {


    public static void main(String[] args) {
        /*try {
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("new sheet");

            // Create a row and put some cells in it. Rows are 0 based.
            HSSFRow row = sheet.createRow(0);
            // Create a cell and put a value in it.
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(1);

            // Or do it on one line.
            row.createCell(1).setCellValue(1.2);
            row.createCell(2).setCellValue("This is a string");
            row.createCell(3).setCellValue(true);

            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream("d://workbook.xls");
            wb.write(fileOut);
        }
            catch (Exception e) {
            }*/
       /* if (args == null) {
            throw new NullPointerException("参数错误");
        }
        if (args.length < 1) {
            throw new NullPointerException("参数错误");
        }*/
        // 承诺发货时间：2020-11-01 15:50
        //String json = JsonUtil.readJsonFile(args[0]);
        LocalDateTime firstTime = null;
        LocalDateTime lastTime = null;
        try {
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            //LocalDate parse = LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE);
            firstTime = LocalDateTime.parse(args[0], pattern);
            //firstTime = LocalDateTime.parse("2020-10-28 02:54:26", pattern);
            lastTime = LocalDateTime.parse(args[1], pattern);
            //lastTime = LocalDateTime.parse("2020-10-28 05:34:17", pattern);
            //System.out.println("parse : " + firstTime + " lastTime : " + lastTime);
        } catch (Exception e) {
            System.out.println("时间格式不正确 请重新输入 ->" + e.getMessage());
            return;
        }

        File directory = new File("");
        String currentPath = directory.getAbsolutePath();
        File[] files = new File(currentPath).listFiles();

        List<OrderList.Item> totalList = new ArrayList<>();

        Stream.of(files).filter(x -> x.getName().contains("json")).forEach(y -> {
            // 对每个文件遍历，
            String json = JsonUtil.readJsonFile(y.getAbsolutePath());
            PddResult pddResult = JSON.parseObject(json, PddResult.class);
            List<OrderList.Item> pageItems = pddResult.getResult().getPageItems();
            //System.out.println(pageItems);
            totalList.addAll(pageItems);
        });
        // 去重
        ArrayList<OrderList.Item> pageItems = totalList.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(OrderList.Item::getOrder_sn))), ArrayList::new));
        //System.out.println("去重后 ： " + pageItems.size());

        // 过滤掉不在时间范围内的订单
        long firstTimeMilli = firstTime.toInstant(ZoneOffset.of("+8")).toEpochMilli()/1000;
        long lastTimeMilli = lastTime.toInstant(ZoneOffset.of("+8")).toEpochMilli()/1000;
        //System.out.println(firstTimeMilli);
        //System.out.println(lastTimeMilli);
        List<OrderList.Item> collect = pageItems.stream().filter(x -> {

            long promise_shipping_time = Long.parseLong(x.getPromise_shipping_time());
            if (promise_shipping_time >= firstTimeMilli && promise_shipping_time <= lastTimeMilli) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        pageItems = (ArrayList<OrderList.Item>) collect;
        //System.out.println("过滤时间后 : " + pageItems.size());

        //String json = JsonUtil.readJsonFile("D:\\workSpace\\IDEA\\PddExport\\out\\artifacts\\PddExport_jar\\json.txt");
        int total = 0;
        //PddResult pddResult = JSON.parseObject(json, PddResult.class);
        //int totalItemNum = pddResult.getResult().getTotalItemNum();
        //System.out.println(totalItemNum);
        //List<OrderList.Item> pageItems = pddResult.getResult().getPageItems();
        List<String> titles = new ArrayList<>();
        titles.add("商品信息");
        titles.add("尺码");
        titles.add("数量");
        titles.add("商品总价");
        titles.add("买家");
        titles.add("订单/售后状态");
        titles.add("收货人");
        titles.add("创建时间");
        titles.add("承诺发货时间");
        titles.add("id");
        titles.add("订单号");

        // 输出统计信息start
        Map<String, Integer> spceMap = new HashMap<>();
        pageItems.stream().forEach(x -> {
            Iterator<Map.Entry<String, Integer>> iterator = spceMap.entrySet().iterator();
            boolean successFirst = false;
            boolean successSecond = false;
            boolean successOne = false;
            boolean hasTowSize = false;
            String first = null;
            String second = null;
            String one = null;

            String spec = x.getSpec().split(" ")[0];
            if (spec.contains("】")) {
                spec = spec.split("】")[1];
            }
            // "spec": "【俩件装】W601蓝色+W613黑色,7XL 建议260-330斤",
            // "【单件装】W32深灰,5XL 200-225斤"
            if (spec.contains("+")) {
                // 两件装
                hasTowSize = true;
                String[] split = spec.split("\\+");
                if (split.length >= 2) {
                    String[] sizeSplit = split[1].split(",");
                    // 码数
                    String size = sizeSplit[1];
                    // 第一件
                    first = split[0] + "," + size;
                    // 第二件
                    second = split[1];
                }
            } else {
                // 一件装
                one = spec;
            }
            if (spceMap.size() != 0) {
                while(iterator.hasNext()) {
                    Map.Entry<String, Integer> next = iterator.next();
                    if (next.getKey().equals(first)) {
                        int value = next.getValue();
                        value += x.getGoods_number();
                        next.setValue(value);
                        successFirst = true;
                    } else if (next.getKey().equals(second)) {
                        int value = next.getValue();
                        value += x.getGoods_number();
                        next.setValue(value);
                        successSecond = true;
                    } else if (next.getKey().equals(one)) {
                        int value = next.getValue();
                        value += x.getGoods_number();
                        next.setValue(value);
                        successOne = true;
                    }
                }
                if (hasTowSize) {
                    if (!successFirst) {
                        spceMap.put(first, x.getGoods_number());
                    }
                    if (!successSecond) {
                        spceMap.put(second, x.getGoods_number());
                    }
                } else {
                    if (!successOne) {
                        spceMap.put(one, x.getGoods_number());
                    }
                }
            } else {
                if (spec.contains("+")) {
                    // 两件装
                    String[] split = spec.split("\\+");
                    if (split.length >= 2) {
                        String[] sizeSplit = split[1].split(",");
                        // 码数
                        String size = sizeSplit[1];
                        // 第一件
                        first = split[0] + "," + size;
                        spceMap.put(first, x.getGoods_number());
                        // 第二件
                        second = split[1];
                        spceMap.put(second, x.getGoods_number());
                    }
                } else {
                    // 一件装
                    one = spec;
                    spceMap.put(one, x.getGoods_number());
                }
            }
        });
        List<Line> lines = new ArrayList<>();
        TreeMap<String, Integer> sortedMap = new TreeMap<>(spceMap);
        Iterator<Map.Entry<String, Integer>> iterator = sortedMap.entrySet().iterator();
        if (sortedMap.size() != 0) {
            while(iterator.hasNext()) {
                Map.Entry<String, Integer> next = iterator.next();
                total += next.getValue();
                //System.out.println("货号:"+next.getKey() + "  num:"+next.getValue());
                Line sumLine = new Line();
                List<Column> sumColumns = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    Column column = new Column();
                    if (i == 0) {
                        column.setValue("  ");
                    } else if (i == 1) {
                        column.setValue(next.getKey());
                    } else if (i == 2) {
                        column.setValue(next.getValue());
                    }
                    sumColumns.add(column);
                    sumLine.setColumns(sumColumns);
                }
                lines.add(sumLine);
            }
        }
        Line line1 = new Line();

        // 输出统计信息end

        // 输出详细信息

        // 分割
        Line linex = new Line();
        List<Column> columnsx = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            if (i == 1) {
                Column column = new Column();
                column.setValue("总数");
                columnsx.add(column);
                linex.setColumns(columnsx);
            } else if(i == 2){
                Column column = new Column();
                column.setValue("总共 " + total +" 件");
                columnsx.add(column);
                linex.setColumns(columnsx);
            }
        }
        lines.add(linex);

        List<Column> columns1 = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            if (i < 3) {
                Column column = new Column();
                column.setValue("  ");
                columns1.add(column);
                line1.setColumns(columns1);
            } else {
                Column column = new Column();
                column.setValue("¥");
                columns1.add(column);
                line1.setColumns(columns1);
            }
        }
        lines.add(line1);
        lines.add(line1);
        lines.add(line1);
        lines.add(line1);
        lines.add(line1);
        // 列表
        for (int j = 0;j<pageItems.size();j++){
            List<Column> columns = new ArrayList<>();
            Line line = new Line();
            for (int i = 0; i < 11; i++) {
                Column column = new Column();
                switch (i) {
                    case 0:column.setValue(pageItems.get(j).getGoods_name());break;
                    case 1:column.setValue(pageItems.get(j).getSpec());break;
                    case 2:column.setValue(pageItems.get(j).getGoods_number());break;
                    case 3:column.setValue(pageItems.get(j).getGoods_amount());break;
                    case 4:column.setValue(pageItems.get(j).getNickname());break;
                    case 5:column.setValue(pageItems.get(j).getOrder_status_str());break;
                    case 6:column.setValue(pageItems.get(j).getReceive_name());break;
                    case 7:column.setValue(systemToDate(pageItems.get(j).getConfirm_time()));break;
                    case 8:column.setValue(systemToDate(pageItems.get(j).getPromise_shipping_time()));break;
                    case 9:column.setValue(pageItems.get(j).getGoods_id());break;
                    case 10:column.setValue(pageItems.get(j).getOrder_sn());break;
                }
                columns.add(column);
            }
            line.setColumns(columns);
            lines.add(line);
        }



        POIUtil.exportExcel(titles,lines,"备货单");
        System.out.println("备货单导出成功，请查看当前目录下<备货单.xls>");
    }

    private static String systemToDate(String time) {
        long l = Long.parseLong(time);
        Instant timestamp = Instant.ofEpochMilli(l*1000);
        ZonedDateTime losAngelesTime = timestamp.atZone(ZoneId.of("Asia/Shanghai"));
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("YYYY-MM-dd hh:mm:ss");
        LocalDateTime localDateTime = losAngelesTime.toLocalDateTime();
        String format = pattern.format(localDateTime);
        return format;
    }
    private static LocalDateTime toLocalDateTime(String time) {
        long l = Long.parseLong(time);
        Instant timestamp = Instant.ofEpochMilli(l*1000);
        ZonedDateTime losAngelesTime = timestamp.atZone(ZoneId.of("Asia/Shanghai"));
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("YYYY-MM-dd hh:mm:ss");
        return losAngelesTime.toLocalDateTime();
    }
}
