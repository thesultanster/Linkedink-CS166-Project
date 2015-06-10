/*
 * Partner 1: Amanda Berryhill, 861070154
 * Partner 2: Sultan Khan, 861047478
 * Group ID: 49
 * CS166: Project
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
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.text.SimpleDateFormat;
import java.util.Date;

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

  //=====================================================================================================
  //==============BEGIN HELPER FUNCTIONS (SQL)================================================================

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
    //boolean outputHeader = true;
    while (rs.next())
    {
      /*
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
      */
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
  
  
  public List<List<String>> getCSV(String csvFile) {
 
    //String csvFile = "USR-Table 1.csv";
    BufferedReader br = null;
    String line = "";
    String cvsSplitBy = ",";
    boolean containsBad = false;   

    List<List<String>> result  = new ArrayList<List<String>>();

    try 
    {
      br = new BufferedReader(new FileReader(csvFile));
      while ((line = br.readLine()) != null) 
      {
   
        // use comma as separator
        String[] next = line.split(cvsSplitBy);
       
        containsBad = false; 
        for (String s : next)
        {
          if (s.contains("'"))
          {
            containsBad = true;
            break;
          }
        }
        if (containsBad)
        {
          continue;
        }

        //if(usr[0].contains("'") || usr[1].contains("'") || usr[2].contains("'") || usr[3].contains("'") || usr[4].contains("'"))
        //  continue;
   
        //System.out.println(usr[0] + " " + usr[1]);
   
        List<String> record = new ArrayList<String>();
        for(int i=0; i < next.length; i++ )
          record.add(next[i]);
        result.add(record);
      }
   
    } 
    catch (Exception e) 
    {
      e.printStackTrace();
    }  
    finally 
    {
      if (br != null) 
      {
        try 
        {
          br.close();
        } 
        catch ( Exception e) 
        {
          e.printStackTrace();
        }
      }
	  }
 
	  //System.out.println("Done");
	  return result;
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

  /*
  * Return if username exists
  */
  public boolean isUser(String username)
  {
    int rowCount = 0;
    try 
    { // See if username exists
      String query =
        "SELECT  USR.userId "
        +"FROM USR  "
        +"WHERE USR.userId = '"+username+"';";
      
      rowCount = executeQuery(query); 
      //System.out.println("total row(s): " + rowCount); 
    }
    catch(Exception e)
    {
      //System.err.println(e.getMessage()); 
    }
    return rowCount > 0;
  }

  //==============END: HELPER FUNCTIONS (SQL)============================================================
  //===================================================================================================== 
  //==============BEGIN: HELPER FUNCTIONS (user input)===================================================

  /*
  * Return int from user input
  **/
  public static int getUserInputInt(String prompt) 
  {
    String s;
    int input;
    // returns only if a correct value is given.
    while (true) 
    {
      System.out.print(prompt);
      try 
      { 
        // read the integer, parse it, and break.
        s = in.readLine();
        if (s.equals(""))   //stop if input is empty
        {
          return -1;
        }
        input = Integer.parseInt(s);
        break;
      }//end try
      catch (Exception e) 
      {
      }//end catch
      System.out.println("!! ERROR: Invalid input !!");
    }
    return input;
  }//end getUserInputInt() -> int

  /*
  * Return int from user input
  **/
  public static int getUserInputInt(String prompt, int maxValue) 
  {
    String s;
    int input;
    // returns only if a correct value is given.
    while (true)
    {
      System.out.print(prompt);
      try 
      { 
        // read the integer, parse it, and break.
        s = in.readLine();
        if (s.equals(""))   //stop if input is empty
        {
          return -1;
        }
        input = Integer.parseInt(s);
        if (input >= 0 && input <= maxValue)
        {
          break;
        }
      }//end try
      catch (Exception e) 
      {
      }//end catch
      System.out.println("!! ERROR: Invalid input !!");
    }
    return input;
  }//end getUserInputInt() -> int
  
  /*
  * Return string from user input
  */
  public static String getUserInputString(String prompt) 
  {
    String input;
    // returns only if a correct value is given.
    while(true) 
    {
      System.out.print(prompt);
      try
      {
        // read the string, check length, and break.
        input = in.readLine();
        break;
      }//end try
      catch(Exception e)
      {
      }//end catch
      System.out.println("!! ERROR: Invalid input !!");
    } //end while
    return input;
  } //end getUserInputString() -> string

  /*
  * Return string from user input
  */
  public static String getUserInputString(String prompt, int maxLength) 
  {
    String input;
    // returns only if a correct value is given.
    while(true) 
    {
      System.out.print(prompt);
      try
      {
        // read the string, check length, and break.
        input = in.readLine();
        if (maxLength < 0 || input.length() <= maxLength)
        {
          break;
        }
        System.out.println("!! ERROR: Invalid input. Length must be >= "+maxLength+" !!");
      }//end try
      catch(Exception e)
      {
        System.out.println("!! ERROR: Invalid input !!");
      }//end catch
    } //end while
    return input;
  } //end getUserInputString() -> string

  //==============END: HELPER FUNCTIONS (user input)=====================================================
  //=====================================================================================================
  //==============BEGIN: HELPER FUNCTIONS (other)========================================================

  public static void clrScreen()
  {
    final String ANSI_CLS = "\u001b[2J";
    final String ANSI_HOME = "\u001b[H";
    System.out.print(ANSI_CLS + ANSI_HOME);
    System.out.flush();
  }
  
  //==============END: HELPER FUNCTIONS (other)==========================================================
  //=====================================================================================================
  //==============BEGIN MAIN==============================================================================

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
      
      
      // Import Data from CSV
      importUsr(esql);
      importMessage(esql);
      

      LoginPrompt(esql);

      boolean keepon = true;
      while(keepon) 
      {
        clrScreen();
   
        System.out.println("Welcome to LinkedInk!");
        System.out.println("-------------------------");
        System.out.println("0. Change My Password");
        System.out.println("1. Edit Profile (TODO)");
        System.out.println("2. Search People");
        System.out.println("3. Send Friend Requests (IN PROGRESS)");
        System.out.println("4. View Friend Requests");
        System.out.println("5. View Friends List (IN PROGRESS)");
        System.out.println("6. Send Messages to Anyone");
        System.out.println("7. Read Messages" );
        System.out.println("9. < EXIT");

        switch (getUserInputInt("Select option: "))
        {
          case 0: ChangePassword(esql); break;
          case 1: EditProfile(esql); break;
          case 2: SearchName(esql); break;
          case 3: SendFriendRequests(esql); break;
          case 4: ViewFriendRequests(esql); break;
          case 5: ViewFriendsList(esql); break;
          case 6: SendMessages(esql); break;
          case 7: ReadMessages(esql); break;
          case 9: keepon = false; break;
          default : System.out.println("!! ERROR: Invalid Selection !!"); break;
        }//end switch
      }//end while
    }//end try
    catch(Exception e) 
    {
      System.err.println (e.getMessage ());
    }//end catch
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
      }//end try
      catch (Exception e) 
      {
        // ignored.
      }//end catch
    }//end finally
  }//end main

  //=============END: MAIN===============================================================================
  //=====================================================================================================
  //=============BEGIN: HELPER FUNCTIONS (IMPORT DATABASE)===============================================
  
  
  public static void importUsr(ProfNetwork esql)
  {
    List<List<String>> result  = new ArrayList<List<String>>();
    result = esql.getCSV("../../data/USR-Table 1.csv");
    
    for ( List<String> usr : result )
    {
      String sql = "INSERT INTO USR VALUES ('" + usr.get(0) + "','" + usr.get(1) + "','" + usr.get(2) + "','" + usr.get(3) + "','" + usr.get(4) + "');";
      try 
      {
        esql.executeUpdate(sql);
        
      }//end try
      catch(Exception e)
      {
        System.err.println(e.getMessage());
      }//end catch
    }  
  }

  public static void importMessage(ProfNetwork esql)
  {
    String status;
    List<List<String>> result  = new ArrayList<List<String>>();
    result = esql.getCSV("../../data/Message-Table 1.csv");
    
    for ( List<String> message : result )
    {
      status = message.get(6);
      if (status.equals("Delivered") || status.equals("Sent") || status.equals("Draft"))
      {
        status = "UNREAD";
      }
      else if (status.equals("Read"))
      {
        status = "READ";
      }

      String sql = "INSERT INTO MESSAGE VALUES ('" + message.get(0) + "','" + message.get(1) + "','" + message.get(2) + "','" + message.get(3) + "','" + message.get(4) + "'," + message.get(5) + ",'" + status + "');";
      try 
      {
        esql.executeUpdate(sql);
      }//end try
      catch(Exception e)
      {
        System.err.println(e.getMessage());
      }//end catch
    }  
  }

  //=============END: HELPER FUNCTIONS (IMPORT DATABASE)=================================================
  //=====================================================================================================
  //=============BEGIN: LOGIN AND SIGN UP================================================================

  /*
  * Prompt the user to login with username and password
  */
  public static void Login(ProfNetwork esql)
  {
	  clrScreen();
	
	  System.out.println(
      "\n\n*******************************************************\n" +
      "                     Login      	               \n" +
      "*******************************************************\n");

    String username;
    String password; 
    int rowCount = 0;

    // Login credentials
    while (true)
    {
      username = getUserInputString("Username: ");
      password = getUserInputString("Password: ");
      
      try 
      { 
        // See if username and password combo exist
        String query =
          "SELECT  USR.userId "
          +"FROM USR  "
          +"WHERE USR.userId = '"+username+"' AND USR.password = '"+password+"';";
        
        rowCount = esql.executeQuery(query); 
        if (rowCount > 0)
        {
          break;
        }
        //System.out.println("total row(s): " + rowCount); 
      }
      catch(Exception e)
      {
        System.err.println(e.getMessage()); 
      }
      System.out.println("ERROR: Invalid Credentials");
    }

    try
    {
      esql.UpdateUserInfo(username);
    } 
    catch( Exception e )
    {
      System.err.println(e.getMessage());
    }

    System.out.println("ESQL Testing: "+ esql.username + " " + esql.password + " " + esql.email + " " + esql.name + " " + esql.dob);
  }//end Login()

  /*
  * Prompt user for new account information to sign up
  */
  public static void SignUp(ProfNetwork esql)
  {
    clrScreen();
	
	  System.out.println(
      "\n\n*******************************************************\n" +
      "                     Sign Up      	               \n" +
      "*******************************************************\n");

    String username;            //Desired Username
    String password;            //Desired password
    String email;               //Email
    String name;                //Full name
    String dob;                 //Date of birth

    while (true)
    {
      // Input Available Username 
      while (true)
      {
        username = getUserInputString("Desired Username: ", 10);
        if (!esql.isUser(username))
        {
          break;
        }
        System.out.println("!! ERROR: Username taken !!");
      }//end while for username
      password = getUserInputString("Desired Password: ", 10);        //Input password 
      email = getUserInputString("Your Email Address: ");             //Input email 
      name = getUserInputString("Your Full Name: ", 50);              //Input name
      dob = getUserInputString("Your Date of Birth (YYYY/MM/DD) : "); //Input date of birth

      String sql = "INSERT INTO USR VALUES ('" + username + "','" + password + "','" + email + "','" + name + "','" + dob + "');";
      try 
      {
        esql.executeUpdate(sql);
        System.out.println("User craeated: "+ username + " " + password + " " + email + " " + name + " " + dob);
        esql.UpdateUserInfo(username);
        System.out.println("User logged in: "+username);
        break;
      }//end try
      catch(Exception e)
      {
        System.err.println(e.getMessage());
      }//end catch
    }//end while
  }//end SignUp()
    
  public static void LoginPrompt(ProfNetwork esql)
  {
    clrScreen();
    boolean done = false;

    System.out.println(
      "\n\n*******************************************************\n" +
      "                        Welcome      	               \n" +
      "*******************************************************\n");
    System.out.println("0. Sign Up!");
    System.out.println("1. Sign In");

    while (!done)
    {
      done = true;
      switch (getUserInputInt("Select option: ", 1))
      {
        case 0:
          SignUp(esql);
          break;
        case 1:
          Login(esql);
          break;
        default : 
          System.out.println("!! Invalid option !!"); 
          done = false;
          break;
      }//end switch 
    }//end while
  } //end LoginPrompt()

  //=============END: LOGIN AND SIGN UP==================================================================
  //=====================================================================================================
  //=============START: CHANGE PASSWORD==================================================================

  /*
  * Update user's password 
  */
  public static void ChangePassword(ProfNetwork esql)
  {
	  clrScreen();
	
    String password;

	  // Input Password
    password = getUserInputString("Desired Password (Enter to Cancel): ", 10);
    if (password.isEmpty())
    {
      return; 
    }

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

  public static void EditProfile(ProfNetwork esql)
  {
    getUserInputString("TODO: Edit Profile");
  }

  /*
  * Search for any user by username
  */
  public static void SearchName(ProfNetwork esql)
  {
    clrScreen();

    String name, sql;
    List<List<String>> result;

    while (true)
    {
      try 
      {
        name = getUserInputString("Search For (Name or Enter to Cancel): ");
        if (name.isEmpty())
        {
          break;
        }
        sql = "SELECT userId, name FROM USR WHERE name ILIKE '%" + name + "%';";
        result = esql.executeQueryAndReturnResult(sql);
        if (result.isEmpty())
        {
          System.out.println("No Results.");
        }
        for (List<String> sublist : result)
        {
          System.out.format("%-30s%-30s", sublist.get(0), sublist.get(1));
          System.out.println();
        }
      }
      catch(Exception e)
      {
        System.err.println(e.getMessage());
      }
    }
  }

  //=============END: CHANGE PASSWORD====================================================================
  //=====================================================================================================
  //=============BEGIN: FRIENDS==========================================================================

  /*
  * Send friend requests to people
  */
  public static void SendFriendRequests(ProfNetwork esql)
  {
    String connectionid, sql;
    int friendCount = 0; 

    System.out.println("* * * * Send New Friend Requests * * * *");

    //get number of friends 
    try
    {
      sql = "SELECT U.userId, U.name "
           +"FROM   USR U, CONNECTION_USR C "
           +"WHERE  U.userId != '" + esql.username + "' AND C.status='ACCEPT' "
                  + "AND ((U.userId = C.userId AND C.connectionId = '" + esql.username + "') "
                  +"OR (C.userId = '" + esql.username + "' AND C.connectionId = U.userId))";
      friendCount = esql.executeQuery(sql);  //number of friends
    }
    catch(Exception e)
    {
      System.err.println(e.getMessage());
      return;
    }

    while (true)
    {
      //get username to send request to
      while (true)
      {
        connectionid = getUserInputString("Send Request to (Enter to Cancel): "); 
        if (connectionid.equals(""))
        {
          return;
        }
        if (!esql.isUser(connectionid))
        {
          System.out.println("!! ERROR: User does not exist !!");
        }
        else if (esql.isConnected(esql, connectionid))
        {
          System.out.println("!! ERROR: Already connected with this user !!");
        }
        else
        {
          break;
        }
      }
      
      //if necessary, check the level of connections
      if (friendCount >= 5)
      {
        HashSet set = new HashSet();
        if (!CheckLimitedRange(esql, esql.username, connectionid, 3, set))
        {
          System.out.println("!! ERROR: User is not within third level of connections !!");
          continue;
        }
      }

      //send connection request to user
      try
      {
        sql = "INSERT INTO CONNECTION_USR VALUES ('" + esql.username + "','" + connectionid + "','REQUEST');";
        esql.executeUpdate(sql);
      }//end try
      catch(Exception e)
      {
        System.err.println(e.getMessage());
      }//end catch

      System.out.println("Successfully Sent Request to " + connectionid + "."); 
    }//end while
  }//end SendFriendRequests()

  /*
  * Return if current user is already connected username
  */
  public static boolean isConnected(ProfNetwork esql, String username)
  {
    String sql;
    int rowCount = 0;

    try
    {
      sql = "SELECT  * "
           +"FROM    CONNECTION_USR C "
           +"WHERE   (C.userId='" + esql.username + "' AND C.connectionId='" + username + "') "
                +"OR (C.userId='" + username + "' AND C.connectionId='" + esql.username + "')";
      rowCount = esql.executeQuery(sql);
    }
    catch(Exception e)
    {
      System.out.println(e.getMessage());
    }
    return rowCount > 0;
  }

  /*
  * Send friend requests to people within 3 levels
  */
  public static boolean CheckLimitedRange(ProfNetwork esql, String username, String connectionid, int maxLevel, HashSet set)
  {
    String sql;
    List<List<String>> range; 

    //System.out.println("CHECKING LEVEL");

    if (maxLevel < 0 || set.contains(username))
    {
      return false;
    }
    set.add(username); 

    try
    {
      sql = "SELECT U.userId, U.name "
           +"FROM   USR U, CONNECTION_USR C "
           +"WHERE  U.userId != '" + username + "' AND C.status='ACCEPT' "
                  + "AND ((U.userId = C.userId AND C.connectionId = '" + username + "') "
                  +"OR (C.userId = '" + username + "' AND C.connectionId = U.userId))";
      range = esql.executeQueryAndReturnResult(sql);
      for (List<String> sublist : range)
      {
        username = sublist.get(0).trim();
        if (username.equals(connectionid) || CheckLimitedRange(esql, username, connectionid, maxLevel-1, set))
        {
          //System.out.println("EQUALS " + username + " = " + connectionid);
          return true;
        }
        //System.out.println(maxLevel + " : " + username);
      }
    }
    catch(Exception e)
    {
      System.out.println(e.getMessage());
    }

    return false; 
  }

  /*
  * View and respond to friend requests
  */
  public static void ViewFriendRequests(ProfNetwork esql)
  {
    String sql, status; 
    List<List<String>> connectionRequests;
    int respondTo;

    System.out.println("* * * * Viewing New Friend Requests * * * *");

    try
    {
      sql = "SELECT C.userId FROM CONNECTION_USR C WHERE C.status='REQUEST' AND C.connectionid='" + esql.username + "';";
      connectionRequests = esql.executeQueryAndReturnResult(sql); 
    }
    catch(Exception e)
    {
      System.err.println(e.getMessage());
      return;
    }

    //if no requests 
    if (connectionRequests.isEmpty())
    {
      getUserInputString("No Friend Requests. Enter to Continue.");
      return; 
    }
    
    //list usernames of requests 
    for (int i = 0; i < connectionRequests.size(); ++i)
    {
      System.out.format("%-3d%s", i, connectionRequests.get(i).get(0));
      System.out.println();
    }

    //allow user to respond to friend requests
    while (true)
    {
      respondTo = getUserInputInt("Respond to (Enter to Cancel): ", connectionRequests.size()-1); 
      if (respondTo < 0)
      {
        return;
      }
      
      System.out.println("* * * * SELECT RESPONSE * * * *");
      System.out.println("0. Accept Request");
      System.out.println("1. Decline Request");
      switch(getUserInputInt("Select Option: ", 1))
      {
        case 0:   //accept
          status = "ACCEPT";
          break;
        case 1:   //decline
          status = "DECLINE";
          break; 
        default:
          return;
      }
      //update connection status
      try 
      {
        sql = "UPDATE CONNECTION_USR SET status='"+status+"' WHERE CONNECTION_USR.userId='"+connectionRequests.get(respondTo).get(0)+"' AND CONNECTION_USR.connectionId='"+esql.username+"'";
        esql.executeUpdate(sql);
      }
      catch(Exception e)
      {
        System.err.println(e.getMessage());
      }
    }

    
  }//end ViewFriendRequests

  /*
  * View friends list
  */
  public static void ViewFriendsList(ProfNetwork esql)
  {
    String sql; 
    List<List<String>> friendsList;
    int viewProfileOf;

    try
    {
      sql = "SELECT U.userId, U.name "
           +"FROM   USR U, CONNECTION_USR C "
           +"WHERE  U.userId != '" + esql.username + "' AND C.status='ACCEPT' "
                  + "AND ((U.userId = C.userId AND C.connectionId = '" + esql.username + "') "
                  +"OR (C.userId = '" + esql.username + "' AND C.connectionId = U.userId))";
      friendsList = esql.executeQueryAndReturnResult(sql); 
    }
    catch(Exception e)
    {
      System.err.println(e.getMessage());
      return;
    }

    //if no friends 
    if (friendsList.isEmpty())
    {
      getUserInputString("No Friends. Enter to Continue.");
      return; 
    }
    
    //list usernames of requests 
    for (int i = 0; i < friendsList.size(); ++i)
    {
      System.out.format("%-3d%-13s%s", i, friendsList.get(i).get(0), friendsList.get(i).get(1));
      System.out.println();
    }

    viewProfileOf = getUserInputInt("View Profile of (Enter to Continue): ", friendsList.size()-1); 
    if (viewProfileOf < 0)
    {
      return;
    }
    System.out.println("Profile of... " + friendsList.get(viewProfileOf).get(0) + ": " + friendsList.get(viewProfileOf).get(1));
    getUserInputString("Enter to Continue.");

  }//end ViewFriendsList
  
  //=============END: FRIENDS============================================================================
  //=====================================================================================================
  //=============BEGIN: MESSAGES=========================================================================

  /*
  * Send message to any user
  */
  public static void SendMessages(ProfNetwork esql)
  {
    clrScreen();

    String senderid = esql.username;        //Username of sender
    String receiverid;                      //Username of receiver (to be entered)
    String contents;                        //Contents of message (to be entered)
    String status = "UNREAD";               //Status of read or unread 
    int deleteStatus = 0;                   //Status of deletion 
    String sendTime = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date()); //Get current date and time in SQL format

    while (true)
    {
      //Get valid username to send to
      while (true)
      {
        receiverid = getUserInputString("Send To (Username or Enter to Cancel): ");
        if (receiverid.equals(""))
        {
          return; 
        }
        if (esql.isUser(receiverid))
        {
          break;
        }
        System.out.println("!! ERROR: Username does not exist !!");
      }
      contents = getUserInputString("Message: ");

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
    
      String sql = "INSERT INTO MESSAGE VALUES ('"+keyVal+"','"+ senderid +"','"+receiverid+"','"+contents+"','"+sendTime+"','"+deleteStatus+"','"+status+"');";

      try 
      {
        esql.executeUpdate(sql);
      }
      catch(Exception e)
      {
        System.err.println(e.getMessage());
      }
      System.out.println("Message sent to "+receiverid);
      System.out.println("* * * * NEXT MESSAGE * * * *");
	  }
  }

  /*
  * View all messages and delete messages
  */
  public static void ReadMessages(ProfNetwork esql)
  {
    String sql; 
    List<List<String>> messages;
    List<String> message;
    int toDelete = 0, newDeleteStatus = 0; 
    String msgid, senderid, receiverid, deleteStatus; 

    try
    {
      //get all messages to/from user
      sql = "SELECT * "
            +"FROM MESSAGE M "
            +"WHERE (M.senderId='"+esql.username+"' AND (M.deleteStatus=0 OR M.deleteStatus=2)) "
               +"OR (M.receiverId='"+esql.username+"' AND (M.deleteStatus=0 OR M.deleteStatus=1))"; 
      messages = esql.executeQueryAndReturnResult(sql);
      //update unread messages to read
      sql = "UPDATE MESSAGE SET status='READ' WHERE MESSAGE.receiverId='"+esql.username+"' AND STATUS='UNREAD'";
	    esql.executeUpdate(sql);
    }//end try
    catch(Exception e)
    {
      System.err.println(e.getMessage());
      return; 
    }//end catch

    System.out.println("* * * * Messages * * * *");
    
    //if there are no messages
    if (messages.isEmpty())
    {
      getUserInputString("No Messages");
      return; 
    }
    
    //output messages
    for (int i = 0; i < messages.size(); ++i)
    {

      List<String> sublist = messages.get(i);
      if (sublist.isEmpty())
      {
        continue;
      }
      //output index, timestamp, senderid, receiverid, read status, content
      System.out.format("%-2d%-25s%-13s%-13s%-13s%-15s", i, sublist.get(4).trim(), sublist.get(1).trim(), sublist.get(2).trim(), sublist.get(6).trim(), sublist.get(3).trim()); 
      System.out.println(); 
    }
    
    //prompt for messages to delete
    toDelete = getUserInputInt("Message to delete (Enter to Cancel): ", messages.size()-1);
    if (toDelete < 0)
    {
      return;
    }

    message = messages.get(toDelete);
    msgid = message.get(0).trim();
    senderid = message.get(1).trim(); 
    receiverid = message.get(2).trim(); 
    deleteStatus = message.get(5).trim();
    if (senderid.equals(esql.username))
    {
      newDeleteStatus = deleteStatus.equals("0") ? 1 
                        : deleteStatus.equals("2") ? 3 
                        : -1;
    }
    else if (receiverid.equals(esql.username))
    {
      newDeleteStatus = deleteStatus.equals("0") ? 2 
                        : deleteStatus.equals("1") ? 3 
                        : -1;
    }
    
    System.out.println(newDeleteStatus);
	  
    if (newDeleteStatus == -1)
    {
      return; 
    }

	  try 
    {
      sql = "UPDATE MESSAGE SET deleteStatus="+newDeleteStatus+" WHERE MESSAGE.msgId="+msgid;
	    esql.executeUpdate(sql);
    }
    catch(Exception e)
    {
	    System.err.println(e.getMessage());
	  }
  }

  //=============END: MESSAGES===========================================================================






  //=====================================================================================================
  //=====================================================================================================
  
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

}//end ProfNetwork

