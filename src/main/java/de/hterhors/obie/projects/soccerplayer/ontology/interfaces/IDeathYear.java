package de.hterhors.obie.projects.soccerplayer.ontology.interfaces;

import de.hterhors.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.hterhors.obie.core.ontology.annotations.DatatypeProperty;
import de.hterhors.obie.core.ontology.annotations.ImplementationClass;
import de.hterhors.obie.core.ontology.interfaces.IDatatype;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.DeathYear;
/**
*
* @author hterhors
*
*
*Jan 8, 2019
*/

@AssignableSubInterfaces(get={})

@ImplementationClass(get=DeathYear.class)

@DatatypeProperty public interface IDeathYear
 extends IDatatype, ISoccerPlayerThing{

}
