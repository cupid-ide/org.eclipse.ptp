/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.ptp.internal.remote.remotetools.ui;

import org.eclipse.ptp.internal.remote.remotetools.core.RemoteToolsServices;
import org.eclipse.remote.core.IRemoteServices;
import org.eclipse.remote.ui.IRemoteUIConnectionManager;
import org.eclipse.remote.ui.IRemoteUIFileManager;
import org.eclipse.remote.ui.IRemoteUIServices;

public class RemoteToolsUIServices implements IRemoteUIServices {
	private static RemoteToolsUIServices fInstance = null;

	/**
	 * Get shared instance of this class
	 * 
	 * @return instance
	 */
	public static RemoteToolsUIServices getInstance(IRemoteServices services) {
		if (fInstance == null && services instanceof RemoteToolsServices) {
			fInstance = new RemoteToolsUIServices((RemoteToolsServices) services);
		}
		return fInstance;
	}

	private final RemoteToolsServices fServices;

	private RemoteToolsUIServices(RemoteToolsServices services) {
		fServices = services;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.remote.ui.IRemoteUIServicesDescriptor#getId()
	 */
	public String getId() {
		return fServices.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.remote.ui.IRemoteUIServicesDescriptor#getName()
	 */
	public String getName() {
		return fServices.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.remote.ui.IRemoteUIServicesDescriptor#getUIConnectionManager()
	 */
	public IRemoteUIConnectionManager getUIConnectionManager() {
		return new RemoteToolsUIConnectionManager(fServices);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.remote.ui.IRemoteUIServicesDescriptor#getUIFileManager()
	 */
	public IRemoteUIFileManager getUIFileManager() {
		return new RemoteToolsUIFileManager();
	}
}
