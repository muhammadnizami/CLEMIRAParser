/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser;

import java.util.List;

/**
 *
 * @author nizami
 */
public class MultiModelParser extends CLEMIRAParser{
    
    CLEMIRAParser simpleSentenceParser;
    CLEMIRAParser compoundSentenceParser;
    
    public MultiModelParser(CLEMIRAParser simpleSentenceParser, CLEMIRAParser compoundSentenceParser){
        this.simpleSentenceParser=simpleSentenceParser;
        this.compoundSentenceParser=compoundSentenceParser;
    }

    @Override
    public void train(List<DependencyInstance> instances) throws Exception {
        throw new UnsupportedOperationException("Please train the individual parsers and load them when parsing."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void optimizeForSerialization() {
        throw new UnsupportedOperationException("Multi-model parser cannot be serialized."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static boolean isCompoundSentence(DependencyInstance instance){
        for (int i=1;i<=instance.getLength();i++){
            if (instance.getPos()[i].equals(conjunctionPOS)){
                return true;
            }
        }
        return false;
    }

    @Override
    public DependencyInstance parse(DependencyInstance instance) {
        if (isCompoundSentence(instance)){
            return compoundSentenceParser.parse(instance);
        }else{
            return simpleSentenceParser.parse(instance);
        }
    }

    @Override
    public DependencyInstanceScores giveScores(DependencyInstance instance) {
        if (isCompoundSentence(instance)){
            return compoundSentenceParser.giveScores(instance);
        }else{
            return simpleSentenceParser.giveScores(instance);
        }
    }
    
}
