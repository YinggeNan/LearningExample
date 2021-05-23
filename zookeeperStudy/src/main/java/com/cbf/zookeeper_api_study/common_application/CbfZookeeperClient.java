package com.cbf.zookeeper_api_study.common_application;

import com.cbf.zookeeper_api_study.watcher.BaseWatcher;
import com.cbf.zookeeper_api_study.watcher.WatcherFactory;
import com.cbf.zookeeper_api_study.watcher.WatcherTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author Sky
 * @version 1.0
 * @date 2021/5/15
 * @description zookeeper util
 */
@Slf4j
public class CbfZookeeperClient {
    /**
     * zk变量尽量不让使用该框架的人使用
     */
    private ZooKeeper zk = null;

    public ZooKeeper getZk() {
        return zk;
    }

    /**
     * 逗号分割的ip:port,ip是server的ip,port是server的zoo.cfg配置文件中的clientPort配置项的值
     */
    public CbfZookeeperClient(String connectionString, int sessionTimeOut){
        try{
            zk = new ZooKeeper(connectionString, sessionTimeOut, event -> {
                log.info("a event comes, type:"+event.getType());
                log.info("session state:"+ event.getState().name());
            });
            /**
             *  有时候刚刚使用zookeeperClient可能由于刚启动获取不到数据,休眠两秒，让zookeeper client连接稳定
             */
            Thread.sleep(2000);
        } catch (IOException  e) {
            e.printStackTrace();
        } catch (InterruptedException e){

        }
    }
    public boolean createNodeIfNotExisted(String path, String content){
        if(content==null){
            content = "";
        }
        try{
            zk.create(path, content.getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }catch (KeeperException e){
            log.error("create node {} failed {}",path, e.code().toString());
            return false;
        }
        return true;
    }
    public boolean createNodeIfNotExisted(String path, String content, CreateMode createMode){
        if(content==null){
            content = "";
        }
        try{
            zk.create(path, content.getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }catch (KeeperException e){
            log.error("create node {} failed {}",path, e.code().toString());
            return false;
        }
        return true;
    }
    /**
     * zookeeper自带的delete方法只能删除没有子节点的节点
     */
    public boolean deleteNodeWithNoChildren(String path){
        try{
        // 指定要删除的版本，-1表示删除所有版本
        zk.delete(path, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e){
            log.error("delete node {} failed:{}", path, e.code().name());
        }
        return true;
    }
    public boolean deleteNodeWithChildren(String path){
        List<List<String>> allChildren = getAllLevelLevelChildren(path);
        try{

            // 从最底层往上层删除节点
            for(int i=allChildren.size()-1;i>=0;i--){
                for(String child: allChildren.get(i)){
                    // 指定要删除的版本，-1表示删除所有版本
                    zk.delete(child, -1);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e){
            log.error("delete node {} failed:{}", path, e.code().name());
        }
        return true;
    }
    public boolean existNode(String path){
        Stat stat = null;
        try{
            stat = zk.exists(path, false);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
        return stat==null;
    }
    public Stat getStat(String path){
        Stat stat = null;
        try{
            stat = zk.exists(path, false);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
        return stat;
    }

    /**
     * 返回的是 /parent/child 这种节点
     * @param parentPath
     * @return
     */
    public List<String> getNextLevelChildren(String parentPath){
        List<String> childrenWithSlash = null;
        try{
            List<String> children = zk.getChildren(parentPath, false);
            childrenWithSlash = new LinkedList<>();
            for(int i=0;i<children.size();i++){
                childrenWithSlash.add(parentPath+"/" + children.get(i));
            }
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
        return childrenWithSlash;
    }

    /**
     * 使用层次遍历返回zookeeper树型命名空间的所有节点
     * 比如index=0的为第一层所有节点从左到右排序
     * 返回的是 /parent/child 这种节点
     * @param parentPath
     * @return
     */
    public List<List<String>> getAllLevelLevelChildren(String parentPath){
        Queue<String> queue = new LinkedList<>();
        List<List<String>> allChildren = new LinkedList<>();
        if(StringUtils.isEmpty(parentPath)){
            return allChildren;
        }
        queue.add(parentPath);
        while(queue.size()>0){
            int currentLevelNumber = queue.size();
            List<String> currentLevelChildren = new LinkedList<>();
            for(int i=0;i<currentLevelNumber;i++){
                String node = queue.poll();
                currentLevelChildren.add(node);
                try{
                    List<String> nextLevelChildren= zk.getChildren(node, false);
                    for(String item:nextLevelChildren){
                        if (node.equals("/")){
                            queue.add(node+item);
                        }else{
                            queue.add(node+"/"+item);
                        }
                    }
                } catch (InterruptedException | KeeperException e) {
                    e.printStackTrace();
                }
            }
            allChildren.add(currentLevelChildren);
        }
        return allChildren;
    }
    public boolean updateNode(String path, String content){
        try{
            zk.setData(path, content.getBytes(), -1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean registerWatcher(String path, Watcher watcher, WatcherTypeEnum typeEnum, WatcherTypeEnum.registerTimes registerTimes){
        BaseWatcher baseWatcher = WatcherFactory.constructWatcher(this, zk, watcher, path, typeEnum, registerTimes);
        assert baseWatcher != null;
        baseWatcher.registerWatcher();
        return true;
    }
    public String getData(String path){
        String content = null;
        try{
            byte[] bytes = zk.getData(path, false, zk.exists(path, false));
            content = new String(bytes);
        } catch (InterruptedException  e) {
            e.printStackTrace();
        } catch (KeeperException e){
            log.error("path:{}, has error:{}",e.getPath(), e.getMessage());
        } catch (IllegalArgumentException e){
            log.error("Path must start with / character, path:{}",path);
        }
        return content;
    }
}
