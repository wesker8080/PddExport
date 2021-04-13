package com.wesker;

import org.junit.Test;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

/*{
    "productList": [
        {
            "productName": [
                "productId",
                "productId"
            ]
        },
        {
            "productName": [
                "productId",
                "productId"
            ]
        }
    ]
}*/
public class ProductList {

    List<Product> productList;

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public class Product{
        List<String> ids;
        String productName;

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public List<String> getIds() {
            return ids;
        }

        public void setIds(List<String> ids) {
            this.ids = ids;
        }
    }
    @Test
    public void tset(){
        try {
            Process process = Runtime.getRuntime().exec("ipconfig /all");
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            while ((line = input.readLine()) != null) {
                //System.out.println("MAC address = [" + line + "]");
                if (line.contains("00-D8-61-8F-93-3D")) {
                    System.out.println("合法设备");
                    break;
                }
            }
        } catch (java.io.IOException e) {
            System.err.println("IOException " + e.getMessage());
        }
    }
}
