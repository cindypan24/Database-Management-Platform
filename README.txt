Our top level file is DBMSMain.java

To extract join conditions from the WHERE clause we followed the pdf's recommended strategy
of implementing a left-deep tree where the hypothetical leafs are tables and the hypothetical nodes are sub-WHERE expressions.
We store the relationships between tables and the sub-WHERE expressions inside a hashmap in our WhereVisitor. 
The keys of our hashmap is an arraylist of strings representing table names that are in the select or join operation.
The value is an arraylist of expressions representing the conjuncts inside
the sub-WHERE to be used for that specific select or join operation. To evaluate the join conditions, our WhereVisitor looks
at each conjunct in the initial complete WHERE expression. If the expression
only contains a single table, it inserts this table name as a key to the
hashmap, and adds the expression to the list of expressions that are to be
selected on that table's select operator. If the expression contains two
tables, it checks and see where each table is being joined together in the
left-deep tree and adds the expression to the later join operator key in the
hashmap.

When a select or join operator wants the expression for its tables, the
WHERE visitor stitches the expressions together into a single AND expression.