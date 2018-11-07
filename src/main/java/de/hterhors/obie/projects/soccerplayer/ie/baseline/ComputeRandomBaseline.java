package de.hterhors.obie.projects.soccerplayer.ie.baseline;

import java.util.Random;

import de.hterhors.obie.core.evaluation.PRF1Container;
import de.hterhors.obie.core.ontology.OntologyInitializer;
import de.hterhors.obie.ml.corpus.distributor.FoldCrossCorpusDistributor;
import de.hterhors.obie.ml.run.AbstractRunner;
import de.hterhors.obie.ml.run.StandardRERunner;
import de.hterhors.obie.ml.run.param.RunParameter;
import de.hterhors.obie.ml.run.param.RunParameter.Builder;
import de.hterhors.obie.ml.tools.baseline.RandomBaseline;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerProjectEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.parameter.SoccerPlayerParameterQuickAccess;

public class ComputeRandomBaseline {

	public static void main(String[] args) throws Exception {

		OntologyInitializer.initializeOntology(SoccerPlayerOntologyEnvironment.getInstance());

		nFoldCrossValidation();
	}

	private static void nFoldCrossValidation() throws Exception {
		PRF1Container mean = new PRF1Container(0, 0, 0);

		long allTime = System.currentTimeMillis();

		Builder paramBuilder = SoccerPlayerParameterQuickAccess.getREParameter();

		paramBuilder.setCorpusDistributor(SoccerPlayerParameterQuickAccess.predefinedDistributor.foldCrossDist(1F));

		paramBuilder.setOntologyEnvironment(SoccerPlayerOntologyEnvironment.getInstance());
		paramBuilder.setProjectEnvironment(SoccerPlayerProjectEnvironment.getInstance());

		RunParameter param = paramBuilder.build();
		AbstractRunner runner = new StandardRERunner(param);

		final long initSeed = 100L;

		while (runner.corpusProvider.nextFold()) {

			long seed = new Random().nextLong();

			System.out.println("#############################");
			System.out.println("New " + ((FoldCrossCorpusDistributor) runner.getParameter().corpusDistributor).n
					+ "-fold cross validation iteration: "
					+ String.valueOf(runner.corpusProvider.getCurrentFoldIndex() + 1));
			long time = System.currentTimeMillis();

//			System.out.println("Set test instances to:");
//			runner.corpusProvider.getTestCorpus().getInternalInstances().forEach(System.out::println);
			System.out.println("#############################");
			PRF1Container pfr1 = new RandomBaseline(param, seed).run(runner.corpusProvider.getTestCorpus());

			mean = new PRF1Container((mean.p + pfr1.p) / 2, (mean.r + pfr1.r) / 2, (mean.f1 + pfr1.f1) / 2);
			System.out.println("Time needed: " + (System.currentTimeMillis() - time));

		}
		System.out.println(((FoldCrossCorpusDistributor) runner.getParameter().corpusDistributor).n
				+ " fold cross validation mean: ");
		System.out.println(mean);
		System.out.println("Time needed: " + (System.currentTimeMillis() - allTime));

	}
}
