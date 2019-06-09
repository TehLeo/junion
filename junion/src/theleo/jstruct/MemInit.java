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
package theleo.jstruct;

import theleo.jstruct.allocator.Allocator;
import theleo.jstruct.allocator.ArrayAllocator;
import theleo.jstruct.allocator.DefaultArrayAllocator;
import theleo.jstruct.allocator.SystemAllocator;
import theleo.jstruct.bridge.DefaultBridge;
import theleo.jstruct.bridge.Bridge;

/**
 * 
 *
 * @author Juraj Papp
 */
public class MemInit {
	/**
	 * If a thread decides to use stack for structs,
	 * at that time a stack is allocated with the following values.
	 * 
	 * STACK_INIT_SIZE - initial size of storage in bytes
	 * STACK_INIT_MAX_SIZE - max size of storage in bytes
	 * 
	 * STACK_INIT_OBJ_SIZE - initial size of storage for java object references
	 * STACK_INIT_MAX_OBJ_SIZE - max size of storage for java object references
	 * 
	 */
	public static long STACK_INIT_SIZE = 1024;
	/**
	 * See {@link MemInit#STACK_INIT_SIZE}
	 */
	public static long STACK_INIT_MAX_SIZE = 262144;
	/**
	 * See {@link MemInit#STACK_INIT_SIZE}
	 */
	public static int STACK_INIT_OBJ_SIZE = 32;
	/**
	 * See {@link MemInit#STACK_INIT_SIZE}
	 */
	public static int STACK_INIT_MAX_OBJ_SIZE = 262144;
	
	private static boolean initialized = false;
	private static Allocator allocator = new SystemAllocator();
	private static ArrayAllocator arrayAllocator = new DefaultArrayAllocator();
	private static Bridge bridge = new DefaultBridge();
	
	/**
	 * Set the allocator interface used for allocating memory.
	 * 
	 * <p>
	 * This method has to called before class {@link theleo.jstruct.hidden.Mem0}
	 * gets loaded. Thus, call this method
	 * within a static initializer of your application, eg:
	 * </p>
	 * 
	 * 
	 * 
	 * <pre>{@code
	 * static {
	 *     MemInit.setAllocator(new Allocator() { ... });
	 * }
	 * }</pre>
	 * 
	 * 
	 * @param a allocator
	 * @throws IllegalArgumentException if already initialized
	 * @throws NullPointerException if a is null
	 */
	public static void setAllocator(Allocator a) {
		if(initialized) throw new IllegalArgumentException("Mem0 already initialized!");
		if(a == null) throw new NullPointerException();
		allocator = a;
	}
	
	/**
	 * Set the array allocator interface used for allocating arrays.
	 * 
	 * <p>
	 * This method has to called before class {@link theleo.jstruct.hidden.Mem0}
	 * gets loaded. Thus, call this method
	 * within a static initializer of your application, eg:
	 * </p>
	 * 
	 * 
	 * 
	 * <pre>{@code
	 * static {
	 *     MemInit.setArrayAllocator(...);
	 * }
	 * }</pre>
	 * 
	 * 
	 * @param a array allocator
	 * @throws IllegalArgumentException if already initialized
	 * @throws NullPointerException if a is null
	 */
	public static void setArrayAllocator(ArrayAllocator a) {
		if(initialized) throw new IllegalArgumentException("Mem0 already initialized!");
		if(a == null) throw new NullPointerException();
		arrayAllocator = a;
	}
	
	/**
	 * Set the bridge interface. By default {@link theleo.jstruct.bridge.DefaultBridge} is used.
	 * 
	 * <p>
	 * During development or debugging a debug bridge which preforms additional checks can be used
	 * such as:
	 * {@link theleo.jstruct.bridge.DebugBridge}
	 * </p>
	 * 
	 * <p>
	 * This method has to called before class {@link theleo.jstruct.hidden.Mem0}
	 * gets loaded. Thus, call this method
	 * within a static initializer of your application, eg:
	 * </p>
	 * 
	 * <pre>{@code
	 * static {
	 *     MemInit.setBridge(new DebugBridge());
	 * }
	 * }</pre>
	 * 
	 * @param b bridge interface to set
	 * 
	 * @throws IllegalArgumentException if already initialized
	 * @throws NullPointerException if b is null
	 */
	public static void setBrige(Bridge b) {
		if(initialized) throw new IllegalArgumentException("Mem0 already initialized!");
		if(b == null) throw new NullPointerException();
		bridge = b;
	}
	
	/**
	 * Returns the current allocator.
	 * By default {@link theleo.jstruct.allocator.SystemAllocator} is returned.
	 * 
	 * @return allocator
	 */
	public static Allocator getAllocator() {
		return allocator;
	}	

	/**
	 * Returns the current array allocator.
	 * By default {@link theleo.jstruct.allocator.DefaultArrayAllocator} is returned.
	 * 
	 * @return array allocator
	 */
	public static ArrayAllocator getArrayAllocator() {
		return arrayAllocator;
	}
	
	
	/**
	 * Returns the current bridge.
	 * By default {@link theleo.jstruct.hidden.DefaultBridge} is returned.
	 * 
	 * @return bridge interface
	 */
	public static Bridge getBridge() {
		return bridge;
	}
	
	/**
	 * Returns true is Mem0 class was already initialized.
	 * If true is returned most of the settings that can be changed in
	 * this class are no longer modifiable.
	 * 
	 * @return true if initialized
	 */
	public static boolean isInitialized() {
		return initialized;
	}
	/**
	 * Not to be called by user. Called the first time Mem0 class is loaded.
	 */
	public static void setInitialized() { 
		initialized = true;
	}

	
	
	
}
