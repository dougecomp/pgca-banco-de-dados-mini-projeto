
package operators.comparison;


/**
 * Checks if one value is greater than the other value
 * @author João B. Rocha-Junior
 */
public class Greater implements RelOperator{

    /**
     * This method compare two values A and B, returning true is A is greater
     * than B
     * @param A
     * @param B
     * @return true, if the value of A is greater than the value of B
     */
    @Override
    public boolean compare(Comparable A, Comparable B) {
        return A.compareTo(B)>0;
    }
    
}
