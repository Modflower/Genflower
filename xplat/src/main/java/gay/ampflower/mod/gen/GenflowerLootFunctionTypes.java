package gay.ampflower.mod.gen;

import gay.ampflower.mod.gen.loot.function.SetRandomPotionLootFunction;
import gay.ampflower.mod.gen.util.Pivot;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.JsonSerializer;

/**
 * @author Ampflower
 * @since 0.0.0
 **/
public final class GenflowerLootFunctionTypes {
	public static final LootFunctionType SET_RANDOM_POTION = register("set_random_potion", new SetRandomPotionLootFunction.Serializer());

	private static LootFunctionType register(final String name, final JsonSerializer<? extends LootFunction> jsonSerializer) {
		return Pivot.INSTANCE.register(RegistryKeys.LOOT_FUNCTION_TYPE, name, new LootFunctionType(jsonSerializer));
	}

	public static void init() {
	}
}
