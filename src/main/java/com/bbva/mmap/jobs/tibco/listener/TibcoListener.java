package com.bbva.mmap.jobs.tibco.listener;

import com.bbva.mmap.common.utils.FileSeeker;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Jorge on 22/3/16.
 */
public class TibcoListener implements JobExecutionListener {

    private final static Logger logger = LoggerFactory.getLogger(TibcoListener.class);
    private final static String fileSeparator = System.getProperty("file.separator");

    @Autowired
    private String tibcoHostname;
    @Autowired
    private String xmlPoolPath;
    @Autowired
    private String xmlOutput;

    @Autowired
    private FileSeeker fileSeeker;

    /** BEFOREJOB:
     * 1-. Se buscan los ficheros .process
     * 2-. Copia los nuevos ficheros encontrados al pool
     * 3-. Elimina los caracteres pd: del xsd. Puede provocar problemas con el marshaller
     * **/
    @Override
    public void beforeJob(JobExecution jobExecution) {

        File fileXmlPoolPath = new File(xmlPoolPath);
        try {
            //Se identifican que ficheros deben ser copiados al pool
            logger.info("Copying files to " + xmlPoolPath + "...");
            ArrayList<String> tibcoProcessFiles = fileSeeker.getTibcoServicesFiles();
            for (String process:tibcoProcessFiles){
                File processFile = new File(process);
                String applicationName = "";
                String pattern = Pattern.quote(fileSeparator);
                String[] processFileList = processFile.getCanonicalPath().split(pattern);

                for (String item:processFileList){
                    if(item.contains("_root")){
                        applicationName = item.replace("_root","");
                    }
                }
                File targetPath = new File(xmlPoolPath + applicationName + "__" + processFile.getName());
                FileUtils.copyFile(processFile,targetPath);
            }
            logger.info("Completed.");

            //ELIMINAR LOS CARACTERES pd: DE LAS ETIQUETAS DE LOS XSD
            logger.info("Modifying .process files...");
            File[] tibcoProcessFileList = fileXmlPoolPath.listFiles();
            for (File processFile:tibcoProcessFileList){
                if(processFile.exists()){
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(processFile));
                    String line="",oldText="";
                    while ((line = bufferedReader.readLine()) != null){
                        oldText += line + "\r\n";
                    }
                    bufferedReader.close();

                    String newText = oldText.replace("pd:", "");
                    newText = newText.replace("ns1:", "");
                    newText = newText.replace("ns2:", "");
                    newText = newText.replace("ns4:", "");
                    newText = newText.replace("xsl:", "");
                    newText = newText.replace("<ns:","<");
                    newText = newText.replace("</ns:","</");
                    newText = newText.replace("xsl:", "");
                    newText = newText.replace("pfx:2", "");
                    newText = newText.replace("pfx:", "");
                    newText = newText.replace("eaifw:","");
                    FileWriter fileWriter = new FileWriter(processFile);
                    fileWriter.write(newText);
                    fileWriter.close();
                }
            }
            logger.info("Completed.");
        }
        catch (Exception e){
            logger.error("There was an error in beforeJob step: " + e.toString());
        }

    }
    /** AFTERJOB
     *  1-. Debido a un bug de Jaxb2Marshaller, es necesario insertar las tabulaciones posteriormente
     *  2-. Vacia el pool de ficheros
     *  **/
    @Override
    public void afterJob(JobExecution jobExecution) {
        String xmlOutputFile = xmlOutput + "ESBInspector_" + tibcoHostname + ".xml";
        File resultXmlFile = new File(xmlOutputFile);
        logger.info("Formatting " + resultXmlFile.getName() + "...");
        if (resultXmlFile.exists()){
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setValidating(false);
            try {
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(resultXmlFile);

                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty(OutputKeys.INDENT, "YES");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                Writer writer = new StringWriter();
                transformer.transform(new DOMSource(document),new StreamResult(writer));

                FileWriter fileWriter = new FileWriter(resultXmlFile.getCanonicalFile());
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(writer.toString());
                bufferedWriter.close();

            } catch (Exception e) {
                logger.error(e.toString());
            }
            finally {
                File fileXmlPoolPath = new File(xmlPoolPath);
                if (fileXmlPoolPath.isDirectory()) {
                    try {
                        logger.info("Clearing " + fileXmlPoolPath.getCanonicalPath());
                        FileUtils.cleanDirectory(fileXmlPoolPath);
                    } catch (IOException e) {
                        logger.error(e.toString());
                    }
                }
                if(resultXmlFile.exists()){
                    logger.info("COMPLETED --> " + resultXmlFile.getName() + " size is " + FileUtils.sizeOf(resultXmlFile)/1024 + " Kb");
                }
            }
        }

    }
}
