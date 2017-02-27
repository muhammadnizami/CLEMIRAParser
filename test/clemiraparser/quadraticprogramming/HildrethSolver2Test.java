/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.quadraticprogramming;

import org.junit.BeforeClass;

/**
 *
 * @author nizami
 */
public class HildrethSolver2Test extends HildrethSolverTest{
    @BeforeClass
    public static void onlyOnce() {
       hildrethSolver = new HildrethSolver();
    }
}
