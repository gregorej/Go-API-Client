package com.thoughtworks.go;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.core.Is.is;

import java.io.IOException;
import java.io.File;

public class PipelineTest {

    @Test
    public void shouldReturnWhoTriggerThePipeline() throws Exception {
        Pipeline pipeline = Pipeline.create(file("pipeline-9.xml"));
        assertThat(pipeline.getApprovedBy(), is("CruiseTimer"));
    }

    private String file(String name) throws IOException {
        return FileUtils.readFileToString(new File(name));
    }
}