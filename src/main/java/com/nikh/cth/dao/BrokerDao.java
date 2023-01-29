package com.nikh.cth.dao;

import com.nikh.cth.bean.broker.Broker;
import com.nikh.cth.bean.broker.BrokerToTickerDBEntry;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BrokerDao {

    String GET_BROKERS_SQL = """
        SELECT * FROM brokers;
    """;

    String GET_TICKERS_BY_BROKER_SQL = """
        SELECT * FROM broker_tickers
        WHERE brk_id = #{brkId}                
    """;

    String GET_ALL_TICKERS_SQL = """
        SELECT brk_id, string_agg(ticker_name, ' ') as ticker_array_str FROM broker_tickers
        GROUP BY brk_id               
    """;

    @Select(GET_BROKERS_SQL)
    List<Broker> getBrokers();
    @Select(GET_TICKERS_BY_BROKER_SQL)
    List<String> getBrokerTickers(@Param("brkId") Integer brkId);

    @Select(GET_ALL_TICKERS_SQL)
    @Results({
            @Result(column = "brk_id", property = "brkId"),
            @Result(column = "ticker_array_str", property = "tickerArrayStr")
    }
    )
    List<BrokerToTickerDBEntry> getAllTickers();
}
