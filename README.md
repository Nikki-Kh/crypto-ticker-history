# Crypto-ticker-history Spring Boot Application

## Description
### General Description
This is a simple (Java 17 / Maven 3 / Spring Boot 2) application interacting with a pair of crypto brokers: Kraken and Kucoin.
The app recurrently pull up-to-date ticker rate data from those brokers and store it in a database.
The Various REST endpoints are provided to operate with existing and collected data such as:
1) **get information about brokers and tickers;**
2) **get ticker rates' history;**
3) **get last ticker rates wih ability to specify broker;** 
4) **get interval data for desired time window.**

On a startup application perform 2 important steps:
1) Collect descriptive information about brokers and tracked tickers into cache,
   since this kind of information won't be updated often and can be stored in-memory to speed up data retrieval.
2) `TickerRateUpdateTaskDealer` component get information about brokers and register recurrent tasks of type
`TickerRateUpdateTask` to connect to brokers' API and get the latest ticker rates.
Update period is specified in DB and is represented by `updInterval` field.
Once data is collected with the help of `WebClient`, it is then stored in DB.

### Database
For demonstration purpose H2 embedded in-memory database is used.
This DB contains four tables:
1) users - user data for auth. Only holds pair **admin/password**.
2) brokers - list of supported brokers.
3) broker_tickers - list of pairs **broker/ticker**, defining **tickers** observed at **broker**.
4) ticker_rate_history - list of values for **ticker** at **broker** at specific time.

During start up DB schema and initial data are loaded from _schema.sql_ and _data.sql_ files, located at
`src/main/resources`.
You can browse DB with your browser during runtime via special endpoint ```/h2-console```.
Access to the page is granted without auth, however, to access db UI credential pair `sa/password` should be used.


## Requirements

1) JDK 17
2) Maven 3
3) Docker (optional)

## How to Run

Pull application from [Git](https://github.com/Nikki-Kh/crypto-ticker-history.git) 
### Option 1: Launch from IDE
1) Open project in preferred IDE: IntelliJ IDEA, NetBeans, Eclipse etc.
2) Navigate to class: `src/main/java/com/nikh/cth/CryptoTickerHistoryApplication.java`
3) Press `run` button near class declaration or main function

### Option 2: Launch with maven spring-boot plugin
1) Open preferred command line tool
2) Navigate to project root directory
3) Run command `mvn spring-boot run`

### Option 3: Launch with Docker
0) Make sure, that Docker daemon is running
1) Open preferred command line tool
2) Navigate to project root directory
3) run command `docker build --tag cth . `
4) run command `docker run -p8081:8081 cth`


Application will start and will be accessible by address [localhost:8081](localhost:8081)

## Endpoints

### Auth
#### /auth
Description: A Basic endpoint for user authentication
Accepts a pair of credentials and returns standard jwt token with setup expiration period of 1h.
```
POST /auth
Accept: application/json
Content-Type: application/json

Request:
{
    "name" : "admin",
    "password" : "password"
}

Response:
{
    "token": "your_generated_token"
}
```

### Brokers
#### /brokers
Description: endpoint to get information about supported brokers.
To get access to this endpoint Auth token should be provided.
```
GET /brokers

Authorization: Bearer your_generated_token

Response: 
[
    {
        "brkId": 1,
        "brkName": "broker_1",
        "updInterval": 10, //seconds
        "createdWhen": "2023-02-13T20:00:00"
        "updWhen": "2023-02-13T20:00:00"
    },
    ...
]
```
#### /brokers/{id}
Description: an extension of previous endpoint.
Provide **brkId** as path variable to get information about tracked tickers for the selected broker.
To get access to this endpoint Auth token should be provided.
```
GET /brokers/1

Authorization: Bearer your_generated_token

Response: 
[
    "ticker_1",
    "ticker_2",
    ...
]
```
### Tickers

#### /tickers/rates?brkId=
Description: An endpoint to receive last ticker rates.
With optional parameter **brkId** last rates can be retrieved for a selected broker instead of whole list of brokers.
To get access to this endpoint Auth token should be provided.
```
GET /tickers/rates?brkId=

Authorization: Bearer your_generated_token

Response: 
{
    "1": [
            {
                "brkId": 1,
                "tickerName": "ticker_1",
                "value": 10.01,
                "createdWhen": "2023-02-13T20:00:00"
            },
         ...
         ],
    "2": [
            {
                "brkId": 2,
                "tickerName": "ticker_1",
                "value": 10.02,
                "createdWhen": "2023-02-13T20:00:00"
            },
         ...
         ]     
}
```
#### /history
Description: An endpoint to receive rates history in a time window for chosen pair of broker and ticker.
To get access to this endpoint Auth token should be provided.
```
GET /tickers/rates/history

Authorization: Bearer your_generated_token
Accept: application/json
Content-Type: application/json

Request:
{
    "brkId": 1,
    "tickerName": "ticker_1",
    "startDate": "2023-02-12T17:20:00",
    "endDate": "2023-02-12T17:30:00"
}

Response: 
[
    {
        "brkId": 1,
        "tickerName": "ticker_1",
        "value": 10.01,
        "createdWhen": "2023-02-13T17:27:00"
    },
    {
        "brkId": 1,
        "tickerName": "ticker_1",
        "value": 10.04,
        "createdWhen": "2023-02-13T17:22:00"
    },
    ...
]
```

#### /interval
Description: An endpoint to receive interval data for chosen pair of broker and ticker.
Request field **intervalPeriod** specifies interval window in a format _"NM"_, where _'N'_ - integer, and _'M'_ - 
one of the following letters: _'s'_ - seconds, _'m'_ - minutes, _'h'_ - hours.


In case there is no collected data for an interval, **details** message would be provided instead of desired values.
To get access to this endpoint Auth token should be provided.
```
GET /tickers/rates/interval

Authorization: Bearer your_generated_token
Accept: application/json
Content-Type: application/json

Request:
{
    "brkId": 1,
    "tickerName": "ticker_1",
    "startDate": "2023-02-12T17:20:00",
    "endDate": "2023-02-12T17:30:00",
    "intervalPeriod": "3m"
}

Response: 
[
    {
        "startDate": "2023-02-12T17:20:00",
        "minRate": 1.01,
        "maxRate": 3.03,
        "avgRate": 2.02,
        "details": null
    },
    {
        "startDate": "2023-02-12T17:23:00",
        "minRate": null,
        "maxRate": null,
        "avgRate": null,
        "details": "No data for this interval"
    },
    ...
]
```
