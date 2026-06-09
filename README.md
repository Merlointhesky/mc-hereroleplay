# HereRolePlay

HereRolePlay is a custom RPG plugin for Paper Minecraft servers. It moves away from passive, RNG-heavy grinding and introduces a class system dictated by your tools (inspired by FFXIV's Armoury System) and active skill point allocation.

## Features
- **3 Core Pillars**: Combat, Collect, and Craft. Gain XP by playing the game naturally based on the tool you use.
- **Dynamic Attributes**: Spend points in the `/hrp` menu to instantly increase your Max Health, Movement Speed, Attack Damage, or Max Mana.
- **Active Skills**: Use Mana to cast spells or perform combat maneuvers! Shift+Click logs with an Axe to use *Timber*, or hit the swap-hands key (F) with a Sword to *Cleave*.
- **Developer API**: Built to replace generic skill trackers in your other server plugins, exposing simple API methods to give XP and fetch player profiles.

## Class System
As you allocate attribute points, you will automatically unlock new classes once you meet their stat requirements. Open the Class Directory in the `/hrp` menu to view your progress!

### Base Classes
*   **Warrior**: Requires 20 Strength, 20 Vitality. Focuses on melee power and armor scaling.
*   **Ranger**: Requires 20 Agility, 20 Vitality. Focuses on ranged critical hit chance and movement speed.
*   **Wizard**: Requires 20 Vitality, 20 Intelligence. Focuses on casting magic spells efficiently.
*   **Miner**: Requires 20 Strength, 20 Agility. Focuses on excavation bursts and toughness.
*   **Farmer**: Requires 20 Agility, 20 Intelligence. Focuses on AoE healing and resource yields.
*   **Engineer**: Requires 20 Strength, 20 Intelligence. Focuses on industrial mass production and enchanting.

### Hero Classes
Hero classes are elite specializations that require massive investment into 3 different attributes.
*   **Paladin**: Requires 30 Strength, 30 Vitality, 30 Intelligence. A pure active skill tank and healer.
*   **Landlord**: Requires 30 Strength, 30 Agility, 30 Vitality. Focuses on world shaping and transmutation.
*   **Alchemist**: Requires 30 Agility, 30 Vitality, 30 Intelligence. Focuses on output doubling for potions and enchanting.

## Commands
* `/hrp` - Opens the Main Hub, allowing you to access your Profile, the Class Directory, and the Point Allocation menu.
* `/hrp admin givexp <player> <amount>` - Gives Combat XP to a player.

## License
Licensed under the GNU GPLv3.
