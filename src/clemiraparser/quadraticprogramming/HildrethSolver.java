/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.quadraticprogramming;

import clemiraparser.util.MySparseVector;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.RealVector;

/**
 *  the Hildreth algorithm described in Jamil(2014) for distance minimization problem.
 * the algorithm presented here is the modified version for Ax<=b
 * , with the precalculation of A norms and dot product of A and x0
 * @author nizami
 */
public class HildrethSolver {
    //default coniguration
    double alpha= 1; //alpha should be between 0 and 2
    int maxIter = 10000;
    double zeroReplacement = 0.000000000001; //zero replacement for zero norms
    public HildrethSolver(){}
    
    public void setAlpha(double new_alpha){
        this.alpha=new_alpha;
    }
    
    public void setMaxIter(int maxIter){
        this.maxIter = maxIter;
    }
    
    public void setZeroReplacement(double z){
        zeroReplacement = z;
    }
    
    /**
     * @param x_0
     * @param A
     * @param b
     * @return x* described in Jamil(2014)
     * */
    public RealVector solve(RealVector x_0, RealVector[] A, double[] b){
        
        if (A.length != b.length)
            throw new DimensionMismatchException(A.length,b.length);
        
        RealVector delta_x = new MySparseVector(x_0.getDimension());
        double [] z = new double[b.length];

        //cached calculations
        double [] Anorms = new double[A.length];
        double [] Adotx0 = new double[A.length];
        for (int i=0;i<A.length;i++){
            Anorms[i] = A[i].dotProduct(A[i]);
            if (Anorms[i]==0)
                Anorms[i]=zeroReplacement;
            Adotx0[i] = A[i].dotProduct(x_0);
        }
        //System.out.println("iter x: " + x.toString());
            
        for (int k=0;k<maxIter;k++){
            int i_k=k%b.length;
            double c = min(z[i_k],alpha*(b[i_k]-Adotx0[i_k]-A[i_k].dotProduct(delta_x))/Anorms[i_k]);
        /*    System.out.println("dotProduct x A: " + x.dotProduct(A[i_k]) );
            System.out.println("dotProduct A A: " + A[i_k].dotProduct(A[i_k]) );
            System.out.println("lalala: " + (b[i_k]-x.dotProduct(A[i_k]))/A[i_k].dotProduct(A[i_k]));
            System.out.println("alpha*lalala: " + alpha*(b[i_k]-x.dotProduct(A[i_k]))/A[i_k].dotProduct(A[i_k]));
            System.out.println("z: "+z[i_k]);
            System.out.println("iter c: "+ c);*/
            RealVector r = A[i_k].mapMultiply(c);
            delta_x=r.add(delta_x);
            for (int i=0;i<z.length;i++){
                if (i==i_k)
                    z[i]-=c;
            }
        //    System.out.println("iter x: " + x.toString());
            
        }
        
        return delta_x.add(x_0);
    }
    double min(double a, double b){
        return a<b?a:b;
    }
}