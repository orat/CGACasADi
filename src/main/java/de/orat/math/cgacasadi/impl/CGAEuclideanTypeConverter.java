package de.orat.math.cgacasadi.impl;

import de.orat.math.gacalc.api.ExprGraphFactory;
import de.orat.math.gacalc.api.MultivectorNumeric;
import de.orat.math.gacalc.spi.iEuclideanTypeConverter;
import de.orat.math.gacalc.spi.iExprGraphFactory;
import de.orat.math.gacalc.spi.iMultivectorNumeric;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Tuple3d;
import org.jogamp.vecmath.Vector3d;
import util.cga.CGACayleyTable;
import util.cga.CGACayleyTableGeometricProduct;
import util.cga.CGAMultivectorSparsity;
import util.cga.SparseCGAColumnVector;

/**
 * MultivectorNumeric kann leicht aus SparseDoubleColumnVector mit der 
 * iExprGraphFactory oder direkt via iMultivectorNumeric erzeugt werden.
 * 
 * Diese Implementierung hängt vom zugrunde liegenden Koordinatensystem und Metrik 
 * ab. Damit ist das abhängig von einer konkreten Implementierung von CGA und sollte
 * daher mit in das implementierende repo verschoben werden. Es braucht dann vermutlich
 * ein SPI dafür und eine API um sich die Implementierung zu beschaffen.
 * 
 * Oder aber ich schaffe es diese Implementierung koordiantenunabhängig zu abstrahieren,
 * indem ich benötigte Koordinatensystem-abhängige Methoden in iExprGraphFactory implementiere
 * 
 * TODO
 * 
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CGAEuclideanTypeConverter implements iEuclideanTypeConverter {
    
    private final iExprGraphFactory fac;
    
    public CGAEuclideanTypeConverter(iExprGraphFactory fac){
        this.fac = fac;
    }
    
    private static final CGACayleyTable cgaCayleyTable = CGACayleyTableGeometricProduct.instance();
    
    // aus Arguments class de.dhbw.rahmlab.geomalgelang.api;
    
    private static CGAMultivectorSparsity euclideanVectorSparsity = 
            new CGAMultivectorSparsity(new int[]{
             cgaCayleyTable.getBasisBladeRow("e1"),
             cgaCayleyTable.getBasisBladeRow("e2"),
             cgaCayleyTable.getBasisBladeRow("e3"),
    });
    public SparseCGAColumnVector euclidean_vector(Tuple3d tuple3d) {
        // createEx(v.x).add(createEy(v.y)).add(createEz(v.z)
        return (SparseCGAColumnVector) fac.createE(tuple3d);
        /*return new SparseCGAColumnVector(euclideanVectorSparsity, 
                new double[]{tuple3d.x, tuple3d.y, tuple3d.z});*/
    }

    public SparseCGAColumnVector euclidean_bivector(Vector3d v1, Vector3d v2) {
        SparseCGAColumnVector v1mv = new SparseCGAColumnVector(euclideanVectorSparsity, 
                new double[]{v1.x, v1.y, v1.z});
        iMultivectorNumeric v1mvn = fac.createMultivectorNumeric(v1mv);
        SparseCGAColumnVector v2mv = new SparseCGAColumnVector(euclideanVectorSparsity, 
                new double[]{v2.x, v2.y, v2.z});
        iMultivectorNumeric v2mvn = fac.createMultivectorNumeric(v2mv);
        //return v1mvn.op(v2mvn);
        throw new RuntimeException("not yet implemented!");
    }

    public SparseCGAColumnVector scalar_opns(double scalar) {
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{0});
        return new SparseCGAColumnVector(sparsity, new double[]{scalar});
    }

    public SparseCGAColumnVector scalar_ipns(double scalar) {
        //var mvec = new CGAScalarIPNS(scalar);
        CGAMultivectorSparsity sparsity = new CGAMultivectorSparsity(new int[]{32});
        return new SparseCGAColumnVector(sparsity, new double[]{scalar});
    }

    public SparseCGAColumnVector bool(boolean bool) {
        //var mvec = new CGABoolean(bool);
        throw new RuntimeException("not yet implemented!");
    }
    private static CGAMultivectorSparsity roundPointIPNSSparsity = 
            new CGAMultivectorSparsity(new int[]{
             cgaCayleyTable.getBasisBladeRow("e1"),
             cgaCayleyTable.getBasisBladeRow("e2"),
             cgaCayleyTable.getBasisBladeRow("e3"),
             cgaCayleyTable.getBasisBladeRow("e4"),
             cgaCayleyTable.getBasisBladeRow("e5"),
    });
    public SparseCGAColumnVector createRoundPointIPNS(Point3d point){
        CGAMultivectorSparsity sparsity = roundPointIPNSSparsity;
        /*CGAMultivector result = (o
                .add(createEx(p.x))
                .add(createEy(p.y))
                .add(createEz(p.z))
                .add(createInf(0.5*(p.x*p.x+p.y*p.y+p.z*p.z)))).gp(weight);
        return result;*/
        //fac.createBaseVectorOrigin(1d).add(fac.createBaseVectorX(p.x))
                
        throw new RuntimeException("not yet implemented!");
        //return new SparseCGAColumnVector(sparsity, new double[]{1d, point.x, point.y, point.z, 0.5*(point.x*point.x+point.y*point.y+point.z*point.z)});
    }
    public SparseCGAColumnVector round_point_opns(Point3d point) {
        //var mvec = new CGARoundPointOPNS(point);
        throw new RuntimeException("not yet implemented!");
    }

    // point-pair
    public SparseCGAColumnVector pointpair_opns(Point3d point1, double weight1,
        Point3d point2, double weight2) {
        //var mvec = new CGAPointPairOPNS(point1, weight1, point2, weight2);
        throw new RuntimeException("not yet implemented!");
    }

    public SparseCGAColumnVector pointpair_opns(Point3d point1, Point3d point2) {
        //var mvec = new CGAPointPairOPNS(new CGARoundPointIPNS(point1), new CGARoundPointIPNS(point2));
        throw new RuntimeException("not yet implemented!");
    }

    // direction from point-2 to point-1
    public SparseCGAColumnVector pointpair_ipns/*2*/(Point3d location, Vector3d normal, double radius) {
        //var mvec = new CGAPointPairIPNS(location, normal, radius);
        throw new RuntimeException("not yet implemented!");
    }

    // via opns dual
    public SparseCGAColumnVector pointpair_ipns(Point3d point1, Point3d point2) {
        //var mvec = new CGAPointPairOPNS(new CGARoundPointIPNS(point1),
        //	new CGARoundPointIPNS(point2)).dual();
        throw new RuntimeException("not yet implemented!");
    }

    // line
    public SparseCGAColumnVector line_opns(Point3d point1, double weight1, Point3d point2, double weight2) {
        //var mvec = new CGALineOPNS(point1, weight1, point2, weight2);
        throw new RuntimeException("not yet implemented!");
    }

    public SparseCGAColumnVector line_opns(Point3d point1, Point3d point2) {
        //var mvec = new CGALineOPNS(point1, point2);
        throw new RuntimeException("not yet implemented!");
    }

    public SparseCGAColumnVector line_ipns(Point3d location, Vector3d normal) {
        //var mvec = new CGALineIPNS(location, normal);
        throw new RuntimeException("not yet implemented!");
    }

    // sphere
    public SparseCGAColumnVector sphere_ipns(Point3d center, double radius, double weight) {
        //var mvec = new CGASphereIPNS(center, radius, weight);
        throw new RuntimeException("not yet implemented!");
    }

    public SparseCGAColumnVector sphere_opns(Point3d center, double radius) {
        //var mvec = new CGASphereOPNS(center, radius);
        throw new RuntimeException("not yet implemented!");
    }

    public SparseCGAColumnVector sphere_ipns(Point3d center, double radius) {
        //var mvec = new CGASphereIPNS(center, radius);
        throw new RuntimeException("not yet implemented!");
    }

    // plane
    public SparseCGAColumnVector plane_ipns(Vector3d normal, double dist, double weight) {
        //var mvec = new CGAPlaneIPNS(normal, dist, weight);
        throw new RuntimeException("not yet implemented!");
    }

    public SparseCGAColumnVector plane_ipns(Vector3d normal, double dist) {
        //return this.plane_ipns(argName, normal, dist, 1.0);
        throw new RuntimeException("not yet implemented!");
    }

    public SparseCGAColumnVector plane_ipns(Point3d location, Vector3d normal) {
        //var mvec = new CGAPlaneIPNS(location, normal);
        throw new RuntimeException("not yet implemented!");
    }

    public SparseCGAColumnVector plane_opns(Point3d location, Vector3d normal) {
        //var mvec = new CGAPlaneIPNS(location, normal);
        throw new RuntimeException("not yet implemented!");
    }

    // circle
    public SparseCGAColumnVector circle_opns(Point3d point1, double weight1,
            Point3d point2, double weight2, Point3d point3, double weight3) {
        //var mvec = new CGACircleOPNS(point1, weight1, point2, weight2, point3, weight3);
        throw new RuntimeException("not yet implemented!");
    }

    public SparseCGAColumnVector circle_opns(Point3d point1, Point3d point2, Point3d point3) {
        //var mvec = new CGACircleOPNS(point1, point2, point3);
        throw new RuntimeException("not yet implemented!");
    }

    public SparseCGAColumnVector circle_ipns(Point3d location, Vector3d normal, double radius) {
        //var mvec = new CGACircleIPNS(location, normal, radius);
        throw new RuntimeException("not yet implemented!");
    }

    // oriented points
    public SparseCGAColumnVector oriented_point_ipns(Point3d location, Vector3d normal) {
        //var mvec = new CGAOrientedPointIPNS(location, normal);
        throw new RuntimeException("not yet implemented!");
    }

    // flat points
    public SparseCGAColumnVector flat_point_ipns(Point3d location) {
        //var mvec = new CGAFlatPointIPNS(location);
        throw new RuntimeException("not yet implemented!");
    }

    // tangent
    public SparseCGAColumnVector tangent_opns(Point3d location, Vector3d direction) {
        //var mvec = new CGATangentVectorOPNS(location, direction);
        throw new RuntimeException("not yet implemented!");
    }

    // attitude
    public SparseCGAColumnVector attitude_ipns(Vector3d t) {
        //var mvec = new CGAAttitudeVectorIPNS(t);
        throw new RuntimeException("not yet implemented!");
    }

    public SparseCGAColumnVector attitude_opns(Vector3d t) {
        //var mvec = new CGAAttitudeVectorOPNS(t);
        throw new RuntimeException("not yet implemented!");
    }

    // translation
    public SparseCGAColumnVector translator(Vector3d point) {
        //var mvec = new CGATranslator(point);
        throw new RuntimeException("not yet implemented!");
    }
}
