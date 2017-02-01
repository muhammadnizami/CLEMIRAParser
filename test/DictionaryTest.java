
import clemiraparser.DependencyFileReader;
import clemiraparser.DependencyInstance;
import clemiraparser.dictionary.Dictionary;
import java.io.File;
import java.io.FileNotFoundException;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author nizami
 */
public class DictionaryTest {
    @Test
    public void testCreate() throws FileNotFoundException{
        Dictionary d = Dictionary.createDictionary("id-ud-train.conllu");
        System.out.println("size: "+d.getSize());
        
        DependencyFileReader reader = new DependencyFileReader(new File("id-ud-train.conllu"));
        DependencyInstance dependencyInstance = reader.nextInstance();
        
        System.out.println(d.featureVector(dependencyInstance, 1, 0));
    }
}
