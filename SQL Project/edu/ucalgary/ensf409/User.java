package edu.ucalgary.ensf409;

import java.util.*;
import java.io.*;
import java.sql.*;

/**
 *User.java
 *Purpose: Intakes the user data need to make a connection to the data base.
 *This includes the DBURL info, and their username and password.
 *It then intakes order information from the username.
 *This includes their desired furniture, type of furniture, and amount wanted.
 *
 *@version 1.1 04/05/2021
 *@author Sanchi Kumar, Miki Rowbottom, Alliana Dela Pena, Julien Campbell
 */
public class User{
//Fields
  //variables type, furniture and amount store the corresponding info gained from the user
  private String type; 
  private String furniture;
  private int amount;
  //variables DBURL, USERNAME, and PASSWORD store the user input data needed to create connection to database
  private final String DBURL;
  private final String USERNAME;
  private final String PASSWORD;
  //myConnect is used for the connection
  private Connection myConnect;
  //cheapest combo contains an ArrayList of strings containing the IDs for the cheapest combination of furniture to fulfill the order
  private ArrayList<String> cheapestCombo = new ArrayList<String>();
  //price is used to store the total price of order
  private int price;
  
  private Chair newChair = null;
  private Lamp newLamp = null;
  private Desk newDesk = null;
  private Filing newFiling = null;
  
  private OrderForm orderChair = null;
  private OrderForm orderDesk = null;
  private OrderForm orderFiling = null;
  private OrderForm orderLamp = null;
  //A singular object for each of the different manufactureres for each of the furnitures
  private String[] chairManu = {"Office Furnishings","Furniture Goods","Fine Office Supplies","Chairs R Us"};
  private String[] deskManu = {"Office Furnishings","Furniture Goods","Fine Office Supplies","Academic Desks"};
  private String[] filingManu = {"Office Furnishings","Furniture Goods","Fine Office Supplies"};
  private String[] lampManu = {"Office Furnishings","Furniture Goods","Fine Office Supplies"};


//Constructors
/**
 *In order to construct class User, this constructor obtains all the information needed from the user.
 *Also because User is reponsible for continuing the program, it calls further methods such as initializeConnection and constructFurniture.
 */
  public User(){ 
    //Create a Scanner
    Scanner input = new Scanner(System.in);

    //Enter a DBURL for SQL Connection
    System.out.println("Enter a DBURL:");
    this.DBURL = input.nextLine();

    //Enter a Username for SQL Connection
    System.out.println("Enter a Username:");
    this.USERNAME = input.nextLine();

    //Enter a Password for SQL Connection
    System.out.println("Enter a Password:");
    this.PASSWORD = input.nextLine();

    //Establish a SQL Connection
    initializeConnection();

    //Enter Furniture
    System.out.println("Enter desired Furniture from the list (Ex. chair, desk, etc): ");
    this.furniture = input.nextLine().toLowerCase();

    //Enter A Furniture type
    System.out.println("Enter a type Furniture (Ex. mesh, task, etc):");
	  String ex = input.nextLine();
	  if(ex.toLowerCase().equals("swing arm")){
		  this.type = ex.substring(0,1).toUpperCase() + ex.substring(1,6).toLowerCase() + ex.substring(6,7).toUpperCase() + ex.substring(7,ex.length()).toLowerCase();
		  System.out.println(this.type);
	  }
	  else{
		this.type = ex.substring(0,1).toUpperCase() + ex.substring(1).toLowerCase();
	  }

    //Enter the amount of Furniture needed
    System.out.println("Enter the amount:");

    //if amount is a letter or < 1 output invalid
    String strAmount = input.nextLine();

    //Try to close the Scanner
    if(input != null){
		try{
			input.close();
		}
		catch(Exception e){
			System.out.println("Error closing Scanner");
			e.printStackTrace();
		}
	}

  //Calls constructFurniture to create order
  constructFurniture(strAmount);  

  //Try to close the connection
	if(this.myConnect != null){
		try{
			this.myConnect.close();
		}
		catch(SQLException e){
			System.out.println("Error closing Database Connection");
			e.printStackTrace();
		}
	}

    
  }

  //This constructor is used for unit testing
  public User(String dburl, String username, String pass, String furniture, String type, String strAmount){
    this.furniture = furniture;
    this.type = type;
    this.DBURL = dburl;
    this.USERNAME = username;
    this.PASSWORD = pass;

    //Establish a SQL Connection
    initializeConnection();
    
    //Calls constructFurniture to create order
    constructFurniture(strAmount);
  }

   
  //Methods
  //Getters
  public String getType(){
    return this.type;
  }
  
  public String getFurniture(){
    return this.furniture;
  }

  public int getAmount(){
    return this.amount;
  }

  public ArrayList<String> getID(){
    return this.cheapestCombo;
  }
  
  public int getPrice(){
	return this.price;  
  }

  //Setters
  public void setType(String type){
    this.type = type;
  }
  
  public void setFurniture(String furniture){
    this.furniture = furniture;
  }

  public void setAmount(int amount){
    this.amount = amount;
  }

/**
 *Establishes a SQL connection utilizing users input of DBURL, USERNAME and PASSWORD
 *The users input was obtained via the constructor of class User
 *
 *@param void
 */
  public void initializeConnection(){
    try{
      this.myConnect = DriverManager.getConnection(this.DBURL, this.USERNAME, this.PASSWORD);
    }

    catch(SQLException e){
      System.out.println("Error connection to database.");
      System.exit(1);
    }
  }//end of initializeConnection method


 /**
  * Constructs an instance of either class Chair, Desk, Filing, or Lamp based on string in furniture (obtained from user).
  * Checks if Order requested by user is possible and valid.
  * If order is not possible, calls method to find manufacturer names that could fulfill their order via class Manufacturer.
  * @param strAmount amount of furniture ordered
  */
  public void constructFurniture(String strAmount){
    //checks if strAmount is an integer if not system exits
    try{
      this.amount = Integer.parseInt(strAmount);
    }
    catch (Exception e){
      System.out.println("Invalid amount.");
      System.exit(1);
    }

    //checks if amount is not zero or a negative number
    if(this.amount < 1){
      System.out.println("Invalid amount.");
      System.exit(1);
    }
	
    // checks if the furniture is chair
    if(this.furniture.equals("chair")){
      //If it is a chair, then check to see if its type matches one of those in the database
      if(!this.type.equals("Task") && !this.type.equals("Kneeling") && !this.type.equals("Mesh") && !this.type.equals("Ergonomic") && !this.type.equals("Executive")){
        System.out.println("Furniture type input is not valid for specified furniture.");
        System.exit(1);
      }

      //Finally, create an instance of a chair
      this.newChair = new Chair(this.myConnect, this.type);
      //Use the cheapChair method of class Chair to get back an ArrayList of IDs
      this.cheapestCombo = this.newChair.cheapChair(this.amount);
     
      //if chair order is fulfilled get price and chair combination IDs
      if(!this.cheapestCombo.isEmpty()){
        this.price = this.newChair.getPrice();
        //Since the order is fulfilled instantiate the OrderForm variable so that it outputs the Order
        this.orderChair = new OrderForm(this.amount, this.type, this.furniture, this.price, this.cheapestCombo);
      }
      //else if chair order is unfulfilled get the manufacturer Names 
	    else{
        //Instantiate the OrderForm variable so it now outputs the manufacturer names instead of the order
        this.orderChair = new OrderForm(this.amount, this.type, this.furniture, this.chairManu);
      }

    }	
	
  //Check if the furniture is a desk
	else if(this.furniture.equals("desk")){
    // checks if type is valid for desk
    if(!this.type.equals("Standing") && !this.type.equals("Traditional") && !this.type.equals("Adjustable")){
      System.out.println("Furniture type input is not valid for specified furniture.");
      System.exit(1);
    }
      //Finally, create an instance of a desk
      this.newDesk = new Desk(this.myConnect, this.type);
      //Use the method cheapDesk of class Desk to get back an ArrayList of IDs
      this.cheapestCombo = this.newDesk.cheapDesk(this.amount);
      
      //if desk order is fulfilled get price and desk combination IDs
      if(!this.cheapestCombo.isEmpty()){
	      this.price = this.newDesk.getPrice();
        //Since the order is fulfilled, instantiate an OrderForm variable so that it outputs an order
        this.orderDesk = new OrderForm(this.amount, this.type, this.furniture, this.price, this.cheapestCombo);
      }
      //else if desk order is unfulfilled get the manufacturer Names 
	    else{
        //Instantiate an OrderForm variable so that it outptus the manufacturer names
        this.orderDesk = new OrderForm(this.amount, this.type, this.furniture, this.deskManu);
      }
      
    }
	
  //Check if the furniture is a filing cabinet
	else if(this.furniture.equals("filing")){
    // checks if type is valid for filing
    if(!this.type.equals("Small") && !this.type.equals("Medium") && !this.type.equals("Large")){
      System.out.println("Furniture type input is not valid for specified furniture.");
      System.exit(1);
    }
    
    //Finally, create a Filing variable
    this.newFiling = new Filing(this.myConnect, this.type);
    //Use the method cheapFiling of class Filing to get back an ArrayList of IDs
    this.cheapestCombo = this.newFiling.cheapFiling(this.amount);
	
      //if filing order is fulfilled get price and filing combination IDs
      if(!this.cheapestCombo.isEmpty()){
	      this.price = this.newFiling.getPrice();
        //Instantiate an OrderForm variable so that it outputs the order
        this.orderFiling = new OrderForm(this.amount, this.type, this.furniture, this.price, this.cheapestCombo);
      }
      //else if filing order is unfulfilled get the manufacturer Names 
	    else{
        //Instantiate an OrderForm variable so that it outputs the manufacturer names
        this.orderFiling = new OrderForm(this.amount, this.type, this.furniture, this.filingManu);
      }  
    }
	
  //Check if the furniture is a lamp
	else if(this.furniture.equals("lamp")){
    // checks if type is valid for lamp
    if(!this.type.equals("Desk") && !this.type.equals("Study") && !this.type.equals("Swing Arm")){
      System.out.println("Furniture type input is not valid for specified furniture.");
      System.exit(1);
    }
    //Finally, create a Lamp variable
    this.newLamp = new Lamp(this.myConnect, this.type);
    //Use the method cheapLamp of class Lamp to get back an ArrayList of IDs
    this.cheapestCombo = this.newLamp.cheapLamp(this.amount);
	
      //if lamp order is fulfilled get price and lamp combination IDs
      if(!this.cheapestCombo.isEmpty()){
	      this.price = this.newLamp.getPrice();
        //Instantiate an OrderForm variable so that it outputs the order
        this.orderLamp = new OrderForm(this.amount, this.type, this.furniture, this.price, this.cheapestCombo);
      }
      //else if lamp order is unfulfilled get the manufacturer Names 
	    else{
        //Instantiate an OrderForm variable so that it outputs the manufacturer names
        this.orderLamp = new OrderForm(this.amount, this.type, this.furniture, this.lampManu);
      }
    }

    //If the furniture entered by the use does not match any furniture in the database
    else{
	  	System.out.println("User Input not applicable to the current inventory.");
	  	System.exit(1);
	  }

    //Print out the cheapest combo and the price of the combo
	if(!this.cheapestCombo.isEmpty()){
		System.out.println("\nID:");
		for(String line: this.cheapestCombo){
			System.out.println(line);
		}
		System.out.println("\nPrice: $" + this.price);
	}
	
	//Order could not be fulfilled since ID list is empty
	else{
		System.out.println("Order could not be fulfilled.");
	}
  }//end of constructFurniture method
}//end of class User

