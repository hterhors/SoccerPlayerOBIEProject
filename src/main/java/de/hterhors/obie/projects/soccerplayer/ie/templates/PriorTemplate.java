package de.hterhors.obie.projects.soccerplayer.ie.templates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hterhors.obie.core.ontology.AbstractOBIEIndividual;
import de.hterhors.obie.core.ontology.interfaces.IDatatype;
import de.hterhors.obie.ml.run.param.OBIERunParameter;
import de.hterhors.obie.ml.templates.AbstractOBIETemplate;
import de.hterhors.obie.ml.variables.OBIEState;
import de.hterhors.obie.ml.variables.TemplateAnnotation;
import de.hterhors.obie.projects.soccerplayer.ie.templates.PriorTemplate.Scope;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.IPlace;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerClub;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayer;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayerThing;
import factors.Factor;
import factors.FactorScope;

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

	private static final String SOCCER_PLAYER_CLASS_IDENTIFIER = "soccerplayer_class";

	private static final String SOCCER_PLAYER_INDIVIDUAL_IDENTIFIER = "soccerplayer_individual";

	private static final String BIRTH_PLACE_PROPERTY_IDENTIFIER = "birthPlace";

	private static final String TEAM_PROPERTY_IDENTIFIER = "teams";

	private static final String POSITION_PROPERTY_IDENTIFIER = "position";

	private static final String BIRTH_YEAR_PROPERTY_IDENTIFIER = "birthYear";

	public PriorTemplate(OBIERunParameter parameter) {
		super(parameter);
	}

	class Scope extends FactorScope {

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

	@Override
	public List<Scope> generateFactorScopes(OBIEState state) {
		List<Scope> factors = new ArrayList<>();

		/*
		 * For all soccer player in the document create an individual factor scope.
		 *
		 * In the lecture corpus there is only one soccer player per document!
		 *
		 */
		for (TemplateAnnotation entityAnnotation : state.getCurrentTemplateAnnotations().getTemplateAnnotations()) {

			Map<String, List<String>> assignedClasses = new HashMap<>();

			ISoccerPlayer soccerPlayer = ((ISoccerPlayer) entityAnnotation.get());

			/*
			 * If the observed soccerPlayer annotation is null we don't need to create any
			 * features.
			 */
			if (soccerPlayer == null)
				continue;

			/**
			 * If we are interested in the class type we can store this here. Usually the
			 * class type is not important if the individual was found! However, in some
			 * cases it might make sense.
			 */
			addClass(SOCCER_PLAYER_CLASS_IDENTIFIER, assignedClasses, soccerPlayer);

			/**
			 * If we are interested in the actual individual, we can store this here. We
			 * assume that the name is sufficient enough to distinguish between all
			 * individuals. This assumption might not hold. In that case the namespace
			 * should also be part of the feature!
			 */
			addIndividual(SOCCER_PLAYER_INDIVIDUAL_IDENTIFIER, assignedClasses, soccerPlayer);

			/**
			 * Given the soccer player ontology we are not interested in any classes. As no
			 * class contains any (important) sub classes.
			 */

			if (soccerPlayer.getBirthPlaces() != null)
				for (IPlace birthPlace : soccerPlayer.getBirthPlaces()) {
					addIndividual(BIRTH_PLACE_PROPERTY_IDENTIFIER, assignedClasses, birthPlace);
				}

			if (soccerPlayer.getBirthYear() != null) {
				addDatatype(BIRTH_YEAR_PROPERTY_IDENTIFIER, assignedClasses, soccerPlayer.getBirthYear());
			}

			if (soccerPlayer.getPositionAmerican_football_positions() != null) {
				addIndividual(POSITION_PROPERTY_IDENTIFIER, assignedClasses,
						soccerPlayer.getPositionAmerican_football_positions());
			}

			if (soccerPlayer.getTeamSoccerClubs() != null)
				for (ISoccerClub team : soccerPlayer.getTeamSoccerClubs()) {
					addIndividual(TEAM_PROPERTY_IDENTIFIER, assignedClasses, team);
				}

			final Scope scope = new Scope(this, assignedClasses);
			factors.add(scope);
		}

		return factors;
	}

	/**
	 * If we are interested in capturing the datatype value of a datatype property
	 * slot we can add this here.
	 * 
	 * @param birthYearPropertyIdentifier the identifier to store the value
	 * @param assignedClasses             the map to store the value
	 * @param birthYear                   the filler of the slot
	 */
	private void addDatatype(String birthYearPropertyIdentifier, Map<String, List<String>> assignedClasses,
			IDatatype birthYear) {
		assignedClasses.putIfAbsent(birthYearPropertyIdentifier, new ArrayList<>());
		assignedClasses.get(birthYearPropertyIdentifier).add(birthYear.getSemanticValue());
	}

	/**
	 * If we are interested in the actual individual, we can store this here. We
	 * assume that the name is sufficient enough to distinguish between all
	 * individuals. This assumption might not hold. In that case the namespace
	 * should also be part of the feature!
	 * 
	 * @param classTypeIdentifier the identifier to store the value
	 * @param assignedClasses     the map to store the value
	 * @param soccerPlayerThing   the filler of the slot
	 */
	private void addIndividual(String classTypeIdentifier, Map<String, List<String>> assignedClasses,
			ISoccerPlayerThing soccerPlayerThing) {

		if (soccerPlayerThing == null)
			return;

		final AbstractOBIEIndividual spi = soccerPlayerThing.getIndividual();

		if (spi == null)
			return;

		assignedClasses.putIfAbsent(classTypeIdentifier, new ArrayList<>());
		assignedClasses.get(classTypeIdentifier).add(// spi.nameSpace +
				spi.name);
	}

	/**
	 * If we are interested in the class type we can store this here. Usually the
	 * class type is not important if the individual was found! However, in some
	 * cases it might make sense.
	 * 
	 * @param identifier        the identifier to store the value
	 * @param assignedClasses   the map to store the value
	 * @param soccerPlayerThing the filler of the slot
	 */
	private void addClass(final String identifier, Map<String, List<String>> assignedClasses,
			ISoccerPlayerThing soccerPlayerThing) {

		if (soccerPlayerThing == null)
			return;

		assignedClasses.putIfAbsent(identifier, new ArrayList<>());
		assignedClasses.get(identifier).add(soccerPlayerThing.getClass().getSimpleName());
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
