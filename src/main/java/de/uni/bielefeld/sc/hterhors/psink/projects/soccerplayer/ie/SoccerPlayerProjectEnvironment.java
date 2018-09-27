package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.projects.AbstractProjectEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayerThing;

public class SoccerPlayerProjectEnvironment extends AbstractProjectEnvironment {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static Logger log = LogManager.getRootLogger();

	private static SoccerPlayerProjectEnvironment instance = new SoccerPlayerProjectEnvironment();

	private final File rawCorpusFile = new File("corpus/soccerplayer_4_props_v1.bin");

	private final File projectCorpusDirectory = new File("bigram/corpus/");

	public static SoccerPlayerProjectEnvironment getInstance() {
		log.info("Return instance of " + SoccerPlayerProjectEnvironment.class.getName());
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
