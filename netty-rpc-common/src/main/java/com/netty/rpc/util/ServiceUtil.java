package com.netty.rpc.util;

import org.springframework.util.StringUtils;

public class ServiceUtil {
    public static final String SERVICE_CONCAT_TOKEN = ":";
    public static final String DEFAULT_VERSION = "1.0";

    public static String makeServiceKey(String interfaceName, String version) {
        String serviceKey = interfaceName;
        if(!StringUtils.hasText(version)){
            version = DEFAULT_VERSION;
        }
        serviceKey += SERVICE_CONCAT_TOKEN.concat(version);
        return serviceKey;
    }
}
