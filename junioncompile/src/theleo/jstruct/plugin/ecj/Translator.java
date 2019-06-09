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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.text.Document;
import theleo.jstruct.StructHeapType;
import theleo.jstruct.plugin.Log;
import theleo.jstruct.plugin.ecj.StructCache.*;
import static theleo.jstruct.plugin.ecj.StructCache.padding;
import theleo.jstruct.plugin.ecj.Translator.StringCache.MethodType;
import theleo.jstruct.plugin.ecj.Translator.StringCache.OpSymbol;

/**
 *
 * @author Juraj Papp
 */
public class Translator extends BaseTranslator {
	public static final String TheLeo = "theleo", jstruct = "jstruct", hidden = "hidden";
	public static final String STRUCT_TYPE_VAR = "$STRUCT_TYPE$";
	public static final String HYB_CLS_OFFSETS = "$theleo_structoffsets$";
	public static final String STACK_OBJ = "$STACK_OBJ$";
	public static final String STACK_BASE = "$STACK_BASE$";
	public static final String STACK_BASE_OBJ = "$STACK_BASE_OBJ$";
	public static final String STACK_BASE_HI = "$STACK_BASE_HI$";
	public static final String STACK_RETURN_ADDRESS = "returnAddress";
//	public static final String LOCAL_R1 = "$theleo_localR1$";
	
	
	
	
	public static final String allocateArray = "allocateArray";
	public static final String allocateArrayHeap = "allocateArrayHeap";
	public static final String allocateArrayDirect = "allocateArrayDirect";
	public static final String allocateArrayDirectBuffer = "allocateArrayDirectBuffer";
	public static final String allocateArrayStack = "allocateArrayStack";

	
//	public static final String Array1 = "R1";
	public static final String[] STACK = {"theleo", "jstruct", "hidden", "Stack"};
//	public static final String[] REF1 = {"theleo", "jstruct", "hidden", Array1};
//	public static final String[] AUTO_ARRAY = {"theleo", "jstruct", "hidden", "AutoArray"};
//	public static final String[] AUTO_HYBRID = {"theleo", "jstruct", "hidden", "AutoHybrid"};
	public static final String[] STRUCT_TYPE = {"theleo", "jstruct", "reflect", "StructType"};
	public static final String[] ARRAY_TYPE = {"theleo", "jstruct", "ArrayType"};
	public static final String[] STRUCT_HEAP_TYPE = {"theleo", "jstruct", "StructHeapType"};
//	public static final String[] MEM0_U = {"theleo", "jstruct", "hidden", "Mem0", "u"};
	public static final String[] MEM = {"theleo", "jstruct", "Mem"};
	public static final String[] MEM0_AA = {"theleo", "jstruct", "hidden", "Mem0", "AA"};
	public static final String[] BOXED_LONG = {"java", "lang", "Long"};
	
	public static final String ANNOTATION_DIRECT = "theleo.jstruct.Direct";
	public static final String ANNOTATION_DIRECT_BUFFER = "theleo.jstruct.DirectBuffer";
	public static final String ANNOTATION_HEAP = "theleo.jstruct.Heap";
	public static final String ANNOTATION_STACK = "theleo.jstruct.Stack";
	
	public static final String TYPEBIND_METHOD = "theleo.jstruct.METHOD";
	public static final String TYPEBIND_METHOD_ARG = "theleo.jstruct.METHOD_ARG";
//	public static final String TYPEBIND_LOCAL_VARIABLE = "theleo.jstruct.LOCAL_VARIABLE";
	public static final String TYPEBIND_FIELD_VARIABLE = "theleo.jstruct.FIELD_VARIABLE";
	public static final String TYPEBIND_FIELD_VARIABLE_NAME = "theleo.jstruct.FIELD_VARIABLE_NAME";

	public static final String TYPEBIND_STRUCT_ARRAY = "theleo.jstruct.STRUCT_ARRAY";

	
//	public static final String TYPEBIND_REF = "theleo.jstruct.REF";
//	public static final String REF_SAFE = "SAFE_REF";
	
	public static final String METHOD_PTR = "theleo.jstruct.Mem.ptr";
	public static final String METHOD_IDX_ARRAY_ACCESS = "theleo.jstruct.XXX.idx.ArrayAccess";
	public static final String METHOD_IDX_SIMPLE_NAME = "theleo.jstruct.XXX.idx.SimpleName";
	public static final String METHOD_IDX_RETURN = "theleo.jstruct.XXX.idx.Return";
	public static final String METHOD_IDX_STRUCT_FIELD = "theleo.jstruct.XXX.idx.StructField";
	public static final String METHOD_ARG_IDX_0 = "theleo.jstruct.Mem0.idx.Arg0";
	public static final String METHOD_ARG_IDX_ASSIGNMENT = "theleo.jstruct.Mem0.idx.Assignment";
	
	public static final String[] StructHeapType_Char = new String[StructHeapType.values().length];
	public static final String[] STACK_DATA_NAME = new String[StructHeapType.values().length];
	public static final String[] R1X_NAME = new String[StructHeapType.values().length];
	public static final String[][] R1X = new String[StructHeapType.values().length][];
	
	public static final String[] ARRAY_RX_NAME = new String[4];
	public static final String[][] ARRAY_RX = new String[4][];

	static {
		String ArrayClassName = "R1";
		StructHeapType[] values = StructHeapType.values();
		for(int i = 0; i < values.length; i++) {
			if(values[i] == StructHeapType.All) {
				StructHeapType_Char[i] = "";
				STACK_DATA_NAME[i] = "$STACK_DATA$";
				R1X_NAME[i] = "theleo.jstruct.hidden."+ArrayClassName;
				R1X[i] = new String[]{"theleo", "jstruct", "hidden", ArrayClassName};
			}
			else {
				StructHeapType_Char[i] = ""+values[i].name().charAt(0);
				STACK_DATA_NAME[i] = "$STACK_DATA$"+values[i].name().charAt(0);
				R1X_NAME[i] = "theleo.jstruct.hidden."+ArrayClassName+"."+values[i].name().charAt(0);
				R1X[i] = new String[]{"theleo", "jstruct", "hidden", ArrayClassName, ""+values[i].name().charAt(0)};
			}
		}
		ARRAY_RX_NAME[0] = "theleo.jstruct.hidden.R1.R2";
		ARRAY_RX_NAME[1] = "theleo.jstruct.hidden.R1.R3";
		ARRAY_RX_NAME[2] = "theleo.jstruct.hidden.R1.R4";
		ARRAY_RX_NAME[3] = "theleo.jstruct.hidden.R1.RN";
		
		ARRAY_RX[0] = new String[]{ "theleo", "jstruct", "hidden", ArrayClassName, "R2"};
		ARRAY_RX[1] = new String[]{ "theleo", "jstruct", "hidden", ArrayClassName, "R3"};
		ARRAY_RX[2] = new String[]{ "theleo", "jstruct", "hidden", ArrayClassName, "R4"};
		ARRAY_RX[3] = new String[]{ "theleo", "jstruct", "hidden", ArrayClassName, "RN"};
	}	
	
	public static String ARRAY_RX_NAME(int dims) {
		if(dims < 2) throw new IllegalArgumentException();
		if(dims > 4) return ARRAY_RX_NAME[3];
		return ARRAY_RX_NAME[dims-2];
	}
	public static String[] ARRAY_RX(int dims) {
		if(dims < 2) throw new IllegalArgumentException();
		if(dims > 4) return ARRAY_RX[3];
		return ARRAY_RX[dims-2];
	}
	
	public class TypeTranslator extends ASTVisitor {
		@Override
		public boolean visit(SimpleType node) {
			Entry e = entry(node);
			if(e != null) {
				replace(node, type(BOXED_LONG));
				return false;
			}
			return true;
		}
	}
//	public class FieldTranslator extends ASTVisitor {
//		@Override
//		public boolean visit(SimpleType node) {
//			return Translator.this.visit(node);
////			Entry e = entry(node);
////			if(e != null) {
////				PrimitiveType type = ast.newPrimitiveType(PrimitiveType.LONG);
////				replace(node, type);
////				return false;
////			}
////			return true;
//		}
//		
//	}
	public class StackAllocInitializerChecker extends ASTVisitor {
		Entry e;
		String varName;
		BitSet b = new BitSet();
		
		public void init(Entry e, String varName) {
			this.e = e;
			this.varName = varName+'.';
			if(e.initCheckBitsNum > b.size()) b = new BitSet(b.size()<<1);
			else b.clear();
		}
		
		public boolean isInitialized() {
			return b.cardinality() == e.initCheckBitsNum;
		}
		
		@Override
		public boolean visit(Assignment node) {
			Expression left = node.getLeftHandSide();
			
			String v = left.toString();
			if(v.startsWith(varName)) {
				String field = v.substring(varName.length());
				
				Long l = e.initCheck.get(field);
				if(l != null) {
					int pos = (int)(l&0xffffffff);
					int rep = (int)(l>>32);
					for(int i = 0; i < rep; i++) b.set(pos+i);
				}			
			}
			
			return true;
		}
	}
	
	
//	HashMap<Object, Object> propertyMap = new HashMap<>();
	
	
	TypeTranslator typeTranslator;
//	FieldTranslator fieldTranslator;
	StackAllocInitializerChecker stackAllocChecker;
	public Translator(CompilationUnit cu, Document doc, String sourceFileName) {
		super(cu, doc, sourceFileName);
		tmpTranslator = new TmpTranslator(cu, doc, sourceFileName);
		typeTranslator = new TypeTranslator();
//		fieldTranslator = new FieldTranslator();
		stackAllocChecker = new StackAllocInitializerChecker();
	}	
	public static boolean debug = false;
	static void err(Object s) {
		if(debug) System.err.println("");
	}
	
	
	
	public void prepend(ASTNode toAdd, ASTNode node) {
		ASTNode parent = node.getParent();
		StructuralPropertyDescriptor desc = node.getLocationInParent();
		if(desc instanceof ChildListPropertyDescriptor) {
			ChildListPropertyDescriptor ch = (ChildListPropertyDescriptor)desc;
			List<ASTNode> list = (List)parent.getStructuralProperty(ch);
			
			int index = list.indexOf(node);
			list.add(index, toAdd);			
		}
		else throw new IllegalArgumentException("Cannot prepend: " + toAdd + ", parent " + parent + ", " + desc);
	}
//	public void append(ASTNode toAdd, ASTNode node) {
//		ASTNode parent = node.getParent();
//		StructuralPropertyDescriptor desc = node.getLocationInParent();
//		if(desc instanceof ChildListPropertyDescriptor) {
//			ChildListPropertyDescriptor ch = (ChildListPropertyDescriptor)desc;
//			List<ASTNode> list = (List)parent.getStructuralProperty(ch);
//			
//			int index = list.indexOf(node);
//			list.add(index+1, toAdd);			
//		}
//		else throw new IllegalArgumentException("Cannot prepend: " + toAdd);
//	}
	
	
	
	
	public Statement parentStatement(ASTNode node) {
		while(node != null)
			if(node instanceof Statement) return (Statement)node;
			else node = node.getParent();
		return null;
	}
	public Statement nextStatement(Statement s) {
		int i = statementIndex(s);
		if(i == -1) return null;
		Block b = (Block)s.getParent();
		List l = b.statements();
		i++;
		if(i < l.size()) return (Statement)l.get(i);
		return null;
	}
	public int statementIndex(Statement s) {
		Block b = (Block)s.getParent();
		List l = b.statements();
		for(int i = 0 ; i < l.size(); i++) {
			if(l.get(i) == s) return i;
		}
		return -1;
	}

	@Override
	public boolean visit(TypeLiteral node) {
		return false;
	}
	
	@Override
	public boolean visit(SimpleType node) {			
		Entry e = entry(node);
		if(e != null) {
//			PrimitiveType type = ast.newPrimitiveType(PrimitiveType.LONG);
			replace(node, type(e, R1X[e.heapType.ordinal()]));
			return false;
		}
		return true;
	}
	
	@Override
	public boolean visit(ParameterizedType node) {
//		node.accept(typeTranslator);
//		return false;
		return true;
	}
	
	@Override
	public boolean visit(ArrayType node) {		
		Entry e = entry(node.getElementType());
		if(e != null) {			
			int dims = node.getDimensions();
//			if(dims > 1) throw new CompilerError("Multidimensional arrays not implemented. " + dims);
//			SimpleType type = (dims > 1) ? type(TheLeo, jstruct, hidden, (e.hasJavaObjects()?"HybN":"RefN"))
//			: type(TheLeo, jstruct, hidden, (e.hasJavaObjects()?"Hyb1":R1X_NAME[e.heapType.ordinal()]));						
			
			//add type of array
			Type type = type(e, R1X[e.heapType.ordinal()]);
			if(dims > 1) {
				ParameterizedType p = ast.newParameterizedType(type(e, ARRAY_RX(dims)));
				p.typeArguments().add(type);
				p.setProperty(TYPEBIND_PROP, e);
				type = p;
			}
			type.setProperty(TYPEBIND_STRUCT_ARRAY, dims);
			
//			SimpleType type = type(theleo, jstruct, hidden, (e.hasJavaObjects()?"Hyb":"Ref")+dims);						
			replace(node, type);			
			return false;
		}
		return false;
	}

	@Override
	public boolean visit(ArrayCreation node) {
		Entry e = entry(node.getType().getElementType());
		if(e != null) {
			List dims = node.dimensions();
//			if(dims.size() > 1) throw new CompilerError("Multidimensional arrays not implemented. " + dims.size());
//			ASTNode expr = copy((ASTNode)dims.get(0));
			
			Expression expr;
			MethodInvocation m = ast.newMethodInvocation();
			expr = m;
			
//			if(e.hasJavaObjects()) {
//				m.setExpression(name(MEM0));
//				m.setName(name("allocHybrid"));
//				List args = m.arguments();
////				args.add(expr);
//				args.add(returnLong(e.structSize));
//				args.add(returnBool(e.zero));
//				args.add(returnLong(e.globalObjCount));
//				
//				FieldAccess fa = ast.newFieldAccess();
//				fa.setExpression(ast.newName(e.qualifiedName));
//				fa.setName(name(HYB_CLS_OFFSETS));
//				args.add(fa);
//				
//				for(int i = 0; i < dims.size(); i++)
//					args.add(copy((ASTNode)dims.get(i)));
////				for(int i = 0; i < e.objOffsets.length; i++) {
////					args.add(returnLong(e.objOffsets[i]));
////					args.add(returnLong(e.objCounts[i]));
////				}
//			}
//			else {
//				m.setExpression(name(MEM0_AA));
				m.setExpression(name(MEM));
				String methodName = allocateArray;
				List args = m.arguments();
				
				ArrayType arrayType = node.getType();
//				Type elementType = arrayType.getElementType();
//				TypeLiteral t = ast.newTypeLiteral(); 
//				t.setType(newType(elementType.resolveBinding()));
//				args.add(t);

				args.add(newEntryType(e));
				
//				FieldAccess fa2 = ast.newFieldAccess();
//				fa2.setExpression(name(STRUCT_HEAP_TYPE));
//				fa2.setName(name(e.heapType.name()));
//				args.add(fa2);
//				args.add(returnInt(e.structSize));
//				args.add(returnInt(e.globalObjCount));
//				if(e.hasJavaObjects()) {
//					FieldAccess fa = ast.newFieldAccess();
//					fa.setExpression(ast.newName(e.qualifiedName));
//					fa.setName(name(HYB_CLS_OFFSETS));
//					args.add(fa);
//				}
//				else {
//					args.add(ast.newNullLiteral());
//				}
				
				ArrayList<IAnnotationBinding> list = tmpAnnotationList;
				getAnnotations(arrayType.resolveBinding(), list);
				for(IAnnotationBinding a : list) {
					String qName = a.getAnnotationType().getQualifiedName();
					if(qName.equals(ANNOTATION_DIRECT)) {
						methodName = allocateArrayDirect;
						boolean zero = true;
						Boolean zeroProp = (Boolean)StructCache.getValue(a.getAllMemberValuePairs(), "zero");
						if(zeroProp != null ) 
							zero = zeroProp;
						args.add(returnBool(zero));
						break;
					}
					else if(qName.equals(ANNOTATION_DIRECT_BUFFER)) {
						methodName = allocateArrayDirectBuffer;
						break;
					}
					else if(qName.equals(ANNOTATION_HEAP)) {
						if(e.heapType == StructHeapType.None) {
							throw new CompilerError("Struct " + e.qualifiedName + " does not allow heap allocations!");
						}
						methodName = allocateArrayHeap;
						theleo.jstruct.ArrayType type = theleo.jstruct.ArrayType.Default;
						IVariableBinding ib = (IVariableBinding)StructCache.getValue(a.getAllMemberValuePairs(), "value");
						if(ib != null) {
							type = theleo.jstruct.ArrayType.valueOf(ib.getName());
						}
						if(type == theleo.jstruct.ArrayType.Default) {
							switch(e.heapType) {
								case All: 
								case AllAsWellTestDoNotUse:
								case Byte: type = theleo.jstruct.ArrayType.Byte; break;
								case Short: type = theleo.jstruct.ArrayType.Short; break;
								case Char: type = theleo.jstruct.ArrayType.Char; break;
								case Int: type = theleo.jstruct.ArrayType.Int; break;
								case Float: type = theleo.jstruct.ArrayType.Float; break;
								case Long: type = theleo.jstruct.ArrayType.Long; break;
								case Double: type = theleo.jstruct.ArrayType.Double; break;
							}
						}
						else if(e.heapType != StructHeapType.All && e.heapType != StructHeapType.AllAsWellTestDoNotUse){
							if(!e.heapType.name().equals(type.name())) {
								throw new CompilerError("Struct " + e.qualifiedName + " does not allow heap allocations of type " + type.name() + "!");
							}
						}
						
						FieldAccess fa = ast.newFieldAccess();
						fa.setExpression(name(ARRAY_TYPE));
						fa.setName(name(type.name()));
						args.add(fa);
						break;
					}
					else if(qName.equals(ANNOTATION_STACK)) {
						methodName = allocateArrayStack;
						MethodFrame mf = getMethod();
						
						if(e.globalObjCount == 0) mf.popStack();
						else mf.popStackObjs();
						args.add(0, name(STACK_OBJ));
//						throw new UnsupportedOperationException("not yet implemented");
						break;
					}
				}
				
				m.setName(name(methodName));
								
				for(int i = 0; i < dims.size(); i++)
					args.add(copy((ASTNode)dims.get(i)));
				
//				CastExpression c = ast.newCastExpression();
//				c.setType(type(R1X[e.heapType.ordinal()]));
//				c.setExpression(m);
//				expr = c;
				
//				m.setName(name("alloc"));
//				List args = m.arguments();
////				args.add(expr);
//				args.add(returnLong(e.structSize));
//				args.add(returnBool(e.zero));
//				for(int i = 0; i < dims.size(); i++)
//					args.add(copy((ASTNode)dims.get(i)));
//			}
			
			replace(node, expr);			
			return false;
		}
		return true;
	}

	

		@Override
	public boolean visit(SimpleName node) {
//		if(REF_SAFE.equals(node.getProperty(TYPEBIND_REF))) return true;
//		Entry e = entry(node);
//		if(e != null) {
//			IBinding ib = node.resolveBinding();
//			if(ib != null) {
//				if(ib.getKind() == IBinding.VARIABLE) {
//					IVariableBinding v = (IVariableBinding)ib;
//					if(v.isField()) {
//						node.setProperty(TYPEBIND_REF, REF_SAFE);
//						Expression exp = (Expression)copy(node);
//						
//						MethodInvocation m = ast.newMethodInvocation();
//						m.setExpression(name(MEM0));
//						m.setName(name("ref"));
//						m.arguments().add(exp);
//						m.setProperty(TYPEBIND_PROP, e);
//						m.setProperty(TYPEBIND_METHOD, METHOD_PTR);
//						replace(node, m);
//						return false;
//
////						System.out.println("field " + node);
////						return true;
//					}
//				}
//			}
//		}
		return true;
	}

	@Override
	public void endVisit(QualifiedName node) {
		if(isReplace(node)) { 
			ASTNode r = getReplace(node);
			if(r instanceof QualifiedName) node = (QualifiedName)r;
			else if(r instanceof FieldAccess) {
				endVisit((FieldAccess)r);
				return;
			}
			else throw new IllegalArgumentException();
		}
		
		Expression expr = node.getQualifier();
		Entry e = entry(expr);
		if(e != null) {	
//			FieldAccess fa = ast.newFieldAccess();
//			fa.setExpression((Expression)copySubtreeIfHasParent(expr));
//			fa.setName((SimpleName)copySubtreeIfHasParent(node.getName()));
//			replace(node, fa);
//			
//			replaceFieldAccess(e, fa, fa.getExpression(), fa.getName().getIdentifier());
			replaceFieldAccess(e, node, expr, node.getName().getIdentifier());
		}
		else {
			e = entry(node);
			if(e != null) replaceFieldAccessJava(e, node, expr, node.getName().getIdentifier(), node.resolveBinding());
		}
	}
	@Override
	public void endVisit(FieldAccess node) {
		if(isReplace(node)) { 
			ASTNode r = getReplace(node);
			if(r instanceof FieldAccess) node = ((FieldAccess)r);
			else throw new IllegalArgumentException();
		}
		
		Expression expr = node.getExpression();
		Entry e = entry(expr);
		if(e != null) {	
			replaceFieldAccess(e, node, expr, node.getName().getIdentifier());
		}
		else {
			e = entry(node);
			if(e != null) replaceFieldAccessJava(e, node, expr, node.getName().getIdentifier(), node.resolveFieldBinding());
		}
	}
	
	
//	@Override
//	public boolean visit(QualifiedName node) {		
//		Expression expr = (Expression)translate(node.getQualifier());
//		Entry e = entry(expr);
//		if(e != null) {			
//			replaceFieldAccess(e, node, expr, node.getName().getIdentifier());
//			return false;
//		}
//		else {
//			e = entry(node);
//			if(e != null) replaceFieldAccessJava(e, node, expr, node.getName().getIdentifier(), node.resolveBinding());
//		}
//		return false;
//	}
//
//	@Override
//	public boolean visit(FieldAccess node) {
//		Expression expr = (Expression)translate(node.getExpression());
//		Entry e = entry(expr);
//		if(e != null) {
//			replaceFieldAccess(e, node, expr, node.getName().getIdentifier());
//			return false;
//		}
//		else {
//			e = entry(node);
//			if(e != null) replaceFieldAccessJava(e, node, expr, node.getName().getIdentifier(), node.resolveFieldBinding());
//		}
//		return false;
//	}
	
	public void replaceFieldAccessJava(Entry e, Expression node, Expression translatedExpr,String identifier, IBinding binding) {
//		if(REF_SAFE.equals(node.getProperty(TYPEBIND_REF))) return;
		if(node.getProperty(TYPEBIND_FIELD_VARIABLE) != null) return;
		
		ASTNode setProp = node;
//		FieldAccess fa = null;
//		if(node instanceof QualifiedName) {
//			fa = ast.newFieldAccess();
//			fa.setExpression((Expression)copySubtreeIfHasParent(translatedExpr));
//			fa.setName(name(identifier));
//			setProp = fa;
//		}
		
		if(binding.getKind() == IBinding.VARIABLE) {
			IVariableBinding v = (IVariableBinding)binding;
			if(v.isField()) {
				setProp.setProperty(TYPEBIND_PROP, e);
				setProp.setProperty(TYPEBIND_FIELD_VARIABLE_NAME, identifier); 
				setProp.setProperty(TYPEBIND_FIELD_VARIABLE, v.getDeclaringClass().getQualifiedName());
				
//				FieldAccess fa = ast.newFieldAccess();
//				fa.setExpression((Expression)copySubtree(translatedExpr));
//				fa.setName(name(identifier));
//
//				MethodInvocation m = ast.newMethodInvocation();
//				m.setExpression(name(MEM0));
//				m.setName(name("ref"));
//				m.arguments().add(fa);
//				m.setProperty(TYPEBIND_PROP, e);
//				m.setProperty(TYPEBIND_METHOD, METHOD_PTR);
//				replace(node, m);
			}	
		}
		
//		if(fa != null) replace(node, fa);
	}
	
	

	@Override
	public boolean visit(PrefixExpression node) {
		return super.visit(node); 
	}

	@Override
	public void endVisit(PrefixExpression node) {
		super.endVisit(node);
		//Prefix expressions have been translated
		//If prefix on a structure's value
		
		Expression op = node.getOperand();
		if(op instanceof MethodInvocation) {
			MethodInvocation m = (MethodInvocation)op;
			
			MethodTmp tmp3 = getMethodTmp(m);
			if(tmp3 != null) {
				replaceMethodTmp(m, (MethodInvocation)m, tmp3, MethodType.get, null, Assignment.Operator.ASSIGN);
				m = (MethodInvocation)node.getOperand();
			}
			
			switch(node.getOperator().toString()) {
				case "++":
					m.setName(name(m.getName()+"PrI"));
					break;
				case "--":
					m.setName(name(m.getName()+"PrD"));
					break;
				case "+":
				case "-":
				case "~":
				case "!":
					//No action needed for these operators
					return;
				default:
					throw new CompilerError("Unknown prefix operator: " + node.getOperator().toString());
			}
//			m.setExpression(name(MEM0));

			replace(node, ASTNode.copySubtree(ast,m));
		}
	}
	
	

	@Override
	public boolean visit(PostfixExpression node) {
		boolean ret = super.visit(node);
		return ret;
	}

	@Override
	public void endVisit(PostfixExpression node) {
		super.endVisit(node); 
		
		//Postfix expressions have been translated
		//If postfix on a structure's value
		
		Expression op = node.getOperand();
		if(op instanceof MethodInvocation) {
			MethodInvocation m = (MethodInvocation)op;
			
			MethodTmp tmp3 = getMethodTmp(m);
			if(tmp3 != null) {
				replaceMethodTmp(m, (MethodInvocation)m, tmp3, MethodType.get, null, Assignment.Operator.ASSIGN);
				m = (MethodInvocation)node.getOperand();
			}
			
			//there are only two postfix operators
			switch(node.getOperator().toString()) {
				case "++":
					m.setName(name(m.getName()+"PoI"));
					break;
				case "--":
					m.setName(name(m.getName()+"PoD"));
					break;
				default:
					throw new CompilerError("Unknown postfix operator: " + node.getOperator().toString());
			}
//			m.setExpression(name(MEM0));

			replace(node, ASTNode.copySubtree(ast,m));
		}
	}
	
	

	@Override
	public boolean visit(ExpressionStatement node) {
		return super.visit(node); 
	}

	@Override
	public void endVisit(ExpressionStatement node) {
		Expression e = trimCast(node.getExpression());
		if(e != node.getExpression())
			node.setExpression(e);
	}

	
	
	protected MethodInvocation copyObject(Entry e, Expression rhs, Expression lhs) {
		MethodInvocation m = ast.newMethodInvocation();
		m.setExpression(name(MEM0));

		if(e.hasJavaObjects()) {
			m.setName(name("copyHybrid"));
			m.setProperty(TYPEBIND_PROP, e);

			List list = m.arguments();
			list.add(rhs);
			list.add(lhs);
			list.add(ast.newNumberLiteral(""+e.structSizeNoEndPadding()));
			for(int i = 0; i < e.objOffsets.length; i++) {
				list.add(returnLong(e.objOffsets[i]));
				list.add(returnLong(e.objCounts[i]));
			}
		}
		else {
			m.setName(name("copyMemory"));
			m.setProperty(TYPEBIND_PROP, e);

			List list = m.arguments();
			list.add(rhs);
			list.add(lhs);
			list.add(ast.newNumberLiteral(""+e.structSizeNoEndPadding()));
		}
		return m;
	}

	@Override
	public boolean visit(MethodInvocation node) {	
		IMethodBinding m = node.resolveMethodBinding();
		
		if(m != null) {
			ITypeBinding cls = m.getDeclaringClass();
			if(cls != null) {
				String binName = cls.getBinaryName();
				if(binName.equals("theleo.jstruct.Mem")) {
					Entry e; int ival;
					ASTNode n;
					String metName = m.getName();
					switch(metName) {
						case "alignment":
							e = typeLiteral(node.arguments().get(0));
							ival = e == null?-1:e.align;

							replace(node, returnInt(ival));
							return false;
						case "endPadding":
							e = typeLiteral(node.arguments().get(0));
							ival = e == null?-1:e.endPadding;

							replace(node, returnInt(ival));
							return false;
						case "isNull":
							Expression expr = (Expression)translate((ASTNode)node.arguments().get(0));
							replaceMethodCallArguments(node.arguments());
							expr = (Expression)(node.arguments().get(0));

							expr = trimWrap(expr);
//							if(expr instanceof SimpleName) {
								InfixExpression ii = ast.newInfixExpression();
//								ii.setLeftOperand(name(((SimpleName) expr).getIdentifier()+STRUCT_DATA));
								ii.setLeftOperand((Expression) copySubtreeIfHasParent(expr));
								ii.setRightOperand(ast.newNullLiteral());
								ii.setOperator(InfixExpression.Operator.EQUALS);
								ii.setProperty(TYPEBIND_PROP, FieldType.BOOLEAN);
								replace(node, wrap(ii));
//							}
//							else {
//								Methodtm
//							}
//							if(n.getProperty(TYPEBIND_METHOD) == METHOD_PTR) {
//								MethodInvocation mi = (MethodInvocation)n;
//								ASTNode nn = (ASTNode)mi.arguments().get(0);
//								
//								MethodInvocation m0 = ast.newMethodInvocation();
//								m0.setExpression(name(MEM0));
//								m0.setName(name("isNull"));
//								m0.setProperty(TYPEBIND_PROP, FieldType.BOOLEAN);
//								m0.arguments().add(copySubtree(nn));
//								replace(node, m0);
//							}
							return false;
						case "len":
//							e = typeLiteral(node.arguments().get(0));
							Expression lenExpr = (Expression)copy(trimWrap((Expression)node.arguments().get(1)));
							
							if(lenExpr instanceof NumberLiteral) {
								int num = Integer.parseInt(((NumberLiteral)lenExpr).getToken());
								if(num >= 0 && num <= 3) {
									num++;
									FieldAccess fa = ast.newFieldAccess();
									fa.setExpression((Expression)copy((Expression)node.arguments().get(0)));
									fa.setName(name(num == 1?"length":("length"+num)));
									fa.setProperty(TYPEBIND_PROP, FieldType.LONG);
									replace(node, fa);
									return false;
								}
							}
							
							MethodInvocation mi2 = ast.newMethodInvocation();
							mi2.setExpression((Expression)copy((Expression)node.arguments().get(0)));
							mi2.setName(name("getLength"));
							mi2.arguments().add(lenExpr);
							mi2.setProperty(TYPEBIND_PROP, FieldType.LONG);
							
							replace(node, mi2);
							return false;
						case "li":
							n = copy((Expression)node.arguments().get(0));
							n.setProperty(TYPEBIND_PROP, FieldType.LONG);
							
							replace(node, n);
//							break;
							return false;
						case "sizeOf":
							e = typeLiteral(node.arguments().get(0));
							ival = e == null?-1:e.structSize;

							replace(node, returnInt(ival));
							return false;
						case "sizeOfData":
							e = typeLiteral(node.arguments().get(0));
							ival = e == null?-1:e.structSizeWithoutPadding;

							replace(node, returnInt(ival));
							return false;
						case "stackRaw":
						case "stack0":
						case "stackInit":
							e = typeLiteral(node.arguments().get(0));
							MethodFrame mf = getMethod();
							NumberLiteral pos = (NumberLiteral)returnLong(0);
							
							if(metName.equals("stackInit")) {
								Statement s = parentStatement(node);
								Statement next = nextStatement(s);
								if(next instanceof Block) {
									Block init = (Block)next;

									VariableDeclarationFragment frag = (VariableDeclarationFragment)node.getParent();
									stackAllocChecker.init(e, frag.getName().getIdentifier());

									init.accept(stackAllocChecker);

									if(!stackAllocChecker.isInitialized()) {
										CompilerError.exec(CompilerError.STACK_VARIABLE_NOT_INITIALIZED, s.toString());
									}
								}
								else CompilerError.exec(CompilerError.STACK_VARIABLE_NOT_INITIALIZED, s.toString());
							}
							
							FieldAccess fa2 = ast.newFieldAccess();
							fa2.setExpression(name(STACK_DATA_NAME[e.heapType.ordinal()]));
							fa2.setName(name("base"));
							
							InfixExpression op = ast.newInfixExpression();
							op.setLeftOperand(name(STACK_BASE));
							op.setOperator(InfixExpression.Operator.PLUS);
							op.setRightOperand(pos);
							op.setProperty(TYPEBIND_PROP, FieldType.LONG);
							
							InfixExpression op3 = ast.newInfixExpression();
							op3.setLeftOperand(fa2);
							op3.setOperator(InfixExpression.Operator.PLUS);
							op3.setRightOperand(op);
							
							Expression addressExpr;
							
							if(e.hasJavaObjects()) {
								NumberLiteral posObj = (NumberLiteral)returnLong(0);
								
								mf.add(e, pos, posObj);
								
								InfixExpression op2 = ast.newInfixExpression();
								op2.setLeftOperand(name(STACK_BASE_OBJ));
								op2.setOperator(InfixExpression.Operator.PLUS);
								op2.setRightOperand(posObj);
								op2.setProperty(TYPEBIND_PROP, FieldType.LONG);
								
								MethodInvocation mi = ast.newMethodInvocation();
								mi.setExpression(name(MEM0));
								mi.setName(name("allocHybOnStack"));							
								mi.setProperty(TYPEBIND_PROP, FieldType.LONG);
								List args = mi.arguments();
								args.add(op3);
								args.add(op2);
//								args.add(name(STACK_BASE_HI));
								
								
								FieldAccess fa = ast.newFieldAccess();
								fa.setExpression(newEntryType(e));
								fa.setName(name("objectOffset"));
								args.add(fa);
								
								
//								for(int i = 0; i < e.objOffsets.length; i++) {
//									args.add(returnLong(e.objOffsets[i]));
//									args.add(returnLong(e.objCounts[i]));
//								}

								addressExpr = mi;
								
//								replace(node, mi);
							}
							else {
								mf.add(e, pos);
								addressExpr = op3;
//								replace(node, op);
							}
							
							
							replace(node, methodTmpStackVar(name(STACK_DATA_NAME[e.heapType.ordinal()]), addressExpr, e));
							
							return false;
						case "layoutString":
							e = typeLiteral(node.arguments().get(0));
							
							replace(node, e == null? ast.newNullLiteral(): returnString(e.getStructLayout()));
							return false;
					}
				}
				else if(binName.equals("theleo.jstruct.hidden.Mem0")) {
					Entry e; 
					ASTNode n;
					String metName = m.getName();
					switch(metName) {
						case "as":
							Object prop = ((Expression)node.arguments().get(0)).getProperty(TYPEBIND_PROP);
							n = copy((Expression)node.arguments().get(0));
							if(prop == null)
								n.setProperty(TYPEBIND_PROP, entry((Expression)node.arguments().get(0)));
							else n.setProperty(TYPEBIND_PROP,prop);
							
							replace(node, n);
							return false;
						case "asLong":
							n = copy((Expression)node.arguments().get(0));
							n.setProperty(TYPEBIND_PROP, FieldType.LONG);
							replace(node, n);
							return false;	
						case "stackRaw":
							e = typeLiteral(node.arguments().get(0));
							MethodFrame mf = getMethod();
							NumberLiteral pos = (NumberLiteral)returnLong(0);
												
							InfixExpression op = ast.newInfixExpression();
							op.setLeftOperand(name(STACK_BASE));
							op.setOperator(InfixExpression.Operator.PLUS);
							op.setRightOperand(pos);
							op.setProperty(TYPEBIND_PROP, FieldType.LONG);
							
							if(e.hasJavaObjects()) {
								NumberLiteral posObj = (NumberLiteral)returnLong(0);
								
								mf.add(e, pos, posObj);
								
								InfixExpression op2 = ast.newInfixExpression();
								op2.setLeftOperand(name(STACK_BASE_OBJ));
								op2.setOperator(InfixExpression.Operator.PLUS);
								op2.setRightOperand(posObj);
								op2.setProperty(TYPEBIND_PROP, FieldType.LONG);
								
								MethodInvocation mi = ast.newMethodInvocation();
								mi.setExpression(name(MEM0));
								mi.setName(name("allocHybOnStack"));							
								mi.setProperty(TYPEBIND_PROP, FieldType.LONG);
								List args = mi.arguments();
								args.add(op);
								args.add(op2);
								args.add(name(STACK_BASE_HI));
								FieldAccess fa = ast.newFieldAccess();
								fa.setExpression(ast.newName(e.qualifiedName));
								fa.setName(name(HYB_CLS_OFFSETS));
								args.add(fa);
//								for(int i = 0; i < e.objOffsets.length; i++) {
//									args.add(returnLong(e.objOffsets[i]));
//									args.add(returnLong(e.objCounts[i]));
//								}
								
								replace(node, mi);
							}
							else {
								mf.add(e, pos);

								

								replace(node, op);
							}
							return false;
						case "getHybOffsets":
							TypeLiteral typeLit = (TypeLiteral)node.arguments().get(0);
							e = entry(typeLit.getType());
							if(e == null || !e.hasJavaObjects()) {
								replace(node, ast.newNullLiteral());
							}
							else {
								Type type = typeLit.getType();	
								String qualName = type.resolveBinding().getQualifiedName();
								FieldAccess fa = ast.newFieldAccess();
								fa.setExpression(ast.newName(qualName));
								fa.setName(name(HYB_CLS_OFFSETS));
								replace(node, fa);
							}
							return false;
					}
				}
			}
		}
				
//		if(m != null) {
//			if(!(node.getParent() instanceof ExpressionStatement) &&
//					node.getProperty(TYPEBIND_METHOD_ARG) == null) {
//				Entry e = entry(m.getReturnType());
//				if(e != null) {
//					MethodFrame mf = getMethod();
//					if(!(node.getParent() instanceof ReturnStatement)) {
//						mf.useStack();
//					}
//					
////					SimpleName s = (SimpleName)expr;
////					MethodInvocation m = ast.newMethodInvocation();
////					List args = m.arguments();
////					m.setExpression(name(MEM0));
////					m.setName(name("tmpptr"));
////
////					args.add(name(s.getIdentifier()+STRUCT_DATA));
////					args.add(name(s.getIdentifier()));
////					args.add(returnInt(f.offset));
////
////					m.setProperty(TYPEBIND_PROP, f.type);
////					m.setProperty(TYPEBIND_METHOD_TMP, new MethodTmp(f));
////
////					replace(node, m);	
//
//					Log.err("  return stack " + node  + ", " + node.hashCode());
//
//					MethodInvocation mm = ast.newMethodInvocation();
//					mm.setExpression(name(MEM0));
//					mm.setName(name("tmpstack"));
////					
//					FieldAccess fa = ast.newFieldAccess();
//					fa.setExpression(name(STACK_OBJ));
//					fa.setName(name(STACK_RETURN_ADDRESS));
//					fa.setProperty(TYPEBIND_PROP, FieldType.LONG);
//					
//					node.setProperty(TYPEBIND_METHOD_ARG, METHOD_ARG_IDX_0);
//					
//					List args = mm.arguments();			
//					args.add(copy(node));
//					args.add(fa);
//					args.add(returnInt(0));
//					
//					mm.setProperty(TYPEBIND_PROP, e);
//					mm.setProperty(TYPEBIND_METHOD_TMP, new MethodTmp(e));
//					mm.setProperty(TYPEBIND_METHOD, METHOD_IDX_RETURN);
//					replace(node, mm);	
//					
//					Log.err("  return stack " + mm);
//					
//
////					mm.setProperty(TYPEBIND_PROP, e);
////					mm.setProperty(TYPEBIND_METHOD, METHOD_IDX_RETURN);
////					replace(node, mm);	
//
////					MethodInvocation mm = ast.newMethodInvocation();
////					mm.setExpression(name(MEM0));
//////					mm.setName(name("idx"));
////					mm.setName(name("tmpstack"));
////					
////					FieldAccess fa = ast.newFieldAccess();
////					fa.setExpression(name(STACK_OBJ));
////					fa.setName(name(STACK_RETURN_ADDRESS));
////					fa.setProperty(TYPEBIND_PROP, FieldType.LONG);
////					
////					node.setProperty(TYPEBIND_METHOD_ARG, METHOD_ARG_IDX_0);
////					
////					List args = mm.arguments();			
////					args.add(copy(node));
////					args.add(fa);
////
////					mm.setProperty(TYPEBIND_PROP, e);
////					mm.setProperty(TYPEBIND_METHOD, METHOD_IDX_RETURN);
////					replace(node, mm);	
//					return false;
//				}
//			}
//			
//		}		
		return true;
	}	

	
	@Override
	public void endVisit(MethodInvocation node) {
		if(isReplace(node)) return;
		replaceMethodCallArguments(node.arguments());
		IMethodBinding m = node.resolveMethodBinding();
		if(m != null) {
			if(!(node.getParent() instanceof ExpressionStatement) &&
					node.getProperty(TYPEBIND_METHOD_ARG) == null) {
				Entry e = entry(m.getReturnType());
				if(e != null) {
					MethodFrame mf = getMethod();
					if(!(node.getParent() instanceof ReturnStatement)) {
						mf.useStack();
					}
					
//					SimpleName s = (SimpleName)expr;
//					MethodInvocation m = ast.newMethodInvocation();
//					List args = m.arguments();
//					m.setExpression(name(MEM0));
//					m.setName(name("tmpptr"));
//
//					args.add(name(s.getIdentifier()+STRUCT_DATA));
//					args.add(name(s.getIdentifier()));
//					args.add(returnInt(f.offset));
//
//					m.setProperty(TYPEBIND_PROP, f.type);
//					m.setProperty(TYPEBIND_METHOD_TMP, new MethodTmp(f));
//
//					replace(node, m);	


					MethodInvocation mm = ast.newMethodInvocation();
					mm.setExpression(name(MEM0));
					mm.setName(name("tmpstack"));
//					
					FieldAccess fa = ast.newFieldAccess();
					fa.setExpression(name(STACK_OBJ));
					fa.setName(name(STACK_RETURN_ADDRESS));
					fa.setProperty(TYPEBIND_PROP, FieldType.LONG);
					
					node.setProperty(TYPEBIND_METHOD_ARG, METHOD_ARG_IDX_0);
					
					List args = mm.arguments();			
					args.add(copySubtree(node));
//					args.add(copy(node));
					args.add(fa);
					args.add(returnInt(0));
					
					mm.setProperty(TYPEBIND_PROP, e);
					mm.setProperty(TYPEBIND_METHOD_TMP, new MethodTmp(e));
					mm.setProperty(TYPEBIND_METHOD, METHOD_IDX_RETURN);
					replace(node, mm);	
					
					

//					mm.setProperty(TYPEBIND_PROP, e);
//					mm.setProperty(TYPEBIND_METHOD, METHOD_IDX_RETURN);
//					replace(node, mm);	

//					MethodInvocation mm = ast.newMethodInvocation();
//					mm.setExpression(name(MEM0));
////					mm.setName(name("idx"));
//					mm.setName(name("tmpstack"));
//					
//					FieldAccess fa = ast.newFieldAccess();
//					fa.setExpression(name(STACK_OBJ));
//					fa.setName(name(STACK_RETURN_ADDRESS));
//					fa.setProperty(TYPEBIND_PROP, FieldType.LONG);
//					
//					node.setProperty(TYPEBIND_METHOD_ARG, METHOD_ARG_IDX_0);
//					
//					List args = mm.arguments();			
//					args.add(copy(node));
//					args.add(fa);
//
//					mm.setProperty(TYPEBIND_PROP, e);
//					mm.setProperty(TYPEBIND_METHOD, METHOD_IDX_RETURN);
//					replace(node, mm);	
					return;
				}
			}
			
		}		
	}

	@Override
	public void endVisit(ConstructorInvocation node) {
		replaceMethodCallArguments(node.arguments());
	}

	@Override
	public void endVisit(ClassInstanceCreation node) {
		replaceMethodCallArguments(node.arguments());
	}

	@Override
	public void endVisit(SuperConstructorInvocation node) {
		replaceMethodCallArguments(node.arguments());
	}
	@Override
	public void endVisit(SuperMethodInvocation node) {
		replaceMethodCallArguments(node.arguments());
	}

	
	
	
	@Override
	public void preVisit(ASTNode node) {
		super.preVisit(node); 
	}
	//@theleo.jstruct.hidden.Order(id=5) int i ;
	
	@Override
	public boolean visit(TypeDeclaration node) {
		Entry e = entry(node);
		if(e != null) {					
			List l = node.bodyDeclarations();
			outer:
			for(int i = 0; i < l.size(); i++) {
				ASTNode n = (ASTNode)l.get(i);
				if(n instanceof FieldDeclaration) {
					FieldDeclaration field = (FieldDeclaration)n;
					List mods =  field.modifiers();
					for(int j = 0; j < mods.size(); j++) {
						IExtendedModifier mod = (IExtendedModifier)mods.get(j);
						if(mod.isModifier()) {
							Modifier m = (Modifier)mod;
							if(m.isStatic()) {
								field.accept(this);
								continue outer;
							}
						}
					}
//					FieldType ft = e.offsetTable.get(field.);
//					NormalAnnotation anno = ast.newNormalAnnotation();
//					anno.setProperty("id", e.);
					
//					mods.add(e)
					
//					err("mods " + mods);
				}
				else ((ASTNode)l.get(i)).accept(this);
			}
							
			ClassInstanceCreation c = ast.newClassInstanceCreation();
			c.setType(type(STRUCT_TYPE));

			TypeLiteral type = ast.newTypeLiteral();
			type.setType(typeQualified(e.qualifiedName));

			List args = c.arguments();
			args.add(type);

			FieldAccess fa2 = ast.newFieldAccess();
			fa2.setExpression(name(STRUCT_HEAP_TYPE));
			fa2.setName(name(e.heapType.name()));
			args.add(fa2);
			args.add(returnInt(e.structSize));
			args.add(returnInt(e.align));
			args.add(returnInt(e.globalObjCount));
			
			if(e.hasJavaObjects()) {
				ArrayInitializer arrInit = ast.newArrayInitializer();
				List nums = arrInit.expressions();
				
				for(int i = 0; i < e.objOffsets.length; i++) {
					nums.add(returnInt(e.objOffsets[i]));
					nums.add(returnInt(e.objCounts[i]));
				}
				
				ArrayCreation arr =	ast.newArrayCreation();
				Type intType = ast.newPrimitiveType(PrimitiveType.INT);
				intType.setProperty(TYPEBIND_PROP, FieldType.INT);
				
				arr.setType(ast.newArrayType(intType, 1));
				arr.setInitializer(arrInit);
				arr.setProperty(TYPEBIND_PROP, FieldType.OBJECT);
				args.add(arr);			
			}
			else args.add(ast.newNullLiteral());
			
			VariableDeclarationFragment var = ast.newVariableDeclarationFragment();
			var.setName(name(STRUCT_TYPE_VAR));
			var.setInitializer(c);	
			
			FieldDeclaration dec = ast.newFieldDeclaration(var);
			List mods = dec.modifiers();
			mods.add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
			mods.add(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
			mods.add(ast.newModifier(Modifier.ModifierKeyword.FINAL_KEYWORD));
			dec.setType(type(STRUCT_TYPE));
			l.add(dec);

			return false;
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(ReturnStatement node) {
		if(node.getExpression() == null) return super.visit(node);
		Entry e = entry(node.getExpression());
		if(e != null) {
			MethodFrame mf = getMethod();
			
			
			Expression rhs = node.getExpression();
			
			if(rhs instanceof SimpleName) {
				
//				String varName = sn.getIdentifier();
//				String varName2 = varName+STRUCT_DATA;

				String rhsName = ((SimpleName)rhs).getIdentifier();
				String rhsName2 = rhsName+STRUCT_DATA;

//				node.setLeftHandSide(name(varName2));
				Assignment a = ast.newAssignment();
				
				FieldAccess fa = ast.newFieldAccess();
				fa.setExpression(name(STACK_OBJ));
				fa.setName(name(STACK_RETURN_ADDRESS));
				fa.setProperty(TYPEBIND_PROP, FieldType.LONG);
				
				a.setLeftHandSide(fa);

				MethodInvocation m = ast.newMethodInvocation();
				m.setExpression(name(MEM0));
				m.setName(name("idxR"));

				a.setRightHandSide(name(rhsName));
				m.arguments().add(name(rhsName2));
				m.arguments().add(a);

				m.setProperty(TYPEBIND_PROP, e);
				m.setProperty(TYPEBIND_METHOD, METHOD_IDX_SIMPLE_NAME);

				a.setProperty(TYPEBIND_PROP, e);
				node.setExpression(m);
				
				mf.useStack();
				return false;
			}
			else {
//				Log.err("ASSIGNMENT 5  3" );
////				String varName = sn.getIdentifier();
////				String varName2 = varName+STRUCT_DATA;
//
//				node.setLeftHandSide(name(varName2));
				Expression ex = (Expression)copy(rhs);
				if(ex.getProperty(TYPEBIND_METHOD) == METHOD_IDX_ARRAY_ACCESS) {
					Assignment a = ast.newAssignment();
				
					FieldAccess fa = ast.newFieldAccess();
					fa.setExpression(name(STACK_OBJ));
					fa.setName(name(STACK_RETURN_ADDRESS));
					fa.setProperty(TYPEBIND_PROP, FieldType.LONG);

					a.setLeftHandSide(fa);
					
					
					MethodInvocation mi = (MethodInvocation)ex;
					a.setRightHandSide((Expression)copy((ASTNode)mi.arguments().get(1)));
					mi.arguments().set(1, a);
					
					a.setProperty(TYPEBIND_PROP, e);
					node.setExpression(ex);
				}
				else if(ex.getProperty(TYPEBIND_METHOD) == METHOD_IDX_RETURN) {
					return true;
				}
				else {
					throw new IllegalArgumentException("not yet implemented " + node);
				}				
				
//					node.setRightHandSide((Expression)copy(rhs));
				mf.useStack();
				return false;
			}
			
		}
		return super.visit(node);
	}

	@Override
	public void endVisit(VariableDeclarationStatement node) {
		Entry e = entry(node.getType());
		if(e != null && node.getType().getProperty(TYPEBIND_STRUCT_ARRAY) == null) {
			replaceDeclaration(e, node, node.fragments(), node.modifiers(), true);
		}
	}
	@Override
	public void endVisit(FieldDeclaration node) {
		Entry e = entry(node.getType());
		if(e != null && node.getType().getProperty(TYPEBIND_STRUCT_ARRAY) == null) {
			replaceDeclaration(e, node, node.fragments(), node.modifiers(), false);
		}
	}
	
	public static class StackAllocReq {
		public int index;
		public int offset = 0;
		public Entry type;
		public NumberLiteral num;
		
		public NumberLiteral posObj;
		public StackAllocReq(int index, Entry type, NumberLiteral num) {
			this.index = index;
			this.type = type;
			this.num = num;
		}

		public StackAllocReq(int index, Entry type, NumberLiteral num, NumberLiteral posObj) {
			this.index = index;
			this.type = type;
			this.num = num;
			this.posObj = posObj;
		}
		
	}
	public static class MethodFrame {
		public static final String LOCAL_PREFIX = "$theleo_local$";
		private static final ArrayList<StackAllocReq> empty = new ArrayList<>(0);
		boolean useStack = false;
		boolean popStack = false;
		boolean popStackObjs = false;
//		boolean useLocalR1 = false;
		HashMap<String, Object> localTmp = null;
		ArrayList<StackAllocReq> stackAlloc;
		public void add(Entry e, NumberLiteral nm) {
			if(stackAlloc == null) stackAlloc = new ArrayList<>();
			stackAlloc.add(new StackAllocReq(stackAlloc.size(), e, nm));
		}
		public void add(Entry e, NumberLiteral nm,NumberLiteral posObj) {
			if(stackAlloc == null) stackAlloc = new ArrayList<>();
			stackAlloc.add(new StackAllocReq(stackAlloc.size(), e, nm, posObj));
		}
		public void useStack() { useStack = true; }
		public void popStack() { popStack = true; }
		public void popStackObjs() { popStack = true; popStackObjs = true; }
		public String useLocalR1(Entry e) {
			return useLocal(R1X_NAME[e.heapType.ordinal()]);
		}
		public String useLocal(Entry e) {
			return useLocal(R1X_NAME[e.heapType.ordinal()]);
		}
		public String useLocal(Object qualType) {
			if(localTmp == null) localTmp = new HashMap<>(8);
//			String name = localTmp.get(qualType);
//			if(name == null) {
				String name = LOCAL_PREFIX+localTmp.size();
				localTmp.put(name, qualType);
//			}

			return name;
		}
		public ArrayList<StackAllocReq> getStackAllocReq() {
			return stackAlloc == null?empty:stackAlloc;
		}
	}
	
	
	
	ArrayList<MethodFrame> methodStack = new ArrayList<>();
	public MethodFrame getMethod() {
		return methodStack.get(methodStack.size()-1);
	}

	
	@Override
	public boolean visit(MethodDeclaration node) {
		boolean copy = false;
		List parameters = node.parameters();
		for(int i = 0; i < parameters.size(); i++) {
			SingleVariableDeclaration s = (SingleVariableDeclaration) parameters.get(i);
			Entry e = entry(s.getType());
			if(e != null) {
				if(!copy) {
					copy = true;
					MethodDeclaration m = (MethodDeclaration)ASTNode.copySubtree(ast, node);
					ThrowStatement t = ast.newThrowStatement();
					ClassInstanceCreation c = ast.newClassInstanceCreation();
					c.setType(typeQualified("theleo.jstruct.exceptions.CompileException"));
					t.setExpression(c);
					Block b = ast.newBlock();
					b.statements().add(t);
					m.setBody(b);
					prepend(m, node);
				}
				SingleVariableDeclaration s2 = ast.newSingleVariableDeclaration();
				s2.setName(name(s.getName().getIdentifier()+STRUCT_DATA));
				s2.setType(type(e, R1X[e.heapType.ordinal()]));
				s.setType(ast.newPrimitiveType(PrimitiveType.LONG));
				parameters.add(i, s2);
				i++;
			}
		}
		
//		IMethodBinding methodBinding = node.resolveBinding();
//		
//		if(methodBinding != null) {
//			IAnnotationBinding[] ab = methodBinding.getAnnotations();
//			if(ab != null) {
//				for(int i = 0; i < ab.length; i++) {
//					IAnnotationBinding aa = ab[i];
//					String aName = aa.getAnnotationType().getQualifiedName();
//					if(aName.equals("theleo.jstruct.ReturnStruct")) {
//						PrimitiveType type = ast.newPrimitiveType(PrimitiveType.LONG);
//						node.setReturnType2(type);
//					}
//				}
//			}
//		}
		
		methodStack.add(new MethodFrame());
		return true;
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		MethodFrame mFrame = methodStack.remove(methodStack.size()-1);
//		if(mFrame.useLocalR1) {
//			List statements = node.getBody().statements();
//						
//			VariableDeclarationFragment fragObj = ast.newVariableDeclarationFragment();
//			fragObj.setName(name(LOCAL_R1));
//			
//			VariableDeclarationStatement localR1 = ast.newVariableDeclarationStatement(fragObj);
//			localR1.setType(type(R1X[e.heapType.ordinal()]));
//			
//			statements.add(0, localR1);
//		}
		if(mFrame.localTmp != null) {
			List statements = node.getBody().statements();
			
			for(Map.Entry<String, Object> e : mFrame.localTmp.entrySet()) {
				VariableDeclarationFragment fragObj = ast.newVariableDeclarationFragment();
				fragObj.setName(name(e.getKey()));

				VariableDeclarationStatement localR1 = ast.newVariableDeclarationStatement(fragObj);
				Object type = e.getValue();
				if(type instanceof String)
					localR1.setType(typeQualified((String)type));
				else if(type instanceof Type)
					localR1.setType((Type)type);
				else throw new IllegalArgumentException();

				statements.add(0, localR1);
			}
		}
		if(mFrame.stackAlloc != null || mFrame.popStack) {	
			int stackDataBits = 0;
			boolean reorder = false;
			int stackSize = 0;
			ArrayList<StackAllocReq> fields = mFrame.getStackAllocReq();
			int stackSizeObj = 0;
			for(int i = 0; i < fields.size(); i++) {
				StackAllocReq r = fields.get(i);
				Entry e = r.type;
				stackDataBits |= (1<<e.heapType.ordinal());
				if(e.hasJavaObjects()) {
					stackSizeObj += e.objOffsets.length;
				}
				
				r.offset = stackSize;
				if(e.align != 0) {
					reorder |= (r.offset%e.align) != 0;
				}
				stackSize += e.structSizeNoEndPadding();
			}
			
			if(reorder) {
				Collections.sort(fields, REORDER_COMP);

				for(int i = 0; i < fields.size(); i++)
					fields.get(i).offset = -1;

//				objEntry = null;
				int offset = 0;
				stackSize = 0;
				for(int i = 0; i < fields.size(); i++) {						
					StackAllocReq f = fields.get(i);
					//check if already used to fill padding
					if(f.offset != -1) continue;
//					if(f.type == FieldType.OBJECT && objEntry != null) {
//						f.offset = objEntry.offset;
//						continue;
//					}

					long padding = padding(offset, f.type.align);
					if(padding > 0) {
						//check if padding can be filled
						for(int j = 0; j < fields.size(); j++) {
							if(i == j) continue;
							StackAllocReq f2 = fields.get(j);
							if(f2.offset != -1) continue;
							if(f2.type.structSizeNoEndPadding() > padding) continue;
							if(f2.type.structSizeNoEndPadding() == padding) {
//								if(f2.type == FieldType.OBJECT) objEntry = f2;
								f2.offset = offset;
								offset += padding;
								padding = 0;
								break;
							} else {
								long pad2 = padding(offset, f2.type.align);
								if(pad2 == 0) {
//									if(f2.type == FieldType.OBJECT) objEntry = f2;
									f2.offset = offset;
									offset += f2.type.structSizeNoEndPadding();
									padding -= f2.type.structSizeNoEndPadding();
									if(padding == 0) break;
									j = -1;
								}
							}
						}
					}
//					if(f.type == FieldType.OBJECT) objEntry = f;
					offset += padding;
					f.offset = offset;
					offset += f.type.structSizeNoEndPadding();
				}
				for(int i = 0; i < fields.size(); i++) {
					StackAllocReq f = fields.get(i);
					stackSize = Math.max(stackSize, f.offset+f.type.structSizeNoEndPadding());
				}
			}
		
			long endPadding = padding(stackSize, 8);
			stackSize += endPadding;
			
			for(int i = 0; i < fields.size(); i++) {
				StackAllocReq r = fields.get(i);
				r.num.setToken(""+r.offset);
			}
			
			if(stackSizeObj != 0) {
				int objs = 0;
				Collections.sort(fields, OFFSET_COMP);
				for(int i = 0; i < fields.size(); i++) {
					StackAllocReq r = fields.get(i);
					if(r.type.hasJavaObjects()) {
						r.posObj.setToken(""+objs);
						objs += r.type.objOffsets.length;
					}
				}	
			}
			
		
			MethodInvocation stack = ast.newMethodInvocation();
			stack.setExpression(name(MEM0));
			stack.setName(name("stack"));
			stack.setProperty(TYPEBIND_PROP, FieldType.LONG);
			
			MethodInvocation get = ast.newMethodInvocation();
			get.setExpression(name(STACK_OBJ));
			get.setName(name("get"));
			get.arguments().add(returnInt(stackSize));
			get.setProperty(TYPEBIND_PROP, FieldType.LONG);
			
			MethodInvocation pop = ast.newMethodInvocation();
			pop.setExpression(name(STACK_OBJ));
			pop.setName(name("pop"));
			pop.arguments().add(name(STACK_BASE));
			if(stackSizeObj != 0) pop.arguments().add(name(STACK_BASE_OBJ));
			pop.setProperty(TYPEBIND_PROP, FieldType.LONG);
			
			VariableDeclarationFragment fragObj = ast.newVariableDeclarationFragment();
			fragObj.setName(name(STACK_OBJ));
			fragObj.setInitializer(stack);
			
			VariableDeclarationStatement stackObj = ast.newVariableDeclarationStatement(fragObj);
			stackObj.setType(type(STACK));
			
			
			
			VariableDeclarationFragment fragBase = ast.newVariableDeclarationFragment();
			fragBase.setName(name(STACK_BASE));
			fragBase.setInitializer(get);
			
			VariableDeclarationStatement stackBase = ast.newVariableDeclarationStatement(fragBase);
			stackBase.setType(ast.newPrimitiveType(PrimitiveType.LONG));
			
			Block finallyBlock = ast.newBlock();
			finallyBlock.statements().add(ast.newExpressionStatement(pop));
			
			TryStatement t = ast.newTryStatement();
			t.setBody((Block)copySubtree(node.getBody()));
			t.setFinally(finallyBlock);
			
			Block newbody = ast.newBlock();
			List statements = newbody.statements();
			statements.add(stackObj);
			
			for(int i = 0; i < STACK_DATA_NAME.length; i++) {
				if((stackDataBits & (1<<i)) != 0) {
					FieldAccess stackData1 = ast.newFieldAccess();
					stackData1.setExpression(name(STACK_OBJ));
					stackData1.setName(name("data"+StructHeapType_Char[i]));

					VariableDeclarationFragment fragObjData = ast.newVariableDeclarationFragment();
					fragObjData.setName(name(STACK_DATA_NAME[i]));
					fragObjData.setInitializer(stackData1);

					VariableDeclarationStatement stackData = ast.newVariableDeclarationStatement(fragObjData);
					stackData.setType(type(R1X[i]));

					statements.add(stackData);
				}
			}
			
			statements.add(stackBase);
			
			if(stackSizeObj != 0 || mFrame.popStackObjs) {
				get.arguments().add(returnInt(stackSizeObj));
				
				FieldAccess getObj = ast.newFieldAccess();
				getObj.setExpression(name(STACK_OBJ));
				getObj.setName(name("returnObjIndex"));
				getObj.setProperty(TYPEBIND_PROP, FieldType.INT);
				
				VariableDeclarationFragment fragBaseObj = ast.newVariableDeclarationFragment();
				fragBaseObj.setName(name(STACK_BASE_OBJ));
				fragBaseObj.setInitializer(getObj);
			
				VariableDeclarationStatement stackBaseObj = ast.newVariableDeclarationStatement(fragBaseObj);
				stackBaseObj.setType(ast.newPrimitiveType(PrimitiveType.INT));
				statements.add(stackBaseObj);
				
//				MethodInvocation getObj = ast.newMethodInvocation();
//				getObj.setExpression(name(STACK_OBJ));
//				getObj.setName(name("getObj"));
//				getObj.arguments().add(returnInt(stackSizeObj));
//				getObj.setProperty(TYPEBIND_PROP, FieldType.INT);
//				
//				VariableDeclarationFragment fragBaseObj = ast.newVariableDeclarationFragment();
//				fragBaseObj.setName(name(STACK_BASE_OBJ));
//				fragBaseObj.setInitializer(getObj);
//			
//				VariableDeclarationStatement stackBaseObj = ast.newVariableDeclarationStatement(fragBaseObj);
//				stackBaseObj.setType(ast.newPrimitiveType(PrimitiveType.INT));
//				statements.add(stackBaseObj);
				
//				FieldAccess fa = ast.newFieldAccess();
//				fa.setExpression(name(STACK_OBJ));
//				fa.setName(name("hybridIndex"));
//				
//				VariableDeclarationFragment fragBaseObj2 = ast.newVariableDeclarationFragment();
//				fragBaseObj2.setName(name(STACK_BASE_HI));
//				fragBaseObj2.setInitializer(fa);
			
//				VariableDeclarationStatement stackBaseObj2 = ast.newVariableDeclarationStatement(fragBaseObj2);
//				stackBaseObj2.setType(ast.newPrimitiveType(PrimitiveType.INT));
//				statements.add(stackBaseObj2);
			}
			
			statements.add(t);
//			node.setBody(newbody);
			MethodDeclaration newDecl = (MethodDeclaration)copySubtree(node);
			newDecl.setBody(newbody);
			replace(node, newDecl);
		}
		else if(mFrame.useStack) {
			List statements = node.getBody().statements();
			
			MethodInvocation stack = ast.newMethodInvocation();
			stack.setExpression(name(MEM0));
			stack.setName(name("stack"));
			stack.setProperty(TYPEBIND_PROP, FieldType.LONG);
			
			VariableDeclarationFragment fragObj = ast.newVariableDeclarationFragment();
			fragObj.setName(name(STACK_OBJ));
			fragObj.setInitializer(stack);
			
			VariableDeclarationStatement stackObj = ast.newVariableDeclarationStatement(fragObj);
			stackObj.setType(type(STACK));
			
			statements.add(0, stackObj);
		}
	}

	@Override
	public boolean visit(LineComment node) {
		return super.visit(node);
		
	}
	
	

//	int compUnitCounter;
//	@Override
//	public boolean visit(CompilationUnit node) {
//		node.imports();
//		compUnitCounter++;
//		System.out.println("COMP UNIT " + compUnitCounter);
//		
//		
//		return super.visit(node);
//	}
	
	
	
	@Override
	public void endVisit(CompilationUnit node) {
		node.accept(tmpTranslator);
	}

	@Override
	public boolean visit(ImportDeclaration node) {
		//No need to visit import declaration
		
		return false;
	}
		
	public static String cls(Object o) {
		if(o == null) return null;
		return o.getClass().toString();
	}
	
	
	
	
	private static final Comparator<StackAllocReq> OFFSET_COMP =
				new Comparator<StackAllocReq>() {
			@Override
			public int compare(StackAllocReq o1, StackAllocReq o2) {
				return o1.offset - o2.offset;
			}
		};
	private static final Comparator<StackAllocReq> REORDER_COMP =
				new Comparator<StackAllocReq>() {
			@Override
			public int compare(StackAllocReq o1, StackAllocReq o2) {
				if(o1.type.align == o2.type.align) {
					return o1.index - o2.index;
				}
				return (o2.type.align - o1.type.align);
			}
		};
	
	
	public static class StringCache {
		public static int OpSymbolSize = OpSymbol.values().length;
		public static int TypeSymbolSize = TypeSymbol.values().length;
		public static enum OpSymbol {
			Add, Sub, Mul, Div, Mod,
			And, Or, Xor,
			LL, RR, RRR,
			PrI, PrD, PoI, PoD;
		}
		
		public static enum MethodType {
			get(0), put(10);

			int pos;

			private MethodType(int pos) {
				this.pos = pos;
			}
		}
		public static String[] STRING_CACHE = {
			"gZ","gB","gS","gC","gI","gF","gJ","gD","gL","null","pZ","pB","pS","pC","pI","pF","pJ","pD","pL","null","pZAdd","pZSub","pZMul","pZDiv","pZMod","pZAnd","pZOr","pZXor","pZLL","pZRR","pZRRR","pZPrI","pZPrD","pZPoI","pZPoD","pBAdd","pBSub","pBMul","pBDiv","pBMod","pBAnd","pBOr","pBXor","pBLL","pBRR","pBRRR","pBPrI","pBPrD","pBPoI","pBPoD","pSAdd","pSSub","pSMul","pSDiv","pSMod","pSAnd","pSOr","pSXor","pSLL","pSRR","pSRRR","pSPrI","pSPrD","pSPoI","pSPoD","pCAdd","pCSub","pCMul","pCDiv","pCMod","pCAnd","pCOr","pCXor","pCLL","pCRR","pCRRR","pCPrI","pCPrD","pCPoI","pCPoD","pIAdd","pISub","pIMul","pIDiv","pIMod","pIAnd","pIOr","pIXor","pILL","pIRR","pIRRR","pIPrI","pIPrD","pIPoI","pIPoD","pFAdd","pFSub","pFMul","pFDiv","pFMod","pFAnd","pFOr","pFXor","pFLL","pFRR","pFRRR","pFPrI","pFPrD","pFPoI","pFPoD","pJAdd","pJSub","pJMul","pJDiv","pJMod","pJAnd","pJOr","pJXor","pJLL","pJRR","pJRRR","pJPrI","pJPrD","pJPoI","pJPoD","pDAdd","pDSub","pDMul","pDDiv","pDMod","pDAnd","pDOr","pDXor","pDLL","pDRR","pDRRR","pDPrI","pDPrD","pDPoI","pDPoD"
		};
		public static String getString(MethodType m, TypeSymbol s) {
			return STRING_CACHE[m.pos+s.ordinal()];
		}
		public static String getString(MethodType m, TypeSymbol s, OpSymbol o) {
			if(o == null) return getString(m, s);
			return getString(s, o);
		}
		public static String getString(TypeSymbol s, OpSymbol o) {
			return STRING_CACHE[MethodType.put.pos*2+s.ordinal()*OpSymbolSize+o.ordinal()];
		}
	}
	public static ArrayList<IAnnotationBinding> tmpAnnotationList = new ArrayList<>();
	public static void getAnnotations(ITypeBinding b, ArrayList<IAnnotationBinding> l) {
		l.clear();
		while(true) {
			IAnnotationBinding[] arr = b.getTypeAnnotations();
			if(arr != null) 
				for(int i = 0; i < arr.length; i++) l.add(arr[i]);
			if(b.isArray()) b = b.getComponentType();
			else break;
		}
	}
	public SimpleName getNameQualOrField(Expression fa) {
		fa = trimWrap(fa);
		if(fa instanceof FieldAccess)
			return ((FieldAccess)fa).getName();
		else if(fa instanceof QualifiedName)
			return ((QualifiedName)fa).getName();
		else throw new CompilerError("");
	}
	public Expression getExprQualOrField(Expression fa) {
		fa = trimWrap(fa);
		if(fa instanceof FieldAccess)
			return ((FieldAccess)fa).getExpression();
		else if(fa instanceof QualifiedName)
			return ((QualifiedName)fa).getQualifier();
		else throw new CompilerError("" + fa);
	}
	public void setNameQualOrField(Expression fa, String n) {
		fa = trimWrap(fa);
		if(fa instanceof FieldAccess)
			setName((FieldAccess)fa, n);
		else if(fa instanceof QualifiedName)
			setName((QualifiedName)fa, n);
		else throw new CompilerError("");
	}
	public void setName(QualifiedName fa, String n) {
		fa.setName(name(n));
	}
	public void setName(FieldAccess fa, String n) {
		fa.setName(name(n));
	}
	
	public Expression trimCast(Expression expr) {
		Expression e = expr;
		while(e instanceof ParenthesizedExpression) {
			e = ((ParenthesizedExpression)e).getExpression();
		}
		if(e instanceof CastExpression) 
			return (Expression)ASTNode.copySubtree(ast,((CastExpression)e).getExpression());
		return expr;
	}
	
	
	
	
	
	
	
	
	
	public class TmpTranslator extends BaseTranslator {
		public TmpTranslator(CompilationUnit ast, Document doc, String sourceFileName) {
			super(ast, doc, sourceFileName);
		}
		@Override
		public void endVisit(MethodInvocation node) {
			MethodTmp tmp = getMethodTmp(node);
			if(tmp != null) replaceMethodTmp(node, (MethodInvocation)node, tmp, MethodType.get, null, Assignment.Operator.ASSIGN);
		}
	}
	
	
	
	
	@Override
	public boolean visit(ArrayAccess node) {
		Entry e = entry(node);
		if(e != null) {
			Expression name = (node.getArray());
			
			List args = new ArrayList(4);
			args.add(copy(node.getIndex()));
			
			while(name instanceof ArrayAccess) {
				ArrayAccess aa = (ArrayAccess)name;
				name = aa.getArray();
				args.add(0,copy(aa.getIndex()));
			}
			boolean isLocalVar = false;
			if(name instanceof SimpleName) {
				SimpleName s = (SimpleName)name;
				IBinding b = s.resolveBinding();
				if(b instanceof IVariableBinding) {
					if(!((IVariableBinding) b).isField()) {
						isLocalVar = true;
//						m.setProperty(TYPEBIND_LOCAL_VARIABLE, true);
					}
				}
			}
			MethodInvocation m;
			if(isLocalVar) {
				m = methodTmpArrayIndex((Expression)copy(name),
						(SimpleName)copy(name), e, args);
			}
			else {
				MethodFrame mf = getMethod();
				String LOCAL_R1 = mf.useLocalR1(e);
				
				Assignment a = ast.newAssignment();
				a.setLeftHandSide(name(LOCAL_R1));
				a.setRightHandSide((Expression)copy(name));
				
				m = methodTmpArrayIndex(a,
						name(LOCAL_R1), e, args);
			}
//			m.arguments().addAll(args);
			
			replace(node, m);	
			
					
			
			

			return false;
		}
		return true;
	}
	
	public void replaceOffset(Entry e, ASTNode node, MethodTmp tmp, Expression n, StructCache.FieldEntry f, int o) {
						
		MethodInvocation mi = (MethodInvocation)n;

		if(f.isReference()) {
			if(f.isSibling) {
				MethodFrame mf = getMethod();
				String varNameData = mf.useLocal(e);
				String varName = mf.useLocal(ast.newPrimitiveType(PrimitiveType.LONG));
				
				Assignment aData = ast.newAssignment();
				aData.setLeftHandSide(name(varNameData));
				aData.setRightHandSide(tmp.getArrayReference(mi));
				
				Assignment a = ast.newAssignment();
				a.setLeftHandSide(name(varName));
				a.setRightHandSide(methodB("gJ", null,
							name(varNameData),
							addOffset(tmp.getLongAddress(mi), f.offset)));

				MethodInvocation idx = ast.newMethodInvocation();
				idx.setExpression(name(MEM0));
				idx.setName(name("idxRef0"));
				idx.arguments().add(aData);
				idx.arguments().add(a);

				MethodInvocation m = methodTmpRef(
					idx,
					name(varName),
//							name(s.getIdentifier()+STRUCT_DATA),
//							methodB("gJ", null,
//									name(s.getIdentifier()+STRUCT_DATA),
//									addOffset(name(s.getIdentifier()), f.offset)),
					e, f, null, null);
				m.arguments().add(tmp.getArrayReference(mi));
				m.arguments().add(tmp.getLongAddress(mi));
				m.arguments().add(returnInt(f.offset));

				replace(node, m);	
			}
			else {
//				Object o = gL(r,l, i);
//				long addr = gJ(r, l+add);

				MethodFrame mf = getMethod();
				String varNameData = mf.useLocal(e);		
				String varName = mf.useLocal(ast.newPrimitiveType(PrimitiveType.LONG));
				
				Assignment aData = ast.newAssignment();
				aData.setLeftHandSide(name(varNameData));
				aData.setRightHandSide(tmp.getArrayReference(mi));
				
				Assignment a = ast.newAssignment();
				a.setLeftHandSide(name(varName));
				a.setRightHandSide(tmp.getLongAddress(mi));
				
				MethodInvocation m = methodTmpRef(
					methodB("gL", type(R1X[e.heapType.ordinal()]),
							aData,
							addOffset(a, f.refData.offset),
							returnInt(f.refData.objOffset)),

					methodB("gJ", null,
							name(varNameData),
							addOffset(name(varName), f.offset)),
					e, f, varNameData, varName);
				m.arguments().add(tmp.getArrayReference(mi));
				m.arguments().add(tmp.getLongAddress(mi));
				m.arguments().add(returnInt(f.offset));

				replace(node, m);	
			}
		}
		else {
			tmp.f = f;

			if(tmp.type == TypebindMethodTmp.Array) {
				tmp.type = TypebindMethodTmp.Array_Ptr;

//				m.setName(name("arrptr"));
			}
			tmp.offset += o;
			mi.arguments().set(2, returnInt(tmp.offset));
			n.setProperty(TYPEBIND_PROP, (f.isStruct()||f.isReference())?f.structType:f.type);
			replace(node,copySubtreeIfHasParent(n));
		}
	}
	
	public void replaceFieldAccess(Entry e, Expression node, Expression expr, String identifier) {
		
		FieldEntry f = e.offsetTable.get(identifier);
				
		

		if(f == null) {
			CompilerError.exec(CompilerError.STRUCT_FIELD_NOT_FOUND, node.toString());
			return;
		}
		
		expr = trimWrap(expr);
		
		MethodTmp tmp = getMethodTmp(expr);
		if(tmp != null) {
			replaceOffset(e, node, tmp, expr, f, f.offset);
			
//			switch(tmp.type) {
//				case Array:
//					
//					break;
//				case Array_Ptr:
//
//					break;
//				case Ptr:
//
//					break;
//			}
		}
		else {
			if(expr instanceof SimpleName) {
				SimpleName s = (SimpleName)expr;
				
				if(f.isReference()) {
					if(f.isSibling) {
						MethodFrame mf = getMethod();
						String varName = mf.useLocal(ast.newPrimitiveType(PrimitiveType.LONG));
												
						Assignment a = ast.newAssignment();
						a.setLeftHandSide(name(varName));
						a.setRightHandSide(methodB("gJ", null,
									name(s.getIdentifier()+STRUCT_DATA),
									addOffset(name(s.getIdentifier()), f.offset)));
						
						MethodInvocation idx = ast.newMethodInvocation();
						idx.setExpression(name(MEM0));
						idx.setName(name("idxRef0"));
						idx.arguments().add(name(s.getIdentifier()+STRUCT_DATA));
						idx.arguments().add(a);
						
						MethodInvocation m = methodTmpRef(
							idx,
							name(varName),
//							name(s.getIdentifier()+STRUCT_DATA),
//							methodB("gJ", null,
//									name(s.getIdentifier()+STRUCT_DATA),
//									addOffset(name(s.getIdentifier()), f.offset)),
							e, f, null, null);
						m.arguments().add(name(s.getIdentifier()+STRUCT_DATA));
						m.arguments().add(name(s.getIdentifier()));
						m.arguments().add(returnInt(f.offset));
						
						replace(node, m);	
					}
					else {
//						Object o = gL(r,l, i);
//						long addr = gJ(r, l+add);

						MethodInvocation m = methodTmpRef(
							methodB("gL", type(R1X[e.heapType.ordinal()]),
									name(s.getIdentifier()+STRUCT_DATA),
									addOffset(name(s.getIdentifier()), f.refData.offset),
									returnInt(f.refData.objOffset)),

							methodB("gJ", null,
									name(s.getIdentifier()+STRUCT_DATA),
									addOffset(name(s.getIdentifier()), f.offset)),
							e, f, null, null);
						m.arguments().add(name(s.getIdentifier()+STRUCT_DATA));
						m.arguments().add(name(s.getIdentifier()));
						m.arguments().add(returnInt(f.offset));
						
						replace(node, m);	
					}
				}
				else {
					MethodInvocation m = ast.newMethodInvocation();
					List args = m.arguments();
					m.setExpression(name(MEM0));
					m.setName(name("tmpptr"));

					args.add(name(s.getIdentifier()+STRUCT_DATA));
						args.add(name(s.getIdentifier()));
						args.add(returnInt(f.offset));

					m.setProperty(TYPEBIND_PROP, f.type);
					m.setProperty(TYPEBIND_METHOD_TMP, new MethodTmp(f));

					replace(node, m);	
				}				
			}
			else {
				String qType = (String)expr.getProperty(TYPEBIND_FIELD_VARIABLE);
				String fieldName = (String)expr.getProperty(TYPEBIND_FIELD_VARIABLE_NAME);

				MethodFrame mf = getMethod();
				String varName = mf.useLocal(qType);				
				
				Assignment a = ast.newAssignment();
				a.setLeftHandSide(name(varName));
				a.setRightHandSide((Expression)copySubtree(getExprQualOrField(expr)));
				
//				a.setRightHandSide((Expression)ASTNode.copySubtree(ast,(Expression)copySubtree(getExprQualOrField(expr))));
					
				FieldAccess fa2 = ast.newFieldAccess();
				fa2.setExpression(wrap(a));
				fa2.setName(name(fieldName+STRUCT_DATA));
										
				FieldAccess fa = ast.newFieldAccess();
				fa.setExpression(name(varName));
				fa.setName(name(fieldName));
				
				MethodInvocation m = ast.newMethodInvocation();
				List args = m.arguments();
				m.setExpression(name(MEM0));
				m.setName(name("tmpptr"));

				args.add(fa2);
				args.add(fa);
				args.add(returnInt(f.offset));
				
				m.setProperty(TYPEBIND_PROP, f.type);
				m.setProperty(TYPEBIND_METHOD_TMP, new MethodTmp(f));
			
				replace(node, m);	
			}
//			else throw new IllegalArgumentException();
		}
		
	}

	

	@Override
	public void endVisit(Assignment node) {
		Entry e = entry(node.getLeftHandSide());
		replaceAssignment(e, node, node.getLeftHandSide(), node.getRightHandSide(),
				node.getOperator());
	}
	
	
	public void replaceAssignment(Entry e, Expression node,
		Expression left, Expression right, Assignment.Operator op) {
	
		
//		if(e == null) {
//			MethodTmp tmpL = getMethodTmp(left);
//			if(tmpL != null && (tmpL.f != null)) e = tmpL.f.structType;
//		}
		
		if(e != null) {
			MethodTmp tmpR = getMethodTmp(right);
			
			if(tmpR != null) {
				MethodInvocation miR = (MethodInvocation)right;
				
				
				if(left instanceof SimpleName) {
					SimpleName s = (SimpleName)left;
					Assignment a = ast.newAssignment();
					a.setLeftHandSide(name(s.getIdentifier()+STRUCT_DATA));		
					
					MethodInvocation mIndex = ast.newMethodInvocation();
					a.setRightHandSide((Expression)copySubtree(miR.arguments().get(0)));
					switch(tmpR.type) {
						case Array:
						case Array_Ptr: {
								mIndex.setExpression(wrap(a));
								mIndex.setName(name(getIndexMethodName(miR)));
								tmpR.fillIndexMethodArgs(miR, mIndex.arguments());
								
								replace(right, tmpR.addLongAddressOffset(mIndex));
							}
							return;
						case Ptr:
						case Ref:
						case StackVar: {
								mIndex.setExpression(name(MEM0));
								mIndex.setName(name("idx"));
								mIndex.arguments().add(a);

								if(tmpR.type == TypebindMethodTmp.Ptr) {
									mIndex.arguments().add(tmpR.getLongAddress(miR));
								}
								else {
									ASTNode arg1 = (ASTNode)miR.arguments().get(1);
									miR.arguments().set(1, ast.newNullLiteral());
									mIndex.arguments().add(arg1);
								}

								if(node != null) node.setProperty(TYPEBIND_METHOD, METHOD_ARG_IDX_ASSIGNMENT);

								replace(right, mIndex);
							}
							return;
//						case Ref: {
//								System.out.println("");
//								
//							}
//							break;
						default:
							throw new IllegalArgumentException();
					}
				}
				else {
					MethodTmp tmpL = getMethodTmp(left);
					if(tmpL != null) {
						MethodInvocation miL = (MethodInvocation)left;
						
						Entry eL = tmpL.getEntryResult();
						Entry eR = tmpR.getEntryResult();

						if(eL != null && eR != null) {
							if(tmpL.type == TypebindMethodTmp.Ref) {
								MethodInvocation p = ast.newMethodInvocation();
								p.setExpression(name(MEM0_B));
								if(tmpL.f.isSibling) {
//									t = TypeSymbol.LONG;
//									ftype = FieldType.LONG;
//									p.setName(name("pJ"));
									throw new IllegalArgumentException();
								}
								else {
									Expression mi = methodB("pR", null, 
											getRefArg(miL, 3),
											getRefArg(miL, 4),
											getRefArg(miL, 5),
											returnInt(tmpL.f.refData.objOffset),
											tmpR.getArrayReference(miR),
											tmpR.getLongAddress(miR)
									);
									if(tmpL.varData != null) {
										MethodFrame mf = getMethod();
										mf.localTmp.remove(tmpL.var);
										mf.localTmp.remove(tmpL.varData);
										tmpL.var = null;
										tmpL.varData = null;
									}
									replace(node, mi);
									return;
									
									
//									p.setName(name("pR"));
//									p.arguments().add(tmpL.getArrayReference(miL));
//									p.arguments().add(tmpL.getLongAddressBase(miL));
//									p.arguments().add(tmpL.getLongAddressOffset(miL));
//									p.arguments().add(returnInt(tmpL.f.refData.objOffset));
//
//									if(right instanceof SimpleName) {
//										SimpleName s = (SimpleName)putValue;
//										p.arguments().add(name(s.getIdentifier()+STRUCT_DATA));
//										p.arguments().add(name(s.getIdentifier()));
//									}
//									else {
//										p.arguments().add(tmpR.getArrayReference(miR));
//										p.arguments().add(tmpR.getLongAddress(miR));
//									}
//									replace(node, p);
//									return;
								}
							}
							else if(eL == eR) {										
								MethodInvocation mRight = (MethodInvocation)right;

								MethodInvocation mi = ast.newMethodInvocation();
								mi.setExpression(name(MEM0));
								mi.setName(name("copy"));

								List args = mi.arguments();

								args.add(tmpR.getArrayReference(mRight));
								args.add(tmpR.getLongAddress(mRight));
								args.add(tmpL.getArrayReference(miL));
								args.add(tmpL.getLongAddress(miL));
								args.add(newEntryType(eR));
								args.add(returnLong(1));

								replace(node, mi);
							}
							else {
								CompilerError.exec(CompilerError.STRUCT_COPY_TYPE_MISMATCH, eR.qualifiedName, eL.qualifiedName);
							}

						}
						else throw new IllegalArgumentException();
					}
					else throw new IllegalArgumentException();
				}			
			}
			else {
				if(left instanceof SimpleName) {
					SimpleName s = (SimpleName)left;
							
					Assignment a = ast.newAssignment();
					a.setLeftHandSide(name(s.getIdentifier()+STRUCT_DATA));	
					
					if(right instanceof SimpleName) {
						SimpleName s2 = (SimpleName)right;
						a.setRightHandSide(name(s2.getIdentifier()+STRUCT_DATA));

						MethodInvocation m = ast.newMethodInvocation();
						m.setExpression(name(MEM0));
						m.setName(name("idx"));

						a.setRightHandSide(name(s2.getIdentifier()+STRUCT_DATA));
						m.arguments().add(a);
						m.arguments().add(name(s2.getIdentifier()));

						m.setProperty(TYPEBIND_PROP, e);
						m.setProperty(TYPEBIND_METHOD, METHOD_IDX_SIMPLE_NAME);

						a.setProperty(TYPEBIND_PROP, e);
						
						if(node != null) node.setProperty(TYPEBIND_METHOD, METHOD_ARG_IDX_ASSIGNMENT);
						
						replace(right, m);
					}
					else if(right.getProperty(TYPEBIND_METHOD) == METHOD_ARG_IDX_ASSIGNMENT) {
						Assignment aa = (Assignment)right;						
						while(aa.getRightHandSide() instanceof Assignment) 
							aa = (Assignment)aa.getRightHandSide();
						MethodInvocation mm = (MethodInvocation)aa.getRightHandSide();
						a.setRightHandSide((Expression)copySubtree(mm.arguments().get(0)));
						mm.arguments().set(0, a);
						if(node != null) node.setProperty(TYPEBIND_METHOD, METHOD_ARG_IDX_ASSIGNMENT);
					}
					else throw new IllegalArgumentException();
				}
				else if(left instanceof FieldAccess || left instanceof QualifiedName) {
//					getExprQualOrField(left);
//					getNameQualOrField(left);
					
					String qType = (String)left.getProperty(TYPEBIND_FIELD_VARIABLE);
					String fieldName = (String)left.getProperty(TYPEBIND_FIELD_VARIABLE_NAME);

					MethodFrame mf = getMethod();
					String varName = mf.useLocal(qType);				

					Assignment a = ast.newAssignment();
					a.setLeftHandSide(name(varName));
					a.setRightHandSide((Expression)copySubtree(getExprQualOrField(left)));

					FieldAccess fa3 = ast.newFieldAccess();
					fa3.setExpression(wrap(a));
					fa3.setName(name(fieldName));

					replace(left, fa3);
					
					if(right instanceof SimpleName) {
						SimpleName s2 = (SimpleName)right;
						
						FieldAccess fa2 = ast.newFieldAccess();
						fa2.setExpression(name(varName));
						fa2.setName(name(fieldName+STRUCT_DATA));
						
						Assignment a2 = ast.newAssignment();
						a2.setLeftHandSide(fa2);
						a2.setRightHandSide(name(s2.getIdentifier()+STRUCT_DATA));


						MethodInvocation m = ast.newMethodInvocation();
						m.setExpression(name(MEM0));
						m.setName(name("idx"));

						m.arguments().add(a2);
						m.arguments().add(name(s2.getIdentifier()));

						m.setProperty(TYPEBIND_PROP, e);
						m.setProperty(TYPEBIND_METHOD, METHOD_IDX_STRUCT_FIELD);
						a2.setProperty(TYPEBIND_PROP, e);
						
						if(node != null) node.setProperty(TYPEBIND_METHOD, METHOD_ARG_IDX_ASSIGNMENT);

						replace(right, m);
					}
					else throw new IllegalArgumentException();
				
				}
				else {
					MethodTmp tmpL = getMethodTmp(left);
					
					if(tmpL != null) {
						MethodInvocation mi = (MethodInvocation)left;
						replaceMethodTmp(node, mi, tmpL, MethodType.put, right, op);
						
//						Entry eL = tmpL.getEntryResult();
//						Entry eR = entry(right);
//						if(eL != null && eR != null) {
//							if(eL == eR) {
//								if(right instanceof SimpleName) {
//									MethodInvocation mLeft = (MethodInvocation)left;
//									SimpleName s = (SimpleName)right;
//
//									MethodInvocation mi = ast.newMethodInvocation();
//									mi.setExpression(name(MEM0));
//									mi.setName(name("copy"));
//
//									List args = mi.arguments();
//
//									args.add(name(s.getIdentifier()+STRUCT_DATA));
//									args.add(name(s.getIdentifier()));
//									args.add(tmpL.getArrayReference(mLeft));
//									args.add(tmpL.getLongAddress(mLeft));
//									args.add(newEntryType(eR));
//									args.add(returnLong(1));
//
//									replace(node, mi);
//								}
//								else throw new IllegalArgumentException();
//							}
//							else {
//								CompilerError.exec(CompilerError.STRUCT_COPY_TYPE_MISMATCH, eR.qualifiedName, eL.qualifiedName);
//							}
//						}
//						else throw new IllegalArgumentException();
					}
					else throw new IllegalArgumentException();
				}
			}
		}
		else {		
			MethodTmp tmp = getMethodTmp(left);


			if(tmp != null) {

				MethodInvocation mi = (MethodInvocation)left;
				replaceMethodTmp(node, mi, tmp, MethodType.put, right, op);
			}
		}
	}
	
	
	
	private void replaceDeclaration(
			Entry e, ASTNode node, List nodeFragments, List nodeModifiers,
			boolean isVarDecl) {
		ASTNode s = null;
		List sFragments = null, sMods = null;
//		List newNodeFragments = null, newNodeMods = null;
		
		for(int i = 0; i < nodeFragments.size(); i++) {
			VariableDeclarationFragment varfrag = (VariableDeclarationFragment)nodeFragments.get(i);
			String varName = varfrag.getName().getIdentifier()+STRUCT_DATA;

			VariableDeclarationFragment f = ast.newVariableDeclarationFragment();
			f.setName(name(varName));
			
			if(varfrag.getInitializer() != null) {
				replaceAssignment(e, null, varfrag.getName(), varfrag.getInitializer(),
						Assignment.Operator.ASSIGN);
				//Assignment here
//				varfrag.setInitializer((Expression)translate(varfrag.getInitializer()));
			}
			
//			if(varfrag.getInitializer() != null) {
//				Assignment a = ast.newAssignment();
//				a.setLeftHandSide(name(varName));
//
//				Expression ex = (Expression)copy(varfrag.getInitializer());
//				if(ex.getProperty(TYPEBIND_METHOD) == METHOD_IDX_ARRAY_ACCESS) {
//					/*MethodInvocation mi = (MethodInvocation)ex;
//					a.setRightHandSide((Expression)copy((ASTNode)mi.arguments().get(1)));
//					mi.arguments().set(1, a);
//					varfrag.setInitializer(ex);*/
////					newvarfrag.setInitializer(ex);
//					
//					MethodInvocation mi = (MethodInvocation)ex;
//					a.setRightHandSide((Expression)copy(mi.getExpression()));
//					mi.setExpression(wrap(a));
//					varfrag.setInitializer(ex);
//				}
//				else if(ex.getProperty(TYPEBIND_METHOD) == METHOD_IDX_RETURN) {
//					/*MethodInvocation mi = (MethodInvocation)ex;
//					a.setRightHandSide((Expression)copy((ASTNode)mi.arguments().get(1)));
//					mi.arguments().set(1, a);
//					varfrag.setInitializer(ex);*/
////					newvarfrag.setInitializer(ex);
//
//					MethodInvocation mi = (MethodInvocation)ex;
//					a.setRightHandSide((Expression)copy((ASTNode)mi.arguments().get(0)));
//					mi.arguments().set(0, a);
//					varfrag.setInitializer(ex);
//				}
//				else if(ex instanceof SimpleName) {
//					String rhsName = ((SimpleName)ex).getIdentifier();
//					String rhsName2 = rhsName+STRUCT_DATA;
//					
////					node.setLeftHandSide(name(varName2));
//
//					MethodInvocation m = ast.newMethodInvocation();
//					m.setExpression(name(MEM0));
//					m.setName(name("idx"));
//					
//					a.setRightHandSide(name(rhsName));
//					m.arguments().add(name(rhsName2));
//					m.arguments().add(a);
//					
//					m.setProperty(TYPEBIND_PROP, e);
//					m.setProperty(TYPEBIND_METHOD, METHOD_IDX_SIMPLE_NAME);
//
//					a.setProperty(TYPEBIND_PROP, e);
//					varfrag.setInitializer(m);
////					newvarfrag.setInitializer(m);
//				}
//				else {
//					throw new IllegalArgumentException("not yet implemented " + node);
//				}
//			}

			if(s == null) {
				if(isVarDecl) {
					VariableDeclarationStatement s1;
//					VariableDeclarationStatement newNode1;
					s = s1 = ast.newVariableDeclarationStatement(f);
//					newNode = newNode1 =ast.newVariableDeclarationStatement(newvarfrag);
					sFragments = s1.fragments(); sMods = s1.modifiers();
//					newNodeFragments = newNode1.fragments(); newNodeMods = newNode1.modifiers();
				}
				else {
					FieldDeclaration s1;
//					FieldDeclaration newNode1;
					s = s1 = ast.newFieldDeclaration(f);
//					newNode = newNode1 = ast.newFieldDeclaration(newvarfrag);
					sFragments = s1.fragments(); sMods = s1.modifiers();
//					newNodeFragments = newNode1.fragments(); newNodeMods = newNode1.modifiers();
				}
			}
			else {
				sFragments.add(f);
//				newNodeFragments.add(newvarfrag);
			}
		}
		
		if(isVarDecl) ((VariableDeclarationStatement)s).setType(type(e, R1X[e.heapType.ordinal()]));
		else ((FieldDeclaration)s).setType(type(e, R1X[e.heapType.ordinal()]));

		
		for(Object o : nodeModifiers) {
			sMods.add(copySubtree((ASTNode)o));
//			newNodeMods.add(copySubtree((ASTNode)o));
		}

		prepend(s, node);
		
		if(isVarDecl) ((VariableDeclarationStatement)node).setType(ast.newPrimitiveType(PrimitiveType.LONG));
		else ((FieldDeclaration)node).setType(ast.newPrimitiveType(PrimitiveType.LONG));
//		replace(node, newNode);
		
	}
	
	
}
































