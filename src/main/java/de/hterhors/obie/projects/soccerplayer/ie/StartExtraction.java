package de.hterhors.obie.projects.soccerplayer.ie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import corpus.SampledInstance;
import de.hterhors.obie.core.evaluation.PRF1Container;
import de.hterhors.obie.core.ontology.AbstractOntologyEnvironment;
import de.hterhors.obie.core.ontology.OntologyInitializer;
import de.hterhors.obie.core.projects.AbstractProjectEnvironment;
import de.hterhors.obie.ml.activelearning.FullDocumentEntropyRanker;
import de.hterhors.obie.ml.activelearning.FullDocumentRandomRanker;
import de.hterhors.obie.ml.activelearning.IActiveLearningDocumentRanker;
import de.hterhors.obie.ml.corpus.distributor.AbstractCorpusDistributor;
import de.hterhors.obie.ml.corpus.distributor.ActiveLearningDistributor;
import de.hterhors.obie.ml.corpus.distributor.ByInstanceNameDistributor;
import de.hterhors.obie.ml.run.AbstractOBIERunner;
import de.hterhors.obie.ml.run.StandardRERunner;
import de.hterhors.obie.ml.run.eval.EvaluatePrediction;
import de.hterhors.obie.ml.run.param.OBIERunParameter;
import de.hterhors.obie.ml.run.param.OBIERunParameter.Builder;
import de.hterhors.obie.ml.templates.AbstractOBIETemplate;
import de.hterhors.obie.ml.templates.FrequencyTemplate;
import de.hterhors.obie.ml.templates.InBetweenContextTemplate;
import de.hterhors.obie.ml.templates.InterTokenTemplate;
import de.hterhors.obie.ml.templates.LocalTemplate;
import de.hterhors.obie.ml.templates.SlotIsFilledTemplate;
import de.hterhors.obie.ml.templates.TokenContextTemplate;
import de.hterhors.obie.ml.variables.InstanceTemplateAnnotations;
import de.hterhors.obie.ml.variables.OBIEInstance;
import de.hterhors.obie.ml.variables.OBIEState;
import de.hterhors.obie.projects.soccerplayer.ie.ner.regex.SoccerPlayerRegExNEL;
import de.hterhors.obie.projects.soccerplayer.ie.templates.BirthYearTemplate;
import de.hterhors.obie.projects.soccerplayer.ie.templates.PriorTemplate;
import de.hterhors.obie.projects.soccerplayer.ontology.interfaces.ISoccerPlayer;

/**
 * 
 * Prepare for active learning
 * 
 * Read README.md for more and detailed information.
 * 
 *
 * Preferred VM run-parameter: -Xmx12g -XX:+UseG1GC -XX:+UseStringDeduplication
 *
 * 
 * Main starting class for the information extraction task using the
 * SoccerPlayerOntology and SoccerPlayer-Wikipedia data set.
 * 
 * -XX:+PrintStringDeduplicationStatistics
 * 
 * @author hterhors
 *
 */
public class StartExtraction {

	private static final String DEFAULT_ACTIVE_LEARNING_STRATEGY = "random";
	private static final String DEFAULT_RESULT_FILE_NAME = "tmpResultFile";

	protected static Logger log = LogManager.getRootLogger();

	public static void main(String[] args) throws Exception {

		args = new String[] { "randomResults", "random" };

		log.info("1) argument: file to store results");
		log.info("2) argument: mode of active learning, \"random\"(default) or \"entropy\"");

		final File printResults = new File(args.length == 0 ? DEFAULT_RESULT_FILE_NAME : args[0]);
		final String acMode = args.length < 2 ? DEFAULT_ACTIVE_LEARNING_STRATEGY : args[1];

		if (!(acMode.equals("random") || acMode.equals("entropy"))) {
			log.error("Unkown active learning mode: " + acMode);
			System.exit(1);
		}

		if (printResults.getParentFile() != null && !printResults.getParentFile().exists()) {
			log.error("Parent dir does not exist: " + printResults.getParentFile().getCanonicalPath());
			System.exit(1);
		}

		new StartExtraction(acMode, printResults);

	}

	/**
	 * The runID. This serves as an identifier for locating and saving the model. If
	 * anything was changed during the development the runID should be reset.
	 */
	private final static String runID = "random" + new Random().nextInt();

	/**
	 * The project environment.
	 */
	private final AbstractProjectEnvironment projectEnvironment = SoccerPlayerProjectEnvironment.getInstance();

	/**
	 * The ontology environment.
	 */
	private final AbstractOntologyEnvironment ontologyEnvironment = SoccerPlayerOntologyEnvironment.getInstance();

	public StartExtraction(String acModus, File printResults) throws Exception {
		{
			OntologyInitializer.initializeOntology(ontologyEnvironment);
		}

		log.info("Current run id = " + runID);

		/**
		 * This parameterBuilder contains standard configurations of the system that are
		 * used for Relation Extraction tasks. You can but may not change the parameter
		 * predefined in here unless you know what you are doing!
		 */
		final Builder paramBuilder = SoccerPlayerParameterQuickAccess.getREParameter();

//		InvestigationRestriction investigationRestriction = new InvestigationRestriction(ISoccerPlayer.class, false);
//
//		paramBuilder.setInvestigationRestriction(investigationRestriction);

		/*
		 * Add parameter...
		 */

		/**
		 * The number of epochs that the system should be trained.
		 */
		final int epochs = 3;

		/**
		 * The distribution of the documents in the corpus. Origin takes training ,
		 * development and test data as they are. Use shufflDist() for shuffle the
		 * documents before and redistribute to train (80%), dev(0%) and test(20%). (You
		 * may change that distribution by building your own distributor...
		 */
//		final AbstractCorpusDistributor corpusDistributor = SoccerPlayerParameterQuickAccess.predefinedDistributor
//				.activeLearningDist(1F);

		final AbstractCorpusDistributor corpusDistributor = new ByInstanceNameDistributor.Builder()
				.setCorpusSizeFraction(1F)
				.setNamesOfTrainingInstances(new HashSet<String>(Arrays.asList("Abdelfettah_Rhiati", "Abdelhamid_Sadmi",
						"Abdelmajid_Lamriss", "Abdul-Jabar_Hashim_Hanoon", "Abdullah_Al-Dosari", "Abdullah_Mayouf",
						"Abe_Rosenthal", "Abhijit_Das", "Ablade_Kumah", "Aboubacar_Bangoura_(footballer)",
						"Adam_Bedell", "Adam_Marjan", "Adam_Nichols", "Adedeji_Oshilaja", "Aden_Charmakeh",
						"Aditya_Putra_Dewa", "Adixi_Lenzivio", "Adrian_Bird_(footballer)", "Ahmad_Sabri_Ismail",
						"Akil_Byron", "Alan_Buck", "Alan_Dennis", "Alan_Dunne", "Alari_Lell", "Albert_Evers",
						"Albert_Iremonger", "Albert_Mays_(footballer)", "Albert_Read_(footballer)", "Aldo_Poy",
						"Alec_Denton", "Aleksandr_Olerski", "Alessandro_Bianchi_(footballer_born_1989)", "Alex_Freitas",
						"Alex_Gardner_(footballer)", "Alex_McLintock", "Alf_Carroll", "Alf_Haynes", "Alfred_Dobson",
						"Alfred_Downey", "Alfredo_Brown", "Alfred_Sabin", "Allal_Ben_Kassou", "Allan_Devanney",
						"Altay_Kahraman", "Ambrose_Harris", "Andre_Anis", "Andreas_Kittos",
						"Andrew_Chalmers_(footballer)", "Andrew_Dawber", "Andy_Collett", "Andy_Couzens",
						"Andy_Davis_(British_Virgin_Islands_footballer)", "Aniston_Fernandes", "Anthony_Wordsworth",
						"Antoine_Gounet", "Antonio_Greco", "Antony_Lecointe", "Arthur_Bancroft", "Arthur_Bellamy",
						"Arthur_Cowell", "Arthur_Gardner_(footballer)", "Arthur_Hallworth",
						"Arthur_Morris_(footballer)", "Arthur_Nineham", "Arthur_Stanton", "Arthur_Topham",
						"Arthur_Wigglesworth", "Artur_Dyson", "Ashish_Chettri", "Ashley_Fernandes",
						"Augusty_Bartillard", "Ayanda_Gcaba", "Ayron_Verkindere", "Barry_Dominey", "Barry_Mealand",
						"Ben_Herd", "Benjamin_Huggel", "Ben_Olney", "Ben_Yagan", "Bernard_Hall_(footballer)",
						"Bernard_Middleditch", "Bernard_Smith_(footballer)", "Bernhard_Wessel", "Berry_Brown",
						"Bert_Fenwick", "Bertie_Bowler", "Bilel_Ifa", "Bill_Bainbridge", "Bill_Hart_(footballer)",
						"Bill_Jaques", "Bill_Robertson_(English_footballer)", "Bill_Ruffell", "Bill_Whare",
						"Bill_Yates_(footballer)", "Billy_Cooper_(footballer)", "Billy_Furness", "Bimal_Magar",
						"Biswajit_Biswas", "Blagoy_Makendzhiev", "Bobby_Hassell", "Bobby_Hill_(footballer)",
						"Bobby_Howlett", "Bobby_Kerr_(footballer)", "Bobby_Mills_(footballer)", "Bobby_Park",
						"Bobby_Tynan", "Bob_Connor", "Boboi_Singh", "Bozhidar_Mitrev", "Bradley_Beattie",
						"Bradley_Garmston", "Brian_Honeywood", "Brima_Bangura", "Brima_Sesay",
						"Bruce_Crawford_(footballer)", "Bruno_Appels", "Byron_Lawrence", "Cameron_Lancaster",
						"Carlos_Contreras_(footballer)", "Carlos_Mateus_Ximenes", "Carlos_Nvomo", "Cedric_Badjeck",
						"Chadi_Hammami", "Charles_Geerts_(footballer)", "Charles_Vanden_Wouwer", "Charlie_Thomson",
						"Choi_Yung-keun", "Chris_Morrow", "Christian_Dean", "Cindy_Daws", "C_Lallawmzuala",
						"Claude_Papi", "Clint_Boulton", "Conny_Johansson", "Conor_Hubble", "Constant_Huysmans",
						"Cornel_Chin-Sue", "Craig_Bingham", "Curtis_Main", "Cyril_Hennion", "Dallas_Jaye",
						"Damian_Keeley", "Daniel_Bentley", "Daniel_Mudau", "Daniel_Oliveira_Costa", "Daniel_Pappoe",
						"Dany_Maury", "Darren_Bastow", "Darren_Holden_(footballer)", "Dave_Bus",
						"Dave_Lamont_(footballer)", "David_Buck_(footballer)", "David_Bueso", "David_Letham",
						"David_MacGregor", "David_McGurk", "David_Soames", "David_Svensson", "Davie_Cameron",
						"Dele_Alli", "Delroy_Scott", "Demetrio_Neyra", "Denis_Lindsay_(footballer)", "Dimitar_Kostov",
						"Dimitrios_Manos", "Dimitris_Machairas", "Dmytro_Zaderetskyi", "Donervon_Daniels",
						"Dowlyn_Daly", "Driss_Trichard", "Duggie_Brown_(footballer)", "Dumitru_Stajila", "Dylan_Mares",
						"Dzintar_Klavan", "Eladio_Reyes", "Eliseo_Brown", "Emmanuel_Ogoli", "Enis_Maljici",
						"Enzo_Crivelli", "Erich_Hasenkopf", "Ernesto_Belis", "Ernesto_Brown", "Ernie_Steventon",
						"Evans_Kangwa", "Everton_Sena", "Fabien_Cool", "Fadel_Jilal", "Fanai_Lalrempuia", "Fikri_Elma",
						"Francisco_Alcaraz_(footballer)", "Frank_Adams_(footballer)", "Fred_Barron_(footballer)",
						"Fred_Beardsley", "Fred_Ewer", "Fred_Pelly", "Fred_Woodward", "Gael_Margulies",
						"Gary_Harvey_(footballer)", "Gary_Riddell", "Gary_Robson_(footballer)", "Gavin_McGowan",
						"Geoff_Coffin", "Geoff_Dyson", "Geoffrey_Kizito", "Geoff_Smith_(footballer)", "George_Bray",
						"George_Howe_(footballer)", "Gerardo_Romero", "Ger_Lagendijk", "Gideon_V._Way",
						"Gilles_Dewaele", "Giuseppe_Koschier", "Graham_Whittle", "Greg_Tempest", "Greig_Young",
						"Guillaume_Ducatel", "Gyula_Prassler", "Hadi_Tavoosi", "Ha_Jung-won", "Hamish_McNeill",
						"Hamoud_Al-Shemmari", "Hamyar_Nasser_Al-Ismaili", "Han_Chang-wha", "Hany_Mukhtar",
						"Harm_Zeinstra", "Harold_Keenan", "Harry_Gilberg", "Harry_Gooney", "Harry_Goslin",
						"Harry_Haddon", "Harry_McMenemy", "Harry_Moore_(footballer)", "Hassan_Daher", "Hassan_Mattar",
						"Heinz_Versteeg", "Henry_Healless", "Herbie_Williams", "Hocine_Benmiloudi", "Horacio_Sequeira",
						"Horst_Nemec", "Hugo_Lepe", "Hugo_Seixas", "Hugo_Villanueva", "Hussain_Al-Romaihi",
						"Hymie_Kloner", "Ian_Atkinson", "Ian_Johnstone", "Ibra_Agbo", "Iliya_Dimitrov",
						"Imre_Kiss_(footballer_born_1957)", "Ioannis_Stefas", "Isaac_Shai", "Ivan_Deyanov",
						"Ivan_Dimitrov_(footballer)", "Ivan_Ivanov_(footballer_born_1942)", "Ivan_O'Konnel-Bronin",
						"Ivan_Toney", "Ivar_Eriksson", "Ivaylo_Zafirov", "Jaanus_Veensalu", "Jack_Bertolini",
						"Jack_Callender", "Jack_Cropley", "Jack_Deakin", "Jack_Kelsey", "Jaime_Huerta", "Jaime_Isuardi",
						"Jake_Cuenca", "Jakob_Haugaard", "Jamal_Al-Qabendi", "James_Lamont_(footballer)",
						"James_Vance_(footballer)", "James_Vincent_(footballer)", "Jamie_Jones_(footballer)",
						"Jamie_Richards_(footballer)", "Jan_Mulder_(footballer)", "Jasem_Bahman",
						"Jason_Davis_(footballer)", "Jayanta_Paul", "Jean_Van_Steen", "Jeffrey_Gouweleeuw",
						"Jennifer_Molina", "Jeong_Gi-dong", "Jim_Gallacher", "Jimmy_Page_(footballer)",
						"Jimmy_Prescott", "Jo_Backaert", "Joe_Ashley", "Joe_Bryan", "Joe_Lodge", "Joe_Peacock",
						"Joe_Reader", "Johan_Eklund", "Johann_Windisch", "Johan_Svantesson", "John_Baines_(footballer)",
						"John_Brown_(footballer,_born_March_1940)", "John_Cox_(footballer)", "John_Graham_(forward)",
						"Johnson_Akuchie", "John_Souttar", "Jonathan_Miles_(footballer)", "Jon_McLaughlin_(footballer)",
						"Jordens_Peters", "Josef_Majer", "Joseph_Clemente", "Joseph_Gryzik", "Joseph_Kamga",
						"Josh_Brizell", "Josh_Rowbotham", "Josh_Scowen", "Juan_Alfonso_Valle", "Julian_Aguirre_Agudelo",
						"Julius_Ubido", "Jurgen_Wevers", "Justin_Booty", "Justin_Chavez", "Justin_Manao", "Karim_Yoda",
						"Karl-Erik_Grahn", "Karl_Nickerl", "Katie_Larkin", "Keith_Griffiths_(footballer)",
						"Keith_Lasley", "Kennedy_Mudenda", "Kenny_Banks", "Kenny_Davenport", "Ke_Seung-woon",
						"Kevin_Dickenson", "Kevin_Walker_(Scottish_footballer)", "Kevin_Welsh_(footballer)",
						"Khairan_Eroza_Razali", "Kim_Bong-hwan", "Kim_Jong-min_(footballer)", "Klaus_Decker",
						"Knut_Hansson", "Kurt_Elshot", "Kurt_Svensson", "Kyle_Benedictus", "Lachie_Thomson",
						"Lahcen_Ouadani", "Lalrinzuala_Khiangte", "Larbi_El_Hadi", "Lee_Jong-ho", "Lee_Margerison",
						"Lee_Sang-yi", "Lennard_Remy", "Lenny_Pereira", "Len_Oliver_(footballer)", "Len_Stansbridge",
						"Leonardo_Ferrel", "Leonne_Stentler", "Les_Dicker", "Leslie_Lea", "Lester_Langlais",
						"Lewis_Dunk", "Lewis_Milne", "Luc_Van_Hoyweghen", "Ludivine_Diguelman", "Luis_Congo",
						"Luis_Cruz_(footballer)", "Luis_Miguel_Valle", "Luis_Reyna", "Macauley_Bonne", "Mahboub_Juma'a",
						"Mahmood_Abdulla", "Malcolm_Currie", "Malcolm_Devitt", "Malcolm_Newlands", "Manny_Smith",
						"Manuel_D'Souza", "Marcel_Tisserand", "Mario_Medina", "Marius_Zarn",
						"Mark_Fletcher_(footballer)", "Mark_Gall", "Mark_Radford_(footballer)", "Mark_Sherrod",
						"Mark_Torrance", "Martin_van_Leeuwen", "Masoud_Zeraei", "Matthew_Coad",
						"Matthew_Cooper_(footballer)", "Mattia_Dal_Bello", "Maurice_Norman", "Maurice_Webster",
						"Michael_Galea", "Michael_Timisela", "Michalis_Karas", "Michele_Lo_Russo", "Miguel_Davis",
						"Miguel_Mba", "Miguel_Segura", "Mikael_Mandron", "Milen_Ivanov", "Milko_Gaydarski",
						"Mirko_Bigazzi", "Mitch_Harding", "Mobin_Rai", "Modesto_Denis", "Mohamad_Atwi",
						"Mohamed_Kaci-Said", "Mohamed_Oulhaj", "Mohammed_Karam", "Momar_Bangoura", "Moses_Oloya",
						"Mouaouia_Meklouche", "Moussa_Doumbia", "Muayad_Al-Haddad", "Mursyid_Effendi",
						"Nassir_Al-Ghanim", "Nat_Walton", "Nawab_Zeeshan", "Ned_Bromley",
						"Nick_Kuipers_(footballer_born_1992)", "Nicolas_Bovi", "Nie_Tao", "Nikola_Parshanov",
						"Noel_George", "Noel_Simpson", "Noel_Turner", "Norman_Chalk", "Norman_Heath", "Obi_Onyeike",
						"Ofir_Mizrahi", "Oh_Yoon-kyung", "Oleksandr_Aksyonov", "Oliver_Gill", "Oliver_Gustafsson",
						"Oliver_Whateley", "Osas_Okoro", "Packie_Bonner", "Pape_Latyr_N'Diaye", "Park_Sun-yong",
						"Parwinder_Singh", "Pascal_Di_Tommaso", "Paul_Denny_(footballer)", "Paul_Fleming_(footballer)",
						"Paul_Flowers_(footballer)", "Paul_Mulrooney", "Pelayo_Eribo", "Petar_Velichkov",
						"Peter_Allen_(footballer)", "Peter_Fregene", "Peter_Gideon", "Peter_Hill_(footballer)",
						"Peter_Mooney_(footballer)", "Peter_Roney", "Peter_Sanders_(sportsman)",
						"Phil_Thomas_(footballer)", "Piotr_Robakowski", "Poibang_Pohshna", "Prasenjit_Ghosh",
						"Quiterio_Olmedo", "Raafat_Attia", "Ralph_O'Donnell", "Ray_White_(footballer)", "Razali_Saad",
						"Redouane_Drici", "Reidar_Olsen", "Remko_Pasveer", "Reza_Naalchegar", "Ricardo_Talu",
						"Richard_Lee_(footballer)", "Richard_Wood_(footballer)", "Ridha_El_Louze", "Robbie_Haw",
						"Robbin_Ruiter", "Roberto_Leopardi", "Robert_Van_Kerkhoven", "Robin_Gurung", "Rob_Penders",
						"Rod_Cameron_(footballer)", "Roel_Wiersma", "Roger_Denton", "Roly_Gregoire", "Roman_Nykytyuk",
						"Roman_Zobnin", "Romell_Brathwaite", "Ross_McFarlane_(footballer)", "Rowen_Muscat",
						"Roy_MacLaren_(footballer)", "Rudy_Getzinger", "Rune_Carlsson", "Ryan_Lloyd", "Ryoo_Chang-kil",
						"Saad_Al-Houti", "Sadok_Sassi", "Salahdine_Hmied", "Salvador_Villalba", "Sami_Al-Hashash",
						"Samir_Qotb", "Samir_Said", "Samuel_Day_(sportsman)", "Samuel_Shadap", "Sanjiban_Ghosh",
						"Savaliga_Afu", "Selwyn_Whalley", "Serafim_Neves", "Seton_Airlie", "Shahrizam_Mohamed",
						"Shakeel_Abbasi", "Shaun_Barker", "Shaun_Mawer", "Sheldon_Govier", "Silvio_Appiani",
						"Simon_Burman", "Simone_Gozzi", "Sjors_Verdellen", "Solomon_March", "Sophus_Hansen",
						"Sourav_Chakraborty", "Stan_Cutting", "Stefan_Velichkov", "Steve_Middleton",
						"Steve_Richardson_(footballer)", "Steve_Seargeant", "Steve_Tutill", "Stewart_Bright",
						"Stuart_Kettlewell", "Sudhakaran_Kumar", "Surjay_Pariyar", "Szilvia_Szeitl",
						"Tamanqueiro_(Portuguese_footballer)", "Tarmo_Saks", "Terry_Baker_(footballer)",
						"Thandokuhle_Mkhonza", "Thomas_Danks", "Thomas_Gascoigne_(footballer)", "Tihomir_Naydenov",
						"Tobias_Badila", "Tom_Burrows_(footballer)", "Tommy_Graham_(English_footballer)",
						"Tommy_McCulloch_(goalkeeper)", "Tommy_McQuaid", "Tommy_Morren", "Tommy_Sampy",
						"Tom_Perry_(footballer)", "Tonny_Brochmann", "Tony_Blake", "Tony_Emery", "Tony_Hawksworth",
						"Tony_Leach", "Tony_Wingate", "Tracy_Ducar", "Trevor_Harris_(footballer)", "Tyson_Caiado",
						"Ugo_Okoye", "Urbano_Rivera", "Urmas_Kaljend", "Urmas_Liivamaa", "Vadaine_Oliver",
						"Valeriy_Boychenko", "Vegard_Skjerve", "Veton_Berisha", "Vidin_Apostolov", "Vince_Kenny",
						"Wailadmi_Passah", "Waleed_Al-Jasem", "Wally_Hazelden", "Walter_Balmer_(footballer)",
						"Walter_Buchanan_(footballer)", "Walter_Ponting", "Wardun_Yusof", "Wayne_Quinn", "Wayne_Sobers",
						"Wilfried_Louis", "William_Carr_(footballer)", "William_Carrier", "William_Gyves",
						"William_Horne_(footballer)", "William_Howson_(footballer)", "Willi_Gerdau", "Yang_Qipeng",
						"Yang_Seung-kook", "Yorick_Antheunis", "Yussef_Al-Suwayed", "Zakaria_Abdullai",
						"Zeferino_Martins", "Zhao_Xuebin", "Zhu_Cong_(footballer)")))
				.setNamesOfTestInstances(new HashSet<>(Arrays.asList("Aaron_Jones_(footballer)", "Abdelaziz_Souleimani",
						"Acurcio_Carrelo", "Adama_Sawadogo", "Alex_Gilbey", "Alf_Peachey", "Ali_Crawford",
						"Allassane_Sango", "Allen_Larue", "Amoes", "Andrea_Ferrari_(footballer)",
						"Andrew_Cant_(footballer)", "Andy_Halls", "Andy_Moule", "Archibald_Barton", "Arie_de_Winter",
						"Arne_Linderholm", "Arnie_Sidebottom", "Arthur_Mulford", "Arthur_Samson", "Arvo_Kraam",
						"Augustin_Fernandes", "Azidan_Sarudin", "Ben_Doane", "Benjamin_Kirsten", "Billal_Zouani",
						"Billy_Beaumont", "Billy_Exley", "Blas_Cristaldo", "Bram_van_Polen", "Brian_Abrey",
						"Buenaventura_Ferreira", "Callum_Reilly_(footballer)", "Carlos_Lett", "Charles_Dennington",
						"Charlie_Deacon", "Chris_Philipps", "Cipriano_Santos", "Clovis_Kamdjo", "David_Cliss",
						"Desire_Montgomery_Butler", "Dinesh_Singh_(footballer)", "Dmytro_Yarchuk", "Eddy_Antoine",
						"Eden_Nachmani", "Elie_Ikangu", "Erik_Persson", "Ernest_Milton", "Filipe_de_Souza_Conceicao",
						"Fred_Furniss", "Fred_Speller", "Gianpiero_Combi", "Graeme_McCracken",
						"Gunnar_Olsson_(footballer)", "Gwyn_Hughes_(footballer)", "Howard_Johnson_(footballer)",
						"Ian_Wells", "Jack_Mew", "Janek_Kiisman", "Jared_Sims", "Jim_Blacker",
						"Jim_Duncan_(footballer)", "Jimmy_Cuthbertson", "Jimmy_Greenock", "Jimmy_Thorpe",
						"John_Mansfield_(footballer)", "Jonathan_Cubero", "Jorrit_Kunst", "Josh_Fuller",
						"Julio_Maceiras", "July_Mahlangu", "Keith_Harvey", "Lee_Chang-myung", "Len_Garwood",
						"Leonardo_Pais", "Leonel_Parris", "Leon_Tol", "Liam_Gray", "Liao_Bochao", "Lino_Nessi",
						"Lucas_Dawson", "Lucas_Michel_Mendes", "Luke_Hyam", "Marcelino_Vargas", "Marcelle_Bruce",
						"Maria_Mitkou", "Mark_Lamont", "Martin_Lepa", "Mathieu_Deplagne", "Matt_Sparrow",
						"Michael_Maidens", "Miguel_Van_Damme", "Mitchell_Joseph", "Mitch_Stockentree",
						"Mohamed_Ben_Mouza", "Mohd_Shahrazen_Said", "Munir_El_Haddadi", "Nikhil_Kadam", "Obed_Owusu",
						"Ole_Christoffer_Heieren_Hansen", "Patrick_Cassidy_(footballer)", "Patrick_Modeste",
						"Paul_Gladon", "Paul_Hilton_(footballer)", "Paul_Shardlow", "Peter_Kunter", "Phil_Barlow",
						"Phil_Griggs", "Plamen_Krachunov", "Raimo_de_Vries", "Rain_Vessenberg", "Ray_Pennick",
						"Renan_dos_Santos", "Richard_Williams_(footballer)", "Roar_Strand",
						"Roberto_Sosa_(Uruguayan_footballer)", "Roddie_MacKenzie", "Ron_Thompson_(footballer)",
						"Rosen_Vankov", "Rudolf_Pichler", "Russell_Green", "Sam_Currie", "Sam_Hignett",
						"Samuel_Cheetham_(footballer)", "Sandy_Mutch", "Sjaak_Troost", "Sonny_Stevens", "Stan_Newsham",
						"Sung_Nak-woon", "Taariq_Fielies", "Teddy_Brayshaw", "Tim_Hofstede", "Tom_Allan_(footballer)",
						"Tom_Brooks_(footballer)", "Tom_Eastman", "Tommy_Black_(footballer,_born_1908)",
						"Tommy_Hoyland", "Tommy_Spurr", "Tom_Wade", "Tony_Miller_(footballer)", "Vahid_Heydarieh",
						"Valentin_Galev", "Victor_Kros", "Wan_Zaman_Wan_Mustapha", "William_Berry_(footballer)",
						"Wu_Wei_(footballer)")))
				.build();

		paramBuilder.setCorpusDistributor(corpusDistributor);
		paramBuilder.setRunID(runID);
		paramBuilder.setProjectEnvironment(projectEnvironment);
		paramBuilder.setOntologyEnvironment(ontologyEnvironment);
		paramBuilder.setEpochs(epochs);

		/*
		 * Add factor-graph-templates.
		 */
		addTemplates(paramBuilder);

		/*
		 * Build parameter.
		 */
		OBIERunParameter parameter = paramBuilder.build();

		/*
		 * Created new standard Relation Extraction runner.
		 */
		AbstractOBIERunner runner = new StandardRERunner(parameter);

		/**
		 * Whether you want to run the prediction of new texts or train and test a model
		 * on a given corpus.
		 */
		boolean predict = false;

		if (!predict) {

			/**
			 * Whether you want to start active learning procedure or normal training
			 */
			boolean activeLearning = parameter.corpusDistributor instanceof ActiveLearningDistributor;

			/*
			 * train and/or test on existing corpus.
			 */

			if (activeLearning) {
				activeLearning(runner, acModus, printResults);
			} else {
				trainTest(runner);
			}

		} else {
			/*
			 * predict on a new documents.
			 */
			predict(runner, Arrays.asList(new File("predict/predict01.txt"), new File("predict/predict02.txt")));
		}
	}

	/**
	 * Add templates to the parameter builder.
	 * 
	 * @param paramBuilder
	 */
	private void addTemplates(Builder paramBuilder) {

		final Set<Class<? extends AbstractOBIETemplate<?>>> templates = new HashSet<>();
		/**
		 * TODO: Add new templates or try existing ones. Copy EmptyTemplate as
		 * code-template.
		 */

		/**
		 * Add your own templates:
		 */
		templates.add(PriorTemplate.class);
		templates.add(BirthYearTemplate.class);

		/**
		 * Predefined generic templates:
		 */
		templates.add(FrequencyTemplate.class);
		templates.add(TokenContextTemplate.class);
		templates.add(InterTokenTemplate.class);
		templates.add(InBetweenContextTemplate.class);
		templates.add(LocalTemplate.class);

		/**
		 * Templates that capture the cardinality of slots
		 */
		templates.add(SlotIsFilledTemplate.class);

		paramBuilder.setTemplates(templates);
	}

	private void predict(AbstractOBIERunner runner, final List<File> filesToPredict) throws IOException {
		log.info("Start prediction of new documents...");
		/*
		 * Load model if exists
		 */
		if (!runner.modelExists()) {
			log.warn("Model does not exists, abort prediction!");
			return;
		}

		try {
			runner.loadModel();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/**
		 * Build instances...
		 */
		final List<OBIEInstance> instancesToPredict = new ArrayList<>();

		for (File file : filesToPredict) {
			OBIEInstance i = new OBIEInstance(file.getName(), Files.lines(file.toPath()).reduce("", String::concat),
					null, new HashSet<>(Arrays.asList(ISoccerPlayer.class)));
			instancesToPredict.add(i);
		}

		/**
		 * Start prediction...
		 */
		List<OBIEState> finalStates = runner.predictInstancesBatch(instancesToPredict,
				new HashSet<>(Arrays.asList(SoccerPlayerRegExNEL.class)));

	}

	/**
	 * Run the system with the specifications and configurations.
	 * 
	 * @param runner
	 * @throws Exception
	 */
	private static void trainTest(AbstractOBIERunner runner) throws Exception {
		log.info("Start training / testing of a model with a given corpus...");
		final long testTime;
		final long trainingTime;
		final long trt;

		if (runner.modelExists()) {
			/*
			 * If the model exists, load the model from the file system. The model location
			 * is specified in the parameter and the environment.
			 */
			runner.loadModel();
			trt = 0;
		} else {
			/*
			 * If the model does not exists train. The model is automatically stored to the
			 * file system to the given model location!
			 */
			trainingTime = System.currentTimeMillis();
			runner.train();
			trt = (System.currentTimeMillis() - trainingTime);
			log.info("Total training time: " + trt + " ms.");
		}

		testTime = System.currentTimeMillis();
		/**
		 * Get predictions that can be evaluated for full evaluation and
		 * perSlotEvaluation.
		 */
		final List<SampledInstance<OBIEInstance, InstanceTemplateAnnotations, OBIEState>> predictions = runner
				.testOnTest();

		/**
		 * Evaluate the trained model on the test data. This is equal to predictOnTest
		 * and apply the results to an evaluator.
		 */
		final PRF1Container overallPRF1 = EvaluatePrediction.evaluateREPredictions(runner.objectiveFunction,
				predictions, runner.parameter.evaluator);
		/*
		 * Same as:
		 */
		// final PRF1Container overallPRF1 = runner.evaluateOnTest();

		log.info("Evaluation results on test data:\n" + overallPRF1);

		log.info("Evaluate predictions per slot:");

		/**
		 * Whether the output for each slot should be shown detailed or not. (Might
		 * generate large output)
		 */
		boolean detailedOutput = false;

		/**
		 * Evaluate the trained model on the test data for each slot individually.
		 */
		EvaluatePrediction.evaluatePerSlotPredictions(runner.objectiveFunction, predictions, runner.parameter.evaluator,
				detailedOutput);
		/*
		 * Same as:
		 */
//		runner.evaluatePerSlotOnTest(detailedOutput);

		final long tet = (System.currentTimeMillis() - testTime);

		log.info("Total training time: " + trt + " ms.");
		log.info("Total test time: " + tet + " ms.");
		log.info("Total time: "
				+ Duration.between(Instant.now(), Instant.ofEpochMilli(System.currentTimeMillis() + (trt + tet))));

	}

	private void activeLearning(AbstractOBIERunner runner, String acMode, File printResults) throws Exception {

		List<PRF1Container> performances = new ArrayList<>();

		long allTime = System.currentTimeMillis();

		int i = 1;

		final IActiveLearningDocumentRanker ranker;

		if (acMode.equals("random")) {
			ranker = new FullDocumentRandomRanker();
		} else if (acMode.equals("entropy")) {
			ranker = new FullDocumentEntropyRanker();
		} else {
			ranker = null;
			log.error("unkown active learning mode");
			System.exit(1);
		}

//		final IActiveLearningDocumentRanker documentModelScoreRanker = new FullDocumentModelScoreRanker();
//		final IActiveLearningDocumentRanker documentVarianceRanker = new FullDocumentVarianceRanker();

		PrintStream resultPrintStream = new PrintStream(new FileOutputStream(printResults, true));
		resultPrintStream.println("############Active Learning Performances: " + runID + "############");
		resultPrintStream.println("#Precision\tRecall\tF1");

		List<OBIEInstance> newTrainingInstances = new ArrayList<>();
		do {

			log.info("#############################");
			log.info("New active learning iteration: " + (i));
			long time = System.currentTimeMillis();

			log.info("Set training instances to("
					+ runner.corpusProvider.getTrainingCorpus().getInternalInstances().size() + "):");
//			runner.corpusProvider.getTrainingCorpus().getInternalInstances().forEach(System.out::println);
			log.info("Remaining training instances ("
					+ runner.corpusProvider.getDevelopCorpus().getInternalInstances().size() + "):");
//			runner.corpusProvider.getDevelopCorpus().getInternalInstances().forEach(System.out::println);
			log.info("#############################");

			runner.train();
//			if (newTrainingInstances.isEmpty()) {
//				runner.train();
//			} else {
//				runner.continueTraining(newTrainingInstances);
//			}

			List<SampledInstance<OBIEInstance, InstanceTemplateAnnotations, OBIEState>> predictions = runner
					.testOnTest();

			PRF1Container prf1 = EvaluatePrediction.evaluateREPredictions(runner.getObjectiveFunction(), predictions,
					runner.parameter.evaluator);

			performances.add(prf1);
			resultPrintStream.println(prf1.p + "\t" + prf1.r + "\t" + prf1.f1);

			log.info("############Active Learning performances############");
			performances.forEach(log::info);

			log.info("Time needed: " + (System.currentTimeMillis() - time));

		} while (!(newTrainingInstances = runner.corpusProvider.updateActiveLearning(runner, ranker)).isEmpty());

		log.info("############Active Learning performances############");
		performances.forEach(log::info);

		log.info("Total time needed: " + (System.currentTimeMillis() - allTime));

		log.info("Print results to: " + printResults);

		resultPrintStream.close();

	}

}
