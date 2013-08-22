/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.google.cloud.solutions.cloudadventure.model.world;

import java.io.Serializable;

/**
 * Items that can be encountered on a {@link Tile} and picked up by the {@link Player}.
 */
public class Pickup implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;
  private String description;
  private long numUses;
  private long maxEffect;

  public Pickup() {}

  public Pickup(String name, String adjective, int numUses, int maxEffect) {
    this.name = name;
    if (maxEffect < 0) {
      this.description = adjective + "-looking " + name;
    } else {
      this.description = name;
    }
    this.numUses = numUses;
    this.maxEffect = maxEffect;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public long getNumUses() {
    return numUses;
  }

  public void setNumUses(long numUses) {
    this.numUses = numUses;
  }

  public long getMaxEffect() {
    return maxEffect;
  }
}
