/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.dictionary;

import clemiraparser.DependencyFileReader;
import clemiraparser.DependencyInstance;
import clemiraparser.DependencyInstanceFeatureVectors;
import clemiraparser.util.MySparseVector;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class Dictionary{
    
    Map<String,Integer> featureStringMap;
    int size;
    
    public Dictionary(){
        size=0;
        featureStringMap = new HashMap<>();
    }
    
    protected void add(DependencyInstance dependencyInstance){
        //for (int i=0;i<=dependencyInstance.getLength();i++){
            for (int j=1;j<=dependencyInstance.getLength();j++){
                int i=dependencyInstance.getDep()[j]; //if you want to use only the actual tree and not all possible edges, uncomment this and comment out the i for loop
                String [] featureStrings = featureString(dependencyInstance,i,j);
                for (String s : featureStrings){
                    add(s);
                }
            }
        //}
    }
    
    protected void add(String featureString){
        if (!featureStringMap.containsKey(featureString)){
            featureStringMap.put(featureString, size);
            size++;
        }
    }
    
    public static Dictionary createDictionary(String filePath) throws FileNotFoundException{
        File f = new File(filePath);
        DependencyFileReader dependencyFileReader = new DependencyFileReader(f);
        Dictionary dictionary = new Dictionary();
        
        while (dependencyFileReader.hasNextInstance()){
            DependencyInstance dependencyInstance = dependencyFileReader.nextInstance();
            dictionary.add(dependencyInstance);
        }
        
        return dictionary;
    }
    
    public int getSize(){
        return size;
    }
    
    public String [] featureString(DependencyInstance instance, int pid, int cid){
        List<String> ret = new LinkedList<>();
        
        String pWord = instance.getWord()[pid];
        String pPos = instance.getPos()[pid];
        String cWord = instance.getWord()[cid];
        String cPos = instance.getPos()[cid];
        
        //basic unigram features
        ret.add("PP:"+pWord+",PP:"+pPos);
        ret.add("PW:"+pWord);
        ret.add("PP:"+pPos);
        ret.add("CW:"+cWord+",CP:"+cPos);
        ret.add("CW:"+cWord);
        ret.add("CP:"+cPos);
        
        //basic bigram features
        ret.add("PW:"+pWord+",PP:"+pPos+",CW:"+cWord+",CP:"+cPos);
        ret.add("PP:"+pPos+",CW:"+cWord+",CP:"+cPos);
        ret.add("PW:"+pWord+",CW:"+cWord+",CP:"+cPos);
        ret.add("PW:"+pWord+",PP:"+pPos+",CP:"+cPos);
        ret.add("PW:"+pWord+",PP:"+pPos+",CW:"+cWord);
        ret.add("PW:"+pWord+",CW:"+cWord);
        ret.add("PP:"+pPos+",CP:"+cPos);
        
        //in between POS Features
        if (cid<pid){
            for (int i=cid+1;i<pid;i++){
                String bPos = instance.getPos()[i];
                ret.add("PP:"+pPos+",BP:"+bPos+",CP:"+cPos);
            }
        }else{
            for (int i=pid+1;i<cid;i++){
                String bPos = instance.getPos()[i];
                ret.add("PP:"+pPos+",BP:"+bPos+",CP:"+cPos);
            }
        }
        
        //surrounding word POS features
        String pPosNext=pid<instance.getLength()?instance.getPos()[pid+1]:"<END>";
        String cPosNext=cid<instance.getLength()?instance.getPos()[cid+1]:"<END>";
        String pPosPrev=pid>0?instance.getPos()[pid-1]:"<START>";
        String cPosPrev=cid>0?instance.getPos()[cid-1]:"<START>";
        ret.add("PP:"+pPos+",PP+1:"+pPosNext+",CP-1:"+cPosPrev+",CP:"+cPos);
        ret.add("PP-1:"+pPosPrev+",PP:"+pPos+",CP-1:"+cPosPrev+",CP:"+cPos);
        ret.add("PP:"+pPos+",PP+1:"+pPosNext+",CP:"+cPos+",CP+1:"+cPosNext);
        ret.add("PP-1:"+pPosPrev+",PP:"+pPos+",CP:"+cPos+",CP+1:"+cPosNext);
        
        
        return ret.toArray(new String[ret.size()]);
    }
    
    public RealVector featureVector(DependencyInstance instance, int pid, int cid){
        String featureStrings[] = featureString(instance, pid, cid);
        
        MySparseVector r = new MySparseVector(getSize());
        RealVector retval = r;
        
        for (String s : featureStrings){
            if (featureStringMap.containsKey(s)){
                int index = featureStringMap.get(s);
                retval.setEntry(index,1.0d);
            }
        }
        return retval;
    }
    
    public DependencyInstanceFeatureVectors featureVectors(DependencyInstance instance){
        DependencyInstanceFeatureVectors featureVectors = new DependencyInstanceFeatureVectors(instance.getLength(),size);
        
        for (int i=0;i<=instance.getLength();i++){
            for (int j=1;j<=instance.getLength();j++){
                RealVector featureVector = this.featureVector(instance, i, j);
                featureVectors.setEdgeFeatureVector(i, j, featureVector);
            }
        }
        
        return featureVectors;
    }

}
