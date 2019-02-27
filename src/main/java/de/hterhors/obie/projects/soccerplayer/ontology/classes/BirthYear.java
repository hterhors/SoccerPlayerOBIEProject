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
 *         Jan 8, 2019
 */

@DirectInterface(get = IBirthYear.class)

@DirectSiblings(get = {})

@AssignableSubClasses(get = {})

@DatatypeProperty
@SuperRootClasses(get = { BirthYear.class, })
public class BirthYear implements IBirthYear {

	final static public String ONTOLOGY_NAME = "http://psink/soccerPlayer/BirthYear";
	private Integer characterOffset;
	private Integer characterOnset;
	final private String interpretedValue;
	final static private Map<IOBIEThing, String> resourceFactory = new HashMap<>();
	final static private long serialVersionUID = 5L;
	@TextMention
	final private String textMention;

	public BirthYear() {
		this.interpretedValue = null;
		this.textMention = null;
	}

	public BirthYear(BirthYear birthYear) {
		this.characterOffset = birthYear.getCharacterOffset();
		this.characterOnset = birthYear.getCharacterOnset();
		this.interpretedValue = birthYear.getInterpretedValue();
		this.textMention = birthYear.getTextMention();
	}

	public BirthYear(String interpretedValue, String textMention) {
		this.interpretedValue = interpretedValue;
		this.textMention = textMention;
	}

	public BirthYear(String interpretedValue) {
		this.interpretedValue = interpretedValue;
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
		if (characterOnset == null) {
			if (other.characterOnset != null)
				return false;
		} else if (!characterOnset.equals(other.characterOnset))
			return false;
		if (textMention == null) {
			if (other.textMention != null)
				return false;
		} else if (!textMention.equals(other.textMention))
			return false;
		if (interpretedValue == null) {
			if (other.interpretedValue != null)
				return false;
		} else if (!interpretedValue.equals(other.interpretedValue))
			return false;
		if (characterOffset == null) {
			if (other.characterOffset != null)
				return false;
		} else if (!characterOffset.equals(other.characterOffset))
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
	public String getInterpretedValue() {
		return interpretedValue;
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
	public String getTextMention() {
		return textMention;
	}

	/***/
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.characterOnset == null) ? 0 : this.characterOnset.hashCode());
		result = prime * result + ((this.textMention == null) ? 0 : this.textMention.hashCode());
		result = prime * result + ((this.interpretedValue == null) ? 0 : this.interpretedValue.hashCode());
		result = prime * result + ((this.characterOffset == null) ? 0 : this.characterOffset.hashCode());
		return result;
	}

	/***/
	@Override
	public boolean isEmpty() {
		boolean isEmpty = true;
		isEmpty &= this.interpretedValue == null;
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
		return "BirthYear [characterOffset=" + characterOffset + ",characterOnset=" + characterOnset
				+ ",interpretedValue=" + interpretedValue + ",serialVersionUID=" + serialVersionUID + ",textMention="
				+ textMention + "]";
	}

}
