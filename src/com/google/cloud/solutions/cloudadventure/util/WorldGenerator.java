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

package com.google.cloud.solutions.cloudadventure.util;

import java.util.Random;

/**
 * This class helps generate certain parts of the player world: the {@link Maze}. It also provides
 * some mappings to numbers used in the game, for example: Creature damage to adjective.
 */
public class WorldGenerator {

  private static Random random = new Random();

  /**
   * Gets a random int from 0 (inclusive) to paramater exclusive (exclusive).
   * <p>
   * getRandom(10) might return 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 with equal probability each.
   * 
   * @param exclusive
   * @return the random integer
   */
  public static int getRandom(int exclusive) {
    return random.nextInt(exclusive);
  }

  /**
   * These are location "types". When a random String here is combined with a random String from
   * LOCATION_NAMES, they together form a location description. The two arrays need not be the same
   * length.
   */
  private static final String[] LOCATION_TYPES = { "Room", "Room", "Room", "Room", "Cave", "Cave",
      "Cave", "Beach", "Forest", "Chamber", "Chamber", "Chamber", "Canyon", "Falls", "River",
      "Lake", "Trail", "Lagoon", "Cliffs", "Cliffs", "Clearing", "Field", "Meadow" };

  /**
   * These are location "names". When a random String here is combined with a random String from
   * LOCATION_TYPES, they together form a location description. The two arrays need not be the same
   * length.
   */
  private static final String[] LOCATION_NAMES = { "Crystal", "Snarfle", "Quixote", "Stormrider",
      "Redhorn", "Doom", "Red Dawn", "Blue Twilight", "The Viennese Waltz", "Sorcery",
      "Curious Happenings", "Golden Hawk", "Alcyone", "Prometheus", "Blinding Light", "Echo",
      "Fountain of Youth", "Wonder", "Star Crossings", "The God of War", "Mythology", "Jupiter" };

  /**
   * An array of adjectives that may be used for Creature effects, in order from least damage to
   * most damage.
   */
  public static final String[] DMG_CRTR_DESCRIPTION = { "ineffectual", "inept", "unimpressive",
      "average", "strong", "fierce", "vicious", "ferocious", "monstrous", "savage", "brutal",
      "legendary" };

  /**
   * An array of adjectives that may be used for Creature effects, in order from least healing to
   * most healing.
   */
  private static final String[] HEAL_CRTR_DESCRIPTION = { "kind", "kind", "welcoming", "welcoming",
      "beautific", "beautific", "enchanting", "enchanting", "runic", "runic", "legendary",
      "legendary" };

  /**
   * An array of adjectives that may be used for Pickup effects, in order from least damage to most
   * damage.
   */
  private static final String[] DMG_PICKUP_DESCRIPTION = { "small", "small", "decent", "decent",
      "decent", "decent", "hefty", "hefty", "enormous", "enormous", "legendary", "legendary" };

  /**
   * @return a randomly-generated name for a map location
   */
  public static String generateLocationDescription() {
    String type = LOCATION_TYPES[getRandom(LOCATION_TYPES.length)];
    String name = LOCATION_NAMES[getRandom(LOCATION_NAMES.length)];
    if (random.nextBoolean()) {
      return type + " of " + name;
    } else {
      return name + " " + type;
    }
  }

  /**
   * Gets an appropriate adjective based on the effect the creature will have on the player when
   * interacted with. The effect may be beneficial (positive) or not (negative).
   * 
   * @param effect
   * @return a String adjective
   */
  public static String getCreatureEffectBasedAdjective(int effect) {
    if (effect < 0) {
      return DMG_CRTR_DESCRIPTION[Math.min(DMG_CRTR_DESCRIPTION.length - 1, Math.abs(effect) - 1)];
    } else if (effect > 0) {
      return HEAL_CRTR_DESCRIPTION[Math.min(HEAL_CRTR_DESCRIPTION.length - 1, effect - 1)];
    } else {
      return "non-descript";
    }
  }

  /**
   * Gets an appropriate adjective based on the effect the pickup will have on the player when
   * interacted with, or against another object.
   * 
   * @param effect
   * @return a String adjective
   */
  public static String getPickupDmgBasedAdjective(int effect) {
    if (effect < 0) {
      return DMG_PICKUP_DESCRIPTION[Math.min(DMG_PICKUP_DESCRIPTION.length - 1,
          Math.abs(effect) - 1)];
    } else {
      return "";
    }
  }
}
