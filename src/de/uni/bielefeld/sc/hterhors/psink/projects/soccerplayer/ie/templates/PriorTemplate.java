package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.templates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.OBIERunParameter;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.templates.AbstractOBIETemplate;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.templates.scope.OBIEFactorScope;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.variables.EntityAnnotation;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.variables.OBIEState;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.interfaces.IBirthPlace;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayer;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.interfaces.ITeam;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.templates.PriorTemplate.Scope;
import factors.Factor;

/**
 * This template creates a prior for each slot filler candidate given the
 * training data. This results in taking always that slot filler candidate that
 * appears most in the training data for its corresponding slot.
 * 
 * For that a feature is created for each assigned slot value class for each
 * property of the currently observed entity annotation.
 * 
 * @author hterhors
 *
 */
public class PriorTemplate extends AbstractOBIETemplate<Scope> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LogManager.getFormatterLogger(PriorTemplate.class.getName());

	public PriorTemplate(OBIERunParameter parameter) {
		super(parameter);
	}

	class Scope extends OBIEFactorScope {

		/**
		 * A map where the key is the property name and the value is a list of assigned
		 * class names (for object properties) or values (for datatype properties) for
		 * that property.
		 */
		final Map<String, List<String>> assignedClassesNamesOrValues;

		public Scope(AbstractOBIETemplate<Scope> template, Map<String, List<String>> assignedClassesNamesOrValues) {
			super(template, assignedClassesNamesOrValues);
			this.assignedClassesNamesOrValues = assignedClassesNamesOrValues;
		}

	}

	private static final String CLASS_TYPE_IDENTIFIER = "classType";

	private static final String BIRTH_PLACE_PROPERTY_IDENTIFIER = "hasBirthPlaces";

	private static final String TEAM_PROPERTY_IDENTIFIER = "hasTeams";

	private static final String POSITION_PROPERTY_IDENTIFIER = "hasPosition";

	private static final String BIRTH_YEAR_PROPERTY_IDENTIFIER = "hasBirthYear";

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

			Map<String, List<String>> assignedClasses = new HashMap<>();

			ISoccerPlayer soccerPlayer = ((ISoccerPlayer) entityAnnotation.getAnnotationInstance());

			/*
			 * If the observed soccerPlayer annotation is null we don't need to create any
			 * features.
			 */
			if (soccerPlayer == null)
				continue;

			assignedClasses.putIfAbsent(CLASS_TYPE_IDENTIFIER, new ArrayList<>());
			assignedClasses.get(CLASS_TYPE_IDENTIFIER).add(soccerPlayer.getClass().getSimpleName());

			if (soccerPlayer.getBirthPlaces() != null)
				for (IBirthPlace birthPlace : soccerPlayer.getBirthPlaces()) {
					if (birthPlace != null) {
						assignedClasses.putIfAbsent(BIRTH_PLACE_PROPERTY_IDENTIFIER, new ArrayList<>());
						assignedClasses.get(BIRTH_PLACE_PROPERTY_IDENTIFIER).add(birthPlace.getClass().getSimpleName());
					}
				}

			if (soccerPlayer.getBirthYear() != null) {
				assignedClasses.putIfAbsent(BIRTH_YEAR_PROPERTY_IDENTIFIER, new ArrayList<>());
				assignedClasses.get(BIRTH_YEAR_PROPERTY_IDENTIFIER).add(soccerPlayer.getBirthYear().getTextMention());

			}

			if (soccerPlayer.getPosition() != null) {
				assignedClasses.putIfAbsent(POSITION_PROPERTY_IDENTIFIER, new ArrayList<>());
				assignedClasses.get(POSITION_PROPERTY_IDENTIFIER)
						.add(soccerPlayer.getPosition().getClass().getSimpleName());
			}

			if (soccerPlayer.getTeams() != null)
				for (ITeam team : soccerPlayer.getTeams()) {
					if (team != null) {
						assignedClasses.putIfAbsent(TEAM_PROPERTY_IDENTIFIER, new ArrayList<>());
						assignedClasses.get(TEAM_PROPERTY_IDENTIFIER).add(team.getClass().getSimpleName());
					}

				}

			final Scope scope = new Scope(this, assignedClasses);
			factors.add(scope);
		}

		return factors;
	}

	@Override
	public void computeFactor(Factor<Scope> factor) {

		for (String propertyName : factor.getFactorScope().assignedClassesNamesOrValues.keySet()) {

			/*
			 * If the property had multiple values, as in one-to-many relations (such as
			 * hasTeams) we create a feature that takes both values into account. This might
			 * be very sparse.
			 */
			if (factor.getFactorScope().assignedClassesNamesOrValues.get(propertyName).size() > 1)
				factor.getFeatureVector().set("Prior towards: " + propertyName + "->"
						+ factor.getFactorScope().assignedClassesNamesOrValues.get(propertyName), true);

			for (String assignedClassesNamesOrValue : factor.getFactorScope().assignedClassesNamesOrValues
					.get(propertyName)) {

				/*
				 * For each property and for each value for that property create a single
				 * feature.
				 */
				factor.getFeatureVector().set("Prior towards: " + propertyName + "->" + assignedClassesNamesOrValue,
						true);
			}
		}

	}

}
