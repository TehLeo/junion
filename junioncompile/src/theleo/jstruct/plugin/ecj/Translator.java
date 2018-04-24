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
import java.util.List;
import org.eclipse.jdt.core.dom.*;
import theleo.jstruct.hidden.Mem0;
import theleo.jstruct.plugin.Log;
import theleo.jstruct.plugin.ecj.StructCache.*;

/**
 *
 * @author Juraj Papp
 */
public class Translator extends ASTVisitor {
	public static final String theleo = "theleo", jstruct = "jstruct", hidden = "hidden";
	public static final String[] AUTO_ARRAY = {"theleo", "jstruct", "hidden", "AutoArray"};
	public static final String[] AUTO_HYBRID = {"theleo", "jstruct", "hidden", "AutoHybrid"};
	public static final String[] MEM0 = {"theleo", "jstruct", "hidden", "Mem0"};
	public static final String[] MEM0_U = {"theleo", "jstruct", "hidden", "Mem0", "u"};
	public static final String[] BOXED_LONG = {"java", "lang", "Long"};
	
	public static final String TYPEBIND_PROP = "theleo.jstruct.CUSTOM_TYPEBIND";
	public static final String TYPEBIND_METHOD = "theleo.jstruct.METHOD";
	public static final String TYPEBIND_REF = "theleo.jstruct.METHOD";
	
	public static final String REF_SAFE = "SAFE_REF";
	
	public static final String METHOD_PTR = "theleo.jstruct.Mem.ptr";
	
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
	public class FieldTranslator extends ASTVisitor {
		@Override
		public boolean visit(SimpleType node) {
			Entry e = entry(node);
			if(e != null) {
				PrimitiveType type = ast.newPrimitiveType(PrimitiveType.LONG);
				replace(node, type);
				return false;
			}
			return true;
		}
	}
	
	
//	HashMap<Object, Object> propertyMap = new HashMap<>();
	
	AST ast;
	TypeTranslator typeTranslator;
	FieldTranslator fieldTranslator;
	public Translator(CompilationUnit cu) {
		ast = cu.getAST();
		typeTranslator = new TypeTranslator();
		fieldTranslator = new FieldTranslator();
	}	
	public static boolean debug = false;
	static void err(Object s) {
		if(debug) System.err.println("");
	}
	
	public void replace(ASTNode old, ASTNode neww) {
		if(!copyStack.isEmpty()) {
			if(copyStack.get(copyStack.size()-1) == old) {
				copyStack.set(copyStack.size()-1, neww);
				err("COPY STACK REPLACE");
				Object oldProp = old.getProperty(TYPEBIND_PROP);
				if(oldProp != null) {
					err("   copy old prop");
					neww.setProperty(TYPEBIND_PROP, oldProp);
				}
					
				return;
			}
		}
		
		ASTNode parent = old.getParent();
		StructuralPropertyDescriptor desc = old.getLocationInParent();
		if(desc instanceof ChildListPropertyDescriptor) {
			ChildListPropertyDescriptor ch = (ChildListPropertyDescriptor)desc;
			List<ASTNode> list = (List)parent.getStructuralProperty(ch);
			
			int index = list.indexOf(old);
			list.set(index, neww);			
		}
		else parent.setStructuralProperty(desc, neww);		
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
	public SimpleType type(String... name) {
		return ast.newSimpleType(name(name));
	}
	ArrayList<ASTNode> copyStack = new ArrayList<>();
	public ASTNode copy(ASTNode node) {
		copyStack.add(node);
		node.accept(this);
		ASTNode n = copyStack.remove(copyStack.size()-1);
		if(n != node) {
			err("Different");
			if(n.getProperty(TYPEBIND_PROP) == null) 
				throw new CompilerError("Differnet null type");
			
			return n;
			//throw new CompilerError("different");
		}
		return ASTNode.copySubtree(ast, node);
	}
	public ASTNode translate(ASTNode node) {
		copyStack.add(node);
		node.accept(this);
		ASTNode n = copyStack.remove(copyStack.size()-1);
		if(n != node) {
			err("Different");
			if(n.getProperty(TYPEBIND_PROP) == null) 
				throw new CompilerError("Differnet null type");
			
			return n;
			//throw new CompilerError("different");
		}
		return node;
	}
	
	public Entry entry(TypeDeclaration name) {
		Object o = name.getProperty(TYPEBIND_PROP);
		if(o instanceof FieldType) return null;
		Entry e = (Entry)o;
		if(e != null) return e;
		return StructCache.get(name);
	}
	public Entry entry(Name name) {
		Object o = name.getProperty(TYPEBIND_PROP);
		if(o instanceof FieldType) return null;
		Entry e = (Entry)o;
		if(e != null) return e;
		return StructCache.get(name);
	}
	public Entry entry(Type name) {
		Object o = name.getProperty(TYPEBIND_PROP);
		if(o instanceof FieldType) return null;
		Entry e = (Entry)o;
		if(e != null) return e;
		return StructCache.get(name);
	}
	public Entry entry(Expression name) {
		if(name instanceof Name) return entry((Name)name);
		Object o = name.getProperty(TYPEBIND_PROP);
		if(o instanceof FieldType) return null;
		Entry e = (Entry)o;
		if(e != null) return e;
		
		return StructCache.get(name);
	}

	@Override
	public boolean visit(SimpleType node) {			
		Entry e = entry(node);
		if(e != null) {
			PrimitiveType type = ast.newPrimitiveType(PrimitiveType.LONG);
			replace(node, type);
			return false;
		}
		return true;
	}
	

	

	@Override
	public boolean visit(ParameterizedType node) {
		node.accept(typeTranslator);
		return false;
	}
	
	@Override
	public boolean visit(ArrayType node) {		
		Entry e = entry(node.getElementType());
		if(e != null) {			
			int dims = node.getDimensions();
			if(dims > 1) throw new CompilerError("Multidimensional arrays not implemented. " + dims);
		
			SimpleType type = type(theleo, jstruct, hidden, (e.hasJavaObjects()?"Hyb":"Ref")+dims);						
			replace(node, type);			
			return false;
		}
		return true;
	}

	@Override
	public boolean visit(ArrayCreation node) {
		Entry e = entry(node.getType().getElementType());
		if(e != null) {
			List dims = node.dimensions();
			if(dims.size() > 1) throw new CompilerError("Multidimensional arrays not implemented. " + dims.size());
			ASTNode expr = copy((ASTNode)dims.get(0));
			
			MethodInvocation m = ast.newMethodInvocation();
			m.setExpression(name(MEM0));
			
			if(e.hasJavaObjects()) {
				m.setName(name("allocHybrid"));
				List args = m.arguments();
				args.add(expr);
				args.add(returnLong(e.structSize));
				args.add(returnBool(e.zero));
				args.add(returnLong(e.globalObjCount));
				for(int i = 0; i < e.objOffsets.length; i++) {
					args.add(returnLong(e.objOffsets[i]));
					args.add(returnLong(e.objCounts[i]));
				}
			}
			else {
				m.setName(name("alloc"));
				List args = m.arguments();
				args.add(expr);
				args.add(returnLong(e.structSize));
				args.add(returnBool(e.zero));
			}

			replace(node, m);			
			return false;
		}
		return true;
	}

	@Override
	public boolean visit(ArrayAccess node) {		
		Entry e = entry(node);
		if(e != null) {			
			ASTNode name = copy(node.getArray());
			ASTNode index = copy(node.getIndex());
			
			MethodInvocation m = ast.newMethodInvocation();
			m.setExpression((Expression)name);
			m.setName(name("getIndex"));
			m.arguments().add(index);
					
			m.setProperty(TYPEBIND_PROP, e);
			
			replace(node, m);			
			return false;
		}		
		return true;
	}

		@Override
	public boolean visit(SimpleName node) {
		if(REF_SAFE.equals(node.getProperty(TYPEBIND_REF))) return true;
		Entry e = entry(node);
		if(e != null) {
			IBinding ib = node.resolveBinding();
			if(ib != null) {
				if(ib.getKind() == IBinding.VARIABLE) {
					IVariableBinding v = (IVariableBinding)ib;
					if(v.isField()) {
						node.setProperty(TYPEBIND_REF, REF_SAFE);
						Expression exp = (Expression)copy(node);
						
						MethodInvocation m = ast.newMethodInvocation();
						m.setExpression(name(MEM0));
						m.setName(name("ref"));
						m.arguments().add(exp);
						m.setProperty(TYPEBIND_PROP, e);
						m.setProperty(TYPEBIND_METHOD, METHOD_PTR);
						replace(node, m);
						return false;
					}
				}
			}
		}
		return true;
	}
	
	@Override
	public boolean visit(QualifiedName node) {
		Expression expr = (Expression)translate(node.getQualifier());
		Entry e = entry(expr);
		if(e != null) {			
			replaceFieldAccess(e, node, expr, node.getName().getIdentifier());
			return false;
		}
		else {
			e = entry(node);
			if(e != null) replaceFieldAccessJava(e, node, expr, node.getName().getIdentifier(), node.resolveBinding());
		}
		return false;
	}

	@Override
	public boolean visit(FieldAccess node) {
		Expression expr = (Expression)translate(node.getExpression());
		Entry e = entry(expr);
		if(e != null) {
			replaceFieldAccess(e, node, expr, node.getName().getIdentifier());
			return false;
		}
		else {
			e = entry(node);
			if(e != null) replaceFieldAccessJava(e, node, expr, node.getName().getIdentifier(), node.resolveFieldBinding());
		}
		return false;
	}
	
	public void replaceFieldAccessJava(Entry e, Expression node, Expression expr,String identifier, IBinding binding) {
		if(REF_SAFE.equals(node.getProperty(TYPEBIND_REF))) return;
		
		if(binding.getKind() == IBinding.VARIABLE) {
			IVariableBinding v = (IVariableBinding)binding;
			if(v.isField()) {
				node.setProperty(TYPEBIND_REF, REF_SAFE);
				Expression exp = (Expression)copy(node);

				MethodInvocation m = ast.newMethodInvocation();
				m.setExpression(name(MEM0));
				m.setName(name("ref"));
				m.arguments().add(exp);
				m.setProperty(TYPEBIND_PROP, e);
				m.setProperty(TYPEBIND_METHOD, METHOD_PTR);
				replace(node, m);
			}	
		}
	}
	
	public void replaceFieldAccess(Entry e, Expression node, Expression expr, String identifier) {
//		err(" STRUCT ACCESS " + node.toString());
//		err("  STRUCT " + e.binaryName);
//		err("   '" + identifier + "'");
		
		
					
		FieldEntry f = e.offsetTable.get(identifier);
		if(f == null) CompilerError.exec(CompilerError.STRUCT_FIELD_NOT_FOUND, node.toString());
		
		int offset = f.offset;
//		Expression selected = (Expression)copy(expr);
		Expression selected = (Expression)ASTNode.copySubtree(ast, expr);
		
		Expression args;
		if(offset == 0) args = selected;
		else {
			InfixExpression add = ast.newInfixExpression();
			add.setOperator(InfixExpression.Operator.PLUS);
			add.setLeftOperand(selected);
			add.setRightOperand(ast.newNumberLiteral(""+offset));
			args = add;
		}
		
		if(f.isStruct()) {
			replace(node, args);
			args.setProperty(TYPEBIND_PROP, f.structType);
			return;
		}
		if(f.isReference()) {
			MethodInvocation m = ast.newMethodInvocation();
			m.setExpression(name(MEM0));
			m.setName(name("ptr"));
			m.arguments().add(args);
			m.setProperty(TYPEBIND_PROP, f.structType);
			m.setProperty(TYPEBIND_METHOD, METHOD_PTR);
			replace(node, m);
			return;
		}
		
		MethodInvocation m = ast.newMethodInvocation();
		m.setExpression(name((f.type==FieldType.OBJECT || f.type == FieldType.BOOLEAN)?MEM0:MEM0_U));
		m.setName(name("get"+f.type.name));
		m.arguments().add(args);
		
		m.setProperty(TYPEBIND_PROP, f.type);
		
		if(f.type == FieldType.OBJECT) {
			m.arguments().add(returnInt(f.objOffet));
			
			CastExpression cast = ast.newCastExpression();			
			cast.setType(newType(f.typeb));
			cast.setExpression(m);
			cast.setProperty(TYPEBIND_PROP, f.type);
			replace(node, wrap(cast));
		}
		else replace(node, m);
	}
	

	@Override
	public boolean visit(ExpressionStatement node) {
		return super.visit(node); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean visit(Assignment node) {
		//err("ASSIGNMENT OP "  + node.getOperator());
				
		Expression left = node.getLeftHandSide();
		Expression expr = null;
		SimpleName select = null;
		
		Entry e = entry(left);
		if(e != null) {
			Log.err("ASSIGNMETN VISIT " + node);
		}
		
		if(left instanceof FieldAccess) {
			FieldAccess fa = (FieldAccess)left;
			
			expr = (Expression)translate(fa.getExpression());
			select = fa.getName();
		}
		else if(left instanceof QualifiedName) {
			QualifiedName qn = (QualifiedName)left;
			expr = (Expression)translate(qn.getQualifier());
			select = qn.getName();
		}
		if(expr != null) {
			e = entry(expr);
			if(e != null) {
				
				FieldEntry ft = e.offsetTable.get(select.getIdentifier());
				if(ft == null) CompilerError.exec(CompilerError.STRUCT_FIELD_NOT_FOUND, e.binaryName + " . " + select.getIdentifier());
				
				
				if(node.getOperator() != Assignment.Operator.ASSIGN) {
					throw new CompilerError("Assignment operator not supported.");
				}
				
				Expression rhs = node.getRightHandSide();
				if(rhs instanceof InfixExpression) {
					InfixExpression inf = (InfixExpression)rhs;
					if(!inf.hasExtendedOperands()) {
						
					}
				}
				
				int offset = ft.offset;
				expr = (Expression)ASTNode.copySubtree(ast, expr);
				rhs = (Expression)copy(node.getRightHandSide());

				Expression args;
				if(offset == 0) args = expr;
				else {
					InfixExpression add = ast.newInfixExpression();
					add.setOperator(InfixExpression.Operator.PLUS);
					add.setLeftOperand(expr);
					add.setRightOperand(ast.newNumberLiteral(""+offset));
					args = add;
				}
				
				if(ft.isStruct()) {
					err("STRUCT TYpe");
					replace(node, copyObject(ft.structType, rhs, args));
					return false;
				}
				else if(ft.isReference()) {
					MethodInvocation m = ast.newMethodInvocation();
					m.setExpression(name(MEM0_U));
					m.setName(name("putLong"));
					m.setProperty(TYPEBIND_PROP, ft.type);

					List list = m.arguments();
					list.add(args);
					
					if(rhs instanceof NullLiteral) list.add(returnLong(0));
					else list.add(rhs);
					
					replace(node, m);
					return false;
				}
				else {
					MethodInvocation m = ast.newMethodInvocation();
					m.setExpression(name((ft.type==FieldType.OBJECT || ft.type == FieldType.BOOLEAN)?MEM0:MEM0_U));
					m.setName(name("put"+ft.type.name));
					m.setProperty(TYPEBIND_PROP, ft.type);

					List list = m.arguments();
					list.add(args);
					
					if(ft.type == FieldType.OBJECT) m.arguments().add(returnInt(ft.objOffet));
					
					if(ft.type.primCast) {
						CastExpression cast = ast.newCastExpression();
						cast.setType(newType(ft.typeb));
						cast.setExpression(rhs);
						cast.setProperty(TYPEBIND_PROP, ft.type);
						rhs = wrap(cast);
					}					
					list.add(rhs);
					
					replace(node, m);
					return false;
				}
			}
		}
		
		left = (Expression)translate(left);
		e = entry(left);
		if(e != null) {
			replace(node, copyObject(e, (Expression)copy(node.getRightHandSide()), (Expression)ASTNode.copySubtree(ast,left)));
			return false;
		}
		
		
		return super.visit(node);
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
		err("Here");
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
							n = translate((ASTNode)node.arguments().get(0));
							if(n.getProperty(TYPEBIND_METHOD) == METHOD_PTR) {
								MethodInvocation mi = (MethodInvocation)n;
								ASTNode nn = (ASTNode)mi.arguments().get(0);
								
								MethodInvocation m0 = ast.newMethodInvocation();
								m0.setExpression(name(MEM0));
								m0.setName(name("isNull"));
								m0.setProperty(TYPEBIND_PROP, FieldType.BOOLEAN);
								m0.arguments().add(ASTNode.copySubtree(ast, nn));
								replace(node, m0);
							}
							return false;
						case "li":
							n = copy((Expression)node.arguments().get(0));
							n.setProperty(TYPEBIND_PROP, FieldType.LONG);
							
							replace(node, n);
							break;
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
						case "layoutString":
							e = typeLiteral(node.arguments().get(0));
							
							replace(node, e == null? ast.newNullLiteral(): returnString(e.getStructLayout()));
							return false;
					}
				}
			}
				
		}		
		return true;
	}	
	
	private Entry typeLiteral(Object o) {
		TypeLiteral typeLit = (TypeLiteral)o;
		Type type = typeLit.getType();
		return entry(type);
	}
	private Expression returnBool(boolean val) {
		BooleanLiteral num = ast.newBooleanLiteral(val);
		num.setProperty(TYPEBIND_PROP, FieldType.BOOLEAN);
		return num;
	}
	private Expression returnLong(long val) {
		NumberLiteral num = ast.newNumberLiteral(""+val);
		num.setProperty(TYPEBIND_PROP, FieldType.LONG);
		return num;
	}
	private Expression returnInt(int val) {
		NumberLiteral num = ast.newNumberLiteral(""+val);
		num.setProperty(TYPEBIND_PROP, FieldType.INT);
		return num;
	}
	private Expression returnString(String val) {
		StringLiteral str = ast.newStringLiteral();
		str.setLiteralValue(val);
		str.setProperty(TYPEBIND_PROP, FieldType.OBJECT);
		return str;
	}
	
	@Override
	public void preVisit(ASTNode node) {
		err("PRE " + node.getClass());
		super.preVisit(node); //To change body of generated methods, choose Tools | Templates.
	}
	//@theleo.jstruct.hidden.Order(id=5) int i ;
	
	@Override
	public boolean visit(TypeDeclaration node) {
		err("TYPE " + node.getName());
		Entry e = entry(node);
		if(e != null) {
			err("  STRUCT");
						
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
			
			return false;
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		node.accept(fieldTranslator);
		return false;
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
}
