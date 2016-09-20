package example;

import field.DoubleField;
import field.IntegerField;
import field.StringField;
import file.HeapFile;
import file.Record;
import java.io.IOException;
import java.util.Iterator;
import operators.comparison.OperatorSet;
import statistics.DefaultStatisticCenter;

public class HeapFileExample {

    public static void print(Iterator<Record> it) {
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    public static void main(String[] args) throws IOException {
        int pageSize = 4096;

        HeapFile file = new HeapFile(new DefaultStatisticCenter(),
                "heapfile.hfl", pageSize,
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

        System.out.println("\n\n Statistics:");
        System.out.println(file.getStatisticCenter().status());

        System.out.println("\n\nAll students");
        print(file.scan()) ; 

        System.out.println("\n\nStudents with income equals 6400");
        print(file.search("income", 6400.0));

        System.out.println("\n\nStudents with income greater than 3000");
        print(file.range("income", OperatorSet.gt, 3000.0));

        System.out.println("\n\nStudents with income between 1000 and 1500, inclusive");
        print(file.range("income", OperatorSet.gte, 1000.0, OperatorSet.and, OperatorSet.ste, 1500.0));

        System.out.println("\n\nRemove students with name='João B. Rocha-Junior'");
        file.remove("sname", "João B. Rocha-Junior");

        System.out.println("\n\nAll students");
        print(file.scan());

        file.close();

        System.out.println("\n\n Statistics:");
        System.out.println(file.getStatisticCenter().status());
    }
}
