package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.baseline;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.evaluation.PRF1Container;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.corpus.distributor.FoldCrossCorpusDistributor;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.AbstractOBIERunner;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.StandardRERunner;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.OBIERunParameter;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.OBIERunParameter.OBIEParameterBuilder;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.tools.baseline.HighFrequencyBaseline;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.SoccerPlayerParameterQuickAccess;

public class HighFreqBaseline {

	public static void main(String[] args) throws Exception {

		nFoldCrossValidation();
	}

	private static void nFoldCrossValidation() throws Exception {
		PRF1Container mean = new PRF1Container(0, 0, 0);

		long allTime = System.currentTimeMillis();

		OBIEParameterBuilder paramBuilder = SoccerPlayerParameterQuickAccess.getREParameter();

		paramBuilder
				.setCorpusDistributor(SoccerPlayerParameterQuickAccess.preDefinedCorpusDistributor.foldCrossDist(1F));

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
