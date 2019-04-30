import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.utility.Delay;

public class Test {

	public static void main(String[] args) {
		LCD.drawString("Plugin Test", 0, 4);
//		Delay.msDelay(500);
		
		
		EV3ColorSensor sensorRight = new EV3ColorSensor(SensorPort.S1);
		EV3ColorSensor sensorLeft = new EV3ColorSensor(SensorPort.S2);
		SensorMode brightnessModeRight = sensorRight.getRedMode();
		SensorMode brightnessModeLeft = sensorLeft.getRedMode();
		
		float[] sampleRight = new float[brightnessModeRight.sampleSize()];
		float[] sampleLeft = new float[brightnessModeLeft.sampleSize()];
		
		EV3LargeRegulatedMotor motorRight = new EV3LargeRegulatedMotor(MotorPort.A);
		EV3LargeRegulatedMotor motorLeft = new EV3LargeRegulatedMotor(MotorPort.B);
		
		while (true) {
			brightnessModeRight.fetchSample(sampleRight, 0);
			brightnessModeLeft.fetchSample(sampleLeft, 0);
			LCD.refresh();
			LCD.clear();
			
			LCD.drawString("sample1[0]: " + sampleRight[0], 1, 1); // weiss: 0.8, schwarz: 0.02
			LCD.drawString("sample2[0]: " + sampleLeft[0], 1, 2);
			
			
			if (sampleRight[0] > 0.5 && sampleLeft[0] > 0.5) {
				LCD.drawString("gerade", 1, 3);
				motorRight.forward();
				motorLeft.forward();
			} else if (sampleLeft[0] < 0.2) {
				LCD.drawString("links", 1, 3);
				motorRight.forward();
				motorLeft.stop();
			} else if (sampleRight[0] < 0.2) {
				LCD.drawString("rechts", 1, 3);
				motorLeft.forward();
				motorRight.stop();
			}
			
			// mittlere und runter taste zum beenden
		}
		
	}

}
