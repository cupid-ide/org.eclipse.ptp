/*******************************************************************************
 * Copyright (c) 2011 University of Illinois All rights reserved. This program
 * and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html 
 * 	
 * Contributors: 
 * 	Albert L. Rossi - design and implementation
 ******************************************************************************/
package org.eclipse.ptp.rm.jaxb.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ptp.core.IPTPLaunchConfigurationConstants;
import org.eclipse.ptp.core.util.CoreExceptionUtils;
import org.eclipse.ptp.remote.core.IRemoteConnection;
import org.eclipse.ptp.remote.core.RemoteServicesDelegate;
import org.eclipse.ptp.remote.core.exception.RemoteConnectionException;
import org.eclipse.ptp.rm.jaxb.control.internal.ICommandJob;
import org.eclipse.ptp.rm.jaxb.control.internal.ICommandJobStatus;
import org.eclipse.ptp.rm.jaxb.control.internal.ICommandJobStatusMap;
import org.eclipse.ptp.rm.jaxb.control.internal.messages.Messages;
import org.eclipse.ptp.rm.jaxb.control.internal.runnable.JobStatusMap;
import org.eclipse.ptp.rm.jaxb.control.internal.runnable.ManagedFilesJob;
import org.eclipse.ptp.rm.jaxb.control.internal.runnable.command.CommandJob;
import org.eclipse.ptp.rm.jaxb.control.internal.runnable.command.CommandJobStatus;
import org.eclipse.ptp.rm.jaxb.control.internal.utils.JobIdPinTable;
import org.eclipse.ptp.rm.jaxb.control.internal.variables.RMVariableMap;
import org.eclipse.ptp.rm.jaxb.control.runnable.ScriptHandler;
import org.eclipse.ptp.rm.jaxb.core.IJAXBResourceManager;
import org.eclipse.ptp.rm.jaxb.core.IJAXBResourceManagerConfiguration;
import org.eclipse.ptp.rm.jaxb.core.IJAXBResourceManagerControl;
import org.eclipse.ptp.rm.jaxb.core.IVariableMap;
import org.eclipse.ptp.rm.jaxb.core.data.AttributeType;
import org.eclipse.ptp.rm.jaxb.core.data.CommandType;
import org.eclipse.ptp.rm.jaxb.core.data.ControlType;
import org.eclipse.ptp.rm.jaxb.core.data.ManagedFileType;
import org.eclipse.ptp.rm.jaxb.core.data.ManagedFilesType;
import org.eclipse.ptp.rm.jaxb.core.data.PropertyType;
import org.eclipse.ptp.rm.jaxb.core.data.ScriptType;
import org.eclipse.ptp.rmsystem.AbstractResourceManagerConfiguration;
import org.eclipse.ptp.rmsystem.AbstractResourceManagerControl;
import org.eclipse.ptp.rmsystem.IJobStatus;
import org.eclipse.ptp.rmsystem.IResourceManager;

/**
 * The part of the JAXB resource manager responsible for handling job
 * submission, termination, suspension and resumption. Also provides on-demand
 * job status checking. <br>
 * <br>
 * The state maintained by the control is volatile (in-memory only). The control
 * is responsible for handing off to the caller status objects containing job
 * state, as well as means of acessing the process (if interactive) and the
 * standard out and error streams. When the job completes, these are eliminated
 * from its internal map.<br>
 * <br>
 * The logic of this manager is generic; the specific commands used, files
 * staged, and script constructed (if any) are all configured via the resource
 * manager XML.
 * 
 * @author arossi
 * 
 */
public final class JAXBResourceManagerControl extends AbstractResourceManagerControl implements IJAXBResourceManagerControl {

	private final IJAXBResourceManagerConfiguration config;

	private Map<String, String> launchEnv;
	private Map<String, ICommandJob> jobTable;
	private ICommandJobStatusMap jobStatusMap;
	private JobIdPinTable pinTable;
	private RMVariableMap rmVarMap;
	private ControlType controlData;
	private boolean appendLaunchEnv;

	/**
	 * @param jaxbServiceProvider
	 *            the configuration object containing resource manager specifics
	 */
	public JAXBResourceManagerControl(AbstractResourceManagerConfiguration jaxbServiceProvider) {
		super(jaxbServiceProvider);
		config = (IJAXBResourceManagerConfiguration) jaxbServiceProvider;
	}

	/**
	 * @return whether to append (true) the env passed in through the
	 *         LaunchConfiguration, or replace the current env with it.
	 */
	public boolean getAppendEnv() {
		return appendLaunchEnv;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rm.jaxb.core.IJAXBResourceManagerControl#getEnvironment()
	 */
	public IVariableMap getEnvironment() {
		return rmVarMap;
	}

	/**
	 * @return table of open remote processes
	 */
	public Map<String, ICommandJob> getJobTable() {
		return jobTable;
	}

	/**
	 * @return any environment variables passed in through the
	 *         LaunchConfiguration
	 */
	public Map<String, String> getLaunchEnv() {
		return launchEnv;
	}

	/**
	 * @param monitor
	 * @return wrapper object for remote services, connections and file managers
	 */
	public RemoteServicesDelegate getRemoteServicesDelegate(IProgressMonitor monitor) {
		return new RemoteServicesDelegate(config.getRemoteServicesId(), config.getConnectionName(), monitor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.rm.jaxb.core.IJAXBResourceManagerControl#getState()
	 */
	public String getState() {
		return getResourceManager().getState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rm.jaxb.core.IJAXBResourceManagerControl#getStatusMap()
	 */
	public ICommandJobStatusMap getStatusMap() {
		return jobStatusMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rm.jaxb.core.IJAXBResourceManagerControl#jobStateChanged
	 * (java.lang.String)
	 */
	public void jobStateChanged(String jobId) {
		((IJAXBResourceManager) getResourceManager()).fireJobChanged(jobId);
	}

	/*
	 * For termination, pause, hold, suspension and resume requests. Resets the
	 * environment, generates a uuid property; if the control request is
	 * termination, calls remove on the state map. (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rmsystem.AbstractResourceManagerControl#doControlJob(
	 * java.lang.String, java.lang.String,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void doControlJob(String jobId, String operation, IProgressMonitor monitor) throws CoreException {
		try {
			if (!resourceManagerIsActive()) {
				return;
			}

			if (monitor != null) {
				monitor.beginTask(operation, 10);
			}

			pinTable.pin(jobId);
			PropertyType p = new PropertyType();
			p.setVisible(false);
			p.setName(jobId);
			rmVarMap.put(jobId, p);

			worked(monitor, 3);
			doControlCommand(jobId, operation);

			worked(monitor, 4);
			rmVarMap.remove(jobId);

			if (TERMINATE_OPERATION.equals(operation)) {
				jobStatusMap.cancel(jobId);
			}

			worked(monitor, 3);
		} catch (CoreException ce) {
			getResourceManager().setState(IResourceManager.ERROR_STATE);
			throw ce;
		} finally {
			pinTable.release(jobId);
			worked(monitor, 0);
		}
	}

	@Override
	protected void doDispose() {
		// NOP for the moment
	}

	/*
	 * Used by the client to refresh status on demand. (non-Javadoc) Generates a
	 * jobId property; if the returned state is RUNNING, starts the proxy (a
	 * rentrant call to a started proxy does nothing); if COMPLETED, the status
	 * is removed from the map.
	 * 
	 * @see
	 * org.eclipse.ptp.rmsystem.AbstractResourceManagerControl#doGetJobStatus
	 * (java.lang.String)
	 */
	@Override
	protected IJobStatus doGetJobStatus(String jobId) throws CoreException {
		try {
			if (!resourceManagerIsActive()) {
				return new CommandJobStatus(getResourceManager().getUniqueName(), jobId, IJobStatus.COMPLETED, null, this);
			}
			pinTable.pin(jobId);

			/*
			 * First check to see when the last call was made; throttle requests
			 * coming in intervals less than
			 * ICommandJobStatus.UPDATE_REQUEST_INTERVAL
			 */
			ICommandJobStatus status = jobStatusMap.getStatus(jobId);
			if (status != null) {
				if (IJobStatus.COMPLETED.equals(status.getState())) {
					/*
					 * leave the status in the map in case there are further
					 * calls (regarding remote file state); it will be pruned by
					 * the daemon
					 */
					status = jobStatusMap.terminated(jobId);
					if (status.stateChanged()) {
						jobStateChanged(jobId);
					}
					return status;
				}

				long now = System.currentTimeMillis();
				long lapse = now - status.getLastUpdateRequest();
				if (lapse < ICommandJobStatus.UPDATE_REQUEST_INTERVAL) {
					return status;
				}
				status.setUpdateRequestTime(now);
			}

			String state = status == null ? IJobStatus.UNDETERMINED : status.getStateDetail();

			PropertyType p = (PropertyType) rmVarMap.get(jobId);

			CommandType job = controlData.getGetJobStatus();
			if (job != null) {
				p = new PropertyType();
				p.setVisible(false);
				p.setName(jobId);
				rmVarMap.put(jobId, p);
				runCommand(jobId, job, false, true);
				p = (PropertyType) rmVarMap.remove(jobId);
			}

			if (p != null) {
				state = String.valueOf(p.getValue());
			}

			if (status == null) {
				status = new CommandJobStatus(getResourceManager().getUniqueName(), jobId, state, null, this);
				jobStatusMap.addJobStatus(jobId, status);
			} else {
				status.setState(state);
			}

			if (IJobStatus.COMPLETED.equals(state)) {
				/*
				 * leave the status in the map in case there are further calls
				 * (regarding remote file state); it will be pruned by the
				 * daemon
				 */
				status = jobStatusMap.terminated(jobId);
			}

			if (status.stateChanged()) {
				jobStateChanged(jobId);
			}

			// XXX eliminate when monitoring is in place
			System.out.println(Messages.RefreshedJobStatusMessage + jobId + JAXBControlConstants.CM + JAXBControlConstants.SP
					+ status.getState());
			return status;
		} catch (CoreException ce) {
			getResourceManager().setState(IResourceManager.ERROR_STATE);
			throw ce;
		} finally {
			pinTable.release(jobId);
		}
	}

	/*
	 * Executes any shutdown commands, then calls halt on the status map thread.
	 * NOTE: closing the RM does not terminate the remote connection it may be
	 * using. (non-Javadoc)
	 * 
	 * @see org.eclipse.ptp.rmsystem.AbstractResourceManagerControl#doShutdown()
	 */
	@Override
	protected void doShutdown() throws CoreException {
		try {
			doOnShutdown();
			((IJAXBResourceManagerConfiguration) getResourceManager().getConfiguration()).clearReferences();
			jobStatusMap.halt();
			getResourceManager().setState(IResourceManager.STOPPED_STATE);
		} catch (CoreException ce) {
			getResourceManager().setState(IResourceManager.ERROR_STATE);
			throw ce;
		}
	}

	/*
	 * Connects and executes any startup commands. (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ptp.rmsystem.AbstractResourceManagerControl#doStartup(org
	 * .eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void doStartup(IProgressMonitor monitor) throws CoreException {
		if (monitor != null) {
			monitor.beginTask(IResourceManager.STARTING_STATE, 10);
		}
		try {
			initialize(monitor);
			worked(monitor, 2);
			getResourceManager().setState(IResourceManager.STARTING_STATE);
		} catch (CoreException ce) {
			worked(monitor, 0);
			getResourceManager().setState(IResourceManager.ERROR_STATE);
			throw ce;
		} catch (Throwable t) {
			worked(monitor, 0);
			getResourceManager().setState(IResourceManager.ERROR_STATE);
			throw CoreExceptionUtils.newException(t.getMessage(), t);
		}

		try {
			doConnect(monitor);
			worked(monitor, 2);
			doOnStartUp(monitor);
			worked(monitor, 4);
			getResourceManager().setState(IResourceManager.STARTED_STATE);
		} catch (CoreException ce) {
			JAXBControlCorePlugin.log(ce);
			getResourceManager().setState(IResourceManager.ERROR_STATE);
			/*
			 * don't rethrow, as the error message generated by the platform
			 * JobManager will have already appeared
			 */
		} catch (Throwable t) {
			JAXBControlCorePlugin.log(t);
			getResourceManager().setState(IResourceManager.ERROR_STATE);
			/*
			 * don't rethrow, as the error message generated by the platform
			 * JobManager will have already appeared
			 */
		}
		worked(monitor, 0);
	}

	/*
	 * The main command for job submission. (non-Javadoc) The environment is
	 * reset on each call; a uuid tag is generated for the submission until a
	 * resource-specific identifier is returned (there should be a stream
	 * tokenizer associated with the job command in this case which sets the
	 * uuid property).
	 * 
	 * @see
	 * org.eclipse.ptp.rmsystem.AbstractResourceManagerControl#doSubmitJob(org
	 * .eclipse.debug.core.ILaunchConfiguration, java.lang.String,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IJobStatus doSubmitJob(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
			throws CoreException {
		/*
		 * give submission a unique id which will in most cases be replaced by
		 * the resource-generated id for the job/process
		 */
		String uuid = UUID.randomUUID().toString();

		if (!resourceManagerIsActive()) {
			return new CommandJobStatus(getResourceManager().getUniqueName(), uuid, IJobStatus.UNDETERMINED, null, this);
		}

		if (monitor != null) {
			monitor.beginTask(mode, 20);
		}

		String jobId = null;
		try {
			pinTable.pin(uuid);
			PropertyType p = new PropertyType();
			p.setVisible(false);
			rmVarMap.put(uuid, p);

			/*
			 * overwrite property/attribute values based on user choices
			 */
			updatePropertyValuesFromTab(configuration, monitor);
			worked(monitor, 2);

			/*
			 * create the script if necessary; adds the contents to env as
			 * "${rm:script}". If a custom script has been selected for use, the
			 * SCRIPT_PATH property will have been passed in with the launch
			 * configuration; if so, the following returns immediately.
			 */
			maybeHandleScript(uuid, controlData.getScript());
			worked(monitor, 3);

			ManagedFilesType files = controlData.getManagedFiles();

			/*
			 * if the script is to be staged, a managed file pointing to either
			 * its as its content (${rm:script#value}), or to its path
			 * (SCRIPT_PATH) must exist.
			 */
			files = maybeAddManagedFileForScript(files);
			worked(monitor, 3);

			if (!maybeHandleManagedFiles(uuid, files)) {
				throw CoreExceptionUtils.newException(Messages.CannotCompleteSubmitFailedStaging, null);
			}
			worked(monitor, 4);

			ICommandJob job = doJobSubmitCommand(uuid, mode);

			worked(monitor, 5);

			ICommandJobStatus status = job.getJobStatus();

			/*
			 * property containing actual jobId as name was set in the wait
			 * call; we may need the new jobId mapping momentarily to resolve
			 * proxy-specific info
			 */
			rmVarMap.remove(uuid);
			jobId = p.getName();

			/*
			 * job was cancelled during waitForId
			 */
			if (jobId == null) {
				return new CommandJobStatus(getResourceManager().getUniqueName(), uuid, IJobStatus.CANCELED, null, this);
			}
			pinTable.pin(jobId);
			rmVarMap.put(jobId, p);

			jobStatusMap.addJobStatus(status.getJobId(), status);
			status.setLaunchConfig(configuration);
			worked(monitor, 2);

			/*
			 * to ensure the most recent script is used at the next call
			 */
			rmVarMap.remove(jobId);
			rmVarMap.remove(JAXBControlConstants.SCRIPT_PATH);
			rmVarMap.remove(JAXBControlConstants.SCRIPT);
			return status;
		} finally {
			pinTable.release(uuid);
			pinTable.release(jobId);
			worked(monitor, 0);
		}
	}

	/**
	 * Checks to see if there was an exception thrown by the run method.
	 * 
	 * @param job
	 * @throws CoreException
	 *             if the job execution raised and exception
	 */
	private void checkJobForError(ICommandJob job) throws CoreException {
		IStatus status = job.getRunStatus();
		if (status != null && status.getSeverity() == IStatus.ERROR) {
			Throwable t = status.getException();
			if (t instanceof CoreException) {
				throw (CoreException) t;
			} else {
				throw CoreExceptionUtils.newException(status.getMessage(), t);
			}
		}
	}

	/**
	 * If there are special server connections to open, those need to be taken
	 * care of by a command to be run on start-up; here we just check for open
	 * connections.
	 * 
	 * @param monitor
	 * @throws RemoteConnectionException
	 */
	private void doConnect(IProgressMonitor monitor) throws RemoteConnectionException {
		IRemoteConnection conn = getRemoteServicesDelegate(monitor).getLocalConnection();
		if (!conn.isOpen()) {
			conn.open(monitor);
			if (!conn.isOpen()) {
				throw new RemoteConnectionException(Messages.LocalConnectionError);
			}
		}
		conn = getRemoteServicesDelegate(monitor).getRemoteConnection();
		if (!conn.isOpen()) {
			conn.open(monitor);
			if (!conn.isOpen()) {
				throw new RemoteConnectionException(Messages.RemoteConnectionError + conn.getAddress());
			}
		}
	}

	/**
	 * @param jobId
	 *            resource-specific id
	 * @param operation
	 *            terminate, hold, suspend, release, resume.
	 * @throws CoreException
	 *             If the command is not supported
	 */
	private void doControlCommand(String jobId, String operation) throws CoreException {
		CoreException ce = CoreExceptionUtils.newException(Messages.RMNoSuchCommandError + operation, null);

		CommandType job = null;
		if (TERMINATE_OPERATION.equals(operation)) {
			maybeKillInteractive(jobId);
			job = controlData.getTerminateJob();
			if (job == null) {
				return;
			}
		} else if (SUSPEND_OPERATION.equals(operation)) {
			job = controlData.getSuspendJob();
			if (job == null) {
				throw ce;
			}
		} else if (RESUME_OPERATION.equals(operation)) {
			job = controlData.getResumeJob();
			if (job == null) {
				throw ce;
			}
		} else if (RELEASE_OPERATION.equals(operation)) {
			job = controlData.getReleaseJob();
			if (job == null) {
				throw ce;
			}
		} else if (HOLD_OPERATION.equals(operation)) {
			job = controlData.getHoldJob();
			if (job == null) {
				throw ce;
			}
		}

		runCommand(jobId, job, false, true);
	}

	/**
	 * Run either interactive or batch job for run or debug modes.
	 * ILaunchManager.RUN_MODE and ILaunchManager.DEBUG_MODE are the
	 * corresponding LaunchConfiguration modes; batch/interactive are currently
	 * determined by the configuration (the configuration cannot implement
	 * both). This may need to be modified.
	 * 
	 * @param uuid
	 *            temporary internal id for as yet unsubmitted job
	 * @param mode
	 *            either ILaunchManager.RUN_MODE and ILaunchManager.DEBUG_MODE
	 * @return job wrapper object
	 * @throws CoreException
	 */
	private ICommandJob doJobSubmitCommand(String uuid, String mode) throws CoreException {
		CommandType command = null;
		boolean batch = false;

		if (ILaunchManager.RUN_MODE.equals(mode)) {
			command = controlData.getSubmitBatch();
			if (command != null) {
				batch = true;
			} else {
				command = controlData.getSubmitInteractive();
			}
		} else if (ILaunchManager.DEBUG_MODE.equals(mode)) {
			command = controlData.getSubmitBatchDebug();
			if (command != null) {
				batch = true;
			} else {
				command = controlData.getSubmitInteractiveDebug();
			}
		}

		if (command == null) {
			throw CoreExceptionUtils.newException(Messages.MissingRunCommandsError + JAXBControlConstants.SP + uuid
					+ JAXBControlConstants.SP + mode, null);
		}

		/*
		 * NOTE: changed this to join, because the waitForId is now part of the
		 * run() method of the command itself (05.01.2011)
		 */
		return runCommand(uuid, command, batch, true);
	}

	/**
	 * Run the shut down commands, if any
	 * 
	 * @throws CoreException
	 */
	private void doOnShutdown() throws CoreException {
		List<CommandType> onShutDown = controlData.getShutDownCommand();
		runCommands(onShutDown);
		for (ICommandJob job : jobTable.values()) {
			job.terminate();
			ICommandJobStatus status = job.getJobStatus();
			status.setState(IJobStatus.CANCELED);
			String jobId = status.getJobId();
			maybeForceExternalTermination(jobId);
			jobStateChanged(jobId);
		}
	}

	/**
	 * Run the start up commands, if any
	 * 
	 * @param monitor
	 * @throws CoreException
	 */
	private void doOnStartUp(IProgressMonitor monitor) throws CoreException {
		List<CommandType> onStartUp = controlData.getStartUpCommand();
		runCommands(onStartUp);
	}

	/**
	 * Sets the maps and data tree.
	 */
	private void initialize(IProgressMonitor monitor) throws Throwable {
		launchEnv = new TreeMap<String, String>();
		jobTable = new HashMap<String, ICommandJob>();
		pinTable = new JobIdPinTable();

		/*
		 * Use the base configuration which contains the config file information
		 */
		IJAXBResourceManagerConfiguration base = (IJAXBResourceManagerConfiguration) getResourceManager().getConfiguration();
		rmVarMap = (RMVariableMap) base.getRMVariableMap();
		controlData = base.getResourceManagerData().getControlData();
		setFixedConfigurationProperties(monitor);
		launchEnv.clear();
		appendLaunchEnv = true;

		/*
		 * start daemon
		 */
		jobStatusMap = new JobStatusMap(this);
		((Thread) jobStatusMap).start();
	}

	/**
	 * Checks for existence of either internally generated script or custom
	 * script path. In either case, either replaces contents of the
	 * corresponding managed file object or creates one.
	 * 
	 * @param files
	 *            the set of managed files for this submission
	 * @return the set of managed files, possibly with the script file added
	 */
	private ManagedFilesType maybeAddManagedFileForScript(ManagedFilesType files) {
		PropertyType scriptVar = (PropertyType) rmVarMap.get(JAXBControlConstants.SCRIPT);
		PropertyType scriptPathVar = (PropertyType) rmVarMap.get(JAXBControlConstants.SCRIPT_PATH);
		if (scriptVar != null || scriptPathVar != null) {
			if (files == null) {
				files = new ManagedFilesType();
				files.setFileStagingLocation(JAXBControlConstants.ECLIPSESETTINGS);
			}
			List<ManagedFileType> fileList = files.getFile();
			ManagedFileType scriptFile = null;
			if (!fileList.isEmpty()) {
				for (ManagedFileType f : fileList) {
					if (f.getName().equals(JAXBControlConstants.SCRIPT_FILE)) {
						scriptFile = f;
						break;
					}
				}
			}
			if (scriptFile == null) {
				scriptFile = new ManagedFileType();
				scriptFile.setName(JAXBControlConstants.SCRIPT_FILE);
				fileList.add(scriptFile);
			}
			scriptFile.setResolveContents(false);
			scriptFile.setUniqueIdPrefix(true);
			if (scriptPathVar != null) {
				scriptFile.setPath(String.valueOf(scriptPathVar.getValue()));
				scriptFile.setDeleteAfterUse(false);
			} else {
				scriptFile.setContents(JAXBControlConstants.OPENVRM + JAXBControlConstants.SCRIPT + JAXBControlConstants.PD
						+ JAXBControlConstants.VALUE + JAXBControlConstants.CLOSV);
				scriptFile.setDeleteAfterUse(true);
			}
		}
		return files;
	}

	/**
	 * Some interactive jobs are launched as pseudo-terminals; in this case, an
	 * external call may be necessary to terminate them cleanly.
	 * 
	 * @param jobId
	 */
	private void maybeForceExternalTermination(String jobId) {
		if (jobId == null) {
			return;
		}

		CommandType job = controlData.getTerminateJob();
		if (job == null) {
			return;
		}

		PropertyType p = (PropertyType) rmVarMap.get(jobId);
		if (p == null) {
			pinTable.pin(jobId);
			p = new PropertyType();
			p.setVisible(false);
			p.setName(jobId);
			rmVarMap.put(jobId, p);
		}

		try {
			runCommand(jobId, job, false, true);
		} catch (CoreException t) {
			JAXBControlCorePlugin.log(t);
		} finally {
			pinTable.release(jobId);
		}

		rmVarMap.remove(jobId);
	}

	/**
	 * Write content to file if indicated, and stage to host.
	 * 
	 * @param uuid
	 *            temporary internal id for as yet unsubmitted job
	 * @param files
	 *            the set of managed files for this submission
	 * @return whether the necessary staging completed without error
	 * @throws CoreException
	 */
	private boolean maybeHandleManagedFiles(String uuid, ManagedFilesType files) throws CoreException {
		if (files == null || files.getFile().isEmpty()) {
			return true;
		}
		ManagedFilesJob job = new ManagedFilesJob(uuid, files, this);
		job.schedule();
		try {
			job.join();
		} catch (InterruptedException ignored) {
		}
		return job.getSuccess();
	}

	/**
	 * Serialize script content if necessary. We first check to see if there is
	 * a custom script (path).
	 * 
	 * @param uuid
	 *            temporary internal id for as yet unsubmitted job
	 * @param script
	 *            configuration object describing how to construct the script
	 *            from the environment
	 */
	private void maybeHandleScript(String uuid, ScriptType script) {
		PropertyType p = (PropertyType) rmVarMap.get(JAXBControlConstants.SCRIPT_PATH);
		if (p != null && p.getValue() != null) {
			return;
		}
		if (script == null) {
			return;
		}
		ScriptHandler job = new ScriptHandler(uuid, script, rmVarMap, launchEnv, false);
		job.schedule();
		try {
			job.join();
		} catch (InterruptedException ignored) {
		}
	}

	/**
	 * If job is interactive, kill the process directly rather than issuing a
	 * remote command.
	 * 
	 * @param jobId
	 *            either process id or internal identifier.
	 * @return whether job has been canceled
	 */
	private boolean maybeKillInteractive(String jobId) {
		ICommandJobStatus status = jobStatusMap.getStatus(jobId);
		boolean killed = false;
		if (status != null) {
			killed = status.cancel();
		}
		return killed;
	}

	/**
	 * @return whether the state of the resource manager is stopped or not.
	 */
	private boolean resourceManagerIsActive() {
		IResourceManager rm = getResourceManager();
		if (rm != null) {
			String rmState = rm.getState();
			return !rmState.equals(IResourceManager.STOPPED_STATE);
		}
		return false;
	}

	/**
	 * Create command job, and schedule. Used for job-specific commands
	 * directly.
	 * 
	 * @param uuid
	 *            temporary internal id for as yet unsubmitted job
	 * @param command
	 *            configuration object containing the command arguments and
	 *            tokenizers
	 * @param batch
	 *            whether batch or interactive
	 * @param join
	 *            whether to launch serially or not
	 * @return the runnable job object
	 * @throws CoreException
	 */
	private ICommandJob runCommand(String uuid, CommandType command, boolean batch, boolean join) throws CoreException {
		if (command == null) {
			throw CoreExceptionUtils.newException(Messages.RMNoSuchCommandError, null);
		}

		ICommandJob job = new CommandJob(uuid, command, batch, (IJAXBResourceManager) getResourceManager());
		job.schedule();

		if (join) {
			try {
				job.join();
			} catch (InterruptedException ignored) {
			}
			checkJobForError(job);
		}

		return job;
	}

	/**
	 * Run command sequence. Invoked by startup or shutdown commands. Delegates
	 * to {@link #runCommand(String, CommandType, boolean, boolean)}. If a job
	 * in the sequence fails, the subsequent commands will not run.
	 * 
	 * @param cmds
	 *            configuration objects containing the command arguments and
	 *            tokenizers
	 * @throws CoreException
	 */
	private void runCommands(List<CommandType> cmds) throws CoreException {
		for (CommandType cmd : cmds) {
			runCommand(null, cmd, false, true);
		}
	}

	/**
	 * User name and service address. Set in case the script needs these
	 * variables.
	 */
	private void setFixedConfigurationProperties(IProgressMonitor monitor) {
		IRemoteConnection rc = getRemoteServicesDelegate(monitor).getRemoteConnection();
		rmVarMap.maybeAddProperty(JAXBControlConstants.CONTROL_USER_VAR, rc.getUsername(), false);
		rmVarMap.maybeAddProperty(JAXBControlConstants.CONTROL_ADDRESS_VAR, rc.getAddress(), false);
	}

	/**
	 * Transfers the values from the configuration to the live map.
	 * 
	 * @param configuration
	 *            passed in from Launch Tab when the "run" command is chosen.
	 * @throws CoreException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void updatePropertyValuesFromTab(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		Map lcattr = configuration.getAttributes();
		for (Object key : lcattr.keySet()) {
			Object value = lcattr.get(key);
			Object target = rmVarMap.get(key.toString());
			if (target instanceof PropertyType) {
				PropertyType p = (PropertyType) target;
				p.setValue(value.toString());
			} else if (target instanceof AttributeType) {
				AttributeType ja = (AttributeType) target;
				ja.setValue(value);
			}
		}

		/*
		 * The non-selected variables have been excluded from the launch
		 * configuration; but we need to null out the superset values here that
		 * are undefined. We also need to take care of the tailF redirect
		 * variables (which are not visible but are set in the launch tab by an
		 * option checkbox).
		 */
		for (String key : rmVarMap.getVariables().keySet()) {
			if (!lcattr.containsKey(key)) {
				Object target = rmVarMap.get(key.toString());
				if (target instanceof PropertyType) {
					PropertyType p = (PropertyType) target;
					if (p.isVisible()) {
						p.setValue(null);
					}
				} else if (target instanceof AttributeType) {
					AttributeType ja = (AttributeType) target;
					if (ja.isVisible()) {
						ja.setValue(null);
					}
				}
			}
		}

		/*
		 * pull these out of the configuration; they are needed for the script
		 */
		rmVarMap.maybeOverwrite(JAXBControlConstants.SCRIPT_PATH, JAXBControlConstants.SCRIPT_PATH, configuration);
		rmVarMap.maybeOverwrite(JAXBControlConstants.DIRECTORY, IPTPLaunchConfigurationConstants.ATTR_WORKING_DIR, configuration);
		rmVarMap.maybeOverwrite(JAXBControlConstants.EXEC_PATH, IPTPLaunchConfigurationConstants.ATTR_EXECUTABLE_PATH,
				configuration);
		rmVarMap.maybeOverwrite(JAXBControlConstants.PROG_ARGS, IPTPLaunchConfigurationConstants.ATTR_ARGUMENTS, configuration);
		setFixedConfigurationProperties(monitor);

		launchEnv.clear();
		launchEnv.putAll(configuration.getAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, launchEnv));
		appendLaunchEnv = configuration.getAttribute(ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, appendLaunchEnv);
	}

	/**
	 * Encapsulates null check.
	 * 
	 * @param monitor
	 * @param units
	 */
	private void worked(IProgressMonitor monitor, int units) {
		if (monitor != null) {
			if (units == 0) {
				monitor.done();
			} else {
				monitor.worked(units);
			}
		}
	}

}