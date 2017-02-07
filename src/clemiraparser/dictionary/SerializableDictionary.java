/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.dictionary;

import clemiraparser.DependencyInstance;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author nizami
 */
public abstract class SerializableDictionary extends Dictionary implements java.io.Serializable {
    public static final long serialVersionUID = 0L;
    List<DependencyInstance> buildInstance;
    
    public SerializableDictionary(){
        super();
        this.buildInstance = new LinkedList<>();
    }
    
    @Override
    protected void add(DependencyInstance instance){
        buildInstance.add(instance);
        super.add(instance);
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.writeObject(buildInstance);
    }
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        List<DependencyInstance> readObject = (List<DependencyInstance>) in.readObject();
        buildInstance = new LinkedList<>();
        for (DependencyInstance instance : readObject){
            add(instance);
        }
    }
}
