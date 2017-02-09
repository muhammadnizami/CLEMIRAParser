/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.dictionary;

import clemiraparser.DependencyInstance;
import java.util.LinkedList;
import java.util.List;

/**
 * the feature vector copied and slightly modified from MSTParser
 * @author nizami
 */
public class MSTParserUnlabeledDependencyDictionary extends UnlabeledDependencyDictionary{
    
    public String [] featureString(DependencyInstance instance, int pid, int cid){
           
        int small = cid < pid ? cid : pid;
        int large = cid > pid ? cid : pid;
        boolean attR = cid < pid ? false : true;
        
        String [] pos = instance.getPos();
        String [] toks = instance.getWord();
        List<String> ret = new LinkedList<>();
        
	String att = "";
	if(attR)
	    att = "RA";
	else
	    att = "LA";
		
	int dist = Math.abs(large-small);
	String distBool = "0";
	if(dist > 1)
	    distBool = "1";
	if(dist > 2)
	    distBool = "2";
	if(dist > 3)
	    distBool = "3";
	if(dist > 4)
	    distBool = "4";
	if(dist > 5)
	    distBool = "5";
	if(dist > 10)
	    distBool = "10";
		
	String attDist = "&"+att+"&"+distBool;

	String pLeft = small > 0 ? pos[small-1] : "STR";
	String pRight = large < pos.length-1 ? pos[large+1] : "END";
	String pLeftRight = small < large-1 ? pos[small+1] : "MID";
	String pRightLeft = large > small+1 ? pos[large-1] : "MID";
		
        
	// feature posR posMid posL
	for(int i = small+1; i < large; i++) {
	    String allPos = pos[small]+" "+pos[i]+" "+pos[large];
	    ret.add("PC="+allPos+attDist);
	    ret.add("1PC="+allPos);
	}

	// feature posL-1 posL posR posR+1
	ret.add("PT="+pLeft+" "+pos[small]+" "+pos[large]+" "+pRight+attDist);
	ret.add("PT1="+pos[small]+" "+pos[large]+" " +pRight+attDist);
	ret.add("PT2="+pLeft+" "+pos[small]+" "+pos[large]+attDist);
	ret.add("PT3="+pLeft+" "+pos[large]+" "+pRight+attDist);
	ret.add("PT4="+pLeft+" "+pos[small]+" "+pRight+attDist);
		
	ret.add("1PT="+pLeft+" "+pos[small]+" "+pos[large]+" "+pRight);
	ret.add("1PT1="+pos[small]+" "+pos[large]+" " +pRight);
	ret.add("1PT2="+pLeft+" "+pos[small]+" "+pos[large]);
	ret.add("1PT3="+pLeft+" "+pos[large]+" "+pRight);
	ret.add("1PT4="+pLeft+" "+pos[small]+" "+pRight);
		
	// feature posL posL+1 posR-1 posR
	ret.add("APT="+pos[small]+" "+pLeftRight+" "
		 +pRightLeft+" "+pos[large]+attDist);
	ret.add("APT1="+pos[small]+" "+pRightLeft+" "+pos[large]+attDist);
	ret.add("APT2="+pos[small]+" "+pLeftRight+" "+pos[large]+attDist);
	ret.add("APT3="+pLeftRight+" "+pRightLeft+" "+pos[large]+attDist);
	ret.add("APT4="+pos[small]+" "+pLeftRight+" "+pRightLeft+attDist);

	ret.add("1APT="+pos[small]+" "+pLeftRight+" "
		 +pRightLeft+" "+pos[large]);
	ret.add("1APT1="+pos[small]+" "+pRightLeft+" "+pos[large]);
	ret.add("1APT2="+pos[small]+" "+pLeftRight+" "+pos[large]);
	ret.add("1APT3="+pLeftRight+" "+pRightLeft+" "+pos[large]);
	ret.add("1APT4="+pos[small]+" "+pLeftRight+" "+pRightLeft);
		
	// feature posL-1 posL posR-1 posR
	// feature posL posL+1 posR posR+1
	ret.add("BPT="+pLeft+" "+pos[small]+" "+pRightLeft+" "+pos[large]+attDist);
	ret.add("1BPT="+pLeft+" "+pos[small]+" "+pRightLeft+" "+pos[large]);
	ret.add("CPT="+pos[small]+" "+pLeftRight+" "+pos[large]+" "+pRight+attDist);
	ret.add("1CPT="+pos[small]+" "+pLeftRight+" "+pos[large]+" "+pRight);

	String head = attR ? toks[small] : toks[large];
	String headP = attR ? pos[small] : pos[large];
	String child = attR ? toks[large] : toks[small];
	String childP = attR ? pos[large] : pos[small];
		
	String all = head + " " + headP + " " + child + " " + childP;
	String hPos = headP + " " + child + " " + childP;
	String cPos = head + " " + headP + " " + childP;
	String hP = headP + " " + child;
	String cP = head + " " + childP;
	String oPos = headP + " " + childP;
	String oLex = head + " " + child;

	ret.add("A="+all+attDist); //this
	ret.add("B="+hPos+attDist);
	ret.add("C="+cPos+attDist);
	ret.add("D="+hP+attDist);
	ret.add("E="+cP+attDist);
	ret.add("F="+oLex+attDist); //this
	ret.add("G="+oPos+attDist);
	ret.add("H="+head+" "+headP+attDist);
	ret.add("I="+headP+attDist);
	ret.add("J="+head+attDist); //this
	ret.add("K="+child+" "+childP+attDist);
	ret.add("L="+childP+attDist);
	ret.add("M="+child+attDist); //this

	ret.add("AA="+all); //this
	ret.add("BB="+hPos);
	ret.add("CC="+cPos);
	ret.add("DD="+hP);
	ret.add("EE="+cP);
	ret.add("FF="+oLex); //this
	ret.add("GG="+oPos);
	ret.add("HH="+head+" "+headP);
	ret.add("II="+headP);
	ret.add("JJ="+head); //this
	ret.add("KK="+child+" "+childP);
	ret.add("LL="+childP);
	ret.add("MM="+child); //this

	if(head.length() > 5 || child.length() > 5) {
	    int hL = head.length();
	    int cL = child.length();
		    
	    head = hL > 5 ? head.substring(0,5) : head;
	    child = cL > 5 ? child.substring(0,5) : child;
		    
	    all = head + " " + headP + " " + child + " " + childP;
	    hPos = headP + " " + child + " " + childP;
	    cPos = head + " " + headP + " " + childP;
	    hP = headP + " " + child;
	    cP = head + " " + childP;
	    oPos = headP + " " + childP;
	    oLex = head + " " + child;
	
	    ret.add("SA="+all+attDist); //this
	    ret.add("SF="+oLex+attDist); //this
	    ret.add("SAA="+all); //this
	    ret.add("SFF="+oLex); //this

	    if(cL > 5) {
		ret.add("SB="+hPos+attDist);
		ret.add("SD="+hP+attDist);
		ret.add("SK="+child+" "+childP+attDist);
		ret.add("SM="+child+attDist); //this
		ret.add("SBB="+hPos);
		ret.add("SDD="+hP);
		ret.add("SKK="+child+" "+childP);
		ret.add("SMM="+child); //this
	    }
	    if(hL > 5) {
		ret.add("SC="+cPos+attDist);
		ret.add("SE="+cP+attDist);
		ret.add("SH="+head+" "+headP+attDist);
		ret.add("SJ="+head+attDist); //this
			
		ret.add("SCC="+cPos);
		ret.add("SEE="+cP);
		ret.add("SHH="+head+" "+headP);
		ret.add("SJJ="+head); //this
	    }
	}
		
	return ret.toArray(new String[ret.size()]);
		
    }
}
