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
package theleo.jstruct.allocator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import theleo.jstruct.ArrayType;
import theleo.jstruct.MemInit;
import theleo.jstruct.StructHeapType;
import theleo.jstruct.hidden.Mem0;
import theleo.jstruct.hidden.R1;
import theleo.jstruct.hidden.Stack;
import theleo.jstruct.hidden.Vars;
import theleo.jstruct.reflect.StructType;

/**
 *
 * @author Juraj Papp
 */
public class DefaultArrayAllocator implements ArrayAllocator {
	public static final byte[] ZERO_BYTES = new byte[0];
	public static final short[] ZERO_SHORTS = new short[0];
	public static final char[] ZERO_CHARS = new char[0];
	public static final int[] ZERO_INTS = new int[0];
	public static final float[] ZERO_FLOATS = new float[0];
	public static final long[] ZERO_LONGS = new long[0];
	public static final double[] ZERO_DOUBLES = new double[0];
	public static final Object[] ZERO_OBJECTS = new Object[0];
	
	public boolean manualAlign;
	public int allocReq = 8;
	public boolean onDefaultAllocateDirect = false;
	public boolean onDefaultAllocateDirectZero = true;
	
	public boolean onDirectUseDirectByteBufferIfLessThanMaxIntegerSize = false;

	@Override
	public R1 allocateArray(StructType type, long items) {
		if(onDefaultAllocateDirect || type.type == StructHeapType.None)	return allocateArrayDirect(type, onDefaultAllocateDirectZero, items);
		else {
			ArrayType t = null;
			switch(type.type) {
				case All:
				case AllAsWellTestDoNotUse: t = ArrayType.Long; break;
				case Byte: t = ArrayType.Byte; break;
				case Short: t = ArrayType.Short; break;
				case Char: t = ArrayType.Char; break;
				case Int: t = ArrayType.Int; break;
				case Float: t = ArrayType.Float; break;
				case Long: t = ArrayType.Long; break;
				case Double: t = ArrayType.Double; break;
				default: throw new IllegalArgumentException();
			}
			return allocateArrayHeap(type, t, items);
		}
	}

	@Override
	public R1 allocateArrayHeap(StructType type, ArrayType array, long items) {
		long bytes = items*type.size;
		long tmp;
		Object arr;
		long base;
		switch(array) {
			case Byte: 
				if(bytes > Vars.MAX_HEAP_ARRAY) throw new OutOfMemoryError("Heap array exceeds max size: " + items);
				arr = bytes==0?ZERO_BYTES:new byte[(int)bytes];
				base = Vars.ARRAY_BYTE_BASE_OFFSET;
				break;
			case Char:
				tmp = (bytes+1)>>1;
				if(tmp > Vars.MAX_HEAP_ARRAY) throw new OutOfMemoryError("Heap array exceeds max size: " + tmp);
				arr = tmp==0?ZERO_CHARS:new char[(int)tmp];
				base = Vars.ARRAY_CHAR_BASE_OFFSET;
				break;
			case Short:
				tmp = (bytes+1)>>1;
				if(tmp > Vars.MAX_HEAP_ARRAY) throw new OutOfMemoryError("Heap array exceeds max size: " + tmp);
				arr = tmp==0?ZERO_SHORTS:new short[(int)tmp];
				base = Vars.ARRAY_SHORT_BASE_OFFSET;
				break;
			case Int:
				tmp = (bytes+3)>>2;
				if(tmp > Vars.MAX_HEAP_ARRAY) throw new OutOfMemoryError("Heap array exceeds max size: " + tmp);
				arr = tmp==0?ZERO_INTS:new int[(int)tmp];
				base = Vars.ARRAY_INT_BASE_OFFSET;
				break;
			case Float:
				tmp = (bytes+3)>>2;
				if(tmp > Vars.MAX_HEAP_ARRAY) throw new OutOfMemoryError("Heap array exceeds max size: " + tmp);
				arr = tmp==0?ZERO_FLOATS:new float[(int)tmp];
				base = Vars.ARRAY_FLOAT_BASE_OFFSET;
				break;
			case Long:
				tmp = (bytes+7)>>3;
				if(tmp > Vars.MAX_HEAP_ARRAY) throw new OutOfMemoryError("Heap array exceeds max size: " + tmp);
				arr = tmp==0?ZERO_LONGS:new long[(int)tmp];
				base = Vars.ARRAY_LONG_BASE_OFFSET;
				break;
			case Double:
				tmp = (bytes+7)>>3;
				if(tmp > Vars.MAX_HEAP_ARRAY) throw new OutOfMemoryError("Heap array exceeds max size: " + tmp);
				arr = tmp==0?ZERO_DOUBLES:new double[(int)tmp];
				base = Vars.ARRAY_DOUBLE_BASE_OFFSET;
				break;
			default: throw new IllegalArgumentException();
		}
		
		return R1.create(type, array, arr, items, base, arr);
	}

	
	@Override
	public R1 allocateArrayDirect(StructType type, boolean zero, long items) {
		long bytes = items*type.size;
		if(onDirectUseDirectByteBufferIfLessThanMaxIntegerSize && bytes <= Integer.MAX_VALUE)
			return allocateArrayDirectBuffer(type, items);
				
		long allocBase;
		long base;
		if(manualAlign) {
			allocBase = Mem0.A.allocateMemory(bytes+allocReq);
			if((allocBase & (allocReq-1)) != 0)
				base = allocBase + allocReq - (allocBase & (allocReq-1));
			else base = allocBase;
		}
		else {
			base = allocBase = Mem0.A.allocateMemory(bytes);
			if((allocBase & (allocReq-1)) != 0) {
				Mem0.A.freeMemory(allocBase);
				Mem0.LOGGER.log(Level.SEVERE, "Allocator returned unaligned address: Enabling manual alignment! ");
				manualAlign = true;
				allocBase = Mem0.A.allocateMemory(bytes+allocReq);
				if((allocBase & (allocReq-1)) != 0)
					base = allocBase + allocReq - (allocBase & (allocReq-1));
				else base = allocBase;
			}
		}
				
		R1 r = R1.create(type, null, null, items, base, null);
		DefaultCleaner.register(r, allocBase);
		return r;
	}

	@Override
	public R1 allocateArrayDirectBuffer(StructType type, long items) {
		long bytes = items*type.size;
		if(bytes > Integer.MAX_VALUE) throw new OutOfMemoryError("DirectBuffer exceeds max size!");
		ByteBuffer buf = ByteBuffer.allocateDirect((int)bytes).order(ByteOrder.nativeOrder());
						
		long base = Vars.getBufferAddress(buf);
		if(base == 0) throw new OutOfMemoryError("ByteBuffer.allocateDirect: out of memory.");
		
		if((base & (allocReq-1)) != 0) {
			Mem0.LOGGER.log(Level.SEVERE, "ByteBuffer.allocateDirect returned unaligned buffer! ");
		}
		
		R1 r = R1.create(type, null, null, items, base, buf);
		return r;
	}
	
	@Override
	public R1 allocateStack(long bytes, int objects, Stack stack) {
		long allocBase;
		long base;
		if(manualAlign) {
			allocBase = Mem0.A.allocateMemory(bytes+allocReq);
			if((allocBase & (allocReq-1)) != 0)
				base = allocBase + allocReq - (allocBase & (allocReq-1));
			else base = allocBase;
		}
		else {
			base = allocBase = Mem0.A.allocateMemory(bytes);
			if((allocBase & (allocReq-1)) != 0) {
				Mem0.A.freeMemory(allocBase);
				Mem0.LOGGER.log(Level.SEVERE, "Allocator returned unaligned address: Enabling manual alignment! ");
				manualAlign = true;
				allocBase = Mem0.A.allocateMemory(bytes+allocReq);
				if((allocBase & (allocReq-1)) != 0)
					base = allocBase + allocReq - (allocBase & (allocReq-1));
				else base = allocBase;
			}
		}

		R1 r = R1.create(StructHeapType.All, ArrayType.Byte, null, bytes, 1, base, null, new Object[objects]);
		DefaultCleaner.register(r);
		
		return r;
	}
	@Override
	public R1 allocateArrayStack(Stack s, StructType type, long items) {
		return s.array(type, items);
	}	
}
