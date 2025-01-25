package com.baeldung.thrift.impl;

import org.apache.thrift.server.*;
import org.apache.thrift.transport.*;

public class CrossPlatformServiceServer {
    TServer server;
    
    public void start() throws TTransportException {
        TServerTransport serverTransport = new TServerSocket(9090);
        server = new TSimpleServer(new TServer.Args(serverTransport)
          .processor(new CrossPlatformService.Processor<>(new CrossPlatformServiceImpl())));

        System.out.print("Starting the server... ");

        server.serve();

        System.out.println("done.");
    }

    public void stop() {
        if (server != null && server.isServing()) {
            System.out.print("Stopping the server... ");

            server.stop();

            System.out.println("done.");
        }
    }

    public static void main(String[] args) throws Exception {
        CrossPlatformServiceServer s= new CrossPlatformServiceServer();
        s.start();
        
    }

}
