# Temperature And Humidity Monitoring System
This is a project that combines low cost hardware and cloud technologies to establish an ambience environment monitoring system, and provide an easy and accessible 
human computer interface.

## Architecture

### Overview

![architecture](https://github.com/shuyuan6/TemperatureAndHumidity/blob/master/architecture.png?raw=true)


### Data generation side

A cost efficient Raspberry Pi 4 runs at the enviornment that needs monitoring, with an attached DHT22 temperature and humidity sensor. A Python service running on the Raspberry Pi periodically reads sensor data and sends the data to AWS DynamoDB for storage.

### Data ingestion side

A pre-created table living on AWS DynamoDB receives writes from data generation side. Meanwhile, AWS Cloudwatch alarms are also set up to monitor that writes are constantly flowing in. If for some reason the data stops flowing in, an alarm will be triggered and an email would be sent to the administrator.

### Data displaying and analysis side
A webservice, either hosted on administrator's desktop computer, or AWS EC2 for external normal user, is set up. The web service renders the data with HTML in both tabular and graph based format, showing max, min, average, median temperature in a history window with configurable length.

## Technology
Java, Python, web service built with raw sockets.

## Data storage schema
Here is a sneak peek on the data schema used by the aforementioned DynamoDB table.
![ddb](https://github.com/shuyuan6/TemperatureAndHumidity/blob/master/ddb_table.png?raw=true)


## Future work
The most fun part of the project, aside from engineering challenges arising from building it, is to provide various creative ways for end users to analyze data and gain insignts from the data. Perhaps it can even serve as the prototype of a commerical temperature monitor system.
