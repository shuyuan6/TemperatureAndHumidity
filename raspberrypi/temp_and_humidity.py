###################################################################################
#
# @Project: Real time temperator and humidity monitoring and uploading to cloud
# @Description: uses Raspberry Pi 4, DHT22 sensor, AWS DynamoDB
# @Author: Shuyuan Lyu
# @LastUpdate: Aug 10 2020
#
####################################################################################

import Adafruit_DHT
import time
import boto3
from decimal import Decimal

dynamodb = boto3.resource('dynamodb')


table = dynamodb.Table('temp_and_humidity')

print(table.creation_date_time)

DHT_SENSOR = Adafruit_DHT.DHT22
DHT_PIN = 4

while True:
    humidity, temperature = Adafruit_DHT.read_retry(DHT_SENSOR, DHT_PIN)

    if humidity is not None and temperature is not None:
        timestamp = ts = time.time()
        try:
            table.put_item(
                Item={
                    'timestamp': Decimal(str(timestamp)),
                    'temp': Decimal(str(temperature)),
                    'humidity': Decimal(str(humidity))
                }
            )
            pass
        except:
            print("Failed to upload data to cloud")
        print("Time: {0:f}, Temp={1:0.1f}*C  Humidity={2:0.1f}%".format(timestamp, temperature, humidity))
    else:
        print("Failed to retrieve data from humidity sensor")


    time.sleep(10)
