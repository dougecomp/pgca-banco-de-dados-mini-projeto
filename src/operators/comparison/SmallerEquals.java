package operators.comparison;

/**
 * Checks if one value is smaller or Equals the other value
 * @author João B. Rocha-Junior
 */
public class SmallerEquals implements RelOperator{

    /**
     * This method compare two values A and B, returning true if A is smaller
     * than B or if A is equals to B
     * @param A the A value
     * @param B the B value
     * @return true, if the value of A is smaller than or equals to the  B value
     */
    @Override
    public boolean compare(Comparable A, Comparable B) {
        return A.compareTo(B)<0 || A.compareTo(B)==0;        
    }    
}

