/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.dictionary;

import clemiraparser.DependencyInstance;
import clemiraparser.labeling.simple.DependencyLabelsFeatureVectors;
import clemiraparser.util.ArraySparseBinaryVector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class SimpleDependencyLabelingDictionary extends DependencyLabelDictionary{
public static final long serialVersionUID = 0L;
    
    
    public SimpleDependencyLabelingDictionary(){
        super();
    }

    @Override
    protected String[] featureString(DependencyInstance instance) {
        List<String> r = new LinkedList<>();
        for (int i=1;i<instance.getLength();i++){

            String [] feats = featureString(instance, i, instance.getDep_type()[i]);
            r.addAll(Arrays.asList(feats));
        }
        return r.toArray(new String[r.size()]);
    }
    
    private static final MSTParserUnlabeledDependencyDictionary mstparserunlabeled = new MSTParserUnlabeledDependencyDictionary();   
    protected String[] featureString(DependencyInstance instance, int c, String lab){
        int p = instance.getDep()[c];
        String [] toks = instance.getWord();
        String [] pos = instance.getPos();
        String[] posA = new String[pos.length];
        boolean attR = c >= p;
        String [] a = mstparserunlabeled.featureString(instance,p,c);
        List<String> ret = new ArrayList<>();
        ret.addAll(Arrays.asList(a));
        posA[c] = pos[c].substring(0,1);
        List<String> tmp1 = MSTParserFeatureString(toks,pos,posA,c,lab,attR,true);
        List<String> tmp2 = MSTParserFeatureString(toks,pos,posA,p,lab,attR,false);
        ret.addAll(tmp1);
        ret.addAll(tmp2);
                
        return ret.toArray(new String[ret.size()]);
    }
    
    protected List<String> MSTParserFeatureString(String[] toks,
					     String[] pos,
					     String[] posA,
					     int word,
					     String type,
					     boolean attR,
					     boolean childFeatures) {
        String att = "";
	if(attR)
	    att = "RA";
	else
	    att = "LA";

	att+="&"+childFeatures;
		
	String w = toks[word];
	String wP = pos[word];

	String wPm1 = word > 0 ? pos[word-1] : "STR";
	String wPp1 = word < pos.length-1 ? pos[word+1] : "END";
        
        List<String> r = new ArrayList<>();

	r.add("NTS1="+type+"&"+att);
	r.add("ANTS1="+type);
	for(int i = 0; i < 2; i++) {
	    String suff = i < 1 ? "&"+att : "";
	    suff = "&"+type+suff;

	    r.add("NTH="+w+" "+wP+suff);
	    r.add("NTI="+wP+suff);
	    r.add("NTIA="+wPm1+" "+wP+suff);
	    r.add("NTIB="+wP+" "+wPp1+suff);
	    r.add("NTIC="+wPm1+" "+wP+" "+wPp1+suff);
	    r.add("NTJ="+w+suff); //this
			
	}
        return r;
    }
    
    protected ArraySparseBinaryVector featureVector(DependencyInstance instance, int c, String lab){
        String [] featureStrings = this.featureString(instance, c, lab);
        
        return featureVector(featureStrings);
    }
    
    protected ArraySparseBinaryVector featureVector(DependencyInstance instance, int c, int l){
        String lab = intToLabelMap.get(l);
        return featureVector(instance, c, lab);
    }
    
    public DependencyLabelsFeatureVectors featureVectors (DependencyInstance instance){
        DependencyLabelsFeatureVectors featureVectors = new DependencyLabelsFeatureVectors(instance.getLength(),knownLabelSize,getSize());
        
        for (int i=1;i<=instance.getLength();i++){
            for (int l=0;l<knownLabelSize;l++){
                ArraySparseBinaryVector v = featureVector(instance,i,l);
                featureVectors.setLabelFeatureVector(i, l, v);
            }
        }
        
        return featureVectors;
        
    }
    
}
