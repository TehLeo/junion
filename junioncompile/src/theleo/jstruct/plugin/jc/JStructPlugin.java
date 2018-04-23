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
package theleo.jstruct.plugin.jc;

import com.sun.source.util.*;
import com.sun.tools.javac.api.BasicJavacTask;
import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.main.Option;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.CharBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.processing.Processor;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.internal.corext.dom.ASTFlattener;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import sun.misc.Unsafe;
import theleo.jstruct.plugin.Log;
import theleo.jstruct.plugin.SourceCompiler;
import theleo.jstruct.plugin.ecj.CompilerError;
import theleo.jstruct.plugin.ecj.ReadableFlattener;
import theleo.jstruct.plugin.ecj.StructCache;
import theleo.jstruct.plugin.ecj.Translator;

/**
 *
 * @author Juraj Papp
 */
public class JStructPlugin implements Plugin {
	public static final String NAME = "junion";

	@Override
	public String getName() {
		return NAME;
	}
	
	public static String[] classPaths;

	@Override
	public void init(JavacTask task, String... args) {
		if(args != null) {
			for(String s : args) {
				s = s.trim();
				switch(s) {
					case "-debug": Log.enabled = true; break;
				}
			}
		}
		
		try {
			Log.err("ARGS " + Arrays.toString(args));
			Log.err("CLS : " + getClass().getClassLoader().getClass());
			
			ClassLoader cl = getClass().getClassLoader();
			URL[] currentClassPath = ((URLClassLoader) cl).getURLs();
			String[] classPathStrings = Arrays.stream(currentClassPath).map(url -> {
				try {
					return Paths.get(url.toURI()).toAbsolutePath().toString();
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}
			}).toArray(String[]::new);
			Log.err(Arrays.asList(classPathStrings));
			classPaths = classPathStrings;
			
			Context context = ((BasicJavacTask) task).getContext();
//			sourceVersion = Source.instance(context).name;
			wrapCompiler(context);
			
			
			
		} catch (Exception ex) {
			Logger.getLogger(JStructPlugin.class.getName()).log(Level.SEVERE, null, ex);
			throw new CompilerError("error: ", ex);
		}
	}
	
	
	
	public static <T> T read(Class c, String name) {
        try {
            java.lang.reflect.Field f = c.getDeclaredField(name);
            f.setAccessible(true);
            return (T)f.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	public static <T> T read(Object o, String name) {
        try {
            java.lang.reflect.Field f = o.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return (T)f.get(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public static void wrapCompiler(Context c) throws Exception {
		Context.Key<JavaCompiler> compilerKey = read(JavaCompiler.class, "compilerKey");
		JavaCompiler comp = c.get(compilerKey);
		
		Unsafe u = getUnsafe();
		JavaCompilerWrapper jcw = (JavaCompilerWrapper)u.allocateInstance(JavaCompilerWrapper.class);
		//now gotta copy all the nonstatic fields
		Class jc = JavaCompiler.class;
		Field[] fs = jc.getDeclaredFields();
		for(Field f : fs) {
			if(!Modifier.isStatic(f.getModifiers())) {
				f.setAccessible(true);
				f.set(jcw, f.get(comp));
			}
		}
		c.put(compilerKey, (JavaCompiler)null);
		c.put(compilerKey, (JavaCompiler)jcw);
	}
	
	
	public static class JavaCompilerWrapper extends JavaCompiler {

		public JavaCompilerWrapper() {
			super(null);
		}

		@Override
		public void compile(List<JavaFileObject> sourceFileObjects, List<String> classnames, Iterable<? extends Processor> processors) {
			Log.err("JAVA COMPILER WRAPPER " + sourceFileObjects.size());
			Log.err("Class Names " + classnames + ", " + classnames.size());
			
			Log.err("DJAVA_ENDORSED_DIRS " + options.get(Option.DJAVA_ENDORSED_DIRS));
			Log.err("DJAVA_EXT_DIRS " + options.get(Option.DJAVA_EXT_DIRS));
			Log.err("ENDORSEDDIRS " + options.get(Option.ENDORSEDDIRS));
			Log.err("EXTDIRS " + options.get(Option.EXTDIRS));
			Log.err("SourcePath " + options.get(Option.SOURCEPATH));
			Log.err("SourceFile " + options.get(Option.SOURCEFILE));
			Log.err("Source " + options.get(Option.SOURCE));
			Log.err("D " + options.get(Option.D));
			Log.err("CP " + options.get(Option.CP));
			Log.err("CLASSPATH " + options.get(Option.CLASSPATH));
			Log.err("PROCPATH " + options.get(Option.PROCESSORPATH));
			Log.err("VERSION " + options.get(Option.VERSION));
			
			HashSet<String> sourcePathMap = new HashSet<>();
			
			
			
			
			
			Log.err("ARGS " + ManagementFactory.getRuntimeMXBean().getInputArguments());
			
//			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//			StandardJavaFileManager fm = compiler.getStandardFileManager(null, null, null);

//			Iterable<? extends File> locations = fm.getLocation(StandardLocation.SOURCE_PATH);
			
			ArrayList<JavaFileObject> javaFiles = new ArrayList<>();
			ArrayList<JavaFileObject> translated = new ArrayList<>(sourceFileObjects.size());
			
			HashMap<String, WrappedSourceFile> sourceFiles = new HashMap<>();
			
			try {
				for(int i = 0; i < sourceFileObjects.size(); i++) {

						JavaFileObject jfo = sourceFileObjects.get(i);
						Log.err("JAVA COMPILER WRAPPER " + jfo + ", " + jfo.getClass());
						Log.err(" SIMPLE NAME " + jfo.getName());
						
						if(jfo.getKind() == JavaFileObject.Kind.SOURCE) {
							CharSequence seq = jfo.getCharContent(true);
							WrappedSourceFile wf = new WrappedSourceFile(jfo);
							wf.setContent(seq);
							wf.setPackageName(SourceCompiler.extractPackageName(seq));
							
							Log.err(" PACKAGE " + wf.packageName);
							
							sourceFiles.put(new File(jfo.toUri()).getCanonicalPath(), wf);
							
							File file = new File(jfo.toUri()).getParentFile();
							if(file != null) {
								if(wf.packageName == null) {
									sourcePathMap.add(file.getCanonicalPath());
								}
								else {
									int dot1 = 1+SourceCompiler.count(wf.packageName, '.');
									Log.err("DOTS " + dot1);
									Log.err("F~ILE  " + file);
									for(int n = 0; n < dot1 && file != null; n++)
										file = file.getParentFile();
									if(file != null) sourcePathMap.add(file.getCanonicalPath());
								}
							}
							
							translated.add(wf);
							javaFiles.add(wf);
						}
						else translated.add(jfo);						
				}
				
//				org.eclipse.jdt.core.ICompilationUnit[] units = from(javaFiles);
				String sourcePaths[] = sourcePathMap.toArray(new String[sourcePathMap.size()]);
				Log.err("SOURCE PATHS " + Arrays.toString(sourcePaths));

				String sourceVersion = options.get(Option.SOURCE);
				
				Log.err("Source Version " + sourceVersion);
				ASTParser parser = SourceCompiler.createParser(sourceVersion);
				parser.setEnvironment(classPaths, sourcePaths, null, true);
				
				
				
				URL[] urls = new URL[classPaths.length];
				for(int i = 0; i < classPaths.length; i++) {
					urls[i] = Paths.get(classPaths[i]).toUri().toURL();
					Log.err("URL " + urls[i]);
				}

				StructCache.loader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
				FileASTRequestor ast = new FileASTRequestor() {
					@Override
					public void acceptAST(String sourceFilePath, CompilationUnit comp) {
						super.acceptAST(sourceFilePath, comp); //To change body of generated methods, choose Tools | Templates.
					
						try {
							Log.err("Source " + sourceFilePath);

							StructCache.currentAST = comp.getAST();
							
							comp.recordModifications();
							Translator translator = new Translator(comp);
							comp.accept(translator);
							
							
							
							WrappedSourceFile ws = sourceFiles.get(sourceFilePath);
							Log.err("WS " + ws);
							if(ws != null) {
								//ws.content//
								Document doc = new Document(ws.content.toString());
								TextEdit te = comp.rewrite(doc, null);
								te.apply(doc);
								String result = doc.get();
							
//								ASTFlattener flat = new ReadableFlattener(ws.content);
//								comp.accept(flat);

//								String result = flat.getResult();
								ws.setContent(result);

								Log.err("Source " + source);
								Log.err(result);
							
							}
							
							
//							if(source instanceof CompUnit) {
//								CompUnit cu = (CompUnit)source;
//								JavaFileObject jfo = cu.jfo;
//								if(jfo instanceof WrappedSourceFile) {
//									WrappedSourceFile wsf = (WrappedSourceFile)jfo;
//									wsf.setContent(result);
//								}
//							}

						} catch (Exception ex) {
							ex.printStackTrace();
							System.err.println(ex.getMessage());
							System.exit(1);
						}
					}
					@Override
					public void acceptBinding(String bindingKey, IBinding binding) {
						super.acceptBinding(bindingKey, binding);
					}					
				};	
				
				String[] sourceInputFiles = sourceFiles.keySet().toArray(new String[sourceFiles.size()]);
				Log.err("INPUT FILES " + Arrays.toString(sourceInputFiles));
				
//				org.eclipse.jdt.core.ICompilationUnit[] compilationUnits = new org.eclipse.jdt.core.ICompilationUnit[units.length];
//				System.arraycopy(units, 0, compilationUnits, 0, units.length);
//				parser.createASTs(units, new String[] {}, ast, null);

				parser.createASTs(sourceInputFiles, null, new String[]{}, ast, null);	
			} catch (Exception ex) {
				ex.printStackTrace();
				System.err.println(ex.getMessage());
				//throw new CompilerError("error ", ex);
				System.exit(1);
			}			
			Log.err("Going to Compile");
			
			super.compile(List.from(translated), classnames, processors); 
		}
		
	}
	public static class WrappedSourceFile implements JavaFileObject {
		private JavaFileObject target;
		public CharSequence content;
		
		public String packageName;

		public WrappedSourceFile(JavaFileObject target) {
			this.target = target;
		}
		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}
		public void setContent(CharSequence seq) { 
			content = seq;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return content;
		}

		@Override
		public Kind getKind() {
			return target.getKind();
		}

		@Override
		public boolean isNameCompatible(String simpleName, Kind kind) {
			return target.isNameCompatible(simpleName, kind);
		}

		@Override
		public NestingKind getNestingKind() {
			return target.getNestingKind();
		}

		@Override
		public javax.lang.model.element.Modifier getAccessLevel() {
			return target.getAccessLevel();
		}

		@Override
		public URI toUri() {
			return target.toUri();
		}

		@Override
		public String getName() {
			return target.getName();
		}

		@Override
		public InputStream openInputStream() throws IOException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public OutputStream openOutputStream() throws IOException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
			 CharSequence charContent = getCharContent(ignoreEncodingErrors);
			if (charContent == null)
				throw new UnsupportedOperationException();
			if (charContent instanceof CharBuffer) {
				CharBuffer buffer = (CharBuffer)charContent;
				if (buffer.hasArray())
					return new CharArrayReader(buffer.array());
			}
			return new StringReader(charContent.toString());
		}

		@Override
		public Writer openWriter() throws IOException {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}

		@Override
		public long getLastModified() {
			return target.getLastModified();
		}

		@Override
		public boolean delete() {
			return target.delete();
		}

		@Override
		public int hashCode() {
			return target.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof WrappedSourceFile)
				return obj.equals(this);
			return target.equals(obj);
		}
		
	}
	private static Unsafe getUnsafe() {
		try {
			Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
			singleoneInstanceField.setAccessible(true);
			return (Unsafe) singleoneInstanceField.get(null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	
		
//	public static class CompUnit extends org.eclipse.jdt.internal.compiler.batch.CompilationUnit {
//		public JavaFileObject jfo;
//		public CompUnit(char[] contents, String fileName, String encoding) {
//			super(contents, fileName, encoding);
//		}	
//	}
//	public static org.eclipse.jdt.core.ICompilationUnit[] from(java.util.List<JavaFileObject> obj) throws Exception {
//		org.eclipse.jdt.core.ICompilationUnit[] comp = new org.eclipse.jdt.core.ICompilationUnit[obj.size()];
//		
//		for(int i = 0; i < comp.length; i++) {
//			JavaFileObject jfo = obj.get(i);
//			CharSequence ch = jfo.getCharContent(true);
//			char[] arr = new char[ch.length()];
//			for(int j = 0; j < arr.length; j++) arr[j] = ch.charAt(j);
//			
//			CompUnit c = new CompUnit(arr, jfo.getName(), "UTF-8");
//			c.jfo = jfo;
//			comp[i] = c;
//		}
//		
//		return comp;
//	}
	
	/**/
	
	
}
