import time
import threading
import boto3
from decimal import Decimal
from datetime import datetime
from boto3.dynamodb.conditions import Key, Attr

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('temp_and_humidity')

now_ts = time.time()
one_hour_ago_ts = time.time() - 3600

response = table.scan(
    ScanFilter={
        'timestamp': {
            'AttributeValueList': [Decimal(one_hour_ago_ts)],
            'ComparisonOperator': 'GE'
        }
    }
)

items = response['Items']
data_list = []
for item in items:
    data_list.append(item)

data_list.sort(key=lambda i: float(i['timestamp']))

for item in data_list:
    print(item)
