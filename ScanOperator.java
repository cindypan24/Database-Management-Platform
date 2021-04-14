import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.io.BufferedWriter;

/**
 * ScanOperator implements a full scan of the table for the "SELECT * FROM..."
 * query. It reads the wanted table from the text file and its schema, and has a
 * getNextTuple method that creates and returns a new Tuple for each line.
 */
public class ScanOperator extends Operator {

    File file;
    BufferedReader br;
    ArrayList<String> schema;
    DatabaseCatalog catalog;
    String tableName;
    BufferedWriter writer;

    /**
     * Constructor for the ScanOperator class.
     * 
     * @param tbName  The name of the table (As String) to be scanned
     * @param aliases The HashMap of alises (as Strings) of the table to be scanned,
     *                including the name itself as alias
     * @param w       The BufferedWriter to be used to write the output
     * @return a ScanOperator instance (this is a constructor)
     */
    public ScanOperator(String tbName, HashMap<String, String> aliases, BufferedWriter w) throws IOException {
        catalog = DatabaseCatalog.getInstance();
        String pathName = aliases.get(tbName);
        String filePath = catalog.getTbPath(pathName);
        schema = catalog.getSchema(pathName);
        file = new File(filePath);
        br = new BufferedReader(new FileReader(file));
        tableName = tbName;
        writer = w;
    }

    /**
     * @return an ArrayList<String>, each of which are the names of the Columns,
     *         ordered to show their place in a row of the table. The schema is from
     *         the schema.txt being read in the constructor.
     */
    public ArrayList<String> getSchema() {
        return schema;
    }

    /**
     * Gets the name of the table that has been passed into the constructor to be
     * scanned
     * 
     * @return The name of the table to be scanned
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Resets to the beginning of the table by reading from the beginning of the
     * data file.
     */
    public void reset() {
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }

    /**
     * Reads the next line in the data file to create a new Tuple and returns it.
     * 
     * @return The next tuple in the table
     */
    public Tuple getNextTuple() {
        try {
            String line = br.readLine();
            if (line != null) {
                return new Tuple(line);
            } else
                return null;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    /** @return The BufferedWriter used by the ScanOperator. */
    public BufferedWriter getWriter() {
        return writer;
    }

    /**
     * Gets to the end of the table by getting the next Tuple until there are none
     * left.
     */
    public void dump() {
        Tuple t = getNextTuple();
        while (t != null) {
            try {
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