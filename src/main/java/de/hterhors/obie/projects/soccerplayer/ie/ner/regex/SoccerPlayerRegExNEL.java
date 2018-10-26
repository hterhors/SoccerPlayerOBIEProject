package de.hterhors.obie.projects.soccerplayer.ie.ner.regex;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hterhors.obie.core.OntologyAnalyzer;
import de.hterhors.obie.core.ontology.AbstractIndividual;
import de.hterhors.obie.core.ontology.annotations.DatatypeProperty;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import de.hterhors.obie.ml.dtinterpreter.IDatatypeInterpretation;
import de.hterhors.obie.ml.ner.regex.AbstractRegExNER;
import de.hterhors.obie.projects.soccerplayer.ie.dtinterpreter.SoccerPlayerInterpreterProvider;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayer;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayerThing;

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

	protected Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> generateHandMadeCrossReferencesForClasses(
			Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> regularExpressionPattern,
			Class<? extends IOBIEThing> rootClassType) {
		/**
		 * Add cross reference pattern. A cross reference pattern is used for extend
		 * specific fields by pattern of other fields.
		 */
		return Collections.emptyMap();
	}

	protected Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> generateCrossReferencePatternForClasses(
			Class<? extends IOBIEThing> rootClassType) {
		return Collections.emptyMap();
	}

	protected Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> addHandMadePatternForClasses(
			Class<? extends IOBIEThing> rootClassType) {

		Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> relatedHandMadepattern = new HashMap<>();

		Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> allHandMadepattern = SoccerPlayerRegExPattern
				.getHandMadePattern();

		for (Class<? extends IOBIEThing> classType : OntologyAnalyzer.getRelatedClassTypesUnderRoot(rootClassType)) {

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
			OntologyAnalyzer.getRelatedClassTypesUnderRoot(rootClassType);
			throw new IllegalArgumentException(
					"STOP PROCESS! Unknown class for handmade pattern: " + rootClassType.getSimpleName());
		}
		return relatedHandMadepattern;
	}

	@Override
	protected Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> addFurtherPatternForClasses() {
		return Collections.emptyMap();
	}

	@Override
	protected Map<Class<? extends ISoccerPlayerThing>, Set<Pattern>> addPlainRegExPatternForClasses(
			Class<? extends IOBIEThing> interfaceClassType) {
		return new SoccerPlayerRegExPattern().autoGeneratePatternForClasses(interfaceClassType);
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

	@Override
	protected Map<AbstractIndividual, Set<Pattern>> addHandMadePatternForIndividuals(
			Class<? extends IOBIEThing> rootClassType) {
		return Collections.emptyMap();
	}

	@Override
	protected Map<AbstractIndividual, Set<Pattern>> addFurtherPatternForIndividuals() {
		return Collections.emptyMap();
	}

	@Override
	protected Map<AbstractIndividual, Set<Pattern>> generateHandMadeCrossReferencesForIndividuals(
			Map<AbstractIndividual, Set<Pattern>> regularExpressionPattern,
			Class<? extends IOBIEThing> rootClassType) {
		return Collections.emptyMap();
	}

	@Override
	protected Map<AbstractIndividual, Set<Pattern>> generateCrossReferencePatternForIndividuals(
			Class<? extends IOBIEThing> rootClassType) {
		return Collections.emptyMap();
	}

	@Override
	protected Map<AbstractIndividual, Set<Pattern>> addPlainRegExPatternForIndividuals(
			Class<? extends IOBIEThing> rootClassType) {
		return new SoccerPlayerRegExPattern().autoGeneratePatternForIndividuals(rootClassType);
	}

}
