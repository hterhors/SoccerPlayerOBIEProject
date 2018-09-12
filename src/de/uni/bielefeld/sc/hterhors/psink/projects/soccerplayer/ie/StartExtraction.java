package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie;

import java.util.HashSet;
import java.util.Set;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.evaluation.PRF1Container;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.AbstractOBIERunner;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.StandardRERunner;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.OBIERunParameter;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.run.param.OBIERunParameter.OBIEParameterBuilder;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.templates.AbstractOBIETemplate;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.templates.InBetweenContextTemplate;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.templates.InterTokenTemplate;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.templates.LocalTemplate;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.templates.SlotIsFilledTemplate;
import de.uni.bielefeld.sc.hterhors.psink.obie.ie.templates.TokenContextTemplate;

/**
 *
 * Preferred VM parameter: -Xmx12g -XX:+UseG1GC -XX:+UseStringDeduplication
 * 
 * @author hterhors
 *
 */
public class StartExtraction {

	public static void main(String[] args) throws Exception {

		new StartExtraction();

	}

	/**
	 * The runID. This serves as an identifier for locating and saving the model. If
	 * anything was changed during the development the runID should be reset.
	 */
	public final static String runID = "-birthYearTemplate";

	public StartExtraction() throws Exception {

		OBIEParameterBuilder paramBuilder = SoccerPlayerParameterQuickAccess.getREParameter();

		paramBuilder.setCorpusDistributor(SoccerPlayerParameterQuickAccess.originDist());
		paramBuilder.setRunID(runID);

		addTemplates(paramBuilder);

		OBIERunParameter parameter = paramBuilder.build();

		AbstractOBIERunner runner = new StandardRERunner(parameter);

		run(runner);
	}

	private void addTemplates(OBIEParameterBuilder paramBuilder) {

		Set<Class<? extends AbstractOBIETemplate<?>>> templates = new HashSet<>();

//		templates.add(FrequencyTemplate.class);

//		templates.add(BirthYearTemplate.class);
//		templates.add(PriorTemplate.class);

		templates.add(TokenContextTemplate.class);
		templates.add(InterTokenTemplate.class);
		templates.add(SlotIsFilledTemplate.class);
		templates.add(InBetweenContextTemplate.class);
		templates.add(LocalTemplate.class);

		/*
		 * Cardinality
		 */

//		templates.add(MainSlotVarietyTemplate.class);
		paramBuilder.setTemplates(templates);
	}
	// 100 Epochs
//	Time needed: 10672169
//	p: 0.8522702104097447	r: 0.7169988925802879	f1: 0.7788043438548948

	private static void run(AbstractOBIERunner runner) throws Exception {

		long time = System.currentTimeMillis();

		if (runner.modelExists()) {
			runner.loadModel();
		} else {
			runner.train();
		}

		PRF1Container pfr1 = runner.evaluateOnTest();

		System.out.println("Time needed: " + (System.currentTimeMillis() - time));

		System.out.println(pfr1);

	}

}
