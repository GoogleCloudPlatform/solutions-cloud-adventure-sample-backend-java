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

import java.util.List;

/**
 * Message class used to carry a simple string message from one user to a number of recipients.
 */
public class GameMessage {

  private String from;
  private List<String> to;
  private String gameId;

  public String getFrom() {
    return from;
  }

  public GameMessage setFrom(String from) {
    this.from = from;
    return this;
  }

  public List<String> getTo() {
    return to;
  }

  public GameMessage setTo(List<String> to) {
    this.to = to;
    return this;
  }

  public String getGameId() {
    return gameId;
  }

  public GameMessage setGameId(String gameId) {
    this.gameId = gameId;
    return this;
  }
}
