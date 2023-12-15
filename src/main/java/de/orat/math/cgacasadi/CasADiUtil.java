package de.orat.math.cgacasadi;

import de.dhbw.rahmlab.casadi.impl.casadi.DM;
import de.orat.math.cgacasadi.impl.SparseCGASymbolicMultivector;
import de.dhbw.rahmlab.casadi.impl.casadi.MX;
import de.dhbw.rahmlab.casadi.impl.casadi.SX;
import de.dhbw.rahmlab.casadi.impl.casadi.Sparsity;
import de.dhbw.rahmlab.casadi.impl.std.Dict;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorCasadiInt;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorDouble;
import de.dhbw.rahmlab.casadi.impl.std.StdVectorStdString;
import util.CayleyTable;
import util.CayleyTable.Cell;
import de.orat.math.sparsematrix.ColumnVectorSparsity;
import de.orat.math.sparsematrix.DenseDoubleMatrix;
import de.orat.math.sparsematrix.MatrixSparsity;
import de.orat.math.sparsematrix.SparseDoubleMatrix;
import de.orat.math.sparsematrix.SparseStringMatrix;

/**
 * @author Oliver Rettig (Oliver.Rettig@orat.de)
 */
public class CasADiUtil {
    
    private static long[] toLongArr(int[] values){
        long[] result = new long[values.length];
        for (int i=0;i<values.length;i++){
            result[i] = values[i];
        }
        return result;
    }
    private static int[] toIntArr(StdVectorCasadiInt values){
        int[] result = new int[values.size()];
        for (int i=0;i<values.size();i++){
            result[i] = values.get(i).intValue();
        }
        return result;
    }
    private static int[] toIntArr(long[] values){
        int[] result = new int[values.length];
        for (int i=0;i<values.length;i++){
            result[i] = (int) values[i];
        }
        return result;
    }
    
    public static MatrixSparsity toMatrixSparsity(de.dhbw.rahmlab.casadi.impl.casadi.Sparsity mxSparsity){
        //TODO
        // kann ich identifizieren ob es ein Row- oder Column -Vektor ist und wenn ja
        // mit welchem grade?
        return new MatrixSparsity((int) mxSparsity.rows(), (int) mxSparsity.columns(), 
                                  toIntArr(mxSparsity.get_colind()), 
                                  toIntArr(mxSparsity.get_row()));
    }
    
    public static ColumnVectorSparsity toColumnVectorSparsity(de.dhbw.rahmlab.casadi.impl.casadi.Sparsity mxSparsity){
        return new ColumnVectorSparsity((int) mxSparsity.rows(),  
                                  toIntArr(mxSparsity.get_row()));
    }
    
    public static CGAMultivectorSparsity toCGAMultivectorSparsity(de.dhbw.rahmlab.casadi.impl.casadi.Sparsity mxSparsity){
        return new CGAMultivectorSparsity(toIntArr(mxSparsity.get_row()));
    }

    public static MX toMX(SparseDoubleMatrix m){
       return new MX(toCasADiSparsity(m.getSparsity()), toMX(m.getData()));
    }
    public static MX toMX(double[] values){
        return new MX(toStdVectorDouble(values));
    }
    
    /**
     * Create a corresponding matrix for geometric product calculation.
     */
    public static MX toMXProductMatrix(SparseCGASymbolicMultivector mv, CGACayleyTable cgaCayleyTable){
       
        System.out.println("toMXproductMatrix(cv):  "+mv.getSparsity().toString());
        MatrixSparsity matrixSparsity = createSparsity(cgaCayleyTable, mv);
        System.out.println("toMXproductMatrix(ct):  "+matrixSparsity.toString());
        de.dhbw.rahmlab.casadi.impl.casadi.Sparsity sp = CasADiUtil.toCasADiSparsity(matrixSparsity);
        // testweise dense --> hat keinen Unterschied gemacht
        //de.dhbw.rahmlab.casadi.impl.casadi.Sparsity sp = CasADiUtil.toCasADiSparsity(MatrixSparsity.dense(32, 32));
        //System.out.println("toMXProductMatrix(ct2): "+sp.toString(true));
        
        //MX denseResult = MX.sym(mv.getMX().name(), mv.getBladesCount(), mv.getBladesCount());
        //MX result = MX.sym(mv.getMX().name(), sp);
        MX result = new MX(sp);
        
        MatrixSparsity sparsity = mv.getSparsity();
        for (int i=0;i<cgaCayleyTable.getRows();i++){
            for (int j=0;j<cgaCayleyTable.getCols();j++){
                Cell cell = cgaCayleyTable.getCell(i, j); 
                // Cell enthält einen basis-blade
                if (cell.bladeIndex() >=0){
                    if (sparsity.isNonZero(cell.bladeIndex(), 0)){
                        if (cell.Value() == 1d){
                            result.at(i, j).assign(mv.getMX().at(cell.bladeIndex(),0)); 
                            System.out.println("to(sym)["+String.valueOf(i)+"]["+String.valueOf(j)+"]="+
                                    mv.getMX().at(cell.bladeIndex(),0));
                        // -1, oder -xxxx multipliziert mit dem basis-blade
                        } else {
                            // Das ist ja eine Skalarmultiplikation
                            // Wie kann ohne dot() hier arbeiten? ein mix mit SX geht ja vermutlich nicht
                            // aber vielleicht kann hier ja ein Function Objekt erzeugt und eingefügt werden?
                            //TODO
                            // mit Funktion ist unklar, ob ich dann nicht notwendigerweise den
                            // Multivektor als SX speichern muss
                            // dann wiederum ist unklar, ob SX alle Operatoren hat im Vergleich mit MX 
                            // die ich brauche
                            // wie kann ich eine Funktion in die Cell einer Matrix hängen?
                            //TODO
                            
                            // vorher hatte ich hier dot
                            // cell.Value enthält den Zahlenwert der in der entsprechenden
                            // Zelle der Cayleytable steht. Dieser muss multipliziert werden
                            // mit dem Wert der Zelle des korrespondierenden Multivektors. Das
                            // Zell-Objekt enthält dazu den index im Column-Vector.
                            result.at(i, j).assign(MX.times(new MX(cell.Value()), 
                                    mv.getMX().at(cell.bladeIndex())));
                            System.out.println("to(num)["+String.valueOf(i)+"]["+String.valueOf(j)+"]="+
                                   MX.times(new MX(cell.Value()), 
                                    new MX(mv.getMX().at(cell.bladeIndex())) ));
                        }
                    // wegen sparsity 0 setzen
                    } else {
                        //FIXME
                        // muss ich dann überhaupt einen Wert setzen?
                        //denseResult.at(i, j).assign(new MX(0d));
                    }
                // cell enthält eine 0 als Koeffizient
                } else {
                    //FIXME
                    // muss ich dann überhaupt einen Wert setzen?
                    //denseResult.at(i, j).assign(new MX(0d));
                }
            }
        }
        
        // testweise
        // Ausgabe taugt nichts, also die toStringMatrix() method taugt nichts
        //System.out.println(CasADiUtil.toStringMatrix(denseResult).toString(true));
        
        //TODO
        // nachfolgende schlägt was fehl!!!
        // Das zweite Argument soll nur die nonzeros enthalten, ich gehe aber davon
        // aus, dass es die dense matrix enthält
        //MX result = new MX(sp, denseResult);
        //MX result = MX.project(denseResult, sp);
        //MX result = MX.reshape(denseResult, sp);
        //MX result = new MX(sp, denseResult);
        //MX.setSparse(sp); //FIXME method setSparse scheint zu fehlen
        return result;
    }
    
    /**
     * Create a sparsity object for the given cayleyTable based on the sparsity
     * of the given sparse multivector.
     * 
     * @param cayleyTable
     * @param mv sparse multivector
     * @return matrix sparsity for the given cayley table
     */
    private static MatrixSparsity createSparsity(CayleyTable cayleyTable, SparseCGASymbolicMultivector mv){
        double[][] values = new double[mv.getBladesCount()][mv.getBladesCount()];
        ColumnVectorSparsity sparsity = mv.getSparsity();
        for (int i=0;i<cayleyTable.getRows();i++){
            for (int j=0;j<cayleyTable.getCols();j++){
                Cell cell = cayleyTable.getCell(i, j); 
                // Cell enthält einen basis-blade
                if (cell.bladeIndex() >=0){
                    if (sparsity.isNonZero(cell.bladeIndex(), 0)){
                        values[i][j] = 1d;
                    } 
                } 
            }
        }
        return new MatrixSparsity(values);
    }
    public static double[] nonzeros(DM dm){
        StdVectorDouble res = dm.nonzeros();
        double[] result = new double[res.size()];
        for (int i=0;i<result.length;i++){
            result[i] = res.get(i);
        }
        return result;
    }
    public static double[] elements(DM dm){
        StdVectorDouble res = dm.get_elements();
        double[] result = new double[res.size()];
        for (int i=0;i<result.length;i++){
            result[i] = res.get(i);
        }
        return result;
    }
    public static DenseCGAColumnVector toDenseDoubleMatrix(DM dm, CGAMultivectorSparsity sparsity){
        double[] nonzeros = nonzeros(dm);
        return new DenseCGAColumnVector(nonzeros, sparsity.getrow());
    }
    public static SparseStringMatrix toStringMatrix(MX m){
        String[][] stringArr = new String[(int) m.rows()][(int) m.columns()];
        for (int i=0;i<m.rows();i++){
            for (int j=0;j<m.columns();j++){
                MX cell = m.at(j, j);
                // name() ist nicht immer definiert!!!!
                // {mac(((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((a[0] = 1)[32] = a[0])[64] = a[1])[96] = a[2])[128] = a[3])[160] = a[4])[192] = a[0])[224] = a[0])[256] = a[0])[288] = a[0])[320] = a[1])[352] = a[1])[384] = a[1])[416] = a[2])[448] = a[2])[480] = a[3])[512] = a[0])[544] = a[0])[576] = a[0])[608] = a[0])[640] = a[0])[672] = a[0])[704] = a[1])[736] = a[1])[768] = a[1])[800] = a[2])[832] = a[0])[864] = a[0])[896] = a[0])[928] = a[0])[960] = a[1])[992] = a[0])[1] = a[0])[33] = 1)[65] = a[0])[97] = a[0])[129] = a[0])[161] = a[0])[193] = a[1])[225] = a[2])[257] = a[3])[289] = a[4])[321] = a[0])[353] = a[0])[385] = a[0])[417] = a[0])[449] = a[0])[481] = a[0])[513] = a[1])[545] = a[1])[577] = a[1])[609] = a[2])[641] = a[2])[673] = a[3])[705] = a[0])[737] = a[0])[769] = a[0])[801] = a[0])[833] = a[1])[865] = a[1])[897] = a[1])[929] = a[2])[961] = a[0])[993] = a[1])[2] = a[1])[34] = 1)[66] = 1)[98] = a[1])[130] = a[1])[162] = a[1])[194] = 1)[226] = 1)[258] = 1)[290] = 1)[322] = a[2])[354] = a[3])[386] = a[4])[418] = a[1])[450] = a[1])[482] = a[1])[514] = 1)[546] = 1)[578] = 1)[610] = 1)[642] = 1)[674] = 1)[706] = a[2])[738] = a[2])[770] = a[3])[802] = a[1])[834] = 1)[866] = 1)[898] = 1)[930] = 1)[962] = a[2])[994] = 1)[3] = a[2])[35] = 1)[67] = 1)[99] = 1)[131] = a[2])[163] = a[2])[195] = a[0])[227] = 1)[259] = 1)[291] = 1)[323] = 1)[355] = 1)[387] = 1)[419] = a[3])[451] = a[4])[483] = a[2])[515] = a[0])[547] = a[0])[579] = a[0])[611] = 1)[643] = 1)[675] = 1)[707] = 1)[739] = 1)[771] = 1)[803] = a[3])[835] = a[0])[867] = a[0])[899] = a[0])[931] = 1)[963] = 1)[995] = a[0])[4] = a[3])[36] = 1)[68] = 1)[100] = 1)[132] = 1)[164] = a[3])[196] = a[0])[228] = a[0])[260] = 1)[292] = 1)[324] = a[1])[356] = 1)[388] = 1)[420] = 1)[452] = 1)[484] = a[4])[516] = 1)[548] = a[0])[580] = a[0])[612] = a[0])[644] = a[0])[676] = 1)[708] = a[1])[740] = a[1])[772] = 1)[804] = 1)[836] = 1)[868] = 1)[900] = a[0])[932] = a[0])[964] = a[1])[996] = 1)[5] = a[4])[37] = 1)[69] = 1)[101] = 1)[133] = 1)[165] = -1)[197] = a[0])[229] = a[0])[261] = a[0])[293] = a[0])[325] = a[1])[357] = a[1])[389] = a[1])[421] = a[2])[453] = a[2])[485] = a[3])[517] = 1)[549] = 1)[581] = 1)[613] = 1)[645] = 1)[677] = 1)[709] = 1)[741] = 1)[773] = 1)[805] = 1)[837] = a[0])[869] = a[0])[901] = a[0])[933] = a[0])[965] = a[1])[997] = 1)[6] = a[0])[38] = 1)[70] = a[0])[102] = a[0])[134] = a[0])[166] = a[0])[198] = -1)[230] = 1)[262] = 1)[294] = 1)[326] = a[0])[358] = a[0])[390] = a[0])[422] = a[0])[454] = a[0])[486] = a[0])[518] = 1)[550] = 1)[582] = 1)[614] = 1)[646] = 1)[678] = 1)[710] = a[0])[742] = a[0])[774] = a[0])[806] = a[0])[838] = 1)[870] = 1)[902] = 1)[934] = 1)[966] = a[0])[998] = 1)[7] = a[0])[39] = 1)[71] = 1)[103] = a[0])[135] = a[0])[167] = a[0])[199] = a[1])[231] = -1)[263] = 1)[295] = 1)[327] = 1)[359] = 1)[391] = 1)[423] = a[0])[455] = a[0])[487] = a[0])[519] = a[1])[551] = a[1])[583] = a[1])[615] = 1)[647] = 1)[679] = 1)[711] = 1)[743] = 1)[775] = 1)[807] = a[0])[839] = a[1])[871] = a[1])[903] = a[1])[935] = 1)[967] = 1)[999] = a[1])[8] = a[0])[40] = 1)[72] = 1)[104] = 1)[136] = a[0])[168] = a[0])[200] = a[1])[232] = a[2])[264] = -1)[296] = 1)[328] = a[0])[360] = 1)[392] = 1)[424] = 1)[456] = 1)[488] = a[0])[520] = 1)[552] = a[1])[584] = a[1])[616] = a[2])[648] = a[2])[680] = 1)[712] = a[0])[744] = a[0])[776] = 1)[808] = 1)[840] = 1)[872] = 1)[904] = a[1])[936] = a[2])[968] = a[0])[1000] = 1)[9] = a[0])[41] = 1)[73] = 1)[105] = 1)[137] = 1)[169] = 1)[201] = a[1])[233] = a[2])[265] = a[3])[297] = 1)[329] = a[0])[361] = a[0])[393] = a[0])[425] = a[0])[457] = a[0])[489] = a[0])[521] = 1)[553] = 1)[585] = 1)[617] = 1)[649] = 1)[681] = 1)[713] = 1)[745] = 1)[777] = 1)[809] = 1)[841] = a[1])[873] = a[1])[905] = a[1])[937] = a[2])[969] = a[0])[1001] = 1)[10] = a[1])[42] = a[0])[74] = 1)[106] = a[1])[138] = a[1])[170] = a[1])[202] = 1)[234] = a[0])[266] = a[0])[298] = a[0])[330] = -1)[362] = 1)[394] = 1)[426] = a[1])[458] = a[1])[490] = a[1])[522] = 1)[554] = 1)[586] = 1)[618] = a[0])[650] = a[0])[682] = a[0])[714] = 1)[746] = 1)[778] = 1)[810] = a[1])[842] = 1)[874] = 1)[906] = 1)[938] = a[0])[970] = 1)[1002] = 1)[11] = a[1])[43] = a[0])[75] = 1)[107] = 1)[139] = a[1])[171] = a[1])[203] = 1)[235] = 1)[267] = a[0])[299] = a[0])[331] = a[2])[363] = -1)[395] = 1)[427] = 1)[459] = 1)[491] = a[1])[523] = a[0])[555] = 1)[587] = 1)[619] = 1)[651] = 1)[683] = a[0])[715] = a[2])[747] = a[2])[779] = 1)[811] = 1)[843] = a[0])[875] = a[0])[907] = 1)[939] = 1)[971] = a[2])[1003] = a[0])[12] = a[1])[44] = a[0])[76] = 1)[108] = 1)[140] = 1)[172] = 1)[204] = 1)[236] = 1)[268] = 1)[300] = 1)[332] = a[2])[364] = a[3])[396] = 1)[428] = a[1])[460] = a[1])[492] = a[1])[524] = a[0])[556] = a[0])[588] = a[0])[620] = a[0])[652] = a[0])[684] = a[0])[716] = 1)[748] = 1)[780] = 1)[812] = 1)[844] = 1)[876] = 1)[908] = 1)[940] = 1)[972] = a[2])[1004] = a[0])[13] = a[2])[45] = a[0])[77] = a[1])[109] = 1)[141] = a[2])[173] = a[2])[205] = a[0])[237] = 1)[269] = a[0])[301] = a[0])[333] = 1)[365] = a[1])[397] = a[1])[429] = -1)[461] = 1)[493] = a[2])[525] = 1)[557] = a[0])[589] = a[0])[621] = 1)[653] = 1)[685] = a[0])[717] = 1)[749] = 1)[781] = a[1])[813] = 1)[845] = 1)[877] = 1)[909] = a[0])[941] = 1)[973] = 1)[1005] = 1)[14] = a[2])[46] = a[0])[78] = a[1])[110] = 1)[142] = 1)[174] = 1)[206] = a[0])[238] = 1)[270] = 1)[302] = 1)[334] = 1)[366] = 1)[398] = 1)[430] = a[3])[462] = 1)[494] = a[2])[526] = 1)[558] = 1)[590] = 1)[622] = a[0])[654] = a[0])[686] = a[0])[718] = a[1])[750] = a[1])[782] = a[1])[814] = 1)[846] = a[0])[878] = a[0])[910] = a[0])[942] = 1)[974] = 1)[1006] = 1)[15] = a[3])[47] = a[0])[79] = a[1])[111] = a[2])[143] = 1)[175] = 1)[207] = a[0])[239] = a[0])[271] = 1)[303] = 1)[335] = a[1])[367] = 1)[399] = 1)[431] = 1)[463] = 1)[495] = 1)[527] = a[0])[559] = 1)[591] = 1)[623] = 1)[655] = 1)[687] = a[0])[719] = 1)[751] = 1)[783] = a[1])[815] = a[2])[847] = 1)[879] = 1)[911] = a[0])[943] = a[0])[975] = a[1])[1007] = a[0])[16] = a[0])[48] = a[1])[80] = 1)[112] = a[0])[144] = a[0])[176] = a[0])[208] = 1)[240] = a[1])[272] = a[1])[304] = a[1])[336] = 1)[368] = 1)[400] = 1)[432] = a[0])[464] = a[0])[496] = a[0])[528] = -1)[560] = 1)[592] = 1)[624] = a[1])[656] = a[1])[688] = a[1])[720] = 1)[752] = 1)[784] = 1)[816] = a[0])[848] = 1)[880] = 1)[912] = 1)[944] = a[1])[976] = 1)[1008] = 1)[17] = a[0])[49] = a[1])[81] = 1)[113] = 1)[145] = a[0])[177] = a[0])[209] = 1)[241] = 1)[273] = a[1])[305] = a[1])[337] = a[0])[369] = 1)[401] = 1)[433] = 1)[465] = 1)[497] = a[0])[529] = a[2])[561] = -1)[593] = 1)[625] = 1)[657] = 1)[689] = a[1])[721] = a[0])[753] = a[0])[785] = 1)[817] = 1)[849] = a[2])[881] = a[2])[913] = 1)[945] = 1)[977] = a[0])[1009] = a[2])[18] = a[0])[50] = a[1])[82] = 1)[114] = 1)[146] = 1)[178] = 1)[210] = 1)[242] = 1)[274] = 1)[306] = 1)[338] = a[0])[370] = a[0])[402] = a[0])[434] = a[0])[466] = a[0])[498] = a[0])[530] = a[2])[562] = a[3])[594] = 1)[626] = a[1])[658] = a[1])[690] = a[1])[722] = 1)[754] = 1)[786] = 1)[818] = 1)[850] = 1)[882] = 1)[914] = 1)[946] = 1)[978] = a[0])[1010] = a[2])[19] = a[0])[51] = a[2])[83] = a[0])[115] = 1)[147] = a[0])[179] = a[0])[211] = a[1])[243] = 1)[275] = a[2])[307] = a[2])[339] = 1)[371] = a[0])[403] = a[0])[435] = 1)[467] = 1)[499] = a[0])[531] = 1)[563] = a[1])[595] = a[1])[627] = -1)[659] = 1)[691] = a[2])[723] = 1)[755] = 1)[787] = a[0])[819] = 1)[851] = 1)[883] = 1)[915] = a[1])[947] = 1)[979] = 1)[1011] = 1)[20] = a[0])[52] = a[2])[84] = a[0])[116] = 1)[148] = 1)[180] = 1)[212] = a[1])[244] = 1)[276] = 1)[308] = 1)[340] = 1)[372] = 1)[404] = 1)[436] = a[0])[468] = a[0])[500] = a[0])[532] = 1)[564] = 1)[596] = 1)[628] = a[3])[660] = 1)[692] = a[2])[724] = a[0])[756] = a[0])[788] = a[0])[820] = 1)[852] = a[1])[884] = a[1])[916] = a[1])[948] = 1)[980] = 1)[1012] = 1)[21] = a[0])[53] = a[3])[85] = a[0])[117] = a[0])[149] = 1)[181] = 1)[213] = a[1])[245] = a[2])[277] = 1)[309] = 1)[341] = a[0])[373] = 1)[405] = 1)[437] = 1)[469] = 1)[501] = a[0])[533] = a[1])[565] = 1)[597] = 1)[629] = 1)[661] = 1)[693] = 1)[725] = 1)[757] = 1)[789] = a[0])[821] = a[0])[853] = 1)[885] = 1)[917] = a[1])[949] = a[2])[981] = a[0])[1013] = a[1])[22] = a[1])[54] = 1)[86] = a[2])[118] = 1)[150] = a[1])[182] = a[1])[214] = 1)[246] = a[0])[278] = 1)[310] = 1)[342] = 1)[374] = a[2])[406] = a[2])[438] = 1)[470] = 1)[502] = a[1])[534] = a[0])[566] = 1)[598] = 1)[630] = a[0])[662] = a[0])[694] = 1)[726] = -1)[758] = 1)[790] = a[2])[822] = 1)[854] = a[0])[886] = a[0])[918] = 1)[950] = a[0])[982] = 1)[1014] = a[0])[23] = a[1])[55] = 1)[87] = a[2])[119] = 1)[151] = 1)[183] = 1)[215] = 1)[247] = a[0])[279] = a[0])[311] = a[0])[343] = 1)[375] = 1)[407] = 1)[439] = a[1])[471] = a[1])[503] = a[1])[535] = a[0])[567] = a[0])[599] = a[0])[631] = 1)[663] = 1)[695] = 1)[727] = a[3])[759] = 1)[791] = a[2])[823] = 1)[855] = 1)[887] = 1)[919] = 1)[951] = a[0])[983] = 1)[1015] = a[0])[24] = a[1])[56] = 1)[88] = a[3])[120] = a[1])[152] = 1)[184] = 1)[216] = 1)[248] = 1)[280] = a[0])[312] = a[0])[344] = a[2])[376] = 1)[408] = 1)[440] = 1)[472] = 1)[504] = a[1])[536] = 1)[568] = a[0])[600] = a[0])[632] = a[0])[664] = a[0])[696] = 1)[728] = 1)[760] = 1)[792] = 1)[824] = a[1])[856] = a[0])[888] = a[0])[920] = 1)[952] = 1)[984] = a[2])[1016] = 1)[25] = a[2])[57] = 1)[89] = 1)[121] = a[3])[153] = 1)[185] = 1)[217] = a[0])[249] = 1)[281] = a[0])[313] = a[0])[345] = 1)[377] = a[1])[409] = a[1])[441] = 1)[473] = 1)[505] = a[2])[537] = a[0])[569] = 1)[601] = 1)[633] = a[0])[665] = a[0])[697] = 1)[729] = a[1])[761] = a[1])[793] = 1)[825] = 1)[857] = 1)[889] = 1)[921] = a[0])[953] = 1)[985] = 1)[1017] = a[0])[26] = a[0])[58] = 1)[90] = a[0])[122] = 1)[154] = a[0])[186] = a[0])[218] = 1)[250] = a[1])[282] = 1)[314] = 1)[346] = 1)[378] = a[0])[410] = a[0])[442] = 1)[474] = 1)[506] = a[0])[538] = a[3])[570] = 1)[602] = 1)[634] = a[1])[666] = a[1])[698] = 1)[730] = 1)[762] = 1)[794] = a[0])[826] = 1)[858] = 1)[890] = a[3])[922] = 1)[954] = a[1])[986] = 1)[1018] = a[4])[27] = a[0])[59] = 1)[91] = a[0])[123] = 1)[155] = 1)[187] = 1)[219] = 1)[251] = a[1])[283] = a[1])[315] = a[1])[347] = 1)[379] = 1)[411] = 1)[443] = a[0])[475] = a[0])[507] = a[0])[539] = a[4])[571] = a[2])[603] = a[2])[635] = 1)[667] = 1)[699] = 1)[731] = a[0])[763] = a[0])[795] = a[0])[827] = 1)[859] = 1)[891] = -1)[923] = 1)[955] = a[1])[987] = 1)[1019] = a[3])[28] = a[0])[60] = 1)[92] = a[0])[124] = a[0])[156] = 1)[188] = 1)[220] = 1)[252] = 1)[284] = a[1])[316] = a[1])[348] = a[0])[380] = 1)[412] = 1)[444] = 1)[476] = 1)[508] = a[0])[540] = 1)[572] = a[4])[604] = a[3])[636] = a[1])[668] = a[1])[700] = 1)[732] = 1)[764] = 1)[796] = a[0])[828] = a[0])[860] = a[2])[892] = a[2])[924] = -1)[956] = 1)[988] = a[0])[1020] = 1)[29] = a[0])[61] = 1)[93] = 1)[125] = a[0])[157] = 1)[189] = 1)[221] = a[1])[253] = 1)[285] = a[2])[317] = a[2])[349] = 1)[381] = a[0])[413] = a[0])[445] = 1)[477] = 1)[509] = a[0])[541] = a[1])[573] = 1)[605] = 1)[637] = a[4])[669] = a[3])[701] = 1)[733] = a[0])[765] = a[0])[797] = 1)[829] = a[0])[861] = 1)[893] = 1)[925] = a[1])[957] = -1)[989] = 1)[1021] = a[1])[30] = a[1])[62] = a[0])[94] = 1)[126] = a[1])[158] = 1)[190] = 1)[222] = 1)[254] = a[0])[286] = 1)[318] = 1)[350] = 1)[382] = a[2])[414] = a[2])[446] = 1)[478] = 1)[510] = a[1])[542] = 1)[574] = a[0])[606] = a[0])[638] = 1)[670] = 1)[702] = a[0])[734] = a[4])[766] = a[3])[798] = 1)[830] = a[1])[862] = a[0])[894] = a[0])[926] = 1)[958] = a[0])[990] = -1)[1022] = 1)[31] = a[0])[63] = a[1])[95] = 1)[127] = a[0])[159] = 1)[191] = 1)[223] = 1)[255] = a[1])[287] = 1)[319] = 1)[351] = 1)[383] = a[0])[415] = a[0])[447] = 1)[479] = 1)[511] = a[0])[543] = 1)[575] = a[2])[607] = a[2])[639] = 1)[671] = 1)[703] = a[1])[735] = a[0])[767] = a[0])[799] = 1)[831] = a[0])[863] = a[4])[895] = a[3])[927] = 1)[959] = a[1])[991] = 1)[1023] = -1)',b,values(32x1))[0]},
                //stringArr[i][j] = cell.toString();
                // result: {
                // {[1,1]},
                // {[1,1]},
                //stringArr[i][j] = "["+String.valueOf(cell.rows())+","+String.valueOf(cell.columns())+"]";
                
                //stringArr[i][j] = cell.toString(true);
                //Dict dict = cell.info();
                
                //stringArr[i][j] = dict.toString();
                stringArr[i][j] = cell.toString(false);
                
                //stringArr[i][j] = MX.print_operator(cell, new StdVectorStdString());
            }
        }
        return new SparseStringMatrix(stringArr);
    }
     
    public static Sparsity toCasADiSparsity(de.orat.math.sparsematrix.MatrixSparsity sparsity){
        StdVectorCasadiInt row = new StdVectorCasadiInt(toLongArr(sparsity.getrow()));
        StdVectorCasadiInt colind = new StdVectorCasadiInt(toLongArr(sparsity.getcolind()));
        Sparsity result = new Sparsity(sparsity.getn_row(), sparsity.getn_col(), colind, row);
        result.spy();
        return result;
    }
    
    public static DM toDM(double[] values){
        ColumnVectorSparsity sparsity = new ColumnVectorSparsity(values);
        //TODO
        // Achtung: Hier gehe ich davon aus, dass einfach nur die non-values zu übergeben sind
        // das habe ich aber noch nicht verifiziert
        StdVectorDouble nonzeros = new StdVectorDouble();
        for (int i=0;i<values.length;i++){
            if (values[i] != 0) nonzeros.add(values[i]);
        }
        return new DM(toCasADiSparsity(sparsity), nonzeros, false);
    }
    
    public static DM toDM(int n_row, double[] nonzeros, int[] rows){
        ColumnVectorSparsity sparsity = new ColumnVectorSparsity(n_row, rows);
        return new DM(toCasADiSparsity(sparsity), toStdVectorDouble(nonzeros), false);
    }
     
    public static StdVectorDouble toStdVectorDouble(double[] values){
        StdVectorDouble result = new StdVectorDouble();
        for (int i=0;i<values.length;i++){
            result.add(values[i]);
        }
        return result;
    }
    
    /**
     * @param sparsity
     * @param values only nonzeros
     * @return 
     */
    public static DM toDM(ColumnVectorSparsity sparsity, double[] values){
        //TODO
        // Achtung: Hier gehe ich davon aus, dass einfach nur die non-values zu übergeben sind
        // das habe ich aber noch nicht verifiziert
        StdVectorDouble stdVectorDoubles = new StdVectorDouble();
        for (int i=0;i<values.length;i++){
            stdVectorDoubles.add(values[i]);
        }
        return new DM(toCasADiSparsity(sparsity),stdVectorDoubles, false);
    }
}
