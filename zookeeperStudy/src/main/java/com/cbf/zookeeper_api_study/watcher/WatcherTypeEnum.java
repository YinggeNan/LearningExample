package com.cbf.zookeeper_api_study.watcher;

/**
 * @author Sky
 * @version 1.0
 * @date 2021/5/16
 * @description:
 */
public enum WatcherTypeEnum {
    /**
     *  字节点数量发生变化: 增加或删除子节点
     */
    NodeChildrenNumberChange("cbfCommon.zookeeper.customClient.watcher.NodeChildrenNumberChangeWatcher"),
    /**
     * 子节点内容发生变化
     */
    NodeChildrenContentChange("cbfCommon.zookeeper.customClient.watcher.NodeChildrenContentChangeWatcher"),
    /**
     * 字节点数量发生变化或子节点内容发生变化
     */
    NodeChildrenChange("cbfCommon.zookeeper.customClient.watcher.NodeChildrenChangeWatcher"),
    /**
     * 节点被删除
     */
    NodeDelete("cbfCommon.zookeeper.customClient.watcher.NodeDeleteWatcher"),
    /**
     * 节点被创建
     */
    NodeCreate("cbfCommon.zookeeper.customClient.watcher.NodeCreateWatcher"),
    /**
     * 节点内容发生变化
     */
    NodeContentChange("cbfCommon.zookeeper.customClient.watcher.NodeContentChangeWatcher"),
    NodeChange("cbfCommon.zookeeper.customClient.watcher.NodeChangeWatcher");

    private String className;
    public String getClassName(){
        return className;
    }
    WatcherTypeEnum(String className){
        this.className = className;
    }
    public enum registerTimes{
        /**
         * 注册一次
         */
        ONCE,
        /**
         * 永久注册
         */
        PERSISTENT;
    }
}
