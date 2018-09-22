package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.templates;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.OBIERunParameter;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.templates.AbstractOBIETemplate;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.templates.scope.OBIEFactorScope;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.variables.EntityAnnotation;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.variables.OBIEState;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.classes.BirthYear;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayer;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.templates.BirthYearPriorTemplate.Scope;
import factors.Factor;

/**
 * This is an empty template that serves as code-template to create new ones.
 * 
 * @author hterhors
 *
 */
public class BirthYearPriorTemplate extends AbstractOBIETemplate<Scope> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LogManager.getFormatterLogger(BirthYearPriorTemplate.class.getName());

	public BirthYearPriorTemplate(OBIERunParameter parameter) {
		super(parameter);
	}

	/**
	 * Define a factor scope for this template
	 * 
	 * @author hterhors
	 *
	 */
	class Scope extends OBIEFactorScope {

		/*
		 * TODO: add variables for feature computation.
		 */

		int birthYear;

		public Scope(AbstractOBIETemplate<Scope> template, int birthYear) {
			super(template, birthYear);
			this.birthYear = birthYear;
		}

	}

	@Override
	public List<Scope> generateFactorScopes(OBIEState state) {
		List<Scope> factors = new ArrayList<>();

		/*
		 * For all soccer player in the document create a new scope.
		 */
		for (EntityAnnotation entityAnnotation : state.getCurrentPrediction().getEntityAnnotations()) {

			ISoccerPlayer soccerPlayer = ((ISoccerPlayer) entityAnnotation.getAnnotationInstance());

			if (soccerPlayer == null)
				break;

			if (soccerPlayer.getBirthYear() == null)
				break;

			final int birthYear = Integer.valueOf(soccerPlayer.getBirthYear().getTextMention());
			/*
			 * TODO: get variables and pass it to the scope.
			 */

			final Scope scope = new Scope(this, birthYear);

			factors.add(scope);
		}

		return factors;
	}

	@Override
	public void computeFactor(Factor<Scope> factor) {

		Scope scope = factor.getFactorScope();

		final int by = scope.birthYear;

		/*
		 * TODO: Get variables from scope and compute features. Add features to feature
		 * vector.
		 */

		factor.getFeatureVector().set("BirthYearPrior" + by, true);

	}

}
