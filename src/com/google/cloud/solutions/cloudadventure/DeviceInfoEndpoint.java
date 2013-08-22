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

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.cloud.solutions.cloudadventure.model.DeviceInfo;
import com.google.cloud.solutions.cloudadventure.util.StorageUtils;

import javax.inject.Named;

/**
 * An Endpoint class which exposes methods to manipulate primarily the DeviceInfo model resource.
 */
@Api(name = "cloudadventure")
public class DeviceInfoEndpoint {

  @ApiMethod(path = "devices", name = "devices.get")
  public DeviceInfo getDeviceInfo(@Named("userHandle") String userHandle) {
    Key key = StorageUtils.getDeviceDatastoreKey(userHandle);
    Entity entity;
    try {
      entity = StorageUtils.getDatastore().get(key);
    } catch (EntityNotFoundException e) {
      return null;
    }
    DeviceInfo device = new DeviceInfo();
    device.setUserHandle(entity.getKey().getName());
    device.setDeviceRegistrationId((String) entity.getProperty("reg_id"));
    return device;
  }

  @ApiMethod(path = "devices", name = "devices.insert")
  public DeviceInfo insertDeviceInfo(DeviceInfo deviceInfo) {
    Key key = StorageUtils.getDeviceDatastoreKey(deviceInfo.getUserHandle());
    Entity entity = new Entity(key);
    entity.setProperty("reg_id", deviceInfo.getDeviceRegistrationId());
    StorageUtils.getDatastore().put(entity);
    return deviceInfo;
  }

  @ApiMethod(path = "devices", name = "devices.update")
  public DeviceInfo updateDeviceInfo(DeviceInfo deviceInfo) {
    Key key = StorageUtils.getDeviceDatastoreKey(deviceInfo.getUserHandle());
    Entity entity = new Entity(key);
    entity.setProperty("reg_id", deviceInfo.getDeviceRegistrationId());
    StorageUtils.getDatastore().put(entity);
    return deviceInfo;
  }

  @ApiMethod(path = "devices", name = "devices.remove")
  public void removeDeviceInfo(@Named("userHandle") String userHandle) {
    Key key = StorageUtils.getDeviceDatastoreKey(userHandle);
    StorageUtils.getDatastore().delete(key);
  }
}
