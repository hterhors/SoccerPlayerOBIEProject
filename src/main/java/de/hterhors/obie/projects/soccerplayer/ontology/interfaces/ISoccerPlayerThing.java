package de.hterhors.obie.projects.soccerplayer.ontology.interfaces;

import org.apache.jena.rdf.model.Model;

import de.hterhors.obie.core.ontology.annotations.AssignableSubInterfaces;

/**
 *
 * @author hterhors
 *
 *
 *         Oct 18, 2018
 */

@AssignableSubInterfaces(get = { IAmerican_football_positions.class, IPlace.class, ISoccerClub.class,
		ISoccerPlayer.class, IBirthYear.class, })
public interface ISoccerPlayerThing extends de.hterhors.obie.core.ontology.interfaces.IOBIEThing {

	public static String RDF_MODEL_NAMESPACE = "http://psink/soccerplayer/";

	/***/
	public Integer getCharacterOffset();

	/***/
	public Integer getCharacterOnset();

	/***/
	public String getONTOLOGY_NAME();

	/***/
	public Model getRDFModel(String resourceIDPrefix);

	/***/
	public String getResourceName();

	/***/
	public String getTextMention();

	/***/
	public boolean isEmpty();

	/***/
	public void setCharacterOnset(Integer onset);

}
