import java.util.Comparator;
import java.util.*;
import net.sf.jsqlparser.schema.*;
import net.sf.jsqlparser.expression.*;

/**
 * OrderByComparator implements the comparator interface to compare two tuples
 */
public class OrderByComparator implements Comparator<Tuple> {
  ArrayList<Column> columns;
  ArrayList<String> schema;

  /**
   * Constructor for the OrderByComparator class.
   * 
   * @param c The list of columns that are to be compared
   * @param s The schema of the tuples being compared
   * @return an OrderbyComparator instance
   */
  public OrderByComparator(ArrayList<Column> c, ArrayList<String> s) {
    columns = c;
    schema = s;
  }

  /**
   * @param a The first tuple to be compared
   * @param b The second tuple to be compared
   * @return the difference of the values in a certain column of the two tuples
   */
  @Override
  public int compare(Tuple a, Tuple b) {
    // Iterate through all of the columns of interest
    for (int i = 0; i < columns.size(); i++) {
      String colName = columns.get(i).getColumnName();
      String wholeColName = columns.get(i).getWholeColumnName();
      int colIndex = schema.indexOf(colName);
      if (colIndex == -1)
        colIndex = schema.indexOf(wholeColName);
      // If the column values are different, return the difference
      if (a.getColumn(colIndex) != b.getColumn(colIndex))
        return a.getColumn(colIndex) - b.getColumn(colIndex);
    }
    return 0;
  }
}
