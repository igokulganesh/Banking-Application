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
		String query = "Select * From Account where Ac_no = " + ac_No ;
		ResultSet rs = Sql.Select(query);

		if(!rs.next())
			throw new Exception() ;
		
		this.ac_No = ac_No ; 
		this.cus_id = rs.getInt("Customer_ID") ; 
		this.ac_type = rs.getInt("Ac_type");
		this.balance = rs.getDouble("balance");
		this.dateCreated = rs.getDate("date_created").toLocalDate() ;
		this.lastUsed = rs.getDate("last_used").toLocalDate(); 	
	}

	public Account(Customer cus, int ac_No) throws Exception
	{
		String query = "Select * From Account where Customer_ID = " + cus.user_ID + " and Ac_no = " + ac_No ;
		ResultSet rs = Sql.Select(query);

		if(!rs.next())
			throw new Exception() ;
		
		this.ac_No = ac_No ; 
		this.cus_id = cus.user_ID ; 
		this.ac_type = rs.getInt("Ac_type");
		this.balance = rs.getDouble("balance");
		this.dateCreated = rs.getDate("date_created").toLocalDate() ;
		this.lastUsed = rs.getDate("last_used").toLocalDate(); 
	}


	public static void MakeTransaction(int ac_No, double debit, double credit, double balance, String remarks) throws Exception
	{
		String query = "insert into Transaction(Ac_no, debit, credit, close_bal, Date_of_Trans, remarks) values(?, ?, ?, ?, now(), ?)";  		
		PreparedStatement stmt = Sql.con.prepareStatement(query);
		stmt.setInt(1, ac_No);
		stmt.setDouble(2, debit);
		stmt.setDouble(3, credit);
		stmt.setDouble(4, balance);
		stmt.setString(5, remarks);
		stmt.executeUpdate();				

		stmt.close();		
	}

	public static void ViewTransaction(String query) throws Exception 
	{
		ResultSet rs = Sql.Select(query);

		System.out.println(
			"\n\t\t\t ::: Account Statement ::: \n" +
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

		System.out.println("\n\t\\tt *** \n"); 
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


		System.out.println("\n <<< Transacion Details >>> \n");

		System.out.println(
			"From : " + cus.username +
			"\nAccount No: " + ac.ac_No +
			"\nTo : " + toAc.ac_No +
			"\nAmount : " + amt + 
			"\nBalance : " + ac.balance  +
			"\n\n \t\t *** \n"
		);
	}

	public void updateBalance()
	{
		String query = "Update Account set balance =" + this.balance +  " where Ac_no = " + this.ac_No ;
		Sql.Select(query);
	}

	public boolean viewAllAccount(Customer cus) throws Exception
	{

		String query = "Select * from Account where Customer_ID = " + cus.user_ID ;
		ResultSet rs = Sql.Select(query);

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

		System.out.println("\n\t\t\t *****\n");

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

	public static Account CreateAccount(Customer cus) throws Exception
	{
		Main.cls();
		System.out.println("\t\t ::: Create an Account :::\n");
		System.out.print(
			"\t1. Savings Account \n" +
			"\t2. Current Account \n" +
			"\n Choose Account Type: "
		);

		int ac_type = Input.getInt();

		if(ac_type > 2 || ac_type < 1)
		{
			System.out.println("\t\t\t **** Not Valid Choice ***\n");
			return null ; 
		}

		Account ac = new Account(); 
		String query = "insert into Account(customer_ID, Ac_type, date_created, last_used) values( " + cus.user_ID  + "," + ac_type + ", CURDATE(), CURDATE())";  		
		Sql.Update(query);

		ResultSet rs = Sql.Select( "SELECT LAST_INSERT_ID();");
		
		if(!rs.next())
		{
			System.out.println("Error Occurred While Creating Account...");
			return null ;
		}

		ac.ac_No = rs.getInt(1);
		ac.cus_id = cus.user_ID ; 
		ac.ac_type = ac_type ; 
		ac.dateCreated = LocalDate.now();
		ac.lastUsed = LocalDate.now();
		return ac ; 
	}

	public void IncreaseBal(double amt) throws Exception
	{
		if(amt <= 0)
		{
			System.out.println("InVaild Amount");
			return ;
		}
		double prev_bal = this.balance ;  
		this.balance = prev_bal + amt ; 
		String query = "Update Account set balance=" + this.balance + "where Ac_no=" + this.ac_No ; 
		Sql.Update(query); 
	}

	public void deleteAc() throws Exception
	{
		String query = "delete from Account where ac_no= " + ac_No ; 
		Sql.Select(query);	
	}

	public void withDrawal(double amt) throws Exception
	{
		if(amt <= 0)
		{
			System.out.println("InVaild Amount\n");
			return ; 
		}

		String query = "Select balance From Account where Ac_no =" + ac_No ; 
		ResultSet rs = Sql.Select(query);

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
		Sql.Update(query); 

		System.out.println("\n\t\t<<< Money withdrawan Successfully >>>\n");
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