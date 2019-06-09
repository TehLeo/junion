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
package theleo.jstruct.allocator;
import java.util.logging.Level;
import theleo.jstruct.hidden.Mem0;
import static theleo.jstruct.hidden.Vars.u;

/**
 * Delegates allocation requests to the system.
 * 
 * @author Juraj Papp
 */
public class SystemAllocator implements Allocator {
	@Override
	public void initializeAllocator() {
		
	}

	@Override
	public void cleanupAllocator() {
		
	}

	@Override
	public long allocateMemory(long size) {
		long l = u.allocateMemory(size);
		//check if suceeded
		if(l == 0) throwOOM(size);
		//check if aligned to 8 bytes
		if((l & 7) != 0) {
			//should not happen
			Mem0.LOGGER.log(Level.SEVERE, "Unsafe.allocateMemory returned unaligned address! ");
			//in case it does
			//the pointer is aligned manually in DefaultArrayAllocator
		}
		return l;
	}
	

	@Override
	public void freeMemory(long addr) {
		u.freeMemory(addr);
	}

	private static void throwOOM(long size) {
		throw new OutOfMemoryError("Could not allocate " + size + " bytes!");
	}
}
