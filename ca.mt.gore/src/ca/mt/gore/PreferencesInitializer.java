
package ca.mt.gore;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PreferencesInitializer extends AbstractPreferenceInitializer {
	public static final String BUNDLEID = "ca.mt.gore";
	public static final String SETTINGS_JSON = "settings.json";

	public static IPreferenceStore getPreferences() {
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, BUNDLEID);
	}

	@Override
	public void initializeDefaultPreferences() {
		DefaultScope.INSTANCE.getNode(BUNDLEID).put(SETTINGS_JSON,
				"{\n  \"experimentalWorkspaceModule\":true,\n  \"experimentalDiagnosticsDelay\": \"100ms\",\n  \"codelens\":{\"test\":true}\n}");
	}
}
