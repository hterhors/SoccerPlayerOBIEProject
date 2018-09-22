package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.OBIERunParameter;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.templates.AbstractOBIETemplate;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.templates.scope.OBIEFactorScope;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.variables.EntityAnnotation;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.variables.NELAnnotation;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.variables.OBIEInstance;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.variables.OBIEState;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.classes.BirthYear;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.interfaces.IBirthYear;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayer;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.templates.BirthYearTemplate.Scope;
import factors.Factor;

/**
 * This template creates features that tell whether the assigned birth year of a
 * soccer player is the earliest year that can be found in the document text.
 * 
 * For that the scope of a factor is defined by the currently assigned year and
 * the document itself. The feature is then computed by comparing all possible
 * years that can be found in the text with the currently assigned one.
 * 
 * @author hterhors
 *
 */
public class BirthYearTemplate extends AbstractOBIETemplate<Scope> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LogManager.getFormatterLogger(BirthYearTemplate.class.getName());

	public BirthYearTemplate(OBIERunParameter parameter) {
		super(parameter);
	}

	class Scope extends OBIEFactorScope {

		/**
		 * The currently assigned year.
		 */
		final String assignedYear;

		/**
		 * The document that contains other annotations of years.
		 */
		final OBIEInstance currentInstance;

		public Scope(AbstractOBIETemplate<Scope> template, OBIEInstance currentInstance,
				final String semanticValue) {
			super(template, currentInstance, semanticValue);
			this.currentInstance = currentInstance;
			this.assignedYear = semanticValue;
		}

	}

	@Override
	public List<Scope> generateFactorScopes(OBIEState state) {
		List<Scope> factors = new ArrayList<>();

		/*
		 * For all soccer player in the document create an individual scope.
		 *
		 * In the lecture corpus there is only one soccer player per document.
		 *
		 */
		for (EntityAnnotation entityAnnotation : state.getCurrentPrediction().getEntityAnnotations()) {

			IBirthYear birthYear = ((ISoccerPlayer) entityAnnotation.getAnnotationInstance()).getBirthYear();

			/*
			 * If the birth year was not yet set, we don't need to generate any features
			 * here.
			 */
			if (birthYear == null)
				continue;

			final Scope scope = new Scope(this, state.getInstance(), birthYear.getTextMention());

			factors.add(scope);
		}

		return factors;
	}

	@Override
	public void computeFactor(Factor<Scope> factor) {

		final Set<NELAnnotation> possibleBirthYearAnnotations = factor.getFactorScope().currentInstance
				.getNamedEntityLinkingAnnotations().getAnnotationsBySemanticValues(BirthYear.class);

		final int assignedYear = Integer.parseInt(factor.getFactorScope().assignedYear);

		boolean isEarliestMentionedYear = true;

		for (NELAnnotation namedEntityLinkingAnnotation : possibleBirthYearAnnotations) {

			/*
			 * Note, that we do not have to skip the comparison with itself as we check
			 * less-or-equal.
			 */
			final int birthYearCandidate = Integer.parseInt(namedEntityLinkingAnnotation.textMention);

			isEarliestMentionedYear &= assignedYear <= birthYearCandidate;

			/*
			 * To speed up the process we can stop here if there is a year which is earlier.
			 */
			if (!isEarliestMentionedYear)
				break;
		}

		factor.getFeatureVector().set("Assigned year is earliest mentioned year in text", isEarliestMentionedYear);

	}

}
