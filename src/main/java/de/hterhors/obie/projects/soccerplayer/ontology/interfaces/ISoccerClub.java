package de.hterhors.obie.projects.soccerplayer.ontology.interfaces;

import de.hterhors.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.hterhors.obie.core.ontology.annotations.ImplementationClass;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.SoccerClub;
/**
*
* @author hterhors
*
*
*Nov 13, 2018
*/

@AssignableSubInterfaces(get={})

@ImplementationClass(get=SoccerClub.class)
 public interface ISoccerClub
 extends ISoccerPlayerThing{

}
