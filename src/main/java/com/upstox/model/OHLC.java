package com.upstox.model;

import lombok.Data;

import java.util.Date;

@Data
public class OHLC {

    public Double open;
    public Double high;
    public Double low;
    public Double close;
    public Double volume;
    public String symbol;
    public int bar_num;
    public Date tradeInTime;
    public Double tradePrice;
}
