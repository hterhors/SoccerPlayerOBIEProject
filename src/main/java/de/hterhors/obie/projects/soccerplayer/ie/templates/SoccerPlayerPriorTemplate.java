package de.hterhors.obie.projects.soccerplayer.ie.templates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hterhors.obie.core.ontology.AbstractIndividual;
import de.hterhors.obie.core.ontology.interfaces.IDatatype;
import de.hterhors.obie.ml.run.param.RunParameter;
import de.hterhors.obie.ml.templates.AbstractOBIETemplate;
import de.hterhors.obie.ml.utils.ReflectionUtils;
import de.hterhors.obie.ml.variables.OBIEState;
import de.hterhors.obie.ml.variables.TemplateAnnotation;
import de.hterhors.obie.projects.soccerplayer.ie.templates.SoccerPlayerPriorTemplate.Scope;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.IAmerican_football_positions;
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
 * @see Same as {@link GenericPriorTemplate} but specific for SoccerPlayer
 */
public class SoccerPlayerPriorTemplate extends AbstractOBIETemplate<Scope> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LogManager.getFormatterLogger(SoccerPlayerPriorTemplate.class.getName());

	private static final String SOCCER_PLAYER_CLASS_IDENTIFIER = "soccerplayer_class";

	private static final String SOCCER_PLAYER_INDIVIDUAL_IDENTIFIER = "soccerplayer_individual";

	private static final String BIRTH_PLACE_PROPERTY_IDENTIFIER = "birthPlace";

	private static final String TEAM_PROPERTY_IDENTIFIER = "teams";

	private static final String POSITION_PROPERTY_IDENTIFIER = "position";

	private static final String BIRTH_YEAR_PROPERTY_IDENTIFIER = "birthYear";

	private static final String DEATH_YEAR_PROPERTY_IDENTIFIER = "deathYear";

//	private static final String PLAYER_NUMBER_PROPERTY_IDENTIFIER = "playerNumber";

	public SoccerPlayerPriorTemplate(RunParameter parameter) {
		super(parameter);
	}

	class Scope extends FactorScope {

		/**
		 * A map where the key is the property name and the value is a list of assigned
		 * class names (for object properties) or values (for datatype properties) for
		 * that property.
		 */
		final String propertyName;
		final Set<String> assignedClassesNamesOrValues;

		public Scope(AbstractOBIETemplate<Scope> template, String propertyName,
				Set<String> assignedClassesNamesOrValues) {
			super(template, propertyName, assignedClassesNamesOrValues);
			this.assignedClassesNamesOrValues = assignedClassesNamesOrValues;
			this.propertyName = propertyName;
		}

	}

	@Override
	public List<Scope> generateFactorScopes(OBIEState state) {
		List<Scope> factors = new ArrayList<>();

		/*
		 * For all soccer player in the document create an individual factor scope.
		 *
		 */
		for (TemplateAnnotation entityAnnotation : state.getCurrentTemplateAnnotations().getTemplateAnnotations()) {

			ISoccerPlayer soccerPlayer = ((ISoccerPlayer) entityAnnotation.getThing());

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
			addScope(factors, getClassScope(SOCCER_PLAYER_CLASS_IDENTIFIER, soccerPlayer));

			/**
			 * If we are interested in the actual individual, we can store this here. We
			 * assume that the name is sufficient enough to distinguish between all
			 * individuals. This assumption might not hold. In that case the namespace
			 * should also be part of the feature!
			 */
			addScope(factors, getIndividualScope(SOCCER_PLAYER_INDIVIDUAL_IDENTIFIER, soccerPlayer));

			/**
			 * Given the soccer player ontology we are not interested in any classes. As no
			 * class contains any (important) sub classes.
			 */

			if (soccerPlayer.getBirthPlaces() != null)

				for (IPlace place : soccerPlayer.getBirthPlaces()) {
					addScope(factors, getIndividualScope(BIRTH_PLACE_PROPERTY_IDENTIFIER, place));
				}

			if (soccerPlayer.getBirthYear() != null) {
				addScope(factors, getDatatypeScope(BIRTH_YEAR_PROPERTY_IDENTIFIER, soccerPlayer.getBirthYear()));
			}

			if (soccerPlayer.getDeathYear() != null) {
				addScope(factors, getDatatypeScope(DEATH_YEAR_PROPERTY_IDENTIFIER, soccerPlayer.getDeathYear()));
			}

//			if (soccerPlayer.getPlayerNumber() != null) {
//				addScope(factors, getDatatypeScope(PLAYER_NUMBER_PROPERTY_IDENTIFIER, soccerPlayer.getPlayerNumber()));
//			}

			if (soccerPlayer.getPositionAmerican_football_positions() != null) {
				for (IAmerican_football_positions position : soccerPlayer.getPositionAmerican_football_positions()) {
					addScope(factors, getIndividualScope(POSITION_PROPERTY_IDENTIFIER, position));
				}
			}

			if (soccerPlayer.getTeamSoccerClubs() != null)
				for (ISoccerClub team : soccerPlayer.getTeamSoccerClubs()) {
					getIndividualScope(TEAM_PROPERTY_IDENTIFIER, team);
				}

		}

		return factors;
	}

//	private Scope getIndividualScope(String slotIdentifier, List<? extends ISoccerPlayerThing> slotFillerValues) {
//
//		if (slotFillerValues == null)
//			return null;
//
//		final Set<String> distinctValues = new HashSet<String>(slotFillerValues.size());
//
//		for (ISoccerPlayerThing thing : slotFillerValues) {
//
//			if (thing == null) {
//				continue;
//			}
//
//			final AbstractIndividual individual = thing.getIndividual();
//
//			if (individual == null) {
//				continue;
//			}
//
//			distinctValues.add(getIndividualID(individual));
//		}
//
//		if (distinctValues.isEmpty())
//			return null;
//
//		return new Scope(this, slotIdentifier, distinctValues);
//	}

	private void addScope(List<Scope> factors, Scope scope) {
		if (scope != null)
			factors.add(scope);
	}

	/**
	 * If we are interested in capturing the datatype value of a datatype property
	 * slot we can add this here.
	 * 
	 * @param birthYearPropertyIdentifier the identifier to store the value
	 * @param datatypeSlotValue           the filler of the slot
	 */
	private Scope getDatatypeScope(String slotIdentifier, IDatatype datatypeSlotValue) {
		final Set<String> distinctValues = new HashSet<>();
		distinctValues.add(datatypeSlotValue.getSemanticValue());
		return new Scope(this, slotIdentifier, distinctValues);
	}

	/**
	 * If we are interested in the actual individual, we can store this here. We
	 * assume that the name is sufficient enough to distinguish between all
	 * individuals. This assumption might not hold. In that case the namespace
	 * should also be part of the feature!
	 * 
	 * @param classTypeIdentifier the identifier to store the value
	 * @param soccerPlayerThing   the filler of the slot
	 */
	private Scope getIndividualScope(String slotIdentifier, ISoccerPlayerThing soccerPlayerThing) {

		final Set<String> distinctValues;

		if (soccerPlayerThing == null)
			return null;

		final AbstractIndividual individual = soccerPlayerThing.getIndividual();

		if (individual == null)
			return null;

		distinctValues = new HashSet<>();

		distinctValues.add(getIndividualID(individual));

		return new Scope(this, slotIdentifier, distinctValues);
	}

	private String getIndividualID(AbstractIndividual individual) {
		// spi.nameSpace +
		return individual.name;
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
	private Scope getClassScope(final String identifier, ISoccerPlayerThing soccerPlayerThing) {

		final Set<String> distinctValues;
		if (soccerPlayerThing == null)
			return null;

		distinctValues = new HashSet<>();
		distinctValues.add(ReflectionUtils.simpleName(soccerPlayerThing.getClass()));

		return new Scope(this, identifier, distinctValues);

	}

	@Override
	public void computeFactor(Factor<Scope> factor) {

		/*
		 * If the property had multiple values, as in one-to-many relations (such as
		 * hasTeams) we create a feature that takes both values into account. This might
		 * be very sparse.
		 */
		if (factor.getFactorScope().assignedClassesNamesOrValues.size() > 1)
			factor.getFeatureVector().set("Prior towards: " + factor.getFactorScope().propertyName + "->"
					+ factor.getFactorScope().assignedClassesNamesOrValues, true);

		for (String assignedClassesNamesOrValue : factor.getFactorScope().assignedClassesNamesOrValues) {

			/*
			 * For each property and for each value for that property create a single
			 * feature.
			 */
			factor.getFeatureVector()
					.set("Prior: " + factor.getFactorScope().propertyName + "->" + assignedClassesNamesOrValue, true);
		}
	}

}
