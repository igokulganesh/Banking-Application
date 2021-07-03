import java.time.* ; 
import java.sql.*; 

public class Account 
{

	static String AcType[] = {"Savings Account", "Current Account", "Fixed Deposit", "Recurring Deposit", "Loan" } ; 
	
	public int ac_No ;
	public int cus_id ;
	public int ac_type ;
	public double balance ; 
	public LocalDate dateCreated ;
	public LocalDate lastUsed ;
	
	public Account(){}

	public Account(int ac_No) throws Exception
	{
		Connection con = Main.connect() ;
		Statement stmt = con.createStatement();

		String query = "Select * From Account where Ac_no = " + ac_No ;
		ResultSet rs = stmt.executeQuery(query);

		if(!rs.next())
			throw new Exception() ;
		
		this.ac_No = ac_No ; 
		this.cus_id = rs.getInt("Customer_ID") ; 
		this.ac_type = rs.getInt("Ac_type");
		this.balance = rs.getDouble("balance");
		this.dateCreated = rs.getDate("date_created").toLocalDate() ;
		this.lastUsed = rs.getDate("last_used").toLocalDate(); 

		stmt.close();
		con.close();	
	}

	public Account(Customer cus, int ac_No) throws Exception
	{
		Connection con = Main.connect() ;
		Statement stmt = con.createStatement();

		String query = "Select * From Account where Customer_ID = " + cus.user_ID + " and Ac_no = " + ac_No ;
		ResultSet rs = stmt.executeQuery(query);

		if(!rs.next())
			throw new Exception() ;
		
		this.ac_No = ac_No ; 
		this.cus_id = cus.user_ID ; 
		this.ac_type = rs.getInt("Ac_type");
		this.balance = rs.getDouble("balance");
		this.dateCreated = rs.getDate("date_created").toLocalDate() ;
		this.lastUsed = rs.getDate("last_used").toLocalDate(); 

		stmt.close();
		con.close();
	}

	public static void UpdateOpenBalance(int acNo, double prev_bal, double bal, String remarks) throws Exception
	{
		Connection con = Main.connect(); 
		String query = "insert into OpenBalance(ac_no, prev_bal, bal, remarks, date_of_trans) values (?, ?, ?, ?, now());" ; 
		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setInt(1, acNo); 
		stmt.setDouble(2, prev_bal);
		stmt.setDouble(3, bal);
		stmt.setString(4, remarks); 
		stmt.executeUpdate(); 

		stmt.close();
		con.close();
	}

	public static void ViewOpenBalance(Customer cus) throws Exception
	{
		Connection con = Main.connect() ;
		Statement stmt = con.createStatement();

		String query = "Select * from OpenBalance where ac_no in (select Ac_no from Account where Customer_ID=" + cus.user_ID + ") order by Date_of_Trans DESC;" ;
		ResultSet rs = stmt.executeQuery(query);

		System.out.println(
			"| Ac Number | Previous Balance | Actual Balance |   Date \t |   Remarks  |\n"
		);
		while(rs.next())
		{
			int ac_No = rs.getInt("Ac_no");
			double prev_bal = rs.getDouble("prev_bal");
			double bal = rs.getDouble("bal");
			String remarks = rs.getString("remarks");

			System.out.println(ac_No + "\t\t" + prev_bal + "\t\t" + bal + "\t\t" + rs.getDate("Date_of_Trans") + " " + rs.getTime("Date_of_Trans")  + "\t" + remarks);
		}

		stmt.close();
		con.close(); 
	}

	public static void MakeTransaction(int ac_No, double debit, double credit, double balance, String remarks) throws Exception
	{
		Connection con = Main.connect() ;
		String query = "insert into Transaction(Ac_no, debit, credit, close_bal, Date_of_Trans, remarks) values(?, ?, ?, ?, now(), ?)";  		
		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setInt(1, ac_No);
		stmt.setDouble(2, debit);
		stmt.setDouble(3, credit);
		stmt.setDouble(4, balance);
		stmt.setString(5, remarks);
		stmt.executeUpdate();				

		stmt.close();
		con.close();
		
	}

	public static void ViewTransaction(String query) throws Exception 
	{
		Connection con = Main.connect() ;
		Statement stmt = con.createStatement();

		ResultSet rs = stmt.executeQuery(query);

		System.out.println(
			"\n ::: Account Statement ::: \n" +
			"\n\n|    Date    \t|\t Ac Number \t|   Debit  \t|   Credit \t|  Balance  \t|  Remarks  \t|\n"
		);
		while(rs.next())
		{
			int ac_No = rs.getInt("Ac_no");
			double debit = rs.getDouble("debit");
			double credit = rs.getDouble("credit");
			double bal = rs.getDouble("close_bal");
			String remarks = rs.getString("remarks");

			System.out.println(rs.getDate("Date_of_Trans") + " " + rs.getTime("Date_of_Trans") + " \t| " + ac_No + " \t| " + debit + "\t\t" + credit + "\t\t" + bal  + "\t\t" + remarks);
		}

		System.out.println("\n\t\t *** \n"); 
		stmt.close();
		con.close(); 
	}

	public static void MoneyTransfer(Customer cus, int ac_No, int toAcNo, double amt) throws Exception
	{
		Account ac, toAc ; 
		try
		{
			ac = new Account(cus, ac_No);
			toAc = new Account(toAcNo);
		}
		catch(Exception ex)
		{
			System.out.println("\t*** InVaild Account Number ***\n");
			return ; 
		}

		double from_prev_bal = ac.getBalance();
		if(amt > from_prev_bal)
		{
			System.out.println("\t*** Your Account has Not Enough Money ***\n");
			return ;
		}

		double to_prev_bal = toAc.getBalance(); 
		ac.balance = from_prev_bal - amt ; 
		toAc.balance = to_prev_bal + amt ; 

		ac.updateBalance();
		toAc.updateBalance();

		MakeTransaction(ac.ac_No, amt, 0, ac.balance,  "Money Transfered to " + toAc.ac_No);
		MakeTransaction(toAc.ac_No, 0, amt, toAc.balance, "Money Received From " + ac.ac_No);

/*
		UpdateOpenBalance(ac.ac_No, from_prev_bal, ac.balance, "Money Transfered"); 
		UpdateOpenBalance(toAc.ac_No, to_prev_bal, toAc.balance, "Money Received"); 
*/
		System.out.println("\n <<< Transacion Details >>> \n");

		System.out.println(
			"From : " + cus.name +
			"\nAccount No: " + ac.ac_No +
			"\nTo : " + toAc.ac_No +
			"\nAmount : " + amt + 
			"\nBalance : " + ac.balance  +
			"\n\n \t\t *** \n"
		);
	}

	public void updateBalance() throws Exception
	{
		Connection con = Main.connect() ;
		String query = "Update Account set balance = ? where Ac_no = ?" ;

		PreparedStatement stmt = con.prepareStatement(query);

		stmt.setDouble(1, this.balance);
		stmt.setInt(2, this.ac_No);
		stmt.executeUpdate();

		stmt.close(); 
		con.close();
	}

	public boolean viewAllAccount(Customer cus) throws Exception
	{
		Connection con = Main.connect() ;
		Statement stmt = con.createStatement();

		String query = "Select * from Account where Customer_ID = " + cus.user_ID ;
		ResultSet rs = stmt.executeQuery(query);

		if(!rs.next())
		{
			System.out.println("\n <<< You Have No Account >>>\n");
			return false ; 
		}

		System.out.println(
			"\n\n | Ac Number | Account Type | Balance |\n"
		);

		do{
			ac_No = rs.getInt("Ac_no");
			ac_type = rs.getInt("Ac_type");
			balance = rs.getDouble("balance");
			System.out.println(" " + ac_No + " " + AcType[ac_type-1] + "\tRs. " + balance);
		}while(rs.next()); 

		System.out.println("\n\t\t *****\n");


		stmt.close(); 
		con.close(); 
		return true ; 
	}
	
	public void AccountDetails()
	{
		System.out.println(
			" ::: Account Details :::\n" +
			"\n Account Number = " + this.ac_No +
			"\n Account Type = " + AcType[this.ac_type-1] +
			"\n Balance = Rs. " + balance + 
			"\n Date Created = " + dateCreated + 
			"\t last Accessed = " + lastUsed  
		);
	}

	public void CreateAccount(Customer cus, int ac_type) throws Exception
	{
		Connection con = Main.connect() ;
		String query = "insert into Account(customer_ID, Ac_type, date_created, last_used) values(?, ?, CURDATE(), CURDATE())";  		
		PreparedStatement stmt = con.prepareStatement(query);
		stmt.setInt(1, cus.user_ID);
		stmt.setInt(2, ac_type);
		stmt.executeUpdate();

		ResultSet rs = stmt.executeQuery( "SELECT LAST_INSERT_ID();");
		
		if(!rs.next())
		{
			System.out.println("Error Occurred While Creating Account...");
			System.exit(0); 
		}

		this.ac_No = rs.getInt(1);
		this.cus_id = cus.user_ID ; 
		this.ac_type = ac_type ; 
		this.dateCreated = LocalDate.now();
		this.lastUsed = LocalDate.now();

		stmt.close(); 
		con.close();
	}

	public void Deposit(double amt) throws Exception
	{
		if(amt <= 0)
		{
			System.out.println("InVaild Amount");
			return ;
		}

		Connection con = Main.connect(); 
		Statement stmt = con.createStatement();

		String query = "Select balance From Account where Ac_no =" + ac_No ; 
		ResultSet rs = stmt.executeQuery(query);

		rs.next(); 
		double prev_bal = rs.getDouble("balance"); 

		this.balance = prev_bal + amt ; 

		MakeTransaction(this.ac_No, 0, amt, this.balance,  "Deposited"); 

		query = "Update Account set balance=" + this.balance + "where Ac_no=" + this.ac_No ; 
		stmt.executeUpdate(query); 

		System.out.println("\n<<< Successfully Deposited >>>\n");

		stmt.close(); 
		con.close(); 
	}

	public void deleteAc() throws Exception
	{
		Connection con = Main.connect(); 
		Statement stmt = con.createStatement(); 

		String query = "delete from Account where ac_no= " + ac_No ; 
		stmt.executeUpdate(query);

		stmt.close(); 
		con.close(); 	
	}

	public void withDrawal(double amt) throws Exception
	{
		if(amt <= 0)
		{
			System.out.println("InVaild Amount\n");
			return ; 
		}

		Connection con = Main.connect(); 
		Statement stmt = con.createStatement(); 

		String query = "Select balance From Account where Ac_no =" + ac_No ; 
		ResultSet rs = stmt.executeQuery(query);

		rs.next(); 
		double prev_bal = rs.getDouble("balance"); 

		if(prev_bal < amt )
		{
			System.out.println("Not Enough Money you have\n");
			return ; 
		}

		this.balance = prev_bal - amt ; 

		MakeTransaction(this.ac_No, amt, 0, this.balance,  "withdrawan");

		query = "Update Account set balance=" + this.balance + "where Ac_no=" + this.ac_No ; 
		stmt.executeUpdate(query); 

		System.out.println("\n<<< Money withdrawan Successfully >>>\n");

		stmt.close(); 
		con.close(); 
	}

	public double getBalance()
	{
		return balance ; 
	}
}

/*
class Savings_Ac extends Account 
{
	public double interest ;
}

class Currect_Ac extends Account 
{
	public double overDraft ;
}

class Fixed_Deposit extends Account 
{
	public double initial_amt ; 
	public double interest ; 
	public LocalDate duration ;
}

class Recurring_Deposit extends Account
{
	public double initial_amt ; 
	public double interest ; 
	public LocalDate duration ;
}
*/