package de.orat.math.cgacasadi.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import de.orat.math.gacalc.caching.iFunctionSymbolicCache;

public class CGASymbolicFunctionCache implements iFunctionSymbolicCache<SparseCGASymbolicMultivector, CachedSparseCGASymbolicMultivector> {

    private final Map<String, CGASymbolicFunction> functionCache = new HashMap<>();

    @Override
    public CachedSparseCGASymbolicMultivector getOrCreateSymbolicFunction(String name, List<SparseCGASymbolicMultivector> args, Function<List<SparseCGASymbolicMultivector>, SparseCGASymbolicMultivector> res) {
        CGASymbolicFunction fun = functionCache.get(name);
        if (fun == null) {
            final int size = args.size();
            List<SparseCGASymbolicMultivector> params = new ArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                SparseCGASymbolicMultivector arg = args.get(i);
                // Convert to purely symbolic multivector.
                SparseCGASymbolicMultivector param = new SparseCGASymbolicMultivector(Integer.toString(i), arg.getSparsity());
                params.add(param);
            }
            fun = new CGASymbolicFunction(name, params, List.of(res.apply(params)));
            functionCache.put(name, fun);
        }
        SparseCGASymbolicMultivector retVal = fun.callSymbolic(args).get(0);
        return new CachedSparseCGASymbolicMultivector(this, retVal);
    }

    @Override
    public Set<String> getCachedFunktionNames() {
        return Collections.unmodifiableSet(this.functionCache.keySet());
    }

    // special characters like ._-[]{} are not allowed as function names
    @Override
    public String createBipedFuncName(String name, int[] arg1Grades, int[] arg2Grades) {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("a");
        for (int i = 0; i < arg1Grades.length - 1; i++) {
            sb.append(String.valueOf(arg1Grades[i]));
            //sb.append("-");
        }
        sb.append(String.valueOf(arg1Grades[arg1Grades.length - 1]));
        sb.append("b");
        for (int i = 0; i < arg2Grades.length - 1; i++) {
            sb.append(String.valueOf(arg2Grades[i]));
            //sb.append("-");
        }
        sb.append(String.valueOf(arg2Grades[arg2Grades.length - 1]));
        //sb.append("_");
        return sb.toString();
    }

    // @Override
    public String createBipedFuncName(String name, int[] arg1Grades, String constName) {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("a");
        for (int i = 0; i < arg1Grades.length - 1; i++) {
            sb.append(String.valueOf(arg1Grades[i]));
            //sb.append("-");
        }
        sb.append(String.valueOf(arg1Grades[arg1Grades.length - 1]));
        sb.append("b");
        sb.append(constName);
        //sb.append("_");
        return sb.toString();
    }
}
