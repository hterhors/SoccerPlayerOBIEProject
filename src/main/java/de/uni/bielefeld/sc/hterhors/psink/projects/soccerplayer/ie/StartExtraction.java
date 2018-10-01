package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import corpus.SampledInstance;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.evaluation.PRF1Container;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.AbstractOntologyEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.projects.AbstractProjectEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.corpus.distributor.AbstractCorpusDistributor;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.AbstractOBIERunner;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.StandardRERunner;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.eval.EvaluatePrediction;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.OBIERunParameter;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.OBIERunParameter.OBIEParameterBuilder;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.templates.AbstractOBIETemplate;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.variables.InstanceEntityAnnotations;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.variables.OBIEInstance;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.variables.OBIEState;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayer;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.ner.regex.SoccerPlayerRegExNEL;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.templates.BirthYearTemplate;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.templates.PriorTemplate;

/**
 * 
 * Read README.md for more and detailed information.
 * 
 *
 * Preferred VM run-parameter: -Xmx12g -XX:+UseG1GC -XX:+UseStringDeduplication
 * 
 * Main starting class for the information extraction task using the
 * SoccerPlayerOntology and SoccerPlayer-Wikipedia data set.
 * 
 * @author hterhors
 *
 */
public class StartExtraction {

	protected static Logger log = LogManager.getRootLogger();

	public static void main(String[] args) throws Exception {

		new StartExtraction();

	}

	/**
	 * The runID. This serves as an identifier for locating and saving the model. If
	 * anything was changed during the development the runID should be reset.
	 */
	private final static String runID = "prior+birthyear";

	/**
	 * The project environment.
	 */
	private final AbstractProjectEnvironment projectEnvironment = SoccerPlayerProjectEnvironment.getInstance();

	/**
	 * The ontology environment.
	 */
	private final AbstractOntologyEnvironment ontologyEnvironment = SoccerPlayerOntologyEnvironment.getInstance();

	public StartExtraction() throws Exception {

		log.info("Current run id = " + runID);

		/**
		 * This parameterBuilder contains standard configurations of the system that are
		 * used for Relation Extraction tasks. You can but may not change the parameter
		 * predefined in here unless you know what you are doing!
		 */
		final OBIEParameterBuilder paramBuilder = SoccerPlayerParameterQuickAccess.getREParameter();

		/*
		 * Add parameter...
		 */

		/**
		 * The number of epochs that the system should be trained.
		 */
		final int epochs = 10;

		/**
		 * The distribution of the documents in the corpus. Origin takes training ,
		 * development and test data as they are. Use shufflDist() for shuffle the
		 * documents before and redistribute to train (80%), dev(0%) and test(20%). (You
		 * may change that distribution by building your own distributor...
		 */
		final AbstractCorpusDistributor corpusDistributor = SoccerPlayerParameterQuickAccess.preDefinedCorpusDistributor
				.originDist(1F);

		paramBuilder.setCorpusDistributor(corpusDistributor);
		paramBuilder.setRunID(runID);
		paramBuilder.setProjectEnvironment(projectEnvironment);
		paramBuilder.setOntologyEnvironment(ontologyEnvironment);
		paramBuilder.setEpochs(epochs);

		/*
		 * Add factor-graph-templates.
		 */
		addTemplates(paramBuilder);

		/*
		 * Build parameter.
		 */
		OBIERunParameter parameter = paramBuilder.build();

		/*
		 * Created new standard Relation Extraction runner.
		 */
		AbstractOBIERunner runner = new StandardRERunner(parameter);

		/**
		 * Whether you want to run the prediction of new texts or train and test a model
		 * on a given corpus.
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

	/**
	 * Add templates to the parameter builder.
	 * 
	 * @param paramBuilder
	 */
	private void addTemplates(OBIEParameterBuilder paramBuilder) {

		final Set<Class<? extends AbstractOBIETemplate<?>>> templates = new HashSet<>();
		/**
		 * TODO: Add new templates or try existing ones. Copy EmptyTemplate as
		 * code-template.
		 */

		/**
		 * Add your own templates:
		 */
		templates.add(PriorTemplate.class);
		templates.add(BirthYearTemplate.class);

		/**
		 * Predefined generic templates:
		 */
//		templates.add(FrequencyTemplate.class);

//		templates.add(TokenContextTemplate.class);
//		templates.add(InterTokenTemplate.class);
//		templates.add(SlotIsFilledTemplate.class);
//		templates.add(InBetweenContextTemplate.class);
//		templates.add(LocalTemplate.class);

		/**
		 * Templates that capture the cardinality of slots
		 */

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
				new HashSet<>(Arrays.asList(SoccerPlayerRegExNEL.class)));

	}

	/**
	 * Run the system with the specifications and configurations.
	 * 
	 * @param runner
	 * @throws Exception
	 */
	private static void trainTest(AbstractOBIERunner runner) throws Exception {
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
		final List<SampledInstance<OBIEInstance, InstanceEntityAnnotations, OBIEState>> predictions = runner
				.testOnTest();

		/**
		 * Evaluate the trained model on the test data. This is equal to predictOnTest
		 * and apply the results to an evaluator.
		 */
		final PRF1Container overallPRF1 = EvaluatePrediction.evaluateREPredictions(runner.objectiveFunction,
				predictions, runner.parameter.evaluator);
		/*
		 * Same as:
		 */
		// final PRF1Container overallPRF1 = runner.evaluateOnTest();

		log.info("Evaluation results on test data:\n" + overallPRF1);

		log.info("Evaluate predictions per slot:");

		/**
		 * Whether the output for each slot should be shown detailed or not. (Might
		 * generate large output)
		 */
		boolean detailedOutput = false;

		/**
		 * Evaluate the trained model on the test data for each slot individually.
		 */
		EvaluatePrediction.evaluatePerSlotPredictions(runner.objectiveFunction, predictions, runner.parameter.evaluator,
				detailedOutput);
		/*
		 * Same as:
		 */
//		runner.evaluatePerSlotOnTest(detailedOutput);

		final long tet = (System.currentTimeMillis() - testTime);

		log.info("Total training time: " + trt + " ms.");
		log.info("Total test time: " + tet + " ms.");
		log.info("Total time: "
				+ Duration.between(Instant.now(), Instant.ofEpochMilli(System.currentTimeMillis() + (trt + tet))));

	}

}
