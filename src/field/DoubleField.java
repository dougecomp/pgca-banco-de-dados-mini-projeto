package field;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DoubleField extends Field {

    /**
     * Creates a Field of Double.
     * 
     * @param name the name of the field
     */
    public DoubleField(String name) {
        super(name, Double.SIZE/8);
    }

    /**
     * Creates a Field of Double.
     * 
     * @param name the name of the field
     * @param value the value stored in the field
     */
    public DoubleField(String name, Comparable value) {
        super(name, Double.SIZE/8, value);
    }

    @Override
    public void setValue(Comparable value){
        super.setValue((double)value);        
    }
    
    @Override
    public void writeValue(DataOutputStream out) throws IOException {
        out.writeDouble((double) getValue());
    }

    @Override
    public void readValue(DataInputStream in) throws IOException {
        setValue(in.readDouble());        
    }
    
    @Override
    public Object clone(){
        return new DoubleField(getName(), getValue());
    }
}
