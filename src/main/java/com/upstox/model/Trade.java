package com.upstox.model;

import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
public class Trade {

    private String sym;
    private Double P;
    private Double Q;
    private Long TS2;

    public Date getTradeInDateTime()  {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formats = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        long nanoseconds = TS2;
        LocalDateTime ldt = Instant.ofEpochMilli(nanoseconds/1000000)
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        Date date = null;
        try {
            date = format.parse(ldt.format(formats));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;

    }
}
