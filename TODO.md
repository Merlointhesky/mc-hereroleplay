# HereRolePlay (HRP) Technical Design Document

## 1. Core Philosophy
HereRolePlay is a Minecraft RPG plugin designed for Paper servers that emphasizes **player agency** and **sandbox freedom**. It moves away from passive, RNG-heavy grinding in favor of a class system dictated by the player's tools (inspired by FFXIV's Armoury System) and active skill allocation.

HereRolePlay is designed to replace Auraskills compatibility in the other plugins. It will be the main skill plugin used by the server. All other plugins will use HereRolePlay for skill tracking and progression and generate experience in the appropriate section via an API.

---

## 2. The 3 Pillars (Core Categories)
Every action maps to one of three categories based on the **tool in hand** or the **context of the action**.

### A. Combat
* **Triggers:** Killing any entity (mobs, players, animals) while wielding a weapon.
* **Tools:** Swords, Axes, Bows, Crossbows, Tridents, Mace, Spear.

### B. Collect
* **Triggers:** Breaking natural resources or killing entities with a gathering tool.
* **Tools:** Pickaxes, Shovels, Hoes, Shears.
* **Harvest Rule:** Breaking natural resources (crops/logs) yields Collect XP regardless of hand item.

### C. Craft
* **Triggers:** Pulling items from utility blocks or creating items in menus.
* **Stations:** Crafting Tables, Furnaces, Smokers, Blast Furnaces, Brewing Stands, Enchanting Tables, Smithing Tables, Anvils, Stonecutters, Looms, Cartography Tables.
* **Easter Egg:** Flint & Steel ignition grants 1 Craft XP.

---

## 3. Progression & Mathematics
### XP Scaling Formula
Level progression uses a non-linear power curve:
$X_{req} = B 	imes (L - 1)^{C}$
*(B = Base Modifier, L = Level, C = Scaling Coefficient)*
There is no limit to the level up via experience gains.

### XP Tiers
* **Tier 1:** 1 XP (Common/Trivial)
* **Tier 2:** 5 XP (Uncommon/Moderate)
* **Tier 3:** 25 XP (Rare/Dangerous, e.g., Pale Logs, Diamond Ore)
* **Tier 4:** 250 XP (Bosses, Netherite Upgrades)

### Workstation Multipliers
* **Blast Furnace / Smoker and other work specific workstations:** 2.0x XP Multiplier.
* **Enchanting Table:** Scaled by Rarity Weight of enchantment.

---

## 4. Core Attributes & Mana (The "Body & Mind")
Players spend Skill Points on both Base Stats and Category Passives.

* **Strength:** Melee/Ranged Damage (Attribute: `GENERIC_ATTACK_DAMAGE`)
* **Agility:** Movement Speed (Attribute: `GENERIC_MOVEMENT_SPEED`)
* **Vitality:** Max Health (Attribute: `GENERIC_MAX_HEALTH`)
* **Intelligence:** Mana Capacity & Regen (Custom Mana Resource)

---

## 5. Active Skills & Mana
Skills are triggered via contextual inputs (Shift+Click, F-Key swap).

* **Collect Bursts:** Timber (Axe), Excavate/Tunnel (Shovel/Pick), Bountiful Harvest (Hoe).
* **Combat Maneuvers:** Cleave (Sword), Execution (Axe), Seismic Smash (Mace), Dragoon's Dive (Spear), Shield Bash (Shield).
* **Support:** Nature's Mend (Hoe in non-farm context).
* **Magic:** Arcane Missile (Stick), Elemental Blast (Stick).

---

## 6. Class System
Classes are unlocked via total points in specific attributes.

| Class | Requirements | Core Mechanic |
| :--- | :--- | :--- |
| **Warrior** | 20 Str + Vit | Melee power, Armor scaling |
| **Ranger** | 20 Agi + Vit | Ranged Crit chance, Move speed |
| **Wizard** | 20 Int + Vit | Magic spells via Stick, Mana efficient |
| **Miner** | 20 Str + Agi | Excavation bursts, Toughness |
| **Farmer** | 20 Int + Agi | AoE Healing, Resource yield |
| **Engineer** | 20 Str + Int | Industrial mass production, Enchanting bonuses |

### Hero Classes (Prestige - 100+ Total Points)
* **Paladin (Str+Vit+Int):** Pure active skill tank/healer (Aegis, Holy Nova).
* **Landlord (Str+Agi+Vit):** World shaping (Transmutation: 1 block swaps 64 connected).
* **Alchemist (Int+Vit+Agi):** Output doubling (Potions, Enchanting).

### Mythical Class (Admin)
* **Admin Class:** Unlocked by mastering everything. Grants `/hrp givexp` permissions and cosmetic aura.

---

## 7. GUI Design (`/hrp`)
* **Main Hub:** Access to Profile, Allocation Menu, and Class Directory.
* **Allocation Menu:** Nested 6-row GUI to spend points on Attributes and Passives (1pt = single, Shift+Click = 5pt).
* **Class Mastery Submenus:** Unique per class, detailing Active Skill upgrades and Exclusive Passives.