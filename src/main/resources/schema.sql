CREATE TABLE brokers (
    brk_id INTEGER NOT NULL AUTO_INCREMENT,
    brk_name VARCHAR(32) NOT NULL,
    API_addr VARCHAR(32) NOT NULL,
    API_key VARCHAR(32),
    upd_interval INTEGER NOT NULL,
    created_when timestamp default now(),
    updated_when timestamp default now(),
    PRIMARY KEY (brk_id),
    UNIQUE (brk_name)
);

CREATE TABLE broker_tickers (
    brk_id INTEGER NOT NULL
    CONSTRAINT broker_tickers_brk_id_FOREIGN_KEY REFERENCES brokers,
    ticker_name VARCHAR(16) NOT NULL,
    created_when timestamp default now(),
    updated_when timestamp default now(),
    PRIMARY KEY (brk_id, ticker_name),
    CONSTRAINT FOREIGN
);

CREATE TABLE ticker_value_history (
    id INTEGER NOT NULL AUTO_INCREMENT,
    brk_id INTEGER NOT NULL
    CONSTRAINT broker_tickers_brk_id_FOREIGN_KEY REFERENCES brokers,
    ticker_name VARCHAR(32) NOT NULL,
    ticker_value NUMERIC NOT NULL,
    created_when timestamp default now(),
    updated_when timestamp default now(),
    PRIMARY KEY (id)
);