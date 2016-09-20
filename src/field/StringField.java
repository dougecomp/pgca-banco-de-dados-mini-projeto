package field;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class represents a field of String
 * @author João B. Rocha-Junior
 */
public class StringField extends Field {

    /**
     * Creates a Field of String. The size must be two times of the largest
     * expected size plus 2. This is because it takes two bytes to store the size
     * of the string plus two bytes (char) for each character of the String
     * 
     * @param name the name of the field
     * @param size the maximum size of the field in bytes
     */
    public StringField(String name, int size) {
        super(name, size);
    }

    /**
     * Creates a Field of String. The size must be two times of the largest
     * expected size plus 2. This is because it takes two bytes to store the size
     * of the string plus two bytes (char) for each character of the String
     * 
     * @param name the name of the field
     * @param size the maximum size of the field in bytes
     * @param value the value of the field
     */
    public StringField(String name, int size, Comparable value) {
        super(name, size, value);
    }

    @Override
    public void setValue(Comparable value) {
        if (value instanceof String) {
            super.setValue(value);
        } else {
            throw new RuntimeException("The StringField only accepts String values!");
        }
    }

    @Override
    public void writeValue(DataOutputStream out) throws IOException {
        if(getSize() >= (((String) getValue()).length()*2 + 2)) {
            out.writeShort(((String) getValue()).length());
            out.writeChars((String) getValue());
        }else{
            throw new RuntimeException("The size allocated for the field '"+getName()+
                    "' is not sufficient to store the value.");
        }
    }

    @Override
    public void readValue(DataInputStream in) throws IOException {
        int size = in.readShort();
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < size; i++) {
            buf.append(in.readChar());
        }
        setValue(buf.toString());
    }

    @Override
    public Object clone() {
        return new StringField(getName(), getSize(), getValue());
    }
}
