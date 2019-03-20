package de.hterhors.obie.projects.soccerplayer.ie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import corpus.SampledInstance;
import de.hterhors.obie.core.evaluation.PRF1;
import de.hterhors.obie.core.ontology.AbstractIndividual;
import de.hterhors.obie.core.ontology.AbstractOntologyEnvironment;
import de.hterhors.obie.core.ontology.OntologyInitializer;
import de.hterhors.obie.core.projects.AbstractProjectEnvironment;
import de.hterhors.obie.ml.activelearning.FullDocumentAtomicChangeEntropyRanker;
import de.hterhors.obie.ml.activelearning.FullDocumentEntropyRanker;
import de.hterhors.obie.ml.activelearning.FullDocumentLengthRanker;
import de.hterhors.obie.ml.activelearning.FullDocumentMarginBasedRanker;
import de.hterhors.obie.ml.activelearning.FullDocumentModelScoreRanker;
import de.hterhors.obie.ml.activelearning.FullDocumentObjectiveScoreRanker;
import de.hterhors.obie.ml.activelearning.FullDocumentRandFillerRanker;
import de.hterhors.obie.ml.activelearning.FullDocumentRandomRanker;
import de.hterhors.obie.ml.activelearning.FullDocumentVarianceRanker;
import de.hterhors.obie.ml.activelearning.IActiveLearningDocumentRanker;
import de.hterhors.obie.ml.corpus.BigramInternalCorpus;
import de.hterhors.obie.ml.corpus.distributor.AbstractCorpusDistributor;
import de.hterhors.obie.ml.corpus.distributor.ActiveLearningDistributor;
import de.hterhors.obie.ml.corpus.distributor.FoldCrossCorpusDistributor;
import de.hterhors.obie.ml.run.AbstractOBIERunner;
import de.hterhors.obie.ml.run.DefaultSlotFillingRunner;
import de.hterhors.obie.ml.run.eval.EvaluatePrediction;
import de.hterhors.obie.ml.run.param.RunParameter;
import de.hterhors.obie.ml.run.param.RunParameter.Builder;
import de.hterhors.obie.ml.templates.AbstractOBIETemplate;
import de.hterhors.obie.ml.templates.CooccurrenceTemplate;
import de.hterhors.obie.ml.templates.FrequencyTemplate;
import de.hterhors.obie.ml.templates.GenericMainTemplatePriorTemplate;
import de.hterhors.obie.ml.templates.InBetweenContextTemplate;
import de.hterhors.obie.ml.templates.InterTokenTemplate;
import de.hterhors.obie.ml.templates.LocalTemplate;
import de.hterhors.obie.ml.templates.SlotIsFilledTemplate;
import de.hterhors.obie.ml.templates.TokenContextTemplate;
import de.hterhors.obie.ml.variables.InstanceTemplateAnnotations;
import de.hterhors.obie.ml.variables.OBIEInstance;
import de.hterhors.obie.ml.variables.OBIEState;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerProjectEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.ner.regex.SoccerPlayerRegExNEL;
import de.hterhors.obie.projects.soccerplayer.ie.parameter.SoccerPlayerParameterQuickAccess;
import de.hterhors.obie.projects.soccerplayer.ie.templates.BirthDeathYearPairTemplate;
import de.hterhors.obie.projects.soccerplayer.ie.templates.BirthDeathYearTemplate;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayer;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayerThing;
import learning.Trainer;

/**
 * USE THIS CLASS TO LEARN HOW THE SYSTEM WORKS.
 * 
 * This is a lighter version of the SoccerPlayerExtraction-class.
 * 
 * Read README.md for more and detailed information. (under development and thus
 * not necessarily up-to-date)
 * 
 *
 * Preferred VM run-parameter: -Xmx12g -XX:+UseG1GC -XX:+UseStringDeduplication
 *
 * 
 * Main starting class for the information extraction task using the
 * SoccerPlayerOntology and SoccerPlayer-Wikipedia data set.
 * 
 * -XX:+PrintStringDeduplicationStatistics
 * 
 * @author hterhors
 * @author hterhors
 *
 */
public class LightSoccerPlayerExtraction {

	protected static Logger log = LogManager.getRootLogger();

	public static void main(String[] args) throws Exception {

		new LightSoccerPlayerExtraction();

	}

	/**
	 * The runID. This serves as an identifier for locating and saving the model. If
	 * anything was changed during the development the runID should be reset.
	 */
	private static String runID = "randomRun" + new Random().nextInt();

	/**
	 * The project environment.
	 */
	private final AbstractProjectEnvironment<ISoccerPlayerThing> projectEnvironment = SoccerPlayerProjectEnvironment
			.getInstance();

	/**
	 * The ontology environment.
	 */
	private final AbstractOntologyEnvironment ontologyEnvironment = SoccerPlayerOntologyEnvironment.getInstance();

	final long seed;

	public LightSoccerPlayerExtraction() throws Exception {

		seed = 100L;// new Random().nextLong();

		{
			OntologyInitializer.initializeOntology(SoccerPlayerOntologyEnvironment.getInstance());
		}

		log.info("Current run id = " + runID);

		/*
		 * Build parameter.
		 */
		RunParameter parameter = getParameter();

		/*
		 * Created new standard Relation Extraction runner.
		 */
		AbstractOBIERunner runner = new DefaultSlotFillingRunner(parameter);

		/**
		 * Whether you want to run the prediction of new documents (online mode) or
		 * train and test a model on a given corpus (during development).
		 */
		boolean predict = false;

		if (!predict) {

			/*
			 * train and/or test on existing corpus.
			 */
			trainTest(runner);

		} else {
			/*
			 * predict on a new documents.
			 */
			predict(runner, Arrays.asList(new File("predict/predict01.txt"), new File("predict/predict02.txt")));
		}

	}

	public RunParameter getParameter() {
		/**
		 * The number of epochs that the system should be trained.
		 */
		final int epochs = 10;

		/**
		 * The faction size of the corpus. 0.1F = 10% (about 156 Train docs) of the original size. Use 1.0F for
		 * all documents. You may reduce faction size during development to save time and memory usage. 
		 */
		float fractionOfCorpus = 0.1F;

		/**
		 * This parameterBuilder contains standard configurations of the system that are
		 * used for Relation Extraction tasks. You can but may not change the parameter
		 * predefined in here unless you know what you are doing!
		 */
		final Builder paramBuilder = SoccerPlayerParameterQuickAccess.getBaseParameter();

		/*
		 * Add parameter...
		 */
		/**
		 * The distribution of the documents in the corpus. Origin takes training ,
		 * development and test data as they are. Use shufflDist(fractionOfCorpus) for
		 * shuffling the documents before and redistribute to train (80%), dev(0%) and
		 * test(20%). (You may change that distribution by building your own
		 * distributor.
		 */
		final AbstractCorpusDistributor corpusDistributor = SoccerPlayerParameterQuickAccess.predefinedDistributor
				.originDist(fractionOfCorpus);

		/*
		 * Set parameter
		 */
		paramBuilder.setCorpusDistributor(corpusDistributor);
		paramBuilder.setRunID(runID);
		paramBuilder.setProjectEnvironment(projectEnvironment);
		paramBuilder.setOntologyEnvironment(ontologyEnvironment);
		paramBuilder.setEpochs(epochs);

		/*
		 * Add factor-graph-templates.
		 */
		addTemplates(paramBuilder);

		return paramBuilder.build();

	}

	/**
	 * Add templates to the parameter builder.
	 * 
	 * @param paramBuilder
	 */
	private void addTemplates(Builder paramBuilder) {

		final Set<Class<? extends AbstractOBIETemplate<?>>> templates = new HashSet<>();

		templates.add(BirthDeathYearTemplate.class);
		templates.add(BirthDeathYearPairTemplate.class);
		templates.add(GenericMainTemplatePriorTemplate.class);

		/*
		 * Predefined generic templates:
		 */
		templates.add(FrequencyTemplate.class);
		templates.add(TokenContextTemplate.class);
		templates.add(InterTokenTemplate.class);
		templates.add(InBetweenContextTemplate.class);

		templates.add(LocalTemplate.class);

		/*
		 * Templates that capture the cardinality of slots
		 */
		templates.add(SlotIsFilledTemplate.class);

		paramBuilder.setTemplates(templates);
	}

	private void predict(AbstractOBIERunner runner, final List<File> filesToPredict) throws IOException {
		log.info("Start prediction of new documents...");
		/*
		 * Load model if exists
		 */
		if (!runner.modelExists()) {
			log.warn("Model does not exists, abort prediction!");
			return;
		}

		try {
			runner.loadModel();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/**
		 * Build instances...
		 */
		final List<OBIEInstance> instancesToPredict = new ArrayList<>();

		for (File file : filesToPredict) {
			OBIEInstance i = new OBIEInstance(file.getName(), Files.lines(file.toPath()).reduce("", String::concat),
					null, new HashSet<>(Arrays.asList(ISoccerPlayer.class)));
			instancesToPredict.add(i);
		}

		/**
		 * Start prediction...
		 */
		List<OBIEState> finalStates = runner.predictInstancesBatch(instancesToPredict,
//				TODO: add generic to RunParameter	runner.getParameter().rootSearchTypes
				new HashSet<>(Arrays.asList(new SoccerPlayerRegExNEL(ISoccerPlayer.class))));

	}

	/**
	 * Run the system with the specifications and configurations.
	 * 
	 * @param runner
	 * @throws Exception
	 */
	private void trainTest(AbstractOBIERunner runner) throws Exception {
		log.info("Start training / testing of a model with a given corpus...");
		final long testTime;
		final long trainingTime;
		final long trt;

		if (runner.modelExists()) {
			/*
			 * If the model exists, load the model from the file system. The model location
			 * is specified in the parameter and the environment.
			 */
			runner.loadModel();
			trt = 0;
		} else {
			/*
			 * If the model does not exists train. The model is automatically stored to the
			 * file system to the given model location!
			 */
			trainingTime = System.currentTimeMillis();
			runner.train();
			trt = (System.currentTimeMillis() - trainingTime);
			log.info("Total training time: " + trt + " ms.");
		}

		testTime = System.currentTimeMillis();
		/**
		 * Get predictions that can be evaluated for full evaluation and
		 * perSlotEvaluation.
		 */
		final List<SampledInstance<OBIEInstance, InstanceTemplateAnnotations, OBIEState>> predictions = runner
				.testOnTest();

		/**
		 * Evaluate the trained model on the test data. This is equal to predictOnTest
		 * and apply the results to an evaluator.
		 *
		 * Same as:
		 *
		 * // final PRF1Container overallPRF1 = runner.evaluateOnTest();
		 */
		final PRF1 overallPRF1 = EvaluatePrediction.evaluateSlotFillingPredictions(runner.objectiveFunction, predictions,
				runner.getParameter().evaluator);

		log.info("Evaluation results on test data:\n" + overallPRF1);

		log.info("Evaluate predictions per slot:");

		/**
		 * Whether the output for each slot should be shown detailed or not. (Might
		 * generate large output)
		 */
		boolean detailedOutput = false;

		/**
		 * Evaluate the trained model on the test data for each slot individually.
		 *
		 * Same as:
		 *
		 * // runner.evaluatePerSlotOnTest(detailedOutput);
		 */
		EvaluatePrediction.evaluatePerSlotPredictions(runner.objectiveFunction, predictions,
				runner.getParameter().evaluator, detailedOutput);

		final String logPerformance = overallPRF1.getPrecision() + "\t" + overallPRF1.getRecall() + "\t"
				+ overallPRF1.getF1();

		log.info(logPerformance);

		final long tet = (System.currentTimeMillis() - testTime);
		log.info("--------------" + runID + "---------------");

		log.info("Total training time: " + trt + " ms.");
		log.info("Total test time: " + tet + " ms.");
		log.info("Total time: "
				+ Duration.between(Instant.now(), Instant.ofEpochMilli(System.currentTimeMillis() + (trt + tet))));

	}

}
