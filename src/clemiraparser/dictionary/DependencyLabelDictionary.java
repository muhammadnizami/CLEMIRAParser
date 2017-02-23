/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.dictionary;

import clemiraparser.DependencyInstance;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author nizami
 */
abstract class DependencyLabelDictionary extends SerializableDictionary{

    Map<Integer, String> intToLabelMap;
    int knownLabelSize;
    Map<String, Integer> knownLabels;
    
    public DependencyLabelDictionary(){
        super();
        knownLabels = new HashMap<>();
        intToLabelMap = new HashMap<>();
        knownLabelSize = 0;
    }

    public String label(int labelNum) {
        return labelNum>=0?intToLabelMap.get(labelNum):null;
    }

    public int labelNum(String lab) {
        return knownLabels.get(lab);
    }
    
    public int [] labels(DependencyInstance instance){
        int [] labels = new int[instance.getLength()+1];
        for (int i=1;i<=instance.getLength();i++){
            labels[i] = labelNum(instance.getDep_type()[i]);
        }
        return labels;
    }

    public int[] labels(DependencyInstance instance, int i) {
        List<Integer> labs = new LinkedList<>();
        for (int k = 0; k <= instance.getLength(); k++) {
            if (instance.getDep()[k] == i) {
                labs.add(knownLabels.get(instance.getDep_type()[k]));
            }
        }
        int[] retval = new int[labs.size()];
        for (int j = 0; j < labs.size(); j++) {
            retval[j] = labs.get(j);
        }
        return retval;
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
    protected void initDictionary() {
        knownLabels = new HashMap<>();
        intToLabelMap = new HashMap<>();
        knownLabelSize = 0;
    }
    
}
