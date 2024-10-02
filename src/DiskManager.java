import java.nio.ByteBuffer;
import java.util.Vector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;

public class DiskManager {
	private DBConfig config;
	private Vector<PageId> pagesLibres;
	private int nbFichiers;
	
	public DiskManager(DBConfig dbConfig) {
		config=dbConfig;
		pagesLibres= new Vector<PageId>();
		nbFichiers=0;
	}
	
	
	public PageId AllocPage() {
		if(pagesLibres.size()!=0) {
			PageId libre=pagesLibres.get(0);
			pagesLibres.remove(0);
			return libre;
		}
		try {
			 
            File f = new File(config.getDbpath()+"BinData\\F"+(nbFichiers)+".rsdb");
            
            if (f.createNewFile())
                System.out.println("File created");
            else
                System.out.println("File already exists");
            
            int nbpages=config.getDm_maxfilesize()/config.getPagesize();
            for(int i=1;i<nbpages;i++) pagesLibres.add(new PageId(nbFichiers,i));
            
            PageId pid=new PageId(nbFichiers,0);
            nbFichiers++;
            return pid;
            
        }
        catch (Exception e) {
            System.err.println(e);
        }
		return null;
		
		
		
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