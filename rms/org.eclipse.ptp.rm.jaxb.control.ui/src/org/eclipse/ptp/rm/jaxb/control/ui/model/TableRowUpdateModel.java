/*******************************************************************************
 * Copyright (c) 2011 University of Illinois All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html 
 * 	
 * Contributors: 
 * 	Albert L. Rossi - design and implementation
 ******************************************************************************/
package org.eclipse.ptp.rm.jaxb.control.ui.model;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.ptp.rm.jaxb.control.ui.JAXBControlUIConstants;
import org.eclipse.ptp.rm.jaxb.control.ui.handlers.ValueUpdateHandler;
import org.eclipse.ptp.rm.jaxb.core.data.AttributeType;

/**
 * Cell editor for Table Viewer.
 * 
 * @author arossi
 * 
 */
public class TableRowUpdateModel extends CellEditorUpdateModel {

	/**
	 * Cell editor model for Table row with underlying Property data.
	 * 
	 * @param name
	 *            of the model, which will correspond to the name of a Property
	 *            or Attribute the value is to be saved to
	 * @param linkUpdateTo
	 *            if a change in this property or attribute value overwrites
	 *            other property or attribute values
	 * @param handler
	 *            the handler for notifying other widgets to refresh their
	 *            values
	 * @param editor
	 *            the cell editor for the value cell
	 * @param items
	 *            if this is a combo editor, the selection items
	 * @param readOnly
	 *            if this is a text box, whether it is editable
	 */
	public TableRowUpdateModel(String name, List<String> linkUpdateTo, ValueUpdateHandler handler, CellEditor editor,
			String[] items, boolean readOnly) {
		super(name, linkUpdateTo, handler, editor, items, readOnly, JAXBControlUIConstants.ZEROSTR, JAXBControlUIConstants.ZEROSTR,
				JAXBControlUIConstants.ZEROSTR);
	}

	/**
	 * * Cell editor model for Table row with underlying Attribute data.
	 * 
	 * @param name
	 *            of the model, which will correspond to the name of a Property
	 *            or Attribute the value is to be saved to
	 * @param linkUpdateTo
	 *            if a change in this property or attribute value overwrites
	 *            other property or attribute values
	 * @param handler
	 *            the handler for notifying other widgets to refresh their
	 *            values
	 * @param editor
	 *            the cell editor for the value cell
	 * @param items
	 *            if this is a combo editor, the selection items
	 * @param readOnly
	 *            if this is a text box, whether it is editable
	 * @param data
	 *            the Attribute object
	 */
	public TableRowUpdateModel(String name, List<String> linkUpdateTo, ValueUpdateHandler handler, CellEditor editor,
			String[] items, boolean readOnly, AttributeType data) {
		super(name, linkUpdateTo, handler, editor, items, readOnly, data.getTooltip(), data.getDescription(), data.getStatus());
	}

	/*
	 * For a table row, the field to access on the model corresponds to the
	 * column name. If the item is not checked, an empty string is
	 * returned.(non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rm.jaxb.ui.IColumnViewerLabelSupport#getDisplayValue(
	 * java.lang.String)
	 */
	public String getDisplayValue(String columnName) {
		String displayValue = null;
		if (JAXBControlUIConstants.COLUMN_NAME.equals(columnName)) {
			displayValue = name;
		} else if (isChecked()) {
			if (JAXBControlUIConstants.COLUMN_DESC.equals(columnName)) {
				displayValue = description;
			} else if (JAXBControlUIConstants.COLUMN_DEFAULT.equals(columnName)) {
				displayValue = defaultValue;
			} else if (JAXBControlUIConstants.COLUMN_TYPE.equals(columnName)) {
				displayValue = getType();
			} else if (JAXBControlUIConstants.COLUMN_VALUE.equals(columnName)) {
				displayValue = getValueAsString();
			} else if (JAXBControlUIConstants.COLUMN_STATUS.equals(columnName)) {
				displayValue = status;
			}
		}
		if (displayValue == null) {
			return JAXBControlUIConstants.ZEROSTR;
		}
		return displayValue;
	}
}
