package de.hterhors.obie.projects.soccerplayer.examples;

import java.util.List;

import de.hterhors.obie.core.ontology.OntologyInitializer;
import de.hterhors.obie.ml.corpus.BigramCorpusProvider;
import de.hterhors.obie.ml.explorer.SlotFillerExplorer;
import de.hterhors.obie.ml.run.param.EInstantiationType;
import de.hterhors.obie.ml.run.param.OBIERunParameter;
import de.hterhors.obie.ml.run.param.OBIERunParameter.Builder;
import de.hterhors.obie.ml.utils.OBIEClassFormatter;
import de.hterhors.obie.ml.variables.OBIEInstance;
import de.hterhors.obie.ml.variables.OBIEState;
import de.hterhors.obie.projects.soccerplayer.ie.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.SoccerPlayerParameterQuickAccess;
import de.hterhors.obie.projects.soccerplayer.ie.SoccerPlayerProjectEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.templates.BirthYearTemplate;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.BirthYear;

/**
 * This class contains example code for different exploration strategies that
 * are used in the OBIE-framework.
 * 
 * The most important are:
 * 
 * 1) SlotFillerExplorer. Explores values of slots, does not change the number
 * of filler in a slot.
 * 
 * 2) SlotCardinalityExplorer Explores the number of fillers in a slot.
 * 
 * 3) TemplateCardinalityExplorer Explores the number of root-templates.
 * 
 * 4) EntityRecognitionAndLinkingExplorer Explores spans and assigned concepts
 * for Named Entity Recognition and Linking.
 * 
 * @author hterhors
 *
 */
public class ExplorationExample {

	public static void main(String[] args) {

		/*
		 * Initialize ontology if working with the framework.
		 */
		OntologyInitializer.initializeOntology(SoccerPlayerOntologyEnvironment.getInstance());

		/*
		 * Get some standard parameter. These are not important for this example. the
		 * more important parameter follow below:
		 */
		final Builder paramBuilder = getStandardParameter();

		/*
		 * Initialize new templates emptily! For more values see: EInstantiationType
		 */
		paramBuilder.setInstantiationType(EInstantiationType.EMPTY);

		/**
		 * Set to true if the exploration of possible slot filler should guided by the
		 * ontological classes instead of the annotations in the document. If set to
		 * true each possible slot filler type is explored only once. If set to false,
		 * slot filler types are based on the found entities in the text. E.g. Entity A
		 * was found in two different positions in the text. Both appearances are as
		 * slot filler. Datatype slots are always filled by textual evidence.
		 */
		paramBuilder.setExploreOnOntologyLevel(true);

		/**
		 * Set to true if the exploration should be filtered by entities that were
		 * previously found in the document rather than on all entities of the ontology.
		 */
		paramBuilder.setRestrictExplorationToFoundConcepts(true);

		/**
		 * Finalize the parameter set.
		 */
		final OBIERunParameter param = paramBuilder.build();

		/**
		 * Restore the corpus from file. This corpus already contains annotations for
		 * named entities! See BigramCorpusCreator.
		 * 
		 * Such entities can be used to sample if setExploreOnOntologyLevel was set to
		 * false and setRestrictExplorationToFoundConcepts was set to true.
		 */
		final BigramCorpusProvider corpusProvider = BigramCorpusProvider.loadCorpusFromFile(param);

		/**
		 * Get a "random" instance from the training set, which we use to explore. A
		 * instance is a document containing of textual content, NERL annotations, and
		 * Gold annotation of template types.
		 */
		final OBIEInstance instance = corpusProvider.getTrainingCorpus().getInternalInstances().get(1);

		/*
		 * Print document content
		 */
		System.out.println("_________" + instance.getName() + "_________");
		System.out.println(instance.getContent());
		System.out.println("___________________________");

		/*
		 * Print all entity annotations for a specific class. In this case BirthYear. We
		 * will find those BirthYear in the proposal states that are generated during
		 * exploration.
		 */
		instance.getNamedEntityLinkingAnnotations().getClassAnnotations(BirthYear.class).forEach(System.out::println);

		/*
		 * Exemplary chose one explorer.
		 */

		/**
		 * SlotFillerExplorer explores values for slots of a given template. It does not
		 * change the cardinality, although it ispossible to set values to null.
		 */
		SlotFillerExplorer slotFllerExplorer = new SlotFillerExplorer(param);

		/*
		 * Create initial state. The template that should be explored is generated in
		 * the state given the instantiation type previously set in the parameter.
		 */
		System.out.println("Initial state:");
		System.out.println("===========================");
		OBIEState state = new OBIEState(instance, param);
		System.out.println(OBIEClassFormatter.format(
				state.getCurrentTemplateAnnotations().getTemplateAnnotations().iterator().next().get()));
		System.out.println("===========================");

		/**
		 * Generate next proposal states.
		 */
		List<OBIEState> generatedClasses = slotFllerExplorer.getNextStates(state);

		/*
		 * Print proposal states.
		 */
		for (OBIEState proposalState : generatedClasses) {
			System.out.println("Proposal state:");
			System.out.println(proposalState);
			System.out.println("==================");
		}
		System.out.println("Number of generated proposal states: " + generatedClasses.size());

		/**
		 * TODO:
		 * 
		 * Experiment with:
		 * 
		 * paramBuilder.setInstantiationType(EInstantiationType.EMPTY);
		 * 
		 * paramBuilder.setExploreOnOntologyLevel(false);
		 * 
		 * paramBuilder.setRestrictExplorationToFoundConcepts(true);
		 * 
		 */
	}

	/**
	 * We need to set some standard parameter.
	 * 
	 * @return
	 */
	private static Builder getStandardParameter() {
		Builder paramBuilder = SoccerPlayerParameterQuickAccess.getREParameter();

		paramBuilder.setProjectEnvironment(SoccerPlayerProjectEnvironment.getInstance());
		paramBuilder.setOntologyEnvironment(SoccerPlayerOntologyEnvironment.getInstance());
		paramBuilder
				.setCorpusDistributor(SoccerPlayerParameterQuickAccess.preDefinedCorpusDistributor.originDist(1.0F));
		paramBuilder.addTemplate(BirthYearTemplate.class);

		return paramBuilder;
	}
}
