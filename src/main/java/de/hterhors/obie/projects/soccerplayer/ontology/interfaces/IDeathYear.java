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
*Nov 13, 2018
*/

@ImplementationClass(get=DeathYear.class)

@DatatypeProperty
@AssignableSubInterfaces(get={})
 public interface IDeathYear
 extends IDatatype, ISoccerPlayerThing{

}
