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
 *         Oct 9, 2018
 */

@AssignableSubInterfaces(get = {})

@DatatypeProperty
@ImplementationClass(get = BirthYear.class)
public interface IBirthYear extends IDatatype, ISoccerPlayerThing {

}
