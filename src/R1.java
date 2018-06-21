import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class R1 {

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
     * 
     * c. A detailed description of the algorithm of my program
     *   (1) First scan, I traverse each rows and get all combination of cust and prod.
     *   (2) Second scan, for each combination I calculate total quant and total count of avg, avg for other prod and avg for other cust. total quant/total count = result.
     * 
     */
    
    public static void main(String[] args) {

        // need 8 empty spaces in an array
        int space = 8;
        // stores each array
        ArrayList<String[]> list = new ArrayList<String[]>();

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

            // first scan, get all combination of cust and prod
            ResultSet rs = stat.executeQuery("SELECT * FROM sales");
            while (rs.next()) {
                
                // get parameters of a row
                String cust = rs.getString("cust");
                String prod = rs.getString("prod");
                
                // notInList is a flag to show whether someone's data is in the list or not
                boolean notInList = true;
                // to see where is someone's data in the list
                for(int i = 0; i < list.size(); i++) {
                    String[] arr = list.get(i);
                    if(arr[0].equals(cust) && arr[1].equals(prod)) {
                        notInList = false;
                        break;
                    }
                }
                
                if(notInList) {
                    // someone's data is not in the list. Just add the data in.
                    String[] arr = new String[space];
                    arr[0] = cust;      // CUSTOMER
                    arr[1] = prod;     // PRODUCT
                    arr[2] = "0";      // avg_sum
                    arr[3] = "0";      // avg_count
                    arr[4] = "0";     // avg_for_other_prod_sum
                    arr[5] = "0";     // avg_for_other_prod_count
                    arr[6] = "0";      // avg_for_other_cust_sum
                    arr[7] = "0";      // avg_for_other_cust_count
                    
                    list.add(arr);
                }
            }
            
            // second scan, calculate information
            ResultSet rs02 = stat.executeQuery("SELECT * FROM sales");
            while (rs02.next()) {
                
                // get parameters of a row
                String cust = rs02.getString("cust");
                String prod = rs02.getString("prod");
                String quant = rs02.getString("quant");
                
                for(int i = 0; i < list.size(); i++) {
                    String[] arr = list.get(i);
                    // avg
                    if(arr[0].equals(cust) && arr[1].equals(prod)) {
                        arr[2] = Integer.parseInt(arr[2])+Integer.parseInt(quant)+"";
                        arr[3] = Integer.parseInt(arr[3])+1+"";
                        list.set(i, arr);
                    }
                    // avg for other prod
                    else if(arr[0].equals(cust) && (!arr[1].equals(prod))) {
                        arr[4] = Integer.parseInt(arr[4])+Integer.parseInt(quant)+"";
                        arr[5] = Integer.parseInt(arr[5])+1+"";
                        list.set(i, arr);
                    }
                    // avg for other cust
                    else if((!arr[0].equals(cust)) && arr[1].equals(prod)) {
                        arr[6] = Integer.parseInt(arr[6])+Integer.parseInt(quant)+"";
                        arr[7] = Integer.parseInt(arr[7])+1+"";
                        list.set(i, arr);
                    }
                }
            }
            
            
            // finish reading, print out result
            System.out.println("CUSTOMER  PRODUCT  THE_AVG  OTHER_PROD_AVG  OTHER_CUST_AVG");
            System.out.println("========  =======  =======  ==============  ==============");
            for(int i = 0; i < list.size(); i++) {
                String[] arr = list.get(i);
                System.out.println( String.format("%-8s", arr[0]) + "  " +
                                    String.format("%-7s", arr[1]) + "  " +
                                    String.format("%7s", (Integer.parseInt(arr[2])/Integer.parseInt(arr[3]) + "")) + "  " +
                                    String.format("%14s", (Integer.parseInt(arr[4])/Integer.parseInt(arr[5]) + "")) + "  " +
                                    String.format("%14s", (Integer.parseInt(arr[6])/Integer.parseInt(arr[7]) + "")));
            }
            
        }
        catch(SQLException e) {
            System.out.println("Connection URL or username or password errors!");
            e.printStackTrace();
        }

    }

}
