package com.word.game.word_game.json;

import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Log4j2
public class ProcessWordJson {
    private static String key = "7067DB5FABBB2117916FE807D850E20C";
    private static String urlString;

    public List<String> processWord(String word) throws Exception {
        urlString = "https://stdict.korean.go.kr/api/search.do?key="+key+"&q="+word+"&req_type=json";
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        List<String> defList = new ArrayList<>();
        int responseCode = connection.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK){
            try {
                Scanner sc = new Scanner(connection.getInputStream());
                String response = sc.useDelimiter("\\a").next();

                sc.close();

                JSONObject jsonObj = new JSONObject(response);
                JSONArray defArray = jsonObj.getJSONObject("channel").getJSONArray("item");

                for (int i = 0; i < defArray.length(); i++) {
                    JSONObject definition = ((JSONObject) defArray.get(i)).getJSONObject("sense");
                    defList.add((String) definition.get("definition"));
                }
            }
            catch (Exception e){
                return defList;
            }
        }
        else{
            throw new Exception("ERROR");
        }
        return defList;
    }

}
