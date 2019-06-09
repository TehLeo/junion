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
package theleo.jstruct.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import theleo.jstruct.exceptions.CompileException;
import theleo.jstruct.exceptions.StructIndexOutOfBoundsException;
import theleo.jstruct.hidden.Mem0;
import theleo.jstruct.hidden.R1;
import theleo.jstruct.hidden.Stack;
import theleo.jstruct.reflect.StructType;

/**
 * Resizable struct array implementation.
 * StructList manages a struct array and resizes it when needed.
 * 
 * Unlike an ArrayList, which stores references to objects, struct list stores actual data of structs.
 * 
 * Each reference returned from this list (eg. through get method) return a reference
 * to the struct object inside the current array. And thus changes to the returned object
 * are reflected back in the array.
 * 
 * However, note that any operation(eg add, remove) which modifies the size of this list, invalidates the previously
 * returned references. Eg.: Consider each returned reference as a tuple of (struct array and index to the array). If eg. new element is
 * added that requires an allocation of a bigger array, the previously returned reference points to the older array.
 * 
 * @author Juraj Papp
 * @param <T>
 */
public class StructList<T> implements List<T> {
	public static int DEFAULT_CAPACITY = 4;
	protected final StructType type;
	protected R1 array = null; 
	protected long longSize = 0;
	protected transient int modCount = 0;
	
	public StructList(Class type) {
		this.type = StructType.forClass(type);
	}
	public StructList(StructType type) {
		this.type = type;
	}

	public long longSize() { return longSize; }
	@Override
	public int size() {
		return longSize > Integer.MAX_VALUE?Integer.MAX_VALUE:(int)longSize;
	}

	/**
	 * Returns true if list is empty.
	 * @return true if empty
	 */
	@Override
	public boolean isEmpty() {
		return longSize == 0;
	}
		
	/**
	 * Retrieves struct type reference at specified index without copying.
	 * 
	 * The reference points to a valid location in a struct array. Thus, changes
	 * to the data through the reference are reflected in the struct array and thus in the list.
	 * 
	 * However, any operation(eg add, remove) which modifies the size of this list, invalidates the previously
	 * returned references.
	 * 
	 * Eg.: Consider each returned reference as a tuple of (struct array and index to the array). If eg. new element is
	 * added that requires a allocation of a bigger array, the previously returned reference points to the older array.
	 *  
	 * @param index
	 * @return 
	 */
	public T get(long index) {
		R1 a = array;
		Stack s = Mem0.stack();
		s.returnAddress = a.idx(index);
		return (T)a;
	}
	
	/**
	 * Retrieves struct type reference at specified index without copying.
	 * 
	 * The reference points to a valid location in a struct array. Thus, changes
	 * to the data through the reference are reflected in the struct array and thus in the list.
	 * 
	 * However, any operation(eg add, remove) which modifies the size of this list, invalidates the previously
	 * returned references. 
	 * 
	 * Eg.: Consider each returned reference as a tuple of (struct array and index to the array). If eg. new element is
	 * added that requires a allocation of a bigger array, the previously returned reference points to the older array.
	 * 
	 * @param index
	 * @return 
	 */
	@Override
	public T get(int index) {
		Stack s = Mem0.stack();
		R1 a = array;
		s.returnAddress = a.idx(index);
		return (T)a;
	}
	protected R1 growIfNeeded() {
		R1 a = array;
		if(a == null || a.longLength <= longSize) {
			long newSize = Math.max(DEFAULT_CAPACITY, longSize<<1);
			
			R1 arr = (R1)Mem0.AA.allocateArray(type, newSize);
			if(a != null) Mem0.copy(a, a.base, arr, arr.base, type, longSize);
			
			array = arr;
			return arr;
		}
		return a;
	}
	
	/**
	 * Adds a struct type to this list and returns a reference to it.
	 * 
	 * The reference points to a valid location in a struct array. Thus, changes
	 * to the data through the reference are reflected in the struct array and thus in the list.
	 * 
	 * However, any operation(eg add, remove) which modifies the size of this list, invalidates the previously
	 * returned references. 
	 * 
	 * Eg.: Consider each returned reference as a tuple of (struct array and index to the array). If eg. new element is
	 * added that requires a allocation of a bigger array, the previously returned reference points to the older array.
	 * 
	 * @return reference to newly added struct array
	 */
	public T add() {
		Stack s = Mem0.stack();
		modCount++;
		R1 a = growIfNeeded();
		s.returnAddress = a.idx(longSize);
		longSize++;
		return (T)a;
	}
	
	/**
	 * See {@link #add(Object) }
	 * 
	 * @param r
	 * @param l
	 * @return 
	 */
	public boolean add(R1 r, long l) {
		R1 a = growIfNeeded();
		modCount++;
		Mem0.copy(r, l, a, a.idx(longSize), type, 1);
		longSize++;
		return true;
	}
	
	/**
	 * Adds struct type to this list by copying its data.
	 * @param e struct type
	 * @return true
	 */
	@Override
	public boolean add(T e) {
		throw new CompileException();
	}
	
	/**
	 * Removes the element at the end of the list.
	 * 
	 * @return true if an element was removed, false if list was empty
	 */
	public boolean removeLast() {
		if(longSize <= 0) return false;
		modCount++;
		R1 a = array;
		if(type.objectCount != 0) Mem0.nullRefs(a, a.idx(longSize), type, 1);
		longSize--;
		return true;
	}
	/**
	 * Removes the element at the specified position.
	 * Shifts any subsequent elements to the left (subtracts one
     * from their indices). Returns null. (Cannot return the removed element, since its data was just deleted.)
     *
	 * @param index
	 * @return null
	 */
	public T remove(long index) {
		R1 a = array;
		if(index == longSize-1) removeLast();
		else {
			modCount++;
			Mem0.shift(a, a.idx(index), type, longSize-index, -1);
			if(type.objectCount != 0) Mem0.nullRefs(a, a.idx(longSize-1), type, 1);
			longSize--;
		}
		return null;
	}
	
	/**
	 * Removes the element at the specified position.
	 * Shifts any subsequent elements to the left (subtracts one
     * from their indices). Returns null. (Cannot return the removed element, since its data was just deleted.)
     *
	 * @param index
	 * @return null
	 */
	@Override
	public T remove(int index) {
		R1 a = array;
		if(index == longSize-1) removeLast();
		else {
			modCount++;
			Mem0.shift(a, a.idx(index), type, longSize-index, -1);
			if(type.objectCount != 0) Mem0.nullRefs(a, a.idx(index), type, 1);
		}
		return null;
	}
	
	@Override
	public void clear() {
		modCount++;
		if(type.objectCount != 0) {
			R1 a = array;
			Mem0.nullRefs(a, a.base, type, longSize);
		}
		longSize = 0;
	}
	
	public final long idx(R1 r, int i) {
		if(i < 0 || i >= longSize) StructIndexOutOfBoundsException.throwException(i);
		return i*(long)r.strSize+r.base;
	}
	public final long idx(R1 r, long i) {
		if(i < 0 || i >= longSize) StructIndexOutOfBoundsException.throwException(i);
		return i*(long)r.strSize+r.base;
	}
	
	
	/**
	 * See {@link #set(long, Object) }
	 * 
	 * @param index
	 * @param r
	 * @param l
	 * @return 
	 */
	public T set(long index, R1 r, long l) {
		R1 a = array;
		long p = idx(a,index);
		Mem0.copy(r, l, a, p, type, 1);
		Stack s = Mem0.stack();
		s.returnAddress = p;
		return (T)a;
	}
	public T set(long l, T element) {
		throw new CompileException();
	}
	
	/**
	 * See {@link #set(int, Object) }
	 * 
	 * @param index
	 * @param r
	 * @param l
	 * @return 
	 */
	public T set(int index, R1 r, long l) {
		R1 a = array;
		long p = idx(a,index);
		Mem0.copy(r, l, a, p, type, 1);
		Stack s = Mem0.stack();
		s.returnAddress = p;
		return (T)a;
	}
	
	@Override
	public T set(int index, T element) {
		throw new CompileException();
	}
	
	/**
	 * See {@link #add(int, Object) }
	 * 
	 * @param index
	 * @param r
	 * @param l
	 * @return 
	 */
	public void add(long index, R1 r, long l) {
		if(index == longSize) add(r, l);
		else {
			modCount++;
			R1 a = growIfNeeded();
			Mem0.shift(a, index, type, longSize-index, 1);
			Mem0.copy(r, l, a, idx(a,index), type, 1);
		}
	}
	/**
	 * See {@link #add(int, Object) }
	 * 
	 * @param index
	 * @param r
	 * @param l
	 * @return 
	 */
	public void add(int index, R1 r, long l) {
		if(index == longSize) add(r, l);
		else {
			modCount++;
			R1 a = growIfNeeded();
			Mem0.shift(a, index, type, longSize-index, 1);
			Mem0.copy(r, l, a, idx(a,index), type, 1);
		}
	}
	
	@Override
	public void add(int index, T element) {
		throw new CompileException();
	}

	@Override
	public Iterator<T> iterator() {
		return new Iter();
	}
	private class Iter implements Iterator<T> {
		private final R1 r;
		private long pos, end, lastRet=-1;
		int expectedModCount = modCount;

		public Iter() {
			R1 a = array;
			r = a;
			pos = a.base;
			end = a.idx(longSize-1);
		}
		
		
		@Override
		public boolean hasNext() {
			return pos <= end;
		}

		@Override
		public T next() {
			checkForComodification();
			long p = pos;
			if(p > end) throw new NoSuchElementException();
			pos += r.strSize;
			lastRet = p;
			Stack s = Mem0.stack();
			s.returnAddress = p;
			return (T)r;
		}
		final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }

		@Override
		public void remove() {
			if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();
			StructList.this.remove((lastRet-r.base)/r.strSize);
			pos = lastRet;
			lastRet = -1;
			expectedModCount = modCount;
			end -= r.strSize;
		}
		
	}
	
	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("Not supported.");
	}
	
	
	@Override
	public boolean contains(Object o) {
		throw new UnsupportedOperationException("Not supported.");
	}

	
	
	
	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException("Not supported yet."); 
	}
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("Not supported yet."); 
	}
	
	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	

	

	
	@Override
	public int indexOf(Object o) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int lastIndexOf(Object o) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ListIterator<T> listIterator() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	
	
	
}
