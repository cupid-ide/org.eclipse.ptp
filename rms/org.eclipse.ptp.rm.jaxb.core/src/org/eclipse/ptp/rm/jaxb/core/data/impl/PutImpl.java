package org.eclipse.ptp.rm.jaxb.core.data.impl;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.ptp.rm.jaxb.core.data.Put;
import org.eclipse.ptp.rm.jaxb.core.messages.Messages;

public class PutImpl extends AbstractRangeAssign {

	private final Range keys;

	public PutImpl(Put put) {
		this.field = put.getField();
		String rString = put.getKeyGroups();
		if (rString == null) {
			rString = put.getKeyIndices();
		}
		keys = new Range(rString);
		rString = put.getValueGroups();
		if (rString == null) {
			rString = put.getValueIndices();
		}
		range = new Range(rString);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object[] getValue(Object previous, String[] values) {
		if (values == null) {
			return new Object[] { previous };
		}
		keys.setLen(values.length);

		List<Object> foundKeys = keys.findInRange(values);
		if (foundKeys.isEmpty()) {
			return new Object[] { previous };
		}

		range.setLen(values.length);
		List<Object> foundValues = range.findInRange(values);
		int sz = foundKeys.size();
		if (sz != foundValues.size()) {
			throw new IllegalStateException(Messages.StreamParserInconsistentMapValues + sz + CM + foundValues.size());
		}

		Map<String, String> map = null;
		if (previous != null && previous instanceof Map<?, ?>) {
			map = (Map<String, String>) previous;
		} else {
			map = new TreeMap<String, String>();
		}

		for (int i = 0; i < sz; i++) {
			map.put(foundKeys.get(i).toString(), (String) foundValues.get(i));
		}

		return new Object[] { map };
	}
}