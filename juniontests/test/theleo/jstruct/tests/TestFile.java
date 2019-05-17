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
package theleo.jstruct.tests;

import java.awt.Point;
import theleo.jstruct.Mem;
import theleo.jstruct.Struct;
import theleo.jstruct.hidden.Mem0;

/**
 *
 * @author Juraj Papp
 */
public class TestFile {
	@Struct
	public static class Vec3 { 
		int x, y, z;
		Point p; 
	}
	public static void main(String[] args) {
		Vec3 v = Mem.stack(Vec3.class);
		{ v.x = 0; v.y = 1; v.z = 2; v.p = null; }
		v.p = new Point();
		
//		(get(v, 1+2+3+5+v.x--).x)++;
//		float i = 0;
		
//		i = (i)++;
		System.out.println(v.z);
		
		get(v, 1).z = 5;
		System.out.println(v.z);
		double d = v.p.getX();
		double d2 = (v.p).x;
		v.p.x++;
		
	}
	static Vec3 get(Vec3 v, int i) {return v;} 
}
