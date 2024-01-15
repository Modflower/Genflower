package gay.ampflower.mod.gen.util;

import java.util.Collection;
import java.util.function.IntFunction;

/**
 * @author Ampflower
 * @since 0.0.0
 **/
final class Unchecked {
	public static <T> T[] array(int length) {
		return array(new Object[length]);
	}

	@SuppressWarnings({"unchecked", "SuspiciousArrayCast"})
	public static <T, V extends T> V[] array(T[] array) {
		return (V[]) array;
	}

	public static <T, V extends T> V[] toArray(Collection<V> collection, IntFunction<T[]> supplier) {
		return collection.toArray(array(supplier.apply(collection.size())));
	}
}
