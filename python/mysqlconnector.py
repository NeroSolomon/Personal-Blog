import mysql.connector
con = mysql.connector.connect(
  host="localhost",
  port="3306",
  user="root",
  password="Hello256",
  database="demo"
)

con.close()