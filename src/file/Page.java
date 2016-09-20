package file;


import field.Field;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * The page stores an array of fields and an int value indicating the number of
 * fields stored in the array
 *
 * @author Robos
 */
class Page implements Iterable<Record>{

    private final int pageSize;    
    private final int recordSize;
    private final Record[] records;
    private int size=0;

    public Page(int pageSize, Field... fields) {
        this.recordSize = getRecordSize(fields);        
        this.pageSize = pageSize;
        this.records = new Record[(pageSize-2)/recordSize];
    }

    private int getRecordSize(Field... fields) {
        int fieldsSize = 0;
        for (Field field : fields) {
            fieldsSize += field.getSize();
        }
        return fieldsSize;
    }

    public void insert(Record record) {
        if (!isFull()) {
            records[size]=record;
            size++;
        }
    }

  /**
     * This method removes all records that match the fieldName and value. 
     *
     * @param fieldName the field name to be compared with
     * @param value the value that satisfies the search criteria
     * @return true if at least one record in the page was removed
     * @throws IOException
     */
    public boolean remove(String fieldName, Comparable value) {
        boolean atLeastOneRemoved = false;
        for(int i=0;i<size;i++){
            if(records[i].get(fieldName).getValue().equals(value)){
                records[i]=records[size-1];
                size--;
                i--;
                atLeastOneRemoved = true;
            }
        }        
        return atLeastOneRemoved;
    }

    /**
     * This method returns the number of records in the page.
     * @return the number of records in the page.
     */
    public int size() {
        return size;
    }

    /**
     * @return true, if there is no more space to store another record
     */
    public boolean isFull() {
        //Short.SIZE/8 represents the 2 bytes allocated to store the number of records
        return (pageSize - Short.SIZE/8 - (size*recordSize)) < recordSize;
    }

    /**
     * Converts a Page object in a byte array.
     *
     * @return the byteArray of the page
     * @throws IOException
     */
    public byte[] toByteArray() throws IOException {        
        ByteArrayOutputStream byteArrayOutput = new MyByteArrayOutputStream(pageSize);
        DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);

        dataOutput.writeShort(size()); //writes the nummber of records

        for (int i=0;i<size;i++) {
            for (Field field : records[i]) {
                field.writeValue(dataOutput);//only the field value is stored!
            }
        }

        dataOutput.close();

        return byteArrayOutput.toByteArray();
    }

    /**
     * This method creates a Page object from given byte[] and fields specification.
     * @param bytes the byte array used to produce the Page object
     * @param fields the fields used to produce records of the Page.
     * @return the Page object created
     * @throws IOException 
     */
    public static Page createPage(byte[] bytes, Field... fields) throws IOException {
        Page page = new Page(bytes.length, fields);        
        ByteArrayInputStream byteArrayInput = new ByteArrayInputStream(bytes);
        DataInputStream dataInput = new DataInputStream(byteArrayInput);

        int numRecords = dataInput.readShort();
        for (int i = 0; i < numRecords; i++) {
            Record rec = new Record();
            for (int v = 0; v < fields.length; v++) {
                Field field = (Field) fields[v].clone();
                field.readValue(dataInput); // replaces the field value
                rec.add(field);
            }
            page.insert(rec);
        }
        dataInput.close();
        return page;
    }

    /**
     * This method returns an iterator over the records of the Page.
     * @return the iterator over the records.
     */
    @Override
    public Iterator<Record> iterator() {
        return new Iterator<Record>(){
            private int indexNext = 0;
            @Override
            public boolean hasNext() {
                return indexNext < size;
            }

            @Override
            public Record next() {
                return records[indexNext++];
            }      

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet."); 
            }
        };
    }
    
    
    /**
     * Class created to override the method toByteArray, avoiding to create
     * a new array.
     */
    private class MyByteArrayOutputStream extends ByteArrayOutputStream{
        public MyByteArrayOutputStream(int size) {
            super(size);
        }
        @Override
        public synchronized byte[] toByteArray() {
            return super.buf;
        }        
    }
}
