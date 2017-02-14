/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class ArraySparseBinaryVector extends RealVector implements Serializable{
    
    public static final long serialVersionUID = 0L;

    @Override
    public double getEntry(int i) throws OutOfRangeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setEntry(int i, double d) throws OutOfRangeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RealVector append(RealVector rv) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RealVector append(double d) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RealVector getSubVector(int i, int i1) throws NotPositiveException, OutOfRangeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSubVector(int i, RealVector rv) throws OutOfRangeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isNaN() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isInfinite() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RealVector ebeDivide(RealVector rv) throws DimensionMismatchException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RealVector ebeMultiply(RealVector rv) throws DimensionMismatchException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getDimension() {
        return dimension;
    }
    
    public static class ArraySparseBinaryVectorBuilder{
        
        private TreeSet<Integer> elSet;
        private int dimension;
        
        public ArraySparseBinaryVectorBuilder(int dimension){
            this.dimension = dimension;
            elSet = new TreeSet<>();
        }

        public void setEntry(int i, double d) throws OutOfRangeException {
            if (i>=dimension)
                throw new OutOfRangeException(i,0,dimension-1);

            if (d!=0){
                if (d!=1)
                    throw new UnsupportedOperationException ("Binary vectors only supports value 0 and 1");
                elSet.add(i);
            }else if (elSet.contains(i)){
                elSet.remove(i);
            }
        }
        
        public ArraySparseBinaryVector toVector(){
            ArraySparseBinaryVector v = new ArraySparseBinaryVector(dimension);
            v.elArray = new int[elSet.size()];
            int i=0;
            for (Integer e : elSet){
                v.elArray[i]=e;
                i++;
            }
            return v;
        }
    }
    
    int[] elArray;
    int dimension;
    
    public ArraySparseBinaryVector(int dimension){
        this.dimension = dimension;
        elArray  = new int[0];
    }
    
    @Override
    public RealVector mapMultiply(double d){
        ListSparseVector retval = new ListSparseVector(this.getDimension());
        
        for (int e : elArray){
            retval.elList.add(new ListSparseVector.Entry(e,d));
        }
        
        return retval;
    }
    
    @Override
    public ListSparseVector mapDivide(double d){
        ListSparseVector retval = new ListSparseVector(this.getDimension());
                
        for (int e : elArray){
            retval.elList.add(new ListSparseVector.Entry(e,1.0/d));
        }
        
        return retval;
    }
    
    @Override
    public RealVector subtract(RealVector rv){
        return toMySparseVector().subtract(rv);
    }
    
    public ListSparseVector toListSparseVector(){
        ListSparseVector lsv = new ListSparseVector(getDimension());
        for (int i : elArray){
            lsv.elList.add(new ListSparseVector.Entry(i,1.0));
        }
        return lsv;
    }
    
    public MySparseVector toMySparseVector(){
        MySparseVector msv = new MySparseVector(getDimension());
        for (int i : elArray){
            msv.setEntry(i, 1);
        }
        return msv;
    }
    
    @Override
    public RealVector add(RealVector rv){
        return toMySparseVector().add(rv);
    }
    
    @Override
    public double dotProduct(RealVector rv){
        return toMySparseVector().dotProduct(rv);
    }

    public ArraySparseBinaryVector copy() {
        ArraySparseBinaryVector r = new ArraySparseBinaryVector(getDimension());
        r.elArray = new int[elArray.length];
        for (int i=0;i<elArray.length;i++){
            r.elArray[i]=elArray[i];
        }
        return r;
    }

    @Override
    public String toString(){
        return toMySparseVector().toString();
    }
    
    public double[] toArray(){
        return toMySparseVector().toArray();
    }
    
}
