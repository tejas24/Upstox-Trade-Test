package com.upstox;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upstox.model.Trade;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class Reader implements Runnable {

    protected BlockingQueue<Trade> blockingQueue;
    String stockName;

    public Reader(BlockingQueue<Trade> blockingQueue, String stockName) {
        this.blockingQueue = blockingQueue;
        this.stockName = stockName;
    }

    public void run() {
        String fileName = "\\UpstoxTradeTest\\src\\main\\resources\\trades.json";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String line;

        Trade trade;
        try {

            FileReader fileReader = new FileReader(fileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                trade = new Trade();
                JsonNode jsonNode = objectMapper.readTree(line);
                trade.setSym(jsonNode.get("sym").asText());
                trade.setP(jsonNode.get("P").asDouble());
                trade.setQ(jsonNode.get("Q").asDouble());
                trade.setTS2(jsonNode.get("TS2").asLong());
                //System.out.println(trade.getTradeInDateTime());
//                if(trade.getSym().equals("XETHZUSD")) {
//                    CalculateOHLC compute = new CalculateOHLC();
//                    OHLC ohlc = compute.FetchBarStats(trade, 1);
//                    System.out.println(ohlc.toString());
//                }
//                SwingUtilities.invokeLater(() -> {
//                    OHLCChart example = new OHLCChart(
//                            "High Low Chart Example");
//                    example.setSize(800, 400);
//                    example.setLocationRelativeTo(null);
//                    example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//                    example.setVisible(true);
//                });

                if (trade.getSym().equals(stockName))
                    blockingQueue.put(trade);
                //Common.trades.add(trade);
            }
            blockingQueue.put(new Trade());
            //System.out.println(trades);
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error reading file '" + fileName + "'");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
