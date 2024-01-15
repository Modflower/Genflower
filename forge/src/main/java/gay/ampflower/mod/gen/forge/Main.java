package gay.ampflower.mod.gen.forge;

import gay.ampflower.mod.gen.util.Pivot;
import gay.ampflower.mod.gen.util.Util;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * @author Ampflower
 * @since 0.0.0
 **/
@Mod(Util.MODID)
public final class Main {

	public Main() {
		final var self = FMLJavaModLoadingContext.get();

		// Thanks, I hate it.
		self.getModEventBus().register(Pivot.INSTANCE);
	}
}
