/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.quadraticprogramming;

import clemiraparser.util.MySparseVector;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 * the Optimization for HildrethSolver
 * @author nizami
 */
public class HildrethSolver2 extends HildrethSolver {
    
    
    /**
     * @param x_0
     * @param A
     * @param b
     * @return x* described in Jamil(2014)
     * */
    public RealVector solve(RealVector x_0, RealVector[] A, double[] b){
        
        if (A.length != b.length)
            throw new DimensionMismatchException(A.length,b.length);
        
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
        
        RealVector [] AATranspose = new RealVector[A.length];
        for (int i=0;i<A.length;i++){
            AATranspose[i] = new ArrayRealVector(A.length);
            for (int j=0;j<A.length;j++){
                AATranspose[i].setEntry(j, A[j].dotProduct(A[i]));
            }
        }
        RealVector d = new ArrayRealVector(A.length);
            
        for (int k=0;k<maxIter;k++){
            int i_k=k%b.length;
            double c = min(z[i_k],alpha*(-b[i_k]+Adotx0[i_k]+d.dotProduct(AATranspose[i_k]))/Anorms[i_k]);
            d.addToEntry(i_k, -c);
            for (int i=0;i<z.length;i++){
                if (i==i_k)
                    z[i]-=c;
            }
            
        }
        
        //calculating x
        RealVector delta_x = new MySparseVector(x_0.getDimension());
        for (int i=0;i<A.length;i++){
            delta_x = delta_x.add(A[i].mapMultiply(d.getEntry(i)));
        }
        
        return delta_x.add(x_0);
    }
}
