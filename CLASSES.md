# HereRolePlay Classes and Abilities

This document lists all active roleplay classes, their active spells/abilities, and passive traits, including current attribute requirements and leveling progression mechanics.

---

## 1. Warrior
*   **Unlock Requirements**: Strength: `20`, Vitality: `20` (Total Points: `40`)
*   **Active Ability**: **Cleave**
    *   *Trigger*: Sword in hand, press `[F]`
    *   *Mana Cost*: `30`
    *   *Description*: Sweep attack dealing AoE damage.
    *   *Scaling*: Deals `10.0 + (level - 1) * 2.0` damage in a `3.0 + (level - 1) * 0.2` block radius.
*   **Passive buff**: **Heavy Strike**
    *   *Description*: Melee damage boost.
    *   *Scaling*: `+1%` melee damage per level.
*   **Passive buff**: **Swift Strike**
    *   *Description*: Melee speed boost. Decrease cooldown for max damage between swings of melee weapons.
    *   *Scaling*: `+1%` melee speed per level. 

## 2. Ranger
*   **Unlock Requirements**: Agility: `20`, Vitality: `20` (Total Points: `40`)
*   **Active Ability**: **Quick Shot**
    *   *Trigger*: Bow in hand, press `[F]`
    *   *Mana Cost*: `20`
    *   *Description*: Fires a rapid sequence of arrows.
    *   *Scaling*: Fires `3 + (level - 1)` arrows.
*   **Passive buff**: **Precision**
    *   *Description*: Chance to land a critical strike.
    *   *Scaling*: `+1%` critical strike chance per level on arrow hits.
*   **Passive buff**: **Critical Damage**
    *   *Description*: Critical Damage boost.
    *   *Scaling*: `+1%` critical strike damage per level on critical hits.


## 3. Wizard
*   **Unlock Requirements**: Vitality: `20`, Intelligence: `20` (Total Points: `40`)
*   **Active Ability 1**: **Arcane Missile**
    *   *Trigger*: Stick in hand, press `[F]`
    *   *Mana Cost*: `15`
    *   *Description*: Shoots a magical missile dealing damage on impact.
    *   *Scaling*: Deals `8.0 + (level - 1) * 2.5` damage.
*   **Active Ability 2**: **Fireball**
    *   *Trigger*: Blaze Rod in hand, press `[F]`
    *   *Mana Cost*: `25`
    *   *Description*: Shoots an explosive fireball that damages and ignites entities.
    *   *Scaling*: Deals `12.0 + (level - 1) * 3.0` damage in a 3-block radius; ignites for `2.0s + (level - 1) * 0.5s`.
*   **Active Ability 3**: **Chain Lightning**
    *   *Trigger*: Blaze Rod in hand, Sneak + `[F]`
    *   *Mana Cost*: `20`
    *   *Description*: Lightning jumps from target to target, dealing damage.
    *   *Scaling*: Deals `10.0 + (level - 1) * 2.0` damage, jumps up to `1 + level / 10` times.
*   **Active Ability 4**: **Water Wave**
    *   *Trigger*: Stick in hand, Sneak + `[F]`
    *   *Mana Cost*: `30`
    *   *Description*: Creates a water barrier, pushing back and damaging enemies.
    *   *Scaling*: Deals `5.0 + level * 1.5` damage, pushes back with `1.0 + level * 0.15` force.
*   **Passive buff**: **Spell Echo**
    *   *Description*: Mana regeneration boost.
    *   *Scaling*: `+1%` mana regeneration rate per level.

## 4. Miner
*   **Unlock Requirements**: Strength: `20`, Agility: `20` (Total Points: `40`)
*   **Active Ability**: **Timber**
    *   *Trigger*: Axe in hand, Sneak + Right Click log
    *   *Mana Cost*: `20`
    *   *Description*: Instantly breaks columns of logs.
    *   *Scaling*: Breaks a maximum of `10 + (level - 1) * 3` blocks.
    **Active Ability**: **Diggy Diggy Hole**
    *   *Trigger*: Shovel in hand, Sneak + Right Click Dirt/Gravel/Sand
    *   *Mana Cost*: `20`
    *   *Description*: Instantly breaks blocks in a circle around the block you are breaking.
    *   *Scaling*: Breaks a maximum of `10 + (level - 1) * 3` blocks in a radius around the player. If not possible on the same level, start from the next available block nearby the starting broken block in a spherical manner.
    **Active Ability**: **Tunnel Vision**
    *   *Trigger*: Pickaxe in hand, Sneak + Right Click any mine-able blocks (stone, cobble, deepslate, etc.)
    *   *Mana Cost*: `20`
    *   *Description*: Instantly breaks blocks in a 3x3 square around the block you are breaking..
    *   *Scaling*: Breaks a maximum of `10 + (level - 1) * 3` blocks in a column in front of the player creating a tunnel. 
*   **Passive buff**: **Dense Armor**
    *   *Description*: Direct damage reduction.
    *   *Scaling*: `+1%` damage reduction per level.

## 5. Farmer
*   **Unlock Requirements**: Agility: `20`, Intelligence: `20` (Total Points: `40`)
*   **Active Ability**: **Rejuvenation**
    *   *Trigger*: Hoe in hand, Sneak + Right Click
    *   *Mana Cost*: `25`
    *   *Description*: IF near a crop (at least 3 blocks with active seeded crops within range), AOE apply bonemeal x3 to all crops within range, otherwise AoE heal self and nearby allies (players, tamed pets, animals).
    *   *Scaling*: Heals `6.0 (3 hearts) + (level - 1) * 2.0` in a `4.0 + (level - 1) * 0.5` block radius. For the bone meal effect, it will work up to level 10, after that it will just apply bonemeal x3.
*   **Passive buff**: **Bountiful Harvest**
    *   *Description*: Chance for double crops on harvest.
    *   *Scaling*: `+1%` double crop chance per level on block break.

## 6. Engineer
*   **Unlock Requirements**: Strength: `20`, Intelligence: `20` (Total Points: `40`)
*   
*   **Passive buff**: **Efficiency**
    *   *Description*: Applies additional Efficiency effect on tools used.
    *   *Scaling*: `+1%`  per level.
    **Passive buff**: **Hacker**
    *   *Description*: Applies additional Durability on tools used. (reduce durability loss)
    *   *Scaling*: `+1%`  per level.
    **Passive buff**: **Repair**
    *   *Description*: Increases repaired amount when repairing tools and effect of Mending enchants.
    *   *Scaling*: `+1%`  per level.
    **Passive buff**: **Power Surge**
    *   *Description*: Increases speed on movement with vehicles, mounts, rails, boats..
    *   *Scaling*: `+1%`  per level.
    
    
    

---

## Hero Classes

Hero classes require specialized, higher stat distributions to unlock.

## 7. Paladin
*   **Unlock Requirements**: Strength: `60`, Vitality: `60`, Intelligence: `60` (Total Points: `200`)
*   **Active Ability 1**: **Aegis**
    *   *Trigger*: Shield in hand, Sneak + `[F]` (or Block + Shift+`[F]` if off-hand)
    *   *Mana Cost*: `40`
    *   *Description*: Invulnerability (Resistance X) for a duration.
    *   *Scaling*: Invulnerability lasts `10.0s + (level - 1) * 2.5s`.
    *   *Unlock condition*: spend 40 points to unlock this active ability!
*   **Active Ability 2**: **Holy Nova**
    *   *Trigger*: Shield in hand, press `[F]` (or Block + `[F]` if off-hand)
    *   *Mana Cost*: `35`
    *   *Description*: AoE healing for allies and damage to monsters.
    *   *Scaling*: Heals/deals `8.0 + (level - 1) * 2.0` in a `4.0 + (level - 1) * 0.5` block radius.
    *   *Unlock condition*: spend 40 points to unlock this active ability!
*   **Passive buff**: **Guardian**
    *   *Description*: Max health boost.
    *   *Scaling*: `+1 heart` max health per level (calculated as `1 full Heart` per level).

## 8. Landlord
*   **Unlock Requirements**: Strength: `60`, Agility: `60`, Vitality: `60` (Total Points: `200`)
*   **Active Ability**: **Transmutation**
    *   *Trigger*: block in hand, Sneak + [F] on target block
    *   *Mana Cost*: `30`
    *   *Description*: Converts target block and nearby connected blocks of the same type into the held block (doesn't work for non-block items or blacklisted blocks).
    *   *Scaling*: Radius increases by 1+(1 blocks per level). 
    *   *Unlock condition*: spend 40 points to unlock this active ability!
*   **Passive buff**: **Domain Lord**
    *   *Description*: Reduces fall damage.
    *   *Scaling*: `+1%` fall damage reduction per level (100% reduction at level 100).

## 9. Alchemist
*   **Unlock Requirements**: Agility: `60`, Vitality: `60`, Intelligence: `60` (Total Points: `200`)
*   **Passive buff**: **Catalyst**
    *   *Description*: Increases applied potion durations.
    *   *Scaling*: `+1%` potion duration per level.
*   **Passive buff**: **Master of the Craft**
    *   *Description*: Doubles the amount of enchants and potions created when taken from enchanting table or brewing stand.
    *   *Unlock condition*: spend 40 points to unlock this passive buff!
