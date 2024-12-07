package com.yoongu.security.apiserver.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class JsonUtil {

    public static String jsonStringToObject(String jsonStr) {
        String converted = jsonStr;
        if (StringUtils.startsWith(jsonStr, "\"")) {
            converted = converted.substring(1);     // Remove First "
            converted = converted.substring(0, converted.length() - 1);     // Remove Last "
            converted = converted.replaceAll("\\\\", "");   // Remove All \ char
            log.info("H2 Json String Converted : From ({}) -> To ({})", jsonStr, converted);
        }
        return converted;
    }

}
