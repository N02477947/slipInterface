import java.io.IOException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import com.leapmotion.leap.*;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import java.net.ServerSocket;
import java.net.Socket;

class GlobalVars{		
	public static char LastChar = '!';
	public static String FinalString = "";
	public static char[] charArray;
}
class FreeTTS {
	 private static final String VOICENAME_kevin = "kevin";
	 private String text; // string to speech
	 
	 public FreeTTS(String text) {
	  this.text = text;
	 }
	 
	 public void speak() {
	  Voice voice;
	  VoiceManager voiceManager = VoiceManager.getInstance();
	  voice = voiceManager.getVoice(VOICENAME_kevin);
	  voice.allocate();
	  voice.speak(text);
	 }
}
	 
class SampleListener extends Listener {

	public void onInit(Controller controller) {
		System.out.println("Initialized");
	}
	public void onConnect(Controller controller) {
		System.out.println("Connected");
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
	}
	public void onDisconnect(Controller controller) {
		System.out.println("Disconnected");
	}
	public void onExit(Controller controller) {
		GlobalVars.FinalString = GlobalVars.FinalString.replaceAll("\\s","");
		System.out.println("Final String = " + GlobalVars.FinalString);
		System.out.println("Final String Bytes = " + GlobalVars.FinalString.getBytes());
//		int PORT = 1060;
//		byte[] buf = new byte[1024];
//		try{
//			Socket AppClient = new Socket("localhost", PORT);
//			buf = GlobalVars.FinalString.getBytes();
//			BufferedReader input = new BufferedReader(new InputStreamReader(AppClient.getInputStream()));
//			DataOutputStream output = new DataOutputStream(AppClient.getOutputStream());
//			if (AppClient != null && output != null) {
//				output.write(buf); 
//				System.out.println(input.readLine());
//				output.close();
//				input.close();
//				AppClient.close();   
//			}
//		}
//		catch(IOException e){
//			System.out.println(e);
//		}
		//FreeTTS freeTTS = new FreeTTS(GlobalVars.FinalString);
		//freeTTS.speak();
		System.out.println("Exited");
	}
		
	public void onLetter(char letter){
		if(GlobalVars.LastChar != letter)
		{
			if(letter == ' '){
				System.out.println("Space");
				GlobalVars.LastChar = letter;
				GlobalVars.FinalString += letter;
			}else{
				System.out.println(letter);
				GlobalVars.LastChar = letter;
				GlobalVars.FinalString += letter;
			}
		}
	}
	
	public void onFrame(Controller controller) {
		float grip = 0;
		float Rad = 0;
		float pinch = 0;
		float pitch = 0; 
		float yaw = 0;
		float roll = 0; 
		float confidence = 0;

		Frame frame = controller.frame();

		if (!frame.hands().isEmpty()) {
			Hand hand = frame.hands().get(0);
			grip = hand.grabStrength();
			pinch = hand.pinchStrength();
			confidence = hand.confidence();
			Rad = hand.sphereRadius();
			pitch = hand.direction().pitch();
			yaw = hand.direction().yaw();
			roll = hand.palmNormal().roll();

			System.out.println("Confidence: " + confidence);
			System.out.println("Grip Strength: " + grip);
			System.out.println("Sphere Radius: " + Rad);
			System.out.println("Pinch Strength: " + pinch);
			System.out.println("pitch: " + pitch);
			System.out.println("yaw: " + yaw);
			System.out.println("roll: " + roll + "\n");

			FingerList fingers = hand.fingers();
			if (!fingers.isEmpty()) {

			for (Finger finger : fingers) {	
				if(finger.isExtended()){
						System.out.println(finger.type().toString() + "\n");
				}
			}
			
			float distance = (fingers.rightmost().stabilizedTipPosition().getX() - fingers.leftmost().stabilizedTipPosition().getX());
			System.out.println("Distance: " + distance);
			System.out.println("# of fingers extended: " + fingers.extended().count());

				//""
				if(fingers.extended().count() == 5)
				{
					onLetter(' ');
				}		
				//A
				if(fingers.extended().count() == 1 && fingers.extended().get(0).type().toString().equals("TYPE_THUMB") && grip >= .9 && roll >-.25 && distance > 70)
				{
					onLetter('A');
				}
				//B
				if(fingers.extended().count() == 4 && grip <=.50)
				{
					onLetter('B');
				}
				//C
				if(fingers.extended().count() == 0 && Rad >=50 && Rad <=60 && roll <=-.75)
				{
					onLetter('C');
				}
				//D
				if(fingers.extended().count() == 1 && fingers.extended().get(0).type().toString().equals("TYPE_INDEX")/* && pinch >= .9*/)
				{
					onLetter('D');
				}
				//E
				if(fingers.extended().count()==0 && grip > .25 && Rad > 30 && pinch >.8  && roll > -.50 && pitch > 0)
				{	
					onLetter('E');
				}
				//F
				if(fingers.extended().count() == 3 && fingers.extended().get(0).type().toString().equals("TYPE_MIDDLE") && 
						fingers.extended().get(1).type().toString().equals("TYPE_RING") && fingers.extended().get(2).type().toString().equals("TYPE_PINKY"))
				{
					onLetter('F');
				}
				//G
				if((fingers.extended().count() == 1 && (fingers.extended().get(0).type().toString().equals("TYPE_THUMB") || fingers.extended().get(0).type().toString().equals("TYPE_INDEX")) || 
						(fingers.extended().count() == 2 && fingers.extended().get(0).type().toString().equals("TYPE_THUMB") && fingers.extended().get(1).type().toString().equals("TYPE_INDEX"))) && 
						roll < -.60 && yaw < .75 && pitch < .3)
				{
					onLetter('G');
				}
				//H
				//I
				if(fingers.extended().count() == 1 && fingers.extended().get(0).type().toString().equals("TYPE_PINKY") && grip < .5)
				{
					onLetter('I');
				}
				//J
				//K
				if(fingers.extended().count() == 2 && fingers.extended().get(0).type().toString().equals("TYPE_INDEX") && 
						fingers.extended().get(1).type().toString().equals("TYPE_MIDDLE") && pinch >0 && pinch < .5 && distance > 48)
				{
					onLetter('K');
				}
				//L
				if(fingers.extended().count() == 2 && fingers.extended().get(0).type().toString().equals("TYPE_THUMB") && 
						fingers.extended().get(1).type().toString().equals("TYPE_INDEX"))
				{
					onLetter('L');
				}
				//M
				if(fingers.extended().count() == 3  && fingers.get(0).type().toString().equals("TYPE_INDEX") && fingers.get(1).type().toString().equals("TYPE_MIDDLE") && fingers.get(2).type().toString().equals("TYPE_RING") && 
						distance < 50)
				{
					onLetter('M');
				}
				//N
				if(fingers.extended().count() == 3  && fingers.get(0).type().toString().equals("TYPE_THUMB") && fingers.get(1).type().toString().equals("TYPE_INDEX") && fingers.get(2).type().toString().equals("TYPE_MIDDLE") )
				{
					onLetter('N');
				}
				//O
				if(fingers.extended().count()==0 && grip > .25 && Rad > 30 && pinch >.8  && roll < -.50)
				{
					onLetter('O');
				}
				//P
				if(fingers.extended().count() == 1 && fingers.extended().get(0).type().toString().equals("TYPE_INDEX") && pitch < .1)
				{
					onLetter('P');
				}
				//Q
				if(fingers.extended().count() == 0 && pitch < .1 && pinch >= .7)
				{
					onLetter('Q');
				}
				//R
				if(fingers.extended().count() == 2 && fingers.extended().get(0).type().toString().equals("TYPE_INDEX") && fingers.extended().get(1).type().toString().equals("TYPE_MIDDLE") &&  distance < 48)
				{
					onLetter('R');
				}
				//S
				//T
				if(grip >= .8 && (fingers.extended().count() == 0 || (fingers.extended().count() == 1 && fingers.extended().get(0).type().toString().equals("TYPE_THUMB"))) && roll < -.75 && pinch < .8 )
				{
					onLetter('T');
				}
				//U
				if(fingers.extended().count() == 2 && fingers.extended().get(0).type().toString().equals("TYPE_INDEX") && fingers.extended().get(1).type().toString().equals("TYPE_PINKY") && distance >70)
				{
					onLetter('U');
				}
				//V
				if(fingers.extended().count() == 2 && fingers.extended().get(0).type().toString().equals("TYPE_INDEX") && fingers.extended().get(1).type().toString().equals("TYPE_MIDDLE") && pinch >.5 && distance > 50)
				{
					onLetter('V');
				}
				//W
				if(fingers.extended().count() == 3  && fingers.get(0).type().toString().equals("TYPE_INDEX") && fingers.get(1).type().toString().equals("TYPE_MIDDLE") && fingers.get(2).type().toString().equals("TYPE_RING") && 
				distance > 55.0)
				{
					onLetter('W');
				}
				//X
				//Y
				if(fingers.extended().count() == 2 && fingers.extended().get(0).type().toString().equals("TYPE_THUMB") &&  fingers.extended().get(1).type().toString().equals("TYPE_PINKY") && distance > 100)
				{
					onLetter('Y');
				}
				//Z
				if(fingers.extended().count() == 3 && fingers.extended().get(0).type().toString().equals("TYPE_THUMB") &&  fingers.extended().get(1).type().toString().equals("TYPE_INDEX") && 
						fingers.extended().get(2).type().toString().equals("TYPE_PINKY") && distance > 100)
				{
					onLetter('Z');
				}
				/*PointableList points = hand.pointables();
				if (!points.isEmpty()) {
					// Calculate the pointables dimensions
					for (Pointable poin : points) {
					}*/
			}
		}

		GestureList gestures = frame.gestures();
		for (int i = 0; i < gestures.count(); i++) {
			Gesture gesture = gestures.get(i);
			switch (gesture.type()) {
			case TYPE_CIRCLE:
				break;
			case TYPE_SWIPE:
				break;
			case TYPE_SCREEN_TAP:
				break;
			case TYPE_KEY_TAP:               
				break;
			default:
				System.out.println("Unknown gesture type.");
				break;
			}
		}
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class Sample {
	public static void main(String[] args) {
		SampleListener listener = new SampleListener();
		Controller controller = new Controller();
		controller.addListener(listener);
		System.out.println("Press Enter to quit...");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		controller.removeListener(listener);
	}
}
