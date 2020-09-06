import com.upstox.CalculateOHLC;
import com.upstox.model.OHLC;
import com.upstox.model.Trade;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

public class OHLCTest {

    Trade trade;
    OHLC ohlc ;
    CalculateOHLC compute;

    @Before
    public void setUp() throws Exception {
        trade = new Trade();
        trade.setSym("XZECXXBT");
        trade.setP(0.01947);
        trade.setQ(0.1);
        trade.setTS2(1538409725339216503L);

        ohlc = new OHLC();
        ohlc.setBar_num(1);
        ohlc.setOpen(0.01947);
        ohlc.setHigh(0.01947);
        ohlc.setLow(0.01947);
        ohlc.setClose(0.0);
        ohlc.setSymbol("XZECXXBT");
        ohlc.setTradeInTime(new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse("Mon Oct 01 21:32:05 IST 2018"));
        ohlc.setTradePrice(0.01947);
        ohlc.setVolume(0.1);
        compute = new CalculateOHLC();

    }

    @Test
    public void testComputeOHLC(){
        assertEquals(ohlc,compute.fetchBarStats(trade,1));
    }
}
