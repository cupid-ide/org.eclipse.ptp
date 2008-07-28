/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.ptp.rm.mpi.openmpi.core;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.ptp.rm.core.AbstractToolsPreferenceManager;


public class OpenMpi12PreferenceManager extends AbstractToolsPreferenceManager {
	private static final String LAUNCH_CMD = "mpirun -display-map ${mpiArgs} ${mpiEnvArgs} ${execPath}/${execName} ${progArgs}";
	private static final String DEBUG_CMD = "mpirun -display-map ${mpiArgs} ${mpiEnvArgs} ${debugExecName}/${debugExecName} ${debugArgs}";
	private static final String DISCOVER_CMD = "ompi_info -a --parseable";
	private static final String PATH = "";
	private static final boolean USE_DEFAULTS = true;	

	public static final String PREFIX = "openmpi-1.2-";

	public static Preferences getPreferences() {
		return Activator.getDefault().getPluginPreferences();
	}

	public static void savePreferences() {
		Activator.getDefault().savePluginPreferences();
	}

	public static void initializePreferences() {
		Preferences preferences = Activator.getDefault().getPluginPreferences();
		// Initialize only preferences as in OpenMpiResourceManagerConfiguration.OPENMPI_CAPABILITIES
		preferences.setDefault(PREFIX + PREFS_LAUNCH_CMD, LAUNCH_CMD);
		preferences.setDefault(PREFIX + PREFS_DEBUG_CMD, DEBUG_CMD);
		preferences.setDefault(PREFIX + PREFS_DISCOVER_CMD, DISCOVER_CMD);
		preferences.setDefault(PREFIX + PREFS_REMOTE_INSTALL_PATH, PATH);
		preferences.setDefault(PREFIX + PREFS_USE_DEFAULTS, USE_DEFAULTS);
	}
}
