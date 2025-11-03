package de.orat.math.cgacasadi.impl;

import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDouble;
import util.cga.CGACayleyTableGeometricProduct;
import de.orat.math.cgacasadi.CasADiUtil;
import de.orat.math.cgacasadi.delegating.annotation.api.GenerateDelegate;
import de.orat.math.cgacasadi.impl.gen.DelegatingSparseCGANumericMultivector;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.gacalc.spi.iConstantsFactory;
import de.orat.math.gacalc.spi.iMultivectorNumeric;
import de.orat.math.gacalc.util.GeometricObject;
import de.orat.math.gacalc.util.GeometricObject.GeometricType;
import de.orat.math.gacalc.util.GeometricObject.Sign;
import de.orat.math.gacalc.util.GeometricObject.Space;
import de.orat.math.gacalc.util.GeometricObject.Type;
import static de.orat.math.gacalc.util.GeometricObject.Type.REAL;
import de.orat.math.gacalc.util.Tuple;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import org.apache.commons.math3.util.Precision;
import util.cga.CGACayleyTable;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
@GenerateDelegate(to = SparseCGASymbolicMultivector.class)
public class SparseCGANumericMultivector extends DelegatingSparseCGANumericMultivector implements iMultivectorNumeric<SparseCGANumericMultivector, SparseCGASymbolicMultivector>, ISparseCGASymbolicMultivector {

    private final static CGACayleyTableGeometricProduct baseCayleyTable = CGACayleyTableGeometricProduct.instance();

    private MultivectorNumeric.Callback callback;
    
    @Override
    public void init(MultivectorNumeric.Callback callback) {
        this.callback = callback;
    }

    //======================================================
    // Constructors and static creators.
    // -> Constructors must only used within subclasses.
    //======================================================
    /**
     * Constructors must only used within subclasses. Can create invalid (not numeric)
     * SparseCGANumericMultivector if used wrong.
     */
    @Deprecated
    protected SparseCGANumericMultivector(SparseCGASymbolicMultivector sym) {
        super(sym);
    }

    /**
     * Constructors must only used within subclasses.
     */
    private SparseCGANumericMultivector(DM dm) {
        super(SparseCGASymbolicMultivector.create(dm));
    }

    /**
     * Can create invalid (not numeric) SparseCGANumericMultivector if used wrong.
     */
    @Deprecated
    @Override
    protected SparseCGANumericMultivector create(SparseCGASymbolicMultivector sym) {
        return new SparseCGANumericMultivector(sym);
    }

    public static SparseCGANumericMultivector create(DM dm) {
        return new SparseCGANumericMultivector(dm);
    }

    public static SparseCGANumericMultivector create(double[] values) {
        if (baseCayleyTable.getBladesCount() != values.length) {
            throw new IllegalArgumentException("Construction of CGA multivevector failed because given array has wrong length "
                + String.valueOf(values.length));
        }
        var dm = CasADiUtil.toDM(values);
        return new SparseCGANumericMultivector(dm);
    }

    public static SparseCGANumericMultivector create(double[] nonzeros, int[] rows) {
        if (baseCayleyTable.getBladesCount() < nonzeros.length) {
            throw new IllegalArgumentException("Construction of CGA multivevector failed because given array has wrong length "
                + String.valueOf(nonzeros.length));
        }
        if (nonzeros.length != rows.length) {
            throw new IllegalArgumentException("Construction of CGA multivector failed because nonzeros.length != rows.length!");
        }
        var dm = CasADiUtil.toDM(baseCayleyTable.getBladesCount(), nonzeros, rows);
        return new SparseCGANumericMultivector(dm);
    }

    private DM lazyDM = null;

    public DM getDM() {
        if (this.lazyDM == null) {
            /*
            * https://github.com/casadi/casadi/wiki/L_rf
            * Evaluates the expression numerically.
            * An error is raised when the expression contains symbols.
             */
            this.lazyDM = SX.evalf(super.delegate.getSX());
        }
        return lazyDM;
    }

    @Override
    public String toString() {
        return this.getDM().toString();
    }
    /*public String toString(){
        return callback.toString();
    }*/
    
    /**
     * Get a complete multivector as double[], inclusive structural 0 values.
     *
     * @return double[32] elements corresponding to the underlaying implementation specific coordindate
     * system.
     */
    @Override
    public SparseDoubleMatrix elements() {
        return CasADiUtil.elements(this.getDM());
    }
    
    
    // compose multivectors corresponding to specific geometric objets to test the decomposition methods
    
    // TODO in welches Interface muss das? welches Interface soll returned werden
    // eventuell hier sign für weight als argument einführen
    public static SparseCGANumericMultivector compose(GeometricObject obj){
        SparseCGANumericMultivector result = null;
        switch (obj.geometricType){
            case GeometricType.ROUND_POINT:
                result = createIPNSRoundPoint(obj);
                break;
            case GeometricType.SPHERE:
                result = createIPNSSphere(obj);
                break;
            case GeometricType.CIRCLE:
                result = createIPNSCircle(obj);
                break;
            case GeometricType.ORIENTED_POINT:
                result = createIPNSOrientedPoint(obj);
                break;
            case GeometricType.PLANE:
                result = createIPNSPlane(obj);
                break;
            case GeometricType.LINE:
                result = createIPNSLine(obj);
                break;
            case GeometricType.SCREW:
                throw new RuntimeException("not yet implemented!");
            case GeometricType.DIPOLE:
                result = createIPNSDipole2(obj);
                break;
            case GeometricType.FLAT_POINT:
                result = createIPNSFlatPoint(obj);
                break;
            //TODO
            default:
                throw new RuntimeException("Composition of the given type is not yet supported!");
        }
        if (!obj.isIPNS()){ 
            result = result.dual();
        }    
        return result;
    }
    public static SparseCGANumericMultivector createIPNSRoundPoint(Tuple location, double signedWeight){
        SparseCGANumericMultivector inf = getConstantsNumeric().getBaseVectorInfinity();
        SparseCGANumericMultivector o = getConstantsNumeric().getBaseVectorOrigin();
        SparseCGANumericMultivector c = createE3(location);
         return o.add(c).add(inf.gpWithScalar(0.5*location.squaredNorm())).
            gpWithScalar(signedWeight);
    }
    private static SparseCGANumericMultivector createIPNSRoundPoint(GeometricObject obj){
        return createIPNSRoundPoint(obj.location[0], obj.getSignedWeight());
    }
    
    private static SparseCGANumericMultivector createIPNSSphere(GeometricObject obj){
        SparseCGANumericMultivector inf = getConstantsNumeric().getBaseVectorInfinity();
        SparseCGANumericMultivector o = getConstantsNumeric().getBaseVectorOrigin();
        
        // following Spence2013 lua implementation
        // local blade = weight * ( no + center + 0.5 * ( ( center .. center ) - sign * radius * radius ) * ni )
        SparseCGANumericMultivector c = createE3(obj.location[0]);
        double v = (obj.location[0].squaredNorm() - obj.getSignedSquaredSize())*0.5;
        return o.add(c).add(inf.gpWithScalar(v)).gpWithScalar(obj.getSignedWeight());
    }
    
    private static SparseCGANumericMultivector createIPNSCircle(GeometricObject obj){
        // Formula corresponding to cgaLua pdf documentation and [Rettig2023]
        // ε₀∧nn+(x⋅nn)E₀+x∧nn+((x⋅nn)x-0.5(x²-r²)nn)∧εᵢ
        
        // CGA lua code
        // local blade = weight * ( no ^ normal + ( center .. normal ) * no_ni + center ^ normal +
        // ( ( center .. normal ) * center - 0.5 * ( ( center .. center ) - sign * radius * radius ) * normal ) ^ ni )
        SparseCGANumericMultivector x = createE3(obj.location[0]);
        SparseCGANumericMultivector n = createE3(obj.attitude);
        double v = (obj.location[0].squaredNorm() - obj.getSignedSquaredSize())*0.5;
        
        SparseCGANumericMultivector o = getConstantsNumeric().getBaseVectorOrigin();
        SparseCGANumericMultivector inf = getConstantsNumeric().getBaseVectorInfinity();
        SparseCGANumericMultivector a = o.op(n).add(x.ip(n).gp(o.op(inf))).add(x.op(n));
        SparseCGANumericMultivector b = x.ip(n).gp(x);
        //SparseCGANumericMultivector c = x.square().sub(sr2).gp(n).gpWithScalar(0.5);
        SparseCGANumericMultivector c = n.gpWithScalar(v);
        return a.add((b.sub(c)).op(inf)).gpWithScalar(obj.getSignedWeight());
    }
    
    //TODO weight wird nicht berücksichtigt!
    private static SparseCGANumericMultivector createOPNSOrientedPoint(GeometricObject obj){
        // @CGA("nn∧x+(0.5x²nn-x (x⋅nn))εᵢ+nnε₀-(x⋅nn)E₀") FIXME x und nn vertauscht am Ende? siehe paper
        //FIXME bei decompose scheint das Vorzeichen der Orientierung falsch zu sein
        // oder ist die decompose()-Methode falsch?
        SparseCGANumericMultivector o = getConstantsNumeric().getBaseVectorOrigin();
        SparseCGANumericMultivector inf = getConstantsNumeric().getBaseVectorInfinity();
        SparseCGANumericMultivector n = createE3(obj.attitude); 
        SparseCGANumericMultivector x = createE3(obj.location[0]);
        
        return n.op(x).add(n.gpWithScalar(0.5d*obj.location[0].squaredNorm())
                .sub(x.gp(x.ip(n))).gp(inf)).
                add(n.gp(o)).sub(x.ip(n).gp(o.op(inf)));
    }
       
    private static SparseCGANumericMultivector createIPNSOrientedPoint(GeometricObject obj){
        SparseCGANumericMultivector o = getConstantsNumeric().getBaseVectorOrigin();
        SparseCGANumericMultivector inf = getConstantsNumeric().getBaseVectorInfinity();
        SparseCGANumericMultivector n = createE3(obj.attitude); 
        SparseCGANumericMultivector x = createE3(obj.location[0]);
        
        
        return n.op(x).add(n.gpWithScalar(0.5d*obj.location[0].squaredNorm())
                .sub(x.gp(x.ip(n))).gp(inf)).
                add(n.gp(o)).sub(x.ip(n).gp(o.op(inf)));
    }
    
    
    // flat objects creation
    
    // tested
    private static SparseCGANumericMultivector createIPNSPlane(GeometricObject obj){
        
        /* this((createEx(n.x)
            .add(createEy(n.y))
            .add(createEz(n.z))
            .add(createInf(P.x*n.x+P.y*n.y+P.z*n.z))).gp(weight));
        */
        SparseCGANumericMultivector attitude = createE3(obj.attitude);
        SparseCGANumericMultivector inf = getConstantsNumeric().getBaseVectorInfinity();
        return attitude
            .add(inf.gpWithScalar(obj.location[0].ip(obj.attitude))).gpWithScalar(obj.getSignedWeight());
    }
    private static SparseCGANumericMultivector createIPNSLine(GeometricObject obj){
        // local blade = weight * ( normal + ( center ^ normal ) * ni ) * i
        SparseCGANumericMultivector inf = getConstantsNumeric().getBaseVectorInfinity();
        SparseCGANumericMultivector I3 = getConstantsNumeric().getEuclideanPseudoscalar();
        SparseCGANumericMultivector nq = createE3(obj.attitude);
        SparseCGANumericMultivector q = createE3(obj.location[0]);
        return nq.add(q.op(nq).gp(inf)).
                gp(I3).gpWithScalar(obj.getSignedWeight());
    }
    private static SparseCGANumericMultivector createOPNSDipole(GeometricObject obj){
        SparseCGANumericMultivector d1 = createIPNSRoundPoint(obj.location[0], 
            Math.sqrt(obj.getSquaredWeight()));
        SparseCGANumericMultivector d2 = createIPNSRoundPoint(obj.location[0],
            Math.sqrt(obj.getSquaredWeight()));
        SparseCGANumericMultivector result = d1.op(d2);
        if (obj.isWeightNegative()) result = result.negate();
        return result;
    }
    
    /**
     * Based on:
     * - attitude, location1,2-->center
     * - signedSquaredSize (könnte ich auch aus location,1,2 bestimmen)
     * - signedWeight wird berücksichtigt
     * 
     * @param obj
     * @return 
     */
    private static SparseCGANumericMultivector createIPNSDipole2(GeometricObject obj){
        
        Tuple center = obj.location[0].add(obj.location[1]).muls(0.5);
        SparseCGANumericMultivector c = createE3(center);
        //Tuple normalizedNormal = obj.attitude.normalize();
        SparseCGANumericMultivector weight = createScalar(obj.getSignedWeight());
                
        SparseCGANumericMultivector n = createE3(obj.attitude);
        System.out.println("create dipole: att="+obj.attitude.toString());
        
        // testweise
        //System.out.println("create dipole: att from locations="+obj.location[1].sub(obj.location[0]).normalize().toString());
        
        SparseCGANumericMultivector sr2 = createScalar(obj.getSignedSquaredSize());
        //System.out.println("create Dipole: sr2="+String.valueOf(obj.getSignedSquaredSize())); // 0.4330127018922193 korrekt
        
        // testweise
        //System.out.println("create Dipole: vec from locations="+obj.location[1].sub(obj.location[0]).toString());
        //System.out.println("create Dipole: sr2 from locations="+String.valueOf(obj.location[1].sub(obj.location[0]).muls(0.5).squaredNorm()));
        
        SparseCGANumericMultivector o = getConstantsNumeric().getBaseVectorOrigin();
        SparseCGANumericMultivector inf = getConstantsNumeric().getBaseVectorInfinity();
        SparseCGANumericMultivector I3 = getConstantsNumeric().getEuclideanPseudoscalar();
        SparseCGANumericMultivector oinf = o.op(inf);
        // code scheint nicht mit der Formel im pdf übereinzustimmen
        // (das erste "-" ist im pdf ein "+"
        // local blade = weight * ( no ^ normal + center ^ normal ^ no_ni - ( center .. normal ) -
        //( ( center .. normal ) * center - 0.5 * ( ( center .. center ) + sign * radius * radius ) * normal ) ^ ni ) * i
        // FIXME component e123 scheint falches Vorzeichen zu haben --> das "+" im pdf ist also richtig
        // CGAMultivector a =  o.op(n).add(c.op(n).op(I0)).sub(c.ip(n));
        SparseCGANumericMultivector a =  o.op(n).add(c.op(n).op(oinf)).add(c.ip(n));
        SparseCGANumericMultivector b = c.ip(n).gp(c);
        SparseCGANumericMultivector d = c.square().add(sr2).gpWithScalar(0.5).gp(n);
        SparseCGANumericMultivector result = a.sub(b.sub(d).op(inf)).gp(I3).gp(weight);
        return result;
    }
    
    private static SparseCGANumericMultivector createIPNSDipole(GeometricObject obj){
        // WORKAROUND
        return createOPNSDipole(obj).dual();
    }
    
    private static SparseCGANumericMultivector createIPNSFlatPoint(GeometricObject obj){
        SparseCGANumericMultivector inf = getConstantsNumeric().getBaseVectorInfinity();
        SparseCGANumericMultivector I3 = getConstantsNumeric().getEuclideanPseudoscalar();
        SparseCGANumericMultivector one = getConstantsNumeric().one();
        
        // local blade = weight * ( 1 - center ^ ni ) * i
        //return (new CGAScalarOPNS(1d)).sub(createE3(c).op(inf)).gp(I3).gp(weight);
        // umgedreht damit Vorzeichen zur opns representation passt
        // return (createE3(c).op(inf).sub(new CGAScalarOPNS(1d))).gp(I3).gp(weight);
        return createE3(obj.location[0]).op(inf).sub(one).gp(I3).gpWithScalar(obj.getSignedWeight());
    }

    
    // helper methods to implement composition methods
    
    private static SparseCGANumericMultivector createScalar(double value){
        return create(new double[]{value}, new int[]{0});
    }
    
    private static SparseCGANumericMultivector createE3(Tuple c){
        return create(new double[]{c.values[0], c.values[1], c.values[2]}, new int[]{1,2,3});
        /* warum funktioniert das nicht
        return getConstantsNumeric().getBaseVectorX().gpWithScalar(c.values[0]).
            add(getConstantsNumeric().getBaseVectorY().gpWithScalar(c.values[1])).
            add(getConstantsNumeric().getBaseVectorZ().gpWithScalar(c.values[2]));
        */
    }
    
    private static CGAConstantsNumeric getConstantsNumeric(){
        return CGAExprGraphFactory.instance.constantsNumeric();
    }
    
    /*private static double getSign(Sign sign){
        switch (sign){
            case Sign.NEGATIVE:
                return -1d;
            case Sign.POSITIVE:
                return 1d;
            default:
                return Double.NaN;
        }
    }*/
    
    
    // TODO in welches Interface muss das? welches Interface soll returned werden
    public GeometricObject decompose(boolean isIPNS){
        
        SparseCGANumericMultivector probePoint; // = constants().getBaseVectorOrigin();
        probePoint = createIPNSRoundPoint(new Tuple(new double[]{0,0,0}), 1d);
        switch (grade()){
            case 0:
                // scalar
                System.out.println("Scalar (grade 0) found: "+toString()+
                                   ". No geometric object for visualization available!");
                return null;
            case 1:
                // ipns plane? (flat)
                //TODO ich brauche hier isFlat() 
                System.out.println("Test grade 1:");
                if (isIPNSFlat()){
                    return decomposePlane(true, probePoint);
                } 
                //if (CGARoundIPNS.is(attitude)){
                if (isIPNSRound()){
                    // hier lande ich flälschlicherweise mit ipns-plane
                    // if radius < eps this results in a round-point
                    return decomposeSphereOrRoundPoint(isIPNS);
                    // ipns round point (round)
                    //if (Math.abs(sphere.squaredSizeOfRound()) < eps){
                    //if (Math.abs(go.squaredSizeOfRound) < eps){
                        //return new CGARoundPointIPNS(attitude);
                    // ipns sphere (round)
                    //} else {
                    //    return sphere;
                    //}
                }
                
                // Attitude not yet supported
                //TODO
                
                // opns attitude scalar (attitude?)
                if (isIPNS && isAttitude()) { //CGAAttitudeIPNS.is(attitude)){
                   // return new CGAAttitudeIPNS(attitude);
                   System.out.println("Grade 1 object found: "+toString()+
                       ". IPNS attitude not yet supported!");
                   return null;
                // ipns attitude trivector? (attitude)
                } else if (isAttitude()){//if (CGAAttitudeOPNS.is(attitude)){
                   // return new CGAAttitudeTrivectorOPNS(attitude);
                   System.out.println("Grade 1 object found: "+toString()+
                       ". OPNS attitude-trivector not yet supported!");
                   return null;
                }
                System.out.println("Illegal grade 1 object found: "+toString());
                break;
                
            case 2:
                if (isIPNS){
                    System.out.println("Test grade 2:");
                    if (isIPNSFlat()){
                        return decomposeScrewAxisOrLine(isIPNS, probePoint);
                    }
                    if (isIPNSRound()){
                        return decomposeCircleOrOrientedPoint(isIPNS);
                    }
                    // ipns attitude bivector (attitude)
                    if (isIPNS && isAttitude()) { //if (CGAAttitudeIPNS.is(attitude)){
                        System.out.println("Grade 2 found: "+toString()+
                            ". IPNS attitude not yet supported!");
                        //return new CGAAttitudeBivectorIPNS(attitude);
                        return null;
                    // ipns tangent bivector? (tangent)
                    //} else if (CGATangentIPNS.is(attitude)){
                    } else if (isTangent(true)){
                        //return new CGATangentBivectorIPNS(attitude);
                        System.out.println("Grade 2 found: "+toString()+
                            ". IPNS tangent bivector not supported!");
                        return null;
                    }
                    System.out.println("Illegal ipns object of grade 2 found: "+toString());
                    break;
                    
                // opns
                } else {
                    // opns flat point (flat)
                    //if (CGAFlatOPNS.is(attitude)){
                    if (isOPNSFlat()){
                        //return new CGAFlatPointOPNS(attitude);
                        return decomposeFlatPoint(false);
                    // opns point pair (round)
                    } else if (isOPNSRound()){//if (CGARoundOPNS.is(attitude)){
                        //return new CGAPointPairOPNS(attitude);
                        return decomposeDipole(false);
                    }
                    System.out.println("Illegal opns object of grade 2 found: "+toString());
                }
                break;
                
            case 3:
                // ipns
                if (isIPNS) {
                    // ipns flat point (flat)
                    //if (CGAFlatIPNS.is(attitude)){
                    System.out.println("Test grade 3:");
                    if (isIPNSFlat()){
                        return decomposeFlatPoint(true);
                    // ipns point pair (round)
                    } else { //if (isIPNSRound()){ //if (CGARoundIPNS.is(attitude)){
                        return decomposeDipole(true);
                    }
                    //System.out.println("Illegal ipns object of grade 3 found: "+toString());
                }
                // opns
                else {
                    //if (CGAFlatOPNS.is(attitude)){
                    if (isOPNSFlat()){
                        // opns line (flat)
                        // opns screw axis? (flat)
                        //TODO CGAScrewAxisOPNS muss noch implementiert werden
                        //CGAScrewAxisOPNS screwAxis = new CGAScrewAxisOPNS(attitude);
                        //if screwAxis.getPitch(9 < eps){}
                        //return new CGALineOPNS(attitude);
                        return decomposeScrewAxisOrLine(false, probePoint);
                    }
                    if (isOPNSRound()){//if (CGARoundOPNS.is(attitude)){
                        return decomposeCircleOrOrientedPoint(false);
                        //CGACircleOPNS circle = new CGACircleOPNS(attitude);
                        // opns oriented point (round)
                        
                    }
                    
                    // opns attitude bivector (attitude)
                    if (isAttitude()){//if (CGAAttitudeOPNS.is(attitude)){
                        //return new CGAAttitudeBivectorOPNS(attitude);
                        System.out.println("Grade 3 found: "+toString()+
                            ". OPNS attitude not yet supported!");
                        return null;
                    // opns tangent bivector (identisch mit ipns flat point) (tangent)
                    //} else if (CGATangentOPNS.is(attitude)){
                    } else if (isTangent(false)){ 
                        //return new CGATangentBivectorOPNS(attitude);
                        System.out.println("Grade 3 found: "+toString()+
                            ". OPNS tangent bivector not yet supported!");
                        return null;
                    }
                    System.out.println("Illegal opns object of grade 3 found: "+toString());
                }
                break;
                
            case 4:
                if (isIPNS){
                    // ipns attitude scalar (attitude)
                    if (isAttitude()) { //if (CGAAttitudeIPNS.is(attitude)){
                        //return new CGAAttitudeIPNS(attitude);
                        System.out.println("Grade 4 found: "+toString()+
                            ". IPNS attitude not yet supported!");
                        //GeometricObject.GeometricType.ATTITUDE
                        return null;
                    }
                    System.out.println("Illegal ipns object of grade 4 found: "+toString());
                    
                // opns
                } else {
                    // opns plane (flat)
                    //if (CGAFlatOPNS.is(attitude)){
                    if (isOPNSFlat()){
                        //return new CGAPlaneOPNS(attitude);
                        return decomposePlane(false, probePoint);
                    // opns round point (round)
                    } else if (isOPNSRound()){// if (CGARoundOPNS.is(attitude)){
                        //return new CGARoundPointOPNS(attitude);
                        return decomposeSphereOrRoundPoint(false);
                    // opns attitude trivector? (attitude)
                    } else if (isAttitude()){//if (CGAAttitudeOPNS.is(attitude)){
                        //return new CGAAttitudeTrivectorOPNS(attitude);
                        System.out.println("Grade 4 found: "+toString()+
                            ". OPNS attitude not yet supported!");
                        return null;
                    }
                    System.out.println("Illegal opns object of grade 4 found: "+toString());
                }
                break;
                
            case 5:
                // ipns scalar
                //return new CGAScalarIPNS(attitude);
                return null;
            default:
                System.out.println("Illegal object of unknown grade found: "+toString());
        }
        return null;
    }
    
    
    // helper methods to decompose numeric multivectors
    
    // decompose flat objects
    
    private GeometricObject decomposeFlatPoint(boolean isIPNS){
        SparseCGANumericMultivector attitude = decomposeAttitudeFlatAsEInf(isIPNS);
        System.out.println("attitude flat point="+attitude.toString());
        Tuple attitude2 = attitudeFlatPoint(isIPNS, this);
        System.out.println("attitude flat point2="+attitude2.toString());
        // attitude/2 ist 0,0,0 statt 0,0,1
        
        return new GeometricObject(GeometricObject.GeometricType.FLAT_POINT, 
                    isIPNS, extractE3(attitude), 
                        locationFlatPoint(this, isIPNS), Double.NaN,
                         squaredWeight(attitude), Sign.UNKNOWN, grade());
    }
    /**
     * Determines the center of a flat point (opns).
     * 
     * @return location as euclidean point
     */
    private static Tuple locationFlatPoint(SparseCGANumericMultivector m1, boolean isIPNS){
        SparseCGANumericMultivector m = m1;
        if (isIPNS) m = m1.undual();
        SparseCGANumericMultivector inf = m.constants().getBaseVectorInfinity();
        SparseCGANumericMultivector o = m.constants().getBaseVectorOrigin();
        SparseCGANumericMultivector oinf = o.op(inf);
        
        // Dorst2007 drills 14.9.2. nr. 5
        SparseCGANumericMultivector result = oinf.lc(o.op(m)).div(oinf.lc(m)).negate();
        return extractE3(result);
    }
    /**
     * Determine the attitude (inclusive weight == not normalized attitude) of a flat point (opns).
     * 
     * tested for line-opns by comparison with [Dorst2007]: drills (chapter 13.9.1)
     * 
     * TODO der korresondondierende Code in ConformalGeometricAlgebra scheint mir nicht zu stimmen...
     * 
     * @return attitude 
     */
    private static Tuple attitudeFlatPoint(boolean isIPNS, SparseCGANumericMultivector m){
        SparseCGANumericMultivector m1 = m;
        if (isIPNS) m1 = m.dual();
        // corresponding to
        // Geometric Algebra: A powerful tool for solving geometric problems in visual computing
        // Leandro A. F. Fernandes, and Manuel M. Oliveira, 2009
        // DOI: 10.1109/SIBGRAPI-Tutorials.2009.10
        // also corresponding to [Dorst2009] p.407
        // tested for line
        //CGAMultivector result =  inf.lc(this).negate().compress();
        SparseCGANumericMultivector inf = m1.constants().getBaseVectorInfinity();
        SparseCGANumericMultivector result =  inf.negate().lc(m1);
        //return new CGAAttitudeOPNS(result);
        return extractE3(result);
    }
    
    private GeometricObject decomposeScrewAxisOrLine(boolean isIPNS, SparseCGANumericMultivector probePoint){
        SparseCGANumericMultivector attitude = decomposeAttitudeFlatAsEInf(isIPNS);
        System.out.println("attFrom line: "+attitude.toString());
        double pitch = Double.NaN;
        boolean isReal = true;
        //FIXME squaredWeight ist immer noch falsch = 18 statt 1
        double squaredWeight = squaredWeight(attitude); // decomposeSquaredWeightFlat(isIPNS)
        
        //TODO wie bekomme ich pitch in den Konstruktor? bzw. wie treffe ich die Entscheidung ob screw oder line?
        return new GeometricObject(GeometricType.LINE, isIPNS, extractE3FromVectorInf(attitude), 
            decomposeLocationFlat(isIPNS, probePoint), 
            isReal, squaredWeight, Sign.UNKNOWN, grade());
    }
    
    //TODO unklar ob squaredSize=0 oder inf sein sollte
    // squaredWeight hat falsches Vorzeichen
    private GeometricObject decomposePlane(boolean isIPNS, SparseCGANumericMultivector probePoint){
        SparseCGANumericMultivector attitude = decomposeAttitudeFlatAsEInf(isIPNS); // bivector^einf, grade-3 
        return new GeometricObject(GeometricObject.GeometricType.PLANE, isIPNS,
                        extractE3FromBivectorInf(attitude), 
                        decomposeLocationFlat(isIPNS, probePoint),
                        true, squaredWeight(attitude), Sign.UNKNOWN, grade());
    }
    
    // - bei line/plane-ipns funktioniert es
    private Tuple decomposeLocationFlat(boolean isIPNS, SparseCGANumericMultivector probePoint){
        SparseCGANumericMultivector m = this;
        if (!isIPNS) m = dual();
       
        SparseCGANumericMultivector result = probePoint.op(m).div(m); // round point ipns
        
        // extract E3 from normalized dual sphere
        // [Dorst2007] p.409
        SparseCGANumericMultivector o = constants().getBaseVectorOrigin();
        SparseCGANumericMultivector inf = constants().getBaseVectorInfinity();
        SparseCGANumericMultivector oinf = o.op(inf);
        
        result = oinf.lc(oinf.op(result)); // euclidean vector
        return extractE3(result);
    }
    

    // decompose round objects
    
    private GeometricObject decomposeSphereOrRoundPoint(boolean isIPNS){
         return new GeometricObject(GeometricObject.GeometricType.SPHERE, isIPNS, null, 
                        locationFromTangentAndRound(this),
                        squaredSizeOfRound(isIPNS), 
                        squaredWeight(attitudeFromTangentAndRoundIPNS(),
                                                 constants().getBaseVectorOrigin()),
                        Sign.UNKNOWN, grade());
    }
    /**
     * Determination of the squared size of a round (dipole, circle, sphere). 
     * 
     * - für sphere-ipns scheint es zu stimmen
     * - Fehler bei circle-ipns -5 statt 4
     * 
     * precondition:
     * - location at the origin?
     * 
     * @param m cga round (opns) or dual round (ipns) object represented by a multivector
     * @return squared size/radius (maybe negative) for round -squared size/radius for dual round (ipns)
     */
    private double squaredSizeOfRound(boolean isIPNS){
        // following Dorst2008 p.407/08 (+errata: ohne Vorzeichen), corresponds to drills 14.9.2
        // gradeInversion() ist elegant, da ich damit die Formel dipole, circle und sphere
        // verwenden kann
        SparseCGANumericMultivector inf = constants().getBaseVectorInfinity();
        SparseCGANumericMultivector result = gp(gradeInversion()).div((inf.lc(this)).square());  
        
        // https://github.com/pygae/clifford/blob/master/clifford/cga.py
        // hier findet sich eine leicht andere Formel, mit der direkt die size/radius
        // also nicht squaredSizeIntern1 bestimmt werden kann
        //dual_sphere = self.dual
        //dual_sphere /= (-dual_sphere | self.cga.einf)
        //return math.sqrt(abs(dual_sphere * dual_sphere))
        //result = attitude.dual().div(attitude.dual().negate().ip(CGAMultivector.createInf(1d)));
        //result = result.gp(result);
        
        if (isIPNS) result = result.negate();
        return extractScalar(result);
    }
    
    /**
     * Determine the squared weight (without sign) of any CGA object.
     * 
     * @param attitude direction specific for the object form the multivector is representing
     * @param probePoint If not specified use e0.
     * @return squared weight >0
     */
    private static double squaredWeight(SparseCGANumericMultivector attitude, SparseCGANumericMultivector probePoint){
        //FIXME abs() scheint mir unnötig zu sein
        return extractScalar(probePoint.lc(attitude).square());
        // liefert gleiches Ergebnis
        // CGAMultivector A = probePoint.ip(attitude);
        //return A.reverse().gp(A).decomposeScalar();
    }
    /**
     * Determine direction/attitude from tangent or round objects in OPNS 
     * representation.
     * 
     * following [Dorst2009] p.407<p>
     * 
     * @return attitude (as a tri-vector for a sphere)
     */
    private SparseCGANumericMultivector attitudeFromTangentAndRoundIPNS(){
        SparseCGANumericMultivector inf = constants().getBaseVectorInfinity();
        return inf.negate().lc(this.undual()).op(inf);
    }
    /*private SparseCGANumericMultivector attitudeFromFlat(boolean isIPNS){
        SparseCGANumericMultivector attitude = this;
        if (isIPNS) attitude = undual();
        SparseCGANumericMultivector inf = constants().getBaseVectorInfinity();
        return inf.negate().lc(attitude);
    }*/
    
    /**
     * code aus CGAKVector für IPNSSphere und IPNSRoundPoint, weitere muss ich noch überprüfen
     * TODO und auch OPNS muss ich noch überprüfen
     * 
     * Determines location from tangent (direct/dual) and round (direct/dual) 
     * objects.
     * 
     * ok für RoundPointIPNS, FlatPointIPNS
     * 
     * scheint für CGAOrientedFiniteRoundOPNS um einen faktor 2 falsch zu sein in allen Koordinaten
     * scheint für CGARound zu stimmen
     * scheint mit CGATangent nicht zu stimmen??? mittlerweile korrigiert?
     * scheint mit CGAOrientedPointPair zu stimmen
     * vielleicht muss das object vorher normalisiert werden
     * TODO
     * 
     * @return location 
     */
    private static Tuple locationFromTangentAndRound(SparseCGANumericMultivector mm){
        // corresponds to the errata of the book [Dorst2007]
        // and also Fernandes2009 supplementary material B
        // location as finite point/dual sphere corresponding to [Dorst2007]
        // createInf(-1d).ip(this) ist die Wichtung, es wird also durch die Wichtung geteilt,
        // d.h. der Punkt wird normiert?
        
        // FIXME unklar, ob Normierung notwendig ist
        //CGAMultivector mn = this; //this.normalize();
        
        // determine location as dual-sphere [Dorst2007 p. 407]
        SparseCGANumericMultivector inf = mm.constants().getBaseVectorInfinity();
        SparseCGANumericMultivector o = mm.constants().getBaseVectorOrigin();
        SparseCGANumericMultivector location = mm.negate().div(inf.lc(mm));
            
        // center of this round, as a null vector
        // https://github.com/pygae/clifford/blob/master/clifford/cga.py Zeile 284:
        // self.mv * self.cga.einf * self.mv // * bedeutet geometrisches Produkt
        //TODO ausprobieren?
        SparseCGANumericMultivector oinf = o.op(inf);
        return extractE3(oinf.ip(oinf.op(location)));
    }
    
    private GeometricObject decomposeCircleOrOrientedPoint(boolean isIPNS){
        // location von circle stimmt nicht, z ist verschieden
        // genauso oriented point, dieser wird aber fälschlicherweise als circle erkannt,
        // d.h squaredSize ist falsch
        // einmal -9 und einmal -5 für squared sizes obwohl beides positiv sein sollte
        Tuple location = locationFromTangentAndRound(this);
        double squaredSize = squaredSizeOfRound(isIPNS);
        System.out.println("decomposeCircleOrOrientedPoint: squaredSize="+String.valueOf(squaredSize));
        SparseCGANumericMultivector inf = constants().getBaseVectorInfinity();
            
        //WORKAROUND anhand squaredSize herausfinden ob circle or oriented point und entsprechend die
        Tuple attitude;
        // attitude bestimmen
        // if oriented point
        if (Precision.equals(squaredSize, 0, GeometricObject.eps)){
            // für oriented Point
            // CGAAttitudeOPNS result = new CGAAttitudeOPNS(inf.negate().lc(undual()).op(inf).compress());
            SparseCGANumericMultivector a = inf.negate().lc(undual()).op(inf);
            attitude = extractE3FromBivectorInf(a);
            // scheint immer noch falsches Vorzeichen zu haben, also gleiches Ergebnis wie die unten
            // definiert attitue() Methode
            // vielleicht lade ich hier gar nicht da circle imaginär erkannt wird, also size fälschlicherweise
            // negativ ist.
        // circle
        } else {
            // Implementation following:
            // https://spencerparkin.github.io/GALua/CGAUtilMath.pdf
            // CGAUtil.lua l.366
            // blade = blade / weight2
            // local normal = -no_ni .. ( blade ^ ni )
            SparseCGANumericMultivector o = constants().getBaseVectorOrigin();
            SparseCGANumericMultivector result = o.op(inf).negate().
                ip(this.gpWithScalar(1d/weight()/*2*/).op(inf));
            attitude = extractE3(result);
        }
        double squaredWeight = weight()*weight();
        
        return new GeometricObject(GeometricObject.GeometricType.CIRCLE, isIPNS, attitude, 
                        location, squaredSize, squaredWeight, Sign.UNKNOWN, grade());
    }
    /**
     * Determine the attitude for IPNS circle.
     * 
     * ok für circle-ipns, falsches Vorzeichen für orientedPoint
     * @return normalized attitude
     */
    public Tuple attitude(){
        // Implementation following:
        // https://spencerparkin.github.io/GALua/CGAUtilMath.pdf
        // CGAUtil.lua l.366
        // blade = blade / weight2
        // local normal = -no_ni .. ( blade ^ ni )
        SparseCGANumericMultivector inf = constants().getBaseVectorInfinity();
        SparseCGANumericMultivector o = constants().getBaseVectorOrigin();
        SparseCGANumericMultivector result = o.op(inf).negate().
            ip(this.gpWithScalar(1d/weight()/*2*/).op(inf));
        return extractE3(result);
        
        // für oriented Point
        // CGAAttitudeBivectorOPNS result = new CGAAttitudeBivectorOPNS(attitudeFromTangentAndRoundIPNS());
        //return result.direction();
        // CGAAttitudeOPNS result = new CGAAttitudeOPNS(inf.negate().lc(undual()).op(inf).compress());
       
    }
    /**
     * Determination the absolute of the weight without usage of a probepoint 
     * and without determination of the attitude.
     * 
     * aus circleIPNS
     * 
     * test ok
     * 
     * @return absolute value of the weight
     */
    public double weight(){
        // Implementation following:
        // https://spencerparkin.github.io/GALua/CGAUtilMath.pdf
        // local weight2Intern = ( #( no_ni .. ( blade ^ ni ) ) ):tonumber()
        // # bedeutet magnitude
        //FIXME warum Math.abs()? Warum bekomme ich hier das Vorzeichen nicht?
        //CGAMultivector result =  o.op(inf.ip(this.op(inf)));
        SparseCGANumericMultivector inf = constants().getBaseVectorInfinity();
        SparseCGANumericMultivector o = constants().getBaseVectorOrigin();
        SparseCGANumericMultivector result = o.op(inf).ip(this.op(inf)).norm();
        return extractScalar(result);
    }
    
    // funktioniert das auch mit imaginären Punktepaaren? vermutlich nein!
    // TODO
    // 
    // corresponding to [Dorst2009] p. 427
    private GeometricObject decomposeDipole(boolean isIPNS){
        SparseCGANumericMultivector attitude = attitudeFromTangentAndRoundIPNS(); // ok
        double squaredWeight = squaredWeight(attitude, constants().getBaseVectorOrigin()); // ok
        
        //WORKAROUND strict-norm (abs) verwenden statt einfach quadrieren, denn
        // das Quadrat kann negativ werden und dann läßt sich die Wurzel nicht ziehen
        //FIXME 
        // unklar, ob dadurch nicht die Reihenfolge der beiden Punkte beeinflusst wird.
        SparseCGANumericMultivector m = this;
        if (isIPNS) m = this.undual();
        
        SparseCGANumericMultivector sqrt = m.norm();
        SparseCGANumericMultivector inf = constants().getBaseVectorInfinity();
        
        // following Fernandes (Formelsammlung, attachement)
        // CGARoundPointIPNS p2 = new CGARoundPointIPNS(sub(sqrt).div(inf.negate().lc(this)).compress());
        Tuple p2 = extractE3(m.sub(sqrt).div(inf.negate().lc(m))); // ipns point
        System.out.println("p2="+p2.toString());
        // CGARoundPointIPNS p1 = new CGARoundPointIPNS(add(sqrt).div(inf.negate().lc(this)).compress());
        Tuple p1 = extractE3(m.add(sqrt).div(inf.negate().lc(m))); // ipns point
        System.out.println("p1="+p1.toString());
        
        double sq = squaredSizeOfRound(isIPNS);
        double sqDist = p1.sub(p2).muls(0.5).squaredNorm();
        System.out.println("test decompose dipole: "+
            String.valueOf(sq)+"="+String.valueOf(sqDist));
        // 19.183012701892203=19.18301270189221
        
        SparseCGANumericMultivector center = locationFromTangendAndRound();
        System.out.println("test decompose dipole: center="+center.toString());
        
        //TODO
        // herausfinden ob real oder imaginärer dipol und info in den Konstruktor übergeben
        return new GeometricObject(Space.IPNS, REAL, p1,
                                     p2, squaredWeight, Sign.UNKNOWN, grade());
    }
    
    
    // helper methods to get locations, attitudes and sizes and weights
   
    /**
     * Decompose attitude from flat as vector/bivector^inf.
     * 
     * @param isIPNS
     * @return bivector^einf or E^inf
     */
    private SparseCGANumericMultivector decomposeAttitudeFlatAsEInf(boolean isIPNS){
        SparseCGANumericMultivector inf = constants().getBaseVectorInfinity();
        SparseCGANumericMultivector m = this;
        if (isIPNS) m = this.undual();
        return inf.negate().lc(m);
    }
    
    /**
     * Determines location from tangend and round objects and also from its dual.
     * 
     * @return location in the euclidian part directly.
     */
    private SparseCGANumericMultivector locationFromTangendAndRound(){
        SparseCGANumericMultivector inf = constants().getBaseVectorInfinity();
        // corresponds to the errata of the book Dorst2007
        SparseCGANumericMultivector result = (this.gp(inf).gp(this)).div((inf.ip(this)).square()).gpWithScalar(-0.5d);
        System.out.println("locationFromTangentAndRound="+result.toString());
        return result;
    }
    
    /**
     * Determines the squared weight (without sign) based on the attitude of any CGA object.
     * 
     * bei ipns plane negativ
     * 
     * @param attitude E^inf hängt vom grade ab
     * @return squared weight > 0
     */
    private static double squaredWeight(SparseCGANumericMultivector attitude){
        return squaredWeight(attitude, attitude.constants().getBaseVectorOrigin());
    } 
    
    /* Extract attitude/direction from Bivector^einf multivector representation.
     * 
     * example: -1.9999999999999991*e1^e2^ei + 1.9999999999999991*e1^e3^ei + 1.9999999999999991*e2^e3^ei
     */
    private static Tuple extractE3FromBivectorInf(SparseCGANumericMultivector m){
        SparseCGANumericMultivector o = m.constants().getBaseVectorOrigin();
        SparseCGANumericMultivector I3i = m.constants().getBaseVectorX().op(m.constants().getBaseVectorY()).
                                             op(m.constants().getBaseVectorZ()).negate();
        SparseCGANumericMultivector result = m.gradeSelection(3).rc(o).negate().lc(I3i);
        return extractE3(result);
    }
    private static Tuple extractE3FromVectorInf(SparseCGANumericMultivector m){
        SparseCGANumericMultivector o = m.constants().getBaseVectorOrigin();
        SparseCGANumericMultivector result = m.gradeSelection(2).rc(o).negate();
        return extractE3(result);
    }
    
    private static Tuple extractE3(SparseCGANumericMultivector m){
        int[] ind = CGACayleyTable.getEuclidIndizes();
        StdVectorDouble elements = m.getDM().get_elements();
        return new Tuple(new double[]{elements.get(ind[0]), 
                           elements.get(ind[1]),
                           elements.get(ind[2])});
    }
    private static double extractScalar(SparseCGANumericMultivector m){
        return m.getDM().get_elements().get(0);
    }
    
    
    // helper methods to analyse multivectors
    
    /**
     * Tests, if the given multivector is an ipns flat.
     * 
     * @param m
     * @return 
     */
    public boolean isIPNSFlat(){
        //if (inf.op(attitude).isNull()) return false;
        //return inf.lc(attitude).isNull();
        SparseCGANumericMultivector inf = constants().getBaseVectorInfinity();
        System.out.println("isIPNSFlat: "+inf.op(this).toString());
        // bei test dipole, also grade 3 finden sich drei nicht structurelle 0 Elemente von grade 4 ganz am Ende des mv
        // [00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 0, 0, 0, 00]
        if (inf.op(this).isNull(GeometricObject.eps)) return false;
        return inf.lc(this).isNull(GeometricObject.eps);
    }
    //TODO
    // Umstellen auf isFlat(boolean isIPNS)
    private boolean isOPNSFlat(){
        SparseCGANumericMultivector inf = constants().getBaseVectorInfinity();
        if (!inf.op(this).isNull(GeometricObject.eps)) return false;
        return !inf.lc(this).isNull(GeometricObject.eps);
    }
    //TODO
    // ich brauche eine Methode isFlat() unabhängig davon ob ipns oder opns
    private boolean isFlat(boolean isIPNS){
        if (isIPNS) return isIPNSFlat();
        return isOPNSFlat();
    }
    
    // TODO ipns und opns-round können zu isRound(boolean isIPNS) zusammengefasst werden
    private boolean isIPNSRound(){
        SparseCGANumericMultivector inf = constants().getBaseVectorInfinity();
        System.out.println("isIPNSFlat: "+inf.op(this).toString());
        if (inf.op(this).isNull(GeometricObject.eps)) return false;
        return !inf.lc(this).isNull(GeometricObject.eps);
    }
    private boolean isOPNSRound(){
        SparseCGANumericMultivector inf = constants().getBaseVectorInfinity();
        if (inf.op(this).isNull(GeometricObject.eps)) return false;
        if (inf.lc(this).isNull(GeometricObject.eps)) return false;
        return !this.square().isNull(GeometricObject.eps);
    }
    private boolean isRound(boolean isIPNS){
        if (isIPNS) return isIPNSRound();
        return isOPNSRound();
    }
    // scheint für ipns und opns gleich zu sein
    private boolean isAttitude(){
        SparseCGANumericMultivector inf = constants().getBaseVectorInfinity();
        if (!inf.op(this).isNull(GeometricObject.eps)) return false;
        return inf.lc(this).isNull(GeometricObject.eps);
    }
    private boolean isTangent(boolean isIPNS){
         throw new RuntimeException("not yet implemented!");
    }
    
    // TODO
    // vervollständigen
    // brauche ich vermutlich nicht mehr, wenn ich als probePoint einfach nur eo verwende
    /*private SparseCGANumericMultivector createRoundPoint(double x, double y, double z){
        SparseCGANumericMultivector o = constants().getBaseVectorOrigin();
        return o.add(constants().getBaseVectorX().gpWithScalar(x))
                .add(constants().getBaseVectorY().gpWithScalar(y))
                .add(constants().getBaseVectorZ().gpWithScalar(z))
                .add(createInf(0.5*(x*x+y*y+z*z))));
    }*/
    
    // TODO kann in eine default impl eines interfaces verschoben werden von dem diese Klasse hier
    // erbt, da andere impl das auch brauchen können, gilt für alle impl
    private boolean isNull(){
        return getSparsity().isNull();
    }
    public boolean isNull(double precision){
        StdVectorDouble values = getDM().get_nonzeros();
        for (int i=0;i<values.size();i++){
            if (!Precision.equals(values.get(i), 0d, precision)) return false;
            // if (Math.abs(values.get(i)) > precision) return false;
        }
        return true;
    }
    
    @Override
    public SparseCGASymbolicMultivector toSymbolic() {
        return super.delegate;
    }

    @Override
    public iConstantsFactory<SparseCGANumericMultivector> constants() {
        return CGAExprGraphFactory.instance.constantsNumeric();
    }

    @Override
    public SX getSX() {
        return this.toSymbolic().getSX();
    }
}