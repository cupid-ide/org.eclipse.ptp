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
package org.eclipse.ptp.debug.external.core.cdi.model;

import java.math.BigInteger;
import org.eclipse.ptp.debug.core.aif.AIFException;
import org.eclipse.ptp.debug.core.aif.IAIFValue;
import org.eclipse.ptp.debug.core.cdi.PCDIException;
import org.eclipse.ptp.debug.core.cdi.model.IPCDIArgument;
import org.eclipse.ptp.debug.core.cdi.model.IPCDIArgumentDescriptor;
import org.eclipse.ptp.debug.core.cdi.model.IPCDILocalVariable;
import org.eclipse.ptp.debug.core.cdi.model.IPCDILocalVariableDescriptor;
import org.eclipse.ptp.debug.core.cdi.model.IPCDILocator;
import org.eclipse.ptp.debug.core.cdi.model.IPCDIStackFrame;
import org.eclipse.ptp.debug.core.cdi.model.IPCDIThread;
import org.eclipse.ptp.debug.external.core.cdi.Locator;
import org.eclipse.ptp.debug.external.core.cdi.Session;
import org.eclipse.ptp.debug.external.core.cdi.VariableManager;
import org.eclipse.ptp.debug.external.core.cdi.model.variable.ArgumentDescriptor;
import org.eclipse.ptp.debug.external.core.cdi.model.variable.LocalVariableDescriptor;
import org.eclipse.ptp.debug.external.core.commands.GetAIFCommand;
import org.eclipse.ptp.debug.external.core.commands.StepFinishCommand;

/**
 * @author Clement chu
 *
 */
public class StackFrame extends PObject implements IPCDIStackFrame {
	IPCDIThread pthread;
	IPCDIArgumentDescriptor[] argDescs;
	IPCDILocalVariableDescriptor[] localDescs;
	Locator fLocator;
	int level = -1;
	BigInteger addr = BigInteger.ZERO;
	String file = "";
	int line = -1;
	String func = "";

	public StackFrame(Target target, int level, String file, String func, int line, BigInteger addr) {
		super(target);
		this.level = level;
		this.addr = addr;
		this.file = file;
		this.line = line;
		this.func = func;
	}
	public IPCDIThread getThread() {
		return pthread;
	}
	public IPCDIArgumentDescriptor[] getArgumentDescriptors() throws PCDIException {
		if (argDescs == null) {
			Session session = (Session)getTarget().getSession();
			VariableManager mgr = session.getVariableManager();
			argDescs = mgr.getArgumentDescriptors(this);
		}
		else {
			Target target = (Target)getTarget();
			for (int i=0; i<argDescs.length; i++) {
				ArgumentDescriptor argDesc = (ArgumentDescriptor)argDescs[i];
				GetAIFCommand command = new GetAIFCommand(target.getTask(), argDesc.getQualifiedName());
				target.getDebugger().postCommand(command);
				argDesc.setAIF(command.getAIF());
			}
		}
		return argDescs;
	}
	public IPCDILocalVariableDescriptor[] getLocalVariableDescriptors() throws PCDIException {
		if (localDescs == null) {
			Session session = (Session)getTarget().getSession();
			VariableManager mgr = session.getVariableManager();
			localDescs = mgr.getLocalVariableDescriptors(this);
		}
		else {
			Target target = (Target)getTarget();
			for (int i=0; i<localDescs.length; i++) {
				LocalVariableDescriptor localDesc = (LocalVariableDescriptor)localDescs[i];
				GetAIFCommand command = new GetAIFCommand(target.getTask(), localDesc.getQualifiedName());
				target.getDebugger().postCommand(command);
				localDesc.setAIF(command.getAIF());
			}
		}
		return localDescs;
	}
	public IPCDIArgument createArgument(IPCDIArgumentDescriptor varDesc) throws PCDIException {
		if (varDesc instanceof ArgumentDescriptor) {
			Session session = (Session)getTarget().getSession();
			VariableManager mgr = session.getVariableManager();
			return mgr.createArgument((ArgumentDescriptor)varDesc);
		}
		return null;
	}
	public IPCDILocalVariable createLocalVariable(IPCDILocalVariableDescriptor varDesc) throws PCDIException {
		if (varDesc instanceof ArgumentDescriptor) {
			return createArgument((IPCDIArgumentDescriptor)varDesc);
		} else if (varDesc instanceof LocalVariableDescriptor) {
			Session session = (Session)getTarget().getSession();
			VariableManager mgr = session.getVariableManager();
			return mgr.createLocalVariable((LocalVariableDescriptor)varDesc);			
		}
		return null;
	}
	public IPCDILocator getLocator() {
		if (fLocator == null) {
			fLocator = new Locator(getFile(), getFunction(), getLine(), addr);
		}
		return fLocator;
		//return new Locator("", "", 0, bigAddr);
	}	
	public boolean equals(IPCDIStackFrame stackframe) {
		if (stackframe instanceof StackFrame) {
			StackFrame stack = (StackFrame)stackframe;
			boolean equal =  pthread != null && pthread.equals(stack.getThread()) && getLevel() == stack.getLevel();
			if (equal) {
				IPCDILocator otherLocator = stack.getLocator();
				IPCDILocator myLocator = getLocator();
				if (Locator.equalString(myLocator.getFile(), otherLocator.getFile())) {
					if (Locator.equalString(myLocator.getFunction(), otherLocator.getFunction())) {
						return true;
					}
				}
			}
		}
		return super.equals(stackframe);
	}
	public void stepReturn() throws PCDIException {
		try {
			finish();
		} catch (PCDIException e) {
			throw new PCDIException(e.getMessage());
		}
	}
	public void stepReturn(IAIFValue value) throws PCDIException {
		try {
			execReturn(value.getValueString());
		} catch (AIFException e) {
			throw new PCDIException(e.getMessage());
		}
	}
	protected void finish() throws PCDIException {
		((Thread)getThread()).setCurrentStackFrame(this, false);
		Target target = (Target)getTarget();
		target.getDebugger().postCommand(new StepFinishCommand(target.getTask()));
	}	
	protected void execReturn(String value) throws PCDIException {
		((Thread)getThread()).setCurrentStackFrame(this, false);
		Target target = (Target)getTarget();
		target.getDebugger().postCommand(new StepFinishCommand(target.getTask()));
	}

	public int getLevel() {
		return level;
	}
	public BigInteger getAddress() {
		return addr;
	}
	public String getFile() {
		return file;
	}
	public int getLine() {
		return line;
	}
	public String getFunction() {
		return func;
	}
	
	public void setThread(IPCDIThread thread) {
		this.pthread = thread;
	}
	public void setLevel(int level) {
		this.level = level;
	}
}
