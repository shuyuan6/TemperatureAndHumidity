This is maven managed Java project. I built and tested it with JDK 11 but it should work with other version of JDK without much issues.

## Usage
To run the webserver on your local lost, you will use AWS credentials that the DynamoDB client can make use of, for example, you can place a file containing your credentail at `~/.aws/credentials` with the following content.

```
[default]
aws_access_key_id = AKIAQL1234WPCLHMVZ2L
aws_secret_access_key = JCD+yLX4ZsP/LYhf+ApNQllXauk1ngyPuEsrZLEf
```

Then, open your favorite browser and type in an URL, such as
```
http://localhost/1000.html
```

The number `1000` in the above URL means you are interested in view the data in the most recent 1000 hours.
