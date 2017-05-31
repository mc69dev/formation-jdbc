package cdi.connections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;


/*
--UPDATE Categories SET CategoryName='JAVA' WHERE CategoryID=1
--UPDATE Products SET UnitPrice=UnitPrice*1.2
 */

public class JdbcTests {
	final static String UrlConnSQLServer="jdbc:sqlserver://localhost;databaseName=northwind;user=mc;password=afip;";
	final static String UrlConnMySQL="jdbc:mysql://localhost/world?user=root&password=";

	final static String DriverNameSQLServer="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	final static String DriverNameMySQL="com.mysql.jdbc.Driver";

	static Connection connection=null; 
	static int server = 2; //1: SQL Server | 2 : MySQL
	public static void main(String[] args) {

		//Connection Base Donn�es
		ConnectionBD();

		//TestLectureSeule();
		//ChangerBaseDonees("northwind");	
		//InfosBase();

		//ExecuteStatementVersions("SELECT * FROM Products");
		//TestExecute();
		//TestExecuteBatch();

		//TestExecuteMultiple();

		//TestPreparedStatement();
		//TestCallableStatement();

		//TestInfosResulset();

		//lectureRs();
		//modificationRs();
		//suppressionRs();
		//ajoutRs();

		//TestTransactions();
		
		//DisplayTables();

		try {
			if(connection!=null && !connection.isClosed()){
				connection.close(); // Fermer Connection
				System.out.println("Connection ferm�e avec succ�s!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	public static void ConnectionBD(){
		try 
		{ 
			if(server==1){
				/* ****************************************
				 * *********Connection SQL SERVER**********
				 **************************************** */
				Class.forName(DriverNameSQLServer); 			
				connection=DriverManager.getConnection(UrlConnSQLServer);
				//connection=DriverManager.getConnection("jdbc:sqlserver://localhost;databaseName=northwind", "mc", "afip");
			}else{
				/* ****************************************
				 * ***********Connection MYSQL*************
				 **************************************** */
				Class.forName(DriverNameMySQL); 			
				connection=DriverManager.getConnection(UrlConnMySQL);
			}
			System.out.println("Connection �tablie avec succ�s! [Base Donn�es : "+connection.getCatalog().toUpperCase()+"]");
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

	private static void TestLectureSeule()
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
	private static void DisplayTables(){

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
	private static void ChangerBaseDonees(String BdName){
		try {
			System.out.println("Base actuelle : " +connection.getCatalog());
			System.out.println("Changement de base de donn�es");
			connection.setCatalog(BdName);
			System.out.println("Base actuelle : " +connection.getCatalog());

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private static void InfosBase(){
		ResultSet rs;
		DatabaseMetaData dbmd;

		try
		{
			dbmd=connection .getMetaData();

			System.out.println("Type de base : " +	dbmd.getDatabaseProductName());
			System.out.println("Version: " +		dbmd.getDatabaseProductVersion());
			System.out.println("Nom du pilote : " + dbmd.getDriverName());
			System.out.println("Version du pilote : " +		dbmd.getDriverVersion());
			System.out.println("Nom de l\'utilisateur : " +		dbmd.getUserName());
			System.out.println("Url de connexion : " + dbmd.getURL());

			rs=dbmd.getTables(null,null,"%",null);
			System.out.println("structure de la base");
			System.out.println("Base\t\t Schema\t Nom Table\t Type Table");

			while(rs.next())
			{
				for (int i = 1; i <=4 ; i++)
				{
					System.out.print(rs.getString(i)+"\t");
				}
				System.out.println();
			}
			rs.close();

			rs=dbmd.getProcedures(null,null,"%");
			System.out.println("Les Proc�dures Stock�es");
			System.out.println("Base\t Schema\t Nom Procedure");
			while(rs.next())
			{
				for (int i = 1; i <=3 ; i++)
				{
					System.out.print(rs.getString(i)+"\t");
				}
				System.out.println();
			}
			rs.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private static void ExecuteStatementVersions(String requete){
		Statement stm;
		ResultSet rs;

		try {

			//stm = connection.createStatement();			
			stm = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

			boolean resultat = stm.execute(requete);
			if (resultat)
			{
				System.out.println("Votre instruction a g�n�r�	un jeu d'enregistrements");
				rs=stm.getResultSet();

				rs.last(); //Erreur si connection.createStatement() : d�filement en avant!! 

				System.out.println("Il contient " + rs.getRow() + " enregistrements");
			}


		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void TestExecute() {
		Statement stm;
		BufferedReader br;
		String requete;
		ResultSet rs;
		boolean resultat;
		try
		{
			stm=connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

			br=new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Saisir votre instruction SQL :");
			System.out.print("===>");
			requete=br.readLine();
			resultat=stm.execute(requete);
			if (resultat)
			{
				System.out.println("Votre instruction a g�n�r� un jeu d'enregistrements");
				rs=stm.getResultSet();
				rs.last();
				System.out.println("Il contient " + rs.getRow() + "	enregistrements");
			}
			else
			{
				System.out.println("Votre instruction a modifi�	des enregistrements dans la base");
				System.out.println("Nombre d�enregistrements modifi�s :"+ stm.getUpdateCount());
			}
		}
		catch (SQLException e)
		{
			System.out.println("Votre instruction n'a pas fonctionn� correctement");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	private static void TestExecuteBatch(){
		Statement stm;
		BufferedReader br;
		String requete="";
		int[] resultats;
		try
		{
			stm=connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);

			br=new BufferedReader(new InputStreamReader(System.in));		
			System.out.println("Saisir vos instructions SQL puis run pour ex�cuter le lot :");

			System.out.print("===>");
			requete=br.readLine();
			while (!requete.equalsIgnoreCase("run"))
			{
				stm.addBatch(requete);
				System.out.print("===>");
				requete=br.readLine();
			}

			System.out.println("Ex�cution du lot d'instructions");

			resultats=stm.executeBatch();
			for (int i=0; i<resultats.length;i++)
			{
				switch (resultats[i])
				{
				case Statement.EXECUTE_FAILED:
					System.out.println("l'ex�cution de	l�instruction " + i + " a �chou�");
					break;
				case Statement.SUCCESS_NO_INFO:
					System.out.println("l'ex�cution de	l�instruction " + i + " a r�ussi");
					System.out.println("le nombred'enregistrements 	modifi�s est inconnu");
					break;
				default:
					System.out.println("l'ex�cution de	l�instruction " + i + " a r�ussi");
					System.out.println("elle a modifi� " +	resultats[i] + " enregistrements");
					break;
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void TestExecuteMultiple(){
		Statement stm;
		BufferedReader br;
		String requete;
		ResultSet rs;
		boolean resultat;
		try
		{
			stm=connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			br=new BufferedReader(new InputStreamReader(System.in));

			System.out.println("saisir vos instruction SQL s�par�es	par ; :");
			requete=br.readLine();
			resultat=stm.execute(requete);
			int i=1;
			// traitement du r�sultat g�n�r� par la premi�re instruction
			if (resultat)
			{
				System.out.println("Votre instruction N� " + i + " a g�n�r� un jeu d'enregistrements");
				rs=stm.getResultSet();
				rs.last();
				System.out.println("il contient " + rs.getRow() + "	enregistrements");
			}
			else
			{
				System.out.println("votre instruction N� " + i + " a modifi� des enregistrements dans la base");
				System.out.println("nombre d�enregistrements modifi�s :" + stm.getUpdateCount());
			}
			i++;

			// d�placement du pointeur sur un �ventuel r�sultat		suivant
			resultat=stm.getMoreResults();

			// boucle tant qu�il y a encore un r�sultat de type jeu	d�enregistrement -> resultat==true
			// ou qu�il y a encore un resultat de type nombre d�enregistrements modifi�s -> getUpdateCount != -1
			while (resultat || stm.getUpdateCount()!=-1)
			{
				if (resultat)
				{
					System.out.println("votre instruction N� " + i + " a g�n�r� un jeu d'enregistrements");
					rs=stm.getResultSet();
					rs.last();
					System.out.println("Il contient " + rs.getRow()	+ " enregistrements");
				}
				else
				{
					System.out.println("Votre instruction N� " + i + " a modifi� des enregistrements dans la base");
					System.out.println("Nombre d�enregistrements modifi�s :" + stm.getUpdateCount());
				}
				i++;

				// d�placement du pointeur sur un �ventuel r�sultat	suivant
				resultat=stm.getMoreResults();
			}
		}
		catch (SQLException e)
		{
			System.out.println("votre instruction n'a pas fonctionn� correctement");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void TestPreparedStatement(){
		PreparedStatement stm;
		BufferedReader br;
		String code;
		ResultSet rs;
		try
		{

			stm=connection.prepareStatement("SELECT * FROM customers WHERE customerID LIKE ?",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);

			br=new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Saisir le code du client recherch� :");
			code=br.readLine();
			stm.setString(1,code);
			rs=stm.executeQuery();

			if(rs.last()){
				System.out.println(rs.getRow() + " enregistrements trouv�(s).");
				rs.beforeFirst();
			}else{
				System.out.println("Aucun enregistrement trouv�!");
			}

			while(rs.next())
			{
				for (int i = 1; i<=rs.getMetaData().getColumnCount(); i++)
				{
					System.out.print(rs.getString(i)+"\t");
				}
				System.out.println();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void TestCallableStatement(){
		/*
			create PROCEDURE commandesParClient @code nchar(5)
			AS
			SELECT OrderID,
			OrderDate,
			RequiredDate,
			ShippedDate
			FROM Orders
			WHERE CustomerID = @code
			ORDER BY OrderID

			CREATE procedure nbCommandes @code nchar(5) as
			declare @nb int
			select @nb=count(*) from orders where customerid=@code
			return @nb
		 */

		CallableStatement cstm1,cstm2;
		BufferedReader br;
		String code;
		ResultSet rs;
		int nbCommandes;
		try
		{
			br=new BufferedReader(new InputStreamReader(System.in));
			System.out.println("saisir le code du client recherch� :");
			code=br.readLine();

			cstm1=connection.prepareCall("{ ?=call nbCommandes ( ? )}");
			cstm1.setString(2,code);
			cstm1.registerOutParameter(1,java.sql.Types.INTEGER);
			cstm1.execute();
			nbCommandes=cstm1.getInt(1);

			System.out.println("Nombre de commandes pass�es par le	client " + code + " : " + nbCommandes );
			cstm2=connection.prepareCall("{ call commandesParClient ( ?	)}",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			cstm2.setString(1,code);
			rs=cstm2.executeQuery();

			System.out.println("d�tail des commandes");
			System.out.println("Num�ro \t Date Commande");
			while (rs.next())
			{
				//positionRs(rs);

				System.out.print(rs.getInt("OrderID") + "\t");
				System.out.println(new	SimpleDateFormat("dd/MM/yy").format(rs.getDate("OrderDate")));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}


	private static void TestInfosResulset(){
		ResultSet rs;

		Statement stm;
		try {
			stm = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			if(stm.execute("SELECT * FROM Products")){
				rs = stm.getResultSet();
				InfosResultset(rs);
			}
			stm.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}


	}
	private static void InfosResultset(ResultSet rs)
	{
		try {
			switch (rs.getType())
			{
			case ResultSet.TYPE_FORWARD_ONLY:
				System.out.println("Le Resultset est � d�filement en avant seulement");
				break;
			case ResultSet.TYPE_SCROLL_INSENSITIVE:
				System.out.println("Le Resultset peut �tre parcouru dans les deux sens");
				System.out.println("Il n'est pas sensible aux modifications faites par d'autres utilisateurs");
				break;
			case ResultSet.TYPE_SCROLL_SENSITIVE:
				System.out.println("Le Resultset peut �tre	parcouru dans les deux sens");
				System.out.println("Il est sensible aux modifications faites par d'autres utilisateurs");
				break;
			}

			switch (rs.getConcurrency())
			{
			case ResultSet.CONCUR_READ_ONLY:
				System.out.println("Les donn�es contenues dans le ResulSet sont en lecture seule");
				break;
			case ResultSet.CONCUR_UPDATABLE:
				System.out.println("Les donn�es contenues dans le ResulSet sont modifiables");
				break;
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private static void positionRs(ResultSet rs)
	{
		try {
			if (rs.isBeforeFirst())
			{
				System.out.println("le pointeur est avant le premier enregistrement");
			}
			if (rs.isAfterLast())
			{
				System.out.println("le pointeur est apr�s le dernier enregistrement");
			}
			if (rs.isFirst())
			{
				System.out.println("le pointeur est sur le premier enregistrement");
			}
			if (rs.isLast())
			{
				System.out.println("le pointeur est sur le dernier enregistrement");
			}
			int position;
			position=rs.getRow();
			if (position!=0)
			{
				System.out.println("c�est l'enregistrement num�ro " + position);
			}
		} catch (SQLException e) {
			// TODO Bloc catch auto-g�n�r�
			e.printStackTrace();
		}
	}

	private static void lectureRs()
	{
		Statement stm;
		String requete;
		ResultSet rs;
		try
		{
			stm=connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
			requete="select * from products " ;

			rs=stm.executeQuery(requete);

			System.out.println("Code\tD�signation\t\t\tPrix U.\tStock\tEpuis�");
			while(rs.next())
			{
				System.out.print(rs.getInt("ProductID")+"\t");
				System.out.print(rs.getString("ProductName")+"\t\t\t\t");

				System.out.print(rs.getDouble("UnitPrice")+"\t");
				rs.getShort("UnitsInStock");
				if (rs.wasNull())
				{
					System.out.print("inconnu\t");
				}
				else
				{
					System.out.print(rs.getShort("UnitsInStock")+"\t");
				}
				System.out.print(rs.getBoolean("Discontinued")+"\t");

				System.out.println();

			}
			rs.close();
			stm.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private static void modificationRs()
	{
		Statement stm;
		String requete;
		ResultSet rs;
		int num=0;
		BufferedReader br;
		String reponse;
		try
		{
			stm=connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			requete="select * from products " ;
			rs=stm.executeQuery(requete);

			System.out.println("num�ro de ligne\tcode produit\td�signation\tprix unitaire\tstock\t�puis�");
			while(rs.next())
			{
				num++;
				System.out.print(num + "\t");
				System.out.print(rs.getInt("ProductID")+"\t");
				System.out.print(rs.getString("ProductName")+"\t");
				System.out.print(rs.getDouble("UnitPrice")+"\t");
				rs.getShort("UnitsInStock");
				if (rs.wasNull())
				{
					System.out.print("inconnu\t");
				}
				else
				{
					System.out.print(rs.getShort("UnitsInStock")+"\t");
				}
				System.out.print(rs.getBoolean("Discontinued")+"\t");
				System.out.println();

			}
			br=new BufferedReader(new InputStreamReader(System.in));
			System.out.println("quelle ligne voulez-vous modifier ? ");
			reponse=br.readLine();
			rs.absolute(Integer.parseInt(reponse));

			System.out.println("d�signation actuelle " + rs.getString("ProductName"));
			System.out.println("saisir la nouvelle valeur ou enter	pour conserver la valeur actuelle");
			reponse=br.readLine();
			if (!reponse.equals(""))
			{
				rs.updateString("ProductName",reponse);
			}
			System.out.println("prix unitaire actuel " + rs.getDouble("UnitPrice"));
			System.out.println("saisir la nouvelle valeur ou enter	pour conserver la valeur actuelle");
			reponse=br.readLine();
			if (!reponse.equals(""))
			{
				rs.updateDouble("UnitPrice",Double.parseDouble(reponse));
			}
			rs.getShort("UnitsInStock");
			if (rs.wasNull())
			{
				System.out.println ("quantit� en stock actuelle inconnue");
			}
			else
			{
				System.out.println("quantit� en stock actuelle " +
						rs.getShort("UnitsInStock"));
			}
			System.out.println("saisir la nouvelle valeur ou enter	pour conserver la valeur actuelle");
			reponse=br.readLine();
			if (!reponse.equals(""))
			{
				rs.updateShort("UnitsInStock",Short.parseShort(reponse));
			}
			System.out.println("voulez-vous valider ces modifications o/n");
			reponse=br.readLine();
			if (reponse.toLowerCase().equals("o"))
			{
				rs.updateRow();
			}
			else
			{

				rs.cancelRowUpdates();
			}
			System.out.println("les valeurs actuelles ");
			System.out.print(rs.getString("ProductName")+"\t");
			System.out.print(rs.getDouble("UnitPrice")+"\t");
			rs.getShort("UnitsInStock");
			if (rs.wasNull())
			{
				System.out.print("inconnu\t");
			}
			else
			{
				System.out.print(rs.getShort("UnitsInStock")+"\t");
			}
			rs.close();
			stm.close();

			System.out.println();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void suppressionRs(){
		Statement stm;
		String requete;
		ResultSet rs;
		int num=0;
		BufferedReader br;
		String reponse;
		try
		{
			stm=connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);

			requete="select * from products" ;
			rs=stm.executeQuery(requete);
			System.out.println("Num�ro\tCode produit\tD�signation\tPrix Unitaire\tStock\tEpuis�");
			while(rs.next())
			{
				num++;
				System.out.print(num + "\t");
				System.out.print(rs.getInt("ProductID")+"\t");
				System.out.print(rs.getString("ProductName")+"\t");
				System.out.print(rs.getDouble("UnitPrice")+"\t");
				rs.getShort("UnitsInStock");
				if (rs.wasNull())
				{
					System.out.print("inconnu\t");
				}
				else
				{
					System.out.print(rs.getShort("UnitsInStock")+"\t");
				}
				System.out.print(rs.getBoolean("Discontinued")+"\t");

				System.out.println();

			}
			br=new BufferedReader(new InputStreamReader(System.in));
			System.out.println("quelle ligne voulez-vous supprimer ?");
			reponse=br.readLine();

			rs.absolute(Integer.parseInt(reponse));		
			rs.deleteRow();

			System.out.println("le pointeur est maintenant sur la ligne " + rs.getRow());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void ajoutRs(){
		Statement stm;
		String requete;
		ResultSet rs;
		BufferedReader br;
		String reponse;
		try
		{
			stm=connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			requete="select * from products " ;
			rs=stm.executeQuery(requete);

			br=new BufferedReader(new InputStreamReader(System.in));
			System.out.println("saisir les valeurs de la nouvelle ligne");

			rs.moveToInsertRow();

			/* identity column
			System.out.print("code produit : ");
			reponse=br.readLine();
			rs.updateInt ("ProductID",Integer.parseInt(reponse));
			 */

			System.out.print("D�signation : ");
			reponse=br.readLine();
			rs.updateString ("ProductName",reponse);
			System.out.print("Prix unitaire : ");
			reponse=br.readLine();
			rs.updateDouble("UnitPrice",Double.parseDouble(reponse));
			System.out.print("Quantit� en stock : ");
			reponse=br.readLine();
			rs.updateDouble("UnitsInStock",Short.parseShort(reponse));

			rs.insertRow();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static void TestTransactions(){
		/*
		 * CREATE TABLE [dbo].[Comptes](
				[Num] [int] IDENTITY(1,1) NOT NULL,
				[Numero] [nvarchar](50) NOT NULL,
				[Solde] [numeric](18, 2) NOT NULL,
			 CONSTRAINT [PK_Comptes] PRIMARY KEY CLUSTERED(	[Num] ASC)
			 WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
			) ON [PRIMARY]
		 * 
		 */
		String compteDebit="c1", compteCredit="c2";
		double	somme=1500;
		
		try{	
			connection.setAutoCommit(false);


			PreparedStatement stm;
			stm=connection.prepareStatement("UPDATE Comptes set Solde=Solde + ? WHERE Numero=?");

			stm.setDouble(1,somme * -1);
			stm.setString(2,compteDebit);
			stm.executeUpdate();
						
			stm.setDouble(1,somme);
			stm.setString(2,compteCredit);
			stm.executeUpdate();
			
						
			connection.commit();
			System.out.println("Transaction Valid�e!");
		}
		catch (Exception e)
		{
			try {
				connection.rollback();
				System.out.print("Transaction Annul�e! : ");
			}
			catch (SQLException e1)
			{
				e1.printStackTrace();
			}
			System.out.println(e.getMessage());
			//e.printStackTrace();
		}
	}
}
