package application;
	
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.io.File;
import java.io.IOException;

import javax.sound.midi.*;


public class Main extends Application {
	File selectedFile;
    FileChooser fileChooser;
    Media media;
    MediaPlayer mp;
    Stage copy;
    MediaView mediaView = new MediaView();
    
    Button startButton = new Button("Start");
    Button stopButton = new Button("Stop");
    Button pauseButton = new Button("Pause");
    Button openButton = new Button("Open File");
    Button testMidiButton = new Button("Test Midi");
    
    Label currentPos = new Label();
    
    boolean midiLoaded = false;
    
    @Override
    public void start(Stage stage) throws Exception{
       
       copy = stage;
       File desktop = new File(System.getProperty("user.home") + "/Desktop");
       desktop.toURI();
       
       fileChooser = new FileChooser();
       fileChooser.setTitle("Hi");
       fileChooser.setInitialDirectory(desktop);
       fileChooser.getExtensionFilters().addAll(
           new ExtensionFilter("Video Files", "*.mp4", "*.mkv"),
           new ExtensionFilter("Midi Files", "*.mid", "*.midi"),
       	   new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"));
       if(selectedFile != null) {
    	   media = new Media(selectedFile.toURI().toString());
           mp = new MediaPlayer(media);
           mediaView = new MediaView(mp);
       }
       

       GridPane pane = new GridPane();
       pane.setPadding(new Insets(10, 10, 10, 10));
       pane.setMinSize(1024, 768);
       pane.setVgap(10);
       pane.setHgap(10);

       startButton.setOnAction(this::play);
       stopButton.setOnAction(this::stop);
       pauseButton.setOnAction(this::pause);
       openButton.setOnAction(this::open);
       testMidiButton.setOnAction(this::midiTest);
       
       currentPos.setText("Midi Not Loaded");
       
       pane.add(openButton, 0, 0);
       pane.add(startButton, 2, 0);
       pane.add(stopButton, 3, 0);
       pane.add(pauseButton, 4, 0);
       pane.add(mediaView, 1, 0);
       pane.add(testMidiButton, 5, 0);
       pane.add(currentPos, 6, 0);
       
       

       Scene scene = new Scene(pane, 720, 720);
       stage.setTitle("JavaFX Example");
       stage.setScene(scene);

       stage.show();
    }

    private void play(ActionEvent event){
    	mp.play();
    }
    
    private void stop(ActionEvent event){
    	mp.stop();
    }
    
    private void pause(ActionEvent event) {
    	mp.pause();
    }
    
    private void open(ActionEvent event) {
    	selectedFile = fileChooser.showOpenDialog(copy);
    	System.out.println(selectedFile.getName().toString().substring(selectedFile.getName().toString().length()-4, selectedFile.getName().toString().length()));
    	if(!(selectedFile.getName().toString().substring(selectedFile.getName().toString().length()-4, selectedFile.getName().toString().length()).equals(".mid"))) {
	        media = new Media(selectedFile.toURI().toString());
	        mp = new MediaPlayer(media);
	        
    	} else if(selectedFile.getName().toString().substring(selectedFile.getName().toString().length()-4, selectedFile.getName().toString().length()).equals(".mid")) {
    		currentPos.setText("Midi File Loaded");
    		midiLoaded = true;
    		testMidiButton.fire();
    	}
    }
    
    private void midiTest(ActionEvent event) {
    	if(midiLoaded) {
	    	try {
	     	   	Synthesizer midiSynth = MidiSystem.getSynthesizer();
	     	   	Sequence midiFile = MidiSystem.getSequence(selectedFile);
	     	   	Sequencer seq = MidiSystem.getSequencer();
	     	   	Transmitter seqTrans;
	     	   	MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
	     	   	MidiDevice device = MidiSystem.getMidiDevice(infos[4]);
	     	   	Receiver deviceRcvr;
	            midiSynth.open();
	            
	            try {
	            	seqTrans = seq.getTransmitter();
	            	deviceRcvr = device.getReceiver();
	            	seqTrans.setReceiver(deviceRcvr);
	            } catch(MidiUnavailableException e) {
	            	
	            }
	            
	            seq.setSequence(midiFile);
	            seq.open();
	            seq.start();
	            //while(seq.isRunning()) currentPos.setText(Long.toString(seq.getTickPosition()));
	            if(!seq.isRunning()) {
	            	seq.close();
	            }
	            
	            
	            
	            
	            //currentPos.setText(Long.toString(seq.getTickPosition()));
	            
	            Instrument[] instr = midiSynth.getDefaultSoundbank().getInstruments();
	            for(int i = 0; i < instr.length-1; i++) {
	            	System.out.println(instr[i]);
	            }
	            System.out.println("");
	            midiSynth.loadInstrument(instr[5]);
	            
	            if (!(device.isOpen())) {
	                try {
	                	device.open();
	                	System.out.println("work");
	              } catch (MidiUnavailableException e) {
	                    System.out.println("nope");
	              }
	            }
	            
	            ShortMessage myMsg = new ShortMessage();
	            try {
					myMsg.setMessage(ShortMessage.NOTE_ON, 0, 60, 93);
				} catch (InvalidMidiDataException e1) {
					e1.printStackTrace();
				}
	            long timeStamp = 10;
	            Receiver rcvr = device.getReceiver();
	            
	            //rcvr.send(myMsg, timeStamp);
	        } catch(MidiUnavailableException | InvalidMidiDataException | IOException e) {
	     	   
	        }
    	}
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
