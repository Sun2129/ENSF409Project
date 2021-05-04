package edu.ucalgary.ensf409;

import java.util.*;
import java.io.*;

/**
 *OrderForm.java
 *Purpose: This serves to write a txt file that contians the order information and result.
 *This includes the IDs of the furniture pieces that will be used and the price.
 *If the order was not possible, it will have information on manufacturers that produce the desired furniture.
 *
 *@version 1.0 04/05/2021
 *@author Sanchit Kumar, Miki Rowbottom, Alliana Dela Pena, Julien Campbell
 */
public class OrderForm{
  //Fields
  private ArrayList<String> finalCombo; //contains the ID's of the furnitures
  private String type;
  private int amount;
  private String furniture;
  private int price;
  private String[] manuNames;
  private boolean canOrder; //if order can be fulfilled then true

  /**
   *Overloaded public Constructor that will be used when the order can be fulfilled.
   *
   *@param amount The amount of pieces ordered.
   *@param type The type of furniture ordered. (Ex. mesh, executive, etc.)
   *@param furniture The furniture ordered. (Ex. chair, desk, etc.)
   *@param price The total price of the order.
   *@param finalCombo The IDs of the pieces of furniture used to fulfill order.
   */
  public OrderForm(int amount, String type, String furniture, int price, ArrayList<String> finalCombo){
    this.amount = amount;
    this.type = type;
    this.price = price;
	  this.furniture = furniture;
    this.finalCombo = finalCombo;
    this.canOrder = true;
	  createFile();
  }

  /**
   *Overloaded public Constructor that will be used when the order cannot be fulfilled.
   *
   *@param amount The amount of pieces ordered.
   *@param type The type of furniture ordered. (Ex. mesh, executive, etc.)
   *@param furniture The furniture ordered. (Ex. chair, desk, etc.)
   *@param manuNames The manufacturer names that the desired furniture could be purchased from.
   */
  public OrderForm(int amount, String type, String furniture, String[] manuNames){
    this.amount = amount;
    this.type = type;
	  this.furniture = furniture;
    this.manuNames = manuNames;
    this.canOrder = false;
	  createFile();
  }

  /**
   *Method used to create the textfile orderform.txt containing the order info.
   */
  public void createFile(){
    try{
      //Create a file called orderform.txt 
      FileWriter writer = new FileWriter("orderform.txt");
      //We then try to write to the file
      BufferedWriter buffWriter = new BufferedWriter(writer);
      buffWriter.write("Furniture Order Form \n\n");
      buffWriter.write("Faculty Name:\nContact:\nDate:\n\n");
      buffWriter.write("Original Request: " + this.type + " " + this.furniture + ", " + this.amount + "\n\n");

    
    if(this.canOrder){ // if order can be fulfilled
      buffWriter.write("Items Ordered:\n");
      
      //writes the IDs of the ordered furnitures to the file
      for(int i = 0; i < this.finalCombo.size(); i++){
        buffWriter.write(finalCombo.get(i));
		  if(i != this.finalCombo.size() - 1){
			buffWriter.write(", ");
		}
      }
      //write the price of the combination to the file
      buffWriter.write("\n\nTotal Price: $" + this.price);
    }
	
    else{ //if order cannot be fulfilled
      buffWriter.write("Order cannot be fulfilled based on current inventory.\n\n");
       buffWriter.write("Here is a list of suggested manufacturers that supply the requested furniture.\n");
      
      //writes the names of the manufacturers that supply the ordered furniture
      for(int i = 0; i < this.manuNames.length; i++){
        buffWriter.write(manuNames[i]);
		if(i != this.manuNames.length - 1){
			buffWriter.write(", ");
		}
      }
    }
      //Close the buffered writer
      if(buffWriter != null){
		try{
			buffWriter.close();
		}
		catch(Exception e){
			System.out.println("Error closing BufferedWriter");
			e.printStackTrace();
		}
	  }
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }
}
