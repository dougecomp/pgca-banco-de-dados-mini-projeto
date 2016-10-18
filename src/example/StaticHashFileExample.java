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
import java.util.Random;
import statistics.DefaultStatisticCenter;

/**
 *
 * @author douglas
 */
public class StaticHashFileExample {

    private static final int QTDBUCKETS = 5;
    private static final int PAGESIZE = 4096;
    private static final String EXTENSION = ".shfl";
    
    public static void print(Iterator<Record> it) {
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }
    
    public static StaticHashFile readFile(String filename) {
        StaticHashFile file = new StaticHashFile(new DefaultStatisticCenter(),
                filename+EXTENSION, PAGESIZE, QTDBUCKETS,
                new IntegerField("id"),
                new DoubleField("lat"),
                new DoubleField("lgt"),
                new StringField("title", 102)); //can store titles with 50 characters

        return file;
    }
    
    public static void indexar(String filename) throws IOException {
        
        StaticHashFile file = new StaticHashFile(new DefaultStatisticCenter(),
                filename+EXTENSION, PAGESIZE, QTDBUCKETS,
                new IntegerField("id"),
                new DoubleField("lat"),
                new DoubleField("lgt"),
                new StringField("title", 102)); //can store titles with 50 characters

        file.open();
        if (file.length() == 0) { //load the file
             BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
                                    
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
        for(int numBucket=0;numBucket<QTDBUCKETS;numBucket++) {
            System.out.println(file.getStatisticCenter(numBucket).status());
        }

        System.out.println("Numero de registros: "+file.cardinality());
        System.out.println("Numero de páginas: "+file.size());
        System.out.println("Numero de bytes: "+file.length());
        file.close();
    }
    
    public static Iterator<Record> buscar(String filename, int id) throws IOException {
        
        StaticHashFile file = new StaticHashFile(new DefaultStatisticCenter(),
                filename+EXTENSION, PAGESIZE, QTDBUCKETS,
                new IntegerField("id"),
                new DoubleField("lat"),
                new DoubleField("lgt"),
                new StringField("title", 102)); //can store titles with 50 characters
        
        file.open();
        
        Iterator<Record> ir = file.search("id", id);
        
        System.out.println(file.getStatisticCenter(0).status());
        
        return ir;
    }

    public static void teste() throws IOException {

        StaticHashFile file = new StaticHashFile(new DefaultStatisticCenter(),
                "staticHashFile.shfl", PAGESIZE, QTDBUCKETS,
                new IntegerField("sid"),
                new StringField("sname", 300),
                new DoubleField("income"));

        
        file.open();

        if (file.length() == 0) {//if the file does not exist yet
            file.insert(file.createRecord(1, "João B. Rocha-Junior", 2304.5));
            file.insert(file.createRecord(2, "Adélia Ico", 5304.5));
            file.insert(file.createRecord(3, "EdCarlos da Silva Santana", 1500.0));
            file.insert(file.createRecord(4, "Fábio Oliveira", 7804.5));
            file.insert(file.createRecord(5, "Ricardo Carvalho", 5990.5));
            file.insert(file.createRecord(6, "Ivonete Maciel", 12700.5));
            file.insert(file.createRecord(7, "João Paulo", 1500.0));
            file.insert(file.createRecord(8, "Karine Almeida", 1500.0));
            file.insert(file.createRecord(9, "Tassalon Silva", 1500.0));
            file.insert(file.createRecord(10, "Kleverton Moisés Silva", 6400.0));
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

        //System.out.println("\n\nRemove students with name='João B. Rocha-Junior'");
        //file.remove("sname", "João B. Rocha-Junior");

        System.out.println("\n\nAll students");
        print(file.scan());

        file.close();

        System.out.println("\n\n Statistics:");
        //System.out.println(file.getStatisticCenter().status());
    }
    
    public static void fazerConsultas(StaticHashFile file, int qtdConsultas) throws IOException {
        
        Random r = new Random();
        int qtdRegistros = (int)file.cardinality();
        for(int i=0;i<qtdConsultas;i++) {
            int id = r.nextInt(qtdRegistros);
            Iterator<Record> it = file.search("id", id);
            System.out.println(file.getStatisticCenter(0).status());
            Record rec = it.next();
            System.out.println(rec.toString());
        }
        
    }
    
    public static void main(String[] args) throws IOException {
        
        String filename = "dublin.txt";
        //String filename = "australia.txt";
        //String filename = "british.txt";
        int qtdConsultas = 10;
        
        // Utilizar função indexar para que os buckets sejam criados
        //indexar(filename);
        StaticHashFile file = readFile(filename);
        file.open();
        
        fazerConsultas(file, qtdConsultas);
        
        file.close();
        
    }
    
}
