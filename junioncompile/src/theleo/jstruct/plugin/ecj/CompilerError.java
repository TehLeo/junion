/*
 * Copyright (c) 2018, Juraj Papp
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
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package theleo.jstruct.plugin.ecj;

/**
 *
 * @author Juraj Papp
 */
public class CompilerError extends RuntimeException {
	public static enum ActionType {
		IGNORE,
		INFO,
		WARNING,
		ERROR;
		
		
	}
	public static class Action {
		public String message;
		public ActionType type;
		public Action(String message, ActionType type) {
			this.message = message;
			this.type = type;
		}

		@Override
		public String toString() {
			return type + ": " + message;
		}
		
	}
	public static Action STRUCT_MISSING_ORDER = new Action("Struct: %s is missing field order information. Recompile to resolve. ", ActionType.WARNING);
	public static Action STRUCT_ORDER_MISMATCH = new Action("Struct: %s field order is different.", ActionType.ERROR);
	
	public static Action STRUCT_FIELD_NOT_FOUND = new Action("Struct field not found: %s", ActionType.ERROR);

	public static Action TYPE_NOT_FOUND = new Action("Type not found: %s", ActionType.ERROR);
	public static Action UNCATEGORIZED = new Action("Uncategorized: %s", ActionType.INFO);
	
	

	public CompilerError(String msg) {
		super(msg);
	}
	public CompilerError(String msg, Throwable t) {
		super(msg, t);
	}
	
	public static void exec(Action action, Object... params) {
		if(action.type == ActionType.IGNORE) return;
		System.err.print(action.type);
		System.err.print(": ");
		System.err.printf(action.message, params);
		System.err.println();
		switch(action.type) {
			case INFO:
				
				break;
			case WARNING:
				
				break;
			case ERROR:				
				throw new CompilerError(String.format(action.message, params));
		}
	}
}
