package de.hterhors.obie.projects.soccerplayer.ontology.classes;

import java.lang.NoSuchMethodException;
import de.hterhors.obie.core.ontology.interfaces.IDatatype;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.*;
import de.hterhors.obie.core.ontology.annotations.SuperRootClasses;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import java.util.HashMap;
import de.hterhors.obie.core.ontology.annotations.OntologyModelContent;
import java.util.ArrayList;
import org.apache.jena.rdf.model.Model;
import de.hterhors.obie.core.ontology.annotations.AssignableSubInterfaces;
import de.hterhors.obie.core.ontology.annotations.ImplementationClass;
import org.apache.jena.rdf.model.Resource;
import java.util.Map;
import java.lang.InstantiationException;
import java.lang.SecurityException;
import de.hterhors.obie.core.ontology.annotations.DirectSiblings;
import java.lang.IllegalAccessException;
import de.hterhors.obie.core.ontology.annotations.AssignableSubClasses;
import de.hterhors.obie.core.ontology.IndividualFactory;
import de.hterhors.obie.core.ontology.annotations.DirectInterface;
import de.hterhors.obie.core.ontology.annotations.RelationTypeCollection;
import de.hterhors.obie.core.ontology.annotations.DatatypeProperty;
import java.lang.IllegalArgumentException;
import de.hterhors.obie.core.ontology.annotations.TextMention;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import de.hterhors.obie.core.ontology.AbstractOBIEIndividual;
import java.util.List;
import org.apache.jena.rdf.model.ModelFactory;

/**
 *
 * @author hterhors
 *
 *
 *         Oct 18, 2018
 */

@AssignableSubClasses(get = {})

@SuperRootClasses(get = { SoccerPlayer.class, })

@DirectInterface(get = ISoccerPlayer.class)

@DirectSiblings(get = {})
public class SoccerPlayer implements ISoccerPlayer {

	final public static IndividualFactory<SoccerPlayerIndividual> individualFactory = new IndividualFactory<>();
	final public static Class<? extends AbstractOBIEIndividual> individualClassType = SoccerPlayerIndividual.class;

	static class SoccerPlayerIndividual extends AbstractOBIEIndividual {

		private static final long serialVersionUID = 1L;

		public SoccerPlayerIndividual(String namespace, String name) {
			super(namespace, name);
		}

		@Override
		public String toString() {
			return "SoccerPlayerIndividual [name=" + name + ", nameSpace=" + nameSpace + "]";
		}

	}

	public IndividualFactory<SoccerPlayerIndividual> getIndividualFactory() {
		return individualFactory;
	}

	public final SoccerPlayerIndividual individual;

	@Override
	public AbstractOBIEIndividual getIndividual() {
		return individual;
	}

	final static public String ONTOLOGY_NAME = "http://dbpedia.org/ontology/SoccerPlayer";
	@RelationTypeCollection
	@OntologyModelContent(ontologyName = "http://dbpedia.org/ontology/birthPlace")
	private List<IPlace> birthPlaces = new ArrayList<>();
	@OntologyModelContent(ontologyName = "http://dbpedia.org/ontology/birthYear")
	@DatatypeProperty
	private IBirthYear birthYear;
	private Integer characterOffset;
	private Integer characterOnset;
	@OntologyModelContent(ontologyName = "http://dbpedia.org/ontology/position")
	private IAmerican_football_positions positionAmerican_football_positions;
	final static private Map<IOBIEThing, String> resourceFactory = new HashMap<>();
	final static private long serialVersionUID = 1L;
	@RelationTypeCollection
	@OntologyModelContent(ontologyName = "http://dbpedia.org/ontology/team")
	private List<ISoccerClub> teamSoccerClubs = new ArrayList<>();
	@TextMention
	final private String textMention;

	public SoccerPlayer(String individualURI, String textMention) {
		this.individual = SoccerPlayer.individualFactory.getIndividualByURI(individualURI);
		this.textMention = textMention;
	}

	public SoccerPlayer() {
		this.individual = null;
		this.textMention = null;
	}

	public SoccerPlayer(SoccerPlayer soccerPlayer) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		this.individual = soccerPlayer.individual;
		for (int j = 0; j < soccerPlayer.getBirthPlaces().size(); j++) {
			if (soccerPlayer.getBirthPlaces().get(j) != null) {
				birthPlaces.add((IPlace) IOBIEThing.getCloneConstructor(soccerPlayer.getBirthPlaces().get(j).getClass())
						.newInstance(soccerPlayer.getBirthPlaces().get(j)));
			} else {
				birthPlaces.add(null);
			}
		}
		if (soccerPlayer.getBirthYear() != null)
			this.birthYear = new BirthYear((BirthYear) soccerPlayer.getBirthYear());
		this.characterOffset = soccerPlayer.getCharacterOffset();
		this.characterOnset = soccerPlayer.getCharacterOnset();
		if (soccerPlayer.getPositionAmerican_football_positions() != null)
			this.positionAmerican_football_positions = (IAmerican_football_positions) IOBIEThing
					.getCloneConstructor(soccerPlayer.getPositionAmerican_football_positions().getClass())
					.newInstance(soccerPlayer.getPositionAmerican_football_positions());
		for (int j = 0; j < soccerPlayer.getTeamSoccerClubs().size(); j++) {
			if (soccerPlayer.getTeamSoccerClubs().get(j) != null) {
				teamSoccerClubs.add((ISoccerClub) IOBIEThing
						.getCloneConstructor(soccerPlayer.getTeamSoccerClubs().get(j).getClass())
						.newInstance(soccerPlayer.getTeamSoccerClubs().get(j)));
			} else {
				teamSoccerClubs.add(null);
			}
		}
		this.textMention = soccerPlayer.getTextMention();
	}

	/***/
	@Override
	public SoccerPlayer addBirthPlace(IPlace place) {
		this.birthPlaces.add(place);
		return this;
	}

	/***/
	@Override
	public SoccerPlayer addTeamSoccerClub(ISoccerClub soccerClub) {
		this.teamSoccerClubs.add(soccerClub);
		return this;
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
		SoccerPlayer other = (SoccerPlayer) obj;
		if (individual == null) {
			if (other.individual != null)
				return false;
		} else if (!individual.equals(other.individual))
			return false;
		if (birthYear == null) {
			if (other.birthYear != null)
				return false;
		} else if (!birthYear.equals(other.birthYear))
			return false;
		if (birthPlaces == null) {
			if (other.birthPlaces != null)
				return false;
		} else if (!birthPlaces.equals(other.birthPlaces))
			return false;
		if (textMention == null) {
			if (other.textMention != null)
				return false;
		} else if (!textMention.equals(other.textMention))
			return false;
		if (positionAmerican_football_positions == null) {
			if (other.positionAmerican_football_positions != null)
				return false;
		} else if (!positionAmerican_football_positions.equals(other.positionAmerican_football_positions))
			return false;
		if (characterOffset == null) {
			if (other.characterOffset != null)
				return false;
		} else if (!characterOffset.equals(other.characterOffset))
			return false;
		if (teamSoccerClubs == null) {
			if (other.teamSoccerClubs != null)
				return false;
		} else if (!teamSoccerClubs.equals(other.teamSoccerClubs))
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
	public List<IPlace> getBirthPlaces() {
		return birthPlaces;
	}

	/***/
	@Override
	public IBirthYear getBirthYear() {
		return birthYear;
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
	public IAmerican_football_positions getPositionAmerican_football_positions() {
		return positionAmerican_football_positions;
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
	public List<ISoccerClub> getTeamSoccerClubs() {
		return teamSoccerClubs;
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
		result = prime * result + ((this.birthYear == null) ? 0 : this.birthYear.hashCode());
		result = prime * result + ((this.birthPlaces == null) ? 0 : this.birthPlaces.hashCode());
		result = prime * result + ((this.textMention == null) ? 0 : this.textMention.hashCode());
		result = prime * result + ((this.positionAmerican_football_positions == null) ? 0
				: this.positionAmerican_football_positions.hashCode());
		result = prime * result + ((this.characterOffset == null) ? 0 : this.characterOffset.hashCode());
		result = prime * result + ((this.teamSoccerClubs == null) ? 0 : this.teamSoccerClubs.hashCode());
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
	public SoccerPlayer setBirthPlaces(List<IPlace> places) {
		if (places == null) {
			throw new IllegalArgumentException("Can not set list objects to null.");
		}
		this.birthPlaces = places;
		return this;
	}

	/***/
	@Override
	public SoccerPlayer setBirthYear(IBirthYear birthYear) {
		this.birthYear = birthYear;
		return this;
	}

	/***/
	@Override
	public void setCharacterOnset(Integer onset) {
		this.characterOnset = onset;
		this.characterOffset = onset + textMention.length();
	}

	/***/
	@Override
	public SoccerPlayer setPositionAmerican_football_positions(
			IAmerican_football_positions american_football_positions) {
		this.positionAmerican_football_positions = american_football_positions;
		return this;
	}

	/***/
	@Override
	public SoccerPlayer setTeamSoccerClubs(List<ISoccerClub> soccerClubs) {
		if (soccerClubs == null) {
			throw new IllegalArgumentException("Can not set list objects to null.");
		}
		this.teamSoccerClubs = soccerClubs;
		return this;
	}

	@Override
	public String toString() {
		return "SoccerPlayer [individual=" + individual + ",birthPlaces=" + birthPlaces + ",birthYear=" + birthYear
				+ ",characterOffset=" + characterOffset + ",characterOnset=" + characterOnset
				+ ",positionAmerican_football_positions=" + positionAmerican_football_positions + ",serialVersionUID="
				+ serialVersionUID + ",teamSoccerClubs=" + teamSoccerClubs + ",textMention=" + textMention + "]";
	}

}
