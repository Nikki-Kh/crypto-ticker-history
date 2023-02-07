INSERT INTO brokers (brk_name, API_addr, upd_interval) values 
('Kraken', 'http://', 5),
('Kucoin', 'http://', 5);

INSERT INTO broker_tickers (brk_id, ticker_name) values 
(1, 'test1'),
(1, 'test2'),
(2, 'test1'),
(2, 'test2');