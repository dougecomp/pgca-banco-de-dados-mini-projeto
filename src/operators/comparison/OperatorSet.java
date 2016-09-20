package operators.comparison;

/**
 * This class keeps instances of all operators, avoiding creating several instances
 * and simplifying the programming.
 * @author João B. Rocha-Junior
 */
public class OperatorSet {
    /**
     * The instance of the And operator
     */
    public static final And and = new And();
    
    /**
     * The instance of the Or operator
     */    
    public static final Or or = new Or();
    
    /**
     * The instance of the Equals operator
     */    
    public static final Equals eq = new Equals();
    
    /**
     * The instance of the NotEquals (different) operator
     */    
    public static final NotEquals ne = new NotEquals();
    
    /**
     * The instance of the Greater  operator
     */    
    public static final Greater gt = new Greater();
    
    /**
     * The instance of the GreaterEquals  operator
     */    
    public static final GreaterEquals gte = new GreaterEquals();
        
    /**
     * The instance of the Smaller  operator
     */    
    public static final Smaller st = new Smaller();
    
    /**
     * The instance of the GreaterEquals  operator
     */    
    public static final SmallerEquals ste = new SmallerEquals();
}
