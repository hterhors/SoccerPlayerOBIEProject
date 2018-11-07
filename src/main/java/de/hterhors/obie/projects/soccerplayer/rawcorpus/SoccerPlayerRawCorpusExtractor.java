package de.hterhors.obie.projects.soccerplayer.rawcorpus;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import de.hterhors.dbpedia.obie.corpus.GenericCorpusExtractor;
import de.hterhors.dbpedia.obie.corpus.GenericCorpusExtractor.IInstanceRestrictionFilter;
import de.hterhors.dbpedia.obie.infobox.DBPediaInfoBoxReaderConfig;
import de.hterhors.dbpedia.obie.wikipage.WikiPageReaderConfig;
import de.hterhors.obie.core.ontology.AbstractOntologyEnvironment;
import de.hterhors.obie.core.ontology.annotations.RelationTypeCollection;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import de.hterhors.obie.ml.dtinterpreter.IDatatypeInterpreter;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.dtinterpreter.SoccerPlayerInterpreter;
import de.hterhors.obie.projects.soccerplayer.ontology.classes.SoccerPlayer;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayerThing;

public class SoccerPlayerRawCorpusExtractor {

	static final public int MAX_NUMBER_OF_ELEMENTS = 3;

	public static void main(String[] args) throws Exception {

		final String directoryPrefix = "/home/hterhors/git/DBPediaOBIECorpusExtractor/";

		AbstractOntologyEnvironment ontologyEnvironment = SoccerPlayerOntologyEnvironment.getInstance();
		IDatatypeInterpreter<ISoccerPlayerThing> interpreter = SoccerPlayerInterpreter.getInstance();
		Class<? extends IOBIEThing> mainResourceClass = SoccerPlayer.class;

		DBPediaInfoBoxReaderConfig infoBoxConfig = new DBPediaInfoBoxReaderConfig(
				new File(directoryPrefix + "data/infobox/ontology_properties_sorted.nt"),
				new File(directoryPrefix + "data/infobox/properties_index.tsv"), "\t");
		WikiPageReaderConfig wikiPageConfig = new WikiPageReaderConfig(new File(directoryPrefix + "data/en-json"),
				new File(directoryPrefix + "data/en-json/index.tsv"), "\t");

		IInstanceRestrictionFilter instanceRestrictionFilter = new IInstanceRestrictionFilter() {

			@SuppressWarnings("unchecked")
			@Override
			public boolean applyFilter(IOBIEThing thing) {
				for (Field field : thing.getClass().getDeclaredFields()) {
					field.setAccessible(true);

					if (field.isAnnotationPresent(RelationTypeCollection.class)) {
						try {
							if (((List<IOBIEThing>) field.get(thing)).size() > MAX_NUMBER_OF_ELEMENTS) {
								return false;
							}
						} catch (Exception e) {
							return false;
						}
					}
				}
				return true;
			}
		};

		final GenericCorpusExtractor<ISoccerPlayerThing> c = new GenericCorpusExtractor<ISoccerPlayerThing>(
				ontologyEnvironment, interpreter, mainResourceClass, infoBoxConfig, wikiPageConfig) {

			@Override
			public String mapResources(String resourceName) {
				/**
				 * We do cleaning here e.g. Goalkeeper equals GoalkeeperAssociationFootball
				 * because there is no reason why there should be 2 types of goal keeper.
				 */
				if (resourceName.endsWith("Goalkeeper"))
					return "Goalkeeper_(association_football)";
				if (resourceName.endsWith("Midfield"))
					return "Midfielder";

				return resourceName;
			}

			@Override
			public String mapProperties(String propertyName) {
				if (propertyName.equals("number"))
					return "playerNumber";
				return propertyName;
			}

		};

		c.instanceRestrictionFilter = instanceRestrictionFilter;

		c.extractCorpus(new File(directoryPrefix + "data/looseSelectionOutput4To6/SoccerPlayer.txt"));

		c.distributeInstances(new Random(100L), 80, 20, 20);

		c.storeCorpusJavaSerialization(
				new File(
						"corpus/raw_corpus_soccerPlayer4To6Prop_v" + ontologyEnvironment.getOntologyVersion() + ".bin"),
				"SoccerPlayer corpus with 4 To 5 properties.");
	}

}
