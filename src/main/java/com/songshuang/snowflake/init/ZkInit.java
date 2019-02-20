package com.songshuang.snowflake.init;

import com.google.common.base.Joiner;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ZkInit implements CommandLineRunner {

  private static final String PARENT_PATH = "/snowFlake";

  @Value("${zookeeper.address}")
  private String zookeeperAddress;

  @Override
  public void run(String... args) throws Exception {
    CuratorFramework curatorClint = CuratorFrameworkFactory.builder()
        .connectString(zookeeperAddress)
        .retryPolicy(new RetryForever(2000))
        .build();

    curatorClint.start();

    if (curatorClint.checkExists().forPath(PARENT_PATH) != null) {
      System.out.println("Path SnowFlake exists.");
      curatorClint.delete().forPath(PARENT_PATH);
    } else {
      System.out.println("Create Path SnowFlake.");
      curatorClint.create().forPath("/snowFlake");
    }

    int size = curatorClint.getChildren().forPath(PARENT_PATH).size();
    // 遍历所有的子节点
    curatorClint.getChildren().forPath(PARENT_PATH).forEach(item -> {

    });
    // 通过在父节点上创界临时节点来变更
    curatorClint.create().withMode(CreateMode.EPHEMERAL).forPath(Joiner.on("/").join(PARENT_PATH, size));

    // watch child change to update node list
  }
}
