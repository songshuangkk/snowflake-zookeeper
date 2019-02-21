package com.songshuang.snowflake.init;

import com.google.common.base.Joiner;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ZkInit implements CommandLineRunner {

  private static final String PARENT_PATH = "/snowFlake";
  @Autowired
  private CuratorFramework curatorClint;

  @Override
  public void run(String... args) throws Exception {
    if (curatorClint.checkExists().forPath(PARENT_PATH) == null) {
      System.out.println("Create Path SnowFlake.");
      curatorClint.create().forPath("/snowFlake");
    }

    int size = curatorClint.getChildren().forPath(PARENT_PATH).size();
    if (size == 0) {
      size++;
    }
    // 遍历所有的子节点
    curatorClint.getChildren().forPath(PARENT_PATH).forEach(item -> {

    });
    // 通过在父节点上创界临时节点来变更
    curatorClint.create().withMode(CreateMode.EPHEMERAL).forPath(Joiner.on("/").join(PARENT_PATH, size));

    // watch child change to update node list
    watch(curatorClint);
  }

  private void watch(CuratorFramework curatorClint) {
    TreeCache treeCache = new TreeCache(curatorClint, PARENT_PATH);

    TreeCacheListener treeCacheListener = (client, event) -> {
      System.out.printf("event type = %s\n", event.getType());
      if (event.getType().equals(TreeCacheEvent.Type.NODE_REMOVED)) {
        System.out.printf("Node %s removed.", event.getData().getPath());
      }
    };
    treeCache.getListenable().addListener(treeCacheListener);

    try {
      treeCache.start();
    } catch (Exception e) {
      e.printStackTrace();
    }

    NodeCache nodeCache = new NodeCache(curatorClint, PARENT_PATH, false);

    NodeCacheListener nodeCacheListener = () -> {
      System.out.println("Node Cache Change");
    };

    nodeCache.getListenable().addListener(nodeCacheListener);

    try {
      nodeCache.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
