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

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import theleo.jstruct.hidden.Mem0;
import theleo.jstruct.hidden.R1;

/**
 *
 * @author Juraj Papp
 */
public class DefaultCleaner {
    public static DefaultCleaner getInstance() {
        return InstanceHolder.INSTANCE;
    }
	
	public static StructPhantomReference register(R1 a) {
		return StructPhantomReference.create(getInstance(), a);
	}
	public static StructPhantomReference register(R1 a, long base) {
		return StructPhantomReference.create(getInstance(), a, base);
	}
	
	private static class InstanceHolder {
        static final DefaultCleaner INSTANCE = new DefaultCleaner();
		static {
			Thread cleanerThread = new Thread() {
				@Override
				public void run() {
					while(true) {
						try {
							StructPhantomReference ref = (StructPhantomReference)INSTANCE.refQueue.remove();

							synchronized(INSTANCE.structRefLock) {
								if(ref.prev == null) INSTANCE.structRef = ref.next; 
								else ref.prev.next = ref.next;
								if(ref.next != null) ref.next.prev = ref.prev;
							}

							ref.doClean();
							ref.clear();
						}
						catch(InterruptedException e) {}
					}
				}
			};
			cleanerThread.setDaemon(true);
			cleanerThread.start();
		}
    }
	
	public static class StructPhantomReference extends PhantomReference<Object> {
		public long pointer = 0, pointerEnd;
		public boolean clean = true;
		
		public StructPhantomReference prev;
		public StructPhantomReference next;
		
		public StructPhantomReference(Object referent, ReferenceQueue<? super Object> q) {
			super(referent, q);
		}
		public void doClean() {
			if(!clean) return;
			clean = false;
			pointerEnd = 0;
			if(pointer != 0) {
				Mem0.A.freeMemory(pointer);
				pointer = 0;
			}
		}
		public static StructPhantomReference create(DefaultCleaner d, R1 a) {
			StructPhantomReference s = new StructPhantomReference(a, d.refQueue);
			s.pointer = a.base();
			s.pointerEnd = a.base()+a.strSize()*a.longLenght();
			synchronized(d.structRefLock) {
				if(d.structRef == null) d.structRef = s;
				else {
					s.next = d.structRef;
					d.structRef.prev = s;
					d.structRef = s;
				}
			}
			return s;
		}
		public static StructPhantomReference create(DefaultCleaner d, R1 a, long base) {
			StructPhantomReference s = new StructPhantomReference(a, d.refQueue);
			s.pointer = base;
			s.pointerEnd = a.base()+a.strSize()*a.longLenght();
			synchronized(d.structRefLock) {
				if(d.structRef == null) d.structRef = s;
				else {
					s.next = d.structRef;
					d.structRef.prev = s;
					d.structRef = s;
				}
			}
			return s;
		}
	}
	
	private final ReferenceQueue<Object> refQueue = new ReferenceQueue<>();
	private final Object structRefLock = new Object();
	private StructPhantomReference structRef;
	
	private DefaultCleaner() {}

	public Object getStructRefLock() {
		return structRefLock;
	}

	public ReferenceQueue<Object> getRefQueue() {
		return refQueue;
	}

	public StructPhantomReference getStructRef() {
		return structRef;
	}

}
