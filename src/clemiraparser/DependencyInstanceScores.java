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
public class DependencyInstanceScores extends DependencyInstance{
    
    private double [] dep_score;
    private double [] dep_type_score;
    
    public DependencyInstanceScores(int length, String[] word, String[] pos, int[] dep, String[] dep_type) {
        super(length, word, pos, dep, dep_type);
    }

    public DependencyInstanceScores(DependencyInstance instance) {
        super(instance.getLength(), instance.getWord(), instance.getPos(), instance.getDep(), instance.getDep_type());
    }

    /**
     * @return the dep_score
     */
    public double[] getDep_score() {
        return dep_score;
    }

    /**
     * @param dep_score the dep_score to set
     */
    public void setDep_score(double[] dep_score) {
        this.dep_score = dep_score;
    }

    /**
     * @return the dep_type_score
     */
    public double[] getDep_type_score() {
        return dep_type_score;
    }

    /**
     * @param dep_type_score the dep_type_score to set
     */
    public void setDep_type_score(double[] dep_type_score) {
        this.dep_type_score = dep_type_score;
    }
    
    @Override
    public String toString(){
        String str = "";
        for (int i=1;i<=getLength();i++){
            str+=i+"\t";
            str+=getWord()[i]+"\t_\t";
            str+=getPos()[i]+"\t_\t_\t";
            str+=getDep()[i]+"\t";
            str+=getDep_type()[i]+"\t";
            str+=getDep_score()[i]+"\t";
            str+=getDep_type_score()[i]+"\n";
        }
        return str;
    }
    
}
