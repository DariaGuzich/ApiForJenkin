package metrics;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class InfluxDBMetrics {
    private static final String INFLUXDB_URL = System.getProperty("influxdb.url", "http://localhost:8086");
    private static final String INFLUXDB_TOKEN = System.getProperty("influxdb.token", "my-super-secret-auth-token");
    private static final String INFLUXDB_ORG = System.getProperty("influxdb.org", "test-org");
    private static final String INFLUXDB_BUCKET = System.getProperty("influxdb.bucket", "test-results");
    
    private final InfluxDBClient client;
    private final WriteApiBlocking writeApi;
    
    public InfluxDBMetrics() {
        this.client = InfluxDBClientFactory.create(INFLUXDB_URL, INFLUXDB_TOKEN.toCharArray(), INFLUXDB_ORG, INFLUXDB_BUCKET);
        this.writeApi = client.getWriteApiBlocking();
    }
    
    public void sendTestResults(String surefireReportsPath) {
        try {
            File reportsDir = new File(surefireReportsPath);
            if (!reportsDir.exists() || !reportsDir.isDirectory()) {
                System.err.println("Surefire reports directory not found: " + surefireReportsPath);
                return;
            }
            
            File[] xmlFiles = reportsDir.listFiles((dir, name) -> name.startsWith("TEST-") && name.endsWith(".xml"));
            if (xmlFiles == null || xmlFiles.length == 0) {
                System.err.println("No test result XML files found in: " + surefireReportsPath);
                return;
            }
            
            for (File xmlFile : xmlFiles) {
                parseAndSendTestResults(xmlFile);
            }
            
            System.out.println("Test results sent to InfluxDB successfully");
            
        } catch (Exception e) {
            System.err.println("Error sending test results to InfluxDB: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void parseAndSendTestResults(File xmlFile) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        
        Element testSuite = document.getDocumentElement();
        String suiteName = testSuite.getAttribute("name");
        int tests = Integer.parseInt(testSuite.getAttribute("tests"));
        int failures = Integer.parseInt(testSuite.getAttribute("failures"));
        int errors = Integer.parseInt(testSuite.getAttribute("errors"));
        int skipped = Integer.parseInt(testSuite.getAttribute("skipped"));
        double time = Double.parseDouble(testSuite.getAttribute("time"));
        
        String buildNumber = System.getProperty("BUILD_NUMBER", "unknown");
        String jobName = System.getProperty("JOB_NAME", "local");
        Instant timestamp = Instant.now();
        
        // Send test suite summary
        Point suitePoint = Point.measurement("test_suite")
                .addTag("suite_name", suiteName)
                .addTag("job_name", jobName)
                .addTag("build_number", buildNumber)
                .addField("total_tests", tests)
                .addField("passed_tests", tests - failures - errors - skipped)
                .addField("failed_tests", failures)
                .addField("error_tests", errors)
                .addField("skipped_tests", skipped)
                .addField("execution_time", time)
                .addField("success_rate", tests > 0 ? (double)(tests - failures - errors) / tests * 100 : 0.0)
                .time(timestamp, WritePrecision.NS);
        
        writeApi.writePoint(suitePoint);
        
        // Send individual test case results
        NodeList testCases = testSuite.getElementsByTagName("testcase");
        for (int i = 0; i < testCases.getLength(); i++) {
            Element testCase = (Element) testCases.item(i);
            String testName = testCase.getAttribute("name");
            String className = testCase.getAttribute("classname");
            double testTime = Double.parseDouble(testCase.getAttribute("time"));
            
            String status = "PASSED";
            String errorMessage = "";
            String errorType = "";
            
            NodeList failures1 = testCase.getElementsByTagName("failure");
            NodeList errors1 = testCase.getElementsByTagName("error");
            NodeList skipped1 = testCase.getElementsByTagName("skipped");
            
            if (failures1.getLength() > 0) {
                status = "FAILED";
                Element failure = (Element) failures1.item(0);
                errorType = failure.getAttribute("type");
                errorMessage = failure.getAttribute("message");
            } else if (errors1.getLength() > 0) {
                status = "ERROR";
                Element error = (Element) errors1.item(0);
                errorType = error.getAttribute("type");
                errorMessage = error.getAttribute("message");
            } else if (skipped1.getLength() > 0) {
                status = "SKIPPED";
            }
            
            Point testPoint = Point.measurement("test_case")
                    .addTag("test_name", testName)
                    .addTag("class_name", className)
                    .addTag("suite_name", suiteName)
                    .addTag("job_name", jobName)
                    .addTag("build_number", buildNumber)
                    .addTag("status", status)
                    .addTag("error_type", errorType)
                    .addField("execution_time", testTime)
                    .addField("error_message", errorMessage)
                    .time(timestamp, WritePrecision.NS);
            
            writeApi.writePoint(testPoint);
        }
    }
    
    public void close() {
        if (client != null) {
            client.close();
        }
    }
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java metrics.InfluxDBMetrics <surefire-reports-path>");
            System.exit(1);
        }
        
        InfluxDBMetrics metrics = new InfluxDBMetrics();
        try {
            metrics.sendTestResults(args[0]);
        } finally {
            metrics.close();
        }
    }
}