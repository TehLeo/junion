/*
 * Copyright (c) 2019, Juraj Papp
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
package theleo.jstruct;

import theleo.jstruct.hidden.Vars;

/**
 *
 * @author Juraj Papp
 */

public enum ArrayType {
	Byte(Vars.ARRAY_BYTE_BASE_OFFSET, 1),
	Short(Vars.ARRAY_SHORT_BASE_OFFSET, 2),
	Char(Vars.ARRAY_CHAR_BASE_OFFSET, 2),
	Int(Vars.ARRAY_INT_BASE_OFFSET, 4),
	Float(Vars.ARRAY_FLOAT_BASE_OFFSET, 4),
	Long(Vars.ARRAY_LONG_BASE_OFFSET, 8),
	Double(Vars.ARRAY_DOUBLE_BASE_OFFSET, 8),
	Default(0, 0);
	
	public final long base;
	public final int size;
	private ArrayType(long base, int size) {
		this.base = base;
		this.size = size;
	}
	
	public static ArrayType get(Object array) {
		if(array instanceof byte[]) {
			return Byte;
		}
		else if(array instanceof short[]) {
			return Short;
		}
		else if(array instanceof char[]) {
			return Char;
		}
		else if(array instanceof int[]) {
			return Int;
		}
		else if(array instanceof float[]) {
			return Float;
		}
		else if(array instanceof long[]) {
			return Long;
		}
		else if(array instanceof double[]) {
			return Double;
		}
		return null;
	}
}
