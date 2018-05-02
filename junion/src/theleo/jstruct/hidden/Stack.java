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
package theleo.jstruct.hidden;

import theleo.jstruct.StackOutOfMemory;

/**
 *
 * @author Juraj Papp
 */
public final class Stack {
	public long base, baseEnd;
	public long position;
	private long pointer;
	public int positionObjs;
	public int hybridIndex;
	public Object[] hybridData;
	public Stack(long size, int objSize) {
		pointer = Mem0.u.allocateMemory(size);
		hybridData = new Object[objSize];
		hybridIndex = Mem0.allocObjectArray(hybridData);
		base = pointer;
		baseEnd = pointer + size;
		position = pointer;
		positionObjs = 0;
	}
	public final long getBase() { return base;}
	public long get(int bytes) {
		long l = position;
		long add = l+bytes;
		if(add >= baseEnd) grow(); 
		position = add;
		return l;
	}
	private void grow() {
		long size = (baseEnd-base)<<1;
		if(size > Mem0.STACK_MAX_SIZE) throw new StackOutOfMemory();
		pointer = Mem0.u.reallocateMemory(pointer, size);
		base = pointer;
		baseEnd = pointer + size;
		position = pointer;
	}
	public void pop(long p) {
		position = p;
	}
	public int getObj(int objs) {
		int i = positionObjs;
		int add = i+objs;
		if(add >= hybridData.length) growArray();
		positionObjs = add;
		return i;
	}
	private void growArray() {
		Mem0.stackGrowArray(this);
	}
	public void pop(long p, int obj) {
		position = p; 
		int to = positionObjs;
		for(int i = obj; i < to; i++) hybridData[i] = null;
		positionObjs = obj;
	}
	@Override
	protected void finalize()  {
		long value = Mem0.u.getAndSetLong(this, PTR_OFFSET, 0);
		if(value != 0) {
			Mem0.u.freeMemory(value);
			Mem0.freeObjectArray(hybridIndex);
			hybridIndex = -1;
		}	
	}
	private static final long PTR_OFFSET;
	static {
		long val;
		try {
			val = Mem0.u.objectFieldOffset(Stack.class.getDeclaredField("pointer"));
		} catch (Exception ex) {
			ex.printStackTrace();
			val = 32L;
			throw new IllegalArgumentException("Could not initialize");
		}
		PTR_OFFSET = val;
	}
}
