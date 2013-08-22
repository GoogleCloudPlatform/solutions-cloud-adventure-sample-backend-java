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

import com.google.cloud.solutions.cloudadventure.model.world.Maze.Cardinal;
import com.google.cloud.solutions.cloudadventure.util.WorldGenerator;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * This class represents one unit of space on a {@link Maze}.
 */
public class Tile implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * The coordinates of this Tile, specifying its absolute location on a {@link Maze}.
   * <p>
   * See {@code Coordinates}.
   */
  public Coordinates coord;

  /**
   * Cardinal directions in which this Tile is open to be traversed.
   */
  private Set<Cardinal> openTo;

  /**
   * The description of the surroundings on the tile.
   */
  private String description;

  /**
   * A set of things on this tile. Available types are specified in {@link CreatureTypes}.
   */
  private Set<Creature> creatures;

  /**
   * A set of things on this tile. Available types are specified in {@link PickupTypes}.
   */
  private Set<Pickup> pickups;

  public Tile() {}

  /**
   * Creates a new instance of {@link Tile}.
   * 
   * @param x the x-coordinate of this Tile
   * @param y the y-coordinate of this Tile
   * @param description the verbose description of this Tile
   * @param creatures list of {@link Creature}s found on this Tile
   * @param pickups list of {@link Pickup}s found on this Tile
   * @param openTo the cardinal directions which this Tile is open to
   */
  protected Tile(final int x, final int y, @Nullable final String description,
      Set<Creature> creatures, Set<Pickup> pickups, Cardinal... openTo) {
    this.coord = new Coordinates(x, y);
    this.openTo = new HashSet<Cardinal>();
    for (Cardinal cardinalDirection : openTo) {
      this.openTo.add(cardinalDirection);
    }
    if (description == null) {
      this.description = WorldGenerator.generateLocationDescription();
    } else {
      this.description = description;
    }
    this.creatures = creatures;
    this.pickups = pickups;
  }

  /**
   * This class represents the absolute location on {@link Maze}.
   */
  public static class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;
    public int x;
    public int y;

    public Coordinates() {
    }

    public Coordinates(final int x, final int y) {
      this.x = x;
      this.y = y;
    }

    public int getX() {
      return x;
    }

    public void setX(int x) {
      this.x = x;
    }

    public int getY() {
      return y;
    }

    public void setY(int y) {
      this.y = y;
    }

    @Override
    public String toString() {
      return super.toString() + "[x:" + x + ",y:" + y + "]";
    }
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public Coordinates getCoord() {
    return coord;
  }

  public Set<Cardinal> getOpenTo() {
    return openTo;
  }

  public String getDescription() {
    return description;
  }

  public Set<Creature> getCreatures() {
    return creatures;
  }

  public Set<Pickup> getPickups() {
    return pickups;
  }

  @Override
  public boolean equals(Object object) {
    if (object == null) {
      return false;
    }
    if (this == object) {
      return true;
    }
    if (!(object instanceof Tile)) {
      return false;
    }
    Tile location = (Tile) object;
    return this.coord.x == location.coord.x && this.coord.y == location.coord.y
        && this.openTo.equals(location.openTo);
  }

  @Override
  public int hashCode() {
    return (String.valueOf(this.coord.x) + String.valueOf(this.coord.y) + this.openTo).hashCode();
  }

  @Override
  public String toString() {
    StringBuilder tileString = new StringBuilder();
    tileString.append(super.toString());
    tileString.append("[coord: ");
    tileString.append(coord.toString());
    tileString.append("; openTo: ");
    for (Cardinal direction : openTo) {
      tileString.append(direction);
      tileString.append("|");
    }
    tileString.append("; description: ");
    tileString.append(description);
    tileString.append("; creatures: ");
    for (Creature creature : creatures) {
      tileString.append(creature);
      tileString.append("|");
    }
    tileString.append("; pickups: ");
    for (Pickup pickup : pickups) {
      tileString.append(pickup);
      tileString.append("|");
    }
    tileString.append("]");
    return tileString.toString();
  }
}
