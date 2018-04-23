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
import java.util.Iterator;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.internal.corext.dom.ASTFlattener;

/**
 *
 * @author Juraj Papp
 */
public class ReadableFlattener extends ASTFlattener {
	ArrayList<Integer> semicolons;
	public ReadableFlattener(CharSequence formattedCode) {
		semicolons = semicolons(formattedCode);
	}
	
	int currentLine = 1;
	int lastCheck = 0;
	
	int tabs = 0;
	
	public void lineBreak2() {
		int len = fBuffer.length();
		for(int i = lastCheck; i < len; i++) {
			if(fBuffer.charAt(i) == '\n') currentLine++;
		}
		currentLine++;
		fBuffer.append('\n');
		for (int i = 0; i < tabs; i++) {
			fBuffer.append(' ');
		}
		lastCheck = fBuffer.length();
	}

	public void lineBreak() {
//		currentLine++;
//		fBuffer.append('\n');
//		for (int i = 0; i < tabs; i++) {
//			fBuffer.append(' ');
//		}
	}

	public boolean br(ASTNode node) {
		return node instanceof ExpressionStatement || node instanceof VariableDeclarationStatement || node instanceof BreakStatement || node instanceof ContinueStatement || node instanceof PackageDeclaration || node instanceof ImportDeclaration || node instanceof Javadoc;
	}
	
	

	@Override
	public void preVisit(ASTNode node) {
//		int li = comp.getLineNumber(node.getStartPosition());
//		if(li > 0) {
//			while(li > currentLine) {
//				fBuffer.append("//" + li + "," + currentLine);
//				lineBreak2();
//			}
//		}
		
		if (node instanceof BodyDeclaration) {
			tabs += 4;
			lineBreak();
		}
		super.preVisit(node);
	}

	@Override
	public void postVisit(ASTNode node) {
		if (node instanceof BodyDeclaration) {
			tabs -= 4;
			lineBreak();
		}
		if (br(node)) {
			lineBreak();
		}
		
		super.postVisit(node);
	}

	@Override
	public boolean visit(Block node) {
		tabs += 4;
		this.fBuffer.append("{"); //$NON-NLS-1$
		lineBreak();
		for (Iterator<Statement> it = node.statements().iterator(); it.hasNext();) {
			Statement s = it.next();
			s.accept(this);
		}
		tabs -= 4;
		lineBreak();
		this.fBuffer.append("}"); //$NON-NLS-1$
		return false;
	}
	public static ArrayList<Integer> semicolons(CharSequence code) {
		int line = 1;
		ArrayList<Integer> list = new ArrayList<>();
		char last = 0;
		int mode = 0;
		for(int i = 0; i < code.length()-1; i++) {
			char ch = code.charAt(i);
			if(mode == 0) {
				switch(ch) {
					case '/':
						if(code.charAt(i+1) == '*')  {
							i++;
							mode = 1;
						}
						else if(code.charAt(i+1) == '/') {
							i++;
							for(; i < code.length(); i++)
								if(code.charAt(i) == '\n') break;
							line++;
						}
						break;
					case '\'':
						mode = 2;
						break;
					case '"':
						mode = 3;
						break;
					case ';':
						list.add(line);
						break;
					case '\n':
						line++;
						break;
				}
			}
			else if(mode == 1) {
				if(ch == '*' && code.charAt(i+1) == '/') {
					mode = 0;
				}
			}
			else if(mode == 2) {
				if(ch == '\'' && last != '\\') {
					mode = 0;
				}
			}
			else if(mode == 3) {
				if(ch == '"' && last != '\\') {
					mode = 0;
				}
			}
			if(i < code.length()) last = code.charAt(i);
		}
		
		return list;
	}
}
