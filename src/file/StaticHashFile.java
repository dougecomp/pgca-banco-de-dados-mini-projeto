/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

import field.Field;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Iterator;
import operators.comparison.LogicOperator;
import operators.comparison.OperatorSet;
import operators.comparison.RelOperator;
import statistics.StatisticCenter;

/**
 *
 * @author douglas
 */
public class StaticHashFile {
    
    private final byte[] buffer;
    private final BlockFile[] blockFiles;
    private final Field[] fields; //the fields of the records stored in the file
    private final int qtdBuckets;
    private String fieldNameSearch;
    private Comparable valueSearch;
    
    /**
     * Creates a BlockFile.
     *
     * @param fileName the name of the file
     * @param pageSize the size of the page for this file. All pages in the will
     * have the same size. The pageSize must be a value above zero.
     * @param qtdBuckets the number of buckets of the static hash
     * @param fields the fields in each record of the file. The order in which
     * the fields are inserted is important.
     */
    public StaticHashFile(String fileName, int pageSize, int qtdBuckets, Field... fields) {
        this(null, fileName, pageSize, qtdBuckets, fields);
    }

    /**
     * Creates a BlockFile.
     *
     * @param statisticCenter the class that will collect statistic information
     * about the file usage
     * @param fileName the name of the file
     * @param pageSize the size of the page for this file. All pages in the will
     * have the same size. The pageSize must be a value above zero.
     * @param qtdBuckets Quantidade de buckets da hash estática
     * @param fields fields of the records
     */
    public StaticHashFile(StatisticCenter statisticCenter, String fileName, int pageSize, int qtdBuckets, Field... fields) {
        this.blockFiles = new BlockFile[qtdBuckets];
        for(int i=0;i<qtdBuckets;i++) {
            this.blockFiles[i] = new BlockFile("temp/"+i+fileName, pageSize, statisticCenter);
        }
        //this.blockFile = new BlockFile(fileName, pageSize, statisticCenter);
        this.fields = fields;
        this.buffer = new byte[pageSize];
        this.qtdBuckets = qtdBuckets;
    }

    /**
     * This method returns the file name.
     *
     * @param numBucket índice do bucket
     * @return the file name
     */
    public String getFileName(int numBucket) {
        return blockFiles[numBucket].getFileName();
    }

    /**
     * This method returns the statistic center of the given file
     *
     * @param numBucket índice do bucket
     * @return the statistic center of the given file
     */
    public StatisticCenter getStatisticCenter(int numBucket) {
        return blockFiles[numBucket].getStatisticCenter();
    }

    /**
     * This method prepares the file to run the operations.
     *
     * @throws IOException
     */
    public void open() throws IOException {
        for(int numBucket=0;numBucket<qtdBuckets;numBucket++) {
            if (new File(this.blockFiles[numBucket].getFileName() + ".mtd").exists()) {
                ObjectInputStream input;
                input = new ObjectInputStream(new FileInputStream(this.blockFiles[numBucket].getFileName() + ".mtd"));
                try {
                    Field[] storedFields = (Field[]) input.readObject();
                    if (!Arrays.equals(fields, storedFields)) {
                        throw new IOException("Fields specifications mismatch!");
                    }
                } catch (ClassNotFoundException ex) {
                    throw new IOException(ex);
                }
                input.close();
            }
            this.blockFiles[numBucket].open();
        }
    }

    /**
     * This method creates a Record from the given values, using the file fields
     * definition.
     *
     * @param values the values of the record
     * @return the record with the given values that follows the fields
     * definition
     */
    public Record createRecord(Comparable... values) {
        if (values.length != fields.length) {
            throw new RuntimeException("The number of values must be the same"
                    + " of the number or fields in the record's file!");
        }
        Record rec = new Record();
        Field field;
        for (int i = 0; i < fields.length; i++) {
            field = (Field) fields[i].clone();
            field.setValue(values[i]);
            rec.add(field);
        }
        return rec;
    }

    /**
     * This method inserts a record in the last page of the file
     *
     * @param record the record to be inserted
     * @throws IOException
     */
    public void insert(Record record) throws IOException {
        int numBucket = this.hash((int) record.get("id").getValue()) % this.qtdBuckets;
        long lastPageId = this.blockFiles[numBucket].size();
        Page page;
        if (lastPageId == 0) { //the file is empty, first page!
            page = new Page(this.blockFiles[numBucket].getBlockSize(), fields);
            lastPageId++;
        } else {
            this.blockFiles[numBucket].read(lastPageId, buffer); //read the last page from the disk
            page = Page.createPage(buffer, fields);
            if (page.isFull()) { //if the page is full, creates a new empty page
                page = new Page(this.blockFiles[numBucket].getBlockSize(), fields);
                lastPageId++;
            }
        }
        page.insert(record); //modifies the page
        this.blockFiles[numBucket].write(lastPageId, page.toByteArray()); //stores back in the disk
    }

    /**
     * This method removes all records that match the fieldName and value. This
     * method leaves spaces in the blocks.
     *
     * @param fieldName the field name to be compared with
     * @param value the value that satisfies the search criteria
     * @throws IOException
     */
    public void remove(String fieldName, Comparable value) throws IOException {
        int numBucket = this.hash( (int) value) % this.qtdBuckets;
        Page page;
        for (int i=1;i<=this.blockFiles[numBucket].size();i++) {            
            this.blockFiles[numBucket].read(i, buffer);
            page = Page.createPage(buffer, fields);
            if(page.remove(fieldName, value)){
                this.blockFiles[numBucket].write(i, page.toByteArray());
            }            
        }
    }

    /**
     * This method searches for all records whose the value of the given field
     * name is equals to the value parameter
     *
     * @param fieldName the field name to be compared with
     * @param value the value that satisfies the search criteria
     * @return the list of records that satisfies the search criteria
     * @throws IOException
     */
    public Iterator<Record> search(String fieldName, Comparable value) throws IOException {
        fieldNameSearch = fieldName;
        valueSearch = value;
        return range(fieldName, OperatorSet.eq, value);
    }

    /**
     * This method searches for all records whose the value of the given field
     * name satisfies the relational operator op
     *
     * @param fieldName the field name to be compared with
     * @param op the relational operator (<,<=, =, >,>= e !=)
     * @param value the value that satisfies the search criteria
     * @return the list of records that satisfies the search criteria
     * @throws IOException
     */
    public Iterator<Record> range(String fieldName, RelOperator op, Comparable value) throws IOException {
        return range(fieldName, op, value, null, null, null);
    }

    /**
     * This method returns records whose the given field's value attends both
     * search criteria
     *
     * @param fieldName the field name to be compared with
     * @param firstOp the first (left) relational operator (<,<=, =, >,>= e !=)
     * @param firstValue the first (left) value that satisfies the search
     * criteria
     * @param logicOp the logic operator (And or Or)
     * @param secondOp the second (right) relational operator (<,<=, =, >,>= e
     * !=)
     * @param secondValue the second (right) value that satisfies the search
     * criteria
     * @return the list of records that satisfies the search criteria
     * @throws IOException
     */
    public Iterator<Record> range(String fieldName, RelOperator firstOp, Comparable firstValue,
            LogicOperator logicOp, RelOperator secondOp, Comparable secondValue) throws IOException {
        return new RangeIterator(staticHashSearch(), fieldName, firstOp, firstValue,
                logicOp, secondOp, secondValue);
    }

    /**
     * This method returns an iterator over all records in the file
     *
     * @return the iterator over all records of the file
     * @throws IOException
     */
    public Iterator<Record> scan() throws IOException {
        return new ScanIterator();
    }
    
    public Iterator<Record> staticHashSearch() throws IOException {
        return new StaticHashSearchIterator();
    }

    /**
     * This method returns the number of records stored in the file. This method
     * performs a scan in the file in order to figure out the number of records.
     *
     * @return the number of records stored in the file.
     * @throws IOException
     */
    public long cardinality() throws IOException {
        long count = 0;
        Iterator it = scan();
        while (it.hasNext()) {
            count++;
            it.next();
        }
        return count;
    }
    
    /**
     * This method returns the number of blocks (pages) stored in the file.
     *
     * @return the number of blocks stored in the file
     * @throws IOException if there is an I/O problem during the operation
     */
    public long size() throws IOException {
       long totalSize = 0;
       for(int numBucket=0;numBucket<qtdBuckets;numBucket++)
           totalSize += this.blockFiles[numBucket].size();
       return totalSize;
    }

    /**
     * This method returns the size of the file in bytes.
     *
     * @return the size of the file in bytes
     * @throws IOException if there is an I/O problem during the operation
     */
    public long length() throws IOException {
        long length = 0;
        for(int i=0;i<qtdBuckets;i++)
            length += this.blockFiles[i].length();
        return length;
    }

    /**
     * This method writes the file metadata
     *
     * @throws IOException
     */
    public void close() throws IOException {
        for(int numBucket=0;numBucket<qtdBuckets;numBucket++) {
            if (!new File(this.blockFiles[numBucket].getFileName() + ".mtd").exists()) {
                ObjectOutputStream output;
                output = new ObjectOutputStream(new FileOutputStream(this.blockFiles[numBucket].getFileName() + ".mtd"));
                output.writeObject(fields);
                output.close();
            }
            this.blockFiles[numBucket].close();
        }
    }

    /**
     * This method iterates of the records that attends a search criteria
     */
    private class RangeIterator implements Iterator<Record> {

        private final Iterator<Record> scan;
        private final String fieldName;
        private final Comparable firstValue, secondValue;
        private final RelOperator firstOp, secondOp;
        private final LogicOperator logicOp;
        private Record nextRec;

        public RangeIterator(Iterator<Record> scan, String fieldName,
                RelOperator firstOp, Comparable firstValue,
                LogicOperator logicOp, RelOperator secondOp, Comparable secondValue) {
            this.scan = scan;
            this.fieldName = fieldName;
            this.firstOp = firstOp;
            this.firstValue = firstValue;
            this.logicOp = logicOp;
            this.secondOp = secondOp;
            this.secondValue = secondValue;

            nextRec = getNextRecord();
        }

        @Override
        public boolean hasNext() {
            return nextRec != null;
        }

        @Override
        public Record next() {
            Record retRec = nextRec;
            nextRec = getNextRecord();
            return retRec;
        }

        private Record getNextRecord() {            
            while (scan.hasNext()) {
                Record rec = scan.next();
                Comparable fieldValue = rec.get(fieldName).getValue();

                if (logicOp == null) { //checks only th left side (first operator and value)
                    if (firstOp.compare(fieldValue, firstValue)) {                        
                        return rec; //it has found a record that attends the query
                    }
                } else {
                    if (logicOp.compare(firstOp.compare(fieldValue, firstValue),
                            secondOp.compare(fieldValue, secondValue))) {                        
                        return rec; //it has found a record that attends the query
                    }
                }                
            }
            return null;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    /**
     * Scans the heap file
     */
    private class StaticHashSearchIterator implements Iterator<Record> {

        private long numPages = 0;
        private byte[] buffer;
        private int currentPageId = 0;
        private Page currentPage;
        private Iterator<Record> recIterator;
        private final int numBucket;

        public StaticHashSearchIterator() throws IOException {
            this.numBucket = hash((int) valueSearch) % qtdBuckets;
            buffer = new byte[blockFiles[numBucket].getBlockSize()];
            numPages = blockFiles[numBucket].size();
            recIterator = nextPageIterator();
        }

        private Iterator<Record> nextPageIterator() {
            try {
                Iterator<Record> it = null;
                while (currentPageId < numPages) {
                    currentPageId++;
                    blockFiles[numBucket].read(currentPageId, buffer);
                    currentPage = Page.createPage(buffer, fields);
                    it = currentPage.iterator();
                    if (it.hasNext()) { //checks if there is records in the page
                        break; //leave the loop
                    } else {
                        it = null;
                    }
                }
                return it;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean hasNext() {
            return recIterator != null && recIterator.hasNext();
        }

        @Override
        public Record next() {
            Record data = null;
            if (recIterator != null) {
                data = recIterator.next();

                if (!recIterator.hasNext()) {
                    recIterator = nextPageIterator();
                }
            }
            return data;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    /**
     * Scans the heap file
     */
    private class ScanIterator implements Iterator<Record> {

        private long numPages = 0;
        private byte[] buffer;
        private int currentPageId = 0;
        private Page currentPage;
        private Iterator<Record> recIterator;
        private int numBucket;

        public ScanIterator() throws IOException {
            this.numBucket = 0;
            buffer = new byte[blockFiles[numBucket].getBlockSize()];
            numPages = blockFiles[numBucket].size();
            recIterator = nextPageIterator();
        }

        private Iterator<Record> nextPageIterator() {
            try {
                Iterator<Record> it = null;
                while (currentPageId < numPages || numBucket < qtdBuckets ) {
                    if( numPages == 0 || currentPageId == numPages) {
                        numBucket++;
                        if(numBucket >= qtdBuckets) {
                            return null;
                        }
                        currentPageId = 0;
                        numPages = blockFiles[numBucket].size();
                        buffer = new byte[blockFiles[numBucket].getBlockSize()];
                        continue;
                    }
                    currentPageId++;
                    blockFiles[numBucket].read(currentPageId, buffer);
                    currentPage = Page.createPage(buffer, fields);
                    it = currentPage.iterator();
                    if (it.hasNext()) { //checks if there is records in the page
                        break; //leave the loop
                    } else {
                        numBucket++;
                        currentPageId = 0;
                        numPages = blockFiles[numBucket].size();
                        buffer = new byte[blockFiles[numBucket].getBlockSize()];
                        it = null;
                    }
                }
                return it;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean hasNext() {
            return recIterator != null && recIterator.hasNext();
        }

        @Override
        public Record next() {
            Record data = null;
            if (recIterator != null) {
                data = recIterator.next();

                if (!recIterator.hasNext()) {
                    recIterator = nextPageIterator();
                }
            }
            return data;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    /**
     * Função hash usada ( a * value ) + b
     * @param value
     * @return 
     */
    public int hash(int value) {
        int a = 7;
        int b = 9;
        return (a * value) + b;
    }
}
