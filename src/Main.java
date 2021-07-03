import java.sql.*;
import java.util.*;
import java.io.*;

public class Main 
{
	public static void cls() throws Exception
	{
		new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); 
		System.out.println("=========================================");
		System.out.println("           Banking Application");
		System.out.println("=========================================\n");
	}

	public static void main(String[] args) throws Exception
	{
		Bank bank = new Bank(); 
		bank.MainMenu();
	}
}

class Sql
{
	static Connection con = null ;
	static Statement stmt = null; 
	static
	{
		try 
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank","root","root");
			stmt = con.createStatement();
		}
		catch(Exception e)
		{
			System.out.println("Database Connection Error");
			System.exit(0);
		}
	}

	public static ResultSet Select(String query)
	{
		try {
			return stmt.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}	

	public static void Update(String query)
	{
		try {
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

class Input
{
	static Scanner sc = new Scanner(System.in); 

	public static void prompt() // Dummy input
	{
		System.out.println("Press \"Enter key\" to continue...");
		sc.nextLine(); 
	}

	public static int getInt()
	{
		String ip = sc.nextLine(); 

		try 
		{
			return Integer.parseInt(ip);
		}
		catch (NumberFormatException e) 
		{
			System.out.println("You didn't enter a valid Number.");
			return -999 ; 
		}
	}

	public static double getDouble()
	{
		String ip = sc.nextLine();

		try
		{
			return Double.parseDouble(ip);
		}
		catch (NumberFormatException e) 
		{
			System.out.println("You didn't enter a valid Number.");
			return -999 ; 
		}
	}	

	public static String getString()
	{	
		String str = sc.nextLine(); 
		return str ;
	}

	

	public static char getChar()
	{	
		String str = sc.nextLine();
		try
		{
			return str.charAt(0);
		}
		catch (Exception e) 
		{
			System.out.println("You didn't enter a valid Character.");
			return 'n' ; 
		}
	}

	public static String getPassword()
	{
		Console console = System.console();
		String password = new String(console.readPassword()); 
		return password ; 
	}
}