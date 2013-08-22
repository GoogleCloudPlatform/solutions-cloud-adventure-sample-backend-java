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
 * Types of Creatures and their stats.
 */
public class CreatureTypes {

  public static class Unicorn extends Creature {
    private static final long serialVersionUID = 1L;

    public Unicorn() {
      super("shining unicorn", WorldGenerator.getCreatureEffectBasedAdjective(15), 0, 15);
    }
  }

  public static class Faerie extends Creature {
    private static final long serialVersionUID = 1L;

    public Faerie() {
      super("faerie", WorldGenerator.getCreatureEffectBasedAdjective(8), 0, 8);
    }
  }

  public static class Sprite extends Creature {
    private static final long serialVersionUID = 1L;

    public Sprite() {
      super("sprite", WorldGenerator.getCreatureEffectBasedAdjective(6), 0, 6);
    }
  }

  public static class Spider extends Creature {
    private static final long serialVersionUID = 1L;

    public Spider() {
      super("overly large spider", WorldGenerator.getCreatureEffectBasedAdjective(-3), 8, -3);
    }
  }

  public static class Newt extends Creature {
    private static final long serialVersionUID = 1L;

    public Newt() {
      super("giant newt", WorldGenerator.getCreatureEffectBasedAdjective(-4), 10, -4);
    }
  }

  public static class Boar extends Creature {
    private static final long serialVersionUID = 1L;

    public Boar() {
      super("wild boar", WorldGenerator.getCreatureEffectBasedAdjective(-6), 12, -6);
    }
  }

  public static class Troll extends Creature {
    private static final long serialVersionUID = 1L;

    public Troll() {
      super("stupid troll", WorldGenerator.getCreatureEffectBasedAdjective(-7), 15, -7);
    }
  }

  public static class Giant extends Creature {
    private static final long serialVersionUID = 1L;

    public Giant() {
      super("angry giant", WorldGenerator.getCreatureEffectBasedAdjective(-8), 15, -8);
    }
  }

  public static class Hippogriff extends Creature {
    private static final long serialVersionUID = 1L;

    public Hippogriff() {
      super("rearing hippogriff", WorldGenerator.getCreatureEffectBasedAdjective(-10), 18, -10);
    }
  }

  public static class Dragon extends Creature {
    private static final long serialVersionUID = 1L;

    public Dragon() {
      super("fire-breathing dragon", WorldGenerator.getCreatureEffectBasedAdjective(-10), 20, -10);
    }
  }
}
