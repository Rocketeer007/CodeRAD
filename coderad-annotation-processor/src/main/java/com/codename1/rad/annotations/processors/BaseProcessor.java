package com.codename1.rad.annotations.processors;

import com.codename1.rad.annotations.Autogenerated;
import com.codename1.rad.annotations.RAD;
import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.codename1.rad.annotations.processors.ProcessorConstants.ENTITY_TYPE;

public abstract class BaseProcessor extends AbstractProcessor {


    abstract void installTypes(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);

    boolean isA(TypeMirror mirror, String fqn) {
        if (mirror == null) {
            throw new IllegalArgumentException("isA() received null type argument.  isA requires a non-null value for its mirror type");
        }
        TypeElement superclass = elements().getTypeElement(fqn);
        if (superclass == null) {
            throw new IllegalArgumentException("Cannot find class "+fqn);
        }
        if (types().isSubtype(mirror, superclass.asType())) return true;
        if (mirror instanceof DeclaredType) {
            return isA((TypeElement)((DeclaredType)mirror).asElement(), fqn);
        }
        return false;

    }

    boolean isPrimitive(TypeMirror el) {
        switch (el.getKind()) {
            case BOOLEAN:
            case FLOAT:
            case DOUBLE:
            case INT:
            case LONG:
            case CHAR:
            case SHORT:
            case BYTE:
                return true;
        }
        return false;
    }




     boolean processSchemas(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {



        Set<? extends Element> annotatedElements = (Set<? extends TypeElement>)roundEnv.getElementsAnnotatedWith(RAD.class);
        for (Element e : annotatedElements) {
            //createEntityClass(e, roundEnv);


        }

        return false;
    }

    String getEntityNameFor(TypeElement el) {
        if (el.getKind() == ElementKind.CLASS && el.getSimpleName().toString().startsWith("Abstract")) {
            return el.getSimpleName().toString().substring("Abstract".length());
        //} else if (el.getKind() == ElementKind.INTERFACE && el.getSimpleName().toString().startsWith("I")) {
        //    return el.getSimpleName().toString().substring(1);
        } else if (el.getKind() == ElementKind.INTERFACE) {
            return el.getSimpleName().toString();
        } else {
            throw new IllegalArgumentException("TypeElement is not an entity. Classes must begin with Abstract, and Interface must begin with I");
        }
    }

    PackageElement getPackageElement(Element el) {
        if (el == null) return null;
        if (el.getKind() == ElementKind.PACKAGE) return (PackageElement)el;
        return getPackageElement(el.getEnclosingElement());
    }



    void validateTag(String tag, Element sourceElement) {
        Element field = findField(tag, sourceElement);
        if (field == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The tag "+tag+" could not be found. Be sure that it is available as a public, protected, or package-private static field in this class/interface or a superclass/interface.", sourceElement);
        }

    }

    Element findField(String name, Element source) {
        if (source == null || source.getKind() == ElementKind.PACKAGE) {
            return null;
        }
        if (source instanceof TypeElement) {
            for (Element child : processingEnv.getElementUtils().getAllMembers((TypeElement)source)) {

                if (child.getKind() == ElementKind.FIELD && child.getSimpleName().contentEquals(name)) {
                    return child;
                }
            }
        }

        Element parent = source.getEnclosingElement();

        if (parent != null) {

            return findField(name, parent);
        }

        return null;

    }

    boolean isEntitySubclass(TypeElement el) {
        DeclaredType superType = (DeclaredType)el.getSuperclass();
        if (superType == null) return false;
        TypeElement superTypeEl = (TypeElement)superType.asElement();

        if (superTypeEl.getQualifiedName().contentEquals(ENTITY_TYPE)) {
            return true;
        }
        return false;

    }



    protected ProcessingEnvironmentWrapper env() {
        return (ProcessingEnvironmentWrapper) processingEnv;
    }

    protected ProcessingEnvironmentWrapper.ElementsWrapper elements() {
        return (ProcessingEnvironmentWrapper.ElementsWrapper)processingEnv.getElementUtils();
    }

    protected ProcessingEnvironmentWrapper.TypesWrapper types() {
        return (ProcessingEnvironmentWrapper.TypesWrapper) processingEnv.getTypeUtils();
    }


    boolean isA(TypeElement el, String qualifiedName) {
        if (el == null) return false;

        if (el.getQualifiedName().contentEquals(qualifiedName)) return true;
        String elQualifiedNameString = el.getQualifiedName().toString();
        if (elQualifiedNameString.contains("<")) {
            elQualifiedNameString = elQualifiedNameString.substring(0, elQualifiedNameString.indexOf("<")).trim();

        }
        String baseName = qualifiedName;
        if (baseName.contains("<")) {
            baseName = baseName.substring(0, baseName.indexOf("<")+1).trim();
        }

        if (baseName.equals(elQualifiedNameString)) return true;

        TypeElement superclass = elements().getTypeElement(qualifiedName);
        if (superclass == null) {
            throw new IllegalArgumentException("Cannot find type "+qualifiedName);
        }
        DeclaredType mirror;
        if (superclass.getTypeParameters().size() ==  ((DeclaredType)el.asType()).getTypeArguments().size()) {
            mirror = types().getDeclaredType(superclass, ((DeclaredType) el.asType()).getTypeArguments().toArray(new TypeMirror[0]));

        } else {
            mirror = (DeclaredType)superclass.asType();
        }
        if (mirror == null) {
            throw new IllegalArgumentException("getDeclaredType() returned null for " + superclass + " wth type arguments " + ((DeclaredType) el.asType()).getTypeArguments());
        }


        if (types().isSubtype(el.asType(), mirror)) return true;
        for (TypeMirror supertype : types().directSupertypes(el.asType())) {
            if (isA(supertype, qualifiedName)) return true;
        }
        return false;

        /*
        TypeMirror mirror = el.asType();
        TypeElement targetTypeEl = processingEnv.getElementUtils().getTypeElement(qualifiedName);


        //processingEnv.getElementUtils().getTypeElement(qualifiedName);
        if (targetTypeEl != null && processingEnv.getTypeUtils().isAssignable(mirror, targetTypeEl.asType())) {
            return true;
        }

        if (el.getQualifiedName().contentEquals(qualifiedName)) return true;

        TypeMirror superClass = el.getSuperclass();
        if (superClass != null) {



            TypeElement superClassTypeEl = (TypeElement)processingEnv.getTypeUtils().asElement(superClass);

            if (superClassTypeEl != null && isA(superClassTypeEl, qualifiedName)) {
                return true;
            }
        }
        for (TypeMirror i : el.getInterfaces()) {
            TypeElement interfaceEl = (TypeElement)processingEnv.getTypeUtils().asElement(i);
            if (interfaceEl == null) {
                if (roundEnv != null) {
                    for (Element autogen : roundEnv.getElementsAnnotatedWith(Autogenerated.class)) {
                        if (autogen instanceof TypeElement) {
                            TypeElement autoGenType = (TypeElement)autogen;
                            if (autoGenType.asType().equals(i)) {
                                interfaceEl = autoGenType;
                                break;
                            }
                        }
                    }
                }
            }

            if (interfaceEl != null && isA(interfaceEl, qualifiedName)) {
                return true;
            }
        }
        return false;
        */
    }

    List<ExecutableElement> getAllTaggedAndAbstractMethods(List<ExecutableElement> out, TypeElement el) {
        if (out == null) out = new ArrayList<>();
        final List<ExecutableElement> fout = out;
        if (el == null) return out;

        el.getEnclosedElements().stream()
        //processingEnv.getElementUtils().getAllMembers(el).stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .filter(e -> e.getAnnotation(RAD.class) != null)
                .filter(e -> e.getAnnotation(RAD.class).tag().length > 0)
                .forEach(e-> {
                    fout.add((ExecutableElement)e);
                });

        getAllTaggedAndAbstractMethods(out,(TypeElement) processingEnv.getTypeUtils().asElement(el.getSuperclass()));
        for (TypeMirror iface : el.getInterfaces()) {
            getAllTaggedAndAbstractMethods(out, (TypeElement)processingEnv.getTypeUtils().asElement(iface));
        }
        return out;
    }


    String[] extractTags(Element e) {
        RAD anno = e.getAnnotation(RAD.class);
        if (anno == null) return new String[0];
        String[] out = e.getAnnotation(RAD.class).tag();
        for (int i=0; i<out.length; i++) {
            validateTag(out[i], e);


        }
        return out;
    }

    static String getSimpleName(Element e) {
        return e.getSimpleName().toString();
    }


    static String[] mergeUnique(String[] s1, String[] s2) {
        List<String> out = new ArrayList<String>(s1.length + s2.length);
        for (String s : s1) {
            if (!out.contains(s)) out.add(s);
        }
        for (String s : s2) {
            if (!out.contains(s)) out.add(s);
        }
        return out.toArray(new String[out.size()]);
    }

    static String getPropName(String methodName) {
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
        } else if (methodName.startsWith("is")) {
            return methodName.substring(2, 3).toLowerCase() + methodName.substring(3);
        }
        return null;
    }


    boolean isAutogenerated(TypeElement el) {
        return el.getAnnotation(Autogenerated.class) != null;
    }

    /**
     * Normalizes fully-qualified class name for output in files.  Also strips "stubs." prefix which is used for storing
     * intermediate stub classes for the two pass generation method.
     * @param qualifiedName The qualified name for a class.
     * @return
     */
    static String _(String qualifiedName) {
        if (qualifiedName == null) return "";
        //if (qualifiedName.startsWith(STUBS_PREFIX)) {
        //    return qualifiedName.substring(qualifiedName.indexOf(".")+1);
        //}
        return qualifiedName;
    }
    //static final String STUBS_PREFIX = "cn1stubs10101010101.";

    //static String stripStubs(String content) {
    //    return content.replace(STUBS_PREFIX, "");
    //}

}
