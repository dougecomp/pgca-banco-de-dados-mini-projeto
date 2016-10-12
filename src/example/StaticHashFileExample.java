/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import field.DoubleField;
import field.IntegerField;
import field.StringField;
import file.Record;
import file.StaticHashFile;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import statistics.DefaultStatisticCenter;

/**
 *
 * @author douglas
 */
public class StaticHashFileExample {
 
    public static void print(Iterator<Record> it) {
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }
    
    public static void dublin() throws IOException {
        int qtdBuckets = 5;
        int pageSize = 4096;
        
        StaticHashFile file = new StaticHashFile(new DefaultStatisticCenter(),
                "dublinObjectsSHF.hfl", pageSize, qtdBuckets,
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
        //System.out.println("\n\n Statistics:");
        //System.out.println(file.getStatisticCenter().status());

        System.out.println("Numero de registros: "+file.cardinality());
        System.out.println("Numero de p�ginas: "+file.size());
        System.out.println("Numero de bytes: "+file.length());
        file.close();
    }

    public static void teste() throws IOException {
        int pageSize = 4096;
        int qtdBuckets = 5;

        StaticHashFile file = new StaticHashFile(new DefaultStatisticCenter(),
                "staticHashFile.hfl", pageSize, qtdBuckets,
                new IntegerField("sid"),
                new StringField("sname", 300),
                new DoubleField("income"));

        
        file.open();
        
        
        if (file.length() == 0) {//if the file does not exist yet
            file.insert(file.createRecord(1, "Jo�o B. Rocha-Junior", 2304.5));
            file.insert(file.createRecord(2, "Ad�lia Ico", 5304.5));
            file.insert(file.createRecord(3, "EdCarlos da Silva Santana", 1500.0));
            file.insert(file.createRecord(4, "F�bio Oliveira", 7804.5));
            file.insert(file.createRecord(5, "Ricardo Carvalho", 5990.5));
            file.insert(file.createRecord(6, "Ivonete Maciel", 12700.5));
            file.insert(file.createRecord(7, "Jo�o Paulo", 1500.0));
            file.insert(file.createRecord(8, "Karine Almeida", 1500.0));
            file.insert(file.createRecord(9, "Tassalon Silva", 1500.0));
            file.insert(file.createRecord(10, "Kleverton Mois�s Silva", 6400.0));
            file.insert(file.createRecord(11, "Tiago Novais", 5600.0));
        }

        //System.out.println("\n\n Statistics:");
        //System.out.println(file.getStatisticCenter().status());

        //System.out.println("\n\nAll students");
        //print(file.scan()) ; 

        //System.out.println("\n\nStudents with income equals 6400");
        //print(file.search("income", 6400.0));

        //System.out.println("\n\nStudents with income greater than 3000");
        //print(file.range("income", OperatorSet.gt, 3000.0));

        //System.out.println("\n\nStudents with income between 1000 and 1500, inclusive");
        //print(file.range("income", OperatorSet.gte, 1000.0, OperatorSet.and, OperatorSet.ste, 1500.0));

        //System.out.println("\n\nRemove students with name='Jo�o B. Rocha-Junior'");
        //file.remove("sname", "Jo�o B. Rocha-Junior");

        System.out.println("\n\nAll students");
        print(file.scan());

        file.close();

        System.out.println("\n\n Statistics:");
        //System.out.println(file.getStatisticCenter().status());
    }
    
    public static void main(String[] args) throws IOException {
        dublin();
    }
    
}