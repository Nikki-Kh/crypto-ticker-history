package com.nikh.cth.dao;

import com.nikh.cth.bean.request.TickerRateRequest;
import com.nikh.cth.bean.ticker.TickerRate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
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
            AND th1.brk_id = ltd.brk_id
            <if test = "#{brkId} != null">
                AND th1.brk_id = #{brkId}
            </if>
            ORDER BY th1.brk_id, th1.ticker_name
            </script>
            """;



    String GET_TICKER_RATE_HISTORY_BY_DATES_SQL = """
            SELECT * from ticker_rate_history
            WHERE brk_id = #{request.brkId}
            AND ticker_name = #{request.tickerName}
            AND created_when >= #{request.startDate}
            AND created_when <= #{request.endDate}
            ORDER BY created_when asc
            """;

    @Select(GET_LAST_TICKER_RATES)
    List<TickerRate> getLastTickerRates(@Param("brkId") Integer brkId);

    @Select(GET_TICKER_RATE_HISTORY_BY_DATES_SQL)
    List<TickerRate> getTickerHistory(@Param("request") TickerRateRequest request);
}
