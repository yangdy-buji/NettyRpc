package com.netty.rpc.protocol;

import lombok.Data;

@Data
public class ServiceInfo{
    // interface name
    private String serviceName;
    // service version
    private String version;

}
