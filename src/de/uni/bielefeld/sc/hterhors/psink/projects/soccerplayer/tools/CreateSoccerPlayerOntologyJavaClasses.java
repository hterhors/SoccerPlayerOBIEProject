package de.uni.bielefeld.sc.hterhors.psink.projects.soccerplayer.tools;

import de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.OWLToJavaBinaries;

public class CreateSoccerPlayerOntologyJavaClasses {

	public static void main(String[] args) throws Exception {

		OWLToJavaBinaries builder = new OWLToJavaBinaries(SoccerPlayerOntologyBuilderEnvironment.getInstance());

		builder.buildAndWriteClasses();

		builder.buildAndWriteInterfaces();

	}

}
