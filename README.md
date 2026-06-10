# HereRolePlay

HereRolePlay is a custom RPG plugin for Paper Minecraft servers. It moves away from passive, RNG-heavy grinding and introduces an armor-like class system dictated by your tool choices (inspired by FFXIV's Armoury System) and active skill point allocation.

---

## 3 Core Pillars & World Interaction

Your progression is organized around three fundamental pillars, each representing a distinct mode of interaction with the Minecraft world. Instead of abstract grinding, every action that shapes, extracts from, or tests your survival in the environment contributes directly to your growth:

### 1. Combat (The Pillar of Battle)
The Combat pillar represents your physical struggle against the hostile forces of the world. The environment is inherently dangerous, and your progression is tied to how you survive and conquer these threats:
*   **Conquering the Wild**: Defeating hostile entities yields Combat XP. The experience scales with the threat level—slaying basic monsters rewards standard XP, defeating dangerous subterranean threats like Endermen or Wither Skeletons grants substantial XP, and vanquishing rare world bosses (such as the Ender Dragon, Wither, or Warden) provides monumental growth.
*   **Survival and Resilience**: Growth is forged through adversity. You gain Combat XP simply by sustaining and surviving damage in combat, directly translating the physical toll of battle into experience.

### 2. Collect (The Pillar of Gathering)
The Collect pillar represents your direct extraction of natural resources from the world's ecosystem. Your interaction with the natural geology and botany rewards your labor:
*   **Geological Extraction**: Breaking natural stone, deepslate, and ores grants Collect XP. Extracting standard veins (Coal, Iron, Quartz, etc.) yields steady experience, while discovering and mining precious, deep-earth treasures like Diamonds, Emeralds, and Ancient Debris rewards a massive XP surge.
*   **Forestry and Arboriculture**: Harvesting timber from the world's forests yields XP. Felling common trees, harvesting exotic Nether stems (Crimson and Warped), or logging rare Pale Oak trees rewards you based on the rarity and danger of the environment.
*   **Agricultural Cultivation**: Harvesting fully grown crops yields Collect XP. The mod intelligently distinguishes between standard crops, wild crops, and rare sniffer crops (such as Torchflowers or Pitcher Crops) to reward agricultural investment.
*   **Exploit Prevention**: To preserve the integrity of exploration, the gathering system ignores player-placed block exploits (such as repeatedly placing and breaking the same ore or wood block) while still allowing you to farm and harvest naturally grown crops.

### 3. Craft (The Pillar of Smelting & Assembly)
The Craft pillar represents your ability to refine raw materials and assemble them into advanced tools, gear, and sustenance, completing the resource cycle:
*   **Industrial Specialization**: Refining raw resources yields Craft XP. While standard furnaces provide basic XP, utilizing specialized industrial machinery like Smokers and Blast Furnaces applies a **2.0x multiplier** to reward industrial scaling and specialization.
*   **Assembly**: Combining materials at a crafting table to build tools, weapons, and structures rewards assembly XP, marking your mastery over material fabrication.

---

## Class System & Abilities

HereRolePlay features a dynamic, stat-based class system. Rather than choosing a single permanent class, your abilities and role are determined by your current attribute allocation and the tools you hold.

*   **Unlocking Classes**: As you distribute points into attributes (Strength, Agility, Vitality, Intelligence), you will unlock specialized Base Classes (such as Warrior, Ranger, Wizard, Miner, Farmer, Engineer) and advanced Hero Classes (such as Paladin, Landlord, Alchemist) when you meet their stat thresholds.
*   **Abilities & Passives**: Each class grants unique active abilities (triggered dynamically in combat or gathering, e.g., Cleave, Quick Shot, Arcane Missile, Timber, Rejuvenation) and powerful passive traits (such as damage modifiers, speed boosts, resource-doubling, or durability conservation).

For the complete, up-to-date documentation on all classes, unlocking requirements, mana costs, skill scaling formulas, and active/passive details, please refer to the **[Classes and Abilities Reference Guide](CLASSES.md)**.

---

## Commands
*   `/hrp` - Opens the Main Hub, allowing you to access your Profile, the Class Directory, and the Point Allocation menu.
*   `/hrp admin givexp <player> <amount>` - Gives Combat XP to a player.

---

## License
Licensed under the GNU GPLv3.
