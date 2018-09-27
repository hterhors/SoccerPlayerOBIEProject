package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.corpus;

import java.util.HashSet;
import java.util.Set;

import de.uni.bielefeld.sc.hterhors.psink.obie.ie.ner.INamedEntitityLinker;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.tools.BigramCorpusBuilder;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.SoccerPlayerOntologyEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.SoccerPlayerProjectEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.ner.regex.SoccerPlayerRegExNEL;

public class BigramCorpusCreator {

	/**
	 * The corpus name prefix. This can be arbitrary but should tell something about
	 * the corpus.
	 */
	final private static String corpusPrefix = "rwss2018";

	public static void main(String[] args) throws Exception {

		Set<Class<? extends INamedEntitityLinker>> linker = new HashSet<>();

		linker.add(SoccerPlayerRegExNEL.class);

		BigramCorpusBuilder.overrideCorpusFileIfExists = true;

		int ontologyVersion = SoccerPlayerOntologyEnvironment.version;

		new BigramCorpusBuilder(SoccerPlayerProjectEnvironment.getInstance(), linker, corpusPrefix, ontologyVersion);

	}

}
