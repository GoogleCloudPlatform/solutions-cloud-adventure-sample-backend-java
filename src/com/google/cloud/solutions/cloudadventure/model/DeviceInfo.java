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

/**
 * A class which keeps track of the Google Cloud Messaging registration info for a user.
 */
public class DeviceInfo {

  private String userHandle;
  private String deviceRegistrationIds;

  public String getUserHandle() {
    return userHandle;
  }

  public void setUserHandle(String userHandle) {
    this.userHandle = userHandle;
  }

  public String getDeviceRegistrationId() {
    return deviceRegistrationIds;
  }

  public void setDeviceRegistrationId(String deviceRegistrationIds) {
    this.deviceRegistrationIds = deviceRegistrationIds;
  }
}
