package gay.ampflower.mod.gen.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author Ampflower
 * @since 0.0.0
 **/
public final class Util {
	public static final String MODID = "genflower";

	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}

	// Call to chunkRegion.toServerWorld() cannot be avoided
	@SuppressWarnings("deprecation")
	public static Optional<Pair<LootTable, LootContextParameterSet.Builder>> getLootTable(WorldView world, Identifier lootTable) {
		final ServerWorld serverWorld;

		if (world instanceof ChunkRegion chunkRegion) {
			// Required as there's no other way to satisfy the loot manager.
			serverWorld = chunkRegion.toServerWorld();
		} else if (world instanceof ServerWorld castedServerWorld) {
			serverWorld = castedServerWorld;
		} else {
			return Optional.empty();
		}

		final var lootManager = serverWorld.getServer().getLootManager();

		return lootManager.getElementOptional(LootDataType.LOOT_TABLES, lootTable)
			.map(table -> new Pair<>(table, new LootContextParameterSet.Builder(serverWorld)));
	}

	@NotNull
	public static NbtCompound safeCopy(final @Nullable NbtCompound original) {
		if (original == null) {
			return new NbtCompound();
		}
		return original.copy();
	}

	@NotNull
	public static NbtList toNbt(@NotNull Inventory inventory) {
		final var list = new NbtList();
		for (int i = 0; i < inventory.size(); i++) {
			final var stack = inventory.getStack(i);
			if (stack.isEmpty()) {
				continue;
			}

			final var nbt = new NbtCompound();
			stack.writeNbt(nbt);
			nbt.putByte("Slot", (byte) i);

			list.add(nbt);
		}
		return list;
	}
}
