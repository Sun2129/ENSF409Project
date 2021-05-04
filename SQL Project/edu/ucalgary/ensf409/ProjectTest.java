package edu.ucalgary.ensf409;

import static org.junit.Assert.*;
import org.junit.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

/** 
 *
 *ProjectTest.java
 *Purpose: Contains unit tests to test the functionality of 
 *the code. Tests invalid user input, correct cheapest combination, price
 *and correct txt file output.
 *@version 1.0 04/05/2021
 *@author Sanchit Kumar, Miki Rowbottom, Alliana Dela Pena, Julien Campbell
 */

public class ProjectTest{
  //FIELDS
  private static Connection myConnect;
  private static final String DBURL = "jdbc:mysql://localhost/inventory";
  private static final String USERNAME = "scm";
  private static final String PASSWORD = "ensf409";
  private static BufferedWriter buffWriter = null;
  private static BufferedWriter buffWriter2 = null;

 
  @BeforeClass
  /**
   * Makes a connection to the database.
   */
  public static void start() {
     //Run this once before all of the tests are run
    try{
      myConnect = DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
    }
    catch(SQLException e){
      System.out.println("Error connection to database.");
      System.exit(1);
    }
  }
  
  
  @AfterClass
  /**
   * Deletes files created during the tests.
   */
  public static void end(){
    //Run this once after all of the tests are run
	  //Close the first BufferedWriter
	  if(buffWriter != null){
		  try{
			  buffWriter.close();
		  }
		  catch(Exception e){
			  e.printStackTrace();
		  }
	  }
	  
	  //Close the second Buffered Writer
	  if(buffWriter2 != null){
		  try{
			  buffWriter2.close();
		  }
		  catch(Exception e){
			  e.printStackTrace();
		  }
	  }
	  
	  //Delete the two Expected txt files
	  File myObj = new File("expected.txt");
	  File myObj2 = new File("expected2.txt");
	  try{
		  myObj.delete();
	  }
	  catch(Exception e){
		 e.printStackTrace();
	  }
	  try{
		   myObj2.delete();
	  }
	   catch(Exception e){
		 e.printStackTrace();
	  }
  }
  
	@Test
  /**
   * Tests if the correct combination of 1 mesh chair will be given.
   */
	public void testChairAmount1Combo(){
    //create Chair object 
		Chair testObj = new Chair(myConnect, "Mesh");

    //call on cheapChair should return correct chair combinations to make 
    //1 chair of type Mesh
    ArrayList<String> chairCombo = testObj.cheapChair(1);

    //create expected combination
	  ArrayList<String> tmp = new ArrayList<String>();
	  tmp.add("C6748");
    tmp.add("C9890");
    tmp.add("C8138");
    
    //sorts the Arraylists to check if it has the same contents
    Collections.sort(chairCombo);
    Collections.sort(tmp);
		
		assertTrue("cheapCombo function failed to get correct combo.", tmp.equals(chairCombo));
	}	
	
	@Test
  /**
   * Tests if the correct combination of 2 adjustable desks will be given.
   */
	public void testDeskAmount2Combo(){
    //create Desk object 
		Desk testDesk = new Desk(myConnect, "Adjustable");

    //call on cheapDesk should return correct desk combinations to make
    //2 desks of type Adjustable
    ArrayList<String> deskCombo = testDesk.cheapDesk(2);
    
    //create expected combination
    ArrayList<String> expectedCombo = new ArrayList<>();
    expectedCombo.add("D2746");
    expectedCombo.add("D4475");
	  expectedCombo.add("D7373");

    //sorts the Arraylists to check if it has the same contents
    Collections.sort(deskCombo);
    Collections.sort(expectedCombo);

		assertTrue("cheapDesk function failed to get correct combo.", expectedCombo.equals(deskCombo));
	}
	
	@Test
  /**
   * Tests if the correct combination of 1 small filing will be given.
   */
	public void testFilingAmount1Combo(){
    //create Filing object 
		Filing testFiling = new Filing(myConnect, "Small");

    //call on cheapFiling should return correct filing combinations to make
    //1 filing of type Small
    ArrayList<String> filingCombo = testFiling.cheapFiling(1);
    
    //create expected combination
    ArrayList<String> expectedCombo = new ArrayList<>();
    expectedCombo.add("F001");
    expectedCombo.add("F013");

    //sorts the Arraylists to check if it has the same contents
    Collections.sort(filingCombo);
    Collections.sort(expectedCombo);
		
		assertTrue("cheapLamp function failed to get correct combo.", expectedCombo.equals(filingCombo));
	}
	
	@Test
  /**
   * Tests if the correct combination of 3 desk lamps will be given.
   */
	public void testLampAmount3Combo(){
    //create Lamp object
		Lamp testLamp = new Lamp(myConnect, "Desk");

    //call on cheapLamp should return correct lamp combinations to make
    //3 lamps of type Desk
    ArrayList<String> lampCombo = testLamp.cheapLamp(3);
    
    //create expected combination
    ArrayList<String> expectedCombo = new ArrayList<>();
    expectedCombo.add("L013");
    expectedCombo.add("L112");
    expectedCombo.add("L564");
    expectedCombo.add("L342");
    expectedCombo.add("L208");

    //sorts the Arraylists to check if it has the same contents
    Collections.sort(lampCombo);
    Collections.sort(expectedCombo);
		
		assertTrue("cheapLamp function failed to get correct combo.", expectedCombo.equals(lampCombo));
	}

 
  @Test
  /** 
  * Tests if an order 2 filings will return the correct price
  */
  public void testPricefor2Filings(){
    //Filing object is created
    Filing newFiling = new Filing(myConnect, "Large");

    //call on cheapFiling to get the cheapest filing combination
    ArrayList<String> combo = newFiling.cheapFiling(2);

    //call on getPrice to get cheapest price
    int price = newFiling.getPrice();

    boolean same = false;

    //checks if price is 600
    if(price == 600){
      same = true;
    }

    assertTrue("getPrice function did not return correct price", same);
  }
  
  
  @Test
  /**
   * Tests if an order of 1 task chair will produce correct orderform.txt file.
   */
  public void testFulfilledOrder(){
	  boolean equal = true;
	  BufferedReader reader1 = null;
	  BufferedReader reader2 = null;

    //create User object and ordering 1 task chair
	  User use = new User(DBURL, USERNAME, PASSWORD, "chair", "Task", "1");
	  createFulfilledOrder();
	  
	  try{
      //Read from the file that is output after finding the cheapest combo
		   reader1 = new BufferedReader(new FileReader("orderform.txt"));
       //Read from an expected file
		   reader2 = new BufferedReader(new FileReader("expected.txt"));
		  String line1;
		  String line2;

      //Read through both files
		  while((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null){
        //If any of the lines are not equal, then the orderform.txt file is not as expected
			  if(!line1.equals(line2)){
				  equal = false;
				  break;
			  }
		  }
	  }
	  
	  catch(Exception e){
		  e.printStackTrace();
	  }
	  
	  finally{
		  if(reader1 != null){
			  try{
				  reader1.close();
			  }
			  catch(Exception e){
				  e.printStackTrace();
			  }
		  }
		  
		  if(reader2 != null){
			  try{
				  reader2.close();
			  }
			  catch(Exception e){
				  e.printStackTrace();
			  }
		  }
		  
	  }
	  
    //Make sure that the files are the same
	  assertTrue("OrderForm does not have the correct output", equal);
  }

  
  @Test
  /**
   * Tests if an unable to fill order of 4 study lamps will produce 
   * the correct orderform.txt file.
   */
  public void testUnfulfilledOrder(){
	  boolean equal = true;
    BufferedReader reader1 = null;
    BufferedReader reader2 = null;
	  User use = new User(DBURL, USERNAME, PASSWORD, "lamp", "Study", "4");
	  createUnfulfilledOrder();
	  
	  try{
      //Read from the orderform.txt file produced when trying to find the cheapest combo
		  reader1 = new BufferedReader(new FileReader("orderform.txt"));
      //Read from an expected file
		  reader2 = new BufferedReader(new FileReader("expected2.txt"));
		  String line1;
		  String line2;
		  
		  //Read through both files 
		  while((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null){
        //If the lines in the files are not equal
			  if(!line1.equals(line2)){
				  equal = false;
				  break;
			  }
		  }
	  }
	  catch(Exception e){
		  e.printStackTrace();
	  }

    finally{
		  if(reader1 != null){
			  try{
				  reader1.close();
			  }
			  catch(Exception e){
				  e.printStackTrace();
			  }
		  }
		  
		  if(reader2 != null){
			  try{
				  reader2.close();
			  }
			  catch(Exception e){
				  e.printStackTrace();
			  }
		  }
		  
	  }
	  
    //Make sure that the files are equal
	  assertTrue("OrderForm does not have the correct output", equal);
  }
  
  
  @Rule
  // Handle System.exit() status
  public final ExpectedSystemExit exit = ExpectedSystemExit.none();

  @Test
  //connection created with invalid dburl expect system exit
  public void testInvalidDburlForConnection(){
	  
    exit.expectSystemExitWithStatus(1); 
    User use = new User( "brownies", USERNAME, PASSWORD, "chair", "Mesh", "1");

  }
	
  @Test
  /**
   * Test a connection created with invalid user name, expect system exit
   */
  public void testInvalidUsernameForConnection(){
   

    exit.expectSystemExitWithStatus(1); 
    User use = new User( DBURL, "cake", PASSWORD, "chair", "Mesh", "1");

  }

  @Test
  /**
   * Test a connection created with invalid password, expect system exit
   */
  public void testInvalidPaswordForConnection(){
   

    exit.expectSystemExitWithStatus(1); 
    User use = new User( DBURL, USERNAME, "ice cream", "chair", "Mesh", "1");

  }

  @Test
  /** 
  * Test invalid user furniture input, expect system exit
  */
  public void testInvalidFurniture(){
   
    exit.expectSystemExitWithStatus(1); 
    User use = new User( DBURL, USERNAME, PASSWORD, "couch", "Mesh", "1");

  }

  @Test
  /** 
  * Test invalid user furniture type, expect system exit
  */
  public void testInvalidFurnitureType(){
   
    exit.expectSystemExitWithStatus(1); 
    User use = new User( DBURL, USERNAME, PASSWORD, "chair", "marble", "1");

  }

  @Test
  /** 
  * Test user amount input is negative, expect system exit
  */
  public void testNegativeAmount(){
   
    exit.expectSystemExitWithStatus(1); 
    User use = new User( DBURL, USERNAME, PASSWORD, "chair", "marble", "-1");

  }

  @Test
  /** 
  * Test user amount input is zero, expect system exit
  */
  public void testZeroAmount(){
   
    exit.expectSystemExitWithStatus(1); 
    User use = new User( DBURL, USERNAME, PASSWORD, "chair", "marble", "0");

  }

  @Test
  /** 
  * Test user amount input is a letter, expect system exit
  */
  public void testNotIntegerAmount(){
   
    exit.expectSystemExitWithStatus(1); 
    User use = new User( DBURL, USERNAME, PASSWORD, "chair", "marble", "z");

  }

  @Test
  /** 
  * Test user amount input is a double, expect system exit
  */
  public void testDoubleAmount(){
   
    exit.expectSystemExitWithStatus(1); 
    User use = new User( DBURL, USERNAME, PASSWORD, "chair", "marble", "5.2");

  }
  
  

  /**
   * Creates hard-coded fulfilled order
   */
  public void createFulfilledOrder(){
	  try{
		  //We then try to write to the file
		  buffWriter = new BufferedWriter(new FileWriter(new File("expected.txt")));
		  buffWriter.write("Furniture Order Form \n\n");
		  buffWriter.write("Faculty Name:\nContact:\nDate:\n\n");
		  buffWriter.write("Original Request: " + "Task" + " " + "chair" + ", " + "1" + "\n\n");
		  buffWriter.write("Items Ordered:\n");
		  buffWriter.write("C0914, C3405");
		  buffWriter.write("\n\nTotal Price: $150");
	  }catch(IOException e){
		e.printStackTrace();
	  }
  }
  


  /**
   * Creates hard-coded unfulfilled order.
   */
  public void createUnfulfilledOrder(){
	  try{
		  //We then try to write to the file
		  buffWriter2 = new BufferedWriter(new FileWriter(new File("expected2.txt")));
		  buffWriter2.write("Furniture Order Form \n\n");
		  buffWriter2.write("Faculty Name:\nContact:\nDate:\n\n");
		  buffWriter2.write("Original Request: " + "Study" + " " + "lamp" + ", " + "4" + "\n\n");
		  buffWriter2.write("Order cannot be fulfilled based on current inventory.\n\n");
		  buffWriter2.write("Here is a list of suggested manufacturers that supply the requested furniture.\n");
		  buffWriter2.write("Office Furnishings, Furniture Goods, Fine Office Supplies");
	  }catch(IOException e){
		e.printStackTrace();
	  }
  }

}