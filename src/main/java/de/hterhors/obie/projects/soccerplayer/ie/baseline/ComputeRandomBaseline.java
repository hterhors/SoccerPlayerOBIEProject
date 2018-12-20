package de.hterhors.obie.projects.soccerplayer.ie.baseline;

import java.util.Random;

import de.hterhors.obie.core.evaluation.PRF1;
import de.hterhors.obie.core.ontology.OntologyInitializer;
import de.hterhors.obie.ml.corpus.distributor.FoldCrossCorpusDistributor;
import de.hterhors.obie.ml.run.AbstractRunner;
import de.hterhors.obie.ml.run.DefaultSlotFillingRunner;
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
		PRF1 mean = new PRF1(0, 0, 0);

		long allTime = System.currentTimeMillis();

		Builder paramBuilder = SoccerPlayerParameterQuickAccess.getBaseParameter();

		paramBuilder.setCorpusDistributor(SoccerPlayerParameterQuickAccess.predefinedDistributor.foldCrossDist(1F));

		paramBuilder.setOntologyEnvironment(SoccerPlayerOntologyEnvironment.getInstance());
		paramBuilder.setProjectEnvironment(SoccerPlayerProjectEnvironment.getInstance());

		RunParameter param = paramBuilder.build();
		AbstractRunner runner = new DefaultSlotFillingRunner(param);

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
			PRF1 prf1 = new RandomBaseline(param, seed).run(runner.corpusProvider.getTestCorpus());

			mean.add(prf1);
			System.out.println("Time needed: " + (System.currentTimeMillis() - time));

		}
		System.out.println(((FoldCrossCorpusDistributor) runner.getParameter().corpusDistributor).n
				+ " fold cross validation mean: ");
		System.out.println(mean);
		System.out.println("Time needed: " + (System.currentTimeMillis() - allTime));

	}
}
//Random baseline mean-P = 0.25997998914665593
//Random baseline mean-R = 0.20701227784561116
//Random baseline mean-F1 = 0.22641510422430478
//Time needed: 128
//10 fold cross validation mean: 
//p: 0.2599305946392536	r: 0.20323367420942295	f1: 0.22415451213200396
//Time needed: 16720
