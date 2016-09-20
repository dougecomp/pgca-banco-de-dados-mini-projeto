package field;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class represents a field of Integer
 * @author João B. Rocha-Junior
 */
public class IntegerField extends Field {

    /**
     * Creates a Field of Integer.
     * 
     * @param name the name of the field
     */
    public IntegerField(String name) {
        super(name, Integer.SIZE/8);
    }

    /**
     * Creates a Field of Integer.
     * 
     * @param name the name of the field
     * @param value the value stored in the field
     */
    public IntegerField(String name, Comparable value) {
        super(name, Integer.SIZE/8, value);
    }

    @Override
    public void setValue(Comparable value){
        super.setValue((int)value);        
    }
    
    @Override
    public void writeValue(DataOutputStream out) throws IOException {
        out.writeInt((int) getValue());
    }

    @Override
    public void readValue(DataInputStream in) throws IOException {
        setValue(in.readInt());        
    }
    
    @Override
    public Object clone(){
        return new IntegerField(getName(), getValue());
    }
}
