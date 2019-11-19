package ca.mt.gore;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.eclipse.core.runtime.Platform;
import org.eclipse.lsp4e.LanguageServerPlugin;
import org.eclipse.lsp4e.server.ProcessOverSocketStreamConnectionProvider;
import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.eclipse.lsp4e.server.StreamConnectionProvider;

public class GoServer implements StreamConnectionProvider {
	private static final int CONNECTION_PORT = 4389;

	private StreamConnectionProvider provider;

	public GoServer() {
	}

	@Override
	public void start() throws IOException {
		if (provider != null) {
			LanguageServerPlugin.logInfo("GORE: Restarting LSP");
			provider.stop();
		}
		createProvider();
		provider.start();
	}

	private void createProvider() throws IOException {
		String os = Platform.getOS();
		List<String> commands = new ArrayList<>();

		String gopls = resolveGoBinary("gopls");
		if (gopls != null) {
			LanguageServerPlugin.logInfo("GORE: Found gopls at " + gopls);
			commands.add(gopls);
			commands.add("serve");
			if (os.equals(Platform.OS_WIN32)) {
				commands.add("--listen=127.0.0.1:" + CONNECTION_PORT);
			}
		} else {
			String bingo = resolveGoBinary("bingo");
			if (bingo != null) {
				LanguageServerPlugin.logInfo("GORE: Found bingo at " + bingo);
				commands.add(bingo);
				commands.add("-trace");
				commands.add("-format-style");
				commands.add("goimports");
				if (os.equals(Platform.OS_WIN32)) {
					commands.add("--mode=tcp");
					commands.add("--addr=127.0.0.1:" + CONNECTION_PORT);
				}
			}
		}

		if (commands.isEmpty()) {
			IOException ex = new IOException("Cannot find gopls or bingo");
			LanguageServerPlugin.logError("GORE: Could not find gopls or bingo", ex);
			throw ex;
		}

		File workingDir = new File(".");
		if (os.equals(Platform.OS_WIN32)) {
			provider = new ProcessOverSocketStreamConnectionProvider(commands, workingDir.toString(), CONNECTION_PORT) {
			};
		} else {
			provider = new ProcessStreamConnectionProvider(commands, workingDir.toString()) {
			};
		}
	}

	private static String resolveGoBinary(String program) {
		if (Platform.OS_WIN32.equals(Platform.getOS()) && !program.toLowerCase().endsWith(".exe")) {
			program += ".exe";
		}
		List<Supplier<String>> pathProviders = Arrays.asList(() -> System.getenv("GOBIN"),
				() -> System.getenv("GOPATH"), () -> run("go", "env", "GOBIN"), () -> run("go", "env", "GOPATH"),
				() -> new File(System.getenv("HOME"), "go").toString());
		for (Supplier<String> provider : pathProviders) {
			String path = provider.get();
			if (!Strings.isNullOrEmpty(path)) {
				String fullPath = resolve(path, program);
				if (fullPath != null) {
					return fullPath;
				}
				fullPath = resolve(path, "bin" + File.separator + program);
				if (fullPath != null) {
					return fullPath;
				}
			}
		}

		return resolve(System.getenv("PATH"), program);
	}

	private static String run(String... cmd) {
		try {
			Process process = new ProcessBuilder(cmd).start();
			if (process.waitFor() != 0) {
				return null;
			}
			return new String(ByteStreams.toByteArray(process.getInputStream()), StandardCharsets.UTF_8).trim();
		} catch (IOException ex) {
			return null;
		} catch (InterruptedException ex) {
			return null;
		}
	}

	private static String resolve(String env, String relativeFile) {
		if (Strings.isNullOrEmpty(env)) {
			return null;
		}
		env = env.trim();
		for (String path : Splitter.on(File.pathSeparator).split(env)) {
			File file = new File(path, relativeFile);
			if (file.exists() && file.canExecute()) {
				return file.getPath();
			}
		}
		return null;
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
