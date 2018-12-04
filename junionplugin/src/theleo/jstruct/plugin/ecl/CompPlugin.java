/*
 * Copyright (c) 2017, Juraj Papp
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
 * DISCLAIMED. IN NO EVENT SHALL COPYRIGHT HOLDER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package theleo.jstruct.plugin.ecl;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jdt.internal.core.JavaModelManager.PerProjectInfo;
import org.eclipse.jdt.internal.core.builder.IncrementalImageBuilder;
import org.eclipse.jdt.internal.core.builder.JavaBuilder;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.Bundle;

import static theleo.jstruct.plugin.ecl.JStructPlugin.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.Resources;



public class CompPlugin extends CompilationParticipant {
	
	/*
	 * 	https://www.eclipse.org/articles/article.php?file=Article-JavaCodeManipulation_AST/index.html
	 * IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("someJavaProject");
		project.open(null //IProgressMonitor );
				
		IJavaProject javaProject = JavaCore.create(project);
		IType lwType = javaProject.findType("net.sourceforge.earticleast.app.Activator");
		ICompilationUnit lwCompilationUnit = lwType.getCompilationUnit();
	 */
	
	static String GEN_FOLDER = "genFolder";
	static String COMPILE_LIBS = "compileLibs";
	static String SHOW_ERROR = "showerror";
	static String FILE_DESC = 
	"JStruct property file (1.0.2)\n"+
	GEN_FOLDER+"= name of folder to generate sources to\n"+
	COMPILE_LIBS+"= list of ';'(Windows)/':'(UNIX) separated compile time class path libraries ";
	
	static char[][] UNINIT_PATTERNS;
	static Field fullExclusionPatternChars;
	static Field exclusionPatterns;
	
	static Path[] ALL_PATHS = new Path[] { new Path("**") };
	static Path[] NONE_PATHS = new Path[] {  };
	
	public CompPlugin() {
		super();
		
	}
	Map<IJavaProject, ProjectData> cache = 
			Collections.synchronizedMap(new HashMap());
	public static class ProjectData {
		public long lastModification=-1;
		public Properties properties = new Properties();
		
		private HashMap<IClasspathEntry, IPath[]> paths = new HashMap<>();
	}
	
	
	
	IJavaProject current;
	
	@Override
	public void buildStarting(BuildContext[] files, boolean isBatch) {
		super.buildStarting(files, isBatch);
//		try {
//			
//		log("STARTING batch: " + isBatch);
//		
//		for(BuildContext bc : files) {
//			IFile file = bc.getFile();
//			
//			log(file.getName());
//			
//		}
//		if(current != null) {
//			IPackageFragment[] packages = current.getPackageFragments();
//			for (IPackageFragment packageFragment : packages) {
//				for (final ICompilationUnit compilationUnit : packageFragment.getCompilationUnits()) {
//					
//				}
//			}
//			//ICompilationUnit unit = ICompilationUnit.;
//		} 
//		else log("current is null");
//		
//		} catch (CoreException e) {
//			e.printStackTrace();
//		}
	}
	
	
	
	
	
	@Override
	public void processAnnotations(BuildContext[] files) {
		super.processAnnotations(files);
		log("Annotation batch: ");
		
	}
	@Override
	public boolean isAnnotationProcessor() {
		return true;
	}
	
	protected void doFinish(IJavaProject project) {
		if(project != null ) {
			
			try {
				
				ProjectData data = cache.get(project);
				if(data == null) {
					log("Unexpected error: project data is null");
					Thread.dumpStack();
					return;
				}
				String genFolder = data.properties.getProperty(GEN_FOLDER).trim();
				String projectName = project.getProject().getName();
				char SEP = IPath.SEPARATOR;
				String genFolderPathRelative = SEP+projectName+SEP+genFolder;
				data.paths.clear();

				PerProjectInfo info = ((JavaProject)project).getPerProjectInfo();
				IClasspathEntry raw[] = info.rawClasspath;

				if(raw != null) {
					for(IClasspathEntry entry : raw) {
						if(entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
							String srcPath = entry.getPath().toString();
							if(srcPath.equals(genFolderPathRelative)) {
								setPaths(entry, ALL_PATHS);
							}
							else {
								
								setPaths(entry, NONE_PATHS);
								
							}
								
						}
						
					}
				}
				
				
				IFolder file = project.getProject().getFolder(genFolder);
				file.refreshLocal(IResource.DEPTH_INFINITE, null);
				    
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
		}
	}
	
	@Override
	public void cleanStarting(IJavaProject project) {
		doFinish(project);		
		super.cleanStarting(project);
	}
	
	@Override
	public void buildFinished(IJavaProject project) {
		super.buildFinished(project);
		log("Finished");
		doFinish(project);
		
		current = null;
	}
	
	
	
	
	@Override
	public void reconcile(ReconcileContext context) {
		super.reconcile(context);
//		ICompilationUnit cu = context.getWorkingCopy();
		log("reconcile");
		
	}
	public static void setPaths(IClasspathEntry entry, IPath[] paths) {
		try {
			exclusionPatterns.set(entry, paths);
			patch(entry);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void patch(IClasspathEntry entry) {
//		IPath paths[] = entry.getExclusionPatterns();
//		if(paths.length != 1) {
//			System.err.println("Exclusion pattern not set");
//			return;
//		}
		try {
			ClasspathEntry e = (ClasspathEntry)entry;
			fullExclusionPatternChars.set(e, UNINIT_PATTERNS);
			e.fullExclusionPatternChars();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public int aboutToBuild(IJavaProject project) {
		log("About to build");
		current = project;
		IStatus error = null;
		ProjectData data = null;
		if(current != null ) {			
			//Build			
			try {
				data = cache.get(project);
				if(data == null) {
					log("Unexpected error: project data is null");
					Thread.dumpStack();
					return READY_FOR_BUILD;
				}
				
				//now just to call external task...
				//sooooo, first round up arguments
				
				String genFolder = data.properties.getProperty(GEN_FOLDER).trim();
				String projectName = project.getProject().getName();
				char SEP = IPath.SEPARATOR;
				String genFolderPathRelative = SEP+projectName+SEP+genFolder;
				data.paths.clear();
				
				IWorkspace ws = ResourcesPlugin.getWorkspace();
		        IWorkspaceRoot root = ws.getRoot();
		        
				String workspace = root.getLocation().toOSString();
				
				ArrayList<IClasspathEntry> sources = new ArrayList<>();
				ArrayList<IClasspathEntry> libs = new ArrayList<>();
				ArrayList<IClasspathEntry> projs = new ArrayList<>();
				
				PerProjectInfo info = ((JavaProject)project).getPerProjectInfo();
				IClasspathEntry raw[] = info.rawClasspath;

				if(raw != null) {
					for(IClasspathEntry entry : raw) {
						if(entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
//							IPath[] paths = entry.getExclusionPatterns();
							String srcPath = entry.getPath().toString();
							
							if(srcPath.equals(genFolderPathRelative)) {
//								if(paths.length != 0) {
//									paths[0] = new Path("pattern777");
//									patch(entry);
//									log("SET PTTE");
//								}
								setPaths(entry, NONE_PATHS);
							}
							else {
//								if(paths.length != 0) {
//									paths[0] = new Path("**");
//									patch(entry);
//									log("SET **");
//								}
								sources.add(entry);
								setPaths(entry, ALL_PATHS);
							}
						}
						else if(entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
							libs.add(entry);
						}
						else if(entry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
							projs.add(entry);
						}
					}
				}
//
//				IClasspathEntry[] exp = ((JavaProject)project).getExpandedClasspath();
//				System.err.println("Here");
				
				StringBuilder sb = new StringBuilder();
				for(int i = 0; i < sources.size(); i++) {
					IClasspathEntry source = sources.get(i);
					sb.append(workspace);
					sb.append(source.getPath().toOSString());
					if(i+1 < sources.size()) sb.append(File.pathSeparatorChar);
				}
				String sourcesString = sb.toString();
				
				sb = new StringBuilder();
				for(int i = 0; i < libs.size(); i++) {
					IClasspathEntry lib = libs.get(i);
					sb.append(lib.getPath().toOSString());
					if(i+1 < libs.size()) sb.append(File.pathSeparatorChar);
				}
				String libsString = sb.toString();
				
				sb = new StringBuilder();
				for(int i = 0; i < projs.size(); i++) {
					IClasspathEntry proj = projs.get(i);
					sb.append(workspace);
					sb.append(proj.getPath().toOSString());
					sb.append(SEP).append("bin");
					if(i+1 < projs.size()) sb.append(File.pathSeparatorChar);
				}
				String projsString = sb.toString();
				if(!libsString.isEmpty()) libsString = libsString + File.pathSeparatorChar;
				libsString += projsString;
				
				String genFolderPath = workspace+genFolderPathRelative;
				String javaVersion = project.getOption(JavaCore.COMPILER_SOURCE, true);
				
				IVMInstall vm = JavaRuntime.getVMInstall(project);
			    if (vm == null) vm = JavaRuntime.getDefaultVMInstall();

			    String javaPath = Paths.get(vm.getInstallLocation().getCanonicalPath(), "bin", "java").toFile().getCanonicalPath();
			    
			   // Bundle bundle = Platform.getBundle(JStructPlugin.PLUGIN_ID);
			    Bundle bundle = JStructPlugin.getInstance().getBundle();
			    
			    URL eclipseURL = FileLocator.find(bundle, new Path("libs"), null);
				URL libsURL = FileLocator.toFileURL(eclipseURL);
				log("libs URL " + libsURL);
				if(libsURL == null) {
					libsURL = FileLocator.resolve(eclipseURL);
					log("libsURL" + libsURL);
				}
				String libsPath = new File(libsURL.toURI()).getCanonicalPath();
				log("libs Path " + libsPath);
				String compileOnlyLibs = data.properties.getProperty(COMPILE_LIBS, "").trim();
				
				String javaClassPath = libsString;
				javaClassPath += compileOnlyLibs;
				if(!compileOnlyLibs.isEmpty()) javaClassPath = javaClassPath + File.pathSeparatorChar;
				javaClassPath += libsPath + SEP + '*';
			  
			    String[] args = new String[] {
			    		javaPath, "-classpath", javaClassPath, "theleo.jstruct.plugin.SourceCompiler",
						"-classpath", libsString,
						"-sourcepath", sourcesString,
						"-outputpath", genFolderPath,
						"-source", javaVersion
				};
			    log("Args " + Arrays.toString(args));
			    long start = System.currentTimeMillis();

			    ProcessBuilder proc = new ProcessBuilder(args);
			    proc.redirectErrorStream(true);
			    Process p = proc.start();
			    
			    StringBuilder output = new StringBuilder();
			    
			    String line;
			    try(BufferedReader input =  
			            new BufferedReader  
			              (new InputStreamReader(p.getInputStream()))) {  
			          while ((line = input.readLine()) != null) {  
			        	  output.append(line);
			        	  log(line);  
			          }  
			    }
			    int exitVal = p.waitFor();
			    log("exit " + exitVal + ", time + " + (System.currentTimeMillis()-start));	
			    
			    IFolder file = project.getProject().getFolder(genFolder);
			    file.refreshLocal(IResource.DEPTH_INFINITE, null);
			    			    
			    if(exitVal != 0) {
			    	error = status(Status.ERROR, "Error exit val " + exitVal + ": " + output.toString(), null);
			    	//log(Status.ERROR, "Error exit val " + exitVal + ": " + output.toString(), null);
			    }
			    
				
			} catch (Exception e) {
				error = status(Status.ERROR, "Error", e);
				e.printStackTrace();
				//log(Status.ERROR, "Error", e);
			}
			
		}
		if(error != null) {
			if("false".equals(data.properties.getProperty(SHOW_ERROR, "true").trim())) {
				System.err.println(error.toString());
				System.err.println(error.getException());
			}
			else throw new IllegalArgumentException(error.toString(), error.getException()); 
		}
		return READY_FOR_BUILD;
	}
	
	
	
	@Override
	public boolean isActive(IJavaProject project) {
		if(project.isOpen()) {
			IFile f = project.getProject().getFile(".junion");
			log("FILE " + f + ", " + f.getFullPath());
			
			if(f.exists()) {
				log("File " + f.getModificationStamp());		
				ProjectData data = cache.get(project);
				long modStamp = f.getModificationStamp();
				
				if(data == null || data.lastModification != modStamp) {
					synchronized (cache) {
						data = cache.get(project);
						if(data == null || data.lastModification != modStamp) {
							if(data == null) {
								cache.putIfAbsent(project, new ProjectData());
								data = cache.get(project);
							}
							data.lastModification = modStamp;
							data.properties.clear();
							try(InputStream io = f.getContents(true)) {
								//property file expects paths in form
								//eg c:/abc/def...
								//or c:\\abc\\def...
								//replace all occurences of single \ with / (os-indepenent), thus allow
								//   c:\abc\def...
								
								String fileContents = new BufferedReader(new InputStreamReader(io))
								  .lines().map((s)->s.replaceAll("(?<!\\\\)\\\\(?!\\\\)", "/")).collect(Collectors.joining("\n"));;
								 data.properties.load(new StringReader(fileContents));
								 //data.properties.load(io);
							} catch (Exception e) {
								e.printStackTrace();
							}
							boolean added = false;
							String genFolder = data.properties.getProperty(GEN_FOLDER);
							if(genFolder == null) {
								added = true;
								log("Added Property GEN FOLDER");
								data.properties.put(GEN_FOLDER, ".generated_src_junion");
							}
							String compileLib = data.properties.getProperty(COMPILE_LIBS);
							if(compileLib == null) {
								added = true;
								log("Added Property COMPILE LIBS");
								data.properties.put(COMPILE_LIBS, "");
							}
							String showError = data.properties.getProperty(SHOW_ERROR);
							if(showError == null) {
								added = true;
								log("Added Property SHOW ERROR");
								data.properties.put(SHOW_ERROR, "true");
							}
							if(added) {
								try {
									PipedInputStream is = new PipedInputStream();
									PipedOutputStream os = new PipedOutputStream(is);
									
									data.properties.store(os, FILE_DESC);
									os.close();
									
									f.setContents(is, true, true, null);
								}
								catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}				
				} 
				return true;
			}
		}		
		
		return false;
	}
	static {
		try {
			Field f = ClasspathEntry.class.getDeclaredField("UNINIT_PATTERNS");
			f.setAccessible(true);
			UNINIT_PATTERNS = (char[][])f.get(null);
			fullExclusionPatternChars = ClasspathEntry.class.getDeclaredField("fullExclusionPatternChars");	
			fullExclusionPatternChars.setAccessible(true);
			exclusionPatterns = ClasspathEntry.class.getDeclaredField("exclusionPatterns");	
			exclusionPatterns.setAccessible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
