# Genflower

A small worldgen and loot utility library for mods and datapacks

## What does it add?

Genflower adds two new structure processors and a loot function to help speed up the development of datapacks,
allowing one to do things that weren't possible, were difficult or tedious before with vanilla's processors.

### Loot Functions

#### Set Random Potion `genflower:set_random_potion`

A potion randomiser function for potions found in brewing stands and chests.

- `potions`: A potion->weight map expressed in one of two ways:
	- Map of ID to Weight: `{"minecraft:luck": 1}`
	- Object-in-List: `[{"data":"minecraft:luck","weight":1}]`

The `weight` field is optional and defaults to 1 when using the object-in-list notation.

<details><summary>Example JSON file</summary>

```json5
// data/useyourname/loot_tables/chests/your_loot.json -> /pools[]
{
	"entries": [
		{
			"type": "minecraft:item",
			"functions": [
				{
					"function": "genflower:set_random_potion",
					"potions": {
						"minecraft:long_invisibility": 1,
						"minecraft:invisibility": 5
					}
				}
			],
			"name": "minecraft:potion"
		},
		{
			"type": "minecraft:item",
			"functions": [
				{
					"function": "genflower:set_random_potion",
					"potions": [
						{
							"data": "minecraft:long_invisibility",
							"weight": 1
						},
						{
							"data": "minecraft:invisibility",
							"weight": 5
						}
					]
				}
			],
			"name": "minecraft:splash_potion"
		}
	]
}
```

</details>

### Structure Processors

#### Block Map `genflower:block_map`

A block to blocks mapping to reduce the verbosity of structure processors.

- `block_map`: A block to list of blocks map used for replacing blocks.
- `probability`: The likelihood of a block being replaced with a new one.
	- Scale of 0-1, defaults to 1.

<details><summary>Example JSON file</summary>

```json5
// data/useyourname/worldgen/processor_list/your_processor.json
{
	"processors": [
		{
			"processor_type": "genflower:block_map",
			"block_map": {
				"minecraft:cobweb": ["minecraft:air"],
				"supplementaries:ash": ["minecraft:air"]
			}
		},
		{
			"processor_type": "genflower:block_map",
			"block_map": {
				"minecraft:deepslate_bricks": [
					"minecraft:cobbled_deepslate",
					"minecraft:deepslate_brick_stairs",
					"minecraft:deepslate_brick_slab"
				],
				"minecraft:lantern": ["wilderwild:display_lantern"],
				"minecraft:deepslate_brick_stairs": [
					"minecraft:deepslate_brick_slab"
				],
				"minecraft:mangrove_stairs": [
					"minecraft:mangrove_slab",
					"minecraft:cobweb"
				],
				"minecraft:anvil": [
					"minecraft:chipped_anvil",
					"minecraft:damaged_anvil"
				]
			},
			"probability": 0.25
		}
	]
}
```

</details>

#### Loot `genflower:loot`

A worldgen-time loot applicator for blocks that have inventories but don't support the `LootTable` NBT tag.

- `loot_table`: A loot table to apply to given blocks
- `blocks`: An allowed list of blocks to inject loot into.
- `slots`: How many slots the block normally supports.
- `probability`: The likelihood of a block having the loot table applied.
	- Scale of 0-1, defaults to 1.

<details><summary>Example JSON file</summary>

```json5
// data/useyourname/worldgen/processor_list/your_processor.json
{
	"processors": [
		{
			"processor_type": "genflower:loot",
			"loot_table": "useyourname:chests/library_bookshelves",
			"blocks": ["minecraft:chiseled_bookshelf"],
			"slots": 6,
			"probability": 0.8
		},
		{
			"processor_type": "genflower:loot",
			"loot_table": "useyourname:chests/library_brewing_stands",
			"blocks": ["minecraft:brewing_stand"],
			"slots": 3
		}
	]
}
```

</details>

