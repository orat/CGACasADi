package de.orat.math.cgacasadi.caching.annotation.processor.representation;

import de.orat.math.cgacasadi.caching.annotation.processor.GenerateCachedProcessor.Utils;
import de.orat.math.cgacasadi.caching.annotation.processor.common.ErrorException;
import de.orat.math.cgacasadi.caching.annotation.processor.common.FailedToCacheException;
import static de.orat.math.cgacasadi.caching.annotation.processor.generation.Classes.T_iMultivectorSymbolic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
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

        if (correspondingElement.getModifiers().contains(Modifier.FINAL)) {
            throw ErrorException.create(correspondingElement, "Has prohibited modifier \"final\".");
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
        if (!iMultivectorSymbolic.contains(T_iMultivectorSymbolic.canonicalName())) {
            throw ErrorException.create(correspondingElement,
                "Needs to implement \"%s\", but does not.", T_iMultivectorSymbolic.canonicalName());
        }

        this.methods = Collections.unmodifiableList(Clazz.computeMethods(correspondingElement, this.qualifiedName, utils));
    }

    private static List<Method> computeMethods(TypeElement correspondingElement, String enclosingClassQualifiedName, Utils utils) throws FailedToCacheException, ErrorException {
        // Safe cast because
        // - filtered for Methods
        // - Methods are ExceutableElements.
        List<ExecutableElement> classMethodElements = (List<ExecutableElement>) correspondingElement.getEnclosedElements()
            .stream()
            .filter(el -> el.getKind() == ElementKind.METHOD)
            .toList();

        // Search for all super interfaces and compute recursive substitutions.
        Set<DeclaredType> allImplementedInterfaces = new LinkedHashSet<>();
        Map<DeclaredType, TypeParametersToArguments> iToParamsToArgs = new HashMap<>();
        {
            List<DeclaredType> subInterfaces = (List<DeclaredType>) correspondingElement.getInterfaces();
            while (!subInterfaces.isEmpty()) {
                List<DeclaredType> nextSubInterfaces = new ArrayList<>();
                // System.out.println("run");
                for (DeclaredType subInterface : subInterfaces) {
                    allImplementedInterfaces.add(subInterface);
                    if (iToParamsToArgs.containsKey(subInterface)) {
                        // System.out.println("sub: " + subInterface.toString() + subInterface.hashCode());
                        continue;
                    }

                    var subParamsToArgs = new TypeParametersToArguments(subInterface);
                    iToParamsToArgs.put(subInterface, subParamsToArgs);
                    // System.out.println(subInterface.toString() + subInterface.hashCode());

                    List<DeclaredType> superInterfaces = (List<DeclaredType>) ((TypeElement) subInterface.asElement()).getInterfaces();
                    for (DeclaredType superInterface : superInterfaces) {
                        if (iToParamsToArgs.containsKey(superInterface)) {
                            // System.out.println("super: " + superInterface.toString() + superInterface.hashCode());
                            continue;
                        }
                        nextSubInterfaces.add(superInterface);

                        var superParamsToArgs = new TypeParametersToArguments(superInterface);
                        superParamsToArgs.substitute(subParamsToArgs);
                        iToParamsToArgs.put(superInterface, superParamsToArgs);
                        // System.out.println(subInterface.toString() + subInterface.hashCode() + " extends/implements " + superInterface.toString() + superInterface.hashCode());
                    }
                }
                subInterfaces = nextSubInterfaces;
            }
        }
        // allImplementedInterfaces.forEach(i -> System.out.println(i));

        Set<String> previousMethodElementsNames = classMethodElements.stream()
            .map(me -> me.getSimpleName().toString())
            .collect(Collectors.toCollection(HashSet::new));

        List<Method> allMethods = new ArrayList<>();
        {
            List<Method> classMethods = checkCreateMethods(classMethodElements, utils, enclosingClassQualifiedName, new TypeParametersToArguments());
            allMethods.addAll(classMethods);
        }

        for (DeclaredType superInterface : allImplementedInterfaces) {
            List<ExecutableElement> interfaceDefaultMethodElements = ((TypeElement) superInterface.asElement()).getEnclosedElements().stream()
                .filter(el -> el.getKind() == ElementKind.METHOD)
                .map(m -> (ExecutableElement) m)
                .filter(m -> m.isDefault())
                // Remove overrides
                .filter(m -> !previousMethodElementsNames.contains(m.getSimpleName().toString()))
                .toList();

            List<String> methodElementsNames = interfaceDefaultMethodElements.stream()
                .map(me -> me.getSimpleName().toString())
                .toList();
            previousMethodElementsNames.addAll(methodElementsNames);

            TypeParametersToArguments typeParametersToArguments = iToParamsToArgs.get(superInterface);
            List<Method> defaultMethods = checkCreateMethods(interfaceDefaultMethodElements, utils, enclosingClassQualifiedName, typeParametersToArguments);
            allMethods.addAll(defaultMethods);
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
