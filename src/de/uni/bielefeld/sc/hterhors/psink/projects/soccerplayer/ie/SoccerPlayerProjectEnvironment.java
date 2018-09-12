package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie;

import java.io.File;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.projects.AbstractOBIEProjectEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayerThing;

public class SoccerPlayerProjectEnvironment extends AbstractOBIEProjectEnvironment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static SoccerPlayerProjectEnvironment instance = new SoccerPlayerProjectEnvironment();

	private final String ontologyBasePackage = "de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.";

	private final int ontologyVersion = 1;

	private final File rawCorpusFile = new File("corpus/soccerplayer_4_props_v1.bin");

	private final File projectCorpusDirectory = new File("bigram/corpus/");

	@Override
	public String getOntologyBasePackage() {
		return ontologyBasePackage;
	}

	@Override
	public int getOntologyVersion() {
		return ontologyVersion;
	}

	public static SoccerPlayerProjectEnvironment getInstance() {
		return instance;
	}

	@Override
	public Class<? extends IOBIEThing> getOntologyThingInterface() {
		return ISoccerPlayerThing.class;
	}

	@Override
	public File getRawCorpusFile() {
		return rawCorpusFile;
	}

	@Override
	public File getBigramCorpusFileDirectory() {
		return projectCorpusDirectory;
	}

}
