package com.songshuang.snowflake.init;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZkConfig {

  @Value("${zookeeper.address}")
  private String zookeeperAddress;

  @Bean
  public CuratorFramework createCurator() {
    CuratorFramework curatorClint = CuratorFrameworkFactory.builder()
        .connectString(zookeeperAddress)
        .retryPolicy(new RetryForever(2000))
        .build();

    curatorClint.start();

    return curatorClint;
  }
}
