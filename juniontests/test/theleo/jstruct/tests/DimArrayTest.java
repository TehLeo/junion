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
package theleo.jstruct.tests;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import theleo.jstruct.Mem;
import theleo.jstruct.Struct;
import theleo.jstruct.hidden.Hyb1;
import theleo.jstruct.hidden.HybN;
import theleo.jstruct.hidden.Mem0;
import theleo.jstruct.hidden.Ref1;
import theleo.jstruct.hidden.RefN;

/**
 *
 * @author Juraj Papp
 */
public class DimArrayTest {
	@Struct
	public static class Vec2 {
		Object o;
		public int a, b;
	}
	
	public static int add(int... a) {
		return a[0]+a[1]+a[2];
	}
	
	public static int add3(int a, int b, int c) {
		return a + b + c;
	}
	
	public static void a(int i) { System.out.println("a ");}
	public static void a(int... i) { System.out.println("a2 ");}
	
	static Vec2[][] arr2 = new Vec2[5][2];
	static Vec2 ptr;
	public static void main(String[] args) {
		ptr = arr2[2][0];
		System.out.println(ptr.a);
		
		System.out.println(arr2);
		
		HybN h = Mem0.as(arr2);
		System.out.println(h.getIndex(0, 0));
		System.out.println(h.getIndex(0, 1));
		System.out.println(h.getIndex(1, 0));
		System.out.println(h.getIndex(1, 1));
		
		System.out.println("len " + h.owner.length);
		
		
		
		for(int aa = 0; aa < Mem.len(arr2, 0); aa++) {
			for(int bb = 0; bb < Mem.len(arr2, 1); bb++) {
				Vec2 v = arr2[aa][bb];
				v.a = aa;
				v.b = bb;
				System.out.println("write " + aa + ", " + bb + ", " + v);
			}
		}
		
		System.out.println("Repeat");
		
		for(int aa = 0; aa < Mem.len(arr2, 0); aa++) {
			for(int bb = 0; bb < Mem.len(arr2, 1); bb++) {
				Vec2 v = arr2[aa][bb];
				System.out.println("read " + v.a + ", " + v.b + ", " + v);
			}
		}
		
		try {
			Field[] fs = Class.forName("theleo.jstruct.tests.DimArrayTest$Vec2").getDeclaredFields();
			for(Field f : fs)
				System.out.println(f);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println(Arrays.toString(Mem0.getHybOffsets(Vec2.class)));
		
		System.out.println(Mem.layoutString(Vec2.class));
		int len1 = 10000, len2 = 10000;
		
		Vec2[] arr = new Vec2[Mem.li(len1*len2)];
		
		Hyb1 ref1 = Mem0.as(arr);
		
//		Ref2 ref2 = new Ref2(ref1, len1, len2);
		HybN ref2 = new HybN(ref1, len1, len2);
		
		System.out.println("" + ref2.getIndex(0, 1));
		System.out.println("" + ref2.getIndex(0, 2));
		System.out.println("" + ref2.getIndex(1, 0));
		
		//166
//		for(int r = 0; r < 10; r++ ) {
//			long time = System.currentTimeMillis();
//			for(int i = 0; i < arr.length; i++) {
//				Vec2 v = arr[i];
//				v.a = i*i;
//				v.b = v.a*i;
//			}
//
//			time = System.currentTimeMillis()-time;
//			System.out.println("Time " + time);
//		}
		
		int a[] = new int[3];
        //230
		for(int r = 0; r < 10; r++ ) {
			long time = System.currentTimeMillis();
			int i = 0;
			for(int x = 0; x < len1; x++) {
				for(int y = 0; y < len2; y++) {
//					Vec2 v = arr[i];
					Vec2 v = Mem0.as(ref2.getIndex(x, y));
//					Vec2 v = Mem0.as(ref2.getIndex(i));
					i++;
//					v.a = y*y;
//					v.b = v.a*y;
//					a[0] = y; a[1] = y; a[2] = x;
//					v.a = add(a);
//					a[0] = x; a[1] = x; a[2] = y;
//					v.b = add(a);
					
//					v.a = add(y,y,x);
//					v.b = add(x,x,y);
					
					v.a = add3(y,y,x);
					v.b = add3(x,x,y);
				}
			}

			time = System.currentTimeMillis()-time;
			System.out.println("Time " + time);
		}
		
		
//		for(int x = 0; x < 8; x++)
//			for(int y = 0; y < 8; y++) {
//				Vec2 v = Mem0.as(ref2.getIndex(x, y));
//				v.a = x;
//				v.b = y;
//			}
//		
//		for(int i = 0; i < 10; i++) {
//			System.out.println("arr[" + i + "] =  " + arr[i].a + ", " + arr[i].b);
//		}
//		
//		
//		Vec2[] slice = Mem.slice(arr, 8, 16);
//		
//		for(int i = 0; i < slice.length; i++) {
//			System.out.println("slice[" + i + "] =  " + slice[i].a + ", " + slice[i].b);
//		}
		
		
		System.out.println("Hello World");
	}
}
