package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.corpus;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni.bielefeld.sc.hterhors.psink.obie.ie.ner.INamedEntitityLinker;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.tools.BigramCorpusBuilder;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.SoccerPlayerProjectEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.ner.regex.SoccerPlayerRegExNEL;

public class BigramCorpusCreator {

	final private static String corpusPrefix = "small";

	protected static Logger log = LogManager.getFormatterLogger(BigramCorpusCreator.class);

	public static void main(String[] args) throws Exception {

		Set<Class<? extends INamedEntitityLinker>> linker = new HashSet<>();
		linker.add(SoccerPlayerRegExNEL.class);

		new BigramCorpusBuilder(SoccerPlayerProjectEnvironment.getInstance(), linker, corpusPrefix);

	}

}
