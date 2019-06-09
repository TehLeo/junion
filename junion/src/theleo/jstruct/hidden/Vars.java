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

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.util.Objects;
import sun.misc.Unsafe;

/**
 *
 * @author Juraj Papp
 */
public class Vars {
	/**
	 * Maximum length of heap array that will be allocated.
	 * The value is set to Integer.MAX_VALUE-8.
	 * 
	 * Some JVms may not allocate arrays with Integer.MAX_VALUE,
	 * now the question is are there JVMs which fail to allocate 
	 * array of length Integer.MAX_VALUE-8?
	 * 
	 * Eg. ArrayList implementation uses MAX_ARRAY_SIZE set to Integer.MAX_VALUE-8 as well
	 */
	public static final int MAX_HEAP_ARRAY = Integer.MAX_VALUE-8;

	public static final Unsafe u;
	public static final long ARRAY_BYTE_BASE_OFFSET;
	public static final long ARRAY_SHORT_BASE_OFFSET;
	public static final long ARRAY_CHAR_BASE_OFFSET;
	public static final long ARRAY_INT_BASE_OFFSET;
	public static final long ARRAY_FLOAT_BASE_OFFSET;
	public static final long ARRAY_LONG_BASE_OFFSET;
	public static final long ARRAY_DOUBLE_BASE_OFFSET;
	
	private static final long BUFFER_ADDRESS;
    public static final long BUFFER_LIMIT;
	
	public static final long BLOCKER_LOCK_OFFSET;

	
//    public static final long R1_ARRAY;
		
	static {
		u = getUnsafe();
		ARRAY_BYTE_BASE_OFFSET = u.arrayBaseOffset(byte[].class);
		ARRAY_SHORT_BASE_OFFSET = u.arrayBaseOffset(short[].class);
		ARRAY_CHAR_BASE_OFFSET = u.arrayBaseOffset(char[].class);
		ARRAY_INT_BASE_OFFSET = u.arrayBaseOffset(int[].class);
		ARRAY_FLOAT_BASE_OFFSET = u.arrayBaseOffset(float[].class);
		ARRAY_LONG_BASE_OFFSET = u.arrayBaseOffset(long[].class);
		ARRAY_DOUBLE_BASE_OFFSET = u.arrayBaseOffset(double[].class);
		
		try {
			BUFFER_ADDRESS = objectFieldOffset(Buffer.class, "address");
			BUFFER_LIMIT = objectFieldOffset(Buffer.class, "limit");
			BLOCKER_LOCK_OFFSET = objectFieldOffset(Thread.class, "blockerLock");
			
//			R1_ARRAY = objectFieldOffset(R1A.class, "array");
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to initialize!" );
		}
	}
	public static long objectFieldOffset(Class c, String name) throws Exception {
		Field f = c.getDeclaredField(name);
		return u.objectFieldOffset(f);
	}
	public static long staticFieldOffset(Class c, String name) throws Exception {
		Field f = c.getDeclaredField(name);
		return u.staticFieldOffset(f);
	}
	
	public static long getBufferAddress(Buffer b) {
		return u.getLong(Objects.requireNonNull(b), BUFFER_ADDRESS);
	}
	
	private static Unsafe getUnsafe() {
		try {
			Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
			singleoneInstanceField.setAccessible(true);
			return (Unsafe) singleoneInstanceField.get(null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
