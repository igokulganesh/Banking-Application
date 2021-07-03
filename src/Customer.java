import java.sql.*;
import java.util.*;

public class Customer 
{
	public int user_ID ;
	public String username ; 
	public String password ; 
	public ArrayList<Account> acList ;

	public ArrayList<Account> getAllAccounts(int id) throws Exception
	{
		acList = new ArrayList<Account>() ;
		String query = "Select * From Account where Customer_ID = " + id ;
		ResultSet rs = Sql.Select(query); 

		while(rs.next())
		{
			Account ac = new Account() ; 
			ac.ac_No = rs.getInt("Ac_no");
			ac.ac_type = rs.getInt("Ac_type");
			ac.balance = rs.getDouble("balance");
			ac.cus_id = id ; 
			acList.add(ac) ; 
		}

		return acList ; 
	}

	public void showAllAccounts()
	{
		System.out.println("\n | Ac Number | Account Type | Balance |\n");

		Account ac ; 
		for (int i = 0; i < acList.size(); i++)
		{
			ac = acList.get(i);
            System.out.println(" " + ac.ac_No + " " + Account.AcType[ac.ac_type-1] + "\tRs. " + ac.balance);
		}
		System.out.println("\n\t\t\t*****\n");
	}

	public Account FindAccount(int acNo)
	{
		for (int i = 0; i < acList.size(); i++)
		{
			if(acList.get(i).ac_No == acNo )
				return acList.get(i) ;
		}	
		return null ;
	}

	public static Customer getCustomer(String uname, String pass) throws Exception
	{ 
		Customer cus = new Customer(); 
		String query = "Select * from User where username=? and password=?" ;  
		PreparedStatement stmt = Sql.con.prepareStatement(query);
		stmt.setString(1, uname);
		stmt.setString(2, pass);
		ResultSet rs = stmt.executeQuery();

		if(rs.next())  
		{
			cus.user_ID = rs.getInt("user_ID");
			cus.username = rs.getString("username") ; 
			cus.password = rs.getString("password");
		}
		else    
		   return null ; 

		cus.acList = cus.getAllAccounts(cus.user_ID);
		return  cus ; 
	} 

	public static Customer getCustomer(int id) throws Exception
	{ 
		Customer cus = new Customer(); 
		String query = "select * from User where user_ID=" + id ;  
		ResultSet rs = Sql.Select(query);

		if(rs.next())  
		{
			cus.user_ID = rs.getInt("user_ID");
			cus.username = rs.getString("username") ; 
			cus.password = rs.getString("password");
		}
		else    
		   return null ; 

		cus.acList = cus.getAllAccounts(cus.user_ID);
		return  cus ; 
	} 

	public static Customer Login() throws Exception
	{
		Main.cls(); 
		System.out.println("\t\t\t ::: Login :::\n");  

		System.out.print("Enter Your Username : ");
		String username = Input.getString(); 
		
		System.out.print("Enter Your Password : ");
		String password = Input.getPassword();

		Customer cus = getCustomer(username, password) ;

		if(cus == null)  
		   System.out.println("Invalid username or/and password...\n\n");   
		
		return cus ; // also returns null
	}

	public static Customer SignUp() throws Exception
	{
		Main.cls();

		System.out.println("\t\t\t ::: SignUp :::\n");
		
		System.out.print("Enter the Username : ");
		String username = Input.getString();

		System.out.print("Enter the Password : ");
		String password = Input.getPassword();

		String query = "Insert into User(username, password) VALUES (?, ?)";
		PreparedStatement stmt;
		Customer cus = null ; 

		try {
			stmt = Sql.con.prepareStatement(query);
			stmt.setString(1, username); 
			stmt.setString(2, password); 
			stmt.executeUpdate();

			cus = getCustomer(username, password); 

			if(cus == null)
			{
				System.out.println("Customer not Created");
				return null ; 
			}

			query = "Insert into Customer(Customer_ID) values (" + cus.user_ID + ");" ; 
			Sql.Update(query); 

			stmt.close(); 
		}
		catch(Exception e)
		{
			System.out.println("Username " + username + " Already Exists");
		}

		return cus ; 
	}
}
