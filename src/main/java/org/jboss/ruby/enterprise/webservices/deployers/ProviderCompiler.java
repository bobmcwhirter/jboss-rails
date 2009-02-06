package org.jboss.ruby.enterprise.webservices.deployers;

import javassist.CannotCompileException;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtNewConstructor;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import org.jboss.ruby.enterprise.webservices.RubyWebServiceProvider;
import org.jboss.ruby.enterprise.webservices.metadata.RubyWebServiceMetaData;

public class ProviderCompiler {

	public static final String GENERATED_PACKAGE = "org.jboss.ruby.enterprise.webservices.generated";
	private ClassLoader classLoader;

	public ProviderCompiler(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public Class<?> compile(RubyWebServiceMetaData metaData) throws NotFoundException, CannotCompileException {

		ClassPool classPool = new ClassPool(true);
		ClassPath classPath = new LoaderClassPath(this.classLoader);
		classPool.appendClassPath(classPath);

		CtClass genClass = createClass(classPool, metaData);
		ClassFile classFile = genClass.getClassFile();

		attachWebServiceProviderAnnotation(classFile, metaData);
		return genClass.toClass(classLoader, getClass().getProtectionDomain());
	}

	private CtClass createClass(ClassPool classPool, RubyWebServiceMetaData metaData) throws NotFoundException, CannotCompileException {
		CtClass genClass = classPool.makeClass(GENERATED_PACKAGE + "." + metaData.getName());
		CtClass superClass = classPool.get(RubyWebServiceProvider.class.getName());
		genClass.setSuperclass(superClass);

		attachConstructor(genClass, metaData);

		return genClass;
	}

	private void attachConstructor(CtClass genClass, RubyWebServiceMetaData metaData) throws CannotCompileException {
		CtConstructor ctor = CtNewConstructor
				.make("public " + metaData.getName() + "() { super(\"" + metaData.getDirectory() + "\", \""  + metaData.getName() + "\"); }", genClass);
		genClass.addConstructor(ctor);
	}

	private void attachWebServiceProviderAnnotation(ClassFile classFile, RubyWebServiceMetaData metaData) {
		ConstPool constPool = classFile.getConstPool();

		Annotation annotation = new Annotation("javax.xml.ws.WebServiceProvider", constPool);
		annotation.addMemberValue("wsdlLocation", new StringMemberValue("app/webservices/" + metaData.getName() + "/" + metaData.getName()
				+ ".wsdl", constPool));
		annotation.addMemberValue("targetNamespace", new StringMemberValue(metaData.getTargetNamespace(), constPool));
		annotation.addMemberValue("portName", new StringMemberValue(metaData.getPortName(), constPool));

		AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		attr.setAnnotation(annotation);
		classFile.addAttribute(attr);
		classFile.setVersionToJava5();

		classFile.addAttribute(attr);
	}

}
