import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.xspec.L;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.simple.JSONArray;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.json.simple.JSONObject;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HumidityAndTempVisualization {

    private void retrieveData(double startTime, double endTime, List<Double> timestamps, List<Double> humidityData, List<Double> temperatureData) {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-west-2")
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("HumidityTemperatureTest");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":deviceID", "1");
        valueMap.put(":TS1", startTime);
        valueMap.put(":TS2", endTime);

        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("DeviceID = :deviceID and TS between :TS1 and :TS2")
                .withValueMap(valueMap);

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator = null;
        Item item = null;

        try {
            items = table.query(querySpec);
            iterator = items.iterator();

            while (iterator.hasNext()) {
                item = iterator.next();
                timestamps.add(item.getDouble("TS"));
                humidityData.add(item.getDouble("humidity"));
                temperatureData.add(item.getDouble("temp"));

                //if (item.getDouble("temp") < 0) {
                //     System.out.println(item);
                //}
            }
        }
        catch (Exception e) {
            System.err.println("Unable to query");
            System.err.println(e.getMessage());
        }
    }

    public String humidityTempToJson(double startTime, double endTime) throws Exception {
        List<Double> timestamps = new ArrayList<>();
        List<Double> humidityData = new ArrayList<>();
        List<Double> temperatureData = new ArrayList<>();

        retrieveData(startTime, endTime, timestamps, humidityData, temperatureData);
        JSONObject obj = new JSONObject();
        obj.put("1", new JSONArray());

        for (int i = 0; i < timestamps.size(); i++) {
            long epoch = timestamps.get(i).longValue();
            Instant instant = Instant.ofEpochSecond(epoch);
            String time = ZonedDateTime.ofInstant(instant, ZoneOffset.ofHours(-7)).toLocalDateTime().toString();
            JSONObject part = new JSONObject();
            part.put("ts", time);
            part.put("temperature", temperatureData.get(i));
            part.put("humidity", humidityData.get(i));
            ((JSONArray)obj.get("1")).add(part);
        }
        return obj.toJSONString();
    }
}

