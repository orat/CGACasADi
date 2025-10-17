package de.orat.math.cgacasadi.impl;

import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.function.Supplier;
import java.util.concurrent.ConcurrentHashMap;
import de.orat.math.gacalc.spi.iConstantsFactorySymbolic;

public class CGAConstantsSymbolic implements iCGAConstants<SparseCGASymbolicMultivector>, iConstantsFactorySymbolic<SparseCGASymbolicMultivector> {

    public static final CGAConstantsSymbolic instance = new CGAConstantsSymbolic();

    private CGAConstantsSymbolic() {

    }

    @Override
    public CGAExprGraphFactory fac() {
        return CGAExprGraphFactory.instance;
    }

    @Override
    public SparseCGASymbolicMultivector getSparseEmptyInstance() {
        String name = "SparseEmptyInstance";
        return cached2(name, () -> PurelySymbolicCachedSparseCGASymbolicMultivector.createSparse(name));
    }

    @Override
    public SparseCGASymbolicMultivector getDenseEmptyInstance() {
        String name = "DenseEmptyInstance";
        return cached2(name, () -> PurelySymbolicCachedSparseCGASymbolicMultivector.createDense(name));
    }

    // ConcurrentHashMap to avoid ConcurrentModificationException while testing.
    private final ConcurrentHashMap<String, SparseCGASymbolicMultivector> cache2
        = new ConcurrentHashMap<>(128, 0.5f);

    @Override
    public SparseCGASymbolicMultivector cached(String name, Supplier<SparseDoubleMatrix> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache2.get(name);
        if (value == null) {
            var sparseDoubleMatrix = creator.get();
            value = SparseCGASymbolicMultivector.create(name, sparseDoubleMatrix);
            this.cache2.putIfAbsent(name, value);
        }
        return value;
    }

    @Override
    public SparseCGASymbolicMultivector cached2(String name, Supplier<SparseCGASymbolicMultivector> creator) {
        // Avoid Recursive Update exception happening with computeIfAbsent.
        var value = this.cache2.get(name);
        if (value == null) {
            value = creator.get();
            this.cache2.putIfAbsent(name, value);
        }
        return value;
    }
}
