package gay.ampflower.mod.gen.forge;

import net.minecraft.registry.RegistryKey;
import net.minecraftforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ampflower
 * @since 0.0.0
 **/
public class RegistrantMap<V> {
	private final Map<RegistryKey<V>, V> values = new HashMap<>();

	public void register(RegistryKey<V> key, V value) {
		final var old = values.put(key, value);
		if (old != null) {
			throw new IllegalArgumentException("duplicate @ " + key + ": overwrote " + old + " with " + value);
		}
	}

	public void pool(RegisterEvent.RegisterHelper<V> helper) {
		for (final var entry : values.entrySet()) {
			helper.register(entry.getKey(), entry.getValue());
		}
	}
}
