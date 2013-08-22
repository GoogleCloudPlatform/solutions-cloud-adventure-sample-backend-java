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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents a user who has created an account through their device on this app. Each
 * user account is directly associated with an account on the user's device.
 */
public class GameUser implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * The unique identifier. It cannot be changed and is unique. Used for the Datastore Key.
   */
  private String account;

  /**
   * The unique handle for this user. It is chosen by the user.
   */
  private String handle;

  /**
   * The handles of the friends of this user.
   */
  private ArrayList<String> friends;

  /**
   * Total number of games this user has played.
   */
  private long totalGames;

  /**
   * Total number of gems this user has collected while in game.
   */
  private long totalGems;

  /**
   * Total number of mobs this user has defeated while in game.
   */
  private long totalMobsKilled;

  /**
   * Creates a new {@link GameUser}. The account string is tied permanently to this user. Static
   * factory method.
   * 
   * @param userAccount the unique key and identifier
   * @return a new {@link GameUser} instance
   */
  public static GameUser create(final String userAccount) {
    GameUser user = new GameUser();
    user.setAccount(userAccount);
    user.setFriends(new ArrayList<String>());
    user.setTotalGames(0);
    user.setTotalGems(0);
    user.setTotalMobsKilled(0);
    return user;
  }

  public String getAccount() {
    return account;
  }

  public void setAccount(String id) {
    this.account = id;
  }

  public String getHandle() {
    return handle;
  }

  public void setHandle(String handle) {
    this.handle = handle;
  }

  public ArrayList<String> getFriends() {
    return friends;
  }

  public void setFriends(ArrayList<String> friends) {
    this.friends = friends;
  }

  public long getTotalGames() {
    return totalGames;
  }

  public void setTotalGames(long totalGames) {
    this.totalGames = totalGames;
  }

  public long getTotalGems() {
    return totalGems;
  }

  public void setTotalGems(long totalGems) {
    this.totalGems = totalGems;
  }

  public long getTotalMobsKilled() {
    return totalMobsKilled;
  }

  public void setTotalMobsKilled(long totalMobsKilled) {
    this.totalMobsKilled = totalMobsKilled;
  }
}
