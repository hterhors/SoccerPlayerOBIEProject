package de.hterhors.obie.projects.soccerplayer.ontology.interfaces;

import de.hterhors.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.hterhors.obie.core.ontology.annotations.ImplementationClass;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.American_football_positions;

/**
 *
 * @author hterhors
 *
 *
 *         Oct 18, 2018
 */

@ImplementationClass(get = American_football_positions.class)

@AssignableSubInterfaces(get = {})
public interface IAmerican_football_positions extends ISoccerPlayerThing {

}
