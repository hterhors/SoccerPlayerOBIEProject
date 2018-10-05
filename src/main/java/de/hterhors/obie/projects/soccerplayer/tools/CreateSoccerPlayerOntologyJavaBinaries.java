package de.hterhors.obie.projects.soccerplayer.tools;

import de.hterhors.obie.projects.soccerplayer.ie.SoccerPlayerOntologyEnvironment;
import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.OWLToJavaBinaries;

public class CreateSoccerPlayerOntologyJavaBinaries {

	public static void main(String[] args) throws Exception {

		OWLToJavaBinaries builder = new OWLToJavaBinaries(SoccerPlayerOntologyEnvironment.getInstance());

		builder.buildAndWriteClasses();

		builder.buildAndWriteInterfaces();

	}

}
