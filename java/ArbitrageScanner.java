import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArbitrageScanner {

    // Agent Configurations (Requirement 03)
    private static final double MIN_PROFIT_THRESHOLD = 15.0; // Minimum desired profit in USD
    private static final double SWAP_FEE_PERCENT = 0.003;    // 0.3% swap fee
    private static final double ESTIMATED_SLIPPAGE = 0.001;  // 0.1% estimated slippage
    private static final double GAS_COST_USD = 4.50;         // Estimated gas cost in USD

    // Class to store scan logs (Requirement 04)
    public static class ScanLog {
        LocalDateTime timestamp;
        double priceSource1;
        double priceSource2;
        double rawSpread;
        double totalCosts;
        double netProfit;
        String decision;

        public ScanLog(double p1, double p2, double rawSpread, double totalCosts, double netProfit, String decision) {
            this.timestamp = LocalDateTime.now();
            this.priceSource1 = p1;
            this.priceSource2 = p2;
            this.rawSpread = rawSpread;
            this.totalCosts = totalCosts;
            this.netProfit = netProfit;
            this.decision = decision;
        }

        @Override
        public String toString() {
            return String.format("[%s] Source 1: %.2f | Source 2: %.2f | Raw Spread: %.2f | Costs: %.2f | Net Profit: %.2f | Decision: %s",
                    timestamp, priceSource1, priceSource2, rawSpread, totalCosts, netProfit, decision);
        }
    }

    private final List<ScanLog> history = new ArrayList<>();

    // Requirement 01: Reads prices from at least 2 sources in the same scan
    public double[] fetchPrices() {
        // In a real implementation, this would make Web3 / API calls (e.g., Web3j or HttpClient)
        // For testnet simulation (Requirement 05), we generate simulated market values:
        double basePrice = 2000.0;
        double variance1 = (Math.random() - 0.5) * 30;
        double variance2 = (Math.random() - 0.5) * 30;
        
        return new double[] { basePrice + variance1, basePrice + variance2 + (Math.random() * 10) };
    }

    // Requirements 02, 03 & 04: Scan, cost/profit calculation, and decision making
    public void scanAndEvaluate() {
        double[] prices = fetchPrices();
        double price1 = prices[0];
        double price2 = prices[1];

        // Calculate raw spread buying on the cheapest and selling on the most expensive source
        double buyPrice = Math.min(price1, price2);
        double sellPrice = Math.max(price1, price2);
        double rawSpread = sellPrice - buyPrice;

        // Requirement 02: Calculate net profit after swap fees, estimated slippage, and gas
        double swapFees = (buyPrice + sellPrice) * SWAP_FEE_PERCENT;
        double slippageCost = sellPrice * ESTIMATED_SLIPPAGE;
        double totalCosts = swapFees + slippageCost + GAS_COST_USD;
        
        double netProfit = rawSpread - totalCosts;

        // Requirement 03: Act only above configurable profit threshold; otherwise log reasons
        String decision;
        if (netProfit >= MIN_PROFIT_THRESHOLD) {
            decision = "EXECUTE: Profitable opportunity above threshold (Testnet Simulation)";
            // Real transaction execution logic on testnet would go here (Requirement 05)
        } else if (netProfit > 0) {
            decision = "IGNORED: Positive profit, but below threshold (" + MIN_PROFIT_THRESHOLD + ")";
        } else {
            decision = "IGNORED: Unprofitable opportunity after costs and gas deduction.";
        }

        // Requirement 04: Log each scan with raw spread, costs, net result, and decision
        ScanLog log = new ScanLog(price1, price2, rawSpread, totalCosts, netProfit, decision);
        history.add(log);
        
        System.out.println(log);
    }

    // Main method to demonstrate the agent running securely (Requirement 05)
    public static void main(String[] args) {
        ArbitrageScanner scanner = new ArbitrageScanner();
        
        System.out.println("=== STARTING ARBITRAGE SCANNER (TESTNET SIMULATION MODE) ===");
        
        // Simulate 5 consecutive scans
        for (int i = 1; i <= 5; i++) {
            System.out.println("\nScan #" + i);
            scanner.scanAndEvaluate();
            try {
                Thread.sleep(1000); // 1 second pause between scans
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("\nTotal scans recorded in history: " + scanner.history.size());
    }
}
