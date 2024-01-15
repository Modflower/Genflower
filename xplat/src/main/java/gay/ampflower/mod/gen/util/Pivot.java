package gay.ampflower.mod.gen.util;

import gay.ampflower.mod.gen.GenflowerLootFunctionTypes;
import gay.ampflower.mod.gen.GenflowerStructureProcessorTypes;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import java.util.ServiceLoader;

/**
 * @author Ampflower
 * @since 0.0.0
 **/
public interface Pivot {
	Pivot INSTANCE = ServiceLoader.load(Pivot.class).findFirst().orElseThrow();

	<V, T extends V> T register(RegistryKey<Registry<V>> key, String id, T t);

	static void register() {
		GenflowerLootFunctionTypes.init();
		GenflowerStructureProcessorTypes.init();
	}
}
