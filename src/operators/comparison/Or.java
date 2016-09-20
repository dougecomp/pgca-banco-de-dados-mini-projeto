package operators.comparison;

/**
 * This operator compares two logical expression, returning true if at least
 * one of them are true.
 * @author João B. Rocha-Junior
 */
public class Or implements LogicOperator{

    /**
     * This method compares two boolean expressions, returning true if at least
     * one of them are true
     * @param A the A boolean value (true or false)
     * @param B the B boolean value (true or false) 
     * @return true, if  A or B are true, false otherwise
     */
    @Override
    public boolean compare(boolean A, boolean B) {
        return A || B;
    }    
}
