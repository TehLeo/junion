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
package theleo.jstruct.plugin.ecj;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.ir.debug.ASTWriter;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTRequestor;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.WildcardType;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.jdt.internal.corext.dom.ASTNodeFactory;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IRegion;
import theleo.jstruct.plugin.Log;
import theleo.jstruct.plugin.ecj.StructCache.Entry;
import theleo.jstruct.plugin.ecj.StructCache.FieldType;
import theleo.jstruct.plugin.ecj.StructCache.TypeSymbol;
import theleo.jstruct.plugin.ecj.Translator.MethodFrame;
import static theleo.jstruct.plugin.ecj.Translator.R1X;
import static theleo.jstruct.plugin.ecj.Translator.STRUCT_TYPE_VAR;
import theleo.jstruct.plugin.ecj.Translator.StringCache.MethodType;
import theleo.jstruct.plugin.ecj.Translator.StringCache.OpSymbol;
import theleo.jstruct.plugin.ecj.Translator.TmpTranslator;
import static theleo.jstruct.plugin.ecj.Translator.err;

/**
 *
 * @author Juraj Papp
 */
public class BaseTranslator extends ASTVisitor {
	public static final String STRUCT_DATA = "$structdata$";

	public static final String[] MEM0 = {"theleo", "jstruct", "hidden", "Mem0"};
	public static final String[] MEM0_B = {"theleo", "jstruct", "hidden", "Mem0", "B"};

	public static final String TYPEBIND_PROP = "theleo.jstruct.CUSTOM_TYPEBIND";
	
	public static final String TYPEBIND_METHOD_TMP = "theleo.jstruct.METHOD_TMP";
	public static final String IS_REPLACE = "theleo.jstruct.IS_REPLACE";
	public static enum TypebindMethodTmp {
		Array, Array_Ptr, Ptr, Ref, StackVar
	}
	public CompilationUnit cu;
	public Document doc;
	public String sourceFileName;
	public AST ast;
	TmpTranslator tmpTranslator;

	public BaseTranslator(CompilationUnit cu, Document doc, String sourceFileName) {
		this.cu = cu;
		this.doc = doc;
		this.sourceFileName = sourceFileName;
		this.ast = cu.getAST();
	}
	public ASTNode copySubtreeIfHasParent(Object o) {
		ASTNode n = (ASTNode)o;
		if(n.getParent() == null) return n;
		return copySubtree(n);
	}
	public ASTNode copySubtree(Object o) {
		ASTNode n = (ASTNode)o;
		MethodTmp m = null;
		if(tmpTranslator != null) {
			m = getMethodTmp(n);
			if(m != null) {
				n.setProperty(TYPEBIND_METHOD_TMP, null);
			}
			n = tmpTranslator.translate(n);
		}
		
		ASTNode copy = ASTNode.copySubtree(ast, n);
		Object t = n.getProperty(TYPEBIND_PROP);
		if(t != null) copy.setProperty(TYPEBIND_PROP, t);
		if(m != null) copy.setProperty(TYPEBIND_METHOD_TMP, m);
		
//		Object t2 = n.getProperty(TYPEBIND_METHOD_TMP);
//		if(t2 != null) copy.setProperty(TYPEBIND_METHOD_TMP, t2);
		return copy;
	}
	public void replace(ASTNode old, ASTNode neww) {
		
//		if(!copyStack.isEmpty()) {
//			if(copyStack.get(copyStack.size()-1) == old) {
//				copyStack.set(copyStack.size()-1, neww);
//				Log.err("COPY STACK REPLACE");
//				Object oldProp = old.getProperty(TYPEBIND_PROP);
//				if(oldProp != null && neww.getProperty(TYPEBIND_PROP) == null) {
//					Log.err("   copy old prop");
//					neww.setProperty(TYPEBIND_PROP, oldProp);
//				}
//				Object oldProp2 = old.getProperty(TYPEBIND_METHOD_TMP);
//				if(oldProp2 != null && neww.getProperty(TYPEBIND_METHOD_TMP) == null) {
//					Log.err("   copy old prop2");
//					neww.setProperty(TYPEBIND_METHOD_TMP, oldProp2);
//				}
//				return;
//			}
//		}
		old.setProperty(IS_REPLACE, neww);
		ASTNode parent = old.getParent();
		StructuralPropertyDescriptor desc = old.getLocationInParent();
		if(desc instanceof ChildListPropertyDescriptor) {
			ChildListPropertyDescriptor ch = (ChildListPropertyDescriptor)desc;
			List<ASTNode> list = (List)parent.getStructuralProperty(ch);
			
			int index = list.indexOf(old);
			list.set(index, neww);			
		}
		else {
			if(parent instanceof QualifiedName && 
					QualifiedName.QUALIFIER_PROPERTY.getId().equals(desc.getId()) &&
				!(neww instanceof SimpleName)) {
				if(!(neww instanceof Expression))throw new IllegalArgumentException();
				//QualifiedName has to be changed to FieldAccess
				
//				throw new IllegalArgumentException("qual name expression");
				FieldAccess fa = ast.newFieldAccess();
				fa.setExpression((Expression)neww);
				fa.setName((SimpleName)copySubtreeIfHasParent(((QualifiedName)parent).getName()));
				
//				for(Map.Entry ee : (Set<Map.Entry>)parent.properties().entrySet()) {
//					Log.err("ee " + ee.getKey());
//				}
				if(parent.getProperty(TYPEBIND_PROP) == null) 
					fa.setProperty(TYPEBIND_PROP, parent.getProperty(TYPEBIND_PROP));
					
				replace(parent, fa);
				return;
			}
			parent.setStructuralProperty(desc, neww);
		}		
	}
//	ArrayList<ASTNode> copyStack = new ArrayList<>();
	public ASTNode copyArg(Object o) {
		return copy((ASTNode)o);
	}
	public ASTNode copy(ASTNode node) {
//		copyStack.add(node);
		node.accept(this);
//		ASTNode n = copyStack.remove(copyStack.size()-1);
		ASTNode n = getReplace(node);
		if(n != node) {
			err("Different");
			if(n.getProperty(TYPEBIND_PROP) == null) 
				throw new CompilerError("Differnet null type: " + node);
			
			return n;
			//throw new CompilerError("different");
		}
		return copySubtree(node);
	}
	public ASTNode translate(ASTNode node) {
//		copyStack.add(node);
		node.accept(this);
//		ASTNode n = copyStack.remove(copyStack.size()-1);
		ASTNode n = getReplace(node);
		if(n != node) {
			err("Different");
			if(n.getProperty(TYPEBIND_PROP) == null) 
				throw new CompilerError("Differnet null type");
			
			return n;
			//throw new CompilerError("different");
		}
		return node;
	}
	public SimpleName name(String name) {
		SimpleName n = ast.newSimpleName(name);
		n.setProperty(TYPEBIND_PROP, FieldType.OBJECT);
		return n;
	}
	public Name name(String... name) {
		Name n = ast.newName(name);
		n.setProperty(TYPEBIND_PROP, FieldType.OBJECT);
		return n;
	}
	public SimpleType typeQualified(String quadlifiedName) {
		SimpleType n = ast.newSimpleType(ast.newName(quadlifiedName));
		n.setProperty(TYPEBIND_PROP, FieldType.OBJECT);
		return n;
	}
	public SimpleType type(String... name) {
		return ast.newSimpleType(name(name));
	}
	public SimpleType type(Object typeprop, String... name) {
		SimpleType t = ast.newSimpleType(name(name));
		t.setProperty(TYPEBIND_PROP, typeprop);
		return t;
	}
	public Entry entry(ITypeBinding type) {
		return StructCache.get(type);
	}
	public Entry entry(TypeDeclaration name) {
		Object o = name.getProperty(TYPEBIND_PROP);
		if(o instanceof StructCache.FieldType) return null;
		Entry e = (Entry)o;
		if(e != null) return e;
		return StructCache.get(name);
	}
	public Entry entry(Name name) {
		Object o = name.getProperty(TYPEBIND_PROP);
		if(o instanceof StructCache.FieldType) return null;
		Entry e = (Entry)o;
		if(e != null) return e;
		return StructCache.get(name);
	}
	public Entry entry(Type name) {
		Object o = name.getProperty(TYPEBIND_PROP);
		if(o instanceof StructCache.FieldType) return null;
		Entry e = (Entry)o;
		if(e != null) return e;
		return StructCache.get(name);
	}
	public StructCache.Entry entry(Expression name) {
		if(name instanceof Name) return entry((Name)name);
		Object o = name.getProperty(TYPEBIND_PROP);
		if(o instanceof StructCache.FieldType) return null;
		Entry e = (Entry)o;
		if(e != null) return e;
		
		return StructCache.get(name);
	}
	public Entry typeLiteral(Object o) {
		TypeLiteral typeLit = (TypeLiteral)o;
		Type type = typeLit.getType();
		return entry(type);
	}
	public Expression returnBool(boolean val) {
		BooleanLiteral num = ast.newBooleanLiteral(val);
		num.setProperty(TYPEBIND_PROP, StructCache.FieldType.BOOLEAN);
		return num;
	}
	public Expression returnLong(long val) {
		NumberLiteral num = ast.newNumberLiteral(""+val);
		num.setProperty(TYPEBIND_PROP, StructCache.FieldType.LONG);
		return num;
	}
	public Expression returnInt(int val) {
		NumberLiteral num = ast.newNumberLiteral(""+val);
		num.setProperty(TYPEBIND_PROP, StructCache.FieldType.INT);
		return num;
	}
	public Expression returnString(String val) {
		StringLiteral str = ast.newStringLiteral();
		str.setLiteralValue(val);
		str.setProperty(TYPEBIND_PROP, StructCache.FieldType.OBJECT);
		return str;
	}
	public ParenthesizedExpression wrap(Expression e) {
		ParenthesizedExpression p = ast.newParenthesizedExpression();
		p.setExpression(e);
		Object o = e.getProperty(TYPEBIND_PROP);
		if(o != null) p.setProperty(TYPEBIND_PROP, o);
		return p;
	}
	public Type newType(ITypeBinding typeBinding) {
		if( typeBinding == null )
			throw new NullPointerException("typeBinding is null");

		if( typeBinding.isPrimitive() ) {
			return ast.newPrimitiveType(
				PrimitiveType.toCode(typeBinding.getName()));
		}

		if( typeBinding.isCapture() ) {
			ITypeBinding wildCard = typeBinding.getWildcard();
			WildcardType capType = ast.newWildcardType();
			ITypeBinding bound = wildCard.getBound();
			if( bound != null ) {
				capType.setBound(newType(bound),wildCard.isUpperbound());
			}
			return capType;
		}
		if(typeBinding.isWildcardType()) {
			WildcardType capType = ast.newWildcardType();
			ITypeBinding bound = typeBinding.getBound();
			if( bound != null ) {
				capType.setBound(newType(bound),typeBinding.isUpperbound());
			}
			return capType;
		}

		if( typeBinding.isArray() ) {
			Type elType = newType(typeBinding.getElementType());
			return ast.newArrayType(elType, typeBinding.getDimensions());
		}

		if( typeBinding.isParameterizedType() ) {
			ParameterizedType type = ast.newParameterizedType(
				newType(typeBinding.getErasure()));

			List<Type> newTypeArgs = type.typeArguments();
			for( ITypeBinding typeArg : typeBinding.getTypeArguments() ) {
				newTypeArgs.add(newType(typeArg));
			}

			return type;
		}

		String qualName = typeBinding.getQualifiedName().trim();
		if(qualName == null || "".equals(qualName) ) {
			throw new IllegalArgumentException("No name for type binding.");
		}
		return ast.newSimpleType(ast.newName(qualName));
	}
	public MethodTmp getMethodTmp(ASTNode n) {
		Object o = n.getProperty(TYPEBIND_METHOD_TMP);
		if(o != null) return (MethodTmp)o;
		return null;
	}
	public MethodInvocation methodTmpStackVar(ASTNode StackExpression,
			Expression AddressIdentifier, Entry TypeBind) {
		MethodInvocation m = ast.newMethodInvocation();
		m.setExpression(name(MEM0));
		m.setName(name("tmpstack"));
		List args = m.arguments();
		args.add((Expression)copySubtreeIfHasParent(StackExpression));
		args.add((Expression)copySubtreeIfHasParent(AddressIdentifier));
		args.add(returnInt(0));
		m.setProperty(TYPEBIND_PROP, TypeBind);
		m.setProperty(TYPEBIND_METHOD_TMP, new MethodTmp(TypebindMethodTmp.StackVar, TypeBind));
		return m;
	}
	public MethodInvocation methodTmpRef(ASTNode StackExpression,
			Expression AddressIdentifier, Entry TypeBind, StructCache.FieldEntry f, String varData, String var) {
		MethodInvocation m = ast.newMethodInvocation();
		m.setExpression(name(MEM0));
		m.setName(name("tmpref"));
		List args = m.arguments();
		args.add((Expression)copySubtreeIfHasParent(StackExpression));
		args.add((Expression)copySubtreeIfHasParent(AddressIdentifier));
		args.add(returnInt(0));
		m.setProperty(TYPEBIND_PROP, TypeBind);
		m.setProperty(TYPEBIND_METHOD_TMP, new MethodTmp(TypebindMethodTmp.Ref, TypeBind, f, varData, var));
		return m;
	}
	public MethodInvocation methodTmpArrayIndex(ASTNode ArrayExpression,
			SimpleName ArrayIdentifier, Entry TypeBind, List dims) {
		if(dims.size() < 1) throw new IllegalArgumentException();

		MethodInvocation m = ast.newMethodInvocation();
		m.setExpression(name(MEM0));
		m.setName(name("tmparr"));
		List args = m.arguments();
		if(dims.size() == 1) {
			args.add((Expression)copySubtreeIfHasParent(ArrayExpression));
		}
		else {
			FieldAccess fa = ast.newFieldAccess();
			fa.setExpression((Expression)copySubtreeIfHasParent(ArrayExpression));
			fa.setName(name("r"));
			args.add(fa);
		}
		args.add((Expression)copySubtreeIfHasParent(ArrayIdentifier));
		args.add(returnInt(0));
		
		if(dims.size() == 1)
			args.add(copySubtreeIfHasParent(dims.get(0)));
		else {
			MethodInvocation idx2 = ast.newMethodInvocation();
			idx2.setExpression((Expression)copySubtreeIfHasParent(ArrayIdentifier));
			idx2.setName(name(getIndexMethodName(dims.size())));
			idx2.setProperty(TYPEBIND_PROP, FieldType.LONG);
			for(int i = 0; i < dims.size(); i++) {
				idx2.arguments().add(copySubtreeIfHasParent(dims.get(i)));
			}
			
			for(int i = 0; i < dims.size(); i++) {
				ASTNode arg3 = (ASTNode)dims.get(i);
				MethodTmp tmp3 = getMethodTmp(arg3);
				if(tmp3 != null) {
					replaceMethodTmp(arg3, (MethodInvocation)arg3, tmp3, MethodType.get, null, Assignment.Operator.ASSIGN);
				}
			}
			args.add(idx2);
		}
		
		m.setProperty(TYPEBIND_PROP, TypeBind);
		m.setProperty(TYPEBIND_METHOD_TMP, new MethodTmp(TypebindMethodTmp.Array, TypeBind));
		return m;
	}
	public class MethodTmp {
		public TypebindMethodTmp type;
		public int offset = 0;
		public Entry e;
		public StructCache.FieldEntry f;
		public String varData, var;
//		public boolean isLocalVar;
		
		public MethodTmp(Entry e) {
			this.type = TypebindMethodTmp.Ptr;
			this.e = e;
		}
		public MethodTmp(StructCache.FieldEntry f) {
//			this.type = f.isReference()?TypebindMethodTmp.Ref:TypebindMethodTmp.Ptr;
			this.type = TypebindMethodTmp.Ptr;
			this.f = f;
			this.offset = f.offset;
		}
		private MethodTmp(TypebindMethodTmp type) {
			this.type = type;
		}
		private MethodTmp(TypebindMethodTmp type, Entry e) {
			this.type = type;
			this.e = e;
		}
		private MethodTmp(TypebindMethodTmp type, Entry e, StructCache.FieldEntry f, String varData, String var) {
			this.type = type;
			this.e = e;
			this.f = f;
			this.varData = varData;
			this.var = var;
		}
		
		public Entry getEntryResult() {
			if(f == null) return e;
			if(f.isStruct()) return f.structType;
			if(f.isReference()) return f.structType;
			return null;
		}
		
		
		public Expression getArrayReference(MethodInvocation mi) {
			return (Expression)copySubtree(mi.arguments().get(0));
		}
		public Expression getLongAddress(MethodInvocation mi) {
			Expression indexExpr = getLongAddressBase(mi);
			
			NumberLiteral lit = (NumberLiteral)mi.arguments().get(2);
			if(offset != Integer.parseInt(lit.getToken()))
				throw new IllegalArgumentException("offset != " + lit);
			
			Expression args;
			if(offset == 0) args = indexExpr;
			else {
				InfixExpression add = ast.newInfixExpression();
				add.setOperator(InfixExpression.Operator.PLUS);
				add.setLeftOperand(indexExpr);
				add.setRightOperand(returnInt(offset));
				args = add;
			}
			return args;
		}
		
		public Expression getLongAddressBase(MethodInvocation mi) {
			Expression indexExpr = null;
			if(null != type)
				switch (type) {
				case StackVar:
				case Ptr:
				case Ref:
					indexExpr = (Expression)copySubtreeIfHasParent(mi.arguments().get(1));
					break;
				case Array:
				case Array_Ptr:
					MethodInvocation mIndex = ast.newMethodInvocation();
					mIndex.setExpression((Expression)copySubtree(mi.arguments().get(1)));
										
					mIndex.setName(name(getIndexMethodName(mi)));
					fillIndexMethodArgs(mi, mIndex.arguments());
					
					indexExpr = mIndex;
//					indexExpr = getLongAddressWithIdx(mIndex);
					break;
				default:
					throw new IllegalArgumentException();
			}
			return indexExpr;
		}
		public void fillIndexMethodArgs(MethodInvocation mi, List store) {
			for(int i = 3; i < mi.arguments().size(); i++) {
				ASTNode arg3 = (ASTNode)mi.arguments().get(i);
				MethodTmp tmp3 = getMethodTmp(arg3);
				if(tmp3 != null) {
					replaceMethodTmp(arg3, (MethodInvocation)arg3, tmp3, MethodType.get, null, Assignment.Operator.ASSIGN);
				}

				store.add(copySubtree(mi.arguments().get(i)));
			}
		}
		public Expression getLongAddressOffset(MethodInvocation mi) {			
			return returnInt(offset);
		}
		public Expression addLongAddressOffset(Expression e) {
			Expression args;
			if(offset == 0) args = e;
			else {
				InfixExpression add = ast.newInfixExpression();
				add.setOperator(InfixExpression.Operator.PLUS);
				add.setLeftOperand(e);
				add.setRightOperand(returnInt(offset));
				args = add;
			}
			return args;
		}
//		public Expression getLongAddressWithIdx(MethodInvocation mi) {
//			return mi;
////			
//////			mi.arguments().add(ast.newNumberLiteral(""+e.structSize));
//////			return mi;
////			Expression ret = mi;
////			
////			//test no index checking
////			ret = wrap((Expression)copySubtreeIfHasParent(mi.arguments().get(0)));
////			
////			if(e.structSize != 1) {
////				InfixExpression mult = ast.newInfixExpression();
////				mult.setOperator(InfixExpression.Operator.TIMES);
////				mult.setLeftOperand((Expression)copySubtreeIfHasParent(ret));
////				mult.setRightOperand(ast.newNumberLiteral(""+e.structSize));
////				return wrap(mult);
////			}
////			else return ret;
//		} 
	}
	public static final String INDEX_METHOD = "idx";
//	public static final String INDEX_METHOD = "idxCheckOnly";
	
	public String getIndexMethodName(MethodInvocation mi) {
		int dims = mi.arguments().size()-3;
		return getIndexMethodName(dims);
	}
	public String getIndexMethodName(int dims) {
		if(dims < 1) throw new IllegalArgumentException();
		else if(dims == 1) {
			return INDEX_METHOD;
		}
		else if(dims < 5) {
			return INDEX_METHOD+dims;
		}
		else return INDEX_METHOD+'N';
	}

	
	public void replaceMethodCallArguments(List list) {
		for(int i = 0; i < list.size(); i++) {
			Expression expr = (Expression)list.get(i);
			Entry e = entry(expr);
			if(e != null) {
				MethodTmp tmp = getMethodTmp(expr);
				Expression trimmed = trimWrap(expr);
				if(trimmed instanceof SimpleName) {
					SimpleName n = name(((SimpleName)trimmed).getIdentifier()+STRUCT_DATA);
					n.setProperty(TYPEBIND_PROP, e);
					list.add(i,n);
					i++;
				}
				else if(tmp != null) {
					MethodInvocation mi = (MethodInvocation)expr;
					list.set(i, tmp.getArrayReference(mi));
					list.add(i+1, tmp.getLongAddress(mi));
					mi.setProperty(TYPEBIND_PROP, FieldType.INT);
					i++;
				}
				else throw new IllegalArgumentException();
			}
		}
	}
	public Expression methodB(String methodName, Type castType, Object... args) {
		MethodInvocation p = ast.newMethodInvocation();
		p.setExpression(name(MEM0_B));
//		p.setName(name(Translator.StringCache.getString(
//				type,
//				t,
//				opsymbol)));
		p.setName(name(methodName));

		for(Object arg : args)
			p.arguments().add(arg);
		
		if(castType != null) {
			CastExpression cast = ast.newCastExpression();			
			cast.setType(castType);
			cast.setExpression(p);
			cast.setProperty(TYPEBIND_PROP, FieldType.OBJECT);
			return cast;
		}
		return p;
	}
	
	public void replaceMethodTmp(ASTNode node, MethodInvocation mi, MethodTmp tmp, MethodType type, ASTNode putValue, Assignment.Operator op) {
		OpSymbol opsymbol = null;
//					String metName = "put";
		if(op != null && op != Assignment.Operator.ASSIGN) {
			if(type == MethodType.get) throw new CompilerError("Used operator for get " + node);
			switch(op.toString()) {
				case "+=":   opsymbol = OpSymbol.Add; break;
				case "-=":   opsymbol = OpSymbol.Sub; break;
				case "*=":	 opsymbol = OpSymbol.Mul; break;
				case "/=":   opsymbol = OpSymbol.Div; break;
				case "&=":   opsymbol = OpSymbol.And; break;
				case "|=":   opsymbol = OpSymbol.Or; break;
				case "^=":   opsymbol = OpSymbol.Xor; break;
				case "%=":   opsymbol = OpSymbol.Mod; break;
				case "<<=":  opsymbol = OpSymbol.LL; break;
				case ">>=":  opsymbol = OpSymbol.RR; break;
				case ">>>=": opsymbol = OpSymbol.RRR; break;
				default:
					throw new CompilerError("Unknown operator " + op.toString());
			}
		}
		MethodInvocation p = ast.newMethodInvocation();
		p.setExpression(name(MEM0_B));
		MethodInvocation miL = mi;
		switch(tmp.type) {
			case Array:
				if(type == MethodType.get) {
					throw CompilerError.get(getErrorLine(node), CompilerError.STRUCT_CONVERT_TO_OBJECT);
				}
				else {
					Expression right = (Expression)putValue;
					MethodTmp tmpL = tmp;

					Entry eL = tmpL.getEntryResult();
					Entry eR = entry(right);
					if(eL != null && eR != null) {
						if(eL == eR) {
							if(right instanceof SimpleName) {
								SimpleName s = (SimpleName)right;

								p.setExpression(name(MEM0));
								p.setName(name("copy"));

								List args = p.arguments();

								args.add(name(s.getIdentifier()+STRUCT_DATA));
								args.add(name(s.getIdentifier()));
								args.add(tmpL.getArrayReference(miL));
								args.add(tmpL.getLongAddress(miL));
								args.add(newEntryType(eR));
								args.add(returnLong(1));

								replace(node, p);
								return;
							}
							else {
								MethodTmp tmpR = getMethodTmp(putValue);
								if(tmpR != null) {
									MethodInvocation mRight = (MethodInvocation)putValue;
									p.setExpression(name(MEM0));
									p.setName(name("copy"));

									List args = p.arguments();
									args.add(tmpR.getArrayReference(mRight));
									args.add(tmpR.getLongAddress(mRight));
									args.add(tmpL.getArrayReference(miL));
									args.add(tmpL.getLongAddress(miL));
									args.add(newEntryType(eR));
									args.add(returnLong(1));

									replace(node, p);
								}
								else throw new IllegalArgumentException();
							}
						}
						else {
							throw CompilerError.get(getErrorLine(node), CompilerError.STRUCT_COPY_TYPE_MISMATCH, eR.qualifiedName, eL.qualifiedName);
						}
					}
					else throw CompilerError.get(getErrorLine(node), CompilerError.UNCATEGORIZED_ERROR);
					
				}
				return;
//				throw new IllegalArgumentException("node " + node + ", " + mi + ", " + type);
		//						break;
			case Array_Ptr:
			case Ptr:
			case Ref:
			case StackVar:
//				boolean isLocalVar = tmp.isLocalVar;
				
				
				FieldType ftype = tmp.f.type;
				
				TypeSymbol t = tmp.f.type.symbol;
				if(t == null) {
//					if(tmp.f.isReference() && tmp.f.isSibling)
//						t = TypeSymbol.LONG;
//					else throw new IllegalArgumentException();
					
					if(tmp.f.isReference()) {
						if(type == MethodType.get) {
							throw CompilerError.get(getErrorLine(node), CompilerError.STRUCT_CONVERT_TO_OBJECT);
						}
						else {
							p.setName(name("pR"));
							if(tmp.type == TypebindMethodTmp.Ref) {
								p.arguments().add(getRefArg(miL, 3));
								if(tmp.f.isSibling) {
									p.arguments().add(getRefArg(miL, 4));
								}
								else {
									p.arguments().add(getRefArg(miL, 4));
									p.arguments().add(getRefArg(miL, 5));
									p.arguments().add(returnInt(tmp.f.refData.objOffset));
								}
							}
							else {
								p.arguments().add(tmp.getArrayReference(mi));
								if(tmp.f.isSibling) {
									p.arguments().add(tmp.getLongAddress(mi));
								}
								else {
									p.arguments().add(tmp.getLongAddressBase(mi));
									p.arguments().add(tmp.getLongAddressOffset(mi));
									p.arguments().add(returnInt(tmp.f.refData.objOffset));
								}
							}
							
							if(putValue instanceof SimpleName) {
								SimpleName s = (SimpleName)putValue;
								p.arguments().add(name(s.getIdentifier()+STRUCT_DATA));
								p.arguments().add(name(s.getIdentifier()));
							}
							else {
								MethodTmp tmpR = getMethodTmp(putValue);
								if(tmpR != null) {
									MethodInvocation miR = (MethodInvocation)putValue;
									p.arguments().add(tmpR.getArrayReference(miR));
									p.arguments().add(tmpR.getLongAddress(miR));
								}
								else throw new IllegalArgumentException();
							}
							replace(node, p);
							return;
						}
					}
					else if(tmp.f.isStruct()) {
						if(type == MethodType.get) {
							throw new IllegalArgumentException();
						}
						
						Expression right = (Expression)putValue;
						MethodTmp tmpL = tmp;
						
						Entry eL = tmpL.getEntryResult();
						Entry eR = entry(right);
						if(eL != null && eR != null) {
							if(eL == eR) {
								if(right instanceof SimpleName) {
									SimpleName s = (SimpleName)right;

									p.setExpression(name(MEM0));
									p.setName(name("copy"));

									List args = p.arguments();

									args.add(name(s.getIdentifier()+STRUCT_DATA));
									args.add(name(s.getIdentifier()));
									args.add(tmpL.getArrayReference(miL));
									args.add(tmpL.getLongAddress(miL));
									args.add(newEntryType(eR));
									args.add(returnLong(1));

									replace(node, p);
									return;
								}
								else {
									MethodTmp tmpR = getMethodTmp(putValue);
									if(tmpR != null) {
										MethodInvocation mRight = (MethodInvocation)putValue;
										p.setExpression(name(MEM0));
										p.setName(name("copy"));

										List args = p.arguments();
										args.add(tmpR.getArrayReference(mRight));
										args.add(tmpR.getLongAddress(mRight));
										args.add(tmpL.getArrayReference(miL));
										args.add(tmpL.getLongAddress(miL));
										args.add(newEntryType(eR));
										args.add(returnLong(1));

										replace(node, p);
										return;
									}
									else throw new IllegalArgumentException();
								}
							}
							else {
								CompilerError.exec(CompilerError.STRUCT_COPY_TYPE_MISMATCH, eR.qualifiedName, eL.qualifiedName);
							}
						}
						else throw new IllegalArgumentException();
					}
					else throw new IllegalArgumentException();
				}
				
				p.setName(name(Translator.StringCache.getString(
						type,
						t,
						opsymbol)));

				p.arguments().add(tmp.getArrayReference(mi));
				p.arguments().add(tmp.getLongAddress(mi));
				

/*
				if(tmp.type == TypebindMethodTmp.Ptr) {
					Object arg0 = mi.arguments().get(0);
//					if(arg0 instanceof SimpleName) {
//						SimpleName s = (SimpleName)arg0;
//						p.arguments().add(name(s.getIdentifier()));
//						indexExpr = (Expression)copySubtreeIfHasParent(mi.arguments().get(1));
//					}
//					else {
//						p.arguments().add(copySubtreeIfHasParent(arg0));
//						indexExpr = (Expression)copySubtreeIfHasParent(mi.arguments().get(1));
//					}
//					
					p.arguments().add(copySubtreeIfHasParent(arg0));
					indexExpr = (Expression)copySubtreeIfHasParent(mi.arguments().get(1));
//						throw new IllegalArgumentException("not yet implemented");
				}
				else {
//					if(isLocalVar) {
					Log.err("    base --- " + mi);

						p.arguments().add((Expression)copySubtree(mi.arguments().get(0)));
						MethodInvocation mIndex = ast.newMethodInvocation();
						mIndex.setExpression((Expression)copySubtree(mi.arguments().get(1)));
						mIndex.setName(name("idx"));
						mIndex.arguments().add(copySubtree(mi.arguments().get(3)));
						indexExpr = mIndex;
//					}
//					else {
////						indexExpr = null;
//						throw new IllegalArgumentException("not yet implemented");
//					}
				}
				*/


				if(ftype == StructCache.FieldType.OBJECT || ftype == StructCache.FieldType.REFERENCE) 
					p.arguments().add(returnInt(tmp.f.objOffset));

				if(type == MethodType.put) {
					if(ftype.primCast && (op != null || (trimWrap((Expression)putValue)) instanceof NumberLiteral)) {
						CastExpression cast = ast.newCastExpression();			
						cast.setType(newType(tmp.f.typeb));
						cast.setExpression((Expression)copySubtree(putValue));
						cast.setProperty(TYPEBIND_PROP, ftype);
						p.arguments().add(cast);
					}
					else p.arguments().add(copySubtree(putValue));
				}

				if(ftype == StructCache.FieldType.OBJECT) {
					CastExpression cast = ast.newCastExpression();			
					cast.setType(newType(tmp.f.typeb));
					cast.setExpression(p);
					cast.setProperty(TYPEBIND_PROP, ftype);
					replace(node, wrap(cast));
				}			
				else replace(node, p);

				break;
		}
			
	}
	public FieldAccess newEntryType(Entry e) {
		FieldAccess structType = ast.newFieldAccess();
		structType.setExpression(ast.newName(e.qualifiedName));
		structType.setName(name(STRUCT_TYPE_VAR));
		return structType;
	}
	public boolean isReplace(ASTNode n) { return n.getProperty(IS_REPLACE) != null;}
	public ASTNode getReplace(ASTNode n) {
		ASTNode r = n;
		while(r.getProperty(IS_REPLACE) != null) r = (ASTNode)r.getProperty(IS_REPLACE);
		return r;
	}
	public Expression addOffset(Expression e, int offset) {
		if(offset == 0) return e;
		InfixExpression add = ast.newInfixExpression();
		add.setOperator(InfixExpression.Operator.PLUS);
		add.setLeftOperand(e);
		add.setRightOperand(returnInt(offset));
		return add;
	}
	public Expression getArg(MethodInvocation mi, int arg) {
		return (Expression)copySubtreeIfHasParent(mi.arguments().get(arg));
	}
	public Expression getRefArg(MethodInvocation mi, int arg) {
		return (Expression)copySubtreeIfHasParent(mi.arguments().get(arg));
	}
	public Expression trimWrap(Expression expr) {
		Expression e = expr;
		while(e instanceof ParenthesizedExpression) {
			e = ((ParenthesizedExpression)e).getExpression();
		}
		return e;
	} 
	
	public String getErrorLine(ASTNode node) {
		int pos = getStartPos(node);
		if(pos < 0) return null;
		int line = cu.getLineNumber(pos);
		try {
			IRegion r = doc.getLineInformationOfOffset(pos);
			
//			int len = doc.getLineLength(line);
//			for(int i = 190; i < 210; i++) {
//				IRegion r = doc.getLineInformation(i);
//				Log.err("Line " + i + ": " + r);
//				Log.err(" OFF " + r.getOffset() + ", len " + r.getLength());
//				Log.err("xx " + doc.get().substring(r.getOffset(), r.getOffset()+r.getLength()));
//				
//			}
//			Log.err("DOC  " + doc.get());
//			
//			Log.err("len fgg " + doc.getLineInformation(line).toString());
			String text = doc.get(r.getOffset(), r.getLength());
			return sourceFileName + ":" + line + ": " + text;
		} catch (Exception ex) {
			return "Line " + line + ": ";
		}
	}
	public int getStartPos(ASTNode node) {
		int n = node.getStartPosition();
		while(n == -1) {
			node = node.getParent();
			if(node == null) return n;
			n = node.getStartPosition();
		}
		return n;
	}
//	public ASTNode getNodeWithPos(ASTNode node) {
//		int n = node.getStartPosition();
//		while(n == -1) {
//			node = node.getParent();
//			if(node == null) return null;
//			n = node.getStartPosition();
//		}
//		return node;
//	}
}
