/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.dictionary;

import clemiraparser.DependencyInstance;

/**
 * This is the dictionary used for one-stage parsing
 * @author nizami
 */
public class LabeledDependencyDictionary extends SerializableDictionary {

    @Override
    protected String[] featureString(DependencyInstance instance) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
