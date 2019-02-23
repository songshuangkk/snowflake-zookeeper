package com.songshuang.snowflake.init;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZkNode {

  private Integer path;

  private Boolean active;
}
