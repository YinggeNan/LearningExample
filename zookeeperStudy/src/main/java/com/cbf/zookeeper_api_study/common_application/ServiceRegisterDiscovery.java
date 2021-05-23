package com.cbf.zookeeper_api_study.common_application;

/**
 * @author Sky
 * @version 1.0
 * @date 2021/5/16
 * @description
 */
public class ServiceRegisterDiscovery {
    private CbfZookeeperClient client = null;
    public ServiceRegisterDiscovery(String connectionString, int sessionTimeOut){
        client = new CbfZookeeperClient(connectionString, sessionTimeOut);
    }
}
