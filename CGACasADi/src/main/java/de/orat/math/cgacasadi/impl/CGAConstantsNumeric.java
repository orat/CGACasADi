package de.orat.math.cgacasadi.impl;

import de.orat.math.gacalc.spi.iConstantsFactoryNumeric;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class CGAConstantsNumeric implements iCGAConstants<SparseCGANumericMultivector>, iConstantsFactoryNumeric<SparseCGANumericMultivector, SparseCGASymbolicMultivector> {

    public static final CGAConstantsNumeric instance = new CGAConstantsNumeric();

    private CGAConstantsNumeric() {

    }

    // Numeric multivectors don't have names.
    @Override
    public SparseCGANumericMultivector newConstant(String name, SparseDoubleMatrix definition) {
        return fac().createMultivectorNumeric(definition);
    }

    // Numeric multivectors don't have names.
    @Override
    public SparseCGANumericMultivector newConstant(String name, SparseCGANumericMultivector definition) {
        return definition;
    }

    @Override
    public CGAExprGraphFactory fac() {
        return CGAExprGraphFactory.instance;
    }

    // ConcurrentHashMap to avoid ConcurrentModificationException while testing.
    private final Map<Supplier<SparseCGANumericMultivector>, SparseCGANumericMultivector> cache
        = new ConcurrentHashMap<>(128, 0.5f);

    @Override
    public synchronized SparseCGANumericMultivector cached(Supplier<SparseCGANumericMultivector> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache.get(creator);
        if (value == null) {
            value = creator.get();
            this.cache.put(creator, value);
        }
        return value;
    }
}
