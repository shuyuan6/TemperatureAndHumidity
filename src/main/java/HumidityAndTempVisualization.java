import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HumidityAndTempVisualization {
    private void retrieveData(int duration, List<Double> timestamps, List<Double> humidityData, List<Double> temperatureData) {

        double ts2 = Instant.now().getEpochSecond();
        double ts1 = ts2 - (duration * 60 * 60);

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-west-2")
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("HumidityTemperatureTest");

        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#tp", "temp");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":deviceID", "1");
        valueMap.put(":TS1", ts1);
        valueMap.put(":TS2", ts2);

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

    public void humidityTempSaveToImage(int duration, String imageName, String path) throws Exception {
        List<Double> timestamps = new ArrayList<>();
        List<Double> humidityData = new ArrayList<>();
        List<Double> temperatureData = new ArrayList<>();

        retrieveData(duration, timestamps, humidityData, temperatureData);
        XYChart chart = new XYChartBuilder().width(1600).height(600).title("Temperature and Humidity")
                .xAxisTitle("Timestamp").yAxisTitle("Record").build();

        // Customize Chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setAxisTitlesVisible(false);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Step);

        double[] tss = new double[timestamps.size()];
        double[] temps = new double[temperatureData.size()];
        double[] humds = new double[humidityData.size()];

        for (int i = 0; i < tss.length; i++) {
            tss[i] = timestamps.get(i);
            temps[i] = temperatureData.get(i);
            humds[i] = humidityData.get(i);
        }
        // Series
        chart.addSeries("Temperature", tss, temps);
        chart.addSeries("Humidity", tss, humds);

        //new SwingWrapper<XYChart>(chart).displayChart();
        BitmapEncoder.saveBitmapWithDPI(chart, path + imageName, BitmapEncoder.BitmapFormat.PNG, 300);

    }

    public String humidityTempToHtml(int duration) throws Exception {
        List<Double> timestamps = new ArrayList<>();
        List<Double> humidityData = new ArrayList<>();
        List<Double> temperatureData = new ArrayList<>();

        retrieveData(duration, timestamps, humidityData, temperatureData);
        StringBuffer sb = new StringBuffer();
        sb.append("<h1>Temperature and Humidity</h1><table><tr><th>Time</th><th>Temperature</th><th>Humidity</th></tr>");
        for (int i = 0; i < timestamps.size(); i++) {
            long epoch = timestamps.get(i).longValue();
            Instant instant = Instant.ofEpochSecond(epoch);
            String time = ZonedDateTime.ofInstant(instant, ZoneOffset.ofHours(-7)).toString();
            sb.append("<tr><td>" + time + "</td><td>" + String.format("%.2f", temperatureData.get(i)) + "</td><td>" + String.format("%.2f", humidityData.get(i)) + "</td></tr>");
        }

        sb.append("</table>");

        return sb.toString();

    }
}

