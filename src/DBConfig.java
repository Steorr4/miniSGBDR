//JAVA Imports
import java.beans.ConstructorProperties;
import java.io.*;

//Jackson Imports
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DBConfig {
	private String dbpath; // Chemin vers la base de données
	private int pagesize; // Taille alloue a une page
	private int dm_maxfilesize;	// Taille maximum d'un fichier .rsbd

	//Constructor
	@ConstructorProperties({"dbpath", "pagesize", "dm_maxfilesize"})
	/* Pour que le mapper comprenne comment construire une instance */
	public DBConfig(String dbpath,int pagesize, int dm_maxfilesize) {
		this.dbpath = dbpath;
		this.pagesize = pagesize;
		this.dm_maxfilesize = dm_maxfilesize;
	}

	//Main Method
	public static DBConfig LoadDBConfig(String fic_config) throws FileNotFoundException, JsonParseException,
			JsonMappingException, IOException{

		ObjectMapper mapper = new ObjectMapper(); // Mapper qui permet de lire un fichier .json
		InputStream is = new FileInputStream(fic_config); // InputStream

		return mapper.readValue(is, DBConfig.class); // Creer une instance de DBConfig en lisant sur l'inputStream et la renvois
	}

	//Getters & Setters
	public String getDbpath() {
		return dbpath;
	}
	public int getPagesize() {
		return pagesize;
	}
	public int getDm_maxfilesize() {
		return dm_maxfilesize;
	}
	public void setDbpath(String dbpath) {
		this.dbpath = dbpath;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	public void setDm_maxfilesize(int dm_maxfilesize) {
		this.dm_maxfilesize = dm_maxfilesize;
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
