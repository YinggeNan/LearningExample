package com.cbf.zookeeper_api_study.watcher;

import com.cbf.zookeeper_api_study.common_application.CbfZookeeperClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

/**
 * @author Sky
 * @version 1.0
 * @date 2021/5/16
 * @description 监视子节点创建、删除、内容修改
 */
@Slf4j
public class NodeChildrenChangeWatcher extends BaseWatcher {
    public NodeChildrenChangeWatcher(CbfZookeeperClient client, ZooKeeper zk, Watcher zookeeperWatcher, String path){
        super(client, zk, zookeeperWatcher, path);
    }

    @Override
    public void registerWatcher() {
        try{
            // 注册子节点创建、删除监视点
            this.zk.getChildren(this.path, this);
            List<List<String>> allChildren = client.getAllLevelLevelChildren(path);
            // 注册子节点内容变量监视点
            for(int i=1;i<allChildren.size();i++){
                for(String child: allChildren.get(i)){
                    this.zk.exists(child, this);
                }
            }
            this.zk.exists(this.path, this);
            log.info("has registered children change watcher for {}", path);
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
            zookeeperWatcher.process(event);
            Event.EventType eventType = event.getType();
            if(eventType.equals(Event.EventType.NodeChildrenChanged)){
                log.info("{}'s children number has changed", path);
                this.zk.getChildren(this.path, this);
            }else if(eventType.equals(Event.EventType.NodeDataChanged)){
                log.info("{}'s children {}'s content has changed", path, event.getPath());
                this.zk.exists(event.getPath(), this);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            log.error(e.code().toString(), e.code().name());
            e.printStackTrace();
        }
    }
}
