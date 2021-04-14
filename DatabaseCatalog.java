import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * The DatabaseCatalog class is used to store informaton about the file paths
 * and schema information of tables. It uses a singleton pattern, so there is
 * only a single instance of the class in the program. This catalog allows the
 * operators to get schema information and file paths by using get methods
 * inside it.
 */
public class DatabaseCatalog {

  private static DatabaseCatalog single_instance = new DatabaseCatalog();

  public BufferedReader br;

  ArrayList<String[]> catalog;

  HashMap<String, ArrayList<String>> schemas = new HashMap<String, ArrayList<String>>();
  HashMap<String, String> paths = new HashMap<String, String>();

  /**
   * Initializes the catalog array list
   */
  private DatabaseCatalog() {
    catalog = new ArrayList<String[]>();
  }

  /**
   * 
   * @return The singleton instance of database catalog
   */
  public static DatabaseCatalog getInstance() {
    return single_instance;
  }

  /**
   * @param inputPath A string representing the input file path provided by the
   *                  user
   * @return void
   * 
   *         Fills the HashMaps which matches table name as the key with filepaths
   *         and schema information as the values
   */
  public void fillCatalogHash(String inputPath) {
    String st;

    try {
      File file = new File(inputPath + File.separator + "db" + File.separator + "schema.txt");
      br = new BufferedReader(new FileReader(file));
    } catch (FileNotFoundException e) {
      System.out.println(e);
    }

    try {
      while ((st = br.readLine()) != null) {
        String[] schema = st.split(" ");
        ArrayList<String> c = new ArrayList<String>(Arrays.asList(schema));
        c.remove(0);
        schemas.put(schema[0], c);
        paths.put(schema[0], inputPath + File.separator + "db" + File.separator + "data" + File.separator + schema[0]);
      }
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  /**
   * @param tbName A string representing the table name
   * @return ArrayList<String> The schema for the table named tbName
   * 
   *         Gets the schema for the table named tbName
   */
  public ArrayList<String> getSchema(String tbName) {
    return schemas.get(tbName);
  }

  /**
   * @param tbName A string representing the table name
   * @return String representing file path
   * 
   *         Gets the file path for the table named tbName
   */
  public String getTbPath(String tbName) {
    return paths.get(tbName);
  }

  /**
   * @return String Representation of Catalog
   * 
   *         Represents the database catalog as a string
   */
  public String toString() {
    String s = "";
    for (int i = 0; i < catalog.size(); i++) {
      String[] temp = catalog.get(i);
      for (String str : temp) {
        s += str + " ";
      }
    }
    return s;
  }

  /**
   * @param tbName A string representing the table name
   * @return String representation of Catalog Hashmaps.
   * 
   *         Returns the String representation of the schema hashmap and the paths
   *         Hashmap;
   */
  public String toStringHash() {
    return schemas.toString() + "\n" + paths.toString();
  }
}
