package de.hterhors.obie.projects.soccerplayer.ie.baseline;

import de.hterhors.obie.core.evaluation.PRF1Container;
import de.hterhors.obie.ml.corpus.distributor.FoldCrossCorpusDistributor;
import de.hterhors.obie.ml.run.AbstractOBIERunner;
import de.hterhors.obie.ml.run.StandardRERunner;
import de.hterhors.obie.ml.run.param.OBIERunParameter;
import de.hterhors.obie.ml.run.param.OBIERunParameter.Builder;
import de.hterhors.obie.ml.tools.baseline.HighFrequencyBaseline;
import de.hterhors.obie.projects.soccerplayer.ie.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.SoccerPlayerParameterQuickAccess;
import de.hterhors.obie.projects.soccerplayer.ie.SoccerPlayerProjectEnvironment;

public class ComputeHighFreqBaseline {

	public static void main(String[] args) throws Exception {

		nFoldCrossValidation();
	}

	private static void nFoldCrossValidation() throws Exception {
		PRF1Container mean = new PRF1Container(0, 0, 0);

		long allTime = System.currentTimeMillis();

		Builder paramBuilder = SoccerPlayerParameterQuickAccess.getREParameter();

		paramBuilder
				.setCorpusDistributor(SoccerPlayerParameterQuickAccess.predefinedDistributor.foldCrossDist(1F));

		paramBuilder.setOntologyEnvironment(SoccerPlayerOntologyEnvironment.getInstance());
		paramBuilder.setProjectEnvironment(SoccerPlayerProjectEnvironment.getInstance());

		OBIERunParameter param = paramBuilder.build();
		AbstractOBIERunner runner = new StandardRERunner(param);

		while (runner.corpusProvider.nextFold()) {

			System.out.println("#############################");
			System.out.println("New " + ((FoldCrossCorpusDistributor) runner.parameter.corpusDistributor).n
					+ "-fold cross validation iteration: "
					+ String.valueOf(runner.corpusProvider.getCurrentFoldIndex() + 1));
			long time = System.currentTimeMillis();

			System.out.println("Set test instances to:");
			runner.corpusProvider.getTestCorpus().getInternalInstances().forEach(System.out::println);
			System.out.println("#############################");
			PRF1Container pfr1 = new HighFrequencyBaseline(param).run(runner.corpusProvider.getTestCorpus());

			mean = new PRF1Container((mean.p + pfr1.p) / 2, (mean.r + pfr1.r) / 2, (mean.f1 + pfr1.f1) / 2);
			System.out.println("Time needed: " + (System.currentTimeMillis() - time));

		}
		System.out.println(((FoldCrossCorpusDistributor) runner.parameter.corpusDistributor).n
				+ " fold cross validation mean: " + mean);
		System.out.println("Time needed: " + (System.currentTimeMillis() - allTime));

	}
}