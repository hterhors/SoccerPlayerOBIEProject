package de.hterhors.obie.projects.soccerplayer.ontology.interfaces;

import de.hterhors.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.hterhors.obie.core.ontology.annotations.DatatypeProperty;
import de.hterhors.obie.core.ontology.annotations.ImplementationClass;
import de.hterhors.obie.core.ontology.interfaces.IDatatype;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.BirthYear;
/**
*
* @author hterhors
*
*
*Jan 8, 2019
*/

@AssignableSubInterfaces(get={})

@ImplementationClass(get=BirthYear.class)

@DatatypeProperty public interface IBirthYear
 extends IDatatype, ISoccerPlayerThing{

}
