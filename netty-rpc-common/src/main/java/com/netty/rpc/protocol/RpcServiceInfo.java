package com.netty.rpc.protocol;

import com.netty.rpc.util.JsonUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
@Data
public class RpcServiceInfo implements Serializable {
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
