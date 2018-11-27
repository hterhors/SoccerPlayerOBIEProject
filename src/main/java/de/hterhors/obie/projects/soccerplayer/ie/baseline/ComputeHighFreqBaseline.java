package de.hterhors.obie.projects.soccerplayer.ie.baseline;

import de.hterhors.obie.core.evaluation.PRF1;
import de.hterhors.obie.core.ontology.OntologyInitializer;
import de.hterhors.obie.ml.corpus.distributor.FoldCrossCorpusDistributor;
import de.hterhors.obie.ml.run.AbstractRunner;
import de.hterhors.obie.ml.run.StandardRERunner;
import de.hterhors.obie.ml.run.param.RunParameter;
import de.hterhors.obie.ml.run.param.RunParameter.Builder;
import de.hterhors.obie.ml.tools.baseline.HighFrequencyBaseline;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerProjectEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.parameter.SoccerPlayerParameterQuickAccess;

public class ComputeHighFreqBaseline {

	public static void main(String[] args) throws Exception {
		OntologyInitializer.initializeOntology(SoccerPlayerOntologyEnvironment.getInstance());

		nFoldCrossValidation();
	}

	private static void nFoldCrossValidation() throws Exception {
		PRF1 mean = new PRF1(0, 0, 0);

		long allTime = System.currentTimeMillis();

		Builder paramBuilder = SoccerPlayerParameterQuickAccess.getREParameter();

		paramBuilder.setCorpusDistributor(SoccerPlayerParameterQuickAccess.predefinedDistributor.foldCrossDist(1F));

		paramBuilder.setOntologyEnvironment(SoccerPlayerOntologyEnvironment.getInstance());
		paramBuilder.setProjectEnvironment(SoccerPlayerProjectEnvironment.getInstance());

		RunParameter param = paramBuilder.build();
		AbstractRunner runner = new StandardRERunner(param);

		while (runner.corpusProvider.nextFold()) {

			System.out.println("#############################");
			System.out.println("New " + ((FoldCrossCorpusDistributor) runner.getParameter().corpusDistributor).n
					+ "-fold cross validation iteration: "
					+ String.valueOf(runner.corpusProvider.getCurrentFoldIndex() + 1));
			long time = System.currentTimeMillis();

			System.out.println("Set test instances to:");
			runner.corpusProvider.getTestCorpus().getInternalInstances().forEach(System.out::println);
			System.out.println("#############################");
			PRF1 prf1 = new HighFrequencyBaseline(param).run(runner.corpusProvider.getTestCorpus());

			mean.add(prf1);
			System.out.println("Time needed: " + (System.currentTimeMillis() - time));

		}
		System.out.println(((FoldCrossCorpusDistributor) runner.getParameter().corpusDistributor).n
				+ " fold cross validation mean: " + mean);
		System.out.println("Time needed: " + (System.currentTimeMillis() - allTime));

	}
}
//Most frequent baseline mean-P = 0.3485754985754987
//Most frequent baseline mean-R = 0.32903269569936244
//Most frequent baseline mean-F1 = 0.3355874419976986
//Time needed: 125
//10 fold cross validation mean: p: 0.3490444433315528	r: 0.32709665160023244	f1: 0.33469538517427666
//Time needed: 15940