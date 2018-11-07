package de.hterhors.obie.projects.soccerplayer.ie.dtinterpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import de.hterhors.obie.ml.dtinterpreter.IDatatypeInterpretation;
import de.hterhors.obie.ml.dtinterpreter.IDatatypeInterpreter;
import de.hterhors.obie.ml.dtinterpreter.INumericInterpreter;
import de.hterhors.obie.projects.soccerplayer.ie.dtinterpreter.impl.BirthDeathYearInterpreter;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.BirthYear;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.DeathYear;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayerThing;

public class SoccerPlayerInterpreter implements IDatatypeInterpreter<ISoccerPlayerThing> {

	private static SoccerPlayerInterpreter instance = null;

	public static SoccerPlayerInterpreter getInstance() {

		if (instance == null)
			instance = new SoccerPlayerInterpreter();

		return instance;
	}

	@Override
	public List<IDatatypeInterpretation> getPossibleInterpretations(String textMention) {
		if (textMention == null || textMention.isEmpty())
			return null;
		/**
		 * TODO: Add more.
		 */
		List<IDatatypeInterpretation> interpretations = new ArrayList<>();

		INumericInterpreter semantics1 = new BirthDeathYearInterpreter.Builder().interprete(textMention).build()
				.normalize();
		if (semantics1.exists()) {
			interpretations.add(semantics1);
		}
//		INumericInterpreter semantics2 = new PlayerNumberInterpreter.Builder().interprete(textMention).build().normalize();
//		if (semantics2.exists()) {
//			interpretations.add(semantics2);
//		}

		return interpretations;
	}

	@Override
	public IDatatypeInterpretation interpret(Class<? extends ISoccerPlayerThing> classType, Matcher matcher) {

		IDatatypeInterpretation semantics = null;
		if (BirthYear.class == classType || DeathYear.class == classType) {
			semantics = new BirthDeathYearInterpreter.Builder().interprete(matcher.group()).build().normalize();
//		} else if (PlayerNumber.class == classType) {
//			semantics = new PlayerNumberInterpreter.Builder().interprete(matcher.group()).build().normalize();
		} else {
			throw new IllegalArgumentException("Unknown data type for: " + classType.getSimpleName());
		}

		if (semantics != null && semantics.exists()) {
			return semantics;
		}
		return semantics;
	}

	@Override
	public IDatatypeInterpretation interpret(Class<? extends ISoccerPlayerThing> classType, final String textMention) {

		final IDatatypeInterpretation semantics;
		if (textMention == null || textMention.isEmpty())
			return null;

		if (BirthYear.class == classType) {
			semantics = new BirthDeathYearInterpreter.Builder().interprete(textMention).build().normalize();
		} else {
			throw new IllegalArgumentException(
					"Unknown data type for(" + textMention + "): " + classType.getSimpleName());
		}
		return semantics;
	}

}
