Use bank ;

select avg(balance) as Balance, customer_id from Account where Ac_no in 
(select Ac_no from Transaction group by ac_no )
group by customer_id order by balance desc ;

/* Monthly Avg balance score */
Select Customer_id, Avg(close_bal)/100000 as Score from Account A inner Join
Transaction T on T.AC_no = A.Ac_No 
where MONTH(T.Date_of_Trans) = MONTH(CURRENT_DATE())
AND YEAR(T.Date_of_Trans) = YEAR(CURRENT_DATE())
group by Customer_Id order by Score desc ; 

Select * from transaction where Ac_no in (10005, 10006, 10007) ; 
Select * from Account where customer_id = 6; 

/* Total Credit */
Select Customer_id, sum(credit)/200000 as sum from Transaction T
inner join Account A on T.Ac_no = A.Ac_no 
group by Customer_id order by sum desc ; 

/* Preferrable Customers */
Select Customer_id, ((Avg(close_bal)/500000) + (sum(credit)/100000)) as Score, Count(T.Ac_no) as TransactionCount
from Account A inner Join Transaction T on T.AC_no = A.Ac_No 
where MONTH(T.Date_of_Trans) = MONTH(CURRENT_DATE())
AND YEAR(T.Date_of_Trans) = YEAR(CURRENT_DATE())
group by Customer_Id 
order by Score desc, TransactionCount desc limit 10 ;

/* No.of transaction */
Select Customer_id, Count(Transaction.Ac_no) as Count From Transaction 
right join Account on Transaction.Ac_no = Account.Ac_no 
group by Customer_id order by count desc ;

select * from user ; 
Select * from Account where customer_id = 3 ; 
Select * from transaction ; 
