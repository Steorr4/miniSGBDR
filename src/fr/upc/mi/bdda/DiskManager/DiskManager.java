package fr.upc.mi.bdda.DiskManager;

//JAVA Imports
import fr.upc.mi.bdda.BufferManager.CustomBuffer;

import java.util.ArrayList;
import java.io.*;
import java.util.List;

/**
 * Classe qui s'occupe de la gestion de l'espace disque.
 * Les couches au-dessus y font appel allouer/desallouer une page ou lire/ecrire sur une page.
 * <br/>(Voir TP2-C pour comprendre le fonctionnement plus en détail)
 */
public class DiskManager{
	private final DBConfig config; // Instance de la config
	private List<PageId> pagesLibres; // Array de pages libres attendant d'être allouées
	private int nbFichiers; // Nombre de fichiers deja créés

	/**
	 * Main constructor.
	 *
	 * @param dbConfig l'instance de la config paramètrant la base de donnée.
	 */
	public DiskManager(DBConfig dbConfig) {
		config=dbConfig;
		pagesLibres = new ArrayList<>();
		nbFichiers=0;
	}

	//Methods

	/**
	 * Réserve une nouvelle page à la demande d'une couche superieure.
	 * Si aucune page n'est disponible, créer alors un nouveau fichier .rsdb et ajoute les nouvelles pages
	 * à la liste des pages disponibles.
	 *
	 * @return L'identifiant de la page allouee.
	 * @throws IOException si une erreur est survenue à la création d'un fichier.
	 */
	public PageId allocPage() throws IOException{

		//S'il y a une page libre, alors on retourne la page en question
		if(!pagesLibres.isEmpty()) {
			PageId pid=pagesLibres.getFirst();
			pagesLibres.removeFirst();
			return pid;
		}

		//S'il n'y a pas de page libre alors, on doit creer un nouveau fichier avec de nouvelles pages.
		File f = new File(config.getDbpath()+"/BinData/F"+nbFichiers+".rsdb");
		f.createNewFile();

		int nbPages=config.getDm_maxfilesize()/config.getPagesize();
		for(int i=1;i<nbPages;i++) pagesLibres.add(new PageId(nbFichiers,i));

		PageId pid=new PageId(nbFichiers,0);
		nbFichiers++;
		return pid;
	}

	/**
	 * Copie le contenu d'une page sur disque dans un buffer en mémoire donné par l'appelant.
	 *
	 * @param pageId la page demmandée.
	 * @param buff le buffer dans lequel on souhaite copier la page.
	 */
	public void readPage(PageId pageId, CustomBuffer buff)  {
		String pathFichier = config.getDbpath()+"/BinData/F"+pageId.getFileIdx()+".rsdb";

		try {

			RandomAccessFile raf = new RandomAccessFile(pathFichier, "r");
			raf.seek((long) pageId.getPageIdx() * config.getPagesize());

			buff.setPos(0);
			byte[] data = new byte[config.getPagesize()];
			for(int i = 0; i < data.length; i++) data[i] = raf.readByte();

			buff.putBytes(data);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Ecrit le contenu du buffeur donné par l'appelant dans une page sur disque.
	 *
	 * @param pageId la page indiquée.
	 * @param buff le buffer dans lequel sont contenues les données que l'on souhaite écrire sur disque.
	 */
	public void writePage(PageId pageId, CustomBuffer buff) {
		String pathFichier = config.getDbpath()+"/BinData/F"+pageId.getFileIdx()+".rsdb";

		try {

			RandomAccessFile raf = new RandomAccessFile(new File(pathFichier),"rw");
			raf.seek((long) pageId.getPageIdx() * config.getPagesize());

			buff.setPos(0);
			byte[] data = new byte[buff.remaining()];
			buff.getBytes(data);
			raf.write(data);

		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Permet de désallouer une page en la rajoutant à la liste des pages vides.
	 *
	 * @param pageId la page à désallouer
	 */
	public void deallocPage(PageId pageId) {
		pagesLibres.add(pageId);
	}

	/**
	 * Enregistre la liste des pages libres dans un fichier dm.save placé à la racine du dossier de la BDD.
	 */
	public void saveState() {
		try {

			FileOutputStream fos = new FileOutputStream(config.getDbpath()+"/dm.save");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(pagesLibres);
			oos.close();

		}catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Charge la liste des pages vides depuis le fichier dm.save généré par la méthode SaveState()
	 */
	public void loadState() {
		try {

			File f = new File(config.getDbpath()+"/dm.save");
			if(!f.exists()) {
				f.createNewFile();
			}
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.pagesLibres = (ArrayList<PageId>) ois.readObject();

		}catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	//Getter
	public List<PageId> getPagesLibres() {
		return pagesLibres;
	}

}