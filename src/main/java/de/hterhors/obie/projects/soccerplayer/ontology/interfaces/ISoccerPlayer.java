package de.hterhors.obie.projects.soccerplayer.ontology.interfaces;

import java.util.List;

import de.hterhors.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.hterhors.obie.core.ontology.annotations.ImplementationClass;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.SoccerPlayer;
/**
*
* @author hterhors
*
*
*Nov 13, 2018
*/

@ImplementationClass(get=SoccerPlayer.class)

@AssignableSubInterfaces(get={})
 public interface ISoccerPlayer
 extends ISoccerPlayerThing{

/***/
	public ISoccerPlayer addBirthPlace(IPlace place);


/***/
	public ISoccerPlayer addPositionAmerican_football_positions(IAmerican_football_positions american_football_positions);


/***/
	public ISoccerPlayer addTeamSoccerClub(ISoccerClub soccerClub);


/***/
	public List<IPlace> getBirthPlaces();


/***/
	public IBirthYear getBirthYear();


/***/
	public IDeathYear getDeathYear();


/***/
	public List<IAmerican_football_positions> getPositionAmerican_football_positions();


/***/
	public List<ISoccerClub> getTeamSoccerClubs();


/***/
	public ISoccerPlayer setBirthPlaces(List<IPlace> birthPlaces);


/***/
	public ISoccerPlayer setBirthYear(IBirthYear birthYear);


/***/
	public ISoccerPlayer setDeathYear(IDeathYear deathYear);


/***/
	public ISoccerPlayer setPositionAmerican_football_positions(List<IAmerican_football_positions> positionAmerican_football_positions);


/***/
	public ISoccerPlayer setTeamSoccerClubs(List<ISoccerClub> teamSoccerClubs);


}
