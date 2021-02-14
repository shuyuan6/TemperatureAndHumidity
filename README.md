# TemperatureAndHumidity
A simple http server to display the temperature and humidity data stored in Dynamo DB.

## Architecture

- Data generation side

A cost efficient Raspberry Pi 4 runs at home, with an attached DHT22 temperature and humidity sensor. A Python service running on the Raspberry Pi periodically reads sensor data and sending the data to AWS DynamoDB for storage.

- Data ingestion side

A pre-created table living on AWS DynamoDB receives writes from data generation side. Meanwhile, AWS Cloudwatch alarms are also set up to monitor that writes are constantly flowing in. If for some reason the data stops flowing in, an alarm will be triggered and an email would be sent to the administrator.

- Data displaying and analysis side
A webservice, either hosted on administrator.

![architecture](https://github.com/shuyuan6/TemperatureAndHumidity/blob/master/architecture.png?raw=true)

## Technology
Java, Python, web service built with raw sockets.
