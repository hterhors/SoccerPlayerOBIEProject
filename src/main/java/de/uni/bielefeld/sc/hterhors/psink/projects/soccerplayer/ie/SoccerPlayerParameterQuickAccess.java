package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie;

import java.io.File;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import de.uni.bielefeld.sc.hterhors.psink.obie.ie.corpus.distributor.AbstractCorpusDistributor;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.corpus.distributor.ActiveLearningDistributor;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.corpus.distributor.FoldCrossCorpusDistributor;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.corpus.distributor.OriginalCorpusDistributor;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.corpus.distributor.ShuffleCorpusDistributor;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.evaluation.DatatypeOrListConditon;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.evaluation.evaluator.CartesianSearchEvaluator;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.evaluation.evaluator.IEvaluator;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.explorer.AbstractOBIEExplorer;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.explorer.SlotCardinalityExplorer;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.explorer.TemplateExplorer;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.InvestigationRestriction;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.EInstantiationType;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.IInitializeNumberOfObjects;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.OBIERunParameter.OBIEParameterBuilder;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayer;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayerThing;

public class SoccerPlayerParameterQuickAccess {

	/**
	 * The corpus data type defines the data that is stored in the provided corpus.
	 * This might be the same class as the search type or somehow related to the
	 * search type. However, if the search type is unrelated to the corpus type
	 * there wont be any data to train / predict.
	 * 
	 * You can even provide more than one type to create a bigger corpus and then
	 * select just one or a subset type using the search type.
	 * 
	 */
	final private static Class<? extends ISoccerPlayerThing> corpusType = ISoccerPlayer.class;

	/**
	 * The search type defines the root class type to sample. This should be the
	 * same as the corpus type or ontological below. I.e. the corpus the might be
	 * OrganismModel, but you could still search only or RatModels.
	 *
	 * Usually the search type is the same as the corpus type.
	 *
	 */
	final private static Class<? extends ISoccerPlayerThing> searchType = corpusType;

	public static OBIEParameterBuilder getREParameter() {

		/**
		 * Some personal notes to this run.
		 */
		final String personalNote = "Reasoning Web Summer School 2018 Lecture";

		/**
		 * the rott directory to where the model should be saved.
		 */
		final File rootDirectory = new File("./");

		/**
		 * The prefix name of the corpus.
		 */
		final String corpusNamePrefix = "rwss2018";

		/**
		 * The type of the instantiated main templates. Use empty if the templates
		 * should be created empty in new states. For more types see EInstantiationType.
		 */
		final EInstantiationType initializer = EInstantiationType.EMPTY;

		/**
		 * The number of main templates that should be instantiated initially for empty
		 * states.
		 */
		final IInitializeNumberOfObjects numberOfInitializedObjects = instance -> 1;

		/**
		 * A template may have a slot that filler has a slot that iller has a slot and
		 * so on. This restricts the maximum evaluation depth. The depth is equal to the
		 * number of sub-slots. Set this to 1 if you want e.g. just the main slots of
		 * the template to be evaluated. For specific evaluation restrictions specify
		 * restrictions in the InvestigationRestiction.
		 */
		final int maxEvaluationDepth = Integer.MAX_VALUE;

		/**
		 * Whether the number of instances in collections-typed slots should be part of
		 * the evaluation or not. In realcase Scenario this needs to be set to true.
		 * 
		 * If set to false the actual number of elements in a list or number of
		 * instantiated templates is not evaluated.
		 */
		final boolean penalizeCardinality = true;

		/**
		 * Whether caching should be activated during evaluation or not. If your system
		 * provides much RAm you might consider setting this to true to speed up the
		 * evaluation process. This makes especially sense when using the
		 * CartesianSearchEvaluator.
		 */
		final boolean enableEvaluationCaching = true;

		/**
		 * The maximum number of entity elements per list or maximum number of
		 * instances. This variable restricts the sampling procedure and needs to be
		 * adjust to the dataset.
		 */
		final int maxNumberOfEntityElements = 3;

		/**
		 * Maximum number of datatype entities in a collection slot that takes datatype
		 * entities as filler. This restricts the sampling procedure and needs to be
		 * adjust to the dataset.
		 */
		final int maxNumberOfDataTypeElements = 3;

		/**
		 * Use this class to specify a fine grained investigation restriction that
		 * affects not only the evaluation but also the sampling procedure.
		 * 
		 * You can add a concrete specification of what slots should be investigated or
		 * not.
		 * 
		 * USE WITH CAUTION! See documentation of InvestigationRestiction for remaining
		 * todos.
		 * 
		 */
		final InvestigationRestriction investigationRestiction = InvestigationRestriction.noRestrictionInstance;

		/**
		 * Fix the randomization of the sampling procedure.
		 */
		final Random rndForSampling = new Random(100L);

		/**
		 * Whether empty created instances should be removed from the evaluation
		 * beforehand. If this is set to true, a state with hundreds of empty instances
		 * and one filled is as good as a state that have only the filled instance.
		 *
		 * During training and prediction this should be set to false. You might
		 * consider setting this variable to true in the final evaluation.
		 */
		final boolean ignoreEmptyInstancesOnEvaluation = false;

		/**
		 * The evaluation type. CartesianSearch Evaluator is the most precise evaluation
		 * as it computes the exact score. In some cases, if the data set is to large or
		 * the number of elements in lists are to high, this evaluation may be to slow
		 * as it needs to compute the factorial number of comparisons.
		 *
		 * Try instead BeamSearchEvaluator or PurityEvaluator.
		 *
		 */
		final IEvaluator evaluator = new CartesianSearchEvaluator(enableEvaluationCaching, maxEvaluationDepth,
				penalizeCardinality, investigationRestiction, new DatatypeOrListConditon(), maxNumberOfEntityElements,
				ignoreEmptyInstancesOnEvaluation);

		/**
		 * The exploration strategies.
		 */
		final Set<Class<? extends AbstractOBIEExplorer>> explorerTypes = new HashSet<>();

		/**
		 * Uses the ontology for creating proposal states. Template slots are filled by
		 * possible slot filler that are defined in the ontology. Per default
		 * ontological classes that were not found in the document (via named entity
		 * recognition and linking) are removed from the candidate set.
		 *
		 * Datatype slots (marked with the @DatatypeProperty.java) annotation can only
		 * be filled with previously found datatype entities!
		 *
		 * The TemplateExplorer can be parameterized via the OBIEParamterBuilder!
		 *
		 */
		explorerTypes.add(TemplateExplorer.class);

		/**
		 * This explorer explores the cardinality of slots that can have more than one
		 * entry. Such slots (java class fields) are makred with the
		 * annotation @RelationTypeCollection.java
		 */
		explorerTypes.add(SlotCardinalityExplorer.class);

		/**
		 * There are many more parameter to check out:
		 */
		return new OBIEParameterBuilder().setCorpusNamePrefix(corpusNamePrefix).setExplorers(explorerTypes)
				.setInitializer(initializer).setNumberOfInitializedObjects(numberOfInitializedObjects)
				.setPersonalNotes(personalNote).setRootDirectory(rootDirectory).addRootSearchType(searchType)
				.setEvaluator(evaluator).setMaxNumberOfEntityElements(maxNumberOfEntityElements)
				.setMaxNumberOfDataTypeElements(maxNumberOfDataTypeElements).setRandomForSampling(rndForSampling);

	}

	public static class preDefinedCorpusDistributor {

		public static AbstractCorpusDistributor shuffleDist() {
			return new ShuffleCorpusDistributor.Builder().setTrainingProportion(80).setDevelopmentProportion(0)
					.setTestProportion(20).build();
		}

		public static AbstractCorpusDistributor originDist() {
			return new OriginalCorpusDistributor.Builder().build();
		}

		public static AbstractCorpusDistributor foldCrossDist() {
			return new FoldCrossCorpusDistributor.Builder().setN(10).setSeed(12345L).build();
		}

		public static AbstractCorpusDistributor activeLearningDist() {
			return new ActiveLearningDistributor.Builder().setB(1).setSeed(100L)
					.setInitialTrainingSelectionFraction(0.01).setTrainingProportion(80).setTestProportion(20).build();
		}
	}
}
