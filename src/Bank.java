public class Bank 
{
	public void MainMenu() throws Exception
	{
		Customer cus ; 
		int ch = 1;
		while(true)
		{
			if(ch != 0)
			{
				Main.cls(); 
				System.out.print("1. Login\n2. SignUp \n0. Exit\n\nEnter Your Choice: ");
			}
			ch = Input.getInt(); 
			switch(ch)
			{
			case 1:
				cus = Customer.Login();
				if(cus == null)
				{
					System.out.println("\n\t\t\t*** Login Failed ***\n");
					Input.prompt();
					break ; 
				}
				if(cus.acList.size() == 0)
				{
					OpenAccount(cus); 
					Input.prompt();
				}
				
				HomeMenu(cus);
				break ; 
			case 2:
				cus = Customer.SignUp();
				if(cus == null)
				{
					System.out.println("\n\t\t\t*** SignUp Failed ***\n");
					Input.prompt();
					break ;
				}
				OpenAccount(cus); 
				Input.prompt();
				HomeMenu(cus);
				break ;
			case 0:
				System.exit(0);
			default:
				System.out.println("Please Enter Valid Choice !!!");
				ch = 0 ;
			}
		}
	}

	public void HomeMenu(Customer cus) throws Exception
	{
		Main.cls();
		System.out.println("\t Welcome ::: " + cus.username + "\n");	
		int ch = 1; 

		while(true)
		{
			System.out.print(
				"\n\t 1. Balance Enquiry\n" +
				"\t 2. Enroll Deposit \n" +
				"\t 3. Enroll WithDrawal \n" +
				"\t 4. Money Transfer \n" + 
				"\t 5. Account Statement \n" + 
				"\t 6. Open an Account \n" + 
				/*"\t 8. Close an Account \n" +*/
				//"\t 9. Preference \n" +
	 			"\t 0. Quit \n\n"  + 
				"Enter Your Choice: "
			);
			 
			ch = Input.getInt();

			switch(ch)
			{
			case 1:
				// Balance Enquire 
				balanceCheck(cus); 
				break ;
			case 2:
				// Deposit 
				Deposit(cus);
				break ;
			case 3:
				// withDrawal
				WithDrawn(cus);
				break ;

			case 4:
				// Money Transfer 
				MoneyTransfer(cus);
				break ;
			case 5:
				// Account Statement 
				AccountStatement(cus); 
				break ; 
			case 6:
				// Open an Account 
				OpenAccount(cus);
				break ;

			case 0:
				System.exit(0);
			default:
				System.out.println("Please Enter Valid Choice !!!");
				ch = 0 ;
			}
		}
	}

	public void balanceCheck(Customer cus) throws Exception
	{
		Main.cls();
		System.out.println(" ::: Balance Enquiry :::\n");
		if(cus.acList.size() == 0)
		{
			System.out.println("\t\t<<< You Have No Account >>>\n");
			return ; 
		}	
		cus.showAllAccounts(); 
	}


	public void Deposit(Customer cus) throws Exception
	{
		Main.cls();
		System.out.println("  ::: Deposit :::\n");
		int ac_No ; 
		if(cus.acList.size() == 0)
		{
			System.out.println("\t\t<<< You Have No Account >>>\n");
			return ; 
		}
		else if(cus.acList.size() == 1)
			ac_No = cus.acList.get(0).ac_No ; 
		else
		{
			cus.showAllAccounts(); 
			System.out.print("Enter Account Number : ");
			ac_No = Input.getInt();	
			if(ac_No == -999) return ; 
		}

		System.out.print("Enter the Amount : ");
		double amt =  Input.getDouble();
		
		if(amt == -999.0) return ; 
		if(amt < 0) 
		{
			System.out.println("Amount cannot be Negative");
			return  ; 
		}
			
		Account ac = cus.FindAccount(ac_No);
		if(ac == null)
		{
			System.out.println("\n *** Not Valid Account Number ***\n");
			return  ; 
		}
		else
		{
			ac.IncreaseBal(amt); 
			Account.MakeTransaction(ac.ac_No, 0, amt, ac.balance,  "Deposited"); 
			System.out.println("\t\t<<< Successfully Deposited >>>\n");
		}
	} 

	public void WithDrawn(Customer cus) throws Exception
	{
		Main.cls();
		System.out.println("  ::: WithDrawal :::\n");
		int ac_No ;
		double amt ; 
		Account ac ; 
		if(cus.acList.size() == 0)
		{
			System.out.println("\t\t<<< You Have No Account >>>\n");
			return  ; 
		}
		else if(cus.acList.size() == 1)
			ac_No = cus.acList.get(0).ac_No ; 
		else
		{
			cus.showAllAccounts(); 
			System.out.print("Enter Account Number : ");
			ac_No = Input.getInt();	
			if(ac_No == -999) return ; 
		}

		System.out.print("Enter the Amount : ");
		amt = Input.getDouble();
		
		if(amt == -999.0) return ; 
		if(amt < 0) 
		{
			System.out.println("Amount cannot be Negative");
			return  ; 
		}
			
		ac = cus.FindAccount(ac_No);
		if(ac == null)
		{
			System.out.println("\n *** Not Valid Account Number ***\n");
			return  ; 
		}


		if(ac.balance >= amt)
		{
			ac.IncreaseBal(-amt); 
			Account.MakeTransaction(ac.ac_No, amt, 0, ac.balance,  "Withdrawan"); 
			System.out.println("\t\t<<< Successfully Withdrawan >>>\n");
		}
		else
			System.out.println("\n *** Not Enough Money in your Account ***\n");
	}

	public void MoneyTransfer(Customer cus) throws Exception
	{
		Main.cls();
		System.out.println(" ::: Money Transfer :::\n");
		int ac_No ;
		double amt ; 
		Account ac, toAc ;
		if(cus.acList.size() == 0)
		{
			System.out.println("\t\t<<< You Have No Account >>>\n");
			return  ; 
		}
		else if(cus.acList.size() == 1)
			ac_No = cus.acList.get(0).ac_No ; 
		else
		{
			cus.showAllAccounts(); 
			System.out.print("Enter Account Number : ");
			ac_No = Input.getInt();	
			if(ac_No == -999) return ; 
		}

		System.out.print("Enter Receiver Account Number : ");
		int toAcNo = Input.getInt();
		
		if(toAcNo == -999) return ; 

		ac = cus.FindAccount(ac_No);
		if(ac == null || ac_No == toAcNo)
		{
			System.out.println("\n *** Not Valid Account Number ***\n");
			return  ; 
		}
 
		try
		{
			toAc = new Account(toAcNo); 
		}catch( Exception ex)
		{
			System.out.println("Not Valid Account Number");
			return  ; 
		}
		

		System.out.print("Enter the Amount : ");
		amt = Input.getDouble();
		
		if(amt == -999.0) return ; 
		if(amt < 0) 
		{
			System.out.println("Amount cannot be Negative");
			return  ; 
		}

		if(ac.balance < amt)
		{
			System.out.println("\n *** Not Enough Money in your Account ***\n");
			return  ; 
		}
		
		Account.MoneyTransfer(cus, ac, toAc, amt);
	}

	public void AccountStatement(Customer cus) throws Exception
	{
		Main.cls(); 
		System.out.print(" ::: Account Statement :::\n\n" + 
			"\t1. Current Month\n" +
			"\t2. Previous Month\n" + 
			"\t3. Specific Date\n" + 
			"\t4. Recent Transactions\n" + 
			"Enter Your Choice : " 
		);

		int ch1 = Input.getInt(); 
		String query = "" ; 
		switch(ch1)
		{
			case 1:
				query = "SELECT * FROM Transaction where " + 
						"MONTH(Date_of_Trans) = MONTH(CURRENT_DATE()) " +
						"AND YEAR(Date_of_Trans) = YEAR(CURRENT_DATE()) " +
						"And ac_no in (Select Ac_no from Account where Customer_ID = " + cus.user_ID + 
						") order by Date_of_Trans DESC;"; 
				Account.ViewTransaction(query);
				break ; 
			case 2:
				query = "SELECT * FROM Transaction where " + 
						"YEAR(Date_of_Trans) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH) " +
						"AND MONTH(Date_of_Trans) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) " +
						"And ac_no in (Select Ac_no from Account where Customer_ID =" + cus.user_ID +
						" ) order by Date_of_Trans DESC;" ; 
				Account.ViewTransaction(query);
				break ; 
			case 3:
				System.out.print("Enter the Start Date (YYYY-MM-DD) : ");
				String d1 = Input.getString(); 
				if(d1 == null) break ; 
				System.out.print("Enter the End Date (YYYY-MM-DD) : ");
				String d2 = Input.getString();
				if(d2 == null) break ; 
				query = "SELECT * FROM Transaction where Date_of_Trans " + 
						"BETWEEN '" + d1 + " 00:00:00' And '" + d2 + " 23:59:59' " + 
						"And ac_no in (Select Ac_no from Account where Customer_ID =" + cus.user_ID +
						" ) order by Date_of_Trans DESC;" ; 

				Account.ViewTransaction(query);
				break ; 
			case 4: 
				query = "Select * from Transaction where ac_no in " + 
						"(Select Ac_no from Account where Customer_ID =" + cus.user_ID + 
						") order by Date_of_Trans DESC limit 0,10;" ;
				Account.ViewTransaction(query);
				break ;
			default: 
				System.out.println("\t\t\t *** Not an Valid Choice *** \n");
		}
	}

	public void OpenAccount(Customer cus) throws Exception 
	{
		Account ac = Account.CreateAccount(cus);
		if(ac == null)
		{
			System.out.println("\n\t\t\t*** Account not Created ***\n");
			return ; 
		}
				
		cus.acList.add(ac); 
		System.out.println(Account.AcType[ac.ac_type-1] + " Created...\n");
		ac.AccountDetails();
	}

}
