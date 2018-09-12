package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.ner.regex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import de.uni.bielefeld.sc.hterhors.psink.obie.ie.ner.regex.BasicRegExPattern;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.classes.BirthYear;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayerThing;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.dtinterpreter.impl.BirthYearInterpreter;

public class SoccerPlayerRegExPattern extends BasicRegExPattern {

	private static Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> handMadepattern = null;

	private static final Set<Pattern> BIRTH_YEAR_REG_EXP = new HashSet<>(Arrays.asList(BirthYearInterpreter.PATTERN));

	public static Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> getHandMadePattern() {

		if (handMadepattern != null) {
			return handMadepattern;
		}

		handMadepattern = new HashMap<Class<? extends ISoccerPlayerThing>, Set<Pattern>>();

		handMadepattern.put(BirthYear.class, SoccerPlayerRegExPattern.BIRTH_YEAR_REG_EXP);
		
		return handMadepattern;
	}

}
