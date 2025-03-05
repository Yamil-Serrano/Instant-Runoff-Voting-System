# Instant Runoff Voting System

## Project Description
This project was developed for the CIIC4020/ICOM4035 Data Structures course during the Spring 2023-2024 semester as Project #1. It implements an alternative voting system called Instant Runoff Voting (IRV) for electing a position in the fictional country of Poor Harbor. Unlike the traditional "most voted wins" system, this method seeks a more representative result.

## Data Structures Used
### Primary Structure
- **Custom Lists without Java Collections**: Custom lists were used to store:
  - Candidates
  - Ballots
  - Votes per candidate

### Main Classes
1. **Candidate**
   - Stores candidate information
   - Properties: ID, name, active status
   - Methods to manage candidate state

2. **Ballot**
   - Represents an individual voting ballot
   - Stores ballot number and votes
   - Methods to validate and manipulate votes

3. **Election**
   - Manages the entire voting process
   - Implements vote counting logic
   - Handles candidate elimination

## Voting Algorithm

### Counting Rules
1. **Initial Round**
   - Count first preference votes (rank 1)
   - If a candidate has more than 50% of first preference votes, they win

2. **Candidate Elimination**
   - If no winner, eliminate the candidate with the least first preference votes
   - In case of a tie, consider the number of second preference votes
   - If tie persists, follow defined tiebreaker criteria

3. **Vote Redistribution**
   - When eliminating a candidate, their votes are redistributed to the next preference
   - Ballots are updated by "moving up" preferences

### Special Features
- Ballot validation
- Tracking of active candidates
- Counting of invalid and blank ballots
- Recording of elimination rounds

## Ballot Validation
A ballot is considered:
- **Valid**: Meets all voting rules
- **Blank**: No candidates selected
- **Invalid**: Does not comply with ranking rules

## Functioning Example
In an election with 5 candidates and 10 ballots:
1. No candidate reaches 50% in the first round
2. Eliminate the candidate with the least votes
3. Redistribute votes according to preferences
4. Repeat until a candidate wins

## Tools and Technologies
- Language: Java
- Project Management: Maven (pom.xml configuration)
- Version Control: Git and GitHub Classroom

## Design Considerations
- No Java collections used
- Implementation of data structures from scratch
- Separation of responsibilities between classes
- Detailed documentation with Javadoc

## Execution Instructions
1. Clone the repository
2. Ensure input files in `inputFiles/`
3. Compile the project
4. Run the `Election` class
5. Results saved in `outputFiles/`

## Important Notes
- Academic project for Data Structures course
- Focus on algorithm and data structure design
- Does not use Java collection libraries