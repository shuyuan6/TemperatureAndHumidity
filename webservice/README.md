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

## How it works
The webservice, after being launched, will be listening to port `80`. For each incoming TCP connection, it will fire up a new thread and execute code inside `ClientHandlerTask`. It would assume that the data flowing in will conform to a simple HTTP protocal (i.e. GET request with a path like `/1000.html`, specifying how many hours of data need to be pulled). It will then use AWS DynamoDB client to read back enough data, visualize the data into a PNG file and put it at `/tmp/`. It will then render a simple HTML webpage containing an image link. On receiving the HTML website, the browser would send a subsequent request to obtain the image which the webservice send the image back to the browser.
