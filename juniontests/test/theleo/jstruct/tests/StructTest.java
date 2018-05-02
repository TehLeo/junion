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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;
import theleo.jstruct.Mem;
import theleo.jstruct.NullPointerDereference;
import theleo.jstruct.Reference;
import theleo.jstruct.Struct;
import theleo.jstruct.hidden.Mem0;

/**
 *
 * @author Juraj Papp
 */
public class StructTest {
	
	@Struct
	public static class L2 {
		public V3 a;
		public V3 b;
		@Reference public V3 c;
		String str;
	}
	
	@Struct
	public static class V3 {
		public double x;
		public float y;
	}
	
	@Struct
	public static class F {
		public float value;
	}
	
	@Struct
	public static class Str2 {
		String a, b;
	}
	
	public static void add(V3 a, V3 b, V3 store) {
		V3 l = Mem.stack(V3.class);
		{ l.x = 1+a.x; l.y = 2+b.y; }
		assertTrue(l.x == 1+a.x);
		assertTrue(l.y == 2+b.y);
		
		store.x = a.x+b.x;
		store.y = a.y+b.y;
	}
	
	@Test
	public void stackTest() {
		Str2 str = Mem.stack(Str2.class);
		{ str.a = "Hello"; str.b = "Stack"; }
		
		assertTrue(str.a.equals("Hello"));
		assertTrue(str.b.equals("Stack"));
		
		F fRaw = Mem0.stackRaw(F.class);
		fRaw.value = 77;
		assertTrue(fRaw.value == 77);
		
		F f = Mem.stack(F.class);
		{ f.value = 7; }
		
		assertTrue(f.value == 7);
		
		V3 v = Mem.stack(V3.class); 
		{ v.x = 10; v.y = 100; }
		
		V3 add = Mem0.stackRaw(V3.class);
		add(v, v, add);
		
		assertTrue(add.x == 20);
		assertTrue(add.y == 200);
		
		L2 l = Mem.stack(L2.class);
		{
			l.a.x = 5;
			l.a.y = 10;
			l.b = v;
			l.c = v;
			l.str = "Hello";
		}
		assertTrue(l.b.y == 100);
		assertTrue(l.str.equals("Hello"));
					
		int a = 5;
		if(a == 5) {
			V3 v2 = Mem.stack(V3.class);
			{ v2.x = 5; v2.y = 15; }
			assertTrue(v2.x == 5);
			assertTrue(v2.y == 15);
		}
		
		
	}
	
	@Struct
	public static class Vec2 {
		public float x, y;
	}
	@Struct
	public static class Vec3 {
		public byte byte_;
		public boolean bool_;
		public short short_;
		public char char_;
		public int int_;
		public float float_;
		public double double_;
		public long long_;
		
		@Reference public Vec3 vec3f;
		public ArrayList<String> object;
		public Vec2 vec2;
	}
	
	long longVal;
	Vec2 local;
	static class A {
		Vec2 aval;
	}
	@Test
	public void testLocal() {
		long lv = longVal;
		lv++;
		try {
			Vec2 v = local;
			fail();
			v.x = 5;
		}
		catch(NullPointerDereference e) {}
		
		A a = new A();
		try {
			Vec2 v = a.aval;
			fail();
		}
		catch(NullPointerDereference e) {}
		try {
			System.err.println("a.val " + a.aval);
			fail();
		}
		catch(NullPointerDereference e) {}
		
		Vec2[] arr = new Vec2[10];
		arr[5].y = 10;
		local = arr[5];
		assertTrue(local.y == 10);
		local.y = 7;
		assertTrue(arr[5].y == 7);
		
		local = null;
		try {
			Vec2 v = local;
			fail();
			v.x = 5;
		}
		catch(NullPointerDereference e) {}
		
		Mem.tag(arr);
	}
	
	@Test
	public void testArrayList() {
		Vec2[] arr = new Vec2[10];
		for(int i = 0; i < arr.length; i++) arr[i].x = i;
		
		ArrayList<Vec2> list = new ArrayList<>();
		list.add(arr[5]);
		
		Vec2 a5 = list.get(0);
		assertTrue(a5.x == 5f);
	}
	
	@Test
	public void bufferTest() {
		ByteBuffer b = ByteBuffer.allocateDirect(24).order(ByteOrder.nativeOrder());
		b.limit(b.capacity());
		
		Vec2[] arr = Mem.wrap(b, Mem.sizeOf(Vec2.class));
		for(int i = 0; i < arr.length; i++) {
			Vec2 a = arr[i];
			a.x = i;
			a.y = i+0.5f;
		}
		
		FloatBuffer fb = b.asFloatBuffer();
		fb.clear();
		int i = 0;
		while(fb.hasRemaining()) {
			assertTrue(fb.get() == (i));
			if(fb.hasRemaining()) assertTrue(fb.get() == (i+0.5f));
			i++;
		}
		
		try {
			arr[-1].x = 5;
			fail();
		}
		catch(IndexOutOfBoundsException e) {
		
		}
		
		try {
			arr[arr.length].x = 5;
			fail();
		}
		catch(IndexOutOfBoundsException e) {
		
		}
		
	}
	
	@Test
	public void dataTest() {
		assertTrue(Mem.alignment(Vec2.class) == 4);
		assertTrue(Mem.sizeOf(Vec2.class) == 8);
		assertTrue(Mem.sizeOfData(Vec2.class) == 8);
		assertTrue(Mem.endPadding(Vec2.class) == 0);
		
		assertTrue(Mem.sizeOf(Vec3.class) == 56);
		assertTrue(Mem.sizeOfData(Vec3.class) == 54);
		assertTrue(Mem.endPadding(Vec3.class) == 2);
		assertTrue(Mem.alignment(Vec3.class) == 8);
		
		Vec3[] v = new Vec3[10];
		
		assertTrue(v.length == 10);
		assertTrue(Mem.isNull(v[5].vec3f));
		
		ArrayList<String> list = new ArrayList<>();
		list.add("Hello Struct");

		Mem.slice(v, 5, v.length)[0].object = list;
		
		assertTrue(v[5].object.get(0).equals("Hello Struct"));
		
		
		Vec3 v7 = v[7];
		v7.bool_ = true;
		v7.byte_ = 15;
		v7.char_ = 123;
		v7.double_ = 14.0;
		v7.float_ = 70f;
		v7.int_ = 12345;
		v7.short_ = 48;
		v7.long_ = 0xffff00000000L;
		v7.vec2.x = 1;
		v7.vec2.y = 77;
		v7.object = list;
		
		Vec3 r = Mem.slice(v, 5, v.length-1, -1)[1];
		
		assertTrue(r == v7);
		
		v[8] = v[6] = v[7];
		v7.vec2 = v7.vec2;
		
		v7.double_ = 156;
		v7.object = null;
		assertTrue(v7.object == null);
		
		r = v[8];
		
		assertTrue(r.bool_);
		assertTrue(r.byte_ == 15);
		assertTrue(r.char_ == 123);
		assertTrue(r.double_ == 14.0);
		assertTrue(r.float_ == 70f);
		assertTrue(r.int_ == 12345);
		assertTrue(r.short_ == 48);
		assertTrue(r.long_ == 0xffff00000000L);
		assertTrue(r.vec2.x == 1);
		assertTrue(r.vec2.y == 77);		
		assertTrue(r.object == list);
		
		try {
			r.vec3f.bool_ = false;
			fail();
		}
		catch(NullPointerDereference e) {
		
		}
		
		r.vec3f = r;
		assertTrue(r.vec3f.long_ == 0xffff00000000L);
		assertTrue(r.vec3f.vec3f.long_ == 0xffff00000000L);
		assertTrue(r.vec3f.vec3f.long_ == 0xffff00000000L);
		assertTrue(r.vec3f.vec3f.vec3f.long_ == 0xffff00000000L);
		
		Vec2[] arr = new Vec2[Mem.li(52L)];
		arr[Mem.li(5L)].x = 4;
	
		Mem.tag(v);
		Mem.tag(arr);
	}
	
	@Struct
	public static class Name {
		public String first;
		public String last;
	}
	
	@Struct
	public static class Str {
		public String a;
		public String b;
		public Name name1;
		public String c;
		public Name name2;
		public A javaObj;
	}
	
	@Test
	public void testHybrid() {
		Vec2[] v2 = new Vec2[10];
		Str[] arr = new Str[10];
				
		Str a = arr[5];
	
		a.a = "a";
		a.b = "b";
		a.c = "c";
		a.name1.first = "name1.first";
		a.name1.last = "name1.last";
		a.name2.first = "name2.first";
		a.name2.last = "name2.last";
		a.javaObj = new A();
		try { a.javaObj.aval.x = 10; fail(); } catch(NullPointerDereference e) {}
		a.javaObj.aval = v2[5];
		v2[5].x = 10;
		a.javaObj.aval.y = 12;
		
		Str[] arr2 = new Str[10];
	
		arr2[4] = arr[5];
		a = arr2[4];
		
		assertTrue("a".equals(a.a));
		assertTrue("b".equals(a.b));
		assertTrue("c".equals(a.c));
		assertTrue("name1.first".equals(a.name1.first));
		assertTrue("name1.last".equals(a.name1.last));
		assertTrue("name2.first".equals(a.name2.first));
		assertTrue("name2.last".equals(a.name2.last));
		assertTrue("name2.last".equals(arr2[4].name2.last));
		assertTrue(!arr2[4].name2.last.isEmpty());
		assertTrue(a.javaObj.aval.x == 10);
		assertTrue(a.javaObj.aval.y == 12);
		
		Name name1 = a.name1;
		assertTrue("name1.first".equals(name1.first));
		assertTrue("name1.last".equals(name1.last));
		
		name1.last = "change";
		assertTrue("change".equals(a.name1.last));
		
		
		Mem.tag(v2);
		Mem.tag(arr);
		Mem.tag(arr2);
				
	}
}
