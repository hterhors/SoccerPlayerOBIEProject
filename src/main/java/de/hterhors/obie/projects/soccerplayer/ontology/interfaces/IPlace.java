package de.hterhors.obie.projects.soccerplayer.ontology.interfaces;

import de.hterhors.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.hterhors.obie.core.ontology.annotations.ImplementationClass;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.Place;
/**
*
* @author hterhors
*
*
*Jan 8, 2019
*/

@AssignableSubInterfaces(get={})

@ImplementationClass(get=Place.class)
 public interface IPlace
 extends ISoccerPlayerThing{

}
