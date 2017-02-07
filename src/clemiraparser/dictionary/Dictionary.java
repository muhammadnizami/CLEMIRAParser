/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.dictionary;

import clemiraparser.DependencyFileReader;
import clemiraparser.DependencyInstance;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author nizami
 */
public abstract class Dictionary{
    
    Map<String,Integer> featureStringMap;
    int size;
    
    public Dictionary(){
        size=0;
        featureStringMap = new HashMap<>();
    }
    
    protected void add(DependencyInstance dependencyInstance){
        String [] featureStrings = featureString(dependencyInstance);
        for (String s : featureStrings){
            add(s);
        }
    }
    
    protected void add(String featureString){
        if (!featureStringMap.containsKey(featureString)){
            featureStringMap.put(featureString, size);
            size++;
        }
    }
    
    public void add(List<DependencyInstance> instances){
        for (DependencyInstance instance :instances){
            add(instance);
        }
    }
    
    public void addFromFile(String filePath) throws FileNotFoundException{
        File f = new File(filePath);
        DependencyFileReader dependencyFileReader = new DependencyFileReader(f);
        
        while (dependencyFileReader.hasNextInstance()){
            DependencyInstance dependencyInstance = dependencyFileReader.nextInstance();
            add(dependencyInstance);
        }
        
    }
    
    public int getSize(){
        return size;
    }
    
    protected abstract String[] featureString(DependencyInstance instance);

}
