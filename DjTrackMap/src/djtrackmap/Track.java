/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package djtrackmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;

/**
 *
 * @author zarazel
 * 
 * TODO:
 * Optimize fields ,regarding id and performance of compareTo()
 * Further field content optimization possible.
 * 
 * DONE 
 * previousTrack do not have to include fields, like comments etc.= previous element
 * can be of type Track.
 */
public class Track implements Comparable<Track>, Serializable {
    
    
    //Assuming Tracks are distinctly identified by author and title.
    private String  id;
    
    private String  author;
    private String  title;
    private String  genre;
    private float   bpm;
    private String  key;
    private String  comment;
    
    private List<Track> previous;
    private List<Link> next;
    
    public Track(String author, String title){
	
	this.author = author;
	this.title = title;
	
	previous = new ArrayList<>();
	next = new ArrayList<>();
	
	refresh();
    }
    
    public Track(String author, String title, float bpm){
	
	this.author = author;
	this.title = title;
	this.bpm = bpm;
	
	previous = new ArrayList<>();
	next = new ArrayList<>();
	
	refresh();
    }
    
    public Track(String author, String title, String genre, float bpm, String key, String comment){
	
	this.author = author;
	this.title = title;
	this.genre = genre;
	this.bpm = bpm;
	this.key = key;
	this.comment = comment;
	
	previous = new ArrayList<>();
	next = new ArrayList<>();
	
	refresh();
    }
    
    
    /*
    optimize author by putting "the" or "DJ" at the end.
    */
    public final void refresh(){
	
	author = author.trim();
	title = title.trim();
	
	if (author.substring(0, 3).compareToIgnoreCase("The ") == 0){
	    author = author.substring(4).concat(" ,The");
	}
	if (author.substring(0, 3).compareToIgnoreCase("The ") == 0){
	    author = author.substring(3).concat(" ,DJ");
	}
	
	//make id
	id = author.concat(title);
	
	Collections.sort(next);
	Collections.sort(previous);
	
    }
    
    /*
    Quasi equivalent to java.lang.String.compareToignoreCase(String).
    CASE INSENSITIVE!
    
    0: track author and title are the same.
    -1: track precedes anotherTrack lexicographically.
    +1: track follows anotherTrack lexicographically.
    */
    @Override
    public int compareTo(Track anotherTrack){
	    
	    
	/*
	Which is FASTER???
	if(this.author.compareTo(anotherTrack.getAuthor()) == 0){
	    return 0;
	} else {
	    return this.author.concat(this.title).compareTo(anotherTrack.getAuthor().concat(anotherTrack.getTitle()));
	}
	
	or (id and refresh method needed)
	
	return this.id.compareTo(anotherTrack.getId());
	*/
	return id.compareToIgnoreCase(anotherTrack.getId());
    }
    
    /*
    Returns true if anotherTrack.id is the same.
    */
    public boolean equals(Track anotherTrack){
	return id.equalsIgnoreCase(anotherTrack.getId());
    }
    
    public boolean containsLinksOrComments(){
	return (comment != null) || !next.isEmpty() || !previous.isEmpty();
    }
    
    /*
    adds a new Link to a nextTrack, if it does not exist already,
    
    TODO!!!
    
    It will calculate the bpm (tempo) difference, when both Tracks' bpm is not 0.
    A positive difference indicates that the nextTrack is faster.
    
    */
    public void addNextLink(Track nextTrack){
	
	//Search for existing Link destination (Link with same TrackID)
	boolean linkExistsAlready = false;
	
	if(!this.equals(nextTrack)){
            
	    for (int i = 0; i<next.size(); i++){
		if(next.get(i).getTrack().equals(nextTrack)){
		    linkExistsAlready = true;
		    i=next.size();
		}
	    }
            
	    if (linkExistsAlready){
		System.out.println("Link to "+nextTrack.getAuthor()+" ~ "+nextTrack.getTitle()+" exists already!\n"
			+ "To insert the Track as a following Track again, you have to delete the existing first.");
	    } else {
		next.add(new Link(nextTrack));
		if( (this.getBpm() != 0) && (nextTrack.getBpm() != 0)){
		    next.get(next.size()-1).setBpmDifference(nextTrack.getBpm()-this.bpm);
		}
                
                nextTrack.previous.add(this);
	    }
	}
    }
    
    public void addPreviousTrack(Track previousTrack){
	
	//Search for existing Link destination (Link with same TrackID)
	boolean linkExistsAlready = false;
	
	if(!this.equals(previousTrack)){
	    for (int i = 0; i<previous.size(); i++){
		if(previous.get(i).equals(previousTrack)){
		    linkExistsAlready = true;
		    i=previous.size();
		}
	    }
	    if (linkExistsAlready){
		System.out.println("Link to "+previousTrack.getAuthor()+" ~ "+previousTrack.getTitle()+" exists already!\n"
			+ "To insert the Track as a following Track again, you have to delete the existing first.");
	    } else {
		previous.add(previousTrack);
	    }
	}
	
    }
    
    /*
    Getter and Setter
    */
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getAuthor() {
	return author;
    }

    public void setAuthor(String author) {
	this.author = author;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getGenre() {
	return genre;
    }

    public void setGenre(String genre) {
	this.genre = genre;
    }

    public float getBpm() {
	return bpm;
    }

    public void setBpm(float bpm) {
	this.bpm = bpm;
    }

    public String getKey() {
	return key;
    }

    public void setKey(String key) {
	this.key = key;
    }

    public String getComment() {
	return comment;
    }

    public void setComment(String comment) {
	this.comment = comment;
    }

    public List<Track> getPreviousTracks() {
	return previous;
    }

    public void setPreviousTracks(List<Track> previousTracks) {
	
        previousTracks.forEach(this::addPreviousTrack);
        
    }

    public List<Link> getNextTracks() {
	return next;
    }

    public void setNextTracks(List<Link> nextTracks) {
	
        nextTracks.forEach((nextTrack) -> {
            this.addNextLink(nextTrack.getTrack());
        });
    }
    
}
