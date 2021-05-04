package edu.ucalgary.ensf409;

import java.util.*;
import java.io.*;
import java.sql.*;

/**
 *Lamp.java
 *Purpose: Utilizing the info obatined from user, Lamp class contains methods and fields
 * to find the cheapest combinations of lamps in the database. It then is able to return the IDs and price. 
 *If no combination is possible, then it returns an empty ArrayList.
 *
 *@version 1.1 04/05/2021
 *@author Sanchit Kumar, Miki Rowbottom, Alliana Dela Pena, Julien Campbell
 */
public class Lamp {
//Fields
  private String type;
  private int price;
  private Connection myConnect;
  private ArrayList<String> finalCombo;



//Constructors
/**
 *Public constructor used to set up the connection and type of lamp desired
 *
 *@param arg Used to set the connection to the database
 *@param type Indicates what type of desk is requested
 */
public Lamp(Connection arg, String type){
  //super(arg, type, amount);
  this.myConnect = arg;
  this.type = type;
}

//getters
public int getPrice(){
  return this.price;
}

/**
 *This method is used to get the IDs of the lamps corresponding to the cheapest combination
 *
 *@return Returns an ArrayList of Strings containing the IDs of the lamps that correspond to the cheapest combination
 */
public ArrayList<String> getID(){
  return this.finalCombo;
}

/**
 *Method to create an ArrayList of String arrays. Each string array represents a lamp in the database.
 *Only lamps of the requested type will be included.
 *
 *@return Returns an ArrayList of String arrays. This ArrayList only contains String[] corresponding to a lamp of the reqested type. 
 */
public ArrayList<String[]> makeArray(String part){
  ArrayList<String[]> arrayPart = new ArrayList<String[]>();
  try {                    
    Statement myStmt = this.myConnect.createStatement();
    ResultSet results;
    results = myStmt.executeQuery("SELECT * FROM lamp");
    while (results.next()){
      //finds chairs of same type(ex. mesh) and has usable legs (i.e. Y for Legs)
      if(results.getString("Type").equals(this.type) && results.getString(part).equals("Y")){

        //adds ID of chair and price to the arraylist
        String[] chairContestant = {results.getString("ID"), Integer.toString(results.getInt("Price"))};
        arrayPart.add(chairContestant);
      }
    }
    myStmt.close();
  } catch (SQLException ex) {
    ex.printStackTrace();
  }   
  return arrayPart;
} //end of makeArray

/**
 *This method is used to find all combinatinos or lamps in the database that can create the requested lamp
 *
 *@return Returns an ArrayList of ArrayList<String[]>. 
 *Each entry of ArrayList<String[]> is a combination of lamps that can make the requested lamp. Note each String[] represents a single lamp
 */
public ArrayList<ArrayList<String[]>> findCombination(){
  ArrayList<ArrayList<String[]>> validCombos = new ArrayList<ArrayList<String[]>>();
  
  ArrayList<String[]> arrayBase = makeArray("Base");
  ArrayList<String[]> arrayBulb = makeArray("Bulb");

  //if arrayLegs or arms or seat or cushion = null; ----> print no combination
  //call method that suggests manufacturers

  for(int i = 0; i < arrayBase.size(); i++){
    for(int j = 0; j < arrayBulb.size(); j++){
        //creating one combination
        ArrayList<String[]> tmp = new ArrayList<>();
        tmp.add(arrayBase.get(i));
		tmp.add(arrayBulb.get(j));
        validCombos.add(tmp); 
      
    }
  }
  return validCombos;
} //end of findCombination method

/**
 *This method is used to find the cheapest valid combination of lamps. It then returns the IDs of those lamps in an ArrayList.
 *
 *@return Returns an ArrayList of Strings that are the IDs for the lamps that make up the cheapest combination.
 */
public ArrayList<String> cheapLamp(int amount){
 //Create an ArrayList that contains every single possible combination that can create the requested chair by calling the findCombination method.
	ArrayList<ArrayList<String[]>> validCombos = findCombination();
  //This arraylist will be returned. It contains the IDs of the solution.
	ArrayList<String> solutionIdNumbers = new ArrayList<String>();
  //This array is used to hold the prices of each chair.
	int[] price = new int[200];
  //This arraylist contains the cheapest combination found
	ArrayList<String[]> cheapestCombination = new ArrayList<String[]>();
	int count = validCombos.size();
	int cheapestPrice = 0; 
	int cheapestIndex = 0; 
  
  //For loop to get the correct amount of furniture
	for(int i = 0; i < amount; i++){
    //Reset the prices in the price array so it doesn't interfere with the for loop
		Arrays.fill(price,0);

    //If the valid combinations is empty, then return an empty ArrayList
		if(validCombos.isEmpty()){
			this.finalCombo = null;
			solutionIdNumbers.clear();
			return solutionIdNumbers;
		}
  
  
  
    //Remove any duplicates IDs from each combination
		for(int x = 0; x < count; x++){	  
      //A singular combination
			ArrayList<String[]> temp = validCombos.get(x);
      //If the combination contains any duplicates, then get rid of the duplicates
			for(int k = 0; k < temp.size(); k++){
				for(int j = k + 1; j < temp.size(); j++){
					if(temp.get(k)[0].equals(temp.get(j)[0])){
						temp.remove(j);
						j--;
					}
				}
			}
      //Add the combinations without any duplicates back into the valid combinations
			validCombos.add(temp);
		}
  
  
    //Get the price of each combination and enter it into the prices array
		for(int y = 0; y < validCombos.size(); y++){
			ArrayList<String[]> temp = validCombos.get(y);
			for(int j = 0; j < temp.size(); j++){
        //Add together the prices of the IDs in the combination
				price[y] += Integer.parseInt(temp.get(j)[1]);
			}
		}
    
    //Set the cheapest price to be the 0th element in the array
		cheapestPrice = price[0];
		for(int k = 0; k < validCombos.size(); k++){
      //tests if there is a lower price than currently recorded
			if(price[k] < cheapestPrice) { 
        //cheaper price found and set
				cheapestPrice = price[k];
        //record the index of the cheapest Combination
				cheapestIndex = k; 
			} 
		}

    //Get the combination that contains the lowest price
		cheapestCombination = validCombos.get(cheapestIndex); 

    //Add to the solution array the IDs of the solution combination
		for(String[] line: cheapestCombination){
			solutionIdNumbers.add(line[0]);
		}
    //Add the price of the combination to the total price
		this.price += price[cheapestIndex];

		//Remove all combinations from validCombos that already contains an ID from the solution
		for(int z = 0; z < validCombos.size(); z++){
			for(int a = 0; a < validCombos.get(z).size(); a++){
				if(solutionIdNumbers.contains(validCombos.get(z).get(a)[0])){
					validCombos.remove(z);
					if( z > 0){
						z--;
					}
					break;
				}
			}
		}

		//Special check for validCombos.get(0)
		for(int b = 0; b < validCombos.get(0).size(); b++){
			if(solutionIdNumbers.contains(validCombos.get(0).get(b)[0])){
				validCombos.remove(0);
				break;
			}
		}
	}
  
	this.finalCombo = solutionIdNumbers;
  //Once we have the solution IDs, then delete the IDs from the inventory
	deleteLamp();
	return solutionIdNumbers;
} 

/**
 *This method is used to delete a lamp entry from the table in the database.
 */
public void deleteLamp(){
  //This contains the solution IDs
  ArrayList<String> cheapestLamp = this.finalCombo;

  for(int i = 0; i < cheapestLamp.size(); i++){
    try {
    //Delete all solution IDs from inventory
    String query = "DELETE FROM lamp WHERE ID = ?";
      PreparedStatement myStmt = this.myConnect.prepareStatement(query);
      myStmt.setString(1, cheapestLamp.get(i));             
      myStmt.executeUpdate();
      myStmt.close();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }//end of for loop

}//end of deleteLamp method



}//end of class Lamp