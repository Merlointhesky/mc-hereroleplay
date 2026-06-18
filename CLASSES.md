# HereRolePlay Classes and Abilities

This document lists all active roleplay classes, their active spells/abilities, and passive traits, including current attribute requirements and leveling progression mechanics.

---

## 1. Warrior
*   **Unlock Requirements**: Strength: `20`, Vitality: `20` (Total Points: `40`)
*   **Active Ability 1**: **Cleave**
    *   *Trigger*: Sword in hand, press `[F]`
    *   *Mana Cost*: `30`
    *   *Description*: Sweep attack dealing AoE damage.
    *   *Scaling*: Deals `10.0 + (level - 1) * 2.0` damage in a `3.0 + (level - 1) * 0.2` block radius.
*   **Active Ability 2**: **Boomerang Throw**
    *   *Trigger*: Axe in hand, press `[F]`
    *   *Mana Cost*: `20`
    *   *Description*: Throws your axe like a boomerang, hitting enemies in its path.
    *   *Scaling*: Deals `8.0 + level * 2.0` damage.
*   **Active Ability 3**: **Thunder Wave**
    *   *Trigger*: Mace in hand, press `[F]`
    *   *Mana Cost*: `25`
    *   *Description*: Calls lightning on yourself, pushing back and damaging nearby enemies.
    *   *Scaling*: Deals `10.0 + level * 2.0` damage in a `5.0 + level * 0.5` block radius, pushing back with `1.5 + level * 0.15` force.
*   **Active Ability 4**: **Spear Knight**
    *   *Trigger*: Spear in hand, press `[F]` (While mounted)
    *   *Mana Cost*: `30`
    *   *Description*: Charges forward on your horse, trampling and damaging enemies.
    *   *Scaling*: Deals `10.0 + level * 2.0` damage to trampled enemies.
*   **Active Ability 5**: **Assassination**
    *   *Trigger*: Sword in hand, Sneak + `[F]`
    *   *Mana Cost*: `30`
    *   *Description*: Teleports behind the closest mob and performs a critical strike.
    *   *Scaling*: Teleports within `8.0 + (level - 1) * 0.2` range; final damage is based on weapon damage + 5.0 base damage, multiplied by the player's Critical Damage skill level (`1.5 + critDamageLvl * 0.01`).
*   **Active Ability 6**: **Laser DOT**
    *   *Trigger*: Trident in hand, press `[F]`
    *   *Mana Cost*: `20`
    *   *Description*: Channels a laser beam dealing damage over time.
    *   *Scaling*: Deals `2.0 + level * 0.5` damage per hit (up to 12 hits).
*   **Passive buff**: **Heavy Strike**
    *   *Description*: Melee damage boost.
    *   *Scaling*: `+1%` melee damage per level.
*   **Passive buff**: **Swift Strike**
    *   *Description*: Melee speed boost. Decrease cooldown for max damage between swings of melee weapons.
    *   *Scaling*: `+1%` melee speed per level. 

## 2. Ranger
*   **Unlock Requirements**: Agility: `20`, Vitality: `20` (Total Points: `40`)
*   **Active Ability 1**: **Quick Shot**
    *   *Trigger*: Bow in hand, press `[F]`
    *   *Mana Cost*: `20`
    *   *Description*: Fires a rapid sequence of arrows.
    *   *Scaling*: Fires `3 + (level - 1)` arrows.
*   **Active Ability 2**: **Piercing Bolt**
    *   *Trigger*: Crossbow in hand, press `[F]`
    *   *Mana Cost*: `25`
    *   *Description*: Fires a high-velocity piercing bolt in a straight line.
    *   *Scaling*: Fires a piercing arrow with pierce level 5 and velocity 2.2.
*   **Passive buff**: **Precision**
    *   *Description*: Chance to land a critical strike.
    *   *Scaling*: `+1%` critical strike chance per level on arrow hits.
*   **Passive buff**: **Critical Damage**
    *   *Description*: Critical Damage boost.
    *   *Scaling*: `+1%` critical strike damage per level on critical hits.
*   **Passive buff**: **Recycle Bolt**
    *   *Description*: Chance to not consume arrows when firing crossbows.
    *   *Scaling*: `+1%` chance per level.


## 3. Wizard
*   **Unlock Requirements**: Vitality: `20`, Intelligence: `20` (Total Points: `40`)
*   **Active Ability 1**: **Rock Blast**
    *   *Trigger*: Stick in hand, press `[F]`
    *   *Mana Cost*: `15`
    *   *Description*: Shoots a high-impact rock projectile.
    *   *Scaling*: Deals `8.0 + (level - 1) * 2.5` damage and breaks block columns into falling blocks in a `1.0 + level * 0.01` block radius.
*   **Active Ability 2**: **Quicksand**
    *   *Trigger*: Stick in hand, Sneak + `[F]`
    *   *Mana Cost*: `25`
    *   *Description*: Transforms blocks into sand and slows targets in the area.
    *   *Scaling*: Slows targets in a radius of `1 + level / 5` blocks.
*   **Active Ability 3**: **Fireball**
    *   *Trigger*: Blaze Rod in hand, press `[F]`
    *   *Mana Cost*: `25`
    *   *Description*: Shoots an explosive fireball that damages and ignites entities.
    *   *Scaling*: Deals `12.0 + (level - 1) * 3.0` damage and ignites targets for `2.0 + (level - 1) * 0.5` seconds within a 3.0 block radius.
*   **Active Ability 4**: **Fire Rain**
    *   *Trigger*: Blaze Rod in hand, Sneak + `[F]`
    *   *Mana Cost*: `30`
    *   *Description*: Channels falling fire around the player, dealing splash damage.
    *   *Scaling*: Channels for 3.0 seconds, dropping fireballs that deal `4.0 + level * 0.5` damage in a 6.0 block radius.
*   **Active Ability 5**: **Water Cannon**
    *   *Trigger*: Tropical Fish in hand, press `[F]`
    *   *Mana Cost*: `20`
    *   *Description*: Projects water forward, pushing targets and preventing jumping.
    *   *Scaling*: Pushes back with `1.0 + level * 0.1` force.
*   **Active Ability 6**: **Water Wave**
    *   *Trigger*: Tropical Fish in hand, Sneak + `[F]`
    *   *Mana Cost*: `30`
    *   *Description*: Creates a water barrier, pushing back and damaging enemies.
    *   *Scaling*: Deals `5.0 + level * 1.5` damage and knocks back enemies within `4.0 + level * 0.5` blocks with `1.0 + level * 0.15` force, creating a temporary water ring.
*   **Active Ability 7**: **Wind Blast**
    *   *Trigger*: Breeze Rod in hand, press `[F]`
    *   *Mana Cost*: `20`
    *   *Description*: Launches an explosive wind charge projectile.
    *   *Scaling*: Deals `8.0 + (level - 1) * 2.0` damage within a 4.0 block radius.
*   **Active Ability 8**: **Gale Force**
    *   *Trigger*: Breeze Rod in hand, Sneak + `[F]`
    *   *Mana Cost*: `25`
    *   *Description*: Launches the player upward and knocks back surrounding mobs.
    *   *Scaling*: Launches player upward with `1.0 + level * 0.05` velocity, and knocks back enemies in a 5.0 block radius.
*   **Passive buff**: **Spell Echo**
    *   *Description*: Mana regeneration boost.
    *   *Scaling*: `+1%` mana regeneration rate per level.

## 4. Miner
*   **Unlock Requirements**: Strength: `20`, Agility: `20` (Total Points: `40`)
*   **Active Ability 1**: **Timber**
    *   *Trigger*: Axe in hand, Sneak + `[F]` on log
    *   *Mana Cost*: `20`
    *   *Description*: Instantly breaks columns of logs.
    *   *Scaling*: Breaks a maximum of `10 + (level - 1) * 3` blocks.
*   **Active Ability 2**: **Diggy Diggy Hole**
    *   *Trigger*: Shovel in hand, Sneak + `[F]` on dirt/gravel/sand
    *   *Mana Cost*: `20`
    *   *Description*: Instantly breaks blocks in a circle around the broken block.
    *   *Scaling*: Breaks a maximum of `10 + (level - 1) * 3` blocks in a radius around the player. If not possible on the same level, starts from the next available block nearby the starting broken block in a spherical manner.
*   **Active Ability 3**: **Tunnel Vision**
    *   *Trigger*: Pickaxe in hand, Sneak + `[F]` on mine-able block
    *   *Mana Cost*: `20`
    *   *Description*: Instantly breaks blocks in a 3x3 square forward.
    *   *Scaling*: Breaks a maximum of `10 + (level - 1) * 3` blocks in a column in front of the player creating a tunnel. 
*   **Passive buff**: **Dense Armor**
    *   *Description*: Direct damage reduction.
    *   *Scaling*: `+1%` damage reduction per level.

## 5. Farmer
*   **Unlock Requirements**: Agility: `20`, Intelligence: `20` (Total Points: `40`)
*   **Active Ability**: **Rejuvenation**
    *   *Trigger*: Hoe in hand, Sneak + `[F]`
    *   *Mana Cost*: `25`
    *   *Description*: If near a crop (at least 3 blocks with active seeded crops within range), applies bonemeal x3 to all crops within range, otherwise AoE heals self and nearby allies.
    *   *Scaling*: Heals `6.0 + (level - 1) * 2.0` in a `4.0 + (level - 1) * 0.5` block radius.
*   **Passive buff 1**: **Bountiful Harvest**
    *   *Description*: Chance for double crops on harvest.
    *   *Scaling*: `+1%` double crop chance per level on block break.
*   **Passive buff 2**: **Fertilizer**
    *   *Description*: Chance for crop block growth speed-up.
    *   *Scaling*: `+1%` crop tick speed chance per level.

## 6. Engineer
*   **Unlock Requirements**: Strength: `20`, Intelligence: `20` (Total Points: `40`)
*   **Passive buff 1**: **Efficiency**
    *   *Description*: Periodically applies a Haste effect on the player.
    *   *Scaling*: Haste amplifier is `(level - 1) / 10` (Haste I at level 1, Haste II at level 11, etc.).
*   **Passive buff 2**: **Hacker**
    *   *Description*: Applies additional Durability on tools used (reduces durability loss).
    *   *Scaling*: `+1%` per level.
*   **Passive buff 3**: **Repair**
    *   *Description*: Increases mending and repair amounts.
    *   *Scaling*: `+1%` per level.
*   **Passive buff 4**: **Power Surge**
    *   *Description*: Increases speed on movement with vehicles, mounts, rails, and boats.
    *   *Scaling*: `+1%` per level.

---

## Hero Classes

Hero classes require specialized, higher stat distributions to unlock.

## 7. Paladin
*   **Unlock Requirements**: Strength: `60`, Vitality: `60`, Intelligence: `60` (Total Points: `200`)
*   **Active Ability 1**: **Aegis**
    *   *Trigger*: Shield in hand, Sneak + `[F]` (or Block + Shift+F if off-hand)
    *   *Mana Cost*: `40`
    *   *Description*: Invulnerability (Resistance X) for a duration.
    *   *Scaling*: Invulnerability lasts `10.0s + (level - 1) * 2.5s`.
*   **Active Ability 2**: **Holy Nova**
    *   *Trigger*: Shield in hand, press `[F]` (or Block + F if off-hand)
    *   *Mana Cost*: `35`
    *   *Description*: AoE healing for allies and damage to monsters.
    *   *Scaling*: Heals/deals `8.0 + (level - 1) * 2.0` in a `4.0 + (level - 1) * 0.5` block radius.
*   **Passive buff 1**: **Guardian**
    *   *Description*: Max health boost.
    *   *Scaling*: `+1 heart` max health per level.
*   **Passive buff 2**: **Iron Resolve**
    *   *Description*: Increases knockback resistance.
    *   *Scaling*: `+1%` knockback resistance per level.

## 8. Landlord
*   **Unlock Requirements**: Strength: `60`, Agility: `60`, Vitality: `60` (Total Points: `200`)
*   **Active Ability**: **Transmutation**
    *   *Trigger*: Block in hand, Sneak + `[F]` on target block
    *   *Mana Cost*: `30`
    *   *Description*: Converts target block and nearby connected blocks of the same type into the held block.
    *   *Scaling*: Radius increases by `1 + (1 block per level)`. 
*   **Passive buff**: **Domain Lord**
    *   *Description*: Reduces fall damage.
    *   *Scaling*: `+1%` fall damage reduction per level (100% reduction at level 100).

## 9. Alchemist
*   **Unlock Requirements**: Agility: `60`, Vitality: `60`, Intelligence: `60` (Total Points: `200`)
*   **Passive buff 1**: **Catalyst**
    *   *Description*: Increases applied potion durations.
    *   *Scaling*: `+1%` potion duration per level.
*   **Passive buff 2**: **Master of the Craft**
    *   *Description*: Doubles the amount of enchants and potions created when taken from enchanting table or brewing stand.

## 10. Necromancer
*   **Unlock Requirements**: Agility: `60`, Vitality: `60`, Intelligence: `60` (Total Points: `200`)
*   **Active Ability 1**: **Raise Undead**
    *   *Trigger*: Bone in hand, Sneak + `[F]`
    *   *Mana Cost*: `35`
    *   *Description*: Summons friendly skeleton cohorts that follow the player and attack hostile mobs.
    *   *Scaling*: Summons `1 + level / 10` skeletons.
*   **Active Ability 2**: **Soul Drain**
    *   *Trigger*: Bone in hand, press `[F]`
    *   *Mana Cost*: `30`
    *   *Description*: Channels health from your active skeletons or nearby undead enemies to heal yourself.
    *   *Scaling*: Drains `(2.0 + level * 0.5) / 2.0` health per tick (6 ticks).
*   **Passive buff**: **Deathly Rejuvenation**
    *   *Description*: Direct health regeneration.
    *   *Scaling*: `+0.01 HP/s` (`+0.02 HP` per 2 seconds) health regeneration per level.

---

## Mastery / Admin Classes

## 11. Admin Class
*   **Unlock Requirements**: Strength: `100`, Agility: `100`, Vitality: `100`, Intelligence: `100` (Total Points: `400`)
*   **Description**: Unlocked by mastering everything. Grants access to all class spells.
