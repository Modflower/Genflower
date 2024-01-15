package gay.ampflower.mod.gen.util;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

/**
 * @author Ampflower
 * @since 0.0.0
 **/
public class VanillaPivot implements Pivot {
	@Override
	public <V, T extends V> T register(final RegistryKey<Registry<V>> key, final String id, final T t) {
		@SuppressWarnings("unchecked") final var registry = (Registry<V>) Registries.REGISTRIES.getOrEmpty(key.getValue()).orElseThrow();
		return Registry.register(registry, Util.id(id), t);
	}
}
