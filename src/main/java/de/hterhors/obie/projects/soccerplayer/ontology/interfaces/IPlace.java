package de.hterhors.obie.projects.soccerplayer.ontology.interfaces;

import de.hterhors.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.hterhors.obie.core.ontology.annotations.ImplementationClass;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.Place;

/**
 *
 * @author hterhors
 *
 *
 *         Oct 18, 2018
 */

@ImplementationClass(get = Place.class)

@AssignableSubInterfaces(get = {})
public interface IPlace extends ISoccerPlayerThing {

}
