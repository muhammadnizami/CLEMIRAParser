
This is the software project for the final project by:

Muhammad Nizami

Sarjana Informatika-STEI-ITB

---------------------------------------
CLEMIRAParser early development version
---------------------------------------

The following package contains a java implementation of the dependency parser
described in Muhammad Nizami's Bachelor Thesis at Bandung Institute of Technology.

----------
Contents
----------

1. Compiling

2. Example of usage

3. Running the parser
   a. Input data format
   b. Training a parser
   c. Running a trained model on new data
   d. Evaluating output
   e. Streaming

----------------
1. Compiling
----------------

To compile the code, use NetBeans IDE 8.1 with JDK 8.

---------------------
2. Example Usage
---------------------

In the directory data/ there are examples of training and testing data.
Data format is described in the next section.

id-ud-train.conllu
- training data provided by Universal Dependencies for Indonesian language.

id-ud-test.conllu
- test data provided by Universal Dependencies for Indonesian language

To run the parser type:
> java -classpath ".:build/classes:lib/trove-3.0.3.jar:lib/commons-math3-3.2.jar:lib/Thomson/ChuLiuEdmonds-1.0-SNAPSHOT.jar:lib/Thomson/org-netbeans-modules-java-j2seproject-copylibstask.jar" -Xmx4096m clemiraparser.CLEMIRAParser \
  train train-file:data/id-ud-train.conllu model-name:dep.model \
  test test-file:data/id-ud-test.conllu output-file:out.txt \
  eval gold-file:data/id-ud-test.conllu 

This will train a parser on the training data, run it on the testing data and
evaluate the output against the gold standard. The results from running the 
parser are in the file out.txt and the trained model in dep.model.

-------------------------
3. Running the Parser
-------------------------

-------------------------
3a. Input data format
-------------------------

This parser accepts CoNLL-U format described in http://universaldependencies.org/format.html

----------------------------
3b. Training the parser
----------------------------

If you have a set of labeled data, first place it in the format described above.

If your training data is in a file train.conllu, you can then run the command:

> java -classpath ".:build/classes:lib/trove-3.0.3.jar:lib/commons-math3-3.2.jar:lib/Thomson/ChuLiuEdmonds-1.0-SNAPSHOT.jar:lib/Thomson/org-netbeans-modules-java-j2seproject-copylibstask.jar" -Xmx4096m clemiraparser.CLEMIRAParser \
  train train-file:data/train.conllu

This will train a parser with all the default properties. Additonal
properties can be described with the following flags:

train
- if present then parser will train a new model

train-file:file.conllu
- use data in file.conllu to train the parser

model-name:model.name
- store trained model in file called model.name

iters:numIters
- Run training algorithm for numIters epochs, default is 10

score-function:original|rootrelaxed
- Specifies the score function used for creating parse set
- Default is original

loss-function:mcdonaldhamming|rootpreferred
- Specifies the loss function used for training
- Default is mcdonaldhamming

constraint:original|modified
- Specifies the constraint used for MIRA optimization problem
- Default is original

chooser:kbest|lworst|kbestlworst|klossmarkedupbest
- Specifies the chooser function for the parse set during training
- Default is kbest

training-k:K
- Specifies the k-best parse set size to create constraints during training
- Default is 1

training-l:L
- Specifies the l-best parse set size to create constraints during training
- Default is 1
- Only needed when using l-worst chooser or k-best-l-worst chooser

training-alpha:alpha
- Specifies the alpha used for modified constraint type
- Default is 3.0

training-lambda:lambda
- Specifies the lambda used for rootpreferred loss function
- Default is 2.0

score-gamma:gamma
- Specifies the gamma used for rootrelaxed loss function
- Default is 0.95

stages:two-simple|two-markov1o|unlabeled|labeling-simple|labeling-markov1o
- Specifies the stages of the parsing
- Default is two-simple

------------------------------------------------
3c. Running a trained model on new data
------------------------------------------------

This section assumes you have trained a model and it is stored in dep.model.

First, format your data properly (section 3a).

It should be noted that the parser assumes both words and POS tags. 

The parser also assumes that the edge label and parent index entries are
in the input. However, these can just be artificially inserted (e.g. with entries
of "LAB" and "0") since the parser will produce these lines
as output.

If the data is in a file called test.conllu, run the command:

> java -classpath ".:build/classes:lib/trove-3.0.3.jar:lib/commons-math3-3.2.jar:lib/Thomson/ChuLiuEdmonds-1.0-SNAPSHOT.jar:lib/Thomson/org-netbeans-modules-java-j2seproject-copylibstask.jar" -Xmx4096m clemiraparser.CLEMIRAParser \
  test model-name:dep.model test-file:test.conllu output-file:out.conllu

This will create an output file "out.txt" with the predictions of the parser.
Other properties can be defined with the following flags:

test
- If included a trained parser will be run on the testing data

test-file:file.conllu
- The file containing the data to run the parser on

number-of-models:single-model|multi-model
- If single-model is selected, the parser will load a model specified by model-name parameter. If multi-model is selected, the parser will load models specified by simple-sentence-model and compound-sentence-model. In multi-model mode, model specified by simple-sentence-model parameter will be used to parse simple sentences, and model specified by compound-sentence-model parameter will be used to parse compound sentences. Whether a sentence is compound or not is determined by the existence of a token with POS tag equal to the POS tag supplied by conjunction-pos parameter.

model-name:model.name
- The name of the stored model to be used, if single-model is selected

output-file:out.conllu
- The result of running the parser on the new data

parsing-k:K
- The intermediate unlabeled trees produced (if a two-stage parser)

simple-sentence-model:model.name
- The name of the stored model to be used on simple sentences

compound-sentence-model:model.name
- The name of the stored model to be used on compound compound sentences

conjunction-pos:CONJ
- The pos tag for which existence inside a sentence indicates the sentence as a compound sentence. default: CONJ.



Note that the parsing stage run will be the same as the trained one. If the trained one is a two-stage parser, it the parsing will be two-stage parsing, and vice versa.


------------------------
3d. Evaluating Output
------------------------

This section describes a simple method for evaluating the output of
the parser against a gold standard.

Assume you have a gold standard, say test.conllu and the output of the parser
say out.conllu, then run the following command:

> java -classpath ".:build/classes:lib/trove-3.0.3.jar:lib/commons-math3-3.2.jar:lib/Thomson/ChuLiuEdmonds-1.0-SNAPSHOT.jar:lib/Thomson/org-netbeans-modules-java-j2seproject-copylibstask.jar" -Xmx4096m clemiraparser.CLEMIRAParser \
  eval gold-file:test.conllu output-file:out.conllu

This will return both labeled and unlabeled accuracy (if the data sets contain
labeled trees) as well as complete sentence accuracy, again labeled and
unlabeled.


------------------------
3e. Streaming
------------------------

This section describes a method for parsing through standard input and output.
Assume you have a trained model, say dep.model, you can run the trained model on 
standard input by running the following command:

> java -classpath ".:build/classes:lib/trove-3.0.3.j
ar:lib/commons-math3-3.2.jar:lib/Thomson/ChuLiuEdmonds-1.0-SNAPSHOT.jar:lib/Thomson/org-netbeans-modules-java-j2seproject-copylibstask.jar" -Xmx4096m clemiraparser.CLEMIRAParser\
  stream model-name:dep.model

It should be noted that the parser assumes both words and POS tags in the input. 

The parser also assumes that the edge label and parent index entries are
in the input. However, these can just be artificially inserted (e.g. with entries
of "LAB" and "0") since the parser will produce these lines
as output.

The parsing result starts after the line "streaming..." in stdout.

Other properties can be defined with the flags described in section 3c, except 
the test-file and output-file tags.