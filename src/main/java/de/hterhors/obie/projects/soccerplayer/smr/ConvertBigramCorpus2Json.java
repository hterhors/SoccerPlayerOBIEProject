package de.hterhors.obie.projects.soccerplayer.smr;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import de.hterhors.obie.core.ontology.AbstractIndividual;
import de.hterhors.obie.core.ontology.OntologyInitializer;
import de.hterhors.obie.core.ontology.ReflectionUtils;
import de.hterhors.obie.core.ontology.annotations.RelationTypeCollection;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;
import de.hterhors.obie.ml.corpus.BigramCorpusProvider;
import de.hterhors.obie.ml.ner.NERLClassAnnotation;
import de.hterhors.obie.ml.ner.NERLIndividualAnnotation;
import de.hterhors.obie.ml.run.param.RunParameter;
import de.hterhors.obie.ml.run.param.RunParameter.Builder;
import de.hterhors.obie.ml.variables.IETmplateAnnotation;
import de.hterhors.obie.ml.variables.OBIEInstance;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerOntologyEnvironment;
import de.hterhors.obie.projects.soccerplayer.environments.SoccerPlayerProjectEnvironment;
import de.hterhors.obie.projects.soccerplayer.ie.parameter.SoccerPlayerParameterQuickAccess;
import de.hterhors.obie.projects.soccerplayer.ie.templates.BirthDeathYearTemplate;
import de.hterhors.obie.projects.soccerplayer.rawcorpus.SoccerPlayerSpecs;
import de.hterhors.semanticmr.corpus.EInstanceContext;
import de.hterhors.semanticmr.crf.structure.annotations.AbstractAnnotation;
import de.hterhors.semanticmr.crf.structure.annotations.AnnotationBuilder;
import de.hterhors.semanticmr.crf.structure.annotations.EntityTemplate;
import de.hterhors.semanticmr.crf.structure.slots.SlotType;
import de.hterhors.semanticmr.crf.variables.Annotations;
import de.hterhors.semanticmr.crf.variables.Document;
import de.hterhors.semanticmr.crf.variables.DocumentToken;
import de.hterhors.semanticmr.crf.variables.Instance;
import de.hterhors.semanticmr.exce.DocumentLinkedAnnotationMismatchException;
import de.hterhors.semanticmr.init.specifications.SystemScope;
import de.hterhors.semanticmr.json.JsonInstanceIO;
import de.hterhors.semanticmr.json.converter.InstancesToJsonInstanceWrapper;
import de.hterhors.semanticmr.json.nerla.JsonNerlaIO;
import de.hterhors.semanticmr.json.nerla.wrapper.JsonEntityAnnotationWrapper;
import de.hterhors.semanticmr.tokenizer.DefaultDocumentTokenizer;

public class ConvertBigramCorpus2Json {

	public static void main(String[] args) throws IOException {
		{
			OntologyInitializer.initializeOntology(SoccerPlayerOntologyEnvironment.getInstance());
		}

		SystemScope.Builder.getSpecsHandler()
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

		List<JsonEntityAnnotationWrapper> nerlas = new ArrayList<>();
		JsonNerlaIO io = new JsonNerlaIO(true);
		for (OBIEInstance instance : corpusProvider.getTrainingCorpus().getInternalInstances()) {

			Instance i = toJsonInstance(EInstanceContext.TRAIN, instance);

			List<Instance> instances = new ArrayList<>();

			instances.add(i);

			InstancesToJsonInstanceWrapper conv = new InstancesToJsonInstanceWrapper(instances);

			JsonInstanceIO writer = new JsonInstanceIO(true);
			String json = writer.writeInstances(conv.convertToWrapperInstances());

			File writeToFile = new File("json/" + instance.getName() + ".json");

			final PrintStream ps = new PrintStream(writeToFile);
			ps.println(json);
			ps.close();

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


		}
		
		String json_nerla = io.toJsonString(nerlas);
		
		File writeToFile_nerla = new File("json/nerla.json");

		final PrintStream ps_nerla = new PrintStream(writeToFile_nerla);
		ps_nerla.println(json_nerla);
		ps_nerla.close();
	}

	private static Instance toJsonInstance(EInstanceContext context, OBIEInstance i) {

		List<AbstractAnnotation> annotations = new ArrayList<>();

		Document d = getDocument(i.getName(), i.getContent());

		for (IETmplateAnnotation a : i.getGoldAnnotation().getAnnotations()) {

			try {
				annotations.add(toAnnotation(d, a.getThing()));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		Annotations goldAnnotations = new Annotations(annotations);

		Instance inst = new Instance(context, d, goldAnnotations);

		System.out.println(inst);
		return inst;
	}

	private static AbstractAnnotation toAnnotation(Document d, IOBIEThing obieAnnotation) throws Exception {
		EntityTemplate ann = new EntityTemplate(AnnotationBuilder.toAnnotation("SoccerPlayer"));

		for (Field f : ReflectionUtils.getSlots(obieAnnotation.getClass())) {

			if (ReflectionUtils.isAnnotationPresent(f, RelationTypeCollection.class)) {

				List<IOBIEThing> fillers = (List<IOBIEThing>) f.get(obieAnnotation);

				for (IOBIEThing iobieThing : fillers) {
					try {
						ann.addMultiSlotFiller(SlotType.get(f.getName()), toFiller(d, iobieThing));
					} catch (DocumentLinkedAnnotationMismatchException e) {
						e.printStackTrace();
					}
				}
			} else {

				IOBIEThing filler = (IOBIEThing) f.get(obieAnnotation);
				try {
					ann.setSingleSlotFiller(SlotType.get(f.getName()), toFiller(d, filler));
				} catch (DocumentLinkedAnnotationMismatchException e) {
					e.printStackTrace();
				}
			}

		}

		return ann;
	}

	private static AbstractAnnotation toFiller(Document d, IOBIEThing iobieThing)
			throws DocumentLinkedAnnotationMismatchException {
		return AnnotationBuilder.toAnnotation(d, iobieThing.getClass().getSimpleName(), iobieThing.getTextMention(),
				iobieThing.getCharacterOnset());

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
