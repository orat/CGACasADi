package de.orat.math.cgacasadi;

import de.orat.math.cgacasadi.impl.CGAExprGraphFactory;

public class ConstCacheTest {

    public static void main(String[] args) {
        var con = CGAExprGraphFactory.instance.constantsNumeric();
        con.testCache();
        var a = con.getPi();
        con.testCache();
        var b = con.getPi();
        con.testCache();
        System.out.println(a.add(b));
    }
}
