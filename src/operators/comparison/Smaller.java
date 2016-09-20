
package operators.comparison;


/**
 * Checks if one value is smaller than the other value
 * @author João B. Rocha-Junior
 */
public class Smaller implements RelOperator{

    /**
     * This method compare two values A and B, returning true is A is smaller
     * than B
     * @param A
     * @param B
     * @return true, if the value of A is smaller than the value of B
     */
    @Override
    public boolean compare(Comparable A, Comparable B) {
        return A.compareTo(B)<0;
    }
    
}
