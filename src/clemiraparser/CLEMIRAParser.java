/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser;

import clemiraparser.unlabeled.UnlabeledParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.DimensionMismatchException;

/**
 *
 * @author nizami
 */
public abstract class CLEMIRAParser implements java.io.Serializable{
    public static final long serialVersionUID = 0L;

    public static String trainfile = null;
    public static String testfile = null;
    public static boolean train = false;
    public static boolean eval = false;
    public static boolean test = false;
    public static String modelName = "dep.model";
    public static String lossFunction = "mcdonaldhamming";
    public static String chooser = "kbest";
    public static String constraint = "original";
    public static String stages = "two";
    public static int numIters = 10;
    public static String outfile = "out.conllu";
    public static String goldfile = null;
    public static int trainK = 1;
    public static int trainL = 1;
    public static int testK = 1;
    public static double trainAlpha = 3.0d;
    public static double trainLambda = 2.0d;
    
    public static CLEMIRAParser parser(){
        if (stages.equals("two")){
            return new TwoStageParser();
        }else if (stages.equals("one")){
            return new OneStageParser();
        }else{
            throw new IllegalArgumentException("Unknown stage " + stages);
        }
    }
        
    abstract public void train(List<DependencyInstance> instances) throws Exception;
    abstract public void optimizeForSerialization();
    
    public List<DependencyInstance> test(List<DependencyInstance> instances){
        List<DependencyInstance> preds = new ArrayList<>(instances.size());
        int count = 0;
        for (DependencyInstance instance : instances){
            DependencyInstance pred = parse(instance);
            preds.add(pred);
            count++;
            System.out.print(" " + count);
        }
        System.out.println();
        return preds;
    }

    /**
     *
     * @param out
     * @param gold
     */
    public static void eval(List<DependencyInstance> out, List<DependencyInstance> gold){
        if (out.size()!=gold.size())
            throw new DimensionMismatchException(out.size(),gold.size());
        int labeledAttachmentCorrects = 0;
        int labeledAttachmentIncorrects = 0;
        int labeledCompleteCorrects = 0;
        int unlabeledAttachmentCorrects = 0;
        int unlabeledAttachmentIncorrects = 0;
        int unlabeledCompleteCorrects = 0;
        for (int i=0;i<out.size();i++){
            DependencyInstance pred = out.get(i);
            DependencyInstance act = gold.get(i);
            if (pred.getLength() != act.getLength())
                throw new DimensionMismatchException(out.size(),gold.size());
            
            int length = pred.getLength();
            boolean unlabeledCompleteCorrect = true;
            boolean labeledCompleteCorrect = true;
            for (int j=1;j<=pred.getLength();j++){
                if (pred.getDep()[j]==act.getDep()[j]){
                    unlabeledAttachmentCorrects++;
                    if (pred.getDep_type()[j].equals(act.getDep_type()[j])){
                        labeledAttachmentCorrects++;
                    }else{
                        labeledAttachmentIncorrects++;
                        labeledCompleteCorrect = false;
                    }
                }else{
                    unlabeledAttachmentIncorrects++;
                    labeledAttachmentIncorrects++;
                    unlabeledCompleteCorrect = false;
                    labeledCompleteCorrect = false;
                }
            }
            if (unlabeledCompleteCorrect)
                unlabeledCompleteCorrects++;
            if (labeledCompleteCorrect)
                labeledCompleteCorrects++;
        }
        double UAS = ((double)unlabeledAttachmentCorrects)/(unlabeledAttachmentIncorrects+unlabeledAttachmentCorrects);
        double unlabeledCompleteCorrect = ((double)unlabeledCompleteCorrects)/out.size();
        double LAS = ((double)labeledAttachmentCorrects)/(labeledAttachmentIncorrects+labeledAttachmentCorrects);
        double labeledCompleteCorrect = ((double)labeledCompleteCorrects)/out.size();
        
        System.out.println("unlabeled attachment corrects: " + unlabeledAttachmentCorrects);
        System.out.println("unlabeled attachment incorrects: " + unlabeledAttachmentIncorrects);
        System.out.println("labeled attachment corrects: " + labeledAttachmentCorrects);
        System.out.println("labeled attachment incorrects: " + labeledAttachmentCorrects);
        System.out.println("UAS: " + UAS);
        System.out.println("LAS: " + LAS);
        System.out.println("unlabeled complete correct: " + unlabeledCompleteCorrect);
        System.out.println("labeled complete correct: " + labeledCompleteCorrect);
    }
    abstract public DependencyInstance parse(DependencyInstance instance);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, Exception {
        // TODO code application logic here
        
        processArguments(args);
        
        if (train){
            System.out.println("===========\n== TRAIN ==\n===========");
            
            System.out.print("reading train file...");
            DependencyFileReader dependencyFileReader = new DependencyFileReader(new File(trainfile));
            List<DependencyInstance> instances = dependencyFileReader.loadAll();
            System.out.println("done");
            
            CLEMIRAParser parser = parser();
            parser.train(instances);
            
            
            System.out.print("saving the model...");
            parser.optimizeForSerialization();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(modelName));
            out.writeObject(parser);
            out.close();
            System.out.print("done");
        }
        if (test){
            System.out.println("==========\n== TEST ==\n==========");
            
            System.out.print("reading test file...");
            DependencyFileReader dependencyFileReader = new DependencyFileReader(new File(testfile));
            List<DependencyInstance> instances = dependencyFileReader.loadAll();
            System.out.println("done");
            
            System.out.print("loading the model...");
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(modelName));
            CLEMIRAParser parser = (CLEMIRAParser) in.readObject();
            in.close();
            System.out.println("done");
            
            System.out.print("parsing...");
            List<DependencyInstance> out = parser.test(instances);
            System.out.println("done");
            
            System.out.print("saving the output...");
            PrintStream outStream = new PrintStream(new File(outfile));
            for (DependencyInstance instance : out){
                outStream.println(instance);
            }
            System.out.println("done");
        }
        if (eval){
            System.out.println("==========\n== EVAL ==\n==========");
            System.out.print("reading gold file...");
            DependencyFileReader dependencyFileReader = new DependencyFileReader(new File(goldfile));
            List<DependencyInstance> gold = dependencyFileReader.loadAll();
            System.out.println("done");
            System.out.print("reading out file...");
            DependencyFileReader dependencyFileReader1 = new DependencyFileReader(new File(outfile));
            List<DependencyInstance> out = dependencyFileReader1.loadAll();
            System.out.println("done");
            
            System.out.println("Evaluating");
            eval(out,gold);
        }
    }
    
    public static void processArguments(String[] args) {
	for(int i = 0; i < args.length; i++) {
	    String[] pair = args[i].split(":");
	    if(pair[0].equals("train")) {
		train = true;
	    }
	    if(pair[0].equals("eval")) {
		eval = true;
	    }
	    if(pair[0].equals("test")) {
		test = true;
	    }
	    if(pair[0].equals("iters")) {
		numIters = Integer.parseInt(pair[1]);
	    }
	    if(pair[0].equals("output-file")) {
		outfile = pair[1];
	    }
	    if(pair[0].equals("gold-file")) {
		goldfile = pair[1];
	    }
	    if(pair[0].equals("train-file")) {
		trainfile = pair[1];
	    }
	    if(pair[0].equals("test-file")) {
		testfile = pair[1];
	    }
	    if(pair[0].equals("model-name")) {
		modelName = pair[1];
	    }
	    if(pair[0].equals("training-k")) {
		trainK = Integer.parseInt(pair[1]);
	    }
	    if(pair[0].equals("training-l")) {
		trainL = Integer.parseInt(pair[1]);
	    }
	    if(pair[0].equals("loss-function")) {
		lossFunction = pair[1];
	    }
            if(pair[0].equals("chooser")){
                chooser = pair[1];
            }
            if(pair[0].equals("constraint")){
                constraint = pair[1];
            }
            if(pair[0].equals("training-alpha")){
                trainAlpha = Double.parseDouble(pair[1]);
            }
            if(pair[0].equals("training-lambda")){
                trainLambda = Double.parseDouble(pair[1]);
            }
            if(pair[0].equals("stages")){
                stages = pair[1];
            }
	}
	
	System.out.println("------\nFLAGS\n------");
	System.out.println("train-file: " + trainfile);
	System.out.println("test-file: " + testfile);
	System.out.println("gold-file: " + goldfile);
	System.out.println("output-file: " + outfile);
	System.out.println("model-name: " + modelName);
	System.out.println("train: " + train);
	System.out.println("test: " + test);
	System.out.println("eval: " + eval);
	System.out.println("loss-function: " + lossFunction);
	System.out.println("chooser: " + chooser);
	System.out.println("constraint: " + constraint);
	System.out.println("training-iterations: " + numIters);
	System.out.println("training-k: " + trainK);
	System.out.println("training-l: " + trainL);
        System.out.println("training-alpha: " + trainAlpha);
        System.out.println("training-lambda: " + trainLambda);
        System.out.println("stages: " + stages);
	System.out.println("------\n");
    }
}
