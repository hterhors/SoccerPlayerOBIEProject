package de.hterhors.obie.projects.soccerplayer.ontology.classes;

import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import de.hterhors.obie.core.ontology.annotations.AssignableSubClasses;
import de.hterhors.obie.core.ontology.annotations.DatatypeProperty;
import de.hterhors.obie.core.ontology.annotations.DirectInterface;
import de.hterhors.obie.core.ontology.annotations.DirectSiblings;
import de.hterhors.obie.core.ontology.annotations.SuperRootClasses;
import de.hterhors.obie.core.ontology.annotations.TextMention;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.IBirthYear;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayerThing;

/**
 *
 * @author hterhors
 *
 *
 *         Oct 18, 2018
 */

@AssignableSubClasses(get = {})

@SuperRootClasses(get = { BirthYear.class, })

@DirectInterface(get = IBirthYear.class)

@DatatypeProperty
@DirectSiblings(get = {})
public class BirthYear implements IBirthYear {

	final static public String ONTOLOGY_NAME = "http://psink/soccerplayer/BirthYear";
	private Integer characterOffset;
	private Integer characterOnset;
	final static private Map<IOBIEThing, String> resourceFactory = new HashMap<>();
	final private String semanticValue;
	final static private long serialVersionUID = 1L;
	@TextMention
	final private String textMention;

	public BirthYear(BirthYear birthYear) {
		this.characterOffset = birthYear.getCharacterOffset();
		this.characterOnset = birthYear.getCharacterOnset();
		this.semanticValue = birthYear.getSemanticValue();
		this.textMention = birthYear.getTextMention();
	}

	public BirthYear(String semanticValue, String textMention) {
		this.semanticValue = semanticValue;
		this.textMention = textMention;
	}

	public BirthYear(String semanticValue) {
		this.semanticValue = semanticValue;
		this.textMention = null;
	}

	public BirthYear() {
		this.semanticValue = null;
		this.textMention = null;
	}

	/***/
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BirthYear other = (BirthYear) obj;
		if (textMention == null) {
			if (other.textMention != null)
				return false;
		} else if (!textMention.equals(other.textMention))
			return false;
		if (characterOffset == null) {
			if (other.characterOffset != null)
				return false;
		} else if (!characterOffset.equals(other.characterOffset))
			return false;
		if (semanticValue == null) {
			if (other.semanticValue != null)
				return false;
		} else if (!semanticValue.equals(other.semanticValue))
			return false;
		if (characterOnset == null) {
			if (other.characterOnset != null)
				return false;
		} else if (!characterOnset.equals(other.characterOnset))
			return false;
		return true;
	}

	/***/
	@Override
	public Integer getCharacterOffset() {
		return characterOffset;
	}

	/***/
	@Override
	public Integer getCharacterOnset() {
		return characterOnset;
	}

	/***/
	@Override
	public String getONTOLOGY_NAME() {
		return ONTOLOGY_NAME;
	}

	/***/
	@Override
	public Model getRDFModel(String resourceIDPrefix) {
		Model model = ModelFactory.createDefaultModel();

		return model;
	}

	/***/
	@Override
	public String getResourceName() {
		if (resourceFactory.containsKey(this)) {
			return ISoccerPlayerThing.RDF_MODEL_NAMESPACE + resourceFactory.get(this);
		} else {
			final String resourceName = getClass().getSimpleName() + "_" + resourceFactory.size();
			resourceFactory.put(this, resourceName);
			return ISoccerPlayerThing.RDF_MODEL_NAMESPACE + resourceName;
		}
	}

	/***/
	@Override
	public String getSemanticValue() {
		return semanticValue;
	}

	/***/
	@Override
	public String getTextMention() {
		return textMention;
	}

	/***/
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.textMention == null) ? 0 : this.textMention.hashCode());
		result = prime * result + ((this.characterOffset == null) ? 0 : this.characterOffset.hashCode());
		result = prime * result + ((this.semanticValue == null) ? 0 : this.semanticValue.hashCode());
		result = prime * result + ((this.characterOnset == null) ? 0 : this.characterOnset.hashCode());
		return result;
	}

	/***/
	@Override
	public boolean isEmpty() {
		boolean isEmpty = true;
		isEmpty &= this.semanticValue == null;
		if (!isEmpty)
			return false;

		return true;
	}

	/***/
	@Override
	public void setCharacterOnset(Integer onset) {
		this.characterOnset = onset;
		this.characterOffset = onset + textMention.length();
	}

	@Override
	public String toString() {
		return "BirthYear [characterOffset=" + characterOffset + ",characterOnset=" + characterOnset + ",semanticValue="
				+ semanticValue + ",serialVersionUID=" + serialVersionUID + ",textMention=" + textMention + "]";
	}

}
