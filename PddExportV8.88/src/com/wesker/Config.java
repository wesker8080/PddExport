package com.wesker;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Config {
    public static String readJsonFile(String filePath){
        BufferedReader reader = null;
        String readJson = "";
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while ((tempString = reader.readLine()) != null){
                readJson += tempString;
            }
        }catch (IOException e){

        }finally {
            if (reader != null){
                try {
                    reader.close();
                }catch (IOException e){

                }
            }
        }

        // 获取json
        try {
            JSONObject jsonObject = JSONObject.parseObject(readJson);
            //System.out.println(JSON.toJSONString(jsonObject));
            return readJson;
        }catch (JSONException e){

        }
        return null;
    }

}
