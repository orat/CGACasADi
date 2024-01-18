package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.Function;
import de.dhbw.rahmlab.casadi.impl.casadi.MX;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDM;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorMX;
import de.orat.math.gacalc.api.FunctionSymbolic;
import de.orat.math.gacalc.spi.iFunctionSymbolic;
import de.orat.math.gacalc.spi.iMultivectorNumeric;
import de.orat.math.gacalc.spi.iMultivectorSymbolic;
import java.util.List;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGASymbolicFunction implements iFunctionSymbolic {

	// available after plugging the impl into the api object
	private FunctionSymbolic.Callback callback;

	private final Function f_sym_casadi;
	private final String name;
	private final int arity;
	private final int resultCount;

	public CGASymbolicFunction(String name, List<iMultivectorSymbolic> parameters, List<iMultivectorSymbolic> returns) {
		this.name = name;
		this.arity = parameters.size();
		this.resultCount = returns.size();
		var f_sym_in = transform(parameters);
		var f_sym_out = transform(returns);
		this.f_sym_casadi = new Function(name, f_sym_in, f_sym_out);
	}

	protected static StdVectorMX transform(List<iMultivectorSymbolic> mvs) {
		List<MX> mxs = mvs.stream().map(mv -> ((SparseCGASymbolicMultivector) mv).getMX()).toList();
		return new StdVectorMX(mxs);
	}

	@Override
	public List<iMultivectorSymbolic> callSymbolic(List<iMultivectorSymbolic> arguments) {
		var f_sym_in = transform(arguments);
		var f_sym_out = new StdVectorMX();
		this.f_sym_casadi.call(f_sym_in, f_sym_out);
		return f_sym_out.stream().map(mx -> ((iMultivectorSymbolic) new SparseCGASymbolicMultivector(mx))).toList();
	}

	@Override
	public List<iMultivectorNumeric> callNumeric(List<iMultivectorNumeric> arguments) {
		var f_num_in = new StdVectorDM(arguments.stream().map(
			imvn -> ((SparseCGANumericMultivector) imvn).dm).toList());
		var f_num_out = new StdVectorDM();
		this.f_sym_casadi.call(f_num_in, f_num_out);
		return f_num_out.stream().map(dm -> ((iMultivectorNumeric) new SparseCGANumericMultivector(dm))).toList();
	}

	@Override
	public String toString() {
		return f_sym_casadi.toString();
	}

	@Override
	public void init(FunctionSymbolic.Callback callback) {
		this.callback = callback;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getArity() {
		return this.arity;
	}

	@Override
	public int getResultCount() {
		return this.resultCount;
	}
}
