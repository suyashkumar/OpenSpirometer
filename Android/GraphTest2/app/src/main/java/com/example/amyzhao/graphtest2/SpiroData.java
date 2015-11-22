package com.example.amyzhao.graphtest2;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
/**
 * SpiroData
 * Created by suyashkumar on 11/22/15.
 *
 */
public class SpiroData {
    //Date date;
    String date;
    List<String> tags;
    List<Double> data;
    double temp;
    double humidity;
    double FEV;
    double FVC;

    public SpiroData(){
        date="The Date";
        temp=0;
        humidity=0;
    }
    // Overloaded constructor allows init of object of construction
    public SpiroData(String inputJSON){
        try {
            populateFieldsfromJSON(inputJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        date="The Date";
    }

    public void populateFieldsfromJSON(String inputJSON) throws JSONException {
        JSONObject jObj = new JSONObject(inputJSON);
        FEV = jObj.getDouble("FEV");
        FVC = jObj.getDouble("FVC");
        temp = jObj.getJSONObject("params").getDouble("temp");
        humidity = jObj.getJSONObject("params").getDouble("humidity");

        JSONArray jArr = jObj.getJSONArray("data");
        data=new ArrayList<Double>();
        for (int i=0; i<jArr.length();i++){
            data.add(jArr.getDouble(i));
        }

        tags = new ArrayList<String>();
        JSONArray tagArray = jObj.getJSONObject("params").getJSONArray("tags");
        for (int i=0;i<tagArray.length();i++){
            tags.add(tagArray.getString(i));
        }


    }
    public String toJSONString(){
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("FEV",this.FEV);
            jObj.put("FVC", this.FVC);

            // Build data array:
            JSONArray dataArray = new JSONArray();
            for (Double item : data){
                dataArray.put(item);
            }
            jObj.put("data",dataArray); // put data into object
            // Build params obj
            JSONObject paramsObj = new JSONObject();
            paramsObj.put("temp",temp);
            paramsObj.put("humidity",humidity);
            // Build tags array
            JSONArray tagsArray = new JSONArray();
            for (String item : tags){
                tagsArray.put(item);
            }
            paramsObj.put("tags",tagsArray);

            jObj.put("params", paramsObj); // add params obj to main JSON obj
            jObj.put("date", date);

            return jObj.toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null; // return null if can't parse JSON obj.
    }

}
