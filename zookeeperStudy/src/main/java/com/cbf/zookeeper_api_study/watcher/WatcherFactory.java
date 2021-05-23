package com.cbf.zookeeper_api_study.watcher;

import com.cbf.zookeeper_api_study.common_application.CbfZookeeperClient;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sky
 * @version 1.0
 * @date 2021/5/16
 * @description
 */
public class WatcherFactory {
    private static Map<WatcherTypeEnum, Class<?>> watcherMap = new HashMap<>();
    static {
        for(WatcherTypeEnum watcherTypeEnum: WatcherTypeEnum.values()){
            try{
                watcherMap.put(watcherTypeEnum, Class.forName(watcherTypeEnum.getClassName()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    public static BaseWatcher constructWatcher(CbfZookeeperClient client, ZooKeeper zk, Watcher watcher, String path, WatcherTypeEnum watcherTypeEnum, WatcherTypeEnum.registerTimes registerTimes){
        Class<?> watcherClass = watcherMap.get(watcherTypeEnum);
        try{
            Constructor con=watcherClass.getDeclaredConstructor(CbfZookeeperClient.class, ZooKeeper.class, Watcher.class, String.class);
            return (BaseWatcher) con.newInstance(client,zk, watcher, path);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
