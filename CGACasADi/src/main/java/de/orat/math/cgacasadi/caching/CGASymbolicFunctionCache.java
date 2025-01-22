package de.orat.math.cgacasadi.caching;

import de.orat.math.cgacasadi.impl.CGASymbolicFunction;
import de.orat.math.cgacasadi.impl.PurelySymbolicCachedSparseCGASymbolicMultivector;
import de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector;
import de.orat.math.cgacasadi.impl.gen.CachedSparseCGASymbolicMultivector;
import de.orat.math.gacalc.spi.iMultivectorSymbolic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * No check for function name collisions. Especially hazardous if an instance is used by more than one class.
 */
public class CGASymbolicFunctionCache implements ISafePublicFunctionCache {

    private final Map<String, CGASymbolicFunction> functionCache
        = new HashMap<>(1024, 0.5f);
    private final Map<String, Integer> cachedFunctionsUsage
        = new HashMap<>(1024, 0.5f);

    public CachedSparseCGASymbolicMultivector getOrCreateSymbolicFunction(String name, List<SparseCGASymbolicMultivector> args, Function<List<? extends CachedSparseCGASymbolicMultivector>, SparseCGASymbolicMultivector> res) {
        CGASymbolicFunction func = functionCache.get(name);

        // Create func if not present.
        if (func == null) {
            final int size = args.size();
            List<PurelySymbolicCachedSparseCGASymbolicMultivector> casadiFuncParams = new ArrayList<>(size);
            List<PurelySymbolicCachedSparseCGASymbolicMultivector> symbolicMultivectorParams = new ArrayList<>(size);
            Map<SparseCGASymbolicMultivector, PurelySymbolicCachedSparseCGASymbolicMultivector> uniqueArgsToParams = new IdentityHashMap<>(size);

            // Convert to purely symbolic multivector.
            for (int i = 0; i < size; ++i) {
                SparseCGASymbolicMultivector arg = args.get(i);
                // sparsity
                var param = new PurelySymbolicCachedSparseCGASymbolicMultivector(getParamName(i), arg.getSparsity());
                casadiFuncParams.add(param);

                // Preserve identity for symbolicMultivectorParams.
                var paramOfArg = uniqueArgsToParams.get(arg);
                if (paramOfArg == null) {
                    // Arg not seen before.
                    symbolicMultivectorParams.add(param);
                    uniqueArgsToParams.put(arg, param);
                } else {
                    // Arg seen before.
                    symbolicMultivectorParams.add(paramOfArg);
                }
            }

            // Specific type: CachedSparseCGASymbolicMultivector.
            SparseCGASymbolicMultivector symbolicReturn = res.apply(symbolicMultivectorParams);
            func = new CGASymbolicFunction(String.format("cache_func_%s", functionCache.size()), casadiFuncParams, List.of(symbolicReturn));
            functionCache.put(name, func);
            cachedFunctionsUsage.put(name, 0);
        }

        // Specific type: CachedSparseCGASymbolicMultivector.
        SparseCGASymbolicMultivector retVal = func.callSymbolic(args).get(0);
        cachedFunctionsUsage.compute(name, (k, v) -> ++v);
        return new CachedSparseCGASymbolicMultivector(retVal);
    }

    @Override
    public void clearCache() {
        this.functionCache.clear();
        this.cachedFunctionsUsage.clear();
    }

    @Override
    public Map<String, Integer> getUnmodifiableCachedFunctionsUsage() {
        return Collections.unmodifiableMap(this.cachedFunctionsUsage);
    }

    @Override
    public SortedMap<String, Integer> getSortedUnmodifiableCachedFunctionsUsage() {
        return new TreeMap<>(getUnmodifiableCachedFunctionsUsage());
    }

    @Override
    public int getCacheSize() {
        return this.functionCache.size();
    }

    @Override
    public String cachedFunctionUsageToString() {
        SortedMap<String, Integer> cachedFunctionUsage = getSortedUnmodifiableCachedFunctionsUsage();
        int maxKeyLength = cachedFunctionUsage.entrySet().stream()
            .mapToInt(entry -> entry.getKey().length())
            .max().orElse(0);
        String format = String.format("%%-%ss : %%s", maxKeyLength);
        return cachedFunctionUsage.entrySet().stream()
            .map(entry -> String.format(format, entry.getKey(), entry.getValue()))
            .collect(Collectors.joining("\n"));
    }

    private static String getParamName(int i) {
        return String.format("a%s", i);
    }

    /**
     * Example: [0:a, 1:a, 2:b] -> [0:0, 1:0, 2:1]
     */
    private static List<Integer> computeIdentityIndexCorrection(Object[] objects) {
        Map<Object, Integer> uniqueObjectsToIndizes = new IdentityHashMap<>(objects.length);
        List<Integer> indexCorrection = new ArrayList<>(objects.length);
        int highestInt = 0;
        for (int objectIndex = 0; objectIndex < objects.length; ++objectIndex) {
            Object object = objects[objectIndex];
            Integer indexOfObject = uniqueObjectsToIndizes.get(object);
            if (indexOfObject == null) {
                // Object not seen before.
                uniqueObjectsToIndizes.put(object, highestInt);
                indexCorrection.add(highestInt);
                ++highestInt;
            } else {
                // Object seen before.
                indexCorrection.add(indexOfObject);
            }
        }
        return indexCorrection;
    }

    /**
     * @param params Either iMultivectorSymbolic or int
     */
    public String createFuncName(String name, Object... params) {
        List<Integer> indexCorrection = computeIdentityIndexCorrection(params);
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("_");
        for (int paramIndex = 0; paramIndex < params.length; ++paramIndex) {
            sb.append("_"); // Consecutive "_" are not be compatible with casadi function names.
            sb.append(getParamName(indexCorrection.get(paramIndex)));
            Object param = params[paramIndex];
            if (param instanceof iMultivectorSymbolic mv) {
                // complete sparsity of the parameter
                String colind = Arrays.stream(mv.getSparsity().getcolind())
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining("_"));
                String row = Arrays.stream(mv.getSparsity().getrow())
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining("_"));
                if (!colind.isEmpty()) {
                    sb.append("_colind_").append(colind);
                }
                if (!row.isEmpty()) {
                    sb.append("_row_").append(row);
                }
            } else if (param instanceof Integer intParam) {
                sb.append("_");
                sb.append(intParam);
            } else {
                throw new RuntimeException("Param of unexpected type.");
            }
            sb.append("_");
        }

        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
