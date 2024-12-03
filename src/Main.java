//Packages
import fr.upc.mi.bdda.BufferManager.*;
import fr.upc.mi.bdda.DiskManager.*;
import fr.upc.mi.bdda.FileAccess.*;
import fr.upc.mi.bdda.FileAccess.Record;

//JAVA Imports
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] agrs){

    //TEST BUFFER MANAGER
        try{

            DBConfig config = DBConfig.LoadDBConfig("src/config.json");
            DiskManager dm = new DiskManager(config);
            BufferManager bm = new BufferManager(config, dm);

            PageId p1 = dm.AllocPage();
            PageId p2 = dm.AllocPage();
            PageId p3 = dm.AllocPage();
            PageId p4 = dm.AllocPage();
            bm.getPage(p1);
            CustomBuffer cb=bm.getPage(p2);
            bm.getPage(p3);
            System.out.println(bm.getPage(p1)); //--

            System.out.println("buffer list initial==============");

            for (CustomBuffer elem : bm.getBufferList()){ //--
                System.out.println(elem);
            }

            System.out.println("==============");

            bm.getPage(p3);
            System.out.println("Buffer list size after p3: " + bm.getBufferList().size());
            for (CustomBuffer elem : bm.getBufferList()){ //--
                System.out.println(elem);
            }


            System.out.println("=============");
            bm.freePage(p1,false);
            bm.freePage(p2,false);
            System.out.println(config.getBm_buffercount());
            System.out.println("=============");
            System.out.println(bm.getPage(p4));
            System.out.println("buffer list apres ajout=============");
            for (CustomBuffer elem : bm.getBufferList()){ //--
                System.out.println(elem);
            }


            //TEST RELATION
            List<ColInfo> colonne=new ArrayList<>();
            colonne.add(new ColInfo("nom", new TypeParam(10, TypeParam.ETypeParam.VARCHAR)));
            colonne.add(new ColInfo("prenom", new TypeParam(10, TypeParam.ETypeParam.CHAR)));
            colonne.add(new ColInfo("age", new TypeNonParam(TypeNonParam.ETypeNonParam.INT)));

            Relation relation = new Relation("nina",colonne,new PageId(0,0),dm,bm);
            List<String> l= new ArrayList<>();
            l.add("sdksdkglksdkskklsdjl");
            l.add("Fontqine");
            l.add("12");
            Record record= new Record(new RecordID(p4,0),l);


            int total= relation.writeRecordToBuffer(record,cb,0);
            System.out.println("taille totale write " +total );
            System.out.println("\ntaille totale read " + relation.readFromBuffer(record, cb, 0) );














        } catch (Exception e) {
            e.printStackTrace();
        }
























    }






}
