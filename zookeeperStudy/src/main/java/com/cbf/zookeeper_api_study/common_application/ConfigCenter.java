package com.cbf.zookeeper_api_study.common_application;

import com.cbf.zookeeper_api_study.watcher.WatcherTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sky
 * @version 1.0
 * @date 2021/5/16
 * @description 由zookeeper集群作为注册中心集群，所以保证了高可用
 * 每个worker端只需要调用一次serviceRegister()
 * 每个配置中心端（路由端）只需要调用一次serviceDiscovery(), 给用户提供服务信息使用 getAllWorkers()
 * 只要worker或配置中心服务器的主线程没有跑完，worker就可以和zookeeper集群一直保持连接，配置中心就可以一直给用户提供服务。
 */
@Slf4j
public class ConfigCenter {
    /**
     * 存储所有worker的{ip:port}
     */
    private Map<String, String> workerInfoMap = new HashMap<>();
    /**
     * zkClient变量尽量不能让使用该框架的人使用
     */
    private CbfZookeeperClient zkClient = null;
    private final String CHILD_NODE_NAME = "/configCenter/worker-";
    /**
     * 注册中心存放所有服务节点的根节点
     */
    private final String CONFIG_CENTER_ROOT_NODE_NAME = "/configCenter";
    /**
     * 逗号分割的ip:port,ip是server的ip,port是server的zoo.cfg配置文件中的clientPort配置项的值
     */
    public ConfigCenter(String connectionString, int sessionTimeOut){
        zkClient = new CbfZookeeperClient(connectionString, sessionTimeOut);
        initRegisterRootNode();

    }
    /**
     * 如果节点"/configCenter"不存在, 就创建该节点
     */
    private void initRegisterRootNode(){
        zkClient.createNodeIfNotExisted(CONFIG_CENTER_ROOT_NODE_NAME,"");
    }
    /**
     * serverInfo是 "ip:port" 字符串
     * 注册临时有序节点"/configCenter/worker-0001"
     * @param serverInfo
     */
    public void serviceRegister(String serverInfo){
        zkClient.createNodeIfNotExisted(CHILD_NODE_NAME, serverInfo, CreateMode.EPHEMERAL_SEQUENTIAL);
    }
    /**
     * 路由模块使用该函数
     * 路由模块应该主动检查所有 ip:port的健康程度
     * 对于哪些ping不到的，就应该主动删除
     * 路由模块应该保证高可用
     */
    public void serviceDiscovery(){
        updateWorkersInfo();
        watchRootChildrenChange();
    }

    /**
     *  获取"/configCenter"下所有临时有序子节点
     *  配置中心服务器用来给用户提供服务信息
     */
    public Map<String, String> getAllWorkers(){
        return workerInfoMap;
    }

    private void watchRootChildrenChange(){
        zkClient.registerWatcher(CONFIG_CENTER_ROOT_NODE_NAME,event -> updateWorkersInfo(),
                WatcherTypeEnum.NodeChildrenChange, WatcherTypeEnum.registerTimes.PERSISTENT);
    }
    private void updateWorkersInfo(){
        workerInfoMap.clear();
        List<String> childrenPath = zkClient.getNextLevelChildren(CONFIG_CENTER_ROOT_NODE_NAME);
        for(String childPath: childrenPath){
            String workerInfo = zkClient.getData(childPath);
            log.info("config center get workerInfo:{}", workerInfo);
            String[] strList = workerInfo.split(":");
            String ip = strList[0];
            String port = strList[1];
            log.info("a worker add in, ip:{}, port:{}", ip, port);
            workerInfoMap.put(ip, port);
        }
    }
}