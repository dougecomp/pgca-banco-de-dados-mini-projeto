package statistics;

/**
 * The main class of the Statistic Center it collects data about events.
 *
 * @author joao
 */
public interface StatisticCenter {

    /**
     * The tally is used to store data that has more than one value and whose
     * the average and standard deviation is important.
     *
     * @param name a tally with the given name is created if it does not exists
     * @return the tally
     */
    public Tally getTally(String name);

    /**
     * The count is used for storing normal counts, for example: number of page
     * faults.
     *
     * @param name a count with the given name is created if it does not exists
     * @return the count
     */
    public Count getCount(String name);

    /**
     * Reset the tallies.
     */
    public void resetTallies();

    /**
     * Reset the counts
     */
    public void resetCounts();

    /**
     * Should clean all the data in the statistic center.
     */
    public void resetStatistics();
    
    /**
     * Returns a summary of all data in the statistic center.
     * @return a string with all parameters in the statistic center
     */
    public String status();
}
