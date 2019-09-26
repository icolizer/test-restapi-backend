# About project:

Java 10 and higher
Use micronaut framework with micronaut-data 
H2 mem as Database

Build into uberJar and run on port 8080
Produces JSON for every endpoint

# Build and Run application:

$cd ~/path/to/src/
$/.gradlew build
$java -jar ~/path/to/src/build/libs/app-0.1.jar

# Endpoints:

POST http://host:8080/v1/accounts Create account
PUT http://host:8080/v1/accounts Update account data
DELETE http://host:8080/v1/accounts Delete account entity
GET http://host:8080/v1/accounts Return all accounts
GET parametrarized http://host:8080/v1/accounts{?max,offset,order} Will return pagable result as array of an objects, all parameters is Optional 
GET http://host:8080/v1/accounts/{id} Will return concrete account by it's Id
POST http://host:8080/v1/transfers Create new transfer and update account balances
DELETE http://host:8080/v1/transfers Delete transfer by id
GET http://host:8080/v1/transfers Return all transfers
GET parametrarized http://host:8080/v1/transfers{?max,offset,order} Will return pagable result as array of an objects, all parameters is Optional 
GET http://host:8080/v1/transfers/{id} Return transfer by it's UUID

# Samples run CURL requests:


Accounts:

Create Account, POST method:
curl -i -X POST -d '{"currency":"USD","balance":"10.01","userId":500000}' -H 'Content-Type: application/json' http://localhost:8080/v1/accounts
Answer: 
{"id":1,"userId":500000,"currency":"USD","balance":10.01,"creationDate":[2019,9,26,8,58,42,593426000],"updateDate":[2019,9,26,8,58,42,593426000]}
Will also contains Location header which guide to GET by id request

Get account by Id:
curl -i -X GET http://localhost:8080/v1/accounts/2
Answer:
{"id":2,"userId":500001,"currency":"USD","balance":10.01,"creationDate":[2019,9,26,8,58,42,593426000],"updateDate":[2019,9,26,8,58,42,593426000]}

Get asc limit 2 accounts
curl -i -X GET http://localhost:8080/v1/accounts?max=2
Answer will be array if data is presented in natural order

Get desc limit 2 accounts:
curl -i -X GET "http://localhost:8080/v1/accounts?offset=0&max=2&order=desc"

Put request will update account by it's Id:
curl -i -X PUT -d '{"id":"1","currency":"RUB","userId":"500002","balance":"10"}' -H 'Content-Type: application/json' "http://localhost:8080/v1/accounts"
Response will be updated value:
{"id":1,"userId":500002,"currency":"RUB","balance":10,"creationDate":[2019,9,26,9,51,1,383000000],"updateDate":[2019,9,26,9,51,1,383000000]}

Delete method need only Id in it's body:
curl -i -X DELETE -d '{"id":3}' -H 'Content-Type: application/json' "http://localhost:8080/v1/accounts"
After execute will only return HttpStatus when 200 that means entry was delete otherwise 400 will be provided

Transfers:

Transfers endpoint works with GET methods the same as accounts endpoint
To create to transfer and transfer balances between accounts use POST method
curl -i -X POST -d '{"accountFrom":1,"accountTo":2,"rate":"0.12","amount":"5"}' -H 'Content-Type: application/json' "http://localhost:8080/v1/transfers"
On success HttpStatus is 201
Rate field is use to recalculate amount for it's exchange rate
Anwser of POST request will be bnewly created Transfer entry and updated balances of accounts:
 {"id":"0176e2dc-51a8-4c69-8ccb-18644abd9ee5",
    "accountFrom":{"id":1,"userId":500002,"currency":"RUB","balance":5,"creationDate":[2019,9,26,9,51,1,383000000],"updateDate":[2019,9,26,9,51,1,383000000]},
    "accountTo":{"id":2,"userId":500001,"currency":"USD","balance":10.600000,"creationDate":[2019,9,26,9,51,7,439000000],"updateDate":[2019,9,26,9,51,7,439000000]},
"rate":0.12,"amount":5,"currencyFrom":"RUB","currencyTo":"USD","date":[2019,9,26,11,58,39,205110000]}

Also Delete method is available for that endpoint by transfers UUID