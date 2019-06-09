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
package theleo.jstruct.reflect;

import theleo.jstruct.StructHeapType;

/**
 * 
 * 
 * @author Juraj Papp
 */
public final class StructType {
	public final Class cls;
	public final StructHeapType type;
	public final int size;
	public final int align;
	public final int objectCount;
	public final int[] objectOffset;

	public StructType(Class cls, StructHeapType type, int size, int align, int objectCount, int[] offsets) {
		this.cls = cls;
		this.type = type;
		this.size = size;
		this.align = align;
		this.objectCount = objectCount;
		this.objectOffset = offsets;
	}

	public Class toClass() {
		return cls;
	}

	@Override
	public String toString() {
		return "struct " + cls.getName();
	}
	
	public boolean isAligned(long addr) {
		return (addr&(align-1))==0;
	}
	
	
	public static class StructNotFoundException extends RuntimeException {
		public StructNotFoundException() {

		}
		public StructNotFoundException(String msg) {
			super(msg);
		}
	
	}
	public static StructType forName(String className) throws StructNotFoundException {
		try {
			return forClass(Class.forName(className));
		}
		catch(ClassNotFoundException e) {
			throw new StructNotFoundException(e.getMessage());
		}
	}
	public static StructType forClass(Class cls) throws SecurityException {
		try {
			Object o = cls.getDeclaredField("$STRUCT_TYPE$").get(null);
			if(o instanceof StructType) {
				return (StructType)o;
			}
			throw new StructNotFoundException();
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new StructNotFoundException(e.getMessage());
		}
	}
	
}
