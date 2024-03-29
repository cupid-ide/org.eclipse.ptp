/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jakub Jurkiewicz <jakub.jurkiewicz@gmail.com> - Fix for Bug 174737
 *     [IDE] New Plug-in Project wizard status handling is inconsistent
 *     Oakland Software Incorporated (Francis Upton) <francisu@ieee.org>
 *		    Bug 224997 [Workbench] Impossible to copy project
 *******************************************************************************/
package org.eclipse.ptp.rdt.ui.wizards;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ptp.rdt.ui.wizards.RemoteProjectContentsLocationArea.IErrorMessageReporter;
import org.eclipse.remote.core.IRemoteConnection;
import org.eclipse.remote.core.IRemoteServices;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;

/**
 * Standard main page for a wizard that is creates a project resource.
 * <p>
 * This page may be used by clients as-is; it may be also be subclassed to suit.
 * </p>
 * <p>
 * Example usage:
 * 
 * <pre>
 * mainPage = new NewRemoteProjectCreationPage(&quot;basicNewProjectPage&quot;);
 * mainPage.setTitle(&quot;Project&quot;);
 * mainPage.setDescription(&quot;Create a new project resource.&quot;);
 * </pre>
 * 
 * </p>
 * 
 * @since 2.0
 */
public class NewRemoteProjectCreationPage extends WizardNewProjectCreationPage {

	// initial value stores
	private String initialProjectFieldValue;

	// widgets
	private Text projectNameField;

	private final Listener nameModifyListener = new Listener() {
		public void handleEvent(Event e) {
			setLocationForSelection();
			boolean valid = validatePage();
			setPageComplete(valid);

		}
	};

	private RemoteProjectContentsLocationArea locationArea;

	// private WorkingSetGroup workingSetGroup;

	// constants
	private static final int SIZING_TEXT_FIELD_WIDTH = 250;

	/**
	 * Creates a new project creation wizard page.
	 * 
	 * @param pageName
	 *            the name of this page
	 */
	public NewRemoteProjectCreationPage(String pageName) {
		super(pageName);
		setPageComplete(false);
	}

	/**
	 * Creates a new project creation wizard page.
	 * 
	 * @param pageName
	 * @param selection
	 * @param workingSetTypes
	 * 
	 * @deprecated default placement of the working set group has been removed.
	 *             If you wish to use the working set block please call
	 *             {@link #createWorkingSetGroup(Composite, IStructuredSelection, String[])}
	 *             in your overridden {@link #createControl(Composite)}
	 *             implementation.
	 * @since 3.4
	 */
	@Deprecated
	public NewRemoteProjectCreationPage(String pageName, IStructuredSelection selection, String[] workingSetTypes) {
		this(pageName);
	}

	/**
	 * (non-Javadoc) Method declared on IDialogPage.
	 */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);

		initializeDialogUnits(parent);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IIDEHelpContextIds.NEW_PROJECT_WIZARD_PAGE);

		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createProjectNameGroup(composite);
		locationArea = new RemoteProjectContentsLocationArea(getErrorReporter(), composite, null);
		if (initialProjectFieldValue != null) {
			locationArea.updateProjectName(initialProjectFieldValue);
		}

		// Scale the buttons based on the rest of the dialog
		for (Button button : locationArea.getButtons()) {
			setButtonLayoutData(button);
		}

		setPageComplete(validatePage());
		// Show description on opening
		setErrorMessage(null);
		setMessage(null);
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}

	/**
	 * Get an error reporter for the receiver.
	 * 
	 * @return IErrorMessageReporter
	 */
	private IErrorMessageReporter getErrorReporter() {
		return new IErrorMessageReporter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.ui.internal.ide.dialogs.ProjectContentsLocationArea
			 * .IErrorMessageReporter#reportError(java.lang.String)
			 */
			public void reportError(String errorMessage, boolean infoOnly) {
				if (infoOnly) {
					setMessage(errorMessage, IStatus.INFO);
					setErrorMessage(null);
				} else
					setErrorMessage(errorMessage);
				boolean valid = errorMessage == null;
				if (valid) {
					valid = validatePage();
				}

				setPageComplete(valid);
			}
		};
	}

	/**
	 * Creates the project name specification controls.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private final void createProjectNameGroup(Composite parent) {
		// project specification group
		Composite projectGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		projectGroup.setLayout(layout);
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// new project label
		Label projectLabel = new Label(projectGroup, SWT.NONE);
		projectLabel.setText(IDEWorkbenchMessages.WizardNewProjectCreationPage_nameLabel);
		projectLabel.setFont(parent.getFont());

		// new project name entry field
		projectNameField = new Text(projectGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		projectNameField.setLayoutData(data);
		projectNameField.setFont(parent.getFont());

		// Set the initial value first before listener
		// to avoid handling an event during the creation.
		if (initialProjectFieldValue != null) {
			projectNameField.setText(initialProjectFieldValue);
		}
		projectNameField.addListener(SWT.Modify, nameModifyListener);
	}

	/**
	 * Returns the current project location path as entered by the user, or its
	 * anticipated initial value. Note that if the default has been returned the
	 * path in a project description used to create a project should not be set.
	 * 
	 * @return the project location path or its anticipated initial value.
	 */
	@Override
	public IPath getLocationPath() {
		return new Path(locationArea.getProjectLocation());
	}

	/**
	 * Returns the current project location URI as entered by the user, or
	 * <code>null</code> if a valid project location has not been entered.
	 * 
	 * @return the project location URI, or <code>null</code>
	 * @since 3.2
	 */
	@Override
	public URI getLocationURI() {
		return locationArea.getProjectLocationURI();
	}

	/**
	 * Returns the current project name as entered by the user, or its
	 * anticipated initial value.
	 * 
	 * @return the project name, its anticipated initial value, or
	 *         <code>null</code> if no project name is known
	 */
	@Override
	public String getProjectName() {
		if (projectNameField == null) {
			return initialProjectFieldValue;
		}

		return getProjectNameFieldValue();
	}

	/**
	 * Returns the value of the project name field with leading and trailing
	 * spaces removed.
	 * 
	 * @return the project name in the field
	 */
	private String getProjectNameFieldValue() {
		if (projectNameField == null) {
			return ""; //$NON-NLS-1$
		}

		return projectNameField.getText().trim();
	}

	/**
	 * @since 6.0
	 */
	public IRemoteConnection getRemoteConnection() {
		return locationArea.getRemoteConnection();
	}

	/**
	 * @since 6.0
	 */
	public IRemoteServices getRemoteServices() {
		return locationArea.getRemoteServices();
	}

	/**
	 * Sets the initial project name that this page will use when created. The
	 * name is ignored if the createControl(Composite) method has already been
	 * called. Leading and trailing spaces in the name are ignored. Providing
	 * the name of an existing project will not necessarily cause the wizard to
	 * warn the user. Callers of this method should first check if the project
	 * name passed already exists in the workspace.
	 * 
	 * @param name
	 *            initial project name for this page
	 * 
	 * @see IWorkspace#validateName(String, int)
	 * 
	 */
	@Override
	public void setInitialProjectName(String name) {
		if (name == null) {
			initialProjectFieldValue = null;
		} else {
			initialProjectFieldValue = name.trim();
			if (locationArea != null) {
				locationArea.updateProjectName(name.trim());
			}
		}
	}

	/**
	 * Set the location to the default location if we are set to useDefaults.
	 */
	private void setLocationForSelection() {
		locationArea.updateProjectName(getProjectNameFieldValue());
	}

	/**
	 * Returns whether this page's controls currently all contain valid values.
	 * 
	 * @return <code>true</code> if all controls are valid, and
	 *         <code>false</code> if at least one is invalid
	 */
	@Override
	protected boolean validatePage() {
		IWorkspace workspace = IDEWorkbenchPlugin.getPluginWorkspace();

		String projectFieldContents = getProjectNameFieldValue();
		if (projectFieldContents.equals("")) { //$NON-NLS-1$
			setErrorMessage(null);
			setMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectNameEmpty);
			return false;
		}

		IStatus nameStatus = workspace.validateName(projectFieldContents, IResource.PROJECT);
		if (!nameStatus.isOK()) {
			setErrorMessage(nameStatus.getMessage());
			return false;
		}

		IProject handle = getProjectHandle();
		if (handle.exists()) {
			setErrorMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectExistsMessage);
			return false;
		}

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectNameFieldValue());
		locationArea.setExistingProject(project);

		String validLocationMessage = locationArea.checkValidLocation();
		if (validLocationMessage != null) { // there is no destination location
											// given
			setErrorMessage(validLocationMessage);
			return false;
		}

		setErrorMessage(null);
		setMessage(null);
		return true;
	}

	/*
	 * see @DialogPage.setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		getControl().setVisible(visible);
		if (visible) {
			projectNameField.setFocus();
		}
	}

	/**
	 * Returns the useDefaults.
	 * 
	 * @return boolean
	 */
	@Override
	public boolean useDefaults() {
		return locationArea.isDefault();
	}
}
