package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie;

import java.io.File;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.projects.AbstractOBIEProjectEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.corpus.distributor.AbstractCorpusDistributor;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.corpus.distributor.ActiveLearningDistributor;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.corpus.distributor.FoldCrossCorpusDistributor;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.corpus.distributor.OriginalCorpusDistributor;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.corpus.distributor.ShuffleCorpusDistributor;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.evaluation.DatatypeOrListConditon;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.evaluation.evaluator.CartesianSearchEvaluator;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.evaluation.evaluator.IEvaluator;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.explorer.AbstractOBIEExplorer;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.explorer.IExplorationCondition;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.explorer.SlotCardinalityExplorer;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.explorer.TemplateExplorer;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.InvestigationRestriction;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.EInitializer;
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

		final AbstractOBIEProjectEnvironment environment = SoccerPlayerProjectEnvironment.getInstance();

		final AbstractCorpusDistributor corpusConfiguration = originDist();

		final String personalNote = "Reasoning Web Summer School 2018 Lecture";

		final File rootDirectory = new File("./");

		final int epochs = 100;

		final String corpusNamePrefix = "small";

		IExplorationCondition explorationCondition = (a, b, c) -> true;

		final EInitializer initializer = EInitializer.EMPTY;

		IInitializeNumberOfObjects numberOfInitializedObjects = instance -> 1;

		int numberOfMaxSamplingSteps = 100;

		final int maxEvaluationDepth = Integer.MAX_VALUE;
		final boolean penalizeCardinality = true;
		final boolean enableCaching = true;

		int maxNumberOfEntityElements = 3;
		int maxNumberOfDataTypeElements = 3;

		Random rndForSampling = new Random(100L);

		boolean ignoreEmptyInstancesOnEvaluation = false;

		// IEvaluator evaluator = new BeamSearchPRF1(enableCaching,
		// maxEvaluationDepth, penalizeCardinality,
		// sampleRestrictions, new SCIOOrListConditon(),
		// maxNumberOfEntityElements,
		// ignoreEmptyInstancesOnEvaluation);

		IEvaluator evaluator = new CartesianSearchEvaluator(enableCaching, maxEvaluationDepth, penalizeCardinality,
				InvestigationRestriction.noRestrictionInstance, new DatatypeOrListConditon(), maxNumberOfEntityElements,
				ignoreEmptyInstancesOnEvaluation);

		Set<Class<? extends AbstractOBIEExplorer>> explorerTypes = new HashSet<>();

		// explorerTypes.add(NoChangeExplorer.class);
		explorerTypes.add(TemplateExplorer.class);
//		explorerTypes.add(DependentCardinalityExplorer.class);

		explorerTypes.add(SlotCardinalityExplorer.class);
		// explorerTypes.add(TemplateCardinalityExplorer.class);

		return new OBIEParameterBuilder().setCorpusDistributor(corpusConfiguration)
				.setCorpusNamePrefix(corpusNamePrefix).setEpochs(epochs).setExplorationCondition(explorationCondition)
				.setExplorers(explorerTypes).setInitializer(initializer)
				.setNumberOfInitializedObjects(numberOfInitializedObjects)
				.setNumberOfMaxSamplingSteps(numberOfMaxSamplingSteps).setPersonalNotes(personalNote)
				.setRootDirectory(rootDirectory).addRootSearchType(searchType).setEnvironment(environment)
				.setEvaluator(evaluator).setMaxNumberOfEntityElements(maxNumberOfEntityElements)
				.setMaxNumberOfDataTypeElements(maxNumberOfDataTypeElements).setRandomForSampling(rndForSampling);

	}

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
		return new ActiveLearningDistributor.Builder().setB(1).setSeed(100L).setInitialTrainingSelectionFraction(0.01)
				.setTrainingProportion(80).setTestProportion(20).build();
	}
}
