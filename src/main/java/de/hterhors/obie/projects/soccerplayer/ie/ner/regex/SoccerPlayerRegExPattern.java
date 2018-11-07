package de.hterhors.obie.projects.soccerplayer.ie.ner.regex;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import de.hterhors.obie.core.ontology.AbstractIndividual;
import de.hterhors.obie.ml.ner.regex.BasicRegExPattern;
import de.hterhors.obie.projects.soccerplayer.ie.dtinterpreter.impl.BirthDeathYearInterpreter;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.BirthYear;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.DeathYear;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayerThing;

public class SoccerPlayerRegExPattern extends BasicRegExPattern<ISoccerPlayerThing> {

	private Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> handMadepattern = null;

	private static final Set<Pattern> BIRTH_DEATH_YEAR_REG_EXP = new HashSet<>(
			Arrays.asList(BirthDeathYearInterpreter.PATTERN));
//	private static final Set<Pattern> PLAYER_NUMBER_YEAR_REG_EXP = new HashSet<>(
//			Arrays.asList(PlayerNumberInterpreter.PATTERN));

	/**
	 * Common words that appears very often in many different contexts.
	 */
	private static final Set<String> SOCCER_PLAYER_STOP_WORDS = new HashSet<>(Arrays.asList("F.C.", "F.", "C.", "A.",
			"FC", "American", "football", "positions", "association", "sports", ""));

	@Override
	public Set<String> getAdditionalStopWords() {
		return SOCCER_PLAYER_STOP_WORDS;
	}

	@Override
	public int getMinTokenlength() {
		return 3;
	}

	@Override
	public Map<AbstractIndividual, Set<Pattern>> getHandMadePatternForIndividuals() {
		return Collections.emptyMap();
	}

	@Override
	public Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> getHandMadePatternForClasses() {
		if (handMadepattern != null) {
			return handMadepattern;
		}

		handMadepattern = new HashMap<Class<? extends ISoccerPlayerThing>, Set<Pattern>>();

		handMadepattern.put(BirthYear.class, SoccerPlayerRegExPattern.BIRTH_DEATH_YEAR_REG_EXP);
		handMadepattern.put(DeathYear.class, SoccerPlayerRegExPattern.BIRTH_DEATH_YEAR_REG_EXP);
//		handMadepattern.put(PlayerNumber.class, SoccerPlayerRegExPattern.PLAYER_NUMBER_YEAR_REG_EXP);

		return handMadepattern;
	}

}
