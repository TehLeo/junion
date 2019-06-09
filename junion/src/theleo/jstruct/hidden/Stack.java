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

import theleo.jstruct.ArrayType;
import theleo.jstruct.Mem;
import theleo.jstruct.exceptions.StackOutOfMemory;
import theleo.jstruct.reflect.StructType;

/**
 *
 * @author Juraj Papp
 */
public final class Stack {
	public long returnAddress;
	public long position;
	public int objectIndex;
	
	public Object[] objectData;
	
	public long maxSize;
	public int maxObjSize;
	public int returnObjIndex;
	
	public R1 data;
	public R1.A dataA;
	public R1.B dataB;
	public R1.C dataC;
	public R1.D dataD;
	public R1.F dataF;
	public R1.I dataI;
	public R1.L dataL;
	public R1.N dataN;	
	public R1.S dataS;
	
	public Stack(long size, long maxSize, int objSize, int maxObjSize) {
		this.maxSize = maxSize;
		this.maxObjSize = maxObjSize;

		R1 r = (R1)Mem0.AA.allocateStack(size, objSize, null);
		data = r;
		objectData = r.ref;
		dataA = R1.wrapA(r);
		dataB = R1.wrapB(r);
		dataC = R1.wrapC(r);
		dataD = R1.wrapD(r);
		dataF = R1.wrapF(r);
		dataI = R1.wrapI(r);
		dataL = R1.wrapL(r);
		dataN = R1.wrapN(r);
		dataS = R1.wrapS(r);
	}
		
	public R1 array(StructType type, long items) {
		long bytes = Mem.roundUp8(items*type.size);
		int objs = (int)(items*type.objectCount);

		long add = position+bytes;
		int addObjs = objectIndex+objs;
		if(add > data.longLength || addObjs > objectData.length) { 
			grow(add, addObjs); 
			add=bytes;
			addObjs = objs;
		}
		
		R1 r = R1.create(type, null, null, items, data.base+position, data, data.ref, objectIndex);
		position = add;
		objectIndex = addObjs;
		return r;
	}
	private void grow(long bytes, int objs) {
		long size = Math.max(8, data.longLength);
		while(size > 0 && size < bytes) size <<= 1;
		if(size <= 0 || size > maxSize) throw new StackOutOfMemory();
		
		int sizeObjs = Math.max(8, objectData.length);
		while(sizeObjs > 0 && sizeObjs < objs) sizeObjs <<= 1;
		if(sizeObjs <= 0 || sizeObjs > maxObjSize) throw new StackOutOfMemory();

		R1 r = (R1)Mem0.AA.allocateStack(size, sizeObjs, null);
		data = r;
		objectData = r.ref;
		dataA = R1.wrapA(r);
		dataB = R1.wrapB(r);
		dataC = R1.wrapC(r);
		dataD = R1.wrapD(r);
		dataF = R1.wrapF(r);
		dataI = R1.wrapI(r);
		dataL = R1.wrapL(r);
		dataN = R1.wrapN(r);
		dataS = R1.wrapS(r);
		
		position = 0;
		objectIndex = 0;
	}
	
	public long get(int bytes, int objs) {
		long pos = position;
		int objPos = objectIndex;
		long add = pos+bytes;
		int addObjs = objPos+objs;
		if(add > data.longLength || addObjs > objectData.length) { 
			grow(add, addObjs); 
			position = bytes;
			objectIndex = objs;
			returnObjIndex = 0;
			return 0;
		}
		position = add;
		objectIndex = addObjs;
		returnObjIndex = objPos;
		return pos;
	} 
	
	
	public final long getBase() { return data.base; }
	public long get(int bytes) {
		long pos = position;
		long add = pos+bytes;
		if(add > data.longLength) {
			grow(add, 0); 
			position = bytes;
			return 0;
		}
		position = add;
		return pos;
	}

	public void pop(long p) {
		position = Math.min(position, p);
	}
	public void pop(long p, int obj) {
		position = Math.min(position, p);
		if(obj < objectIndex) {
			int to = objectIndex;
			for(int i = obj; i < to; i++) objectData[i] = null;
			objectIndex = obj;
		}
	}
	
}
