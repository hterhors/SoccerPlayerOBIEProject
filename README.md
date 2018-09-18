# SoccerPlayerOBIEProject

The SoccerPlayerOBIE Project (Generated with OWL2JavaBinary)

Simple Example project that uses the automatically generated SoccerPlayerOntology.

This projects contains example classes to create the SoccerPlayerOntology and use it in the OBIE Machine Learning Framework.

The class SoccerPlayerProjectEnvironment determines all necessary properties such as corpus locations, ontology version etc. 

The class StartExtraction is the main class for the MachineLearning FrameWork. It depends on a ParameterClass (SoccerPlayerParameterQuickAccess) and on the project environment. 

The class SoccerPlayerParameterQuickAccess contains various parameter that can be set for the machine learning tool such as used templates (features), exploration strategies, number of epochs scoring functions and so on. Changing parameter in this class in particular often requires advanced knowledge of the ML Tool and is not recommended for beginner. 

Templates (Features) can be found in the template package. In this example project only 2 templates exists: 
BirthYearTemplate and PriorTemplate. The functionality and features are directly described in the class itself. 

In OBIE, we distinguish between two types of properties that needs to be filled.

ObjectProeprties (cf. OWL-definition) can be filled with ontology Classes, or NamedIndividuals.
E.g. the playing position of a soccer player is limited and can be easily expressed as classes/NamedIndividuals in the ontology directly. For instance with Defender, Center-Back, Wing-Back etc.

DatatypeProperties refer to some arbitrary String in the document and thus should not (can not) be expressed in the ontology. Filler for such properties needs to be found in the first place. A simple example DTproperty might be the birth year of a soccer player. 

Such SlotFiller candidates can be found with the SoccerPlayerRegExNEL class which uses the SoccerPlayerRegExPattern as pattern dictionary. While for ObjectProperties-Filler some regular expressions are automatically generated (Based on the name of the class. When designing a new ontology it is useful to have proper naming for classes, individuals and properties.) regular expressions for DatatypeProperties need to be defined manually! In OBIE, filler for DatatypeProperties should be (need to be) defined as AbstractInterpreter (compare NumericInterpreter vs. StringInterpreter). Interpreter offer the possibility of evaluating two different Strings as equal e.g. "300 g" = "0.3kg" = "300g" ...

A simple example interpreter for the BirthYear can be found in the dtinterpreter package. 
As this example ontology has just one datatype property it is not necessary to implement further ones. 

This project comes with a small raw corpus containing wikipedia article about soccer player and slotfilling annotations in form of the java classes. However, this corpus can not be used in the OBIE-ML-FrameWork as a NamedEntityRecognition and Linking needs to be done. The class BigramCorpusCreator applies a NEL-Tool (In this case the simple Regular Expression) to all documents and stores it into a new format that can be used by the OBIE-ML-FrameWork. (using the given RegExNEL-Tool, the size of this corpus is approx 10 mb)

the OBIE-ML-Framework comes with some very simple baseline models. How to use this is explained in the HighFreqBaseline class. 



