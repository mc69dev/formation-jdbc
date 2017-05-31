package cdi.connections;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionDirecte {

	final String UrlConnSQLServer="jdbc:sqlserver://localhost;databaseName=northwind;user=mc;password=afip;";
	final String UrlConnMySQL="jdbc:mysql://localhost/northwind?user=root&password=afip";

	final String DriverNameSQLServer="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	final String DriverNameMySQL="com.mysql.jdbc.Driver";
	
	
	Connection connection=null; 
	public ConnectionDirecte(){
		try 
		{ 
			/* ****************************************
			 * *********Connection SQL SERVER**********
			 **************************************** */
			Class.forName(DriverNameSQLServer); 			
			connection=DriverManager.getConnection(UrlConnSQLServer);
			//connection=DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=northwind", "mc", "afip");
				  
			/* ****************************************
			 * ***********Connection MYSQL*************
			 **************************************** */
			//Class.forName(DriverNameMySQL); 			
			//connection=DriverManager.getConnection(UrlConnMySQL);
			
			System.out.println("Connection établie avec succès!");

			TestLectureSeule(connection);
			DisplayTables(connection);
			
			// Fermer Connection
			connection.close();
		} 
		catch (ClassNotFoundException e) 
		{ 
			System.out.println("Erreur pendant le chargement du pilote"); 
		} 	
		catch (SQLException e) 
		{ 
			System.out.println("Erreur pendant la connexion "+e.getMessage()); 
		} 
	}
	
	private void TestLectureSeule(Connection connection)
	{
		boolean etat;
		try
		{
			etat = connection.isReadOnly();
			connection.setReadOnly(!etat);
			if (connection.isReadOnly() != etat)
			{
				System.out.println("le mode lecture seule est pris en charge par ce pilote");
			}
			else
			{
				System.out.println("le mode lecture seule n\'est pas pris en charge par ce pilote");
			}
			connection.setReadOnly(etat);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	private void DisplayTables(Connection connection){
		
		DatabaseMetaData dmd;
		System.out.println("******************************************");
		try {
			dmd = connection.getMetaData();
			ResultSet tables = dmd.getTables(connection.getCatalog(),null,"%",new String[] {"TABLE"});
			
			
			System.out.println("*************Liste Tables :***************");
			while(tables.next()){
			   
			   for(int i=0; i<tables.getMetaData().getColumnCount();i++){				   
			      String nomColonne = tables.getMetaData().getColumnName(i+1);			      
			      Object valeurColonne = tables.getObject(i+1);			      
			      if(nomColonne.equals("TABLE_NAME"))
			    	  System.out.println(" => "+valeurColonne);
			   }
			  
			}
			 
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("******************************************");
	}
	public static void main(String[] args) {
		new ConnectionDirecte();
	}

}
