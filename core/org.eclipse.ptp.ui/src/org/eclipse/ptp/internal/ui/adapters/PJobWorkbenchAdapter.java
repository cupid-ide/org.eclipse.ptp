/*******************************************************************************
 * Copyright (c) 2007 The Regents of the University of California. 
 * This material was produced under U.S. Government contract W-7405-ENG-36 
 * for Los Alamos National Laboratory, which is operated by the University 
 * of California for the U.S. Department of Energy. The U.S. Government has 
 * rights to use, reproduce, and distribute this software. NEITHER THE 
 * GOVERNMENT NOR THE UNIVERSITY MAKES ANY WARRANTY, EXPRESS OR IMPLIED, OR 
 * ASSUMES ANY LIABILITY FOR THE USE OF THIS SOFTWARE. If software is modified 
 * to produce derivative works, such modified software should be clearly marked, 
 * so as not to confuse it with the version available from LANL.
 * 
 * Additionally, this program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * LA-CC 04-115
 *******************************************************************************/
package org.eclipse.ptp.internal.ui.adapters;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ptp.core.elements.IPJob;
import org.eclipse.ptp.internal.ui.ParallelImages;
import org.eclipse.ptp.utils.ui.ImageImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchAdapter;


public class PJobWorkbenchAdapter extends WorkbenchAdapter {
	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.WorkbenchAdapter#getImageDescriptor(java.lang.Object)
	 */
	@Override
	public ImageDescriptor getImageDescriptor(Object object) {
		IPJob job = (IPJob) object;
		final Image image = getImage(job);
		if (image != null) {
			return new ImageImageDescriptor(image);
		}
		return null;
	}

	/**
	 * @param job
	 * @return
	 */
	private Image getImage(IPJob job) {
		return ParallelImages.jobImages[job.getState().ordinal()][job.isDebug() ? 1 : 0];
	}

}
