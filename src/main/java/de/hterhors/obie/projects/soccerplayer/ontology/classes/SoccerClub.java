package de.hterhors.obie.projects.soccerplayer.ontology.classes;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import de.hterhors.obie.core.ontology.AbstractOBIEIndividual;
import de.hterhors.obie.core.ontology.IndividualFactory;
import de.hterhors.obie.core.ontology.annotations.AssignableSubClasses;
import de.hterhors.obie.core.ontology.annotations.DirectInterface;
import de.hterhors.obie.core.ontology.annotations.DirectSiblings;
import de.hterhors.obie.core.ontology.annotations.SuperRootClasses;
import de.hterhors.obie.core.ontology.annotations.TextMention;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerClub;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayerThing;

/**
 *
 * @author hterhors
 *
 *
 *         Oct 18, 2018
 */

@AssignableSubClasses(get = {})

@SuperRootClasses(get = { SoccerClub.class, })

@DirectInterface(get = ISoccerClub.class)

@DirectSiblings(get = {})
public class SoccerClub implements ISoccerClub {

	final public static IndividualFactory<SoccerClubIndividual> individualFactory = new IndividualFactory<>();
	final public static Class<? extends AbstractOBIEIndividual> individualClassType = SoccerClubIndividual.class;

	static class SoccerClubIndividual extends AbstractOBIEIndividual {

		private static final long serialVersionUID = 1L;

		public SoccerClubIndividual(String namespace, String name) {
			super(namespace, name);
		}

		@Override
		public String toString() {
			return "SoccerClubIndividual [name=" + name + ", nameSpace=" + nameSpace + "]";
		}

	}

	public IndividualFactory<SoccerClubIndividual> getIndividualFactory() {
		return individualFactory;
	}

	public final SoccerClubIndividual individual;

	@Override
	public AbstractOBIEIndividual getIndividual() {
		return individual;
	}

	final static public String ONTOLOGY_NAME = "http://dbpedia.org/ontology/SoccerClub";
	private Integer characterOffset;
	private Integer characterOnset;
	final static private Map<IOBIEThing, String> resourceFactory = new HashMap<>();
	final static private long serialVersionUID = 1L;
	@TextMention
	final private String textMention;

	public SoccerClub() {
		this.individual = null;
		this.textMention = null;
	}

	public SoccerClub(SoccerClub soccerClub) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this.individual = soccerClub.individual;
		this.characterOffset = soccerClub.getCharacterOffset();
		this.characterOnset = soccerClub.getCharacterOnset();
		this.textMention = soccerClub.getTextMention();
	}

	public SoccerClub(String individualURI, String textMention) {
		this.individual = SoccerClub.individualFactory.getIndividualByURI(individualURI);
		this.textMention = textMention;
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
		SoccerClub other = (SoccerClub) obj;
		if (individual == null) {
			if (other.individual != null)
				return false;
		} else if (!individual.equals(other.individual))
			return false;
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
		Resource group = model.createResource(getResourceName());
		model.add(group, model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
				model.createResource(ONTOLOGY_NAME));
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
		result = prime * result + ((this.individual == null) ? 0 : this.individual.hashCode());
		result = prime * result + ((this.textMention == null) ? 0 : this.textMention.hashCode());
		result = prime * result + ((this.characterOffset == null) ? 0 : this.characterOffset.hashCode());
		result = prime * result + ((this.characterOnset == null) ? 0 : this.characterOnset.hashCode());
		return result;
	}

	/***/
	@Override
	public boolean isEmpty() {
		boolean isEmpty = true;
		return false;
	}

	/***/
	@Override
	public void setCharacterOnset(Integer onset) {
		this.characterOnset = onset;
		this.characterOffset = onset + textMention.length();
	}

	@Override
	public String toString() {
		return "SoccerClub [individual=" + individual + ",characterOffset=" + characterOffset + ",characterOnset="
				+ characterOnset + ",serialVersionUID=" + serialVersionUID + ",textMention=" + textMention + "]";
	}

}
