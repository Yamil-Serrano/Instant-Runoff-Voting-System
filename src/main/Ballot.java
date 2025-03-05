package main;
import interfaces.List;

/**
 * Represents a ballot in an election, recording the rankings of candidates as per voter's preference.
 * This class provides methods to manipulate and retrieve information about the rankings.
 */
public class Ballot {
	
	private int[][] rankCandidates; // 2D array to store candidates' IDs and their ranks
	private int candidateNum; // Number of candidates ranked on the ballot
	private int ballotNum; // Unique identifier for the ballot

	/**
	 * Constructs a Ballot object from a given line of text and a list of candidates.
	 * The line format should be "ballotId,candidateId:rank,...", where ballotId is the ballot's ID,
	 * and each candidateId:rank pair represents a candidate's ID and the voter's rank for that candidate.
	 * Inactive candidates will be marked accordingly in the rankings.
	 * 
	 * @param line A string representation of the ballot's rankings.
	 * @param candidates A List of all candidates participating in the election.
	 */
	public Ballot(String line, List<Candidate> candidates) {
		
		String[] ballot = line.split(",");
		ballotNum = Integer.parseInt(ballot[0]);
		this.rankCandidates = new int[2][ballot.length - 1];
		
		this.candidateNum = 0;
        for (String parts : ballot) {
            if (parts.contains(":")) {
                String[] rankId = parts.split(":");
                rankCandidates[0][candidateNum] = Integer.parseInt(rankId[1]); // candidates id's
                rankCandidates[1][candidateNum] = Integer.parseInt(rankId[0]);  // ranks
                candidateNum++;
            }
        }
        
        for(Candidate candidate : candidates) {
        	if(!candidate.getActive()) {
        		for(int i = 0 ; i < rankCandidates[0].length - 1; i++) {
        			if( rankCandidates[0][i] == candidate.getId()) {
        				rankCandidates[0][i] = -1;
        			}
        		}
        	}
        }
	}
	/**
	 * Returns the unique ballot number of this Ballot.
	 * 
	 * @return The ballot number as an integer.
	 */
	public int getBallotNum() {
		return ballotNum;
	}
	/**
	 * Returns the rank assigned to a specific candidate by the voter.
	 * 
	 * @param candidateID The ID of the candidate whose rank is queried.
	 * @return The rank of the specified candidate if present; -1 otherwise.
	 */
	public int getRankByCandidate(int candidateID) {
	    int index = 0;
	    for(int candidate : rankCandidates[1]) {
	    	if(candidate == candidateID) {
	    		return rankCandidates[0][index];
	    	}
	    	index++;
	    }
	    return -1;
	}
	/**
	 * Returns the ID of the candidate assigned a specific rank by the voter.
	 * 
	 * @param rank The rank in question.
	 * @return The ID of the candidate with the specified rank if any; -1 otherwise.
	 */
	public int getCandidateByRank(int rank) {
		int index = 0;
	    for(int candidate : rankCandidates[0]) {
	    	if(candidate == rank) {
	    		return rankCandidates[1][index];
	    	}
	    	index++;
	    }
	    return -1;
	}

	/**
	 * Eliminates a candidate from the ballot, adjusting the rankings accordingly.
	 * 
	 * @param candidateId The ID of the candidate to be eliminated.
	 * @return true if the candidate was successfully eliminated; false otherwise.
	 */
	public boolean eliminate(int candidateId) {
		int temp;
		int index = 0;
	    for(int candidate : rankCandidates[1]) {
	    	if(candidate == candidateId || candidate == -1) {
	    		temp = rankCandidates[0][index];
	    		rankCandidates[0][index] = -1;
	    		//rankCandidates[1][index] = -1;
	    		for(int i = 0; i < rankCandidates[0].length; i++) {
	    			if(rankCandidates[0][i] > temp) {
	    				rankCandidates[0][i]--;
	    			}
	    		}
	    		return true;
	    	}
	    	index++;
	    }
	    return false;
	}
	/**
	 * Checks if there are any repeated elements in the given array.
	 * 
	 * @param list An array of integers.
	 * @return true if there is at least one repeated element; false otherwise.
	 */
	private static boolean isRepeated(int[] list) {
		for (int i = 0; i < list.length - 1; i++) {
            for (int j = i + 1; j < list.length; j++) {
                if (list[i] == list[j]) {
                    return true;
                }
            }
        }
		return false;
	}
	/**
	 * Determines the type of the ballot based on its content.
	 * Returns 0 if the ballot is valid, 1 if it is blank, and 2 if it is invalid (e.g., due to repeated ranks).
	 * 
	 * @return An integer indicating the ballot's type: 0 for valid, 1 for blank, and 2 for invalid.
	 */
	public int getBallotType() {
	    // Check if there are no ranks assigned, indicating a blank ballot
	    if (rankCandidates[0].length == 0 || rankCandidates[1].length == 0) {
	        return 1; // Blank ballot
	    }

	    // Check for repeated candidates or ranks
	    if (isRepeated(rankCandidates[0]) || isRepeated(rankCandidates[1])) {
	        return 2; // Invalid ballot due to repeated candidates or ranks
	    }

	    // Check if any candidate has a rank greater than the number of candidates
	    for (int rank : rankCandidates[0]) {
	        if (rank > candidateNum) {
	            return 2; // Invalid ballot due to rank exceeding the number of candidates
	        }
	    }
	    return 0;
	}
}
