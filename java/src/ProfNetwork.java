/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class ProfNetwork {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of EmbeddedSQL
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public ProfNetwork (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end EmbeddedSQL

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            ProfNetwork.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if
      
     

      
      ProfNetwork esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the EmbeddedSQL object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new ProfNetwork (dbname, dbport, user, "");

	 LoginPrompt(esql);
	 
         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("0. Find the pid of parts with cost lower than $_____ (example)");
            System.out.println("1. Find the total number of parts supplied by each supplier");
            System.out.println("2. Find the total number of parts supplied by each supplier who supplies at least 3 parts");
            System.out.println("3. For every supplier that supplies only green parts, print the name of the supplier and the total number of parts that he supplies");
            System.out.println("4. For every supplier that supplies green part and red part, print the name and the price of the most expensive part that he supplies"); 
            System.out.println("5. Find the name of parts with cost lower than $_____");
            System.out.println("6. Find the address of the suppliers who supply _____________ (pname)");
            System.out.println("9. < EXIT");

            switch (readChoice()){
               case 0: QueryExample(esql); break;
               case 1: Query1(esql); break;
               case 2: Query2(esql); break;
               case 3: Query3(esql); break;
               case 4: Query4(esql); break;
               case 5: Query5(esql); break;
               case 6: Query6(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main


    public static void clrScreen()
    {
	final String ANSI_CLS = "\u001b[2J";
	final String ANSI_HOME = "\u001b[H";
	System.out.print(ANSI_CLS + ANSI_HOME);
	System.out.flush();
    }
    
   //=======MENUS============================================================================================
    


    public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting


    public static void Login(){
	
	 
	 
    }

    public static void SignUp(ProfNetwork esql){
	 clrScreen();
	
	 System.out.println(
         "\n\n*******************************************************\n" +
         "              Please Sign Up      	               \n" +
         "*******************************************************\n");

	 Scanner input;
	 
	 // Bool becomes true if wrong username
	 boolean again = false;
	 // Desired Username 
	 String username;
	 // Desired Password
	 String password;
	 // Full Name; 
	 String name;
	 // Email
	 String email;
	 // Dob
	 String dob;

	 
	 // Input Username
	 do {
	     if(again)
		 System.out.println("ERROR: Username must be less than 10 characters");
	  
	     System.out.print("Desired Username: ");
	     input = new Scanner(System.in);
	     username  = input.next();

	     again = true; 
	 } while (username.length() > 10);

	 // Now use bool for password
	 again = false;

	 // Input Password
	 do {
	     if(again)
		 System.out.println("ERROR: Password must be less than 10 characters");
	  
	     System.out.print("Desired Password: ");
	     input = new Scanner(System.in);
	     password  = input.next();

	     again = true; 
	 } while (password.length() > 10);

	 // Input Email
	 System.out.print("Your Email Address: ");
	 input = new Scanner(System.in);
	 email  = input.next();

	 // use bool for name
	 again = false; 

	 // Input Full Name
	 do {
	     if(again)
		 System.out.println("ERROR: Name must be less than 50 characters");
	  
	     System.out.print("Your Name: ");
	     input = new Scanner(System.in);
	     name  = input.next();

	     again = true; 
	 } while (name.length() > 50);

	 // Input DoB
	 System.out.print("Your Date of Birth (YYYY/MM/DD) : ");
	 input = new Scanner(System.in);
	 dob  = input.next();
	 String sql = "INSERT INTO USR VALUES ('" + username + "','" + password + "','" + email + "','" + name + "','" + dob + "');";

	 //String ex = "INSERT INTO USR;";

	 try {
	     esql.executeUpdate(sql);
             System.out.println("User craeated: "+ username + " " + password + " " + email + " " + name + " " + dob);
	 }catch(Exception e){
	     System.err.println(e.getMessage());
	 }
	 
	 

	 
	 
	 
    }
    
    public static void LoginPrompt(ProfNetwork esql){
	clrScreen();

	 System.out.println(
         "\n\n*******************************************************\n" +
         "              Login Screen      	               \n" +
         "*******************************************************\n");

	 System.out.println("0. Sign Up!");
	 System.out.println("1. Sign In");

	 switch (readChoice()){
	 case 0:
	     SignUp(esql);
	     break;
	 case 1: Login(); break;
	 default : System.out.println("Unrecognized choice!"); break;
	 }//end switch 
	 
    }
    

    
   //========================================================================================================
   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   public static void QueryExample(ProfNetwork esql){
      try{
         String query = "SELECT * FROM Catalog WHERE cost < ";
         System.out.print("\tEnter cost: $");
         String input = in.readLine();
         query += input;

         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end QueryExample
   
   public static void Query1(ProfNetwork esql){
      try {
          String query = "SELECT C.sid, COUNT(*) AS Parts FROM Catalog C GROUP BY C.sid"; 
          int rowCount = esql.executeQuery(query); 
          System.out.println("total row(s): " + rowCount); 
      }catch(Exception e){
          System.err.println(e.getMessage()); 
      }
   }//end Query1

   public static void Query2(ProfNetwork esql){
      try {
          String query = "SELECT C.sid, COUNT(*) AS Parts "
                        +"FROM Catalog C "
                        +"WHERE 3 <= ALL (SELECT COUNT(*) " 
                        +"                FROM Catalog C1 "
                        +"                WHERE C1.sid = C.sid "
                        +"                GROUP BY C1.sid) "
                        +"GROUP BY C.sid"; 
          int rowCount = esql.executeQuery(query); 
          System.out.println("total row(s): " + rowCount); 
      }catch(Exception e){
          System.err.println(e.getMessage()); 
      }
   }//end Query2

   public static void Query3(ProfNetwork esql){
      try {
          String query = "SELECT S.sname, COUNT(*) AS Parts "
                        +"FROM Catalog C, Suppliers S "
                        +"WHERE C.sid = S.sid "
                        +"      AND C.sid NOT IN (SELECT C1.sid "
                        +"                    FROM Catalog C1, Parts P "
                        +"                    WHERE C1.pid = P.pid AND P.color != 'Green') "
                        +"GROUP BY S.sname"; 
          int rowCount = esql.executeQuery(query); 
          System.out.println("total row(s): " + rowCount); 
      }catch(Exception e){
          System.err.println(e.getMessage()); 
      }
   }//end Query3

   public static void Query4(ProfNetwork esql){
      try {
          String query = "SELECT S.sname, MAX(C.cost) "
                        +"FROM Catalog C, Suppliers S "
                        +"WHERE C.sid = S.sid "
                        +"      AND C.sid IN (SELECT C1.sid "
                        +"                FROM Catalog C1, Parts P "
                        +"                WHERE C1.pid = P.pid AND P.color = 'Green') "
                        +"      AND C.sid IN (SELECT C1.sid "
                        +"                 FROM Catalog C1, Parts P "
                        +"                WHERE C1.pid = P.pid AND P.color = 'Red') "
                        +"GROUP BY S.sname, S.sid";
          int rowCount = esql.executeQuery(query); 
          System.out.println("total row(s): " + rowCount); 
      }catch(Exception e){
          System.err.println(e.getMessage()); 
      }
   }//end Query4

   public static void Query5(ProfNetwork esql){
      try{
         String query = "SELECT C.cost, P.pname "
                        +"FROM Catalog C, Parts P "
                        +"WHERE C.pid = P.pid AND C.cost < "; 
         System.out.print("\tEnter cost: $");
         String input = in.readLine();
         query += input + " GROUP BY C.cost, P.pname";
         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end Query5

   public static void Query6(ProfNetwork esql){
      try{
        String query = "SELECT S.sname, S.address "
                      +"FROM Suppliers S, Parts P, Catalog C "
                      +"WHERE S.sid = C.sid "
                      +"      AND P.pid = C.pid "
                      +"      AND P.pname = "; 
         System.out.print("\tEnter part name: $");
         String input = in.readLine();
         query += "'" + input + "'";
         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch(Exception e){
         System.err.println (e.getMessage());
      }
   }//end Query6

}//end EmbeddedSQL

