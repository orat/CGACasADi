package de.orat.math.cgacasadi.impl;

import de.orat.math.gacalc.spi.iConstantsFactory;
import de.orat.math.gacalc.spi.iMultivector;

public interface iCGAConstants<IMultivector extends iMultivector<IMultivector>> extends iConstantsFactory<IMultivector> {

    //TODO
    // In Gameron steht aber pseudoscalar().reverse()/(pseudoscalar left contraction pseudoscalar().reverse())
    // vielleicht ist das die Impl. die unabhängig von ga model ist und die impl hier
    // geht nur für CGA?
    @Override
    default IMultivector getInversePseudoscalar() {
        return cached(() -> newConstant("E˜", this.getPseudoscalar().reverse()));
    }
}
