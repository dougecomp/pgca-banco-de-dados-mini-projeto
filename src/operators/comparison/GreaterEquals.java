package operators.comparison;

/**
 * Checks if one value is greater or Equals the other value
 * @author João B. Rocha-Junior
 */
public class GreaterEquals implements RelOperator{

    /**
     * This method compare two values A and B, returning true if A is greater
     * than B or if A is equals to B
     * @param A the A value
     * @param B the B value
     * @return true, if the value of A is greater than or equals to the  B value
     */
    @Override
    public boolean compare(Comparable A, Comparable B) {
        return A.compareTo(B)>0 || A.compareTo(B)==0;        
    }    
}

