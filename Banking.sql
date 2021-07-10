Create database Bank ; 
show databases ;
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

Select * from User ;

Create Table Customer (
	customer_ID int Not Null primary key, 
    PAN varchar(20), 
    aadhar varchar(20), 
    Foreign key (customer_ID) References User(user_ID) ON DELETE CASCADE
);

insert into Customer(Customer_ID) values 
(1),
(2),
(3),
(4),
(5);

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

insert into Transaction(Ac_no, date_of_Trans) values (10000, '2021-06-30');
Select * from Transaction ;
SELECT * FROM Transaction where Date_of_Trans BETWEEN '2021-06-30 00:00:00' And '2021-07-02 23:59:59' 
And ac_no in (Select Ac_no from Account where Customer_ID =1 ) order by Date_of_Trans DESC;

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

Select * from Account; 
SELECT LAST_INSERT_ID();

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

Select * from CheckRecipt ;
Select * from CheckRecipt where toAc in (Select Ac_no from Account where Customer_ID = 2);

Insert into CheckRecipt
(fromAc, toAc, amt, isApproved, isPending) 
values(10000, 10002, 500, false, true);

update CheckRecipt 
set isApproved = true, isPending = false where id = 1;

select * from Transaction ;

select avg(balance) as Balance, customer_id from Account where Ac_no in 
(select Ac_no from Transaction group by ac_no )
group by customer_id order by balance desc ;

select yr, mon, avg(amt)
from (SELECT extract(year from tdate) as yr, extract(month from tdate) as mon,
             extract(day from tdate) as day,
             SUM(IIF(INOUT = 0, AMOUNT, -AMOUNT)) as amt
      FROM PETTYCASH
      WHERE TDATE < '2012-01-01'
      group by extract(year from tdate), extract(month from tdate),
               extract(day from tdate)
     ) t
group by yr, mon
order by yr, mon ;

Select * from Account ;
Select * from user ;
Select * from transaction ; 

select Ac_no, sum(debit) - sum(credit) as Sum from Transaction group by ac_no ;

Select username, count(Ac_no) from user inner join
Account on User.user_id = Account.customer_id 
group by User.user_id ; 

Select Customer_id, balance From Account group by Customer_id order by Balance desc ;

Select Customer_id, Count(Transaction.Ac_no) From Transaction 
inner join Account on Transaction.Ac_no = Account.Ac_no 
group by Customer_id ;

Select Customer_id, sum(credit) - sum(debit) as sum from Transaction 
inner join Account on Transaction.Ac_no = Account.Ac_no 
group by Customer_id order by sum desc ; 

Select Customer_id, Avg(close_bal) as Balance from Account inner Join
Transaction on Transaction.AC_no = Account.Ac_No group by Customer_Id  order by Balance desc ; 
 
Select customer_id, balance from account where ac_no in 
(select Ac_no, avg(close_bal) as balance  from Transaction group by ac_no order by balance desc) ; 

Select * from OpenBalance where ac_no in (select Ac_no from Account where Customer_ID = 2) order by Date_of_Trans DESC; 
Select * from account ;
Select * from Transaction where ac_no in (select Ac_no from Account where Customer_ID = 1) order by Date_of_Trans DESC; 

select * from user ;
select * from Account ;
Select * From Current_AC ;	
Select * From Savings_AC ;	
Select * from OpenBalance ;
Select * from Transaction ; 
Select * from CheckRecipt ;

Select * from Transaction 
order by Date_of_Trans DESC limit 0,10;

delete from Account where ac_no = 10000 ; 

Update Savings_AC set balance = 8000
where Ac_no = 10001 ; 

/* Specific date between*/
SELECT * FROM Transaction where Date_of_Trans 
BETWEEN '2021-06-30' AND '2021-07-01'
And ac_no in (Select Ac_no from Account where Customer_ID =1) 
order by Date_of_Trans DESC;

/* Current Month */
SELECT * FROM Transaction where 
MONTH(Date_of_Trans) = MONTH(CURRENT_DATE())
AND YEAR(Date_of_Trans) = YEAR(CURRENT_DATE())
And ac_no in (Select Ac_no from Account where Customer_ID =1)  
order by Date_of_Trans DESC;

/* Previous Month */
SELECT * FROM Transaction where 
YEAR(Date_of_Trans) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH)
AND MONTH(Date_of_Trans) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH)
And ac_no in (Select Ac_no from Account where Customer_ID =1) 
order by Date_of_Trans DESC;


Drop table user  ; 
Drop table Customer ;
Drop Table Employees ;
Drop table Account ;
Drop table Savings_AC ;
Drop table Current_AC ;
Drop table Recurring_Deposit ;
Drop table Fixed_Deposit ;
Drop table JoinAC ;
Drop table Loan ;
Drop table Transaction ;
Drop table OpenBalance ;
Drop table AC_Type ;