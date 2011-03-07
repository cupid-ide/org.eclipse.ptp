package org.eclipse.ptp.rm.jaxb.core.data.impl;

import java.util.List;

import org.eclipse.ptp.rm.jaxb.core.data.Append;

public class AppendImpl extends AbstractRangeAssign {

	private final String separator;
	private final String endTag;
	private final String startTag;

	public AppendImpl(Append append) {
		this.field = append.getField();
		separator = append.getSeparator();
		startTag = append.getStartTag();
		endTag = append.getEndTag();
		String rString = append.getGroups();
		if (rString == null) {
			rString = append.getIndices();
		}
		range = new Range(rString);
	}

	@Override
	protected Object[] getValue(Object previous, String[] values) {
		if (values == null) {
			return new Object[] { previous };
		}
		range.setLen(values.length);
		List<Object> found = range.findInRange(values);
		if (found.isEmpty()) {
			return new Object[] { previous };
		}
		StringBuffer buffer = new StringBuffer();
		if (previous != null && previous instanceof String) {
			buffer.append(previous);
			if (null != startTag) {
				buffer.append(startTag);
			} else if (null != separator) {
				buffer.append(separator);
			}
		} else if (null != startTag) {
			buffer.append(startTag);
		}
		buffer.append(found.get(0).toString());
		int sz = found.size();
		for (int i = 1; i < sz; i++) {
			if (separator != null) {
				buffer.append(separator);
			}
			buffer.append(found.get(i));
		}
		if (null != endTag) {
			buffer.append(endTag);
		}
		return new Object[] { buffer.toString() };
	}
}