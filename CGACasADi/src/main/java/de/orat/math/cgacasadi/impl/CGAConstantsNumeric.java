package de.orat.math.cgacasadi.impl;

import de.orat.math.gacalc.spi.iConstantsFactoryNumeric;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class CGAConstantsNumeric implements iCGAConstants<SparseCGANumericMultivector>, iConstantsFactoryNumeric<SparseCGANumericMultivector, SparseCGASymbolicMultivector> {

    public static final CGAConstantsNumeric instance = new CGAConstantsNumeric();

    private CGAConstantsNumeric() {

    }

    @Override
    public CGAExprGraphFactory fac() {
        return CGAExprGraphFactory.instance;
    }

    @Override
    public SparseCGANumericMultivector getSparseEmptyInstance() {
        String name = "SparseEmptyInstance";
        return cached2(name, () -> new SparseCGANumericMultivector(PurelySymbolicCachedSparseCGASymbolicMultivector.createSparse(name)));
    }

    @Override
    public SparseCGANumericMultivector getDenseEmptyInstance() {
        String name = "DenseEmptyInstance";
        return cached2(name, () -> new SparseCGANumericMultivector(PurelySymbolicCachedSparseCGASymbolicMultivector.createDense(name)));
    }

    // ConcurrentHashMap to avoid ConcurrentModificationException while testing.
    private final ConcurrentHashMap<String, SparseCGANumericMultivector> cache2
        = new ConcurrentHashMap<>(128, 0.5f);

    @Override
    public SparseCGANumericMultivector cached(String name, Supplier<SparseDoubleMatrix> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache2.get(name);
        if (value == null) {
            var mvSym = CGAConstantsSymbolic.instance.cached(name, creator);
            value = new SparseCGANumericMultivector(mvSym);
            this.cache2.putIfAbsent(name, value);
        }
        return value;
    }

    @Override
    public SparseCGANumericMultivector cached2(String name, Supplier<SparseCGANumericMultivector> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache2.get(name);
        if (value == null) {
            var mvSym = CGAConstantsSymbolic.instance.cached2(name, () -> creator.get().toSymbolic());
            value = new SparseCGANumericMultivector(mvSym);
            this.cache2.putIfAbsent(name, value);
        }
        return value;
    }

    public void testCache() {
        cache2.values().forEach(mv -> System.out.println(mv));
        System.out.println("------------------------");
    }
}
