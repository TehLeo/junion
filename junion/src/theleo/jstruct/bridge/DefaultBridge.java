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
package theleo.jstruct.bridge;
import theleo.jstruct.exceptions.StructReferenceTypeMismatch;
import theleo.jstruct.hidden.R1;
import static theleo.jstruct.hidden.Vars.u;

/**
 * Implements a default brige interface between user code and memory.
 * 
 * Most of the methods directly access/modify the memory.
 * 
 * Since structures are aligned by the compiler,
 * each access is assumed to be aligned.
 *
 * @author Juraj Papp
 */
public final class DefaultBridge implements Bridge {

	@Override
	public final Object gL(final R1 r, final long l, int i) {
		Object arr = r.getArray();
		if(arr == null) i += u.getInt(null,l);
		else i += u.getInt(arr,l);
		return r.ref[i];
	}

	@Override
	public final <T> T pL(final R1 r, final long l, int i, final T o) {
		Object arr = r.getArray();
		if(arr == null) i += u.getInt(null,l);
		else i += u.getInt(arr,l);
		r.ref[i] = o;
		return o;
	}

	
	@Override
	public final R1 pR(final R1 r, final long l, final int add, final int i, final R1 r2, final long l2) {
//		Object o = gL(r,l, i);
//		long addr = gJ(r, l+add);
		pL(r, l, i, r2);
		pJ(r, l+add, l2);
		return r2;
	}
	
	@Override
	public final R1 pR(final R1 r, final long l, final R1 r2, long l2) {
//		long addr = gJ(r, l);
//      Object o = addr == 0?null:r;
		if(r != r2) {
			if(r2 == null) l2 = 0;
			else StructReferenceTypeMismatch.throwException();
		}
		pJ(r, l, l2);
		return r2;
	}
		
	@Override
	public final boolean gZ(final R1 r, final long l) {
		Object arr = r.getArray();
		if(arr == null) return u.getByte(null, l)!=0;
		else return u.getByte(arr, l)!=0;
	}
	@Override
	public final boolean pZ(final R1 r, final long l, final boolean f) {
		Object arr = r.getArray();
		if(arr == null) u.putByte(null, l, f?(byte)1:(byte)0);
		else u.putByte(arr, l, f?(byte)1:(byte)0);
		return f;
	}
	@Override
	public final byte gB(final R1 r, final long l) {
		Object arr = r.getArray();
		if(arr == null) return u.getByte(null, l);
		else return u.getByte(arr, l);
	}
	@Override
	public final byte pB(final R1 r, final long l, final byte f) {
		Object arr = r.getArray();
		if(arr == null) u.putByte(null, l, f);
		else u.putByte(arr, l, f);
		return f;
	}
	@Override
	public final char gC(final R1 r, final long l) {
		Object arr = r.getArray();
		if(arr == null) return u.getChar(null, l);
		else return u.getChar(arr, l);
	}
	@Override
	public final char pC(final R1 r, final long l, final char f) {
		Object arr = r.getArray();
		if(arr == null) u.putChar(null, l, f);
		else u.putChar(arr, l, f);
		return f;
	}
	@Override
	public final short gS(final R1 r, final long l) {
		Object arr = r.getArray();
		if(arr == null) return u.getShort(null, l);
		else return u.getShort(arr, l);
	}
	@Override
	public final short pS(final R1 r, final long l, final short f) {
		Object arr = r.getArray();
		if(arr == null) u.putShort(null, l, f);
		else u.putShort(arr, l, f);
		return f;
	}
	@Override
	public final int gI(final R1 r, final long l) {
		Object arr = r.getArray();
		if(arr == null) return u.getInt(null, l);
		else return u.getInt(arr, l);
	}
	@Override
	public final int pI(final R1 r, final long l, final int f) {
		Object arr = r.getArray();
		if(arr == null) u.putInt(null, l, f);
		else u.putInt(arr, l, f);
		return f;
	}
	@Override
	public final long gJ(final R1 r, final long l) {
		Object arr = r.getArray();
		if(arr == null) return u.getLong(null, l);
		else return u.getLong(arr, l);
	}
	@Override
	public final long pJ(final R1 r, final long l, final long f) {
		Object arr = r.getArray();
		if(arr == null) u.putLong(null, l, f);
		else u.putLong(arr, l, f);
		return f;
	}
	@Override
	public final float gF(final R1 r, final long l) {
		Object arr = r.getArray();
		if(arr == null) return u.getFloat(null, l);
		else return u.getFloat(arr, l);
	}
	@Override
	public final float pF(final R1 r, final long l, final float f) {
		Object arr = r.getArray();
		if(arr == null) u.putFloat(null, l, f);
		else u.putFloat(arr, l, f);
		return f;
	}
	@Override
	public final double gD(final R1 r, final long l) {
		Object arr = r.getArray();
		if(arr == null) return u.getDouble(null, l);
		else return u.getDouble(arr, l);
	}
	@Override
	public final double pD(final R1 r, final long l, final double f) {
		Object arr = r.getArray();
		if(arr == null) u.putDouble(null, l, f);
		else u.putDouble(arr, l, f);
		return f;
	}	
	
	@Override
	public final boolean gZ(final R1.A r, final long l) {
		Object arr = r.array;
		if(arr == null) return u.getByte(null, l)!=0;
		else return u.getByte(arr, l)!=0;
	}
	@Override
	public final boolean pZ(final R1.A r, final long l, final boolean f) {
		Object arr = r.array;
		if(arr == null) u.putByte(null, l, f?(byte)1:(byte)0);
		else u.putByte(arr, l, f?(byte)1:(byte)0);
		return f;
	}
	@Override
	public final byte gB(final R1.A r, final long l) {
		Object arr = r.array;
		if(arr == null) return u.getByte(null, l);
		else return u.getByte(arr, l);
	}
	@Override
	public final byte pB(final R1.A r, final long l, final byte f) {
		Object arr = r.array;
		if(arr == null) u.putByte(null, l, f);
		else u.putByte(arr, l, f);
		return f;
	}
	@Override
	public final char gC(final R1.A r, final long l) {
		Object arr = r.array;
		if(arr == null) return u.getChar(null, l);
		else return u.getChar(arr, l);
	}
	@Override
	public final char pC(final R1.A r, final long l, final char f) {
		Object arr = r.array;
		if(arr == null) u.putChar(null, l, f);
		else u.putChar(arr, l, f);
		return f;
	}
	@Override
	public final short gS(final R1.A r, final long l) {
		Object arr = r.array;
		if(arr == null) return u.getShort(null, l);
		else return u.getShort(arr, l);
	}
	@Override
	public final short pS(final R1.A r, final long l, final short f) {
		Object arr = r.array;
		if(arr == null) u.putShort(null, l, f);
		else u.putShort(arr, l, f);
		return f;
	}
	@Override
	public final int gI(final R1.A r, final long l) {
		Object arr = r.array;
		if(arr == null) return u.getInt(null, l);
		else return u.getInt(arr, l);
	}
	@Override
	public final int pI(final R1.A r, final long l, final int f) {
		Object arr = r.array;
		if(arr == null) u.putInt(null, l, f);
		else u.putInt(arr, l, f);
		return f;
	}
	@Override
	public final long gJ(final R1.A r, final long l) {
		Object arr = r.array;
		if(arr == null) return u.getLong(null, l);
		else return u.getLong(arr, l);
	}
	@Override
	public final long pJ(final R1.A r, final long l, final long f) {
		Object arr = r.array;
		if(arr == null) u.putLong(null, l, f);
		else u.putLong(arr, l, f);
		return f;
	}
	@Override
	public final float gF(final R1.A r, final long l) {
		Object arr = r.array;
		if(arr == null) return u.getFloat(null, l);
		else return u.getFloat(arr, l);
	}
	@Override
	public final float pF(final R1.A r, final long l, final float f) {
		Object arr = r.array;
		if(arr == null) u.putFloat(null, l, f);
		else u.putFloat(arr, l, f);
		return f;
	}
	@Override
	public final double gD(final R1.A r, final long l) {
		Object arr = r.array;
		if(arr == null) return u.getDouble(null, l);
		else return u.getDouble(arr, l);
	}
	@Override
	public final double pD(final R1.A r, final long l, final double f) {
		Object arr = r.array;
		if(arr == null) u.putDouble(null, l, f);
		else u.putDouble(arr, l, f);
		return f;
	}

	@Override
	public final boolean gZ(final R1.B r, final long l) {
		byte[] arr = r.array;
		if(arr == null) return u.getByte(null, l)!=0;
		else return u.getByte(arr, l)!=0;
	}
	@Override
	public final boolean pZ(final R1.B r, final long l, final boolean f) {
		byte[] arr = r.array;
		if(arr == null) u.putByte(null, l, f?(byte)1:(byte)0);
		else u.putByte(arr, l, f?(byte)1:(byte)0);
		return f;
	}
	@Override
	public final byte gB(final R1.B r, final long l) {
		byte[] arr = r.array;
		if(arr == null) return u.getByte(null, l);
		else return u.getByte(arr, l);
	}
	@Override
	public final byte pB(final R1.B r, final long l, final byte f) {
		byte[] arr = r.array;
		if(arr == null) u.putByte(null, l, f);
		else u.putByte(arr, l, f);
		return f;
	}
	@Override
	public final char gC(final R1.B r, final long l) {
		byte[] arr = r.array;
		if(arr == null) return u.getChar(null, l);
		else return u.getChar(arr, l);
	}
	@Override
	public final char pC(final R1.B r, final long l, final char f) {
		byte[] arr = r.array;
		if(arr == null) u.putChar(null, l, f);
		else u.putChar(arr, l, f);
		return f;
	}
	@Override
	public final short gS(final R1.B r, final long l) {
		byte[] arr = r.array;
		if(arr == null) return u.getShort(null, l);
		else return u.getShort(arr, l);
	}
	@Override
	public final short pS(final R1.B r, final long l, final short f) {
		byte[] arr = r.array;
		if(arr == null) u.putShort(null, l, f);
		else u.putShort(arr, l, f);
		return f;
	}
	@Override
	public final int gI(final R1.B r, final long l) {
		byte[] arr = r.array;
		if(arr == null) return u.getInt(null, l);
		else return u.getInt(arr, l);
	}
	@Override
	public final int pI(final R1.B r, final long l, final int f) {
		byte[] arr = r.array;
		if(arr == null) u.putInt(null, l, f);
		else u.putInt(arr, l, f);
		return f;
	}
	@Override
	public final long gJ(final R1.B r, final long l) {
		byte[] arr = r.array;
		if(arr == null) return u.getLong(null, l);
		else return u.getLong(arr, l);
	}
	@Override
	public final long pJ(final R1.B r, final long l, final long f) {
		byte[] arr = r.array;
		if(arr == null) u.putLong(null, l, f);
		else u.putLong(arr, l, f);
		return f;
	}
	@Override
	public final float gF(final R1.B r, final long l) {
		byte[] arr = r.array;
		if(arr == null) return u.getFloat(null, l);
		else return u.getFloat(arr, l);
	}
	@Override
	public final float pF(final R1.B r, final long l, final float f) {
		byte[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f);
		else u.putFloat(arr, l, f);
		return f;
	}
	@Override
	public final double gD(final R1.B r, final long l) {
		byte[] arr = r.array;
		if(arr == null) return u.getDouble(null, l);
		else return u.getDouble(arr, l);
	}
	@Override
	public final double pD(final R1.B r, final long l, final double f) {
		byte[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f);
		else u.putDouble(arr, l, f);
		return f;
	}

	@Override
	public final boolean gZ(final R1.C r, final long l) {
		char[] arr = r.array;
		if(arr == null) return u.getByte(null, l)!=0;
		else return u.getByte(arr, l)!=0;
	}
	@Override
	public final boolean pZ(final R1.C r, final long l, final boolean f) {
		char[] arr = r.array;
		if(arr == null) u.putByte(null, l, f?(byte)1:(byte)0);
		else u.putByte(arr, l, f?(byte)1:(byte)0);
		return f;
	}
	@Override
	public final byte gB(final R1.C r, final long l) {
		char[] arr = r.array;
		if(arr == null) return u.getByte(null, l);
		else return u.getByte(arr, l);
	}
	@Override
	public final byte pB(final R1.C r, final long l, final byte f) {
		char[] arr = r.array;
		if(arr == null) u.putByte(null, l, f);
		else u.putByte(arr, l, f);
		return f;
	}
	@Override
	public final char gC(final R1.C r, final long l) {
		char[] arr = r.array;
		if(arr == null) return u.getChar(null, l);
		else return u.getChar(arr, l);
	}
	@Override
	public final char pC(final R1.C r, final long l, final char f) {
		char[] arr = r.array;
		if(arr == null) u.putChar(null, l, f);
		else u.putChar(arr, l, f);
		return f;
	}
	@Override
	public final short gS(final R1.C r, final long l) {
		char[] arr = r.array;
		if(arr == null) return u.getShort(null, l);
		else return u.getShort(arr, l);
	}
	@Override
	public final short pS(final R1.C r, final long l, final short f) {
		char[] arr = r.array;
		if(arr == null) u.putShort(null, l, f);
		else u.putShort(arr, l, f);
		return f;
	}
	@Override
	public final int gI(final R1.C r, final long l) {
		char[] arr = r.array;
		if(arr == null) return u.getInt(null, l);
		else return u.getInt(arr, l);
	}
	@Override
	public final int pI(final R1.C r, final long l, final int f) {
		char[] arr = r.array;
		if(arr == null) u.putInt(null, l, f);
		else u.putInt(arr, l, f);
		return f;
	}
	@Override
	public final long gJ(final R1.C r, final long l) {
		char[] arr = r.array;
		if(arr == null) return u.getLong(null, l);
		else return u.getLong(arr, l);
	}
	@Override
	public final long pJ(final R1.C r, final long l, final long f) {
		char[] arr = r.array;
		if(arr == null) u.putLong(null, l, f);
		else u.putLong(arr, l, f);
		return f;
	}
	@Override
	public final float gF(final R1.C r, final long l) {
		char[] arr = r.array;
		if(arr == null) return u.getFloat(null, l);
		else return u.getFloat(arr, l);
	}
	@Override
	public final float pF(final R1.C r, final long l, final float f) {
		char[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f);
		else u.putFloat(arr, l, f);
		return f;
	}
	@Override
	public final double gD(final R1.C r, final long l) {
		char[] arr = r.array;
		if(arr == null) return u.getDouble(null, l);
		else return u.getDouble(arr, l);
	}
	@Override
	public final double pD(final R1.C r, final long l, final double f) {
		char[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f);
		else u.putDouble(arr, l, f);
		return f;
	}

	@Override
	public final boolean gZ(final R1.S r, final long l) {
		short[] arr = r.array;
		if(arr == null) return u.getByte(null, l)!=0;
		else return u.getByte(arr, l)!=0;
	}
	@Override
	public final boolean pZ(final R1.S r, final long l, final boolean f) {
		short[] arr = r.array;
		if(arr == null) u.putByte(null, l, f?(byte)1:(byte)0);
		else u.putByte(arr, l, f?(byte)1:(byte)0);
		return f;
	}
	@Override
	public final byte gB(final R1.S r, final long l) {
		short[] arr = r.array;
		if(arr == null) return u.getByte(null, l);
		else return u.getByte(arr, l);
	}
	@Override
	public final byte pB(final R1.S r, final long l, final byte f) {
		short[] arr = r.array;
		if(arr == null) u.putByte(null, l, f);
		else u.putByte(arr, l, f);
		return f;
	}
	@Override
	public final char gC(final R1.S r, final long l) {
		short[] arr = r.array;
		if(arr == null) return u.getChar(null, l);
		else return u.getChar(arr, l);
	}
	@Override
	public final char pC(final R1.S r, final long l, final char f) {
		short[] arr = r.array;
		if(arr == null) u.putChar(null, l, f);
		else u.putChar(arr, l, f);
		return f;
	}
	@Override
	public final short gS(final R1.S r, final long l) {
		short[] arr = r.array;
		if(arr == null) return u.getShort(null, l);
		else return u.getShort(arr, l);
	}
	@Override
	public final short pS(final R1.S r, final long l, final short f) {
		short[] arr = r.array;
		if(arr == null) u.putShort(null, l, f);
		else u.putShort(arr, l, f);
		return f;
	}
	@Override
	public final int gI(final R1.S r, final long l) {
		short[] arr = r.array;
		if(arr == null) return u.getInt(null, l);
		else return u.getInt(arr, l);
	}
	@Override
	public final int pI(final R1.S r, final long l, final int f) {
		short[] arr = r.array;
		if(arr == null) u.putInt(null, l, f);
		else u.putInt(arr, l, f);
		return f;
	}
	@Override
	public final long gJ(final R1.S r, final long l) {
		short[] arr = r.array;
		if(arr == null) return u.getLong(null, l);
		else return u.getLong(arr, l);
	}
	@Override
	public final long pJ(final R1.S r, final long l, final long f) {
		short[] arr = r.array;
		if(arr == null) u.putLong(null, l, f);
		else u.putLong(arr, l, f);
		return f;
	}
	@Override
	public final float gF(final R1.S r, final long l) {
		short[] arr = r.array;
		if(arr == null) return u.getFloat(null, l);
		else return u.getFloat(arr, l);
	}
	@Override
	public final float pF(final R1.S r, final long l, final float f) {
		short[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f);
		else u.putFloat(arr, l, f);
		return f;
	}
	@Override
	public final double gD(final R1.S r, final long l) {
		short[] arr = r.array;
		if(arr == null) return u.getDouble(null, l);
		else return u.getDouble(arr, l);
	}
	@Override
	public final double pD(final R1.S r, final long l, final double f) {
		short[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f);
		else u.putDouble(arr, l, f);
		return f;
	}

	@Override
	public final boolean gZ(final R1.I r, final long l) {
		int[] arr = r.array;
		if(arr == null) return u.getByte(null, l)!=0;
		else return u.getByte(arr, l)!=0;
	}
	@Override
	public final boolean pZ(final R1.I r, final long l, final boolean f) {
		int[] arr = r.array;
		if(arr == null) u.putByte(null, l, f?(byte)1:(byte)0);
		else u.putByte(arr, l, f?(byte)1:(byte)0);
		return f;
	}
	@Override
	public final byte gB(final R1.I r, final long l) {
		int[] arr = r.array;
		if(arr == null) return u.getByte(null, l);
		else return u.getByte(arr, l);
	}
	@Override
	public final byte pB(final R1.I r, final long l, final byte f) {
		int[] arr = r.array;
		if(arr == null) u.putByte(null, l, f);
		else u.putByte(arr, l, f);
		return f;
	}
	@Override
	public final char gC(final R1.I r, final long l) {
		int[] arr = r.array;
		if(arr == null) return u.getChar(null, l);
		else return u.getChar(arr, l);
	}
	@Override
	public final char pC(final R1.I r, final long l, final char f) {
		int[] arr = r.array;
		if(arr == null) u.putChar(null, l, f);
		else u.putChar(arr, l, f);
		return f;
	}
	@Override
	public final short gS(final R1.I r, final long l) {
		int[] arr = r.array;
		if(arr == null) return u.getShort(null, l);
		else return u.getShort(arr, l);
	}
	@Override
	public final short pS(final R1.I r, final long l, final short f) {
		int[] arr = r.array;
		if(arr == null) u.putShort(null, l, f);
		else u.putShort(arr, l, f);
		return f;
	}
	@Override
	public final int gI(final R1.I r, final long l) {
		int[] arr = r.array;
		if(arr == null) return u.getInt(null, l);
		else return u.getInt(arr, l);
	}
	@Override
	public final int pI(final R1.I r, final long l, final int f) {
		int[] arr = r.array;
		if(arr == null) u.putInt(null, l, f);
		else u.putInt(arr, l, f);
		return f;
	}
	@Override
	public final long gJ(final R1.I r, final long l) {
		int[] arr = r.array;
		if(arr == null) return u.getLong(null, l);
		else return u.getLong(arr, l);
	}
	@Override
	public final long pJ(final R1.I r, final long l, final long f) {
		int[] arr = r.array;
		if(arr == null) u.putLong(null, l, f);
		else u.putLong(arr, l, f);
		return f;
	}
	@Override
	public final float gF(final R1.I r, final long l) {
		int[] arr = r.array;
		if(arr == null) return u.getFloat(null, l);
		else return u.getFloat(arr, l);
	}
	@Override
	public final float pF(final R1.I r, final long l, final float f) {
		int[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f);
		else u.putFloat(arr, l, f);
		return f;
	}
	@Override
	public final double gD(final R1.I r, final long l) {
		int[] arr = r.array;
		if(arr == null) return u.getDouble(null, l);
		else return u.getDouble(arr, l);
	}
	@Override
	public final double pD(final R1.I r, final long l, final double f) {
		int[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f);
		else u.putDouble(arr, l, f);
		return f;
	}

	@Override
	public final boolean gZ(final R1.L r, final long l) {
		long[] arr = r.array;
		if(arr == null) return u.getByte(null, l)!=0;
		else return u.getByte(arr, l)!=0;
	}
	@Override
	public final boolean pZ(final R1.L r, final long l, final boolean f) {
		long[] arr = r.array;
		if(arr == null) u.putByte(null, l, f?(byte)1:(byte)0);
		else u.putByte(arr, l, f?(byte)1:(byte)0);
		return f;
	}
	@Override
	public final byte gB(final R1.L r, final long l) {
		long[] arr = r.array;
		if(arr == null) return u.getByte(null, l);
		else return u.getByte(arr, l);
	}
	@Override
	public final byte pB(final R1.L r, final long l, final byte f) {
		long[] arr = r.array;
		if(arr == null) u.putByte(null, l, f);
		else u.putByte(arr, l, f);
		return f;
	}
	@Override
	public final char gC(final R1.L r, final long l) {
		long[] arr = r.array;
		if(arr == null) return u.getChar(null, l);
		else return u.getChar(arr, l);
	}
	@Override
	public final char pC(final R1.L r, final long l, final char f) {
		long[] arr = r.array;
		if(arr == null) u.putChar(null, l, f);
		else u.putChar(arr, l, f);
		return f;
	}
	@Override
	public final short gS(final R1.L r, final long l) {
		long[] arr = r.array;
		if(arr == null) return u.getShort(null, l);
		else return u.getShort(arr, l);
	}
	@Override
	public final short pS(final R1.L r, final long l, final short f) {
		long[] arr = r.array;
		if(arr == null) u.putShort(null, l, f);
		else u.putShort(arr, l, f);
		return f;
	}
	@Override
	public final int gI(final R1.L r, final long l) {
		long[] arr = r.array;
		if(arr == null) return u.getInt(null, l);
		else return u.getInt(arr, l);
	}
	@Override
	public final int pI(final R1.L r, final long l, final int f) {
		long[] arr = r.array;
		if(arr == null) u.putInt(null, l, f);
		else u.putInt(arr, l, f);
		return f;
	}
	@Override
	public final long gJ(final R1.L r, final long l) {
		long[] arr = r.array;
		if(arr == null) return u.getLong(null, l);
		else return u.getLong(arr, l);
	}
	@Override
	public final long pJ(final R1.L r, final long l, final long f) {
		long[] arr = r.array;
		if(arr == null) u.putLong(null, l, f);
		else u.putLong(arr, l, f);
		return f;
	}
	@Override
	public final float gF(final R1.L r, final long l) {
		long[] arr = r.array;
		if(arr == null) return u.getFloat(null, l);
		else return u.getFloat(arr, l);
	}
	@Override
	public final float pF(final R1.L r, final long l, final float f) {
		long[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f);
		else u.putFloat(arr, l, f);
		return f;
	}
	@Override
	public final double gD(final R1.L r, final long l) {
		long[] arr = r.array;
		if(arr == null) return u.getDouble(null, l);
		else return u.getDouble(arr, l);
	}
	@Override
	public final double pD(final R1.L r, final long l, final double f) {
		long[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f);
		else u.putDouble(arr, l, f);
		return f;
	}

	@Override
	public final boolean gZ(final R1.F r, final long l) {
		float[] arr = r.array;
		if(arr == null) return u.getByte(null, l)!=0;
		else return u.getByte(arr, l)!=0;
	}
	@Override
	public final boolean pZ(final R1.F r, final long l, final boolean f) {
		float[] arr = r.array;
		if(arr == null) u.putByte(null, l, f?(byte)1:(byte)0);
		else u.putByte(arr, l, f?(byte)1:(byte)0);
		return f;
	}
	@Override
	public final byte gB(final R1.F r, final long l) {
		float[] arr = r.array;
		if(arr == null) return u.getByte(null, l);
		else return u.getByte(arr, l);
	}
	@Override
	public final byte pB(final R1.F r, final long l, final byte f) {
		float[] arr = r.array;
		if(arr == null) u.putByte(null, l, f);
		else u.putByte(arr, l, f);
		return f;
	}
	@Override
	public final char gC(final R1.F r, final long l) {
		float[] arr = r.array;
		if(arr == null) return u.getChar(null, l);
		else return u.getChar(arr, l);
	}
	@Override
	public final char pC(final R1.F r, final long l, final char f) {
		float[] arr = r.array;
		if(arr == null) u.putChar(null, l, f);
		else u.putChar(arr, l, f);
		return f;
	}
	@Override
	public final short gS(final R1.F r, final long l) {
		float[] arr = r.array;
		if(arr == null) return u.getShort(null, l);
		else return u.getShort(arr, l);
	}
	@Override
	public final short pS(final R1.F r, final long l, final short f) {
		float[] arr = r.array;
		if(arr == null) u.putShort(null, l, f);
		else u.putShort(arr, l, f);
		return f;
	}
	@Override
	public final int gI(final R1.F r, final long l) {
		float[] arr = r.array;
		if(arr == null) return u.getInt(null, l);
		else return u.getInt(arr, l);
	}
	@Override
	public final int pI(final R1.F r, final long l, final int f) {
		float[] arr = r.array;
		if(arr == null) u.putInt(null, l, f);
		else u.putInt(arr, l, f);
		return f;
	}
	@Override
	public final long gJ(final R1.F r, final long l) {
		float[] arr = r.array;
		if(arr == null) return u.getLong(null, l);
		else return u.getLong(arr, l);
	}
	@Override
	public final long pJ(final R1.F r, final long l, final long f) {
		float[] arr = r.array;
		if(arr == null) u.putLong(null, l, f);
		else u.putLong(arr, l, f);
		return f;
	}
	@Override
	public final float gF(final R1.F r, final long l) {
		float[] arr = r.array;
		if(arr == null) return u.getFloat(null, l);
		else return u.getFloat(arr, l);
	}
	@Override
	public final float pF(final R1.F r, final long l, final float f) {
		float[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f);
		else u.putFloat(arr, l, f);
		return f;
	}
	@Override
	public final double gD(final R1.F r, final long l) {
		float[] arr = r.array;
		if(arr == null) return u.getDouble(null, l);
		else return u.getDouble(arr, l);
	}
	@Override
	public final double pD(final R1.F r, final long l, final double f) {
		float[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f);
		else u.putDouble(arr, l, f);
		return f;
	}

	@Override
	public final boolean gZ(final R1.D r, final long l) {
		double[] arr = r.array;
		if(arr == null) return u.getByte(null, l)!=0;
		else return u.getByte(arr, l)!=0;
	}
	@Override
	public final boolean pZ(final R1.D r, final long l, final boolean f) {
		double[] arr = r.array;
		if(arr == null) u.putByte(null, l, f?(byte)1:(byte)0);
		else u.putByte(arr, l, f?(byte)1:(byte)0);
		return f;
	}
	@Override
	public final byte gB(final R1.D r, final long l) {
		double[] arr = r.array;
		if(arr == null) return u.getByte(null, l);
		else return u.getByte(arr, l);
	}
	@Override
	public final byte pB(final R1.D r, final long l, final byte f) {
		double[] arr = r.array;
		if(arr == null) u.putByte(null, l, f);
		else u.putByte(arr, l, f);
		return f;
	}
	@Override
	public final char gC(final R1.D r, final long l) {
		double[] arr = r.array;
		if(arr == null) return u.getChar(null, l);
		else return u.getChar(arr, l);
	}
	@Override
	public final char pC(final R1.D r, final long l, final char f) {
		double[] arr = r.array;
		if(arr == null) u.putChar(null, l, f);
		else u.putChar(arr, l, f);
		return f;
	}
	@Override
	public final short gS(final R1.D r, final long l) {
		double[] arr = r.array;
		if(arr == null) return u.getShort(null, l);
		else return u.getShort(arr, l);
	}
	@Override
	public final short pS(final R1.D r, final long l, final short f) {
		double[] arr = r.array;
		if(arr == null) u.putShort(null, l, f);
		else u.putShort(arr, l, f);
		return f;
	}
	@Override
	public final int gI(final R1.D r, final long l) {
		double[] arr = r.array;
		if(arr == null) return u.getInt(null, l);
		else return u.getInt(arr, l);
	}
	@Override
	public final int pI(final R1.D r, final long l, final int f) {
		double[] arr = r.array;
		if(arr == null) u.putInt(null, l, f);
		else u.putInt(arr, l, f);
		return f;
	}
	@Override
	public final long gJ(final R1.D r, final long l) {
		double[] arr = r.array;
		if(arr == null) return u.getLong(null, l);
		else return u.getLong(arr, l);
	}
	@Override
	public final long pJ(final R1.D r, final long l, final long f) {
		double[] arr = r.array;
		if(arr == null) u.putLong(null, l, f);
		else u.putLong(arr, l, f);
		return f;
	}
	@Override
	public final float gF(final R1.D r, final long l) {
		double[] arr = r.array;
		if(arr == null) return u.getFloat(null, l);
		else return u.getFloat(arr, l);
	}
	@Override
	public final float pF(final R1.D r, final long l, final float f) {
		double[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f);
		else u.putFloat(arr, l, f);
		return f;
	}
	@Override
	public final double gD(final R1.D r, final long l) {
		double[] arr = r.array;
		if(arr == null) return u.getDouble(null, l);
		else return u.getDouble(arr, l);
	}
	@Override
	public final double pD(final R1.D r, final long l, final double f) {
		double[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f);
		else u.putDouble(arr, l, f);
		return f;
	}
	
	
	
	
	@Override
	public final boolean gZ(R1.N r, final long l) {
		return u.getByte(null, l)!=0;
	}
	@Override
	public final boolean pZ(R1.N r, final long l, final boolean f) {
		u.putByte(null, l, f?(byte)1:(byte)0);
		return f;
	}
	@Override
	public final byte gB(R1.N r, final long l) {		
		return u.getByte(null, l);
	}
	@Override
	public final byte pB(R1.N r, final long l, final byte f) {
		u.putByte(null, l, f);
		return f;
	}
	@Override
	public final char gC(R1.N r, final long l) {
		return u.getChar(null, l);
	}
	@Override
	public final char pC(R1.N r, final long l, final char f) {
		u.putChar(null, l, f);
		return f;
	}
	@Override
	public final short gS(R1.N r, final long l) {
		return u.getShort(null, l);
	}
	@Override
	public final short pS(R1.N r, final long l, final short f) {
		u.putShort(null, l, f);
		return f;
	}
	@Override
	public final int gI(R1.N r, final long l) {
		return u.getInt(null, l);
	}
	@Override
	public final int pI(R1.N r, final long l, final int f) {
		u.putInt(null, l, f);
		return f;
	}
	@Override
	public final long gJ(R1.N r, final long l) {
		return u.getLong(null, l);
	}
	@Override
	public final long pJ(R1.N r, final long l, final long f) {
		u.putLong(null, l, f);
		return f;
	}
	@Override
	public final float gF(R1.N r, final long l) {
		return u.getFloat(null, l);
	}
	@Override
	public final float pF(R1.N r, final long l, final float f) {
		u.putFloat(null, l, f);
		return f;
	}
	@Override
	public final double gD(R1.N r, final long l) {
		return u.getDouble(null, l);
	}
	@Override
	public final double pD(R1.N r, final long l, final double f) {
		u.putDouble(null, l, f);
		return f;
	}
	

	//-----------------------assignment operators 
	@Override
	public final boolean pZAnd(final R1 r, final long l, boolean f) {
		Object arr = r.getArray();
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) & b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) & b));
		return b != 0;
	}
	@Override
	public final boolean pZOr(final R1 r, final long l, boolean f) {
		Object arr = r.getArray();
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) | b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) | b));
		return b != 0;
	}
	@Override
	public final boolean pZXor(final R1 r, final long l, boolean f) {
		Object arr = r.getArray();
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) ^ b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) ^ b));
		return b != 0;
	}
	@Override
	public final byte pBAdd(final R1 r, final long l, byte f) {
		Object arr = r.getArray();
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) + f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) + f));
		return f;
	}
	@Override
	public final byte pBSub(final R1 r, final long l, byte f) {
		Object arr = r.getArray();
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) - f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) - f));
		return f;
	}
	@Override
	public final byte pBMul(final R1 r, final long l, byte f) {
		Object arr = r.getArray();
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) * f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) * f));
		return f;
	}
	@Override
	public final byte pBDiv(final R1 r, final long l, byte f) {
		Object arr = r.getArray();
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) / f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) / f));
		return f;
	}
	@Override
	public final byte pBMod(final R1 r, final long l, byte f) {
		Object arr = r.getArray();
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) % f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) % f));
		return f;
	}
	@Override
	public final byte pBAnd(final R1 r, final long l, byte f) {
		Object arr = r.getArray();
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) & f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) & f));
		return f;
	}
	@Override
	public final byte pBOr(final R1 r, final long l, byte f) {
		Object arr = r.getArray();
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) | f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) | f));
		return f;
	}
	@Override
	public final byte pBXor(final R1 r, final long l, byte f) {
		Object arr = r.getArray();
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) ^ f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) ^ f));
		return f;
	}
	@Override
	public final byte pBLL(final R1 r, final long l, byte f) {
		Object arr = r.getArray();
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) << f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) << f));
		return f;
	}
	@Override
	public final byte pBRR(final R1 r, final long l, byte f) {
		Object arr = r.getArray();
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >> f));
		return f;
	}
	@Override
	public final byte pBRRR(final R1 r, final long l, byte f) {
		Object arr = r.getArray();
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >>> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >>> f));
		return f;
	}
	@Override
	public final char pCAdd(final R1 r, final long l, char f) {
		Object arr = r.getArray();
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) + f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) + f));
		return f;
	}
	@Override
	public final char pCSub(final R1 r, final long l, char f) {
		Object arr = r.getArray();
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) - f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) - f));
		return f;
	}
	@Override
	public final char pCMul(final R1 r, final long l, char f) {
		Object arr = r.getArray();
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) * f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) * f));
		return f;
	}
	@Override
	public final char pCDiv(final R1 r, final long l, char f) {
		Object arr = r.getArray();
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) / f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) / f));
		return f;
	}
	@Override
	public final char pCMod(final R1 r, final long l, char f) {
		Object arr = r.getArray();
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) % f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) % f));
		return f;
	}
	@Override
	public final char pCAnd(final R1 r, final long l, char f) {
		Object arr = r.getArray();
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) & f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) & f));
		return f;
	}
	@Override
	public final char pCOr(final R1 r, final long l, char f) {
		Object arr = r.getArray();
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) | f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) | f));
		return f;
	}
	@Override
	public final char pCXor(final R1 r, final long l, char f) {
		Object arr = r.getArray();
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) ^ f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) ^ f));
		return f;
	}
	@Override
	public final char pCLL(final R1 r, final long l, char f) {
		Object arr = r.getArray();
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) << f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) << f));
		return f;
	}
	@Override
	public final char pCRR(final R1 r, final long l, char f) {
		Object arr = r.getArray();
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >> f));
		return f;
	}
	@Override
	public final char pCRRR(final R1 r, final long l, char f) {
		Object arr = r.getArray();
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >>> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >>> f));
		return f;
	}
	@Override
	public final short pSAdd(final R1 r, final long l, short f) {
		Object arr = r.getArray();
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) + f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) + f));
		return f;
	}
	@Override
	public final short pSSub(final R1 r, final long l, short f) {
		Object arr = r.getArray();
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) - f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) - f));
		return f;
	}
	@Override
	public final short pSMul(final R1 r, final long l, short f) {
		Object arr = r.getArray();
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) * f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) * f));
		return f;
	}
	@Override
	public final short pSDiv(final R1 r, final long l, short f) {
		Object arr = r.getArray();
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) / f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) / f));
		return f;
	}
	@Override
	public final short pSMod(final R1 r, final long l, short f) {
		Object arr = r.getArray();
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) % f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) % f));
		return f;
	}
	@Override
	public final short pSAnd(final R1 r, final long l, short f) {
		Object arr = r.getArray();
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) & f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) & f));
		return f;
	}
	@Override
	public final short pSOr(final R1 r, final long l, short f) {
		Object arr = r.getArray();
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) | f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) | f));
		return f;
	}
	@Override
	public final short pSXor(final R1 r, final long l, short f) {
		Object arr = r.getArray();
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) ^ f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) ^ f));
		return f;
	}
	@Override
	public final short pSLL(final R1 r, final long l, short f) {
		Object arr = r.getArray();
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) << f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) << f));
		return f;
	}
	@Override
	public final short pSRR(final R1 r, final long l, short f) {
		Object arr = r.getArray();
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >> f));
		return f;
	}
	@Override
	public final short pSRRR(final R1 r, final long l, short f) {
		Object arr = r.getArray();
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >>> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >>> f));
		return f;
	}
	@Override
	public final int pIAdd(final R1 r, final long l, int f) {
		Object arr = r.getArray();
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) + f);
		else u.putInt(arr, l, f = u.getInt(arr, l) + f);
		return f;
	}
	@Override
	public final int pISub(final R1 r, final long l, int f) {
		Object arr = r.getArray();
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) - f);
		else u.putInt(arr, l, f = u.getInt(arr, l) - f);
		return f;
	}
	@Override
	public final int pIMul(final R1 r, final long l, int f) {
		Object arr = r.getArray();
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) * f);
		else u.putInt(arr, l, f = u.getInt(arr, l) * f);
		return f;
	}
	@Override
	public final int pIDiv(final R1 r, final long l, int f) {
		Object arr = r.getArray();
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) / f);
		else u.putInt(arr, l, f = u.getInt(arr, l) / f);
		return f;
	}
	@Override
	public final int pIMod(final R1 r, final long l, int f) {
		Object arr = r.getArray();
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) % f);
		else u.putInt(arr, l, f = u.getInt(arr, l) % f);
		return f;
	}
	@Override
	public final int pIAnd(final R1 r, final long l, int f) {
		Object arr = r.getArray();
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) & f);
		else u.putInt(arr, l, f = u.getInt(arr, l) & f);
		return f;
	}
	@Override
	public final int pIOr(final R1 r, final long l, int f) {
		Object arr = r.getArray();
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) | f);
		else u.putInt(arr, l, f = u.getInt(arr, l) | f);
		return f;
	}
	@Override
	public final int pIXor(final R1 r, final long l, int f) {
		Object arr = r.getArray();
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) ^ f);
		else u.putInt(arr, l, f = u.getInt(arr, l) ^ f);
		return f;
	}
	@Override
	public final int pILL(final R1 r, final long l, int f) {
		Object arr = r.getArray();
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) << f);
		else u.putInt(arr, l, f = u.getInt(arr, l) << f);
		return f;
	}
	@Override
	public final int pIRR(final R1 r, final long l, int f) {
		Object arr = r.getArray();
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >> f);
		return f;
	}
	@Override
	public final int pIRRR(final R1 r, final long l, int f) {
		Object arr = r.getArray();
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >>> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >>> f);
		return f;
	}
	@Override
	public final long pJAdd(final R1 r, final long l, long f) {
		Object arr = r.getArray();
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) + f);
		else u.putLong(arr, l, f = u.getLong(arr, l) + f);
		return f;
	}
	@Override
	public final long pJSub(final R1 r, final long l, long f) {
		Object arr = r.getArray();
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) - f);
		else u.putLong(arr, l, f = u.getLong(arr, l) - f);
		return f;
	}
	@Override
	public final long pJMul(final R1 r, final long l, long f) {
		Object arr = r.getArray();
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) * f);
		else u.putLong(arr, l, f = u.getLong(arr, l) * f);
		return f;
	}
	@Override
	public final long pJDiv(final R1 r, final long l, long f) {
		Object arr = r.getArray();
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) / f);
		else u.putLong(arr, l, f = u.getLong(arr, l) / f);
		return f;
	}
	@Override
	public final long pJMod(final R1 r, final long l, long f) {
		Object arr = r.getArray();
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) % f);
		else u.putLong(arr, l, f = u.getLong(arr, l) % f);
		return f;
	}
	@Override
	public final long pJAnd(final R1 r, final long l, long f) {
		Object arr = r.getArray();
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) & f);
		else u.putLong(arr, l, f = u.getLong(arr, l) & f);
		return f;
	}
	@Override
	public final long pJOr(final R1 r, final long l, long f) {
		Object arr = r.getArray();
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) | f);
		else u.putLong(arr, l, f = u.getLong(arr, l) | f);
		return f;
	}
	@Override
	public final long pJXor(final R1 r, final long l, long f) {
		Object arr = r.getArray();
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) ^ f);
		else u.putLong(arr, l, f = u.getLong(arr, l) ^ f);
		return f;
	}
	@Override
	public final long pJLL(final R1 r, final long l, long f) {
		Object arr = r.getArray();
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) << f);
		else u.putLong(arr, l, f = u.getLong(arr, l) << f);
		return f;
	}
	@Override
	public final long pJRR(final R1 r, final long l, long f) {
		Object arr = r.getArray();
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >> f);
		return f;
	}
	@Override
	public final long pJRRR(final R1 r, final long l, long f) {
		Object arr = r.getArray();
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >>> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >>> f);
		return f;
	}
	@Override
	public final float pFAdd(final R1 r, final long l, float f) {
		Object arr = r.getArray();
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) + f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) + f);
		return f;
	}
	@Override
	public final float pFSub(final R1 r, final long l, float f) {
		Object arr = r.getArray();
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) - f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) - f);
		return f;
	}
	@Override
	public final float pFMul(final R1 r, final long l, float f) {
		Object arr = r.getArray();
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) * f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) * f);
		return f;
	}
	@Override
	public final float pFDiv(final R1 r, final long l, float f) {
		Object arr = r.getArray();
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) / f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) / f);
		return f;
	}
	@Override
	public final float pFMod(final R1 r, final long l, float f) {
		Object arr = r.getArray();
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) % f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) % f);
		return f;
	}
	@Override
	public final double pDAdd(final R1 r, final long l, double f) {
		Object arr = r.getArray();
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) + f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) + f);
		return f;
	}
	@Override
	public final double pDSub(final R1 r, final long l, double f) {
		Object arr = r.getArray();
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) - f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) - f);
		return f;
	}
	@Override
	public final double pDMul(final R1 r, final long l, double f) {
		Object arr = r.getArray();
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) * f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) * f);
		return f;
	}
	@Override
	public final double pDDiv(final R1 r, final long l, double f) {
		Object arr = r.getArray();
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) / f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) / f);
		return f;
	}
	@Override
	public final double pDMod(final R1 r, final long l, double f) {
		Object arr = r.getArray();
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) % f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) % f);
		return f;
	}
	//-------------------
	
	@Override
	public final boolean pZAnd(final R1.A r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) & b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) & b));
		return b != 0;
	}
	@Override
	public final boolean pZOr(final R1.A r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) | b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) | b));
		return b != 0;
	}
	@Override
	public final boolean pZXor(final R1.A r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) ^ b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) ^ b));
		return b != 0;
	}
	@Override
	public final byte pBAdd(final R1.A r, final long l, byte f) {
		Object arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) + f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) + f));
		return f;
	}
	@Override
	public final byte pBSub(final R1.A r, final long l, byte f) {
		Object arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) - f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) - f));
		return f;
	}
	@Override
	public final byte pBMul(final R1.A r, final long l, byte f) {
		Object arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) * f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) * f));
		return f;
	}
	@Override
	public final byte pBDiv(final R1.A r, final long l, byte f) {
		Object arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) / f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) / f));
		return f;
	}
	@Override
	public final byte pBMod(final R1.A r, final long l, byte f) {
		Object arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) % f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) % f));
		return f;
	}
	@Override
	public final byte pBAnd(final R1.A r, final long l, byte f) {
		Object arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) & f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) & f));
		return f;
	}
	@Override
	public final byte pBOr(final R1.A r, final long l, byte f) {
		Object arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) | f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) | f));
		return f;
	}
	@Override
	public final byte pBXor(final R1.A r, final long l, byte f) {
		Object arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) ^ f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) ^ f));
		return f;
	}
	@Override
	public final byte pBLL(final R1.A r, final long l, byte f) {
		Object arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) << f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) << f));
		return f;
	}
	@Override
	public final byte pBRR(final R1.A r, final long l, byte f) {
		Object arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >> f));
		return f;
	}
	@Override
	public final byte pBRRR(final R1.A r, final long l, byte f) {
		Object arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >>> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >>> f));
		return f;
	}
	@Override
	public final char pCAdd(final R1.A r, final long l, char f) {
		Object arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) + f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) + f));
		return f;
	}
	@Override
	public final char pCSub(final R1.A r, final long l, char f) {
		Object arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) - f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) - f));
		return f;
	}
	@Override
	public final char pCMul(final R1.A r, final long l, char f) {
		Object arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) * f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) * f));
		return f;
	}
	@Override
	public final char pCDiv(final R1.A r, final long l, char f) {
		Object arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) / f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) / f));
		return f;
	}
	@Override
	public final char pCMod(final R1.A r, final long l, char f) {
		Object arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) % f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) % f));
		return f;
	}
	@Override
	public final char pCAnd(final R1.A r, final long l, char f) {
		Object arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) & f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) & f));
		return f;
	}
	@Override
	public final char pCOr(final R1.A r, final long l, char f) {
		Object arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) | f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) | f));
		return f;
	}
	@Override
	public final char pCXor(final R1.A r, final long l, char f) {
		Object arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) ^ f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) ^ f));
		return f;
	}
	@Override
	public final char pCLL(final R1.A r, final long l, char f) {
		Object arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) << f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) << f));
		return f;
	}
	@Override
	public final char pCRR(final R1.A r, final long l, char f) {
		Object arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >> f));
		return f;
	}
	@Override
	public final char pCRRR(final R1.A r, final long l, char f) {
		Object arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >>> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >>> f));
		return f;
	}
	@Override
	public final short pSAdd(final R1.A r, final long l, short f) {
		Object arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) + f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) + f));
		return f;
	}
	@Override
	public final short pSSub(final R1.A r, final long l, short f) {
		Object arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) - f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) - f));
		return f;
	}
	@Override
	public final short pSMul(final R1.A r, final long l, short f) {
		Object arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) * f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) * f));
		return f;
	}
	@Override
	public final short pSDiv(final R1.A r, final long l, short f) {
		Object arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) / f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) / f));
		return f;
	}
	@Override
	public final short pSMod(final R1.A r, final long l, short f) {
		Object arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) % f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) % f));
		return f;
	}
	@Override
	public final short pSAnd(final R1.A r, final long l, short f) {
		Object arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) & f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) & f));
		return f;
	}
	@Override
	public final short pSOr(final R1.A r, final long l, short f) {
		Object arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) | f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) | f));
		return f;
	}
	@Override
	public final short pSXor(final R1.A r, final long l, short f) {
		Object arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) ^ f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) ^ f));
		return f;
	}
	@Override
	public final short pSLL(final R1.A r, final long l, short f) {
		Object arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) << f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) << f));
		return f;
	}
	@Override
	public final short pSRR(final R1.A r, final long l, short f) {
		Object arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >> f));
		return f;
	}
	@Override
	public final short pSRRR(final R1.A r, final long l, short f) {
		Object arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >>> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >>> f));
		return f;
	}
	@Override
	public final int pIAdd(final R1.A r, final long l, int f) {
		Object arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) + f);
		else u.putInt(arr, l, f = u.getInt(arr, l) + f);
		return f;
	}
	@Override
	public final int pISub(final R1.A r, final long l, int f) {
		Object arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) - f);
		else u.putInt(arr, l, f = u.getInt(arr, l) - f);
		return f;
	}
	@Override
	public final int pIMul(final R1.A r, final long l, int f) {
		Object arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) * f);
		else u.putInt(arr, l, f = u.getInt(arr, l) * f);
		return f;
	}
	@Override
	public final int pIDiv(final R1.A r, final long l, int f) {
		Object arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) / f);
		else u.putInt(arr, l, f = u.getInt(arr, l) / f);
		return f;
	}
	@Override
	public final int pIMod(final R1.A r, final long l, int f) {
		Object arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) % f);
		else u.putInt(arr, l, f = u.getInt(arr, l) % f);
		return f;
	}
	@Override
	public final int pIAnd(final R1.A r, final long l, int f) {
		Object arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) & f);
		else u.putInt(arr, l, f = u.getInt(arr, l) & f);
		return f;
	}
	@Override
	public final int pIOr(final R1.A r, final long l, int f) {
		Object arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) | f);
		else u.putInt(arr, l, f = u.getInt(arr, l) | f);
		return f;
	}
	@Override
	public final int pIXor(final R1.A r, final long l, int f) {
		Object arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) ^ f);
		else u.putInt(arr, l, f = u.getInt(arr, l) ^ f);
		return f;
	}
	@Override
	public final int pILL(final R1.A r, final long l, int f) {
		Object arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) << f);
		else u.putInt(arr, l, f = u.getInt(arr, l) << f);
		return f;
	}
	@Override
	public final int pIRR(final R1.A r, final long l, int f) {
		Object arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >> f);
		return f;
	}
	@Override
	public final int pIRRR(final R1.A r, final long l, int f) {
		Object arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >>> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >>> f);
		return f;
	}
	@Override
	public final long pJAdd(final R1.A r, final long l, long f) {
		Object arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) + f);
		else u.putLong(arr, l, f = u.getLong(arr, l) + f);
		return f;
	}
	@Override
	public final long pJSub(final R1.A r, final long l, long f) {
		Object arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) - f);
		else u.putLong(arr, l, f = u.getLong(arr, l) - f);
		return f;
	}
	@Override
	public final long pJMul(final R1.A r, final long l, long f) {
		Object arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) * f);
		else u.putLong(arr, l, f = u.getLong(arr, l) * f);
		return f;
	}
	@Override
	public final long pJDiv(final R1.A r, final long l, long f) {
		Object arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) / f);
		else u.putLong(arr, l, f = u.getLong(arr, l) / f);
		return f;
	}
	@Override
	public final long pJMod(final R1.A r, final long l, long f) {
		Object arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) % f);
		else u.putLong(arr, l, f = u.getLong(arr, l) % f);
		return f;
	}
	@Override
	public final long pJAnd(final R1.A r, final long l, long f) {
		Object arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) & f);
		else u.putLong(arr, l, f = u.getLong(arr, l) & f);
		return f;
	}
	@Override
	public final long pJOr(final R1.A r, final long l, long f) {
		Object arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) | f);
		else u.putLong(arr, l, f = u.getLong(arr, l) | f);
		return f;
	}
	@Override
	public final long pJXor(final R1.A r, final long l, long f) {
		Object arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) ^ f);
		else u.putLong(arr, l, f = u.getLong(arr, l) ^ f);
		return f;
	}
	@Override
	public final long pJLL(final R1.A r, final long l, long f) {
		Object arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) << f);
		else u.putLong(arr, l, f = u.getLong(arr, l) << f);
		return f;
	}
	@Override
	public final long pJRR(final R1.A r, final long l, long f) {
		Object arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >> f);
		return f;
	}
	@Override
	public final long pJRRR(final R1.A r, final long l, long f) {
		Object arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >>> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >>> f);
		return f;
	}
	@Override
	public final float pFAdd(final R1.A r, final long l, float f) {
		Object arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) + f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) + f);
		return f;
	}
	@Override
	public final float pFSub(final R1.A r, final long l, float f) {
		Object arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) - f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) - f);
		return f;
	}
	@Override
	public final float pFMul(final R1.A r, final long l, float f) {
		Object arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) * f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) * f);
		return f;
	}
	@Override
	public final float pFDiv(final R1.A r, final long l, float f) {
		Object arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) / f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) / f);
		return f;
	}
	@Override
	public final float pFMod(final R1.A r, final long l, float f) {
		Object arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) % f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) % f);
		return f;
	}
	@Override
	public final double pDAdd(final R1.A r, final long l, double f) {
		Object arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) + f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) + f);
		return f;
	}
	@Override
	public final double pDSub(final R1.A r, final long l, double f) {
		Object arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) - f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) - f);
		return f;
	}
	@Override
	public final double pDMul(final R1.A r, final long l, double f) {
		Object arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) * f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) * f);
		return f;
	}
	@Override
	public final double pDDiv(final R1.A r, final long l, double f) {
		Object arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) / f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) / f);
		return f;
	}
	@Override
	public final double pDMod(final R1.A r, final long l, double f) {
		Object arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) % f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) % f);
		return f;
	}
	//-------------------
	@Override
	public final boolean pZAnd(final R1.B r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) & b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) & b));
		return b != 0;
	}
	@Override
	public final boolean pZOr(final R1.B r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) | b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) | b));
		return b != 0;
	}
	@Override
	public final boolean pZXor(final R1.B r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) ^ b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) ^ b));
		return b != 0;
	}
	@Override
	public final byte pBAdd(final R1.B r, final long l, byte f) {
		byte[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) + f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) + f));
		return f;
	}
	@Override
	public final byte pBSub(final R1.B r, final long l, byte f) {
		byte[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) - f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) - f));
		return f;
	}
	@Override
	public final byte pBMul(final R1.B r, final long l, byte f) {
		byte[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) * f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) * f));
		return f;
	}
	@Override
	public final byte pBDiv(final R1.B r, final long l, byte f) {
		byte[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) / f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) / f));
		return f;
	}
	@Override
	public final byte pBMod(final R1.B r, final long l, byte f) {
		byte[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) % f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) % f));
		return f;
	}
	@Override
	public final byte pBAnd(final R1.B r, final long l, byte f) {
		byte[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) & f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) & f));
		return f;
	}
	@Override
	public final byte pBOr(final R1.B r, final long l, byte f) {
		byte[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) | f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) | f));
		return f;
	}
	@Override
	public final byte pBXor(final R1.B r, final long l, byte f) {
		byte[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) ^ f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) ^ f));
		return f;
	}
	@Override
	public final byte pBLL(final R1.B r, final long l, byte f) {
		byte[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) << f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) << f));
		return f;
	}
	@Override
	public final byte pBRR(final R1.B r, final long l, byte f) {
		byte[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >> f));
		return f;
	}
	@Override
	public final byte pBRRR(final R1.B r, final long l, byte f) {
		byte[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >>> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >>> f));
		return f;
	}
	@Override
	public final char pCAdd(final R1.B r, final long l, char f) {
		byte[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) + f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) + f));
		return f;
	}
	@Override
	public final char pCSub(final R1.B r, final long l, char f) {
		byte[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) - f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) - f));
		return f;
	}
	@Override
	public final char pCMul(final R1.B r, final long l, char f) {
		byte[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) * f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) * f));
		return f;
	}
	@Override
	public final char pCDiv(final R1.B r, final long l, char f) {
		byte[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) / f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) / f));
		return f;
	}
	@Override
	public final char pCMod(final R1.B r, final long l, char f) {
		byte[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) % f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) % f));
		return f;
	}
	@Override
	public final char pCAnd(final R1.B r, final long l, char f) {
		byte[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) & f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) & f));
		return f;
	}
	@Override
	public final char pCOr(final R1.B r, final long l, char f) {
		byte[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) | f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) | f));
		return f;
	}
	@Override
	public final char pCXor(final R1.B r, final long l, char f) {
		byte[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) ^ f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) ^ f));
		return f;
	}
	@Override
	public final char pCLL(final R1.B r, final long l, char f) {
		byte[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) << f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) << f));
		return f;
	}
	@Override
	public final char pCRR(final R1.B r, final long l, char f) {
		byte[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >> f));
		return f;
	}
	@Override
	public final char pCRRR(final R1.B r, final long l, char f) {
		byte[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >>> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >>> f));
		return f;
	}
	@Override
	public final short pSAdd(final R1.B r, final long l, short f) {
		byte[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) + f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) + f));
		return f;
	}
	@Override
	public final short pSSub(final R1.B r, final long l, short f) {
		byte[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) - f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) - f));
		return f;
	}
	@Override
	public final short pSMul(final R1.B r, final long l, short f) {
		byte[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) * f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) * f));
		return f;
	}
	@Override
	public final short pSDiv(final R1.B r, final long l, short f) {
		byte[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) / f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) / f));
		return f;
	}
	@Override
	public final short pSMod(final R1.B r, final long l, short f) {
		byte[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) % f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) % f));
		return f;
	}
	@Override
	public final short pSAnd(final R1.B r, final long l, short f) {
		byte[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) & f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) & f));
		return f;
	}
	@Override
	public final short pSOr(final R1.B r, final long l, short f) {
		byte[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) | f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) | f));
		return f;
	}
	@Override
	public final short pSXor(final R1.B r, final long l, short f) {
		byte[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) ^ f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) ^ f));
		return f;
	}
	@Override
	public final short pSLL(final R1.B r, final long l, short f) {
		byte[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) << f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) << f));
		return f;
	}
	@Override
	public final short pSRR(final R1.B r, final long l, short f) {
		byte[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >> f));
		return f;
	}
	@Override
	public final short pSRRR(final R1.B r, final long l, short f) {
		byte[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >>> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >>> f));
		return f;
	}
	@Override
	public final int pIAdd(final R1.B r, final long l, int f) {
		byte[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) + f);
		else u.putInt(arr, l, f = u.getInt(arr, l) + f);
		return f;
	}
	@Override
	public final int pISub(final R1.B r, final long l, int f) {
		byte[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) - f);
		else u.putInt(arr, l, f = u.getInt(arr, l) - f);
		return f;
	}
	@Override
	public final int pIMul(final R1.B r, final long l, int f) {
		byte[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) * f);
		else u.putInt(arr, l, f = u.getInt(arr, l) * f);
		return f;
	}
	@Override
	public final int pIDiv(final R1.B r, final long l, int f) {
		byte[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) / f);
		else u.putInt(arr, l, f = u.getInt(arr, l) / f);
		return f;
	}
	@Override
	public final int pIMod(final R1.B r, final long l, int f) {
		byte[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) % f);
		else u.putInt(arr, l, f = u.getInt(arr, l) % f);
		return f;
	}
	@Override
	public final int pIAnd(final R1.B r, final long l, int f) {
		byte[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) & f);
		else u.putInt(arr, l, f = u.getInt(arr, l) & f);
		return f;
	}
	@Override
	public final int pIOr(final R1.B r, final long l, int f) {
		byte[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) | f);
		else u.putInt(arr, l, f = u.getInt(arr, l) | f);
		return f;
	}
	@Override
	public final int pIXor(final R1.B r, final long l, int f) {
		byte[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) ^ f);
		else u.putInt(arr, l, f = u.getInt(arr, l) ^ f);
		return f;
	}
	@Override
	public final int pILL(final R1.B r, final long l, int f) {
		byte[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) << f);
		else u.putInt(arr, l, f = u.getInt(arr, l) << f);
		return f;
	}
	@Override
	public final int pIRR(final R1.B r, final long l, int f) {
		byte[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >> f);
		return f;
	}
	@Override
	public final int pIRRR(final R1.B r, final long l, int f) {
		byte[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >>> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >>> f);
		return f;
	}
	@Override
	public final long pJAdd(final R1.B r, final long l, long f) {
		byte[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) + f);
		else u.putLong(arr, l, f = u.getLong(arr, l) + f);
		return f;
	}
	@Override
	public final long pJSub(final R1.B r, final long l, long f) {
		byte[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) - f);
		else u.putLong(arr, l, f = u.getLong(arr, l) - f);
		return f;
	}
	@Override
	public final long pJMul(final R1.B r, final long l, long f) {
		byte[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) * f);
		else u.putLong(arr, l, f = u.getLong(arr, l) * f);
		return f;
	}
	@Override
	public final long pJDiv(final R1.B r, final long l, long f) {
		byte[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) / f);
		else u.putLong(arr, l, f = u.getLong(arr, l) / f);
		return f;
	}
	@Override
	public final long pJMod(final R1.B r, final long l, long f) {
		byte[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) % f);
		else u.putLong(arr, l, f = u.getLong(arr, l) % f);
		return f;
	}
	@Override
	public final long pJAnd(final R1.B r, final long l, long f) {
		byte[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) & f);
		else u.putLong(arr, l, f = u.getLong(arr, l) & f);
		return f;
	}
	@Override
	public final long pJOr(final R1.B r, final long l, long f) {
		byte[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) | f);
		else u.putLong(arr, l, f = u.getLong(arr, l) | f);
		return f;
	}
	@Override
	public final long pJXor(final R1.B r, final long l, long f) {
		byte[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) ^ f);
		else u.putLong(arr, l, f = u.getLong(arr, l) ^ f);
		return f;
	}
	@Override
	public final long pJLL(final R1.B r, final long l, long f) {
		byte[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) << f);
		else u.putLong(arr, l, f = u.getLong(arr, l) << f);
		return f;
	}
	@Override
	public final long pJRR(final R1.B r, final long l, long f) {
		byte[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >> f);
		return f;
	}
	@Override
	public final long pJRRR(final R1.B r, final long l, long f) {
		byte[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >>> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >>> f);
		return f;
	}
	@Override
	public final float pFAdd(final R1.B r, final long l, float f) {
		byte[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) + f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) + f);
		return f;
	}
	@Override
	public final float pFSub(final R1.B r, final long l, float f) {
		byte[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) - f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) - f);
		return f;
	}
	@Override
	public final float pFMul(final R1.B r, final long l, float f) {
		byte[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) * f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) * f);
		return f;
	}
	@Override
	public final float pFDiv(final R1.B r, final long l, float f) {
		byte[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) / f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) / f);
		return f;
	}
	@Override
	public final float pFMod(final R1.B r, final long l, float f) {
		byte[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) % f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) % f);
		return f;
	}
	@Override
	public final double pDAdd(final R1.B r, final long l, double f) {
		byte[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) + f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) + f);
		return f;
	}
	@Override
	public final double pDSub(final R1.B r, final long l, double f) {
		byte[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) - f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) - f);
		return f;
	}
	@Override
	public final double pDMul(final R1.B r, final long l, double f) {
		byte[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) * f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) * f);
		return f;
	}
	@Override
	public final double pDDiv(final R1.B r, final long l, double f) {
		byte[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) / f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) / f);
		return f;
	}
	@Override
	public final double pDMod(final R1.B r, final long l, double f) {
		byte[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) % f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) % f);
		return f;
	}
	//-------------------
	@Override
	public final boolean pZAnd(final R1.C r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) & b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) & b));
		return b != 0;
	}
	@Override
	public final boolean pZOr(final R1.C r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) | b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) | b));
		return b != 0;
	}
	@Override
	public final boolean pZXor(final R1.C r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) ^ b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) ^ b));
		return b != 0;
	}
	@Override
	public final byte pBAdd(final R1.C r, final long l, byte f) {
		char[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) + f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) + f));
		return f;
	}
	@Override
	public final byte pBSub(final R1.C r, final long l, byte f) {
		char[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) - f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) - f));
		return f;
	}
	@Override
	public final byte pBMul(final R1.C r, final long l, byte f) {
		char[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) * f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) * f));
		return f;
	}
	@Override
	public final byte pBDiv(final R1.C r, final long l, byte f) {
		char[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) / f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) / f));
		return f;
	}
	@Override
	public final byte pBMod(final R1.C r, final long l, byte f) {
		char[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) % f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) % f));
		return f;
	}
	@Override
	public final byte pBAnd(final R1.C r, final long l, byte f) {
		char[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) & f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) & f));
		return f;
	}
	@Override
	public final byte pBOr(final R1.C r, final long l, byte f) {
		char[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) | f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) | f));
		return f;
	}
	@Override
	public final byte pBXor(final R1.C r, final long l, byte f) {
		char[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) ^ f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) ^ f));
		return f;
	}
	@Override
	public final byte pBLL(final R1.C r, final long l, byte f) {
		char[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) << f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) << f));
		return f;
	}
	@Override
	public final byte pBRR(final R1.C r, final long l, byte f) {
		char[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >> f));
		return f;
	}
	@Override
	public final byte pBRRR(final R1.C r, final long l, byte f) {
		char[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >>> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >>> f));
		return f;
	}
	@Override
	public final char pCAdd(final R1.C r, final long l, char f) {
		char[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) + f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) + f));
		return f;
	}
	@Override
	public final char pCSub(final R1.C r, final long l, char f) {
		char[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) - f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) - f));
		return f;
	}
	@Override
	public final char pCMul(final R1.C r, final long l, char f) {
		char[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) * f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) * f));
		return f;
	}
	@Override
	public final char pCDiv(final R1.C r, final long l, char f) {
		char[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) / f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) / f));
		return f;
	}
	@Override
	public final char pCMod(final R1.C r, final long l, char f) {
		char[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) % f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) % f));
		return f;
	}
	@Override
	public final char pCAnd(final R1.C r, final long l, char f) {
		char[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) & f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) & f));
		return f;
	}
	@Override
	public final char pCOr(final R1.C r, final long l, char f) {
		char[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) | f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) | f));
		return f;
	}
	@Override
	public final char pCXor(final R1.C r, final long l, char f) {
		char[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) ^ f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) ^ f));
		return f;
	}
	@Override
	public final char pCLL(final R1.C r, final long l, char f) {
		char[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) << f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) << f));
		return f;
	}
	@Override
	public final char pCRR(final R1.C r, final long l, char f) {
		char[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >> f));
		return f;
	}
	@Override
	public final char pCRRR(final R1.C r, final long l, char f) {
		char[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >>> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >>> f));
		return f;
	}
	@Override
	public final short pSAdd(final R1.C r, final long l, short f) {
		char[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) + f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) + f));
		return f;
	}
	@Override
	public final short pSSub(final R1.C r, final long l, short f) {
		char[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) - f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) - f));
		return f;
	}
	@Override
	public final short pSMul(final R1.C r, final long l, short f) {
		char[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) * f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) * f));
		return f;
	}
	@Override
	public final short pSDiv(final R1.C r, final long l, short f) {
		char[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) / f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) / f));
		return f;
	}
	@Override
	public final short pSMod(final R1.C r, final long l, short f) {
		char[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) % f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) % f));
		return f;
	}
	@Override
	public final short pSAnd(final R1.C r, final long l, short f) {
		char[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) & f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) & f));
		return f;
	}
	@Override
	public final short pSOr(final R1.C r, final long l, short f) {
		char[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) | f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) | f));
		return f;
	}
	@Override
	public final short pSXor(final R1.C r, final long l, short f) {
		char[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) ^ f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) ^ f));
		return f;
	}
	@Override
	public final short pSLL(final R1.C r, final long l, short f) {
		char[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) << f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) << f));
		return f;
	}
	@Override
	public final short pSRR(final R1.C r, final long l, short f) {
		char[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >> f));
		return f;
	}
	@Override
	public final short pSRRR(final R1.C r, final long l, short f) {
		char[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >>> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >>> f));
		return f;
	}
	@Override
	public final int pIAdd(final R1.C r, final long l, int f) {
		char[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) + f);
		else u.putInt(arr, l, f = u.getInt(arr, l) + f);
		return f;
	}
	@Override
	public final int pISub(final R1.C r, final long l, int f) {
		char[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) - f);
		else u.putInt(arr, l, f = u.getInt(arr, l) - f);
		return f;
	}
	@Override
	public final int pIMul(final R1.C r, final long l, int f) {
		char[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) * f);
		else u.putInt(arr, l, f = u.getInt(arr, l) * f);
		return f;
	}
	@Override
	public final int pIDiv(final R1.C r, final long l, int f) {
		char[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) / f);
		else u.putInt(arr, l, f = u.getInt(arr, l) / f);
		return f;
	}
	@Override
	public final int pIMod(final R1.C r, final long l, int f) {
		char[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) % f);
		else u.putInt(arr, l, f = u.getInt(arr, l) % f);
		return f;
	}
	@Override
	public final int pIAnd(final R1.C r, final long l, int f) {
		char[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) & f);
		else u.putInt(arr, l, f = u.getInt(arr, l) & f);
		return f;
	}
	@Override
	public final int pIOr(final R1.C r, final long l, int f) {
		char[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) | f);
		else u.putInt(arr, l, f = u.getInt(arr, l) | f);
		return f;
	}
	@Override
	public final int pIXor(final R1.C r, final long l, int f) {
		char[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) ^ f);
		else u.putInt(arr, l, f = u.getInt(arr, l) ^ f);
		return f;
	}
	@Override
	public final int pILL(final R1.C r, final long l, int f) {
		char[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) << f);
		else u.putInt(arr, l, f = u.getInt(arr, l) << f);
		return f;
	}
	@Override
	public final int pIRR(final R1.C r, final long l, int f) {
		char[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >> f);
		return f;
	}
	@Override
	public final int pIRRR(final R1.C r, final long l, int f) {
		char[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >>> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >>> f);
		return f;
	}
	@Override
	public final long pJAdd(final R1.C r, final long l, long f) {
		char[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) + f);
		else u.putLong(arr, l, f = u.getLong(arr, l) + f);
		return f;
	}
	@Override
	public final long pJSub(final R1.C r, final long l, long f) {
		char[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) - f);
		else u.putLong(arr, l, f = u.getLong(arr, l) - f);
		return f;
	}
	@Override
	public final long pJMul(final R1.C r, final long l, long f) {
		char[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) * f);
		else u.putLong(arr, l, f = u.getLong(arr, l) * f);
		return f;
	}
	@Override
	public final long pJDiv(final R1.C r, final long l, long f) {
		char[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) / f);
		else u.putLong(arr, l, f = u.getLong(arr, l) / f);
		return f;
	}
	@Override
	public final long pJMod(final R1.C r, final long l, long f) {
		char[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) % f);
		else u.putLong(arr, l, f = u.getLong(arr, l) % f);
		return f;
	}
	@Override
	public final long pJAnd(final R1.C r, final long l, long f) {
		char[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) & f);
		else u.putLong(arr, l, f = u.getLong(arr, l) & f);
		return f;
	}
	@Override
	public final long pJOr(final R1.C r, final long l, long f) {
		char[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) | f);
		else u.putLong(arr, l, f = u.getLong(arr, l) | f);
		return f;
	}
	@Override
	public final long pJXor(final R1.C r, final long l, long f) {
		char[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) ^ f);
		else u.putLong(arr, l, f = u.getLong(arr, l) ^ f);
		return f;
	}
	@Override
	public final long pJLL(final R1.C r, final long l, long f) {
		char[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) << f);
		else u.putLong(arr, l, f = u.getLong(arr, l) << f);
		return f;
	}
	@Override
	public final long pJRR(final R1.C r, final long l, long f) {
		char[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >> f);
		return f;
	}
	@Override
	public final long pJRRR(final R1.C r, final long l, long f) {
		char[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >>> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >>> f);
		return f;
	}
	@Override
	public final float pFAdd(final R1.C r, final long l, float f) {
		char[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) + f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) + f);
		return f;
	}
	@Override
	public final float pFSub(final R1.C r, final long l, float f) {
		char[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) - f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) - f);
		return f;
	}
	@Override
	public final float pFMul(final R1.C r, final long l, float f) {
		char[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) * f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) * f);
		return f;
	}
	@Override
	public final float pFDiv(final R1.C r, final long l, float f) {
		char[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) / f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) / f);
		return f;
	}
	@Override
	public final float pFMod(final R1.C r, final long l, float f) {
		char[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) % f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) % f);
		return f;
	}
	@Override
	public final double pDAdd(final R1.C r, final long l, double f) {
		char[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) + f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) + f);
		return f;
	}
	@Override
	public final double pDSub(final R1.C r, final long l, double f) {
		char[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) - f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) - f);
		return f;
	}
	@Override
	public final double pDMul(final R1.C r, final long l, double f) {
		char[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) * f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) * f);
		return f;
	}
	@Override
	public final double pDDiv(final R1.C r, final long l, double f) {
		char[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) / f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) / f);
		return f;
	}
	@Override
	public final double pDMod(final R1.C r, final long l, double f) {
		char[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) % f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) % f);
		return f;
	}
	//-------------------
	@Override
	public final boolean pZAnd(final R1.S r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) & b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) & b));
		return b != 0;
	}
	@Override
	public final boolean pZOr(final R1.S r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) | b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) | b));
		return b != 0;
	}
	@Override
	public final boolean pZXor(final R1.S r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) ^ b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) ^ b));
		return b != 0;
	}
	@Override
	public final byte pBAdd(final R1.S r, final long l, byte f) {
		short[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) + f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) + f));
		return f;
	}
	@Override
	public final byte pBSub(final R1.S r, final long l, byte f) {
		short[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) - f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) - f));
		return f;
	}
	@Override
	public final byte pBMul(final R1.S r, final long l, byte f) {
		short[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) * f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) * f));
		return f;
	}
	@Override
	public final byte pBDiv(final R1.S r, final long l, byte f) {
		short[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) / f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) / f));
		return f;
	}
	@Override
	public final byte pBMod(final R1.S r, final long l, byte f) {
		short[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) % f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) % f));
		return f;
	}
	@Override
	public final byte pBAnd(final R1.S r, final long l, byte f) {
		short[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) & f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) & f));
		return f;
	}
	@Override
	public final byte pBOr(final R1.S r, final long l, byte f) {
		short[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) | f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) | f));
		return f;
	}
	@Override
	public final byte pBXor(final R1.S r, final long l, byte f) {
		short[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) ^ f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) ^ f));
		return f;
	}
	@Override
	public final byte pBLL(final R1.S r, final long l, byte f) {
		short[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) << f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) << f));
		return f;
	}
	@Override
	public final byte pBRR(final R1.S r, final long l, byte f) {
		short[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >> f));
		return f;
	}
	@Override
	public final byte pBRRR(final R1.S r, final long l, byte f) {
		short[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >>> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >>> f));
		return f;
	}
	@Override
	public final char pCAdd(final R1.S r, final long l, char f) {
		short[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) + f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) + f));
		return f;
	}
	@Override
	public final char pCSub(final R1.S r, final long l, char f) {
		short[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) - f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) - f));
		return f;
	}
	@Override
	public final char pCMul(final R1.S r, final long l, char f) {
		short[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) * f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) * f));
		return f;
	}
	@Override
	public final char pCDiv(final R1.S r, final long l, char f) {
		short[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) / f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) / f));
		return f;
	}
	@Override
	public final char pCMod(final R1.S r, final long l, char f) {
		short[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) % f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) % f));
		return f;
	}
	@Override
	public final char pCAnd(final R1.S r, final long l, char f) {
		short[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) & f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) & f));
		return f;
	}
	@Override
	public final char pCOr(final R1.S r, final long l, char f) {
		short[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) | f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) | f));
		return f;
	}
	@Override
	public final char pCXor(final R1.S r, final long l, char f) {
		short[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) ^ f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) ^ f));
		return f;
	}
	@Override
	public final char pCLL(final R1.S r, final long l, char f) {
		short[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) << f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) << f));
		return f;
	}
	@Override
	public final char pCRR(final R1.S r, final long l, char f) {
		short[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >> f));
		return f;
	}
	@Override
	public final char pCRRR(final R1.S r, final long l, char f) {
		short[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >>> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >>> f));
		return f;
	}
	@Override
	public final short pSAdd(final R1.S r, final long l, short f) {
		short[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) + f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) + f));
		return f;
	}
	@Override
	public final short pSSub(final R1.S r, final long l, short f) {
		short[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) - f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) - f));
		return f;
	}
	@Override
	public final short pSMul(final R1.S r, final long l, short f) {
		short[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) * f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) * f));
		return f;
	}
	@Override
	public final short pSDiv(final R1.S r, final long l, short f) {
		short[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) / f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) / f));
		return f;
	}
	@Override
	public final short pSMod(final R1.S r, final long l, short f) {
		short[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) % f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) % f));
		return f;
	}
	@Override
	public final short pSAnd(final R1.S r, final long l, short f) {
		short[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) & f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) & f));
		return f;
	}
	@Override
	public final short pSOr(final R1.S r, final long l, short f) {
		short[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) | f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) | f));
		return f;
	}
	@Override
	public final short pSXor(final R1.S r, final long l, short f) {
		short[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) ^ f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) ^ f));
		return f;
	}
	@Override
	public final short pSLL(final R1.S r, final long l, short f) {
		short[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) << f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) << f));
		return f;
	}
	@Override
	public final short pSRR(final R1.S r, final long l, short f) {
		short[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >> f));
		return f;
	}
	@Override
	public final short pSRRR(final R1.S r, final long l, short f) {
		short[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >>> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >>> f));
		return f;
	}
	@Override
	public final int pIAdd(final R1.S r, final long l, int f) {
		short[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) + f);
		else u.putInt(arr, l, f = u.getInt(arr, l) + f);
		return f;
	}
	@Override
	public final int pISub(final R1.S r, final long l, int f) {
		short[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) - f);
		else u.putInt(arr, l, f = u.getInt(arr, l) - f);
		return f;
	}
	@Override
	public final int pIMul(final R1.S r, final long l, int f) {
		short[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) * f);
		else u.putInt(arr, l, f = u.getInt(arr, l) * f);
		return f;
	}
	@Override
	public final int pIDiv(final R1.S r, final long l, int f) {
		short[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) / f);
		else u.putInt(arr, l, f = u.getInt(arr, l) / f);
		return f;
	}
	@Override
	public final int pIMod(final R1.S r, final long l, int f) {
		short[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) % f);
		else u.putInt(arr, l, f = u.getInt(arr, l) % f);
		return f;
	}
	@Override
	public final int pIAnd(final R1.S r, final long l, int f) {
		short[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) & f);
		else u.putInt(arr, l, f = u.getInt(arr, l) & f);
		return f;
	}
	@Override
	public final int pIOr(final R1.S r, final long l, int f) {
		short[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) | f);
		else u.putInt(arr, l, f = u.getInt(arr, l) | f);
		return f;
	}
	@Override
	public final int pIXor(final R1.S r, final long l, int f) {
		short[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) ^ f);
		else u.putInt(arr, l, f = u.getInt(arr, l) ^ f);
		return f;
	}
	@Override
	public final int pILL(final R1.S r, final long l, int f) {
		short[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) << f);
		else u.putInt(arr, l, f = u.getInt(arr, l) << f);
		return f;
	}
	@Override
	public final int pIRR(final R1.S r, final long l, int f) {
		short[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >> f);
		return f;
	}
	@Override
	public final int pIRRR(final R1.S r, final long l, int f) {
		short[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >>> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >>> f);
		return f;
	}
	@Override
	public final long pJAdd(final R1.S r, final long l, long f) {
		short[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) + f);
		else u.putLong(arr, l, f = u.getLong(arr, l) + f);
		return f;
	}
	@Override
	public final long pJSub(final R1.S r, final long l, long f) {
		short[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) - f);
		else u.putLong(arr, l, f = u.getLong(arr, l) - f);
		return f;
	}
	@Override
	public final long pJMul(final R1.S r, final long l, long f) {
		short[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) * f);
		else u.putLong(arr, l, f = u.getLong(arr, l) * f);
		return f;
	}
	@Override
	public final long pJDiv(final R1.S r, final long l, long f) {
		short[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) / f);
		else u.putLong(arr, l, f = u.getLong(arr, l) / f);
		return f;
	}
	@Override
	public final long pJMod(final R1.S r, final long l, long f) {
		short[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) % f);
		else u.putLong(arr, l, f = u.getLong(arr, l) % f);
		return f;
	}
	@Override
	public final long pJAnd(final R1.S r, final long l, long f) {
		short[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) & f);
		else u.putLong(arr, l, f = u.getLong(arr, l) & f);
		return f;
	}
	@Override
	public final long pJOr(final R1.S r, final long l, long f) {
		short[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) | f);
		else u.putLong(arr, l, f = u.getLong(arr, l) | f);
		return f;
	}
	@Override
	public final long pJXor(final R1.S r, final long l, long f) {
		short[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) ^ f);
		else u.putLong(arr, l, f = u.getLong(arr, l) ^ f);
		return f;
	}
	@Override
	public final long pJLL(final R1.S r, final long l, long f) {
		short[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) << f);
		else u.putLong(arr, l, f = u.getLong(arr, l) << f);
		return f;
	}
	@Override
	public final long pJRR(final R1.S r, final long l, long f) {
		short[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >> f);
		return f;
	}
	@Override
	public final long pJRRR(final R1.S r, final long l, long f) {
		short[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >>> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >>> f);
		return f;
	}
	@Override
	public final float pFAdd(final R1.S r, final long l, float f) {
		short[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) + f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) + f);
		return f;
	}
	@Override
	public final float pFSub(final R1.S r, final long l, float f) {
		short[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) - f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) - f);
		return f;
	}
	@Override
	public final float pFMul(final R1.S r, final long l, float f) {
		short[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) * f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) * f);
		return f;
	}
	@Override
	public final float pFDiv(final R1.S r, final long l, float f) {
		short[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) / f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) / f);
		return f;
	}
	@Override
	public final float pFMod(final R1.S r, final long l, float f) {
		short[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) % f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) % f);
		return f;
	}
	@Override
	public final double pDAdd(final R1.S r, final long l, double f) {
		short[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) + f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) + f);
		return f;
	}
	@Override
	public final double pDSub(final R1.S r, final long l, double f) {
		short[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) - f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) - f);
		return f;
	}
	@Override
	public final double pDMul(final R1.S r, final long l, double f) {
		short[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) * f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) * f);
		return f;
	}
	@Override
	public final double pDDiv(final R1.S r, final long l, double f) {
		short[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) / f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) / f);
		return f;
	}
	@Override
	public final double pDMod(final R1.S r, final long l, double f) {
		short[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) % f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) % f);
		return f;
	}
	//-------------------
	@Override
	public final boolean pZAnd(final R1.I r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) & b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) & b));
		return b != 0;
	}
	@Override
	public final boolean pZOr(final R1.I r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) | b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) | b));
		return b != 0;
	}
	@Override
	public final boolean pZXor(final R1.I r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) ^ b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) ^ b));
		return b != 0;
	}
	@Override
	public final byte pBAdd(final R1.I r, final long l, byte f) {
		int[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) + f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) + f));
		return f;
	}
	@Override
	public final byte pBSub(final R1.I r, final long l, byte f) {
		int[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) - f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) - f));
		return f;
	}
	@Override
	public final byte pBMul(final R1.I r, final long l, byte f) {
		int[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) * f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) * f));
		return f;
	}
	@Override
	public final byte pBDiv(final R1.I r, final long l, byte f) {
		int[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) / f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) / f));
		return f;
	}
	@Override
	public final byte pBMod(final R1.I r, final long l, byte f) {
		int[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) % f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) % f));
		return f;
	}
	@Override
	public final byte pBAnd(final R1.I r, final long l, byte f) {
		int[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) & f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) & f));
		return f;
	}
	@Override
	public final byte pBOr(final R1.I r, final long l, byte f) {
		int[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) | f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) | f));
		return f;
	}
	@Override
	public final byte pBXor(final R1.I r, final long l, byte f) {
		int[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) ^ f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) ^ f));
		return f;
	}
	@Override
	public final byte pBLL(final R1.I r, final long l, byte f) {
		int[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) << f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) << f));
		return f;
	}
	@Override
	public final byte pBRR(final R1.I r, final long l, byte f) {
		int[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >> f));
		return f;
	}
	@Override
	public final byte pBRRR(final R1.I r, final long l, byte f) {
		int[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >>> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >>> f));
		return f;
	}
	@Override
	public final char pCAdd(final R1.I r, final long l, char f) {
		int[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) + f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) + f));
		return f;
	}
	@Override
	public final char pCSub(final R1.I r, final long l, char f) {
		int[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) - f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) - f));
		return f;
	}
	@Override
	public final char pCMul(final R1.I r, final long l, char f) {
		int[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) * f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) * f));
		return f;
	}
	@Override
	public final char pCDiv(final R1.I r, final long l, char f) {
		int[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) / f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) / f));
		return f;
	}
	@Override
	public final char pCMod(final R1.I r, final long l, char f) {
		int[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) % f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) % f));
		return f;
	}
	@Override
	public final char pCAnd(final R1.I r, final long l, char f) {
		int[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) & f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) & f));
		return f;
	}
	@Override
	public final char pCOr(final R1.I r, final long l, char f) {
		int[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) | f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) | f));
		return f;
	}
	@Override
	public final char pCXor(final R1.I r, final long l, char f) {
		int[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) ^ f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) ^ f));
		return f;
	}
	@Override
	public final char pCLL(final R1.I r, final long l, char f) {
		int[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) << f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) << f));
		return f;
	}
	@Override
	public final char pCRR(final R1.I r, final long l, char f) {
		int[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >> f));
		return f;
	}
	@Override
	public final char pCRRR(final R1.I r, final long l, char f) {
		int[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >>> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >>> f));
		return f;
	}
	@Override
	public final short pSAdd(final R1.I r, final long l, short f) {
		int[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) + f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) + f));
		return f;
	}
	@Override
	public final short pSSub(final R1.I r, final long l, short f) {
		int[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) - f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) - f));
		return f;
	}
	@Override
	public final short pSMul(final R1.I r, final long l, short f) {
		int[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) * f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) * f));
		return f;
	}
	@Override
	public final short pSDiv(final R1.I r, final long l, short f) {
		int[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) / f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) / f));
		return f;
	}
	@Override
	public final short pSMod(final R1.I r, final long l, short f) {
		int[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) % f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) % f));
		return f;
	}
	@Override
	public final short pSAnd(final R1.I r, final long l, short f) {
		int[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) & f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) & f));
		return f;
	}
	@Override
	public final short pSOr(final R1.I r, final long l, short f) {
		int[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) | f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) | f));
		return f;
	}
	@Override
	public final short pSXor(final R1.I r, final long l, short f) {
		int[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) ^ f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) ^ f));
		return f;
	}
	@Override
	public final short pSLL(final R1.I r, final long l, short f) {
		int[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) << f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) << f));
		return f;
	}
	@Override
	public final short pSRR(final R1.I r, final long l, short f) {
		int[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >> f));
		return f;
	}
	@Override
	public final short pSRRR(final R1.I r, final long l, short f) {
		int[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >>> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >>> f));
		return f;
	}
	@Override
	public final int pIAdd(final R1.I r, final long l, int f) {
		int[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) + f);
		else u.putInt(arr, l, f = u.getInt(arr, l) + f);
		return f;
	}
	@Override
	public final int pISub(final R1.I r, final long l, int f) {
		int[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) - f);
		else u.putInt(arr, l, f = u.getInt(arr, l) - f);
		return f;
	}
	@Override
	public final int pIMul(final R1.I r, final long l, int f) {
		int[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) * f);
		else u.putInt(arr, l, f = u.getInt(arr, l) * f);
		return f;
	}
	@Override
	public final int pIDiv(final R1.I r, final long l, int f) {
		int[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) / f);
		else u.putInt(arr, l, f = u.getInt(arr, l) / f);
		return f;
	}
	@Override
	public final int pIMod(final R1.I r, final long l, int f) {
		int[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) % f);
		else u.putInt(arr, l, f = u.getInt(arr, l) % f);
		return f;
	}
	@Override
	public final int pIAnd(final R1.I r, final long l, int f) {
		int[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) & f);
		else u.putInt(arr, l, f = u.getInt(arr, l) & f);
		return f;
	}
	@Override
	public final int pIOr(final R1.I r, final long l, int f) {
		int[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) | f);
		else u.putInt(arr, l, f = u.getInt(arr, l) | f);
		return f;
	}
	@Override
	public final int pIXor(final R1.I r, final long l, int f) {
		int[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) ^ f);
		else u.putInt(arr, l, f = u.getInt(arr, l) ^ f);
		return f;
	}
	@Override
	public final int pILL(final R1.I r, final long l, int f) {
		int[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) << f);
		else u.putInt(arr, l, f = u.getInt(arr, l) << f);
		return f;
	}
	@Override
	public final int pIRR(final R1.I r, final long l, int f) {
		int[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >> f);
		return f;
	}
	@Override
	public final int pIRRR(final R1.I r, final long l, int f) {
		int[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >>> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >>> f);
		return f;
	}
	@Override
	public final long pJAdd(final R1.I r, final long l, long f) {
		int[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) + f);
		else u.putLong(arr, l, f = u.getLong(arr, l) + f);
		return f;
	}
	@Override
	public final long pJSub(final R1.I r, final long l, long f) {
		int[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) - f);
		else u.putLong(arr, l, f = u.getLong(arr, l) - f);
		return f;
	}
	@Override
	public final long pJMul(final R1.I r, final long l, long f) {
		int[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) * f);
		else u.putLong(arr, l, f = u.getLong(arr, l) * f);
		return f;
	}
	@Override
	public final long pJDiv(final R1.I r, final long l, long f) {
		int[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) / f);
		else u.putLong(arr, l, f = u.getLong(arr, l) / f);
		return f;
	}
	@Override
	public final long pJMod(final R1.I r, final long l, long f) {
		int[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) % f);
		else u.putLong(arr, l, f = u.getLong(arr, l) % f);
		return f;
	}
	@Override
	public final long pJAnd(final R1.I r, final long l, long f) {
		int[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) & f);
		else u.putLong(arr, l, f = u.getLong(arr, l) & f);
		return f;
	}
	@Override
	public final long pJOr(final R1.I r, final long l, long f) {
		int[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) | f);
		else u.putLong(arr, l, f = u.getLong(arr, l) | f);
		return f;
	}
	@Override
	public final long pJXor(final R1.I r, final long l, long f) {
		int[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) ^ f);
		else u.putLong(arr, l, f = u.getLong(arr, l) ^ f);
		return f;
	}
	@Override
	public final long pJLL(final R1.I r, final long l, long f) {
		int[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) << f);
		else u.putLong(arr, l, f = u.getLong(arr, l) << f);
		return f;
	}
	@Override
	public final long pJRR(final R1.I r, final long l, long f) {
		int[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >> f);
		return f;
	}
	@Override
	public final long pJRRR(final R1.I r, final long l, long f) {
		int[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >>> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >>> f);
		return f;
	}
	@Override
	public final float pFAdd(final R1.I r, final long l, float f) {
		int[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) + f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) + f);
		return f;
	}
	@Override
	public final float pFSub(final R1.I r, final long l, float f) {
		int[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) - f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) - f);
		return f;
	}
	@Override
	public final float pFMul(final R1.I r, final long l, float f) {
		int[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) * f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) * f);
		return f;
	}
	@Override
	public final float pFDiv(final R1.I r, final long l, float f) {
		int[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) / f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) / f);
		return f;
	}
	@Override
	public final float pFMod(final R1.I r, final long l, float f) {
		int[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) % f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) % f);
		return f;
	}
	@Override
	public final double pDAdd(final R1.I r, final long l, double f) {
		int[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) + f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) + f);
		return f;
	}
	@Override
	public final double pDSub(final R1.I r, final long l, double f) {
		int[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) - f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) - f);
		return f;
	}
	@Override
	public final double pDMul(final R1.I r, final long l, double f) {
		int[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) * f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) * f);
		return f;
	}
	@Override
	public final double pDDiv(final R1.I r, final long l, double f) {
		int[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) / f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) / f);
		return f;
	}
	@Override
	public final double pDMod(final R1.I r, final long l, double f) {
		int[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) % f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) % f);
		return f;
	}
	//-------------------
	@Override
	public final boolean pZAnd(final R1.L r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) & b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) & b));
		return b != 0;
	}
	@Override
	public final boolean pZOr(final R1.L r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) | b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) | b));
		return b != 0;
	}
	@Override
	public final boolean pZXor(final R1.L r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) ^ b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) ^ b));
		return b != 0;
	}
	@Override
	public final byte pBAdd(final R1.L r, final long l, byte f) {
		long[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) + f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) + f));
		return f;
	}
	@Override
	public final byte pBSub(final R1.L r, final long l, byte f) {
		long[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) - f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) - f));
		return f;
	}
	@Override
	public final byte pBMul(final R1.L r, final long l, byte f) {
		long[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) * f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) * f));
		return f;
	}
	@Override
	public final byte pBDiv(final R1.L r, final long l, byte f) {
		long[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) / f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) / f));
		return f;
	}
	@Override
	public final byte pBMod(final R1.L r, final long l, byte f) {
		long[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) % f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) % f));
		return f;
	}
	@Override
	public final byte pBAnd(final R1.L r, final long l, byte f) {
		long[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) & f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) & f));
		return f;
	}
	@Override
	public final byte pBOr(final R1.L r, final long l, byte f) {
		long[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) | f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) | f));
		return f;
	}
	@Override
	public final byte pBXor(final R1.L r, final long l, byte f) {
		long[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) ^ f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) ^ f));
		return f;
	}
	@Override
	public final byte pBLL(final R1.L r, final long l, byte f) {
		long[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) << f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) << f));
		return f;
	}
	@Override
	public final byte pBRR(final R1.L r, final long l, byte f) {
		long[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >> f));
		return f;
	}
	@Override
	public final byte pBRRR(final R1.L r, final long l, byte f) {
		long[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >>> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >>> f));
		return f;
	}
	@Override
	public final char pCAdd(final R1.L r, final long l, char f) {
		long[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) + f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) + f));
		return f;
	}
	@Override
	public final char pCSub(final R1.L r, final long l, char f) {
		long[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) - f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) - f));
		return f;
	}
	@Override
	public final char pCMul(final R1.L r, final long l, char f) {
		long[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) * f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) * f));
		return f;
	}
	@Override
	public final char pCDiv(final R1.L r, final long l, char f) {
		long[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) / f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) / f));
		return f;
	}
	@Override
	public final char pCMod(final R1.L r, final long l, char f) {
		long[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) % f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) % f));
		return f;
	}
	@Override
	public final char pCAnd(final R1.L r, final long l, char f) {
		long[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) & f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) & f));
		return f;
	}
	@Override
	public final char pCOr(final R1.L r, final long l, char f) {
		long[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) | f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) | f));
		return f;
	}
	@Override
	public final char pCXor(final R1.L r, final long l, char f) {
		long[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) ^ f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) ^ f));
		return f;
	}
	@Override
	public final char pCLL(final R1.L r, final long l, char f) {
		long[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) << f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) << f));
		return f;
	}
	@Override
	public final char pCRR(final R1.L r, final long l, char f) {
		long[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >> f));
		return f;
	}
	@Override
	public final char pCRRR(final R1.L r, final long l, char f) {
		long[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >>> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >>> f));
		return f;
	}
	@Override
	public final short pSAdd(final R1.L r, final long l, short f) {
		long[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) + f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) + f));
		return f;
	}
	@Override
	public final short pSSub(final R1.L r, final long l, short f) {
		long[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) - f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) - f));
		return f;
	}
	@Override
	public final short pSMul(final R1.L r, final long l, short f) {
		long[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) * f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) * f));
		return f;
	}
	@Override
	public final short pSDiv(final R1.L r, final long l, short f) {
		long[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) / f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) / f));
		return f;
	}
	@Override
	public final short pSMod(final R1.L r, final long l, short f) {
		long[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) % f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) % f));
		return f;
	}
	@Override
	public final short pSAnd(final R1.L r, final long l, short f) {
		long[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) & f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) & f));
		return f;
	}
	@Override
	public final short pSOr(final R1.L r, final long l, short f) {
		long[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) | f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) | f));
		return f;
	}
	@Override
	public final short pSXor(final R1.L r, final long l, short f) {
		long[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) ^ f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) ^ f));
		return f;
	}
	@Override
	public final short pSLL(final R1.L r, final long l, short f) {
		long[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) << f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) << f));
		return f;
	}
	@Override
	public final short pSRR(final R1.L r, final long l, short f) {
		long[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >> f));
		return f;
	}
	@Override
	public final short pSRRR(final R1.L r, final long l, short f) {
		long[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >>> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >>> f));
		return f;
	}
	@Override
	public final int pIAdd(final R1.L r, final long l, int f) {
		long[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) + f);
		else u.putInt(arr, l, f = u.getInt(arr, l) + f);
		return f;
	}
	@Override
	public final int pISub(final R1.L r, final long l, int f) {
		long[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) - f);
		else u.putInt(arr, l, f = u.getInt(arr, l) - f);
		return f;
	}
	@Override
	public final int pIMul(final R1.L r, final long l, int f) {
		long[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) * f);
		else u.putInt(arr, l, f = u.getInt(arr, l) * f);
		return f;
	}
	@Override
	public final int pIDiv(final R1.L r, final long l, int f) {
		long[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) / f);
		else u.putInt(arr, l, f = u.getInt(arr, l) / f);
		return f;
	}
	@Override
	public final int pIMod(final R1.L r, final long l, int f) {
		long[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) % f);
		else u.putInt(arr, l, f = u.getInt(arr, l) % f);
		return f;
	}
	@Override
	public final int pIAnd(final R1.L r, final long l, int f) {
		long[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) & f);
		else u.putInt(arr, l, f = u.getInt(arr, l) & f);
		return f;
	}
	@Override
	public final int pIOr(final R1.L r, final long l, int f) {
		long[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) | f);
		else u.putInt(arr, l, f = u.getInt(arr, l) | f);
		return f;
	}
	@Override
	public final int pIXor(final R1.L r, final long l, int f) {
		long[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) ^ f);
		else u.putInt(arr, l, f = u.getInt(arr, l) ^ f);
		return f;
	}
	@Override
	public final int pILL(final R1.L r, final long l, int f) {
		long[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) << f);
		else u.putInt(arr, l, f = u.getInt(arr, l) << f);
		return f;
	}
	@Override
	public final int pIRR(final R1.L r, final long l, int f) {
		long[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >> f);
		return f;
	}
	@Override
	public final int pIRRR(final R1.L r, final long l, int f) {
		long[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >>> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >>> f);
		return f;
	}
	@Override
	public final long pJAdd(final R1.L r, final long l, long f) {
		long[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) + f);
		else u.putLong(arr, l, f = u.getLong(arr, l) + f);
		return f;
	}
	@Override
	public final long pJSub(final R1.L r, final long l, long f) {
		long[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) - f);
		else u.putLong(arr, l, f = u.getLong(arr, l) - f);
		return f;
	}
	@Override
	public final long pJMul(final R1.L r, final long l, long f) {
		long[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) * f);
		else u.putLong(arr, l, f = u.getLong(arr, l) * f);
		return f;
	}
	@Override
	public final long pJDiv(final R1.L r, final long l, long f) {
		long[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) / f);
		else u.putLong(arr, l, f = u.getLong(arr, l) / f);
		return f;
	}
	@Override
	public final long pJMod(final R1.L r, final long l, long f) {
		long[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) % f);
		else u.putLong(arr, l, f = u.getLong(arr, l) % f);
		return f;
	}
	@Override
	public final long pJAnd(final R1.L r, final long l, long f) {
		long[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) & f);
		else u.putLong(arr, l, f = u.getLong(arr, l) & f);
		return f;
	}
	@Override
	public final long pJOr(final R1.L r, final long l, long f) {
		long[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) | f);
		else u.putLong(arr, l, f = u.getLong(arr, l) | f);
		return f;
	}
	@Override
	public final long pJXor(final R1.L r, final long l, long f) {
		long[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) ^ f);
		else u.putLong(arr, l, f = u.getLong(arr, l) ^ f);
		return f;
	}
	@Override
	public final long pJLL(final R1.L r, final long l, long f) {
		long[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) << f);
		else u.putLong(arr, l, f = u.getLong(arr, l) << f);
		return f;
	}
	@Override
	public final long pJRR(final R1.L r, final long l, long f) {
		long[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >> f);
		return f;
	}
	@Override
	public final long pJRRR(final R1.L r, final long l, long f) {
		long[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >>> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >>> f);
		return f;
	}
	@Override
	public final float pFAdd(final R1.L r, final long l, float f) {
		long[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) + f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) + f);
		return f;
	}
	@Override
	public final float pFSub(final R1.L r, final long l, float f) {
		long[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) - f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) - f);
		return f;
	}
	@Override
	public final float pFMul(final R1.L r, final long l, float f) {
		long[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) * f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) * f);
		return f;
	}
	@Override
	public final float pFDiv(final R1.L r, final long l, float f) {
		long[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) / f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) / f);
		return f;
	}
	@Override
	public final float pFMod(final R1.L r, final long l, float f) {
		long[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) % f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) % f);
		return f;
	}
	@Override
	public final double pDAdd(final R1.L r, final long l, double f) {
		long[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) + f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) + f);
		return f;
	}
	@Override
	public final double pDSub(final R1.L r, final long l, double f) {
		long[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) - f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) - f);
		return f;
	}
	@Override
	public final double pDMul(final R1.L r, final long l, double f) {
		long[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) * f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) * f);
		return f;
	}
	@Override
	public final double pDDiv(final R1.L r, final long l, double f) {
		long[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) / f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) / f);
		return f;
	}
	@Override
	public final double pDMod(final R1.L r, final long l, double f) {
		long[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) % f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) % f);
		return f;
	}
	//-------------------
	@Override
	public final boolean pZAnd(final R1.F r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) & b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) & b));
		return b != 0;
	}
	@Override
	public final boolean pZOr(final R1.F r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) | b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) | b));
		return b != 0;
	}
	@Override
	public final boolean pZXor(final R1.F r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) ^ b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) ^ b));
		return b != 0;
	}
	@Override
	public final byte pBAdd(final R1.F r, final long l, byte f) {
		float[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) + f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) + f));
		return f;
	}
	@Override
	public final byte pBSub(final R1.F r, final long l, byte f) {
		float[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) - f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) - f));
		return f;
	}
	@Override
	public final byte pBMul(final R1.F r, final long l, byte f) {
		float[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) * f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) * f));
		return f;
	}
	@Override
	public final byte pBDiv(final R1.F r, final long l, byte f) {
		float[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) / f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) / f));
		return f;
	}
	@Override
	public final byte pBMod(final R1.F r, final long l, byte f) {
		float[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) % f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) % f));
		return f;
	}
	@Override
	public final byte pBAnd(final R1.F r, final long l, byte f) {
		float[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) & f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) & f));
		return f;
	}
	@Override
	public final byte pBOr(final R1.F r, final long l, byte f) {
		float[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) | f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) | f));
		return f;
	}
	@Override
	public final byte pBXor(final R1.F r, final long l, byte f) {
		float[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) ^ f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) ^ f));
		return f;
	}
	@Override
	public final byte pBLL(final R1.F r, final long l, byte f) {
		float[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) << f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) << f));
		return f;
	}
	@Override
	public final byte pBRR(final R1.F r, final long l, byte f) {
		float[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >> f));
		return f;
	}
	@Override
	public final byte pBRRR(final R1.F r, final long l, byte f) {
		float[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >>> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >>> f));
		return f;
	}
	@Override
	public final char pCAdd(final R1.F r, final long l, char f) {
		float[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) + f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) + f));
		return f;
	}
	@Override
	public final char pCSub(final R1.F r, final long l, char f) {
		float[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) - f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) - f));
		return f;
	}
	@Override
	public final char pCMul(final R1.F r, final long l, char f) {
		float[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) * f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) * f));
		return f;
	}
	@Override
	public final char pCDiv(final R1.F r, final long l, char f) {
		float[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) / f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) / f));
		return f;
	}
	@Override
	public final char pCMod(final R1.F r, final long l, char f) {
		float[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) % f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) % f));
		return f;
	}
	@Override
	public final char pCAnd(final R1.F r, final long l, char f) {
		float[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) & f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) & f));
		return f;
	}
	@Override
	public final char pCOr(final R1.F r, final long l, char f) {
		float[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) | f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) | f));
		return f;
	}
	@Override
	public final char pCXor(final R1.F r, final long l, char f) {
		float[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) ^ f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) ^ f));
		return f;
	}
	@Override
	public final char pCLL(final R1.F r, final long l, char f) {
		float[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) << f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) << f));
		return f;
	}
	@Override
	public final char pCRR(final R1.F r, final long l, char f) {
		float[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >> f));
		return f;
	}
	@Override
	public final char pCRRR(final R1.F r, final long l, char f) {
		float[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >>> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >>> f));
		return f;
	}
	@Override
	public final short pSAdd(final R1.F r, final long l, short f) {
		float[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) + f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) + f));
		return f;
	}
	@Override
	public final short pSSub(final R1.F r, final long l, short f) {
		float[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) - f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) - f));
		return f;
	}
	@Override
	public final short pSMul(final R1.F r, final long l, short f) {
		float[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) * f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) * f));
		return f;
	}
	@Override
	public final short pSDiv(final R1.F r, final long l, short f) {
		float[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) / f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) / f));
		return f;
	}
	@Override
	public final short pSMod(final R1.F r, final long l, short f) {
		float[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) % f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) % f));
		return f;
	}
	@Override
	public final short pSAnd(final R1.F r, final long l, short f) {
		float[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) & f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) & f));
		return f;
	}
	@Override
	public final short pSOr(final R1.F r, final long l, short f) {
		float[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) | f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) | f));
		return f;
	}
	@Override
	public final short pSXor(final R1.F r, final long l, short f) {
		float[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) ^ f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) ^ f));
		return f;
	}
	@Override
	public final short pSLL(final R1.F r, final long l, short f) {
		float[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) << f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) << f));
		return f;
	}
	@Override
	public final short pSRR(final R1.F r, final long l, short f) {
		float[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >> f));
		return f;
	}
	@Override
	public final short pSRRR(final R1.F r, final long l, short f) {
		float[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >>> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >>> f));
		return f;
	}
	@Override
	public final int pIAdd(final R1.F r, final long l, int f) {
		float[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) + f);
		else u.putInt(arr, l, f = u.getInt(arr, l) + f);
		return f;
	}
	@Override
	public final int pISub(final R1.F r, final long l, int f) {
		float[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) - f);
		else u.putInt(arr, l, f = u.getInt(arr, l) - f);
		return f;
	}
	@Override
	public final int pIMul(final R1.F r, final long l, int f) {
		float[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) * f);
		else u.putInt(arr, l, f = u.getInt(arr, l) * f);
		return f;
	}
	@Override
	public final int pIDiv(final R1.F r, final long l, int f) {
		float[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) / f);
		else u.putInt(arr, l, f = u.getInt(arr, l) / f);
		return f;
	}
	@Override
	public final int pIMod(final R1.F r, final long l, int f) {
		float[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) % f);
		else u.putInt(arr, l, f = u.getInt(arr, l) % f);
		return f;
	}
	@Override
	public final int pIAnd(final R1.F r, final long l, int f) {
		float[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) & f);
		else u.putInt(arr, l, f = u.getInt(arr, l) & f);
		return f;
	}
	@Override
	public final int pIOr(final R1.F r, final long l, int f) {
		float[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) | f);
		else u.putInt(arr, l, f = u.getInt(arr, l) | f);
		return f;
	}
	@Override
	public final int pIXor(final R1.F r, final long l, int f) {
		float[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) ^ f);
		else u.putInt(arr, l, f = u.getInt(arr, l) ^ f);
		return f;
	}
	@Override
	public final int pILL(final R1.F r, final long l, int f) {
		float[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) << f);
		else u.putInt(arr, l, f = u.getInt(arr, l) << f);
		return f;
	}
	@Override
	public final int pIRR(final R1.F r, final long l, int f) {
		float[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >> f);
		return f;
	}
	@Override
	public final int pIRRR(final R1.F r, final long l, int f) {
		float[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >>> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >>> f);
		return f;
	}
	@Override
	public final long pJAdd(final R1.F r, final long l, long f) {
		float[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) + f);
		else u.putLong(arr, l, f = u.getLong(arr, l) + f);
		return f;
	}
	@Override
	public final long pJSub(final R1.F r, final long l, long f) {
		float[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) - f);
		else u.putLong(arr, l, f = u.getLong(arr, l) - f);
		return f;
	}
	@Override
	public final long pJMul(final R1.F r, final long l, long f) {
		float[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) * f);
		else u.putLong(arr, l, f = u.getLong(arr, l) * f);
		return f;
	}
	@Override
	public final long pJDiv(final R1.F r, final long l, long f) {
		float[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) / f);
		else u.putLong(arr, l, f = u.getLong(arr, l) / f);
		return f;
	}
	@Override
	public final long pJMod(final R1.F r, final long l, long f) {
		float[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) % f);
		else u.putLong(arr, l, f = u.getLong(arr, l) % f);
		return f;
	}
	@Override
	public final long pJAnd(final R1.F r, final long l, long f) {
		float[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) & f);
		else u.putLong(arr, l, f = u.getLong(arr, l) & f);
		return f;
	}
	@Override
	public final long pJOr(final R1.F r, final long l, long f) {
		float[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) | f);
		else u.putLong(arr, l, f = u.getLong(arr, l) | f);
		return f;
	}
	@Override
	public final long pJXor(final R1.F r, final long l, long f) {
		float[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) ^ f);
		else u.putLong(arr, l, f = u.getLong(arr, l) ^ f);
		return f;
	}
	@Override
	public final long pJLL(final R1.F r, final long l, long f) {
		float[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) << f);
		else u.putLong(arr, l, f = u.getLong(arr, l) << f);
		return f;
	}
	@Override
	public final long pJRR(final R1.F r, final long l, long f) {
		float[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >> f);
		return f;
	}
	@Override
	public final long pJRRR(final R1.F r, final long l, long f) {
		float[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >>> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >>> f);
		return f;
	}
	@Override
	public final float pFAdd(final R1.F r, final long l, float f) {
		float[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) + f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) + f);
		return f;
	}
	@Override
	public final float pFSub(final R1.F r, final long l, float f) {
		float[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) - f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) - f);
		return f;
	}
	@Override
	public final float pFMul(final R1.F r, final long l, float f) {
		float[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) * f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) * f);
		return f;
	}
	@Override
	public final float pFDiv(final R1.F r, final long l, float f) {
		float[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) / f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) / f);
		return f;
	}
	@Override
	public final float pFMod(final R1.F r, final long l, float f) {
		float[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) % f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) % f);
		return f;
	}
	@Override
	public final double pDAdd(final R1.F r, final long l, double f) {
		float[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) + f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) + f);
		return f;
	}
	@Override
	public final double pDSub(final R1.F r, final long l, double f) {
		float[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) - f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) - f);
		return f;
	}
	@Override
	public final double pDMul(final R1.F r, final long l, double f) {
		float[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) * f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) * f);
		return f;
	}
	@Override
	public final double pDDiv(final R1.F r, final long l, double f) {
		float[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) / f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) / f);
		return f;
	}
	@Override
	public final double pDMod(final R1.F r, final long l, double f) {
		float[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) % f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) % f);
		return f;
	}
	//-------------------
	@Override
	public final boolean pZAnd(final R1.D r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) & b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) & b));
		return b != 0;
	}
	@Override
	public final boolean pZOr(final R1.D r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) | b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) | b));
		return b != 0;
	}
	@Override
	public final boolean pZXor(final R1.D r, final long l, boolean f) {
		Object arr = r.array;
		byte b = f?(byte)1:(byte)0;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(arr, l) ^ b));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l) ^ b));
		return b != 0;
	}
	@Override
	public final byte pBAdd(final R1.D r, final long l, byte f) {
		double[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) + f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) + f));
		return f;
	}
	@Override
	public final byte pBSub(final R1.D r, final long l, byte f) {
		double[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) - f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) - f));
		return f;
	}
	@Override
	public final byte pBMul(final R1.D r, final long l, byte f) {
		double[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) * f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) * f));
		return f;
	}
	@Override
	public final byte pBDiv(final R1.D r, final long l, byte f) {
		double[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) / f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) / f));
		return f;
	}
	@Override
	public final byte pBMod(final R1.D r, final long l, byte f) {
		double[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) % f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) % f));
		return f;
	}
	@Override
	public final byte pBAnd(final R1.D r, final long l, byte f) {
		double[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) & f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) & f));
		return f;
	}
	@Override
	public final byte pBOr(final R1.D r, final long l, byte f) {
		double[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) | f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) | f));
		return f;
	}
	@Override
	public final byte pBXor(final R1.D r, final long l, byte f) {
		double[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) ^ f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) ^ f));
		return f;
	}
	@Override
	public final byte pBLL(final R1.D r, final long l, byte f) {
		double[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) << f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) << f));
		return f;
	}
	@Override
	public final byte pBRR(final R1.D r, final long l, byte f) {
		double[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >> f));
		return f;
	}
	@Override
	public final byte pBRRR(final R1.D r, final long l, byte f) {
		double[] arr = r.array;
		if(arr == null) u.putByte(null, l, f = (byte)(u.getByte(arr, l) >>> f));
		else u.putByte(arr, l, f = (byte)(u.getByte(arr, l) >>> f));
		return f;
	}
	@Override
	public final char pCAdd(final R1.D r, final long l, char f) {
		double[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) + f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) + f));
		return f;
	}
	@Override
	public final char pCSub(final R1.D r, final long l, char f) {
		double[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) - f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) - f));
		return f;
	}
	@Override
	public final char pCMul(final R1.D r, final long l, char f) {
		double[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) * f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) * f));
		return f;
	}
	@Override
	public final char pCDiv(final R1.D r, final long l, char f) {
		double[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) / f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) / f));
		return f;
	}
	@Override
	public final char pCMod(final R1.D r, final long l, char f) {
		double[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) % f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) % f));
		return f;
	}
	@Override
	public final char pCAnd(final R1.D r, final long l, char f) {
		double[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) & f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) & f));
		return f;
	}
	@Override
	public final char pCOr(final R1.D r, final long l, char f) {
		double[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) | f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) | f));
		return f;
	}
	@Override
	public final char pCXor(final R1.D r, final long l, char f) {
		double[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) ^ f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) ^ f));
		return f;
	}
	@Override
	public final char pCLL(final R1.D r, final long l, char f) {
		double[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) << f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) << f));
		return f;
	}
	@Override
	public final char pCRR(final R1.D r, final long l, char f) {
		double[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >> f));
		return f;
	}
	@Override
	public final char pCRRR(final R1.D r, final long l, char f) {
		double[] arr = r.array;
		if(arr == null) u.putChar(null, l, f = (char)(u.getChar(arr, l) >>> f));
		else u.putChar(arr, l, f = (char)(u.getChar(arr, l) >>> f));
		return f;
	}
	@Override
	public final short pSAdd(final R1.D r, final long l, short f) {
		double[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) + f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) + f));
		return f;
	}
	@Override
	public final short pSSub(final R1.D r, final long l, short f) {
		double[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) - f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) - f));
		return f;
	}
	@Override
	public final short pSMul(final R1.D r, final long l, short f) {
		double[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) * f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) * f));
		return f;
	}
	@Override
	public final short pSDiv(final R1.D r, final long l, short f) {
		double[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) / f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) / f));
		return f;
	}
	@Override
	public final short pSMod(final R1.D r, final long l, short f) {
		double[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) % f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) % f));
		return f;
	}
	@Override
	public final short pSAnd(final R1.D r, final long l, short f) {
		double[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) & f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) & f));
		return f;
	}
	@Override
	public final short pSOr(final R1.D r, final long l, short f) {
		double[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) | f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) | f));
		return f;
	}
	@Override
	public final short pSXor(final R1.D r, final long l, short f) {
		double[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) ^ f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) ^ f));
		return f;
	}
	@Override
	public final short pSLL(final R1.D r, final long l, short f) {
		double[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) << f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) << f));
		return f;
	}
	@Override
	public final short pSRR(final R1.D r, final long l, short f) {
		double[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >> f));
		return f;
	}
	@Override
	public final short pSRRR(final R1.D r, final long l, short f) {
		double[] arr = r.array;
		if(arr == null) u.putShort(null, l, f = (short)(u.getShort(arr, l) >>> f));
		else u.putShort(arr, l, f = (short)(u.getShort(arr, l) >>> f));
		return f;
	}
	@Override
	public final int pIAdd(final R1.D r, final long l, int f) {
		double[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) + f);
		else u.putInt(arr, l, f = u.getInt(arr, l) + f);
		return f;
	}
	@Override
	public final int pISub(final R1.D r, final long l, int f) {
		double[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) - f);
		else u.putInt(arr, l, f = u.getInt(arr, l) - f);
		return f;
	}
	@Override
	public final int pIMul(final R1.D r, final long l, int f) {
		double[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) * f);
		else u.putInt(arr, l, f = u.getInt(arr, l) * f);
		return f;
	}
	@Override
	public final int pIDiv(final R1.D r, final long l, int f) {
		double[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) / f);
		else u.putInt(arr, l, f = u.getInt(arr, l) / f);
		return f;
	}
	@Override
	public final int pIMod(final R1.D r, final long l, int f) {
		double[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) % f);
		else u.putInt(arr, l, f = u.getInt(arr, l) % f);
		return f;
	}
	@Override
	public final int pIAnd(final R1.D r, final long l, int f) {
		double[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) & f);
		else u.putInt(arr, l, f = u.getInt(arr, l) & f);
		return f;
	}
	@Override
	public final int pIOr(final R1.D r, final long l, int f) {
		double[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) | f);
		else u.putInt(arr, l, f = u.getInt(arr, l) | f);
		return f;
	}
	@Override
	public final int pIXor(final R1.D r, final long l, int f) {
		double[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) ^ f);
		else u.putInt(arr, l, f = u.getInt(arr, l) ^ f);
		return f;
	}
	@Override
	public final int pILL(final R1.D r, final long l, int f) {
		double[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) << f);
		else u.putInt(arr, l, f = u.getInt(arr, l) << f);
		return f;
	}
	@Override
	public final int pIRR(final R1.D r, final long l, int f) {
		double[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >> f);
		return f;
	}
	@Override
	public final int pIRRR(final R1.D r, final long l, int f) {
		double[] arr = r.array;
		if(arr == null) u.putInt(null, l, f = u.getInt(arr, l) >>> f);
		else u.putInt(arr, l, f = u.getInt(arr, l) >>> f);
		return f;
	}
	@Override
	public final long pJAdd(final R1.D r, final long l, long f) {
		double[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) + f);
		else u.putLong(arr, l, f = u.getLong(arr, l) + f);
		return f;
	}
	@Override
	public final long pJSub(final R1.D r, final long l, long f) {
		double[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) - f);
		else u.putLong(arr, l, f = u.getLong(arr, l) - f);
		return f;
	}
	@Override
	public final long pJMul(final R1.D r, final long l, long f) {
		double[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) * f);
		else u.putLong(arr, l, f = u.getLong(arr, l) * f);
		return f;
	}
	@Override
	public final long pJDiv(final R1.D r, final long l, long f) {
		double[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) / f);
		else u.putLong(arr, l, f = u.getLong(arr, l) / f);
		return f;
	}
	@Override
	public final long pJMod(final R1.D r, final long l, long f) {
		double[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) % f);
		else u.putLong(arr, l, f = u.getLong(arr, l) % f);
		return f;
	}
	@Override
	public final long pJAnd(final R1.D r, final long l, long f) {
		double[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) & f);
		else u.putLong(arr, l, f = u.getLong(arr, l) & f);
		return f;
	}
	@Override
	public final long pJOr(final R1.D r, final long l, long f) {
		double[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) | f);
		else u.putLong(arr, l, f = u.getLong(arr, l) | f);
		return f;
	}
	@Override
	public final long pJXor(final R1.D r, final long l, long f) {
		double[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) ^ f);
		else u.putLong(arr, l, f = u.getLong(arr, l) ^ f);
		return f;
	}
	@Override
	public final long pJLL(final R1.D r, final long l, long f) {
		double[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) << f);
		else u.putLong(arr, l, f = u.getLong(arr, l) << f);
		return f;
	}
	@Override
	public final long pJRR(final R1.D r, final long l, long f) {
		double[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >> f);
		return f;
	}
	@Override
	public final long pJRRR(final R1.D r, final long l, long f) {
		double[] arr = r.array;
		if(arr == null) u.putLong(null, l, f = u.getLong(arr, l) >>> f);
		else u.putLong(arr, l, f = u.getLong(arr, l) >>> f);
		return f;
	}
	@Override
	public final float pFAdd(final R1.D r, final long l, float f) {
		double[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) + f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) + f);
		return f;
	}
	@Override
	public final float pFSub(final R1.D r, final long l, float f) {
		double[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) - f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) - f);
		return f;
	}
	@Override
	public final float pFMul(final R1.D r, final long l, float f) {
		double[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) * f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) * f);
		return f;
	}
	@Override
	public final float pFDiv(final R1.D r, final long l, float f) {
		double[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) / f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) / f);
		return f;
	}
	@Override
	public final float pFMod(final R1.D r, final long l, float f) {
		double[] arr = r.array;
		if(arr == null) u.putFloat(null, l, f = u.getFloat(arr, l) % f);
		else u.putFloat(arr, l, f = u.getFloat(arr, l) % f);
		return f;
	}
	@Override
	public final double pDAdd(final R1.D r, final long l, double f) {
		double[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) + f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) + f);
		return f;
	}
	@Override
	public final double pDSub(final R1.D r, final long l, double f) {
		double[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) - f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) - f);
		return f;
	}
	@Override
	public final double pDMul(final R1.D r, final long l, double f) {
		double[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) * f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) * f);
		return f;
	}
	@Override
	public final double pDDiv(final R1.D r, final long l, double f) {
		double[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) / f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) / f);
		return f;
	}
	@Override
	public final double pDMod(final R1.D r, final long l, double f) {
		double[] arr = r.array;
		if(arr == null) u.putDouble(null, l, f = u.getDouble(arr, l) % f);
		else u.putDouble(arr, l, f = u.getDouble(arr, l) % f);
		return f;
	}
	//-------------------

	@Override
	public final byte gBPrI(final R1 r, final long l) {
		Object arr = r.getArray();
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)+1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)+1));
		return b;
	}
	@Override
	public final byte gBPrD(final R1 r, final long l) {
		Object arr = r.getArray();
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)-1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)-1));
		return b;
	}
	@Override
	public final byte gBPoI(final R1 r, final long l) {
		Object arr = r.getArray();
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))+1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))+1));
		return b;
	}
	@Override
	public final byte gBPoD(final R1 r, final long l) {
		Object arr = r.getArray();
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))-1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))-1));
		return b;
	}
	@Override
	public final char gCPrI(final R1 r, final long l) {
		Object arr = r.getArray();
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)+1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)+1));
		return b;
	}
	@Override
	public final char gCPrD(final R1 r, final long l) {
		Object arr = r.getArray();
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)-1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)-1));
		return b;
	}
	@Override
	public final char gCPoI(final R1 r, final long l) {
		Object arr = r.getArray();
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))+1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))+1));
		return b;
	}
	@Override
	public final char gCPoD(final R1 r, final long l) {
		Object arr = r.getArray();
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))-1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))-1));
		return b;
	}
	@Override
	public final short gSPrI(final R1 r, final long l) {
		Object arr = r.getArray();
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)+1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)+1));
		return b;
	}
	@Override
	public final short gSPrD(final R1 r, final long l) {
		Object arr = r.getArray();
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)-1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)-1));
		return b;
	}
	@Override
	public final short gSPoI(final R1 r, final long l) {
		Object arr = r.getArray();
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))+1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))+1));
		return b;
	}
	@Override
	public final short gSPoD(final R1 r, final long l) {
		Object arr = r.getArray();
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))-1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))-1));
		return b;
	}
	@Override
	public final int gIPrI(final R1 r, final long l) {
		Object arr = r.getArray();
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)+1);
		else u.putInt(arr, l, b = u.getInt(arr, l)+1);
		return b;
	}
	@Override
	public final int gIPrD(final R1 r, final long l) {
		Object arr = r.getArray();
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)-1);
		else u.putInt(arr, l, b = u.getInt(arr, l)-1);
		return b;
	}
	@Override
	public final int gIPoI(final R1 r, final long l) {
		Object arr = r.getArray();
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))+1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))+1);
		return b;
	}
	@Override
	public final int gIPoD(final R1 r, final long l) {
		Object arr = r.getArray();
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))-1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))-1);
		return b;
	}
	@Override
	public final long gJPrI(final R1 r, final long l) {
		Object arr = r.getArray();
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)+1);
		else u.putLong(arr, l, b = u.getLong(arr, l)+1);
		return b;
	}
	@Override
	public final long gJPrD(final R1 r, final long l) {
		Object arr = r.getArray();
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)-1);
		else u.putLong(arr, l, b = u.getLong(arr, l)-1);
		return b;
	}
	@Override
	public final long gJPoI(final R1 r, final long l) {
		Object arr = r.getArray();
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))+1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))+1);
		return b;
	}
	@Override
	public final long gJPoD(final R1 r, final long l) {
		Object arr = r.getArray();
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))-1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))-1);
		return b;
	}
	@Override
	public final float gFPrI(final R1 r, final long l) {
		Object arr = r.getArray();
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)+1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)+1);
		return b;
	}
	@Override
	public final float gFPrD(final R1 r, final long l) {
		Object arr = r.getArray();
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)-1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)-1);
		return b;
	}
	@Override
	public final float gFPoI(final R1 r, final long l) {
		Object arr = r.getArray();
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))+1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))+1);
		return b;
	}
	@Override
	public final float gFPoD(final R1 r, final long l) {
		Object arr = r.getArray();
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))-1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))-1);
		return b;
	}
	@Override
	public final double gDPrI(final R1 r, final long l) {
		Object arr = r.getArray();
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)+1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)+1);
		return b;
	}
	@Override
	public final double gDPrD(final R1 r, final long l) {
		Object arr = r.getArray();
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)-1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)-1);
		return b;
	}
	@Override
	public final double gDPoI(final R1 r, final long l) {
		Object arr = r.getArray();
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))+1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))+1);
		return b;
	}
	@Override
	public final double gDPoD(final R1 r, final long l) {
		Object arr = r.getArray();
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))-1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))-1);
		return b;
	}
	
	@Override
	public final byte gBPrI(final R1.A r, final long l) {
		Object arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)+1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)+1));
		return b;
	}
	@Override
	public final byte gBPrD(final R1.A r, final long l) {
		Object arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)-1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)-1));
		return b;
	}
	@Override
	public final byte gBPoI(final R1.A r, final long l) {
		Object arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))+1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))+1));
		return b;
	}
	@Override
	public final byte gBPoD(final R1.A r, final long l) {
		Object arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))-1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))-1));
		return b;
	}
	@Override
	public final char gCPrI(final R1.A r, final long l) {
		Object arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)+1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)+1));
		return b;
	}
	@Override
	public final char gCPrD(final R1.A r, final long l) {
		Object arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)-1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)-1));
		return b;
	}
	@Override
	public final char gCPoI(final R1.A r, final long l) {
		Object arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))+1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))+1));
		return b;
	}
	@Override
	public final char gCPoD(final R1.A r, final long l) {
		Object arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))-1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))-1));
		return b;
	}
	@Override
	public final short gSPrI(final R1.A r, final long l) {
		Object arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)+1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)+1));
		return b;
	}
	@Override
	public final short gSPrD(final R1.A r, final long l) {
		Object arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)-1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)-1));
		return b;
	}
	@Override
	public final short gSPoI(final R1.A r, final long l) {
		Object arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))+1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))+1));
		return b;
	}
	@Override
	public final short gSPoD(final R1.A r, final long l) {
		Object arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))-1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))-1));
		return b;
	}
	@Override
	public final int gIPrI(final R1.A r, final long l) {
		Object arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)+1);
		else u.putInt(arr, l, b = u.getInt(arr, l)+1);
		return b;
	}
	@Override
	public final int gIPrD(final R1.A r, final long l) {
		Object arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)-1);
		else u.putInt(arr, l, b = u.getInt(arr, l)-1);
		return b;
	}
	@Override
	public final int gIPoI(final R1.A r, final long l) {
		Object arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))+1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))+1);
		return b;
	}
	@Override
	public final int gIPoD(final R1.A r, final long l) {
		Object arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))-1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))-1);
		return b;
	}
	@Override
	public final long gJPrI(final R1.A r, final long l) {
		Object arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)+1);
		else u.putLong(arr, l, b = u.getLong(arr, l)+1);
		return b;
	}
	@Override
	public final long gJPrD(final R1.A r, final long l) {
		Object arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)-1);
		else u.putLong(arr, l, b = u.getLong(arr, l)-1);
		return b;
	}
	@Override
	public final long gJPoI(final R1.A r, final long l) {
		Object arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))+1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))+1);
		return b;
	}
	@Override
	public final long gJPoD(final R1.A r, final long l) {
		Object arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))-1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))-1);
		return b;
	}
	@Override
	public final float gFPrI(final R1.A r, final long l) {
		Object arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)+1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)+1);
		return b;
	}
	@Override
	public final float gFPrD(final R1.A r, final long l) {
		Object arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)-1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)-1);
		return b;
	}
	@Override
	public final float gFPoI(final R1.A r, final long l) {
		Object arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))+1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))+1);
		return b;
	}
	@Override
	public final float gFPoD(final R1.A r, final long l) {
		Object arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))-1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))-1);
		return b;
	}
	@Override
	public final double gDPrI(final R1.A r, final long l) {
		Object arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)+1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)+1);
		return b;
	}
	@Override
	public final double gDPrD(final R1.A r, final long l) {
		Object arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)-1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)-1);
		return b;
	}
	@Override
	public final double gDPoI(final R1.A r, final long l) {
		Object arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))+1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))+1);
		return b;
	}
	@Override
	public final double gDPoD(final R1.A r, final long l) {
		Object arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))-1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))-1);
		return b;
	}
	@Override
	public final byte gBPrI(final R1.B r, final long l) {
		byte[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)+1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)+1));
		return b;
	}
	@Override
	public final byte gBPrD(final R1.B r, final long l) {
		byte[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)-1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)-1));
		return b;
	}
	@Override
	public final byte gBPoI(final R1.B r, final long l) {
		byte[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))+1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))+1));
		return b;
	}
	@Override
	public final byte gBPoD(final R1.B r, final long l) {
		byte[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))-1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))-1));
		return b;
	}
	@Override
	public final char gCPrI(final R1.B r, final long l) {
		byte[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)+1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)+1));
		return b;
	}
	@Override
	public final char gCPrD(final R1.B r, final long l) {
		byte[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)-1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)-1));
		return b;
	}
	@Override
	public final char gCPoI(final R1.B r, final long l) {
		byte[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))+1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))+1));
		return b;
	}
	@Override
	public final char gCPoD(final R1.B r, final long l) {
		byte[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))-1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))-1));
		return b;
	}
	@Override
	public final short gSPrI(final R1.B r, final long l) {
		byte[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)+1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)+1));
		return b;
	}
	@Override
	public final short gSPrD(final R1.B r, final long l) {
		byte[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)-1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)-1));
		return b;
	}
	@Override
	public final short gSPoI(final R1.B r, final long l) {
		byte[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))+1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))+1));
		return b;
	}
	@Override
	public final short gSPoD(final R1.B r, final long l) {
		byte[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))-1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))-1));
		return b;
	}
	@Override
	public final int gIPrI(final R1.B r, final long l) {
		byte[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)+1);
		else u.putInt(arr, l, b = u.getInt(arr, l)+1);
		return b;
	}
	@Override
	public final int gIPrD(final R1.B r, final long l) {
		byte[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)-1);
		else u.putInt(arr, l, b = u.getInt(arr, l)-1);
		return b;
	}
	@Override
	public final int gIPoI(final R1.B r, final long l) {
		byte[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))+1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))+1);
		return b;
	}
	@Override
	public final int gIPoD(final R1.B r, final long l) {
		byte[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))-1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))-1);
		return b;
	}
	@Override
	public final long gJPrI(final R1.B r, final long l) {
		byte[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)+1);
		else u.putLong(arr, l, b = u.getLong(arr, l)+1);
		return b;
	}
	@Override
	public final long gJPrD(final R1.B r, final long l) {
		byte[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)-1);
		else u.putLong(arr, l, b = u.getLong(arr, l)-1);
		return b;
	}
	@Override
	public final long gJPoI(final R1.B r, final long l) {
		byte[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))+1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))+1);
		return b;
	}
	@Override
	public final long gJPoD(final R1.B r, final long l) {
		byte[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))-1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))-1);
		return b;
	}
	@Override
	public final float gFPrI(final R1.B r, final long l) {
		byte[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)+1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)+1);
		return b;
	}
	@Override
	public final float gFPrD(final R1.B r, final long l) {
		byte[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)-1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)-1);
		return b;
	}
	@Override
	public final float gFPoI(final R1.B r, final long l) {
		byte[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))+1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))+1);
		return b;
	}
	@Override
	public final float gFPoD(final R1.B r, final long l) {
		byte[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))-1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))-1);
		return b;
	}
	@Override
	public final double gDPrI(final R1.B r, final long l) {
		byte[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)+1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)+1);
		return b;
	}
	@Override
	public final double gDPrD(final R1.B r, final long l) {
		byte[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)-1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)-1);
		return b;
	}
	@Override
	public final double gDPoI(final R1.B r, final long l) {
		byte[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))+1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))+1);
		return b;
	}
	@Override
	public final double gDPoD(final R1.B r, final long l) {
		byte[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))-1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))-1);
		return b;
	}
	@Override
	public final byte gBPrI(final R1.C r, final long l) {
		char[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)+1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)+1));
		return b;
	}
	@Override
	public final byte gBPrD(final R1.C r, final long l) {
		char[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)-1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)-1));
		return b;
	}
	@Override
	public final byte gBPoI(final R1.C r, final long l) {
		char[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))+1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))+1));
		return b;
	}
	@Override
	public final byte gBPoD(final R1.C r, final long l) {
		char[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))-1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))-1));
		return b;
	}
	@Override
	public final char gCPrI(final R1.C r, final long l) {
		char[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)+1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)+1));
		return b;
	}
	@Override
	public final char gCPrD(final R1.C r, final long l) {
		char[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)-1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)-1));
		return b;
	}
	@Override
	public final char gCPoI(final R1.C r, final long l) {
		char[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))+1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))+1));
		return b;
	}
	@Override
	public final char gCPoD(final R1.C r, final long l) {
		char[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))-1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))-1));
		return b;
	}
	@Override
	public final short gSPrI(final R1.C r, final long l) {
		char[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)+1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)+1));
		return b;
	}
	@Override
	public final short gSPrD(final R1.C r, final long l) {
		char[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)-1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)-1));
		return b;
	}
	@Override
	public final short gSPoI(final R1.C r, final long l) {
		char[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))+1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))+1));
		return b;
	}
	@Override
	public final short gSPoD(final R1.C r, final long l) {
		char[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))-1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))-1));
		return b;
	}
	@Override
	public final int gIPrI(final R1.C r, final long l) {
		char[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)+1);
		else u.putInt(arr, l, b = u.getInt(arr, l)+1);
		return b;
	}
	@Override
	public final int gIPrD(final R1.C r, final long l) {
		char[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)-1);
		else u.putInt(arr, l, b = u.getInt(arr, l)-1);
		return b;
	}
	@Override
	public final int gIPoI(final R1.C r, final long l) {
		char[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))+1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))+1);
		return b;
	}
	@Override
	public final int gIPoD(final R1.C r, final long l) {
		char[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))-1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))-1);
		return b;
	}
	@Override
	public final long gJPrI(final R1.C r, final long l) {
		char[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)+1);
		else u.putLong(arr, l, b = u.getLong(arr, l)+1);
		return b;
	}
	@Override
	public final long gJPrD(final R1.C r, final long l) {
		char[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)-1);
		else u.putLong(arr, l, b = u.getLong(arr, l)-1);
		return b;
	}
	@Override
	public final long gJPoI(final R1.C r, final long l) {
		char[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))+1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))+1);
		return b;
	}
	@Override
	public final long gJPoD(final R1.C r, final long l) {
		char[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))-1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))-1);
		return b;
	}
	@Override
	public final float gFPrI(final R1.C r, final long l) {
		char[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)+1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)+1);
		return b;
	}
	@Override
	public final float gFPrD(final R1.C r, final long l) {
		char[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)-1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)-1);
		return b;
	}
	@Override
	public final float gFPoI(final R1.C r, final long l) {
		char[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))+1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))+1);
		return b;
	}
	@Override
	public final float gFPoD(final R1.C r, final long l) {
		char[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))-1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))-1);
		return b;
	}
	@Override
	public final double gDPrI(final R1.C r, final long l) {
		char[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)+1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)+1);
		return b;
	}
	@Override
	public final double gDPrD(final R1.C r, final long l) {
		char[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)-1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)-1);
		return b;
	}
	@Override
	public final double gDPoI(final R1.C r, final long l) {
		char[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))+1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))+1);
		return b;
	}
	@Override
	public final double gDPoD(final R1.C r, final long l) {
		char[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))-1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))-1);
		return b;
	}
	@Override
	public final byte gBPrI(final R1.S r, final long l) {
		short[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)+1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)+1));
		return b;
	}
	@Override
	public final byte gBPrD(final R1.S r, final long l) {
		short[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)-1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)-1));
		return b;
	}
	@Override
	public final byte gBPoI(final R1.S r, final long l) {
		short[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))+1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))+1));
		return b;
	}
	@Override
	public final byte gBPoD(final R1.S r, final long l) {
		short[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))-1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))-1));
		return b;
	}
	@Override
	public final char gCPrI(final R1.S r, final long l) {
		short[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)+1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)+1));
		return b;
	}
	@Override
	public final char gCPrD(final R1.S r, final long l) {
		short[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)-1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)-1));
		return b;
	}
	@Override
	public final char gCPoI(final R1.S r, final long l) {
		short[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))+1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))+1));
		return b;
	}
	@Override
	public final char gCPoD(final R1.S r, final long l) {
		short[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))-1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))-1));
		return b;
	}
	@Override
	public final short gSPrI(final R1.S r, final long l) {
		short[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)+1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)+1));
		return b;
	}
	@Override
	public final short gSPrD(final R1.S r, final long l) {
		short[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)-1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)-1));
		return b;
	}
	@Override
	public final short gSPoI(final R1.S r, final long l) {
		short[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))+1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))+1));
		return b;
	}
	@Override
	public final short gSPoD(final R1.S r, final long l) {
		short[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))-1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))-1));
		return b;
	}
	@Override
	public final int gIPrI(final R1.S r, final long l) {
		short[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)+1);
		else u.putInt(arr, l, b = u.getInt(arr, l)+1);
		return b;
	}
	@Override
	public final int gIPrD(final R1.S r, final long l) {
		short[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)-1);
		else u.putInt(arr, l, b = u.getInt(arr, l)-1);
		return b;
	}
	@Override
	public final int gIPoI(final R1.S r, final long l) {
		short[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))+1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))+1);
		return b;
	}
	@Override
	public final int gIPoD(final R1.S r, final long l) {
		short[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))-1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))-1);
		return b;
	}
	@Override
	public final long gJPrI(final R1.S r, final long l) {
		short[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)+1);
		else u.putLong(arr, l, b = u.getLong(arr, l)+1);
		return b;
	}
	@Override
	public final long gJPrD(final R1.S r, final long l) {
		short[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)-1);
		else u.putLong(arr, l, b = u.getLong(arr, l)-1);
		return b;
	}
	@Override
	public final long gJPoI(final R1.S r, final long l) {
		short[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))+1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))+1);
		return b;
	}
	@Override
	public final long gJPoD(final R1.S r, final long l) {
		short[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))-1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))-1);
		return b;
	}
	@Override
	public final float gFPrI(final R1.S r, final long l) {
		short[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)+1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)+1);
		return b;
	}
	@Override
	public final float gFPrD(final R1.S r, final long l) {
		short[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)-1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)-1);
		return b;
	}
	@Override
	public final float gFPoI(final R1.S r, final long l) {
		short[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))+1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))+1);
		return b;
	}
	@Override
	public final float gFPoD(final R1.S r, final long l) {
		short[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))-1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))-1);
		return b;
	}
	@Override
	public final double gDPrI(final R1.S r, final long l) {
		short[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)+1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)+1);
		return b;
	}
	@Override
	public final double gDPrD(final R1.S r, final long l) {
		short[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)-1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)-1);
		return b;
	}
	@Override
	public final double gDPoI(final R1.S r, final long l) {
		short[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))+1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))+1);
		return b;
	}
	@Override
	public final double gDPoD(final R1.S r, final long l) {
		short[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))-1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))-1);
		return b;
	}
	@Override
	public final byte gBPrI(final R1.I r, final long l) {
		int[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)+1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)+1));
		return b;
	}
	@Override
	public final byte gBPrD(final R1.I r, final long l) {
		int[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)-1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)-1));
		return b;
	}
	@Override
	public final byte gBPoI(final R1.I r, final long l) {
		int[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))+1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))+1));
		return b;
	}
	@Override
	public final byte gBPoD(final R1.I r, final long l) {
		int[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))-1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))-1));
		return b;
	}
	@Override
	public final char gCPrI(final R1.I r, final long l) {
		int[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)+1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)+1));
		return b;
	}
	@Override
	public final char gCPrD(final R1.I r, final long l) {
		int[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)-1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)-1));
		return b;
	}
	@Override
	public final char gCPoI(final R1.I r, final long l) {
		int[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))+1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))+1));
		return b;
	}
	@Override
	public final char gCPoD(final R1.I r, final long l) {
		int[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))-1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))-1));
		return b;
	}
	@Override
	public final short gSPrI(final R1.I r, final long l) {
		int[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)+1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)+1));
		return b;
	}
	@Override
	public final short gSPrD(final R1.I r, final long l) {
		int[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)-1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)-1));
		return b;
	}
	@Override
	public final short gSPoI(final R1.I r, final long l) {
		int[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))+1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))+1));
		return b;
	}
	@Override
	public final short gSPoD(final R1.I r, final long l) {
		int[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))-1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))-1));
		return b;
	}
	@Override
	public final int gIPrI(final R1.I r, final long l) {
		int[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)+1);
		else u.putInt(arr, l, b = u.getInt(arr, l)+1);
		return b;
	}
	@Override
	public final int gIPrD(final R1.I r, final long l) {
		int[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)-1);
		else u.putInt(arr, l, b = u.getInt(arr, l)-1);
		return b;
	}
	@Override
	public final int gIPoI(final R1.I r, final long l) {
		int[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))+1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))+1);
		return b;
	}
	@Override
	public final int gIPoD(final R1.I r, final long l) {
		int[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))-1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))-1);
		return b;
	}
	@Override
	public final long gJPrI(final R1.I r, final long l) {
		int[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)+1);
		else u.putLong(arr, l, b = u.getLong(arr, l)+1);
		return b;
	}
	@Override
	public final long gJPrD(final R1.I r, final long l) {
		int[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)-1);
		else u.putLong(arr, l, b = u.getLong(arr, l)-1);
		return b;
	}
	@Override
	public final long gJPoI(final R1.I r, final long l) {
		int[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))+1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))+1);
		return b;
	}
	@Override
	public final long gJPoD(final R1.I r, final long l) {
		int[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))-1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))-1);
		return b;
	}
	@Override
	public final float gFPrI(final R1.I r, final long l) {
		int[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)+1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)+1);
		return b;
	}
	@Override
	public final float gFPrD(final R1.I r, final long l) {
		int[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)-1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)-1);
		return b;
	}
	@Override
	public final float gFPoI(final R1.I r, final long l) {
		int[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))+1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))+1);
		return b;
	}
	@Override
	public final float gFPoD(final R1.I r, final long l) {
		int[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))-1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))-1);
		return b;
	}
	@Override
	public final double gDPrI(final R1.I r, final long l) {
		int[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)+1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)+1);
		return b;
	}
	@Override
	public final double gDPrD(final R1.I r, final long l) {
		int[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)-1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)-1);
		return b;
	}
	@Override
	public final double gDPoI(final R1.I r, final long l) {
		int[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))+1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))+1);
		return b;
	}
	@Override
	public final double gDPoD(final R1.I r, final long l) {
		int[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))-1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))-1);
		return b;
	}
	@Override
	public final byte gBPrI(final R1.L r, final long l) {
		long[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)+1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)+1));
		return b;
	}
	@Override
	public final byte gBPrD(final R1.L r, final long l) {
		long[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)-1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)-1));
		return b;
	}
	@Override
	public final byte gBPoI(final R1.L r, final long l) {
		long[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))+1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))+1));
		return b;
	}
	@Override
	public final byte gBPoD(final R1.L r, final long l) {
		long[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))-1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))-1));
		return b;
	}
	@Override
	public final char gCPrI(final R1.L r, final long l) {
		long[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)+1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)+1));
		return b;
	}
	@Override
	public final char gCPrD(final R1.L r, final long l) {
		long[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)-1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)-1));
		return b;
	}
	@Override
	public final char gCPoI(final R1.L r, final long l) {
		long[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))+1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))+1));
		return b;
	}
	@Override
	public final char gCPoD(final R1.L r, final long l) {
		long[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))-1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))-1));
		return b;
	}
	@Override
	public final short gSPrI(final R1.L r, final long l) {
		long[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)+1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)+1));
		return b;
	}
	@Override
	public final short gSPrD(final R1.L r, final long l) {
		long[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)-1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)-1));
		return b;
	}
	@Override
	public final short gSPoI(final R1.L r, final long l) {
		long[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))+1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))+1));
		return b;
	}
	@Override
	public final short gSPoD(final R1.L r, final long l) {
		long[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))-1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))-1));
		return b;
	}
	@Override
	public final int gIPrI(final R1.L r, final long l) {
		long[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)+1);
		else u.putInt(arr, l, b = u.getInt(arr, l)+1);
		return b;
	}
	@Override
	public final int gIPrD(final R1.L r, final long l) {
		long[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)-1);
		else u.putInt(arr, l, b = u.getInt(arr, l)-1);
		return b;
	}
	@Override
	public final int gIPoI(final R1.L r, final long l) {
		long[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))+1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))+1);
		return b;
	}
	@Override
	public final int gIPoD(final R1.L r, final long l) {
		long[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))-1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))-1);
		return b;
	}
	@Override
	public final long gJPrI(final R1.L r, final long l) {
		long[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)+1);
		else u.putLong(arr, l, b = u.getLong(arr, l)+1);
		return b;
	}
	@Override
	public final long gJPrD(final R1.L r, final long l) {
		long[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)-1);
		else u.putLong(arr, l, b = u.getLong(arr, l)-1);
		return b;
	}
	@Override
	public final long gJPoI(final R1.L r, final long l) {
		long[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))+1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))+1);
		return b;
	}
	@Override
	public final long gJPoD(final R1.L r, final long l) {
		long[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))-1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))-1);
		return b;
	}
	@Override
	public final float gFPrI(final R1.L r, final long l) {
		long[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)+1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)+1);
		return b;
	}
	@Override
	public final float gFPrD(final R1.L r, final long l) {
		long[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)-1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)-1);
		return b;
	}
	@Override
	public final float gFPoI(final R1.L r, final long l) {
		long[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))+1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))+1);
		return b;
	}
	@Override
	public final float gFPoD(final R1.L r, final long l) {
		long[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))-1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))-1);
		return b;
	}
	@Override
	public final double gDPrI(final R1.L r, final long l) {
		long[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)+1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)+1);
		return b;
	}
	@Override
	public final double gDPrD(final R1.L r, final long l) {
		long[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)-1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)-1);
		return b;
	}
	@Override
	public final double gDPoI(final R1.L r, final long l) {
		long[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))+1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))+1);
		return b;
	}
	@Override
	public final double gDPoD(final R1.L r, final long l) {
		long[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))-1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))-1);
		return b;
	}
	@Override
	public final byte gBPrI(final R1.F r, final long l) {
		float[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)+1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)+1));
		return b;
	}
	@Override
	public final byte gBPrD(final R1.F r, final long l) {
		float[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)-1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)-1));
		return b;
	}
	@Override
	public final byte gBPoI(final R1.F r, final long l) {
		float[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))+1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))+1));
		return b;
	}
	@Override
	public final byte gBPoD(final R1.F r, final long l) {
		float[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))-1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))-1));
		return b;
	}
	@Override
	public final char gCPrI(final R1.F r, final long l) {
		float[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)+1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)+1));
		return b;
	}
	@Override
	public final char gCPrD(final R1.F r, final long l) {
		float[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)-1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)-1));
		return b;
	}
	@Override
	public final char gCPoI(final R1.F r, final long l) {
		float[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))+1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))+1));
		return b;
	}
	@Override
	public final char gCPoD(final R1.F r, final long l) {
		float[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))-1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))-1));
		return b;
	}
	@Override
	public final short gSPrI(final R1.F r, final long l) {
		float[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)+1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)+1));
		return b;
	}
	@Override
	public final short gSPrD(final R1.F r, final long l) {
		float[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)-1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)-1));
		return b;
	}
	@Override
	public final short gSPoI(final R1.F r, final long l) {
		float[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))+1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))+1));
		return b;
	}
	@Override
	public final short gSPoD(final R1.F r, final long l) {
		float[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))-1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))-1));
		return b;
	}
	@Override
	public final int gIPrI(final R1.F r, final long l) {
		float[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)+1);
		else u.putInt(arr, l, b = u.getInt(arr, l)+1);
		return b;
	}
	@Override
	public final int gIPrD(final R1.F r, final long l) {
		float[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)-1);
		else u.putInt(arr, l, b = u.getInt(arr, l)-1);
		return b;
	}
	@Override
	public final int gIPoI(final R1.F r, final long l) {
		float[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))+1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))+1);
		return b;
	}
	@Override
	public final int gIPoD(final R1.F r, final long l) {
		float[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))-1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))-1);
		return b;
	}
	@Override
	public final long gJPrI(final R1.F r, final long l) {
		float[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)+1);
		else u.putLong(arr, l, b = u.getLong(arr, l)+1);
		return b;
	}
	@Override
	public final long gJPrD(final R1.F r, final long l) {
		float[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)-1);
		else u.putLong(arr, l, b = u.getLong(arr, l)-1);
		return b;
	}
	@Override
	public final long gJPoI(final R1.F r, final long l) {
		float[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))+1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))+1);
		return b;
	}
	@Override
	public final long gJPoD(final R1.F r, final long l) {
		float[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))-1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))-1);
		return b;
	}
	@Override
	public final float gFPrI(final R1.F r, final long l) {
		float[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)+1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)+1);
		return b;
	}
	@Override
	public final float gFPrD(final R1.F r, final long l) {
		float[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)-1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)-1);
		return b;
	}
	@Override
	public final float gFPoI(final R1.F r, final long l) {
		float[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))+1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))+1);
		return b;
	}
	@Override
	public final float gFPoD(final R1.F r, final long l) {
		float[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))-1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))-1);
		return b;
	}
	@Override
	public final double gDPrI(final R1.F r, final long l) {
		float[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)+1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)+1);
		return b;
	}
	@Override
	public final double gDPrD(final R1.F r, final long l) {
		float[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)-1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)-1);
		return b;
	}
	@Override
	public final double gDPoI(final R1.F r, final long l) {
		float[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))+1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))+1);
		return b;
	}
	@Override
	public final double gDPoD(final R1.F r, final long l) {
		float[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))-1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))-1);
		return b;
	}
	@Override
	public final byte gBPrI(final R1.D r, final long l) {
		double[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)+1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)+1));
		return b;
	}
	@Override
	public final byte gBPrD(final R1.D r, final long l) {
		double[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, b = (byte)(u.getByte(null, l)-1));
		else u.putByte(arr, l, b = (byte)(u.getByte(arr, l)-1));
		return b;
	}
	@Override
	public final byte gBPoI(final R1.D r, final long l) {
		double[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))+1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))+1));
		return b;
	}
	@Override
	public final byte gBPoD(final R1.D r, final long l) {
		double[] arr = r.array;
		byte b;
		if(arr == null) u.putByte(null, l, (byte)((b = u.getByte(null, l))-1));
		else u.putByte(arr, l, (byte)((b = u.getByte(arr, l))-1));
		return b;
	}
	@Override
	public final char gCPrI(final R1.D r, final long l) {
		double[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)+1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)+1));
		return b;
	}
	@Override
	public final char gCPrD(final R1.D r, final long l) {
		double[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, b = (char)(u.getChar(null, l)-1));
		else u.putChar(arr, l, b = (char)(u.getChar(arr, l)-1));
		return b;
	}
	@Override
	public final char gCPoI(final R1.D r, final long l) {
		double[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))+1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))+1));
		return b;
	}
	@Override
	public final char gCPoD(final R1.D r, final long l) {
		double[] arr = r.array;
		char b;
		if(arr == null) u.putChar(null, l, (char)((b = u.getChar(null, l))-1));
		else u.putChar(arr, l, (char)((b = u.getChar(arr, l))-1));
		return b;
	}
	@Override
	public final short gSPrI(final R1.D r, final long l) {
		double[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)+1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)+1));
		return b;
	}
	@Override
	public final short gSPrD(final R1.D r, final long l) {
		double[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, b = (short)(u.getShort(null, l)-1));
		else u.putShort(arr, l, b = (short)(u.getShort(arr, l)-1));
		return b;
	}
	@Override
	public final short gSPoI(final R1.D r, final long l) {
		double[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))+1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))+1));
		return b;
	}
	@Override
	public final short gSPoD(final R1.D r, final long l) {
		double[] arr = r.array;
		short b;
		if(arr == null) u.putShort(null, l, (short)((b = u.getShort(null, l))-1));
		else u.putShort(arr, l, (short)((b = u.getShort(arr, l))-1));
		return b;
	}
	@Override
	public final int gIPrI(final R1.D r, final long l) {
		double[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)+1);
		else u.putInt(arr, l, b = u.getInt(arr, l)+1);
		return b;
	}
	@Override
	public final int gIPrD(final R1.D r, final long l) {
		double[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, b = u.getInt(null, l)-1);
		else u.putInt(arr, l, b = u.getInt(arr, l)-1);
		return b;
	}
	@Override
	public final int gIPoI(final R1.D r, final long l) {
		double[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))+1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))+1);
		return b;
	}
	@Override
	public final int gIPoD(final R1.D r, final long l) {
		double[] arr = r.array;
		int b;
		if(arr == null) u.putInt(null, l, (b = u.getInt(null, l))-1);
		else u.putInt(arr, l, (b = u.getInt(arr, l))-1);
		return b;
	}
	@Override
	public final long gJPrI(final R1.D r, final long l) {
		double[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)+1);
		else u.putLong(arr, l, b = u.getLong(arr, l)+1);
		return b;
	}
	@Override
	public final long gJPrD(final R1.D r, final long l) {
		double[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, b = u.getLong(null, l)-1);
		else u.putLong(arr, l, b = u.getLong(arr, l)-1);
		return b;
	}
	@Override
	public final long gJPoI(final R1.D r, final long l) {
		double[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))+1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))+1);
		return b;
	}
	@Override
	public final long gJPoD(final R1.D r, final long l) {
		double[] arr = r.array;
		long b;
		if(arr == null) u.putLong(null, l, (b = u.getLong(null, l))-1);
		else u.putLong(arr, l, (b = u.getLong(arr, l))-1);
		return b;
	}
	@Override
	public final float gFPrI(final R1.D r, final long l) {
		double[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)+1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)+1);
		return b;
	}
	@Override
	public final float gFPrD(final R1.D r, final long l) {
		double[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, b = u.getFloat(null, l)-1);
		else u.putFloat(arr, l, b = u.getFloat(arr, l)-1);
		return b;
	}
	@Override
	public final float gFPoI(final R1.D r, final long l) {
		double[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))+1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))+1);
		return b;
	}
	@Override
	public final float gFPoD(final R1.D r, final long l) {
		double[] arr = r.array;
		float b;
		if(arr == null) u.putFloat(null, l, (b = u.getFloat(null, l))-1);
		else u.putFloat(arr, l, (b = u.getFloat(arr, l))-1);
		return b;
	}
	@Override
	public final double gDPrI(final R1.D r, final long l) {
		double[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)+1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)+1);
		return b;
	}
	@Override
	public final double gDPrD(final R1.D r, final long l) {
		double[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, b = u.getDouble(null, l)-1);
		else u.putDouble(arr, l, b = u.getDouble(arr, l)-1);
		return b;
	}
	@Override
	public final double gDPoI(final R1.D r, final long l) {
		double[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))+1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))+1);
		return b;
	}
	@Override
	public final double gDPoD(final R1.D r, final long l) {
		double[] arr = r.array;
		double b;
		if(arr == null) u.putDouble(null, l, (b = u.getDouble(null, l))-1);
		else u.putDouble(arr, l, (b = u.getDouble(arr, l))-1);
		return b;
	}
	@Override
	public final byte gBPrI(final R1.N r, final long l) {
		final byte b = (byte)(u.getByte(null, l)+1);
		u.putByte(null, l, b);
		return b;
	}
	@Override
	public final byte gBPrD(final R1.N r, final long l) {
		final byte b = (byte)(u.getByte(null, l)-1);
		u.putByte(null, l, b);
		return b;
	}
	@Override
	public final byte gBPoI(final R1.N r, final long l) {
		final byte b = u.getByte(null, l);
		u.putByte(null, l, (byte)(b+1));
		return b;
	}
	@Override
	public final byte gBPoD(final R1.N r, final long l) {
		final byte b = u.getByte(null, l);
		u.putByte(null, l, (byte)(b-1));
		return b;
	}
	@Override
	public final char gCPrI(final R1.N r, final long l) {
		final char b = (char)(u.getChar(null, l)+1);
		u.putChar(null, l, b);
		return b;
	}
	@Override
	public final char gCPrD(final R1.N r, final long l) {
		final char b = (char)(u.getChar(null, l)-1);
		u.putChar(null, l, b);
		return b;
	}
	@Override
	public final char gCPoI(final R1.N r, final long l) {
		final char b = u.getChar(null, l);
		u.putChar(null, l, (char)(b+1));
		return b;
	}
	@Override
	public final char gCPoD(final R1.N r, final long l) {
		final char b = u.getChar(null, l);
		u.putChar(null, l, (char)(b-1));
		return b;
	}
	@Override
	public final short gSPrI(final R1.N r, final long l) {
		final short b = (short)(u.getShort(null, l)+1);
		u.putShort(null, l, b);
		return b;
	}
	@Override
	public final short gSPrD(final R1.N r, final long l) {
		final short b = (short)(u.getShort(null, l)-1);
		u.putShort(null, l, b);
		return b;
	}
	@Override
	public final short gSPoI(final R1.N r, final long l) {
		final short b = u.getShort(null, l);
		u.putShort(null, l, (short)(b+1));
		return b;
	}
	@Override
	public final short gSPoD(final R1.N r, final long l) {
		final short b = u.getShort(null, l);
		u.putShort(null, l, (short)(b-1));
		return b;
	}
	@Override
	public final int gIPrI(final R1.N r, final long l) {
		final int b = u.getInt(null, l)+1;
		u.putInt(null, l, b);
		return b;
	}
	@Override
	public final int gIPrD(final R1.N r, final long l) {
		final int b = u.getInt(null, l)-1;
		u.putInt(null, l, b);
		return b;
	}
	@Override
	public final int gIPoI(final R1.N r, final long l) {
		final int b = u.getInt(null, l);
		u.putInt(null, l, b+1);
		return b;
	}
	@Override
	public final int gIPoD(final R1.N r, final long l) {
		final int b = u.getInt(null, l);
		u.putInt(null, l, b-1);
		return b;
	}
	@Override
	public final long gJPrI(final R1.N r, final long l) {
		final long b = u.getLong(null, l)+1;
		u.putLong(null, l, b);
		return b;
	}
	@Override
	public final long gJPrD(final R1.N r, final long l) {
		final long b = u.getLong(null, l)-1;
		u.putLong(null, l, b);
		return b;
	}
	@Override
	public final long gJPoI(final R1.N r, final long l) {
		final long b = u.getLong(null, l);
		u.putLong(null, l, b+1);
		return b;
	}
	@Override
	public final long gJPoD(final R1.N r, final long l) {
		final long b = u.getLong(null, l);
		u.putLong(null, l, b-1);
		return b;
	}
	@Override
	public final float gFPrI(final R1.N r, final long l) {
		final float b = u.getFloat(null, l)+1;
		u.putFloat(null, l, b);
		return b;
	}
	@Override
	public final float gFPrD(final R1.N r, final long l) {
		final float b = u.getFloat(null, l)-1;
		u.putFloat(null, l, b);
		return b;
	}
	@Override
	public final float gFPoI(final R1.N r, final long l) {
		final float b = u.getFloat(null, l);
		u.putFloat(null, l, b+1);
		return b;
	}
	@Override
	public final float gFPoD(final R1.N r, final long l) {
		final float b = u.getFloat(null, l);
		u.putFloat(null, l, b-1);
		return b;
	}
	@Override
	public final double gDPrI(final R1.N r, final long l) {
		final double b = u.getDouble(null, l)+1;
		u.putDouble(null, l, b);
		return b;
	}
	@Override
	public final double gDPrD(final R1.N r, final long l) {
		final double b = u.getDouble(null, l)-1;
		u.putDouble(null, l, b);
		return b;
	}
	@Override
	public final double gDPoI(final R1.N r, final long l) {
		final double b = u.getDouble(null, l);
		u.putDouble(null, l, b+1);
		return b;
	}
	@Override
	public final double gDPoD(final R1.N r, final long l) {
		final double b = u.getDouble(null, l);
		u.putDouble(null, l, b-1);
		return b;
	}

	
}
