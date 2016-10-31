/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import field.DoubleField;
import field.IntegerField;
import field.StringField;
import file.HeapFileBinarySearch;
import file.Record;
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
public class HeapFileBinarySearchExample {
    
    private static final int PAGESIZE = 4096;
    private static final String EXTENSION = ".hbsfl";
    
    public static void print(Iterator<Record> it) {
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }
    
    public static HeapFileBinarySearch readFile(String filename) {
        HeapFileBinarySearch file = new HeapFileBinarySearch(new DefaultStatisticCenter(),
                filename+EXTENSION, PAGESIZE,
                new IntegerField("id"),
                new DoubleField("lat"),
                new DoubleField("lgt"),
                new StringField("title", 102)); //can store titles with 50 characters

        return file;
    }
    
    public static void indexar(String filename) throws IOException {
        
        HeapFileBinarySearch file = new HeapFileBinarySearch(new DefaultStatisticCenter(),
                filename+EXTENSION, PAGESIZE,
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
        System.out.println(file.getStatisticCenter().status());

        System.out.println("Numero de registros: "+file.cardinality());
        System.out.println("Numero de páginas: "+file.size());
        System.out.println("Numero de bytes: "+file.length());
        file.close();
    }
    
    public static Iterator<Record> buscar(String filename, int id) throws IOException {
        
        HeapFileBinarySearch file = new HeapFileBinarySearch(new DefaultStatisticCenter(),
                filename+EXTENSION, PAGESIZE,
                new IntegerField("id"),
                new DoubleField("lat"),
                new DoubleField("lgt"),
                new StringField("title", 102)); //can store titles with 50 characters
        
        file.open();
        
        Iterator<Record> ir = file.search("id", id);
        
        System.out.println(file.getStatisticCenter().status());
        
        return ir;
    }

    public static void teste() throws IOException {

        HeapFileBinarySearch file = new HeapFileBinarySearch(new DefaultStatisticCenter(),
                "staticHashFile.shfl", PAGESIZE,
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
    
    public static void fazerConsultas(HeapFileBinarySearch file, int qtdConsultas) throws IOException {
        
        Random r = new Random();
        int qtdRegistros = (int)file.cardinality();
        for(int i=0;i<qtdConsultas;i++) {
            int id = r.nextInt(qtdRegistros);
            //System.out.println("Consulta Nº "+(i+1));
            //System.out.println("Buscando id nº "+id);
            //file.getStatisticCenter(0).resetCounts();
            Iterator<Record> it = file.search("id", id);
            Record rec = it.next();
            //System.out.println(rec.toString());
            //System.out.println("blocksRead: "+pagesRead);
            //System.out.println("readTime: "+readTime);
            //System.out.println("");
            //System.out.println(file.getStatisticCenter(0).status());
        }
        double readTime = file.getStatisticCenter().getTally("readTime").getMean();
        double pagesRead = file.getStatisticCenter().getCount("blocksRead").getValue();
        System.out.println("blocksRead: "+pagesRead);
        System.out.println("Mean readTime: "+readTime);
        
    }
    
    public static void main(String[] args) throws IOException {
        
        //String filename = "dublin.txt";
        String filename = "australia.txt";
        //String filename = "british.txt";
        boolean indexar = false;
        int qtdConsultas = 200;
        
        // Utilizar função indexar caso o arquivo com os dados ainda não ter sido criado
        if(indexar) {
            System.out.println("Indexando "+filename);
            indexar(filename);
        } else {
            System.out.println("Fazendo consultas em "+filename);
            HeapFileBinarySearch file = readFile(filename);
            file.open();

            fazerConsultas(file, qtdConsultas);

            file.close();
        }
        
    }
    
}
