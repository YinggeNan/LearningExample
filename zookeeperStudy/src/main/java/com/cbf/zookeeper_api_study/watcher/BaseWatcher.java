package com.cbf.zookeeper_api_study.watcher;

/**
 * @author Sky
 * @version 1.0
 * @date 2021/5/16
 * @description
 */


import com.cbf.zookeeper_api_study.common_application.CbfZookeeperClient;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public abstract class BaseWatcher implements Watcher{
    /**
     * 监视节点
     */
    protected String path;
    protected ZooKeeper zk = null;
    protected CbfZookeeperClient client = null;
    /**
     * 回调函数
     */
    protected Watcher zookeeperWatcher = null;
    protected BaseWatcher(CbfZookeeperClient client, ZooKeeper zk, Watcher zookeeperWatcher, String path){
        this.zk = zk;
        this.zookeeperWatcher = zookeeperWatcher;
        this.path = path;
        this.client = client;
    }

    /**
     * 注册watcher
     */
    public abstract void registerWatcher();
}
