package de.orat.math.cgacasadi.impl;

import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.function.Supplier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import de.orat.math.gacalc.spi.iConstantsSymbolic;

public class CGAConstantsSymbolic implements iConstantsSymbolic<SparseCGASymbolicMultivector> {

    public static final CGAConstantsSymbolic instance = new CGAConstantsSymbolic();

    private CGAConstantsSymbolic() {

    }

    @Override
    public SparseCGASymbolicMultivector newConstant(String name, SparseDoubleMatrix definition) {
        return SparseCGASymbolicMultivector.create(name, definition);
    }

    @Override
    public SparseCGASymbolicMultivector newConstant(String name, SparseCGASymbolicMultivector definition) {
        return SparseCGASymbolicMultivector.create(name, definition.getSX());
    }

    @Override
    public CGAExprGraphFactory fac() {
        return CGAExprGraphFactory.instance;
    }

    // ConcurrentHashMap to avoid ConcurrentModificationException while testing.
    private final Map<Supplier<SparseCGASymbolicMultivector>, SparseCGASymbolicMultivector> cache
        = new ConcurrentHashMap<>(128, 0.5f);

    @Override
    public SparseCGASymbolicMultivector cached(Supplier<SparseCGASymbolicMultivector> creator) {
        return this.cache.computeIfAbsent(creator, Supplier::get);
    }

    //TODO
    // In Gameron steht aber pseudoscalar().reverse()/(pseudoscalar left contraction pseudoscalar().reverse())
    // vielleicht ist das die Impl. die unabhängig von ga model ist und die impl hier
    // geht nur für CGA?
    public SparseCGASymbolicMultivector getInversePseudoscalar() {
        return cached(() -> newConstant("E˜", this.getPseudoscalar().reverse()));
    }
}
