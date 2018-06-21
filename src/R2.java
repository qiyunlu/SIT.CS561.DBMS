import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class R2 {

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
     *   (1) For each combination, I calculate total quant and total count of avg. total quant/total count = avg.
     *   (2) put avg value in right place.
     * 
     */
    
    public static void main(String[] args) {

        // need 4 empty spaces in an array
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
                    
                    int[] iarr = {0,0,0,0,0,0,0,0,0};
                    iarr[Q*2-1] += Integer.parseInt(quant);
                    iarr[Q*2] ++;
                    ilist.add(iarr);
                }
                else {
                    // someone's data has already been in the list. Just update the data.
                    int[] iarr = ilist.get(inWhere);
                    iarr[Q*2-1] += Integer.parseInt(quant);
                    iarr[Q*2] ++;
                }
                
            }
            
            
            // finish reading, print out result
            System.out.println("CUSTOMER  PRODUCT  QUARTER  BEFORE_AVG  AFTER_AVG");
            System.out.println("========  =======  =======  ==========  =========");
            for(int i = 0; i < list.size(); i++) {
                String[] arr = list.get(i);
                int[] iarr = ilist.get(i);
                
                for(int Q = 1; Q <= 4; Q++) {
                    if(Q == 1 && iarr[(Q+1)*2] != 0) {
                        System.out.println( String.format("%-8s", arr[0]) + "  " +
                                            String.format("%-7s", arr[1]) + "  " +
                                            String.format("%7s", (Q + "")) + "  " +
                                            String.format("%10s", "null") + "  " +
                                            String.format("%9s", (iarr[(Q+1)*2-1]/iarr[(Q+1)*2] + "")));
                    }
                    else if((Q == 2 || Q == 3) && (iarr[(Q-1)*2] != 0 || iarr[(Q+1)*2] != 0)) {
                        String str = String.format("%-8s", arr[0]) + "  " +
                                     String.format("%-7s", arr[1]) + "  " +
                                     String.format("%7s", (Q + "")) + "  ";
                        if(iarr[(Q-1)*2] != 0) {
                            str += String.format("%10s", (iarr[(Q-1)*2-1]/iarr[(Q-1)*2] + "")) + "  ";
                        }
                        else {
                            str += String.format("%10s", "null") + "  ";
                        }
                        if(iarr[(Q+1)*2] != 0) {
                            str += String.format("%9s", (iarr[(Q+1)*2-1]/iarr[(Q+1)*2] + ""));
                        }
                        else {
                            str += String.format("%9s", "null") + "  ";
                        }
                        
                        System.out.println(str);                        
                    }
                    else if(Q == 4 && iarr[(Q-1)*2] != 0) {
                        System.out.println( String.format("%-8s", arr[0]) + "  " +
                                            String.format("%-7s", arr[1]) + "  " +
                                            String.format("%7s", (Q + "")) + "  " +
                                            String.format("%10s", (iarr[(Q-1)*2-1]/iarr[(Q-1)*2] + "")) + "  " +
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
