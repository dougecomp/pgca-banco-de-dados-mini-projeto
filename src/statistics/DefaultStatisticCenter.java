package statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author joao
 */
public class DefaultStatisticCenter implements StatisticCenter{
    private final HashMap<String, Tally> tallies;
    private final HashMap<String, Count> counts;

    public DefaultStatisticCenter(){
        tallies = new HashMap<>();
        counts = new HashMap<>();
    }

    @Override
    public Tally getTally(String name) {
        Tally tally = tallies.get(name);

        if(tally==null){
            tally = new Tally(name);
            tallies.put(name, tally);
        }
        return tally;
    }

    @Override
    public Count getCount(String name) {
        Count count = counts.get(name);

        if(count==null){
            count = new Count(name);
            counts.put(name, count);
        }
        return count;
    }

    @Override
    public void resetTallies() {
        reset(tallies);
    }

    @Override
    public void resetCounts() {
        reset(counts);
    }

    @Override
    public void resetStatistics(){
        resetTallies();
        resetCounts();
    }

    private void reset(HashMap<String, ? extends StatisticObject> statistics){
        for(StatisticObject obj:statistics.values()){
            obj.reset();
        }
    }

    @Override
    public String status(){
        return getStatus("");
    }
    
    /**
     * The same as getStatus, however it appends a prefix at each result. It
     * is very useful for plotting data using Gnuplot
     * @param fixedName
     * @return
     */
    public String getStatus(String prefix){
        ArrayList<String> results = new ArrayList<>();
        
        for(Tally tally:tallies.values()){
            StringBuilder result = new StringBuilder();

            result.append(prefix); result.append(' ');
            result.append(tally.getName()); result.append(' ');
            result.append(tally.getMean()); result.append(' ');
            if(tally.getObservations()<2){
                result.append("-"); result.append(' ');
            }else{
                result.append(tally.getStdDev()); result.append(' ');
            }
            
            result.append(tally.getMinimum()); result.append(' ');
            result.append(tally.getMaximum()); result.append(' ');            
            result.append(tally.getObservations());
            
            results.add(result.toString());
        }

        for(Count count:this.counts.values()){
            results.add(getResult(prefix, count, 1));
        }
        
        Collections.sort(results);

        return toString(results);
    }

     private static String toString(Collection c){
        StringBuilder result = new StringBuilder();
        for(Object item:c){
            result.append(item.toString());
            result.append("\n");
        }
        return result.toString();
    }

    private String getResult(String fixedName, Count count, double divisor){
        StringBuilder result = new StringBuilder();
        result.append(fixedName); result.append(' ');
        result.append(count.getName()); result.append(' ');
        result.append(count.getValue()/divisor); result.append(' ');
        result.append('-'); result.append(' '); //To be compatible with Tallyes.
        result.append(count.getMinimum()); result.append(' ');
        result.append(count.getMaximum());result.append(' ');
        result.append(count.getObservations());
        return result.toString();
    }

}
