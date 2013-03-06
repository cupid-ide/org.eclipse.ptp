/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.ptp.internal.rm.lml.monitor.ui.handlers;

import org.eclipse.ptp.core.jobs.IJobControl;

/**
 * Release a held job.
 */
public class ReleaseJobHandler extends AbstractControlHandler {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.internal.rm.lml.monitor.ui.handlers.AbstractControlHandler#getOperation()
	 */
	@Override
	protected String getOperation() {
		return IJobControl.RELEASE_OPERATION;
	}

}