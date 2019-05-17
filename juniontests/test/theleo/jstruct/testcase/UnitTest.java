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
package theleo.jstruct.testcase;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.junit.Test;
import static org.junit.Assert.*;
import theleo.jstruct.Mem;
import theleo.jstruct.NullPointerDereference;
import theleo.jstruct.Reference;
import theleo.jstruct.Struct;

/**
 *
 * @author Juraj Papp
 */
public class UnitTest {
	
	public UnitTest() {
	}
	
	@Struct
	public static class Str1 {
		public boolean _boolean;
		public byte _byte;
		public short _short;
		public char _char;
		public int _int;
		public long _long;
		public float _float;
		public double _double;
	}
	
	
	/**
	 * Test whether primitive values are stored and read correctly.
	 */
	@Test
	public void testReadWriteData() {
		//Expected size of Str1 stuct aligned to 8 bytes is 32 bytes
		assertTrue(Mem.sizeOf(Str1.class) == 32);
		
		//create an array of test structures
		Str1[] arr = new Str1[10];
		
		//store test values in this array
		//note the cast (int)arr.length
		//since struct arrays lenght is of type long
		Val[][] vals = new Val[(int)arr.length][8];
				
		for(int i = 0; i < arr.length; i++) {
			Val[] v = vals[i];
			v[0] = new Val((i&1)==0);
			v[1] = new Val((byte)(125+i));
			v[2] = new Val((short)(-16000+i));
			v[3] = new Val((char)(17000+i));
			v[4] = new Val(0xfefefefe+i);
			v[5] = new Val(0x01020304abcdef12L+i);
			v[6] = new Val(1234.12345f+i);
			v[7] = new Val(1234000.12345+i);
			
			Str1 s = arr[i];
			s._boolean = v[0].getBool();
			s._byte = v[1].getByte();
			s._short = v[2].getShort();
			s._char = v[3].getChar();
			s._int = v[4].getInt();
			s._long = v[5].getLong();
			s._float = v[6].getFloat();
			s._double = v[7].getDouble();
		}
		
		for(int i = 0; i < arr.length; i++) {
			Val[] v = vals[i];
			
			Str1 s = arr[i];
			assertTrue(s._boolean == v[0].getBool());
			assertTrue(s._byte == v[1].getByte());
			assertTrue(s._short == v[2].getShort());
			assertTrue(s._char == v[3].getChar());
			assertTrue(s._int == v[4].getInt());
			assertTrue(s._long == v[5].getLong());
			assertTrue(s._float == v[6].getFloat());
			assertTrue(s._double == v[7].getDouble());
		}
		
		//test against side effects
		int counterSum = 0;
		for(int i = 0; i < vals.length; i++) {
			Val[] v = vals[i];
			for(int j = 0; j < v.length; j++) {
				assertTrue(v[j].counter == 2);
				counterSum += v[j].counter;
			}
		}
		assertTrue(counterSum == 160);
		
		System.out.println("Ok testReadWriteData");
	}
	
	@Test
	public void testIndexChecking() {
		
		Str1[] arr = new Str1[10];
		
		Val i1 = new Val(-1);
		Val i10 = new Val(10);
		
		try {
			arr[i1.getInt()]._char = 10;
			fail();
		}
		catch(IndexOutOfBoundsException e) {}
		
		try {
			arr[i10.getInt()]._int = 15;
			fail();
		}
		catch(IndexOutOfBoundsException e) {}
		
		try {
			int i = 115 + 5*(arr[i1.getInt()]._char);
			fail();
		}
		catch(IndexOutOfBoundsException e) {}
		
		System.out.println("Ok testIndexChecking");
	}
	
	@Struct
	public static class Vec3i {
		public int x, y, z;
	}
	
	@Test
	public void testDirectNativeByteBuffer() {
		assertTrue(Mem.sizeOf(Vec3i.class) == 12);
		
		ByteBuffer a = ByteBuffer.allocateDirect(10*Mem.sizeOf(Vec3i.class)).order(ByteOrder.nativeOrder());

		Vec3i[] arr = Mem.wrap(a, Mem.sizeOf(Vec3i.class));
		assertTrue(arr.length == 10);
		for(int i = 0; i < arr.length; i++) {
			arr[i].x = i*3;
			arr[i].y = i*3+1;
			arr[i].z = i*3+2;
		}
		for(int i = 0; i < arr.length; i++) {
			assertTrue(arr[i].x == i*3);
			assertTrue(arr[i].y == i*3+1);
			assertTrue(arr[i].z == i*3+2);
		}
		for(int i = 0; i < 120; i+=4) {
			int buf = a.getInt(i);
			assertTrue(buf == (i>>2));
		}
		System.out.println("Ok testDirectNativeByteBuffer");
	} 
	
	@Struct
	public static class Line3i {
		public Vec3i a, b;
	}
	
	@Test
	public void testNestedStructure() {
		assertTrue(Mem.sizeOf(Line3i.class) == 24);
		
		Line3i[] arr = new Line3i[3];
		for(int i = 0; i < arr.length; i++) {
			Vec3i a = arr[i].a;
			Vec3i b = arr[i].b;
			a.x = i*3;
			a.y = i*3+1;
			a.z = i*3+2;
			
			b.x = -(i*3);
			b.y = -(i*3+1);
			b.z = -(i*3+2);
		}
		
		for(int i = 0; i < arr.length; i++) {
			assertTrue(arr[i].a.x == i*3);
			assertTrue(arr[i].a.y == i*3+1);
			assertTrue(arr[i].a.z == i*3+2);
			assertTrue(arr[i].b.x == -(i*3));
			assertTrue(arr[i].b.y == -(i*3+1));
			assertTrue(arr[i].b.z == -(i*3+2));
		}
		
		System.out.println("Ok testNestedStructure");
	}
	
	@Struct
	public static class Node {
		public int value;
		@Reference public Node left, right;
	}
	
	
	static Node testReferencesNodeStatic;
	Node testReferencesNode;
	@Test
	public void testReferences() {
		//References take 8 bytes, 8+8+4=20 -> (align to 8) 24
		assertTrue(Mem.sizeOf(Line3i.class) == 24);
		
		Runnable run = new Runnable() {
			Node[] nodes = new Node[10];
			
			@Override
			public void run() {
				for(int i = 0; i < nodes.length; i++)
					nodes[i].value = i;

				Node root = nodes[0];

				try {
					//derefrencing a null reference throws NullPointerDereference exception
					Node l = nodes[0].left;
					fail();
				}
				catch(NullPointerDereference e) {}

				try {
					Node l = root.right;
					fail();
				}
				catch(NullPointerDereference e) {}

				//To check is a reference is null Mem.isNull(...) is used
				assertTrue(Mem.isNull(root.left));
				assertTrue(Mem.isNull(root.right));

				root.left = nodes[1];
				root.right = nodes[2];

				assertTrue(!Mem.isNull(root.left));
				assertTrue(!Mem.isNull(root.right));

				assertTrue(Mem.isNull(root.left.left));
				assertTrue(root.left.value == 1);
				assertTrue(root.right.value == 2);

				Node rL = root.left;
				rL.left = root;
				root.right.left = root;

				assertTrue(root.left.left == root.right.left);
				assertTrue(root.left.left.right.left.value == 0);

				try {
					Node n = testReferencesNode;
					fail();
				}
				catch(NullPointerDereference e) {}

				try {
					Node n = testReferencesNodeStatic;
					fail();
				}
				catch(NullPointerDereference e) {}

				testReferencesNode = root;
				testReferencesNodeStatic = root.left;

				assertTrue(testReferencesNode.value == root.value);
				assertTrue(testReferencesNodeStatic == root.left);
				
				System.out.println("Ok testReferences");
			}
		};
		
		run.run();
		
	} 
	@Test
	public void testMultiDimensionalArray() {
		Vec3i[][] arr = new Vec3i[7][14];
		
		int i = 0;
		for(int x = 0; x < Mem.len(arr, 0); x++)
			for(int y = 0; y < Mem.len(arr, 1); y++) {
				arr[x][y].x = x;
				arr[x][y].y = y;
				arr[x][y].z = i++;
			}
		
		Vec3i[] flat = Mem.flatten(arr);
		assertTrue(flat.length == 7*14);
		for(i = 0; i < flat.length; i++) {
			assertTrue(flat[i].z == i);
		}
		
		i = 0;
		Vec3i[][] dims = Mem.dims(flat, 7, 14);
		assertTrue(Mem.len(dims, 0) == 7);
		assertTrue(Mem.len(dims, 1) == 14);
		for(int x = 0; x < Mem.len(dims, 0); x++)
			for(int y = 0; y < Mem.len(dims, 1); y++) {
				assertTrue(dims[x][y].x == x);
				assertTrue(dims[x][y].y == y);
				assertTrue(dims[x][y].z == i++);
			}
		
		
		try {
			dims[6][15].x = 10;
			fail();
		}
		catch(IndexOutOfBoundsException e) {}
		
		//Index checking is done on the resultant index
		//not per component
		//thus the following passes
		assertTrue(dims[5][-1].z == 69);
		
		//what are your thoughts on this
		//ofcourse, indexing checking can be added for each component separately
		//however, more index checking = less performance
		//To have both speed and safety
		//My thoughts on this are:
		//-plan to add compiler flag which will enable index checking on each component
		//-also add flag which will disable index checking
		//-the default index checking on final component
		//Eg.: Then the user chooses, he may develop with a flag to enable index checking on each component
		// then disable it to compile release version for performance, etc
		
		
//		dims = Mem.dims(flat, 7, 14);
//		for(int x = 0; x < Mem.len(dims, 0); x++) {
//			for(int y = 0; y < Mem.len(dims, 1); y++) {
//				System.out.print(dims[x][y].z + " ");
//			}
//			System.out.println("");
//		}
//		
//		dims = Mem.dims(flat, 7, 14);
//		for(int x = 0; x < Mem.len(dims, 0); x++) {
//			for(int y = 0; y < 7; y++) {
//				System.out.print(dims[x][y].z + " ");
//			}
//			System.out.println("");
//		}
			
		System.out.println("Ok testMultiDimensionalArray");
	}
	
	@Test
	public void testStack() {
		Val x = new Val(5);
		Val y = new Val(6);
		Val z = new Val(7);
		
		Vec3i v = Mem.stack(Vec3i.class);
		{v.x = x.getInt(); v.y = y.getInt(); v.z = z.getInt();}
		
		assertTrue(v.x == x.getInt());
		assertTrue(v.y == y.getInt());
		assertTrue(v.z == z.getInt());
	
		assertTrue(x.counter == 2);
		assertTrue(y.counter == 2);
		assertTrue(z.counter == 2);
		
		System.out.println("Ok testStack");
	}
	
	private int add(Vec3i v) { return v.x + v.y + v.z; }
	private void incr(Vec3i v) {
		v.x++;
	}
	
	@Test
	public void testPassByReference() {
		Vec3i v = Mem.stack(Vec3i.class);
		{v.x = 1; v.y = 2; v.z = 4;}
		
		assertTrue(add(v) == 7);
		
		Vec3i[] arr = new Vec3i[3];
		arr[0] = v; //copy from stack
		arr[1] = arr[0]; //copy from arr
		
		v.x = 5;
		arr[2] = v; //copy from stack
		
		assertTrue(add(arr[0]) == 7);
		assertTrue(add(arr[1]) == 7);
		assertTrue(add(arr[2]) == 11);
		
		incr(v);
		incr(arr[0]);
		
		assertTrue(v.x == 6);
		assertTrue(arr[0].x == 2);
		
		System.out.println("Ok testPassByReference");
	}
	
	@Test
	public void testPrefixPostfixOperators() {
		Vec3i v = Mem.stack(Vec3i.class);
		{v.x = 10; v.y = 2; v.z = 4;}
		
		assertTrue(v.x++ == 10);
		assertTrue(v.x == 11);
		assertTrue(v.x-- == 11);
		assertTrue(v.x == 10);
		
		assertTrue(++v.x == 11);
		assertTrue(v.x == 11);
		assertTrue(--v.x == 10);
		assertTrue(v.x == 10);
		
		assertTrue(+v.x == 10);
		assertTrue(-v.x == -10);
		assertTrue(~v.x == ~10);
		
		System.out.println("Ok testPrefixPostfixOperators");
	}
	
	
	
	
	@Test
	public void testAssignmentOperators() {
		Str1 v = Mem.stack(Str1.class);
		{v._boolean = false; v._byte = 0;
		v._char = 0; v._double = 0; v._float = 0;
		v._int = 0; v._long = 0; v._short = 0;}
		
		Counter a = new Counter();
		
		a.get(v)._boolean = true;
		a.get(v)._byte = 1;
		a.get(v)._short = 1;
		a.get(v)._char = 1;
		a.get(v)._int = 1;
		a.get(v)._long = 1;
		a.get(v)._float = 1;
		a.get(v)._double = 1;
		
		assertTrue(v._boolean);
		assertTrue(v._byte == 1);
		assertTrue(v._short == 1);
		assertTrue(v._char == 1);
		assertTrue(v._int == 1);
		assertTrue(v._long == 1);
		assertTrue(v._float == 1);
		assertTrue(v._double == 1);
		
		a.get(v)._boolean &= false;
		assertTrue(!v._boolean);

		a.get(v)._boolean |= true;
		assertTrue(v._boolean);
				
		a.get(v)._boolean ^= true;
		assertTrue(!v._boolean);

		int num = 2;
		a.get(v)._byte += num;
		a.get(v)._short += num;
		a.get(v)._char += num;
		a.get(v)._int += num;
		a.get(v)._long += num;
		a.get(v)._float += num;
		a.get(v)._double += num;
		
		num = 3;
		assertTrue(v._byte == num);
		assertTrue(v._short == num);
		assertTrue(v._char == num);
		assertTrue(v._int == num);
		assertTrue(v._long == num);
		assertTrue(v._float == num);
		assertTrue(v._double == num);
		
		num = 1;
		a.get(v)._byte -= num;
		a.get(v)._short -= num;
		a.get(v)._char -= num;
		a.get(v)._int -= num;
		a.get(v)._long -= num;
		a.get(v)._float -= num;
		a.get(v)._double -= num;
		
		num = 2;
		assertTrue(v._byte == num);
		assertTrue(v._short == num);
		assertTrue(v._char == num);
		assertTrue(v._int == num);
		assertTrue(v._long == num);
		assertTrue(v._float == num);
		assertTrue(v._double == num);
		
		a.get(v)._byte *= num;
		a.get(v)._short *= num;
		a.get(v)._char *= num;
		a.get(v)._int *= num;
		a.get(v)._long *= num;
		a.get(v)._float *= num;
		a.get(v)._double *= num;
		
		num = 4;
		assertTrue(v._byte == num);
		assertTrue(v._short == num);
		assertTrue(v._char == num);
		assertTrue(v._int == num);
		assertTrue(v._long == num);
		assertTrue(v._float == num);
		assertTrue(v._double == num);
		
		num = 2;
		a.get(v)._byte /= num;
		a.get(v)._short /= num;
		a.get(v)._char /= num;
		a.get(v)._int /= num;
		a.get(v)._long /= num;
		a.get(v)._float /= num;
		a.get(v)._double /= num;
		
		assertTrue(v._byte == num);
		assertTrue(v._short == num);
		assertTrue(v._char == num);
		assertTrue(v._int == num);
		assertTrue(v._long == num);
		assertTrue(v._float == num);
		assertTrue(v._double == num);
		
		num = 2;
		a.get(v)._byte %= num;
		a.get(v)._short %= num;
		a.get(v)._char %= num;
		a.get(v)._int %= num;
		a.get(v)._long %= num;
		a.get(v)._float %= num;
		a.get(v)._double %= num;

		num = 0;
		assertTrue(v._byte == num);
		assertTrue(v._short == num);
		assertTrue(v._char == num);
		assertTrue(v._int == num);
		assertTrue(v._long == num);
		assertTrue(v._float == num);
		assertTrue(v._double == num);
		
		
		num = 7;
		a.get(v)._byte |= num;
		a.get(v)._short |= num;
		a.get(v)._char |= num;
		a.get(v)._int |= num;
		a.get(v)._long |= num;
		
		assertTrue(v._byte == num);
		assertTrue(v._short == num);
		assertTrue(v._char == num);
		assertTrue(v._int == num);
		assertTrue(v._long == num);
		
		num = 3;
		a.get(v)._byte &= num;
		a.get(v)._short &= num;
		a.get(v)._char &= num;
		a.get(v)._int &= num;
		a.get(v)._long &= num;
		
		assertTrue(v._byte == num);
		assertTrue(v._short == num);
		assertTrue(v._char == num);
		assertTrue(v._int == num);
		assertTrue(v._long == num);
		
		num = 2;
		a.get(v)._byte ^= num;
		a.get(v)._short ^= num;
		a.get(v)._char ^= num;
		a.get(v)._int ^= num;
		a.get(v)._long ^= num;
		
		num = 1;
		assertTrue(v._byte == num);
		assertTrue(v._short == num);
		assertTrue(v._char == num);
		assertTrue(v._int == num);
		assertTrue(v._long == num);
		
		num = 4;
		a.get(v)._byte <<= num;
		a.get(v)._short <<= num;
		a.get(v)._char <<= num;
		a.get(v)._int <<= num;
		a.get(v)._long <<= num;
		
		num = 16;
		assertTrue(v._byte == num);
		assertTrue(v._short == num);
		assertTrue(v._char == num);
		assertTrue(v._int == num);
		assertTrue(v._long == num);
		
		num = 2;
		a.get(v)._byte >>= num;
		a.get(v)._short >>= num;
		a.get(v)._char >>= num;
		a.get(v)._int >>= num;
		a.get(v)._long >>= num;
		
		num = 4;
		assertTrue(v._byte == num);
		assertTrue(v._short == num);
		assertTrue(v._char == num);
		assertTrue(v._int == num);
		assertTrue(v._long == num);
		
		num = 2;
		a.get(v)._byte >>>= num;
		a.get(v)._short >>>= num;
		a.get(v)._char >>>= num;
		a.get(v)._int >>>= num;
		a.get(v)._long >>>= num;
		
		assertTrue(v._byte == 1);
		assertTrue(v._short == 1);
		assertTrue(v._char == 1);
		assertTrue(v._int == 1);
		assertTrue(v._long == 1);
		
		System.out.println("Ok testAssignmentOperators");
	}
	
	
	private static class Counter {
		public int counter;
		public Str1 get(Str1 s) {
			counter++;
			return s;
		}
	}
	private static class Val {
		public Object val;
		public int counter;
		public Val(Object val) {
			this.val = val;
			this.counter = 0;
		}
		public boolean getBool() {counter++; return (boolean)val;}
		public byte getByte() {counter++; return (byte)val;}
		public short getShort() {counter++; return (short)val;}
		public char getChar() {counter++; return (char)val;}
		public int getInt() {counter++; return (int)val;}
		public long getLong() {counter++; return (long)val;}
		public float getFloat() {counter++; return (float)val;}
		public double getDouble() {counter++; return (double)val;}
		public Object getObject() {counter++; return val;}
	}
	
}
