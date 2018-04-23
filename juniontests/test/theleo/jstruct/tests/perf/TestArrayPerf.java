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
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import theleo.jstruct.Mem;
import theleo.jstruct.Struct;
import theleo.jstruct.hidden.Mem0;
import theleo.jstruct.hidden.Ref1;

/**
 *
 * @author Juraj Papp
 */
public class TestArrayPerf {
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

		
//		runTest(Test.DIRECT_BUFFER, n);
		runTest(Test.STRUCT, n);
//		runTest(Test.OBJECT_ARRAY, n);
//		runTest(Test.PRIMITIVE_ARRAY, n);
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
		
		ByteBuffer a = ByteBuffer.allocateDirect(num*vec3Size).order(ByteOrder.nativeOrder());
		ByteBuffer b = ByteBuffer.allocateDirect(num*vec3Size).order(ByteOrder.nativeOrder());
		ByteBuffer mult = ByteBuffer.allocateDirect(num*vec3Size).order(ByteOrder.nativeOrder());
		
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
	
	public static void mult(Vec3[] av, Vec3[] bv, Vec3[] multv) {
		for(int i = 0; i < av.length; i++) {
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
		Vec3[] av = Mem.wrap(a, Mem.sizeOf(Vec3.class));
		Vec3[] bv = Mem.wrap(b, Mem.sizeOf(Vec3.class));
		Vec3[] multv = Mem.wrap(mult, Mem.sizeOf(Vec3.class));
		
		for(int i = 0; i < 5; i++) {
			long time = System.currentTimeMillis();
			mult(av,bv,multv);
			time = System.currentTimeMillis()-time;
			System.err.println("WARM " + time);
		}
		
		long totalTime = 0;
		for(int i = 0; i < loops; i++) {
			long time = System.currentTimeMillis();
			mult(av,bv,multv);
			time = System.currentTimeMillis()-time;
			System.err.println("Time " + time);
			totalTime += time;
		}
		System.err.println("Total time " + totalTime);
		
		System.err.println(multv[1].z);
	}
	
	public static void testBuffers(FloatBuffer a, FloatBuffer b, FloatBuffer mult, int loops) {
		for(int i = 0; i < 5; i++) {
			long time = System.currentTimeMillis();
			mult(a,b,mult);
			time = System.currentTimeMillis()-time;
			System.err.println("WARM " + time);
		}
		
		long totalTime = 0;
		for(int i = 0; i < loops; i++) {
			long time = System.currentTimeMillis();
			mult(a,b,mult);
			time = System.currentTimeMillis()-time;
			System.err.println("Time " + time);
			totalTime += time;
		}
		System.err.println("Total time " + totalTime);
		
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
			System.err.println("WARM " + time);
		}
		
		long totalTime = 0;
		for(int i = 0; i < loops; i++) {
			long time = System.currentTimeMillis();
			mult(av,bv,multv);
			time = System.currentTimeMillis()-time;
			System.err.println("Time " + time);
			totalTime += time;
		}
		System.err.println("Total time " + totalTime);
		
		System.err.println(av.length);
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
			System.err.println("WARM " + time);
		}
		
		long totalTime = 0;
		for(int i = 0; i < loops; i++) {
			long time = System.currentTimeMillis();
			mult(av,bv,multv);
			time = System.currentTimeMillis()-time;
			System.err.println("Time " + time);
			totalTime += time;
		}
		System.err.println("Total time " + totalTime);
		
		System.err.println(multv[5]);
	}
}