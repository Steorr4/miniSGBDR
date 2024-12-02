package fr.upc.mi.bdda.DiskManager;

//JAVA Imports
import java.beans.ConstructorProperties;
import java.io.*;

//Jackson Imports
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Classe qui permet de recuperer une config au format .json afin de definir les variables de fonctionnemet du SGBD.
 * (Voir TP1-F)
 */
public class DBConfig {
	private final String dbpath; // Chemin vers la base de données
	private final int pagesize; // Taille alloue a une page
	private final int dm_maxfilesize;	// Taille maximum d'un fichier .rsbd
	private final int bm_buffercount; // Nombre de cadre de page
	private final String bm_policy; // Politique de remplacement

	/**
	 * Main constructor.
	 *
	 * @param dbpath le chemin vers la Base de donnée.
	 * @param pagesize la taille d'une page.
	 * @param dm_maxfilesize la taille d'un fichier rsdb.
	 * @param bm_buffercount le nombre de cadre de page max pouvant etre load en mémoire.
	 * @param bm_policy la politique de remplacement utilisée pour échanger les pages load en mémoire (LRU/MRU).
	 */
	@ConstructorProperties({"dbpath", "pagesize", "dm_maxfilesize",
			"bm_buffercount", "bm_policy"})
	/* Pour que le mapper comprenne comment construire une instance */
	public DBConfig(String dbpath, int pagesize, int dm_maxfilesize, int bm_buffercount, String bm_policy) {
		if(pagesize <= 0 || dm_maxfilesize <= 0 ||
			bm_buffercount <= 0){
			throw new IllegalArgumentException("Erreur config");
		}
		this.dbpath = dbpath;
		this.pagesize = pagesize;
		this.dm_maxfilesize = dm_maxfilesize;
		this.bm_buffercount = bm_buffercount;
		this.bm_policy = bm_policy;
	}

	/**
	 * Utilise un mapper de la librairie Jackson afin de lire via le flux d'input le fichier config.json et
	 * construire un objet DBConfig.
	 *
	 * @param fic_config le chemin vers le fichier de config.
	 * @return Une instance de DBConfig base sur les parametres du fichier.
	 * @throws FileNotFoundException si le chemin vers le fichier config est erronné.
	 * @throws IOException un champ du fichier est incorrecte.
	 */
	public static DBConfig LoadDBConfig(String fic_config) throws FileNotFoundException,
            IOException{

		ObjectMapper mapper = new ObjectMapper();
		InputStream is = new FileInputStream(fic_config);

		return mapper.readValue(is, DBConfig.class);
	}

	//Getters
	public String getDbpath() {
		return dbpath;
	}
	public int getPagesize() {
		return pagesize;
	}
	public int getDm_maxfilesize() {
		return dm_maxfilesize;
	}
	public int getBm_buffercount(){
		return bm_buffercount;
	}
	public String getBm_policy() {
		return bm_policy;
	}

	//ToString
	@Override
	public String toString() {
		return "DBConfig : {" +
				"dbpath='" + dbpath + '\'' +
				", pagesize=" + pagesize +
				", dm_maxfilesize=" + dm_maxfilesize +
				'}';
	}
}
