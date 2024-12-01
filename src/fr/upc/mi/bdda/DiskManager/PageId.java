package fr.upc.mi.bdda.DiskManager;

//JAVA Imports
import java.io.Serializable;

/**
 * Pointeur vers une page.
 */
public class PageId implements Serializable {
	private final int fileIdx; // ID d'un fichier .rsbd
	private final int pageIdx; // ID d'une page d'un fichier .rsbd

	/**
	 * Main constructor.
	 *
	 * @param fileIdx l'id du fichier dans laquel la page est stockée.
	 * @param pageIdx l'id de la page dans le fichier.
	 */
	public PageId(int fileIdx,int pageIdx) {
		this.fileIdx=fileIdx;
		this.pageIdx=pageIdx;
	}

	//Getters
	public int getFileIdx() {
		return fileIdx;
	}
	public int getPageIdx() {
		return pageIdx;
	}

	//Object Overrides
	@Override
	public String toString() {
		return "PageId : {" +
				"fileIdx=" + fileIdx +
				", pageIdx=" + pageIdx +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PageId)){ return false;}
        return fileIdx == ((PageId)o).fileIdx && pageIdx == ((PageId)o).pageIdx;
	}

}