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
				"\t 1. Check Pending Request\n" +
				"\t 2. All Check Details\n" +
	 			"\t 0. Quit \n\n"  + 
				"Enter Your Choice: "
			);
			ch = Input.getInt();
			
			switch(ch)
			{
			
			}
		}
	}

}
