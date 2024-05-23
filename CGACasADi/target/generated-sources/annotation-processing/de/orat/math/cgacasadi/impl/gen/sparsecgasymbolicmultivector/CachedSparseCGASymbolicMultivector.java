package de.orat.math.cgacasadi.impl.gen.sparsecgasymbolicmultivector;

import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.orat.math.cgacasadi.caching.CGASymbolicFunctionCache;
import de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector;
import java.util.List;

public final class CachedSparseCGASymbolicMultivector extends SparseCGASymbolicMultivector {
	private static final CGASymbolicFunctionCache CACHE = CGASymbolicFunctionCache.instance();

	public CachedSparseCGASymbolicMultivector(SparseCGASymbolicMultivector mv) {
		super(mv.getName(), mv.getSX());
	}

	public CachedSparseCGASymbolicMultivector(String name, SX sx) {
		super(name, sx);
	}

	/**
	 * @see de.orat.math.gacalc.spi.iMultivectorSymbolic#dual
	 */
	@Override
	public SparseCGASymbolicMultivector dual() {
		String funName = CACHE.createFuncName("dual", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).dual_super());
	}

	private SparseCGASymbolicMultivector dual_super() {
		return super.dual();
	}

	/**
	 * @see de.orat.math.gacalc.spi.iMultivectorSymbolic#op
	 */
	@Override
	public SparseCGASymbolicMultivector op(SparseCGASymbolicMultivector b) {
		String funName = CACHE.createFuncName("op", this, b);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).op_super(params.get(1)));
	}

	private SparseCGASymbolicMultivector op_super(SparseCGASymbolicMultivector b) {
		return super.op(b);
	}

	/**
	 * @see de.orat.math.gacalc.spi.iMultivectorSymbolic#lc
	 */
	@Override
	public SparseCGASymbolicMultivector lc(SparseCGASymbolicMultivector b) {
		String funName = CACHE.createFuncName("lc", this, b);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).lc_super(params.get(1)));
	}

	private SparseCGASymbolicMultivector lc_super(SparseCGASymbolicMultivector b) {
		return super.lc(b);
	}

	/**
	 * @see de.orat.math.gacalc.spi.iMultivectorSymbolic#lc_
	 */
	@Override
	public SparseCGASymbolicMultivector lc_(SparseCGASymbolicMultivector rhs) {
		String funName = CACHE.createFuncName("lc_", this, rhs);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, rhs),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).lc__super(params.get(1)));
	}

	private SparseCGASymbolicMultivector lc__super(SparseCGASymbolicMultivector rhs) {
		return super.lc_(rhs);
	}

	/**
	 * @see de.orat.math.gacalc.spi.iMultivectorSymbolic#rc
	 */
	@Override
	public SparseCGASymbolicMultivector rc(SparseCGASymbolicMultivector b) {
		String funName = CACHE.createFuncName("rc", this, b);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).rc_super(params.get(1)));
	}

	private SparseCGASymbolicMultivector rc_super(SparseCGASymbolicMultivector b) {
		return super.rc(b);
	}

	/**
	 * @see de.orat.math.gacalc.spi.iMultivectorSymbolic#rc2
	 */
	@Override
	public SparseCGASymbolicMultivector rc2(SparseCGASymbolicMultivector b) {
		String funName = CACHE.createFuncName("rc2", this, b);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).rc2_super(params.get(1)));
	}

	private SparseCGASymbolicMultivector rc2_super(SparseCGASymbolicMultivector b) {
		return super.rc2(b);
	}

	/**
	 * @see de.orat.math.gacalc.spi.iMultivectorSymbolic#dot
	 */
	@Override
	public SparseCGASymbolicMultivector dot(SparseCGASymbolicMultivector b) {
		String funName = CACHE.createFuncName("dot", this, b);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).dot_super(params.get(1)));
	}

	private SparseCGASymbolicMultivector dot_super(SparseCGASymbolicMultivector b) {
		return super.dot(b);
	}

	/**
	 * @see de.orat.math.gacalc.spi.iMultivectorSymbolic#ip
	 */
	@Override
	public SparseCGASymbolicMultivector ip(SparseCGASymbolicMultivector b) {
		String funName = CACHE.createFuncName("ip", this, b);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).ip_super(params.get(1)));
	}

	private SparseCGASymbolicMultivector ip_super(SparseCGASymbolicMultivector b) {
		return super.ip(b);
	}

	/**
	 * @see de.orat.math.gacalc.spi.iMultivectorSymbolic#vee
	 */
	@Override
	public SparseCGASymbolicMultivector vee(SparseCGASymbolicMultivector b) {
		String funName = CACHE.createFuncName("vee", this, b);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).vee_super(params.get(1)));
	}

	private SparseCGASymbolicMultivector vee_super(SparseCGASymbolicMultivector b) {
		return super.vee(b);
	}

	/**
	 * @see de.orat.math.gacalc.spi.iMultivectorSymbolic#normalizeByReverseNorm
	 */
	@Override
	public SparseCGASymbolicMultivector normalizeByReverseNorm() {
		String funName = CACHE.createFuncName("normalizeByReverseNorm", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).normalizeByReverseNorm_super());
	}

	private SparseCGASymbolicMultivector normalizeByReverseNorm_super() {
		return super.normalizeByReverseNorm();
	}

	/**
	 * @see de.orat.math.gacalc.spi.iMultivectorSymbolic#div
	 */
	@Override
	public SparseCGASymbolicMultivector div(SparseCGASymbolicMultivector rhs) {
		String funName = CACHE.createFuncName("div", this, rhs);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, rhs),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).div_super(params.get(1)));
	}

	private SparseCGASymbolicMultivector div_super(SparseCGASymbolicMultivector rhs) {
		return super.div(rhs);
	}

	/**
	 * @see de.orat.math.gacalc.spi.iMultivectorSymbolic#versorInverse
	 */
	@Override
	public SparseCGASymbolicMultivector versorInverse() {
		String funName = CACHE.createFuncName("versorInverse", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).versorInverse_super());
	}

	private SparseCGASymbolicMultivector versorInverse_super() {
		return super.versorInverse();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#pseudoscalar
	 */
	@Override
	public SparseCGASymbolicMultivector pseudoscalar() {
		String funName = CACHE.createFuncName("pseudoscalar", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).pseudoscalar_super());
	}

	private SparseCGASymbolicMultivector pseudoscalar_super() {
		return super.pseudoscalar();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#inversePseudoscalar
	 */
	@Override
	public SparseCGASymbolicMultivector inversePseudoscalar() {
		String funName = CACHE.createFuncName("inversePseudoscalar", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).inversePseudoscalar_super());
	}

	private SparseCGASymbolicMultivector inversePseudoscalar_super() {
		return super.inversePseudoscalar();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#sparseEmptyInstance
	 */
	@Override
	public SparseCGASymbolicMultivector sparseEmptyInstance() {
		String funName = CACHE.createFuncName("sparseEmptyInstance", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).sparseEmptyInstance_super());
	}

	private SparseCGASymbolicMultivector sparseEmptyInstance_super() {
		return super.sparseEmptyInstance();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#denseEmptyInstance
	 */
	@Override
	public SparseCGASymbolicMultivector denseEmptyInstance() {
		String funName = CACHE.createFuncName("denseEmptyInstance", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).denseEmptyInstance_super());
	}

	private SparseCGASymbolicMultivector denseEmptyInstance_super() {
		return super.denseEmptyInstance();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#gradeSelection
	 */
	@Override
	public SparseCGASymbolicMultivector gradeSelection(int grade) {
		String funName = CACHE.createFuncName("gradeSelection", this, grade);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).gradeSelection_super(grade));
	}

	private SparseCGASymbolicMultivector gradeSelection_super(int grade) {
		return super.gradeSelection(grade);
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#reverse
	 */
	@Override
	public SparseCGASymbolicMultivector reverse() {
		String funName = CACHE.createFuncName("reverse", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).reverse_super());
	}

	private SparseCGASymbolicMultivector reverse_super() {
		return super.reverse();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#gp
	 */
	@Override
	public SparseCGASymbolicMultivector gp(SparseCGASymbolicMultivector b) {
		String funName = CACHE.createFuncName("gp", this, b);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).gp_super(params.get(1)));
	}

	private SparseCGASymbolicMultivector gp_super(SparseCGASymbolicMultivector b) {
		return super.gp(b);
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#gradeInversion
	 */
	@Override
	public SparseCGASymbolicMultivector gradeInversion() {
		String funName = CACHE.createFuncName("gradeInversion", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).gradeInversion_super());
	}

	private SparseCGASymbolicMultivector gradeInversion_super() {
		return super.gradeInversion();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#undual
	 */
	@Override
	public SparseCGASymbolicMultivector undual() {
		String funName = CACHE.createFuncName("undual", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).undual_super());
	}

	private SparseCGASymbolicMultivector undual_super() {
		return super.undual();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#conjugate
	 */
	@Override
	public SparseCGASymbolicMultivector conjugate() {
		String funName = CACHE.createFuncName("conjugate", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).conjugate_super());
	}

	private SparseCGASymbolicMultivector conjugate_super() {
		return super.conjugate();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#scp
	 */
	@Override
	public SparseCGASymbolicMultivector scp(SparseCGASymbolicMultivector rhs) {
		String funName = CACHE.createFuncName("scp", this, rhs);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, rhs),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).scp_super(params.get(1)));
	}

	private SparseCGASymbolicMultivector scp_super(SparseCGASymbolicMultivector rhs) {
		return super.scp(rhs);
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#add
	 */
	@Override
	public SparseCGASymbolicMultivector add(SparseCGASymbolicMultivector b) {
		String funName = CACHE.createFuncName("add", this, b);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).add_super(params.get(1)));
	}

	private SparseCGASymbolicMultivector add_super(SparseCGASymbolicMultivector b) {
		return super.add(b);
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#sub
	 */
	@Override
	public SparseCGASymbolicMultivector sub(SparseCGASymbolicMultivector b) {
		String funName = CACHE.createFuncName("sub", this, b);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).sub_super(params.get(1)));
	}

	private SparseCGASymbolicMultivector sub_super(SparseCGASymbolicMultivector b) {
		return super.sub(b);
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#negate14
	 */
	@Override
	public SparseCGASymbolicMultivector negate14() {
		String funName = CACHE.createFuncName("negate14", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).negate14_super());
	}

	private SparseCGASymbolicMultivector negate14_super() {
		return super.negate14();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#scalarAbs
	 */
	@Override
	public SparseCGASymbolicMultivector scalarAbs() {
		String funName = CACHE.createFuncName("scalarAbs", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).scalarAbs_super());
	}

	private SparseCGASymbolicMultivector scalarAbs_super() {
		return super.scalarAbs();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#scalarAtan2
	 */
	@Override
	public SparseCGASymbolicMultivector scalarAtan2(SparseCGASymbolicMultivector y) {
		String funName = CACHE.createFuncName("scalarAtan2", this, y);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, y),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).scalarAtan2_super(params.get(1)));
	}

	private SparseCGASymbolicMultivector scalarAtan2_super(SparseCGASymbolicMultivector y) {
		return super.scalarAtan2(y);
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#scalarSqrt
	 */
	@Override
	public SparseCGASymbolicMultivector scalarSqrt() {
		String funName = CACHE.createFuncName("scalarSqrt", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).scalarSqrt_super());
	}

	private SparseCGASymbolicMultivector scalarSqrt_super() {
		return super.scalarSqrt();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#exp
	 */
	@Override
	public SparseCGASymbolicMultivector exp() {
		String funName = CACHE.createFuncName("exp", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).exp_super());
	}

	private SparseCGASymbolicMultivector exp_super() {
		return super.exp();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#sqrt
	 */
	@Override
	public SparseCGASymbolicMultivector sqrt() {
		String funName = CACHE.createFuncName("sqrt", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).sqrt_super());
	}

	private SparseCGASymbolicMultivector sqrt_super() {
		return super.sqrt();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#log
	 */
	@Override
	public SparseCGASymbolicMultivector log() {
		String funName = CACHE.createFuncName("log", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).log_super());
	}

	private SparseCGASymbolicMultivector log_super() {
		return super.log();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#meet
	 */
	@Override
	public SparseCGASymbolicMultivector meet(SparseCGASymbolicMultivector b) {
		String funName = CACHE.createFuncName("meet", this, b);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).meet_super(params.get(1)));
	}

	private SparseCGASymbolicMultivector meet_super(SparseCGASymbolicMultivector b) {
		return super.meet(b);
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#join
	 */
	@Override
	public SparseCGASymbolicMultivector join(SparseCGASymbolicMultivector b) {
		String funName = CACHE.createFuncName("join", this, b);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this, b),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).join_super(params.get(1)));
	}

	private SparseCGASymbolicMultivector join_super(SparseCGASymbolicMultivector b) {
		return super.join(b);
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#norm
	 */
	@Override
	public SparseCGASymbolicMultivector norm() {
		String funName = CACHE.createFuncName("norm", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).norm_super());
	}

	private SparseCGASymbolicMultivector norm_super() {
		return super.norm();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#inorm
	 */
	@Override
	public SparseCGASymbolicMultivector inorm() {
		String funName = CACHE.createFuncName("inorm", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).inorm_super());
	}

	private SparseCGASymbolicMultivector inorm_super() {
		return super.inorm();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#normalizeBySquaredNorm
	 */
	@Override
	public SparseCGASymbolicMultivector normalizeBySquaredNorm() {
		String funName = CACHE.createFuncName("normalizeBySquaredNorm", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).normalizeBySquaredNorm_super());
	}

	private SparseCGASymbolicMultivector normalizeBySquaredNorm_super() {
		return super.normalizeBySquaredNorm();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#normalizeEvenElement
	 */
	@Override
	public SparseCGASymbolicMultivector normalizeEvenElement() {
		String funName = CACHE.createFuncName("normalizeEvenElement", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).normalizeEvenElement_super());
	}

	private SparseCGASymbolicMultivector normalizeEvenElement_super() {
		return super.normalizeEvenElement();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#generalInverse
	 */
	@Override
	public SparseCGASymbolicMultivector generalInverse() {
		String funName = CACHE.createFuncName("generalInverse", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).generalInverse_super());
	}

	private SparseCGASymbolicMultivector generalInverse_super() {
		return super.generalInverse();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#scalarInverse
	 */
	@Override
	public SparseCGASymbolicMultivector scalarInverse() {
		String funName = CACHE.createFuncName("scalarInverse", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).scalarInverse_super());
	}

	private SparseCGASymbolicMultivector scalarInverse_super() {
		return super.scalarInverse();
	}

	/**
	 * @see de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector#reverseNorm
	 */
	@Override
	public SparseCGASymbolicMultivector reverseNorm() {
		String funName = CACHE.createFuncName("reverseNorm", this);
		return CACHE.getOrCreateSymbolicFunction(funName, List.of(this),
		    (List<CachedSparseCGASymbolicMultivector> params) -> params.get(0).reverseNorm_super());
	}

	private SparseCGASymbolicMultivector reverseNorm_super() {
		return super.reverseNorm();
	}
}
