package gay.ampflower.mod.gen.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;

/**
 * @author Ampflower
 * @since 0.0.0
 **/
public final class StateUtils {
	public static BlockState convertStairToSlab(BlockState original, Block target) {
		final var half = original.getOrEmpty(StairsBlock.HALF).orElse(BlockHalf.BOTTOM);

		return target.getDefaultState().with(SlabBlock.TYPE, half == BlockHalf.TOP ? SlabType.TOP : SlabType.BOTTOM);
	}

	public static <S extends State<?, S>, P extends Property<V>, V extends Comparable<V>> S copyProperty(
		final S original,
		final S target,
		final P property) {
		return target.with(property, original.get(property));
	}

	@SuppressWarnings("unchecked")
	public static <S extends State<?, S>, P extends Property<V>, V extends Comparable<V>> S unsafeWith(
		final S target,
		final Property<?> property,
		final Comparable<?> value) {
		assert property.getValues().contains(value);
		return target.with((P) property, (V) value);
	}
}
