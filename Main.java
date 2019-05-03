    
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.utility.Delay;

public class Main {

	public static void main(String[] args) {		
		
		// Sensoren
		EV3ColorSensor sensorRight = new EV3ColorSensor(SensorPort.S1);
		EV3ColorSensor sensorLeft = new EV3ColorSensor(SensorPort.S2);
		SensorMode brightnessModeRight = sensorRight.getRedMode();
		SensorMode brightnessModeLeft = sensorLeft.getRedMode();
		
		// Samples
		float[] sampleRight = new float[brightnessModeRight.sampleSize()];
		float[] sampleLeft = new float[brightnessModeLeft.sampleSize()];
		
		// Motoren
		EV3LargeRegulatedMotor motorRight = new EV3LargeRegulatedMotor(MotorPort.A);
		EV3LargeRegulatedMotor motorLeft = new EV3LargeRegulatedMotor(MotorPort.B);
		
		// value[state][action]
		float[][] value = new float[4][4];
		
		// Zustände
		int pastState = 0;
		int currentState = 0;
		
		// Aktionen
		int pastAction = 0;
		int futureAction = 0;
		
		// Variablen
		float reward = 0;
		float learnrate = (float) 0.01;
		float discount = (float) 0.3;			
		float futureValue = 0;
		float qAlt = 0;
		
		while (true) {
			
			// Sensoren auslesen
			brightnessModeRight.fetchSample(sampleRight, 0);
			brightnessModeLeft.fetchSample(sampleLeft, 0);
			LCD.refresh();
			LCD.clear();
			
			LCD.drawString("sample1[0]: " + sampleRight[0], 1, 1); // weiss: 0.8, schwarz: 0.02
			LCD.drawString("sample2[0]: " + sampleLeft[0], 1, 2);
			
			// In neuen Zustand wechseln
			pastState = currentState;
			currentState = Math.round(sampleLeft[0]) * 2 + Math.round(sampleRight[0]);
			
			// Reward berechnen
			reward = currentState/2 - pastState/2 + currentState % 2 - pastState % 2;
			
			// Q-Update
			qAlt = value[pastState][pastAction];
			value[pastState][pastAction] = value[pastState][pastAction] + learnrate*(reward + discount*value[currentState][futureAction] - value[pastState][pastAction]);	
			
			// Neue Aktion aussuchen
			pastAction = futureAction;
						
			for(int i = 0; i < 4; i++) {
				if(value[currentState][i] > futureValue) {
					futureValue = value[currentState][i];
					futureAction = i;
				}
			}
						
			// Aktion ausführen
			if(futureAction == 3) {
				//LCD.drawString("gerade", 1, 3);
				motorRight.forward();
				motorLeft.forward();
			} else if(futureAction == 2) {
				//LCD.drawString("rechts", 1, 3);
				motorLeft.forward();
				motorRight.stop();
			} else if(futureAction == 1){
				//LCD.drawString("links", 1, 3);
				motorRight.forward();
				motorLeft.stop();
			} else {
				//LCD.drawString("stop", 1, 3);
				motorRight.stop();
				motorLeft.stop();
			}
			
			LCD.drawString("State = " + pastState + "  -->  State + 1 = " + currentState, 1, 3);
			LCD.drawString("Action = " + pastAction + "  -->  Action + 1 = " + futureAction, 1 ,3);
			LCD.drawString("Reward = " + reward, 1, 3);
			LCD.drawString("Q = " + value[pastState][pastAction] + "  -->  Q + 1 = " + value[currentState][futureAction] + "  -->  deltaQ = " + (value[pastState][pastAction] - qAlt), 1 , 3);
		}	
	}

}