# SIT CS561 Programming Assignment 2

### Generate reports based on the following queries:
1. For each customer and product, compute (1) the customer's average sale of this product, (2) the customer’s average sale of the other products (3) the average sale of the product for the other customers and.
2. For customer and product, show the average sales before and after each quarter (e.g., for Q2, show average sales of Q1 and Q3. For “before” Q1 and “after” Q4, display <NULL>). The “YEAR” attribute is not considered for this query – for example, both Q1 of 2007 and Q1 of 2008 are considered Q1 regardless of the year.
3. For customer and product, count for each quarter, how many sales of the previous and how many sales of the following quarter had quantities between that quarter’s average sale and minimum sale. Again for this query, the “YEAR” attribute is not considered.

the only SQL statement you’re allowed to use for your program is:

　　select * from sales;

That is, no where clauses, no aggregate functions (e.g., avg, sum, count), etc.

And, you cannot store the ‘sales’ table in memory.

### The following are sample report output (NOTE: the numbers shown below are not the actual aggregate values. You can write simple SQL queries to find the actual aggregate values).

![](https://github.com/qiyunlu/SIT.CS561.DBMS/raw/master/Example.png)

### Make sure that:
1. Character string data (e.g., customer name and product name) are left justified.
2. Numeric data (e.g., Maximum/minimum Sales Quantities) are right justified.
3. The Date fields are in the format of MM/DD/YYYY (i.e., 01/02/2002 instead of 1/1/2002).
