/*******************************************************************************
 * Copyright (c) 2021 TODO and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     TODO - initial API and implementation
 ******************************************************************************/

package ca.mt.gore;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.eclipse.core.runtime.Platform;

/**
 * Helper class for resolving Go-related binaries.
 */
public class GoResolver {
	public static String resolveGoBinary(String program) {
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

}
