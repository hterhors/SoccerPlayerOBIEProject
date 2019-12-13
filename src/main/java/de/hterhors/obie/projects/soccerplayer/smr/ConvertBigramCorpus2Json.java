package de.hterhors.obie.projects.soccerplayer.smr;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.hterhors.obie.core.ontology.AbstractIndividual;
import de.hterhors.obie.core.ontology.OntologyInitializer;
import de.hterhors.obie.core.ontology.ReflectionUtils;
import de.hterhors.obie.core.ontology.annotations.DatatypeProperty;
import de.hterhors.obie.core.ontology.annotations.RelationTypeCollection;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import de.hterhors.obie.ml.corpus.BigramCorpusProvider;
import de.hterhors.obie.ml.ner.NERLClassAnnotation;
import de.hterhors.obie.ml.ner.NERLIndividualAnnotation;
import de.hterhors.obie.ml.ner.NamedEntityLinkingAnnotations;
import de.hterhors.obie.ml.run.param.RunParameter;
import de.hterhors.obie.ml.run.param.RunParameter.Builder;
import de.hterhors.obie.ml.variables.IETmplateAnnotation;
import de.hterhors.obie.ml.variables.OBIEInstance;
import de.hterhors.obie.ml.variables.OBIEInstance.EInstanceType;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerProjectEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.parameter.SoccerPlayerParameterQuickAccess;
import de.hterhors.obie.projects.soccerplayer.ie.templates.BirthDeathYearTemplate;
import de.hterhors.obie.projects.soccerplayer.rawcorpus.SoccerPlayerSpecs;
import de.hterhors.semanticmr.corpus.EInstanceContext;
import de.hterhors.semanticmr.crf.helper.DefaultDocumentTokenizer;
import de.hterhors.semanticmr.crf.structure.annotations.AbstractAnnotation;
import de.hterhors.semanticmr.crf.structure.annotations.AnnotationBuilder;
import de.hterhors.semanticmr.crf.structure.annotations.EntityTemplate;
import de.hterhors.semanticmr.crf.structure.annotations.EntityTypeAnnotation;
import de.hterhors.semanticmr.crf.structure.annotations.SlotType;
import de.hterhors.semanticmr.crf.variables.Annotations;
import de.hterhors.semanticmr.crf.variables.Document;
import de.hterhors.semanticmr.crf.variables.DocumentToken;
import de.hterhors.semanticmr.crf.variables.Instance;
import de.hterhors.semanticmr.exce.DocumentLinkedAnnotationMismatchException;
import de.hterhors.semanticmr.exce.IllegalSlotFillerException;
import de.hterhors.semanticmr.init.specifications.SystemScope;
import de.hterhors.semanticmr.json.JsonInstanceIO;
import de.hterhors.semanticmr.json.converter.InstancesToJsonInstanceWrapper;
import de.hterhors.semanticmr.json.nerla.JsonNerlaIO;
import de.hterhors.semanticmr.json.nerla.wrapper.JsonEntityAnnotationWrapper;

public class ConvertBigramCorpus2Json {

	public static void main(String[] args) throws IOException {
//
//		OWLReader r = new OWLReader(SoccerPlayerOntologyEnvironment.getInstance());
//
//		r.classes.stream().filter(c -> !c.superclasses.isEmpty()).map(c -> {
//
//			StringBuffer bf = new StringBuffer();
//			for (Iterator<OntologyClass> iterator = c.superclasses.iterator(); iterator.hasNext();) {
//				OntologyClass type = iterator.next();
//				bf.append(type.ontologyClassName + "\t" + c.ontologyClassName);
//				bf.append("\n");
//			}
//			return bf.toString().trim();
//
//		}).forEach(System.out::println);
//
//		System.exit(1);

		{
			OntologyInitializer.initializeOntology(SoccerPlayerOntologyEnvironment.getInstance());
		}

		SystemScope.Builder.getScopeHandler()
				/**
				 * We add a scope reader that reads and interprets the 4 specification files.
				 */
				.addScopeSpecification(SoccerPlayerSpecs.systemsScopeReader)
				/**
				 * We apply the scope, so that we can add normalization functions for various
				 * literal entity types, if necessary.
				 */
				.apply()
				/**
				 * Finally, we build the systems scope.
				 */
				.build();

		/*
		 * Get some standard parameter. These are not important for this example. the
		 * more important parameter follow below:
		 */
		final Builder paramBuilder = getStandardParameter();

		/**
		 * The distribution of the documents in the corpus. Origin takes training ,
		 * development and test data as they are. Use shufflDist() for shuffle the
		 * documents before and redistribute to train (80%), dev(0%) and test(20%). (You
		 * may change that distribution by building your own distributor...
		 */
		paramBuilder.setCorpusDistributor(SoccerPlayerParameterQuickAccess.predefinedDistributor.originDist(1F));

		/*
		 * Build parameter.
		 */
		RunParameter parameter = paramBuilder.build();

		/**
		 * Load the raw corpus from file system. This corpus contains only the document
		 * content and annotations of the template but NO named entity recognition and
		 * linking annotations!
		 */

		BigramCorpusProvider corpusProvider = BigramCorpusProvider.loadCorpusFromFile(parameter);

		JsonNerlaIO io = new JsonNerlaIO(true);
		EInstanceContext context = null;
		for (OBIEInstance instance : corpusProvider.getFullCorpus().getInternalInstances()) {
			List<JsonEntityAnnotationWrapper> nerlas = new ArrayList<>();

			if (instance.getInstanceType() == EInstanceType.TRAIN) {
				context = EInstanceContext.TRAIN;
			}
			if (instance.getInstanceType() == EInstanceType.DEV) {

				context = EInstanceContext.DEVELOPMENT;
			}
			if (instance.getInstanceType() == EInstanceType.TEST) {

				context = EInstanceContext.TEST;
			}
			if (instance.getInstanceType() == EInstanceType.UNSET) {

				context = EInstanceContext.UNSPECIFIED;
			}

			Instance i = toJsonInstance(context, instance, instance.getEntityAnnotations());

			if (i == null)
				continue;

			List<Instance> instances = new ArrayList<>();

			instances.add(i);

			InstancesToJsonInstanceWrapper conv = new InstancesToJsonInstanceWrapper(instances);

			JsonInstanceIO writer = new JsonInstanceIO(true);
			writer.writeInstances(new File("json/instances/" + instance.getName() + ".json"),
					conv.convertToWrapperInstances());

			for (Class<? extends IOBIEThing> annotatedClass : instance.getEntityAnnotations()
					.getAvailableClassTypes()) {
				for (NERLClassAnnotation classNERLAnnotation : instance.getEntityAnnotations()
						.getClassAnnotations(annotatedClass)) {
					String documentID = instance.getName();
					String entityType = classNERLAnnotation.classType.getSimpleName();
					int offset = classNERLAnnotation.getOnset();
					String surfaceForm = classNERLAnnotation.getText();
					JsonEntityAnnotationWrapper w = new JsonEntityAnnotationWrapper(documentID, entityType, offset,
							surfaceForm);
					nerlas.add(w);
				}
			}

			for (AbstractIndividual annotatedIndividual : instance.getEntityAnnotations()
					.getAvailableIndividualTypes()) {
				for (NERLIndividualAnnotation individualNERLAnnotation : instance.getEntityAnnotations()
						.getIndividualAnnotations(annotatedIndividual)) {
					String documentID = instance.getName();
					String entityType = individualNERLAnnotation.relatedIndividual.name;
					int offset = individualNERLAnnotation.getOnset();
					String surfaceForm = individualNERLAnnotation.getText();
					JsonEntityAnnotationWrapper w = new JsonEntityAnnotationWrapper(documentID, entityType, offset,
							surfaceForm);
					nerlas.add(w);
				}
			}

			File writeToFile_nerla = new File("json/nerla/" + instance.getName() + "_nerla.json");
			io.writeNerlas(writeToFile_nerla, nerlas);

		}

	}

	private static Instance toJsonInstance(EInstanceContext context, OBIEInstance i,
			NamedEntityLinkingAnnotations namedEntityLinkingAnnotations) {

		List<AbstractAnnotation> annotations = new ArrayList<>();

		Document d = getDocument(i.getName(), i.getContent());

		for (IETmplateAnnotation a : i.getGoldAnnotation().getAnnotations()) {

			try {
				annotations.add(toAnnotation(d, a.getThing(), namedEntityLinkingAnnotations));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		if (annotations.isEmpty())
			return null;

		Annotations goldAnnotations = new Annotations(annotations);

		Instance inst = new Instance(context, d, goldAnnotations);

		return inst;
	}

	private static AbstractAnnotation toAnnotation(Document d, IOBIEThing obieAnnotation,
			NamedEntityLinkingAnnotations namedEntityLinkingAnnotations) throws Exception {

		EntityTypeAnnotation eta = toFiller(d, obieAnnotation);

		EntityTemplate ann = new EntityTemplate(eta);

		for (Field f : ReflectionUtils.getSlots(obieAnnotation.getClass())) {

			if (ReflectionUtils.isAnnotationPresent(f, RelationTypeCollection.class)) {

				List<IOBIEThing> fillers = (List<IOBIEThing>) f.get(obieAnnotation);

				for (IOBIEThing iobieThing : fillers) {
					if (iobieThing == null)
						continue;
					AbstractAnnotation fil = null;
					try {
						fil = toFiller(d, iobieThing);
					} catch (DocumentLinkedAnnotationMismatchException e) {

						Set<NERLIndividualAnnotation> an = namedEntityLinkingAnnotations
								.getIndividualAnnotations(iobieThing.getIndividual());

						if (an == null || an.isEmpty()) {
							System.out.println(iobieThing);
							e.printStackTrace();
						} else {
							NERLIndividualAnnotation a = an.iterator().next();
							fil = AnnotationBuilder.toAnnotation(d, a.relatedIndividual.name, a.text, a.onset);
						}
					}

					if (fil != null) {
						try {
							ann.addMultiSlotFiller(SlotType.get(getName(f.getName())), fil);
						} catch (IllegalSlotFillerException e) {
							// TODO: handle exception
						}
					}
				}
			} else {

				IOBIEThing filler = (IOBIEThing) f.get(obieAnnotation);
				if (filler == null)
					continue;

				AbstractAnnotation fil = null;
				try {
					fil = toFiller(d, filler);
				} catch (DocumentLinkedAnnotationMismatchException e) {

					Set<NERLIndividualAnnotation> an = namedEntityLinkingAnnotations
							.getIndividualAnnotations(filler.getIndividual());

					if (an == null || an.isEmpty()) {
						System.out.println(filler);
						e.printStackTrace();
					} else {
						NERLIndividualAnnotation a = an.iterator().next();
						fil = AnnotationBuilder.toAnnotation(d, a.relatedIndividual.name, a.text, a.onset);
					}
				}

				if (fil != null)
					ann.setSingleSlotFiller(SlotType.get(f.getName()), fil);
			}

		}

		return ann;
	}

	private static String getName(String name) {
		if (name.equals("teamSoccerClubs"))
			return "team";
//
		if (name.equals("positionAmerican_football_positions"))
			return "position";
		if (name.equals("birthPlaces"))
			return "birthPlace";

		return name;
	}

	private static EntityTypeAnnotation toFiller(Document d, IOBIEThing iobieThing)
			throws DocumentLinkedAnnotationMismatchException {

		if (iobieThing.getIndividual() == null) {

			if (ReflectionUtils.isAnnotationPresent(iobieThing.getClass(), DatatypeProperty.class)) {

				return AnnotationBuilder.toAnnotation(d, getClassName(iobieThing.getClass().getSimpleName()),
						iobieThing.getTextMention(), iobieThing.getCharacterOnset());
			} else {

				return AnnotationBuilder.toAnnotation(getClassName(iobieThing.getClass().getSimpleName()));
			}

		}
		if (ReflectionUtils.isAnnotationPresent(iobieThing.getClass(), DatatypeProperty.class)) {
			return AnnotationBuilder.toAnnotation(d, iobieThing.getIndividual().name, iobieThing.getTextMention(),
					iobieThing.getCharacterOnset());
		} else {
			return AnnotationBuilder.toAnnotation(toIndName(iobieThing.getIndividual().name));
		}

	}

	private static String toIndName(String replaceAll) {
//
//		if (replaceAll.equals("Goalkeeper(associationfootball)"))
//			return "GoalkeeperAssociationFootball";
//		if (replaceAll.equals("Forward(associationfootball)"))
//			return "ForwardAssociationFootball";
//		if (replaceAll.equals("Defender(associationfootball)"))
//			return "DefenderAssociationFootball";

		return replaceAll;
	}

	private static String getClassName(String simpleName) {

//		if (simpleName.equals("American_football_positions"))
//			return "Position";
//
//		if (simpleName.equals("Team"))
//			return "SoccerClub";
//
//		if (simpleName.equals("BirthPlace"))
//			return "Place";

		return simpleName;
	}

	private static Document getDocument(String name, String content) {
		List<DocumentToken> tokenList = DefaultDocumentTokenizer.tokenizeDocumentsContent(content);
		Document d = new Document(name, tokenList);
		return d;
	}

	/**
	 * We need to set some standard parameter.
	 * 
	 * @return
	 */
	private static Builder getStandardParameter() {
		Builder paramBuilder = SoccerPlayerParameterQuickAccess.getBaseParameter();

		paramBuilder.setProjectEnvironment(SoccerPlayerProjectEnvironment.getInstance());
		paramBuilder.setOntologyEnvironment(SoccerPlayerOntologyEnvironment.getInstance());
		paramBuilder.addTemplate(BirthDeathYearTemplate.class);

		return paramBuilder;
	}
}
