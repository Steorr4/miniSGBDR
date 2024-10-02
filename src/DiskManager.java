//JAVA Imports
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.io.*;

public class DiskManager {
	private DBConfig config; // Instance de la config
	private ArrayList<PageId> pagesLibres; // Array de pages libres attendant d'etre allouees
	private int nbFichiers; // Nombre de fichier deja crees

	//Constructor
	public DiskManager(DBConfig dbConfig) {
		config=dbConfig;
		pagesLibres= new ArrayList<PageId>();
		nbFichiers=0;
	}
	

	//Methods
	public PageId AllocPage() throws NullPointerException, IOException{

		if(!pagesLibres.isEmpty()) { //Si il y a un page libre, alors on retourne la page en question
			PageId pid=pagesLibres.getFirst();
			pagesLibres.removeFirst();
			return pid;
		}

		//Si il n'y a pas de page libre alors on doit creer un nouveau fichier avec de nouvelles pages.
		File f = new File(config.getDbpath()+"/BinData/F"+nbFichiers+".rsbd");
		f.createNewFile(); // Creation d'un nouveau fichier
		int nbPages=config.getDm_maxfilesize()/config.getPagesize(); // Calcul du nb de page par fichiers
		for(int i=1;i<nbPages;i++) pagesLibres.add(new PageId(nbFichiers,i)); // Remplissage de l'array avec les nouvelles pages libres
		PageId pid=new PageId(nbFichiers,0); // On garde la premiere page (0) du nouveau ficher
		nbFichiers++; // On incremente le nb de fichier
		return pid; // On renvoit l'id de la nouvelle page
	}
	
	
	public void ReadPage(PageId pageId,ByteBuffer buff)  {
		int nbF=pageId.getFileIdx();
		String nomFichier="F"+nbF+".rsdb";
		try {
			RandomAccessFile raf=new RandomAccessFile(nomFichier,"r");
			raf.seek(pageId.getPageIdx()*config.getPagesize());
			for(int i=0;i<config.getPagesize();i++) {
				buff.put(raf.readByte());
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void WritePage(PageId pageId,ByteBuffer buff) {
		int nbF=pageId.getFileIdx();
		String nomFichier="F"+nbF+".rsdb";
		try {
			RandomAccessFile raf=new RandomAccessFile(nomFichier,"w");
			raf.seek(pageId.getPageIdx()*config.getPagesize());
			for(int i=0;i<config.getPagesize();i++) {
				raf.write(buff.get(i));
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void DeallocPage(PageId pageId) {
		pagesLibres.add(pageId);
	}
	
//	public void SaveState() {
//		 FileOuputStream f= new FileOutputStream("dm.save");
//		 ObjectOutputStream s= new ObjectOutputStream("pageId.obj");
//		 s.writeObject(pagesLibres);
//		 s.flush();
//
//	}
//
//	public void LoadState() {
//		FileInputStream f=new FileInputStream("pageId.obj");
//		ObjectInputStream s=new ObjectInputStream(f);
//		s.readObject();
//	}

}