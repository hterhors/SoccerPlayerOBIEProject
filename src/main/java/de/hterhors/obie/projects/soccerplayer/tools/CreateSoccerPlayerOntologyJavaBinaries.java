package de.hterhors.obie.projects.soccerplayer.tools;

import de.hterhors.obie.projects.soccerplayer.ie.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.tools.owl2javabin.OWLToJavaBinaries;

public class CreateSoccerPlayerOntologyJavaBinaries {

	public static void main(String[] args) throws Exception {

		OWLToJavaBinaries builder = new OWLToJavaBinaries(SoccerPlayerOntologyEnvironment.getInstance());

		builder.buildAndWriteClasses();

		builder.buildAndWriteInterfaces();

	}

}
