import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
public class CompliantNode implements Node {    
    private static final int NUM_OF_TRUST_ROUNDS = 2;    
    private boolean[] followees;    
    private Map<Integer, Integer> followeesScore;    
    private Set<Transaction> pendingTransactions;    
    private Transaction markerTxn;    
    private int round;    
    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {     
        pendingTransactions = new HashSet<>();        
        followeesScore = new HashMap<>();   
    }   
    public void setFollowees(boolean[] followees) {      
        this.followees = followees;   
    }    
    public void setPendingTransaction(Set<Transaction> pendingTransactions) {       
        this.pendingTransactions.clear();       
        this.pendingTransactions.addAll(pendingTransactions);       
        if (!pendingTransactions.isEmpty()) {           
            markerTxn = pendingTransactions.iterator().next();       
        }   
    }   
    public Set<Transaction> sendToFollowers() {      
        Set<Transaction> sendTransactions = new HashSet<>();       
        if (round > NUM_OF_TRUST_ROUNDS) {           
            sendTransactions.addAll(pendingTransactions);           
            pendingTransactions.clear();       
        }
        else if (markerTxn != null) {   
            sendTransactions.add(markerTxn);      
        }      
        return sendTransactions;   
    }  
    public void receiveFromFollowees(Set<Candidate> candidates) {     
        round++;      
        Map<Integer, Set<Transaction>> senderToTxMap = new HashMap<>();    
        for (Candidate c : candidates) {          
            if (followees[c.sender]) {            
                senderToTxMap.computeIfAbsent(c.sender, k -> new HashSet<>()).add(c.tx);        
            }     
        }      
        if (round <= NUM_OF_TRUST_ROUNDS) {       
            for (int i = 0; i < followees.length; i++) {            
                if (followees[i]) {                
                    followeesScore.putIfAbsent(i, 0);                 
                    if (senderToTxMap.containsKey(i) && senderToTxMap.get(i).size() == 1) {                  
                        followeesScore.put(i, followeesScore.get(i) + 1);              
                    }             
                }         
            }   
        } else {      
            for (int i = 0; i < followees.length; i++) {         
                if (followees[i] && followeesScore.getOrDefault(i, 0) == NUM_OF_TRUST_ROUNDS) {        
                    pendingTransactions.addAll(senderToTxMap.getOrDefault(i, new HashSet<>()));           
                }        
            }    
        }  
    }}
