/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yahtzee1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FilenameUtils;
/**
 *
 * @author Chris
 */
public class Person{
    private static int nOfPeople = 0;
    private static List<String> personListString = new ArrayList<String>();
    
    private String name = "";
    private int pb = 0;
    private int timesPlayed = 0;
    private int timesWon = 0;
    private int totalPoints = 0;
    private int upperPoints = 0;
    private int lowerPoints = 0;
    private int upperBonuses = 0;
    private int totalYahtzees =0;
    private int yahtzeeBonuses = 0;
    
    public Person(boolean isReal, String pName){
        name = pName;
        if (isReal==true){
            writePersonByPerson();
            updatePeople();
        }
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public void writePersonByPerson(){ // MAKE ON-CLOSE EVENT
        try{
            PrintWriter writer = new PrintWriter("yResources/people/"+name +".txt");
            writer.println(name);
            writer.println(pb);
            writer.println(timesPlayed);
            writer.println(timesWon);
            writer.println(totalPoints);
            writer.println(upperPoints);
            writer.println(lowerPoints);
            writer.println(upperBonuses);
            writer.println(totalYahtzees);
            writer.println(yahtzeeBonuses);
            writer.close();
        }catch(IOException e){
        }
    }
    
    public void destroyPerson(){
        File personFile = new File("yResources/people/"+name +".txt");
        personFile.delete();
        updatePeople();
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public String getName(){
        return name;
    }
    
    public int getPB(){
        return pb;
    }
            
    public int getTimesPlayed(){
        return timesPlayed;
    }

    public int getTimesWon(){
        return timesWon;
    }

    public int getTotalPoints(){
        return totalPoints;
    }
    
    public int getUpperPoints(){
        return upperPoints;
    }
    
    public int getLowerPoints(){
        return lowerPoints;
    }    
    
    public int getUpperBonuses(){
        return upperBonuses;
    }
    
    public int getTotalYahtzees(){
        return totalYahtzees;
    }
    
    public int getYahtzeeBonuses(){
        return yahtzeeBonuses;
    }
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public double upperPointsAvg(){
        double avg;
        try {
            avg = (((double) upperPoints) / ((double) timesPlayed));
        } catch (ArithmeticException divError) {
            avg = 0;
        }
        return avg;
    }
    
    public double lowerPointsAvg(){
        double avg;
        try {
            avg = (((double) lowerPoints) / ((double) timesPlayed));
        } catch (ArithmeticException divError) {
            avg = 0;
        }
        return avg;
    }
    
    public double totalPointsAvg(){
        double avg;
        try {
            avg = (((double) totalPoints) / ((double) timesPlayed));
        } catch (ArithmeticException divError) {
            avg = 0;
        }
        return avg;
    }
    
    public double yahtzeeAvg(){
        double avg;
        try {
            avg = (((double) totalYahtzees) / ((double) timesPlayed));
        } catch (ArithmeticException divError) {
            avg = 0;
        }
        return avg;
    }
    
    public int upperBonusPercent() {
        int upp;
        try {
            upp = (int) (((double) upperBonuses) / ((double) timesPlayed) * 100);
        } catch (ArithmeticException divError) {
            upp = 0;
        }
        return upp;
    }

    public int winPercent() {
        int percent;
        try {
            percent = (int) ((((double) timesWon) / ((double) timesPlayed)) * 100);
        } catch (ArithmeticException divError) {
            percent = 0;
        }
        return percent;
    }

// - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public void setName(String pName){
        name = pName;
    }
    
    public boolean checkPB(int points){
        if (points>pb){
            pb = points;
            return true;
        }
        return false;
    }
    
    public void addTimePlayed(){
        timesPlayed = timesPlayed+1;
    }

    public void addTimeWon(){
        timesWon += 1;
    }
    
    public void addPoints(int nOfPoints){
        totalPoints += nOfPoints;
    }
    
    public void addUpperPoints(int points){
        upperPoints += points;
    }
    
    public void addLowerPoints(int points){
        lowerPoints += points;
    }
     
    public void addUpperBonus(){
        upperBonuses += 1;
    }
   
    public void addYahtzees(int yahtzees){
        totalYahtzees +=yahtzees;
    }

    public void addYahtzeeBonuses(int points){
        yahtzeeBonuses+=points;
    }
    
    
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - \\
    // statics
    
    // remembering person from file
    public static Person readPersonByName(String pName){
        try{            
                Scanner personListInput = new Scanner(new File("yResources/people/"+pName +".txt"));
                Person newPerson = new Person(false, "");
                newPerson.name = personListInput.nextLine();
                newPerson.pb = Integer.parseInt(personListInput.nextLine());
                newPerson.timesPlayed = Integer.parseInt(personListInput.nextLine());
                newPerson.timesWon = Integer.parseInt(personListInput.nextLine());
                newPerson.totalPoints = Integer.parseInt(personListInput.nextLine());
                newPerson.upperPoints = Integer.parseInt(personListInput.nextLine());
                newPerson.lowerPoints = Integer.parseInt(personListInput.nextLine());
                newPerson.upperBonuses = Integer.parseInt(personListInput.nextLine());
                newPerson.totalYahtzees = Integer.parseInt(personListInput.nextLine());
                newPerson.yahtzeeBonuses = Integer.parseInt(personListInput.nextLine());

                personListInput.close();
                
                return newPerson;
        }catch(FileNotFoundException fnfe){
            return null;
        }
    }
    
    public static int getNOfPeople(){
        return nOfPeople;
    }
        
    public static ObservableList<String> getPersonListList(){
        ObservableList<String> listPerson = FXCollections.observableArrayList();
        listPerson.addAll(personListString);
        return listPerson;
    }
    
    public static String[] getPersonListString(){
        String[] personList = new String[personListString.size()];
        personListString.toArray(personList);
        return personList;
    }
    
    public static boolean isInvalid(String pName){
        String[] personList = getPersonListString();
        for (String personList1 : personList) {
            if (pName.equals(personList1)) {
                return true;
            }
        }
        if (pName.isEmpty() || pName.equals("Guest")){
            return true;
        }
        return false;
    }
    
    public static void updatePeople(){
        personListString.clear();
        File[] listOfFiles = new File("yResources/people/").listFiles();
        nOfPeople = listOfFiles.length;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                personListString.add(FilenameUtils.removeExtension(listOfFiles[i].getName()));
            }
        }
    }
}