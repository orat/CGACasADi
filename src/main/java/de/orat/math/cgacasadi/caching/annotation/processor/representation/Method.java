package de.orat.math.cgacasadi.caching.annotation.processor.representation;

import de.orat.math.cgacasadi.caching.annotation.api.Uncached;
import de.orat.math.cgacasadi.caching.annotation.processor.GenerateCachedProcessor.Utils;
import de.orat.math.cgacasadi.caching.annotation.processor.common.ErrorException;
import de.orat.math.cgacasadi.caching.annotation.processor.common.IgnoreException;
import de.orat.math.cgacasadi.caching.annotation.processor.common.WarningException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

public final class Method {

    public final String name;
    public final String returnType;
    public final Set<Modifier> modifiers;

    /**
     * Unmodifiable
     */
    public final List<Parameter> parameters;

    protected Method(ExecutableElement correspondingElement, String enclosingClassQualifiedName, TypeParametersToArguments typeParametersToArguments, Utils utils) throws ErrorException, IgnoreException, WarningException {
        assert correspondingElement.getKind() == ElementKind.METHOD : String.format(
            "Expected \"%s\" to be a method, but was \"%s\".",
            correspondingElement.getSimpleName(), correspondingElement.getKind());

        this.name = correspondingElement.getSimpleName().toString();
        this.returnType = typeParametersToArguments.clearTypeParameterIfPresent(correspondingElement.getReturnType().toString());
        this.modifiers = correspondingElement.getModifiers();

        // Needs to be the first check.
        Uncached uncached = correspondingElement.getAnnotation(Uncached.class);
        if (uncached != null) {
            throw IgnoreException.create(correspondingElement,
                "Ignored.");
        }

        if (!this.returnType.equals(enclosingClassQualifiedName)) {
            throw WarningException.create(correspondingElement,
                "Return type \"%s\" was not the expected one \"%s\".", this.returnType, enclosingClassQualifiedName);
        }

        if (this.modifiers.contains(Modifier.PRIVATE)) {
            throw WarningException.create(correspondingElement,
                "private method \"%s\" will not be cached.", this.name);
        }
        if (this.modifiers.contains(Modifier.STATIC)) {
            throw WarningException.create(correspondingElement,
                "static method \"%s\" will not be cached.", this.name);
        }
        if (this.modifiers.contains(Modifier.ABSTRACT)) {
            throw WarningException.create(correspondingElement,
                "abstract method \"%s\" will not be cached.", this.name);
        }

        this.parameters = computeParameters(correspondingElement, enclosingClassQualifiedName, typeParametersToArguments, utils);
    }

    private static List<Parameter> computeParameters(ExecutableElement correspondingElement, String enclosingClassQualifiedName, TypeParametersToArguments typeParametersToArguments, Utils utils) throws ErrorException {
        List<VariableElement> parameterElements = (List<VariableElement>) correspondingElement.getParameters();
        List<Parameter> parameters = new ArrayList<>(parameterElements.size());
        for (VariableElement parameterElement : parameterElements) {
            utils.exceptionHandler().handle(() -> {
                Parameter parameter = new Parameter(parameterElement, enclosingClassQualifiedName, typeParametersToArguments, utils);
                parameters.add(parameter);
            });
        }

        return Collections.unmodifiableList(parameters);
    }
}
