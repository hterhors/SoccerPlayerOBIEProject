package de.hterhors.obie.projects.soccerplayer.tools.santo;

import java.io.File;

import de.hterhors.obie.core.tools.annodb.ConvertRawCorpusToSANTOFormat;
import de.hterhors.obie.core.tools.annodb.OWLToAnnoDBConfigurationConverter;
import de.hterhors.obie.projects.soccerplayer.ie.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.SoccerPlayerProjectEnvironment;

/**
 * Loads the raw corpus and transforms it into a SANTO format for file writing.
 * 
 * @author hterhors
 *
 */
public class RawCorpusToSANTOConverter {

	public static void main(String[] args) {

		final File parentCSVDirectory = new File("annodb/configuration/corpus");
		final File parentCorpusDirectory = new File("annodb/corpus");

		new OWLToAnnoDBConfigurationConverter(parentCSVDirectory, SoccerPlayerOntologyEnvironment.getInstance());

		new ConvertRawCorpusToSANTOFormat(parentCorpusDirectory, SoccerPlayerProjectEnvironment.getInstance());

	}

}
