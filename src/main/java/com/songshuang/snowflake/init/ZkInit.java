package com.songshuang.snowflake.init;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.songshuang.snowflake.generator.SnowFlakeGenerator;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

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

    int path = 1;
    int size = curatorClint.getChildren().forPath(PARENT_PATH).size();

    List<ZkNode> nodeList = Lists.newArrayList();
    final int[] maxNode = {0};
    boolean allExists = false;
    // 遍历所有的子节点
    curatorClint.getChildren().forPath(PARENT_PATH).forEach(item -> {
      ZkNode zkNode = ZkNode.builder()
          .active(true)
          .path(Integer.parseInt(item))
          .build();

      nodeList.add(zkNode);

      if (Integer.parseInt(item) > maxNode[0]) {
        maxNode[0] = Integer.parseInt(item);
      }
    });

    for (int i=1; i<=maxNode[0]; i++) {
      boolean exists = false;
      for (ZkNode zkNode : nodeList) {
        if (zkNode.getPath().equals(i)) {
          exists = true;
          break;
        }
      }

      if (!exists) {
        path = i;
        System.out.printf("新增节点machine ID %d\n", path);
        allExists = false;
        break;
      }

      allExists = true;
    }

    if (allExists) {
      path ++;
    }

    // 通过在父节点上创界临时节点来变更
    curatorClint.create().withMode(CreateMode.EPHEMERAL).forPath(Joiner.on("/").join(PARENT_PATH, path));

    SnowFlakeGenerator.MATCH_ID = path;

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
