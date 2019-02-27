package de.hterhors.obie.projects.soccerplayer.examples;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.hterhors.obie.core.ontology.InvestigationRestriction;
import de.hterhors.obie.core.ontology.InvestigationRestriction.RestrictedField;
import de.hterhors.obie.core.ontology.OntologyInitializer;
import de.hterhors.obie.ml.evaluation.evaluator.BeamSearchEvaluator;
import de.hterhors.obie.ml.evaluation.evaluator.CartesianSearchEvaluator;
import de.hterhors.obie.ml.evaluation.evaluator.IOBIEEvaluator;
import de.hterhors.obie.ml.evaluation.evaluator.PurityEvaluator;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.American_football_positions;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.BirthYear;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.Place;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.SoccerPlayer;

/**
 * * In this example we create three SoccerPlayer-class that follow the
 * OBIE-framework structure and compare them using different evaluation
 * strategies.
 * 
 * There are three provided strategies:
 * 
 * 1) CartesianSearchEvaluator slow but exact. Use for simple-mid-complex
 * template slot filling
 * 
 * 2) BeamSearchEvaluator parameterizable tradeoff between exact and slow. Use
 * if CartesianSearchEvaluator is to slow or you have to relay on many slot
 * fillers per slot.
 * 
 * 3) PurityEvaluator fast but not exact. Use if BeamSearch is to slow.
 * 
 * @author hterhors
 *
 */
public class EvaluationExample {

	public static void main(String[] args) {

		/*
		 * Initialize ontology framework.
		 */
		OntologyInitializer.initializeOntology(SoccerPlayerOntologyEnvironment.getInstance());

		Set<RestrictedField> fields = new HashSet<>();

		fields.add(new RestrictedField("teamSoccerClubs", true));
		fields.add(new RestrictedField("birthYear", true));

		/*
		 * Create some templates and fill slots...
		 */
		InvestigationRestriction i = new InvestigationRestriction(fields, true);

		/**
		 * Create a new SoccerPlayer-class for the individual Sadok Sassi. This
		 * individual is part of the ontology (see owl/soccer_player_ontology_v1.owl).
		 */
		SoccerPlayer perfectSadokSassi = new SoccerPlayer("http://dbpedia.org/resource/Sadok_Sassi", null, null);
		SoccerPlayer perfectSadokSassi2 = new SoccerPlayer("http://dbpedia.org/resource/Sadok_Sassi", null, null);
		SoccerPlayer perfectSadokSassi3 = new SoccerPlayer("http://dbpedia.org/resource/Sadok_Sassi", null, null);
		/*
		 * Fill some slots manually...
		 */
		perfectSadokSassi.addPositionAmerican_football_positions(new American_football_positions(
				"http://dbpedia.org/resource/Goalkeeper_(association_football)", null, null));
		perfectSadokSassi.setBirthYear(new BirthYear("1000"));
		
		perfectSadokSassi2.setBirthYear(new BirthYear("2000"));
		perfectSadokSassi3.setBirthYear(new BirthYear("3000"));

		/**
		 * Create a default evaluator using the exact-search algorithm.
		 */
		CartesianSearchEvaluator exactEvaluator = new CartesianSearchEvaluator();
		System.out.println(exactEvaluator.prf1(perfectSadokSassi, perfectSadokSassi));
		System.out.println(exactEvaluator.prf1(perfectSadokSassi, perfectSadokSassi3));
		System.out.println(exactEvaluator.prf1(perfectSadokSassi2, perfectSadokSassi3));
		System.out.println(exactEvaluator.prf1(Arrays.asList(perfectSadokSassi, perfectSadokSassi2),
				Arrays.asList(perfectSadokSassi3, perfectSadokSassi)));

//		compareToNotPerfectSadokSassi(perfectSadokSassi, exactEvaluator, i);
		/*
		 * Interpreting the result:
		 * 
		 * PRF1 [tp=4.0, fp=1.0, fn=1.0, getF1()=0.8, getRecall()=0.8,
		 * getPrecision()=0.8, getJaccard()=0.66]
		 * 
		 * The result shows:
		 * 
		 * 4 TP: Successfully found a class SoccerPlayer, the individual, assign a
		 * position and the position was correct
		 * 
		 * 1 FP: We assigned a wrong birth year.
		 * 
		 * 1 FN: We missed the birth year.
		 */

//		compareToBimalMagar(perfectSadokSassi, exactEvaluator);

		/*
		 * Interpreting the result:
		 * 
		 * PRF1 [tp=4.0, fp=1.0, fn=1.0, getF1()=0.8, getRecall()=0.8,
		 * getPrecision()=0.8, getJaccard()=0.66]
		 * 
		 * The result shows:
		 * 
		 * 3 TP: Successfully found a class SoccerPlayer, assign a position was correct
		 * and the birth year was correct.
		 * 
		 * 4 FP: Wrong individual, wrongly assigned a place and the actual place is
		 * (obviously) also wrong like the assigned position.
		 * 
		 * 2 FN: We missed the individual and the value of the position.
		 */

		/*
		 * Possible other evaluators:
		 */

		/**
		 * BeamSearchEvaluator performs a beam search for finding the best assignment if
		 * slots and slot-slots* have multiple possible values. A beam size of infinite
		 * is equal to an exact search.
		 */
		new BeamSearchEvaluator(1);

		/**
		 * PurityEvaluator performs a comparison using the purity function presented by
		 * Amigo et al. 2011.
		 */
		new PurityEvaluator();

	}

	/**
	 * Compares a given filled template to a predefined filled template which
	 * differs in some slots.
	 * 
	 * @param perfectSadokSassi
	 * @param evaluator
	 * @param i
	 */
	private static void compareToNotPerfectSadokSassi(SoccerPlayer perfectSadokSassi, IOBIEEvaluator evaluator,
			InvestigationRestriction i) {
		/**
		 * Create a second SoccerPlayer for the individual Sadok Sassi. However, this
		 * time some slots are filled differently! We set the BirthYear to 1990 instead
		 * of 2000.
		 */
		SoccerPlayer imperfectSadokSassi = new SoccerPlayer("http://dbpedia.org/resource/Sadok_Sassi", i, null)
				.addPositionAmerican_football_positions(new American_football_positions(
						"http://dbpedia.org/resource/Goalkeeper_(association_football)", null, null))
				.setBirthYear(new BirthYear("1990"));

		/*
		 * Compare both entities.
		 */
		System.out.println(evaluator.prf1(perfectSadokSassi, imperfectSadokSassi));
	}

	/**
	 * Compares a given filled template with a predefined filled template which
	 * differs in many slots.
	 * 
	 * @param perfectSadokSassi
	 * @param evaluator
	 */
	private static void compareToBimalMagar(SoccerPlayer perfectSadokSassi, IOBIEEvaluator evaluator) {

		/**
		 * Create a SoccerPlayer for individual Bimal Magar.
		 */
		SoccerPlayer bimalMagar = new SoccerPlayer("http://dbpedia.org/resource/Bimal_Magar", null, null)
				.addPositionAmerican_football_positions(
						new American_football_positions("http://dbpedia.org/resource/Inside_forward", null, null))
				.setBirthYear(new BirthYear("2000"))
				.addBirthPlace(new Place("http://dbpedia.org/resource/Wales", null, null));

		/*
		 * Compare to perfect Sadok Sassi.
		 */
		System.out.println(evaluator.prf1(perfectSadokSassi, bimalMagar));

	}
}
