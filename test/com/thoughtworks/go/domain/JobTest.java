package com.thoughtworks.go.domain;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.core.Is.is;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.Date;

import com.thoughtworks.go.domain.Job;

public class JobTest {

    @Test
    public void shouldCreateJob() throws Exception {
        Job job = Job.create(file("testdata/2.4/job-2.xml"));
        assertThat(job.getName(), is("two"));
        assertThat(job.getPipelineName(), is("pipeline"));
        assertThat(job.getPipelineCounter(), is(9));
        assertThat(job.getPipelineLabel(), is("83"));
        assertThat(job.getStageName(), is("stage"));
        assertThat(job.getStageCounter(), is(1));
        assertThat(job.getState(), is("Completed"));
        assertThat(job.getResult(), is("Failed"));
        assertThat(job.getJobIdentifier(), is(new JobIdentifier("pipeline", 9, "stage", 1, "two")));

        assertProperties(job);
        assertResources(job);
        assertVariables(job);
    }

    @Test
    public void shouldHaveTheTimeTheJobSpentOnAnAgent() throws Exception {
        Job job = Job.create(file("testdata/2.4/job-with-properties.xml"));

        Date started = ISODateTimeFormat.dateTimeNoMillis().parseDateTime("2010-07-08T11:56:49+05:30").toDate();
        Date finished = ISODateTimeFormat.dateTimeNoMillis().parseDateTime("2010-07-08T11:57:03+05:30").toDate();
        assertThat((finished.getTime() - started.getTime()) / 1000, is(job.timeSpentOnAgent()));
    }

    private void assertProperties(Job job) {
        List<Job.Property> properties = job.getProperties();
        assertThat(properties.size(), is(2));
        assertThat(properties.get(0), is(new Job.Property("cruise_agent", "blrstdcrspbs01.thoughtworks.com")));
        assertThat(properties.get(1), is(new Job.Property("cruise_job_duration", "0")));
    }

    private void assertVariables(Job job) {
        List<Job.EnvVariable> envVariables = job.getEnvVariables();
        assertThat(envVariables.size(), is(1));
        assertThat(envVariables.get(0), is(new Job.EnvVariable("command", "ls -la")));
    }

    private void assertResources(Job job) {
        List<Job.Resource> properties = job.getResources();
        assertThat(properties.size(), is(1));
        assertThat(properties.get(0), is(new Job.Resource("foo")));
    }

    private String file(String name) throws IOException {
        return FileUtils.readFileToString(new File(name));
    }
}
