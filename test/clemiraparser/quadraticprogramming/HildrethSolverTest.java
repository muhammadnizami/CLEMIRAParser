/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.quadraticprogramming;

import clemiraparser.util.MySparseVector;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author nizami
 */
public class HildrethSolverTest {
    static HildrethSolver hildrethSolver;
    @BeforeClass
    public static void onlyOnce() {
       hildrethSolver = new HildrethSolver();
    }
    
    @Test
    public void testTwoDimensionsOneConstraint(){
        int vecLen=2;
        int numIneq=1;
        RealVector x_0 = new MySparseVector(vecLen);
        x_0.setEntry(0,0);
        x_0.setEntry(1,1);
        
        RealVector[] A = new RealVector[numIneq];
        double [] b = new double[numIneq];
        A[0]=new ArrayRealVector(new double[]{-1.0,1.0});
        b[0]=0;
        
        RealVector hildrethSolution = hildrethSolver.solve(x_0,A,b);
        RealVector expected = new ArrayRealVector(new double[]{0.5,0.5});
        Assert.assertEquals(expected, hildrethSolution);
    }
    
    @Test
    public void testTwoDimensionsOneConstraint2(){
        int vecLen=2;
        int numIneq=1;
        RealVector x_0 = new MySparseVector(vecLen);
        x_0.setEntry(0,0);
        x_0.setEntry(1,1);
        
        RealVector[] A = new RealVector[numIneq];
        double [] b = new double[numIneq];
        A[0]=new ArrayRealVector(new double[]{1.0,-1.0});
        b[0]=0;
        
        RealVector hildrethSolution = hildrethSolver.solve(x_0,A,b);
        RealVector expected = new ArrayRealVector(new double[]{0,1});
        Assert.assertEquals(expected, hildrethSolution);
    }
    
    @Test
    public void checkTwoDimensionsOneConstraint3(){
        int vecLen=2;
        int numIneq=1;
        RealVector x_0 = new MySparseVector(vecLen);
        x_0.setEntry(0,1);
        x_0.setEntry(1,2);
        
        RealVector[] A = new RealVector[numIneq];
        double [] b = new double[numIneq];
        A[0]=new ArrayRealVector(new double[]{-1.0,1.0});
        b[0]=-1;
        
        RealVector hildrethSolution = hildrethSolver.solve(x_0,A,b);
        RealVector expected = new ArrayRealVector(new double[]{2,1});
        Assert.assertEquals(expected, hildrethSolution);
    }
    
    @Test
    public void checkTwoDimensionsOneConstraint4(){
        int vecLen=2;
        int numIneq=1;
        RealVector x_0 = new MySparseVector(vecLen);
        x_0.setEntry(0,1);
        x_0.setEntry(1,2);
        
        RealVector[] A = new RealVector[numIneq];
        double [] b = new double[numIneq];
        A[0]=new ArrayRealVector(new double[]{1.0,-1.0});
        b[0]=1;
        
        RealVector hildrethSolution = hildrethSolver.solve(x_0,A,b);
        RealVector expected = new ArrayRealVector(new double[]{1,2});
        Assert.assertEquals(expected, hildrethSolution);
    }
    
    @Test   
    public void checkTwoDimensionsTwoConstraints(){
        int vecLen=2;
        int numIneq=2;
        RealVector x_0 = new MySparseVector(vecLen);
        x_0.setEntry(0,0);
        x_0.setEntry(1,1);
        
        RealVector[] A = new RealVector[numIneq];
        double [] b = new double[numIneq];
        A[0]=new ArrayRealVector(new double[]{-1.0,1.0});
        b[0]=0;
        A[1]=new ArrayRealVector(new double[]{1.0,1.0});
        b[1]=0;
        
        RealVector hildrethSolution = hildrethSolver.solve(x_0,A,b);
        RealVector expected = new ArrayRealVector(new double[]{0,0});
        Assert.assertEquals(expected, hildrethSolution);
    }
    
    @Test
    public void checkTwoDimensionsTwoConstraints2(){
        int vecLen=2;
        int numIneq=2;
        RealVector x_0 = new MySparseVector(vecLen);
        x_0.setEntry(0,0);
        x_0.setEntry(1,1);
        
        RealVector[] A = new RealVector[numIneq];
        double [] b = new double[numIneq];
        A[0]=new ArrayRealVector(new double[]{1.0,-1.0});
        b[0]=0;
        A[1]=new ArrayRealVector(new double[]{-1.0,-1.0});
        b[1]=0;
        
        RealVector hildrethSolution = hildrethSolver.solve(x_0,A,b);
        RealVector expected = new ArrayRealVector(new double[]{0,1});
        Assert.assertEquals(expected, hildrethSolution);
    }
    
    @Test
    public void checkBand(){
        int vecLen=2;
        int numIneq=2;
        RealVector x_0 = new MySparseVector(vecLen);
        x_0.setEntry(0,-1);
        x_0.setEntry(1,2);
        
        RealVector[] A = new RealVector[numIneq];
        double [] b = new double[numIneq];
        A[0]=new ArrayRealVector(new double[]{1.0,-1.0});
        b[0]=1;
        A[1]=new ArrayRealVector(new double[]{-1.0,1.0});
        b[1]=1;
        
        RealVector hildrethSolution = hildrethSolver.solve(x_0,A,b);
        RealVector expected = new ArrayRealVector(new double[]{0,1});
        Assert.assertEquals(expected, hildrethSolution);        
    }
    
    @Test
    public void checkBand2(){
        int vecLen=2;
        int numIneq=2;
        RealVector x_0 = new MySparseVector(vecLen);
        x_0.setEntry(0,0);
        x_0.setEntry(1,0);
        
        RealVector[] A = new RealVector[numIneq];
        double [] b = new double[numIneq];
        A[0]=new ArrayRealVector(new double[]{1.0,-1.0});
        b[0]=1;
        A[1]=new ArrayRealVector(new double[]{-1.0,1.0});
        b[1]=1;
        
        RealVector hildrethSolution = hildrethSolver.solve(x_0,A,b);
        RealVector expected = new ArrayRealVector(new double[]{0,0});
        Assert.assertEquals(expected, hildrethSolution);
    }
}
