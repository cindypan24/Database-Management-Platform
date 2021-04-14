import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import net.sf.jsqlparser.expression.*;
import java.io.BufferedWriter;

/**
 * SelectOperator handles queries with the WHERE clause by evaluation the
 * expression for each tuple. It outputs tuples that satisfy the WHERE clause.
 */
public class SelectOperator extends Operator {

  ScanOperator scanner;
  Expression exp;
  DatabaseCatalog catalog;
  ArrayList<String> schema;
  ExpVisitor vis;
  String tableName;

  /**
   * Constructor for the SelectOperator class visits the Expression from the WHERE
   * clause to return only the rows that match the WHERE clause of the query in
   * getNextTuple.
   * 
   * @param e The expression to be used for the restrictions of the SELECT query
   *          which are found in the WHERE part.
   * @param s The ScanOperator which is the child of this SelectOperator and helps
   *          it read from the table
   * @return An instance of SelectOperator (this is a Constructor)
   */
  public SelectOperator(Expression e, ScanOperator s) throws IOException {
    scanner = s;
    scanner.reset();
    exp = e;
    catalog = DatabaseCatalog.getInstance();
    schema = scanner.getSchema();
    vis = new ExpVisitor();
    vis.setSchema(schema);
    tableName = s.getTableName();
  }

  /**
   * Resets to the beginning of the table by reading from the beginning of the
   * data file.
   */
  public void reset() {
    scanner.reset();
  }

  /**
   * @return an ArrayList<String>, each of which are the names of the Columns,
   *         ordered to show their place in a row of the table.
   */
  public ArrayList<String> getSchema() {
    return schema;
  }

  /**
   * Gets the name of the table that has been passed into the constructor to be
   * selected from
   * 
   * @return The name of the table to be selected from
   */
  public String getTableName() {
    return tableName;
  }

  /**
   * Gets the next tuple from the ScanOperator and returns only the wanted rows
   * that match the expression from the WHERE clause
   * 
   * @return The next tuple in the result of the SELECT query
   */
  public Tuple getNextTuple() {
    Tuple t = scanner.getNextTuple();
    if (exp != null) {
      while (t != null) {
        vis.setTupleSchema(t, schema);
        exp.accept(vis);
        if (vis.getOutcome()) {
          return t;
        } else {
          t = scanner.getNextTuple();
        }
      }
      return null;
    } else {
      return t;
    }

  }

  /** @return The BufferedWriter used by the SelectOperator. */
  public BufferedWriter getWriter() {
    return scanner.getWriter();
  }

  /**
   * Gets to the end of the result by getting the next Tuple until there are none
   * left.
   */
  public void dump() {
    Tuple t = getNextTuple();
    while (t != null) {
      try {
        BufferedWriter writer = scanner.getWriter();
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