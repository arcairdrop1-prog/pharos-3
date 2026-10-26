# Pharos Arbitrage Scanner

An intelligent agent developed in **Java** for the Pharos Agent Arena, designed to monitor prices across multiple sources, deduct operational costs, and securely validate arbitrage opportunities.

## 📋 Requirements Met

1. **Dual Source Reading (Req. 01):** Reads prices from at least two distinct sources within the same scan iteration.
2. **Net Profit Calculation (Req. 02):** Automatically subtracts proportional swap fees, estimated slippage, and fixed gas costs in USD.
3. **Configurable Threshold Filter (Req. 03):** Evaluates and acts only if net profit exceeds the minimum configured threshold; otherwise, logs the rejection reason.
4. **Historical Logging (Req. 04):** Keeps an audit trail of every scan containing prices, raw spread, total costs, net profit, and final decision.
5. **Testnet Safety (Req. 05):** Runs entirely in a secure simulation environment without risking real funds.

## 🚀 How to Run

1. Make sure you have **Java JDK** installed.
2. Compile the file:
   ```bash
   javac ArbitrageScanner.java
