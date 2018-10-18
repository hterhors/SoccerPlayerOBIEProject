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
 *         Oct 18, 2018
 */

@ImplementationClass(get = BirthYear.class)

@DatatypeProperty
@AssignableSubInterfaces(get = {})
public interface IBirthYear extends IDatatype, ISoccerPlayerThing {

}
