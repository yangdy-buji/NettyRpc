package com.netty.rpc.protocol;

import com.netty.rpc.util.JsonUtil;
import lombok.Data;

@Data
public class ServiceInfo{
    // interface name
    private String serviceName;
    // service version
    private String version;

    public String toJson() {
        String json = JsonUtil.objectToJson(this);
        return json;
    }

    @Override
    public String toString() {
        return toJson();
    }
}
