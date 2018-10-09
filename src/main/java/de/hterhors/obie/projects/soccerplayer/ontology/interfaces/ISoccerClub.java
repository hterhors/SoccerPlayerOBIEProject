package de.hterhors.obie.projects.soccerplayer.ontology.interfaces;

import de.hterhors.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.hterhors.obie.core.ontology.annotations.ImplementationClass;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.SoccerClub;

/**
 *
 * @author hterhors
 *
 *
 *         Oct 9, 2018
 */

@AssignableSubInterfaces(get = {})

@ImplementationClass(get = SoccerClub.class)
public interface ISoccerClub extends ISoccerPlayerThing {

}
