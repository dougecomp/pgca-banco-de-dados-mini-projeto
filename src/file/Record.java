package file;


import field.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Objects;

public final class Record implements Iterable<Field> {

    private LinkedHashMap<String, Field> fields;

    /**
     * Create a record for the given fields. The order in which the fields are
     * inserted is important.
     * @param fields the fields that will compose the record
     */
    public Record(Field... fields) {
        this.fields = new LinkedHashMap<>();
        for (Field field : fields) {
            add(field);
        }
    }

    /**
     * This method returns the field given the field name.
     *
     * @param name the name of the field in the record
     * @return the field with the given name or null, if the field was not found
     */
    public Field get(String name) {
        return fields.get(name);
    }

    /**
     * This method adds a new field. Observe that the order of insertion is
     * important
     *
     * @param field the field to be added in this record.
     */
    public void add(Field field) {
        if (this.fields.containsKey(field.getName())) {
            throw new RuntimeException("The field name must be unique!");
        } else {
            this.fields.put(field.getName(), field);
        }
    }

    /**
     * This method returns the number of fields in the record
     *
     * @return the number of fields of the record
     */
    public int size() {
        return fields.size();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Record) {
            Record other = (Record) o;
            return fields.equals(other.fields);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.fields);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        txt.append('[');
        for (Entry<String, Field> e : fields.entrySet()) {
            txt.append(e.getValue()+" ");
        }        
        txt.append(']');
        return txt.toString();
    }

    @Override
    public Iterator<Field> iterator() {
        return fields.values().iterator();
    }
}
