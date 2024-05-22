package de.orat.math.cgacasadi.caching.annotation.processor.representation;

import de.orat.math.cgacasadi.caching.annotation.processor.GenerateCachedProcessor.Utils;
import de.orat.math.cgacasadi.caching.annotation.processor.common.ErrorException;
import de.orat.math.cgacasadi.caching.annotation.processor.common.WarningException;
import de.orat.math.gacalc.spi.iMultivectorSymbolic;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class Clazz {

    public final TypeElement correspondingElement;
    public final String qualifiedName;
    public final String simpleName;
    public final String enclosingQualifiedName;
    /**
     * Unmodifiable
     */
    public final List<Method> methods;

    public Clazz(TypeElement correspondingElement, Utils utils) throws ErrorException, Exception {
        this.correspondingElement = correspondingElement;
        this.simpleName = correspondingElement.getSimpleName().toString();
        this.enclosingQualifiedName = ((QualifiedNameable) correspondingElement.getEnclosingElement()).getQualifiedName().toString();
        this.qualifiedName = correspondingElement.getQualifiedName().toString();

        ElementKind kind = correspondingElement.getKind();
        if (kind != ElementKind.CLASS) {
            throw ErrorException.create(correspondingElement,
                "Expected \"%s\" to be a class, but was \"%s\".",
                this.qualifiedName, kind);
        }

        if (correspondingElement.getModifiers().contains(Modifier.FINAL)) {
            throw ErrorException.create(correspondingElement, "Has prohibited modfier \"final\".");
        }

        List<? extends TypeParameterElement> typeParamsList = correspondingElement.getTypeParameters();
        if (!typeParamsList.isEmpty()) {
            String typeParamsString = typeParamsList.stream()
                .map(tp -> tp.getSimpleName().toString())
                .collect(Collectors.joining(", "));
            throw ErrorException.create(correspondingElement, "Type parameters are prohibited: %s", typeParamsString);
        }

        Set<String> iMultivectorSymbolic = correspondingElement.getInterfaces().stream()
            .map(i -> ((TypeElement) ((DeclaredType) i).asElement()).getQualifiedName().toString())
            .collect(Collectors.toSet());
        if (!iMultivectorSymbolic.contains(iMultivectorSymbolic.class.getCanonicalName())) {
            throw ErrorException.create(correspondingElement,
                "Needs to implement \"%s\", but does not.", iMultivectorSymbolic.class.getCanonicalName());
        }

        this.methods = Clazz.computeMethods(correspondingElement, this.qualifiedName, utils);
    }

    private static List<Method> computeMethods(TypeElement correspondingElement, String enclosingClassQualifiedName, Utils utils) throws WarningException, ErrorException {
        // Safe cast because
        // - filtered for Methods
        // - Methods are ExceutableElements.
        List<ExecutableElement> classMethodElements = (List<ExecutableElement>) correspondingElement.getEnclosedElements()
            .stream()
            .filter(el -> el.getKind() == ElementKind.METHOD)
            .toList();

        // Wenn überschrieben wurde, dann nicht nehmen.
        List<ExecutableElement> interfaceDefaultMethodElements = correspondingElement.getInterfaces().stream()
            .map(i -> ((TypeElement) ((DeclaredType) i).asElement()).getEnclosedElements())
            .flatMap(Collection::stream)
            .filter(el -> el.getKind() == ElementKind.METHOD)
            .map(m -> (ExecutableElement) m)
            .filter(m -> m.isDefault())
            .toList();

//        if (true) {
//            String asdfb = interfaceDefaultMethodElements.stream()
//                // .map(i -> ((ExecutableType) i.asType()).getReturnType())
//                .map(i -> i.getReturnType())
//                .map(t -> t.toString())
//                .collect(Collectors.joining(", "));
//
//            throw ErrorException.create(correspondingElement, asdfb);
//        }
//        var asdf = correspondingElement.getInterfaces().stream()
//            // .map(i -> ((DeclaredType) i).getTypeArguments())
//            .map(i -> ((TypeElement) ((DeclaredType) i).asElement()).getTypeParameters())
//            .flatMap(Collection::stream)
//            .map(t -> t.toString())
//            .toList();
//        if (true) {
//            String asdfb = asdf.stream().collect(Collectors.joining(", "));
//            throw ErrorException.create(correspondingElement, asdfb);
//        }
        TypeParametersToArguments typeParametersToArguments = new TypeParametersToArguments(correspondingElement);

        List<ExecutableElement> allMethodElements = Stream.concat(interfaceDefaultMethodElements.stream(), classMethodElements.stream()).toList();

        Set<String> methodNames = new HashSet<>();
        Set<ExecutableElement> overloadedMethodElements = allMethodElements.stream()
            .filter(el -> !methodNames.add(el.getSimpleName().toString()))
            .collect(Collectors.toCollection(HashSet::new));

        List<Method> methods = new ArrayList<>(allMethodElements.size());
        for (ExecutableElement methodElement : allMethodElements) {
            utils.exceptionHandler().handle(() -> {
                Method methodRepr = new Method(methodElement, enclosingClassQualifiedName, typeParametersToArguments, utils);

                if (overloadedMethodElements.contains(methodElement)) {
                    throw ErrorException.create(methodElement,
                        "Forbidden overloaded method: \"%s\".",
                        methodElement.getSimpleName());
                }

                methods.add(methodRepr);
            });
        }

        return Collections.unmodifiableList(methods);
    }
}
