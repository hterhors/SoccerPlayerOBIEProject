package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.tools;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.AbstractOntologyBuilderEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.IClassFilter;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.owlreader.container.OntologyClass;

public class SoccerPlayerOntologyBuilderEnvironment extends AbstractOntologyBuilderEnvironment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static SoccerPlayerOntologyBuilderEnvironment instance = new SoccerPlayerOntologyBuilderEnvironment();

	private final String ontologyBasePackage = "de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.";

	final public static int version = 1;
	public static final File ONTOLOGY_FILE = new File(
			"/home/hterhors/git/SoccerPlayerOntology/owl/soccer_player_v" + version + ".owl");
	public static final String DEFAULT_NAMESPACE = "http://dbpedia.org/ontology/";
	public static final String ONTOLOGY_NAME = "SoccerPlayer";

	public final String ONTOLOGY_SRC_LOCATION = "/home/hterhors/git/SoccerPlayerOntology/src/"
			+ getBasePackage().replaceAll("\\.", "/");

	@Override
	public int getOntologyVersion() {
		return version;
	}

	@Override
	public String getDataNameSpace() {
		return DEFAULT_NAMESPACE;
	}

	@Override
	public File getOntologyFile() {
		return ONTOLOGY_FILE;
	}

	public static SoccerPlayerOntologyBuilderEnvironment getInstance() {
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
