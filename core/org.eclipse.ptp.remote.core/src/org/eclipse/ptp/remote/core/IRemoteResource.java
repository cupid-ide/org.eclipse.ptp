/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.ptp.remote.core;

import java.net.URI;

import org.eclipse.core.resources.IResource;

/**
 * Interface to a remote resource. There are currently two types of remote resources: fully remote and synchronized. This interface
 * provides a common mechanism for accessing resource information from either type.
 * 
 * Usage:
 * 
 * <code>
 * 	IRemoteResource remoteRes = (IRemoteResource)resource.getAdapter(IRemoteResource.class);
 * 	if (remoteRes != null) {
 * 		URI location = remoteRes.getDefaultLocationURI();
 * 		...
 * 	}
 * </code>
 * 
 * @author greg
 * 
 */
public interface IRemoteResource {
	/**
	 * Get the active location URI of the resource in the remote project. Returns null if the URI can't be obtained (@see
	 * {@link IResource#getLocationURI()}).
	 * 
	 * For fully remote projects, this is just the URI of the remote resource. For synchronized projects, this is the URI of the
	 * resource from the active synchronization target.
	 * 
	 * @return URI or null if URI can't be obtained
	 */
	public URI getActiveLocationURI();

	/**
	 * Get the platform resource corresponding to the remote resource
	 * 
	 * @return IResource
	 */
	public IResource getResource();

	/**
	 * Set the platform resource
	 * 
	 * @param resource
	 *            platform resource corresponding to this remote resource
	 */
	public void setResource(IResource resource);
}