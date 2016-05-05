package com.bbva.mmap.jobs.tibco;

import com.bbva.mmap.common.configuration.ApplicationConfig;
import com.bbva.mmap.jobs.tibco.listener.TibcoListener;
import com.bbva.mmap.jobs.tibco.model.input.ProcessDefinitionModel;
import com.bbva.mmap.jobs.tibco.model.output.TibcoActivityModel;
import com.bbva.mmap.jobs.tibco.model.output.TibcoDataModel;
import com.bbva.mmap.jobs.tibco.model.output.TibcoOutput;
import com.bbva.mmap.jobs.tibco.processor.TibcoJobProcessor;
import com.bbva.mmap.jobs.tibco.processor.TibcoOutputProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jorge on 22/3/16.
 */

@Configuration
@EnableBatchProcessing
@Import({ApplicationConfig.class})
public class TibcoConfiguration{

    @Autowired
    private String xmlPoolPath;
    @Autowired
    private String tibcoHostname;
    @Autowired
    private String xmlOutput;
    @Autowired
    private String xmlWriterRootElement;
    @Autowired
    private String xmlReaderPath;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //CONFIGURACION DEL JOB
    @Bean
    public TibcoListener tibcoListener(){
        return new TibcoListener();
    }

    //CONFIGURACION DEL READER
    @Bean
    public Jaxb2Marshaller jaxb2UnMarshaller(){
        Jaxb2Marshaller jaxb2UnMarshaller = new Jaxb2Marshaller();
        jaxb2UnMarshaller.setClassesToBeBound(new Class[]{ProcessDefinitionModel.class});
        return jaxb2UnMarshaller;
    }

    @Bean
    public StaxEventItemReader<ProcessDefinitionModel> staxEventItemReader(){
        StaxEventItemReader<ProcessDefinitionModel> staxEventItemReader = new StaxEventItemReader<ProcessDefinitionModel>();
        staxEventItemReader.setUnmarshaller(jaxb2UnMarshaller());
        staxEventItemReader.setFragmentRootElementName("ProcessDefinition");
        return staxEventItemReader;
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<ProcessDefinitionModel> multiResourceItemReader(){
        MultiResourceItemReader<ProcessDefinitionModel> multiResourceItemReader = new MultiResourceItemReader<ProcessDefinitionModel>();
        multiResourceItemReader.setResources(getResources(xmlReaderPath));
        multiResourceItemReader.setDelegate(staxEventItemReader());
        multiResourceItemReader.setSaveState(false);
        return multiResourceItemReader;
    }

    private Resource[] getResources(String path){
        ResourceArrayPropertyEditor arrayPropertyEditor = new ResourceArrayPropertyEditor();
        arrayPropertyEditor.setAsText("file:" + path + "*");
        Resource[] resources = (Resource[]) arrayPropertyEditor.getValue();
        return resources;
    }

    //CONFIGURACION DEL PROCESSOR
    @Bean
    public TibcoOutputProcessor tibcoProcessor(){
        return new TibcoOutputProcessor();
    }

    //CONFIGURACION DEL WRITER
    @Bean(destroyMethod = "")
    public StaxEventItemWriter<TibcoOutput> staxEventItemWriter(){
        Resource resource = new FileSystemResource(xmlOutput + "ESBInspector_" + tibcoHostname + ".xml" );
        StaxEventItemWriter<TibcoOutput> staxEventItemWriter = new StaxEventItemWriter<TibcoOutput>();
        staxEventItemWriter.setResource(resource);
        staxEventItemWriter.setEncoding("UTF-8");
        staxEventItemWriter.setVersion("1.0");
        staxEventItemWriter.setMarshaller(jaxb2Marshaller());
        staxEventItemWriter.setRootTagName(xmlWriterRootElement);
        Map<String,String> rootElementAttributes = new HashMap<String, String>();
        rootElementAttributes.put("source","ESB");
        rootElementAttributes.put("hostname",tibcoHostname);
        staxEventItemWriter.setRootElementAttributes(rootElementAttributes);
        return staxEventItemWriter;
    }

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(TibcoOutput.class);
        return jaxb2Marshaller;
    }

    @Bean
    public ProcessDefinitionModel processDefinitionModel(){
        return new ProcessDefinitionModel();
    }

    //CREACION DEL JOB
    @Bean
    public Job tibcoServicesJob(){
        return jobBuilderFactory.get("tibcoServicesJob")
                .incrementer(new RunIdIncrementer())
                .listener(tibcoListener())
                .flow(step())
                .end()
                .build();
    }

    @Bean
    public Step step(){
        return stepBuilderFactory.get("step")
                .allowStartIfComplete(true)
                .<ProcessDefinitionModel,TibcoOutput>chunk(1)
                .reader(multiResourceItemReader())
                .processor(tibcoProcessor())
                .writer(staxEventItemWriter())
                .build();
    }

    @Bean
    public TibcoDataModel tibcoDataModel(){
        return new TibcoDataModel();
    }

    @Bean
    public TibcoActivityModel tibcoActivityModel(){
        return new TibcoActivityModel();
    }

}
