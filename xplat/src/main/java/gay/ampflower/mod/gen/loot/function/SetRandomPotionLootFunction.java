package gay.ampflower.mod.gen.loot.function;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import gay.ampflower.mod.gen.GenflowerLootFunctionTypes;
import gay.ampflower.mod.gen.util.PredictaWeightedList;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.registry.Registries;

/**
 * @author Ampflower
 * @since 0.0.0
 **/
public class SetRandomPotionLootFunction extends ConditionalLootFunction {
	private static final Codec<PredictaWeightedList<Potion>>
		weightedPotionsCodec = PredictaWeightedList.createCodec(Registries.POTION.getCodec(), Registries.POTION);

	private final PredictaWeightedList<Potion> potions;

	public SetRandomPotionLootFunction(final LootCondition[] lootConditions, final PredictaWeightedList<Potion> potions) {
		super(lootConditions);
		this.potions = potions;
	}

	@Override
	protected ItemStack process(final ItemStack stack, final LootContext context) {
		return PotionUtil.setPotion(stack, potions.get(context.getRandom()));
	}

	@Override
	public LootFunctionType getType() {
		return GenflowerLootFunctionTypes.SET_RANDOM_POTION;
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<SetRandomPotionLootFunction> {

		@Override
		public void toJson(final JsonObject jsonObject, final SetRandomPotionLootFunction conditionalLootFunction, final JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, conditionalLootFunction, jsonSerializationContext);
			final var array = new JsonArray();
			final var result = weightedPotionsCodec.encode(conditionalLootFunction.potions, JsonOps.INSTANCE, array);
			jsonObject.add("potions", result.getOrThrow(false, str -> {
				throw new AssertionError("unable to store potions: " + str);
			}));
		}

		@Override
		public SetRandomPotionLootFunction fromJson(final JsonObject json, final JsonDeserializationContext context, final LootCondition[] conditions) {
			final var list = weightedPotionsCodec.decode(JsonOps.INSTANCE, json.get("potions"))
				.getOrThrow(false, str -> {
					throw new JsonSyntaxException("unable to parse potion list: " + str);
				}).getFirst();

			return new SetRandomPotionLootFunction(conditions, list);
		}
	}
}
