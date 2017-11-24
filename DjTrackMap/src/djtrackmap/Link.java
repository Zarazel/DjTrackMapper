package djtrackmap;

import java.io.Serializable;

/**
 *
 * @author zarazel
 */

public class Link implements Comparable<Link>, Serializable {
    
    private Track   track;
    
    private int     rating;
    private float   bpmDifference;
    private String  keyChange;
    private String  Comment;
    
    @Override
    public int compareTo(Link anotherLink){
	if (rating == anotherLink.getRating()){
	    return track.compareTo(track);
	} else if (rating > anotherLink.getRating()) {
	    return -1;
	} else if (rating < anotherLink.getRating()){
	    return 1;
	} else {
	    return 0;
	}
    }
    
    public boolean equals(Link anotherLink){
	return track.equals(anotherLink.getTrack());
    }
    
    public Link(Track nextTrack){
	track = nextTrack;
    }
    
    public Track getTrack() {
	return track;
    }

    public void setTrack(Track track) {
	this.track = track;
    }

    public int getRating() {
	return rating;
    }

    public void setRating(int rating) {
	this.rating = rating;
    }

    public float getBpmDifference() {
	return bpmDifference;
    }

    public void setBpmDifference(float bpmDifference) {
	this.bpmDifference = bpmDifference;
    }

    public String getKeyChange() {
	return keyChange;
    }

    public void setKeyChange(String keyChange) {
	this.keyChange = keyChange;
    }

    public String getComment() {
	return Comment;
    }

    public void setComment(String Comment) {
	this.Comment = Comment;
    }
    
    
}
