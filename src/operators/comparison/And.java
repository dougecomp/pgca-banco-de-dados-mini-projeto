package operators.comparison;

/**
 * This operator compares two logical expression, returning true if both are
 * true and false otherwise
 * @author João B. Rocha-Junior
 */
public class And implements LogicOperator{

    /**
     * This method compares two boolean expressions, returning true if both are
     * true, or false otherwise
     * @param A the A boolean value (true or false)
     * @param B the B boolean value (true or false) 
     * @return true, if both A and B are true, or false otherwise
     */
    @Override
    public boolean compare(boolean A, boolean B) {
        return A && B;
    }    
}
