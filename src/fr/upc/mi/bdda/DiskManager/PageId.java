package fr.upc.mi.bdda.DiskManager;

//Packages
import fr.upc.mi.bdda.BufferManager.*;

//JAVA Imports
import java.io.Serializable;

public class PageId implements Serializable {
	private int fileIdx; // ID d'un fichier .rsbd
	private int pageIdx; // ID d'une page d'un fichier .rsbd

	//Constructor
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

	//ToString
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