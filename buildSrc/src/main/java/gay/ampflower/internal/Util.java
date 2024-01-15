package gay.ampflower.internal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Ampflower
 * @since 0.0.0
 **/
public class Util {
	public static String urlEncode(String str) {
		return URLEncoder.encode(str, StandardCharsets.UTF_8);
	}

	public static String mkChangelog(String git) {
		if(!isBlank(Env.Changelog)) {
			return Env.Changelog;
		}

		if(git == null) {
			return null;
		}

		if(startsWith(Env.Reference, "refs/tags/")) {
			final var host = VcsHost.find(git);
			return "You may view the changelog at " + git + host.release + urlEncode(Env.getTag());
		}

		return "No changelog is available. Perhaps poke at " + git + " for a changelog?";
	}

	public static String mkVersion(final String baseVersion) {
		if(Env.Publish) {
			return baseVersion;
		}

		if(Env.Actions) {
			return baseVersion + "-build." + Env.getRunNumber() + "-commit." + Env.getCommit(7) + "-branch." +getBranchForVersion();
		}

		return baseVersion + "-build.local";
	}

	private static String getBranchForVersion() {
		final var ref = Env.getBranch();
		if(ref == null) {
			return "unknown";
		}
		return ref.replace('/', '.');
	}

	public static boolean isBlank(@Nullable String str) {
		if(str == null) {
			return true;
		}
		return str.isBlank();
	}

	public static boolean startsWith(@Nullable String str, @NotNull String prefix) {
		if(str == null) {
			return false;
		}
		return str.startsWith(prefix);
	}
}
