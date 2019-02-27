package de.hterhors.obie.projects.soccerplayer.ie.templates;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hterhors.obie.ml.run.AbstractRunner;
import de.hterhors.obie.ml.run.param.RunParameter;
import de.hterhors.obie.ml.templates.AbstractOBIETemplate;
import de.hterhors.obie.ml.variables.OBIEState;
import de.hterhors.obie.ml.variables.TemplateAnnotation;
import de.hterhors.obie.projects.soccerplayer.ie.templates.EmptyTemplate.Scope;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayer;
import factors.Factor;
import factors.FactorScope;

/**
 * This is an empty template that serves as code-template to create new ones.
 * 
 * @author hterhors
 *
 */
public class EmptyTemplate extends AbstractOBIETemplate<Scope> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LogManager.getFormatterLogger(EmptyTemplate.class.getName());

	public EmptyTemplate(AbstractRunner runner) {
		super(runner);
	}

	/**
	 * Define a factor scope for this template
	 * 
	 * @author hterhors
	 *
	 */
	class Scope extends FactorScope {

		/*
		 * TODO: add variables for feature computation.
		 */

		public Scope(AbstractOBIETemplate<Scope> template) {
			super(template);
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
			/*
			 * TODO: get variables and pass it to the scope.
			 */

			final Scope scope = new Scope(this);

			factors.add(scope);
		}

		return factors;
	}

	@Override
	public void computeFactor(Factor<Scope> factor) {

		Scope scope = factor.getFactorScope();

		/*
		 * TODO: Get variables from scope and compute features. Add features to feature
		 * vector.
		 */

//		factor.getFeatureVector().set(FEATURE NAME, TRUE/FALSE/DOUBLE);

	}

}
