package gay.ampflower.mod.gen.structure.processor;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gay.ampflower.mod.gen.GenflowerStructureProcessorTypes;
import gay.ampflower.mod.gen.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;


/**
 * @author Ampflower
 * @since 0.0.0
 **/
public class LootStructureProcessor extends StructureProcessor {
	private static final Logger logger = LogUtils.getLogger();

	public static final Codec<LootStructureProcessor> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			RegistryCodecs.entryList(RegistryKeys.BLOCK).fieldOf("blocks").forGetter(processor -> processor.blocks),
			Identifier.CODEC.fieldOf("loot_table").forGetter(processor -> processor.lootTable),
			Codec.INT.fieldOf("slots").forGetter(processor -> processor.slots)
		).apply(instance, LootStructureProcessor::new)
	);

	private final RegistryEntryList<Block> blocks;
	private final Identifier lootTable;
	private final int slots;

	private transient final BooleanProperty[][] properties;

	private LootStructureProcessor(RegistryEntryList<Block> blocks, Identifier lootTable, int slots) {
		this.blocks = blocks;
		this.lootTable = lootTable;
		this.slots = slots;
		this.properties = new BooleanProperty[blocks.size()][];
	}

	@Nullable
	@Override
	public StructureTemplate.StructureBlockInfo process(
		final WorldView world,
		final BlockPos $pos$donotuse,
		final BlockPos $pivot$donotuse,
		final StructureTemplate.StructureBlockInfo originalBlockInfo,
		final StructureTemplate.StructureBlockInfo currentBlockInfo,
		final StructurePlacementData data) {

		final var pos = currentBlockInfo.pos();

		final var state = currentBlockInfo.state();
		final var index = indexOf(state);
		if (index < 0) {
			return currentBlockInfo;
		}

		final var optionalLootTable = Util.getLootTable(world, this.lootTable);
		if (optionalLootTable.isEmpty()) {
			logger.warn("No such loot table {} for {}", this.lootTable, pos);
			return currentBlockInfo;
		}
		final var pair = optionalLootTable.get();

		final var random = data.getRandom(currentBlockInfo.pos());
		final var inventory = new SimpleInventory(slots);

		final var context = pair.getRight()
			.add(LootContextParameters.ORIGIN, currentBlockInfo.pos().toCenterPos());

		pair.getLeft().supplyInventory(inventory, context.build(LootContextTypes.CHEST), random.nextLong());

		if (inventory.isEmpty()) {
			logger.warn("No loot emitted by {} for {}; is the table filled out?", this.lootTable, pos);
			return currentBlockInfo;
		}

		final BlockState result = populate(state, index, inventory);

		final var nbt = Util.safeCopy(currentBlockInfo.nbt());
		nbt.put("Items", Util.toNbt(inventory));

		return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), result, nbt);
	}

	private BooleanProperty[] slotProperties(BlockState original, int index) {
		{
			final var properties = this.properties[index];
			if (properties != null) {
				return properties;
			}
		}

		final var properties = new BooleanProperty[slots];

		for (final var property : original.getProperties()) {
			if (!(property instanceof BooleanProperty booleanProperty)) {
				logger.trace("Ignoring not a boolean property {}", property);
				continue;
			}
			final String digits = StringUtils.getDigits(property.getName());
			if (digits == null || digits.isEmpty()) {
				logger.trace("Ignoring boolean property {}", property);
				continue;
			}

			final int slot = Integer.parseInt(digits);

			if (slot >= slots) {
				logger.trace("Excess boolean property for slot {}: {}", slot, property);
				continue;
			}

			properties[slot] = booleanProperty;
		}

		return this.properties[index] = properties;
	}

	private int indexOf(BlockState state) {
		final var entry = state.getRegistryEntry();
		final var itr = blocks.iterator();
		int i = 0;
		while (itr.hasNext()) {
			if (entry.equals(itr.next())) {
				return i;
			}
			i++;
		}

		return -1;
	}

	private BlockState populate(BlockState state, int index, Inventory inventory) {
		BlockState result = state;
		final var properties = slotProperties(state, index);

		for (int i = 0; i < slots; i++) {
			final var stack = inventory.getStack(i);
			final var property = properties[i];
			if (!stack.isEmpty() && property != null) {
				logger.trace("Flipped {} given {}", i, stack);
				result = result.with(property, true);
				if (state == result) {
					logger.warn("State didn't change from {}? Does {} exist?", state, property);
				}
			}
		}

		return result;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return GenflowerStructureProcessorTypes.LOOT;
	}
}
