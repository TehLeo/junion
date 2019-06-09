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
package theleo.jstruct.hidden;

import theleo.jstruct.ArrayType;
import theleo.jstruct.StructHeapType;
import theleo.jstruct.exceptions.StructIndexOutOfBoundsException;
import theleo.jstruct.reflect.StructType;

/**
 *
 * @author Juraj Papp
 */
public abstract class R1 {
	public final long base;		
	public final long longLength;

	public final int strSize;
	public final int length;
	
	public final Object gcobj;
	public final Object[] ref;
	
	public static class R2<T extends R1> {
		public final T r;
		public final long length, length2;

		public R2(T r, long len1, long len2) {
			this.r = r;
			this.length = len1;
			this.length2 = len2;
		}
		
		public final long idx(long l) {
			return r.idx(l);
		}
		public final long idx2(long x, long y) {
			return (x*length2+y);
		}
		public final long getLength(int i) {
			if(i == 0) return length;
			if(i == 1) return length2;
			throw new IllegalArgumentException();
		}
	}
	public static class R3<T extends R1> {
		public final T r;
		public final long length, length2, length3;

		public R3(T r, long len1, long len2, long len3) {
			this.r = r;
			this.length = len1;
			this.length2 = len2;
			this.length3 = len3;
		}
		public final long idx(long l) {
			return r.idx(l);
		}
		public final long idx3(long x, long y, long z) {
			return ((x*length2+y)*length3+z);
		}
		public final long getLength(int i) {
			if(i == 0) return length;
			if(i == 1) return length2;
			if(i == 2) return length3;
			throw new IllegalArgumentException();
		}
	}
	public static class R4<T extends R1> {
		public final T r;
		public final long length, length2, length3, length4;

		public R4(T r, long len1, long len2, long len3, long len4) {
			this.r = r;
			this.length = len1;
			this.length2 = len2;
			this.length3 = len3;
			this.length4 = len4;
		}
		public final long idx(long l) {
			return r.idx(l);
		}
		public final long idx4(long x, long y, long z, long w) {
			return (((x*length2+y)*length3+z)*length4+w);
		}
		public final long getLength(int i) {
			if(i == 0) return length;
			if(i == 1) return length2;
			if(i == 2) return length3;
			if(i == 3) return length4;
			throw new IllegalArgumentException();
		}
	}
	public static class RN<T extends R1> {
		public final T r;
		public final long length;
		public final long[] lengths;

		public RN(T r, long... len) {
			this.r = r;
			this.length = len[0];
			this.lengths = len;
		}
		public final long idx(long l) {
			return r.idx(l);
		}
		public final long idxN(long... x) {
			long sum = x[0];
			for(int i = 1; i < x.length; i++) 
				sum = x[i] + sum*lengths[i];
		
			return sum;
		
//		long sum = index[index.length-1];
//		for(int i = index.length-2; i >= 0; i--) 
//			sum = index[i] + sum*lengthN[i];
//			throw new IllegalArgumentException();
		}
		public final long getLength(int i) {
			return lengths[i];
		}
	}
		
	public static class N extends R1 {
		public N(Object array, long longLength, int strSize, long base, Object gcobj, Object[] ref) {
			super(longLength, strSize, base, gcobj, ref);
			if(array != null) throw new IllegalArgumentException();
		}
		@Override
		public final Object getArray() {
			return null;
		}
	}

	public static class B extends R1 {
		public final byte[] array;
		public B(Object array, long longLength, int strSize, long base, Object gcobj, Object[] ref) {
			super(longLength, strSize, base, gcobj, ref);
			if(array != null && !(array instanceof byte[])) throw new IllegalArgumentException();
			this.array = (byte[])array;
		}
		@Override
		public final byte[] getArray() {
			return array;
		}
	}
	
	public static class S extends R1 {
		public final short[] array;
		public S(Object array, long longLength, int strSize, long base, Object gcobj, Object[] ref) {
			super(longLength, strSize, base, gcobj, ref);
			if(array != null && !(array instanceof short[])) throw new IllegalArgumentException();
			this.array = (short[])array;
		}
		@Override
		public final short[] getArray() {
			return array;
		}
	}
	
	public static class C extends R1 {
		public final char[] array;
		public C(Object array, long longLength, int strSize, long base, Object gcobj, Object[] ref) {
			super(longLength, strSize, base, gcobj, ref);
			if(array != null && !(array instanceof char[])) throw new IllegalArgumentException();
			this.array = (char[])array;
		}
		@Override
		public final char[] getArray() {
			return array;
		}
	}
	
	public static class I extends R1 {
		public final int[] array;
		public I(Object array, long longLength, int strSize, long base, Object gcobj, Object[] ref) {
			super(longLength, strSize, base, gcobj, ref);
			if(array != null && !(array instanceof int[])) throw new IllegalArgumentException();
			this.array = (int[])array;
		}
		@Override
		public final int[] getArray() {
			return array;
		}
	}
	
	public static class F extends R1 {
		public final float[] array;
		public F(Object array, long longLength, int strSize, long base, Object gcobj, Object[] ref) {
			super(longLength, strSize, base, gcobj, ref);
			if(array != null && !(array instanceof float[])) throw new IllegalArgumentException();
			this.array = (float[])array;
		}
		@Override
		public final float[] getArray() {
			return array;
		}
	}
	
	public static class L extends R1 {
		public final long[] array;
		public L(Object array, long longLength, int strSize, long base, Object gcobj, Object[] ref) {
			super(longLength, strSize, base, gcobj, ref);
			if(array != null && !(array instanceof long[])) throw new IllegalArgumentException("This struct does not allow array of type: " + array.getClass());
			this.array = (long[])array;
		}
		@Override
		public final long[] getArray() {
			return array;
		}
	}
	
	public static class D extends R1 {
		public final double[] array;
		public D(Object array, long longLength, int strSize, long base, Object gcobj, Object[] ref) {
			super(longLength, strSize, base, gcobj, ref);
			if(array != null && !(array instanceof double[])) throw new IllegalArgumentException("This struct does not allow array of type: " + array.getClass());
			this.array = (double[])array;
		}
		@Override
		public final double[] getArray() {
			return array;
		}
	}
	
	public static class A extends R1 {
		public final Object array;
		public A(Object array, long longLength, int strSize, long base, Object gcobj, Object[] ref) {
			super(longLength, strSize, base, gcobj, ref);
			this.array = array;
		}
		@Override
		public final Object getArray() {
			return array;
		}
	}
	
	public R1(long longLength, int strSize, long base, Object gcobj, Object[] ref) {
		this.base = base;
		this.longLength = longLength;
		this.strSize = strSize;
		this.length = longLength >= Integer.MAX_VALUE?Integer.MAX_VALUE:((int)longLength);
		this.gcobj = gcobj;
		this.ref = ref;
	}
	
	public final long idx(int i) {
		if(i < 0 || i >= length) StructIndexOutOfBoundsException.throwException(i);
		return i*(long)strSize+base;
	}
	public final long idx(long i) {
		if(i < 0 || i >= longLength) StructIndexOutOfBoundsException.throwException(i);
		return i*(long)strSize+base;
	}
	
	public final void idxCheck(int i) {
		if(i < 0 || i >= length) StructIndexOutOfBoundsException.throwException(i);
	}
	public final void idxCheck(long i) {
		if(i < 0 || i >= longLength) StructIndexOutOfBoundsException.throwException(i);
	}
	public static long idx(R1 r, int i) {
		if(i < 0 || i >= r.length) StructIndexOutOfBoundsException.throwException(i);
		return i*(long)r.strSize+r.base;
	}
	public static long idx(R1 r, long i) {
		if(i < 0 || i >= r.longLength) StructIndexOutOfBoundsException.throwException(i);
		return i*(long)r.strSize+r.base;
	}
	
	
//	public final long idx(int i) {
//		if(i < 0 || i >= length) StructIndexOutOfBoundsException.throwException(i);
////		return i*(long)strSize+base;
//		return i*(long)strSize;
//	}
//	public final long idx(long i) {
//		if(i < 0 || i >= longLength) StructIndexOutOfBoundsException.throwException(i);
////		return i*(long)strSize+base;
//		return i*(long)strSize;
//	}
	
	public final long idxCheckOnly(int i) {
		if(i < 0 || i >= length) StructIndexOutOfBoundsException.throwException(i);
//		return i*(long)strSize+base;
		return i;
	}
	public final long idxCheckOnly(long i) {
		if(i < 0 || i >= longLength) StructIndexOutOfBoundsException.throwException(i);
//		return i*(long)strSize+base;
		return i;
	}
	public final long idxCheckOnly(int i, int strSize) {
		if(i < 0 || i >= length) StructIndexOutOfBoundsException.throwException(i);
//		return i*(long)strSize+base;
		return i*(long)strSize;
	}
	public final long idxCheckOnly(long i, int strSize) {
		if(i < 0 || i >= longLength) StructIndexOutOfBoundsException.throwException(i);
//		return i*(long)strSize+base;
		return i*(long)strSize;
	}
	
	
//	
//	public static long idx(R1 r, int i) {
//		if(i < 0 || i >= r.length) StructIndexOutOfBoundsException.throwException(i);
////		return i*(long)r.strSize+r.base;
//		return i*(long)r.strSize;
//	}
//	public static long idx(R1 r, long i) {
//		if(i < 0 || i >= r.longLength) StructIndexOutOfBoundsException.throwException(i);
////		return i*(long)r.strSize+r.base;
//		return i*(long)r.strSize;
//	}
	
	public long base() {
		return base;
	}
	public long strSize() {
		return strSize;
	}
	public long longLenght() {
		return longLength;
	}
	
	public static R1 create(StructType type, ArrayType t, Object array, long longLength, long base, Object gcobj) {
		return create(type.type, t, array, longLength, type.size, base, gcobj, type.objectCount, type.objectOffset);
	}
	public static R1 create(StructType type, ArrayType t, Object array, long longLength, long base, Object gcobj, Object[] objData, int objDataOffset) {	
		if(type.objectOffset != null) {
			//A struct array which stores Objects/References in structs
			//allocates an array of Objects to store them.
			
			//Why do Struct References need Object array?
			//To store a strong reference to parent array so garbage collector knows about it
			
			long objsL = longLength*type.objectCount;
			if(objsL > Vars.MAX_HEAP_ARRAY) throw new OutOfMemoryError("Array exceeds number of Java object references: " + objsL);
			//Could support more then Integer.MAX_VALUE obj references
			//if anyone is going to need that
			int objs = (int)objsL;
			
			R1 r = create(type.type, t, array, longLength, type.size, base, gcobj, objData);
			
//			base = 0;
			//4 bytes to store the index
						
			int index = objDataOffset;
			for(long l = 0; l < longLength; l++) {
				for(int i = 0; i < type.objectOffset.length; i += 2) {
					int off = type.objectOffset[i];
					int count = type.objectOffset[i+1];

//					u.putInt(base+off, found);
//					u.putInt(base+off+4, index);
					
					Mem0.B.pI(r, base+off, index);
//					Mem0.B.pI(r, base+off, 0);
					Mem0.B.pI(r, base+off+4, index);
//					Mem0.B.pJ(r, base+off, (0L | index));
//					Mem0.B.pI(r, base+off+4, index);

					index += count;
				}
				base += type.size;
			}
			
			return r;
		}
		return create(type.type, t, array, longLength, type.size, base, gcobj, null);
	}	
//	public abstract long base();
//	public abstract long strSize();
//	public abstract long longLenght();
	public static R1 create(StructHeapType h, ArrayType t, Object array, long longLength, int strSize, long base, Object gcobj, int objsCount, int[] objOffsets) {	
		if(objOffsets != null) {
			//A struct array which stores Objects/References in structs
			//allocates an array of Objects to store them.
			
			//Why do Struct References need Object array?
			//To store a strong reference to parent array so garbage collector knows about it
			
			long objsL = longLength*objsCount;
			if(objsL > Vars.MAX_HEAP_ARRAY) throw new OutOfMemoryError("Array exceeds number of Java object references: " + objsL);
			//Could support more then Integer.MAX_VALUE obj references
			//if anyone is going to need that
			int objs = (int)objsL;
			
			R1 r = create(h, t, array, longLength, strSize, base, gcobj, new Object[objs]);
			
//			base = 0;
			//4 bytes to store the index
						
			int index = 0;
			for(long l = 0; l < longLength; l++) {
				for(int i = 0; i < objOffsets.length; i += 2) {
					int off = objOffsets[i];
					int count = objOffsets[i+1];

//					u.putInt(base+off, found);
//					u.putInt(base+off+4, index);
					
					Mem0.B.pI(r, base+off, index);
//					Mem0.B.pI(r, base+off, 0);
					Mem0.B.pI(r, base+off+4, index);
//					Mem0.B.pJ(r, base+off, (0L | index));
//					Mem0.B.pI(r, base+off+4, index);

					index += count;
				}
				base += strSize;
			}
			
			return r;
		}
		return create(h, t, array, longLength, strSize, base, gcobj, null);
	}
	public static R1 create(StructHeapType h, ArrayType t, Object array, long longLength, int strSize, long base, Object gcobj, Object[] ref) {		
		switch(h) {
			case All:
				//test .N perf
//				if(array == null) return new R1.N(array, longLength, strSize, base, gcobj, ref);
				if(array == null) return new R1.B(array, longLength, strSize, base, gcobj, ref);
				switch(t) {
					case Byte: return new R1.B(array, longLength, strSize, base, gcobj, ref);
					case Short: return new R1.S(array, longLength, strSize, base, gcobj, ref);
					case Char: return new R1.C(array, longLength, strSize, base, gcobj, ref);
					case Int: return new R1.I(array, longLength, strSize, base, gcobj, ref);
					case Float: return new R1.F(array, longLength, strSize, base, gcobj, ref);
					case Long: return new R1.L(array, longLength, strSize, base, gcobj, ref);
					case Double: return new R1.D(array, longLength, strSize, base, gcobj, ref);
				}
				break;
			case AllAsWellTestDoNotUse: return new R1.A(array, longLength, strSize, base, gcobj, ref);
			case Byte: return new R1.B(array, longLength, strSize, base, gcobj, ref);
			case Short: return new R1.S(array, longLength, strSize, base, gcobj, ref);
			case Char: return new R1.C(array, longLength, strSize, base, gcobj, ref);
			case Int: return new R1.I(array, longLength, strSize, base, gcobj, ref);
			case Float: return new R1.F(array, longLength, strSize, base, gcobj, ref);
			case Long: return new R1.L(array, longLength, strSize, base, gcobj, ref);
			case Double: return new R1.D(array, longLength, strSize, base, gcobj, ref);
			case None: return new R1.N(array, longLength, strSize, base, gcobj, ref);
		}
		throw new IllegalArgumentException();
	}
	public static R1.A wrapA(R1 r) {		
		if(r.getArray() != null) throw new IllegalArgumentException();
		if(r instanceof R1.A) return (R1.A)r;
		return new R1.A(null, r.longLength, r.strSize, r.base, r.gcobj, r.ref);
	}
	public static R1.B wrapB(R1 r) {		
		if(r.getArray() != null) throw new IllegalArgumentException();
		if(r instanceof R1.B) return (R1.B)r;
		return new R1.B(null, r.longLength, r.strSize, r.base, r.gcobj, r.ref);
	}
	public static R1.C wrapC(R1 r) {		
		if(r.getArray() != null) throw new IllegalArgumentException();
		if(r instanceof R1.C) return (R1.C)r;
		return new R1.C(null, r.longLength, r.strSize, r.base, r.gcobj, r.ref);
	}
	public static R1.D wrapD(R1 r) {		
		if(r.getArray() != null) throw new IllegalArgumentException();
		if(r instanceof R1.D) return (R1.D)r;
		return new R1.D(null, r.longLength, r.strSize, r.base, r.gcobj, r.ref);
	}
	public static R1.F wrapF(R1 r) {		
		if(r.getArray() != null) throw new IllegalArgumentException();
		if(r instanceof R1.F) return (R1.F)r;
		return new R1.F(null, r.longLength, r.strSize, r.base, r.gcobj, r.ref);
	}
	public static R1.I wrapI(R1 r) {		
		if(r.getArray() != null) throw new IllegalArgumentException();
		if(r instanceof R1.I) return (R1.I)r;
		return new R1.I(null, r.longLength, r.strSize, r.base, r.gcobj, r.ref);
	}
	public static R1.L wrapL(R1 r) {		
		if(r.getArray() != null) throw new IllegalArgumentException();
		if(r instanceof R1.L) return (R1.L)r;
		return new R1.L(null, r.longLength, r.strSize, r.base, r.gcobj, r.ref);
	}
	public static R1.N wrapN(R1 r) {		
		if(r.getArray() != null) throw new IllegalArgumentException();
		if(r instanceof R1.N) return (R1.N)r;
		return new R1.N(null, r.longLength, r.strSize, r.base, r.gcobj, r.ref);
	}
	public static R1.S wrapS(R1 r) {		
		if(r.getArray() != null) throw new IllegalArgumentException();
		if(r instanceof R1.S) return (R1.S)r;
		return new R1.S(null, r.longLength, r.strSize, r.base, r.gcobj, r.ref);
	}
	public abstract Object getArray();
}
