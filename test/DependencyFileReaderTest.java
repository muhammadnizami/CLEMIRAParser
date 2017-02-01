
import clemiraparser.DependencyFileReader;
import clemiraparser.DependencyInstance;
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
public class DependencyFileReaderTest {
    @Test
    public void testRead() throws FileNotFoundException{
        File f = new File("id-ud-test.conllu");
        DependencyFileReader reader = new DependencyFileReader(f);
        
        int count = 0;
        while (reader.hasNextInstance()){
            DependencyInstance i = reader.nextInstance();
            count++;
            System.out.println("count: " + count);
            System.out.println(i);
        }
    }
}
