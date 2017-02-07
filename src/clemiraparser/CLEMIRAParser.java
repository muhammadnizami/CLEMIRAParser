/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser;

import clemiraparser.util.MySparseVector;
import clemiraparser.miraoptimizationproblem.ConstraintType;
import clemiraparser.miraoptimizationproblem.LossFunction;
import clemiraparser.miraoptimizationproblem.KBestChooser;
import clemiraparser.miraoptimizationproblem.MIRAConstraintType;
import clemiraparser.miraoptimizationproblem.McDonaldHammingLoss;
import clemiraparser.miraoptimizationproblem.Chooser;
import clemiraparser.dictionary.UnlabeledDependencyDictionary;
import clemiraparser.miraoptimizationproblem.EdgeFactorizedLoss;
import clemiraparser.miraoptimizationproblem.KBestLWorstChooser;
import clemiraparser.miraoptimizationproblem.KLossMarkedUpBestChooser;
import clemiraparser.miraoptimizationproblem.LWorstChooser;
import clemiraparser.miraoptimizationproblem.ModifiedConstraintType;
import clemiraparser.miraoptimizationproblem.RootPreferredLossFunction;
import com.google.common.collect.ImmutableMap;
import edu.cmu.cs.ark.cle.Arborescence;
import edu.cmu.cs.ark.cle.ChuLiuEdmonds;
import edu.cmu.cs.ark.cle.graph.DenseWeightedGraph;
import edu.cmu.cs.ark.cle.util.Weighted;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class CLEMIRAParser implements java.io.Serializable{
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
    public static int numIters = 10;
    public static String outfile = "out.conllu";
    public static String goldfile = null;
    public static int trainK = 1;
    public static int trainL = 1;
    public static int testK = 1;
    public static double trainAlpha = 3.0d;
    public static double trainLambda = 2.0d;
    
    public static LossFunction lossFunction() throws Exception{
        if (lossFunction.equals("mcdonaldhamming")){
            return new McDonaldHammingLoss();
        }else if (lossFunction.equals("rootpreferred")){
            return new RootPreferredLossFunction(trainLambda);
        }else{
            throw new Exception("unknown loss function " + lossFunction);
        }
    }
    public static Chooser chooser() throws Exception{
        if (chooser.equals("kbest")){
            return new KBestChooser(trainK);
        }else if (chooser.equals("lworst")){
            return new LWorstChooser(trainL);
        }else if (chooser.equals("kbestlworst")){
            return new KBestLWorstChooser(trainK,trainL);
        }else if (chooser.equals("klossmarkedupbest")){
            return new KLossMarkedUpBestChooser(trainK, (EdgeFactorizedLoss) lossFunction());
        }else{
            throw new Exception("unknown chooser " + chooser);
        }
    }
    public static ConstraintType constraint() throws Exception{
        if (constraint.equals("original")){
            return new MIRAConstraintType();
        }else if (constraint.equals("modified")){
            return new ModifiedConstraintType(trainAlpha);
        }else {
            throw new Exception("unknown constraint " + constraint);
        }
    }
    
    UnlabeledDependencyDictionary dictionary;
    Parameter parameter;
    
    public void train(UnlabeledDependencyDictionary dictionary,
            List<DependencyInstance> instances,
            LossFunction lossFunction,
            Chooser chooser,
            ConstraintType constraint,
            int numIter){
        this.dictionary = dictionary;
        parameter = new Parameter(dictionary.getSize());
        int T = instances.size();
        RealVector v = new MySparseVector(dictionary.getSize());
        
        for (int i=0;i<numIter;i++){
            
            System.out.println("========================\n" +
                "Iteration: "+i+"\n" +
                "========================");
            System.out.print("Processed:");
            int t=1;
            long start = System.currentTimeMillis();            
            for (DependencyInstance instance : instances){
                DependencyInstanceFeatureVectors instancefv = dictionary.featureVectors(instance);
                parameter.update(constraint, lossFunction, chooser, instancefv, instance.getDep());
                v = v.add(parameter.getWeightVector());
                
                t++;
                if ((t)%500==0){
                    System.out.println("\t"+(t));
                }
            }
            if (T%500!=0){
                System.out.println("\t"+T);
            }
            long end = System.currentTimeMillis();
            System.out.println("Training iter took: "+(end-start));
        }
        
        RealVector w = v.mapDivide(numIter*T);
        parameter.setWeightVector(w);
    }
    public List<DependencyInstance> test(List<DependencyInstance> instances){
        List<DependencyInstance> preds = new ArrayList<>(instances.size());
        for (DependencyInstance instance : instances){
            DependencyInstance pred = parse(instance);
            preds.add(pred);
        }
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
    public DependencyInstance parse(DependencyInstance instance){
        DependencyInstanceFeatureVectors instancefv = dictionary.featureVectors(instance);
        double[][] scoreTable = parameter.getScoreTable(instancefv);
        DenseWeightedGraph g = DenseWeightedGraph.from(scoreTable);
        Weighted<Arborescence<Integer>> maxArborescence = ChuLiuEdmonds.getMaxArborescence(g, 0);
        
        int [] deppred = new int[instance.getLength()+1];
        ImmutableMap<Integer,Integer> parents = maxArborescence.val.parents;
        for (int i=1;i<=instance.getLength();i++){
            deppred[i]=parents.get(i);
        }
        DependencyInstance retval = new DependencyInstance(instance.getLength(), instance.getWord(), instance.getPos(), deppred);
        
        return retval;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, Exception {
        // TODO code application logic here
        
        processArguments(args);
        
        if (train){
            System.out.println("===========\n== TRAIN ==\n===========");
            long start = System.currentTimeMillis();
            UnlabeledDependencyDictionary dictionary = new UnlabeledDependencyDictionary();
            dictionary.addFromFile(trainfile);
            
            long end = System.currentTimeMillis();
            System.out.println("creating dictionary took: " + (end-start));
            System.out.println("Num Feats: " + dictionary.getSize());
            
            System.out.print("reading train file...");
            DependencyFileReader dependencyFileReader = new DependencyFileReader(new File(trainfile));
            List<DependencyInstance> instances = dependencyFileReader.loadAll();
            System.out.println("done");
                        
            System.out.println("training...");
            CLEMIRAParser parser = new CLEMIRAParser();
            parser.train(dictionary, instances, lossFunction(), chooser(), constraint(), numIters);
            System.out.println("done");
            
            System.out.print("saving the model...");
            parser.parameter.optimizeForSerialization();
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
	System.out.println("------\n");
    }
}
