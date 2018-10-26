package de.hterhors.obie.projects.soccerplayer.ie;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.hterhors.obie.core.ontology.AbstractOntologyEnvironment;
import de.hterhors.obie.core.owlreader.IClassFilter;
import de.hterhors.obie.core.owlreader.container.OntologyClass;

public class SoccerPlayerOntologyEnvironment extends AbstractOntologyEnvironment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static SoccerPlayerOntologyEnvironment instance = new SoccerPlayerOntologyEnvironment();

	private final String ontologyBasePackage = "de.hterhors.obie.projects.soccerplayer.ontology.";

	final public static int version = 1;
//	public static final File ONTOLOGY_FILE = new File("owl/PopulatedPlace_autoGen_v1.owl");
//	public static final File ONTOLOGY_FILE = new File("owl/Manga_autoGen_v1.owl");
	public static final File ONTOLOGY_FILE = new File("owl/soccer_player_v" + version + ".owl");
	public static final String DEFAULT_DATA_NAMESPACE = "http://psink/soccerPlayer/";
//	public static final String DEFAULT_DATA_NAMESPACE = "http://psink/manga/";
//	public static final String DEFAULT_DATA_NAMESPACE = "http://psink/populatedPlace/";
//	public static final String ONTOLOGY_NAME = "Manga";
//	public static final String ONTOLOGY_NAME = "PopulatedPlace";
	public static final String ONTOLOGY_NAME = "SoccerPlayer";

	public final String ONTOLOGY_SRC_LOCATION = "src/main/java/" + getBasePackage().replaceAll("\\.", "/");

	@Override
	public int getOntologyVersion() {
		return version;
	}

	@Override
	public String getDataNameSpace() {
		return DEFAULT_DATA_NAMESPACE;
	}

	@Override
	public File getOntologyFile() {
		return ONTOLOGY_FILE;
	}

	public static SoccerPlayerOntologyEnvironment getInstance() {
		return instance;
	}

	@Override
	public String getOntologyName() {
		return ONTOLOGY_NAME;
	}

	@Override
	public IClassFilter getOwlClassFilter() {
		return new IClassFilter() {
			@Override
			public boolean matchesCondition(OntologyClass ocd) {
				return true;
			}
		};
	}

	@Override
	public Set<String> getCollectiveClasses() {
		return Collections.emptySet();
	}

	@Override
	public String getOntologySourceLocation() {
		return ONTOLOGY_SRC_LOCATION;
	}

	@Override
	public String getBasePackage() {
		return ontologyBasePackage;
	}

	@Override
	public String getAdditionalPrefixes() {
		return "";
	}

	@Override
	public List<String> getAdditionalPropertyNames() {
		return Collections.emptyList();
	}

}
