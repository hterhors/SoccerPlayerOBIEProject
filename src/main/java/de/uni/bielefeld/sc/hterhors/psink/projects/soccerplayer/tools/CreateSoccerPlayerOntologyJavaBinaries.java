package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.tools;

import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.OWLToJavaBinaries;
import de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.ie.SoccerPlayerOntologyEnvironment;

public class CreateSoccerPlayerOntologyJavaBinaries {

	public static void main(String[] args) throws Exception {

		OWLToJavaBinaries builder = new OWLToJavaBinaries(SoccerPlayerOntologyEnvironment.getInstance());

		builder.buildAndWriteClasses();

		builder.buildAndWriteInterfaces();

	}

}
