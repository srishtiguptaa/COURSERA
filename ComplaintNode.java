import java.util.*;

interface Node {
    void receiveTransaction(String sender, String transaction);
    void broadcastTransaction(String transaction);
}

class CompliantNode implements Node {
    private Set<String> transactions;
    private List<CompliantNode> followers;

    public CompliantNode() {
        this.transactions = new HashSet<>();
        this.followers = new ArrayList<>();
    }

    public void addFollower(CompliantNode follower) {
        this.followers.add(follower);
    }

    @Override
    public void receiveTransaction(String sender, String transaction) {
        // Compliant nodes always accept transactions from trusted nodes
        this.transactions.add(transaction);
    }

    @Override
    public void broadcastTransaction(String transaction) {
        // Compliant nodes broadcast transactions to their followers
        for (CompliantNode follower : followers) {
            follower.receiveTransaction(this.toString(), transaction);
        }
    }

    @Override
    public String toString() {
        // Just returning a unique identifier for the node
        return super.toString();
    }
}

public class ConsensusAlgorithm {
    public static void main(String[] args) {
        Map<String, List<String>> trustRelationships = new HashMap<>();
        trustRelationships.put("A", Arrays.asList("B", "C"));
        trustRelationships.put("B", Arrays.asList("D", "E"));
        trustRelationships.put("C", Collections.singletonList("F"));
        trustRelationships.put("D", Collections.emptyList());
        trustRelationships.put("E", Collections.emptyList());
        trustRelationships.put("F", Collections.emptyList());

        Map<String, CompliantNode> nodes = new HashMap<>();
        for (String nodeName : trustRelationships.keySet()) {
            nodes.put(nodeName, new CompliantNode());
        }

        for (Map.Entry<String, List<String>> entry : trustRelationships.entrySet()) {
            String nodeName = entry.getKey();
            List<String> followers = entry.getValue();
            for (String followerName : followers) {
                nodes.get(nodeName).addFollower(nodes.get(followerName));
            }
        }

        // Simulate consensus algorithm with sample transactions
        List<String[]> transactions = Arrays.asList(new String[]{"A", "transaction1"},
                                                     new String[]{"B", "transaction2"});
        simulateConsensus(nodes, transactions);

        // Print transactions received by each node
        for (Map.Entry<String, CompliantNode> entry : nodes.entrySet()) {
            String nodeName = entry.getKey();
            CompliantNode node = entry.getValue();
            System.out.println("Node " + nodeName + " received transactions: " + node.transactions);
        }
    }

    private static void simulateConsensus(Map<String, CompliantNode> nodes, List<String[]> transactions) {
        for (String[] transaction : transactions) {
            String sender = transaction[0];
            String transactionData = transaction[1];
            for (CompliantNode node : nodes.values()) {
                node.receiveTransaction(sender, transactionData);
            }
        }
    }
}
