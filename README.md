# SoccerPlayerOBIEProject

The SoccerPlayerOBIE Project 

**NOTE** This project is still under heavy development!

**Quick Start**

1)  Clone necessary projects (e.g. in terminal) :

git clone https://github.com/hterhors/SoccerPlayerOBIEProject.git;

git clone https://github.com/hterhors/OBIECore.git;

git clone https://github.com/hterhors/SoccerPlayerOntology.git;

git clone https://github.com/hterhors/OWL2JavaBin.git;

git clone https://github.com/hterhors/OBIEMachineLearningFramework.git;

git clone https://github.com/ag-sc/BIRE.git -b simplified-api;

2)  Start IDE of choice (e.g. Eclipse):

Make sure that maven is installed (per default in many IDEs). 

3)  Import Maven projects:

Right-click "Import..." -> Existing Maven Projects. Browse for git folder -> Select all 6 projects 

4)  Update all maven projects:

In Eclipse right-click -> Maven -> "Update Project..." select all projects.

5)  **Convert provided raw-corpus into bigram-corpus by running *ie.corpus.BigramCorpusCreator.java***

  This needs to be done only once because the provided corpus does not contain any entity annotations on textual level.

6)  Execute main in *ie.StartExtraction.java* 

**Goals of this project**

This project is an example of how to use the ontology based information extraction machine learning framework for a data set about soccer players.

The data set contains Wikipedia articles such as https://en.wikipedia.org/wiki/Herbie_Williams that are about soccer players. The goal is to extract information of these articles that belong to the soccer player(s) mentioned in the text.
The information that needs to be extracted is described in a corresponding ontology. The ontology describes the structure of information (ontological template / schema) but also provides possible property-values in form of calsses, named individuals, etc.  

For instance: given the text from the article linked above: 

----------------------------------------------------------------------------

*Herbert John Williams, Jr. (born 6 October 1940) is a Welsh former footballer who played at both professional and international levels as an inside forward.*

*Career*
*Born in Swansea, Williams spent his entire professional career with hometown club Swansea City, making 513 appearances in the Football League between 1958 and 1975.*

*After leaving Swansea, Williams spent the 1975 season as player-coach of Australian side Balgownie Rangers.*

*He also earned three international caps for Wales,[3] appearing in two FIFA World Cup qualifying matches.*

----------------------------------------------------------------------------

The goal is to find the player, this article is talking about http://psink.de/dbpedia/HerbieWilliams (**artifical namespace, thus does not resolve**) and its properties that are defined by the ontology. 
For instance: 

1)  Herbie Williams - born (hasBirthYear) - **"1940"** (datatype proeperty thus literal).
2)  Herbie Williams - born in (hasBirthPlace) - **Wales** (object property thus class / named individual).
3)  Herbie Williams - position (hasPosition) - **inside forward** (object property thus class / named individual).
4)  Herbie Williams - member of team (hasTeam) - **Swansea City** (object property thus class / named individual).
5)  Herbie Williams - member of team (hasTeam) -  **Wales National Team** (object property thus class / named individual).

**Dependencies**

Dependencies are part of git submodule and are cloned automatically.

You need the following dependent projects:

1)  OBIECore https://github.com/hterhors/OBIECore
2)  BIRE https://github.com/ag-sc/BIRE  (**simplified-api branch**)
3)  OBIEMLFramework https://github.com/hterhors/OBIEMachineLearningFrameWork
4)  OWL2JavaBin https://github.com/hterhors/OWL2JavaBin
5)  SoccerPlayerOntology https://github.com/hterhors/SoccerPlayerOntology

**Related Projects, Implementations / Examples**
1) OWL2JavaBin https://github.com/hterhors/OWL2JavaBin is a tool that can be used to convert ontologies written in OWL into java binaries which are used in the OBIE-ML-Framework.
2) SoccerPlayerOntology https://github.com/hterhors/SoccerPlayerOntology is an example ontology that was generated with OWL2javaBin. It contains the OWL file and the resulting java binaries. 
3) SoccerPlayerOBIEProject https://github.com/hterhors/SoccerPlayerOBIEProject is a project that works with the generated SoccerPalyerOntology. It contains example source code for
  i) the information extraction task using the OBIE MachineLearningFramework (incl. template / feature generation), 
  ii) how to convert an OWL to java binaries. 
  It further, contains an examplary annotated data set that was automatically generated from Wikipedia/dbpedia data using the DBPediaDatasetExtraction project.
4)  OBIECore https://github.com/hterhors/OBIECore contains core source code for all OBIE-related projects. 

**Description**

This is a simple example project that uses the automatically generated SoccerPlayerOntology for information extraction using the OBIEMLFramework.

This projects contains example classes to create the SoccerPlayerOntology and use it in the OBIE Machine Learning Framework.

The class *StartExtraction* is the main class to start the relation extraction. It depends on a parameter providing class *SoccerPlayerParameterQuickAccess* and on the project environment *SoccerPlayerProjectEnvironment*. 

The class *SoccerPlayerProjectEnvironment* determines all environment-parameters such as corpus locations, ontology version etc. 

The class *SoccerPlayerParameterQuickAccess* contains various parameters that can be set in the machine learning tool such as exploration strategies, number of epochs, scoring functions, and so on. Changing parameters in this class often requires advanced knowledge of the ML tool and is not recommended for beginners. Running this project does not require any changes in this parameter set. 

Templates (features) can be found in the template package. In this example project only 2 templates exist: 
*BirthYearTemplate* and *PriorTemplate*. The functionality and features are directly described in classes. 

In OBIE, we distinguish between two types of properties that needs to be filled.

1)  ObjectProperties (cf. OWL-definition https://www.w3.org/TR/owl-ref/#ObjectProperty-def) can be filled with ontology classes, or NamedIndividuals. Occurrences of classes and named individuals can be provided by some named entity recognition framework. 

2)  DatatypeProperties (cf. OWL-definition https://www.w3.org/TR/owl-ref/#DatatypeProperty-def) refer to some arbitrary string in the document and thus can not be expressed in the ontology. Filler for such properties need to be found in the first place using some named enitty recognition and linking framework. 


The class *SoccerPlayerRegExNEL* implements a simple NER and NEL framework that is based on regular expressions and can be generically applied to all ontologies. *SoccerPlayerRegExPattern* is used as an additional pattern dictionary for generating regular expressions:

1)  Filler for object properties (properties that are filled by ontological classes and named individuals) some regular expressions are automatically generated (based on the name of the class. **When designing a new ontology it is useful to have proper naming for classes, individuals and properties.**)

2)  Regular expressions for datatype properties need to be defined manually! In OBIE, filler for datatype properties need to be implemented as *AbstractInterpreter* (compare NumericInterpreter vs. StringInterpreter). An interpreter-class separats the actual value and the corresponding unit, if any from a given String. For instance: We want to find the weight of something. The found string is: "0.3 kg". However, the training data requests "300 g" which is semantically equal. Thus, each interpreter needs to implement a normalization function that is applied to the value and the unit. The normalized form is used when comparing values for datatype properties. This offers the possibility of evaluating different Strings as equal.
E.g., "0.3kg" = "300g" = "300 gram" = "300gram"...

A very simple example interpreter for the datatype property *hasBirthYear* can be found in the *dtinterpreter*-package: *BirthYearInterpreter*.

**Usage**

This project comes with a small (automatically and thus probabily not perfectly) annotated dataset. This raw corpus contains 
1)  text from Wikipedia articles about soccer player
2)  slot-filling annotations in form of the fully implemented java classes.
However, the corpus in its downloadable form can not be used in the OBIE-ML-FrameWork as a named entity recognition and linking tool needs to be applied first.

The class *BigramCorpusCreator* (Bigram = BIRE) applies a provided NEL-tool (In this case the simple regular expression tool that was described before) to all documents and stores the newly created annotations into a new format that can be used by the OBIE-ML-FrameWork. (Using the given RegExNEL-Tool, the size of this corpus should be approx. 10 mb)

After all dependencies are resolved and the project compiles, you need to adjust parameter in the environment before running the system! After all configurations such as paths to corpora models, etc. are updated the system can be run.

Simply start the *StartExtraction* class that contains the main-method. 


**Output**

In the first run (if all patehes are set correctly) the system should generate a model for each epoch which is written to the hard drive. A model is simply a directory containing a file for each used template. Each template-file contains a list of features and their parameter-value that were learned during training. 

When starting the system again the model can be loaded from the hard drive for prediction. 
The model name is automatically generated by some parameter, templates and the number of epochs. 
If you want to load a model from a different epoch just set the epoch variable to the desired one. 

**Integrated Baseline**

The OBIE-ML-Framework comes with some very simple baseline models. How to use this is explained in the *HighFreqBaseline* class. 



