/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yahtzee1;

import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 *
 * @author Chris
 */
public class Dice {
    final int diceID;
    boolean reRoll = true;
    int value;
    ImageView dView;
    static double hue=0;
    private static Effect selectedEffect = new ColorAdjust(0, -0.5, 0, 0);
    private static Effect unselectedEffect = new ColorAdjust(0, 0, 0, 0);
    
    public Dice(double x,double y, int id) {
        diceID = id;
        dView = new ImageView();
        dView.setLayoutX(x);
        dView.setLayoutY(y);
        dView.setOnMouseClicked(e -> {
            reRoll = !reRoll;
            if (reRoll) {
                dView.setEffect(selectedEffect);
            } else {
                dView.setEffect(unselectedEffect);
            }
        });
    }
        
    public boolean isReRoll(){
        return reRoll;
    }
    
    public void setReRoll(){
        reRoll=true;
    }
    
  
    
    public void roll() {
        value = (int) ((Math.random() * 6) + 1);
        TranslateTransition tt = new TranslateTransition(Duration.seconds(1));
        tt.setInterpolator(Interpolator.EASE_OUT);
        tt.setFromX(-100);
        tt.setFromY(400 - diceID * 68);
        tt.setToX(0);
        tt.setToY(0);
        RotateTransition rt = new RotateTransition(Duration.seconds(1));
        if (Math.random() * 2 > 1) {
            rt.setByAngle(720);
        } else {
            rt.setByAngle(-720);
        }
        rt.setInterpolator(Interpolator.EASE_OUT);
        ParallelTransition prt = new ParallelTransition(dView, tt, rt);
        prt.play();
        dView.setImage(new Image(getClass().getResourceAsStream("resources/dice"+value + ".png")));
        dView.setEffect(unselectedEffect);
        reRoll = false;
    }
        
    public int getValue(){
        return value;
    }
    
    public ImageView getImageView() {
            return dView;
    }
    
    public static void setHue(double h){
        hue=h;
    }
    
    public static double getHue(){
        return hue;
    }
    
}
