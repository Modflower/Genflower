package gay.ampflower.mod.gen.util;

import com.google.common.collect.Iterators;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author Ampflower
 * @since 0.0.0
 **/
public class PredictaWeightedList<T> implements Iterable<T> {
	private static final Logger logger = LogUtils.getLogger();
	private static final Entry<?>[] EMPTY = new Entry[0];
	private @NotNull Entry<T>[] entries;
	private int totalWeight;

	@SafeVarargs
	public PredictaWeightedList(Entry<T>... entries) {
		this.entries = entries;
		this.totalWeight = calcWeights(entries);
	}

	public PredictaWeightedList(List<Entry<T>> entries) {
		this(Unchecked.toArray(entries, Entry[]::new));
	}

	public PredictaWeightedList() {
		this(Unchecked.array(EMPTY));
	}

	public static <C> Codec<PredictaWeightedList<C>> createCodec(Codec<C> codec) {
		return Entry.createCodec(codec).listOf().xmap(PredictaWeightedList::new, list -> List.of(list.entries));
	}

	public void add(@NotNull T value, int weight) {
		Objects.requireNonNull(value, "value");

		final int index = entries.length;
		entries = Arrays.copyOf(entries, index + 1);
		entries[index] = new Entry<>(value, weight);

		this.totalWeight = weight;
	}

	public T get(Random random) {
		if (entries.length == 0) {
			return null;
		}
		final int pickedWeight;
		int weight = pickedWeight = random.nextInt(this.totalWeight);

		for (final var entry : entries) {
			if (0 > (weight -= entry.weight)) {
				return entry.value;
			}
		}

		logger.warn("Randomly picked weight {} over-iterated {}; {} remaining, is the weight wrong? recalc: {} vs original: {}",
			pickedWeight, entries.length, weight, calcWeights(entries), totalWeight);
		return entries[random.nextInt(entries.length)].value;
	}

	public List<T> shuffle(Random random) {
		final int length = entries.length;
		final T[] values = Unchecked.array(length);
		final ShuffledEntry<T>[] shuffled = shuffle0(random);

		for (int i = 0; i < length; i++) {
			values[i] = shuffled[i].value;
		}

		return Arrays.asList(values);
	}

	private ShuffledEntry<T>[] shuffle0(Random random) {
		final int length = entries.length;
		final ShuffledEntry<T>[] values = Unchecked.array(new ShuffledEntry[length]);

		for (int i = 0; i < length; i++) {
			values[i] = new ShuffledEntry<>(
				entries[i].value,
				Math.pow(random.nextDouble(), 1d / entries[i].weight)
			);
		}

		Arrays.sort(values);

		return values;
	}

	@NotNull
	@Override
	public Iterator<T> iterator() {
		return new Itr<>(entries);
	}

	@NotNull
	public Iterator<Entry<T>> iteratorRaw() {
		return Iterators.forArray(entries);
	}

	private static int calcWeights(final Entry<?>[] entries) {
		int totalWeight = 0;
		for (final var entry : entries) {
			totalWeight += entry.weight;
		}
		return totalWeight;
	}

	public record Entry<T>(T value, int weight) {
		public static <C> Codec<Entry<C>> createCodec(Codec<C> codec) {
			return new EntryCodec<>(codec);
		}
	}

	private record EntryCodec<C>(Codec<C> codec) implements Codec<Entry<C>> {
		@Override
		public <T> DataResult<Pair<Entry<C>, T>> decode(final DynamicOps<T> ops, final T input) {
			Dynamic<T> dyn = new Dynamic<>(ops, input);
			return dyn.get("data")
				.flatMap(codec::parse)
				.map(datax -> new Entry<>(datax, dyn.get("weight").asInt(1)))
				.map(entry -> Pair.of(entry, ops.empty()));
		}

		@Override
		public <T> DataResult<T> encode(final Entry<C> input, final DynamicOps<T> ops, final T prefix) {
			return ops.mapBuilder()
				.add("weight", ops.createInt(input.weight))
				.add("data", codec.encodeStart(ops, input.value))
				.build(prefix);
		}
	}

	// Implementation details below

	private record ShuffledEntry<T>(T value, double shuffle) implements Comparable<ShuffledEntry<T>> {
		@Override
		public int compareTo(@NotNull final PredictaWeightedList.ShuffledEntry<T> tShuffledEntry) {
			return Double.compare(tShuffledEntry.shuffle, shuffle);
		}
	}

	private static class Itr<T> implements Iterator<T> {
		private final @NotNull Entry<T>[] array;
		private int i = 0;

		private Itr(final Entry<T>[] array) {
			this.array = array;
		}

		@Override
		public boolean hasNext() {
			return i < array.length;
		}

		@Override
		public T next() {
			return array[i++].value;
		}
	}
}
