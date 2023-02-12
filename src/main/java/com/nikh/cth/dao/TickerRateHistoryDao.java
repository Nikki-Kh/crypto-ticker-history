package com.nikh.cth.dao;

import com.nikh.cth.bean.request.TickerRateRequest;
import com.nikh.cth.bean.ticker.TickerRate;
import com.nikh.cth.utils.SortOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TickerRateHistoryDao {

    String GET_LAST_TICKER_RATES = """
            <script>
            SELECT th1.* FROM ticker_rate_history th1,
            (SELECT brk_id, ticker_name, max(created_when) as created_when
             FROM ticker_rate_history
             GROUP BY brk_id, ticker_name) ltd
            WHERE th1.brk_id = ltd.brk_id
            AND th1.created_when = ltd.created_when
            AND th1.ticker_name = ltd.ticker_name
            <if test = "brkId != null">
                AND th1.brk_id = #{brkId}
            </if>
            ORDER BY th1.brk_id, th1.ticker_name
            </script>
            """;



    String GET_TICKER_RATE_HISTORY_BY_DATES_SQL = """
            <script>
            SELECT * from ticker_rate_history 
            WHERE brk_id = #{request.brkId}
            AND ticker_name = #{request.tickerName}
            AND created_when >= #{request.startDate}
            AND #{request.endDate} > created_when 
            ORDER BY created_when
            <if test = "order == 'desc'">
                DESC
            </if>  
            </script>
            """;


    String INSERT_NEW_TICKER_RATES_SQL = """    
            <script>
            INSERT INTO ticker_rate_history
            (brk_id, ticker_name, ticker_rate)
            VALUES
            <foreach collection="tRates" item = "tRate" index="index" open="(" separator="),("  close=")">
                #{tRate.brkId},
                #{tRate.tickerName},
                #{tRate.value}
            </foreach>
            </script>
            """;

    @Select(GET_LAST_TICKER_RATES)
    List<TickerRate> getLastTickerRates(@Param("brkId") Integer brkId);

    @Select(GET_TICKER_RATE_HISTORY_BY_DATES_SQL)
    List<TickerRate> getTickerHistory(@Param("request") TickerRateRequest request, @Param("order") @SortOrder String order);

    @Insert(INSERT_NEW_TICKER_RATES_SQL)
    int insertNewTickerRates(@Param("tRates") List<TickerRate> tickerRates);
}
