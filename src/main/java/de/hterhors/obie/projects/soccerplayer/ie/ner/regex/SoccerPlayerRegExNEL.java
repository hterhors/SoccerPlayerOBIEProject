package de.hterhors.obie.projects.soccerplayer.ie.ner.regex;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hterhors.obie.core.ontology.AbstractIndividual;
import de.hterhors.obie.ml.ner.regex.AbstractRegExNERL;
import de.hterhors.obie.projects.soccerplayer.ie.dtinterpreter.SoccerPlayerInterpreter;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayerThing;

public class SoccerPlayerRegExNEL extends AbstractRegExNERL<ISoccerPlayerThing> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int MIN_NER_LENGTH = 2;

	public static Logger log = LogManager.getFormatterLogger(SoccerPlayerRegExNEL.class.getSimpleName());

	public SoccerPlayerRegExNEL(Set<Class<? extends ISoccerPlayerThing>> rootClasses) {
		super(rootClasses, new SoccerPlayerRegExPattern(), SoccerPlayerInterpreter.getInstance(), MIN_NER_LENGTH);
	}

	public SoccerPlayerRegExNEL(Class<? extends ISoccerPlayerThing> rootClass) {
		this(new HashSet<>(Arrays.asList(rootClass)));
	}

	@Override
	protected Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> getAdditionalPatternForClasses(
			Class<? extends ISoccerPlayerThing> rootClassType) {
		return Collections.emptyMap();

	}

	@Override
	protected Map<AbstractIndividual, Set<Pattern>> getAdditionalPatternForIndividuals(
			Class<? extends ISoccerPlayerThing> rootClassType) {
		return Collections.emptyMap();

	}

}
