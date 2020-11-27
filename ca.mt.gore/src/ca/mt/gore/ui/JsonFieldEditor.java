/*******************************************************************************
 * Copyright (c) 2020 Google LLC and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Google LLC - initial API and implementation
 ******************************************************************************/

package ca.mt.gore.ui;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * Field editor with JSON validation.
 */
public class JsonFieldEditor extends StringFieldEditor {

	/**
	 * @param name                  the preference name
	 * @param label                 the label for the string
	 * @param parefieldEditorParent parent
	 */
	public JsonFieldEditor(String name, String label, Composite fieldEditorParent) {
		super(name, label, UNLIMITED, 7, VALIDATE_ON_KEY_STROKE, fieldEditorParent);
		setEmptyStringAllowed(true);
	}

	@Override
	protected boolean doCheckState() {
		String s = getStringValue();
		if (Strings.isNullOrEmpty(s)) {
			return true;
		}
		try {
			new Gson().fromJson(s, JsonObject.class);
		} catch (JsonSyntaxException e) {
			setErrorMessage(e.getMessage());
			return false;
		}
		return true;
	}

}
