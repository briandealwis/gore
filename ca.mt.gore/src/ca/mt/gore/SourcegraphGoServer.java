package ca.mt.gore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.lsp4e.LanguageServerPlugin;
import org.eclipse.lsp4e.server.ProcessOverSocketStreamConnectionProvider;
import org.eclipse.lsp4e.server.ProcessStreamConnectionProvider;
import org.eclipse.lsp4e.server.StreamConnectionProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class SourcegraphGoServer implements StreamConnectionProvider {
	private static final int CONNECTION_PORT = 4389;

	private StreamConnectionProvider provider;

	public SourcegraphGoServer() {
		List<String> commands = new ArrayList<>();
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		IPath workingDir = Path.EMPTY;
		try {
			// FileLocator's variables must appear as the first path element
			String os = Platform.getOS(); // linux, win32, macosx
			String arch = Platform.getOSArch(); // x86_64
			IPath binaryPath = new Path("binaries").append(os + "." + arch).append("go-langserver");
			URL binaryUrl = FileLocator.find(bundle, binaryPath, null);
			IPath binary = new Path(FileLocator.toFileURL(binaryUrl).getPath());
			workingDir = binary.removeLastSegments(1); 
			commands.add(binary.toOSString());
		} catch (IOException e) {
			LanguageServerPlugin.logError(e);
			return;
		}
		commands.add("-trace");
		commands.add("-format-tool");
		commands.add("gofmt");
		commands.add("-gocodecompletion");
		commands.add("-func-snippet-enabled");
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			commands.add("--mode=tcp");
			commands.add("--addr=127.0.0.1:" + CONNECTION_PORT);
			provider = new ProcessOverSocketStreamConnectionProvider(commands, workingDir.toOSString(),
					CONNECTION_PORT) {
			};
		} else {
			provider = new ProcessStreamConnectionProvider(commands, workingDir.toOSString()) {
			};
		}
	}

	@Override
	public void start() throws IOException {
		provider.start();
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