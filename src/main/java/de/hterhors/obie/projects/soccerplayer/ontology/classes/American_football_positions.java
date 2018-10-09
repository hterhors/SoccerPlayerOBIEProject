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
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.IAmerican_football_positions;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayerThing;

/**
 *
 * @author hterhors
 *
 *
 *         Oct 9, 2018
 */

@AssignableSubClasses(get = {})

@DirectInterface(get = IAmerican_football_positions.class)

@DirectSiblings(get = {})

@SuperRootClasses(get = { American_football_positions.class, })
public class American_football_positions implements IAmerican_football_positions {

	final public static IndividualFactory<American_football_positionsIndividual> individualFactory = new IndividualFactory<>();
	final public static Class<? extends AbstractOBIEIndividual> individualClassType = American_football_positionsIndividual.class;

	static class American_football_positionsIndividual extends AbstractOBIEIndividual {

		private static final long serialVersionUID = 1L;

		public American_football_positionsIndividual(String namespace, String name) {
			super(namespace, name);
		}

		@Override
		public String toString() {
			return "American_football_positionsIndividual [name=" + name + ", nameSpace=" + nameSpace + "]";
		}

	}

	public IndividualFactory<American_football_positionsIndividual> getIndividualFactory() {
		return individualFactory;
	}

	public final American_football_positionsIndividual individual;

	@Override
	public AbstractOBIEIndividual getIndividual() {
		return individual;
	}

	final static public String ONTOLOGY_NAME = "http://dbpedia.org/page/American_football_positions";
	final public String annotationID;
	private Integer characterOffset;
	private Integer characterOnset;
	final static private Map<IOBIEThing, String> resourceFactory = new HashMap<>();
	final static private long serialVersionUID = 1L;
	@TextMention
	final private String textMention;

	public American_football_positions(String individualURI, String annotationID, String textMention) {
		this.individual = American_football_positions.individualFactory.getIndividualByURI(individualURI);
		this.annotationID = annotationID;
		this.textMention = textMention;
	}

	public American_football_positions(American_football_positions american_football_positions)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		this.individual = american_football_positions.individual;
		this.annotationID = american_football_positions.getAnnotationID();
		this.characterOffset = american_football_positions.getCharacterOffset();
		this.characterOnset = american_football_positions.getCharacterOnset();
		this.textMention = american_football_positions.getTextMention();
	}

	public American_football_positions() {
		this.individual = null;
		this.annotationID = null;
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
		American_football_positions other = (American_football_positions) obj;
		if (individual == null) {
			if (other.individual != null)
				return false;
		} else if (!individual.equals(other.individual))
			return false;
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
		if (characterOffset == null) {
			if (other.characterOffset != null)
				return false;
		} else if (!characterOffset.equals(other.characterOffset))
			return false;
		if (annotationID == null) {
			if (other.annotationID != null)
				return false;
		} else if (!annotationID.equals(other.annotationID))
			return false;
		return true;
	}

	/***/
	@Override
	public String getAnnotationID() {
		return annotationID;
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
		result = prime * result + ((this.characterOnset == null) ? 0 : this.characterOnset.hashCode());
		result = prime * result + ((this.textMention == null) ? 0 : this.textMention.hashCode());
		result = prime * result + ((this.characterOffset == null) ? 0 : this.characterOffset.hashCode());
		result = prime * result + ((this.annotationID == null) ? 0 : this.annotationID.hashCode());
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
		return "American_football_positions [individual=" + individual + ",annotationID=" + annotationID
				+ ",characterOffset=" + characterOffset + ",characterOnset=" + characterOnset + ",serialVersionUID="
				+ serialVersionUID + ",textMention=" + textMention + "]";
	}

}
