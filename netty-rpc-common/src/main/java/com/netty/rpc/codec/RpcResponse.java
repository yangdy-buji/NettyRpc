package com.netty.rpc.codec;

import lombok.Data;

import java.io.Serializable;

/**
 * RPC Response
 *
 * @author luxiaoxun
 */
@Data
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 8215493329459772524L;

    private String requestId;
    private String error;
    private Object result;

    public boolean isError() {
        return error != null;
    }

}
