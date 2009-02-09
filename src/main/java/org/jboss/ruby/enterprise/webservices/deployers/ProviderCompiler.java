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
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import org.jboss.logging.Logger;
import org.jboss.ruby.enterprise.webservices.RubyWebServiceProvider;
import org.jboss.ruby.enterprise.webservices.metadata.RubyWebServiceMetaData;
import org.jboss.ws.annotation.EndpointConfig;
import org.jboss.ws.extensions.policy.PolicyScopeLevel;
import org.jboss.ws.extensions.policy.annotation.Policy;
import org.jboss.ws.extensions.policy.annotation.PolicyAttachment;

public class ProviderCompiler {
	
	private static final Logger log = Logger.getLogger( ProviderCompiler.class );

	public static final String GENERATED_PACKAGE = "org.jboss.ruby.enterprise.webservices.generated";
	private ClassLoader classLoader;
	private String runtimePoolName;

	public ProviderCompiler(ClassLoader classLoader, String runtimePoolName) {
		this.classLoader = classLoader;
		this.runtimePoolName = runtimePoolName;
	}

	public Class<?> compile(RubyWebServiceMetaData metaData) throws NotFoundException, CannotCompileException {

		ClassPool classPool = new ClassPool(true);
		ClassPath classPath = new LoaderClassPath(this.classLoader);
		classPool.appendClassPath(classPath);

		CtClass genClass = createClass(classPool, metaData);
		ClassFile classFile = genClass.getClassFile();
		ConstPool constPool = classFile.getConstPool();

		AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag );
		classFile.addAttribute( attr );
		classFile.setVersionToJava5();
		attachWebServiceProviderAnnotation(classFile, attr, metaData);
		log.info( "gen : " + genClass );
		try {
			for ( Object ann : genClass.getAnnotations() ) {
				log.info( "ANNTATION: " + ann );
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		CtConstructor ctor = CtNewConstructor.make("public " + metaData.getName() + "() { super(\"" + metaData.getDirectory() + "\", \""
				+ metaData.getName() + "\", \"" + this.runtimePoolName + "\"); }", genClass);
		genClass.addConstructor(ctor);
	}

	private void attachWebServiceProviderAnnotation(ClassFile classFile, AnnotationsAttribute attr, RubyWebServiceMetaData metaData) {
		log.info( "adding @WebServiceProvider" );
		ConstPool constPool = classFile.getConstPool();

		Annotation annotation = new Annotation("javax.xml.ws.WebServiceProvider", constPool);
		annotation.addMemberValue("wsdlLocation", new StringMemberValue("app/webservices/" + metaData.getName() + "/" + metaData.getName()
				+ ".wsdl", constPool));
		annotation.addMemberValue("targetNamespace", new StringMemberValue(metaData.getTargetNamespace(), constPool));
		annotation.addMemberValue("portName", new StringMemberValue(metaData.getPortName(), constPool));

		//attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
		//attr.setAnnotation(annotation);
		attr.addAnnotation( annotation );
		
		log.info( "added : " + annotation );

		//classFile.addAttribute(attr);
	}
	
}
