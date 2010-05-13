/*******************************************************************************
 * Copyright (c) 2009, 2010 University of Utah School of Computing
 * 50 S Central Campus Dr. 3190 Salt Lake City, UT 84112
 * http://www.cs.utah.edu/formal_verification/
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alan Humphrey - Initial API and implementation
 *    Christopher Derrick - Initial API and implementation
 *    Prof. Ganesh Gopalakrishnan - Project Advisor
 *******************************************************************************/

package org.eclipse.ptp.gem.popup.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ptp.gem.messages.Messages;
import org.eclipse.ptp.gem.util.GemUtilities;
import org.eclipse.ptp.gem.views.GemAnalyzer;
import org.eclipse.ptp.gem.views.GemConsole;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class AnalyzerPopUpAction implements IObjectActionDelegate {

	private IStructuredSelection selection;

	/**
	 * Constructor.
	 */
	public AnalyzerPopUpAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if (this.selection == null) {
			return;
		}

		IFile f = (IFile) this.selection.getFirstElement();
		IPath s = f.getLocation();
		String sourceFilePath = s.toPortableString();

		// Make sure the file selection is valid
		if (this.selection.toString().equals("<empty selection>")) { //$NON-NLS-1$
			GemUtilities.showErrorDialog(Messages.AnalyzerPopUpAction_0,
					Messages.AnalyzerPopUpAction_1);
		} else {
			String id = action.getId();
			GemUtilities.saveLastFile(sourceFilePath);

			// Check if the log file exists
			String logFilePath = GemUtilities.getLogFile(sourceFilePath);

			// Find the active page
			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();

			// Open the Console View
			try {
				page.showView(GemConsole.ID);
			} catch (PartInitException pie) {
				GemUtilities.showExceptionDialog(
						Messages.AnalyzerPopUpAction_2, pie);
				GemUtilities.logError(Messages.AnalyzerPopUpAction_2, pie);
			}

			// Open the Analyzer View
			try {
				page.showView(GemAnalyzer.ID);
				boolean isValidSourceFile = id
						.equals("org.eclipse.ptp.gem.analyzerPopupC")  //$NON-NLS-1$
						|| id.equals("org.eclipse.ptp.gem.analyzerPopupCpp") //$NON-NLS-1$
						|| id.equals("org.eclipse.ptp.gem.analyzerPopupC++") //$NON-NLS-1$
						|| id.equals("org.eclipse.ptp.gem.analyzerPopupCp") //$NON-NLS-1$
						|| id.equals("org.eclipse.ptp.gem.analyzerPopupCc"); //$NON-NLS-1$
				GemUtilities.activateAnalyzer(sourceFilePath, logFilePath,
						isValidSourceFile,
						id.equals("org.eclipse.ptp.gem.executablePopup")); //$NON-NLS-1$
			} catch (PartInitException pie) {
				GemUtilities.showExceptionDialog(
						Messages.AnalyzerPopUpAction_2, pie);
				GemUtilities.logError(Messages.AnalyzerPopUpAction_2, pie);
			}
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		} else {
			this.selection = null;
		}
	}

}