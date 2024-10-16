package de.orat.math.cgacasadi.delegating.annotation.processor.representation;

import com.google.common.collect.Sets;
import de.orat.math.cgacasadi.delegating.annotation.api.GenerateDelegate;
import de.orat.math.cgacasadi.delegating.annotation.processor.GenerateDelegatingProcessor.Utils;
import de.orat.math.cgacasadi.delegating.annotation.processor.common.ErrorException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
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
    public final DeclaredType annotateddTo;

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

        DeclaredType to;
        try {
            GenerateDelegate annotation = correspondingElement.getAnnotation(GenerateDelegate.class);
            annotation.to().getClass();
            throw new AssertionError("Should have thrown a MirroredTypeException before this.");
        } catch (MirroredTypeException mte) {
            // Save assumption because classes are DeclaredTypes.
            to = (DeclaredType) mte.getTypeMirror();
        }
        this.annotateddTo = to;

        var toSuperTypeElements = computeSuperTypes(to, utils).values().stream().map(tm -> ((DeclaredType) tm).asElement()).collect(Collectors.toSet());
        Map<String, TypeMirror> commonSuperTypes = computeSuperTypes(correspondingElement.asType(), utils);
        var commonSuperTypeElemenents = commonSuperTypes.entrySet().stream().filter(e -> !toSuperTypeElements.contains(((DeclaredType) e.getValue()).asElement())).toList();
        commonSuperTypes.entrySet().removeAll(commonSuperTypeElemenents);
        // commonSuperTypes.keySet().forEach(k -> System.out.println(k));

        this.methods = Collections.unmodifiableList(Clazz.computeMethods(commonSuperTypes, this.qualifiedName, utils));
    }

    private static Map<String, TypeMirror> computeSuperTypes(TypeMirror baseType, Utils utils) {
        // Compute all recursive super types
        Map<String, TypeMirror> allSuperTypes = new LinkedHashMap<>();
        {
            List<TypeMirror> currentSubTypes = List.of(baseType);
            while (!currentSubTypes.isEmpty()) {
                List<TypeMirror> nextSubTypes = new ArrayList<>();
                for (var currentSubType : currentSubTypes) {
                    var previousEntry = allSuperTypes.putIfAbsent(currentSubType.toString(), currentSubType);
                    if (previousEntry != null) {
                        continue;
                    }

                    // Includes substitutions.
                    var currentSuperTypes = utils.typeUtils().directSupertypes(currentSubType);
                    nextSubTypes.addAll(currentSuperTypes);
                }
                currentSubTypes = nextSubTypes;
            }
        }
        allSuperTypes.remove(baseType.toString());
        allSuperTypes.remove("java.lang.Object");
        // allSuperTypes.keySet().forEach(s -> System.out.println("superTypes: " + s.toString() + s.hashCode()));
        return allSuperTypes;
    }

    private static List<Method> computeMethods(Map<String, TypeMirror> superTypes, String enclosingClassQualifiedName, Utils utils) throws ErrorException {
        Set<String> previousMethodElementsNames = new HashSet<>();
        List<Method> allMethods = new ArrayList<>();

        for (TypeMirror superInterface : superTypes.values()) {
            List<ExecutableElement> interfaceDefaultMethodElements = ((TypeElement) ((DeclaredType) superInterface).asElement()).getEnclosedElements().stream()
                .filter(el -> el.getKind() == ElementKind.METHOD)
                .map(m -> (ExecutableElement) m)
                // Remove overrides
                .filter(m -> !previousMethodElementsNames.contains(m.getSimpleName().toString()))
                .toList();

            List<String> methodElementsNames = interfaceDefaultMethodElements.stream()
                .map(me -> me.getSimpleName().toString())
                .toList();
            previousMethodElementsNames.addAll(methodElementsNames);

            TypeParametersToArguments typeParametersToArguments = new TypeParametersToArguments((DeclaredType) superInterface);
            List<Method> containedMethods = checkCreateMethods(interfaceDefaultMethodElements, utils, enclosingClassQualifiedName, typeParametersToArguments);
            allMethods.addAll(containedMethods);
        }

        return allMethods;
    }

    // private static
    private static List<Method> checkCreateMethods(List<ExecutableElement> methodElements, Utils utils, String enclosingClassQualifiedName, TypeParametersToArguments typeParametersToArguments) {
        List<Method> methods = new ArrayList<>(methodElements.size());
        Set<String> methodNames = new HashSet<>(methodElements.size());

        for (ExecutableElement methodElement : methodElements) {
            utils.exceptionHandler().handle(() -> {
                Method methodRepr = new Method(methodElement, enclosingClassQualifiedName, typeParametersToArguments, utils);

                if (methodNames.contains(methodRepr.name)) {
                    throw ErrorException.create(methodElement,
                        "Forbidden overloaded method: \"%s\".",
                        methodElement.getSimpleName());
                }

                methods.add(methodRepr);
                methodNames.add(methodRepr.name);
            });
        }

        return methods;
    }
}
