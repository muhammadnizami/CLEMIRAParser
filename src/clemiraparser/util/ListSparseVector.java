/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class ListSparseVector extends RealVector implements Serializable{
    
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
    
    public static class ListSparseVectorBuilder{
        
        private SortedMap<Integer, Double> elMap;
        private int dimension;
        
        public ListSparseVectorBuilder(int dimension){
            this.dimension = dimension;
            elMap = new TreeMap<>();
        }

        public void setEntry(int i, double d) throws OutOfRangeException {
            if (i>=dimension)
                throw new OutOfRangeException(i,0,dimension-1);

            if (d!=0){
                elMap.put(i, d);
            }else if (elMap.containsKey(i)){
                elMap.remove(i);
            }
        }
        
        public ListSparseVector toVector(){
            ListSparseVector v = new ListSparseVector(dimension);
            for (SortedMap.Entry<Integer,Double> e : elMap.entrySet()){
                v.elList.add(new Entry(e.getKey(),e.getValue()));
            }
            return v;
        }
    }
    
    public static final class Entry implements Serializable{
        final public int index;
        final public double value;
        
        public Entry(int index, double value){
            this.index=index;
            this.value=value;
        }
        
        public Entry(Entry e){
            this.index = e.index;
            this.value = e.value;
        }
    }
    
    List<Entry> elList;
    int dimension;
    
    public ListSparseVector(int dimension){
        this.dimension = dimension;
        elList  = new ArrayList<>();
    }
    
    @Override
    public ListSparseVector mapMultiply(double d){
        ListSparseVector retval = new ListSparseVector(this.getDimension());
        
        for (Entry e : elList){
            retval.elList.add(new Entry(e.index,e.value*d));
        }
        
        return retval;
    }
    
    @Override
    public ListSparseVector mapDivide(double d){
        ListSparseVector retval = new ListSparseVector(this.getDimension());
        
        for (Entry e : elList){
            retval.elList.add(new Entry(e.index,e.value/d));
        }
        
        return retval;
    }
    
    @Override
    public RealVector subtract(RealVector rv){
        if (ListSparseVector.class.isInstance(rv))
            return subtract((ListSparseVector) rv);
        else if (ArraySparseBinaryVector.class.isInstance(rv))
            return subtract(((ArraySparseBinaryVector)rv).toListSparseVector());
        else
            throw new UnsupportedOperationException("Not Supported Yet");
    }
    
    public ListSparseVector subtract(ListSparseVector rv){
        ListSparseVector nrv = rv.mapMultiply(-1);
        return add(nrv);
    }
    
    @Override
    public RealVector add(RealVector rv){
        if (ListSparseVector.class.isInstance(rv))
            return add((ListSparseVector) rv);
        else if (ArraySparseBinaryVector.class.isInstance(rv))
            return add(((ArraySparseBinaryVector)rv).toListSparseVector());
        else
            throw new UnsupportedOperationException("Not Supported Yet");
    }
    
    public ListSparseVector add(ListSparseVector rv){
        if (rv.elList.isEmpty())
            return this.copy();
        if (elList.isEmpty())
            return rv.copy();
        
        ListSparseVector retval = new ListSparseVector(this.dimension);
        ListIterator<Entry> itthis = elList.listIterator();
        ListIterator<Entry> itrv = rv.elList.listIterator();
        Entry ethis = itthis.next();
        Entry erv = itrv.next();
        boolean stop = false;
        while (!stop){
            if (ethis.index<erv.index){
                retval.elList.add(new Entry(ethis.index,ethis.value));
                if (itthis.hasNext()){
                    ethis = itthis.next();
                }else{
                    retval.elList.add(new Entry(erv));
                    stop=true;
                }
            }else if (ethis.index>erv.index){
                retval.elList.add(new Entry(erv.index,erv.value));
                if (itrv.hasNext()){
                    erv = itrv.next();
                }else{
                    retval.elList.add(new Entry(ethis));
                    stop=true;
                }
            }else{
               retval.elList.add(new Entry(ethis.index,ethis.value+erv.value));
               if (itthis.hasNext()&&itrv.hasNext()){
                   ethis=itthis.next();
                   erv=itrv.next();
               }else{
                   stop=true;
               }
            }
        }
        while (itrv.hasNext()){
            erv = itrv.next();
            retval.elList.add(new Entry(erv));
        }
        while (itthis.hasNext()){
            ethis = itthis.next();
            retval.elList.add(new Entry(ethis));
        }
        
        return retval;
    }
    
    @Override
    public double dotProduct(RealVector rv){
        if (ListSparseVector.class.isInstance(rv))
            return dotProduct((ListSparseVector) rv);
        else if (ArraySparseBinaryVector.class.isInstance(rv))
            return dotProduct(((ArraySparseBinaryVector)rv).toListSparseVector());
        else
            throw new UnsupportedOperationException("Not Supported Yet");
    }

    public double dotProduct(ListSparseVector rv){
        double result = 0;

        if (rv.elList.isEmpty())
            return 0;
        if (elList.isEmpty())
            return 0;
        
        ListSparseVector retval = new ListSparseVector(this.dimension);
        ListIterator<Entry> itthis = elList.listIterator();
        ListIterator<Entry> itrv = rv.elList.listIterator();
        Entry ethis = itthis.next();
        Entry erv = itrv.next();
        boolean stop = false;
        while (!stop){
            if (ethis.index<erv.index){
                if (itthis.hasNext()){
                    ethis = itthis.next();
                }else{
                    stop=true;
                }
            }else if (ethis.index>erv.index){
                if (itrv.hasNext()){
                    erv = itrv.next();
                }else{
                    stop=true;
                }
            }else{
               result+=ethis.value*erv.value;
               if (itthis.hasNext()&&itrv.hasNext()){
                   ethis=itthis.next();
                   erv=itrv.next();
               }else{
                   stop=true;
               }
            }
        }
        return result;
    }
    
    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public ListSparseVector copy() {
        ListSparseVector copy = new ListSparseVector(getDimension());
        copy.elList.addAll(elList);
        return copy;
    }

    @Override
    public String toString(){
        String s = "{";
        for (Entry e : elList){
            s+="("+e.index+":"+e.value+"), ";
        }
        s = s.substring(0,s.length()-2);
        s+="}";
        return s;
    }
    
    @Override
    public double[] toArray(){
        double[] ret = new double[dimension];
        for (Entry e : elList){
            ret[e.index] = e.value;
        }
        return ret;
    }
    
}
