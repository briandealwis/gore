
package ca.mt.gore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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
		// https://github.com/golang/tools/blob/master/gopls/doc/settings.md
		// https://github.com/golang/tools/blob/master/gopls/doc/workspace.md
		JsonObject settings = new JsonObject();
		settings.addProperty("experimentalWorkspaceModule", true);
		settings.addProperty("experimentalDiagnosticsDelay", "100ms");
		JsonObject lenses = new JsonObject();
		lenses.addProperty("test", true);
		settings.add("codelenses", lenses);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		DefaultScope.INSTANCE.getNode(BUNDLEID).put(SETTINGS_JSON, gson.toJson(settings));
	}
}
