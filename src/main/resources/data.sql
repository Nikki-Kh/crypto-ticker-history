INSERT INTO users (username, password) values
('admin', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6');

INSERT INTO brokers (brk_name, API_addr, upd_interval) values
('Kraken', 'https://api.kraken.com', 30),
('Kucoin', 'https://api.kucoin.com', 30);

INSERT INTO broker_tickers (brk_id, ticker_name) values
(1, 'XBTUSD'),
(1, 'XETHZUSD'),
(2, 'ETH-USDT'),
(2, 'BTC-USDT');