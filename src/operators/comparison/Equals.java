package operators.comparison;

/**
 * Checks if two values are equals
 * @author João B. Rocha-Junior
 */
public class Equals implements RelOperator{

    /**
     * This method compares two values, returning true if A is equals to B
     * @param A the A value
     * @param B the B value
     * @return true, if the value of A is equals to the value of B
     */
    @Override
    public boolean compare(Comparable A, Comparable B) {
        return A.compareTo(B)==0;
    }
    
}
