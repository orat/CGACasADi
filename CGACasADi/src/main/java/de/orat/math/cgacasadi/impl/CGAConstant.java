package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.orat.math.cgacasadi.impl.gen.CachedSparseCGASymbolicMultivector;
import de.orat.math.gacalc.spi.iConstant;
import de.orat.math.sparsematrix.SparseDoubleMatrix;

public class CGAConstant extends CachedSparseCGASymbolicMultivector implements iConstant<SparseCGASymbolicMultivector> {

    public CGAConstant(String name, SparseDoubleMatrix sparseDoubleMatrix) {
        super(SparseCGASymbolicMultivector.create(name, sparseDoubleMatrix));
    }

    public CGAConstant(SparseCGASymbolicMultivector mv) {
        super(mv);
    }

    public CGAConstant(String name, SX sx) {
        super(name, sx);
    }
}
