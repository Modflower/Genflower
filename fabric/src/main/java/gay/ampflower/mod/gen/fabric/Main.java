package gay.ampflower.mod.gen.fabric;

import gay.ampflower.mod.gen.util.Pivot;
import net.fabricmc.api.ModInitializer;

/**
 * @author Ampflower
 * @since 0.0.0
 **/
public class Main implements ModInitializer {

	@Override
	public void onInitialize() {
		Pivot.register();
	}
}
