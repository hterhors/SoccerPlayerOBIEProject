package de.hterhors.obie.projects.soccerplayer.examples;

import de.hterhors.obie.core.ontology.AbstractIndividual;
import de.hterhors.obie.core.ontology.OntologyInitializer;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import de.hterhors.obie.ml.corpus.BigramCorpusProvider;
import de.hterhors.obie.ml.ner.NERLClassAnnotation;
import de.hterhors.obie.ml.ner.NERLIndividualAnnotation;
import de.hterhors.obie.ml.run.param.RunParameter;
import de.hterhors.obie.ml.run.param.RunParameter.Builder;
import de.hterhors.obie.ml.utils.OBIEClassFormatter;
import de.hterhors.obie.ml.variables.OBIEInstance;
import de.hterhors.obie.ml.variables.TemplateAnnotation;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerProjectEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.parameter.SoccerPlayerParameterQuickAccess;
import de.hterhors.obie.projects.soccerplayer.ie.templates.BirthDeathYearTemplate;

public class PrintBigramCorpusExample {

	public static void main(String[] args) {
		{
			OntologyInitializer.initializeOntology(SoccerPlayerOntologyEnvironment.getInstance());
		}
		/*
		 * Get some standard parameter. These are not important for this example. the
		 * more important parameter follow below:
		 */
		final Builder paramBuilder = getStandardParameter();

		/**
		 * The distribution of the documents in the corpus. Origin takes training ,
		 * development and test data as they are. Use shufflDist() for shuffle the
		 * documents before and redistribute to train (80%), dev(0%) and test(20%). (You
		 * may change that distribution by building your own distributor...
		 */
		paramBuilder.setCorpusDistributor(SoccerPlayerParameterQuickAccess.predefinedDistributor.originDist(1F));

		/*
		 * Build parameter.
		 */
		RunParameter parameter = paramBuilder.build();

		/**
		 * Load the raw corpus from file system. This corpus contains only the document
		 * content and annotations of the template but NO named entity recognition and
		 * linking annotations!
		 */

		BigramCorpusProvider corpusProvider = BigramCorpusProvider.loadCorpusFromFile(parameter);

		for (OBIEInstance instance : corpusProvider.getFullCorpus().getInternalInstances()) {

			if (!instance.getName().endsWith("Vincenzo_Zinna"))
				continue;

			System.out.println("________" + instance.getName() + "________");
			System.out.println(instance.getContent());
			System.out.println(instance.getGoldAnnotation().getTemplateAnnotations());
			System.out.println(instance.getGoldAnnotation().getTemplateAnnotations().iterator().next().getThing());
			System.out.println("____________Template Annotation(s)____________");

			for (Class<? extends IOBIEThing> annotatedClass : instance.getNamedEntityLinkingAnnotations()
					.getAvailableClassTypes()) {
				for (NERLClassAnnotation classNERLAnnotation : instance.getNamedEntityLinkingAnnotations()
						.getClassAnnotations(annotatedClass)) {
					System.out.println(classNERLAnnotation);
				}
			}

			for (AbstractIndividual annotatedIndividual : instance.getNamedEntityLinkingAnnotations()
					.getAvailableIndividualTypes()) {
				for (NERLIndividualAnnotation individualNERLAnnotation : instance.getNamedEntityLinkingAnnotations()
						.getIndividualAnnotations(annotatedIndividual)) {
					System.out.println(individualNERLAnnotation);
				}
			}

			for (TemplateAnnotation templateAnnotation : instance.getGoldAnnotation().getTemplateAnnotations()) {
				System.out.println(OBIEClassFormatter.format(templateAnnotation.getThing()));
			}
			break;

		}
	}

	/**
	 * We need to set some standard parameter.
	 * 
	 * @return
	 */
	private static Builder getStandardParameter() {
		Builder paramBuilder = SoccerPlayerParameterQuickAccess.getBaseParameter();

		paramBuilder.setProjectEnvironment(SoccerPlayerProjectEnvironment.getInstance());
		paramBuilder.setOntologyEnvironment(SoccerPlayerOntologyEnvironment.getInstance());
		paramBuilder.addTemplate(BirthDeathYearTemplate.class);

		return paramBuilder;
	}
}
