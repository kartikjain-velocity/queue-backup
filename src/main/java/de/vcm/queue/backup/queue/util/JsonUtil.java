package de.vcm.queue.backup.queue.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    public static String toJson(Object object){
        ObjectMapper objectMapper = new ObjectMapper();
        String objectAsString = "";
        try {
            objectAsString = objectMapper.writeValueAsString(object);
            System.out.println("--->" + objectAsString);
        } catch (JsonProcessingException e) {
            //TODO: Add exception handling
            e.printStackTrace();
        }
        return objectAsString;
    }
}
