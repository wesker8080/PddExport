package com.wesker;

import com.alibaba.fastjson.JSON;
import com.csvreader.CsvReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static Map<String, Integer> spceMap;
    private static ArrayList<Line> remarkList;
    private static Map<String,String> outputMap;
    private static List<ShopProduct> productList1;
    private static boolean isZiYuanConfig = false;
    private static String dataFile = "a.json";
    //private String dateFile = "b.json";
    public static void main(String[] args) {
        //dataFile = "a.json"; //子源
        if (!check()) {
            System.out.println("非法设备，请联系开发者.V:Wesker_8080" );
            return;
        }
        dataFile = "b.json"; //嘉妹
        spceMap = new HashMap<>();
        outputMap = new HashMap<>();
        remarkList = new ArrayList<>();
        productList1 = new ArrayList<>();
        cacheShopMap();
        read();


    }
    private static void cacheShopMap() {
        if (json == null) {
            json = Config.readJsonFile(dataFile);// 嘉妹
        }
        ProductList product = JSON.parseObject(json, ProductList.class);
        List<ProductList.Product> productList = product.getProductList();
        for (ProductList.Product p : productList) {
            ShopProduct shopProduct = new ShopProduct();
            shopProduct.setShopName(p.getProductName());
            shopProduct.setShopMap(new HashMap<>());
            productList1.add(shopProduct);
        }
    }
    private static List<Line> lines = new ArrayList<>();
    public static void outputExcel(LinkedHashMap<String, String> sortOutputMap) {
        if (remarkList.size() != 0) {
            for (Line line : remarkList) {
                lines.add(line);
            }
        }

        Iterator<Map.Entry<String, String>> outputMapIterator = sortOutputMap.entrySet().iterator();
        while (outputMapIterator.hasNext()) {
            Map.Entry<String, String> next = outputMapIterator.next();
            //System.out.println("最终整合： "  + next.getValue() + " : "+ next.getKey() );
            String[] ss = next.getValue().split("_");
            //档口名称
            String shopName = ss[0];
            //商品ID
            String productId = ss[1];
            String remark = "";
            if (ss.length > 2) {
                remark = ss[2];
            }
            String[] s = next.getKey().split("_");
            //尺码
            String info = s[0];
            //数量
            String num = s[1];
            for (int i = 0; i < productList1.size(); i++) {
                if (shopName.equals(productList1.get(i).getShopName())) {
                    productList1.get(i).getShopMap().put(next.getKey(), next.getValue());
                }
            }
        }
        Line emptyLine = createEmptyLine();

        for (int i = 0; i < productList1.size(); i++) {
            lines.add(emptyLine);
            lines.add(emptyLine);
            Map<String, String> shopMap = productList1.get(i).getShopMap();
            createLineByMap(shopMap);
        }

        List<String> titles = new ArrayList<>();
        titles.add("档口名称");
        titles.add("尺码");
        titles.add("数量");
        titles.add("商品id");
        titles.add("商家备注");
        titles.add("商品标题");
        titles.add("收货人");
        titles.add("创建时间");
        titles.add("承诺发货时间");
        titles.add("id");
        titles.add("订单号");
        POIUtil.exportExcel(titles,lines,"备货单");
        System.out.println("备货单导出成功，请查看当前目录下<备货单.xls>");
    }

    private static Line createEmptyLine() {
        Line linex = new Line();
        List<Column> columnsx = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i == 1) {
                Column column = new Column();
                //column.setValue("");
                columnsx.add(column);
                linex.setColumns(columnsx);
            }
        }
       return linex;
    }

    public static void createLineByMap(Map<String, String> map) {
        LinkedHashMap<String, String> sortOutputMap1 = map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
        Iterator<Map.Entry<String, String>> outputMapIterator = sortOutputMap1.entrySet().iterator();
        while (outputMapIterator.hasNext()) {
            Map.Entry<String, String> next = outputMapIterator.next();
            System.out.println("最终整合： "  + next.getValue() + " : "+ next.getKey() );
            String[] ss = next.getValue().split("_");
            //档口名称
            String shopName = ss[0];
            //商品ID
            String productId = ss[1];
            String remark = "";
            if (ss.length > 2) {
                remark = ss[2];
            }
            String[] s = next.getKey().split("_");
            //尺码
            String info = s[0];
            //数量
            String num = s[1];

            Line sumLine = new Line();
            List<Column> sumColumns = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Column column = new Column();
                if (i == 0) {
                    column.setValue(shopName);
                } else if (i == 1) {
                    column.setValue(info);
                } else if (i == 2) {
                    column.setValue(num);
                } else if (i == 3) {
                    column.setValue(productId);
                } else if (i == 4) {
                    column.setValue(remark);
                }
                sumColumns.add(column);
                sumLine.setColumns(sumColumns);
            }
            lines.add(sumLine);

        }

    }
    public static LinkedHashMap<String, String> findProductIdByProductInfo(Map<String, Integer>  map) {
        String filePath = "pdd.csv";
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> next = iterator.next();
            //System.out.println(next.getKey() + " : " + next.getValue());
            String info = next.getKey();
            int num = next.getValue();
            String val = info + "_" + num;
            String name;
            if (!info.contains(",")) {
                name = info;
            } else {
                name = info.split(",")[0];
            }
            // 创建CSV读对象
            CsvReader csvReader = null;
            try {
                csvReader = new CsvReader(filePath,',', Charset.forName("UTF-8"));
                // 读表头
                boolean s = csvReader.readHeaders();
                while (csvReader.readRecord()){
                    String guige = csvReader.get("商品规格");
                    String allInfo = guige.split(",")[0];
                    if (allInfo.contains("】")) {
                        String[] split = allInfo.split("】");
                        allInfo = split[1];
                    }
                    String[] split = null;
                    if (allInfo.contains("+")) {
                        split = allInfo.split("\\+");
                    }
                    if (split != null && (split[0].equals(name) || split[1].equals(name))) {
                        // 如果这个规格包含整理出来的规格，取出商品ID
                        String productId = csvReader.get("商品id").substring(0,12);
                        // 根据ID找出档口
                        String productName = findProductNameByProductId(productId);
                        if (productName != null) {
                            // 存放进新Map
                            String productNameIdRemark;
                            productNameIdRemark = productName + "_" + productId;
                            outputMap.put(val, productNameIdRemark);
                            break;
                        }
                    } else if (allInfo.equals(name)){
                        // 如果这个规格包含整理出来的规格，取出商品ID
                        String productId = csvReader.get("商品id").substring(0,12);
                        // 根据ID找出档口
                        String productName = findProductNameByProductId(productId);
                        if (productName != null) {
                            // 存放进新Map
                            String productNameIdRemark;
                            productNameIdRemark = productName + "_" + productId;
                           /* if (remark != null && !remark.equals("")) {
                                productNameIdRemark = productName + "_" + productId + "_" + remark;
                            } else {
                                productNameIdRemark = productName + "_" + productId;
                            }*/
                            outputMap.put(val, productNameIdRemark);
                            break;
                        }
                    }

                }
            } catch (Exception e) {
                System.out.println("error : " +e.getMessage());
                e.printStackTrace();
            }
        }

        LinkedHashMap<String, String> sortOutputMap = outputMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
        LinkedHashMap<String, String> sortOutputMap1 = sortOutputMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue(), (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));

        Iterator<Map.Entry<String, String>> outputMapIterator = sortOutputMap1.entrySet().iterator();
        while (outputMapIterator.hasNext()) {
            Map.Entry<String, String> next = outputMapIterator.next();
            //System.out.println("最终整合： "  + next.getValue() + " : "+ next.getKey() );
        }
        return sortOutputMap1;
    }

    private static String json;
    private static String findProductNameByProductId(String productId) {
        if (json == null) {
            json = Config.readJsonFile(dataFile);
        }
        ProductList product = JSON.parseObject(json, ProductList.class);
        List<ProductList.Product> productList = product.getProductList();
        for (ProductList.Product p : productList) {
            for (String id : p.getIds()) {
                if (productId.equals(id)) {
                    return p.getProductName();
                }
            }
        }
        return null;
    }
    public static boolean check() {
        try {
            Process process = Runtime.getRuntime().exec("ipconfig /all");
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            while ((line = input.readLine()) != null) {
                //System.out.println("MAC address = [" + line + "]");
                if (line.contains("1C-B7-2C-AE-E4-DF")) {
                    System.out.println("已认证");
                    return true;
                }
            }
        } catch (java.io.IOException e) {
            System.err.println("IOException " + e.getMessage());
        }
        return false;
    }
    public static void read() {
        String filePath = "pdd.csv";

        //System.out.println(product.getProductList().get(0).getProductName());
        try {
            // 创建CSV读对象
            CsvReader csvReader = new CsvReader(filePath,',', Charset.forName("UTF-8"));
            // 读表头
            csvReader.readHeaders();
            while (csvReader.readRecord()){
                // 读一整行
                //System.out.println(csvReader.getRawRecord());
                // 读这行的某一列
                 //System.out.println(csvReader.get("商品id"));
                 //System.out.println(csvReader.get("商家备注"));
                String id = csvReader.get("商品id");
                cacheProductId(csvReader.get("商品id"));
                String remark = csvReader.get("商家备注");
                if ((remark == null || remark.equals(""))) {
                    // 不带商家备注的统一处理
                    dealProductInfo(csvReader.get("商品规格"), csvReader.get("商品数量(件)"));
                } else {
                    // 带商家备注的特殊处理
                    Line line = dealProductRemark(csvReader.get("商品规格"), csvReader.get("商品数量(件)"), csvReader.get("商品id"), remark);
                    if (line != null) {
                        remarkList.add(line);
                    }
                }
            }
            Iterator<Map.Entry<String, Integer>> iterator = spceMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, Integer> next = iterator.next();
                //System.out.println(next.getKey() + " : " + next.getValue());
            }
            LinkedHashMap<String, String> map = findProductIdByProductInfo(spceMap);


            outputExcel(map);
            //getProductIds().stream().forEach(x -> System.out.println(x));
            mapProductId(getProductIds());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void mapProductId(Set<String> productIds) {

    }

    /**
     *
     * @param
     * @param
     * @param
     * @param remark
     */
    private static Line dealProductRemark(String info, String num, String productId, String remark) {
          // System.out.println(remark);
           String shopName = findProductNameByProductId(productId);
           Line sumLine = new Line();
           List<Column> sumColumns = new ArrayList<>();
           for (int i = 0; i < 5; i++) {
               Column column = new Column();
               if (i == 0) {
                   column.setValue(shopName);
               } else if (i == 1) {
                   column.setValue(info);
               } else if (i == 2) {
                   column.setValue(num);
               } else if (i == 3) {
                   column.setValue(productId);
               } else if (i == 4) {
                   column.setValue(remark);
               }
               sumColumns.add(column);
               sumLine.setColumns(sumColumns);
           }
           return sumLine;
    }

    private static void dealProductInfo(String info, String num) {

        String replace = num.substring(0,1);
        int count = Integer.valueOf(replace);
        Iterator<Map.Entry<String, Integer>> iterator = spceMap.entrySet().iterator();
        boolean successFirst = false;
        boolean successSecond = false;
        boolean successOne = false;
        boolean hasTowSize = false;
        String first = null;
        String second = null;
        String one = null;
        String spec = info.split(" ")[0];
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
        //System.out.println("first:"+first + "  second:"+second + " one: " + one);



        if (spceMap.size() != 0) {
            while(iterator.hasNext()) {
                Map.Entry<String, Integer> next = iterator.next();
                if (next.getKey().equals(first)) {
                    int value = next.getValue();
                    value += count;
                    next.setValue(value);
                    successFirst = true;
                } else if (next.getKey().equals(second)) {
                    int value = next.getValue();
                    value += count;
                    next.setValue(value);
                    successSecond = true;
                } else if (next.getKey().equals(one)) {
                    int value = next.getValue();
                    value += count;
                    next.setValue(value);
                    successOne = true;
                }
            }
            if (hasTowSize) {
                if (!successFirst) {
                    spceMap.put(first, count);
                }
                if (!successSecond) {
                    spceMap.put(second, count);
                }
            } else {
                if (!successOne) {
                    spceMap.put(one, count);
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
                    spceMap.put(first, count);
                    // 第二件
                    second = split[1];
                    spceMap.put(second, count);
                }
            } else {
                // 一件装
                one = spec;
                spceMap.put(one, count);
            }
        }
    }

    private static void cacheRemarkMap() {

    }

    private static Set<String> productIds = new HashSet<String>();

    private static void cacheProductId(String id) {
        productIds.add(id);
    }
    private static Set<String> getProductIds() {
        return productIds;
    }
}


