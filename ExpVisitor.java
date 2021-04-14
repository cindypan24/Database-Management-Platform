import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.*;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.*;
import net.sf.jsqlparser.statement.select.*;
import java.util.*;

/**
 * ExpVisitor implements the interface ExpressionVisitor for the functions that
 * are needed for this stage of the project. It has visit functions for each
 * type of Expression so that when the function accept calls visit on any type
 * of Expression it calls the appropriate visit function that takes in that type
 * and saves the value in a global variable instead of returning.
 */
public class ExpVisitor implements ExpressionVisitor {

  Tuple tuple;
  ArrayList<String> schema;
  // Represents the type of outcome. If it is long, it is true. If boolean, it is
  // false.
  // boolean is probably the root. Will be accessed by the select operator
  public boolean bout;
  long lout;

  public ExpVisitor() {

  }

  /**
   * Constructor for tuple and schema
   * 
   * @param t the Tuple to be used
   * @param s The schema as an ArrayList of Strings that represent the column
   *          names
   */
  public ExpVisitor(Tuple t, ArrayList<String> s) {
    tuple = t;
    schema = s;
  }

  /**
   * Not implemented in our SQL.
   * 
   * @param addition representing Addition in expression
   * 
   */
  @Override
  public void visit(Addition addition) {

  }

  /**
   * Not implemented in our SQL.
   * 
   * @param allComparisonExpression representing AllComparisonExpression in
   *                                expression
   */
  @Override
  public void visit(AllComparisonExpression allComparisonExpression) {

  }

  /**
   * Gets and visits the left and right expressions of the AndExpression and
   * calculates the result of logical (left result) AND (right result)
   * 
   * @param andExpression respresenting an AndExpression
   */
  @Override
  public void visit(AndExpression andExpression) {
    Expression left = andExpression.getLeftExpression();
    Expression right = andExpression.getRightExpression();
    // Visits the left expression
    left.accept(this);
    // Saves the outcome to a variable
    boolean loutcome = bout;
    // Visits the right expression
    right.accept(this);
    // Saves the outcome to a variable
    boolean routcome = bout;

    // Equivalent of returning output
    bout = loutcome && routcome;
    if (bout) {
      lout = 1;
    } else {
      lout = 0;
    }
  }

  /**
   * Not implemented in our SQL.
   * 
   * @param anyComparisonExpression representing an instance of
   *                                AnyComparisonExpression
   * @return void
   */
  @Override
  public void visit(AnyComparisonExpression anyComparisonExpression) {

  }

  /**
   * Not implemented in our SQL.
   * 
   * @param between representing Between in expression
   */
  @Override
  public void visit(Between between) {

  }

  /**
   * Not implemented in our SQL.
   * 
   * @param bitwiseAnd representing BitwiseAnd in expression
   */
  @Override
  public void visit(BitwiseAnd bitwiseAnd) {

  }

  /**
   * Not implemented in our SQL.
   * 
   * @param bitwiseOr representing BitwiseOr in expression
   */
  @Override
  public void visit(BitwiseOr bitwiseOr) {

  }

  /**
   * Not implemented in our SQL.
   * 
   * @param bitwiseXor representing BitwiseXor in expression
   */
  @Override
  public void visit(BitwiseXor bitwiseXor) {

  }

  /**
   * Not implemented in our SQL.
   * 
   * @param caseExpression representing CaseExpression in expression. N
   * 
   */
  @Override
  public void visit(CaseExpression caseExpression) {

  }

  /**
   * Visits expressions type Column and gets the value in that Column for the
   * current Tuple.
   * 
   * @param tableColumn represents an instance of Column whose value is wanted
   */
  @Override
  public void visit(Column tableColumn) {
    String colName = tableColumn.getColumnName();
    String wholeColName = tableColumn.getWholeColumnName();

    int colIndex = schema.indexOf(colName);
    if (colIndex == -1) {
      colIndex = schema.indexOf(wholeColName);
    }
    if (colIndex != -1) {
      lout = tuple.getColumn(colIndex);
    }
  }

  /**
   * Not implemented in our SQL
   *
   * @param concat representing Concat in expression
   */
  @Override
  public void visit(Concat concat) {

  }

  /**
   * Not implemented in our SQL
   *
   * @param dateValue representing DateValue in expression
   */
  @Override
  public void visit(DateValue dateValue) {

  }

  /**
   * Not implemented in our SQL
   * 
   * @param division representing Division in expression
   */
  @Override
  public void visit(Division division) {

  }

  /**
   * Not implemented in our SQL
   * 
   * @param doubleValue representing DoubleValue in expression
   */
  @Override
  public void visit(DoubleValue doubleValue) {

  }

  /**
   * Gets and visits the left and right expressions. Checks if the results are
   * equal.
   * 
   * @param equalsTo an instance of EqualsTo
   */
  @Override
  public void visit(EqualsTo equalsTo) {
    Expression left = equalsTo.getLeftExpression();
    Expression right = equalsTo.getRightExpression();

    // Visits the left expression
    left.accept(this);
    long loutcome;
    loutcome = lout;

    right.accept(this);
    long routcome;
    routcome = lout;

    if (loutcome == routcome) {
      lout = 1;
      bout = true;
    } else {
      lout = 0;
      bout = false;
    }
  }

  /**
   * Not implemented in our SQL
   * 
   * @param existsExpression representing ExistsExpression in expression
   */
  @Override
  public void visit(ExistsExpression existsExpression) {

  }

  /**
   * Not implemented in our SQL
   * 
   * @param funcrepresenting Function in expression
   */
  @Override
  public void visit(Function function) {

  }

  /**
   * Gets and visits the left and right expressions and checks if the the result
   * of the left is greater than the result of the right.
   * 
   * @param greaterThan an instance of GreaterThan
   */
  @Override
  public void visit(GreaterThan greaterThan) {
    Expression left = greaterThan.getLeftExpression();
    Expression right = greaterThan.getRightExpression();

    // Visits the left expression
    left.accept(this);
    long loutcome;
    loutcome = lout;

    right.accept(this);
    long routcome;
    routcome = lout;

    if (loutcome > routcome) {
      lout = 1;
      bout = true;
    } else {
      lout = 0;
      bout = false;
    }
  }

  /**
   * Gets and visits the left and right expressions and checks if the the result
   * of the left is greater than or equal to the result of the right.
   * 
   * @param greaterThanEquals an instance of GreaterThanEquals
   */
  @Override
  public void visit(GreaterThanEquals greaterThanEquals) {
    Expression left = greaterThanEquals.getLeftExpression();
    Expression right = greaterThanEquals.getRightExpression();

    // Visits the left expression
    left.accept(this);
    long loutcome;
    loutcome = lout;

    right.accept(this);
    long routcome;
    routcome = lout;

    if (loutcome >= routcome) {
      lout = 1;
      bout = true;
    } else {
      lout = 0;
      bout = false;
    }
  }

  /**
   * Not implemented in our SQL
   * 
   * @param inExpression representing InExpression in expression
   */
  @Override
  public void visit(InExpression inExpression) {

  }

  /**
   * Not implemented in our SQL
   * 
   * @param likeExpression representing LikeExpression in expression
   */
  @Override
  public void visit(InverseExpression inverseExpression) {

  }

  /**
   * Not implemented in our SQL
   * 
   * @param isNullExpression representing IsNullExpression in expression
   */
  @Override
  public void visit(IsNullExpression isNullExpression) {

  }

  /**
   * Not implemented in our SQL
   * 
   * @param jdbcParameter representing JdbcParameter in expression
   */
  @Override
  public void visit(JdbcParameter jdbcParameter) {

  }

  /**
   * Not implemented in our SQL
   * 
   * @param likeExpression representing LikeExpression in expression
   */
  @Override
  public void visit(LikeExpression likeExpression) {

  }

  /**
   * Gets the value of an instance of LongValue as a long.
   * 
   * @param longValue representing LongValue in expression
   */
  @Override
  public void visit(LongValue longValue) {
    lout = longValue.getValue();
  }

  /**
   * Not implemented in our SQL
   * 
   * @param multiplication representing Multiplication in expression
   */
  @Override
  public void visit(Matches matches) {

  }

  /**
   * Gets and visits the left and right expressions. Checks if the left expression
   * is less than the right expression.
   * 
   * @param minorThan The expression of instance MinorThan
   */
  @Override
  public void visit(MinorThan minorThan) {
    Expression left = minorThan.getLeftExpression();
    Expression right = minorThan.getRightExpression();

    // Visits the left expression
    left.accept(this);
    long loutcome;
    loutcome = lout;

    right.accept(this);
    long routcome;
    routcome = lout;

    if (loutcome < routcome) {
      lout = 1;
      bout = true;
    } else {
      lout = 0;
      bout = false;
    }
  }

  /**
   * Gets and visits the left and right expressions. Checks if the left expression
   * is less than or equal to the right expression.
   * 
   * @param minorThanEquals an instance of MinorThanEquals
   */
  @Override
  public void visit(MinorThanEquals minorThanEquals) {
    Expression left = minorThanEquals.getLeftExpression();
    Expression right = minorThanEquals.getRightExpression();

    left.accept(this);
    long loutcome;
    loutcome = lout;

    right.accept(this);
    long routcome;
    routcome = lout;

    if (loutcome <= routcome) {
      lout = 1;
      bout = true;
    } else {
      lout = 0;
      bout = false;
    }
  }

  /**
   * Not implemented in our SQL
   * 
   * @param multiplication representing Multiplication in expression
   */
  @Override
  public void visit(Multiplication multiplication) {

  }

  /**
   * Gets and visits the left and right expressions. Checks if the results of the
   * left expression and right expression are not equal.
   * 
   * @param notEqualsTo an instance of NotEqualsTo
   */
  @Override
  public void visit(NotEqualsTo notEqualsTo) {
    Expression left = notEqualsTo.getLeftExpression();
    Expression right = notEqualsTo.getRightExpression();

    // Visits the left expression
    left.accept(this);
    long loutcome;
    loutcome = lout;

    right.accept(this);
    long routcome;
    routcome = lout;

    if (loutcome != routcome) {
      lout = 1;
      bout = true;
    } else {
      lout = 0;
      bout = false;
    }
  }

  /**
   * Not implemented in our SQL
   * 
   * @param nullValue representing NullValue in expression
   */
  @Override
  public void visit(NullValue nullValue) {

  }

  /**
   * Not implemented in our SQL
   * 
   * @param orExpression representing OrExpression in expression
   */
  @Override
  public void visit(OrExpression orExpression) {

  }

  /**
   * Not implemented in our SQL
   * 
   * @param parenthesis representing Parenthesis in expression
   */
  @Override
  public void visit(Parenthesis parenthesis) {

  }

  /**
   * Not implemented in our SQL
   * 
   * @param stringValue representing StringValue in expression
   */
  @Override
  public void visit(StringValue stringValue) {

  }

  /**
   * Not implemented in our SQL
   * 
   * @param subSelect representing SubSelect in expression
   */
  @Override
  public void visit(SubSelect subSelect) {

  }

  /**
   * Not implemented in our SQL
   * 
   * @param subtraction representing Subtraction in expression
   */
  @Override
  public void visit(Subtraction subtraction) {

  }

  /**
   * Not implemented in our SQL
   * 
   * @param timestampValue representing TimestampValue in expression
   */
  @Override
  public void visit(TimestampValue timestampValue) {

  }

  /**
   * Not implemented in our SQL
   * 
   * @param timeValue representing TimeValue in expression
   */
  @Override
  public void visit(TimeValue timeValue) {

  }

  /**
   * Not implemented in our SQL
   * 
   * @param whenClause representing WhenClause in expression
   */
  @Override
  public void visit(WhenClause whenClause) {

  }

  /**
   * @param t a tuple
   * @param s an ArrayList<String> representing the schema for the tuple
   * @return void
   * 
   *         Sets the tuple and schema for the expression visitor
   */
  public void setTupleSchema(Tuple t, ArrayList<String> s) {
    tuple = t;
    schema = s;
  }

  /**
   * @param t where t is a tuple
   * @return void
   * 
   *         Sets the tuple for the expression visitor
   */
  public void setTuple(Tuple t) {
    tuple = t;
  }

  /**
   * @param s where s is a schema
   * @return void
   * 
   *         Sets the schema for the expression visitor
   */
  public void setSchema(ArrayList<String> s) {
    schema = s;
  }

  /**
   * 
   * @return boolean
   * 
   *         Gets the output for the expression
   */
  public boolean getOutcome() {
    return bout;
  }

}
