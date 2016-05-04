package com.bbva.mmap.common.configuration;

import com.bbva.mmap.common.utils.FileSeeker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Se mapean las clases comunes al programa.
 */

@Configuration
@PropertySource("file:config/ESBInspector.properties")
public class ApplicationConfig {

    //SE PASAN LOS PARAMETROS COMO BEANS PARA OFRECERLOS A TODAS LAS CLASES QUE LO NECESITEN
    private String tibcoHostname;
    @Value("${tibco.domains}")
    private String tibcoDomains;
    @Value("${tibco.path.deployment}")
    private String tibcoDeploymentPath;
    @Value("${tibco.process.xsd.rootelement}")
    private String tibcoProcessRootElement;
    @Value("${tibco.process.xsd.extension}")
    private String tibcoFileExtension;
    @Value("${xml.path.pool}")
    private String xmlPoolPath;
    @Value("${xml.path.reader}")
    private String xmlReaderPath;
    @Value("${xml.path.starters.output}")
    private String xmlOutput;
    @Value("${xml.writer.rootElement}")
    private String xmlWriterRootElement;
    @Value("${xml.interval}")
    private int xmlInterval;


    //RESUELVE ${} EN @Value
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public FileSeeker fileSeeker(){
        return new FileSeeker();
    }

    @Bean
    public String tibcoHostname() throws UnknownHostException {
        tibcoHostname = InetAddress.getLocalHost().getHostName();
        return tibcoHostname;
    }

    @Bean
    public String tibcoDomains() {
        return tibcoDomains;
    }

    @Bean
    public String tibcoDeploymentPath() {
        return tibcoDeploymentPath;
    }

    @Bean
    public String tibcoProcessRootElement() {
        return tibcoProcessRootElement;
    }

    @Bean
    public String tibcoFileExtension() {
        return tibcoFileExtension;
    }

    @Bean
    public String xmlPoolPath() {
        return xmlPoolPath;
    }

    @Bean
    public String xmlReaderPath(){
        return xmlReaderPath;
    }

    @Bean
    public String xmlOutput() {
        return xmlOutput;
    }

    @Bean
    public String xmlWriterRootElement() {
        return xmlWriterRootElement;
    }

    @Bean
    public int xmlInterval() {
        return xmlInterval;
    }

    @Bean
    public String pathSeparator(){
        return System.getProperty("file.separator");
    }
}
