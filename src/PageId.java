public class PageId {
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
}