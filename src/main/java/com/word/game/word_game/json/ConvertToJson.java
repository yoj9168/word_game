package com.word.game.word_game.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.word.game.word_game.message.JSON;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class ConvertToJson {
    public static String convertToJson(String key, String value) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JSON json = new JSON(key, value);
        log.info(objectMapper.writeValueAsString(json));
        return objectMapper.writeValueAsString(json);
    }
    public static String convertToJson(String key, List<String> value) throws JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        JSON json = new JSON(key, value);
        log.info(objectMapper.writeValueAsString(json));
        return objectMapper.writeValueAsString(json);
    }
}
