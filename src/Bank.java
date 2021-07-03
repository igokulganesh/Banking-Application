public class Bank 
{
	public void MainMenu() throws Exception
	{
		Main.cls(); 
		System.out.print("1. Login\n2. SignUp \n0. Exit\n\nEnter Your Choice: ");
		Customer cus ; 
		int ch = 0;
		while(ch == 0)
		{
			ch = Input.getInt(); 
			switch(ch)
			{
			case 1:
				cus = Customer.Login();
				if(cus == null)
					break ; 
				if(cus.acList.size() == 0)
					NewCustomer(cus);
				else
					HomeMenu(cus);
				break ; 
			case 2:
				cus = Customer.SignUp();
				if(cus == null)
					break ;
				NewCustomer(cus);
				break ;
			case 0:
				System.exit(0);
			default:
				System.out.println("Please Enter Valid Choice !!!");
				ch = 0 ;
			}
		}
	}

	public void NewCustomer(Customer cus) throws Exception
	{
		Main.cls(); 
		System.out.println(
			"\t\t\t ::: Welcome ::: " + cus.username + "\n\n" +
			"You have no Account\n"  
		);

		Account ac = OpenAccount(cus);
		System.out.println(Account.AcType[ac.ac_type-1] + " Created...\n");
		ac.AccountDetails();

		HomeMenu(cus); 
	}

	public void HomeMenu(Customer cus) throws Exception
	{
		Main.cls(); 
		System.out.println("\t Welcome ::: " + cus.username + "\n");
		
		int ch = 1; 
		Account ac = new Account() ;
		int ac_No ;
		double amt ; 
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
				Main.cls();
				System.out.println(" ::: Balance Enquiry :::\n");
				ac.viewAllAccount(cus);
				break ;
			case 2:
				// Deposit 
				Main.cls();
				System.out.println(" ::: Deposit :::\n");
				
				if(!ac.viewAllAccount(cus))
					break ; 

				System.out.print("Enter Account Number : ");
				ac_No = Input.getInt();
				
				if(ac_No == -999) break; 

				System.out.print("Enter the Amount : ");
				amt =  Input.getDouble();
				
				if(amt == -999.0) break; 
				if(amt < 0) 
				{
					System.out.println("Amount cannot be Negative");
					break ; 
				}

				try
				{
					ac = new Account(cus, ac_No);
					ac.Deposit(amt); 
				}
				catch(Exception ex)
				{
					System.out.println("\n *** Not Valid Account Number ***\n");
				}
				break ;
			case 3:
				// withDrawal
				Main.cls();
				System.out.println(" ::: WithDrawal :::\n");
				
				if(!ac.viewAllAccount(cus))
					break ; 

				System.out.print("Enter Account Number : ");
				ac_No = Input.getInt();
				
				if(ac_No == -999) break; 

				System.out.print("Enter the Amount : ");
				amt =  Input.getDouble();
				
				if(amt == -999.0) break; 
				if(amt < 0) 
				{
					System.out.println("Amount cannot be Negative");
					break ; 
				}
				
				try
				{
					ac = new Account(cus, ac_No);
					ac.withDrawal(amt);
				}
				catch(Exception ex)
				{
					System.out.println("\n *** Not Valid Account Number ***\n");
				}				

				break ;
			case 4:
				// Money Transfer 
				Main.cls();
				System.out.println(" ::: Money Transfer :::\n");
				if(!ac.viewAllAccount(cus))
					break ; 
				
				System.out.print("Enter Your Account Number : ");
				ac_No = Input.getInt();
				
				if(ac_No == -999) break; 

				System.out.print("Enter Receiver Account Number : ");
				int toAcNo = Input.getInt();
				
				if(toAcNo == -999) break; 
				
				System.out.print("Enter the Amount : ");
				amt =  Input.getDouble();
				
				if(amt == -999.0) break; 
				if(amt < 0) 
				{
					System.out.println("Amount cannot be Negative");
					break ; 
				}
				
				Account.MoneyTransfer(cus, ac_No, toAcNo, amt);

				break ;
			case 5:
				// Account Statement 
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
						System.out.print("Enter the End Date (YYYY-MM-DD) : ");
						String d2 = Input.getString();

						query = "SELECT * FROM Transaction where Date_of_Trans " + 
								"BETWEEN '" + d1 + "' And '" + d2 + "' " + 
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
				break ; 
			case 6:
				// Open an Account 
				ac = OpenAccount(cus);
				Main.cls();
				System.out.println(Account.AcType[ac.ac_type-1] + " Created...\n");
				ac.AccountDetails();
				System.out.println();
				break ;

			case 0:
				System.exit(0);
			default:
				System.out.println("Please Enter Valid Choice !!!");
				ch = 0 ;
			}
		}
	}

	public Account OpenAccount(Customer cus) throws Exception
	{
		Main.cls();
		System.out.println("\t\t ::: Create an Account :::\n");
		System.out.print(
			"\t1. Savings Account \n" +
			"\t2. Current Account \n" +
/*			"\t3. Fixed Deposit \n" + 
			"\t4. Recurrent Deposit \n" + */
			" Choose Account Type: "
		);

		int ch = Input.getInt();

		if(ch > 2 || ch < 1)
		{
			System.out.println("\t\t\t **** Not Valid Choice ***\n");
			return null ; 
		}

		Account ac = new Account(); 
		ac.CreateAccount(cus, ch);
		return ac ; 
	}

}
