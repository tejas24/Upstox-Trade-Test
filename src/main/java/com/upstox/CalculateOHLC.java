package com.upstox;

import com.upstox.model.OHLC;
import com.upstox.model.Trade;
import com.upstox.util.Common;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class CalculateOHLC implements Runnable {
    protected BlockingQueue<Trade> blockingQueue = null;

    static Date intervalStartTime = new Date();
    static Date intervalEndTime = new Date();
    static int count = 0;
    static int barNum = 1;

    static String inputStockName;
    static int inputInterval = 15;

    OHLC previousOhlc;
    String previousStockName = "";

    public CalculateOHLC(BlockingQueue<Trade> blockingQueue, String eventname, String interval) {
        this.blockingQueue = blockingQueue;
    }

    public CalculateOHLC() {
    }

    @Override
    public void run() {
        Trade trade = null;
        int bar_num = 1;
        while (true) {
            try {
                processor(trade = blockingQueue.take());

                if (trade.getSym() == null) {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static Date addSeconds(Date toDate, int seconds) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(toDate);
        cal.add(Calendar.SECOND, seconds);
        Date newDate = cal.getTime();
        return newDate;
    }

    public void processor(Trade trade) {
        if (count == 0) {
            intervalStartTime = trade.getTradeInDateTime();
            intervalEndTime = addSeconds(intervalStartTime, inputInterval);
            fetchBarStats(trade, barNum);
        } else {
            if (trade.getTradeInDateTime().after(intervalStartTime) && trade.getTradeInDateTime().before(intervalEndTime)) {
                fetchBarStats(trade, barNum);
            } else {
                printBarOutput();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                barNum++;
                intervalStartTime = addSeconds(intervalEndTime, 1);
                intervalEndTime = addSeconds(intervalStartTime, 15);
                OHLC ohlc = fetchBarStats(trade, barNum);
            }
        }
        count++;
    }

    public static void printBarOutput() {
        if (Common.barOutput != null && Common.barOutput.size() > 0) {
            List<OHLC> result = Common.barOutput.stream().filter(i -> i.bar_num == barNum).collect(Collectors.toList());
            ;
            if (result != null) {
                System.out.println("############### BAR = " + barNum + " ##################");
                for (OHLC o : result) {
                    System.out.println(" Name : " + o.symbol + " Open : " + o.open + " High : " + o.high + " Low : " + o.low + " Volume : " + o.volume + " Trade Time : " + o.tradePrice + " Trade In Time : " + o.getTradeInTime().toString());
                }
            }
            Common.barOutput.removeIf(i -> i.bar_num == barNum);
        }
    }

    public OHLC fetchBarStats(Trade trade, int bar_num) {
        OHLC ohlc = new OHLC();
        try {
            if (previousOhlc == null || previousOhlc.bar_num != bar_num) {
                ohlc.open = ohlc.high = ohlc.low = trade.getP();
                ohlc.close = Double.valueOf(0);
                ohlc.symbol = trade.getSym();
                ohlc.volume = trade.getQ();
                ohlc.tradeInTime = trade.getTradeInDateTime();
                ohlc.bar_num = bar_num;
                ohlc.tradePrice = trade.getP();
                previousOhlc = ohlc;
                previousStockName = trade.getSym();
                ohlc.bar_num = bar_num;
                Common.barOutput.add(ohlc);
            } else {
                if (!previousStockName.equals(trade.getSym())) {
                    previousOhlc = Common.barOutput.stream().filter(w -> w.symbol == trade.getSym()).findFirst().get();
                    if (previousOhlc == null) {
                        ohlc.open = ohlc.high = ohlc.low = trade.getP();
                        ohlc.close = Double.valueOf(0);
                        ohlc.symbol = trade.getSym();
                        ohlc.volume = trade.getQ();
                        ohlc.tradeInTime = trade.getTradeInDateTime();
                        ohlc.bar_num = bar_num;
                        ohlc.tradePrice = trade.getP();
                        previousOhlc = ohlc;
                        previousStockName = trade.getSym();
                        ohlc.bar_num = bar_num;
                        Common.barOutput.add(ohlc);
                    }
                } else {
                    OHLC stockDetail = Common.barOutput.stream().filter(w -> w.symbol == previousStockName).findFirst().get();
                    stockDetail.low = trade.getP() < previousOhlc.low ? trade.getP() : previousOhlc.low;
                    stockDetail.high = trade.getP() > previousOhlc.high ? trade.getP() : previousOhlc.high;
                    stockDetail.volume = ohlc.volume + trade.getQ();
                    stockDetail.symbol = trade.getSym();
                    stockDetail.tradeInTime = trade.getTradeInDateTime();
                    stockDetail.bar_num = bar_num;
                    stockDetail.tradePrice = trade.getP();
                    previousOhlc = stockDetail;
                    previousStockName = trade.getSym();
                }
            }
        } catch (Exception ex) {
            System.out.println("Error occurred in ohlc processing" + ex.getMessage());
        }
        return ohlc;
    }
}
