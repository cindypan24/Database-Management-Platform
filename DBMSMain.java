import java.io.*;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.*;

public class DBMSMain {

  private static final String queriesFile = "queries.sql";

  public static void main(String[] args) {
    // Assigns the user input
    String input = args[0];
    String output = args[1];

    // Create an instance of the database catalog
    DatabaseCatalog catalog = DatabaseCatalog.getInstance();
    catalog.fillCatalogHash(input);

    try {

      // Use the JSqlParser to read in queries
      CCJSqlParser parser = new CCJSqlParser(new FileReader(input + File.separator + "queries.sql"));
      Statement statement;
      QueryPlanner planner = new QueryPlanner();
      int i = 1;
      // Read from queries.sql until there are no more queries
      while ((statement = parser.Statement()) != null) {
        // Initializes writing queries to files
        File newQuery = new File(output + File.separator + "query" + i);
        FileWriter fileWriter = new FileWriter(newQuery);
        BufferedWriter writer = new BufferedWriter(fileWriter);
        try {
          // Parses query
          Select select = (Select) statement;
          // Initializes operator and gets query plan to execute query
          Operator op = planner.getQueryPlan(select, writer);
          // Prints to file
          op.dump();
        } catch (Exception e) {
          e.printStackTrace();
        }
        writer.close();
        i++;
      }
    } catch (Exception e) {
      System.err.println("Exception occurred during parsing");
      e.printStackTrace();
    }

  }
}