package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.ner.regex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.OntologyAnalyzer;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.DatatypeProperty;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.dtinterpreter.IDatatypeInterpretation;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.ner.regex.AbstractRegExNER;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.ner.regex.BasicRegExPattern;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayer;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayerThing;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.dtinterpreter.SoccerPlayerInterpreterProvider;

public class SoccerPlayerRegExNEL extends AbstractRegExNER<ISoccerPlayerThing> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int MIN_NER_LENGTH = 2;

	public static Logger log = LogManager.getFormatterLogger(SoccerPlayerRegExNEL.class.getSimpleName());

	public SoccerPlayerRegExNEL(Set<Class<? extends IOBIEThing>> rootClasses) {
		super(rootClasses);
	}

	protected Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> generateHandMadeCrossReferences(
			Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> regularExpressionPattern,
			Class<? extends IOBIEThing> rootClassType) {

		Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> patternDependendCrossReferencePattern = new HashMap<>();
		/**
		 * Add cross reference pattern. A cross reference pattern is used for extend
		 * specific fields by pattern of other fields.
		 */
		return patternDependendCrossReferencePattern;
	}

	protected Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> generateCrossReferencePattern(
			Class<? extends IOBIEThing> rootClassType) {
		Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> patternDependendCrossReferencePattern = new HashMap<>();
		return patternDependendCrossReferencePattern;
	}

	protected Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> addHandMadePattern(
			Class<? extends IOBIEThing> rootClassType) {

		Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> relatedHandMadepattern = new HashMap<>();

		Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> allHandMadepattern = SoccerPlayerRegExPattern
				.getHandMadePattern();

		for (Class<? extends IOBIEThing> classType : OntologyAnalyzer.getRelatedClassesTypesUnderRoot(rootClassType)) {

			if (classType.isAnnotationPresent(DatatypeProperty.class) && !allHandMadepattern.containsKey(classType)) {
				log.warn("WARN!!! No hand made pattern for data type property: " + classType.getSimpleName());
			}

			if (allHandMadepattern.containsKey(classType)) {
				relatedHandMadepattern.put((Class<ISoccerPlayerThing>) classType, allHandMadepattern.get(classType));
			}
		}

		if (rootClassType == ISoccerPlayer.class) {
		} else {
			/*
			 * TODO: add more hand made pattern HandMade Pattern
			 */
			log.warn("STOP PROCESS! Unknown class for handmade pattern: " + rootClassType.getSimpleName());
			OntologyAnalyzer.debug = true;
			OntologyAnalyzer.getRelatedClassesTypesUnderRoot(rootClassType);
			throw new IllegalArgumentException(
					"STOP PROCESS! Unknown class for handmade pattern: " + rootClassType.getSimpleName());
		}
		return relatedHandMadepattern;
	}

	@Override
	protected Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> addFurtherPattern() {
		return new HashMap<>();
	}

	@Override
	protected Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> addPlainRegExPattern(
			Class<? extends IOBIEThing> interfaceClassType) {
		return BasicRegExPattern.<ISoccerPlayerThing>autoGeneratePattern(interfaceClassType);
	}

	@Override
	protected IDatatypeInterpretation getSemanticInterpretation(Class<? extends ISoccerPlayerThing> dataTypeClass,
			Matcher matcher) {
		return SoccerPlayerInterpreterProvider.getInstance().interpret(dataTypeClass, matcher);
	}

	@Override
	protected int getMinNERLength() {
		return MIN_NER_LENGTH;
	}

}
