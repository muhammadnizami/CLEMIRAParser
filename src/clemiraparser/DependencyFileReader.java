/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author nizami
 */
public class DependencyFileReader {
    Scanner sc;
    public DependencyFileReader(File f) throws FileNotFoundException{
        
        sc = new Scanner(f);
    }
    
    public DependencyFileReader(Scanner sc){
        this.sc= sc;
    }
    
    public DependencyInstance nextInstance(){
        String inputLine = sc.nextLine();
        String depStr = "";
        while (inputLine.length() > 0) {
            depStr+=inputLine+="\n";
            inputLine = sc.nextLine();
        }
        
        return new DependencyInstance(depStr);
    }
    
    public boolean hasNextInstance(){
        return sc.hasNextLine();
    }
    
    public List<DependencyInstance> loadAll(){
        
        List<DependencyInstance> instances = new LinkedList<>();
        while (hasNextInstance()){
            DependencyInstance i = nextInstance();
            instances.add(i);
        }
        return instances;
    }
}
