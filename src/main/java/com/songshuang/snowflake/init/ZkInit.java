package com.songshuang.snowflake.init;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ZkInit implements CommandLineRunner {

  @Value("${zookeeper.address}")
  private String zookeeperAddress;

  @Override
  public void run(String... args) throws Exception {
    CuratorFramework curatorClint = CuratorFrameworkFactory.builder()
        .connectString(zookeeperAddress)
        .retryPolicy(new RetryForever(2000))
        .build();

    curatorClint.start();

//    if (curatorClint.checkExists().forPath("/snowFlake") != null) {
//      System.out.println("Path SnowFlake exists.");
//      curatorClint.delete().forPath("/snowFlake");
//    } else {
//      System.out.println("Create Path SnowFlake.");
//      curatorClint.create().forPath("/snowFlake");
//    }

    // 通过在父节点上创界临时节点来变更

    curatorClint.getChildren().forPath("/snowFlake");

    curatorClint.create().withMode(CreateMode.EPHEMERAL).forPath("/snowFlake/child");

    curatorClint.getChildren().forPath("/snowFlake");
  }
}
