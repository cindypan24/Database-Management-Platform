import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.*;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.*;
import net.sf.jsqlparser.statement.select.*;

import java.util.*;

/**
 * WhereVisitor is a visitor pattern implementation where it is accepted by the
 * expression of the WHERE in the SELECT query and it breaks down this WHERE
 * expression into smaller WHERE expressions, to be applied at different points
 * in the left deep tree.
 * 
 * The basis for the sub-WHERE expressions(those to be used at different nodes
 * in the left deep tree) is inside the hashmap. The key is an arraylist of
 * strings representing table names that are in the select or join operation.
 * The value is an arraylist of expressions representing the conjuncts inside
 * the sub-WHERE to be used for that specific select or join operation. It looks
 * at each conjunct in the initial total WHERE expression. If the expression
 * only contains a single table, it inserts this table name as a key to the
 * hashmap, and adds the expression to the list of expressions that are to be
 * selected on that table's select operator. If the expression contains two
 * tables, it checks and see where each table is being joined together in the
 * left deep tree, and adds the expression to the later join operator key in the
 * hashmap.
 * 
 * When a select or join operator wants the expression for it's tables, the
 * WHERE visitor stitches the expression together into a single AND expression.
 */

public class WhereVisitor implements ExpressionVisitor {

  ArrayList<String> tbNames;
  HashMap<ArrayList<String>, ArrayList<Expression>> map = new HashMap<ArrayList<String>, ArrayList<Expression>>();
  Expression express;
  String table;
  long longVal;
  // Contains all expressions not containing any column values
  ArrayList<Expression> nonTable = new ArrayList<Expression>();

  /**
   * @param t ArrayList<String> representing the order tables are being joined in
   *          the left deep tree
   * 
   * 
   *          Constructor for the Where Visitor. Initializes the tbnames
   *          ArrayList.
   */
  public WhereVisitor(ArrayList<String> t) {
    tbNames = t;

  }

  /**
   * @param addition Addition An Addition expression
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(Addition addition) {

  }

  /**
   * @param allComparisonExpression AllComparisonExpression An
   *                                AllComparisonExpression expression
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(AllComparisonExpression allComparisonExpression) {

  }

  /**
   * @param andExpression AndExpression
   * 
   * @return void
   * 
   *         Visits the AndExpression. Then visits its left and right expressions.
   */
  @Override
  public void visit(AndExpression andExpression) {
    Expression left = andExpression.getLeftExpression();
    Expression right = andExpression.getRightExpression();
    // Visits the left expression
    left.accept(this);
    // Visits the right expression
    right.accept(this);
  }

  /**
   * @param anyComparisonExpression AnyComparisonExpression
   * 
   * @return void
   * 
   *         Not implemented in our SQL.
   */
  @Override
  public void visit(AnyComparisonExpression anyComparisonExpression) {

  }

  /**
   * @param between Between
   * 
   * @return void
   * 
   *         Not implemented in our SQL.
   */

  @Override
  public void visit(Between between) {

  }

  /**
   * @param bitwiseAnd BitwiseAnd
   * 
   * @return void
   * 
   *         Not implemented in our SQL.
   */

  @Override
  public void visit(BitwiseAnd bitwiseAnd) {

  }

  /**
   * @param bitwiseOr BitwiseOr
   * 
   * @return void
   * 
   *         Not implemented in our SQL.
   */

  @Override
  public void visit(BitwiseOr bitwiseOr) {

  }

  /**
   * @param bitwiseXor BitwiseXor
   * 
   * @return void
   * 
   *         Not implemented in our SQL.
   */
  @Override
  public void visit(BitwiseXor bitwiseXor) {

  }

  /**
   * @param caseExpression CaseExpression
   * 
   * @return void
   * 
   *         Not implemented in our SQL.
   */
  @Override
  public void visit(CaseExpression caseExpression) {

  }

  /**
   * @param tableColumn Column representing Column in expression
   * 
   * @return void
   * 
   *         Sets the table variable to be equal to the table of this column.
   */
  @Override
  public void visit(Column tableColumn) {
    table = tableColumn.getTable().toString();
  }

  /**
   * @param concat Concat representing Concat
   * 
   * @return void
   * 
   *         Not implemented in our SQL
   */
  @Override
  public void visit(Concat concat) {

  }

  /**
   * @param dateValue DateValue representing DateValue
   * 
   * @return void
   * 
   *         Not implemented in our SQL
   */
  @Override
  public void visit(DateValue dateValue) {

  }

  /**
   * @param division Division representing Division
   * 
   * @return void
   * 
   *         Not implemented in our SQL
   */
  @Override
  public void visit(Division division) {

  }

  /**
   * @param doubleValue DoubleValue representing DoubleValue
   * 
   * @return void
   * 
   *         Not implemented in our SQL
   */
  @Override
  public void visit(DoubleValue doubleValue) {

  }

  /**
   * @param equalsTo representing EqualsTo in expression
   * 
   * @return void
   * 
   *         Visits the left and right expression, and gets the tables within
   *         them. If there is only one table and one number Sailors.A =1, it
   *         represents a select and puts the value into the Hashmap as Sailors =
   *         [Sailors.A=1]. If there are two tables in the expression such as
   *         Sailors.A = Boats.D, it represents a join between the two tables and
   *         is put into the Hashmap as [[Boats,Sailors]=[Sailors.A = Boats.D]]
   */
  @Override
  public void visit(EqualsTo equalsTo) {
    Expression left = equalsTo.getLeftExpression();
    Expression right = equalsTo.getRightExpression();

    // Visits the left expression
    left.accept(this);
    String leftTable = table;
    Long leftLong = longVal;
    // Visits the right expression
    right.accept(this);
    String rightTable = table;
    Long rightLong = longVal;
    // Gets the table indices
    ArrayList<String> newKey = new ArrayList<String>();
    int lindex = -1;
    if (leftTable != null) {
      lindex = tbNames.indexOf(leftTable);

    }

    int rindex = -1;
    if (rightTable != null) {
      rindex = tbNames.indexOf(rightTable);

    }
    // Sets up keys for hashmap
    if (lindex != -1 && rindex == -1) {
      newKey.add(leftTable);
    } else {
      if (rindex != -1 && lindex == -1) {
        newKey.add(rightTable);
      }
    }

    if (lindex != -1 && rindex != -1) {
      if (lindex == rindex) {
        // Same table name
        newKey.add(leftTable);
      } else {
        if (lindex > rindex) {
          // +1 is because exclusive
          newKey = new ArrayList<String>(tbNames.subList(0, lindex + 1));
        } else {
          if (lindex < rindex) {
            newKey = new ArrayList<String>(tbNames.subList(0, rindex + 1));
          }
        }
      }
    }
    // Adds key and expression to Hashmap
    if (newKey.isEmpty() == false) {
      if (map.containsKey(newKey)) {
        ArrayList<Expression> list = map.get(newKey);
        list.add(equalsTo);
        map.put(newKey, list);
      } else {
        ArrayList<Expression> list = new ArrayList<>();
        list.add(equalsTo);
        map.put(newKey, list);
      }
    } else {
      nonTable.add(equalsTo);
    }
  }

  /**
   * @param existsExpression ExistsExpression An ExistsExpression
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(ExistsExpression existsExpression) {

  }

  /**
   * @param function Function An Function
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(Function function) {

  }

  /**
   * @param greaterThan representing GreaterThan in expression
   * 
   * @return void
   * 
   *         Visits the left and right expression, and gets the tables within
   *         them. If there is only one table and one number Sailors.A >1, it
   *         represents a select and puts the value into the Hashmap as Sailors =
   *         [Sailors.A>1]. If there are two tables in the expression such as
   *         Sailors.A > Boats.D, it represents a join between the two tables and
   *         is put into the Hashmap as [[Boats,Sailors]=[Sailors.A > Boats.D]]
   */
  @Override
  public void visit(GreaterThan greaterThan) {
    Expression left = greaterThan.getLeftExpression();
    Expression right = greaterThan.getRightExpression();

    // Visits the left expression
    left.accept(this);
    String leftTable = table;
    Long leftLong = longVal;
    // Visits the right expression
    right.accept(this);
    String rightTable = table;
    Long rightLong = longVal;
    // Gets table indices
    ArrayList<String> newKey = new ArrayList<String>();
    int lindex = -1;
    if (leftTable != null) {
      lindex = tbNames.indexOf(leftTable);

    }

    int rindex = -1;
    if (rightTable != null) {
      rindex = tbNames.indexOf(rightTable);

    }
    // Creates key for hashmap
    if (lindex != -1 && rindex == -1) {
      newKey.add(leftTable);
    } else {
      if (rindex != -1 && lindex == -1) {
        newKey.add(rightTable);
      }
    }
    // Continues creating key for hashmap
    if (lindex != -1 && rindex != -1) {
      if (lindex == rindex) {
        // Same table name
        newKey.add(leftTable);
      } else {
        if (lindex > rindex) {
          // +1 is because exclusive
          newKey = new ArrayList<String>(tbNames.subList(0, lindex + 1));
        } else {
          if (lindex < rindex) {
            newKey = new ArrayList<String>(tbNames.subList(0, rindex + 1));
          }
        }
      }
    }
    // Puts key and value in Hashmap
    if (newKey.isEmpty() == false) {
      if (map.containsKey(newKey)) {
        ArrayList<Expression> list = map.get(newKey);
        list.add(greaterThan);
        map.put(newKey, list);
      } else {
        ArrayList<Expression> list = new ArrayList<>();
        list.add(greaterThan);
        map.put(newKey, list);
      }
    } else {
      nonTable.add(greaterThan);
    }

  }

  /**
   * @param greaterThanEquals representing GreaterThanEquals in expression
   * 
   * @return void
   * 
   *         Visits the left and right expression, and gets the tables within
   *         them. If there is only one table and one number Sailors.A >=1, it
   *         represents a select and puts the value into the Hashmap as Sailors =
   *         [Sailors.A>=1]. If there are two tables in the expression such as
   *         Sailors.A >= Boats.D, it represents a join between the two tables and
   *         is put into the Hashmap as [[Boats,Sailors]=[Sailors.A >= Boats.D]]
   */
  @Override
  public void visit(GreaterThanEquals greaterThanEquals) {
    Expression left = greaterThanEquals.getLeftExpression();
    Expression right = greaterThanEquals.getRightExpression();

    // Visits the left expression
    left.accept(this);
    String leftTable = table;
    Long leftLong = longVal;
    // Visits the right expression
    right.accept(this);
    String rightTable = table;
    Long rightLong = longVal;
    // Gets the indices for the table
    ArrayList<String> newKey = new ArrayList<String>();
    int lindex = -1;
    if (leftTable != null) {
      lindex = tbNames.indexOf(leftTable);

    }

    int rindex = -1;
    if (rightTable != null) {
      rindex = tbNames.indexOf(rightTable);

    }
    // Begins creating key for hashmap
    if (lindex != -1 && rindex == -1) {
      newKey.add(leftTable);
    } else {
      if (rindex != -1 && lindex == -1) {
        newKey.add(rightTable);
      }
    }
    // Continues creating key
    if (lindex != -1 && rindex != -1) {
      if (lindex == rindex) {
        // Same table name
        newKey.add(leftTable);
      } else {
        if (lindex > rindex) {
          // +1 is because exclusive
          newKey = new ArrayList<String>(tbNames.subList(0, lindex + 1));
        } else {
          if (lindex < rindex) {
            newKey = new ArrayList<String>(tbNames.subList(0, rindex + 1));
          }
        }
      }
    }
    // Adds to HashMap
    if (newKey.isEmpty() == false) {
      if (map.containsKey(newKey)) {
        ArrayList<Expression> list = map.get(newKey);
        list.add(greaterThanEquals);
        map.put(newKey, list);
      } else {
        ArrayList<Expression> list = new ArrayList<>();
        list.add(greaterThanEquals);
        map.put(newKey, list);
      }
    } else {
      nonTable.add(greaterThanEquals);
    }
  }

  /**
   * @param inExpression InExpression An InExpression
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(InExpression inExpression) {

  }

  /**
   * @param inverseExpression InverseExpression An InverseExpression
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(InverseExpression inverseExpression) {

  }

  /**
   * @param isNullExpression IsNullExpression An isNullExpression
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(IsNullExpression isNullExpression) {

  }

  /**
   * @param jdbcParameter JdbcParameter A JdbcParameter
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(JdbcParameter jdbcParameter) {

  }

  /**
   * @param likeExpression LikeExpression A LikeExpression
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(LikeExpression likeExpression) {

  }

  /**
   * @param longValue LongValue representing LongValue in expression
   * 
   * @return void
   * 
   *         Sets the longVal variable to be equal to this long Value
   */
  @Override
  public void visit(LongValue longValue) {
    table = null;
    longVal = longValue.getValue();
  }

  @Override
  public void visit(Matches matches) {

  }

  /**
   * @param minorThan representing minorThan in expression
   * 
   * @return void
   * 
   *         Visits the left and right expression, and gets the tables within
   *         them. If there is only one table and one number Sailors.A <1, it
   *         represents a select and puts the value into the Hashmap as Sailors =
   *         [Sailors.A<1]. If there are two tables in the expression such as
   *         Sailors.A < Boats.D, it represents a join between the two tables and
   *         is put into the Hashmap as [[Boats,Sailors]=[Sailors.A < Boats.D]]
   */
  @Override
  public void visit(MinorThan minorThan) {
    Expression left = minorThan.getLeftExpression();
    Expression right = minorThan.getRightExpression();

    // Visits the left expression
    left.accept(this);
    String leftTable = table;
    Long leftLong = longVal;
    // Visits the right expression
    right.accept(this);
    String rightTable = table;
    Long rightLong = longVal;
    // Gets join indicies for table
    ArrayList<String> newKey = new ArrayList<String>();
    int lindex = -1;
    if (leftTable != null) {
      lindex = tbNames.indexOf(leftTable);

    }

    int rindex = -1;
    if (rightTable != null) {
      rindex = tbNames.indexOf(rightTable);

    }
    // Begins creating key for HashMap
    if (lindex != -1 && rindex == -1) {
      newKey.add(leftTable);
    } else {
      if (rindex != -1 && lindex == -1) {
        newKey.add(rightTable);
      }
    }

    if (lindex != -1 && rindex != -1) {
      if (lindex == rindex) {
        // Same table name
        newKey.add(leftTable);
      } else {
        if (lindex > rindex) {
          // +1 is because exclusive
          newKey = new ArrayList<String>(tbNames.subList(0, lindex + 1));
        } else {
          if (lindex < rindex) {
            newKey = new ArrayList<String>(tbNames.subList(0, rindex + 1));
          }
        }
      }
    }
    // Adds key and value to HashMap
    if (newKey.isEmpty() == false) {
      if (map.containsKey(newKey)) {
        ArrayList<Expression> list = map.get(newKey);
        list.add(minorThan);
        map.put(newKey, list);
      } else {
        ArrayList<Expression> list = new ArrayList<>();
        list.add(minorThan);
        map.put(newKey, list);
      }
    } else {
      nonTable.add(minorThan);
    }
  }

  /**
   * @param minorThanequals representing MinorThanEquals in expression
   * 
   * @return void
   * 
   *         Visits the left and right expression, and gets the tables within
   *         them. If there is only one table and one number Sailors.A <=1, it
   *         represents a select and puts the value into the Hashmap as Sailors =
   *         [Sailors.A<=1]. If there are two tables in the expression such as
   *         Sailors.A <= Boats.D, it represents a join between the two tables and
   *         is put into the Hashmap as [[Boats,Sailors]=[Sailors.A <= Boats.D]]
   */
  @Override
  public void visit(MinorThanEquals minorThanEquals) {
    Expression left = minorThanEquals.getLeftExpression();
    Expression right = minorThanEquals.getRightExpression();

    // Visits the left expression
    left.accept(this);
    String leftTable = table;
    Long leftLong = longVal;
    // Visits the right expression
    right.accept(this);
    String rightTable = table;
    Long rightLong = longVal;
    // Gets table indicies
    ArrayList<String> newKey = new ArrayList<String>();
    int lindex = -1;
    if (leftTable != null) {
      lindex = tbNames.indexOf(leftTable);

    }

    int rindex = -1;
    if (rightTable != null) {
      rindex = tbNames.indexOf(rightTable);

    }
    // Begin creating key for table
    if (lindex != -1 && rindex == -1) {
      newKey.add(leftTable);
    } else {
      if (rindex != -1 && lindex == -1) {
        newKey.add(rightTable);
      }
    }

    if (lindex != -1 && rindex != -1) {
      if (lindex == rindex) {
        // Same table name
        newKey.add(leftTable);
      } else {
        if (lindex > rindex) {
          // +1 is because exclusive
          newKey = new ArrayList<String>(tbNames.subList(0, lindex + 1));
        } else {
          if (lindex < rindex) {
            newKey = new ArrayList<String>(tbNames.subList(0, rindex + 1));
          }
        }
      }
    }
    // Add to Hashmap
    if (newKey.isEmpty() == false) {
      if (map.containsKey(newKey)) {
        ArrayList<Expression> list = map.get(newKey);
        list.add(minorThanEquals);
        map.put(newKey, list);
      } else {
        ArrayList<Expression> list = new ArrayList<>();
        list.add(minorThanEquals);
        map.put(newKey, list);
      }
    } else {
      nonTable.add(minorThanEquals);
    }
  }

  /**
   * @param multiplication Multiplication A Multiplication
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(Multiplication multiplication) {

  }

  /**
   * @param notEqualsTo representing NotEqualsTo in expression
   * 
   * @return void
   * 
   *         Visits the left and right expression, and gets the tables within
   *         them. If there is only one table and one number Sailors.A !=1, it
   *         represents a select and puts the value into the Hashmap as Sailors =
   *         [Sailors.A!=1]. If there are two tables in the expression such as
   *         Sailors.A != Boats.D, it represents a join between the two tables and
   *         is put into the Hashmap as [[Boats,Sailors]=[Sailors.A != Boats.D]]
   */
  @Override
  public void visit(NotEqualsTo notEqualsTo) {
    Expression left = notEqualsTo.getLeftExpression();
    Expression right = notEqualsTo.getRightExpression();

    // Visits the left expression
    left.accept(this);
    String leftTable = table;
    Long leftLong = longVal;
    // Visits the right expression
    right.accept(this);
    String rightTable = table;
    Long rightLong = longVal;
    // Gets table indicies
    ArrayList<String> newKey = new ArrayList<String>();
    int lindex = -1;
    if (leftTable != null) {
      // newKey.add(leftTable);
      lindex = tbNames.indexOf(leftTable);

    }

    int rindex = -1;
    if (rightTable != null) {
      rindex = tbNames.indexOf(rightTable);

    }
    // Begins creating key from table names
    if (lindex != -1 && rindex == -1) {
      newKey.add(leftTable);
    } else {
      if (rindex != -1 && lindex == -1) {
        newKey.add(rightTable);
      }
    }

    if (lindex != -1 && rindex != -1) {
      if (lindex == rindex) {
        // Same table name
        newKey.add(leftTable);
      } else {
        if (lindex > rindex) {
          // +1 is because exclusive
          newKey = new ArrayList<String>(tbNames.subList(0, lindex + 1));
        } else {
          if (lindex < rindex) {
            newKey = new ArrayList<String>(tbNames.subList(0, rindex + 1));
          }
        }
      }
    }
    // Adds to HashMap
    if (newKey.isEmpty() == false) {
      if (map.containsKey(newKey)) {
        ArrayList<Expression> list = map.get(newKey);
        list.add(notEqualsTo);
        map.put(newKey, list);
      } else {
        ArrayList<Expression> list = new ArrayList<>();
        list.add(notEqualsTo);
        map.put(newKey, list);
      }
    } else {
      nonTable.add(notEqualsTo);
    }
  }

  /**
   * @param nullValue NullValue A NullValue
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(NullValue nullValue) {

  }

  /**
   * @param orExpression OrExpression An OrExpression
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(OrExpression orExpression) {

  }

  /**
   * @param Parenthesis Parenthesis A Parenthesis
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(Parenthesis Parenthesis) {

  }

  /**
   * @param stringValue StringValue A StringValue
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(StringValue stringValue) {

  }

  /**
   * @param subSelect SubSelect A SubSelect
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(SubSelect subSelect) {

  }

  /**
   * @param subtraction Subtraction A Subtraction
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(Subtraction subtraction) {

  }

  /**
   * @param timestampeValue TimestampValue A TimestampValue
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(TimestampValue timestampValue) {

  }

  /**
   * @param timeValue TimeValue A TimeValue
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(TimeValue timeValue) {

  }

  /**
   * @param whenClause WhenClause A WhenClause
   * @return void
   * 
   *         Not implemented for our SQL
   */
  @Override
  public void visit(WhenClause whenClause) {

  }

  /**
   * @param
   * @return void
   * 
   *         Prints out the hashmap
   */
  public void printHashMap() {
    System.out.println(map.toString());
  }

  /**
   * @param tbNames ArrayList<String> of the table names
   * @return And Expression representing the WHERE expression for the select or
   *         join operator
   * 
   *         Goes to the Hashmap and retrieves the ArrayList<Expressions> for a
   *         specific select or Join operator. It then combines the array list
   *         into a single AND expression which is returned.
   */
  public Expression combineExpression(ArrayList<String> tbNames) {
    Expression leftExp;
    Expression rightExp;
    AndExpression result = new AndExpression();
    // Gets the list of expressions for the operator
    if (map.containsKey(tbNames)) {
      ArrayList<Expression> exps = map.get(tbNames);
      if (!exps.isEmpty()) {
        leftExp = exps.get(0);
        // Combines all the expressions
        for (int i = 0; i < exps.size() - 1; i++) {
          rightExp = exps.get(i + 1);
          result.setLeftExpression(leftExp);
          result.setRightExpression(rightExp);
          leftExp = result;

        }
        return leftExp;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  /**
   * @param e Expression is the sub-WHERE
   * @return And Expression representing the WHERE expression for the select or
   *         join operator
   * 
   *         Goes to the ArrayList<Expression> representing the expressions which
   *         only contain integers. Add them together into an AND expression and
   *         then combine it with expression e.
   */
  public Expression combineLastExpression(Expression e) {
    Expression leftExp;
    Expression rightExp;
    AndExpression result = new AndExpression();
    if (!nonTable.isEmpty()) {
      leftExp = nonTable.get(0);
      // Combines the expression
      for (int i = 0; i < nonTable.size() - 1; i++) {
        rightExp = nonTable.get(i + 1);
        result.setLeftExpression(leftExp);
        result.setRightExpression(rightExp);
        leftExp = result;
      }
      // Combines the non-table expression with e
      result.setLeftExpression(leftExp);
      result.setRightExpression(e);
      return result;
    } else {
      return e;
    }
  }
}
