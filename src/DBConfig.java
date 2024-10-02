//import org.json.simple.JSONObject;
//import org.json.simple.parser.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

public class DBConfig {
	private String dbpath; //chemin vers la base de données
	private int pagesize;
	private int dm_maxfilesize;

	public DBConfig(String dbpath,int pagesize, int dm_maxfilesize) {
		this.dbpath = dbpath;
		this.pagesize=pagesize;
		this.dm_maxfilesize=dm_maxfilesize;
		
	}
	
	/*public static DBConfig LoadDBConfig(String fichier_config) throws ParseException{
		try {
		
		Object o=new JSONParser().parse(new FileReader(fichier_config));
		JSONObject j= (JSONObject) o;
		String path=(String)j.get("path");
		return new DBConfig(path);
		
		}
		catch(IOException e) {
			System.err.println("erreur de lecture" );
		}
		return null;
		
	}*/
	public static DBConfig LoadDBConfig(String fichier_config) {
		try {
			FileReader fr=new FileReader(fichier_config);
			BufferedReader br= new BufferedReader((fr));
			String str=br.readLine();
			int nbp=Integer.parseInt(br.readLine());
			int dm=Integer.parseInt(br.readLine());
			br.close();
			return new DBConfig(str,nbp,dm);
		}
		catch(IOException e) {
			System.err.println("erreur");
		}
		
		return null;
	}
	



	
	public void afficher() {
		System.out.println("le chemin: "+dbpath);
	}

	public String getDbpath() {
		return dbpath;
	}
	
	public int getPagesize() {
		return pagesize;
	}

	
	public int getDm_maxfilesize() {
		return dm_maxfilesize;
	}
	
}