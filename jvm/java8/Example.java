import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Example {
    public static void main(String[] args) throws Exception {
        Example app = new Example();
        String apiKey = "";
        List<Job> jobs = new ArrayList<Job>();

        jobs.add(app.createJob("ID_WERTPAPIER", "851399"));

        List<JobResult> jobResults = app.mapJobs(apiKey, jobs);

        // System.out.println(listJobResultsToString(jobResults));
    }

    private static void print(String s) {
        System.out.println(s);
    }

    private Job createJob(String idType, String idValue) {
        return new Job(idType, idValue);
    }

    private static String join(String sep, List<String> list) {
        if (list.size() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder(list.get(0));

        for(int i = 1, len = list.size(); i < len; i++) {
            sb.append(sep).append(list.get(i));
        }

        return sb.toString();
    }

    private static String listJobsToJson(List<Job> jobs) {
        List<String> jobsJson = new ArrayList<String>();

        for (Job job : jobs) {
            jobsJson.add(job.toJsonObject());
        }

        return "[" + join(",", jobsJson) + "]";
    }

    private List<JobResult> responseJsonToListJobResult(String json) throws Exception {
        if (!json.startsWith("[")) {
            throw new Exception(json);
        }

        String[] jobResultStrs = json
            .replaceAll("(^.*?\\[|\\].*?$)", "")
            .replaceAll("\\},\\{", ",,")
            .split(",,");
        List<JobResult> jobResults = new ArrayList<JobResult>();

        for (String jobResultStr : jobResultStrs) {
            jobResults.add(new JobResult(jobResultStr));
        }

        return jobResults;
    }

    private static String listJobResultsToString(List<JobResult> jobResults) {
        List<String> jobResultStrs = new ArrayList<String>();

        for (JobResult jobResult : jobResults) {
            jobResultStrs.add(jobResult.toString());
        }

        return join("\n", jobResultStrs);
    }

    private List<JobResult> mapJobs(String apiKey, List<Job> jobs) throws Exception {
        String url = "https://api.openfigi.com/v1/mapping";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type","application/json");

        if (apiKey != null && apiKey != "") {
            con.setRequestProperty("X-OPENFIGI-APIKEY", apiKey);
        }

        String postJsonData = listJobsToJson(jobs);

        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postJsonData);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();

        String responseJson = response.toString();

        return responseJsonToListJobResult(responseJson);
    }

    /* Job class */

    public class Job {
        private String idType, idValue, exchCode, micCode, currency,
            marketSecDes;

        public Job(String idType, String idValue) {
            this.idType = idType;
            this.idValue = idValue;
            this.exchCode = null;
            this.micCode = null;
            this.currency = null;
            this.marketSecDes = null;
        }

        public Job exchCode(String exchCode) {
            this.exchCode = exchCode;
            return this;
        }

        public Job micCode(String micCode) {
            this.micCode = micCode;
            return this;
        }

        public Job currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Job marketSecDes(String marketSecDes) {
            this.marketSecDes = marketSecDes;
            return this;
        }

        public String toJsonObject() {
            StringBuilder jsonSb = new StringBuilder("{");

            jsonSb.append(jsonKeyValuePair("idType", this.idType))
                .append(",")
                .append(jsonKeyValuePair("idValue", this.idValue));

            if (this.exchCode != null) {
                jsonSb.append(",").append(jsonKeyValuePair("exchCode", this.exchCode));
            }

            if (this.micCode != null) {
                jsonSb.append(",").append(jsonKeyValuePair("micCode", this.micCode));
            }

            if (this.currency != null) {
                jsonSb.append(",").append(jsonKeyValuePair("currency", this.currency));
            }

            if (this.marketSecDes != null) {
                jsonSb.append(",").append(jsonKeyValuePair("marketSecDes", this.marketSecDes));
            }

            return jsonSb.append("}").toString();
        }

        private String jsonKeyValuePair(String key, String value) {
            return "\"" + key + "\":\"" + value + "\"";
        }
    }

    /* Figi class */

    class Figi {
        public String figi, securityType, marketSector, ticker, name, uniqueID,
            exchCode, shareClassFIGI, compositeFIGI, securityType2,
            securityDescription, uniqueIDFutOpt;

        public Figi(String json) {
            populateFromJsonObj(json);
        }

        private Figi populateFromJsonObj(String json) {
            Map<String, String> keyValuePairs = jsonObjToKeyValuePairs(json);

            for (Map.Entry<String, String> entry : keyValuePairs.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (value == "null") {
                    continue;
                }

                switch(key) {
                    case "figi":
                        this.figi = value; break;
                    case "securityType":
                        this.securityType = value; break;
                    case "marketSector":
                        this.marketSector = value; break;
                    case "ticker":
                        this.ticker = value; break;
                    case "name":
                        this.name = value; break;
                    case "uniqueID":
                        this.uniqueID = value; break;
                    case "exchCode":
                        this.exchCode = value; break;
                    case "shareClassFIGI":
                        this.shareClassFIGI = value; break;
                    case "compositeFIGI":
                        this.compositeFIGI = value; break;
                    case "securityType2":
                        this.securityType2 = value; break;
                    case "securityDescription":
                        this.securityDescription = value; break;
                    case "uniqueIDFutOpt":
                        this.uniqueIDFutOpt = value; break;
                }
            }

            return this;
        }

        private Map<String, String> jsonObjToKeyValuePairs(String json) {
            String[] pairStrs = json.replaceAll("(^.*?\\{|\\}.*?$)", "").split(",");
            Map<String, String> map = new HashMap<String, String>();

            for (String pairStr : pairStrs) {
                String[] kvp = pairStr.split(":");
                map.put(
                    kvp[0].replaceAll("(^.*?\\\"|\\\".*?$)", ""),
                    kvp[1].replaceAll("(^.*?\\\"|\\\".*?$)", "")
                );
            }

            return map;
        }

        public String toString() {
            return (new StringBuilder())
                .append("figi: ").append(this.figi).append("\n")
                .append("securityType: ").append(this.securityType).append("\n")
                .append("marketSector: ").append(this.marketSector).append("\n")
                .append("ticker: ").append(this.ticker).append("\n")
                .append("name: ").append(this.name).append("\n")
                .append("uniqueID: ").append(this.uniqueID).append("\n")
                .append("exchCode: ").append(this.exchCode).append("\n")
                .append("shareClassFIGI: ").append(this.shareClassFIGI).append("\n")
                .append("compositeFIGI: ").append(this.compositeFIGI).append("\n")
                .append("securityType2: ").append(this.securityType2).append("\n")
                .append("securityDescription: ").append(this.securityDescription).append("\n")
                .append("uniqueIDFutOpt: ").append(this.uniqueIDFutOpt)
                .toString();
        }
    }

    /* JobResult Class */

    class JobResult {
        public String error;
        public List<Figi> figis;

        public JobResult(String json) {
            print(json);
            populateFromJsonObj(json);
        }

        private JobResult populateFromJsonObj(String json) {
            String str = json.replaceAll("(^.*?\\{.*?\\\"|\\}.*?$)", "");

            if (str.startsWith("data")) {
                String[] figiJsonObjs = str.replaceAll("(data\\\".*?\\[|\\].*?$)", "").split(",");
                figis = new ArrayList<Figi>();

                for (String figiJsonObj : figiJsonObjs) {
                    figis.add(new Figi(figiJsonObj));
                }
            } else {
                error = str.replaceAll("(error\\\".*?\\\"|\\\".*?$)", "");
            }

            return this;
        }

        public String toString() {
            if (error != null) {
                return error;
            }

            List<String> figiStrs = new ArrayList<String>();

            for (Figi figi : figis) {
                figiStrs.add(figi.toString());
            }

            return join("\n", figiStrs);
        }
    }
}
