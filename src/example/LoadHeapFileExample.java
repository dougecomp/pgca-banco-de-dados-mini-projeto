package example;

import field.DoubleField;
import field.IntegerField;
import field.StringField;
import file.HeapFile;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import statistics.DefaultStatisticCenter;

public class LoadHeapFileExample {

    public static void main(String[] args) throws IOException {

        HeapFile file = new HeapFile(new DefaultStatisticCenter(),
                "objects.hfl", 4096,
                new IntegerField("id"),
                new DoubleField("lat"),
                new DoubleField("lgt"),
                new StringField("title", 102)); //can store titles with 50 characters

        file.open();
        if (file.length() == 0) { //load the file
             BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("dublin.txt")));
                                    
            int id, p1, p2;
            double lat, lgt;
            String title;
            
            String line = reader.readLine();
            while(line!=null){
                p1= line.indexOf(' '); 
                id = Integer.parseInt(line.substring(0,p1)); //read the id
                
                p2= line.indexOf(' ', p1+1); //discard the second id
                
                p1=p2+1;p2= line.indexOf(' ', p1);
                lat = Double.parseDouble(line.substring(p1,p2));  //read the latitude
                                
                p1=p2+1;p2= line.indexOf(' ', p1);
                lgt = Double.parseDouble(line.substring(p1,p2));  //read the longitude
                
                title = line.substring(p2+1).trim(); 
                title = title.length()>50 ? title.substring(0,50) : title; //get the first 50 characters of the title
                                
                file.insert(file.createRecord(id, lat, lgt, title));
                
                line = reader.readLine();
            }   
            reader.close();
        }
        System.out.println("\n\n Statistics:");
        System.out.println(file.getStatisticCenter().status());

        System.out.println("Numero de registros: "+file.cardinality());
        System.out.println("Numero de páginas: "+file.size());
        System.out.println("Numero de bytes: "+file.length());
        file.close();
    }
}
