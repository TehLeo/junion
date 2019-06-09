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
package theleo.jstruct.tests.perf;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import theleo.jstruct.Mem;
import theleo.jstruct.Struct;
import theleo.jstruct.*;
import theleo.jstruct.hidden.Mem0;
import static theleo.jstruct.hidden.Mem0.B;
import theleo.jstruct.hidden.Stack;
import theleo.jstruct.reflect.StructType;

/**
 *
 * @author Juraj Papp
 */
public class TestArrayPerf {
	
	static {
//		MemInit.setBrige(new DebugBridge(new DefaultBridge()));
		
//		DebugFixedAllocator alloc = new DebugFixedAllocator(1024*1024*256, 1024*1024);
//		DebugBridge bridge = new DebugBridge(new DefaultBridge(), alloc);
//		
////		bridge.debugAllocator = alloc;
//		MemInit.setAllocator(alloc);		
//		MemInit.setBrige(bridge);
	}
	
	
	@Struct
	public static class Vec3 {
		float x, y, z;		
	}
	public static class Vec3Obj {
		float x, y, z;	
	}
	static enum Test {
		OBJECT_ARRAY, DIRECT_BUFFER, PRIMITIVE_ARRAY, STRUCT
	}
	
	public static void main(String[] args) {
		
		//Uncomment to select which test to run, or run both
		int n = 5000000;
//		int n = 50000;
		
//		runTest(Test.DIRECT_BUFFER, n);     //1010
		runTest(Test.STRUCT, n);            //455
//		runTest(Test.OBJECT_ARRAY, n);      //1158
//		runTest(Test.PRIMITIVE_ARRAY, n);   //441

		for(Object o : log)
			System.out.print(o);
	}
	static ArrayList<Object> log = new ArrayList<>(2048);
	public static void log(Object a) {
		log.add(a);
		log.add('\n');
	}
	public static void log(Object a, Object b) {
		log.add(a);
		log.add(b);
		log.add('\n');
	}
		
	public static void runTest(Test test, int n) {
		int loops = 20;
		int num = n;
		
		if(test == Test.OBJECT_ARRAY) {
			testObject(num, loops);
			return;
		}
		if(test == Test.PRIMITIVE_ARRAY) {
			testPrimArray(num, loops);
			return;
		}
		
		int vec3Size = Mem.sizeOf(Vec3.class);
		
		Vec3[] av = new @DirectBuffer Vec3[num];
		Vec3[] bv = new @DirectBuffer Vec3[num];
		Vec3[] multv = new @DirectBuffer Vec3[num];
		
		ByteBuffer a = Mem.getJavaByteBuffer(av);
		ByteBuffer b = Mem.getJavaByteBuffer(bv);
		ByteBuffer mult = Mem.getJavaByteBuffer(multv);
		
		
		
//		ByteBuffer a = ByteBuffer.allocateDirect(num*vec3Size).order(ByteOrder.nativeOrder());
//		ByteBuffer b = ByteBuffer.allocateDirect(num*vec3Size).order(ByteOrder.nativeOrder());
//		ByteBuffer mult = ByteBuffer.allocateDirect(num*vec3Size).order(ByteOrder.nativeOrder());
		
		
		
		
		a.limit(a.capacity()); b.limit(a.capacity()); mult.limit(a.capacity());
		
		FloatBuffer af = a.asFloatBuffer();
		FloatBuffer bf = b.asFloatBuffer();
		FloatBuffer multf = mult.asFloatBuffer();
		
		af.clear(); bf.clear(); multf.clear();
		
		for(int i = 0; i < af.capacity(); i++) {
			af.put(i, i);
			bf.put(i, i*10f);
		}
		
		if(test == Test.DIRECT_BUFFER) testBuffers(af, bf, multf, loops);
		else if(test == Test.STRUCT) testStruct(af, bf, multf, loops);
	}
	
//	public static void mult(byte[] a$array, long a, byte[] b$array, long b, byte[] m$array, long m) {
//		B.pF(m$array, m  , B.gF(a$array, a  )*B.gF(b$array, b  ) );
//		B.pF(m$array, m+4, B.gF(a$array, a+4)*B.gF(b$array, b+4) );
//		B.pF(m$array, m+8, B.gF(a$array, a+8)*B.gF(b$array, b+8) );
//	}
//	public static final class Ptr {
//		public long index;
//		public final byte[] arr;
//		public Ptr(long index, byte[] arr) {
//			this.index = index;
//			this.arr = arr;
//		}
//	}
	
	
	
//	public static long min5(Ref1 ar1, long a, Ref1 ar2, long b) {
//		Stack s = Mem0.stack();
//		if(theleo.jstruct.hidden.Mem0.B.gF(ar1, a) < theleo.jstruct.hidden.Mem0.B.gF(ar2, b))
////			Vars.u.putLong(s, Stack.RET_ADDRESS, a);
//			return Stack.ret2(s,ar1,a);
//		else 
////			Vars.u.putLong(s, Stack.RET_ADDRESS, b);
//			return Stack.ret2(s,ar2,b);
//	}
	public static void mult(Vec3[] av, Vec3[] bv, Vec3[] multv) {
//		Stack s = Mem0.stack();
//		Ptr t1 = new Ptr(0, null);
//		Ptr t2 = new Ptr(0, null);
//		Ptr t3 = new Ptr(0, null);
		
//		for(int i = 0; i < av.length; i++) {
//			multv[i].x = av[i].x*bv[i].x;
//			multv[i].y = av[i].y*bv[i].y;
//			multv[i].z = av[i].z*bv[i].z;
//		}
//		Ref1 a = Mem0.as(av);
//		Ref1 b = Mem0.as(bv);
//		Ref1 m = Mem0.as(multv);
//		

//		final long len = a.length;
//		final long mb = m.base;
//		final long ab = a.base;
//		final long bb = b.base;
//		long i12 = 0;
//		Ref1 min$ref;
//		Vec3 min;
//		long min2;
		for(int i = 0; i < av.length; i++) {
					
//			Mem0.u.putFloat(mb+i12, Mem0.u.getFloat(ab+i12)*Mem0.u.getFloat(bb+i12) );
//			Mem0.u.putFloat(mb+i12+4, Mem0.u.getFloat(ab+i12+4)*Mem0.u.getFloat(bb+i12+4) );
//			Mem0.u.putFloat(mb+i12+8, Mem0.u.getFloat(ab+i12+8)*Mem0.u.getFloat(bb+i12+8) );
//			
//			i12 += 12;
			
//			Mem0.putFloat(mb+i*12, Mem0.u.getFloat(ab+i*12)*Mem0.u.getFloat(bb+i*12) );
//			Mem0.putFloat(mb+i*12+4, Mem0.u.getFloat(ab+i*12+4)*Mem0.u.getFloat(bb+i*12+4) );
//			Mem0.putFloat(mb+i*12+8, Mem0.u.getFloat(ab+i*12+8)*Mem0.u.getFloat(bb+i*12+8) );
			
//			Mem0.putFloat(m.base+i*12, Mem0.u.getFloat(a.base+i*12)*Mem0.u.getFloat(b.base+i*12) );
//			Mem0.putFloat(m.base+i*12+4, Mem0.u.getFloat(a.base+i*12+4)*Mem0.u.getFloat(b.base+i*12+4) );
//			Mem0.putFloat(m.base+i*12+8, Mem0.u.getFloat(a.base+i*12+8)*Mem0.u.getFloat(b.base+i*12+8) );
			
//			Mem0.putFloat(m.base+i*m.structSize, Mem0.u.getFloat(a.base+i*a.structSize)*Mem0.u.getFloat(b.base+i*b.structSize) );
//			Mem0.putFloat(m.base+i*m.structSize+4, Mem0.u.getFloat(a.base+i*a.structSize+4)*Mem0.u.getFloat(b.base+i*b.structSize+4) );
//			Mem0.putFloat(m.base+i*m.structSize+8, Mem0.u.getFloat(a.base+i*a.structSize+8)*Mem0.u.getFloat(b.base+i*b.structSize+8) );
			
//int \u0042;
//			\u0042.pF(\u0042.x(m,i)  , \u0042.gF(\u0042.x(a,i)  )*\u0042.gF(\u0042.x(b,i)  ) );
//			£.pF(£.x(m,i)  , £.gF(£.x(a,i)  )*£.gF(£.x(b,i)  ) );
			
//			B.pFMul(B.x(m,i)  , B.pFMul(B.x(a,i), B.gF(B.x(b,i) )) );
//			B.pFMul(B.x(m,i)+4, B.pFMul(B.x(a,i)+4, B.gF(B.x(b,i)+4)) );
//			B.pFMul(B.x(m,i)+8, B.pFMul(B.x(a,i)+8, B.gF(B.x(b,i)+8)) );
			
//			final long tmpIndex = B.x(m,i), tmpIndexA = B.x(a,i);
//			B.pF(tmpIndex  , B.gF(tmpIndex)*       B.pF(tmpIndexA,    B.gF(tmpIndexA)*       B.gF(B.x(b,i) )) );
//			B.pF(tmpIndex+4, B.gF(tmpIndex+4)*     B.pF(tmpIndexA+4,  B.gF(tmpIndexA+4)*     B.gF(B.x(b,i)+4)) );
//			B.pF(tmpIndex+8, B.gF(tmpIndex+8)*     B.pF(tmpIndexA+8,  B.gF(tmpIndexA+8)*     B.gF(B.x(b,i)+8)) );
			
//			B.pF(B.x(m,i)  , B.gF(B.x(m,i))*       B.pF(B.x(a,i),    B.gF(B.x(a,i))*       B.gF(B.x(b,i) )) );
//			B.pF(B.x(m,i)+4, B.gF(B.x(m,i)+4)*     B.pF(B.x(a,i)+4,  B.gF(B.x(a,i)+4)*     B.gF(B.x(b,i)+4)) );
//			B.pF(B.x(m,i)+8, B.gF(B.x(m,i)+8)*     B.pF(B.x(a,i)+8,  B.gF(B.x(a,i)+8)*     B.gF(B.x(b,i)+8)) );
			
//			B.pF(B.x(m,i)  , B.gF(B.x(a,i)  )*B.gF(B.x(b,i)  ) );
//			B.pF(B.x(m,i)+4, B.gF(B.x(a,i)+4)*B.gF(B.x(b,i)+4) );
//			B.pF(B.x(m,i)+8, B.gF(B.x(a,i)+8)*B.gF(B.x(b,i)+8) );
			
//			B.pF(m, B.x(m,i)  , B.gF(a, B.x(a,i)  )*B.gF(b, B.x(b,i)  ) );
//			B.pF(m, B.x(m,i)+4, B.gF(a, B.x(a,i)+4)*B.gF(b, B.x(b,i)+4) );
//			B.pF(m, B.x(m,i)+8, B.gF(a, B.x(a,i)+8)*B.gF(b, B.x(b,i)+8) );

//			long mm = B.x(m,i);
//			long aa = B.x(a,i);
//			long bb = B.x(b,i);
//
//			B.pF(m.array, mm  , B.gF(a.array, aa  )*B.gF(b.array, bb  ) );
//			B.pF(m.array, mm+4, B.gF(a.array, aa+4)*B.gF(b.array, bb+4) );
//			B.pF(m.array, mm+8, B.gF(a.array, aa+8)*B.gF(b.array, bb+8) );

//			min$ref = copy(min4(Mem0.as(av, Ref1.class), Mem0.as(av, Ref1.class).getIndex(i), Mem0.as(bv, Ref1.class), Mem0.as(bv, Ref1.class).getIndex(i)),
//					Mem0.asLong(min = Mem0.asLong(s.returnAddress))
//					);
			

//			min$ref = min4(Mem0.as(av, Ref1.class), Mem0.as(av, Ref1.class).getIndex(i), Mem0.as(bv, Ref1.class), Mem0.as(bv, Ref1.class).getIndex(i));
//			min = Mem0.asLong(s.returnAddress);
			
//			min = Mem0.asLong(Vars.u.getLong(s, Stack.RET_ADDRESS));
			
//			theleo.jstruct.hidden.Mem0.B.pF(min$ref, Mem0.as(multv, Ref1.class).getIndex(i), ((float) theleo.jstruct.hidden.Mem0.B.gF(min$ref, Mem0.asLong(min))));

//			//460
//			min3(Mem0.as(av, Ref1.class).getIndex(i), Mem0.as(bv, Ref1.class).getIndex(i));
//			min = Mem0.asLong(s.returnAddress);
////			min = Mem0.asLong(Vars.u.getLong(s, Stack.RET_ADDRESS));
//			
//			theleo.jstruct.hidden.Mem0.B.pF(Mem0.as(multv, Ref1.class).getIndex(i), ((float) theleo.jstruct.hidden.Mem0.B.gF(Mem0.asLong(min))));

			//453
//			min = Mem0.asLong(min2(Mem0.as(av, Ref1.class).getIndex(i), Mem0.as(bv, Ref1.class).getIndex(i)));
//			theleo.jstruct.hidden.Mem0.B.pF(Mem0.as(multv, Ref1.class).getIndex(i), ((float) theleo.jstruct.hidden.Mem0.B.gF(Mem0.asLong(min))));
			
			//454
//			min = min(av[i], bv[i]);
//			multv[i].x = min.x;
			
			
			
			
			
//			mult(t1.set(B.x(a,i), a.array), t2.set(B.x(b,i), b.array), t3.set(B.x(m,i), m.array) );
			
			
//			mult(a.array, b.array, m.array, B.x(a,i), B.x(b,i), B.x(m,i));
//			mult(a.array, a.getIndex(i), b.array, b.getIndex(i), m.array, m.getIndex(i));
			
			
			
//			B.pF(m.array, B.x(m,i)  , B.gF(a.array, B.x(a,i)  )*B.gF(b.array, B.x(b,i)  ) );
//			B.pF(m.array, B.x(m,i)+4, B.gF(a.array, B.x(a,i)+4)*B.gF(b.array, B.x(b,i)+4) );
//			B.pF(m.array, B.x(m,i)+8, B.gF(a.array, B.x(a,i)+8)*B.gF(b.array, B.x(b,i)+8) );

//			Mem0.B.pF(m.gI(i), Mem0.B.gF(a.gI(i))*Mem0.B.gF(b.gI(i)) );
//			Mem0.B.pF(m.gI(i)+4, Mem0.B.gF(a.gI(i)+4)*Mem0.B.gF(b.gI(i)+4) );
//			Mem0.B.pF(m.gI(i)+8, Mem0.B.gF(a.gI(i)+8)*Mem0.B.gF(b.gI(i)+8) );
			
//			Mem0.b.putFloat(m.getIndex(i), Mem0.b.getFloat(a.getIndex(i))*Mem0.b.getFloat(b.getIndex(i)) );
//			Mem0.b.putFloat(m.getIndex(i)+4, Mem0.b.getFloat(a.getIndex(i)+4)*Mem0.b.getFloat(b.getIndex(i)+4) );
//			Mem0.b.putFloat(m.getIndex(i)+8, Mem0.b.getFloat(a.getIndex(i)+8)*Mem0.b.getFloat(b.getIndex(i)+8) );
			
//			Mem0.putFloat(m.getIndex(i), Mem0.getFloat(a.getIndex(i))*Mem0.getFloat(b.getIndex(i)) );
//			Mem0.putFloat(m.getIndex(i)+4, Mem0.getFloat(a.getIndex(i)+4)*Mem0.getFloat(b.getIndex(i)+4) );
//			Mem0.putFloat(m.getIndex(i)+8, Mem0.getFloat(a.getIndex(i)+8)*Mem0.getFloat(b.getIndex(i)+8) );

//			multv[i].x *= av[i].x *= bv[i].x;
//			multv[i].y *= av[i].y *= bv[i].y;
//			multv[i].z *= av[i].z *= bv[i].z;


			multv[i].x = av[i].x*bv[i].x;
			multv[i].y = av[i].y*bv[i].y;
			multv[i].z = av[i].z*bv[i].z;
		}
		
	}
	public static void mult(Vec3Obj[] av, Vec3Obj[] bv, Vec3Obj[] multv) {
		for(int i = 0; i < av.length; i++) {
			multv[i].x = av[i].x*bv[i].x;
			multv[i].y = av[i].y*bv[i].y;
			multv[i].z = av[i].z*bv[i].z;
		}
	}
	public static void mult(float[] av, float[] bv, float[] multv) {
		for(int i = 0; i < av.length; i++) {
			multv[i] = av[i]*bv[i];
		}
	}

	public static void testStruct(FloatBuffer a, FloatBuffer b, FloatBuffer mult, int loops) {
		Vec3[] av = Mem.wrap(a, StructType.forClass(Vec3.class));
		Vec3[] bv = Mem.wrap(b, StructType.forClass(Vec3.class));
		Vec3[] multv = Mem.wrap(mult, StructType.forClass(Vec3.class));
		
		for(int i = 0; i < 5; i++) {
			long time = System.currentTimeMillis();
			mult(av,bv,multv);
			time = System.currentTimeMillis()-time;
			log("WARM ", time);
		}
		
		long totalTime = 0;
		for(int i = 0; i < loops; i++) {
			long time = System.currentTimeMillis();
			mult(av,bv,multv);
			time = System.currentTimeMillis()-time;
			log("Time ", time);
			totalTime += time;
		}
		log("Total time ", totalTime);
		
		log(multv[1].z);
	}
	
	public static void testBuffers(FloatBuffer a, FloatBuffer b, FloatBuffer mult, int loops) {
		for(int i = 0; i < 5; i++) {
			long time = System.currentTimeMillis();
			mult(a,b,mult);
			time = System.currentTimeMillis()-time;
			log("WARM ", time);
		}
		
		long totalTime = 0;
		for(int i = 0; i < loops; i++) {
			long time = System.currentTimeMillis();
			mult(a,b,mult);
			time = System.currentTimeMillis()-time;
			log("Time ", time);
			totalTime += time;
		}
		log("Total time ", totalTime);
		
	}
	public static void mult(FloatBuffer a, FloatBuffer b, FloatBuffer store) {
		a.clear(); b.clear(); store.clear();
		while(a.hasRemaining()) {
			store.put(a.get()*b.get());
		}
	}
	public static void testObject(int n, int loops) {
		Vec3Obj[] av = new Vec3Obj[n];
		Vec3Obj[] bv = new Vec3Obj[n];
		Vec3Obj[] multv = new Vec3Obj[n];
		
		for(int i = 0; i < av.length; i++) {
			av[i] = new Vec3Obj();
			bv[i] = new Vec3Obj();
			multv[i] = new Vec3Obj();
			
			av[i].x = i*3;av[i].y = i*3+1;av[i].z = i*3+2;
			bv[i].x = i*30;bv[i].y = i*30+10;bv[i].z = i*30+20;
		}
		
		
		for(int i = 0; i < 5; i++) {
			long time = System.currentTimeMillis();
			mult(av,bv,multv);
			time = System.currentTimeMillis()-time;
			log("WARM ", time);
		}
		
		long totalTime = 0;
		for(int i = 0; i < loops; i++) {
			long time = System.currentTimeMillis();
			mult(av,bv,multv);
			time = System.currentTimeMillis()-time;
			log("Time ", time);
			totalTime += time;
		}
		log("Total time ", totalTime);
		
		log(av.length);
	}
	public static void testPrimArray(int n, int loops) {
		float[] av = new float[n*3];
		float[] bv = new float[n*3];
		float[] multv = new float[n*3];
		
		for(int i = 0; i < av.length; i++) {
			av[i] = i;
			bv[i] = i*10f;
		}
		
		
		for(int i = 0; i < 5; i++) {
			long time = System.currentTimeMillis();
			mult(av,bv,multv);
			time = System.currentTimeMillis()-time;
			log("WARM ", time);
		}
		
		long totalTime = 0;
		for(int i = 0; i < loops; i++) {
			long time = System.currentTimeMillis();
			mult(av,bv,multv);
			time = System.currentTimeMillis()-time;
			log("Time ", time);
			totalTime += time;
		}
		log("Total time ", totalTime);
		
		log(multv[5]);
	}
}