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

import theleo.jstruct.exceptions.CompileException;
import theleo.jstruct.bridge.Bridge;
import java.util.logging.Logger;
import theleo.jstruct.MemInit;
import theleo.jstruct.allocator.Allocator;
import theleo.jstruct.allocator.ArrayAllocator;
import theleo.jstruct.exceptions.StructIndexOutOfBoundsException;
import static theleo.jstruct.hidden.Vars.*;
import theleo.jstruct.reflect.StructType;

/**
 *
 * @author Juraj Papp
 */
public class Mem0 {
	public static final Logger LOGGER = Logger.getLogger(Mem0.class.getName());
	
	public static final Allocator A;
	public static final ArrayAllocator AA;
	public static final Bridge B;
		
	static {
		A = MemInit.getAllocator();
		AA = MemInit.getArrayAllocator();
		B = MemInit.getBridge();				
		A.initializeAllocator();		
		MemInit.setInitialized();
	}
		
	public static long allocHybOnStack(long addr, int posObj, int[] ownerOffet) {
//		if(zero) u.setMemory(base, allocSize, (byte)0);
		
		for(int i = 0; i < ownerOffet.length; i += 2) {
			int off = ownerOffet[i];
			int count = ownerOffet[i+1];
			
			u.putInt(addr+off, posObj);
			u.putInt(addr+off+4, posObj);
			posObj += count;
		}
		
		return addr;
	}
		
	/**
	 * Returns true if this thread has a 
	 * struct stack.
	 * 
	 * @return true if struct stack is present.
	 */
	public static boolean hasStack() {
		return u.getObject(Thread.currentThread(), BLOCKER_LOCK_OFFSET) instanceof Stack;
	}
	
	private static Stack initStack(Object o) {
		Thread t = Thread.currentThread();
		Stack s = new Stack(MemInit.STACK_INIT_SIZE, MemInit.STACK_INIT_MAX_SIZE,
					MemInit.STACK_INIT_OBJ_SIZE, MemInit.STACK_INIT_MAX_OBJ_SIZE);
		synchronized(o) {
			u.putObject(t, BLOCKER_LOCK_OFFSET, s);
		}
		return s;
	}
	/**
	 * Returns the stack for struct data for the current thread
	 * or creates a new one if it is not yet present.
	 * 
	 * @return stack for struct data
	 */
	public static Stack stack() {
		Object o = u.getObject(Thread.currentThread(), BLOCKER_LOCK_OFFSET);
		if(o instanceof Stack) return (Stack)o;
		return initStack(o);
	}
	
	/**
	 * Allows to reinterpret cast struct type.
	 *  
	 * @param o - struct type
	 * @return 
	 */
	public static <T> T as(Object o) {
		return (T)o;
	}
	
	public static <T> T asLong(Object o) {
		return (T)o;
	}
	
	public static <T> T as(Object o, Class<T> c) {
		return (T)o;
	}
	
	/**
	 * Allocates struct on thread's stack. As with any stack allocation, 
	 * stack allocated structs are valid until the method returns.
	 * 
	 * @param classLiteral class literal
	 * @return stack allocated struct
	 */
	public static <T> T stackRaw(Class<T> classLiteral) { throw new CompileException(); }
	
	
	public static void nullRefs(R1 dst, long to, StructType type, long items) {
		Object[] ref = dst.ref;
		if(ref != null && type.objectCount != 0) {
			if((type.size & 7) != 0) throw new IllegalArgumentException("Unaligned");
			long add = 0;
			for(long s = 0; s < items; s++) {
				int k = 0;
				for(int i = 0; i < type.size; i += 8) {
					if(k < type.objectOffset.length && type.objectOffset[k] == i) {
						int count = type.objectOffset[k+1];
						for(int j = 0; j < count; j++) {
							B.pL(dst, to+add, j, null);
						}
						k+=2;
					}
					add += 8;
				}
			}
		}
	}
	public static void shift(R1 src, long from, StructType type, long items, long shift) {
		if(shift == 0 || items == 0) return;
		long strSize = type.size;
		long d = shift*strSize;
		if(shift < 0) {
			long base = src.base;
			long end = base+src.longLength*src.strSize;
			
			if(from+d < base || from+d > end) new StructIndexOutOfBoundsException();
			if(from+(items-1)*strSize < base || from+(items-1)*strSize > end) new StructIndexOutOfBoundsException();
//			src.idxCheck(from+d);
//			src.idxCheck(from+(items-1)*strSize);
			for(long s = 0; s < items; s++) {
				copy(src, from, src, from+d, type, 1);
				from += strSize;
			}
		}
		else {
			throw new UnsupportedOperationException("not yet implemented.");
		}
	}
//		if(shift == 0) return;
//		Object srcA = src.getArray();
//		long bytes = type.size*items;
//		if(src.ref != null) {
//			
////			int oFromI = u.getInt(4+from+type.objectOffset[0]);
////			int oToI = u.getInt(4+to+type.objectOffset[0]);
//			
//			if((type.size & 7) != 0) throw new IllegalArgumentException("Unaligned");
//			long add = 0;
//			for(long s = 0; s < items; s++) {
//				int k = 0;
//				for(int i = 0; i < type.size; i += 8) {
//					if(k < type.objectOffset.length && type.objectOffset[k] == i) {
//						int count = type.objectOffset[k+1];
//						for(int j = 0; j < count; j++) {
//							B.pL(dst, to+add, j, B.gL(src, from+add, j));
//						}
//						k+=2;
//					}
////					else u.putLong(dstA, to+i, u.getLong(srcA, from+i));
//					else u.copyMemory(srcA, from+add, dstA, to+add, 8);
//					add += 8;
//				}
//			}
//			
//			//Todo, optimize to copy more than 8 bytes
//			//or copy with longs instead
////			for(int i = 0; i < bytes; i+=8) {
////				if(k < type.objectOffset.length && type.objectOffset[k] == i) {
////					int count = type.objectOffset[k+1];
////					for(int j = 0; j < count; j++) {
//////						putObject0(oTo, oToI+add,
//////								getObject0(oFrom, oFromI+add));
////						B.pL(dst, to, j, B.gL(src, from, j));
//////						add++;
////					}
////					k+=2;
////				}
//////				else {
//////					if(i+8 > bytes)
//////						u.copyMemory(srcA, from+i, dstA, to+i, bytes-i);
//////					else u.putLong(dstA, to+i, u.getLong(srcA, from+i));
//////				}
////				else u.copyMemory(srcA, from+i, dstA, to+i, i+8 > bytes? bytes-i: 8);
////			}
//		}
//		else {
//			
//			u.copyMemory(srcA, from, dstA, to, bytes);
//		}
//	}
	private static void copyMemory(R1 src, long from,
                           R1 dst, long to, long bytes) {
//		from += src.base;
//		to += dst.base;
		u.copyMemory(src.getArray(), from, dst.getArray(), to, bytes);
	}
	public static void copy(R1 src, long from,
                           R1 dst, long to, StructType type, long items) {
				
		Object srcA = src.getArray();
		Object dstA = dst.getArray();
		long bytes = type.size*items;
		if(type.objectCount != 0 && src.ref != null) {
			if(dst.ref == null) throw new IllegalArgumentException();
			
//			int oFromI = u.getInt(4+from+type.objectOffset[0]);
//			int oToI = u.getInt(4+to+type.objectOffset[0]);
			
			if((type.size & 7) != 0) throw new IllegalArgumentException("Unaligned");
			long add = type.objectOffset[0];
			for(long s = 0; s < items; s++) {
				int k = 0;
				for(int i = 0; i < type.size; i += 8) {
					if(k < type.objectOffset.length && type.objectOffset[k] == i) {
						int count = type.objectOffset[k+1];
						for(int j = 0; j < count; j++) {
							B.pL(dst, to+add, j, B.gL(src, from+add, j));
						}
						k+=2;
					}
//					else u.putLong(dstA, to+i, u.getLong(srcA, from+i));
//					else u.copyMemory(srcA, from+add, dstA, to+add, 8);
					else copyMemory(src, from+add, dst, to+add, 8);
					add += 8;
				}
			}
			
			//Todo, optimize to copy more than 8 bytes
			//or copy with longs instead
//			for(int i = 0; i < bytes; i+=8) {
//				if(k < type.objectOffset.length && type.objectOffset[k] == i) {
//					int count = type.objectOffset[k+1];
//					for(int j = 0; j < count; j++) {
////						putObject0(oTo, oToI+add,
////								getObject0(oFrom, oFromI+add));
//						B.pL(dst, to, j, B.gL(src, from, j));
////						add++;
//					}
//					k+=2;
//				}
////				else {
////					if(i+8 > bytes)
////						u.copyMemory(srcA, from+i, dstA, to+i, bytes-i);
////					else u.putLong(dstA, to+i, u.getLong(srcA, from+i));
////				}
//				else u.copyMemory(srcA, from+i, dstA, to+i, i+8 > bytes? bytes-i: 8);
//			}
		}
		else {
			//TODO: null references instead
			if(dst.ref != null) throw new IllegalArgumentException();
			if(srcA == dstA) {
				if(to == from) {
					System.err.println("Mem0.copy same place noop...");
					return;
				}
				if(to >= from && to < from+bytes) {
					//overlapping memory 
					for(long s = 0; s < items; s++) {

					}
				}
				else if(from >= to && from < to+bytes) {
					//overlapping memory
					
				}
				else {
//					u.copyMemory(srcA, from, dstA, to, bytes);
					copyMemory(src, from, dst, to, bytes);
				}
			}
//			else u.copyMemory(srcA, from, dstA, to, bytes);
			else copyMemory(src, from, dst, to, bytes);
		}
	}
	
	public static R1 getArray(R1 r, long l) { return r; }
	public static R1 getArray(Object o) { throw new CompileException(); }
	
	public static long getAddress(R1 r, long l) { return l; }
	public static long getAddress(Object o) { throw new CompileException(); }
	
	public static <T> T idxRef0(T r, long l) {
		return l == 0?null:r;
	}
	public static long idx(R1 r, long l) {
		return l;
	}
	public static <T> T idxR(T r, long l) {
		return r;
	}
	
	public static String getInfo(R1 r) {
		return "R1{" + "base=" + r.base + ", longLength=" + r.longLength + ", strSize=" + r.strSize + ", length=" + r.length + ", gcobj=" + r.gcobj + ", ref=" + r.ref + '}';
	}
	
}
