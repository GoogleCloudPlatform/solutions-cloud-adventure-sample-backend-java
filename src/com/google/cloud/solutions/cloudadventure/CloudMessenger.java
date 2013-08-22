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

import com.google.android.gcm.server.*;
import com.google.cloud.solutions.cloudadventure.model.DeviceInfo;
import com.google.cloud.solutions.cloudadventure.model.FriendMessage;
import com.google.cloud.solutions.cloudadventure.model.GameMessage;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * This class manages the sending of various types of messages through Google Cloud Messaging.
 */
public class CloudMessenger {

  private static final Logger LOG = Logger.getLogger(CloudMessenger.class.getName());
  private static final String API_KEY = << your API key >>;
  private static final DeviceInfoEndpoint endpoint = new DeviceInfoEndpoint();

  /*
   * NOTE on the following constants: any updates to these will also need to be mirrored in
   * GCMIntentService.java in the corresponding client application.
   */
  private static final String GCM_PAYLOAD_PING_REASON = "GCM_PAYLOAD_PING_REASON";
  private static final String GCM_PAYLOAD_MESSAGE = "GCM_PAYLOAD_MESSAGE";
  private static final String GCM_PAYLOAD_FROM_USER_HANDLE = "GCM_PAYLOAD_FROM_USER_HANDLE";
  private static final String GCM_PAYLOAD_TO_USER_HANDLE = "GCM_PAYLOAD_TO_USER_HANDLE";
  private static final String GCM_PAYLOAD_GAME_ID = "GCM_PAYLOAD_GAME_ID";

  private static final String PING_REASON_GAME_INVITE = "PING_REASON_GAME_INVITE";
  private static final String PING_REASON_GAME_STARTED = "PING_REASON_GAME_STARTED";
  private static final String PING_REASON_GAME_DESTROYED = "PING_REASON_GAME_DESTROYED";
  private static final String PING_REASON_GAME_ENDED = "PING_REASON_GAME_ENDED";
  private static final String PING_REASON_PLAYER_JOINED = "PING_REASON_PLAYER_JOINED";
  private static final String PING_REASON_PLAYER_LEFT = "PING_REASON_PLAYER_LEFT";
  private static final String PING_REASON_PLAYER_END_SCORE = "PING_REASON_PLAYER_END_STATS";
  private static final String PING_REASON_FRIEND_INVITE = "PING_REASON_FRIEND_INVITE";
  private static final String PING_REASON_FRIEND_ACCEPT = "PING_REASON_FRIEND_ACCEPT";

  /**
   * Sends the message using the Sender object to the registered device.
   *
   * @param message the message to be sent in the GCM ping to the device.
   * @param sender the Sender object to be used for ping,
   * @param deviceInfo the registration id of the device.
   * @return Result the result of the ping.
   * @throws IOException
   */
  private static Result sendViaGcm(Message msg, Sender sender, DeviceInfo deviceInfo)
      throws IOException {
    Result result = sender.send(msg, deviceInfo.getDeviceRegistrationId(), 5);
    LOG.info("Sent ping to device of user: " + deviceInfo.getUserHandle());
    if (result.getMessageId() != null) {
      String canonicalRegId = result.getCanonicalRegistrationId();
      if (canonicalRegId != null) {
        endpoint.removeDeviceInfo(deviceInfo.getDeviceRegistrationId());
        deviceInfo.setDeviceRegistrationId(canonicalRegId);
        endpoint.insertDeviceInfo(deviceInfo);
      }
    } else {
      String error = result.getErrorCodeName();
      if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
        endpoint.removeDeviceInfo(deviceInfo.getDeviceRegistrationId());
      }
    }

    return result;
  }

  /**
   * Ping the registered device of the recipient of the invitation.
   *
   * @param message the message to be sent in the GCM ping
   * @param friendMessage contains the information needed for sending and processing the invite
   * @return {@code true} if delivery was successful; {@code false} otherwise
   * @throws IOException
   */
  public static boolean pingFriendInvite(String message, FriendMessage friendMessage)
      throws IOException {
    return pingFriendMessage(message, friendMessage, PING_REASON_FRIEND_INVITE);
  }

  /**
   * Ping the registered device of the recipient (the sender of the initial friend invite).
   *
   * @param message the message to be sent in the GCM ping
   * @param toHandle the user to send the acceptance ping (the sender of the initial friend invite)
   * @throws IOException
   */
  public static boolean pingFriendAccept(String message, FriendMessage friendMessage)
      throws IOException {
    return pingFriendMessage(message, friendMessage, PING_REASON_FRIEND_ACCEPT);
  }

  /**
   * Ping a message using the {@link FriendMessage}.
   */
  private static boolean pingFriendMessage(
      String message, FriendMessage friendMessage, String gcmPayloadPingReason) throws IOException {
    String toHandle = friendMessage.getTo();
    Message msg = new Message.Builder()
        .collapseKey(friendMessage.getFrom())
        .addData(GCM_PAYLOAD_PING_REASON, gcmPayloadPingReason)
        .addData(GCM_PAYLOAD_FROM_USER_HANDLE, friendMessage.getFrom())
        .addData(GCM_PAYLOAD_TO_USER_HANDLE, toHandle)
        .addData(GCM_PAYLOAD_MESSAGE, message)
        .build();
    if (verifyFields(msg)) {
      DeviceInfo deviceInfo = endpoint.getDeviceInfo(toHandle);
      if (deviceInfo != null) {
        LOG.info("Building friend invite message to send to user: " + toHandle
            + " from user: " + friendMessage.getFrom());
        Sender sender = new Sender(API_KEY);
        sendViaGcm(msg, sender, deviceInfo);
        return true;
      } else {
        LOG.warning("The device was not found in registry for user handle " + toHandle);
      }
    } else {
      LOG.warning("Empty fields in the GCM Message. No message sent.");
    }
    return false;
  }

  /**
   * Ping the registered devices of the recipients of the invitation.
   *
   * @param message the message to be sent in the GCM ping
   * @param gameMessage contains the information needed for sending and processing the invite
   * @return {@code true} if delivery to all users were successful; {@code false} otherwise
   * @throws IOException
   */
  public static boolean pingGameInvite(String message, GameMessage gameMessage)
      throws IOException {
    return pingGameMessage(message, gameMessage, PING_REASON_GAME_INVITE);
  }

  /**
   * Ping the registered devices of the recipients of the notification.
   *
   * @param gameMessage contains the information needed for sending and processing the invite
   * @return {@code true} if delivery to all users were successful; {@code false} otherwise
   * @throws IOException
   */
  public static boolean pingGameStarted(GameMessage gameMessage) throws IOException {
    return pingGameMessage("Game has started.", gameMessage, PING_REASON_GAME_STARTED);
  }

  /**
   * Ping the registered devices of the recipients of the notification.
   *
   * @param message the message to be sent in the GCM ping
   * @param gameMessage contains the information needed for sending and processing the invite
   * @return {@code true} if delivery to all users were successful; {@code false} otherwise
   * @throws IOException
   */
  public static boolean pingGameDestroyed(String message, GameMessage gameMessage)
      throws IOException {
    return pingGameMessage(message, gameMessage, PING_REASON_GAME_DESTROYED);
  }

  /**
   * Ping the registered devices of the recipients of the notification.
   *
   * @param gameMessage contains the information needed for sending and processing the invite
   * @return {@code true} if delivery to all users were successful; {@code false} otherwise
   * @throws IOException
   */
  public static boolean pingGamePlayerJoin(GameMessage gameMessage) throws IOException {
    return pingGameMessage("Joining game.", gameMessage, PING_REASON_PLAYER_JOINED);
  }

  /**
   * Ping the registered devices of the recipients of the notification.
   *
   * @param gameMessage contains the information needed for sending and processing the invite
   * @return {@code true} if delivery to all users were successful; {@code false} otherwise
   * @throws IOException
   */
  public static boolean pingGamePlayerLeave(GameMessage gameMessage) throws IOException {
    return pingGameMessage("Leaving game.", gameMessage, PING_REASON_PLAYER_LEFT);
  }

  /**
   * Ping the registered devices of the recipients of the notification.
   *
   * @param gameMessage contains the information needed for sending and processing the invite
   * @return {@code true} if delivery to all users were successful; {@code false} otherwise
   * @throws IOException
   */
  public static boolean pingGameEnded(GameMessage gameMessage) throws IOException {
    return pingGameMessage("Game has ended.", gameMessage, PING_REASON_GAME_ENDED);
  }

  /**
   * Ping the registered devices of the recipients of the notification.
   *
   * @param gameMessage contains the information needed for sending and processing the invite
   * @param gems the number of gems picked up in this game
   * @param mobsKilled the number of mobiles killed in this game
   * @param deaths the number of deaths of player in this game
   * @return {@code true} if delivery to all users were successful; {@code false} otherwise
   * @throws IOException
   */
  public static boolean pingGamePlayerSendEndScore(
      GameMessage gameMessage, long gems, long mobsKilled, long deaths) throws IOException {
    boolean success = false;
    for (String toHandle : gameMessage.getTo()) {
      Message msg = new Message.Builder()
          .collapseKey(gameMessage.getFrom())
          .addData(GCM_PAYLOAD_PING_REASON, PING_REASON_PLAYER_END_SCORE)
          .addData(GCM_PAYLOAD_FROM_USER_HANDLE, gameMessage.getFrom())
          .addData(GCM_PAYLOAD_TO_USER_HANDLE, toHandle)
          .addData(GCM_PAYLOAD_GAME_ID, gameMessage.getGameId())
          .addData(GCM_PAYLOAD_MESSAGE, "Here are the endgame scores for this player.")
          .addData("gems", Long.toString(gems))
          .addData("mobs_killed", Long.toString(mobsKilled))
          .addData("deaths", Long.toString(deaths))
          .build();
      if (verifyFields(msg)) {
        DeviceInfo deviceInfo = endpoint.getDeviceInfo(toHandle);
        if (deviceInfo != null) {
          LOG.info("Building game message to send to user: " + toHandle
              + " from user: " + gameMessage.getFrom());
          Sender sender = new Sender(API_KEY);
          sendViaGcm(msg, sender, deviceInfo);
          success = true;
        } else {
          LOG.warning("The device was not found in registry for user handle " + toHandle);
        }
      } else {
        LOG.warning("Empty fields in the GCM Message. No invites sent.");
      }
    }
    return success;
  }

  /**
   * Ping a message using the {@link GameMessage}.
   */
  public static boolean pingGameMessage(
      String message, GameMessage gameMessage, String gcmPayloadPingReason) throws IOException {
    boolean success = false;
    for (String toHandle : gameMessage.getTo()) {
      Message msg = new Message.Builder()
          .collapseKey(gameMessage.getFrom())
          .addData(GCM_PAYLOAD_PING_REASON, gcmPayloadPingReason)
          .addData(GCM_PAYLOAD_FROM_USER_HANDLE, gameMessage.getFrom())
          .addData(GCM_PAYLOAD_TO_USER_HANDLE, toHandle)
          .addData(GCM_PAYLOAD_GAME_ID, gameMessage.getGameId())
          .addData(GCM_PAYLOAD_MESSAGE, message)
          .build();
      if (verifyFields(msg)) {
        DeviceInfo deviceInfo = endpoint.getDeviceInfo(toHandle);
        if (deviceInfo != null) {
          LOG.info("Building game message to send to user: " + toHandle
              + " from user: " + gameMessage.getFrom());
          Sender sender = new Sender(API_KEY);
          sendViaGcm(msg, sender, deviceInfo);
          success = true;
        } else {
          LOG.warning("The device was not found in registry for user handle " + toHandle);
        }
      } else {
        LOG.warning("Empty fields in the GCM Message. No invites sent.");
      }
    }
    return success;
  }

  /**
   * Verifies that the fields in a {@link Message} are non-null.
   * <p>
   * See https://code.google.com/p/gcm/issues/detail?id=5
   *
   * @param msg the invite to verify
   * @return {@code true} if all fields are verified, {@code false} if otherwise
   */
  private static boolean verifyFields(Message msg) {
    boolean verified = true;
    for (Entry<String, String> entry : msg.getData().entrySet()) {
      verified = entry.getValue() != null;
    }
    return verified;
  }
}
