package com.netty.rpc.server.core;

public  interface Server {
    /**
     * start server
     *
     * @param
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * stop server
     *
     * @throws Exception
     */
    void stop() throws Exception;

}
