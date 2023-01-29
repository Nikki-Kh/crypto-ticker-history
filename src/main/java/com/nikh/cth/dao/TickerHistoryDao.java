package com.nikh.cth.dao;

import com.nikh.cth.bean.ticker.TickerRate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TickerHistoryDao {

    String GET_TICKER_HISTORY_SQL = """
            SELECT * from ticker_value_history
            WHERE brk_id = #{brkId}
            AND ticker_name = #{tickerName}
            ORDER BY created_when desc
            """;

    String GET_TICKER_HISTORY_BY_DATES_SQL = """
            SELECT * from ticker_value_history
            WHERE brk_id = #{brkId}
            AND ticker_name = #{tickerName}
            AND created_when >= #{startDate}
            AND created_when <= #{endDate}
            ORDER BY created_when asc
            """;

    @Select(GET_TICKER_HISTORY_SQL)
    List<TickerRate> getTickerHistory(@Param("brkId") Integer brkId,
                                      @Param("tickerName") String tickerName);

    @Select(GET_TICKER_HISTORY_BY_DATES_SQL)
    List<TickerRate> getTickerHistory(@Param("brkId") Integer brkId,
                                      @Param("tickerName") String tickerName,
                                      @Param("startDate")LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);
}
