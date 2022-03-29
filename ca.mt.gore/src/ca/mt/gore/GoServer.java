package ca.mt.gore;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.Platform;
import org.eclipse.lsp4e.LanguageServerPlugin;
import org.eclipse.lsp4e.server.ProcessOverSocketStreamConnectionProvider;
import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.eclipse.lsp4e.server.StreamConnectionProvider;

public class GoServer implements StreamConnectionProvider {
	private final class ProcessStreamConnectionProviderExtension extends ProcessStreamConnectionProvider {
		private ProcessStreamConnectionProviderExtension(List<String> commands, String workingDir) {
			super(commands, workingDir);
		}

		@Override
		protected ProcessBuilder createProcessBuilder() {
			ProcessBuilder pb = super.createProcessBuilder();
			addGoplsLocationToEnvironment(pb.environment());
			return pb;
		}
	}

	private final class ProcessOverSocketStreamConnectionProviderExtension
			extends ProcessOverSocketStreamConnectionProvider {
		private ProcessOverSocketStreamConnectionProviderExtension(List<String> commands, String workingDir, int port) {
			super(commands, workingDir, port);
		}

		@Override
		protected ProcessBuilder createProcessBuilder() {
			ProcessBuilder pb = super.createProcessBuilder();
			addGoplsLocationToEnvironment(pb.environment());
			return pb;
		}
	}

	private static final int CONNECTION_PORT = 4389;

	private StreamConnectionProvider provider;

	private String goplsDir;

	public GoServer() {}

	@Override
	public void start() throws IOException {
		if (provider != null) {
			LanguageServerPlugin.logInfo("GORE: Restarting LSP");
			provider.stop();
		}
		createProvider();
		provider.start();
	}

	@Override
	public Object getInitializationOptions(URI rootUri) {
		JsonObject settings = new JsonObject();

		String settingsJson = PreferencesInitializer.getPreferences().getString(PreferencesInitializer.SETTINGS_JSON);
		if (!Strings.isNullOrEmpty(settingsJson)) {
			try {
				settings = new Gson().fromJson(settingsJson, JsonObject.class);
			} catch (JsonSyntaxException e) {
				LanguageServerPlugin.logInfo("GORE: Invalid JSON object for settings: " + e + "\n" + settingsJson);
			}
		}

		File vscodeSettingsFile = new File(new File(new File(rootUri), ".vscode"), "settings.json");
		try (Reader r = new FileReader(vscodeSettingsFile)) {
			JsonObject vscodeSettings = new Gson().fromJson(r, JsonObject.class);
			if (vscodeSettings != null && (vscodeSettings = vscodeSettings.getAsJsonObject("gopls")) != null) {
				settings = mergeJsonObjects(settings, vscodeSettings);
			}
		} catch (IOException ex) {
		}

		return settings;
	}

	/**
	 * Merge {@code original} and {@code other} into a new objects, such that all
	 * values from {@code other} are copied onto and replace by those in
	 * {@code original}, and return the result.
	 *
	 * @param original
	 * @param other
	 */
	private JsonObject mergeJsonObjects(JsonObject original, JsonObject other) {
		JsonObject result = original.deepCopy();
		for (Map.Entry<String, JsonElement> entry : other.entrySet()) {
			original.remove(entry.getKey());
			original.add(entry.getKey(), entry.getValue());
		}
		return result;
	}

	@Override
	public String getTrace(URI rootUri) {
		// should be one of "off", "messages", or "verbose"
		return "messages";
	}

	private void createProvider() throws IOException {
		String os = Platform.getOS();

		String gopls = GoResolver.resolveGoBinary("gopls");
		if (gopls == null) {
			IOException ex = new IOException("Cannot find gopls");
			LanguageServerPlugin.logError("GORE: Could not find gopls or bingo", ex);
			throw ex;
		}

		LanguageServerPlugin.logInfo("GORE: Found gopls at " + gopls);
		goplsDir = gopls.substring(0, gopls.lastIndexOf(File.separator));

		List<String> commands = Arrays.asList(gopls, "serve"); // "-rpc.trace"
		File workingDir = new File(".");
		if (os.equals(Platform.OS_WIN32)) {
			commands.add("--listen=127.0.0.1:" + CONNECTION_PORT);
			provider = new ProcessOverSocketStreamConnectionProviderExtension(commands, workingDir.toString(),
					CONNECTION_PORT);
		} else {
			provider = new ProcessStreamConnectionProviderExtension(commands, workingDir.toString());
		}
	}

	/**
	 * Update the gopls environment by adding the gopls location to the PATH.
	 */
	public void addGoplsLocationToEnvironment(Map<String, String> environment) {
		environment.merge("PATH", goplsDir, (pathValue, update) -> pathValue + File.pathSeparator + update); //$NON-NLS-1$
		LanguageServerPlugin.logInfo("GORE: setting PATH=" + environment.get("PATH")); //$NON-NLS-1$//$NON-NLS-2$
	}

	@Override
	public InputStream getInputStream() {
		return provider.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() {
		return provider.getOutputStream();
	}

	@Override
	public InputStream getErrorStream() {
		return provider.getErrorStream();
	}

	@Override
	public void stop() {
		provider.stop();
	}
}
