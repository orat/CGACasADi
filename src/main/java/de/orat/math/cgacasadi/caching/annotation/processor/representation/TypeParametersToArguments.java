package de.orat.math.cgacasadi.caching.annotation.processor.representation;

import de.orat.math.cgacasadi.caching.annotation.processor.common.ErrorException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

public class TypeParametersToArguments {

    private final Map<String, String> innerMap;

    public TypeParametersToArguments(TypeElement correspondingElement) throws ErrorException {
        Map<String, String> typeParametersToArguments = new HashMap<>();
        for (DeclaredType i : (List<DeclaredType>) correspondingElement.getInterfaces()) {
            List<String> params = ((TypeElement) i.asElement()).getTypeParameters().stream()
                .map(param -> param.getSimpleName().toString())
                .toList();

            // Correct cast, because type parameters are prohibited.
            List<String> args = i.getTypeArguments().stream()
                .map(arg -> ((TypeElement) ((DeclaredType) arg).asElement()).getQualifiedName().toString())
                .toList();

            if (params.size() != args.size()) {
                throw new AssertionError("Incorrect assumption 1 in GenerateCachedProcessor:TypeParametersToArguments:computeMethods.");
            }

            for (int pos = 0; pos < params.size(); ++pos) {
                String param = params.get(pos);
                if (typeParametersToArguments.containsKey(param)) {
                    throw new AssertionError("Incorrect assumption 2 in GenerateCachedProcessor:TypeParametersToArguments:computeMethods.");
                }
                typeParametersToArguments.put(param, args.get(pos));
            }
        }

        this.innerMap = typeParametersToArguments;
    }

    public String clearTypeParameterIfPresent(String type) {
        return this.innerMap.getOrDefault(type, type);
    }
}
