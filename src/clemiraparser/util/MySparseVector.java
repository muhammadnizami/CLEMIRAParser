/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class MySparseVector extends RealVector implements java.io.Serializable{
    public static final long serialVersionUID = 0L;
    HashMap<Integer,Double> elMap = new HashMap<>();
    int dimension;
    
    public MySparseVector(int dimension){
        this.dimension = dimension;
    }
    
    public int numNonZeroElements(){
        return elMap.size();
    }
    
    @Override
    public RealVector mapMultiply(double d){
        RealVector retval = new MySparseVector(this.getDimension());
        
        for (Map.Entry<Integer,Double> e : elMap.entrySet()){
            retval.setEntry(e.getKey(),e.getValue()*d);
        }
        
        return retval;
    }
    
    @Override
    public RealVector mapDivide(double d){
        RealVector retval = new MySparseVector(this.getDimension());
        
        for (Map.Entry<Integer,Double> e : elMap.entrySet()){
            retval.setEntry(e.getKey(),e.getValue()/d);
        }
        
        return retval;
    }
    
    @Override
    public RealVector subtract(RealVector rv){
        if (ArraySparseBinaryVector.class.isInstance(rv))
            return ((ArraySparseBinaryVector)rv).subtractFrom(this);
        RealVector nrv = rv.mapMultiply(-1);
        return add(nrv);
    }
    
    @Override
    public RealVector add(RealVector rv){
        if (ArraySparseBinaryVector.class.isInstance(rv))
            return rv.add(this);
        RealVector retval = rv.copy();
        for (Map.Entry<Integer,Double> e : elMap.entrySet()){
            retval.addToEntry(e.getKey(), e.getValue());
        }
        return retval;
    }
    
    public void addTo(RealVector rv){
        for (Map.Entry<Integer,Double> e : elMap.entrySet()){
            rv.addToEntry(e.getKey(), e.getValue());
        }
    }

    @Override
    public double dotProduct(RealVector rv){
        if (ArraySparseBinaryVector.class.isInstance(rv))
            return rv.dotProduct(this);
        double result = 0;
        for (Map.Entry<Integer,Double> e : elMap.entrySet()){
            result+=e.getValue()*rv.getEntry(e.getKey());
        }
        return result;
    }
    
    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public double getEntry(int i) throws OutOfRangeException {
        if (i>=dimension)
            throw new OutOfRangeException(i,0,dimension-1);
        return elMap.getOrDefault(i,0.0);
    }

    @Override
    public void setEntry(int i, double d) throws OutOfRangeException {
        if (i>=dimension)
            throw new OutOfRangeException(i,0,dimension-1);
        
        if (d!=0){
            elMap.put(i, d);
        }else if (elMap.containsKey(i)){
            elMap.remove(i);
        }
    }

    @Override
    public RealVector append(RealVector rv) {
        int initDim = this.dimension;
        MySparseVector retval = (MySparseVector) this.copy();
        retval.dimension+=rv.getDimension();
        for (int i=0;i<rv.getDimension();i++){
            retval.setEntry(i+initDim,rv.getEntry(i));
        }
            
        return retval;
    }
    
    public RealVector append(MySparseVector rv){
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
    public RealVector copy() {
        MySparseVector copy = new MySparseVector(getDimension());
        copy.elMap = (HashMap<Integer, Double>) this.elMap.clone();
        return copy;
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
    public String toString(){
        String s = "{";
        for (Map.Entry<Integer,Double> e : elMap.entrySet()){
            s+="("+e.getKey()+":"+e.getValue()+"), ";
        }
        s = s.substring(0,s.length()-2);
        s+="}";
        return s;
    }
    
    @Override
    public double[] toArray(){
        double[] ret = new double[dimension];
        for (Map.Entry<Integer,Double> e : elMap.entrySet()){
            ret[e.getKey()] = e.getValue();
        }
        return ret;
    }
    
}