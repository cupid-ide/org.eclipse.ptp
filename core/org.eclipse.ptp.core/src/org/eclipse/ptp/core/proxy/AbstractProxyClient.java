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

package org.eclipse.ptp.core.proxy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ptp.core.proxy.event.IProxyEvent;
import org.eclipse.ptp.core.proxy.event.IProxyEventListener;
import org.eclipse.ptp.core.proxy.event.ProxyEvent;
import org.eclipse.ptp.core.util.BitList;

public abstract class AbstractProxyClient {
	private String				sessHost;
	private int					sessPort;
	private ServerSocket			sessSvrSock;
	private Socket				sessSock;
	private OutputStreamWriter	sessOut;
	private InputStreamReader		sessIn;
	private boolean				connected;
	private boolean				exitThread;
	private Thread				eventThread;
	private Thread				acceptThread;
	protected List				listeners = new ArrayList(2);

	public AbstractProxyClient(String host, int port) {
		this.sessHost = host;
		this.sessPort = port;
		this.connected = false;
	}
	
	private String encodeLength(int val) {
		char[] res = new char[8];
		String str = Integer.toHexString(val);
		int rem = 8 - str.length();
		
		for (int i = 0 ; i < 8 ; i++) {
			if (i < rem)
				res[i] = '0';
			else
				res[i] = str.charAt(i - rem);
		}
		return String.valueOf(res);
	}
	
	private String encodeBitSet(BitList set) {
		String lenStr = Integer.toHexString(set.size());
		return lenStr + ":" + set.toString();
	}
	
	protected void sendCommand(String cmd) throws IOException {
		if (sessSock != null && sessSock.isConnected()) {
			String buf = encodeLength(cmd.length()) + " " + cmd;
			System.out.println("<" + buf + ">");
			sessOut.write(buf);
			sessOut.flush();
		}
	}

	protected void sendCommand(String cmd, String args) throws IOException {
		this.sendCommand(cmd + " " + args);
	}
	
	protected void sendCommand(String cmd, BitList set) throws IOException {
		String setStr = encodeBitSet(set);
		this.sendCommand(cmd, setStr);
	}
	
	protected void sendCommand(String cmd, BitList set, String args) throws IOException {
		String setStr = encodeBitSet(set);
		this.sendCommand(cmd, setStr + " " + args);
	}

	public void addEventListener(IProxyEventListener listener) {
		listeners.add(listener);
	}
	
	public void removeEventListener(IProxyEventListener listener) {
		listeners.remove(listener);
	}
	
	public int sessionConnect() {
		return 0;
	}
	
	public void sessionCreate() throws IOException {
		sessSvrSock = new ServerSocket(sessPort);
		acceptThread = new Thread("Proxy Client Accept Thread") {
			public void run() {
				try {
					sessSock = sessSvrSock.accept();
					sessOut = new OutputStreamWriter(sessSock.getOutputStream());
					sessIn = new InputStreamReader(sessSock.getInputStream());
					connected = true;
					startEventThread();
				} catch (IOException e) {
				}
				System.out.println("accept thread exiting...");
			}
		};
		acceptThread.start();
	}

	private void startEventThread() throws IOException {
		eventThread = new Thread("Proxy Client Event Thread") {
			public void run() {
				try {
					exitThread = false;
					while (!exitThread) {
						sessionProgress();
					}
				} catch (IOException e) {
				}
				System.out.println("event thread exiting...");
			}
		};
		eventThread.start();
	}
	
	protected synchronized void fireEvent(IProxyEvent event) {
		if (listeners == null)
			return;
		Iterator i = listeners.iterator();
		while (i.hasNext()) {
			IProxyEventListener listener = (IProxyEventListener) i.next();
			listener.fireEvent(event);
		}
	}
	
	public void sessionProgress() throws IOException {
		char[] len_bytes = new char[9];
		
		int n = sessIn.read(len_bytes, 0, 9);
		if (n < 0) {
			sessIn.close();
			sessOut.close();
			exitThread = true;
			return;
		}
		
		String len_str = new String(len_bytes, 0, 8);
		int len = Integer.parseInt(len_str, 16);
		
		char[] event_bytes = new char[len];

		n = sessIn.read(event_bytes, 0, len);
		if (n < 0) {
			sessIn.close();
			sessOut.close();
			exitThread = true;
			return;
		}

		String event_str = new String(event_bytes);
		
		IProxyEvent e = convertEvent(event_str);
		if (e == null)
			e = ProxyEvent.toEvent(event_str);
		
		fireEvent(e);
	}

	public void sessionFinish() throws IOException {
		this.sendCommand("QUI");
	}
	
	public abstract IProxyEvent convertEvent(String str);
}
