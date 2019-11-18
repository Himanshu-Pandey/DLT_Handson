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

    function getVotesFor(uint8 party) public view returns (uint8) {
        return votes[party];
    }
}
