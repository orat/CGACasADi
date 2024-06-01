package de.orat.math.cgacasadi.impl;

import de.orat.math.gacalc.spi.iExprGraphFactory;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import java.util.function.Supplier;
import de.orat.math.gacalc.spi.iConstantsProvider;
import java.util.HashMap;
import java.util.Map;

public class CGAConstantsProvider implements iConstantsProvider<SparseCGASymbolicMultivector, CGAConstant> {

    @Override
    public CGAConstant newConstant(String name, SparseDoubleMatrix definition) {
        return new CGAConstant(name, definition);
    }

    @Override
    public iExprGraphFactory<SparseCGASymbolicMultivector, PurelySymbolicCachedSparseCGASymbolicMultivector> fac() {
        return CGAExprGraphFactory.instance;
    }

    private final Map<Supplier<CGAConstant>, CGAConstant> cache = new HashMap<>(128, 0.5f);

    @Override
    public CGAConstant cached(Supplier<CGAConstant> creator) {
        return this.cache.computeIfAbsent(creator, Supplier::get);
    }

    //TODO
    // In Gameron steht aber pseudoscalar().reverse()/(pseudoscalar left contraction pseudoscalar().reverse())
    // vielleicht ist das die Impl. die unabhängig von ga model ist und die impl hier
    // geht nur für CGA?
    @Override
    public CGAConstant getInversePseudoscalar() {
        return cached(() -> new CGAConstant("E˜", this.getPseudoscalar().reverse().getSX()));
    }

}
