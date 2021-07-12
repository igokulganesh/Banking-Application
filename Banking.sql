Create database Bank ; 
use bank ;

create Table User(
	user_ID int Not NUll primary key auto_increment, 
    username varchar(50) Not Null unique, 
    password varchar(50) Not Null, 
    FullName varchar(50), 
    PhNumber varchar(10), 
    Email_ID varchar(50), 
    Address varchar(100), 
    gender char(1), 
    is_Staff bool default false,
    is_authenticated boolean, 
    last_login datetime, 
    date_joined datetime
);

INSERT INTO User (username, password, FullName, PhNumber, Email_ID, Address, Gender, Date_joined) VALUES 
('gokul', '1234', 'Gokul', '9876543210', 'igokulganesh@gmail.com', 'Madurai', 'M', now()), 
('ganesh', '1234', 'Gokul Ganesh', '9876543210', 'igokulganesh@gmail.com', 'Madurai', 'M', '2021-06-24'), 
('pavi', '1234', 'Pavi', '9876543210', 'igokulganesh@gmail.com', 'Madurai', 'F', '2021-06-24'), 
('Kanya', '1234', 'I Kamura Kani', '9876543210', 'igokulganesh@gmail.com', 'Madurai', 'F', '2021-06-24'), 
('Iyyappan', '1234', 'Iyyappan D', '9876543210', 'igokulganesh@gmail.com', 'Madurai', 'M', '2021-06-24');

INSERT INTO User (username, password, is_Staff) VALUES 
('emp1', '1234', true), 
('emp2', '1234', true), 
('emp3', '1234', true); 

Create Table Customer (
	customer_ID int Not Null primary key, 
    PAN varchar(20), 
    aadhar varchar(20), 
    Foreign key (customer_ID) References User(user_ID) ON DELETE CASCADE
);

insert into Customer(Customer_ID) values (1), (2), (3), (4), (5);

Create Table Employees(
	emp_ID int not null primary key, 
    salary decimal(12, 2), 
	user_ID int, 
    
    Foreign key (user_ID) References User(user_ID) ON DELETE CASCADE
);

Create Table AC_Type(
	Type_ID int not null primary key, 
    type_name varchar(30)
);

insert into AC_Type values
(1, 'Savings'), 
(2, 'Current'),
(3, 'Fixed Deposit'), 
(4, 'Recurrent Deposit'),
(5, 'Loan'),
(6, 'Join'); 

Create Table Account(
	Ac_no bigint unsigned AUTO_INCREMENT not null primary key, 
    customer_ID int, 
    Ac_type int,
    Balance decimal(20, 2), 
	date_created date, 
    last_used date, 
    Status bool, 
    foreign key (customer_ID) References Customer(customer_ID) ON DELETE CASCADE,
    foreign key (Ac_type) References AC_Type(Type_ID)
);

alter table Account auto_increment=10000;

Create Table Savings_AC(
	Ac_no bigint unsigned not null primary key, 
    interest decimal(4, 2), 
     
    foreign key (Ac_no) References Account(Ac_no) ON DELETE CASCADE 
);

Create Table Current_AC(
	Ac_no bigint unsigned not null primary key,
    OverDraft decimal(20, 2),
    
    foreign key (Ac_no) References Account(Ac_no) ON DELETE CASCADE
);

Create Table Fixed_Deposit(
	Ac_no bigint unsigned not null primary key, 
    Initial_Amt decimal(20, 2), 
    interest decimal(4, 2),
    duration date,

    foreign key (Ac_no) References Account(Ac_no) ON DELETE CASCADE
);

Create Table Recurring_Deposit(
	Ac_no bigint unsigned, 
    interest decimal(4, 2),
    duration date, 

    foreign key (Ac_no) References Account(Ac_no) ON DELETE CASCADE
);

Create Table JoinAC(
	AC_ID int not null primary key, 
	Ac_no bigint unsigned, 
    customer_ID int, 
    
    Foreign key (Ac_no) References Account(Ac_no) ON DELETE CASCADE, 
    Foreign key (customer_ID) References Customer(customer_ID) ON DELETE CASCADE
);

Create table Loan(
	Ac_no bigint unsigned not null primary key,  
    interest decimal(4, 2), 
    Description varchar(50), 
    status boolean, 
    Total_Amt decimal(20, 2), 
    duration date, 
    Date_of_loan date, 
    
	Foreign key (Ac_no) References Account(Ac_no) ON DELETE CASCADE
);

Create Table Transaction(
	Trans_ID int not null primary key auto_increment,
    Ac_no bigint unsigned, 
    debit decimal(20, 2), 
    credit decimal(20, 2),
    Close_bal decimal(20, 2),
    Date_of_Trans datetime, 
    remarks varchar(50),
    
	foreign key (Ac_no) References Account(Ac_no)  ON DELETE Set Null
);

Create Table CheckRecipt(
	id int  not null primary key auto_increment,
    fromAc bigint unsigned, 
    toAc bigint unsigned, 
	amt decimal(20, 2), 
    isApproved bool, 
    isPending bool,
    
	foreign key (fromAc) References Account(Ac_no) ON DELETE Set Null,
	foreign key (toAc) References Account(Ac_no) ON DELETE Set Null
);

Create Table TransactionReq
(
	id int  not null primary key auto_increment,
    fromAc bigint unsigned, 
    toAc bigint unsigned, 
	amt decimal(20, 2), 
    isApproved bool, 
    isPending bool,
    
	foreign key (fromAc) References Account(Ac_no) ON DELETE Set Null,
	foreign key (toAc) References Account(Ac_no) ON DELETE Set Null
);
