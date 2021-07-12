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

/* Total Credit */
Select Customer_id, sum(credit)/200000 as sum from Transaction T
inner join Account A on T.Ac_no = A.Ac_no 
group by Customer_id order by sum desc ; 

/* Preferrable Customers */
Select Customer_id, (Avg(close_bal)/100000) + (sum(credit)/200000) as Score from Account A inner Join
Transaction T on T.AC_no = A.Ac_No 
where MONTH(T.Date_of_Trans) = MONTH(CURRENT_DATE())
AND YEAR(T.Date_of_Trans) = YEAR(CURRENT_DATE())
group by Customer_Id order by Score desc limit 5 ; 

