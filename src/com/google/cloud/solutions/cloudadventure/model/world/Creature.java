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
 * Creatures that can be encountered on a {@link Tile}.
 */
public class Creature implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;
  private String description;
  private long hitPoints;
  private long maxEffect;

  public Creature() {}

  public Creature(String name, String adjective, int hitPoints, int maxEffect) {
    this.name = name;
    this.description = adjective + "-looking " + name;
    this.hitPoints = hitPoints;
    this.maxEffect = maxEffect;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public long getHitPoints() {
    return hitPoints;
  }

  public void setHitPoints(long hitPoints) {
    this.hitPoints = hitPoints;
  }

  public long getMaxEffect() {
    return maxEffect;
  }
}
