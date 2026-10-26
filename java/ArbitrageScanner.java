import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArbitrageScanner {

    // Configurações do Agente (Requisito 03)
    private static final double MIN_PROFIT_THRESHOLD = 15.0; // Lucro mínimo desejado em USD
    private static final double SWAP_FEE_PERCENT = 0.003;    // 0.3% de taxa de swap
    private static final double ESTIMATED_SLIPPAGE = 0.001;  // 0.1% de deslizamento estimado
    private static final double GAS_COST_USD = 4.50;         // Custo estimado de gás em USD

    // Classe para armazenar os dados de cada varredura (Requisito 04)
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
            return String.format("[%s] Fonte 1: %.2f | Fonte 2: %.2f | Bruto: %.2f | Custos: %.2f | Líquido: %.2f | Decisão: %s",
                    timestamp, priceSource1, priceSource2, rawSpread, totalCosts, netProfit, decision);
        }
    }

    private final List<ScanLog> history = new ArrayList<>();

    // Requisito 01: Lê preços de pelo menos 2 fontes na mesma digitalização
    public double[] fetchPrices() {
        // Numa implementação real, aqui faria chamadas Web3 / APIs (ex: Web3j ou HttpClient)
        // Para simulação em testnet (Requisito 05), geramos valores de mercado simulados:
        double basePrice = 2000.0;
        double variance1 = (Math.random() - 0.5) * 30; // Pequena flutuação
        double variance2 = (Math.random() - 0.5) * 30;
        
        return new double[] { basePrice + variance1, basePrice + variance2 + (Math.random() * 10) };
    }

    // Requisito 02 & 03 & 04: Varredura, cálculo de custos/lucro e tomada de decisão
    public void scanAndEvaluate() {
        double[] prices = fetchPrices();
        double price1 = prices[0];
        double price2 = prices[1];

        // Calcula a diferença bruta (Spread) considerando a compra na mais barata e venda na mais cara
        double buyPrice = Math.min(price1, price2);
        double sellPrice = Math.max(price1, price2);
        double rawSpread = sellPrice - buyPrice;

        // Requisito 02: Calcula o lucro líquido após taxas de swap, deslizamento e gás
        double swapFees = (buyPrice + sellPrice) * SWAP_FEE_PERCENT;
        double slippageCost = sellPrice * ESTIMATED_SLIPPAGE;
        double totalCosts = swapFees + slippageCost + GAS_COST_USD;
        
        double netProfit = rawSpread - totalCosts;

        // Requisito 03: Age apenas acima do limite configurável; caso contrário, regista o motivo
        String decision;
        if (netProfit >= MIN_PROFIT_THRESHOLD) {
            decision = "EXECUTAR: Oportunidade lucrativa acima do limite (Simulação Testnet)";
            // Aqui entraria a lógica de submissão da transação na testnet (Requisito 05)
        } else if (netProfit > 0) {
            decision = "IGNORADO: Lucro positivo, mas abaixo do limite configurável (" + MIN_PROFIT_THRESHOLD + ")";
        } else {
            decision = "IGNORADO: Oportunidade não lucrativa após dedução de custos e gás.";
        }

        // Requisito 04: Registra cada varredura com distribuição bruta, custos, resultado líquido e decisão
        ScanLog log = new ScanLog(price1, price2, rawSpread, totalCosts, netProfit, decision);
        history.add(log);
        
        System.out.println(log);
    }

    // Método principal para demonstrar o agente a correr de forma segura (Requisito 05)
    public static void main(String[] args) {
        ArbitrageScanner scanner = new ArbitrageScanner();
        
        System.out.println("=== INICIANDO SCANNER DE ARBITRAGEM (MODO SIMULAÇÃO TESTNET) ===");
        
        // Simula 5 varreduras consecutivas
        for (int i = 1; i <= 5; i++) {
            System.out.println("\nVarredura #" + i);
            scanner.scanAndEvaluate();
            try {
                Thread.sleep(1000); // Pausa de 1 segundo entre as varreduras
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("\nTotal de varreduras registradas no histórico: " + scanner.history.size());
    }
}
