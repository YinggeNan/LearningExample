package com.cbf.zookeeper_api_study.watcher;

import com.cbf.zookeeper_api_study.common_application.CbfZookeeperClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * @author Sky
 * @version 1.0
 * @date 2021/5/16
 * @description 监视节点内容修改
 */
@Slf4j
public class NodeContentChangeWatcher extends BaseWatcher{
    public NodeContentChangeWatcher(CbfZookeeperClient client, ZooKeeper zk, Watcher zookeeperWatcher, String path){
        super(client,zk, zookeeperWatcher, path);
    }

    @Override
    public void registerWatcher() {
        try{
            this.zk.getData(path, this, zk.exists(path, false));
            log.info("has registered node content watcher for {}", path);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            log.error(e.code().toString(), e.code().name());
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        try{
            log.info("node content has changed");
            zookeeperWatcher.process(event);
            if(event.getType().equals(Event.EventType.NodeDataChanged)){
                this.zk.getData(path, this, zk.exists(path, false));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            log.error(e.code().toString(), e.code().name());
            e.printStackTrace();
        }
    }
}
