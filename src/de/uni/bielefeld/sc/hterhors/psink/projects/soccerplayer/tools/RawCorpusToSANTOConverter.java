package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.tools;

import java.io.File;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.annodb.ConvertRawCorpusToSANTOFormat;
import de.uni.bielefeld.sc.hterhors.psink.obie.core.tools.annodb.OWLToAnnoDBConfigurationConverter;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.SoccerPlayerProjectEnvironment;

/**
 * Loads the raw corpus and transforms it into an SANTO format for file writing.
 * 
 * @author hterhors
 *
 */
public class RawCorpusToSANTOConverter {

	public static void main(String[] args) {

		final File parentCSVDirectory = new File("annodb/configuration/corpus");
		final File parentCorpusDirectory = new File("annodb/corpus");

		new OWLToAnnoDBConfigurationConverter(parentCSVDirectory, SoccerPlayerOntologyBuilderEnvironment.getInstance());

		new ConvertRawCorpusToSANTOFormat(parentCorpusDirectory, SoccerPlayerProjectEnvironment.getInstance());

	}

}
