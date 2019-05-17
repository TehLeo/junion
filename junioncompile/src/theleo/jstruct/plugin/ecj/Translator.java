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

import com.sun.source.tree.MethodInvocationTree;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.jdt.core.dom.*;
import theleo.jstruct.plugin.Log;
import theleo.jstruct.plugin.ecj.StructCache.*;
import static theleo.jstruct.plugin.ecj.StructCache.padding;

/**
 *
 * @author Juraj Papp
 */
public class Translator extends ASTVisitor {
	public static final String theleo = "theleo", jstruct = "jstruct", hidden = "hidden";
	public static final String HYB_CLS_OFFSETS = "$theleo_structoffsets$";
	public static final String STACK_OBJ = "$theleo_stack$";
	public static final String STACK_BASE = "$theleo_stackBase$";
	public static final String STACK_BASE_OBJ = "$theleo_stackBaseObj$";
	public static final String STACK_BASE_HI = "$theleo_stackBaseHI$";
	public static final String[] STACK = {"theleo", "jstruct", "hidden", "Stack"};
	public static final String[] AUTO_ARRAY = {"theleo", "jstruct", "hidden", "AutoArray"};
	public static final String[] AUTO_HYBRID = {"theleo", "jstruct", "hidden", "AutoHybrid"};
	public static final String[] MEM0 = {"theleo", "jstruct", "hidden", "Mem0"};
	public static final String[] MEM0_U = {"theleo", "jstruct", "hidden", "Mem0", "u"};
	public static final String[] BOXED_LONG = {"java", "lang", "Long"};
	
	public static final String TYPEBIND_PROP = "theleo.jstruct.CUSTOM_TYPEBIND";
	public static final String TYPEBIND_METHOD = "theleo.jstruct.METHOD";
	public static final String TYPEBIND_REF = "theleo.jstruct.REF";
	
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
	
	AST ast;
	TypeTranslator typeTranslator;
//	FieldTranslator fieldTranslator;
	StackAllocInitializerChecker stackAllocChecker;
	public Translator(CompilationUnit cu) {
		ast = cu.getAST();
		typeTranslator = new TypeTranslator();
//		fieldTranslator = new FieldTranslator();
		stackAllocChecker = new StackAllocInitializerChecker();
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
				if(oldProp != null && neww.getProperty(TYPEBIND_PROP) == null) {
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
//			if(dims > 1) throw new CompilerError("Multidimensional arrays not implemented. " + dims);
			SimpleType type = (dims > 1) ? type(theleo, jstruct, hidden, (e.hasJavaObjects()?"HybN":"RefN"))
			: type(theleo, jstruct, hidden, (e.hasJavaObjects()?"Hyb1":"Ref1"));						
			
//			SimpleType type = type(theleo, jstruct, hidden, (e.hasJavaObjects()?"Hyb":"Ref")+dims);						
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
//			if(dims.size() > 1) throw new CompilerError("Multidimensional arrays not implemented. " + dims.size());
//			ASTNode expr = copy((ASTNode)dims.get(0));
			
			MethodInvocation m = ast.newMethodInvocation();
			m.setExpression(name(MEM0));
			
			if(e.hasJavaObjects()) {
				m.setName(name("allocHybrid"));
				List args = m.arguments();
//				args.add(expr);
				args.add(returnLong(e.structSize));
				args.add(returnBool(e.zero));
				args.add(returnLong(e.globalObjCount));
				
				FieldAccess fa = ast.newFieldAccess();
				fa.setExpression(ast.newName(e.qualifiedName));
				fa.setName(name(HYB_CLS_OFFSETS));
				args.add(fa);
				
				for(int i = 0; i < dims.size(); i++)
					args.add(copy((ASTNode)dims.get(i)));
//				for(int i = 0; i < e.objOffsets.length; i++) {
//					args.add(returnLong(e.objOffsets[i]));
//					args.add(returnLong(e.objCounts[i]));
//				}
			}
			else {
				m.setName(name("alloc"));
				List args = m.arguments();
//				args.add(expr);
				args.add(returnLong(e.structSize));
				args.add(returnBool(e.zero));
				for(int i = 0; i < dims.size(); i++)
					args.add(copy((ASTNode)dims.get(i)));
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
			ASTNode name = (node.getArray());
//			ASTNode index = copy(node.getIndex());
			
//			System.err.println("Array is " + node.getArray() + " index " + index) ;
			
			MethodInvocation m = ast.newMethodInvocation();
			List args = m.arguments();
			args.add(copy(node.getIndex()));
			
			while(name instanceof ArrayAccess) {
				ArrayAccess aa = (ArrayAccess)name;
				name = aa.getArray();
				args.add(0,copy(aa.getIndex()));
			}
					
			m.setExpression((Expression)copy(name));
			m.setName(name("getIndex"));
					
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

//						System.out.println("field " + node);
//						return true;
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
	
	public void replaceFieldAccessJava(Entry e, Expression node, Expression translatedExpr,String identifier, IBinding binding) {
		if(REF_SAFE.equals(node.getProperty(TYPEBIND_REF))) return;
		
		if(binding.getKind() == IBinding.VARIABLE) {
			IVariableBinding v = (IVariableBinding)binding;
			if(v.isField()) {
				node.setProperty(TYPEBIND_REF, REF_SAFE);
				FieldAccess fa = ast.newFieldAccess();
				fa.setExpression((Expression)ASTNode.copySubtree(ast, translatedExpr));
				fa.setName(name(identifier));

				MethodInvocation m = ast.newMethodInvocation();
				m.setExpression(name(MEM0));
				m.setName(name("ref"));
				m.arguments().add(fa);
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
			
			switch(node.getOperator().toString()) {
				case "++":
					m.setName(name(m.getName()+"PreIncr"));
					break;
				case "--":
					m.setName(name(m.getName()+"PreDecr"));
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
			m.setExpression(name(MEM0));

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
			
			//there are only two postfix operators
			switch(node.getOperator().toString()) {
				case "++":
					m.setName(name(m.getName()+"PostIncr"));
					break;
				case "--":
					m.setName(name(m.getName()+"PostDecr"));
					break;
				default:
					throw new CompilerError("Unknown postfix operator: " + node.getOperator().toString());
			}
			m.setExpression(name(MEM0));

			replace(node, ASTNode.copySubtree(ast,m));
		}
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
		
		Entry e;//  = entry(left);
				
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
			if(e == null) {
				e = entry(left);
				if(e != null) {
					//Assignment to reference
					if(node.getOperator() != Assignment.Operator.ASSIGN) {
						throw new CompilerError("Assignment operator not supported...");
					}
					
					FieldAccess fa = ast.newFieldAccess();
					fa.setExpression(expr);
					fa.setName(name(select.getIdentifier()));
					
					node.setLeftHandSide(fa);
					node.setRightHandSide((Expression)translate(node.getRightHandSide()));
					
					return false;
				}
			}
			else {
				FieldEntry ft = e.offsetTable.get(select.getIdentifier());
				if(ft == null) CompilerError.exec(CompilerError.STRUCT_FIELD_NOT_FOUND, e.binaryName + " . " + select.getIdentifier());
				
				Expression rhs = node.getRightHandSide();
//				if(rhs instanceof InfixExpression) {
//					InfixExpression inf = (InfixExpression)rhs;
//					if(!inf.hasExtendedOperands()) {
//						
//					}
//				}
				
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
					if(node.getOperator() != Assignment.Operator.ASSIGN) {
						throw new CompilerError("Assignment operator not supported on struct.");
					}
					
					err("STRUCT TYpe");
					replace(node, copyObject(ft.structType, rhs, args));
					return false;
				}
				else if(ft.isReference()) {
					if(node.getOperator() != Assignment.Operator.ASSIGN) {
						throw new CompilerError("Assignment operator not supported on reference.");
					}
					
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
					String metName = "put";
					if(node.getOperator() != Assignment.Operator.ASSIGN) {
						switch(node.getOperator().toString()) {
							case "+=":   metName = "putAdd"; break;
							case "-=":   metName = "putSub"; break;
							case "*=":	 metName = "putMul"; break;
							case "/=":   metName = "putDiv"; break;
							case "&=":   metName = "putAnd"; break;
							case "|=":   metName = "putOr"; break;
							case "^=":   metName = "putXor"; break;
							case "%=":   metName = "putMod"; break;
							case "<<=":  metName = "putLShift"; break;
							case ">>=":  metName = "putRShift"; break;
							case ">>>=": metName = "putRRShift"; break;
							default:
								throw new CompilerError("Unknown operator " + node.getOperator().toString());
						}
					}
					
					MethodInvocation m = ast.newMethodInvocation();
//					m.setExpression(name((ft.type==FieldType.OBJECT || ft.type == FieldType.BOOLEAN || node.getOperator() != Assignment.Operator.ASSIGN)?MEM0:MEM0_U));
					m.setExpression(name(MEM0));
					m.setName(name(metName+ft.type.name));
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
			if(node.getLeftHandSide() instanceof SimpleName) {
				SimpleName sn = (SimpleName)node.getLeftHandSide();
				//Assignment to reference
				if(node.getOperator() != Assignment.Operator.ASSIGN) {
					throw new CompilerError("Assignment operator not supported.");
				}
				node.setLeftHandSide((Expression)ASTNode.copySubtree(ast,sn));
				
				Expression rhs = node.getRightHandSide();
				if(rhs instanceof NullLiteral) {
					IBinding b = sn.resolveBinding();
					if(b instanceof IVariableBinding) {
					IVariableBinding vb = (IVariableBinding)b;
						if(!vb.isField()) {
							throw new CompilerError("Local Reference " + node + " cannot be set to null.");
						}
					}
					
					node.setRightHandSide(returnLong(0));
				}
				else node.setRightHandSide((Expression)copy(rhs));
				return false;
			}
			
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
						case "len":
//							e = typeLiteral(node.arguments().get(0));
							
							MethodInvocation mi2 = ast.newMethodInvocation();
							mi2.setExpression((Expression)copy((Expression)node.arguments().get(0)));
							mi2.setName(name("getLength"));
							mi2.arguments().add((Expression)copy((Expression)node.arguments().get(1)));
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
						case "stack":
							e = typeLiteral(node.arguments().get(0));
							MethodFrame mf = getMethod();
							NumberLiteral pos = (NumberLiteral)returnLong(0);
							
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
							n = copy((Expression)node.arguments().get(0));
//							n.setProperty(TYPEBIND_PROP, FieldType.LONG);
							
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
			
			if(e.hasJavaObjects()) {
				//Store objects offsets/counts in static field
				//to speed up allocation
				
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
				
				VariableDeclarationFragment var = ast.newVariableDeclarationFragment();
				var.setName(name(HYB_CLS_OFFSETS));
				var.setInitializer(arr);
				
				FieldDeclaration dec = ast.newFieldDeclaration(var);
				List mods = dec.modifiers();
				mods.add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
				mods.add(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
				mods.add(ast.newModifier(Modifier.ModifierKeyword.FINAL_KEYWORD));
				dec.setType((Type)copy(arr.getType()));
				l.add(dec);
			}
			
			return false;
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
//		node.accept(fieldTranslator); 
		//translated types...

//		Entry e = entry(node.getType());
//		if(e != null) {
//			PrimitiveType type = ast.newPrimitiveType(PrimitiveType.LONG);
//			replace(node, type);
//			return false;
//		}
//		return false;
//		return false;

		Entry e = entry(node.getType());
		if(e != null) {
			PrimitiveType type = ast.newPrimitiveType(PrimitiveType.LONG);
			
			FieldDeclaration copy = (FieldDeclaration)ASTNode.copySubtree(ast, node);
			copy.setType(type);
			
			replace(node, copy);
			return false;
		}

		return super.visit(node);
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
		ArrayList<StackAllocReq> stackAlloc;
		public void add(Entry e, NumberLiteral nm) {
			if(stackAlloc == null) stackAlloc = new ArrayList<>();
			stackAlloc.add(new StackAllocReq(stackAlloc.size(), e, nm));
		}
		public void add(Entry e, NumberLiteral nm,NumberLiteral posObj) {
			if(stackAlloc == null) stackAlloc = new ArrayList<>();
			stackAlloc.add(new StackAllocReq(stackAlloc.size(), e, nm, posObj));
		}
	}
	
	
	
	ArrayList<MethodFrame> methodStack = new ArrayList<>();
	public MethodFrame getMethod() {
		return methodStack.get(methodStack.size()-1);
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		IMethodBinding methodBinding = node.resolveBinding();
		
		if(methodBinding != null) {
			IAnnotationBinding[] ab = methodBinding.getAnnotations();
			if(ab != null) {
				for(int i = 0; i < ab.length; i++) {
					IAnnotationBinding aa = ab[i];
					String aName = aa.getAnnotationType().getQualifiedName();
					if(aName.equals("theleo.jstruct.ReturnStruct")) {
						PrimitiveType type = ast.newPrimitiveType(PrimitiveType.LONG);
						node.setReturnType2(type);
					}
				}
			}
		}
		
		methodStack.add(new MethodFrame());
		return true;
	}

	@Override
	public void endVisit(MethodDeclaration node) {
		MethodFrame mFrame = methodStack.remove(methodStack.size()-1);
		if(mFrame.stackAlloc != null) {			
			boolean reorder = false;
			int stackSize = 0;
			ArrayList<StackAllocReq> fields = mFrame.stackAlloc;
			int stackSizeObj = 0;
			for(int i = 0; i < fields.size(); i++) {
				StackAllocReq r = fields.get(i);
				Entry e = r.type;
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
			t.setBody((Block)ASTNode.copySubtree(ast, node.getBody()));
			t.setFinally(finallyBlock);
			
			Block newbody = ast.newBlock();
			List statements = newbody.statements();
			statements.add(stackObj);
			statements.add(stackBase);
			
			if(stackSizeObj != 0) {
				MethodInvocation getObj = ast.newMethodInvocation();
				getObj.setExpression(name(STACK_OBJ));
				getObj.setName(name("getObj"));
				getObj.arguments().add(returnInt(stackSizeObj));
				getObj.setProperty(TYPEBIND_PROP, FieldType.INT);
				
				VariableDeclarationFragment fragBaseObj = ast.newVariableDeclarationFragment();
				fragBaseObj.setName(name(STACK_BASE_OBJ));
				fragBaseObj.setInitializer(getObj);
			
				VariableDeclarationStatement stackBaseObj = ast.newVariableDeclarationStatement(fragBaseObj);
				stackBaseObj.setType(ast.newPrimitiveType(PrimitiveType.INT));
				statements.add(stackBaseObj);
				
				FieldAccess fa = ast.newFieldAccess();
				fa.setExpression(name(STACK_OBJ));
				fa.setName(name("hybridIndex"));
				
				VariableDeclarationFragment fragBaseObj2 = ast.newVariableDeclarationFragment();
				fragBaseObj2.setName(name(STACK_BASE_HI));
				fragBaseObj2.setInitializer(fa);
			
				VariableDeclarationStatement stackBaseObj2 = ast.newVariableDeclarationStatement(fragBaseObj2);
				stackBaseObj2.setType(ast.newPrimitiveType(PrimitiveType.INT));
				statements.add(stackBaseObj2);
			}
			
			statements.add(t);
			node.setBody(newbody);
		}
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
}
