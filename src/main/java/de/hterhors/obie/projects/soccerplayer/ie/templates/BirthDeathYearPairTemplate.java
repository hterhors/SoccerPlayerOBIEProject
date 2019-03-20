package de.hterhors.obie.projects.soccerplayer.ie.templates;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hterhors.obie.ml.ner.NERLClassAnnotation;
import de.hterhors.obie.ml.run.AbstractOBIERunner;
import de.hterhors.obie.ml.run.param.RunParameter;
import de.hterhors.obie.ml.templates.AbstractOBIETemplate;
import de.hterhors.obie.ml.variables.OBIEInstance;
import de.hterhors.obie.ml.variables.OBIEState;
import de.hterhors.obie.ml.variables.IETmplateAnnotation;
import de.hterhors.obie.projects.soccerplayer.ie.templates.BirthDeathYearPairTemplate.Scope;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.BirthYear;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.DeathYear;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.IBirthYear;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.IDeathYear;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayer;
import factors.Factor;
import factors.FactorScope;

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
public class BirthDeathYearPairTemplate extends AbstractOBIETemplate<Scope> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LogManager.getFormatterLogger(BirthDeathYearPairTemplate.class.getName());

	public BirthDeathYearPairTemplate(AbstractOBIERunner runner) {
		super(runner);
	}

	class Scope extends FactorScope {

		/**
		 * The currently assigned birth year.
		 */
		final int assignedBirthYear;

		/**
		 * The currently assigned death year.
		 */
		final int assignedDeathYear;

		public Scope(AbstractOBIETemplate<Scope> template, final int assignedBirthYear, final int assignedDeathYear) {
			super(template, assignedBirthYear, assignedDeathYear);
			this.assignedBirthYear = assignedBirthYear;
			this.assignedDeathYear = assignedDeathYear;
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
		for (IETmplateAnnotation entityAnnotation : state.getCurrentIETemplateAnnotations().getAnnotations()) {

			IDeathYear deathYear = ((ISoccerPlayer) entityAnnotation.getThing()).getDeathYear();

			/*
			 * If the birth year was not yet set, we don't need to generate any features
			 * here.
			 */
			if (deathYear == null)
				continue;

			IBirthYear birthYear = ((ISoccerPlayer) entityAnnotation.getThing()).getBirthYear();

			/*
			 * If the birth year was not yet set, we don't need to generate any features
			 * here.
			 */
			if (birthYear == null)
				continue;

			final Scope birthScope = new Scope(this, Integer.parseInt(birthYear.getTextMention()),
					Integer.parseInt(deathYear.getTextMention()));

			factors.add(birthScope);

		}

		return factors;
	}

	@Override
	public void computeFactor(Factor<Scope> factor) {

		final int assignedBirthYear = factor.getFactorScope().assignedBirthYear;
		final int assignedDeathYear = factor.getFactorScope().assignedDeathYear;

		if (assignedBirthYear <= assignedDeathYear - 70)
			factor.getFeatureVector().set("Dist Birth and Death >= 70", true);
		else if (assignedBirthYear <= assignedDeathYear - 60)
			factor.getFeatureVector().set("Dist Birth and Death >= 60", true);
		else if (assignedBirthYear <= assignedDeathYear - 50)
			factor.getFeatureVector().set("Dist Birth and Death >= 50", true);
		else if (assignedBirthYear <= assignedDeathYear - 40)
			factor.getFeatureVector().set("Dist Birth and Death >= 40", true);
		else if (assignedBirthYear <= assignedDeathYear - 30)
			factor.getFeatureVector().set("Dist Birth and Death >= 30", true);
		else if (assignedBirthYear <= assignedDeathYear - 20)
			factor.getFeatureVector().set("Dist Birth and Death >= 20", true);
		else if (assignedBirthYear <= assignedDeathYear - 10)
			factor.getFeatureVector().set("Dist Birth and Death >= 10", true);
		else
			factor.getFeatureVector().set("Dist Birth and Death < 10", true);

	}

}
