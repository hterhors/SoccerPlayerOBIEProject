package de.hterhors.obie.projects.soccerplayer.examples;

import de.hterhors.obie.core.ontology.AbstractOBIEIndividual;
import de.hterhors.obie.core.ontology.OntologyInitializer;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import de.hterhors.obie.ml.corpus.BigramCorpusProvider;
import de.hterhors.obie.ml.ner.NERLClassAnnotation;
import de.hterhors.obie.ml.ner.NERLIndividualAnnotation;
import de.hterhors.obie.ml.run.param.OBIERunParameter;
import de.hterhors.obie.ml.run.param.OBIERunParameter.Builder;
import de.hterhors.obie.ml.utils.OBIEClassFormatter;
import de.hterhors.obie.ml.variables.OBIEInstance;
import de.hterhors.obie.ml.variables.TemplateAnnotation;
import de.hterhors.obie.projects.soccerplayer.ie.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.SoccerPlayerParameterQuickAccess;
import de.hterhors.obie.projects.soccerplayer.ie.SoccerPlayerProjectEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.templates.BirthYearTemplate;

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
		OBIERunParameter parameter = paramBuilder.build();

		/**
		 * Load the raw corpus from file system. This corpus contains only the document
		 * content and annotations of the template but NO named entity recognition and
		 * linking annotations!
		 */

		BigramCorpusProvider corpusProvider = BigramCorpusProvider.loadCorpusFromFile(parameter);

		for (OBIEInstance instance : corpusProvider.getFullCorpus().getInternalInstances()) {

			System.out.println("________" + instance.getName() + "________");
			System.out.println(instance.getContent());
			System.out.println("____________Template Annotation(s)____________");

			for (Class<? extends IOBIEThing> annotatedClass : instance.getNamedEntityLinkingAnnotations()
					.getAvailableClassTypes()) {
				for (NERLClassAnnotation classNERLAnnotation : instance.getNamedEntityLinkingAnnotations()
						.getClassAnnotations(annotatedClass)) {
					System.out.println(classNERLAnnotation);
				}
			}

			for (AbstractOBIEIndividual annotatedIndividual : instance.getNamedEntityLinkingAnnotations()
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
		Builder paramBuilder = SoccerPlayerParameterQuickAccess.getREParameter();

		paramBuilder.setProjectEnvironment(SoccerPlayerProjectEnvironment.getInstance());
		paramBuilder.setOntologyEnvironment(SoccerPlayerOntologyEnvironment.getInstance());
		paramBuilder.addTemplate(BirthYearTemplate.class);

		return paramBuilder;
	}
}
