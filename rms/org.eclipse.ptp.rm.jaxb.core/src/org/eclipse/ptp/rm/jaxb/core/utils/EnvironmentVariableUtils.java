/*******************************************************************************
 * Copyright (c) 2011 University of Illinois All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html 
 * 	
 * Contributors: 
 * 	Albert L. Rossi - design and implementation
 ******************************************************************************/
package org.eclipse.ptp.rm.jaxb.core.utils;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ptp.rm.jaxb.core.IJAXBNonNLSConstants;
import org.eclipse.ptp.rm.jaxb.core.data.AttributeType;
import org.eclipse.ptp.rm.jaxb.core.data.NameValuePairType;
import org.eclipse.ptp.rm.jaxb.core.data.PropertyType;
import org.eclipse.ptp.rm.jaxb.core.variables.RMVariableMap;

/**
 * Convenience methods for handling name-value pair objects (the JAXB
 * NameValuePair).
 * 
 * @author arossi
 * 
 */
public class EnvironmentVariableUtils implements IJAXBNonNLSConstants {

	private EnvironmentVariableUtils() {
	}

	/**
	 * Add variable to env after resolving against the resource-manager
	 * environment (used by the CommandJob).
	 * 
	 * @param uuid
	 *            an internal or resource-specific job id.
	 * @param var
	 *            name-value pair
	 * @param env
	 *            the environment map to which to add the pair
	 * @param map
	 *            resource manager active environment map
	 */
	public static void addVariable(String uuid, NameValuePairType var, Map<String, String> env, RMVariableMap map) {
		String key = var.getValue();
		String value = getValue(uuid, key, map);
		if (value != null && !ZEROSTR.equals(value)) {
			env.put(var.getName(), value);
		}
	}

	/**
	 * Add variable to buffer after resolving against the resource-manager
	 * environment (used by the ScriptHandler).
	 * 
	 * @param name
	 *            name of variable
	 * @param value
	 *            value of variable
	 * @param directive
	 *            first line of shell script
	 * @param buffer
	 *            running contents of the script being generated
	 * 
	 * @see org.eclipse.ptp.rm.jaxb.core.runnable.ScriptHandler#composeScript(IProgressMonitor)
	 */
	public static void addVariable(String name, String value, String directive, StringBuffer buffer) {
		if (value != null && !ZEROSTR.equals(value)) {
			if (SETENV.equals(getSyntax(directive))) {
				setenv(name, value, buffer);
			} else if (EXPORT.equals(getSyntax(directive))) {
				export(name, value, buffer);
			}
		}
	}

	/**
	 * For debugging purposes.
	 * 
	 * @param map
	 * @return string of n=v lines
	 */
	public static String toString(RMVariableMap map) {
		StringBuffer b = new StringBuffer();
		for (Object o : map.getVariables().values()) {
			if (o instanceof PropertyType) {
				PropertyType p = (PropertyType) o;
				b.append(p.getName()).append(EQ).append(p.getValue()).append(LINE_SEP);
			} else if (o instanceof AttributeType) {
				AttributeType a = (AttributeType) o;
				b.append(a.getName()).append(EQ).append(a.getValue()).append(LINE_SEP);
			}
		}

		return b.toString();
	}

	/**
	 * Construct the bash-shell export command.
	 * 
	 * @param name
	 *            of variable
	 * @param value
	 *            of variable
	 * @param buffer
	 *            running contents of the script being generated
	 */
	private static void export(String name, String value, StringBuffer buffer) {
		buffer.append(EXPORT).append(SP).append(name).append(EQ).append(QT).append(value).append(QT).append(REMOTE_LINE_SEP);
	}

	/**
	 * Determines what syntax to use for script environment variable definition
	 * based on the first line of the script.
	 * 
	 * @param directive
	 *            first line of shell script
	 * @return syntax type.
	 */
	private static String getSyntax(String directive) {
		if (directive.indexOf(CSH) >= 0) {
			return SETENV;
		}
		return EXPORT;
	}

	/**
	 * Convert the name to a resource-manager map resolver reference, and
	 * resolve.
	 * 
	 * @param uuid
	 *            an internal or resource-specific job id.
	 * @param key
	 *            the attribute or property name
	 * @param map
	 *            resource manager active environment map
	 * @return the resolved value
	 */
	private static String getValue(String uuid, String key, RMVariableMap map) {
		String name = OPENVRM + key + PD + VALUE + CLOSVAL;
		return map.getString(uuid, name);
	}

	/**
	 * Construct the c-shell setenv command.
	 * 
	 * @param name
	 *            of variable
	 * @param value
	 *            of variable
	 * @param buffer
	 *            running contents of the script being generated
	 */
	private static void setenv(String name, String value, StringBuffer buffer) {
		buffer.append(SETENV).append(SP).append(name).append(SP).append(QT).append(value).append(QT).append(REMOTE_LINE_SEP);
	}
}
