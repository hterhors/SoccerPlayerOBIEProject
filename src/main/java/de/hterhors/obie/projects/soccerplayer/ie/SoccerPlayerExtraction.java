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
import de.hterhors.obie.ml.corpus.distributor.ShuffleCorpusDistributor;
import de.hterhors.obie.ml.run.AbstractRunner;
import de.hterhors.obie.ml.run.DefaultSlotFillingRunner;
import de.hterhors.obie.ml.run.eval.EvaluatePrediction;
import de.hterhors.obie.ml.run.param.RunParameter;
import de.hterhors.obie.ml.run.param.RunParameter.Builder;
import de.hterhors.obie.ml.templates.AbstractOBIETemplate;
import de.hterhors.obie.ml.templates.InBetweenContextTemplate;
import de.hterhors.obie.ml.templates.InterTokenTemplate;
import de.hterhors.obie.ml.templates.KnowledgeBaseTemplate;
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
 * -XX:+PrintStringDeduplicationStatistics
 * 
 * @author hterhors
 *
 */
public class SoccerPlayerExtraction {

	private static final String DEFAULT_ACTIVE_LEARNING_STRATEGY = "random";
	private static final String DEFAULT_ACTIVE_LEARNING_SEED = "200";
	private static final String DEFAULT_RESULT_FILE_NAME = "tmpResultFile";

	protected static Logger log = LogManager.getRootLogger();
	final private ETemplateMode templateMode;

	enum ETemplateMode {
		KB, LING;
	}

	enum EQueryType {
		Q1, Q2, Q3, Q4, Q5, ALL;
	}

	public static void main(String[] args) throws Exception {

		if (args == null || args.length == 0)
//			args = new String[] { "varianceResults", "variance" };
			args = new String[] { "randomResults", "random", "100", "1", "KB", "ALL" };
//			args = new String[] { "marginResults", "margin" };
//			args = new String[] { "lengthResults", "length" };
//			args = new String[] { "rndFillerResults", "rndFiller" };
//			args = new String[] { "entropyResults", "entropy" };
//			args = new String[] { "entropyAtomicResults", "entropyAtomic" };
//			args = new String[] { "modelResults", "model" };
//			args = new String[] { "objectiveResults", "objective" };

		log.info("1) argument: file to store results");
		log.info(
				"2) argument: mode of active learning, \"random\"(default), \"entropy\", \"entropyAtomic\", \"objective\", \"model\", \"margin\", \"length\" or \"variance\"");
		log.info("3) argument: Random inital seed");
		log.info("4) argument: Number of N-best documents for entropy");
		log.info("5) argument: mode of templates, one of \"KB\", or \"LING\"");
		log.info("6) argument: mode of KB queries, one of \"1\", \"2\",\"3\",\"4\", or \"5\", \"ALL\"");

		new SoccerPlayerExtraction(args);

	}

	/**
	 * The runID. This serves as an identifier for locating and saving the model. If
	 * anything was changed during the development the runID should be reset.
	 */
//	private static String runID = "randomRun-66039460";
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
	final File printResults;
	final String acMode;
	final long seed;

	EQueryType queryType;

	public SoccerPlayerExtraction(String[] args) throws Exception {

		printResults = new File(args.length < 1 ? DEFAULT_RESULT_FILE_NAME : args[0]);
		acMode = args.length < 2 ? DEFAULT_ACTIVE_LEARNING_STRATEGY : args[1];
		seed = Long.parseLong(args.length < 3 ? DEFAULT_ACTIVE_LEARNING_SEED : args[2]);

		if (args.length >= 4) {
			FullDocumentEntropyRanker.N = Integer.parseInt(args[3]);
		}
		templateMode = ETemplateMode.valueOf(args[4]);
		queryType = EQueryType.valueOf(args[5]);

		log.info("Store results into: " + printResults);
		log.info("Active Learning Modus: " + acMode);

		if (printResults.getParentFile() != null && !printResults.getParentFile().exists()) {
			log.error("Parent dir does not exist: " + printResults.getParentFile().getCanonicalPath());
			System.exit(1);
		}

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
		AbstractRunner runner = new DefaultSlotFillingRunner(parameter, false);

		/**
		 * Whether you want to run the prediction of new texts or train and test a model
		 * on a given corpus.
		 */
		boolean predict = false;

		if (!predict) {

			/*
			 * train and/or test on existing corpus.
			 */
//			reverseEngeneerACLearning(runner,seed);

			if (parameter.corpusDistributor instanceof ActiveLearningDistributor) {
				activeLearning(runner);
			} else if (parameter.corpusDistributor instanceof FoldCrossCorpusDistributor) {
				nFoldCrossValidation(runner);
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

	public RunParameter getParameter() {
		/**
		 * This parameterBuilder contains standard configurations of the system that are
		 * used for Relation Extraction tasks. You can but may not change the parameter
		 * predefined in here unless you know what you are doing!
		 */
		final Builder paramBuilder = SoccerPlayerParameterQuickAccess.getBaseParameter();

//		InvestigationRestriction investigationRestriction = new InvestigationRestriction(ISoccerPlayer.class, false);
//
//		paramBuilder.setInvestigationRestriction(investigationRestriction);

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
//		final AbstractCorpusDistributor corpusDistributor = SoccerPlayerParameterQuickAccess.predefinedDistributor
//				.shuffleDist(1F);

//		final AbstractCorpusDistributor corpusDistributor = new FoldCrossCorpusDistributor.Builder().setN(10)
//		.setSeed(seed).setCorpusSizeFraction(1F).build();

		final AbstractCorpusDistributor corpusDistributor = new ShuffleCorpusDistributor.Builder().setSeed(seed)
				.setTrainingProportion(80).setDevelopmentProportion(0).setCorpusSizeFraction(1F).setTestProportion(20)
				.build();

//		final AbstractCorpusDistributor corpusDistributor = new ActiveLearningDistributor.Builder()
//				.setMode(EMode.PERCENTAGE).setBPercentage(0.051f).setSeed(seed).setCorpusSizeFraction(1F)
//				.setInitialTrainingSelectionFraction(0.05f).setTrainingProportion(80).setTestProportion(20).build();

//		final AbstractCorpusDistributor corpusDistributor = new ActiveLearningDistributor.Builder().setB(25)
//				.setSeed(200L).setCorpusSizeFraction(1F).setInitialTrainingSelectionFraction(0.0855f)
//				.setTrainingProportion(80).setTestProportion(20).build();

//		final AbstractCorpusDistributor corpusDistributor = ByNameDist.corpusDistributor;

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
		/**
		 * TODO: Add new templates or try existing ones. Copy EmptyTemplate as
		 * code-template.
		 */

		/*
		 * SoccerPlayer specific templates:
		 */
		if (true) {

			templates.add(BirthDeathYearTemplate.class);
			templates.add(BirthDeathYearPairTemplate.class);
//			templates.add(GenericMainTemplatePriorTemplate.class);

			// TODO: Add your own templates

			if (templateMode == ETemplateMode.KB) {

				templates.add(KnowledgeBaseTemplate.class);

				if (queryType == EQueryType.Q1) {
					KnowledgeBaseTemplate.useQuery1 = false;
				} else if (queryType == EQueryType.Q2) {
					KnowledgeBaseTemplate.useQuery2 = false;
				} else if (queryType == EQueryType.Q3) {
					KnowledgeBaseTemplate.useQuery3 = false;
				} else if (queryType == EQueryType.Q4) {
					KnowledgeBaseTemplate.useQuery4 = false;
				} else if (queryType == EQueryType.Q5) {
					KnowledgeBaseTemplate.useQuery5 = false;
				}
			}

			// templates.add(TrainAsKnowledgeBaseTemplate.class);

//			templates.add(CooccurrenceTemplate.class);
//			templates.add(DocumentClassificationTemplate.class);
//			templates.add(LevenshteinTemplate.class);
			/*
			 * Predefined generic templates:
			 */
//			templates.add(FrequencyTemplate.class);

			templates.add(TokenContextTemplate.class);
			templates.add(InterTokenTemplate.class);
			templates.add(InBetweenContextTemplate.class);

			templates.add(LocalTemplate.class);

			/*
			 * Templates that capture the cardinality of slots
			 */
			templates.add(SlotIsFilledTemplate.class);
		} else {
			/*
			 * DBPedia generic templates:
			 */
//			if (templateMode == ETemplateMode.SS || templateMode == ETemplateMode.BOTH)
//				templates.add(GenericMainTemplatePriorTemplate.class);
//
//			// TODO: Add your own templates for specific ontologies
//			if (templateMode == ETemplateMode.PS || templateMode == ETemplateMode.BOTH)
//				templates.add(CooccurrenceTemplate.class);

			/*
			 * Predefined generic templates:
			 */
//		templates.add(FrequencyTemplate.class);
			templates.add(TokenContextTemplate.class);
			templates.add(InterTokenTemplate.class);
			templates.add(InBetweenContextTemplate.class);
			templates.add(LocalTemplate.class);

			/*
			 * Templates that capture the cardinality of slots
			 */
			templates.add(SlotIsFilledTemplate.class);
		}

		paramBuilder.setTemplates(templates);
	}

	private void predict(AbstractRunner runner, final List<File> filesToPredict) throws IOException {
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
//			TODO: add generic to RunParameter	runner.getParameter().rootSearchTypes
				new HashSet<>(Arrays.asList(new SoccerPlayerRegExNEL(ISoccerPlayer.class))));

	}

	/**
	 * Run the system with the specifications and configurations.
	 * 
	 * @param runner
	 * @throws Exception
	 */
	private void trainTest(AbstractRunner runner) throws Exception {
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
		final PRF1 overallPRF1 = EvaluatePrediction.evaluateREPredictions(runner.objectiveFunction, predictions,
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

		PrintStream resultPrintStream = new PrintStream(new FileOutputStream(printResults, true));
		resultPrintStream.println("############Performances: " + runID + "############");
		resultPrintStream.println("#Model\tPrecision\tRecall\tF1");
		final String logPerformance = templateMode + "\t" + overallPRF1.getPrecision() + "\t" + overallPRF1.getRecall()
				+ "\t" + overallPRF1.getF1();
		resultPrintStream.println(logPerformance);

		resultPrintStream.close();

		final long tet = (System.currentTimeMillis() - testTime);
		log.info("--------------" + runID + "---------------");

		log.info("Total training time: " + trt + " ms.");
		log.info("Total test time: " + tet + " ms.");
		log.info("Total time: "
				+ Duration.between(Instant.now(), Instant.ofEpochMilli(System.currentTimeMillis() + (trt + tet))));

	}

	private void activeLearning(AbstractRunner runner) throws Exception {

		runID = acMode + new Random().nextInt();

		long allTime = System.currentTimeMillis();

		int i = 1;

		final IActiveLearningDocumentRanker ranker;

		if (acMode.equals("random")) {
			ranker = new FullDocumentRandomRanker(runner);
		} else if (acMode.equals("entropy")) {
			ranker = new FullDocumentEntropyRanker(runner);
		} else if (acMode.equals("rndFiller")) {
			ranker = new FullDocumentRandFillerRanker(runner);
		} else if (acMode.equals("entropyAtomic")) {
			ranker = new FullDocumentAtomicChangeEntropyRanker(runner);
		} else if (acMode.equals("variance")) {
			ranker = new FullDocumentVarianceRanker(runner);
		} else if (acMode.equals("objective")) {
			ranker = new FullDocumentObjectiveScoreRanker(runner);
		} else if (acMode.equals("model")) {
			ranker = new FullDocumentModelScoreRanker(runner);
		} else if (acMode.equals("length")) {
			ranker = new FullDocumentLengthRanker(runner);
		} else if (acMode.equals("margin")) {
			ranker = new FullDocumentMarginBasedRanker(runner);
		} else {
			ranker = null;
			log.error("unkown active learning mode");
			System.exit(1);
		}

		PrintStream resultPrintStream = new PrintStream(new FileOutputStream(printResults, true));
		resultPrintStream.println("############Active Learning Performances: " + runID + "############");
		resultPrintStream.println("#Iteration\t#TrainData\tPrecision\tRecall\tF1");

		List<OBIEInstance> newTrainingInstances = new ArrayList<>();

		final int maxNumberOfIterations = 50;
		int iterationCounter = 0;

		List<String> performances = new ArrayList<>();

		do {

			if (++iterationCounter > maxNumberOfIterations) {
				log.info("#############################");
				log.info("Reached maximum number of iterations: " + maxNumberOfIterations);
				log.info("#############################");
				break;
			}

			final int c = iterationCounter;

			log.info("#############################");
			log.info("New active learning iteration: " + (i));
			log.info("#############################");

			long time = System.currentTimeMillis();

			if (newTrainingInstances.isEmpty()) {
				runner.train();
			} else {
				log.info("New instances:");
				newTrainingInstances.forEach(s -> log.info(c + "_NEW\t" + s.getName()));

				runner.clean(getParameter());
				runner.train();
			}

			log.info("Apply current model to test data...");

			Level trainerLevel = LogManager.getFormatterLogger(Trainer.class.getName()).getLevel();
			Level runnerLevel = LogManager.getFormatterLogger(AbstractRunner.class).getLevel();

			Configurator.setLevel(Trainer.class.getName(), Level.FATAL);
			Configurator.setLevel(AbstractRunner.class.getName(), Level.FATAL);

			List<SampledInstance<OBIEInstance, InstanceTemplateAnnotations, OBIEState>> predictions = runner
					.testOnTest();

			log.info("Training instances:");
			runner.corpusProvider.getTrainingCorpus().getInternalInstances()
					.forEach(s -> log.info(c + "_TRAIN\t" + s.getName()));

			log.info("Test instances:");
			runner.corpusProvider.getTestCorpus().getInternalInstances()
					.forEach(s -> log.info(c + "_TEST\t" + s.getName()));

			Configurator.setLevel(Trainer.class.getName(), trainerLevel);
			Configurator.setLevel(AbstractRunner.class.getName(), runnerLevel);

			PRF1 prf1 = EvaluatePrediction.evaluateREPredictions(runner.getObjectiveFunction(), predictions,
					runner.getParameter().evaluator);

			final String logPerformance = iterationCounter + "\t"
					+ runner.corpusProvider.getTrainingCorpus().getInternalInstances().size() + "\t"
					+ prf1.getPrecision() + "\t" + prf1.getRecall() + "\t" + prf1.getF1();

			performances.add(logPerformance);
			resultPrintStream.println(logPerformance);

			log.info("############Active Learning performances: " + runID + "############");
			performances.forEach(log::info);

			log.info("Time needed: " + (System.currentTimeMillis() - time));

		} while (!(newTrainingInstances = runner.corpusProvider.updateActiveLearning(runner, ranker)).isEmpty());

		log.info("--------------" + runID + "---------------");

		log.info("Total time needed: " + (System.currentTimeMillis() - allTime));

		log.info("Print results to: " + printResults);

		resultPrintStream.close();

	}

	/**
	 * Test every single document and sort
	 * 
	 * @param runner
	 * @throws Exception
	 */
	private void reverseEngeneerACLearning(AbstractRunner runner) throws Exception {

		final List<OBIEInstance> memTrain = new ArrayList<>(
				runner.corpusProvider.getTrainingCorpus().getInternalInstances());

		class X implements Comparable<X> {
			InstanceTemplateAnnotations thing;
			double f1Score;
			String name;

			public X(InstanceTemplateAnnotations instanceTemplateAnnotations, double f1Score, String name) {
				super();
				this.thing = instanceTemplateAnnotations;
				this.f1Score = f1Score;
				this.name = name;
			}

			@Override
			public String toString() {
				return "X [thing=" + thing + ", f1Score=" + f1Score + ", name=" + name + "]";
			}

			@Override
			public int compareTo(X o) {
				return -Double.compare(f1Score, o.f1Score);
			}

		}
		final List<X> sortablePerformances = new ArrayList<>();

		for (OBIEInstance instance : runner.corpusProvider.getDevelopCorpus().getInternalInstances()) {

			log.info("Add instance: " + instance);

			List<OBIEInstance> trainingInstances = new ArrayList<>();

			trainingInstances.addAll(memTrain);
			trainingInstances.add(instance);

			runner.corpusProvider.trainingCorpus = new BigramInternalCorpus(trainingInstances);

			runner.clean(getParameter());
			runner.train();

			Level trainerLevel = LogManager.getFormatterLogger(Trainer.class.getName()).getLevel();
			Level runnerLevel = LogManager.getFormatterLogger(AbstractRunner.class).getLevel();

			Configurator.setLevel(Trainer.class.getName(), Level.FATAL);
			Configurator.setLevel(AbstractRunner.class.getName(), Level.FATAL);

			List<SampledInstance<OBIEInstance, InstanceTemplateAnnotations, OBIEState>> predictions = runner
					.testOnTest();

			Configurator.setLevel(Trainer.class.getName(), trainerLevel);
			Configurator.setLevel(AbstractRunner.class.getName(), runnerLevel);

			PRF1 prf1 = EvaluatePrediction.evaluateREPredictions(runner.getObjectiveFunction(), predictions,
					runner.getParameter().evaluator);

			final String logPerformance = runner.corpusProvider.getTrainingCorpus().getInternalInstances().size() + "\t"
					+ prf1.getPrecision() + "\t" + prf1.getRecall() + "\t" + prf1.getF1();

			log.info("-----------------------------");
			log.info(logPerformance);

			log.info("-----------------------------");

			sortablePerformances.add(new X(instance.getGoldAnnotation(), prf1.getF1(), instance.getName()));
			Collections.sort(sortablePerformances);
			sortablePerformances.forEach(log::info);
			log.info("-----------------------------");
			System.gc();

		}

		log.info("--------------" + runID + "---------------");
		sortablePerformances.forEach(log::info);

	}

	private void nFoldCrossValidation(AbstractRunner runner) throws Exception {
		PRF1 mean = new PRF1(0, 0, 0);

		long allTime = System.currentTimeMillis();

		Map<AbstractIndividual, PRF1> results = new HashMap<>();
		while (runner.corpusProvider.nextFold()) {
			log.info("#############################");
			log.info("New " + ((FoldCrossCorpusDistributor) runner.getParameter().corpusDistributor).n
					+ "-fold cross validation iteration: "
					+ String.valueOf(runner.corpusProvider.getCurrentFoldIndex() + 1));
			long time = System.currentTimeMillis();

			log.info("Number of training data: "
					+ runner.corpusProvider.getTrainingCorpus().getInternalInstances().size());
			log.info("Number of test data: " + runner.corpusProvider.getTestCorpus().getInternalInstances().size());

//			System.out.println("Set training instances to:");
//			runner.corpusProvider.getTrainingCorpus().getInternalInstances().forEach(System.out::println);
//			System.out.println("Set test instances to:");
//			runner.corpusProvider.getTestCorpus().getInternalInstances().forEach(System.out::println);
			log.info("#############################");
//			try {
//				runner.loadModel();
//			} catch (Exception e) {
//				System.err.println(e.getMessage());
//				runner.train();
//			}

			runner.clean(getParameter());
			runner.train();

			List<SampledInstance<OBIEInstance, InstanceTemplateAnnotations, OBIEState>> predictions = runner
					.testOnTest();

			PRF1 prf1 = EvaluatePrediction.evaluateREPredictions(runner.getObjectiveFunction(), predictions,
					runner.getParameter().evaluator);

			mean.add(prf1);
			log.info("Time needed: " + (System.currentTimeMillis() - time));

		}

		log.info(((FoldCrossCorpusDistributor) runner.getParameter().corpusDistributor).n
				+ " fold cross validation mean: " + mean);

		for (Entry<AbstractIndividual, PRF1> ie : results.entrySet()) {
			log.info(ie.getKey().name + "-->" + ie.getValue().getF1());
		}

		log.info("Time needed: " + (System.currentTimeMillis() - allTime));

	}
}

//without cooc.
//p: 0.8122975277067348	r: 0.780333292737385	f1: 0.7959946477754023
//Evaluate predictions per slot:
//
//
//#############################
//Restricted to: Template-Type
//Mean-Precisiion = 0.9667519181585678
//Mean-Recall = 0.9667519181585678
//Mean-F1 = 0.9667519181585678
//#############################
//
//
//#############################
//Restricted to: Field(s):birthPlaces
//Mean-Precisiion = 0.6892583120204604
//Mean-Recall = 0.530690537084399
//Mean-F1 = 0.581841432225063
//#############################
//
//
//#############################
//Restricted to: Template-Type & Field(s):birthPlaces
//Mean-Precisiion = 0.8439897698209716
//Mean-Recall = 0.7438192668371691
//Mean-F1 = 0.7726342710997459
//#############################
//
//
//#############################
//Restricted to: Field(s):birthYear
//Mean-Precisiion = 0.9923273657289002
//Mean-Recall = 0.9923273657289002
//Mean-F1 = 0.9923273657289002
//#############################
//
//
//#############################
//Restricted to: Template-Type & Field(s):birthYear
//Mean-Precisiion = 0.979539641943734
//Mean-Recall = 0.979539641943734
//Mean-F1 = 0.979539641943734
//#############################
//
//
//#############################
//Restricted to: Field(s):deathYear
//Mean-Precisiion = 0.8644501278772379
//Mean-Recall = 0.8644501278772379
//Mean-F1 = 0.8644501278772379
//#############################
//
//
//#############################
//Restricted to: Template-Type & Field(s):deathYear
//Mean-Precisiion = 0.9156010230179028
//Mean-Recall = 0.9808184143222506
//Mean-F1 = 0.9373401534526856
//#############################
//
//
//#############################
//Restricted to: Field(s):positionAmerican_football_positions
//Mean-Precisiion = 0.7813299232736572
//Mean-Recall = 0.7706734867860189
//Mean-F1 = 0.7736572890025577
//#############################
//
//
//#############################
//Restricted to: Template-Type & Field(s):positionAmerican_football_positions
//Mean-Precisiion = 0.9288150042625745
//Mean-Recall = 0.9087809036658144
//Mean-F1 = 0.9016197783461214
//#############################
//
//
//#############################
//Restricted to: Field(s):teamSoccerClubs
//Mean-Precisiion = 0.6240409207161125
//Mean-Recall = 0.616794543904518
//Mean-F1 = 0.5988917306052863
//#############################
//
//
//#############################
//Restricted to: Template-Type & Field(s):teamSoccerClubs
//Mean-Precisiion = 0.7493606138107419
//Mean-Recall = 0.7570332480818416
//Mean-F1 = 0.737535014005603
//#############################
//--------------randomRun-1710092315---------------
//Total training time: 1043465 ms.
//Total test time: 32311 ms.
//Total time: PT17M55.776S