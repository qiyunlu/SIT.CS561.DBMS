import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class R3 {

    /*
     * header comments:
     * 
     * a. General instructions on how to execute my program
     *   (1) Download the jdbc postgresql jar file at https://jdbc.postgresql.org/download.html
     *   (2) Open eclipse and create a JAVA project 'DBMS CS 561'.
     *   (3) Right click on project.
     *   (4) Go to Build Path -> Configure Build Path.
     *   (5) In the Libraries section, click 'Add External Jars' and  add the postgresql jar file which was downloaded in step 1.
     *   (6) Add file to the src folder (Default package) of the Eclipse project.
     *   (7) Right click on filename.
     *   (8) Select Run As -> Java Application.
     *   (9) Check the Output in the eclipse console.
     * 
     * b. Justification of my choice of data structures for my program
     *   I use arrays to store information for each combination because arrays are very flexible for random access and the length of the information is fixed.
     *   I use an ArrayList to hold all combinations' arrays because the ArrayList can change its length to facilitate the insertion of new arrays.
     *   For each combination, I store 4 quarters' information(average etc.) together in one separate ArrayList.
     * 
     * c. A detailed description of the algorithm of my program
     *   (1) First scan, I traverse each rows and get all combination of cust and prod and calculate its min value and avg value in each quarter.
     *   (2) Second scan, for each combination and each quarter I count the number of quant which meet the conditions.
     * 
     */

    public static void main(String[] args) {

        // need 2 empty spaces in an array
        int space = 2;
        // stores each array
        ArrayList<String[]> list = new ArrayList<String[]>();
        ArrayList<int[]> ilist = new ArrayList<int[]>();
        
        // the user name, password and database name of postgreSQL
        String usr ="postgres";
        String pwd ="26892681147";
        String url ="jdbc:postgresql://localhost:5432/CS561";

        // load the class, and initialize it
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Loading successfully.");
        }
        catch(Exception e) {
            System.out.println("Loading unsuccessfully.");
            e.printStackTrace();
        }

        try {
            Connection conn = DriverManager.getConnection(url, usr, pwd);
            System.out.println("Connecting server successfully.\n");

            Statement stat = conn.createStatement();

            // first scan, get all combination of cust, prod, quarter and its min and avg
            ResultSet rs = stat.executeQuery("SELECT * FROM sales");
            while (rs.next()) {
                
                // get parameters of a row
                String cust = rs.getString("cust");
                String prod = rs.getString("prod");
                String month = rs.getString("month");
                String quant = rs.getString("quant");
                
                // get quarter
                int Q = 0;
                if(month.equals("1") || month.equals("2") || month.equals("3")) {
                    Q = 1;
                }
                else if(month.equals("4") || month.equals("5") || month.equals("6")) {
                    Q = 2;
                }
                else if(month.equals("7") || month.equals("8") || month.equals("9")) {
                    Q = 3;
                }
                else {
                    Q = 4;
                }
                
                // notInList is a flag to show whether someone's data is in the list or not
                boolean notInList = true;
                // to see where is someone's data in the list
                int inWhere = -1;
                for(int i = 0; i < list.size(); i++) {
                    String[] arr = list.get(i);
                    if(arr[0].equals(cust) && arr[1].equals(prod)) {
                        notInList = false;
                        inWhere = i;
                        break;
                    }
                }
                                
                if(notInList) {
                    // someone's data is not in the list. Just add the data in.
                    String[] arr = new String[space];
                    arr[0] = cust;      // CUSTOMER
                    arr[1] = prod;      // PRODUCT
                    list.add(arr);
                    
                    int[] iarr = new int[30];
                    for(int i = 0; i < 30; i++) {
                        iarr[i] = 0;
                    }
                    iarr[Q*5] = Integer.parseInt(quant);
                    iarr[Q*5+1] += Integer.parseInt(quant);
                    iarr[Q*5+2]++;
                    ilist.add(iarr);
                }
                else {
                    // someone's data has already been in the list. Just update the data.
                    int[] iarr = ilist.get(inWhere);
                    if(iarr[Q*5] == 0) {
                        iarr[Q*5] = Integer.parseInt(quant);
                        iarr[Q*5+1] += Integer.parseInt(quant);
                        iarr[Q*5+2]++;
                    }
                    else {
                        iarr[Q*5] = Math.min(Integer.parseInt(quant), iarr[Q*5]);
                        iarr[Q*5+1] += Integer.parseInt(quant);
                        iarr[Q*5+2]++;
                    }
                }
            }
            
            // second scan, calculate information
            ResultSet rs02 = stat.executeQuery("SELECT * FROM sales");
            while (rs02.next()) {
                
                // get parameters of a row
                String cust = rs02.getString("cust");
                String prod = rs02.getString("prod");
                String month = rs02.getString("month");
                String quant = rs02.getString("quant");
                
                // get quarter
                int Q = 0;
                if(month.equals("1") || month.equals("2") || month.equals("3")) {
                    Q = 1;
                }
                else if(month.equals("4") || month.equals("5") || month.equals("6")) {
                    Q = 2;
                }
                else if(month.equals("7") || month.equals("8") || month.equals("9")) {
                    Q = 3;
                }
                else {
                    Q = 4;
                }
                
                int inWhere = -1;
                for(int i = 0; i < list.size(); i++) {
                    String[] arr = list.get(i);
                    if(arr[0].equals(cust) && arr[1].equals(prod)) {
                        inWhere = i;
                        break;
                    }
                }
                
                int[] iarr = ilist.get(inWhere);
                // before
                if(iarr[(Q+1)*5+2] != 0 && Integer.parseInt(quant) >= iarr[(Q+1)*5] && Integer.parseInt(quant) <= iarr[(Q+1)*5+1]/iarr[(Q+1)*5+2]) {
                    iarr[(Q+1)*5+3]++;
                }
                // after
                if(iarr[(Q-1)*5+2] != 0 && Integer.parseInt(quant) >= iarr[(Q-1)*5] && Integer.parseInt(quant) <= iarr[(Q-1)*5+1]/iarr[(Q-1)*5+2]) {
                    iarr[(Q-1)*5+4]++;
                }
                ilist.set(inWhere, iarr);
            }
            
            
            // finish reading, print out result
            System.out.println("CUSTOMER  PRODUCT  QUARTER  BEFORE_TOT  AFTER_TOT");
            System.out.println("========  =======  =======  ==========  =========");
            for(int i = 0; i < list.size(); i++) {
                String[] arr = list.get(i);
                int[] iarr = ilist.get(i);
                
                for(int Q = 1; Q <= 4; Q++) {
                    if(Q == 1 && iarr[Q*5+4] != 0) {
                        System.out.println( String.format("%-8s", arr[0]) + "  " +
                                            String.format("%-7s", arr[1]) + "  " +
                                            String.format("%7s", (Q + "")) + "  " +
                                            String.format("%10s", "null") + "  " +
                                            String.format("%9s", iarr[Q*5+4] + ""));
                    }
                    else if((Q == 2 || Q == 3) && (iarr[Q*5+3] != 0 || iarr[Q*5+4] != 0)) {
                        String str = String.format("%-8s", arr[0]) + "  " +
                                     String.format("%-7s", arr[1]) + "  " +
                                     String.format("%7s", (Q + "")) + "  ";
                        if(iarr[Q*5+3] != 0) {
                            str += String.format("%10s", iarr[Q*5+3] + "") + "  ";
                        }
                        else {
                            str += String.format("%10s", "null") + "  ";
                        }
                        if(iarr[Q*5+4] != 0) {
                            str += String.format("%9s", iarr[Q*5+4] + "");
                        }
                        else {
                            str += String.format("%9s", "null") + "  ";
                        }
                        
                        System.out.println(str);                        
                    }
                    else if(Q == 4 && iarr[Q*5+3] != 0) {
                        System.out.println( String.format("%-8s", arr[0]) + "  " +
                                            String.format("%-7s", arr[1]) + "  " +
                                            String.format("%7s", (Q + "")) + "  " +
                                            String.format("%10s", iarr[Q*5+3] + "") + "  " +
                                            String.format("%9s", "null"));
                    }
                }
            }
            
        }
        catch(SQLException e) {
            System.out.println("Connection URL or username or password errors!");
            e.printStackTrace();
        }

    }

}
