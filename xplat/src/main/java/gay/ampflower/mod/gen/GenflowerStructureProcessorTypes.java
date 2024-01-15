package gay.ampflower.mod.gen;

import com.mojang.serialization.Codec;
import gay.ampflower.mod.gen.structure.processor.BlockMapStructureProcessor;
import gay.ampflower.mod.gen.structure.processor.LootStructureProcessor;
import gay.ampflower.mod.gen.util.Pivot;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;

/**
 * @author Ampflower
 * @since 0.0.0
 **/
public final class GenflowerStructureProcessorTypes {
	public static final StructureProcessorType<BlockMapStructureProcessor> BLOCK_MAP = register("block_map", BlockMapStructureProcessor.CODEC);
	public static final StructureProcessorType<LootStructureProcessor> LOOT = register("loot", LootStructureProcessor.CODEC);

	private static <P extends StructureProcessor> StructureProcessorType<P> register(String id, Codec<P> codec) {
		return Pivot.INSTANCE.register(RegistryKeys.STRUCTURE_PROCESSOR, id, () -> codec);
	}

	public static void init() {
	}
}
