package de.hterhors.obie.projects.soccerplayer.ie.corpus;

import de.hterhors.obie.core.ontology.OntologyInitializer;
import de.hterhors.obie.ml.tools.BigramCorpusBuilder;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerProjectEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.ner.regex.SoccerPlayerRegExNEL;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayer;

public class BigramCorpusCreator {

	public static void main(String[] args) throws Exception {

		OntologyInitializer.initializeOntology(SoccerPlayerOntologyEnvironment.getInstance());

		BigramCorpusBuilder.overrideCorpusFileIfExists = true;

		new BigramCorpusBuilder(SoccerPlayerProjectEnvironment.getInstance(),
				SoccerPlayerOntologyEnvironment.getInstance(), new SoccerPlayerRegExNEL(ISoccerPlayer.class));

	}

}