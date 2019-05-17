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

public class RefN {
	
	public final Ref1 owner;
	public final long[] lengthN;

	public RefN(Ref1 owner, long... len) {
		this.owner = owner;
		this.lengthN = len;
	}
	
	public final long getIndex(int... index) {
		//y+x*lengthY
		//Address = Base + ((depthindex*col_size+colindex) * row_size + rowindex) * Element_Size
		//A[depth] [col] [row]
		
		long sum = index[0];
		for(int i = 1; i < index.length; i++) 
			sum = index[i] + sum*lengthN[i];
		
		return owner.getIndex(sum);
		
//		long sum = index[index.length-1];
//		for(int i = index.length-2; i >= 0; i--) 
//			sum = index[i] + sum*lengthN[i];
//		
//		return owner.getIndex(sum);
	}

	public final long getIndex(long... index) {
		long sum = index[0];
		for(int i = 1; i < index.length; i++) 
			sum = index[i] + sum*lengthN[i];
		
		return owner.getIndex(sum);
	}
	
	public final long getLength(int dim) { return lengthN[dim]; }
	public final long getCapacity() { 
		long sum = lengthN[0];
		for(int i = 1; i < lengthN.length; i++) sum *= lengthN[i];
		return sum;
	}
}
