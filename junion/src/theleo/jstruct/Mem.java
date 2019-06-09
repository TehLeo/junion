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
package theleo.jstruct;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.ShortBuffer;
import theleo.jstruct.exceptions.CompileException;
import theleo.jstruct.exceptions.UnalignedAccessException;
import theleo.jstruct.hidden.Mem0;
import theleo.jstruct.hidden.R1;
import theleo.jstruct.hidden.R1.R2;
import theleo.jstruct.hidden.R1.R3;
import theleo.jstruct.hidden.R1.R4;
import theleo.jstruct.hidden.R1.RN;
import theleo.jstruct.hidden.Vars;
import theleo.jstruct.reflect.StructType;

/**
 * <b>This class lists utility methods, which are supposed to be memory-safe.</b> <br><br>
 * 
 * Additional methods, which are unsafe, are located in:
 * {@link theleo.jstruct.hidden.Mem0}.
 *
 * @author Juraj Papp
 */
public class Mem {
	
	
	/**
	 * Returns the heap array if the passed struct array 
	 * was allocated on the heap and null otherwise.
	 * 
	 * @param <T>
	 * @param r
	 * @return java primitive array or null
	 */
	public static <T> T getJavaArray(Object r) {
		return (T)((R1)r).getArray();
	}
	
	/**
	 * Returns the java.nio.Buffer if this struct array
	 * was allocated with a buffer and null otherwise.
	 * 
	 * @param r
	 * @return buffer or null
	 */
	public static Buffer getJavaBuffer(Object r) {
		Object gc = ((R1)r).gcobj;
		if(gc instanceof Buffer) return (Buffer)gc;
		return null;
	}
	
	/**
	 * Returns the java.nio.ByteBuffer if this struct array
	 * was allocated with a byte buffer and null otherwise.
	 * 
	 * @param r
	 * @return byte buffer or null
	 */
	public static ByteBuffer getJavaByteBuffer(Object r) {
		Object gc = ((R1)r).gcobj;
		if(gc instanceof ByteBuffer) return (ByteBuffer)gc;
		return null;
	}
	
	/**
	 * Allocates struct on thread's stack. Stack allocated structs are valid until the method returns.
	 * <br><br>
	 * <b>This method sets the memory to zero.</b>
	 * 
	 * Usage:
	 * <pre>
	 * Vec3 v = Mem.stack0(Vec3.class);
	 * </pre>
	 * 
	 * If you would like to initialize the memory yourself instead, or not fill it with 0s see:<br>
	 *  {@link #stackInit(java.lang.Class)}. <br>
	 *  {@link #stackRaw(java.lang.Class)}. 
	 * 
	 * @param classLiteral class literal
	 * @return stack allocated struct
	 */
	public static <T> T stack0(Class<T> classLiteral) { throw new CompileException(); }
	
	/**
	 * Allocates struct on thread's stack. Stack allocated structs are valid until the method returns.
	 * <br><br>
	 * <b>This method requires struct initialization. Initialization is achieved with the
	 * following syntax:</b>
	 * 
	 * Usage:
	 * <pre>
	 * Vec3 v = Mem.stackInit(Vec3.class);
	 * { v.x = 1; v.y = 2; v.z = 3; }
	 * </pre>
	 * 
	 * If you do not want to use initialization, you can use
	 * <br>
	 *  {@link #stack0(java.lang.Class)}. <br>
	 *  {@link #stackRaw(java.lang.Class)}. 
	 * 
	 * @param classLiteral class literal
	 * @return stack allocated struct
	 */
	public static <T> T stackInit(Class<T> classLiteral) { throw new CompileException(); }
		
	/**
	 * Allocates struct on thread's stack. Stack allocated structs are valid until the method returns.
	 * <br><br>
	 * <b>The struct's initial memory is undefined.</b>
	 * 
	 * Usage:
	 * <pre>
	 * Vec3 v = Mem.stackRaw(Vec3.class);
	 * </pre>
	 * 
	 * If you would like to initialize the memory yourself instead, or fill it with 0s see:
	 * <br>
	 *  {@link #stackInit(java.lang.Class)}. <br>
	 *  {@link #stack0(java.lang.Class)}. 
	 * 
	 * @param classLiteral class literal
	 * @return stack allocated struct
	 */
	public static <T> T stackRaw(Class<T> classLiteral) { throw new CompileException(); }
	
	
	/**
	 * Provides long indexing functionality for struct arrays.
	 * 
	 * <pre>
	 * long arrSizeLong = ...;
	 * eg.
	 * Vec3[] arr = new Vec3[Mem.li(arrSizeLong)];
	 * Vec3 v = arr[Mem.li(4147483647L)];
	 * </pre>
	 * 
	 * @param l - long index to an array
	 * @return 
	 */
	public static int li(long l) { throw new CompileException(); }
	
	/**
	 * Returns a multidimensional view of a 1D struct array.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param <T>
	 * @param r array
	 * @param len
	 * @return 
	 */
	public static <T> T dims(Object r, long... len) { 
		throw new CompileException();
	}
	
	/**
	 * Returns a multidimensional view of a 1D struct array.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param <T>
	 * @param r array
	 * @param len1
	 * @param len2
	 * @return 
	 */
	public static <T extends R1> R2<T> dims(T r, long len1, long len2) { 
		return new R2(r,len1,len2);
	}
	/**
	 * Returns a multidimensional view of a 1D struct array.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param <T>
	 * @param r array
	 * @param len1
	 * @param len2
	 * @param len3
	 * @return 
	 */
	public static <T extends R1> R3<T> dims(T r, long len1, long len2, long len3) { 
		return new R3(r,len1,len2,len3);
	}
	/**
	 * Returns a multidimensional view of a 1D struct array.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param <T>
	 * @param r array
	 * @param len1
	 * @param len2
	 * @param len3
	 * @param len4
	 * @return 
	 */
	public static <T extends R1> R4<T> dims(T r, long len1, long len2, long len3, long len4) { 
		return new R4(r,len1,len2,len3,len4);
	}
	/**
	 * Returns a multidimensional view of a 1D struct array.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param <T>
	 * @param r array
	 * @param len
	 * @return 
	 */
	public static <T extends R1> RN<T> dims(T r, long... len) { 
		return new RN(r,len);
	}
	
	public static <T extends R1> T allocateStack(long items, int objects, theleo.jstruct.hidden.Stack stack) { return (T)Mem0.AA.allocateStack(items, objects, stack);}

	public static <T extends R1> T allocateArray(StructType type, long items) { return (T)Mem0.AA.allocateArray(type, items); }
	public static <T extends R1> T allocateArrayHeap(StructType type, ArrayType array, long items) { return (T)Mem0.AA.allocateArrayHeap(type, array, items);}
	public static <T extends R1> T allocateArrayDirect(StructType type, boolean zero, long items) { return (T)Mem0.AA.allocateArrayDirect(type, zero, items);}
	public static <T extends R1> T allocateArrayDirectBuffer(StructType type, long items) { return (T)Mem0.AA.allocateArrayDirectBuffer(type, items);}
	public static <T extends R1> T allocateArrayStack(theleo.jstruct.hidden.Stack s, StructType type, long items) { return (T)Mem0.AA.allocateArrayStack(s, type, items);}
	
	public static <T extends R2> T allocateArray(StructType type, long len1, long len2) {
		return (T)dims(Mem0.AA.allocateArray(type, len1*len2), len1, len2);
	}
	public static <T extends R2> T allocateArrayHeap(StructType type, ArrayType array, long len1, long len2) {
		return (T)dims(Mem0.AA.allocateArrayHeap(type, array, len1*len2), len1, len2);
	}
	public static <T extends R2> T allocateArrayDirect(StructType type, boolean zero, long len1, long len2) {
		return (T)dims(Mem0.AA.allocateArrayDirect(type, zero, len1*len2), len1, len2);
	}
	public static <T extends R2> T allocateArrayDirectBuffer(StructType type, long len1, long len2) {
		return (T)dims(Mem0.AA.allocateArrayDirectBuffer(type, len1*len2), len1, len2);
	}
	public static <T extends R2> T allocateArrayStack(theleo.jstruct.hidden.Stack s, StructType type, long len1, long len2) {
		return (T)dims(Mem0.AA.allocateArrayStack(s, type, len1*len2), len1, len2);
	}
	
	public static <T extends R3> T allocateArray(StructType type, long len1, long len2, long len3) {
		return (T)dims(Mem0.AA.allocateArray(type, len1*len2*len3), len1, len2, len3);
	}
	public static <T extends R3> T allocateArrayHeap(StructType type, ArrayType array, long len1, long len2, long len3) {
		return (T)dims(Mem0.AA.allocateArrayHeap(type, array, len1*len2*len3), len1, len2, len3);
	}
	public static <T extends R3> T allocateArrayDirect(StructType type, boolean zero, long len1, long len2, long len3) {
		return (T)dims(Mem0.AA.allocateArrayDirect(type, zero, len1*len2*len3), len1, len2, len3);
	}
	public static <T extends R3> T allocateArrayDirectBuffer(StructType type, long len1, long len2, long len3) {
		return (T)dims(Mem0.AA.allocateArrayDirectBuffer(type, len1*len2*len3), len1, len2, len3);
	}
	public static <T extends R3> T allocateArrayStack(theleo.jstruct.hidden.Stack s, StructType type, long len1, long len2, long len3) {
		return (T)dims(Mem0.AA.allocateArrayStack(s, type, len1*len2*len3), len1, len2, len3);
	}
	
	public static <T extends R4> T allocateArray(StructType type, long len1, long len2, long len3, long len4) {
		return (T)dims(Mem0.AA.allocateArray(type, len1*len2*len3*len4), len1, len2, len3, len4);
	}
	public static <T extends R4> T allocateArrayHeap(StructType type, ArrayType array, long len1, long len2, long len3, long len4) {
		return (T)dims(Mem0.AA.allocateArrayHeap(type, array, len1*len2*len3*len4), len1, len2, len3, len4);
	}
	public static <T extends R4> T allocateArrayDirect(StructType type, boolean zero, long len1, long len2, long len3, long len4) {
		return (T)dims(Mem0.AA.allocateArrayDirect(type, zero, len1*len2*len3*len4), len1, len2, len3, len4);
	}
	public static <T extends R4> T allocateArrayDirectBuffer(StructType type, long len1, long len2, long len3, long len4) {
		return (T)dims(Mem0.AA.allocateArrayDirectBuffer(type, len1*len2*len3*len4), len1, len2, len3, len4);
	}
	public static <T extends R4> T allocateArrayStack(theleo.jstruct.hidden.Stack s, StructType type, long len1, long len2, long len3, long len4) {
		return (T)dims(Mem0.AA.allocateArrayStack(s, type, len1*len2*len3*len4), len1, len2, len3, len4);
	}
	
	public static <T extends RN> T allocateArray(StructType type, long... len) {
		long items = len[0];
		for(int i = 1; i < len.length; i++) items *= len[i];
		return (T)dims(Mem0.AA.allocateArray(type, items), len);
	}
	public static <T extends RN> T allocateArrayHeap(StructType type, ArrayType array, long... len) {
		long items = len[0];
		for(int i = 1; i < len.length; i++) items *= len[i];
		return (T)dims(Mem0.AA.allocateArrayHeap(type, array, items), len);
	}
	public static <T extends RN> T allocateArrayDirect(StructType type, boolean zero, long... len) {
		long items = len[0];
		for(int i = 1; i < len.length; i++) items *= len[i];
		return (T)dims(Mem0.AA.allocateArrayDirect(type, zero, items), len);
	}
	public static <T extends RN> T allocateArrayDirectBuffer(StructType type, long... len) {
		long items = len[0];
		for(int i = 1; i < len.length; i++) items *= len[i];
		return (T)dims(Mem0.AA.allocateArrayDirectBuffer(type, items), len);
	}
	public static <T extends RN> T allocateArrayStack(theleo.jstruct.hidden.Stack s, StructType type, long... len) {
		long items = len[0];
		for(int i = 1; i < len.length; i++) items *= len[i];
		return (T)dims(Mem0.AA.allocateArrayStack(s, type, items), len);
	}
	
	public static boolean refEquals(R1 str1, long l1, R1 str2, long l2) {
		return str1 == str2 && l1 == l2;
	} 
	public static boolean refEquals(Object str1, Object str2) { throw new CompileException();} 
	
	/**
	 * Returns the length of multidimensional struct array.
	 * eg. Vec3[][] arr = new Vec3[5][7];
	 * 
	 * len(arr, 0) returns 5
	 * len(arr, 1) returns 7
	 * 
	 * @param arr - struct array
	 * @param dim - dimension index
	 * @return the lenght of multidimensional array
	 */
	public static long len(Object arr, int dim) { throw new CompileException(); }
		
	/**
	 * The following function does nothing except
	 * trying to keep the object alive. 
	 * 
	 * For Java 9+, use Reference.reachabilityFence instead.
	 * 	 
	 * It is useful in the following scenario.
	 * 
	 * <pre>
	 * void test() {
	 *	Vec3[] arr = new Vec[3];
	 * 
	 *	Vec3 v4 = arr[4];
	 *	//do some long task here....
	 *  //since we no longer access arr
	 *  //compiler might garbage collect it
	 *	//we do not want that since we still use variable v4
	 *	
	 *  // since we reference arr here, garbage collector might not gc it
	 *	Mem.tag(arr); 
	 * }
	 * </pre>
	 * 
	 * 
	 * @param o - struct array
	 */
	public static void tag(Object o) {}
	
	/**
	 * Returns a flattened 1D view of a multidimensional struct array.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param a - struct array
	 * @return 
	 */
	public static <T> T[] flatten(Object a) { throw new IllegalArgumentException("Type is not struct array."); }
	/**
	 * Returns a flattened 1D view of a multidimensional struct array.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param <T>
	 * @param a - struct array
	 * @return 
	 */
	public static <T extends R1> T flatten(T a) { return a; }
	
	/**
	 * Returns a flattened 1D view of a multidimensional struct array.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param <T>
	 * @param a - struct array
	 * @return 
	 */
	public static <T extends R1> T flatten(R2<T> a) { return a.r; }
	/**
	 * Returns a flattened 1D view of a multidimensional struct array.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param <T>
	 * @param a - struct array
	 * @return 
	 */
	public static <T extends R1> T flatten(R3<T> a) { return a.r; }
	/**
	 * Returns a flattened 1D view of a multidimensional struct array.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param <T>
	 * @param a - struct array
	 * @return 
	 */
	public static <T extends R1> T flatten(R4<T> a) { return a.r; }
	/**
	 * Returns a flattened 1D view of a multidimensional struct array.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param <T>
	 * @param a - struct array
	 * @return 
	 */
	public static <T extends R1> T flatten(RN<T> a) { return a.r; }
	
	
//	
//	
//	/**
//	 * Creates a slice of an array without 
//	 * copying the array contents.
//	 * 
//	 * Changes in one array are reflected in the other.
//	 * 
//	 * @param from - inclusive
//	 * @param to   - exclusive
//	 */
//	public static <T> T slice(T o, long from, long to) { throw new IllegalArgumentException("Type is not struct array."); }
//	/**
//	 * Creates a slice of an array without 
//	 * copying the array contents.
//	 * 
//	 * Changes in one array are reflected in the other.
//	 * 
//	 * @param from - inclusive
//	 * @param to   - exclusive
//	 * @param step - can be any nonzero number, eg: 2 will return
//	 *			slice with every second element, -1 will return
//	 *			a slice with elements order reversed
//	 */
//	public static <T> T slice(T arr, long from, long to, long step) { throw new IllegalArgumentException("Type is not struct array."); }
//	
//	/**
//	 * Creates a slice of an array without 
//	 * copying the array contents.
//	 * 
//	 * Changes in one array are reflected in the other.
//	 * 
//	 * @param from - inclusive
//	 * @param to   - exclusive
//	 */
//	public static Ref1 slice(Ref1 arr, long from, long to) {
//		if(from < 0 || from >= arr.length || 
//				to < from || to > arr.length)
//			throw new IndexOutOfBoundsException("Slice out of bounds (" + from + ", " + to + "), length is " + arr.length);
//		return new Ref1(arr, arr.base+from*arr.structSize, to-from, arr.structSize);
//	}
//	/**
//	 * Creates a slice of an array without 
//	 * copying the array contents.
//	 * 
//	 * Changes in one array are reflected in the other.
//	 * 
//	 * @param from - inclusive
//	 * @param to   - exclusive
//	 * @param step - can be any nonzero number, eg: 2 will return
//	 *			slice with every second element, -1 will return
//	 *			a slice with elements order reversed
//	 */
//	public static Ref1 slice(Ref1 arr, long from, long to, long step) {
//		if(step == 0 || from < 0 || from >= arr.length || 
//				to < from || to > arr.length) throw new IndexOutOfBoundsException("Slice out of bounds (" + from + ", " + to + ", " + step + "), Length is " + arr.length);
//	
//		long size = (to-from)/Math.abs(step);
//		return (step > 0)?new Ref1(arr.owner, arr.base+from*arr.structSize, size, step*arr.structSize):
//				new Ref1(arr.owner, arr.base+from*arr.structSize-(size-1)*arr.structSize*step, size, step*arr.structSize);
//	}
//	
//	/**
//	 * Creates a slice of an array without 
//	 * copying the array contents.
//	 * 
//	 * Changes in one array are reflected in the other.
//	 * 
//	 * @param from - inclusive
//	 * @param to   - exclusive
//	 */
//	public static Hyb1 slice(Hyb1 arr, long from, long to) {
//		if(from < 0 || from >= arr.length || 
//				to < from || to > arr.length)
//			throw new IndexOutOfBoundsException("Slice out of bounds (" + from + ", " + to + "), length is " + arr.length);
//		return new Hyb1(arr.owner, arr.base+from*arr.structSize, to-from, arr.structSize, arr.step);
//	}
//	/**
//	 * Creates a slice of an array without 
//	 * copying the array contents.
//	 * 
//	 * Changes in one array are reflected in the other.
//	 * 
//	 * @param from - inclusive
//	 * @param to   - exclusive
//	 * @param step - can be any nonzero number, eg: 2 will return
//	 *			slice with every second element, -1 will return
//	 *			a slice with elements order reversed
//	 */
//	public static Hyb1 slice(Hyb1 arr, long from, long to, int step) {
//		if(step == 0 || from < 0 || from >= arr.length || 
//				to < from || to > arr.length) throw new IndexOutOfBoundsException("Slice out of bounds (" + from + ", " + to + ", " + step + "), Length is " + arr.length);
//	
//		long size = (to-from)/Math.abs(step);
//		return (step > 0)?new Hyb1(arr.owner, arr.base+from*arr.structSize, size, step*arr.structSize, step*arr.step):
//				new Hyb1(arr.owner, arr.base+from*arr.structSize-(size-1)*arr.structSize*step, size, step*arr.structSize, step*arr.step);
//	}
//	
//	
	
	
	/**
	 * Creates a view for a writable heap buffer or a direct byte buffer with a native order.
	 * 
	 * Changes in the array are reflected in the buffer.
	 * 
	 * @param <T>
	 * @param b - heap buffer or a direct byte buffer with a native order
	 * @return struct array
	 * 
	 * @throws ReadOnlyBufferException if b is read only
	 * @throws IllegalArgumentException if b does not have native order
	 * @throws UnalignedAccessException if wrapping b with the given type could result in unaligned access
	 */
	public static <T> T wrap(Buffer b, StructType type) {
//		b = new MappedByteBuffer.
//		ByteBuffer.allocateDirect(0)
//		FileChannel f = new FileChannelImpl
//		return wrap(b, type, 0, 0, type.size);
		if(b.isReadOnly()) 
			throw new ReadOnlyBufferException();
		
		ByteOrder o = null;
		
		if(b instanceof ByteBuffer) o = ((ByteBuffer) b).order();
		else if(b instanceof ShortBuffer) o = ((ShortBuffer) b).order();
		else if(b instanceof CharBuffer) o = ((CharBuffer) b).order();
		else if(b instanceof IntBuffer) o = ((IntBuffer) b).order();
		else if(b instanceof FloatBuffer) o = ((FloatBuffer) b).order();
		else if(b instanceof LongBuffer) o = ((LongBuffer) b).order();
		else if(b instanceof DoubleBuffer) o = ((DoubleBuffer) b).order();
		if(o != ByteOrder.nativeOrder()) throw new IllegalArgumentException("Buffer does not have native order!"); 

		if(b.isDirect()) {
			long base = Vars.getBufferAddress(b);
			long bytes = b.capacity();
			
			if(!type.isAligned(base)) throw new UnalignedAccessException();
			
			if(b instanceof ByteBuffer) {}
			else if(b instanceof ShortBuffer || b instanceof CharBuffer) bytes <<= 1;
			else if(b instanceof IntBuffer || b instanceof FloatBuffer) bytes <<= 2;
			else if(b instanceof LongBuffer || b instanceof DoubleBuffer) bytes <<= 3;
			else throw new IllegalArgumentException("What kind of buffer is this?");
			
			long items = bytes/type.size;

			return (T)R1.create(type.type, null, null, items, type.size, base, null, null);
		}
		else {
			if(!b.hasArray()) throw new IllegalArgumentException("Buffer is neither direct nor has an array, what kind of buffer is this?"); 
			Object array = b.array();
			ArrayType arrtype = ArrayType.get(array);
			if(arrtype == null) throw new IllegalArgumentException("Buffer array unrecognized type: " + array);
			
			long base = arrtype.base+arrtype.size*b.arrayOffset();
			long bytes = arrtype.size*b.capacity();
			
			long items = bytes/type.size;
			
			if(!type.isAligned(base)) throw new UnalignedAccessException();
			
			return (T)R1.create(type.type, arrtype, array, items, type.size, base, array, null);
		}
	}
	
	/**
	 * Returns true if reference is null.
	 * 
	 * @param o - struct refence
	 * @return true if null
	 * @throws IllegalArgumentException if o is not reference
	 */
	public static boolean isNull(long o) { throw new IllegalArgumentException("Not a reference."); }
	/**
	 * Returns true if reference is null.
	 * 
	 * @param o struct refence
	 * @return true if null
	 * @throws IllegalArgumentException if o is not reference
	 */
	public static boolean isNull(Object o) { throw new IllegalArgumentException("Not a reference.");}
		
	//---Layout info---
	/**
	 * Returns the byte alignment of struct.
	 * In other words, returns the maximum size of primtive value
	 * declared in the struct.
	 * If nested structes are present, their alignment is taken into
	 * the calculation.
	 * If reference to struct is present, 8 will be returned, due to
	 * references being represented as a long.
	 * Possible values for struct include [0, 1, 2, 4, 8]
	 * 
	 * <pre>
	 * Returns -1 for object types.
	 * 
	 * use as: alignment(Vec3.class)
	 * 
	 * not allowed:
	 * Class c = Vec3.class
	 * alignment(c)
	 * </pre>
	 * 
	 * @param classLiteral - class literal
	 * @return number of bytes, or -1 if the given class literal is not a struct type
	 */
	public static int alignment(Class classLiteral) { throw new CompileException(); }
	/**
	 * Returns number of bytes added at the end to align the struct.
	 * Returns -1 for object types.
	 * 
	 * <pre>
	 * use as: endPadding(Vec3.class)
	 * 
	 * not allowed:
	 * Class c = Vec3.class
	 * endPadding(c)
	 * </pre>
	 * 
	 * @param classLiteral - class literal
	 * @return number of bytes, or -1 if the given class literal is not a struct type
	 */
	public static int endPadding(Class classLiteral) { throw new CompileException(); }
	/**
	 * Returns number of bytes used to store the given struct type including all padding.
	 * Returns -1 for object types.
	 * 
	 * <pre>
	 * use as: sizeOf(Vec3.class)
	 * 
	 * not allowed:
	 * Class c = Vec3.class
	 * sizeOf(c)
	 * </pre>
	 * 
	 * @param classLiteral - class literal
	 * @return number of bytes, or -1 if the given class literal is not a struct type
	 */
	public static int sizeOf(Class classLiteral) { throw new CompileException(); }
	/**
	 * Returns number of bytes used to store the given struct type exluding all padding.
	 * Returns -1 for object types.
	 * 
	 * <pre>
	 * use as: sizeOfData(Vec3.class)
	 * 
	 * not allowed:
	 * Class c = Vec3.class
	 * sizeOfData(c)
	 * </pre>
	 * 
	 * @param classLiteral - class literal
	 * @return number of bytes, or -1 if the given class literal is not a struct type
	 */
	public static int sizeOfData(Class classLiteral) { throw new CompileException(); }
	
	
	
	
	/**
	 * Returns debug string of structure layout.
	 * 
	 * <pre>
	 * use as: layoutString(Vec3.class)
	 * 
	 * not allowed:
	 * Class c = Vec3.class
	 * layoutString(c)
	 * </pre>
	 * 
	 * @param classLiteral - class literal
	 * @return debug string of structure or null
	 */
	public static String layoutString(Class classLiteral) { throw new CompileException(); }
	
	
	
	
	
	
	
	
	
	
	/**
	 * Returns true if n is a multiple of 8. 
	 * 
	 * @param n long number
	 * @return true if n is a multiple of 8
	 */
	public boolean isMult8(long n) {
		return (n&7) == 0;
	}
	/**
	 * Rounds a long number up to the nearest multiple of 8.
	 * 
	 * On input in range [Long.MAX_VALUE-7, Long.MAX_VALUE] overflows.
	 * 
	 * @param n long number
	 * @return n or a nearest multiple of 8
	 */
	public static long roundUp8(long n) {  
        return ((n + 7L) & (-8L));  
    }  
	/**
	 * Rounds a long number up to the nearest multiple of x,
	 * where x is a positive power of two.
	 * 
	 * On input in range [Long.MAX_VALUE-x+1, Long.MAX_VALUE] might overflow.
	 * 
	 * @param n long number
	 * @param x positive power of two
	 * @return n or a nearest multiple of x
	 */
	public static long roundUpPow2X(long n, long x) {  
        return ((n + x - 1) & (-x));  
    }  
	
	
}
