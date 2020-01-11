/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yahtzee1;

import java.util.ArrayList;

/**
 *
 * @author Chris
 */
public class Player {
    public static int nOfPlayers = 0;
    private static ArrayList<String> personListString = new ArrayList<String>();
    private String name = "";
    private boolean[] partUsed= new boolean[13];
    private int[] playerScore = new int[13];
    private int upperTotal, lowerTotal, grandTotal;
    private int upperBonus=0, yahtzeeBonus=0;
    private boolean isRealPlayer;
    private boolean isPerson;
    
    public Player(boolean isReal, String pName){
        isRealPlayer = isReal;
        name = pName;
        isPerson=(!pName.equals("Guest"))&&isReal;
    }

    public int useCategory(int ID, int[] dice, boolean isYahtzeeBonus){
        int temp;
        boolean changed;
        int g;
        boolean scored = false;
        // sorting dice
        do {
            g = 0;
            changed = false;
            do {
                if (dice[g] > dice[g + 1]) {
                    temp = dice[g + 1];
                    dice[g + 1] = dice[g];
                    dice[g] = temp;
                    changed = true;
                }
                g = g + 1;
            } while (g < 4 && changed == false);
        } while (changed == true);
        
        int points=0;
        switch(ID){
            case 0: // ACES
            case 1: // TWOS
            case 2: // THREES
            case 3: // FOURS
            case 4: // FIVES
            case 5: // SIXES
                scored=true;
                break;
            case 6: // 3-OF-A-KIND
                for (int i=0;i<3;i++){
                    if (dice[i]==dice[i+1] && dice[i]==dice[i+2]){
                        scored = true;
                    }
                }
                break;
            case 7: // 4-OF-A-KIND
                for (int i=0;i<2;i++){
                    if (dice[i]==dice[i+1] && dice[i]==dice[i+2] && dice[i]==dice[i+3]){
                        scored = true;
                    }
                }
                break;
            case 8: // FULL HOUSE
                if ((dice[0]==dice[1] && dice[0]==dice[2])&&(dice[3]==dice[4])){
                        scored = true;
                    } else if ((dice[2]==dice[3] && dice[2]==dice[4])&&(dice[0]==dice[1])){
                        scored = true;
                    }
                break;
            case 9: // SMALL STRAIGHT
                if ((dice[0] + 1) == dice[1] && (dice[0] + 2) == dice[2] && (dice[0] + 3 == dice[3])) { // 0123 
                    scored = true;
                } else if ((dice[1] + 1) == dice[2] && ((dice[2] + 1) == dice[3]) && ((dice[3] + 1) == dice[4])) { // 1234
                    scored = true;
                } else if ((dice[0] + 1) == dice[1] && (dice[1] + 1) == dice[2] && ((dice[2] + 1) == dice[4])) { // 0124
                    scored = true;
                } else if (((dice[0] + 1) == dice[2]) && ((dice[2] + 1) == dice[3]) && ((dice[3] + 1) == dice[4])) { // 0234
                    scored = true;
                }
                break;
            case 10: // LARGE STRAIGHT
                scored=true;
                for (int i=0;i<5;i++){
                    if (dice[0]+i!=dice[i]){
                        scored=false;
                    }
                }
                break;
            case 11: // YAHTZEE
                if (isYahtzee(dice)){
                    scored=true;
                }
                break;
            case 12: // CHANCE
                scored=true;
                break;
        }
        
        if (isYahtzeeBonus == true) {
            scored = true;
            if (playerScore[11] == 50) {
                yahtzeeBonus += 100;
            }
        }
        
        if (scored==true){
            points = scorePoints(ID,dice);
        }
        
        playerScore[ID]=points;
        partUsed[ID]=true;
        return playerScore[ID];
    }
    
    public int useCategoryBot(int ID) { // for bots
        int points = 0;
        int PPoints = (int) (Math.random() * 432 + 1);
        switch (ID) {
            case 0: // ACES
                if (PPoints < 26) {
                    points = 0;
                } else if (PPoints < 175) {
                    points = 1;
                } else if (PPoints < 293) {
                    points = 2;
                } else if (PPoints < 365) {
                    points = 3;
                } else if (PPoints > 364) {
                    points = 4;
                }
                break;
            case 1: // TWOS
                if (PPoints < 18) {
                    points = 0;
                } else if (PPoints < 74) {
                    points = 2;
                } else if (PPoints < 214) {
                    points = 4;
                } else if (PPoints < 346) {
                    points = 6;
                } else if (PPoints > 345) {
                    points = 8;
                }
                break;
            case 2: // THREES
                if (PPoints < 6) {
                    points = 0;
                } else if (PPoints < 31) {
                    points = 3;
                } else if (PPoints < 131) {
                    points = 6;
                } else if (PPoints < 298) {
                    points = 9;
                } else if (PPoints < 432) {
                    points = 12;
                } else if (PPoints < 433) {
                    points = 15;
                }
                break;
            case 3: // FOURS
                if (PPoints < 6) {
                    points = 0;
                } else if (PPoints < 22) {
                    points = 4;
                } else if (PPoints < 99) {
                    points = 8;
                } else if (PPoints < 302) {
                    points = 12;
                } else if (PPoints < 432) {
                    points = 16;
                } else if (PPoints < 433) {
                    points = 20;
                }
                break;
            case 4: // FIVES
                if (PPoints < 4) {
                    points = 0;
                } else if (PPoints < 19) {
                    points = 5;
                } else if (PPoints < 81) {
                    points = 10;
                } else if (PPoints < 294) {
                    points = 15;
                } else if (PPoints < 431) {
                    points = 20;
                } else if (PPoints < 433) {
                    points = 25;
                }
                break;
            case 5: // SIXES
                if (PPoints < 4) {
                    points = 0;
                } else if (PPoints < 19) {
                    points = 6;
                } else if (PPoints < 67) {
                    points = 12;
                } else if (PPoints < 290) {
                    points = 18;
                } else if (PPoints < 431) {
                    points = 24;
                } else if (PPoints < 433) {
                    points = 30;
                }
                break;
            case 6: // 3-OF-A-KIND
                if (PPoints < 32) {
                    points = 0;
                } else if (PPoints < 34) {
                    points = 7;
                } else if (PPoints < 39) {
                    points = 8;
                } else if (PPoints < 47) {
                    points = 9;
                } else if (PPoints < 52) {
                    points = 10;
                } else if (PPoints < 62) {
                    points = 11;
                } else if (PPoints < 72) {
                    points = 12;
                } else if (PPoints < 81) {
                    points = 13;
                } else if (PPoints < 90) {
                    points = 14;
                } else if (PPoints < 114) {
                    points = 15;
                } else if (PPoints < 135) {
                    points = 16;
                } else if (PPoints < 153) {
                    points = 17;
                } else if (PPoints < 176) {
                    points = 18;
                } else if (PPoints < 198) {
                    points = 19;
                } else if (PPoints < 233) {
                    points = 20;
                } else if (PPoints < 250) {
                    points = 21;
                } else if (PPoints < 283) {
                    points = 22;
                } else if (PPoints < 327) {
                    points = 23;
                } else if (PPoints < 369) {
                    points = 24;
                } else if (PPoints < 396) {
                    points = 25;
                } else if (PPoints < 404) {
                    points = 26;
                } else if (PPoints < 424) {
                    points = 27;
                } else if (PPoints < 433) {
                    points = 28;
                }
                break;
            case 7: // 4-OF-A-KIND
                if (PPoints < 187) {
                    points = 0;
                } else if (PPoints < 194) {
                    points = 6;
                } else if (PPoints < 202) {
                    points = 7;
                } else if (PPoints < 209) {
                    points = 8;
                } else if (PPoints < 225) {
                    points = 9;
                } else if (PPoints < 235) {
                    points = 10;
                } else if (PPoints < 241) {
                    points = 11;
                } else if (PPoints < 248) {
                    points = 12;
                } else if (PPoints < 259) {
                    points = 13;
                } else if (PPoints < 278) {
                    points = 14;
                } else if (PPoints < 286) {
                    points = 16;
                } else if (PPoints < 308) {
                    points = 17;
                } else if (PPoints < 325) {
                    points = 18;
                } else if (PPoints < 329) {
                    points = 19;
                } else if (PPoints < 345) {
                    points = 21;
                } else if (PPoints < 359) {
                    points = 22;
                } else if (PPoints < 365) {
                    points = 23;
                } else if (PPoints < 371) {
                    points = 24;
                } else if (PPoints < 380) {
                    points = 25;
                } else if (PPoints < 406) {
                    points = 26;
                } else if (PPoints < 416) {
                    points = 27;
                } else if (PPoints < 424) {
                    points = 28;
                } else if (PPoints < 433) {
                    points = 29;
                }
                break;
            case 8: // FULL HOUSE
                if (PPoints < 298) {
                    points = 25;
                }
                break;
            case 9: // SMALL STRAIGHT
                if (PPoints < 401) {
                    points = 30;
                }
                break;
            case 10: // LARGE STRAIGHT
                if (PPoints < 261) {
                    points = 40;
                }
                break;
            case 11: // YAHTZEE
                if (PPoints < 130) {
                    points = 50;
                }
                break;
            case 12: // CHANCE
                if (PPoints < 4) {
                    points = 9;
                } else if (PPoints < 9) {
                    points = 10;
                } else if (PPoints < 12) {
                    points = 11;
                } else if (PPoints < 18) {
                    points = 12;
                } else if (PPoints < 33) {
                    points = 13;
                } else if (PPoints < 42) {
                    points = 14;
                } else if (PPoints < 59) {
                    points = 15;
                } else if (PPoints < 86) {
                    points = 16;
                } else if (PPoints < 119) {
                    points = 17;
                } else if (PPoints < 165) {
                    points = 18;
                } else if (PPoints < 203) {
                    points = 19;
                } else if (PPoints < 239) {
                    points = 20;
                } else if (PPoints < 289) {
                    points = 21;
                } else if (PPoints < 328) {
                    points = 22;
                } else if (PPoints < 364) {
                    points = 23;
                } else if (PPoints < 393) {
                    points = 24;
                } else if (PPoints < 412) {
                    points = 25;
                } else if (PPoints < 426) {
                    points = 26;
                } else if (PPoints < 429) {
                    points = 27;
                } else if (PPoints < 432) {
                    points = 28;
                } else if (PPoints > 431) {
                    points = 29;
                }
                break;
        }
        
        playerScore[ID]=points;
        partUsed[ID]=true;
        return playerScore[ID];
    }
        
    public void updateTotals(){
        upperTotal=0;
        lowerTotal=0;
        for (int i=0;i<6;i++){
            upperTotal+=playerScore[i];
        }
        if (upperTotal>62){
            upperBonus=35;
        }
        upperTotal+=upperBonus;
        for (int i=6;i<13;i++){
            lowerTotal+=playerScore[i];
        }
        lowerTotal+=yahtzeeBonus;
        grandTotal = upperTotal+lowerTotal;        
    }
    
    public static boolean isYahtzee(int[] dice){
        boolean yahtzee = true;
                for (int i=1; i<5;i++){
                    if (dice[0]!=dice[i]){
                        yahtzee=false;
                    }
                }
        return yahtzee;
    }
    
    public boolean partUsed(int partID){
        return partUsed[partID];
    }
    
    private int scorePoints(int catID,int[] dice){
        int points = 0;
        switch (catID) {
            case 0: // ACES
            case 1: // TWOS
            case 2: // THREES
            case 3: // FOURS
            case 4: // FIVES
            case 5: // SIXES
                for (int i = 0; i < 5; i++) {
                    if (dice[i] == (catID+1)) {
                        points += (catID+1);
                    }
                }
                break;
            case 8: // FULL HOUSE
                points = 25;
                break;
            case 9: // SMALL STRAIGHT
                points = 30;
                break;
            case 10: // LARGE STRAIGHT
                points = 40;
                break;
            case 11: // YAHTZEE
                points = 50;
                break;
            case 6: // 3-OF-A-KIND
            case 7: // 4-OF-A-KIND
            case 12: // CHANCE
                for (int i = 0; i < 5; i++) {
                    points += dice[i];
                }
                break;
        } 
        return points;
    }

    public boolean isReal(){
        return isRealPlayer;
    }
    
    public boolean isPerson(){
        return isPerson;
    }
    
    public String getName(){
        return name;
    }

    public int getUpperBonus(){
        return upperBonus;
    }
    
    public int[] getScoreArray(){
        return playerScore;
    }
    
    public int getScoreByCategory(int categoryID){
        return playerScore[categoryID];
    }
    public int getUpperTotal(){
        return upperTotal;
    }
    public int getLowerTotal(){
        return lowerTotal;
    }
    public int getGrandTotal(){
        return grandTotal;
    }
    public int getYahtzeeBonus(){
        return yahtzeeBonus;
    }
}