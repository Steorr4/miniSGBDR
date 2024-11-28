package fr.upc.mi.bdda.DiskManager;

//Packages
import fr.upc.mi.bdda.BufferManager.*;

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
		File f = new File(config.getDbpath()+"/BinData/F"+nbFichiers+".rsdb");
		f.createNewFile(); // Creation d'un nouveau fichier
		int nbPages=config.getDm_maxfilesize()/config.getPagesize(); // Calcul du nb de page par fichiers
		for(int i=1;i<nbPages;i++) pagesLibres.add(new PageId(nbFichiers,i)); // Remplissage de l'array avec les nouvelles pages libres
		PageId pid=new PageId(nbFichiers,0); // On garde la premiere page (0) du nouveau ficher
		nbFichiers++; // On incremente le nb de fichier
		return pid; // On renvoit l'id de la nouvelle page
	}
	
	
	public void ReadPage(PageId pageId, ByteBuffer buff)  {
		String pathFichier=config.getDbpath()+"/BinData/F"+pageId.getFileIdx()+".rsdb"; // Recupere le nom du fichier grace a l'id du pageid

		try {

			RandomAccessFile raf = new RandomAccessFile(pathFichier, "r");
			raf.seek(pageId.getPageIdx() * config.getPagesize()); // Se pose au debut de la page dans le fichier

			int i = 0;
			while(i<config.getPagesize() && i<(raf.length()-pageId.getPageIdx()*config.getPagesize())) {
				buff.put(raf.readByte());
				i++;
			} // Copie toute la page dans le tampon

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void WritePage(PageId pageId, ByteBuffer buff) {
		String pathFichier= config.getDbpath()+"/BinData/F"+pageId.getFileIdx()+".rsdb";

		try {
			RandomAccessFile raf=new RandomAccessFile(new File(pathFichier),"rw");
			raf.seek(pageId.getPageIdx()*config.getPagesize());
			for(int i=0; i< config.getPagesize(); i++) raf.write(buff.get());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void DeallocPage(PageId pageId) {
		pagesLibres.add(pageId); // Desalloue une page en la rendant disponible a la reecriture
		// (Le traitement du fait que l'on ne peut pas aller lire une page desalloue se fera surement sur une couche + haute)
	}
	
	public void SaveState() { //TODO a tester plus en profondeur
		try {
			FileOutputStream fos = new FileOutputStream(config.getDbpath()+"/dm.save");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(pagesLibres);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void LoadState() {
		try {
			FileInputStream fis = new FileInputStream(config.getDbpath()+"/dm.save");
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.pagesLibres = (ArrayList<PageId>) ois.readObject();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}

	}


}