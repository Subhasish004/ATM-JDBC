package ATMSystem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
// Subhasish 

class Account {
	public static boolean ops = false;
	static double balance;
static Scanner sc = new Scanner(System.in);
// Methods 
public static  String  CreateAccountNumber() {
    	Random random = new Random();
	    String numbers = "0123456789";
	    StringBuilder sb = new StringBuilder();
	    int length=5;
	    for(int i = 0; i < length; i++) {
	    	int index = random.nextInt(numbers.length());
	        char randomChar = numbers.charAt(index);
	        sb.append(randomChar);
	      }
	    String accountNumber = sb.toString();
	    return accountNumber;	
	}
  //Display account details
public static void AccountDetails(Statement statement,String PhoneNumber)throws SQLException {
		String query="Select * from bankaccount "
				+ "join customer on bankaccount.account_number  = customer.account_number "
				+ "join card on customer.account_number  = card.account_number "
				+ "where customer.customer_phone_number  = ?";
		PreparedStatement preparedStatement = statement.getConnection().prepareStatement(query);  
        // Set the parameter in the prepared statement
        preparedStatement.setString(1, PhoneNumber);
        // Execute the query
        ResultSet resultset = preparedStatement.executeQuery();
		if (resultset.next()) {
			System.out.println("#########################################################");
            System.out.println("Account Number : " + resultset.getString(1));
            System.out.println("Account type : " + resultset.getString(2));
            System.out.println("Name : " + resultset.getString(4));
            System.out.println("Address : " + resultset.getString(5));
            System.out.println("Phone number : " + resultset.getString(6));
            System.out.println("Card Number : " + resultset.getString(8));
            System.out.println("Expiration Date : " + resultset.getString(9));
			System.out.println("#########################################################");
        }else{
        	System.out.println("Invalid Phone Number !!");
        }
 }//Details
//Create A account  by input details 
public static int CreateAccount(Statement statement)throws SQLException,NullPointerException{
	try{
	 @SuppressWarnings("resource")
	Scanner sc = new Scanner(System.in);
	 System.out.println("Customer Name : ");
	 Customer.customer_name = sc.next();
	 System.out.println("Input Address ");
	 Customer.address = sc.next();
	 System.out.println("Input Contact Number");
     Customer.contact = sc.next();
	 System.out.println("Input Account Type \n1 -> Savings Account"
	 		+ "\nDefault -> Zerobalance Account");
    	int  AccChoice = sc.nextInt();
    	String setAccountType="";
    	if (AccChoice == 1 ){
    		setAccountType="Savings Account";
    	}else {
    		setAccountType="Zerobalance Account";
    	}
    System.out.println("Input Card Expiry date Formart {yyyy-mm-dd}");
    String expiryDate = sc.next();
    System.out.println("Enter 4 digit PIN");
	String  pin = sc.next();
	Double Balance = 0.00;
	String accountNumber = CreateAccountNumber();
	String cardNumber = Card.CreateCardNumber();
     // SQL statement to insert a new account
     String insertAccountSQL = "INSERT INTO bankaccount VALUES (?,?,?)";
     String insertCustomerSQL = "INSERT INTO customer VALUES (?,?,?,?)";
     String insertCardSQL = "INSERT INTO card VALUES (?,?,?,?)";
     String insertLoginSQL = "INSERT INTO login VALUES (?,?)";
     // Prepare Account
     PreparedStatement preparedAccount =statement.getConnection()
    		 .prepareStatement(insertAccountSQL);
     preparedAccount.setString(1, accountNumber);
     preparedAccount.setString(2, setAccountType);
     preparedAccount.setDouble(3, Balance);
     int r1 = preparedAccount.executeUpdate();
    // System.out.println("Account Update Status -> "+ r1);
     //account
     //Prepare customer
     PreparedStatement preparedCustomer =statement.getConnection()
    		 .prepareStatement(insertCustomerSQL);
     preparedCustomer.setString(1, Customer.customer_name);
     preparedCustomer.setString(2, Customer.address);
     preparedCustomer.setString(3, Customer.contact);
     preparedCustomer.setString(4, accountNumber);
     int r2 = preparedCustomer.executeUpdate();
     //System.out.println("Customer Update Status -> "+ r2);
     //customer
     //Prepare Card
     PreparedStatement preparedCard =statement.getConnection()
    		 .prepareStatement(insertCardSQL);
     preparedCard.setString(1, cardNumber);
     preparedCard.setString(2, expiryDate);
     preparedCard.setString(3, pin);
     preparedCard.setString(4, accountNumber);
     int r3 = preparedCard.executeUpdate();
     //System.out.println("Account Update Status -> "+ r3);
     //Card
     //Update Login Table 
     PreparedStatement preparedLogin=statement.getConnection()
    		 .prepareStatement(insertLoginSQL);
     preparedLogin.setString(1, accountNumber);
     preparedLogin.setString(2, pin);
     int r4 = preparedLogin.executeUpdate();
     //System.out.println("Account Log In  Status -> "+ r4);
     //------------------------------------------------------------------
     System.out.println("<<+++NOTE \n ACCOUNT NUMBER AND PIN \n USE FOR LOG IN+++>>");
     AccountDetails(statement, Customer.contact);
     return (r1+r2+r3+r4);
	}//try
	catch(SQLException e) {
        e.printStackTrace(); // Handle exceptions properly in a real application
        return 0;
    }
}//End Create ACCOUNT

//Update  Balance
public static boolean updateAccountBalance(Statement statement , String accNumber , double amount)throws Exception{
	String sql = " update bankaccount set balance =  balance + ? where account_number = ?";
	int record = 0;
	try(PreparedStatement ps = statement.getConnection().prepareStatement(sql)){
		ps.setDouble(1, amount);
		ps.setString(2, accNumber);
		record = ps.executeUpdate();
		System.out.println(record);
	}
	if (record == 1)
		return true;
	else
		return false;
}//Update balance
//Deposite 
public static void deposit(Connection con,Statement statement, String accNumber) throws Exception {
	System.out.println("Enter Deposite Amount");
	Transaction.amount = sc.nextDouble();
	boolean status = updateAccountBalance(statement, accNumber, Transaction.amount);
	if(status){
		Transaction.balance= Balance(statement, accNumber);
		Transaction.type = "Deposite";
		AddTransactions(statement, accNumber,Transaction.amount,Transaction.balance,Transaction.type);
		con.commit();
	}else{
		con.rollback();
		System.out.println("Deposite Failed");
	}
	}//deposit
//Withdraw
public static void withdraw(Connection con,Statement statement, String accNumber) throws Exception {
	System.out.println("Enter Withdraw Amount");
	Transaction.amount = sc.nextDouble();
	balance= Balance(statement, accNumber);
	if (Transaction.amount <= balance){
	boolean status= updateAccountBalance(statement, accNumber, -Transaction.amount);
		if (status){
			balance= Balance(statement, accNumber);
			Transaction.type = "WithDraw";
			AddTransactions(statement, accNumber,Transaction.amount,balance,Transaction.type);
			con.commit();
		}else{
			con.rollback();
			System.out.println("Withdraw Failed");
		}
	}else{
		System.out.println("Insufficeint Balance");
	}
    }//withdraw
 //Transaction details
public static void TransactionStatement(Statement statement, String accNumber) throws Exception {
	String sql = "select * from transactions where account_number = ?";
	 PreparedStatement ps = statement.getConnection().prepareStatement(sql);
	 ps.setString(1, accNumber);
	 ResultSet rs = ps.executeQuery();
	while(rs.next()){
		System.out.println("Transiction ID : "+ rs.getInt(1)+"  Account Number :  "+rs.getString(2)
		+"  "+rs.getString(3)
		+"  Amount : Rs"+rs.getDouble(4)+"  TimeStamp : "+ rs.getString(5)+"  Balance : Rs"+ rs.getDouble(6)
		+"\n============================================================================================");
	}
    }//print trans

//Add Transaction to DB
public static void AddTransactions(Statement statement,String AccNumber,double amount , double balance,String type) throws Exception{
	 String insertTranscSQL = "INSERT INTO transactions (account_number, transaction_type ,amount, balance) "
	 		+ "VALUES (?, ?, ?, ?)";
	 PreparedStatement preparedStatement =statement.getConnection().prepareStatement(insertTranscSQL);
     preparedStatement.setString(1, AccNumber);
     preparedStatement.setString(2, type);
     preparedStatement.setDouble(3, amount);
     preparedStatement.setDouble(4, balance);
     preparedStatement.executeUpdate();
		//System.out.println("Records Inserted:  "+ records);
		String sql = "SELECT *  FROM transactions "
				+ " WHERE account_number = ?"
				+ " ORDER BY time_stamp DESC "
				+ " LIMIT 1";
		 PreparedStatement ps = statement.getConnection().prepareStatement(sql);
		 ps.setString(1, AccNumber);
		 ResultSet rs = ps.executeQuery();
		while(rs.next()){
			System.out.println("Transiction ID : "+ rs.getInt(1)+"  Account Number :  "+rs.getString(2)
			+"  "+rs.getString(3)
			+"  Amount : Rs"+rs.getDouble(4)+"  TimeStamp : "+ rs.getString(5)+"  Balance : Rs"+ rs.getDouble(6)+
			"\n========================================"
			+ "====================================================");
		}
}//add trans
//Transfer fund
public static void transferMoney(Connection con,Statement statement, String fromAccID )throws Exception{
	System.out.println("Enter account number to transfer ");
	String toAccID = sc.next();
	System.out.println("Enter Amount to transfer ");
	Transaction.amount = sc.nextDouble();
	double Frombalance= Balance(statement, fromAccID);
	if (Transaction.amount  <= Frombalance){
		boolean r1 = updateAccountBalance(statement , fromAccID , - Transaction.amount );
		boolean r2 = updateAccountBalance(statement , toAccID , Transaction.amount );
		if ((r1 && r2)==true){
			String FromType = "Transfered to : "+toAccID;
			String ToType ="Recieved from : "+fromAccID;
			Frombalance= Balance(statement, fromAccID);
			double Tobalance= Balance(statement, toAccID);
			AddTransactions(statement, fromAccID,Transaction.amount ,Frombalance,FromType);
			AddTransactions(statement, toAccID,Transaction.amount ,Tobalance,ToType);
			con.commit();
			System.out.println("Transaction sucessful ");
		}else{
			con.rollback();
			System.out.println("Transaction Failed !");
		}
	}//
	else{
		System.out.println("Insufficeint Balance");
	}
}//TF

//Log in Operations
public static void Operations(Connection con,Statement statement,String AccNumber) throws Exception {
		System.out.println("Enter your choice ");
		System.out.println("1 - View Balance ");
		System.out.println("2 -  Deposite ");
		System.out.println("3 -  Withdraw ");
		System.out.println("4 -  Transfer Money");
		System.out.println("5 - View Transactions ");
		System.out.println("6 - Log Out ");
		try{
		String choice = ATMSystem.getUserChoice();
		switch(choice){
		case "1":
			 @SuppressWarnings("unused")
			 double b=Balance(statement,AccNumber);
			break;
		case "2":
			deposit(con,statement, AccNumber);
			break;
		case "3" :
			withdraw(con,statement, AccNumber);
			break;
		case "4" :
			transferMoney(con,statement, AccNumber);
			break;
		case "5":
			TransactionStatement(statement, AccNumber);
			break;
		case "6":
			ops=false;
			break;
		default:
				System.out.println("Wrong Choice ");
				break;
		}
		}catch(InputMismatchException in){
			ops=false;
			in.printStackTrace();
		}
	}

private static double Balance(Statement statement, String accNumber)throws SQLException {
try{
		String balanceSQL="Select balance from bankaccount where account_number =?";
		PreparedStatement ps = statement.getConnection().prepareStatement(balanceSQL);  
        ps.setString(1, accNumber);
        ResultSet resultset = ps.executeQuery();
        if (resultset.next()) {
        	 balance = resultset.getDouble(1);
        	System.out.println("======================");
        	System.out.println("Account : "+accNumber);
        	System.out.println("Balance : Rs" +  balance);
        	System.out.println("======================");
        }
}catch(Exception e){e.printStackTrace();}
        return balance;
	
	}//Balance

}//class