import java.io.*;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.schema.*;

/**
 * ProjectionOperator outputs the columns that are specified after the SELECT
 * clause in the order they are listed. If "SELECT *", it outputs every column.
 */
public class ProjectionOperator extends Operator {

  Operator operator;
  ArrayList<String> oldSchema;
  ArrayList<String> newSchema = new ArrayList<String>();
  ArrayList<Integer> colIndices;

  /**
   * Constructor for the ProjectionOperator class.
   * 
   * @param col The names of the columns to be outputted
   * @param o   The child operator of ProjectionOperator
   * @return a ProjectionOperator instance
   */
  public ProjectionOperator(ArrayList<Column> col, Operator o) throws IOException {
    operator = o;
    operator.reset();
    oldSchema = operator.getSchema();
    for (Column c : col) {
      newSchema.add(c.getWholeColumnName());
    }
    // Stores the indicies of the columns being selected in colIndices
    colIndices = new ArrayList<Integer>();
    for (int i = 0; i < col.size(); i++) {
      String colName = col.get(i).getColumnName();
      String wholeColName = col.get(i).getWholeColumnName();
      int colIndex = oldSchema.indexOf(colName);
      if (colIndex == -1) {
        colIndex = oldSchema.indexOf(wholeColName);
      }
      colIndices.add(colIndex);
    }
  }

  /** Reset to the beginning of the tuples by setting ind to 0 */
  public void reset() {
    operator.reset();
  }

  /**
   * @return an ArrayList<String>, each of which are the names of the Columns,
   *         ordered to show their place in a row of the table. The schema is from
   *         the schema.txt in the constructor.
   */
  public ArrayList<String> getSchema() {
    return newSchema;
  }

  /**
   * @return The next tuple using only the columns that are being selected
   */
  public Tuple getNextTuple() {
    Tuple t = operator.getNextTuple();
    ArrayList<Integer> list = new ArrayList<>();
    if (t != null) {
      // Iterate through the selected columns and add those values to a new tuple
      for (int i : colIndices) {
        list.add(t.getColumn(i));
      }
      return new Tuple(list);
    }
    return t;

  }

  /** @return The BufferedWriter used by the ProjectionOperator. */
  public BufferedWriter getWriter() {
    return operator.getWriter();
  }

  /** Gets the next tuple and writes it to the file until there are none left */
  public void dump() {
    Tuple t = getNextTuple();
    while (t != null) {
      try {
        BufferedWriter writer = operator.getWriter();
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