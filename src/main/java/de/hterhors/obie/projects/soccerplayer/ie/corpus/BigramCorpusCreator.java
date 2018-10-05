package de.hterhors.obie.projects.soccerplayer.ie.corpus;

import java.util.HashSet;
import java.util.Set;

import de.hterhors.obie.ml.ner.INamedEntitityLinker;
import de.hterhors.obie.ml.tools.BigramCorpusBuilder;
import de.hterhors.obie.projects.soccerplayer.ie.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.SoccerPlayerProjectEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.ner.regex.SoccerPlayerRegExNEL;

public class BigramCorpusCreator {

	/**
	 * The corpus name prefix. This can be arbitrary but should tell something about
	 * the corpus. m5eps = max 5 elements per slot
	 */
	final private static String corpusPrefix = "m5eps";

	public static void main(String[] args) throws Exception {

		Set<Class<? extends INamedEntitityLinker>> linker = new HashSet<>();

		linker.add(SoccerPlayerRegExNEL.class);

		BigramCorpusBuilder.overrideCorpusFileIfExists = true;


		new BigramCorpusBuilder(SoccerPlayerProjectEnvironment.getInstance(), linker, corpusPrefix,
				SoccerPlayerOntologyEnvironment.getInstance());

	}

}
