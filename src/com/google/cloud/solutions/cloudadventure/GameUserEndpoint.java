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

package com.google.cloud.solutions.cloudadventure;

import com.google.cloud.solutions.cloudadventure.model.FriendMessage;
import com.google.cloud.solutions.cloudadventure.model.GameUser;
import com.google.cloud.solutions.cloudadventure.util.StorageUtils;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An Endpoint class which exposes methods to manipulate primarily the GameUser model resource.
 */
@Api(name = "cloudadventure")
public class GameUserEndpoint {

  private static final Logger LOG = Logger.getLogger(GameUserEndpoint.class.getName());

  /**
   * Creates a new {@link GameUser}. The account string is tied permanently to this user and
   * functions as its key. Inserts the user into Datastore and returns the new instance of it.
   * 
   * @param userAccount the unique key and identifier
   * @return a new {@link GameUser} instance
   */
  @ApiMethod(path = "users/create/{userAccount}", name = "users.create",
      httpMethod = HttpMethod.POST)
  public GameUser createUser(@Named("userAccount") final String userAccount) {
    return insertUser(GameUser.create(userAccount));
  }

  /**
   * Sends an invitation to a user to be the sender's friend.
   * 
   * @param friendMessage contains the information needed for sending and processing the invite
   * @return the original {@link FriendMessage} if delivery was successful; {@code null} otherwise
   */
  @ApiMethod(path = "users/invite", name = "users.inviteFriend", httpMethod = HttpMethod.POST)
  public FriendMessage sendFriendInvite(final FriendMessage friendMessage) {
    boolean success = false;
    try {
      success = CloudMessenger.pingFriendInvite(
          "Friend invite from " + friendMessage.getFrom() + ".", friendMessage);
    } catch (IOException e) {
      LOG.warning(e.getMessage());
    }
    return success ? friendMessage : null;
  }

  /**
   * Updates the the friends lists of both users specified by the message. A notification is
   * sent from the user who accepted, to the user who sent the initial invite.
   * 
   * @param friendMessage contains the information needed for sending and processing the invite
   */
  @ApiMethod(path = "users/acceptFriend", name = "users.acceptFriend", httpMethod = HttpMethod.PUT)
  public void acceptFriendship(final FriendMessage friendMessage) {
    String fromHandle = friendMessage.getFrom();
    String toHandle = friendMessage.getTo();
    try {
      GameUser fromUser = getUserByHandle(fromHandle);
      GameUser toUser = getUserByHandle(toHandle);
      if (fromUser.getFriends() == null) {
        fromUser.setFriends(new ArrayList<String>());
      }
      fromUser.getFriends().add(toHandle);
      updateUser(fromUser);
      if (toUser.getFriends() == null) {
        toUser.setFriends(new ArrayList<String>());
      }
      toUser.getFriends().add(fromHandle);
      updateUser(toUser);

      CloudMessenger.pingFriendAccept(
          fromHandle + " has accepted your friend invite.", friendMessage);
    } catch (IOException e) {
      LOG.warning(e.getMessage());
    }
  }

  /**
   * Updates the stored user with the scores from their most recently finished game.
   *
   * @param handle
   * @param gemsCollected
   * @param mobsKilled
   */
  protected static void updateUserWithPostGameScores(
      final String handle, long gemsCollected, long mobsKilled) {
    Filter userHandleFilter =
        new Query.FilterPredicate("user_handle", FilterOperator.EQUAL, handle);
    Query q = new Query("User").setFilter(userHandleFilter);
    Entity entity = StorageUtils.getDatastore().prepare(q).asSingleEntity();
    entity.setProperty("total_games",
        (Long) entity.getProperty("total_gems") + 1);
    entity.setProperty("total_gems",
        (Long) entity.getProperty("total_gems") + gemsCollected);
    entity.setProperty("total_mobs_killed",
        (Long) entity.getProperty("total_mobs_killed") + mobsKilled);
    StorageUtils.getDatastore().put(entity);
  }

  /**
   * Gets the user from the Datastore.
   * 
   * @param account the account string of the user
   * @return the {@link GameUser}
   */
  @SuppressWarnings("unchecked")
  @ApiMethod(path = "users/{account}", name = "users.get")
  public GameUser getUser(@Named("account") String account) {
    Key key = StorageUtils.getUserDatastoreKey(account);
    Entity entity;
    try {
      entity = StorageUtils.getDatastore().get(key);
    } catch (EntityNotFoundException e) {
      return null;
    }
    GameUser user = new GameUser();
    user.setAccount(entity.getKey().getName());
    user.setHandle((String) entity.getProperty("user_handle"));
    user.setFriends((ArrayList<String>) entity.getProperty("friends"));
    user.setTotalGames((Long) entity.getProperty("total_games"));
    user.setTotalGems((Long) entity.getProperty("total_gems"));
    user.setTotalMobsKilled((Long) entity.getProperty("total_mobs_killed"));
    return user;
  }

  /**
   * Gets the user from the Datastore using their unique user handle.
   * 
   * @param handle the handle of the user
   * @return the {@link GameUser}
   */
  @SuppressWarnings("unchecked")
  @ApiMethod(path = "users/name/{handle}", name = "users.getByHandle")
  public GameUser getUserByHandle(@Named("handle") String handle) {
    Filter userHandleFilter =
        new Query.FilterPredicate("user_handle", FilterOperator.EQUAL, handle);
    Query q = new Query("User").setFilter(userHandleFilter);
    Entity entity = StorageUtils.getDatastore().prepare(q).asSingleEntity();
    if (entity == null) {
      return null;
    }
    GameUser user = new GameUser();
    user.setAccount(entity.getKey().getName());
    user.setHandle((String) entity.getProperty("user_handle"));
    user.setFriends((ArrayList<String>) entity.getProperty("friends"));
    user.setTotalGames((Long) entity.getProperty("total_games"));
    user.setTotalGems((Long) entity.getProperty("total_gems"));
    user.setTotalMobsKilled((Long) entity.getProperty("total_mobs_killed"));
    return user;
  }

  /**
   * Inserts the user into the Datastore.
   * 
   * @param user the user to be inserted
   * @return the {@link GameUser} that was inserted
   */
  @ApiMethod(path = "users", name = "users.insert")
  public GameUser insertUser(GameUser user) {
    Key key = StorageUtils.getUserDatastoreKey(user.getAccount());
    Entity entity = new Entity(key);
    entity.setProperty("user_handle", user.getHandle());
    entity.setUnindexedProperty("friends", user.getFriends());
    entity.setProperty("total_games", user.getTotalGames());
    entity.setProperty("total_gems", user.getTotalGems());
    entity.setProperty("total_mobs_killed", user.getTotalMobsKilled());
    StorageUtils.getDatastore().put(entity);
    return user;
  }

  /**
   * Updates the user with the same account in the Datastore. Uses HTTP PUT.
   * 
   * @param user the user to be updated
   * @return the {@link GameUser} that was updated
   */
  @ApiMethod(path = "users", name = "users.update")
  public GameUser updateUser(GameUser user) {
    Key key = StorageUtils.getUserDatastoreKey(user.getAccount());
    Entity entity = new Entity(key);
    entity.setProperty("user_handle", user.getHandle());
    entity.setUnindexedProperty("friends", user.getFriends());
    entity.setProperty("total_games", user.getTotalGames());
    entity.setProperty("total_gems", user.getTotalGems());
    entity.setProperty("total_mobs_killed", user.getTotalMobsKilled());
    StorageUtils.getDatastore().put(entity);
    return user;
  }

  /**
   * Remove the user from the Datastore.
   * 
   * @param account the account string of the user
   */
  @ApiMethod(path = "users/{account}", name = "users.remove")
  public void removeUser(@Named("account") String account) {
    Key key = StorageUtils.getUserDatastoreKey(account);
    StorageUtils.getDatastore().delete(key);
  }
}
