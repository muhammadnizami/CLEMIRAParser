/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.dictionary;

import clemiraparser.DependencyInstance;
import clemiraparser.labeling.DependencyLabelsFeatureVectors;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class DependencyLabelingDictionary extends MSTParserUnlabeledDependencyDictionary{
    public static final long serialVersionUID = 0L;
    
    Map<String,Integer> knownLabels;
    Map<Integer,String> intToLabelMap;
    int knownLabelSize;
    
    public DependencyLabelingDictionary(){
        super();
        knownLabels = new HashMap<>();
        intToLabelMap = new HashMap<>();
        knownLabelSize = 0;
    }
    
    @Override
    protected void add(DependencyInstance instance){
        super.add(instance);
        for (int i=0;i<=instance.getLength();i++){
            String lab = instance.getDep_type()[i];
            if (!knownLabels.containsKey(lab)){
                knownLabels.put(lab, knownLabelSize);
                intToLabelMap.put(knownLabelSize, lab);
                knownLabelSize++;
            }
        }
    }

    @Override
    protected String[] featureString(DependencyInstance instance) {
        List<String> r = new LinkedList<>();
        for (int i=1;i<instance.getLength();i++){
            //search left sibling
            int j;
            for (j=i-1;j>=0;j--)
                if (instance.getDep()[j]==instance.getDep()[i])
                    break;
            
            String leftLabel = j>=0?instance.getDep_type()[j]:"null";
            String [] feats = featureString(instance, i, j, instance.getDep_type()[i],leftLabel,instance.getDep()[i]);
            r.addAll(Arrays.asList(feats));
        }
        return r.toArray(new String[r.size()]);
    }
    
    protected String[] featureString(DependencyInstance instance, int c, int cleft, String lab, String lableftsib, int p){
        List<String> ret = new LinkedList<>();
//        String [] sup = super.featureString(instance, p, c);
//        String [] supleft = super.featureString(instance, p, cleft);
        /*ret.addAll(Arrays.asList(sup));
        for (String s : supleft){
            ret.add("L"+s);
        }*/
        
        String [] w = instance.getWord();
        String [] pos = instance.getPos();
        
        String pPos = pos[p];
        String cPos = pos[c];
        String clPos = cleft>=0?pos[cleft]:"null";
        String pWord = w[p];
        String cWord = w[c];
        String clWord = cleft>=0?w[cleft]:"null";
        
        //edge features
        ret.add("L:"+lab);
        ret.add("LL:"+lableftsib);
        ret.add("PP:"+pPos);
        ret.add("CP:"+cPos);
        ret.add("PP:"+pPos+",CP:"+cPos);
        ret.add("PW:"+pWord);
        ret.add("CW:"+cWord);
        ret.add("PW:"+pWord+",PP:"+pPos);
        ret.add("CW:"+cWord+",CP:"+cPos);
        ret.add("PW:"+pWord+",CP:"+cPos);
        ret.add("PP:"+pPos+",CW:"+cWord);
        
        //sibling features
        ret.add("CLPOS:"+clPos);
        ret.add("CLW:"+clWord);
        
        //context features
        if (c>p){
            for (int j=p+1;j<c;j++){
                String bPos = pos[j];
                ret.add("PP:"+pPos+",BP:"+bPos+",CP:"+cPos);
            }
        }else{
            for (int j=p-1;j>c;j--){
                String bPos = pos[j];
                ret.add("PP:"+pPos+",BP:"+bPos+",CP:"+cPos);
            }
        }
        
        //nonlocal features
        //child of child
        int numChildOfChild = 0;
        for (int j=0;j<=instance.getLength();j++){
            if (instance.getDep()[j]==c)
                numChildOfChild++;
        }
        ret.add("NCC:"+numChildOfChild);
        //parent of parent
        int parentOfParent = instance.getDep()[p];
        ret.add("PPP:"+(parentOfParent>0?pos[parentOfParent]:"null"));
        ret.add("PPW:"+(parentOfParent>0?w[parentOfParent]:"null"));
        
        //conjunctions
        //TODO
                
        return ret.toArray(new String[ret.size()]);
    }
    
    protected RealVector featureVector(DependencyInstance instance, int c, int cleft, String lab, String lableftsib, int p){
        String [] featureStrings = this.featureString(instance, c, cleft, lab, lableftsib, p);
        
        return featureVector(featureStrings);
    }
    
    public DependencyLabelsFeatureVectors featureVectors (DependencyInstance instance, int i){
        
        int j = 1;
        
        //searching for first child
        boolean firstChildFound = false;
        while (j<=instance.getLength() && !firstChildFound){
            if (instance.getDep()[j]==i)
                firstChildFound=true;
            else
                j++;
        }
        
        if (firstChildFound){
            int firstChild = j;
            
            DependencyLabelsFeatureVectors retval = new DependencyLabelsFeatureVectors(this.knownLabelSize, getSize());
            RealVector [] initial = new RealVector[knownLabelSize];
            for (int k=0;k<knownLabelSize;k++){
                initial[k] = featureVector(instance,firstChild,-1,intToLabelMap.get(k),"null",i);
            }
            
            retval.setInitialFeatureVectors(initial);
            
            int prev = firstChild;
            int cur = prev+1;
            while(cur<=instance.getLength()){
                if (instance.getDep()[cur]==i){
                    RealVector[][] transition = new RealVector[knownLabelSize][knownLabelSize];
                    
                    for (int k=0;k<knownLabelSize;k++){
                        String prevLab = intToLabelMap.get(k);
                        for (int l=0;l<knownLabelSize;l++){
                            String curLab = intToLabelMap.get(l);
                            
                            transition[k][l] = featureVector(instance,cur,prev,curLab,prevLab,i);
                        }
                    }
                    
                    retval.grow(transition);
                    prev=cur;
                }
                cur++;
            }
            
            return retval;
        }else{
            return null;
        }
        
    }
    
    public int[] labels(DependencyInstance instance, int i){
        
        List<Integer> labs = new LinkedList<>();
        for (int k=0;k<=instance.getLength();k++){
            if (instance.getDep()[k]==i)
               labs.add(knownLabels.get(instance.getDep_type()[k]));
        }
        int [] retval = new int[labs.size()];
        for (int j=0;j<labs.size();j++){
            retval[j]=labs.get(j);
        }
        return retval;
    }
    
    public String label(int labelNum){
        return intToLabelMap.get(labelNum);
    }
    
    public int labelNum(String lab){
        return knownLabels.get(lab);
    }

    @Override
    protected void initDictionary() {
        knownLabels = new HashMap<>();
        intToLabelMap = new HashMap<>();
        knownLabelSize = 0;
    }
    
}
