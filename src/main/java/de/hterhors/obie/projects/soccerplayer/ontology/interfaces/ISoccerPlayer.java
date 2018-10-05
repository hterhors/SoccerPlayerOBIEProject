package de.hterhors.obie.projects.soccerplayer.ontology.interfaces;

import de.hterhors.obie.projects.soccerplayer.ontology.classes.*;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.AbstractOBIEIndividual;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.IndividualFactory;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.AssignableSubClasses;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.DatatypeProperty;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.DirectInterface;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.DirectSiblings;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.ImplementationClass;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.OntologyModelContent;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.RelationTypeCollection;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.SuperRootClasses;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations.TextMention;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IDatatype;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
/**
*
* @author hterhors
*
*
*Oct 5, 2018
*/

@AssignableSubInterfaces(get={})

@ImplementationClass(get=SoccerPlayer.class)
 public interface ISoccerPlayer
 extends ISoccerPlayerThing{

/***/
	public ISoccerPlayer addBirthPlace(IPlace place);


/***/
	public ISoccerPlayer addTeamSoccerClub(ISoccerClub soccerClub);


/***/
	public List<IPlace> getBirthPlaces();


/***/
	public IBirthYear getBirthYear();


/***/
	public IAmerican_football_positions getPositionAmerican_football_positions();


/***/
	public List<ISoccerClub> getTeamSoccerClubs();


/***/
	public ISoccerPlayer setBirthPlaces(List<IPlace> birthPlaces);


/***/
	public ISoccerPlayer setBirthYear(IBirthYear birthYear);


/***/
	public ISoccerPlayer setPositionAmerican_football_positions(IAmerican_football_positions positionAmerican_football_positions);


/***/
	public ISoccerPlayer setTeamSoccerClubs(List<ISoccerClub> teamSoccerClubs);


}
