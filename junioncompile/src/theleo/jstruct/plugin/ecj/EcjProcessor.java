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
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import sun.misc.Unsafe;

@SupportedAnnotationTypes(value= {"*"})
public class EcjProcessor extends AbstractProcessor {

	public EcjProcessor() {
		System.err.println("INSTANCE2 ");
		//Thread.dumpStack();

	}
	
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
//		Unsafe u = getUnsafe();
		
//		Thread.dumpStack();
		System.err.println("INIT4 " + processingEnv.getSourceVersion());
//		System.err.println(processingEnv.getClass().getSuperclass().getSuperclass());
//		//Field[] fields = processingEnv.getClass().getSuperclass().getSuperclass().getDeclaredFields();
//		//for(Field f : fields)
//		//	System.err.println(f);
//		
//		Class baseEnv = findSuperClass(processingEnv.getClass(),
//				"org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl"
//				);
//	
//		if(baseEnv != null) {
//			System.err.println("Eclipse compiler");
//			org.eclipse.jdt.internal.compiler.Compiler compiler = read(processingEnv, "_compiler", baseEnv);
//		    System.err.println();
//		    
//		    
//			
//			//org.eclipse.jdt.internal.compiler.Compiler
//		}
//		System.err.println("Cls ");
		
		
	
		super.init(processingEnv);
		
				
	}
	@Override
	public boolean process(Set<? extends TypeElement> set, RoundEnvironment round) {
		Elements ele = processingEnv.getElementUtils();
		Types types = processingEnv.getTypeUtils();
				
		
		return false;
	}
	
	public static Class findSuperClass(Class cls, String name) {
		while(cls != null) {
			String clsName = cls.getName();
			System.err.println("Serr " + name);
			if(clsName.equals(name)) return cls;
			cls = cls.getSuperclass();
		}
		return null;
	}
	
	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}
	public static <T> T read(Object o, String name) {
		return read(o, name, o.getClass());
    }
	public static <T> T read(Object o, String name, Class c) {
        try {
            java.lang.reflect.Field f = c.getDeclaredField(name);
            f.setAccessible(true);
            return (T)f.get(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
}
