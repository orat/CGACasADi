package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.orat.math.gacalc.spi.iMultivectorSymbolic;

public interface ISparseCGASymbolicMultivector<T_ISparseCGASymbolicMultivector extends ISparseCGASymbolicMultivector<T_ISparseCGASymbolicMultivector>> extends iMultivectorSymbolic<T_ISparseCGASymbolicMultivector> {

    SX getSX();
}
