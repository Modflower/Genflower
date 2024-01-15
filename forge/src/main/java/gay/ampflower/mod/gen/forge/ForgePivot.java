package gay.ampflower.mod.gen.forge;

import com.mojang.logging.LogUtils;
import gay.ampflower.mod.gen.util.Pivot;
import gay.ampflower.mod.gen.util.Util;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ampflower
 * @since 0.0.0
 **/
@Mod.EventBusSubscriber(modid = Util.MODID)
public class ForgePivot implements Pivot {
	private static final Logger logger = LogUtils.getLogger();
	private final Map<RegistryKey<?>, RegistrantMap<?>> map = new HashMap<>();

	@Override
	public synchronized <V, T extends V> T register(final RegistryKey<Registry<V>> key, final String id, final T t) {
		@SuppressWarnings("unchecked") final var list = (RegistrantMap<V>) map.computeIfAbsent(key, $key -> new RegistrantMap<>());
		list.register(RegistryKey.of(key, Util.id(id)), t);
		logger.debug("Registered {} @ {} within {}", t, id, key);
		return t;
	}

	@SubscribeEvent
	public void register(RegisterEvent event) {
		Pivot.register();

		for (final var entry : map.entrySet()) {
			unsafeRegister(event, entry.getKey(), entry.getValue());
		}
	}

	// Usage should never allow this to add incorrect types.
	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void unsafeRegister(RegisterEvent base, RegistryKey key, RegistrantMap list) {
		base.register(key, list::pool);
	}
}
