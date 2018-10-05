package de.hterhors.obie.projects.soccerplayer.ontology.classes;

import java.lang.NoSuchMethodException;
import java.util.HashMap;
import java.util.ArrayList;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import java.util.Map;
import java.lang.InstantiationException;
import java.lang.SecurityException;
import java.lang.IllegalAccessException;
import java.lang.IllegalArgumentException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.jena.rdf.model.ModelFactory;

import de.hterhors.obie.core.ontology.AbstractOBIEIndividual;
import de.hterhors.obie.core.ontology.IndividualFactory;
import de.hterhors.obie.core.ontology.annotations.AssignableSubClasses;
import de.hterhors.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.hterhors.obie.core.ontology.annotations.DatatypeProperty;
import de.hterhors.obie.core.ontology.annotations.DirectInterface;
import de.hterhors.obie.core.ontology.annotations.DirectSiblings;
import de.hterhors.obie.core.ontology.annotations.ImplementationClass;
import de.hterhors.obie.core.ontology.annotations.OntologyModelContent;
import de.hterhors.obie.core.ontology.annotations.RelationTypeCollection;
import de.hterhors.obie.core.ontology.annotations.SuperRootClasses;
import de.hterhors.obie.core.ontology.annotations.TextMention;
import de.hterhors.obie.core.ontology.interfaces.IDatatype;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.*;

/**
*
* @author hterhors
*
*
*Oct 5, 2018
*/

@DatatypeProperty
@SuperRootClasses(get={BirthYear.class, })

@AssignableSubClasses(get={})

@DirectSiblings(get={})

@DirectInterface(get=IBirthYear.class)
 public class BirthYear implements IBirthYear{

	final static public String ONTOLOGY_NAME = "http://psink/soccerplayer/BirthYear";
	final public String annotationID;
	private Integer characterOffset;
	private Integer characterOnset;
	final static private Map<IOBIEThing, String> resourceFactory = new HashMap<>();
	final private String semanticValue;
	final static private long serialVersionUID = 1L;
	@TextMention
final private String textMention;


	public BirthYear(String annotationID, String semanticValue, String textMention){
this.annotationID = annotationID;
this.semanticValue = semanticValue;
this.textMention = textMention;
}
	public BirthYear(String semanticValue){
this.annotationID = null;
this.semanticValue = semanticValue;
this.textMention = null;
}
	public BirthYear(BirthYear birthYear){
this.annotationID = birthYear.getAnnotationID();
this.characterOffset = birthYear.getCharacterOffset();
this.characterOnset = birthYear.getCharacterOnset();
this.semanticValue = birthYear.getSemanticValue();
this.textMention = birthYear.getTextMention();
}
	public BirthYear(){
this.annotationID = null;
this.semanticValue = null;
this.textMention = null;
}


	/***/
@Override
	public boolean equals(Object obj){
		if (this == obj)
return true;
if (obj == null)
return false;
if (getClass() != obj.getClass())
return false;
BirthYear other = (BirthYear) obj;
if (annotationID == null) {
if (other.annotationID!= null)
return false;
} else if (!annotationID.equals(other.annotationID))
return false;
if (characterOffset == null) {
if (other.characterOffset!= null)
return false;
} else if (!characterOffset.equals(other.characterOffset))
return false;
if (characterOnset == null) {
if (other.characterOnset!= null)
return false;
} else if (!characterOnset.equals(other.characterOnset))
return false;
if (semanticValue == null) {
if (other.semanticValue!= null)
return false;
} else if (!semanticValue.equals(other.semanticValue))
return false;
if (textMention == null) {
if (other.textMention!= null)
return false;
} else if (!textMention.equals(other.textMention))
return false;
return true;
}
	/***/
@Override
	public String getAnnotationID(){
		return annotationID;}
	/***/
@Override
	public Integer getCharacterOffset(){
		return characterOffset;}
	/***/
@Override
	public Integer getCharacterOnset(){
		return characterOnset;}
	/***/
@Override
	public String getONTOLOGY_NAME(){
		return ONTOLOGY_NAME;}
	/***/
@Override
	public Model getRDFModel(String resourceIDPrefix){
		Model model = ModelFactory.createDefaultModel();

return model;
}
	/***/
@Override
	public String getResourceName(){
		if (resourceFactory.containsKey(this)) {
return ISoccerPlayerThing.RDF_MODEL_NAMESPACE + resourceFactory.get(this);
} else {
final String resourceName = getClass().getSimpleName() + "_" + resourceFactory.size();
resourceFactory.put(this, resourceName);
return ISoccerPlayerThing.RDF_MODEL_NAMESPACE + resourceName;}
}
	/***/
@Override
	public String getSemanticValue(){
		return semanticValue;}
	/***/
@Override
	public String getTextMention(){
		return textMention;}
	/***/
@Override
	public int hashCode(){
		final int prime = 31;
int result = 1;
result = prime * result + ((this.annotationID == null) ? 0 : this.annotationID.hashCode());
result = prime * result + ((this.characterOffset == null) ? 0 : this.characterOffset.hashCode());
result = prime * result + ((this.characterOnset == null) ? 0 : this.characterOnset.hashCode());
result = prime * result + ((this.semanticValue == null) ? 0 : this.semanticValue.hashCode());
result = prime * result + ((this.textMention == null) ? 0 : this.textMention.hashCode());
return result;}
	/***/
@Override
	public boolean isEmpty(){
		boolean isEmpty = true;
isEmpty &= this.semanticValue == null;
if(!isEmpty) return false;

return true;}
	/***/
@Override
	public void setCharacterOnset(Integer onset){
		this.characterOnset = onset;
 this.characterOffset = onset + textMention.length();}


@Override
public String toString(){
return "BirthYear [annotationID="+annotationID+",characterOffset="+characterOffset+",characterOnset="+characterOnset+",semanticValue="+semanticValue+",serialVersionUID="+serialVersionUID+",textMention="+textMention+"]";}


}
