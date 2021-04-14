import java.util.*;
import java.io.BufferedWriter;

/**
 * Operator is the abstract class for all operations performed by queries
 */
public abstract class Operator {

    /**
     * @return The schema for the corresponding table being operated on
     */
    ArrayList<String> getSchema() {
        return null;
    }

    /**
     * @return The BufferedWriter of operator
     */
    BufferedWriter getWriter() {
        return null;
    }

    /**
     * Reset the operator
     */
    void reset() {
    }

    /**
     * @return The next Tuple of interest
     */
    Tuple getNextTuple() {
        return null;
    }

    /**
     * Gets all tuples of interest and writes to output files
     */
    void dump() {
    }

}