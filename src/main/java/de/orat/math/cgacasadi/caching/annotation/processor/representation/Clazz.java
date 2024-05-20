package de.orat.math.cgacasadi.caching.annotation.processor.representation;

import de.orat.math.cgacasadi.caching.annotation.processor.GenerateCachedProcessor.Utils;
import de.orat.math.cgacasadi.caching.annotation.processor.common.ErrorException;
import de.orat.math.gacalc.spi.iMultivectorSymbolic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

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

        Set<String> iMultivectorSymbolic = correspondingElement.getInterfaces().stream()
            .map(i -> ((TypeElement) ((DeclaredType) i).asElement()).getQualifiedName().toString())
            .collect(Collectors.toSet());
        if (!iMultivectorSymbolic.contains(iMultivectorSymbolic.class.getCanonicalName())) {
            throw ErrorException.create(correspondingElement,
                "Needs to implement \"%s\", but does not.", iMultivectorSymbolic.class.getCanonicalName());
        }

        this.methods = Clazz.computeMethods(correspondingElement, this.qualifiedName, utils);
    }

    private static List<Method> computeMethods(TypeElement correspondingElement, String enclosingClassQualifiedName, Utils utils) {
        // Safe cast because
        // - filtered for Methods
        // - Methods are ExceutableElements.
        List<ExecutableElement> allMethodElements = (List<ExecutableElement>) correspondingElement.getEnclosedElements()
            .stream()
            .filter(el -> el.getKind() == ElementKind.METHOD)
            .toList();

        Set<String> methodNames = new HashSet<>();
        Set<ExecutableElement> overloadedMethodElements = allMethodElements.stream()
            .filter(el -> !methodNames.add(el.getSimpleName().toString()))
            .collect(Collectors.toCollection(HashSet::new));

        List<Method> methods = new ArrayList<>(allMethodElements.size());
        for (ExecutableElement methodElement : allMethodElements) {
            utils.exceptionHandler().handle(() -> {
                Method methodRepr = new Method(methodElement, enclosingClassQualifiedName, utils);

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
