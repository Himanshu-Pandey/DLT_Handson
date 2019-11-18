pragma solidity >=0.4.22 <0.6.0;
contract Election {

    address election_commision;
    mapping(address => bool) hasVoted;
    mapping(uint8 => uint8) votes;

    constructor() public {
        votes[1] = 0;
        votes[2] = 0;
        election_commision = msg.sender;
    }

    function vote(uint8 selection) public {
        require(!hasVoted[msg.sender]);
        votes[selection] = votes[selection] + 1;
        hasVoted[msg.sender] = true;
    }

    //Method to get votes for a party
    //Only election commission can get votes
    function getVotesFor(uint8 party) public view returns (uint8) {
        require(msg.sender == election_commision);
        return votes[party];
    }
}
