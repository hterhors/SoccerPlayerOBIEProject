package de.hterhors.obie.projects.soccerplayer.examples;

import java.util.List;

import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import de.hterhors.obie.core.tools.corpus.OBIECorpus;
import de.hterhors.obie.core.tools.corpus.OBIECorpus.Instance;
import de.hterhors.obie.ml.utils.OBIEClassFormatter;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerProjectEnvironment;

/**
 * This class contains example code for loading the raw corpus file and printing
 * the first instances. This corpus is NOT used in the ML framework. Before
 * this, we need to convert the raw corpus into a bigram corpus using the
 * BigramCorpusCreator.
 * 
 * Doing this the corpus is enriched with information provided by a specified
 * Named Entity Recognition and Linking Tool.
 * 
 * @author hterhors
 *
 * @See BigramCorpusCreator
 */
public class PrinRawCorpusExample {

	public static void main(String[] args) {

		/**
		 * Load the raw corpus from file system. This corpus contains only the document
		 * content and annotations of the template but NO named entity recognition and
		 * linking annotations!
		 */
		final OBIECorpus rawCorpus = OBIECorpus
				.readRawCorpusData(SoccerPlayerProjectEnvironment.getInstance().getRawCorpusFile());

		for (Instance instance : rawCorpus.getInstances().values()) {

			System.out.println("________" + instance.name + "________");
			System.out.println(instance.content);
			System.out.println("____________Template Annotation(s)____________");

			for (List<IOBIEThing> templateAnnotations : instance.annotations.values()) {
				for (IOBIEThing templateAnnotation : templateAnnotations) {
					System.out.println(OBIEClassFormatter.format(templateAnnotation));
				}

			}

		}
	}
}