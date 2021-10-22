package org.crof;

import org.crof.utils.Parser;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ParserTest {
    @Test
    public void validateParse(){
        String expectedResult = "This is a quote";
        assertEquals(Parser.stripQuotes("\"This is a quote\""),expectedResult);
        assertEquals(Parser.stripQuotes("\"\"\"This is a quote\""),expectedResult);
    }

    @Test
    public void validateParseBody(){
        String body = "{\"username\":\"John\",\"password\":\"Crofford\"}";
        Map<String,String> map = Parser.parseBody(body);
        assertEquals(map.get("username"),"John");
        assertEquals(map.get("password"),"Crofford");
    }
}
