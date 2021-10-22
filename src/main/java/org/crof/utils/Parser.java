package org.crof.utils;

import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    public static Map<String,String> parseBody(String body){
        Map<String,String> vals = new HashMap<>();
        Pattern pattern = Pattern.compile("\"[0-9a-zA-Z ',!]+\"");
        Matcher matcher = pattern.matcher(body);
        String first = "";
        String second = "";
        int count = 0;
        while(matcher.find()){
            if(count == 0){
                first = stripQuotes(matcher.group());
                count++;
            }
            else{
                second = stripQuotes(matcher.group());
                vals.put(first,second);
                count--;
            }
        }

        return vals;
    }

    public static String stripQuotes(String input){
        return input.replaceAll("\"","");
    }
}
