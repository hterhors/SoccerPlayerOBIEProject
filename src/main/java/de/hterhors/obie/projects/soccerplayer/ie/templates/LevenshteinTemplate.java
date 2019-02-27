package de.hterhors.obie.projects.soccerplayer.ie.templates;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hterhors.obie.core.ontology.AbstractIndividual;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import de.hterhors.obie.ml.metrics.LevenShteinSimilarities;
import de.hterhors.obie.ml.run.AbstractRunner;
import de.hterhors.obie.ml.run.param.RunParameter;
import de.hterhors.obie.ml.templates.AbstractOBIETemplate;
import de.hterhors.obie.ml.variables.OBIEState;
import de.hterhors.obie.ml.variables.TemplateAnnotation;
import de.hterhors.obie.projects.soccerplayer.ie.templates.LevenshteinTemplate.Scope;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayer;
import factors.Factor;
import factors.FactorScope;

/**
 * This is an empty template that serves as code-template to create new ones.
 * 
 * @author hterhors
 *
 */
public class LevenshteinTemplate extends AbstractOBIETemplate<Scope> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LogManager.getFormatterLogger(LevenshteinTemplate.class.getName());

	public LevenshteinTemplate(AbstractRunner runner) {
		super(runner);
	}

	/**
	 * Define a factor scope for this template
	 * 
	 * @author hterhors
	 *
	 */
	class Scope extends FactorScope {

		public final AbstractIndividual contextAbstractIndividual;
		public final String surfaceForm;

		public Scope(Class<? extends IOBIEThing> rootClassType, AbstractIndividual abstractIndividual,
				String surfaceForm) {
			super(LevenshteinTemplate.this, rootClassType, abstractIndividual, surfaceForm);
			this.contextAbstractIndividual = abstractIndividual;
			this.surfaceForm = surfaceForm;
		}

	}

	@Override
	public List<Scope> generateFactorScopes(OBIEState state) {

		List<Scope> factors = new ArrayList<>();

		/*
		 * For all soccer player in the document create a new scope.
		 */
		for (TemplateAnnotation templateAnnotation : state.getCurrentTemplateAnnotations().getTemplateAnnotations()) {

			ISoccerPlayer soccerPlayer = ((ISoccerPlayer) templateAnnotation.getThing());

			if (soccerPlayer.getTextMention() == null)
				continue;
			if (soccerPlayer.getIndividual() == null)
				continue;

//			final int lengthDistance = Math
//					.abs(soccerPlayer.getIndividual().name.length() - soccerPlayer.getTextMention().length());

			final Scope scope = new Scope(templateAnnotation.rootClassType, soccerPlayer.getIndividual(),
					soccerPlayer.getTextMention());

			factors.add(scope);
		}

		return factors;
	}

	@Override
	public void computeFactor(Factor<Scope> factor) {

		Scope scope = factor.getFactorScope();

		final double levenDist = LevenShteinSimilarities.levenshteinSimilarity(
				factor.getFactorScope().contextAbstractIndividual.name, factor.getFactorScope().surfaceForm, 100);
		x: {
			if (levenDist > 0.1) {
				factor.getFeatureVector()
						.set("<" + scope.contextAbstractIndividual.name + "> leven sim > 0.1 " + levenDist, true);
				factor.getFeatureVector().set("leven sim > 0.1 " + levenDist, true);
				break x;
			}
			if (levenDist > 0.2) {
				factor.getFeatureVector()
						.set("<" + scope.contextAbstractIndividual.name + "> leven sim > 0.2 " + levenDist, true);
				factor.getFeatureVector().set("leven sim > 0.2 " + levenDist, true);
				break x;
			}
			if (levenDist > 0.3) {
				factor.getFeatureVector()
						.set("<" + scope.contextAbstractIndividual.name + "> leven sim > 0.3 " + levenDist, true);
				factor.getFeatureVector().set("leven sim > 0.3 " + levenDist, true);
				break x;
			}
			if (levenDist > 0.4) {
				factor.getFeatureVector()
						.set("<" + scope.contextAbstractIndividual.name + "> leven sim > 0.4 " + levenDist, true);
				factor.getFeatureVector().set("leven sim > 0.4 " + levenDist, true);
				break x;
			}
			if (levenDist > 0.5) {
				factor.getFeatureVector()
						.set("<" + scope.contextAbstractIndividual.name + "> leven sim > 0.5 " + levenDist, true);
				factor.getFeatureVector().set("leven sim > 0.5 " + levenDist, true);
				break x;
			}
			if (levenDist > 0.6) {
				factor.getFeatureVector()
						.set("<" + scope.contextAbstractIndividual.name + "> leven sim > 0.6 " + levenDist, true);
				factor.getFeatureVector().set("leven sim > 0.6 " + levenDist, true);
				break x;
			}
			if (levenDist > 0.7) {
				factor.getFeatureVector()
						.set("<" + scope.contextAbstractIndividual.name + "> leven sim > 0.7 " + levenDist, true);
				factor.getFeatureVector().set("leven sim > 0.7 " + levenDist, true);
				break x;
			}
			if (levenDist > 0.8) {
				factor.getFeatureVector()
						.set("<" + scope.contextAbstractIndividual.name + "> leven sim > 0.8 " + levenDist, true);
				factor.getFeatureVector().set("leven sim > 0.8 " + levenDist, true);
				break x;
			}
			if (levenDist > 0.9) {
				factor.getFeatureVector()
						.set("<" + scope.contextAbstractIndividual.name + "> leven sim > 0.9 " + levenDist, true);
				factor.getFeatureVector().set("leven sim > 0.9 " + levenDist, true);
				break x;
			}
		}
	}

}
