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
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.google.cloud.solutions.cloudadventure.model.Handle;
import com.google.cloud.solutions.cloudadventure.util.StorageUtils;

import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An Endpoint class which exposes methods to manipulate primarily the Handle resource.
 */
@Api(name = "cloudadventure")
public class HandleEndpoint {

  private static final Logger LOG = Logger.getLogger(HandleEndpoint.class.getName());

  /**
   * Checks for user handle uniqueness within a transaction.
   * 
   * @param handle
   * @return {@link Handle} instance if a new unique handle has been successfully created
   *         {@value "!"} if the handle was not unique
   *         {@code null} if an exception has occurred
   */
  @ApiMethod(path = "handles/claim/{handle}", name = "handles.claim", httpMethod = HttpMethod.POST)
  public Handle claimHandle(@Named("handle") final String handle) {
    Handle handleObject = new Handle();
    Key key = StorageUtils.getHandleDatastoreKey(handle);
    Entity entity = new Entity(key);
    Transaction tx = StorageUtils.getDatastore().beginTransaction();
    try {
      // If handle already exists, set return Handle with special character.
      StorageUtils.getDatastore().get(key);
      handleObject.setHandle("!");
      tx.commit();
    } catch (EntityNotFoundException e) {
      // If handle is unique, set return handle with handle name. Puts new Handle in Datastore.
      StorageUtils.getDatastore().put(entity);
      handleObject.setHandle(handle);
      tx.commit();
    } catch (Exception e) {
      // If some other exception occurs, return a null.
      LOG.warning(e.getMessage());
      return null;
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
    }
    return handleObject;
  }
}
