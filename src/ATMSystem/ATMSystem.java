package ATMSystem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
//Subhasish 

public class ATMSystem {
	static Scanner sc = new Scanner(System.in);
	static boolean main_ops=true;
    public static void main(String[] args) throws Exception {
    	//Connection====================================
    	String url = "jdbc:mysql://localhost:3306/atmsystem";
		String userName = "root";
		String password = "root";
		try{
			//Load the JDBC driver
			Class.forName("com.mysql.cj.jdbc.Driver");
			//Establish  a connection
				Connection con = DriverManager
						.getConnection(url, userName, password);
				System.out.println("Connection Established");
				//Create a Statement
				Statement statement=con.createStatement();
				con.setAutoCommit(false);
		//===============================================
    	System.out.println("********WELCOME TO OUR BANKING SYSTEM************");
		
		while(main_ops){
			System.out.println("+++Enter Your Choice : +++++++++++ ");
			System.out.println("0 - Create Account ");
			System.out.println("1 - Display your Account ");
			System.out.println("2 - Log In ");
			System.out.println("3 - Exit");
			System.out.print("Enter your choice (0-3): ");
		String choice = getUserChoice();
		//Switch--------------------------------------------------
		switch(choice){
		case "0":
			int status = Account.CreateAccount(statement);
			if (status ==4 ){
				con.commit();
			System.out.println("Account Create Sucessfully ");}
			else{
				con.rollback();
				System.out.println("Failed !! Enter Valid Information");
			}
			break;
		case "1":   
			System.out.println("Enter Your Phone Number");
			String phone_number  = sc.next();
			Account.AccountDetails(statement,phone_number);
        break; 		
		case "2":
			Login(con,statement);
            break;  	
		case "3":
			con.close();
			main_ops = false;
			System.exit(0);
			break;
		default: 
			System.out.println("Wrong Option");
			break;
			}//end switch
		}//end while	
		}
		catch (SQLException  e ) {
	    	e.printStackTrace();
	    }
		catch(Exception e){
			e.printStackTrace();
		}  
    }
    static String getUserChoice() {
	   String choice="";
	    try {
	         choice = sc.next();
	    } catch(Exception e){
			e.printStackTrace();
		}  
	    return choice;
    }//user coice
    private static void Login(Connection con,Statement statement){
    	try{
    	System.out.println("Enter your Account number - ");
    	String Acc_number = sc.next();
    	System.out.println("Enter your Pin - ");
    	String pass =sc.next();
		 String query="Select * from login where account_number = ?";
      // Create a prepared statement
		 PreparedStatement ps = statement.getConnection().prepareStatement(query);  
		 ps.setString(1, Acc_number);
		 ResultSet resultset = ps.executeQuery();
      if (resultset.next()) {
        if(pass.equals(resultset.getString(2))){
        	Account.ops=true;
        	while(Account.ops)
        		Account.Operations(con,statement,Acc_number);}
        else
        	System.out.println("Incorrect Pin !!");
      }else{
    	  System.out.println("Invalid Account Number !!");
      }
      }catch(Exception e){
    	  e.printStackTrace();
      }
    	
	}//login
}//class 