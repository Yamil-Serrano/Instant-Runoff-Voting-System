/**
 * Represents a candidate with a unique ID, name, and active status. 
 * The class provides methods to access and modify the candidate's information.
 */
package main;

public class Candidate {
  
    private int id; // Unique identifier for the candidate
    private String name; // Name of the candidate
    private boolean isActive; // Indicates whether the candidate is currently active
    private String[] candidate; // Array to hold candidate information parsed from a string

    /**
     * Constructs a Candidate object from a comma-separated string containing the candidate's ID, name, and initial active status as true.
     * 
     * @param line A comma-separated string containing the candidate's ID and name. The format should be "id,name".
     */
    public Candidate(String line) {
    	this.candidate = line.split(",");
        this.id = Integer.parseInt(candidate[0]); 
        this.name = candidate[1];
        this.isActive = true; 
    }

	/**
	 * Returns the candidate's ID.
	 * 
	 * @return An integer representing the candidate's unique ID.
	 */
	 public int getId() {
		 return id;
	 }

	 /**
	  * Returns the candidate's active status.
	  *
	  * @return A boolean indicating whether the candidate is currently active.
	  */
	 public boolean isActive() {
		 return isActive;
	 } 

	 /**
	  * Returns the candidate's name.
	  *
	  * @return A string representing the candidate's name.
	  */
	 public String getName() {
		 return name;
	 }

	 /**
	  * Sets the candidate's active status.
	  *
	  * @param itis is A boolean indicating the new active status of the candidate.
	  */
	 public void setActive(boolean itis) {
		 isActive = itis;
	 }

	 /**
	  * Returns the candidate's active status. This method is functionally identical to isActive().
	  *
	  * @return A boolean indicating whether the candidate is currently active.
	  */
	 public boolean getActive() {
		 return isActive;
	 }
}
