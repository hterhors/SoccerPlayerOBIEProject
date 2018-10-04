package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.examples;

import java.util.List;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.OntologyInitializer;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.corpus.OBIECorpus;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.corpus.OBIECorpus.Instance;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.corpus.BigramCorpusProvider;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.evaluation.evaluator.CartesianSearchEvaluator;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.explorer.SlotFillerExplorer;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.EInstantiationType;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.OBIERunParameter;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.OBIERunParameter.OBIEParameterBuilder;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.utils.OBIEClassFormatter;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.variables.OBIEInstance;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.variables.OBIEState;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.classes.American_football_positions;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.classes.BirthYear;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.classes.Place;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.classes.SoccerPlayer;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.SoccerPlayerOntologyEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.SoccerPlayerParameterQuickAccess;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.SoccerPlayerProjectEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.templates.BirthYearTemplate;

/**
 * TODO: write documentation and comments!
 * 
 * @author hterhors
 *
 */
public class ExampleCode {

	public static void main(String[] args) {

		OntologyInitializer.initializeOntology(SoccerPlayerOntologyEnvironment.getInstance());

//		printRawCorpus();

//		exploreEntities();

		evaluateEntities();

	}

	private static void printRawCorpus() {

		OBIECorpus rawCorpus = OBIECorpus
				.readRawCorpusData(SoccerPlayerProjectEnvironment.getInstance().getRawCorpusFile());

		for (Instance instance : rawCorpus.getInstances().values()) {

			System.out.println("________" + instance.name + "________");
			System.out.println(instance.content);
			System.out.println("____________Template Annotation(s)____________");

			for (List<IOBIEThing> templateAnnotations : instance.annotations.values()) {
				for (IOBIEThing templateAnnotation : templateAnnotations) {
					System.out.println(OBIEClassFormatter.format(templateAnnotation));
				}

			}
			break;

		}
	}

	private static void evaluateEntities() {
		/**
		 * Ground truth Herbie
		 */
		SoccerPlayer goldHerbie = new SoccerPlayer("http://dbpedia.org/resource/Sadok_Sassi", null, null);
		goldHerbie.setPositionAmerican_football_positions(new American_football_positions(
				"http://dbpedia.org/resource/Goalkeeper_(association_football)", null, null));
		goldHerbie.setBirthYear(new BirthYear("2000"));

		/**
		 * Perfect prediction
		 */
		SoccerPlayer perfectHerbie = new SoccerPlayer("http://dbpedia.org/resource/Sadok_Sassi", null, null);
		perfectHerbie.setPositionAmerican_football_positions(new American_football_positions(
				"http://dbpedia.org/resource/Goalkeeper_(association_football)", null, null));
		perfectHerbie.setBirthYear(new BirthYear("2000"));

		/**
		 * Predicted Herbie
		 */
		SoccerPlayer predictedHerbie = new SoccerPlayer("http://dbpedia.org/resource/Herbie_Williams", null, null);
		predictedHerbie.setPositionAmerican_football_positions(new American_football_positions(
				"http://dbpedia.org/resource/Goalkeeper_(association_football)", null, null));
		predictedHerbie.setBirthYear(new BirthYear("1999"));

		/**
		 * Predicted Robbie
		 */
		SoccerPlayer predictedRobbie = new SoccerPlayer("http://dbpedia.org/resource/Sadok_Sassi", null, null);
		predictedRobbie.setPositionAmerican_football_positions(new American_football_positions(
				"http://dbpedia.org/resource/Goalkeeper_(association_football)", null, null));
		predictedRobbie.setBirthYear(new BirthYear("2000"));
		predictedRobbie.addBirthPlace(new Place("http://dbpedia.org/resource/Wales", null, null));

		/**
		 * Exact search
		 */
		CartesianSearchEvaluator evaluator = new CartesianSearchEvaluator();

		System.out.println(evaluator.prf1(goldHerbie, goldHerbie));
		System.out.println(evaluator.prf1(goldHerbie, perfectHerbie));
		System.out.println(evaluator.prf1(goldHerbie, predictedHerbie));
		System.out.println(evaluator.prf1(goldHerbie, predictedRobbie));
	}

	private static void exploreEntities() {
		OBIEParameterBuilder paramBuilder = getParameter();

		/**
		 * Initialize with empty state!
		 */
		paramBuilder.setInitializer(EInstantiationType.EMPTY);

		OBIERunParameter param = paramBuilder.build();

		BigramCorpusProvider corpusProvider = BigramCorpusProvider.loadCorpusFromFile(param);

		/*
		 * George_Howe_(footballer)
		 */
		OBIEInstance instance = corpusProvider.getTrainingCorpus().getInternalInstances().get(1);

		System.out.println("_________" + instance.getName() + "_________");
		System.out.println(instance.getContent());
		System.out.println("___________________________");

		/*
		 * print all birth years
		 */
		instance.getNamedEntityLinkingAnnotations().getClassAnnotations(BirthYear.class).forEach(System.out::println);

		/**
		 * Test explorer
		 */
		SlotFillerExplorer te = new SlotFillerExplorer(param);
//		TemplateCardinalityExplorer tce = new TemplateCardinalityExplorer(param);
//		SlotCardinalityExplorer sce = new SlotCardinalityExplorer(param);

		System.out.println("===========================");
		OBIEState state = new OBIEState(instance, param);
		System.out.println("Current state:");
		System.out.println(OBIEClassFormatter.format(
				state.getCurrentPrediction().getTemplateAnnotations().iterator().next().getTemplateAnnotation()));
		System.out.println("===========================");
		int size = 0;
		List<OBIEState> generatedClasses = te.getNextStates(state);

		for (OBIEState scioClass : generatedClasses) {
			System.out.println(scioClass);
			System.out.println("==================");
			size++;
		}
		System.out.println(size);
	}

	private static OBIEParameterBuilder getParameter() {
		OBIEParameterBuilder paramBuilder = SoccerPlayerParameterQuickAccess.getREParameter();

		paramBuilder.setProjectEnvironment(SoccerPlayerProjectEnvironment.getInstance());
		paramBuilder
				.setCorpusDistributor(SoccerPlayerParameterQuickAccess.preDefinedCorpusDistributor.originDist(1.0F));
		paramBuilder.addTemplate(BirthYearTemplate.class);

		return paramBuilder;
	}
}
