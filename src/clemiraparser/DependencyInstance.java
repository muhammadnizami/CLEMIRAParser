/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser;

/**
 *
 * @author nizami
 */
public class DependencyInstance implements java.io.Serializable {
    public static final long serialVersionUID = -145317420504696715L;
    private int length;
    private String[] word;
    private String[] pos;
    private int[] dep;
    private String[] dep_type;
    
    public DependencyInstance(String CoNLLUDepStr){
        String [] toks = CoNLLUDepStr.split("\n");
        length = toks.length;
        word = new String[length+1];
        pos = new String[length+1];
        dep = new int[length+1];
        dep_type = new String[length+1];
        for (String tok : toks){
            String [] fields = tok.split("\t");
            int id = Integer.parseInt(fields[0]);
            word[id] = fields[1];
            pos[id] = fields[3];
            dep[id] = Integer.parseInt(fields[6]);
            dep_type[id] = fields[7];
        }
        word[0]="<root>";
        pos[0]="<ROOT>";
        dep[0]=-1;
    }
    
    public DependencyInstance(int length, String [] word, String [] pos, int [] dep, String[] dep_type){
        this.length = length;
        this.word = word;
        this.pos = pos;
        this.dep = dep;
        this.dep_type = dep_type;
    }
    
    public DependencyInstance(int length, String [] word, String [] pos, int [] dep){
        this.length = length;
        this.word = word;
        this.pos = pos;
        this.dep = dep;
        this.dep_type = new String[length+1];
        for (int i=0;i<=length;i++){
            dep_type[i]="_";
        }
    }
    
    public int[] getDep(){
        return dep;
    }
    
    public String toString(){
        String str = "";
        for (int i=1;i<=getLength();i++){
            str+=i+"\t";
            str+=getWord()[i]+"\t_\t";
            str+=getPos()[i]+"\t_\t_\t";
            str+=getDep()[i]+"\t";
            str+=getDep_type()[i]+"\t_\t_\n";
        }
        return str;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @return the word
     */
    public String[] getWord() {
        return word;
    }

    /**
     * @return the pos
     */
    public String[] getPos() {
        return pos;
    }

    /**
     * @return the dep_type
     */
    public String[] getDep_type() {
        return dep_type;
    }
}
