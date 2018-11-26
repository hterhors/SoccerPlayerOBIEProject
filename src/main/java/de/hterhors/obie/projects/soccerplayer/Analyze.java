package de.hterhors.obie.projects.soccerplayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.queries.BoostingQuery;

import de.hterhors.obie.core.owlreader.OWLReader;
import de.hterhors.obie.core.tools.TFIDF;
import de.hterhors.obie.core.tools.corpus.OBIECorpus;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerProjectEnvironment;

public class Analyze {

	public static void main(String[] args) {

		OWLReader owlDataReader = new OWLReader(SoccerPlayerOntologyEnvironment.getInstance());

		Map<String, Integer> tokenList = new ConcurrentHashMap<>(20_000);

		final Map<String, List<String>> documents = new HashMap<String, List<String>>();

		owlDataReader.classes.stream().forEach(resource -> {

			List<String> l = new ArrayList<>();

			final String[] tokens = resource.ontologyClassName.split("\\W|_");
			documents.put(resource.fullyQualifiedOntolgyName, l);
			for (int i = 0; i < tokens.length; i++) {

				if (tokens[i].trim().isEmpty())
					continue;

//				if (tokens[i + 1].trim().isEmpty())
//					continue;

				final String bigram = tokens[i].toLowerCase();// + " " + tokens[i + 1];

				tokenList.put(bigram, tokenList.getOrDefault(bigram, 0) + 1);
				l.add(bigram);
			}

		});

		List<Map.Entry<String, Integer>> sortedWhiteList = new ArrayList<>(tokenList.size());
		for (Entry<String, Integer> entry : tokenList.entrySet()) {
			sortedWhiteList.add(entry);
		}

		Collections.sort(sortedWhiteList, new Comparator<Map.Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return -Integer.compare(o1.getValue(), o2.getValue());
			}

		});
//		sortedWhiteList.forEach(System.out::println);

		Map<String, Double> tfidfs = TFIDF.getIDFs(documents);

//		tfidfs.entrySet().forEach(System.out::println);

		List<Map.Entry<String, Double>> stfid = new ArrayList<>(tokenList.size());
		for (Entry<String, Double> entry : tfidfs.entrySet()) {
			stfid.add(entry);
		}

		Collections.sort(stfid, new Comparator<Map.Entry<String, Double>>() {

			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return Double.compare(o1.getValue(), o2.getValue());
			}

		});
		stfid.forEach(System.out::println);
		//
		final OBIECorpus rawCorpus = OBIECorpus
				.readRawCorpusData(SoccerPlayerProjectEnvironment.getInstance().getRawCorpusFile());

		Map<String, Integer> tokenListInstances = new ConcurrentHashMap<>(20_000);
		rawCorpus.getInstances().values().parallelStream().forEach(resource -> {

			final String[] tokens = resource.content.split("\\W");

			for (int i = 0; i < tokens.length; i++) {

				if (tokens[i].trim().isEmpty())
					continue;

//				if (tokens[i + 1].trim().isEmpty())
//					continue;

				final String bigram = tokens[i].toLowerCase();// + " " + tokens[i + 1];

				tokenListInstances.put(bigram, tokenListInstances.getOrDefault(bigram, 0) + 1);

			}

		});
		
		tokenListInstances.keySet().retainAll(tfidfs.keySet());

		List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(tokenListInstances.size());
		for (Entry<String, Integer> entry : tokenListInstances.entrySet()) {
			sortedList.add(entry);
		}

		Collections.sort(sortedList, new Comparator<Map.Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return -Integer.compare(o1.getValue(), o2.getValue());
			}

		});
		sortedList.forEach(System.out::println);

	}

}
