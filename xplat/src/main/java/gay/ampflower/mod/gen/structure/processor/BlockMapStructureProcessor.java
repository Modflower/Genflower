package gay.ampflower.mod.gen.structure.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gay.ampflower.mod.gen.GenflowerStructureProcessorTypes;
import gay.ampflower.mod.gen.util.StateUtils;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Ampflower
 * @since 0.0.0
 **/
public class BlockMapStructureProcessor extends StructureProcessor {
	public static final Codec<BlockMapStructureProcessor> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			Codec.simpleMap(
				Registries.BLOCK.getCodec(),
				RegistryCodecs.entryList(RegistryKeys.BLOCK),
				Registries.BLOCK
			).fieldOf("block_map").forGetter(processor -> processor.blockMap),
			Codec.floatRange(0, 1).optionalFieldOf("probability", 1.F).forGetter(processor -> processor.probability)
		).apply(instance, BlockMapStructureProcessor::new)
	);

	private final Map<Block, RegistryEntryList<Block>> blockMap;
	private final float probability;

	private transient final Map<Property<?>, List<Comparable<?>>> propertyListMap;

	private BlockMapStructureProcessor(Map<Block, RegistryEntryList<Block>> blockMap, float probability) {
		this.blockMap = blockMap;
		this.probability = probability;
		this.propertyListMap = genMap(blockMap);
	}

	private static Map<Property<?>, List<Comparable<?>>> genMap(Map<Block, RegistryEntryList<Block>> blockMap) {
		final var propertyListMap = new Reference2ObjectOpenHashMap<Property<?>, List<Comparable<?>>>();
		final var witness = new HashSet<Property<?>>();

		for (final var entry : blockMap.entrySet()) {
			final var block = entry.getKey();
			witness.addAll(block.getStateManager().getProperties());

			put(propertyListMap, entry.getValue(), witness);

			witness.clear();
		}

		if (propertyListMap.isEmpty()) {
			return Map.of();
		}

		return Map.copyOf(propertyListMap);
	}

	private static void put(Map<Property<?>, List<Comparable<?>>> propertyListMap, RegistryEntryList<Block> blocks, Set<Property<?>> witness) {
		for (final var block : blocks) {
			for (final var property : block.value().getStateManager().getProperties()) {
				if (witness.add(property)) {
					propertyListMap.put(property, List.copyOf(property.getValues()));
				}
			}
		}
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

		final var random = data.getRandom(currentBlockInfo.pos());
		if (random.nextFloat() > probability) return currentBlockInfo;

		final var state = currentBlockInfo.state();
		final var list = blockMap.get(state.getBlock());
		if (list == null || list.size() == 0) return currentBlockInfo;

		final var trans = list.getRandom(random).orElseThrow(AssertionError::new).value();
		final BlockState result;

		if (state.getProperties().equals(trans.getDefaultState().getProperties())) {
			result = trans.getStateWithProperties(state);
		} else if (state.getBlock() instanceof StairsBlock && trans instanceof SlabBlock) {
			result = StateUtils.convertStairToSlab(state, trans);
		} else {
			result = copyOrRandomise(state, trans, random);
		}

		return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), result, currentBlockInfo.nbt());
	}

	private BlockState copyOrRandomise(BlockState original, Block target, Random random) {
		var result = target.getDefaultState();
		final var properties = new HashSet<>(result.getProperties());

		for (final var property : original.getProperties()) {
			if (properties.remove(property)) {
				result = StateUtils.copyProperty(original, result, property);
			}
		}

		// Should never randomly become waterlogged
		properties.remove(Properties.WATERLOGGED);

		// Prevents rendering bugs by illegal state
		properties.remove(WallBlock.UP);

		for (final var property : properties) {
			result = StateUtils.unsafeWith(result, property, Util.getRandom(propertyListMap.get(property), random));
		}

		return result;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return GenflowerStructureProcessorTypes.BLOCK_MAP;
	}
}
