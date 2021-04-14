
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.schema.*;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class QueryPlanner {
  /**
   * This function returns an Operator that has all required elements and
   * operators passed into it so that it can execute all required steps for the
   * current query.
   * 
   * @param statement The Select instance received from the JSQLParser
   * @return An Operator that executes all required steps
   */
  public Operator getQueryPlan(Select statement, BufferedWriter writer) {
    try {
      Operator last;
      HashMap<String, String> aliases = new HashMap<String, String>();
      PlainSelect body = (PlainSelect) statement.getSelectBody();
      FromItem cur_body = body.getFromItem();
      String alias = cur_body.getAlias();
      ArrayList<String> tables = new ArrayList<String>();

      /**
       * Puts in aliases into a HashMap where the alias is the key and the table name
       * is the value. It puts in the table name as alias if there is no alias.
       */
      if (alias != null) {
        aliases.put(alias, "" + cur_body.toString().split(" ")[0]);
        tables.add(alias);
      } else {
        aliases.put("" + cur_body, "" + cur_body);
        tables.add("" + cur_body);
      }
      List<Join> j = body.getJoins();

      // Handles the join clause of the query inside this if statement
      if (j != null) {
        Expression exp = body.getWhere();
        ArrayList<Join> joins = new ArrayList<Join>(j);

        String table;
        String al;
        // Get the aliases into the Hashmap if there are any used in the join.
        for (Join jo : joins) {
          if (alias != null) {
            table = jo.toString().split(" ")[0];
            al = jo.toString().split(" ")[2];
            aliases.put(al, table);
            tables.add(al);
          } else {
            table = jo.toString();
            tables.add(table);
            aliases.put(table, table);
          }
        }
        // Execute the where part of the query on both tables of join
        WhereVisitor w = new WhereVisitor(tables);
        exp.accept(w);
        String leftTable = tables.get(0);

        // create operators for the left table
        Operator leftop = new ScanOperator(leftTable, aliases, writer);
        if (w.combineExpression(new ArrayList<String>(Arrays.asList(leftTable))) != null) {
          leftop = new SelectOperator(w.combineExpression(new ArrayList<String>(Arrays.asList(leftTable))),
              (ScanOperator) leftop);
        }
        JoinOperator result;
        /**
         * Combine the expressions for the rest of the tables after the first one such
         * that they are evaluated like a left deep tree.
         */
        for (int i = 0; i < tables.size() - 1; i++) {
          String rightTable = tables.get(i + 1);
          Operator rightop = new ScanOperator(rightTable, aliases, writer);
          if (w.combineExpression(new ArrayList<String>(Arrays.asList(rightTable))) != null) {
            rightop = new SelectOperator(w.combineExpression(new ArrayList<String>(Arrays.asList(rightTable))),
                (ScanOperator) rightop);
          }
          ArrayList<String> key = new ArrayList<String>(tables.subList(0, (i + 1) + 1));
          Expression joinConditions = w.combineExpression(key);
          if (i + 1 >= tables.size() - 1) {
            joinConditions = w.combineLastExpression(joinConditions);
          }

          /**
           * Create a join Operator using the left and right operator and the combined
           * expressions/join conditions.
           */
          result = new JoinOperator(leftop, rightop, joinConditions);
          leftop = result;

        }

        Operator output = leftop;
        List<SelectItem> itemList = body.getSelectItems();

        // create a ProjectionOperator if it is not returning all columns.
        if (itemList.get(0) instanceof AllColumns) {
          last = output;
        } else {
          ArrayList<Column> colNames = new ArrayList<Column>();
          for (SelectItem i : itemList) {
            SelectExpressionItem s = (SelectExpressionItem) i;
            Column c = (Column) s.getExpression();
            colNames.add(c);
          }
          output = new ProjectionOperator(colNames, output);
        }
        last = output;

      } else {
        // This is the case without join
        // non-optional scan operator

        Operator op = new ScanOperator(tables.get(0), aliases, writer);

        // create SelectOperator using the WHERE part of the query
        Expression exp = body.getWhere();
        if (exp != null) {
          op = new SelectOperator(exp, (ScanOperator) op);
        }

        List<SelectItem> itemList = body.getSelectItems();
        // no projection if it needs all columns
        if (itemList.get(0) instanceof AllColumns) {
          last = op;
        } else {
          // Create a ProjectionOperator if needed
          ArrayList<Column> colNames = new ArrayList<Column>();
          for (SelectItem i : itemList) {
            SelectExpressionItem s = (SelectExpressionItem) i;
            Column c = (Column) s.getExpression();
            colNames.add(c);
          }
          op = new ProjectionOperator(colNames, op);
        }
        last = op;
      }
      // Create a SortOperator if the query has an ORDER BY clause
      if (body.getOrderByElements() != null) {
        List<OrderByElement> cols = body.getOrderByElements();
        ArrayList<OrderByElement> tr = new ArrayList<OrderByElement>(cols);
        Column current;
        ArrayList<Column> columnNames = new ArrayList<Column>();
        for (int i = 0; i < tr.size(); i++) {
          current = (Column) tr.get(i).getExpression();
          columnNames.add(current);
        }
        last = new SortOperator(columnNames, last);

      }
      Distinct d = body.getDistinct();

      // Create a DuplicateEliminationOperator is the query has a DISTINCT clause
      if (d != null) {

        if (body.getOrderByElements() == null) {
          last = new SortOperator(new ArrayList<Column>(), last);
        }
        last = new DuplicateEliminationOperator(last);
      }
      return last;
    } catch (IOException e) {
      System.out.println(e);
      return null;
    }
  }
}
