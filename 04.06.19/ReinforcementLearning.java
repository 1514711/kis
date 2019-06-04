    
import java.util.Random;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.utility.Delay;
// Prüfungsanmeldung, in Liste eintragen!
// PDF als Email, mit Name und Matrikelnummer:  4 - 8 Seiten schreiben (ieee paper format) zu wie das Projekt gelaufen ist, mit abstract einführung, related work, eigene Umsetztung, auswertung/ kritische diskussion, future work/ diskussion, (versuche, konzepte, diskussionsausblick.. (Aufbau wie eine Bachelorarbeit) == Merkzettel KIS für Abschlussarbeiten
// Bsp: Releated Works, wann hat der Algorithmus ausgelernt, abstract, anleitung, eigenene Implementierung, discussion, zukünftige Arbeiten.
public class ReinforcementLearning {
/*
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
*/	
	// Samples
	float[] sampleRight = new float[1];
	float[] sampleLeft = new float[1];
		
	// value[state][action]
	float[][] value;

	// Zustände
	int pastState;
	int currentState;
					
	// Aktionen
	int pastAction;
	int futureAction;
						
	// Variablen
	float reward;
	float learnrate;
	float discount;			
	
	
	public ReinforcementLearning() {
		value = new float[4][4];

		pastState = 3;
		currentState = 3;
		
		pastAction = 3;
		futureAction = 3;
		
		reward = 1;
		learnrate = (float) 0.01;
		discount = (float) 0.9;	
	}
	
	
	
	public int train(boolean sampleLeftBoolean, boolean sampleRightBoolean, float reward) {		

		float futureValue = 0;
		float qAlt = 0;		 
		
		
		if(sampleLeftBoolean) {
			sampleLeft[0] = 0;
		} else {
			sampleLeft[0] = 1;
		}
			
		if(sampleRightBoolean) {
			sampleRight[0] = 0;
		} else {
			sampleRight[0] = 1;
		}			
			
		// In neuen Zustand wechseln
		pastState = currentState;
		currentState = Math.round(sampleLeft[0]) * 2 + Math.round(sampleRight[0]);	

		// Reward berechnen
		//reward = currentState/2 - pastState/2 + currentState % 2 - pastState % 2;
		
		// Q-Update
		qAlt = value[pastState][pastAction];
		value[pastState][pastAction] = value[pastState][pastAction] + learnrate*(reward + discount*value[currentState][futureAction] - value[pastState][pastAction]);	
						
		// Neue Aktion aussuchen
		pastAction = futureAction;
		futureValue = value[currentState][1];
		
		for(int i = 1; i < 4; i++) {
			if(value[currentState][i] >= futureValue) {
				futureValue = value[currentState][i];
				futureAction = i;
			}
		}
							
		System.out.println("State Past = " + pastState + "  -->  State Current = " + currentState);
		System.out.println("Action Past= " + pastAction + "  -->  Action Future = " + futureAction);
		System.out.println("Reward = " + reward);
		System.out.println("Q-Past [" + pastState + "][" + pastAction + "]= " + value[pastState][pastAction] + "  -->  deltaQ = " + (value[pastState][pastAction] - qAlt));
		System.out.println("Q - Current [" + currentState + "][" + futureAction + "]= " + value[currentState][futureAction]);
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				System.out.print("Value[" + i + "][" + j + "] = " + value[i][j] + "  ,  ");
			}
			System.out.println();
		}
		System.out.println("---------------------------------------------------------------------------------------");
		
		/*
		if(random)
			return new Random().nextInt(4);
		else
			return futureAction;
		 */
		
		return futureAction;
	}	
	
	
	public void run() {
		
		// Variablen
		float futureValue = 0;
		float qAlt = 0;
				
		while (true) {
/*					
			// Sensoren auslesen (Aus Simulation per Parameter)
			brightnessModeRight.fetchSample(sampleRight, 0);
			brightnessModeLeft.fetchSample(sampleLeft, 0);
			LCD.refresh();
			LCD.clear();
			
			LCD.drawString("sample1[0]: " + sampleRight[0], 1, 1); // weiss: 0.8, schwarz: 0.02
			LCD.drawString("sample2[0]: " + sampleLeft[0], 1, 2);
*/
			// In neuen Zustand wechseln
			pastState = currentState;
			currentState = Math.round(sampleLeft[0]) * 2 + Math.round(sampleRight[0]);
					
			// Reward berechnen
			//reward = currentState/2 - pastState/2 + currentState % 2 - pastState % 2;
					
			// Q-Update
			//qAlt = value[pastState][pastAction];
			//value[pastState][pastAction] = value[pastState][pastAction] + learnrate*(reward + discount*value[currentState][futureAction] - value[pastState][pastAction]);	
					
			// Neue Aktion aussuchen
			pastAction = futureAction;
			futureValue = value[currentState][1];
			
			for(int i = 1; i < 4; i++) {
				if(value[currentState][i] >= futureValue) {
					futureValue = value[currentState][i];
					futureAction = i;
				}
			}
/*
			// Aktion ausführen (An Simulation weitergeben)
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
*/
		}	
	}
}