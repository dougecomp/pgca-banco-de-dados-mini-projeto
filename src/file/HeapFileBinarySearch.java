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

public class HeapFileBinarySearch {

    private final byte[] buffer;
    private final BlockFile blockFile;
    private final Field[] fields; //the fields of the records stored in the file

    /**
     * Creates a BlockFile.
     *
     * @param fileName the name of the file
     * @param pageSize the size of the page for this file. All pages in the will
     * have the same size. The pageSize must be a value above zero.
     * @param fields the fields in each record of the file. The order in which
     * the fields are inserted is important.
     */
    public HeapFileBinarySearch(String fileName, int pageSize, Field... fields) {
        this(null, fileName, pageSize, fields);
    }

    /**
     * Creates a BlockFile.
     *
     * @param fileName the name of the file
     * @param pageSize the size of the page for this file. All pages in the will
     * have the same size. The pageSize must be a value above zero.
     * @param statisticCenter the class that will collect statistic information
     * about the file usage
     */
    public HeapFileBinarySearch(StatisticCenter statisticCenter, String fileName, int pageSize, Field... fields) {
        this.blockFile = new BlockFile(fileName, pageSize, statisticCenter);
        this.fields = fields;
        this.buffer = new byte[pageSize];
    }

    /**
     * This method returns the file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return blockFile.getFileName();
    }

    /**
     * This method returns the statistic center of the given file
     *
     * @return the statistic center of the given file
     */
    public StatisticCenter getStatisticCenter() {
        return blockFile.getStatisticCenter();
    }

    /**
     * This method prepares the file to run the operations.
     *
     * @throws IOException
     */
    public void open() throws IOException {
        if (new File(blockFile.getFileName() + ".mtd").exists()) {
            ObjectInputStream input;
            input = new ObjectInputStream(new FileInputStream(blockFile.getFileName() + ".mtd"));
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
        blockFile.open();
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
        long lastPageId = blockFile.size();
        Page page;
        if (lastPageId == 0) { //the file is empty, first page!
            page = new Page(blockFile.getBlockSize(), fields);
            lastPageId++;
        } else {
            blockFile.read(lastPageId, buffer); //read the last page from the disk
            page = Page.createPage(buffer, fields);
            if (page.isFull()) { //if the page is full, creates a new empty page
                page = new Page(blockFile.getBlockSize(), fields);
                lastPageId++;
            }
        }
        page.insert(record); //modifies the page
        blockFile.write(lastPageId, page.toByteArray()); //stores back in the disk
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
        Page page;
        for (int i=1;i<=blockFile.size();i++) {            
            blockFile.read(i, buffer);
            page = Page.createPage(buffer, fields);
            if(page.remove(fieldName, value)){
                blockFile.write(i, page.toByteArray());
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
        return new RangeIterator(scan(), fieldName, firstOp, firstValue,
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

    /**
     * This method returns the number of records stored in the file. This method
     * performs a scan in the file in order to figure out the number of records.
     *
     * @return the number of records stored in the file.
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
       return blockFile.size();
    }

    /**
     * This method returns the size of the file in bytes.
     *
     * @return the size of the file in bytes
     * @throws IOException if there is an I/O problem during the operation
     */
    public long length() throws IOException {
        return blockFile.length();
    }

    /**
     * This method writes the file metadata
     *
     * @throws IOException
     */
    public void close() throws IOException {
        if (!new File(blockFile.getFileName() + ".mtd").exists()) {
            ObjectOutputStream output;
            output = new ObjectOutputStream(new FileOutputStream(blockFile.getFileName() + ".mtd"));
            output.writeObject(fields);
            output.close();
        }
        blockFile.close();
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
    private class ScanIterator implements Iterator<Record> {

        private long numPages = 0;
        private final byte[] buffer;
        private int currentPageId = 0; // Ajustar para começar do meio do arquivo, ou seja, começar na página central do arquivo
        private Page currentPage;
        private Iterator<Record> recIterator;

        public ScanIterator() throws IOException {
            buffer = new byte[blockFile.getBlockSize()];
            numPages = blockFile.size();
            recIterator = nextPageIterator();
        }

        private Iterator<Record> nextPageIterator() {
            try {
                Iterator<Record> it = null;
                while (currentPageId < numPages) {
                    currentPageId++; // Alterar essa parte para que esse id seja dividido pela metade ( esquerda ou direita )
                    blockFile.read(currentPageId, buffer);
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
}
