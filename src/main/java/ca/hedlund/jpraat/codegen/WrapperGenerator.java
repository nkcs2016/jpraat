package ca.hedlund.jpraat.codegen;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.WString;

import ca.hedlund.jpraat.annotations.Declared;
import ca.hedlund.jpraat.annotations.NativeType;
import ca.hedlund.jpraat.annotations.Wrapped;

/**
 * Class which uses reflection to create a set of C++ wrapper files for a 
 * given class.
 *
 */
public class WrapperGenerator {

	private final StringBuffer buffer = new StringBuffer();
	
	// assume includes are in sub-folder
	private String includePrefix = "../";
	private List<String> included = new ArrayList<String>();
	
	private final static String errorFunctions = 
			"#include <pthread.h>\r\n" + 
			"#include <string.h>\r\n" + 
			"#include <stdlib.h>\r\n" + 
			"static char _lastError[256] = {'\\0'};\r\n" + 
			"static pthread_mutex_t jpraat_mut = PTHREAD_MUTEX_INITIALIZER;\r\n" + 
			"\r\n" + 
			"PRAAT_LIB_EXPORT const char* jpraat_last_error() {\r\n" + 
			"	static char retVal[256] = {'\\0'};\r\n" + 
			"	pthread_mutex_lock(&jpraat_mut);\r\n" + 
			"	strncpy(retVal, _lastError, 255);\r\n" + 
			"	pthread_mutex_unlock(&jpraat_mut);\r\n" + 
			"	return retVal;\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"PRAAT_LIB_EXPORT void jpraat_clear_error() {\r\n" + 
			"	pthread_mutex_lock(&jpraat_mut);\r\n" + 
			"	_lastError[0] = '\\0';\r\n" + 
			"	pthread_mutex_unlock(&jpraat_mut);\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"PRAAT_LIB_EXPORT void jpraat_set_error(const char* err) {\r\n" + 
			"	pthread_mutex_lock(&jpraat_mut);\r\n" + 
			"	strncpy(_lastError, err, 255);\r\n" + 
			"	pthread_mutex_unlock(&jpraat_mut);\r\n" + 
			"}\r\n" + 
			"\r\n" + 
			"PRAAT_LIB_EXPORT void jpraat_set_melder_error() {\r\n" + 
			"	wchar_t* melderErr = Melder_getError();\r\n" + 
			"	if(melderErr != NULL) {\r\n" + 
			"		pthread_mutex_lock(&jpraat_mut);\r\n" + 
			"		wcstombs(_lastError, melderErr, 255);\r\n" + 
			"		pthread_mutex_unlock(&jpraat_mut);\r\n" + 
			"	}\r\n" + 
			"}";
	
	public WrapperGenerator() {
		super();
	}
	
	public String getGeneratedSource() {
		return buffer.toString();
	}
	
	public void processClass(Class<?> clazz) {
		// setup includes
		
		buffer.append("#include ").append('\"').append(includePrefix).append("sys/praatlib.h").append('\"').append('\n');
		
		for(Method method:clazz.getMethods()) {
			// get annotations for method
			final Wrapped wrappedAnnotation = method.getAnnotation(Wrapped.class);
			if(wrappedAnnotation != null) {
				setupIncludes(method);
			}
		}
		
		buffer.append('\n').append(errorFunctions).append('\n');

		// create wrappers
		for(Method method:clazz.getMethods()) {
			// get annotations for method
			final Wrapped wrappedAnnotation = method.getAnnotation(Wrapped.class);
			if(wrappedAnnotation != null) {
				processMethod(method);
			}
		}
	}
	
	private void setupIncludes(Method method) {
		final Declared headerAnnotation = method.getAnnotation(Declared.class);
		if(headerAnnotation != null) {
			if(!included.contains(headerAnnotation.value())) {
				buffer.append("#include ")
						.append('\"')
						.append(includePrefix)
						.append(headerAnnotation.value())
						.append('\"').append('\n');
				included.add(headerAnnotation.value());
			}
		}
	}
	
	public void processMethod(Method method) {
		String methodName = method.getName();
		String praatMethod = methodName;
		if(methodName.endsWith("_wrapped")) {
			praatMethod = methodName.substring(0, methodName.length()-("_wrapped").length());
		}
		
		buffer.append("\n");
		buffer.append("// ").append(methodName).append(" -> ").append(praatMethod).append("\n");
		
		buffer.append("PRAAT_LIB_EXPORT ");
		
		final NativeType nativeType = method.getAnnotation(NativeType.class);
		// return value
		String returnType = toNativeType(method.getReturnType());
		if(nativeType != null) {
			returnType = nativeType.value();
		}
		buffer.append(returnType);
		buffer.append(" ");
		buffer.append(methodName);
		
		buffer.append("(");
		int argc = 0;
		for(Parameter param:method.getParameters()) {
			if(argc++ > 0)
				buffer.append(",");
			final NativeType nativeParamType = param.getAnnotation(NativeType.class);
			if(nativeParamType != null)
				buffer.append(nativeParamType.value());
			else
				buffer.append(toNativeType(param.getType()));
			buffer.append(" ")
				.append(param.getName());
		}
		buffer.append(") {").append("\n");
		
//		buffer.append("\t").append("jpraat_lock();").append("\n");
//		buffer.append("\t").append("jpraat_clear_error();").append("\n");
		
		if(method.getReturnType() != void.class) {
			buffer.append("\t").append(returnType).append(" retVal;").append("\n");
		}
		
		buffer.append("\t").append("try {").append("\n");
		buffer.append("\t\t");
		if(method.getReturnType() != void.class) {
			buffer.append("return ");
		}
		buffer.append(praatMethod).append("(");
		argc = 0;
		for(Parameter param:method.getParameters()) {
			if(argc++ > 0)
				buffer.append(",");
			buffer.append(param.getName());
		}
		buffer.append(");\n");
		
		buffer.append("\t").append("}");
		buffer.append(" catch (const char* e) {").append("\n");
		buffer.append("\t\t").append("jpraat_set_error(e);").append("\n");
		buffer.append("\t").append("}").append(" catch (MelderError) {").append("\n");
		buffer.append("\t\t").append("jpraat_set_melder_error();").append("\n");
		buffer.append("\t").append("}").append(" catch (...) {").append("\n");
		buffer.append("\t\t").append("jpraat_set_error(\"Unknown error\");").append("\n");
		buffer.append("\t").append("}").append("\n");
		
//		buffer.append("\t").append("jpraat_unlock();").append("\n");
		
		if(method.getReturnType() != void.class) {
			buffer.append("\t").append("return NULL;\n");
		}
		
		buffer.append("}").append("\n");
	}
	
	private String toNativeType(Class<?> clazz) {
		String retVal = clazz.getSimpleName();
		
		if(clazz == boolean.class) {
			retVal = "bool";
		} else if(clazz == WString.class) {
			retVal = "const wchar_t*";
		} else if(clazz == String.class) {
			retVal = "const char*";
		} else if(clazz == NativeLong.class) {
			retVal = "long";
		} else if(clazz.isEnum()) {
			retVal = "enum " + retVal;
		}
		
		return retVal;
	}
	
	/**
	 * arg[0]..arg[n-2] - classes to process
	 * arg[n-1] - output file
	 */
	public static void main(String[] args) {
		if(args.length < 2) {
			System.err.println("Usage: java ca.hedlund.jpraat.codegen.WrapperGenerator [<class1> <class2> ...] <file>");
			return;
		}
	
		final WrapperGenerator generator = new WrapperGenerator();
		final String outputFile = args[args.length-1];
		
		for(int i = 0; i <= args.length-2; i++) {
			try {
				generator.processClass(Class.forName(args[i]));
			} catch (ClassNotFoundException e) {
				System.err.println(e.getLocalizedMessage());
			}
		}
		
		try(final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"))) {
			out.write(generator.getGeneratedSource());
			out.flush();
		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
}
