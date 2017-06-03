package com.tecknic.nehal.flowercatalog.parsers;

import com.tecknic.nehal.flowercatalog.model.Flower;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FlowerJSONParser {

    public static List<Flower> parseFeed(String content){

        List<Flower> flowerList = null;
        try {
            JSONArray arr = new JSONArray(content);
            flowerList = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++){

                JSONObject obj = arr.getJSONObject(i);
                Flower flower = new Flower();

                flower.setProductId(obj.getInt("productId"));
                flower.setName(obj.getString("name"));
                flower.setCategory(obj.getString("category"));
                flower.setInstructions(obj.getString("instructions"));
                flower.setPhoto(obj.getString("photo"));
                flower.setPrice(obj.getDouble("price"));

                flowerList.add(flower);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return flowerList;

    }
}
