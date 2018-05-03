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
package theleo.jstruct.plugin;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import theleo.jstruct.plugin.ecj.StructCache;
import theleo.jstruct.plugin.ecj.Translator;

/**
 *
 * @author Juraj Papp
 */
public class SourceCompiler {
	public static final String jstruct_version = "1.1.1";

	public static class Args {
		
		public String unitname = null;
		public String sourceVersion = null;
		public String[] sourcepath = null;
		public String[] sourcefiles = null;
		public String[] classpath = null;
		public String outpath = null; 
		public boolean includeVMBoothpath = true;
		public boolean ignoreAstError = false;

		
		public Args(String[] args) throws Exception {
			for(int i = 0; i < args.length; i++) {
				boolean hasNext = i+1 < args.length;
				String str = args[i].trim();
				
				switch(str.toLowerCase()) {
					case "-unitname":
						if(!hasNext) throw new IllegalArgumentException("Unit name not set");
						unitname = trimQuotes(args[++i]);
						break;
					case "-version":
					case "-source":
						if(!hasNext) throw new IllegalArgumentException("Source version not set: -source <1.8, 10 etc>");
						sourceVersion = trimQuotes(args[++i]);
						break;
					case "-classpath":
					case "-cla":
					case "-cp":
						if(!hasNext) throw new IllegalArgumentException("Classpath not specified! -cp <path:path2...>");
						String cp = trimQuotes(args[++i]);
						classpath = cp.split("[;:]");
						break;
					case "-inpath":
					case "-inpaths":
					case "-sourcepath":
						if(!hasNext) throw new IllegalArgumentException("Source directory not specified!");
						String sp = trimQuotes(args[++i]);
						sourcepath = sp.split("[;:]");
						break;
					case "-out":
					case "-outpath":
					case "-outputpath":
						if(!hasNext) throw new IllegalArgumentException("Output directory not specified!");
						outpath = trimQuotes(args[++i]);
						break;
					case "-LnoVMboothpath":
						includeVMBoothpath = false;
						break;
					case "-infile":
					case "-infiles":
					case "-sourcefiles":
						if(!hasNext) throw new IllegalArgumentException("Source directory not specified!");
						String sf = trimQuotes(args[++i]);
						sourcefiles = sf.split("[;:]");
						break;
					case "-incrementalsource":
						if(!hasNext) throw new IllegalArgumentException("Source directory not specified!");
						String incr = args[++i];
						sourcefiles = array(filter(fromQuotedList(incr), ".java"));
						break;
					case "-ignoreAstError":
						ignoreAstError = true;
						break;
					case "-debug":
						Log.enabled = true;
						break;
					default:
						System.err.println("Unknown argument: " + str);
				}
			
			}
			
			if(sourceVersion == null) throw new IllegalArgumentException("Source version not set: -source <1.8, 10 etc>");
			if(sourcepath == null) sourcepath = new String[0];
			else sourcepath = filterNonExisting(sourcepath);
			if(classpath == null) classpath = new String[0];
			else classpath = filterNonExisting(classpath);
			if(outpath == null) throw new IllegalArgumentException("Output directory not specified: -d <path> ");
			if(sourcefiles == null || sourcefiles.length == 0) {
				//find all java files
				ArrayList<String> files = new ArrayList<>();
				for(String src : sourcepath) {
					add(new File(src), files);
				}
				sourcefiles = files.toArray(new String[files.size()]);
			}
			
			System.err.println("Classpath " + Arrays.toString(classpath));
			System.err.println("Sourcepath " + Arrays.toString(sourcepath));
			System.err.println("Sourcefiles " + Arrays.toString(sourcefiles));
			System.err.println("Outpath " + outpath);
		}
		private static void add(File dir, ArrayList<String> list) throws Exception {
			if(dir == null || !dir.isDirectory()) {
				System.err.println("File " + dir + " is not a directory! ");
				return;
			}
			File[] files = dir.listFiles();
			
			for (File file : files) {
				if (file.isDirectory()) {
					add(file, list);
				} else {
					list.add(file.getCanonicalPath());
				}
			}
		}

		private static String[] array(ArrayList<String> list) { return list.toArray(new String[list.size()]);}
		private static ArrayList<String> filter(ArrayList<String> list, String ext) {
			for(int i = list.size()-1; i >= 0; i--) {
				if(!list.get(i).endsWith(ext)) list.remove(i);
			}
			return list;
		}
		private static ArrayList<String> fromQuotedList(String str) {
			ArrayList<String> list = new ArrayList<>();
			int current = -1;
			int from = 0;
			for(int i = 0; i < str.length(); i++) {
				char ch = str.charAt(i);
				if(current == -1) {
					if(Character.isWhitespace(ch)) continue;
					if(ch == '"') { current = '"'; from = i+1; continue; }
					if(ch == '\'') { current = '\''; from = i+1; continue; }
					current = ' '; from = i;
				}
				else {
					if(ch == current) { list.add(str.substring(from, i).trim()); current = -1; continue; }
				}
			}
			return list;
		}
		private static String trimQuotes(String str) {
			if(str.length() < 2) return str;
			if(str.charAt(0) ==  '"' && str.charAt(str.length()-1) == '"') return str.substring(1, str.length()-1);
			if(str.charAt(0) == '\'' && str.charAt(str.length()-1) == '\'') return str.substring(1, str.length()-1);
			return str;
		}
	}
	static int success = 0;
	public static void main(String[] rawArgs) {
		System.err.println("junion v" + jstruct_version + ':' + Arrays.toString(rawArgs));
		
		try {
			Args args = new Args(rawArgs);
			
			File outpathFile = new File(args.outpath);
			if(!outpathFile.exists()) { outpathFile.mkdir(); }
			else if(!outpathFile.isDirectory()) {
				throw new IllegalArgumentException("Output path: " + outpathFile + " is not a directory.");
			}
			
			ASTParser parser = ASTParser.newParser(AST.JLS10);
			
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setResolveBindings(true);
			//TODO
			if(args.unitname != null) parser.setUnitName(args.unitname);
			parser.setBindingsRecovery(true);
			parser.setStatementsRecovery(true);

			Map options = JavaCore.getOptions();
			
			options.put(JavaCore.COMPILER_COMPLIANCE, args.sourceVersion);
			options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, args.sourceVersion);
			options.put(JavaCore.COMPILER_SOURCE, args.sourceVersion);

			parser.setCompilerOptions(options);
			
			
			String[] classpath = args.classpath;
			String[] sources = args.sourcepath;
			String[] sourceFilePaths = args.sourcefiles;
			
			

			//TODO
//			String[] encodings = new String[sources.length];
//			for(int i = 0; i < encodings.length; i++) encodings[i] = "UTF-8";
			String[] encodings = null;
			parser.setEnvironment(classpath, sources, encodings, args.includeVMBoothpath);
			
			URL[] urls = new URL[classpath.length];
			for(int i = 0; i < classpath.length; i++) {
				urls[i] = Paths.get(classpath[i]).toUri().toURL();
				Log.err("URL " + urls[i]);
			}
									 
			StructCache.loader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
			FileASTRequestor ast = new FileASTRequestor() {
				@Override
				public void acceptAST(String sourceFilePath, CompilationUnit ast) {
					try {
						Log.err("Source " + sourceFilePath);

						StructCache.currentAST = ast.getAST();
						ast.recordModifications();
						Translator translator = new Translator(ast);
						ast.accept(translator);
						
//						ASTFlattener flat = new ASTFlattener();
//						ASTFlattener flat = new ReadableFlattener(ast);
//						ast.accept(flat);
						
						Document doc = new Document(new String(Files.readAllBytes(Paths.get(sourceFilePath))));
						TextEdit te = ast.rewrite(doc, null);
						te.apply(doc);
						String result = doc.get();
						
//						String result = flat.getResult();
						Log.err(result);
						
						IProblem[] problems = ast.getProblems();
						for(int i = 0; i < problems.length; i++) {
							IProblem p = problems[i];
							if(p.isError()) {
								if(!args.ignoreAstError) success = 1;
							}
							printProblem(p);
						}
						String packageName = null;
						PackageDeclaration pkg = ast.getPackage();
						if(pkg != null) {
							packageName = pkg.getName().getFullyQualifiedName();
						}
						
						String fileName = new File(sourceFilePath).getName();
						
						File output = null;
						if(packageName == null) output = new File(outpathFile, fileName);
						else {
							File dir = Paths.get(args.outpath, packageName.split("\\.")).toFile(); 
							if(!dir.exists()) dir.mkdirs();
							output = new File(dir, fileName);
						}
						
						if(output.exists()) {
							try(Scanner in = new Scanner(output)) {
								String line = in.nextLine();
								if(!line.startsWith("/*AUTO-GENERATED")) {
									throw new IllegalArgumentException("File "+ output + " already exists. ");
								}
							}
						}
						String header = "/*AUTO-GENERATED DO NOT EDIT (jstruct"+jstruct_version+")*/";
						try (PrintWriter out = new PrintWriter(output)) {
							out.print(header);
							out.println(result);
						}				
					} catch (Exception ex) {
						success = 1;
						ex.printStackTrace();
					}
				}

				@Override
				public void acceptBinding(String bindingKey, IBinding binding) {
					Log.err("Binding " + bindingKey + ", " + binding);
				}

			};
			
			
			parser.createASTs(sourceFilePaths, encodings, new String[]{}, ast, null);			
		} catch (Exception ex) {
			ex.printStackTrace();
			success = 1;
		}
		
		System.exit(success);
	}
	public static void printProblem(IProblem p) {
		if(p.isError()) {
			System.err.print("ERROR: ");
		}
		else if(p.isWarning()) {
			System.err.print("WARNING: ");
		}
		else if(p.isInfo()) {
			System.err.print("INFO: ");
		}
		else System.err.print("UNKNOWN: ");
		System.err.println(p.getMessage());
		String f = String.valueOf(p.getOriginatingFileName());
		if(f != null) {
			f = new File(f).getName();
		}
		
		System.err.println("\t at " + "("+f+":"+p.getSourceLineNumber()+")");
	}
	
	public static ASTParser createParser(String sourceVersion) {
		ASTParser parser = ASTParser.newParser(AST.JLS10);

		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		//TODO
//		parser.setUnitName(unitName);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);

		Map options = JavaCore.getOptions();
		
		options.put(JavaCore.COMPILER_COMPLIANCE, sourceVersion);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, sourceVersion);
		options.put(JavaCore.COMPILER_SOURCE, sourceVersion);

		parser.setCompilerOptions(options);

		return parser;
	}
	public static String[] filterNonExisting(String[] files) {
		ArrayList<String> list = new ArrayList<>();
		for(int i = 0; i < files.length; i++) {
			String path = files[i].trim();
			File f = new File(path);
			if(f.exists()) list.add(path);
			else System.err.print("WARNING: path " + path + " does not exists. Ignoring.");
		}
		return list.toArray(new String[list.size()]);
	}
	public static int count(String s, char ch) {
		int c = 0;
		for(int i = 0;i < s.length(); i++) {
			if(s.charAt(i) == ch) c++; 
		}
		return c;
	}
	public static String extractPackageName(CharSequence cs) {
		boolean record = false;
		int comment = 0;
		StringBuilder packageName = new StringBuilder();
		for(int i = 0; i < cs.length()-7; i++) {
			char ch = cs.charAt(i);
			
			if(comment == 1) {
				if(ch == '*' && cs.charAt(i+1) == '/') {
					i++;
					comment = 0;
				}
				continue;
			}
			if(comment == 2) {
				if(ch == '\n') {
					comment = 0;
				}
				continue;
			}
			if(ch == ' ') continue;
			
			if(ch == '/' ) {
				if(cs.charAt(i+1) == '*') {
					i++;
					comment = 1;
					continue;
				}
				if(cs.charAt(i+1) == '/') {
					i++;
					comment = 2;
					continue;
				}
			}
			if(ch == ';') break;
			
			if(record) {
				packageName.append(ch);
			}
			else if(ch == 'p' && i+7 < cs.length()) {
				if("package".contentEquals(cs.subSequence(i, i+7))) {
					i += 7;
					record = true;
				}
			}
		}
		return record?packageName.toString():null;
	}
}
