/*******************************************************************************
 * Copyright (c) 2005 The Regents of the University of California. 
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
package org.eclipse.ptp.debug.internal.ui.views.variable;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ptp.debug.ui.PJobVariableManager;
import org.eclipse.ptp.ui.model.IElementHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author Clement chu
 */
public class PVariableDialog extends Dialog {
	protected Text nameText = null;
	protected Table setTable = null;
	protected Table varTable = null;
	protected Button checkBtn = null;
	protected PVariableView view = null;

	public PVariableDialog(PVariableView view) {
		super(view.getSite().getShell());
		this.view = view;
	}
	public void configureShell(Shell newShell) {
		newShell.setText(PVariableMessages.getString("PVariablesDialog.title"));
		super.configureShell(newShell);
	}
	/** Get OK Button
     * @return
     */
    public Button getOkButton() {
        return getButton(IDialogConstants.OK_ID);
    }
	private void createOthersSection(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.verticalSpacing = 25;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
		
		checkBtn = new Button(comp, SWT.CHECK);
		checkBtn.setText(PVariableMessages.getString("PVariablesDialog.checkBtn"));
		checkBtn.setSelection(true);
	}

    /** Display the available variable in debugger
	 * @param parent
	 */
	private void createVarSection(Composite parent) {
		Group aGroup = new Group(parent, SWT.BORDER);
		aGroup.setText(PVariableMessages.getString("PVariablesDialog.varSection"));
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		aGroup.setLayout(layout);
		aGroup.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

		new Label(aGroup, SWT.NONE).setText(PVariableMessages.getString("PVariablesDialog.availVar"));
		varTable = new Table(aGroup, SWT.CHECK | SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.V_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		gd.verticalSpan = 30;
		varTable.setLayoutData(gd);
		varTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateButtons();
			}
		});

		Composite comp2 = new Composite(aGroup, SWT.NONE);
		comp2.setLayout(new GridLayout(2, false));
		comp2.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

		new Label(comp2, SWT.NONE).setText(PVariableMessages.getString("PVariablesDialog.custVar"));
		nameText = new Text(comp2, SWT.BORDER | SWT.NONE);
		nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				updateButtons();
			}
		});
	}
	/** Create section for adding new variable
	 * @param parent
	 */
	private void createSetSection(Composite parent) {
		Group aGroup = new Group(parent, SWT.BORDER);
		aGroup.setText(PVariableMessages.getString("PVariablesDialog.setSection"));
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		aGroup.setLayout(layout);
		aGroup.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
		
		setTable = new Table(aGroup, SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		gd.verticalSpan = 25;
		setTable.setLayoutData(gd);
		setTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem item = (TableItem)e.item;
				if (item.getChecked()) {
					if (item.getText().equals(IElementHandler.SET_ROOT_ID)) {
						setCheckTableItems(false);
					}
					else {
						//uncheck root if checked others
						setTable.getItem(0).setChecked(false);
					}
				}
				updateButtons();
			}	
		});
	}
	/** Set Set table items checked or not
	 * @param checked check or not
	 */
	private void setCheckTableItems(boolean checked) {
		TableItem[] items = setTable.getItems();
		for (int i=1; i<items.length; i++) {
			items[i].setChecked(checked);
		}
	}
	private boolean anyTableItemChecked(Table table) {
		TableItem[] items = table.getItems();
		for (int i=0; i<items.length; i++) {
			if (items[i].getChecked())
				return true;
		}
		return false;
	}
	private void updateButtons() {
		boolean enabled = true;
		
		if (!anyTableItemChecked(varTable)) {
			if (nameText.getText() == null || nameText.getText().length() == 0) {
				enabled = false;
			}
			else if (!anyTableItemChecked(setTable)) {
				enabled = false;
			}
		}
		getOkButton().setEnabled(enabled);
	}
	/** Initialize the content in new section and variable section
	 * 
	 */
	protected void initContent() {
		String[] sets = view.getUIManager().getSets(view.getUIManager().getCurrentJobId());
		TableItem item = null;		
		for (int i=0; i<sets.length; i++) {
			item = new TableItem(setTable, SWT.NONE);
			item.setText(sets[i]);
		}
	}
	/** Create vertical space
	 * @param parent
	 * @param space
	 */
	protected void createVerticalSpan(Composite parent, int space) {
		Label label = new Label(parent, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.verticalSpan = space;
		label.setLayoutData(gd);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	public Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);
		getOkButton().setEnabled(false);
		return control;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 2;
		layout.marginWidth = 2;
		layout.marginTop = 5;
		layout.verticalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		applyDialogFont(composite);

		createVarSection(composite);
		createVerticalSpan(composite, 2);
		createSetSection(composite);
		createOthersSection(composite);

		initContent();
		return composite;
	}
	protected String[] getSelectedSets() {
		TableItem[] items = setTable.getSelection();
		String[] sets = new String[items.length];
		for (int i=0; i<items.length; i++) {
			sets[i] = items[i].getText();
		}
		return sets;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		PJobVariableManager jobMgr = view.getUIManager().getJobVariableManager();
		if (varTable.getSelectionCount() > 0) {
			jobMgr.addJobVariable(view.getUIManager().getCurrentJob(), getSelectedSets(), varTable.getSelection()[0].getText(), checkBtn.getSelection());
		}
		else {
			jobMgr.addJobVariable(view.getUIManager().getCurrentJob(), getSelectedSets(), nameText.getText(), checkBtn.getSelection());
		}
		super.okPressed();
	}
}
