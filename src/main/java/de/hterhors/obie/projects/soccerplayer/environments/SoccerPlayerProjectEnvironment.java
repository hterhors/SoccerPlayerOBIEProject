package de.hterhors.obie.projects.soccerplayer.environments;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import de.hterhors.obie.core.projects.AbstractProjectEnvironment;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayerThing;

public class SoccerPlayerProjectEnvironment extends AbstractProjectEnvironment<ISoccerPlayerThing> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static Logger log = LogManager.getRootLogger();

	private static SoccerPlayerProjectEnvironment instance = new SoccerPlayerProjectEnvironment();
	/**
	 * The corpus name prefix. Shortly describing the corpus.
	 */
	final private static String corpusPrefix = "4To6Props";

	private final File rawCorpusFile = new File(
			"corpus/raw_corpus_soccerPlayer4To6Prop_v" + SoccerPlayerOntologyEnvironment.version + ".bin");

	private final File projectCorpusDirectory = new File("bigram/corpus/");

	public static SoccerPlayerProjectEnvironment getInstance() {
		log.info("Return instance of " + SoccerPlayerProjectEnvironment.class.getName());
		return instance;
	}

	@Override
	public Class<? extends ISoccerPlayerThing> getOntologyThingInterface() {
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

	@Override
	public String getCorpusName() {
		return corpusPrefix;
	}

}
