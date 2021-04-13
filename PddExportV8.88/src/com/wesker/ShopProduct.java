package com.wesker;

import java.util.Map;

public class ShopProduct {
    private String shopName;
    private Map<String, String> shopMap;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public Map<String, String> getShopMap() {
        return shopMap;
    }

    public void setShopMap(Map<String, String> shopMap) {
        this.shopMap = shopMap;
    }
}
