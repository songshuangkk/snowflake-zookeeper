package com.songshuang.snowflake.controller;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZkController {

  @Autowired
  private CuratorFramework curatorFramework;

  @GetMapping("/delete/{num}")
  public void delete(@PathVariable("num") String num) throws Exception {
    curatorFramework.delete().forPath("/snowFlake/" + num);
  }

  @GetMapping("/add/{num}")
  public void add(@PathVariable("num") String num) throws Exception {
    curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath("/snowFlake/" + num);
  }
}
