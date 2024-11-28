package fr.upc.mi.bdda.mainTest;//Jackson Imports
import fr.upc.mi.bdda.BufferManager.*;
import fr.upc.mi.bdda.DiskManager.*;

//JAVA Imports


public class Test {
    public static void main(String[] agrs){

        //TESTS DB_CONFIG
//        try {
//            fr.upc.mi.bdda.DiskManager.DBConfig config = fr.upc.mi.bdda.DiskManager.DBConfig.LoadDBConfig("src/config.json");
//            System.out.println(config);
//        } catch (JsonMappingException e){
//            e.printStackTrace();
//        } catch (JsonParseException e){
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e){
//            e.printStackTrace();
//        }

        //TESTS DISK_MANAGER
//        try {
//            fr.upc.mi.bdda.DiskManager.DBConfig config = fr.upc.mi.bdda.DiskManager.DBConfig.LoadDBConfig("src/config.json");
//            fr.upc.mi.bdda.DiskManager.DiskManager dm = new fr.upc.mi.bdda.DiskManager.DiskManager(config);
//            fr.upc.mi.bdda.DiskManager.PageId p00=dm.AllocPage();
//            for (int i = 1; i<7; i++) System.out.println(dm.AllocPage());
//            byte[]b=new byte[config.getPagesize()];
//            for(int i = 0; i< config.getPagesize(); i++) b[i]=81;
//            ByteBuffer bb = ByteBuffer.wrap(b);
//            Charset cs = Charset.forName("Shift_JIS");
//            dm.WritePage(p00, bb);
//            ByteBuffer bb1 = ByteBuffer.allocate(config.getPagesize());
//            dm.ReadPage(p00, bb1);
//            bb1.flip();
//            CharBuffer cb = cs.decode(bb1);
//            System.out.println(cb);
//
//            dm.SaveState();
//            dm.LoadState();
//
//            fr.upc.mi.bdda.BufferManager.BufferManager bm = new fr.upc.mi.bdda.BufferManager.BufferManager(config, dm);
//            bm.getPage(p00);
//
//        } catch (JsonMappingException e){
//            e.printStackTrace();
//        } catch (JsonParseException e){
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e){
//            e.printStackTrace();
//        } catch (NullPointerException e){
//            e.printStackTrace();
//        }


        try{

            DBConfig config = DBConfig.LoadDBConfig("src/config.json");
            DiskManager dm = new DiskManager(config);
            BufferManager bm = new BufferManager(config, dm);

            PageId p1 = dm.AllocPage();
            PageId p2 = dm.AllocPage();
            PageId p3 = dm.AllocPage();
            PageId p4 = dm.AllocPage();
            bm.getPage(p1);
            bm.getPage(p2);
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
            System.out.println(bm.getPage(p1));
            System.out.println(bm.getPage(p2));
            System.out.println("=============");
            System.out.println(bm.getPage(p4));
            System.out.println("buffer list apres ajout=============");
            for (CustomBuffer elem : bm.getBufferList()){ //--
                System.out.println(elem);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
