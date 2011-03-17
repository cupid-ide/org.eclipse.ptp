package org.eclipse.ptp.rm.jaxb.ui.util;

import java.util.List;
import java.util.Map;

import org.eclipse.ptp.rm.jaxb.core.data.GroupDescriptor;
import org.eclipse.ptp.rm.jaxb.core.data.TabController;
import org.eclipse.ptp.rm.jaxb.core.data.TabFolderDescriptor;
import org.eclipse.ptp.rm.jaxb.core.data.TabItemDescriptor;
import org.eclipse.ptp.rm.jaxb.core.data.Widget;
import org.eclipse.ptp.rm.jaxb.core.variables.RMVariableMap;
import org.eclipse.ptp.rm.jaxb.ui.IJAXBUINonNLSConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;

public class LaunchTabBuilder implements IJAXBUINonNLSConstants {

	private final TabController tabController;

	private final Map<Control, Widget> valueWidgets;
	private final Map<String, Boolean> selected;
	private final RMVariableMap rmVars;

	public LaunchTabBuilder(TabController tabController, RMVariableMap rmVars, Map<Control, Widget> valueWidgets,
			Map<String, Boolean> selected) {
		this.tabController = tabController;
		this.valueWidgets = valueWidgets;
		this.selected = selected;
		this.rmVars = rmVars;
	}

	public void build(Composite parent) throws Throwable {
		List<Object> top = tabController.getTabFolderOrGroupDescriptor();
		for (Object o : top) {
			if (o instanceof GroupDescriptor) {
				addGroup((GroupDescriptor) o, parent);
			} else if (o instanceof TabFolderDescriptor) {
				addFolder((TabFolderDescriptor) o, parent);
			}
		}
	}

	private void addFolder(TabFolderDescriptor fd, Composite parent) {
		TabFolder folder = new TabFolder(parent, WidgetBuilderUtils.getStyle(fd.getStyle()));
		List<TabItemDescriptor> items = fd.getItem();
		for (TabItemDescriptor i : items) {
			addItem(folder, i);
		}
	}

	private void addGroup(GroupDescriptor grp, Composite parent) {
		grp.getGridData();
		grp.getGridLayout();
		grp.getStyle();
		grp.getTitle();

	}

	private void addItem(TabFolder folder, TabItemDescriptor i) {
		// TODO Auto-generated method stub

	}

}