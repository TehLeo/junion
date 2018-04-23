/*
 * Copyright (c) 2017, Juraj Papp
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the copyright holder nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL COPYRIGHT HOLDER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package theleo.jstruct.plugin.ecl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.eclipse.core.runtime.Status;

public class JStructPlugin extends Plugin {
	public static final String PLUGIN_ID = "theleo.jstruct.plugin.ecl";
	public static JStructPlugin instance;
		
	public JStructPlugin() {
		instance = this;
	}
	
	@Override
	public void start(BundleContext b) throws Exception {
		super.start(b);
		
		log("JSTRUCT BUNDLE START");
	}

	@Override
	public void stop(BundleContext b) throws Exception {
		super.stop(b);
		log("JSTRUCT BUNDLE START");
		
	}
	
	
	public static void log(Object obj) {
		if(obj instanceof String) log((String)obj, null);
		else if(obj != null) log(obj.toString(), null);
		else log(null, null);
    }
	public static void log(String msg, Exception e) {
		if(instance != null)
			instance.getLog().log(new Status(Status.INFO, PLUGIN_ID, Status.OK, msg, e));
		else {
			if(e != null) System.out.println(msg + ", " + e.toString());
			else System.out.println(msg);
		}
	}
	public static void log(int status, String msg, Exception e) {
		if(instance != null) {
			Status st = new Status(status, PLUGIN_ID, Status.OK, msg, e);
			instance.getLog().log(st);
			if(status == Status.ERROR || status == Status.WARNING) {
//				StatusManager sm = StatusManager.getManager();
//				log("SM handle" + sm);
//		    	if(sm != null) {
//		    		
//		    		sm.handle(st, StatusManager.SHOW);
//		    	}
			}
		}
		else {
			if(e != null) System.out.println(msg + ", " + e.toString());
			else System.out.println(msg);
		}
	}
	static int StatusManager_SHOW = 2;
	public static Status status(int s, String msg, Exception e) {
		return new Status(s, PLUGIN_ID, StatusManager_SHOW, msg, e);
	}
	public static JStructPlugin getInstance() {
		return instance;
	}
}
