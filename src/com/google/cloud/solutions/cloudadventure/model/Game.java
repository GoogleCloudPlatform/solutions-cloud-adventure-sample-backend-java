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

package com.google.cloud.solutions.cloudadventure.model;

import com.google.cloud.solutions.cloudadventure.model.world.Maze;

import java.io.Serializable;

/**
 * This class represents the Game object in the model, which keeps track of central in-game
 * statistics.
 */
public class Game implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Unique game identifier.
   */
  private String id;

  /**
   * The {@link Maze} map used for this game.
   */
  private Maze maze;

  /**
   * The state of the game. {@code true} if game is running, {@code false} otherwise.
   */
  private boolean isRunning;

  public static Game create(final String id) {
    Game game = new Game();
    game.setId(id);
    return game;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Maze getMaze() {
    return maze;
  }

  public void setMaze(Maze maze) {
    this.maze = maze;
  }

  public boolean isRunning() {
    return isRunning;
  }

  public void setRunning(boolean isRunning) {
    this.isRunning = isRunning;
  }

  /**
   * Starts this game.
   */
  public void start() {
    this.isRunning = true;
  }

  /**
   * Ends this game.
   */
  public void end() {
    this.isRunning = false;
  }
}
