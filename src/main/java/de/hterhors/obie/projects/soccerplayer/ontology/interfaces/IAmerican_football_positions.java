package de.hterhors.obie.projects.soccerplayer.ontology.interfaces;

import de.hterhors.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.hterhors.obie.core.ontology.annotations.ImplementationClass;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.American_football_positions;

/**
 *
 * @author hterhors
 *
 *
 *         Oct 9, 2018
 */

@AssignableSubInterfaces(get = {})

@ImplementationClass(get = American_football_positions.class)
public interface IAmerican_football_positions extends ISoccerPlayerThing {

}
