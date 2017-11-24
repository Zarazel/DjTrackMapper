/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package djtrackmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * This is the "core" of DjTrackMap. 
 * An object of is constructed by the Gui.main method.
 * See Documentation for further details.
 * 
 * (TODO:It creates a List of Tracks created by reading the id3Tags of .mp3 files
 * of the folder, from within DjTrackMap is launched from, and it's subfolders,
 * with the import() method.)
 * 
 * An item of the trackList is called track and contains fields like author, 
 * title etc.
 * The concatenation of the fields author and title is called id, given a track
 * can be distinctly identified by just these two fields:
 * two tracks with the same author and title are the same.
 * The id is used to lexicographically sort the trackList, for better performance
 * of further processing.
 * 
 * Each track contains it's following (next) Tracks (specified by the user) in
 * a List of links, where each link contains the destination a track, 
 * and further qualities, like bpm difference, comments etc.
 * The Track also saves previous Tracks in form of a List of Tracks, as no
 * qualities for these connections need to be considered.(redundancy not needed)
 * 
 * Further djTrackMap handles the (de)serialization (saving and restoring to disk)
 * of the created List, with all it's content e.g. created Links.
 * 
 * TODO:
 * 
 * previousTrack != nextTrack (no self referencing!)
 * 
 * findTrack
 * 
 * MAY CONTAIN BUGS:
 * Check for redundant Tracks. -> List<Integer> findRedundantTracks()
 * 
 * Exceptions, track not found etc.
 * 
 * DONE:
 * previousTracks.
 * Serialization.
 * 
 * @author zarazel
 */

public class DjTrackMap {

    
    List<Track> trackList;
    //String alphabet = "abcdefghijklmnopqrstuvwxyzäöüABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ1234567890 ,;()";
    String alphabet = "abcdefg";

    public DjTrackMap(){
        this.trackList = new ArrayList<>();	
    }
    
    public DjTrackMap(boolean commandLineTest){
        
        this.trackList = new ArrayList<>();
        
	if(commandLineTest){
            
            runTestVerbose(100000,1);
	}
    }
    
    public final void runTest(int trackNumber, int linkNumber){
            
        double startTime = System.nanoTime();
        
        System.out.println("Starting testrun!");
        testRandomizedImport(trackNumber,linkNumber);
        printTrackList();
        
        System.out.println("Sorting it:");
        Collections.sort(trackList);
        printTrackList();
        
        System.out.println("Now find redundant Tracks.");
        findRedundantTracks().forEach((i) -> {
            System.out.print( i +" ");
        });
        System.out.print("are redundant.");
                   
        System.out.println("Now save it to drive, and clear memory.");
        save(trackList);
        trackList.clear();
        printTrackList();
        
        System.out.println("Ok, now finally load it from drive.");
        trackList = load();
        printTrackList();        
        System.out.println("Is the List still there?");
        
        double finishTime = System.nanoTime();
        System.out.println("Test completed after: "+ (finishTime-startTime)/10e9);
        
    }
    
    public final void runTestVerbose(int trackNumber, int linkNumber){
            
        double startTime = System.nanoTime();
        testRandomizedImport(trackNumber,linkNumber);
        Collections.sort(trackList);
        findRedundantTracks().forEach((i) -> {
            System.out.print( i +" ");
        });
        save(trackList);
        trackList.clear();
        trackList = load();
        double finishTime = System.nanoTime();
        System.out.println("Test completed after: "+ (finishTime-startTime)/10e9);
        
    }
    
    public final void testRandomizedImport(int listLength, int linkCount) {
	/*
	* Put Tracks with randomized Values in trackList.
	*/
        for (int i = 0; i<listLength; i++){
            float randomBpm = (float) ThreadLocalRandom.current().nextDouble(60, 240);
            trackList.add(new Track(randomString(10), randomString(20), randomString(10),randomBpm, randomString(2), randomString(140)));
        }
        
        for (int j = 0; j<linkCount; j++){
            int from = ThreadLocalRandom.current().nextInt(0, trackList.size()-1);
            int to = ThreadLocalRandom.current().nextInt(0, trackList.size()-1);
            trackList.get(from).addNextLink(trackList.get(to));
        }
        
    }
    
    public final void testManualImport() {
	/*
	* Manually put Tracks in the List for testing.
	*/
	
	trackList.add(new Track("Poo 2","The pee has no name", 120 ));
	trackList.add(new Track("Dj Popo","It's my Scheiß",123));
	trackList.add(new Track("Klozart","Eine kleine Kackmusik",134));
	trackList.add(new Track("The Shitles","Brown Submarine",110));
	trackList.add(new Track("Dj PoPo","There is a Farty",123));
	trackList.add(new Track("The Dödels","1",112));
	trackList.add(new Track("the DÖdels","2",78));
	trackList.add(new Track("the DÖdels","2",117));
	
	trackList.get(0).addNextLink(trackList.get(1));
	trackList.get(5).addNextLink(trackList.get(3));
	trackList.get(2).addNextLink(trackList.get(1));
	trackList.get(1).addNextLink(trackList.get(2));
	trackList.get(7).addNextLink(trackList.get(3));
	trackList.get(2).addNextLink(trackList.get(4));
	trackList.get(3).addNextLink(trackList.get(6));
    }
    
    public final static void save(List<Track> tracks){
        
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("trackList.bin"))){
            
            out.writeObject(tracks);
            System.out.println("TrackList successfully serialized.");
            
        } catch (Exception e){
            
            System.out.println("Unable to serialize trackList.");
            
        }
    }
    
    public final static ArrayList<Track> load(){
        
        ArrayList<Track> tracks = new ArrayList<>();
        
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("trackList.bin"))){
            
            tracks = (ArrayList<Track>) in.readObject();
            
        } catch (Exception e) {
            
            System.out.println("Unable to read trackList.bin.");
            
        }
        
        return tracks;
        
    }
    
    /*
    MAY CONTAIN A BUG!
    
    Returns List<Integer>of indexes of redundant Tracks in trackList, for later 
    use e.g. remove them from trackList.
    */
    public List<Integer> findRedundantTracks(){
	
	Collections.sort(trackList);
	
	List<Integer> redundantTracks = new ArrayList<>(0);
	
	for (int i = 0; i<trackList.size()-1; i++){
	    
	    if(trackList.get(i).equals(trackList.get(i+1))){
		
		if(redundantTracks.get(redundantTracks.size())!=i){
		    redundantTracks.add(i);
		}
		redundantTracks.add(i+1);
	    }
	}
	
	return redundantTracks;
	
    }
    
    public void printTrackList(){
	
	trackList.forEach((Track track) -> {
	    
	    System.out.println("\nNext Tracks of "+track.getAuthor()+" ~	"
			      +track.getTitle()+" are:\n");

            track.getNextTracks().forEach((link) -> {
                System.out.println(link.getTrack().getAuthor()+" ~	"
                        +link.getTrack().getTitle()+"	bpm:"
                        +link.getBpmDifference());
            });
	    
	});
	
    }
    
    public String randomString(int length) {
        
        char[] text = new char[length];
        
        //Why does for(char character : text){character=...} not work?
        for (int i = 0; i<text.length;i++) {
            text[i] = alphabet.charAt(ThreadLocalRandom.current().nextInt(alphabet.length()));
        }
        return new String(text);
    }
}
