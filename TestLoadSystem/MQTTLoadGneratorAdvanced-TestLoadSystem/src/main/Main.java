package main;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;

import javafx.scene.transform.Scale;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {
	public static String pcNum; // 해당 pc 번호
	private static String broker = "tcp://113.198.85.241:1883"; // test load broker
	
	/*public static GpioController gpio = null;
	public static GpioPinDigitalOutput led_pin;
*/
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		/*InputStream is = Main.class.getResourceAsStream("pcNum.txt"); // 해당 pc번호가 저장되있는 파일

		InputStreamReader fis = new InputStreamReader(is);
		BufferedReader bufReader = new BufferedReader(fis);

		try {
			pcNum = bufReader.readLine();
			System.out.println("My PC Number: " + pcNum); // 저장
			bufReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		int num = scanner.nextInt();
		pcNum = String.valueOf(num);
		/*
		gpio = GpioFactory.getInstance();
		led_pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08);
		
		led_pin.low();*/

		new Connect(broker, pcNum);
	}

}
