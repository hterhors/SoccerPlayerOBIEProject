package de.hterhors.obie.projects.soccerplayer.ie;

import de.hterhors.obie.core.ontology.OntologyInitializer;
import de.hterhors.obie.ml.corpus.BigramCorpusProvider;
import de.hterhors.obie.ml.run.param.RunParameter;
import de.hterhors.obie.ml.run.param.RunParameter.Builder;
import de.hterhors.obie.ml.tools.upperbound.UpperBound;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerProjectEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.parameter.SoccerPlayerParameterQuickAccess;
import de.hterhors.obie.projects.soccerplayer.ie.templates.BirthDeathYearTemplate;

/**
 * This class contains example code for analyzing a bigram-corpus given the
 * named entity recognition and linking annotations.
 * 
 * The results of this analysis can be seen as upper bound of what a perfect
 * system can reach relying on the NERL annotations.
 * 
 * @author hterhors
 *
 */
public class UpperBoundExample {

	public static void main(String[] args) {

		OntologyInitializer.initializeOntology(SoccerPlayerOntologyEnvironment.getInstance());

		/*
		 * Get some standard parameter.
		 */
		final RunParameter param = getStandardParameter().build();

		final BigramCorpusProvider corpusProvider = BigramCorpusProvider.loadCorpusFromFile(param);

		new UpperBound(param, corpusProvider.getTrainingCorpus());

	}

	/**
	 * Set some standard parameter.
	 * 
	 * @return
	 */
	private static Builder getStandardParameter() {
		Builder paramBuilder = SoccerPlayerParameterQuickAccess.getBaseParameter();

		paramBuilder.setProjectEnvironment(SoccerPlayerProjectEnvironment.getInstance());
		paramBuilder.setOntologyEnvironment(SoccerPlayerOntologyEnvironment.getInstance());
		paramBuilder.setCorpusDistributor(SoccerPlayerParameterQuickAccess.predefinedDistributor.originDist(1.0F));

		return paramBuilder;
	}
}

//UpperBound = PRF1 [tp=10953.0, fp=0.0, fn=730.0, getF1()=0.9677504859515815, getRecall()=0.9375160489600274, getPrecision()=1.0, getJaccard()=0.9375160489600274]
//Failures:
//class de.hterhors.obie.projects.soccerplayer.ontology.classes.Place=364
//class de.hterhors.obie.projects.soccerplayer.ontology.classes.American_football_positions=223
//class de.hterhors.obie.projects.soccerplayer.ontology.classes.SoccerClub=128
