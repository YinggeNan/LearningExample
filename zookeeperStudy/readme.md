### zookeeper包提供了对zookeeper原生api的包装
#### use case:
***
一.基本api包装,使用CbfZookeeperClient类
1. 获取给定节点的所有子节点
2. 在给定节点上注册永久watcher
3. 在给定节点上注册可以同时监控子节点数量变化和子节点内容变量的watcher
***
二.常见应用
1. 注册中心 ConfigCenter类
2. 分布式队列 DistributedQueue类
3. 分布式锁 DistributedLock类
4. 分布式Barrier DistributedBarrier类
5. leader选举 LeaderSelection类
6. 分布式序列号生成器 DistributedSequenceGenerator类
***
三.使用例子
1.拉取jar包: gradle、maven  
2.初始化,连接上zookeeper集群  
3.开始使用  