
package ca.mt.gore.ui;

import ca.mt.gore.PreferencesInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class GoplsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private IWorkbench workbench;
	
	public GoplsPreferencePage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		this.workbench = workbench;
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, PreferencesInitializer.BUNDLEID);
	}

	@Override
	protected void createFieldEditors() {
//		addField(new LabelFieldEditor(
//				"See the <a href=\"https://github.com/golang/tools/blob/master/gopls/doc/settings.md\">Gopls Settings</a> for available options."));
		addField(
				new JsonFieldEditor(PreferencesInitializer.SETTINGS_JSON, "Settings (JSON)",
				this.getFieldEditorParent()));
	}
}
