import java.sql.*;

public class Employee extends User
{
	public static Employee Login() throws Exception
	{
		Main.cls(); 
		System.out.println("\t\t\t ::: Staff Login :::\n");  

		System.out.print("Enter Your Username : ");
		String username = Input.getString(); 
		
		System.out.print("Enter Your Password : ");
		String password = Input.getPassword();

		Employee emp = getEmployee(username, password) ;

		if(emp == null)  
		   System.out.println("Invalid username or/and password...\n\n");   
		
		return emp ; // also returns null
	}

	public static Employee getEmployee(String uname, String pass) throws Exception
	{
		String query = "Select * from User where username = '" + uname + "' and password = '" + pass + "' and is_Staff = 1"; 
		ResultSet rs = Sql.Select(query);

		Employee emp = null ; 
		if(rs.next())  
		{
			emp = new Employee(); 
			emp.user_ID = rs.getInt("user_ID");
			emp.username = rs.getString("username") ; 
			emp.password = rs.getString("password");
		} 
		return emp ; 
	}

	public void menu() throws Exception
	{
		Main.cls(); 
		System.out.println("\t Welcome ::: " + username + "\n");	

		int ch = 1 ; 

		while(true)
		{
			System.out.print(
				"\n\t 1. Check Pending Request\n" +
				"\t 2. All Check Receipt\n" +
				"\t 3. Transaction Request\n" + 
				"\t 4. Transaction Details\n" +
				"\t 5. Valuable Customer\n" + 
	 			"\t 0. Back \n\n"  + 
				"Enter Your Choice: "
			);
			ch = Input.getInt();
			
			switch(ch)
			{	
				case 1:
					// Check Pending Request  
					Main.cls();
					System.out.println(
						"\t\t ::: Check Request ::: \n\n" + 
						"| Id | From Ac | To Ac | Amount | isApproved |\n" 
					);
					
					if(!PendingCheck())
					{
						System.out.println("\t\t *** Currently No Pending Request *** ");
						break ; 
					}
					System.out.print("\nEnter the Id to verify : ");
					int id = Input.getInt(); 
					VerifyCheck(id);
					break ;
				case 2:
					// All Check 
					Main.cls();
					System.out.println(
						"\t\t ::: All Check Receipt ::: \n\n" + 
						"| Id | From Ac | To Ac | Amount | Status |\n" 
					);
					PendingCheck();
					ApprovedCheck();
					System.out.println("\t\t\t ***\n");
					break ;
				case 3: 
					// Transaction Request
					Main.cls();
					System.out.println(
						"\t\t ::: Transaction Request ::: \n\n" + 
						"| Id | From Ac | To Ac | Amount | isApproved |\n" 
					);
					
					if(!PendingTrans())
					{
						System.out.println("\t\t *** Currently No Pending Request *** ");
						break ; 
					}
					System.out.print("\nEnter the Id to verify : ");
					id = Input.getInt(); 
					VerifyTrans(id);

					break ; 
				case 4:
					// All Transaction 
					Main.cls();
					System.out.println(
						"\t\t ::: Check Request ::: \n\n" + 
						"| Id | From Ac | To Ac | Amount | isApproved |\n" 
					);
					PendingTrans();
					ApprovedTrans();
					break ; 
				case 5:
					// Preferred Customers 
					Main.cls();
					System.out.println(	
						"\t\t\t ::: Valuable Customer ::: \n\n" + 
						"| Customer ID \t| Points |\n"
					);

					String query = 
					"Select Customer_id, (Avg(close_bal)/100000) + (sum(credit)/200000) as Score " +
					"from Account A inner Join Transaction T on T.AC_no = A.Ac_No " +
					"where MONTH(T.Date_of_Trans) = MONTH(CURRENT_DATE()) " + 
					"AND YEAR(T.Date_of_Trans) = YEAR(CURRENT_DATE()) " + 
					"group by Customer_Id order by Score desc limit 5 ;" ; 

					ResultSet rs = Sql.Select(query);

					while(rs.next())
					{
						id = rs.getInt(1);
						double bal = rs.getDouble(2);
						System.out.println(" " + id + " \t\t| " + bal );
					}

					break ; 
				case 0: 
					return ; 
				default:
					System.out.println("Please Enter Valid Choice !!!");
			}
		}
	}

	public void VerifyCheck(int id) throws Exception
	{
		ResultSet rs = Sql.Select("Select * from CheckRecipt where isPending = true and id = " + id) ;

		if(!rs.next())
		{
			System.out.println("Not Valid Entry");
			return ; 
		}

		int fromAcNo = rs.getInt("fromAc");
		int toAcNo = rs.getInt("toAc");
		double amt = rs.getDouble("amt");

		Account fromAc = new Account(fromAcNo);
		Account toAc = new Account(toAcNo); 
		String query ; 
		if(fromAc.balance < amt)
		{
			query = "Update CheckRecipt set isApproved = false, isPending = false where id = " + id ; 
			Sql.Update(query);
			System.out.println(
				"\n\t *** Not Enough Money in Sender Account ***\n" + 
				"\t<<< Request Rejected >>>\n");
			return ;
		}

		Customer fromCus = Customer.getCustomer(fromAc.cus_id) ;
		Customer toCus = Customer.getCustomer(toAc.cus_id) ; 

		System.out.print(
			"From : " + fromCus.username + " ( " + fromAcNo + " )\n" +
			"To : " + toCus.username + " ( " + toAcNo + " )\n" +
			"Amount : Rs. " + amt + "\n" +
			"\nDo you accept the Request (Y/N) ? "
		);
		char ch = Input.getChar();

		if(ch != 'y' && ch != 'Y')
		{
			query = "Update CheckRecipt set isApproved = false, isPending = false where id = " + id ; 
			Sql.Update(query);
			System.out.println("\t<<< Request Rejected >>>\n");
			return ; 
		}

		fromAc.IncreaseBal(-amt); 
		toAc.IncreaseBal(amt); 

		Account.MakeTransaction(fromAc.ac_No, amt, 0, fromAc.balance, "Money Transfered to (" + toAc.ac_No  + ") " + toCus.username + " through Check" );
		Account.MakeTransaction(toAc.ac_No, 0, amt, toAc.balance, "Money Received From (" + fromAc.ac_No  + ") " + fromCus.username + " through Check" );
	
		query = "Update CheckRecipt set isApproved = true, isPending = false where id = " + id ; 
		Sql.Update(query);
		System.out.println("\t<<< Request Accepted >>>\n");
	}

	public boolean PendingCheck() throws Exception
	{
		ResultSet rs = Sql.Select("Select * from CheckRecipt where isPending = true") ;
		boolean notEmpty = false ; 
		while(rs.next())
		{
			int id = rs.getInt("id");
			int fromAc = rs.getInt("fromAc");
			int toAc = rs.getInt("toAc");
			double amt = rs.getDouble("amt");
			boolean isaproved = rs.getBoolean("isApproved");
			boolean isPending = rs.getBoolean("isPending");

			System.out.print(id + " \t " + fromAc + " \t " + toAc + " \t " + amt + " \t ");
			if(isPending)
				System.out.println("Pending");
			else if(isaproved)
				System.out.println("Accepted");
			else
				System.out.println("Rejected");
			notEmpty = true ; 
		}
		return notEmpty ;
	}

	public boolean PendingTrans() throws Exception
	{
		ResultSet rs = Sql.Select("Select * from TransactionReq where isPending = true") ;
		boolean notEmpty = false ; 
		while(rs.next())
		{
			int id = rs.getInt("id");
			int fromAc = rs.getInt("fromAc");
			int toAc = rs.getInt("toAc");
			double amt = rs.getDouble("amt");
			boolean isaproved = rs.getBoolean("isApproved");
			boolean isPending = rs.getBoolean("isPending");

			System.out.print(id + " \t " + fromAc + " \t " + toAc + " \t " + amt + " \t ");
			if(isPending)
				System.out.println("Pending");
			else if(isaproved)
				System.out.println("Accepted");
			else
				System.out.println("Rejected");
			notEmpty = true ; 
		}
		return notEmpty ;
	}

	public boolean ApprovedCheck() throws Exception
	{
		ResultSet rs = Sql.Select("Select * from CheckRecipt where isPending = false") ;
		boolean notEmpty = false ; 
		while(rs.next())
		{
			int id = rs.getInt("id");
			int fromAc = rs.getInt("fromAc");
			int toAc = rs.getInt("toAc");
			double amt = rs.getDouble("amt");
			boolean isaproved = rs.getBoolean("isApproved");
			boolean isPending = rs.getBoolean("isPending");

			System.out.print(id + " \t " + fromAc + " \t " + toAc + " \t " + amt + " \t ");
			if(isPending)
				System.out.println("Pending");
			else if(isaproved)
				System.out.println("Accepted");
			else
				System.out.println("Rejected");

			notEmpty = true ; 
		}
		return notEmpty ; 
	}

	public boolean ApprovedTrans() throws Exception
	{
		ResultSet rs = Sql.Select("Select * from TransactionReq where isPending = false") ;
		boolean notEmpty = false ; 
		while(rs.next())
		{
			int id = rs.getInt("id");
			int fromAc = rs.getInt("fromAc");
			int toAc = rs.getInt("toAc");
			double amt = rs.getDouble("amt");
			boolean isaproved = rs.getBoolean("isApproved");
			boolean isPending = rs.getBoolean("isPending");

			System.out.print(id + " \t " + fromAc + " \t " + toAc + " \t " + amt + " \t ");
			if(isPending)
				System.out.println("Pending");
			else if(isaproved)
				System.out.println("Accepted");
			else
				System.out.println("Rejected");

			notEmpty = true ; 
		}
		return notEmpty ; 
	}

	public void VerifyTrans(int id) throws Exception
	{
		ResultSet rs = Sql.Select("Select * from TransactionReq where isPending = true and id = " + id) ;

		if(!rs.next())
		{
			System.out.println("Not Valid Entry");
			return ; 
		}

		int fromAcNo = rs.getInt("fromAc");
		int toAcNo = rs.getInt("toAc");
		double amt = rs.getDouble("amt");

		Account fromAc = new Account(fromAcNo);
		Account toAc = new Account(toAcNo); 
		String query ; 

		Customer fromCus = Customer.getCustomer(fromAc.cus_id) ;
		Customer toCus = Customer.getCustomer(toAc.cus_id) ; 

		System.out.print(
			"From : " + fromCus.username + " ( " + fromAcNo + " )\n" +
			"To : " + toCus.username + " ( " + toAcNo + " )\n" +
			"Amount : Rs. " + amt + "\n" +
			"\nDo you accept the Request (Y/N) ? "
		);
		char ch = Input.getChar();

		if(ch != 'y' && ch != 'Y')
		{
			query = "Update TransactionReq set isApproved = false, isPending = false where id = " + id ; 
			Sql.Update(query);
			fromAc.IncreaseBal(amt); 	
			Account.MakeTransaction(fromAc.ac_No, 0, amt, fromAc.balance, "Money Transaction Cancelled Refund");
			System.out.println("\t<<< Request Rejected >>>\n");
			return ; 
		}

		
		toAc.IncreaseBal(amt); 
		Account.MakeTransaction(toAc.ac_No, 0, amt, toAc.balance, "Money Received From " + fromCus.username +  " (" + fromAc.ac_No  + ")");
		query = "Update TransactionReq set isApproved = true, isPending = false where id = " + id ; 
		Sql.Update(query);
		System.out.println("\t<<< Request Accepted >>>\n");
	}

}
