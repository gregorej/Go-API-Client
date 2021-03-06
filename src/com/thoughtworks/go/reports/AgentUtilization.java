package com.thoughtworks.go.reports;

import com.thoughtworks.go.TalkToGo;
import com.thoughtworks.go.domain.Job;
import com.thoughtworks.go.domain.Pipeline;
import com.thoughtworks.go.domain.Stage;
import com.thoughtworks.go.http.HttpClientWrapper;
import com.thoughtworks.go.latest.TalkToGoLatest;
import com.thoughtworks.go.visitor.StageVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentUtilization {

    public static void main(String[] args) {
        HttpClientWrapper wrapper = new HttpClientWrapper("blrstdcrspair03.thoughtworks.com", 8153, "admin", "badger");
        TalkToGo talkToGo = new TalkToGoLatest("pair02", wrapper, false) {
        };
        final Map<String, List<Job>> agentToJobs = new HashMap<String, List<Job>>();
        final Stage[] lastStage = new Stage[1];
        talkToGo.visitAllStages(new StageVisitor() {
            public void visitStage(Stage stage) {
                for (Job job : stage.getJobs()) {
                    String uuid = job.getUUID();
                    List<Job> jobs = agentToJobs.get(uuid);
                    if (jobs == null) {
                        agentToJobs.put(uuid, jobs = new ArrayList<Job>());
                    }
                    jobs.add(job);
                }
                lastStage[0] = stage;
            }

            public void visitPipeline(Pipeline pipeline) {
            }
        });
        System.out.println("***********************\n\n\n");
        for (String uuid : agentToJobs.keySet()) {
            long totalTime = 0;
            String hostname = "<unknown>";
            for (Job job : agentToJobs.get(uuid)) {
                totalTime += job.timeSpentOnAgent();
                hostname = job.getProperty("cruise_agent");
            }
            System.out.println(String.format("Agent with UUID '%s' at hostname '%s' spent a total of '%s' seconds since %s", uuid, hostname, totalTime, lastStage[0].getLastUpdated()));
        }
    }
}
