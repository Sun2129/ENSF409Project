package edu.ucalgary.ensf409;

import java.util.*;
import java.io.*;
import java.sql.*;

/**
 *Filing.java
 *Purpose: Utilizing the info obatined from user, Filing class contains methods and fields
 * to find the cheapest combinations of filers in the database. It then is able to return the IDs and price. 
 *If no combination is possible, then it returns an empty ArrayList.
 *
 *@version 1.1 04/05/2021
 *@author Sanchit Kumar, Miki Rowbottom, Alliana Dela Pena, Julien Campbell
 */
public class Filing {
//Fields
  private String type;
  private int price;
  private Connection myConnect;
  private ArrayList<String> finalCombo;



//Constructors
/**
 *Public constructor used to set up the connection and type of filer desired
 *
 *@param arg Used to set the connection to the database
 *@param type Indicates what type of desk is requested
 */
public Filing(Connection arg, String type){
  //super(arg, type, amount);
  this.myConnect = arg;
  this.type = type;
}

//getters
public int getPrice(){
  return this.price;
}

/**
 *This method is used to get the IDs of the filers corresponding to the cheapest combination
 *
 *@return Returns an ArrayList of Strings containing the IDs of the filers that correspond to the cheapest combination
 */
public ArrayList<String> getID(){
  return this.finalCombo;
}

/**
 *Method to create an ArrayList of String arrays. Each string array represents a filer in the database.
 *Only filers of the requested type will be included.
 *
 *@return Returns an ArrayList of String arrays. This ArrayList only contains String[] corresponding to a filer of the reqested type. 
 */
public ArrayList<String[]> makeArray(String part){
  ArrayList<String[]> arrayPart = new ArrayList<String[]>();
  try {                    
    Statement myStmt = this.myConnect.createStatement();
    ResultSet results;
    results = myStmt.executeQuery("SELECT * FROM filing");
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
 *This method is used to find all combinatinos or filers in the database that can create the requested filer
 *
 *@return Returns an ArrayList of ArrayList<String[]>. 
 *Each entry of ArrayList<String[]> is a combination of filers that can make the requested filer. Note each String[] represents a single filer
 */
public ArrayList<ArrayList<String[]>> findCombination(){
  ArrayList<ArrayList<String[]>> validCombos = new ArrayList<ArrayList<String[]>>();
  
  ArrayList<String[]> arrayRails = makeArray("Rails");
  ArrayList<String[]> arrayDrawer = makeArray("Drawers");
  ArrayList<String[]> arrayCabinet = makeArray("Cabinet");

  //if arrayLegs or arms or seat or cushion = null; ----> print no combination
  //call method that suggests manufacturers

  for(int i = 0; i < arrayRails.size(); i++){
    for(int j = 0; j < arrayDrawer.size(); j++){
      for(int k = 0; k < arrayCabinet.size(); k++){
        //creating one combination
        ArrayList<String[]> tmp = new ArrayList<>();
        tmp.add(arrayRails.get(i));
        tmp.add(arrayDrawer.get(j));
        tmp.add(arrayCabinet.get(k));
        validCombos.add(tmp); 
      }
    }
  }
  return validCombos;
} //end of findCombination method

/**
 *This method is used to find the cheapest valid combination of filers. It then returns the IDs of those filers in an ArrayList.
 *
 *@return Returns an ArrayList of Strings that are the IDs for the filers that make up the cheapest combination.
 */
public ArrayList<String> cheapFiling(int amount){ 
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
	
  //A while loop used to create multiple chairs if requested
	while(amount > 0){
		int amountReduced = 0;
    //Initially clear the price array so it does not interfere with the while loop
		Arrays.fill(price,0);
		
    //If there are no valid combinations, return an empty ArrayList
		if(validCombos.isEmpty()){
			this.finalCombo = null;
			solutionIdNumbers.clear();
			return solutionIdNumbers;
		}
		
		//These nested for loops are for the removal of Identical IDs in a valid combination
		for(int x = 0; x < count; x++){	  
      //A singular combination
			ArrayList<String[]> temp = validCombos.get(x);

			for(int k = 0; k < temp.size(); k++){
				for(int j = k + 1; j < temp.size(); j++){
          //If it contains duplicate IDs, then remove the duplicates
					if(temp.get(k)[0].equals(temp.get(j)[0])){
						temp.remove(j);
						j--;
					}
				}
			}

      //Add back the valid combination that doesn't contain duplicate IDs
			validCombos.add(temp);
		}
		
    //Create a temporary ArrayList of ArrayList<String[]> to contain certain combinations from valid combinations.
		ArrayList<ArrayList<String[]>> temporary = new ArrayList<ArrayList<String[]>>();
    //This is specifically used to determine combinations of furniture that can be made from extra leftover parts of a combination. Since making new furniture from leftover parts of a combination will always be cheaper than just buying more furniture, then we have to check if it is possible to create combinations of furniture from leftover parts of a combination. This only needs to be done if the amount requested is more than 1.
		if(amount > 1){
			for(int s = 0; s < validCombos.size(); s++){
        //Obtain a singular combination.
				ArrayList<String[]> temp = validCombos.get(s);
				int rails = 0;
        int drawers  = 0;
        int cabinet = 0;
				
        //Determine if it is possible to create more furniture from leftover parts of a furniture.
				for(String[] line: temp){
          //Try to create a statement.
					try {                    
						Statement myStmt = this.myConnect.createStatement();
						ResultSet results;
						results = myStmt.executeQuery("SELECT * FROM filing WHERE ID=" + "'" + line[0] + "'");
						while (results.next()){
							if(results.getString("Rails").equals("Y")){
								rails++;
							}
							if(results.getString("Drawers").equals("Y")){
								drawers++;
							}
							if(results.getString("Cabinet").equals("Y")){
								cabinet++;
							}
						}
            //Close the statement
						myStmt.close();
					} catch (SQLException ex) {
						ex.printStackTrace();
					}
				}
				
        //This is used to determine if it is possible to, for example, create 2 furnitures from 3 of the furnitures present in the inventory.
				for(int g = amount; g > 1; g--){
					if(rails == g && drawers == g && cabinet == g){
						temporary.add(temp);
						if(g > amountReduced){
              //Used to recored the highest amount of furniture able to be created from the least amount of furniture
							amountReduced = g;
						}
					}
				}
			}
			
		}
		//If it is possible to create more furniture from leftover parts of a combination, then use that combination.
		if(!temporary.isEmpty()){
			//Populate the price array with the prices of combinations in array
			for(int y = 0; y < temporary.size(); y++){
        //A singular combination
				ArrayList<String[]> temp = temporary.get(y);
				for(int j = 0; j < temp.size(); j++){
          //Add the prices in a singular combination together
					price[y] += Integer.parseInt(temp.get(j)[1]);
				}
			}
			
      //Set the cheapest price to the the price in the 0th position
			cheapestPrice = price[0];
			
      //Then scour through the array of prices and if there is a lower price, then set that as the cheapest price and obtain its index
			for(int k = 0; k < temporary.size(); k++){

				if(price[k] < cheapestPrice) { //tests if there is a lower price than currently recorded
					cheapestPrice = price[k]; //cheaper price found and set
					cheapestIndex = k; //record the index of the cheapest Combination
				} 
			}
			//Use the index that contains the cheapest price to get the combination of IDs from the ArrayList of ArrayList<String[]>
			cheapestCombination = temporary.get(cheapestIndex); 
		
			//Add the IDs into a solution arraylist
			for(String[] line: cheapestCombination){
				solutionIdNumbers.add(line[0]);
			}
      //Add the price of the solution combination to the total price
			this.price += price[cheapestIndex];
      //Reduce the amount needed of furniture needed
			amount -= amountReduced;
		}
		
    //If the temporary array is empty, then no combination can be created from left over parts. So, we scour all of the valid combinations for the cheapest combination.
		if(temporary.isEmpty()){
			//Calculate the price of each item in ValidCombos
			for(int y = 0; y < validCombos.size(); y++){
        //A singular combination
				ArrayList<String[]> temp = validCombos.get(y);
				for(int j = 0; j < temp.size(); j++){
          //Add the prices in a singular combination together
					price[y] += Integer.parseInt(temp.get(j)[1]);
				}
			}
			
			//Determine the index that has the cheapest price and the cheapest price itself
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
			//Get the combination of IDs that contain the cheapest price
			cheapestCombination = validCombos.get(cheapestIndex); 
			
			//Add the IDs into a solution arraylist
			for(String[] line: cheapestCombination){
				solutionIdNumbers.add(line[0]);
			}
      //Add the price of the solution combination to the total price 
			this.price += price[cheapestIndex];
      //Since this case only ever creates 1 furniture item, we only need to reduce the amount of required furniture by 1
			amount--;
		}

		//Remove all combinations from validCombos that already contain an ID from the solution
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
  //If the amount of furniture requested reaches 0, then the loop is done and we have a final solution.
	this.finalCombo = solutionIdNumbers;
  //Since we have confirmed the order, we can now delete the furiture from the inventory
	deleteFiling();
	return solutionIdNumbers;
} 

/**
 *This method is used to delete a filer entry from the table in the database.
 */
public void deleteFiling(){
  ArrayList<String> cheapestFiling = this.finalCombo;

  for(int i = 0; i < cheapestFiling.size(); i++){
    try {
    String query = "DELETE FROM filing WHERE ID = ?";
      PreparedStatement myStmt = this.myConnect.prepareStatement(query);
      myStmt.setString(1, cheapestFiling.get(i));             
      myStmt.executeUpdate();
      myStmt.close();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }//end of for loop

}//end of deleteFiling method



}//end of class Filing