import java.util.*;
import net.sf.jsqlparser.schema.*;
import java.io.*;

/**
 * SortOperator handles queries with the ORDER BY clause. It sorts the output
 * tuples according to the ORDER BY specification and outputs the sorted tuples
 * in ascending order.
 */
public class SortOperator extends Operator {

  ArrayList<Column> columns;
  Operator op;
  ArrayList<String> schema;
  ArrayList<Tuple> tuples = new ArrayList<Tuple>();
  OrderByComparator comp;
  int ind;
  String tbName = "";

  /**
   * Constructor for the SortOperator class.
   * 
   * @param col The names of the columns to be sorted by
   * @param o   The child operator of SortOperator
   * @return a SortOperator instance
   */
  public SortOperator(ArrayList<Column> col, Operator o) {

    // Initialize class fields
    columns = col;
    op = o;
    schema = op.getSchema();
    ind = 0;

    // Create arraylists containing possible ways to name the columns
    ArrayList<String> shortColNames = new ArrayList<String>();
    ArrayList<String> wholeColNames = new ArrayList<String>();
    ArrayList<Table> colTables = new ArrayList<Table>();

    for (int i = 0; i < columns.size(); i++) {
      shortColNames.add(columns.get(i).getColumnName());
      wholeColNames.add(columns.get(i).getWholeColumnName());
      colTables.add(columns.get(i).getTable());
    }

    // Create the list of columns to be sorted by of type Column
    for (int y = 0; y < schema.size(); y++) {
      if (!shortColNames.contains(schema.get(y))) {
        if (!wholeColNames.contains(schema.get(y))) {
          String t = null;
          String c;
          if (schema.get(y).contains(".")) {
            String[] s = schema.get(y).split("\\.");
            t = s[0];
            c = s[1];
          } else {
            c = schema.get(y);
          }
          Column s = new Column();
          Table tb = new Table();
          if (t != null) {
            tb.setName(t);
          }
          s.setTable(tb);
          s.setColumnName(c);
          columns.add(s);
        }
      }
    }

    // Get the list of tuples and sort using Collections.sort with the
    // OrderByComparator
    comp = new OrderByComparator(columns, schema);
    Tuple t = op.getNextTuple();
    while (t != null) {
      tuples.add(t);
      t = op.getNextTuple();
    }
    Collections.sort(tuples, comp);
  }

  /**
   * @return The next tuple in the order it has been sorted by incrementing the
   *         index
   */
  public Tuple getNextTuple() {
    if (ind >= tuples.size()) {
      return null;
    } else {
      Tuple m = tuples.get(ind);
      ind++;
      return m;
    }
  }

  /** Reset to the first tuple by setting the index to 0 */
  public void reset() {
    ind = 0;
  }

  /**
   * @return The BufferedWriter used by the SortOperator.
   */
  public BufferedWriter getWriter() {
    return op.getWriter();
  }

  /**
   * @return An ArrayList<String>, each of which are the names of the Columns,
   *         ordered to show their place in a row of the table. The schema is from
   *         the schema.txt in the constructor.
   */
  public ArrayList<String> getSchema() {
    return schema;
  }

  /** Gets the next tuple and writes it to the file until there are none left */
  public void dump() {
    Tuple t = getNextTuple();
    while (t != null) {
      try {
        BufferedWriter writer = op.getWriter();
        String out = t.toString();
        writer.write(out, 0, out.length());
        writer.newLine();
      } catch (IOException e) {
        System.out.println(e);
      }
      t = getNextTuple();
    }
  }
}
