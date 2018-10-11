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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import corpus.SampledInstance;
import de.hterhors.obie.core.evaluation.PRF1Container;
import de.hterhors.obie.core.ontology.AbstractOntologyEnvironment;
import de.hterhors.obie.core.ontology.OntologyInitializer;
import de.hterhors.obie.core.projects.AbstractProjectEnvironment;
import de.hterhors.obie.ml.activelearning.FullDocumentEntropyRanker;
import de.hterhors.obie.ml.activelearning.FullDocumentRandomRanker;
import de.hterhors.obie.ml.activelearning.IActiveLearningDocumentRanker;
import de.hterhors.obie.ml.corpus.distributor.AbstractCorpusDistributor;
import de.hterhors.obie.ml.corpus.distributor.ActiveLearningDistributor;
import de.hterhors.obie.ml.run.AbstractOBIERunner;
import de.hterhors.obie.ml.run.StandardRERunner;
import de.hterhors.obie.ml.run.eval.EvaluatePrediction;
import de.hterhors.obie.ml.run.param.OBIERunParameter;
import de.hterhors.obie.ml.run.param.OBIERunParameter.Builder;
import de.hterhors.obie.ml.templates.AbstractOBIETemplate;
import de.hterhors.obie.ml.templates.FrequencyTemplate;
import de.hterhors.obie.ml.templates.InBetweenContextTemplate;
import de.hterhors.obie.ml.templates.InterTokenTemplate;
import de.hterhors.obie.ml.templates.LocalTemplate;
import de.hterhors.obie.ml.templates.SlotIsFilledTemplate;
import de.hterhors.obie.ml.templates.TokenContextTemplate;
import de.hterhors.obie.ml.variables.InstanceTemplateAnnotations;
import de.hterhors.obie.ml.variables.OBIEInstance;
import de.hterhors.obie.ml.variables.OBIEState;
import de.hterhors.obie.projects.soccerplayer.ie.ner.regex.SoccerPlayerRegExNEL;
import de.hterhors.obie.projects.soccerplayer.ie.templates.BirthYearTemplate;
import de.hterhors.obie.projects.soccerplayer.ie.templates.PriorTemplate;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayer;

/**
 * 
 * Prepare for active learning
 * 
 * Read README.md for more and detailed information.
 * 
 *
 * Preferred VM run-parameter: -Xmx12g -XX:+UseG1GC -XX:+UseStringDeduplication
 *
 * 
 * Main starting class for the information extraction task using the
 * SoccerPlayerOntology and SoccerPlayer-Wikipedia data set.
 * 
 * 
 * 
 * @author hterhors
 *
 */
public class StartExtraction {

	private static final String DEFAULT_ACTIVE_LEARNING_STRATEGY = "random";
	private static final String DEFAULT_RESULT_FILE_NAME = "tmpResultFile";

	protected static Logger log = LogManager.getRootLogger();

	public static void main(String[] args) throws Exception {

		args = new String[] { "entropyResults", "entropy" };

		log.info("1) argument: file to store results");
		log.info("2) argument: mode of active learning, \"random\"(default) or \"entropy\"");

		final File printResults = new File(args.length == 0 ? DEFAULT_RESULT_FILE_NAME : args[0]);
		final String acMode = args.length < 2 ? DEFAULT_ACTIVE_LEARNING_STRATEGY : args[1];

		if (!(acMode.equals("random") || acMode.equals("entropy"))) {
			log.error("Unkown active learning mode: " + acMode);
			System.exit(1);
		}

		if (printResults.getParentFile() != null && !printResults.getParentFile().exists()) {
			log.error("Parent dir does not exist: " + printResults.getParentFile().getCanonicalPath());
			System.exit(1);
		}

		new StartExtraction(acMode, printResults);

	}

	/**
	 * The runID. This serves as an identifier for locating and saving the model. If
	 * anything was changed during the development the runID should be reset.
	 */
	private final static String runID = "random" + new Random().nextInt();

	/**
	 * The project environment.
	 */
	private final AbstractProjectEnvironment projectEnvironment = SoccerPlayerProjectEnvironment.getInstance();

	/**
	 * The ontology environment.
	 */
	private final AbstractOntologyEnvironment ontologyEnvironment = SoccerPlayerOntologyEnvironment.getInstance();

	public StartExtraction(String acModus, File printResults) throws Exception {
		{
			OntologyInitializer.initializeOntology(ontologyEnvironment);
		}

		log.info("Current run id = " + runID);

		/**
		 * This parameterBuilder contains standard configurations of the system that are
		 * used for Relation Extraction tasks. You can but may not change the parameter
		 * predefined in here unless you know what you are doing!
		 */
		final Builder paramBuilder = SoccerPlayerParameterQuickAccess.getREParameter();

//		InvestigationRestriction investigationRestriction = new InvestigationRestriction(ISoccerPlayer.class, false);
//
//		paramBuilder.setInvestigationRestriction(investigationRestriction);

		/*
		 * Add parameter...
		 */

		/**
		 * The number of epochs that the system should be trained.
		 */
		final int epochs = 3;

		/**
		 * The distribution of the documents in the corpus. Origin takes training ,
		 * development and test data as they are. Use shufflDist() for shuffle the
		 * documents before and redistribute to train (80%), dev(0%) and test(20%). (You
		 * may change that distribution by building your own distributor...
		 */
		final AbstractCorpusDistributor corpusDistributor = SoccerPlayerParameterQuickAccess.preDefinedCorpusDistributor
				.activeLearningDist(1F);

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

			/**
			 * Whether you want to start active learning procedure or normal training
			 */
			boolean activeLearning = parameter.corpusDistributor instanceof ActiveLearningDistributor;

			/*
			 * train and/or test on existing corpus.
			 */

			if (activeLearning) {
				activeLearning(runner, acModus, printResults);
			} else {
				trainTest(runner);
			}

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
	private void addTemplates(Builder paramBuilder) {

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
		templates.add(FrequencyTemplate.class);
		templates.add(TokenContextTemplate.class);
		templates.add(InterTokenTemplate.class);
		templates.add(InBetweenContextTemplate.class);
		templates.add(LocalTemplate.class);

		/**
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
		final List<SampledInstance<OBIEInstance, InstanceTemplateAnnotations, OBIEState>> predictions = runner
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

	private void activeLearning(AbstractOBIERunner runner, String acMode, File printResults) throws Exception {

		List<PRF1Container> performances = new ArrayList<>();

		long allTime = System.currentTimeMillis();

		int i = 1;

		final IActiveLearningDocumentRanker ranker;
		if (acMode.equals("random")) {
			ranker = new FullDocumentRandomRanker();
		} else if (acMode.equals("entropy")) {
			ranker = new FullDocumentEntropyRanker();
		} else {
			ranker = null;
			log.error("unkown active learning mode");
			System.exit(1);
		}

//		final IActiveLearningDocumentRanker documentModelScoreRanker = new FullDocumentModelScoreRanker();
//		final IActiveLearningDocumentRanker documentVarianceRanker = new FullDocumentVarianceRanker();

		PrintStream resultPrintStream = new PrintStream(new FileOutputStream(printResults, true));
		resultPrintStream.println("############Active Learning Performances: " + runID + "############");
		resultPrintStream.println("#Precision\tRecall\tF1");

		List<OBIEInstance> newTrainingInstances = new ArrayList<>();
		do {

			log.info("#############################");
			log.info("New active learning iteration: " + (i));
			long time = System.currentTimeMillis();

			log.info("Set training instances to("
					+ runner.corpusProvider.getTrainingCorpus().getInternalInstances().size() + "):");
//			runner.corpusProvider.getTrainingCorpus().getInternalInstances().forEach(System.out::println);
			log.info("Remaining training instances ("
					+ runner.corpusProvider.getDevelopCorpus().getInternalInstances().size() + "):");
//			runner.corpusProvider.getDevelopCorpus().getInternalInstances().forEach(System.out::println);
			log.info("#############################");

			runner.train();
//			if (newTrainingInstances.isEmpty()) {
//				runner.train();
//			} else {
//				runner.continueTraining(newTrainingInstances);
//			}

			List<SampledInstance<OBIEInstance, InstanceTemplateAnnotations, OBIEState>> predictions = runner
					.testOnTest();

			PRF1Container prf1 = EvaluatePrediction.evaluateREPredictions(runner.getObjectiveFunction(), predictions,
					runner.parameter.evaluator);

			performances.add(prf1);
			resultPrintStream.println(prf1.p + "\t" + prf1.r + "\t" + prf1.f1);

			log.info("############Active Learning performances############");
			performances.forEach(log::info);

			log.info("Time needed: " + (System.currentTimeMillis() - time));

		} while (!(newTrainingInstances = runner.corpusProvider.updateActiveLearning(runner, ranker)).isEmpty());

		log.info("############Active Learning performances############");
		performances.forEach(log::info);

		log.info("Total time needed: " + (System.currentTimeMillis() - allTime));

		log.info("Print results to: " + printResults);

		resultPrintStream.close();

	}

}
