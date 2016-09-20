package field;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * The field class defines an interface for the record's field
 *
 * @author Robos
 */
public abstract class Field implements Serializable, Cloneable {

    private final String name;
    private final int size; //the size of the field in bytes
    private Comparable value;

    /**
     * Creates a field.
     *
     * @param name the name of the field
     * @param size the size of the field in bytes
     */
    public Field(String name, int size) {
        this(name, size, null);
    }

    /**
     * Creates a field.
     *
     * @param name the name of the field
     * @param size the size of the field in bytes
     * @param value the value stored in the field
     */
    public Field(String name, int size, Comparable value) {
        this.name = name;
        this.size = size;
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * The method returns the value in the given field.
     *
     * @return
     */
    public Comparable getValue() {
        return value;
    }

    /**
     * This method checks and sets the value for the given field
     *
     * @param value the value to be set
     */
    public void setValue(Comparable value) {
        this.value = value;
    }

    /**
     * @return the size in bytes allocated for the field.
     */
    public int getSize() {
        return size;
    }

    /**
     * This method writes the field value in the given output stream
     *
     * @param out the output stream
     * @throws IOException
     */
    public abstract void writeValue(DataOutputStream out) throws IOException;

    /**
     * This method reads the field value from the given input stream
     *
     * @param out the input stream
     * @throws IOException
     */
    public abstract void readValue(DataInputStream in) throws IOException;

    /**
     * This method must be implemented for each instance of field and creates a
     * clone of the given field object
     *
     * @return a clone of the given field object with the same attribute values
     */
    @Override
    public abstract Object clone();

    @Override
    public boolean equals(Object o) {
        if (o instanceof Field) {
            Field other = (Field) o;
            return name.equals(other.getName())
                    && this.size == other.getSize();
        }
        return false;
    }

    @Override
    public String toString() {
        return "[" + name + "(" + value.getClass().getSimpleName().substring(0, 3) + "):" + value + "]";
    }
}
