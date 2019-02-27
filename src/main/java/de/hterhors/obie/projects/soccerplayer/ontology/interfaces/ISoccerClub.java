package de.hterhors.obie.projects.soccerplayer.ontology.interfaces;

import de.hterhors.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.hterhors.obie.core.ontology.annotations.ImplementationClass;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.SoccerClub;
/**
*
* @author hterhors
*
*
*Jan 8, 2019
*/

@ImplementationClass(get=SoccerClub.class)

@AssignableSubInterfaces(get={})
 public interface ISoccerClub
 extends ISoccerPlayerThing{

}
