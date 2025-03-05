package main;
import interfaces.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import data_structures.ArrayList;
import java.util.function.Function;

/**
 * Represents an election process, handling the operation of reading candidates and ballots from files,
 * evaluating ballots, and determining the election outcome.
 */
public class Election {
    
    private List<Ballot> validBallotsList; // List of valid ballots
    private List<Candidate> candidates; // List of candidates in the election
    private List<List<Ballot>> ballots_1sVotes_InIdOrder; // Lists of ballots grouped by firstc choice votes for each candidate
    private List<String> eliminatedCandidates; // List of eliminated candidates throughout the rounds
    
    // Counters for different types of ballots
    private int blankBallots = 0; 
    private int invalidBallots = 0;
    private int validBallots = 0;
    
    private int winnerVotes = 0; // Number of votes for the winning candidate
    
    /**
     * Default constructor initializing an election with default filenames for candidates and ballots.
     */
    public Election() {
        this("candidates.csv", "ballots.csv");
    }
    
    /**
     * Constructs an Election object using specified filenames for candidates and ballots.
     * Initializes lists and reads data from the files.
     * 
     * @param candidates_filename The filename for the candidates CSV file.
     * @param ballot_filename The filename for the ballots CSV file.
     */
    public Election(String candidates_filename, String ballot_filename) {
        
        this.eliminatedCandidates = new ArrayList<String>(); // Initialize the list of eliminated candidates
        this.validBallotsList = new ArrayList<Ballot>(); // Initialize the list of valid ballots
        this.candidates = new ArrayList<Candidate>(); // Initialize the list of candidates
        this.ballots_1sVotes_InIdOrder = new ArrayList<List<Ballot>>(); // Initialize the list of ballots grouped by first choice
        
        candidatesReader("inputFiles/" + candidates_filename); // Read candidates from file
        for (int i = 0; i < candidates.size(); i++) {
            this.ballots_1sVotes_InIdOrder.add(new ArrayList<Ballot>());
        } 
        ballotReader("inputFiles/" + ballot_filename); // Read ballots from file
        
    }
    
    /**
     * Writes a summary of the election results to a file, including the number of different types of ballots,
     * rounds of elimination, and the winner.
     */
    public void summaryWriter() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("outputFiles/expected_output_test.txt"))) {
            writer.write("Number of ballots: " + getTotalBallots() + "\n");
            writer.write("Number of blank ballots: " + blankBallots + "\n");
            writer.write("Number of invalid ballots: " + invalidBallots + "\n");
            for (int i = 0; i < eliminatedCandidates.size(); i++) {
            	String[] candidate = eliminatedCandidates.get(i).split("-");
                writer.write("Round " + (i + 1) + ": "+ candidate[0] + " was eliminated with " + candidate[1] + " #1's\n");
                // If you have the name of the eliminated candidate, you can also write it here.
            }
            writer.write("Winner: " + getWinner() + " wins with " + gethighestVotes() + " #1's\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Reads candidates from a specified file and adds them to the list of candidates.
     * 
     * @param candidates_filename The path and name of the file containing candidate data.
     */
    private void candidatesReader(String candidates_filename) {
        String candidateLine;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(candidates_filename)); // Opens file for reading
            while ((candidateLine = reader.readLine()) != null) { // Read until end of file
                Candidate candidate = new Candidate(candidateLine); // Create candidate object from line
                this.candidates.add(candidate); // Add candidate to list
            }
            reader.close(); // Close the file
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Reads ballots from a specified file, evaluates them, and classifies them accordingly.
     * 
     * @param ballot_filename The path and name of the file containing ballot data.
     */
    private void ballotReader(String ballot_filename) {
        String ballotLine;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(ballot_filename));
            while ((ballotLine = reader.readLine()) != null) {
                Ballot ballot = new Ballot(ballotLine, candidates); // Create ballot object from line
                // Classify the ballot as valid, blank, or invalid
                if (ballot.getBallotType() == 0) {
                    this.validBallotsList.add(ballot); // Add to valid ballots list
                    if (ballot.getCandidateByRank(1) != -1) {
                        ballots_1sVotes_InIdOrder.get(ballot.getCandidateByRank(1) - 1).add(ballot); // Group by first choice
                    }
                    validBallots++;
                }
                else if (ballot.getBallotType() == 1) blankBallots++;
                else if (ballot.getBallotType() == 2) invalidBallots++;
            }
            reader.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Determines the winner of the election based on the ranked-choice voting system.
     * If no candidate wins outright, the candidate with the fewest votes is eliminated in each round until a winner is found.
     * 
     * @return The name of the winning candidate.
     */
    public String getWinner() {
    	boolean winnerFound = false;
    	int totalVotes = getTotalValidBallots() / 2; // Get the total number of valid ballots
        Candidate leadingCandidate = null;
        int highestVotes = 0;
    	 while (!winnerFound) {
    	        for (int i = 0; i < candidates.size(); i++) {
    	            Candidate candidate = candidates.get(i);
    	            if (!candidate.isActive()) continue; // Skip inactive candidates

    	            int votes = ballots_1sVotes_InIdOrder.get(i).size(); // Count first choice votes for this candidate
    	            if (votes > highestVotes) { 
    	                highestVotes = votes;
    	                leadingCandidate = candidate;
    	            }

    	            if (votes > totalVotes) { // Check if this candidate has more than half the votes
    	            	winnerVotes = votes;
    	            	winnerFound = !winnerFound;
    	            }
    	        }

    	        // eliminating the candidate with few votes
    	        int minVotes = 10000; // Assuming a high number to find the minimun
    	        int idOfCandidate_Eliminate = 0; // Will store the ID of the candidate to eliminate

            for (int i = 0; i < candidates.size(); i++) {
                Candidate candidate = candidates.get(i);

                if (!candidate.isActive()) continue; //Skip if the candidate is not active

                int firstChoiceVotes = ballots_1sVotes_InIdOrder.get(i).size();

                if (firstChoiceVotes < minVotes) { // Comparation to set the values of the next candidate to be eliminated
                    minVotes = firstChoiceVotes;
                    idOfCandidate_Eliminate = candidate.getId();
                } 
                else if (firstChoiceVotes == minVotes) {
                    int currentCandidateId = candidate.getId();

                    for (int j = 2; j <= candidates.size(); j++) {
                        int totalVotesForMinCandidate = 0;
                        int totalVotesForCurrentCandidate = 0;

                        for (Ballot ballot : validBallotsList) {
                            if (ballot.getCandidateByRank(j) == idOfCandidate_Eliminate) {
                                totalVotesForMinCandidate++;
                            }
                        }

                        for (Ballot ballot : validBallotsList) {
                            if (ballot.getCandidateByRank(j) == currentCandidateId) {
                                totalVotesForCurrentCandidate++;
                            }
                        }

                        if (totalVotesForMinCandidate > totalVotesForCurrentCandidate) {
                            idOfCandidate_Eliminate = currentCandidateId;
                            break;

                        } else if (totalVotesForMinCandidate < totalVotesForCurrentCandidate) {
                            break;
                        }

                        if (j == candidates.size() && totalVotesForMinCandidate == totalVotesForCurrentCandidate) {
                            if (idOfCandidate_Eliminate > currentCandidateId) {
                                idOfCandidate_Eliminate = currentCandidateId;
                            }
                        }
                    }
                }
            }
            eliminateCandidate(idOfCandidate_Eliminate);

            Candidate remainingCandidate = null;
            int activeCount = 0;
            for (Candidate candidate : candidates) {
                if (candidate.isActive()) {
                    remainingCandidate = candidate;
                    activeCount++;
                    if (activeCount > 1) break;
                }
            }

            if (activeCount == 1 && remainingCandidate != null) {
                return remainingCandidate.getName();
            }

            if (leadingCandidate == null) {
                return "No winner could be determined.";
            }
        }
    	 return leadingCandidate.getName(); // This candidate wins
    	 
    }
    
    /**
     * Eliminates a candidate by their ID from the election, redistributing their votes according to voters' next preferences.
     * 
     * @param candidateId The ID of the candidate to be eliminated.
     */
    private void eliminateCandidate(int candidateId) {
        for (Candidate candidate : candidates) {
            if (candidate.getId() == candidateId) {
                candidate.setActive(false); // Set the candidate as inactive
                int onesVotesCounter = ballots_1sVotes_InIdOrder.get(candidateId - 1).size();
                eliminatedCandidates.add(candidate.getName() + "-" + onesVotesCounter); // Add to eliminated candidates list
                break;
            }
        }
        List<Ballot> newBallots = new ArrayList<Ballot>();
        for (Ballot ballot : ballots_1sVotes_InIdOrder.get(candidateId - 1)) {
            newBallots.add(ballot);
        }
        
        ballots_1sVotes_InIdOrder.get(candidateId - 1).clear(); // Clear ballots for eliminated candidate
        
        for (Ballot ballot : newBallots) {
            
            boolean activeCandidateFound = false;
            
            while (!activeCandidateFound) {
                ballot.eliminate(candidateId); // Eliminate vote for this candidate
                int newCandidateID = ballot.getCandidateByRank(1); // Get next preferred candidate
                
                if (newCandidateID == -1) {
                    break; // If no valid next choice, break loop
                }
                for (Candidate candidate : candidates) {
                    if (candidate.getId() == newCandidateID && candidate.isActive()) {
                        ballots_1sVotes_InIdOrder.get(newCandidateID - 1).add(ballot); // Assign ballot to next preferred active candidate
                        activeCandidateFound = true;
                        break;
                    }
                }
                if (!activeCandidateFound) {candidateId = newCandidateID;} // If not found, try the next candidate in preference
            }
        }
    }
    
    /**
     * Prints each candidate's information using a custom print format defined by a lambda function.
     * 
     * @param func A function defining how candidate information should be formatted and printed.
     */
    public void printCandidates(Function<Candidate, String> func) { //This method will iterate over the list of candidates
        for (Candidate candidate : candidates) {
            System.out.println(func.apply(candidate));
        }
    }
    
    /**
     * Gets the highest number of votes received by the winning candidate.
     * 
     * @return The number of votes for the winning candidate.
     */
    public int gethighestVotes() {
    	return winnerVotes;
    }
    
    /**
     * Retrieves the total number of ballots processed in the election, including valid, invalid, and blank ballots.
     * 
     * @return The total number of ballots.
     */
    public int getTotalBallots() {
        return (validBallots + invalidBallots + blankBallots);
    }

    /**
     * Retrieves the total number of ballots that were deemed invalid. Invalid ballots are those that do not conform
     * to the expected format or have other issues preventing them from being counted as valid.
     * 
     * @return The total number of invalid ballots.
     */
    public int getTotalInvalidBallots() {
        return invalidBallots;
    }

    /**
     * Retrieves the total number of ballots that were left blank. A blank ballot is one where the voter did not indicate
     * any preference among the candidates.
     * 
     * @return The total number of blank ballots.
     */
    public int getTotalBlankBallots() {
        return blankBallots;
    }

    /**
     * Retrieves the total number of ballots that were counted as valid. Valid ballots are those that were properly filled out
     * according to the election's rules.
     * 
     * @return The total number of valid ballots.
     */
    public int getTotalValidBallots() {
        return validBallots;
    }

    /**
     * Retrieves a list of candidates who have been eliminated from the election. This list includes each eliminated candidate's
     * name and the number of first-choice votes they had at the time of elimination.
     * 
     * @return A List of strings representing eliminated candidates and their vote counts.
     */
    public List<String> getEliminatedCandidates() {
        return eliminatedCandidates;
    }

}