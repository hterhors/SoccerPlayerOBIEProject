package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie;

import java.util.HashSet;
import java.util.Set;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.evaluation.PRF1Container;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.projects.AbstractOBIEProjectEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.corpus.distributor.AbstractCorpusDistributor;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.AbstractOBIERunner;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.StandardRERunner;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.OBIERunParameter;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.OBIERunParameter.OBIEParameterBuilder;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.templates.AbstractOBIETemplate;
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

	public static void main(String[] args) throws Exception {

		new StartExtraction();

	}

	/**
	 * The runID. This serves as an identifier for locating and saving the model. If
	 * anything was changed during the development the runID should be reset.
	 */
	private final static String runID = "-birthYearTemplate2";
	/**
	 * The systems environment.
	 */
	private final AbstractOBIEProjectEnvironment environment = SoccerPlayerProjectEnvironment.getInstance();

	public StartExtraction() throws Exception {

		/**
		 * This parameterBuilder contains standard configurations of the system that are
		 * used for Relation Extraction tasks. You can but may not change the parameter
		 * predefined in here unless you know what you are doing!
		 */
		final OBIEParameterBuilder paramBuilder = SoccerPlayerParameterQuickAccess.getREParameter();

		/*
		 * Add required missing parameter...
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
				.originDist();

		paramBuilder.setCorpusDistributor(corpusDistributor);
		paramBuilder.setRunID(runID);
		paramBuilder.setEnvironment(environment);
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

		run(runner);
	}

	/**
	 * Add templates to the parameter builder.
	 * 
	 * @param paramBuilder
	 */
	private void addTemplates(OBIEParameterBuilder paramBuilder) {

		final Set<Class<? extends AbstractOBIETemplate<?>>> templates = new HashSet<>();

		/**
		 * Add your own templates:
		 */
		templates.add(BirthYearTemplate.class);
		templates.add(PriorTemplate.class);

		/**
		 * Predefined and generic template:
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

//		templates.add(MainSlotVarietyTemplate.class);

		paramBuilder.setTemplates(templates);
	}

	/**
	 * Run the system with the specifications and configurations.
	 * 
	 * @param runner
	 * @throws Exception
	 */
	private static void run(AbstractOBIERunner runner) throws Exception {

		final long time = System.currentTimeMillis();

		if (runner.modelExists()) {
			/*
			 * If the model exists, load the model from the file system. The model location
			 * is specified in the parameter and the environment.
			 * 
			 * TODO: unify!
			 */
			runner.loadModel();
		} else {
			/*
			 * If the model does not exists train. The model is automatically stored to the
			 * file system to the given model location!
			 */
			runner.train();
		}

		System.out.println("Time needed to train the model: " + (System.currentTimeMillis() - time) + " ms.");

		/**
		 * Evaluate the trained model on the test data. This is equal to predictOnTest
		 * and apply the results to an evaluator.
		 */
		final PRF1Container pfr1 = runner.evaluateOnTest();

		System.out.println("Evaluation results on test data:\n" + pfr1);

	}

}
