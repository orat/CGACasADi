package de.orat.math.cgacasadi.delegating.annotation.processor.representation;

import de.orat.math.cgacasadi.delegating.annotation.processor.GenerateDelegatingProcessor.Utils;
import de.orat.math.cgacasadi.delegating.annotation.processor.common.WarningException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public final class Method {

    public final String name;
    public final String returnType;
    public final Set<Modifier> modifiers;

    /**
     * Unmodifiable
     */
    public final List<Parameter> parameters;
    public final String enclosingType;

    protected Method(ExecutableElement correspondingElement, String enclosingClassQualifiedName, TypeParametersToArguments typeParametersToArguments, Utils utils) throws WarningException {
        assert correspondingElement.getKind() == ElementKind.METHOD : String.format(
            "Expected \"%s\" to be a method, but was \"%s\".",
            correspondingElement.getSimpleName(), correspondingElement.getKind());

        this.enclosingType = ((TypeElement) correspondingElement.getEnclosingElement()).getQualifiedName().toString();
        this.name = correspondingElement.getSimpleName().toString();
        this.returnType = typeParametersToArguments.clearTypeParameterIfPresent(correspondingElement.getReturnType().toString());
        this.modifiers = new HashSet<>(correspondingElement.getModifiers());
        modifiers.remove(Modifier.DEFAULT);
        modifiers.remove(Modifier.ABSTRACT);

        if (this.modifiers.contains(Modifier.PRIVATE)) {
            throw WarningException.create(correspondingElement,
                "\"%s\": private method will not be cached.", this.name);
        }
        if (this.modifiers.contains(Modifier.STATIC)) {
            throw WarningException.create(correspondingElement,
                "\"%s\": static method will not be cached.", this.name);
        }

        if (!this.returnType.equals(enclosingClassQualifiedName)) {
            throw WarningException.create(correspondingElement,
                "\"%s\": Return type \"%s\" was not the expected one \"%s\".", this.name, this.returnType, enclosingClassQualifiedName);
        }

        this.parameters = computeParameters(correspondingElement, enclosingClassQualifiedName, typeParametersToArguments, utils);
    }

    private static List<Parameter> computeParameters(ExecutableElement correspondingElement, String enclosingClassQualifiedName, TypeParametersToArguments typeParametersToArguments, Utils utils) {
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
