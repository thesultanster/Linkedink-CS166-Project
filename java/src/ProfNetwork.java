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
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class ProfNetwork 
{
  boolean logged;     //If user is logged on
  String username;    //Desired username
  String password;    //Desired password
  String name;        //Full name
  String email;       //Email
  String dob;         //Date of birth
   
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
  public ProfNetwork (String dbname, String dbport, String user, String passwd) throws SQLException 
  {
    System.out.print("Connecting to database...");
    try
    {
      // constructs the connection URL
      String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
      System.out.println ("Connection URL: " + url + "\n");

      // obtain a physical connection
      this._connection = DriverManager.getConnection(url, user, passwd);
      System.out.println("Done");
    }
    catch (Exception e)
    {
      System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
      System.out.println("Make sure you started postgres on this machine");
      System.exit(-1);
    }//end catch
  }//end ProfNetwork

  /**
  * Method to execute an update SQL statement.  Update SQL instructions
  * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
  *
  * @param sql the input SQL string
  * @throws java.sql.SQLException when update failed
  */
  public void executeUpdate (String sql) throws SQLException 
  {
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
  public int executeQuery (String query) throws SQLException 
  {
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
    while (rs.next())
    {
      if(outputHeader)
      {
        for(int i = 1; i <= numCol; i++)
        {
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
   * Method to execute an input query SQL instruction (i.e. SELECT).  This
   * method issues the query to the DBMS and returns the results as
   * a list of records. Each record in turn is a list of attribute values
   *
   * @param query the input query string
   * @return the query result as a list of records
   * @throws java.sql.SQLException when failed to execute the query
   */
  public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException 
  {
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

    // iterates through the result set and saves the data returned by the query.
    boolean outputHeader = false;
    List<List<String>> result  = new ArrayList<List<String>>();
    while (rs.next())
    {
      List<String> record = new ArrayList<String>();
      for (int i=1; i<=numCol; ++i)
        record.add(rs.getString (i));
      result.add(record);
    }//end while
    stmt.close ();
    return result;
  }//end executeQueryAndReturnResult

    

  /**
  * Method to execute an input query SQL instruction to get userinfo and store
  * results into the object
  *
  * @param ProfNetwork object
  * @return vodi
  * @throws java.sql.SQLException when failed to execute the query
  */
  public void  UpdateUserInfo (String username) throws SQLException 
  {
    String query =
      "SELECT  userId, password, email, name, dateOfBirth "
      +"FROM USR "
      +"WHERE userId = '"+username+"';";

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

    rs.next();
    rs.next();
   
    this.username = rs.getString (1);
    this.password = rs.getString (2);
    this.email = rs.getString (3);
    this.name = rs.getString (4);
    this.dob = rs.getString (5);
      
    stmt.close ();
    
  }//end UpdateUserInfo



  /**
   * Method to fetch the last value from sequence. This
   * method issues the query to the DBMS and returns the current
   * value of sequence used for autogenerated keys
   *
   * @param sequence name of the DB sequence
   * @return current value of a sequence
   * @throws java.sql.SQLException when failed to execute the query
   */
  public int getCurrSeqVal(String sequence) throws SQLException 
  {
    Statement stmt = this._connection.createStatement ();

    ResultSet rs = stmt.executeQuery (String.format("Select nextval('%s')", sequence));
    if (rs.next())
      return rs.getInt(1);
    return -1;
  }



  /**
  * Method to close the physical connection if it is open.
  */
  public void cleanup()
  {
    try
    {
      if (this._connection != null)
      {
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
  public static void main (String[] args) 
  {
    if (args.length != 3) 
    {
      System.err.println (
        "Usage: " +
        "java [-classpath <classpath>] " +
        ProfNetwork.class.getName () +
        " <dbname> <port> <user>");
      return;
    }//end if
    
    ProfNetwork esql = null;
    try
    {
      // use postgres JDBC driver.
      Class.forName ("org.postgresql.Driver").newInstance ();
      // instantiate the EmbeddedSQL object and creates a physical connection.
      String dbname = args[0];
      String dbport = args[1];
      String user = args[2];
      esql = new ProfNetwork (dbname, dbport, user, "");

      String userKey = LoginPrompt(esql);

      //System.out.println("UserKey is : " + userKey );

      boolean keepon = true;
      while(keepon) 
      {
        clrScreen();
   
        System.out.println("Welcome to LinkedInk!");
        System.out.println("---------------------");
        System.out.println("0. Change My Password");
        System.out.println("1. Search People");
        System.out.println("2. Read Your Messages" );
        System.out.println("3. For every supplier that supplies only green parts, print the name of the supplier and the total number of parts that he supplies");
        System.out.println("4. For every supplier that supplies green part and red part, print the name and the price of the most expensive part that he supplies"); 
        System.out.println("5. Send Message to Anyone");
        System.out.println("6. Find the address of the suppliers who supply _____________ (pname)");
        System.out.println("9. < EXIT");

        switch (readChoice()){
          case 0: ChangePassword(esql); break;
          case 1: SearchName(esql); break;
          case 2: Query2(esql); break;
          case 3: Query3(esql); break;
          case 4: Query4(esql); break;
          case 5: SendMessage(esql); break;
          case 6: Query6(esql); break;
          case 9: keepon = false; break;
          default : System.out.println("Unrecognized choice!"); break;
        }//end switch
      }//end while
    }
    catch(Exception e) 
    {
      System.err.println (e.getMessage ());
    }
    finally
    {
      // make sure to cleanup the created table and close the connection.
      try
      {
        if(esql != null) 
        {
          System.out.print("Disconnecting from database...");
          esql.cleanup ();
          System.out.println("Done\n\nBye !");
        }//end if
      }
      catch (Exception e) 
      {
        // ignored.
      }//end catch
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
    


  public static void Greeting()
  {
    System.out.println(
      "\n\n*******************************************************\n" +
      "              User Interface      	               \n" +
      "*******************************************************\n");
  }//end Greeting


  public static String Login(ProfNetwork esql)
  {
	  clrScreen();
	
	  // Bool becomes true if wrong login info
	  boolean again = false;

    Scanner input; 
    String username;
    String password; 
    int rowCount = 0;

    // Login credentials
    do
    {
      if(again)
        System.out.println("ERROR: Invalid Credentials");

      System.out.print("Username: ");
      input = new Scanner(System.in);
      username  = input.next();

      System.out.print("Password: ");
      input = new Scanner(System.in);
      password  = input.next();

      try 
      { // See if username exists
        String query =
          "SELECT  USR.userId "
          +"FROM USR  "
          +"WHERE USR.userId = '"+username+"' AND USR.password = '"+password+"';";
        
        rowCount = esql.executeQuery(query); 
        //System.out.println("total row(s): " + rowCount); 
      }
      catch(Exception e)
      {
        System.err.println(e.getMessage()); 
      }
      again = true;
    } while(rowCount == 0); // If username exists then rowCount == 1, otherwise retry

    try
    {
      esql.UpdateUserInfo(username);
    } 
    catch( Exception e )
    {
      System.err.println(e.getMessage());
    }

    System.out.println("ESQL Testing: "+ esql.username + " " + esql.password + " " + esql.email + " " + esql.name + " " + esql.dob);
    // set values to PorfNework object
    //esql.username = username;
    //esql.password = password;

    
    return username; 
  }

  public static String SignUp(ProfNetwork esql)
  {
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
    do 
    {
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
	  do 
    {
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
	  do 
    {
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
	  try 
    {
	    esql.executeUpdate(sql);
      System.out.println("User craeated: "+ username + " " + password + " " + email + " " + name + " " + dob);
	  }
    catch(Exception e)
    {
	    System.err.println(e.getMessage());
	  }
	 
	  esql.username = username;
	  esql.password = password;
	  esql.email = email;
	  esql.name = name;
	  esql.dob = dob; 

	  return username; 
	}
    
  public static String LoginPrompt(ProfNetwork esql)
  {
    clrScreen();

    System.out.println(
      "\n\n*******************************************************\n" +
      "              Login Screen      	               \n" +
      "*******************************************************\n");
    System.out.println("0. Sign Up!");
    System.out.println("1. Sign In");

	  String userKey="";
	 
	  switch (readChoice())
    {
	    case 0:
	      userKey = SignUp(esql);
	      break;
	    case 1:
	      userKey = Login(esql);
	      break;
	    default : 
        System.out.println("Unrecognized choice!"); 
        break;
	  }//end switch 

	  return userKey; 
  }
    
  //========================================================================================================

  //========== Queries ====================================================================================

  public static void ChangePassword(ProfNetwork esql)
  {
	  clrScreen();
	
    Scanner input; 
    String password;
    boolean again = false;

	  // Input Password
	  do 
    {
	    if(again)
		    System.out.println("ERROR: Password must be less than 10 characters");
	  
	    System.out.print("Desired Password: ");
	    input = new Scanner(System.in);
	    password  = input.next();

	    again = true; 
	  } while (password.length() > 10);
	
	  String sql = "UPDATE USR SET password = '"+password+"' WHERE USR.userId = '"+esql.username+"';";
	  try 
    {
	    esql.executeUpdate(sql);
    }
    catch(Exception e)
    {
	    System.err.println(e.getMessage());
	  }
    esql.password = password; 
  }

  public static void SearchName(ProfNetwork esql)
  {
    clrScreen();

    Scanner input;
    String name;
    boolean again = false;

    System.out.print("Enter Name: ");
    input = new Scanner(System.in);
    name  = input.next();

    String sql = "SELECT userId AS Username, name AS Name FROM USR WHERE name = '" + name + "';";

    try 
    {
      esql.executeQuery(sql);
    }
    catch(Exception e)
    {
      System.err.println(e.getMessage());
    }

    System.out.print("Send Request to a user (username SEARCH / 0 EXIT) : ");

    input = new Scanner(System.in);
    String temp  = input.next();
    if(temp.equals("0"))
    {
        return; 
    }
  }

  public static void SendMessage(ProfNetwork esql)
  {
    clrScreen();

    Scanner input;
    int deleteStatus;
    String username = esql.username; 
    String receiverId;
    String contents;
    String status;
    String sendTime = "2004-10-19 10:23:54"; 

    System.out.print("Send To (username): ");
    input = new Scanner(System.in);
    receiverId  = input.next();

	  //TODO error check if receiverid exists

	
    System.out.print("Message : ");
    input = new Scanner(System.in);
    contents  = input.next();

    status="UNREAD";
    deleteStatus = 0;
	
    int keyVal = 0;
    try 
    {
      keyVal = esql.getCurrSeqVal("msg");
    }
    catch(Exception e)
    {
      System.err.println(e.getMessage());
    }

    System.out.println("TEST keyVal: "+keyVal);
	
    String sql = "INSERT INTO MESSAGE VALUES ('"+keyVal+"','"+ username +"','"+receiverId+"','"+contents+"','"+sendTime+"','"+deleteStatus+"','"+status+"');";
	
	  try 
    {
	    esql.executeUpdate(sql);
	  }
    catch(Exception e)
    {
	    System.err.println(e.getMessage());
	  }

	  System.out.print("Sent messagea user (0 SEND ANOTHER / any key EXIT) : ");

	  input = new Scanner(System.in);
	  String temp  = input.next();
	  if (temp.equals("0"))
    {
	    SendMessage(esql);
	  }
	  else 
    {
	    System.out.println("Wrong Value");
	    return;
	  }
	 
  }
  //========================================================================================================

  /*
  * Reads the users choice given from the keyboard
  * @int
  **/
  public static int readChoice() 
  {
    int input;
    // returns only if a correct value is given.
    do 
    {
      System.out.print("Please make your choice: ");
      try 
      { // read the integer, parse it and break.
        input = Integer.parseInt(in.readLine());
        break;
      }
      catch (Exception e) 
      {
        System.out.println("Your input is invalid!");
        continue;
      }//end try
    }while (true);
    return input;
  }//end readChoice

  public static void QueryExample(ProfNetwork esql)
  {
    try
    {
      String query = "SELECT * FROM Catalog WHERE cost < ";
      System.out.print("\tEnter cost: $");
      String input = in.readLine();
      query += input;

      int rowCount = esql.executeQuery(query);
      System.out.println ("total row(s): " + rowCount);
    }
    catch(Exception e)
    {
      System.err.println (e.getMessage());
    }
  }//end QueryExample
   
  public static void Query1(ProfNetwork esql)
  {
    try 
    {
      String query = "SELECT C.sid, COUNT(*) AS Parts FROM Catalog C GROUP BY C.sid"; 
      int rowCount = esql.executeQuery(query); 
      System.out.println("total row(s): " + rowCount); 
    }
    catch(Exception e)
    {
      System.err.println(e.getMessage()); 
    }
  }//end Query1

  public static void Query2(ProfNetwork esql)
  {
    try 
    {
      String query = "SELECT C.sid, COUNT(*) AS Parts "
                      +"FROM Catalog C "
                      +"WHERE 3 <= ALL (SELECT COUNT(*) " 
                      +"                FROM Catalog C1 "
                      +"                WHERE C1.sid = C.sid "
                      +"                GROUP BY C1.sid) "
                      +"GROUP BY C.sid"; 
      int rowCount = esql.executeQuery(query); 
      System.out.println("total row(s): " + rowCount); 
    }
    catch(Exception e)
    {
      System.err.println(e.getMessage()); 
    }
  }//end Query2

  public static void Query3(ProfNetwork esql)
  {
    try 
    {
      String query = "SELECT S.sname, COUNT(*) AS Parts "
                      +"FROM Catalog C, Suppliers S "
                      +"WHERE C.sid = S.sid "
                      +"      AND C.sid NOT IN (SELECT C1.sid "
                      +"                    FROM Catalog C1, Parts P "
                      +"                    WHERE C1.pid = P.pid AND P.color != 'Green') "
                      +"GROUP BY S.sname"; 
      int rowCount = esql.executeQuery(query); 
      System.out.println("total row(s): " + rowCount); 
    }
    catch(Exception e)
    {
      System.err.println(e.getMessage()); 
    }
  }//end Query3

  public static void Query4(ProfNetwork esql)
  {
    try 
    {
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
    }
    catch(Exception e)
    {
        System.err.println(e.getMessage()); 
    }
  }//end Query4

  public static void Query5(ProfNetwork esql)
  {
    try
    {
      String query = "SELECT C.cost, P.pname "
                    +"FROM Catalog C, Parts P "
                    +"WHERE C.pid = P.pid AND C.cost < "; 
      System.out.print("\tEnter cost: $");
      String input = in.readLine();
      query += input + " GROUP BY C.cost, P.pname";
      int rowCount = esql.executeQuery(query);
      System.out.println ("total row(s): " + rowCount);
    }
    catch(Exception e)
    {
      System.err.println (e.getMessage());
    }
  }//end Query5

  public static void Query6(ProfNetwork esql)
  {
    try
    {
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
    }
    catch(Exception e)
    {
      System.err.println (e.getMessage());
    }
  }//end Query6

}//end EmbeddedSQL

