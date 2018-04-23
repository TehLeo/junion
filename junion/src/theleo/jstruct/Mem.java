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
import sun.nio.ch.DirectBuffer;
import theleo.jstruct.hidden.CompileException;
import theleo.jstruct.hidden.Hyb1;
import theleo.jstruct.hidden.Mem0;
import theleo.jstruct.hidden.Ref1;

/**
 *
 * @author Juraj Papp
 */
public class Mem {
	
	/**
	 * Frees the allocated struct array.
	 * This method is not required to be called.
	 * Allocated arrays are freed automatically when
	 * they are garbage collected.
	 * 
	 * @param ptr 
	 */
	public static void free(Ref1 ptr) {
		ptr.close();
	}
	/**
	 * Frees the allocated struct array.
	 * This method is not required to be called.
	 * Allocated arrays are freed automatically when
	 * they are garbage collected.
	 * 
	 * @param o 
	 */
	public static void free(Object o) {
		
	}
	
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
	 * The following function does nothing except
	 * keeping the object alive.
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
	 *  // since we reference arr here, it will not be garbage collected prior to this call
	 *	Mem.tag(arr); 
	 * }
	 * </pre>
	 * 
	 * 
	 * @param o - struct array
	 */
	public static void tag(Object o) {}
	
	/**
	 * Creates a slice of an array without 
	 * copying the array contents.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param from - inclusive
	 * @param to   - exclusive
	 */
	public static <T> T slice(T o, long from, long to) { throw new IllegalArgumentException("Type is not struct array."); }
	/**
	 * Creates a slice of an array without 
	 * copying the array contents.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param from - inclusive
	 * @param to   - exclusive
	 * @param step - can be any nonzero number, eg: 2 will return
	 *			slice with every second element, -1 will return
	 *			a slice with elements order reversed
	 */
	public static <T> T slice(T arr, long from, long to, long step) { throw new IllegalArgumentException("Type is not struct array."); }
	
	/**
	 * Creates a slice of an array without 
	 * copying the array contents.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param from - inclusive
	 * @param to   - exclusive
	 */
	public static Ref1 slice(Ref1 arr, long from, long to) {
		if(from < 0 || from >= arr.length || 
				to < from || to > arr.length)
			throw new IndexOutOfBoundsException("Slice out of bounds (" + from + ", " + to + "), length is " + arr.length);
		return new Ref1(arr, arr.base+from*arr.structSize, to-from, arr.structSize);
	}
	/**
	 * Creates a slice of an array without 
	 * copying the array contents.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param from - inclusive
	 * @param to   - exclusive
	 * @param step - can be any nonzero number, eg: 2 will return
	 *			slice with every second element, -1 will return
	 *			a slice with elements order reversed
	 */
	public static Ref1 slice(Ref1 arr, long from, long to, long step) {
		if(step == 0 || from < 0 || from >= arr.length || 
				to < from || to > arr.length) throw new IndexOutOfBoundsException("Slice out of bounds (" + from + ", " + to + ", " + step + "), Length is " + arr.length);
	
		long size = (to-from)/Math.abs(step);
		return (step > 0)?new Ref1(arr.owner, arr.base+from*arr.structSize, size, step*arr.structSize):
				new Ref1(arr.owner, arr.base+from*arr.structSize-(size-1)*arr.structSize*step, size, step*arr.structSize);
	}
	
		/**
	 * Creates a slice of an array without 
	 * copying the array contents.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param from - inclusive
	 * @param to   - exclusive
	 */
	public static Hyb1 slice(Hyb1 arr, long from, long to) {
		if(from < 0 || from >= arr.length || 
				to < from || to > arr.length)
			throw new IndexOutOfBoundsException("Slice out of bounds (" + from + ", " + to + "), length is " + arr.length);
		return new Hyb1(arr.owner, arr.base+from*arr.structSize, to-from, arr.structSize, arr.step);
	}
	/**
	 * Creates a slice of an array without 
	 * copying the array contents.
	 * 
	 * Changes in one array are reflected in the other.
	 * 
	 * @param from - inclusive
	 * @param to   - exclusive
	 * @param step - can be any nonzero number, eg: 2 will return
	 *			slice with every second element, -1 will return
	 *			a slice with elements order reversed
	 */
	public static Hyb1 slice(Hyb1 arr, long from, long to, int step) {
		if(step == 0 || from < 0 || from >= arr.length || 
				to < from || to > arr.length) throw new IndexOutOfBoundsException("Slice out of bounds (" + from + ", " + to + ", " + step + "), Length is " + arr.length);
	
		long size = (to-from)/Math.abs(step);
		return (step > 0)?new Hyb1(arr.owner, arr.base+from*arr.structSize, size, step*arr.structSize, step*arr.step):
				new Hyb1(arr.owner, arr.base+from*arr.structSize-(size-1)*arr.structSize*step, size, step*arr.structSize, step*arr.step);
	}
	
	//slice, dice, trice, quad(quart), pent
	
	
	
	/**
	 * Creates a view for a direct byte buffer with a native order.
	 * 
	 * Changes in the array are reflected in the buffer.
	 * 
	 * @param <T>
	 * @param b - direct byte buffer with native order
	 * @param strSize - struct size
	 * @return struct array
	 */
	public static <T> T wrap(Buffer b, long strSize) {
		if(!b.isDirect()) throw new IllegalArgumentException("Buffer is not direct!");
		
		ByteBuffer bb;
		if(b instanceof ByteBuffer) bb = (ByteBuffer)b;
		else bb = (ByteBuffer)((DirectBuffer)b).attachment();
		if(bb.order() != ByteOrder.nativeOrder()) throw new IllegalArgumentException("Buffer does not have native order!");
		long addr = ((DirectBuffer)bb).address();
		if(addr == 0) throw new IllegalArgumentException("Buffer address is zero.");
		return (T)new Ref1(b, addr, bb.capacity()/strSize, strSize);
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
	
	
	//---Owner info---
	
	/**
	 * Return true is the owner of the memory is present.
	 * Returns false for nonstruct arrays.
	 * 
	 * @param o - struct array
	 * @return - true if owner is present
	 */
	public static boolean hasOwner(Object o) { return false; }
	/**
	 * Return true is the owner of the memory is present.
	 * Returns false for nonstruct arrays.
	 * 
	 * @param o - struct array
	 * @return - true if owner is present
	 */
	public static boolean hasOwner(Ref1 o) { return false; 	}
	/**
	 * Return true is this array is the first owner.
	 * Returns false for nonstruct arrays.
	 * 
	 * @param o - struct array
	 * @return - true if this array was created directly
	 */
	public static boolean isFirstOwner(Ref1 o) {
		return o.owner == o;
	}
	/**
	 * Return true is this array is the first owner.
	 * Returns false for nonstruct arrays.
	 * 
	 * @param o - struct array
	 * @return - true if this array was created directly
	 */
	public static boolean isOwner(Object o) {
		return false;
	}
	
	
	//---Layout info---
	/**
	 * Returns the byte alignment struct.
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
	
}
