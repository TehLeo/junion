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
package theleo.jstruct.hidden;

/**
 *
 * @author Juraj Papp
 */
public class Ref1 implements AutoCloseable {
	public final long base;
	public final long length;
	public final long structSize;	
	public final Object owner;
	public Ref1(long base, long length, long strSize) {
		this.base = base;
		this.length = length;
		this.structSize = strSize;
		this.owner = this;
	}
	public Ref1(Object owner, long base, long length, long strSize) {
		this.base = base;
		this.length = length;
		this.structSize = strSize;
		this.owner = owner;
	}
	
	public void free() {
				
	}
	
	public final long getIndex(int i) {
		if(i < 0 || i >= length)
//		if(Long.compareUnsigned(i, length) >= 0)
			throw new IndexOutOfBoundsException(Integer.toString(i));
		return base + i * structSize;
	}
	public final long getIndex(long i) {
		if(i < 0 || i >= length)
//		if(Long.compareUnsigned(i, length) >= 0)
			throw new IndexOutOfBoundsException(Long.toString(i));
		return base + i * structSize;
	}
	
	@Override
	public final void close() {
		free();
	}

	@Override
	public String toString() {
		return "StructArray";
	}
	
}
