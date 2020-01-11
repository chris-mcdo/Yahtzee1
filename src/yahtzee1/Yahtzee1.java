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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Chris
 */
public class Yahtzee1 extends Application {
    
    private static boolean yBonus=false;
    private static Player selectedPlayer;
    private static List<Dice> diceList = new ArrayList<>();
    private static int turn=0;
    private static int nOfRolls;
    private static int nOfTurns=1;
    private static List<Person> personList;
    private static double ssVolume, sfxVolume;
    
    @Override
    public void start(Stage primaryStage) {
        
        AudioClip startSound = new AudioClip(Yahtzee1.class.getResource("resources/fanfareIntro.wav").toString());
        primaryStage.getIcons().add(new Image(Yahtzee1.class.getResourceAsStream("resources/yahtzeeIcon.png")));
        
        //  Pane and scene, +style
        Pane root = new Pane();
        root.setStyle("-fx-background-color: rgb(255,110,110)");
        Scene scene = new Scene(root, 300, 250);
        scene.setFill(Color.rgb(255, 110, 110));
        
        Person.updatePeople();
        personList = rememberPeople();
        fetchPrefs();
        
        Button btnStart = new Button();
        Button btnHighscores = new Button();
        Button btnResetHS = new Button();
        Button btnDeletePerson = new Button();
        Button btnDeleteAll = new Button();
        Button btnProfile = new Button();
        Button btnRules = new Button();
        btnStart.setText("Start!");
        btnHighscores.setText("High scores");
        btnResetHS.setText("Reset high scores");
        btnDeletePerson.setText("Delete person");
        btnDeleteAll.setText("Delete all people");
        btnProfile.setText("Profile");
        btnRules.setText("Rules");
        
        Alert alertMsg = new Alert(AlertType.INFORMATION);
        Alert warnMsg = new Alert(AlertType.WARNING);
        Alert errorMsg = new Alert(AlertType.ERROR);
        warnMsg.setHeaderText("Are you sure?");
        warnMsg.getDialogPane().getButtonTypes().setAll(ButtonType.OK,ButtonType.CANCEL);
        
        CheckBox useBots = new CheckBox("Use bots?");
        useBots.setSelected(false);
        CheckBox disableSFX = new CheckBox("Disable SFX");
        disableSFX.setSelected(sfxVolume==0);
        CheckBox disableSS = new CheckBox("Disable start sound");
        disableSS.setSelected(ssVolume==0);
        
        ComboBox cBoxNOfPlayers = new ComboBox(FXCollections.observableArrayList(1,2,3));
        ComboBox cBoxNOfBots = new ComboBox(FXCollections.observableArrayList(0));
        ComboBox cBoxPerson = new ComboBox(Person.getPersonListList());
        cBoxNOfPlayers.setValue(2);
        Label lblHumans = new Label("(Humans)");
        Label lblBots = new Label("(Bots)");
        
        disableSS.setOnAction(e->{
            toggleVolume(0);
        });
        disableSFX.setOnAction(e -> {
            toggleVolume(1);
        });
        
        useBots.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                if (useBots.isSelected()==true){
                    cBoxNOfBots.getSelectionModel().clearSelection();
                    cBoxNOfBots.getItems().clear();
                    cBoxNOfBots.setItems(FXCollections.observableArrayList(1,2,3));
                    cBoxNOfBots.setValue(1);
                    cBoxNOfBots.setDisable(false);
                } else {
                    cBoxNOfBots.getSelectionModel().clearSelection();
                    cBoxNOfBots.getItems().clear();
                    cBoxNOfBots.setItems(FXCollections.observableArrayList(0));
                    cBoxNOfBots.setValue(0);
                    cBoxNOfBots.setDisable(true);
                }
            }
        });
        cBoxNOfBots.setValue(0);

        btnStart.setOnAction(e -> {
            primaryStage.hide();
            gameScreen(primaryStage, Integer.parseInt(cBoxNOfPlayers.getValue().toString()), Integer.parseInt(cBoxNOfBots.getValue().toString()));
            disableSFX.setSelected(sfxVolume == 0);
            disableSS.setSelected(ssVolume == 0);
            cBoxPerson.getSelectionModel().clearSelection();
            cBoxPerson.getItems().clear();
            cBoxPerson.setItems(Person.getPersonListList());
            if (Person.getNOfPeople() != 0) {
                cBoxPerson.getSelectionModel().select(personList.get(0).getName());
                cBoxPerson.setDisable(false);
                btnDeletePerson.setDisable(false);
                btnDeleteAll.setDisable(false);
                btnProfile.setDisable(false);
            }
        });

        btnRules.setOnAction(e -> {
            rulesScreen();
        });

        // highscores button setup
        btnHighscores.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                String[] highscores = getHighscores();
                String highscoresmessage = "";
                for (int i = 1;i<(highscores.length);i++){
                    highscoresmessage += highscores[i] + "\n";
                }
                if (highscores.length==0){
                alertMsg.setHeaderText("No high scores found");
                } else {
                alertMsg.setHeaderText(highscores[0]);
            }
                alertMsg.setTitle("High scores");
                alertMsg.setContentText(highscoresmessage);
                alertMsg.showAndWait();
            }
        });
        
        //resetHighscores button setup
        btnResetHS.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                warnMsg.setTitle("Reset highscores");
                warnMsg.setContentText("You are about to reset all highscores. Are you sure you want to do this?");
                Optional<ButtonType> result = warnMsg.showAndWait();
                if (result.isPresent()) {
                    if (result.get() == ButtonType.OK) {
                        if (resetHighScores() == true) {
                            alertMsg.setTitle("Success");
                            alertMsg.setHeaderText("High scores reset successfully");
                            alertMsg.setContentText("");
                            alertMsg.showAndWait();
                        } else {
                            errorMsg.setTitle("Error");
                            errorMsg.setHeaderText("Error resetting highscores...");
                            errorMsg.setContentText("");
                            errorMsg.showAndWait();
                        }
                    }
                }
            }
        });
        
        btnDeletePerson.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                Person selectedPerson = personList.get(getPersonIndexByName(cBoxPerson.getValue().toString()));
                warnMsg.setTitle(selectedPerson.getName());
                warnMsg.setContentText("You are about to delete person " + selectedPerson.getName() + ". Are you sure you want to do this?");
                Optional<ButtonType> result = warnMsg.showAndWait();
                if (result.isPresent()) {
                    if (result.get() == ButtonType.OK) {
                        personList.clear();
                        selectedPerson.destroyPerson();
                        personList = rememberPeople();
                        cBoxPerson.getSelectionModel().clearSelection();
                        cBoxPerson.getItems().clear();
                        cBoxPerson.setItems(Person.getPersonListList());
                        if (Person.getNOfPeople() == 0) {
                            cBoxPerson.setDisable(true);
                            btnDeletePerson.setDisable(true);
                            btnDeleteAll.setDisable(true);
                            btnProfile.setDisable(true);
                        } else {
                            cBoxPerson.getSelectionModel().select(personList.get(0).getName());
                        }
                    }
                }
            }
        });
        
        btnDeleteAll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                warnMsg.setTitle("Confirm");
                warnMsg.setContentText("You are about to delete all people. Are you sure you want to do this?");
                Optional<ButtonType> result = warnMsg.showAndWait();
                if (result.isPresent()) {
                    if (result.get() == ButtonType.OK) {
                        for (int i = 0; Person.getNOfPeople() != 0; i++) {
                            personList.get(i).destroyPerson();
                        }
                        personList.clear();
                        personList = rememberPeople();
                        cBoxPerson.getSelectionModel().clearSelection();
                        cBoxPerson.getItems().clear();
                        cBoxPerson.setDisable(true);
                        btnDeletePerson.setDisable(true);
                        btnDeleteAll.setDisable(true);
                        btnProfile.setDisable(true);
                    }
                }
            }
        });
        
        btnProfile.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                Person selectedPerson = personList.get(getPersonIndexByName(cBoxPerson.getValue().toString()));
                alertMsg.setTitle(selectedPerson.getName());
                alertMsg.setHeaderText(selectedPerson.getName() +"'s profile");
                String msgText = "";
                msgText += "Name: " +selectedPerson.getName();
                msgText += "\nWin percentage: " +selectedPerson.winPercent() +"% ("+selectedPerson.getTimesWon() +"/"+selectedPerson.getTimesPlayed() +")";
                msgText += "\nAverage points: " +selectedPerson.totalPointsAvg();
                msgText += "\nPersonal best: " +selectedPerson.getPB();
                msgText += "\nUpper-section points per game: " +selectedPerson.upperPointsAvg();
                msgText += "\nUpper bonus scored percentage: " +selectedPerson.upperBonusPercent() +"%";
                msgText += "\nLower-section points per game: " +selectedPerson.lowerPointsAvg();
                msgText += "\nYahtzees scored per game: " +selectedPerson.yahtzeeAvg();
                msgText += "\nTotal Yahtzee bonuses scored: " +selectedPerson.getYahtzeeBonuses();
                
                alertMsg.setContentText(msgText);
                alertMsg.showAndWait();
            }
        });
        
        btnStart.setPrefSize(80, 30);
        
        root.getChildren().add(lblHumans);
        root.getChildren().add(lblBots);
        root.getChildren().add(btnStart);
        root.getChildren().add(btnHighscores);
        root.getChildren().add(btnResetHS);
        root.getChildren().add(btnDeletePerson);
        root.getChildren().add(btnDeleteAll);
        root.getChildren().add(btnProfile);
        root.getChildren().add(btnRules);
        root.getChildren().add(useBots);
        root.getChildren().add(disableSS);
        root.getChildren().add(disableSFX);
        root.getChildren().add(cBoxNOfPlayers);
        root.getChildren().add(cBoxNOfBots);
        root.getChildren().add(cBoxPerson);
        
        btnHighscores.relocate(190, 40);
        btnResetHS.relocate(190, 70);
        btnDeletePerson.relocate(20, 10);
        btnDeleteAll.relocate(20, 40);
        useBots.relocate(20, 80);
        disableSS.relocate(150, 150);
        disableSFX.relocate(150, 180);
        cBoxNOfPlayers.relocate(20, 110);
        cBoxNOfBots.relocate(20, 145);
        cBoxPerson.relocate(110, 10);
        btnProfile.relocate(190, 10);
        btnRules.relocate(20, 185);
        btnStart.relocate(scene.getWidth()/2 - btnStart.getPrefWidth()/2, 220);
        lblHumans.relocate(cBoxNOfPlayers.getLayoutX()+55, cBoxNOfPlayers.getLayoutY()+4);
        lblBots.relocate(cBoxNOfBots.getLayoutX()+55, cBoxNOfBots.getLayoutY()+4);
        
        if (Person.getNOfPeople()==0){
            cBoxPerson.setDisable(true);
            btnDeletePerson.setDisable(true);
            btnDeleteAll.setDisable(true);
            btnProfile.setDisable(true);
        } else {
            cBoxPerson.getSelectionModel().select(personList.get(0).getName());
        }
        cBoxNOfBots.setDisable(true);
        
        primaryStage.setResizable(false);
        primaryStage.setTitle("Yahtzee");
        primaryStage.setScene(scene);
        primaryStage.show();
        startSound.play(ssVolume);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (!(new File("YResources").isDirectory())){
            new File("YResources/people").mkdirs();
            try {
                new File("YResources/highscores.txt").createNewFile();
                new File("YResources/prefs.txt").createNewFile();
                System.out.println("Looks like your program is broken bro...");
            } catch (IOException x) {
                System.out.println(x);
            }
        }
        launch(args);
    }
    
   public static void gameScreen(Stage mainStage, int nOfPlayers, int nOfBots){
       CheckBox disableSFX = new CheckBox("Disable SFX");
       disableSFX.setSelected(sfxVolume==0);
       CheckBox disableSS = new CheckBox("Disable start sound");
       disableSS.setSelected(ssVolume==0);
       AudioClip rollSound = new AudioClip(Yahtzee1.class.getResource("resources/diceRoll.wav").toString());
       AudioClip winSound = new AudioClip(Yahtzee1.class.getResource("resources/winSound.wav").toString());
        disableSS.setOnAction(e->{
            toggleVolume(0);
        });
        disableSFX.setOnAction(e -> {
            toggleVolume(1);
        });
       List<Player> playerList = new ArrayList<>();
       
       Font labelFont = new Font("Calibri", 13.5);
       Font catFont = new Font("Calibri Light", 12);

       Player.nOfPlayers = nOfPlayers + nOfBots;
       Label[] lblNames = new Label[Player.nOfPlayers];
       Label[][] lblArray = new Label[Player.nOfPlayers][13];
       String[] options = new String[]{"Aces", "Twos", "Threes", "Fours", "Fives", "Sixes", "Upper Bonus", "Upper Total", "3-Of-A-Kind", "4-Of-A-Kind", "Full House", "Small Straight", "Large Straight", "Yahtzee", "Chance", "Yahtzee Bonus", "Lower Total", "Grand Total", ""};

       Label lblTurn = new Label();
       lblTurn.setTextFill(Color.DARKGREEN);
       
       BorderStroke bonusBStroke = new BorderStroke(Paint.valueOf("green"), BorderStrokeStyle.SOLID, new CornerRadii(8), null);
       BorderStroke totalsBStroke = new BorderStroke(Paint.valueOf("red"), BorderStrokeStyle.SOLID, new CornerRadii(8), null);
       BorderStroke catBStroke = new BorderStroke(Paint.valueOf("ROYALBLUE"), BorderStrokeStyle.SOLID, new CornerRadii(5), null);
       Border totalsBorder = new Border(totalsBStroke);
       Border bonusBorder = new Border(bonusBStroke);
       Border catBorder = new Border(catBStroke);

       Stage stage = new Stage();
       stage.getIcons().add(new Image(Yahtzee1.class.getResourceAsStream("resources/yahtzeeIcon.png")));
       Pane root = new Pane();
       Scene scene = new Scene(root, 250 + Player.nOfPlayers * 100, 550,Color.rgb(255, 110, 110));
       root.setStyle("-fx-background-color: rgb(255,110,110)");
       int labelWidth = (int)(scene.getWidth()-120)/8+5*(12-Player.nOfPlayers*2);
       int labelHeight = (int)scene.getHeight()/30;
       Alert finished = new Alert(AlertType.INFORMATION);

       ChoiceDialog<String> yBonusChoice = new ChoiceDialog<>();
       yBonusChoice.setTitle("Yahtzee Bonus");
       yBonusChoice.setHeaderText("Select category");
       yBonusChoice.setContentText("You have scored a Yahtzee but your Yahtzee category has already been used!\nSelect another category to use from the list.");
       yBonusChoice.setHeight(225);
       yBonusChoice.setWidth(300);
       yBonusChoice.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
       yBonusChoice.initModality(Modality.APPLICATION_MODAL);
       yBonusChoice.initStyle(StageStyle.UNDECORATED);

       Dialog<ButtonType> nameChoice = new Dialog<>(); // create custom dialog for name input
       nameChoice.setTitle("Select person");
       nameChoice.setGraphic(new ImageView(new Image(Yahtzee1.class.getResourceAsStream("resources/playerGraphic.png"), 80, 75, false, true)));
       ButtonType usePersonType = new ButtonType("Use person");
       ButtonType createPersonType = new ButtonType("Create person");
       ButtonType useGuestType = new ButtonType("Play as guest");
       nameChoice.getDialogPane().getButtonTypes().addAll(usePersonType, createPersonType, useGuestType);
       GridPane grid = new GridPane();
       grid.setHgap(10);
       grid.setVgap(10);
       grid.setPadding(new Insets(20, 150, 10, 10));
       TextField txtNewPerson = new TextField();
       txtNewPerson.setPromptText("your name...");
       txtNewPerson.textProperty().addListener((observable, oldValue, newValue) -> {
           nameChoice.getDialogPane().lookupButton(createPersonType).setDisable(Person.isInvalid(newValue));
       });
       nameChoice.getDialogPane().lookupButton(createPersonType).setDisable(true);
       ChoiceBox personChoice = new ChoiceBox();
       grid.add(new Label("Select person: "), 0, 0);
       grid.add(personChoice, 1, 0);
       grid.add(new Label("New person: "), 0, 1);
       grid.add(txtNewPerson, 1, 1);
       nameChoice.getDialogPane().setContent(grid);
       Platform.runLater(() -> personChoice.requestFocus());
       
       Button btnRoll = new Button();
       Button btnBack = new Button();
       Button btnStartGame = new Button();
       Button btnRules = new Button();
       btnRoll.setText("Roll");
       btnBack.setText("Go back");
       btnStartGame.setText("Start");
       btnRules.setText("Rules");
       ImageView dBackgroundView = new ImageView(new Image(Yahtzee1.class.getResourceAsStream("resources/diceBackground.png")));
       dBackgroundView.relocate(scene.getWidth()-130, 80);
       root.getChildren().add(dBackgroundView);
       
       Label[] lblUB = new Label[Player.nOfPlayers];
       Label[] lblYB = new Label[Player.nOfPlayers];
       Label[] lblUT = new Label[Player.nOfPlayers];
       Label[] lblLT = new Label[Player.nOfPlayers];
       Label[] lblGT = new Label[Player.nOfPlayers];
       Label[] lblPosition = new Label[Player.nOfPlayers];
       
       class Utils {
  
           public void rollDice() {
               btnRoll.setDisable(nOfRolls == 2);
               for (Dice diceList1 : diceList) {
                   if (diceList1.isReRoll()) {
                       diceList1.roll();
                       if (!rollSound.isPlaying()) {
                           rollSound.play(sfxVolume);
                       }
                   }
               }
               nOfRolls = nOfRolls + 1;
               if (Player.isYahtzee(getDiceArray()) && selectedPlayer.partUsed(11) == true) { // YAHTZEE BONUS
                   yBonus = true;
                   boolean bonusLowerEmpty = false;
                   List<String> choices = new ArrayList<>();

                   if (selectedPlayer.partUsed(diceList.get(0).getValue() - 1)) {
                       for (int i = 6; i < 13; i++) {
                           if (!selectedPlayer.partUsed(i)) {
                               choices.add(options[i + 2]);
                               bonusLowerEmpty = true;
                           }
                       }
                       if (bonusLowerEmpty == false) {
                           for (int i = 0; i < 6; i++) {
                               if (!selectedPlayer.partUsed(i)) {
                                   choices.add(options[i]);
                               }
                           }
                       }
                   } else {
                       choices.add(options[diceList.get(0).getValue() - 1]);
                   }
                   yBonusChoice.getItems().setAll(choices);
                   yBonusChoice.setSelectedItem(choices.get(0));
                   Optional<String> result = yBonusChoice.showAndWait();
                   new Utils().lblClicked(turn, getCatByName(result.get()));
                   yBonus = false;
               }
           }
           
           public void clear() {
               playerList.clear();
               diceList.clear();
               turn = 0;
               nOfTurns = 1;
               nOfRolls = 0;
           }
           
           public void lblClicked(int iFinal, int gFinal) {
               if (selectedPlayer == playerList.get(iFinal)) {
                   if (!selectedPlayer.partUsed(gFinal)) {
                       if (!selectedPlayer.isReal()) {
                           lblArray[iFinal][gFinal].setText(String.valueOf(selectedPlayer.useCategoryBot(gFinal))); // move
                       } else {
                           lblArray[iFinal][gFinal].setText(String.valueOf(selectedPlayer.useCategory(gFinal, getDiceArray(), yBonus))); // move
                       }
                       selectedPlayer.updateTotals(); // move labels to player class to remove this
                       lblUB[iFinal].setText(Integer.toString(selectedPlayer.getUpperBonus()));
                       lblYB[iFinal].setText(Integer.toString(selectedPlayer.getYahtzeeBonus()));
                       lblUT[iFinal].setText(Integer.toString(selectedPlayer.getUpperTotal()));
                       lblLT[iFinal].setText(Integer.toString(selectedPlayer.getLowerTotal()));
                       lblGT[iFinal].setText(Integer.toString(selectedPlayer.getGrandTotal()));

                       lblNames[turn].setStyle("-fx-font-weight: normal;");
                       turn = nOfTurns % Player.nOfPlayers;
                       selectedPlayer = playerList.get(turn);
                       nOfTurns = nOfTurns + 1;
                       
                       if (nOfTurns > Player.nOfPlayers * 13) {
                           lblNames[turn].setStyle("-fx-font-weight: normal;");
                           lblTurn.setText("");
                           btnRoll.setDisable(true);
                           boolean changed;
                           int g, temp;
                           String additionalMessages = "";
                           Person selectedPerson;
                           int[] position = new int[Player.nOfPlayers];
                           for (int i = 0; i < position.length; i++) { // position[0]=id of first person
                               position[i] = i;
                           }
                           do { // sort player positions
                               g = 0;
                               changed = false;
                               while (g < Player.nOfPlayers - 1 && changed == false) {
                                   if (playerList.get(position[g]).getGrandTotal() < playerList.get(position[g + 1]).getGrandTotal()) {
                                       temp = position[g + 1];
                                       position[g + 1] = position[g];
                                       position[g] = temp;
                                       changed = true;
                                   }
                                   g = g + 1;
                               }
                           } while (changed == true);
                           for (int b = 0; b < Player.nOfPlayers; b++) {
                               lblPosition[position[b]].setText(Integer.toString(b + 1));
                               if (playerList.get(b).isPerson()) {
                                   selectedPerson = personList.get(getPersonIndexByName(playerList.get(b).getName()));
                                   if (playerList.size() != 1) {
                                       selectedPerson.addUpperPoints(playerList.get(b).getUpperTotal());
                                       selectedPerson.addLowerPoints(playerList.get(b).getLowerTotal());
                                       if (playerList.get(b).getScoreByCategory(11) == 50) {
                                           int nOfYahtzees = 1;
                                           if (playerList.get(b).getYahtzeeBonus() != 0) {
                                               nOfYahtzees += playerList.get(b).getYahtzeeBonus() / 100;
                                               selectedPerson.addYahtzeeBonuses(playerList.get(b).getYahtzeeBonus() / 100);
                                           }
                                           selectedPerson.addYahtzees(nOfYahtzees);
                                       }
                                       selectedPerson.addTimePlayed();
                                       if (b == position[0]) {
                                           selectedPerson.addTimeWon();
                                       }

                                       selectedPerson.addPoints(playerList.get(b).getGrandTotal());
                                       if (playerList.get(b).getUpperBonus() == 35) {
                                           selectedPerson.addUpperBonus();
                                       }
                                   }
                                   if (selectedPerson.checkPB(playerList.get(b).getGrandTotal())) {
                                       additionalMessages += "\n" + playerList.get(b).getName() + ": New personal best! See your profile to view it and other stats.";
                                   }
                                   if (checkHighscores(playerList.get(b).getGrandTotal(), playerList.get(b).getName()) == true) {
                                       additionalMessages += "\n" + playerList.get(b).getName() + ": New high score! Check the high scores to see where you placed.";
                                   }
                                   selectedPerson.writePersonByPerson();
                               }
                           }
                           lblPosition[position[0]].setStyle("-fx-font-weight: bold;");
                           lblPosition[position[0]].setTextFill(Paint.valueOf("purple"));
                           lblNames[position[0]].setTextFill(Paint.valueOf("purple"));
                           Player winner = playerList.get(position[0]);
                           finished.setTitle("Game over");
                           finished.setHeaderText(winner.getName() + " won!");
                           Text upperText = new Text("\nUpper total: " + winner.getUpperTotal());
                           upperText.setStyle("-fx-font-weight: lighter;");
                           Text lowerText = new Text("\nLower total: " + winner.getLowerTotal());
                           lowerText.setStyle("-fx-font-weight: lighter;");
                           Text totalText = new Text("\nGrand total: " + winner.getGrandTotal());
                           totalText.setStyle("-fx-font-weight: bold;");
                           TextFlow flow = new TextFlow(new Text(winner.getName() + " has won with a total " + winner.getGrandTotal() + " points. " + "\nUpper points: " + (winner.getUpperTotal() - winner.getUpperBonus()) + "\nUpper bonus: " + winner.getUpperBonus()),
                                   upperText,
                                   new Text("\nLower points: " + (winner.getLowerTotal() - winner.getYahtzeeBonus()) + "\nYahtzee bonus: " + winner.getYahtzeeBonus()),
                                   lowerText,
                                   totalText,
                                   new Text("\n" + additionalMessages)
                           );

                           finished.getDialogPane().setContent(flow);
                           if (winner.isReal()){
                               winSound.play(sfxVolume);
                           }
                           finished.showAndWait();
                           new Utils().clear();
                       } else if (selectedPlayer.isReal()){
                           nOfRolls = 0;
                           diceList.stream().forEach((diceList1) -> {
                               diceList1.setReRoll();
                           });
                           new Utils().rollDice();
                           lblNames[turn].setStyle("-fx-font-weight: bold;");
                           lblTurn.setText(playerList.get(turn).getName() + "'s turn");
                       }
                       
                       if (!selectedPlayer.isReal()) {
                           int randSelect;
                           do {
                               randSelect = (int) (Math.random() * 13);
                           } while (selectedPlayer.partUsed(randSelect));
                           new Utils().lblClicked(turn, randSelect);
                       }
                        
                  }
               }
           }
       }
        
       for (int i = 0; i < options.length; i++) {

           Label newLabel = new Label(options[i]);
           newLabel.setTextFill(Paint.valueOf("ROYALBLUE"));
           newLabel.setFont(labelFont);

           if (i == 6 || i == 7 || i > 14) {
               for (int g = 0; g < Player.nOfPlayers; g++) {
                   Label newLabel2 = new Label();
                   newLabel2.setMaxSize(labelWidth, labelHeight);
                   newLabel2.setMinSize(labelWidth, labelHeight);
                   newLabel2.setText("-");
                   newLabel2.setAlignment(Pos.CENTER);
                   newLabel2.relocate(g * 100 + 125, 23 * i + 100);
                   newLabel2.setFont(labelFont);
                   
                   switch (i) {
                       case 6:
                           newLabel2.setBorder(bonusBorder);
                           newLabel2.setTextFill(Paint.valueOf("green"));
                           lblUB[g] = newLabel2;
                           root.getChildren().add(lblUB[g]);
                           break;
                       case 7:
                           newLabel2.setBorder(totalsBorder);
                           newLabel2.setTextFill(Paint.valueOf("red"));
                           lblUT[g] = newLabel2;
                           root.getChildren().add(lblUT[g]);
                           break;
                       case 15: // yahtzee bonus
                           newLabel2.setBorder(bonusBorder);
                           newLabel2.setTextFill(Paint.valueOf("green"));
                           lblYB[g] = newLabel2;
                           root.getChildren().add(lblYB[g]);
                           break;
                       case 16:
                           newLabel2.setBorder(totalsBorder);
                           newLabel2.setTextFill(Paint.valueOf("red"));
                           lblLT[g] = newLabel2;
                           root.getChildren().add(lblLT[g]);
                           break;
                       case 17:
                           newLabel2.setBorder(totalsBorder);
                           newLabel2.setTextFill(Paint.valueOf("red"));
                           newLabel2.setStyle("-fx-font-weight: bold;");
                           lblGT[g] = newLabel2;
                           root.getChildren().add(lblGT[g]);
                           break;
                       case 18:
                           newLabel2.setFont(labelFont);
                           newLabel2.setText("");
                           lblPosition[g]=newLabel2;
                           root.getChildren().add(lblPosition[g]);
                           break;
                       default:
                           break;
                   }
               }

               switch (i) {
                   case 6: case 15:
                       newLabel.setTextFill(Paint.valueOf("green"));
                       break;
                   case 17:
                       newLabel.setStyle("-fx-font-weight: bold;");
                   case 7: case 16:
                       newLabel.setTextFill(Paint.valueOf("red"));
                       break;
                   default:
                       break;
               }
           }

           newLabel.relocate(10, 23 * i + 100);
           root.getChildren().add(newLabel);
       }
       
       btnBack.setOnAction(e ->{
           new Utils().clear();
           stage.close();
           mainStage.show();
        });

       btnRules.setOnAction(e->{
           rulesScreen();
       });
       
       List<String> personNamesList = new ArrayList<>();
        for (Person personList1 : personList) {
            personNamesList.add(personList1.getName());
        }
       
        // creating players
        for (int i=0;i<Player.nOfPlayers;i++){
            Player newPlayer;
            if (i < nOfPlayers) {
                nameChoice.setHeaderText("Player " + (i + 1));
                nameChoice.setContentText("Please select person of player " + (i + 1));
                personChoice.getSelectionModel().clearSelection();
                personChoice.getItems().clear();
                txtNewPerson.clear();

                if (personNamesList.isEmpty()) {
                    personChoice.setDisable(true);
                    nameChoice.getDialogPane().lookupButton(usePersonType).setDisable(true);
                } else {
                    personChoice.setDisable(false);
                    nameChoice.getDialogPane().lookupButton(usePersonType).setDisable(false);
                    personChoice.setItems(FXCollections.observableArrayList(personNamesList));
                    personChoice.getSelectionModel().select(personList.get(0).getName());
                }
                Optional<ButtonType> result = nameChoice.showAndWait();
                if (result.get() == usePersonType) {
                    newPlayer = new Player(true, personChoice.getValue().toString());
                    personNamesList.remove(newPlayer.getName());
                } else if (result.get() == createPersonType) {
                    Person newPerson = new Person(true, txtNewPerson.getText());
                    personList.add(newPerson);
                    newPlayer = new Player(true, newPerson.getName());
                } else {
                    newPlayer = new Player(true, "Guest");
                }
            } else {
                newPlayer = new Player(false, "Bot " + ((i - nOfPlayers) +1));
            }
            playerList.add(newPlayer);
            for (int g=0;g<13;g++){
                final int gFinal = g;
                Label newLabel = new Label();
                newLabel.setMaxSize(labelWidth, labelHeight);
                newLabel.setMinSize(labelWidth, labelHeight);
                newLabel.setBorder(catBorder);
                newLabel.setFont(catFont);
                if (g > 5) {
                    newLabel.relocate(i * 100 + 125, 23 * (g + 2) + 100);
                } else {
                    newLabel.relocate(i * 100 + 125, 23 *g + 100);
                }
                newLabel.setText("-");
                newLabel.setAlignment(Pos.CENTER);
                newLabel.setOnMouseClicked(e -> {
                    new Utils().lblClicked(turn,gFinal);
                });
                lblArray[i][g]=newLabel;
                root.getChildren().add(lblArray[i][g]);
            }
            Label nameLabel = new Label();
            nameLabel.setMaxSize(labelWidth, labelHeight);
            nameLabel.setMinSize(labelWidth, labelHeight);
            nameLabel.setAlignment(Pos.CENTER);
            nameLabel.setText(playerList.get(i).getName());
            nameLabel.setFont(labelFont);
            nameLabel.relocate(i*100 + 125, 75);
            lblNames[i]=nameLabel;
            root.getChildren().add(lblNames[i]);
        }
        
        btnRoll.setOnAction(e->{
            new Utils().rollDice();
        });
        
        btnStartGame.setOnAction(e -> {
            btnStartGame.setDisable(true);
            selectedPlayer=playerList.get(turn);
            lblNames[turn].setStyle("-fx-font-weight: bold;");
            lblTurn.setText(playerList.get(turn).getName() +"'s turn");
            for (int i=0;i<5;i++){
                Dice d = new Dice(scene.getWidth() - 90, 75+scene.getHeight()/15+i*(scene.getHeight()/8),i);
                diceList.add(d);
                root.getChildren().add(d.getImageView());
            }
            nOfRolls = 0;
            new Utils().rollDice();
        });
        
        btnBack.relocate(((scene.getWidth()/4) * 2) + (btnBack.getWidth()/2), 20);
        btnStartGame.relocate((scene.getWidth()/4 * 3) + (btnStartGame.getWidth()/2), 20);
        btnRoll.relocate(scene.getWidth()-80, scene.getHeight()-80);
        btnRules.relocate(scene.getWidth()/4 + (btnRules.getWidth()/2) , 20);
        lblTurn.relocate(scene.getWidth()/2-15, 50);
        disableSFX.relocate(30, 525);
        btnRoll.setDisable(true);
        
        root.getChildren().add(lblTurn);
        root.getChildren().add(btnRoll);
        root.getChildren().add(btnStartGame);
        root.getChildren().add(btnBack);
        root.getChildren().add(btnRules);
        root.getChildren().add(disableSFX);
        
        stage.setResizable(false);
        stage.setTitle("Yahtzee");
        stage.setScene(scene);
        stage.show();
    }

    public static void rulesScreen() {
        Stage stage = new Stage();
        stage.getIcons().add(new Image(Yahtzee1.class.getResourceAsStream("resources/yahtzeeIcon.png")));
        Pane root = new Pane();
        Scene scene = new Scene(root, 350, 630);
        Button btnBack = new Button();
        btnBack.setText("Back");
        
        String[] rulesList = new String[]{
        "The object of Yahtzee is to obtain the highest score from throwing 5 dice."
                + "\nThe game consists of 13 rounds. In each round, you roll the dice and then score the roll in one of 13 categories. You must score once in each category. The score is determined by a different rule for each category."
                + "\nThe game ends once all 13 categories have been scored."
        ,"To start with, roll all the dice. After rolling you can either score the current roll (see below), or re-roll any or all of the dice."
                + "\nYou may only roll the dice a total of 3 times. After rolling 3 times you must choose a category to score."
                + "\nYou may score the dice at any point in the round, i.e. it doesn't have to be after the 3rd roll."
        , "To score your combination of 5 dice, you click one of the 13 boxes from your category. There are two sections to the score table - the Upper Section and the Lower Section."
                + "\nOnce a box has been scored, it cannot be scored again for the rest of the game (except the Yahtzee category), so choose wisely."
        , "If you score in the upper section of the table, your score is the total of the specified dice faces."
                + "\nSo if you roll:"
                + "\n5 - 2 - 5 - 6 - 5 and score in the Fives category, your total for the category would be 15, because there are three fives, which are added together."
                + "\nIf the One, Three or Four Categories were selected for scoring with this roll, you would score a zero."
                + "\nIf placed in the Two or Six category, you would score 2 and 6 respectively."
                + "\nBonus:"
                + "\nIf the total of Upper scores is 63 or more, add a bonus of 35. Note that 63 is the total of three each of 1s, 2s, 3s, 4s, 5s and 6s."
        ,        
                "In the lower section, you score either a set amount, or zero if you don't satisfy the category requirements (with the exception of CHANCE)."
                + "\nFor more information on lower section scoring click any of the lower section categories below."
        , "For 3 of a kind you must have at least 3 of the same dice faces, while for 4 of a kind you would need 4 dice faces the same in order to score."
                + "\nYou score the total of all the dice."
        , "A Straight is a sequence of consecutive dice faces, where a small straight is 4 consecutive faces, and a large straight 5 consecutive faces."
                + "\nSmall straights score 30 and a large 40 points."
                + "\nSo if you rolled:"
                + "\n2 - 3 - 2 - 5 - 4"
                + "\nyou could score 30 in small straight or 0 in large straight."
        , "A Full House is where you have 3 of a kind and 2 of a kind. Full houses score 25 points."
                + "\nFor example:"
                + "\n3 - 3 - 2 - 3 - 2"
                + "\nwould score 25 in the Full House category."
        , "A Yahtzee is 5 of a kind and scores 50 points, although you may elect NOT to score it as a yahtzee, instead choosing to take it as a top row score and safegaurd your bonus."
                + "\nAdditional Yahtzees: If you roll more than one Yahtzee in a game, and you have already scored 50 points in the Yahtzee box, you score an additional 100 bonus points for each Yahtzee rolled. You must also put this roll into another category, as follows:"
                + "\n- If the corresponding Upper section category is not filled then you must score there (e.g. 4-4-4-4-4 -> Fours)."
                + "\n- If the corresponding Upper section category is filled you may then put the score in any upper or lower section category. In 3 of a Kind, 4 of a Kind, and Chance categories you would score the total of the dice faces. For the Small Straight, Large Straight, and Full House categories, you would score 30, 40 and 25 points respectively. If there are no empty categories left in the lower section, you must score 0 in an upper section category of your choice."
        , "You can roll anything and be able to put it in the Chance category. You score the total of the dice faces."
        , "You can score any roll in any category at any time, even if the resulting score is zero.\n"
                + "For example, you can use 1-3-3-4-6 in the twos category. It will score 0. This could be used near the end of a game to lose a poor roll in a difficult-to-get or low-scoring category that you haven't filled yet (e.g. aces or yahtzee)."  
        };
        
        String[] catsList = new String[]{"Object of the game", "Game Start","Scoring","Upper section","Lower section","3 and 4 of a kind","Small and Large Straight","Full House", "Yahtzees","Chance","Basic scoring strategy - using bad rolls"};
        Label lblTitle = new Label();
        lblTitle.setTextAlignment(TextAlignment.CENTER);
        lblTitle.setStyle("-fx-font-weight: bold;");
        Label lblRules = new Label();
        lblRules.setPrefWidth(300);
        lblRules.wrapTextProperty().setValue(true);
        
        ListView lv = new ListView();
        lv.setPrefSize(300,255);
        lv.getItems().addAll(Arrays.asList(catsList));
        lv.getSelectionModel().selectedItemProperty().addListener(e->{
            lblTitle.setText(catsList[lv.getSelectionModel().getSelectedIndex()]);
            lblRules.setText(rulesList[lv.getSelectionModel().getSelectedIndex()]);
        });
        btnBack.setOnAction(e->{
            stage.close();
        });
        
        stage.setOnCloseRequest(e->{
            stage.close();
        });
        
        btnBack.relocate(150, 10);
        lblTitle.relocate(25, 310);
        lblRules.relocate(25, 330);
        lv.relocate(25, 45);
        root.getChildren().add(btnBack);
        root.getChildren().add(lv);
        root.getChildren().add(lblTitle);
        root.getChildren().add(lblRules);
        
        stage.setResizable(false);
        stage.setTitle("Rules");
        stage.setScene(scene);
        stage.show();
    }

    public static int getCatByName(String catName) { // returns category ID based off name
        switch (catName) {
            case "Aces":
                return 0;
            case "Twos":
                return 1;
            case "Threes":
                return 2;
            case "Fours":
                return 3;
            case "Fives":
                return 4;
            case "Sixes":
                return 5;
            case "3-Of-A-Kind":
                return 6;
            case "4-Of-A-Kind":
                return 7;
            case "Full House":
                return 8;
            case "Small Straight":
                return 9;
            case "Large Straight":
                return 10;
            case "Chance":
                return 12;
            default:
                return 13;
        }
    }
    
    public static int[] getDiceArray() {
        int[] diceValues = new int[5];
        for (int i = 0; i < diceList.size(); i++) {
            diceValues[i] = diceList.get(i).getValue();
        }
        return diceValues;
    }
    
    public static boolean checkHighscores(int points, String pName){
        try{
            File highscoresFile = new File("yResources/highscores.txt");
            Scanner fileInput = new Scanner(highscoresFile);
            int i = 0;
            int nScores = 0;
            String[] person = new String[11];
            int[] personscore = new int[11];
            
            while (fileInput.hasNextLine()) {
                person[i] = fileInput.nextLine();
                personscore[i] = Integer.parseInt(fileInput.nextLine());
                i = i + 1;
                nScores = nScores + 1;
            }
            i = 0;
            fileInput.close();
            
            if (nScores<10){
                nScores = nScores+1;
            }
            
            boolean madeList = false;
            while (i<nScores && (madeList == false)){
                if (points>personscore[i]){
                    madeList = true;
                    for (int g = nScores;g>i; g--){
                        person[g] = person[g - 1];
                        personscore[g] = personscore[g-1];
                    }
                    person[i] = pName;
                    personscore[i] = points;

                }
                i = i+1;
            }
            PrintWriter writer = new PrintWriter(highscoresFile);
            for (int g = 0;g<nScores;g++){
                writer.println(person[g]);
                writer.println(personscore[g]);
            }
            writer.close();
            return madeList;
            
        }catch(FileNotFoundException fnfe){
            System.out.println(fnfe.getMessage());
            return false;
        }
    }
    
    public static String[] getHighscores(){
             String[] returnString;
             try (Scanner fileInput = new Scanner(new File("yResources/highscores.txt"))) {
                 int i = 0;
                 String[] person = new String[10];
                 int[] personscore = new int[10];
                 List<String> list = new ArrayList<String>();
                 while (fileInput.hasNextLine()){
                     person[i] = fileInput.nextLine();
                     personscore[i] = Integer.parseInt(fileInput.nextLine());
                     list.add((i+1) +". " +person[i] +": " +personscore[i]);
                     i = i+1;
                 }    returnString = new String[list.size()];
                 list.toArray(returnString);
            return returnString;
             } catch(FileNotFoundException fnfe){
            System.out.println(fnfe.getMessage());
            return null;
        }
    }
    
    public static boolean resetHighScores(){
        try{
        File highscoresFile = new File("yResources/highscores.txt");
        PrintWriter writer = new PrintWriter(highscoresFile);
        writer.close();
        return true;
        } catch(FileNotFoundException fnfe) {
            System.out.println(fnfe);
            return false;
        }
    }
    
    public static int getPersonIndexByName(String pName){
        String[] peopleList = Person.getPersonListString();
        int i=0;
        boolean found = false;
        while (found==false){
            if (peopleList[i].equals(pName)){
                found=true;
            } else {
                i=i+1;
            }
        }
        return i;
    }
        
    // 'remembering' people from file:
    public static List<Person> rememberPeople(){
        String[] personNameList = Person.getPersonListString();
        List<Person> listPerson = new ArrayList<Person>();
        for (int i =0;i<Person.getNOfPeople();i++){
            listPerson.add(Person.readPersonByName(personNameList[i])); 
        }
       return listPerson;
    }
    
    public static void fetchPrefs(){
        ssVolume = 0.5;
        sfxVolume = 0.5;
        try {
            File prefs = new File("YResources/prefs.txt");
            Scanner input = new Scanner(prefs);
            if (input.hasNext()){
            Dice.setHue(input.nextDouble());
            ssVolume = input.nextDouble();
            sfxVolume = input.nextDouble();
            } else {
                updatePrefs();
            }
            input.close();
        } catch (IOException ioe){
        System.out.println(ioe);
        }
    }
    
    public static void updatePrefs(){
        try {
            File prefs = new File("YResources/prefs.txt");
            PrintWriter writer = new PrintWriter(prefs);
            writer.println(Dice.getHue());
            writer.println(ssVolume);
            writer.println(sfxVolume);
            writer.close();
        } catch (IOException ioe){
            System.out.println(ioe);
        }
    }

    public static void toggleVolume(int id){ // 0 = start sound 1 = SFX
        switch (id){
            case 0:
                if (ssVolume!=0){
                    ssVolume=0;
                } else {
                    ssVolume=0.5;
                }
            break;
            case 1:
                if (sfxVolume!=0){
                    sfxVolume=0;
                } else {
                    sfxVolume=0.8;
                }
            break;
            default:
                sfxVolume=0;
                ssVolume=0;
        }
        updatePrefs();
    }
    
}