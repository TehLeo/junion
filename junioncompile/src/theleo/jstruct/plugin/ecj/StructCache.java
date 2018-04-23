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
package theleo.jstruct.plugin.ecj;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMemberValuePairBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import sun.tools.jar.resources.jar;
import theleo.jstruct.ArraySize;
import theleo.jstruct.Struct;
import theleo.jstruct.hidden.Order;
import theleo.jstruct.plugin.Log;

/**
 *
 * @author Juraj Papp
 */
public class StructCache {
	public static HashMap<String, Entry> map = new HashMap<>();
	public static AST currentAST;
	public static ClassLoader loader;
	
	private static final Comparator<IVariableBinding> ID_COMP =
				new Comparator<IVariableBinding>() {
			@Override
			public int compare(IVariableBinding o1, IVariableBinding o2) {
				return o1.getVariableId() - o2.getVariableId();
			}
		};
	private static final Comparator<FieldNum> FIELD_COMP =
				new Comparator<FieldNum>() {
			@Override
			public int compare(FieldNum o1, FieldNum o2) {
				return o1.id - o2.id;
			}
		};
	
	private static final Comparator<FieldEntry> REORDER_COMP =
				new Comparator<FieldEntry>() {
			@Override
			public int compare(FieldEntry o1, FieldEntry o2) {
				if(o1.align == o2.align) {
					if(o1.offset == o2.offset)
						return o1.offset - o2.offset;
					else return o2.size - o1.size;
				}
				return (o2.align - o1.align);
			}
		};
	private static final Comparator<FieldEntry> OFFSET_COMP =
				new Comparator<FieldEntry>() {
			@Override
			public int compare(FieldEntry o1, FieldEntry o2) {
				if(o1.offset == o2.offset) return o1.sortOrder - o2.sortOrder;
				return o1.offset - o2.offset;
			}
		};
	
	private static class FieldNum {
		int id;
		Field f;
		public FieldNum(int id, Field f) {
			this.id = id;
			this.f = f;
		}
	}
	public static enum FieldType {
        BOOLEAN(1, "Boolean", false),
        BYTE(1, "Byte", true),
        SHORT(2, "Short", true),
		CHAR(2, "Char", true),
        INT(4, "Int", true),
		FLOAT(4, "Float", true),
        LONG(8, "Long", false),
        DOUBLE(8, "Double", false),
		OBJECT(8, "Object", false),
		REFERENCE(8, null, false),
		STRUCT(0, null, false);
		
		

		public String name;
		public int size;
		
		public boolean primCast;
		FieldType(int size, String name, boolean primCast) {
			this.size = size;
			this.name = name;
			this.primCast = primCast;
		}
		public static FieldType get(String qualifiedName) {
			switch(qualifiedName) {
				case "boolean": return BOOLEAN;
				case "byte": return BYTE;
				case "short": return SHORT;
				case "char": return CHAR;
				case "int": return INT;
				case "float": return FLOAT;
				case "long": return LONG;
				case "double": return DOUBLE;
			}
			throw new CompilerError(qualifiedName + " is not of primitive type.");
		}
		public boolean isSimpleType() {
			return size != 0;
		}
	}
	public static class FieldEntry {
		public String name;
		public int offset = 0;
		public int align = 0;
		public int size = 0;
		public FieldType type;
		public long length = -1;
		
		public int objOffet = Integer.MIN_VALUE;
		public ITypeBinding typeb;
		public int sortOrder;
		
		
//		public long offset;
//		public long size;
//		public long align;
//		public TypeKind type, arraytype;
//		public boolean isObject = false;
		public Entry structType;

		public FieldEntry(String name) {
			this.name = name;
		}
		
		public void setSimpleType(FieldType f) {
			type = f;
			size = f.size;
			align = f.size;
			if(align == 0)
				throw new IllegalArgumentException(f + " is not a simple type.");
		} 
		public boolean setStructType(Entry s) {
			structType = s;
			if(s != null) {
				type = FieldType.STRUCT;
				align = s.align;
				size = s.structSize-s.endPadding;
				return true;
			}
			return false;
		}
		public void setReference() {
			type = FieldType.REFERENCE;
			align = 8;
			size = 8;
		}
		
		public boolean isStruct() { return type == FieldType.STRUCT; }
		public boolean isReference() { return type == FieldType.REFERENCE; }
		public boolean isArray() { return length != -1; }

		@Override
		public String toString() {
			
			return offset + (objOffet==Integer.MIN_VALUE?"":("/"+objOffet)) + ":" + name + " " + type + "(" + align + ")" + ", " + length;
		}
		
		
		
	}
	public static class Entry {
		public boolean isStruct;
		public String binaryName;
		
		public int structSize;
		public int[] objOffsets;
		public int[] refOffsets;
		
		public int[] objCounts;
		
		public int localObjCount, globalObjCount;
		
		public FieldEntry objField;
		public boolean zero = true;
		
		public int align;
		public int endPadding;
		public int structSizeWithoutPadding;
		public HashMap<String, FieldEntry> offsetTable;
		boolean defined = false;
		public String structLayout;
		
		public void initialize(ITypeBinding ib) {
			this.binaryName = ib.getBinaryName();
			Class cls = null;
			boolean canReorder = true;
//			if(ib.isFromSource()) {
				IAnnotationBinding[] as = ib.getAnnotations();
				if(as != null) {
					for(IAnnotationBinding a : as) {
						String aName = a.getAnnotationType().getQualifiedName();
						if(aName.equals("theleo.jstruct.Struct")) {
							isStruct = true;
							Boolean reorderProp = (Boolean)getValue(a.getAllMemberValuePairs(), "autopad");
							if(reorderProp != null ) 
								canReorder = reorderProp;
						}
					}
				}
//			}
//			else {
//				try {
//					cls = Class.forName(binaryName, false, loader);
//					isStruct = cls.getAnnotation(Struct.class) != null;
//				} catch (ClassNotFoundException ex) {
//					throw new CompilerError("error: " + ex.toString());
//				}
//			}
			
			if(isStruct) {
				offsetTable = new HashMap<>();
				structSize = 0;
				structSizeWithoutPadding = 0;
				align = 0;
				localObjCount = 0;

				int objCount = 0;
				int refCount = 0;
				
							
				ArrayList<FieldEntry> fields = new ArrayList<>();
				boolean reorder = false;
				
				FieldEntry objEntry = null;

				long maxSize = 0;
				
				Log.err("---- STRUCT " + binaryName);
				
				if(!ib.isFromSource()) {
					//Force Error If we are missing dependencies
					//otherwise we would create a struct
					//with missing fields
					try {
						cls = Class.forName(binaryName, false, loader);
						if(cls.getAnnotation(Struct.class) == null) //Should not happen
							throw new CompilerError("error: Struct annotation mismatch.");
					} catch (ClassNotFoundException ex) {
						throw new CompilerError("error: " + ex.toString());
					}
				}
				
//				if(ib.isFromSource()) {


					IVariableBinding[] vars = ib.getDeclaredFields();
					Arrays.sort(vars, ID_COMP); //sort according to source position
					
					for(int i = 0; i < vars.length; i++) {
						IVariableBinding e = vars[i];
						int mods = e.getModifiers();
						if(!Modifier.isStatic(mods)) {
							ITypeBinding type = e.getType();
							

//							Log.err(" TYPE PRA " + e);
//							Log.err("TYPE " + type + ", " + mods);
							FieldEntry field = new FieldEntry(e.getName());
							field.sortOrder = i;
							field.typeb = type;
							
							fields.add(field);
							offsetTable.put(field.name, field);
							
							IAnnotationBinding ref = find(e.getAnnotations(), "theleo.jstruct.Reference");
							if(ref != null) {
								field.setReference();
								refCount++;
							}
							else if(type.isArray()) {
								IAnnotationBinding arrSizeIA = find(e.getAnnotations(), "theleo.jstruct.ArraySize");
								if(arrSizeIA == null) {
									//java object array reference
									field.setSimpleType(FieldType.OBJECT);
//									objCount++;
								}
								else {	
									field.length = (int)getValue(arrSizeIA.getAllMemberValuePairs(), "length");
									
									//For now, 1D arrays are supported
									ITypeBinding arrtype = type.getElementType();
									if(arrtype.isPrimitive()) {
										//primitive array
										field.setSimpleType(FieldType.get(arrtype.getQualifiedName()));
										Log.err("prim");
									}
									else if(!field.setStructType(get(arrtype))) {
										//java object reference array
										//however ArraySize annotation is present
										//throw error
										throw new CompilerError("Annotation ArraySize present on Java reference field: " 
												+ binaryName + "." + field.name);
									}
								}								
							}
							else if(type.isPrimitive()) { //primitive value
								field.setSimpleType(FieldType.get(type.getQualifiedName()));
							}
							else if(!field.setStructType(get(type))) { //java object reference
								field.setSimpleType(FieldType.OBJECT);
//								objCount++;
							}
							
							if(field.isStruct()) {
								if(ref == null) {
									if(field.structType.hasRefs()) refCount += field.structType.refOffsets.length;
									if(field.structType.hasJavaObjects()) objCount += field.structType.objOffsets.length;
								}
							}
							
							
							field.offset = structSize;

														
							if(field.type == FieldType.OBJECT) {
								if(objEntry == null) {
									objEntry = field; objCount++;
								}
								else {
									field.offset = objEntry.offset;
								}
								localObjCount++;
							}
							
							align = Math.max(align, field.align);

							long size = field.size;
							if(size > 0 && !(field.type == FieldType.OBJECT && objEntry != field)) {
								reorder |= (field.offset%field.align) != 0;
								structSize += size;
							}
						}
					}
//				}
//				else {
//					Field[] vars = cls.getDeclaredFields();
//					Log.err(" FFF " + vars.length);
//					for(int i = 0; i < vars.length; i++) {
//						Field v = vars[i];
//						int mods = v.getModifiers();
//						if(!java.lang.reflect.Modifier.isStatic(mods)) {
//							Class type = v.getType();
//							
//							Log.err(" TYPE PRA " + v);
//							
//							FieldType field = new FieldType(v.getName());
//						}
//					}
//				}

				if(cls != null) {
					//check fields again with Java Reflect Api (better safe than sorry)
					Field[] fs = cls.getDeclaredFields();
					
					boolean missingOrder = false;
					ArrayList<FieldNum> tmp = new ArrayList<>();
					for(int i = 0; i < fs.length; i++) {
						Field v = fs[i];
						int mods = v.getModifiers();
						if(!java.lang.reflect.Modifier.isStatic(mods)) {
							Order order = v.getAnnotation(Order.class);
							int id = -1;
							if(order == null) missingOrder = true;
							else id = order.id();
							tmp.add(new FieldNum(id, v));
						}
					}
					
					if(tmp.size() != fields.size())
						throw new CompilerError("Struct " + binaryName + " has " + tmp.size() + " or " + fields.size() + " fields. Try to recompile!");
				
					if(missingOrder) CompilerError.exec(CompilerError.STRUCT_MISSING_ORDER, binaryName);
					else {
						Collections.sort(tmp, FIELD_COMP);
						for(int i = 0; i < fs.length; i++) {
							Field f = tmp.get(i).f;
							if(!f.getName().equals(vars[i].getName()))
								CompilerError.exec(CompilerError.STRUCT_ORDER_MISMATCH, binaryName);
						}
					
					}
				}
						
				structSizeWithoutPadding = structSize;
				
				//reorder fields to correct alignment is necessary
				if(reorder) {
					Log.err("Reorder ");
					Collections.sort(fields, REORDER_COMP);

					for(int i = 0; i < fields.size(); i++)
						fields.get(i).offset = -1;

					objEntry = null;
					int offset = 0;
					structSize = 0;
					for(int i = 0; i < fields.size(); i++) {						
						FieldEntry f = fields.get(i);
						//check if already used to fill padding
						if(f.offset != -1) continue;
						if(f.type == FieldType.OBJECT && objEntry != null) {
							f.offset = objEntry.offset;
							continue;
						}

						long padding = padding(offset, f.align);
						if(padding > 0) {
							//check if padding can be filled
							for(int j = 0; j < fields.size(); j++) {
								if(i == j) continue;
								FieldEntry f2 = fields.get(j);
								if(f2.offset != -1) continue;
								if(f2.size > padding) continue;
								if(f2.size == padding) {
									if(f2.type == FieldType.OBJECT) objEntry = f2;
									f2.offset = offset;
									offset += padding;
									padding = 0;
									break;
								} else {
									long pad2 = padding(offset, f2.align);
									if(pad2 == 0) {
										if(f2.type == FieldType.OBJECT) objEntry = f2;
										f2.offset = offset;
										offset += f2.size;
										padding -= f2.size;
										if(padding == 0) break;
										j = -1;
									}
								}
							}
						}
						if(f.type == FieldType.OBJECT) objEntry = f;
						offset += padding;
						f.offset = offset;
						offset += f.size;
					}
					for(int i = 0; i < fields.size(); i++) {
						FieldEntry f = fields.get(i);
						structSize = Math.max(structSize, f.offset+f.size);
					}

				}
				endPadding = padding(structSize, align);
				structSize += endPadding;
				
				if(objCount != 0) {
					objField = objEntry;
					objOffsets = new int[objCount];
					objCounts = new int[objCount];
					int off = 0;
					fields.clear();
					fields.addAll(offsetTable.values());
					Collections.sort(fields, OFFSET_COMP);
					if(objField != null) {
						objOffsets[off] = objField.offset;
						objCounts[off] = localObjCount;
						off++;
					}
					int objArrayOffset = 0;
					for(int i = 0; i < fields.size(); i++) {
						FieldEntry f = fields.get(i);
						if(f.type == FieldType.OBJECT) {
							f.objOffet = objArrayOffset++;
						}
						else if(f.isStruct() && f.structType.hasJavaObjects()) {
							objOffsets[off] = f.offset+f.structType.objField.offset;
							objCounts[off] = f.structType.globalObjCount;
							off++;
							f.objOffet = objArrayOffset;
							objArrayOffset += f.structType.globalObjCount;
						}
					}
					globalObjCount = 0;
					for(int i = 0; i < objCounts.length; i++) globalObjCount += objCounts[i];
					
					if(Log.enabled) Log.err("ObjOffsets " + Arrays.toString(objOffsets));
					if(Log.enabled) Log.err("ObjCounts " + Arrays.toString(objCounts));
					if(Log.enabled) Log.err("Obj count: " + localObjCount + "/"+globalObjCount);
				}
					
				if(Log.enabled) Log.err(getStructLayout());
				
				if(reorder && !canReorder) {
					throw new CompilerError("Autopadding disabled: Invalid structre! Default layout: " + getStructLayout());
				}
				
				defined = true;
				for(FieldEntry f : fields) {
					if(f.type == FieldType.REFERENCE) {
						if(f.typeb.isArray()) {
							ITypeBinding arrtype = f.typeb.getElementType();
							f.structType = get(arrtype);
						}
						else f.structType = get(f.typeb);
						if(f.structType == null) {
							throw new CompilerError("Reference type not found: " + binaryName + ", " + f.name);
						}
					}
				}
			}
			defined = true;
		}
		public boolean hasJavaObjects() { return objOffsets != null; }
		public boolean hasRefs() { return refOffsets != null; }
		public int structSizeNoEndPadding() { return structSize - endPadding;}
		public String getStructLayout() {
			if(!isStruct) return null;
			if(structLayout != null) return structLayout;
			StringBuilder sb = new StringBuilder();
			
			ArrayList<FieldEntry> fields = new ArrayList<>();
			fields.addAll(offsetTable.values());
			Collections.sort(fields, OFFSET_COMP);
			
			sb.append("~Struct ").append(binaryName).
					append("(").append(structSizeWithoutPadding).append('/').
					append(structSize-endPadding).append('/').
					append(structSize).append(')').append(" Align: ").
					append(align).append(" ~\n");
			for(FieldEntry f : fields) {
				sb.append('|');
				sb.append(f.toString()).append("|\n");
			}
			for(int i = 0; i< 7+binaryName.length(); i++) sb.append('~');
			
			return structLayout = sb.toString();
		}
	}
	
	public static Entry get(Expression type) {
		ITypeBinding ib = type.resolveTypeBinding();
		if(ib == null) {
			if(type instanceof MethodInvocation) {
				IMethodBinding mi = ((MethodInvocation)type).resolveMethodBinding();
				if(mi != null) ib = mi.getReturnType();
			}
		}	
		
		if(ib == null) {
			CompilerError.exec(CompilerError.TYPE_NOT_FOUND, type.toString());
			return null;
		}
		else return get(ib);
	}
	public static Entry get(Name type) {
		IBinding b = type.resolveBinding();		
		if(b == null) {
			CompilerError.exec(CompilerError.TYPE_NOT_FOUND, type.toString());
			return null;
		}
		if(b instanceof IPackageBinding) {
			return null;
		}
		if(b instanceof ITypeBinding) {
			return get((ITypeBinding)b);
		}
		if(b instanceof IVariableBinding) {
			IVariableBinding vb = (IVariableBinding)b;
			return get(vb.getType());		
		}
		Log.err("IGNORING BINFIND " + b + ", " + b.getClass());
		return null;
	}
	public static Entry get(Type type) {
		ITypeBinding ib = type.resolveBinding();
		if(ib == null) {
			CompilerError.exec(CompilerError.TYPE_NOT_FOUND, type.toString());
			return null;
		}
		else return get(ib);
	}
	public static Entry get(TypeDeclaration type) {
		ITypeBinding ib = type.resolveBinding();
		if(ib == null) {
			CompilerError.exec(CompilerError.TYPE_NOT_FOUND, type.toString());
			return null;
		}
		else return get(ib);
	}
	private static Entry get(ITypeBinding ib) {
		if(ib == null) {
			CompilerError.exec(CompilerError.UNCATEGORIZED, "Null binding ");
			return null;
		}
		if(ib.isPrimitive()) return null;		
		return get(ib.getBinaryName(), ib);
	} 
	private static Entry get(String binaryName, ITypeBinding ib) {
		Entry e = map.get(binaryName);
		if(e == null) {
			e = new Entry();
			map.put(binaryName, e);
			e.initialize(ib);
		}
		if(!e.defined) throw new CompilerError("Struct Circular Dependence: " + binaryName);
		return e.isStruct?e:null;
	}
	private static IAnnotationBinding find(IAnnotationBinding[] anno, String qualifiedName) {
		if(anno == null) return null;
		for(int i = 0; i < anno.length; i++)
			if(anno[i].getAnnotationType().getQualifiedName().equals(qualifiedName))
				return anno[i];
		return null;
	}
	private static IMemberValuePairBinding find(IMemberValuePairBinding[] anno, String propertyName) {
		if(anno == null) return null;
		for(int i = 0; i < anno.length; i++)
			if(anno[i].getName().equals(propertyName))
				return anno[i];
		return null;
	}
	private static Object getValue(IMemberValuePairBinding[] anno, String propertyName) {
		if(anno == null) return null;
		for(int i = 0; i < anno.length; i++)
			if(anno[i].getName().equals(propertyName))
				return anno[i].getValue();
		return null;
	}
	public static int padding(int numToRound, int multiple) {
		if(multiple == 0) return 0;
		return multiple - 1 - (numToRound + multiple - 1) % multiple;
	}
	
	
}
