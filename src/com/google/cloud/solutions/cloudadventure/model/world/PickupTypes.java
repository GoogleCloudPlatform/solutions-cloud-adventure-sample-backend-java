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

import com.google.cloud.solutions.cloudadventure.util.WorldGenerator;

/**
 * Types of Pickups and their stats.
 */
public class PickupTypes {
  public static class Gem extends Pickup {
    private static final long serialVersionUID = 1L;

    public Gem() {
      super("gem", "", 0, 0);
    }
  }

  public static class GreaterPot extends Pickup {
    private static final long serialVersionUID = 1L;

    public GreaterPot() {
      super("greater health potion", WorldGenerator.getPickupDmgBasedAdjective(10), 1, 10);
    }
  }

  public static class Amulet extends Pickup {
    private static final long serialVersionUID = 1L;

    public Amulet() {
      super("healing amulet", WorldGenerator.getPickupDmgBasedAdjective(8), 2, 8);
    }
  }

  public static class LesserPot extends Pickup {
    private static final long serialVersionUID = 1L;

    public LesserPot() {
      super("lesser health potion", WorldGenerator.getPickupDmgBasedAdjective(5), 1, 5);
    }
  }

  public static class Rock extends Pickup {
    private static final long serialVersionUID = 1L;

    public Rock() {
      super("sharp rock", WorldGenerator.getPickupDmgBasedAdjective(-1), 1, -1);
    }
  }

  public static class Knife extends Pickup {
    private static final long serialVersionUID = 1L;

    public Knife() {
      super("knife", WorldGenerator.getPickupDmgBasedAdjective(-3), 5, -3);
    }
  }

  public static class Dagger extends Pickup {
    private static final long serialVersionUID = 1L;

    public Dagger() {
      super("short dagger", WorldGenerator.getPickupDmgBasedAdjective(-3), 5, -3);
    }
  }

  public static class Scimitar extends Pickup {
    private static final long serialVersionUID = 1L;

    public Scimitar() {
      super("curved scimitar", WorldGenerator.getPickupDmgBasedAdjective(-5), 5, -5);
    }
  }

  public static class Crossbow extends Pickup {
    private static final long serialVersionUID = 1L;

    public Crossbow() {
      super("crossbow", WorldGenerator.getPickupDmgBasedAdjective(-7), 3, -7);
    }
  }

  public static class Broadsword extends Pickup {
    private static final long serialVersionUID = 1L;

    public Broadsword() {
      super("broadsword", WorldGenerator.getPickupDmgBasedAdjective(-9), 3, -9);
    }
  }

  public static class MagicWand extends Pickup {
    private static final long serialVersionUID = 1L;

    public MagicWand() {
      super("magic wand", WorldGenerator.getPickupDmgBasedAdjective(-10), 4, -10);
    }
  }

  public static class Battleaxe extends Pickup {
    private static final long serialVersionUID = 1L;

    public Battleaxe() {
      super("battle axe", WorldGenerator.getPickupDmgBasedAdjective(-11), 5, -11);
    }
  }

  public static class ElvishSword extends Pickup {
    private static final long serialVersionUID = 1L;

    public ElvishSword() {
      super("Elvish sword", WorldGenerator.getPickupDmgBasedAdjective(-12), 5, -12);
    }
  }

}
