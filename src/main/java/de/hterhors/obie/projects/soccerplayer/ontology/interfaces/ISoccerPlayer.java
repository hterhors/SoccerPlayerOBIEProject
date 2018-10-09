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
 *         Oct 9, 2018
 */

@AssignableSubInterfaces(get = {})

@ImplementationClass(get = SoccerPlayer.class)
public interface ISoccerPlayer extends ISoccerPlayerThing {

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
	public ISoccerPlayer setPositionAmerican_football_positions(
			IAmerican_football_positions positionAmerican_football_positions);

	/***/
	public ISoccerPlayer setTeamSoccerClubs(List<ISoccerClub> teamSoccerClubs);

}
