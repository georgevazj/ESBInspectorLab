package com.bbva.mmap.common.utils;

import com.bbva.mmap.jobs.tibco.model.input.ems.EMSResourceModel;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Jorge on 23/3/16.
 */
public class FileSeeker {

    private final static Logger logger = LoggerFactory.getLogger(FileSeeker.class);
    private final static String fileSeparator = System.getProperty("file.separator");

    @Autowired
    private String tibcoDomains;
    @Autowired
    private String tibcoDeploymentPath;
    @Autowired
    private String tibcoFileExtension;

    //Se llama desde el beforeJob del listener. Se encarga de buscar los ficheros Starter o cualquier .process que contengan etiquetas destination o variables que apunten a colas
    public ArrayList<String> getTibcoServicesFiles(){
        ArrayList<String> tibcoServicesFiles = new ArrayList<String>();
        String[] tibcoDomainsList = tibcoDomains.split(",");
        File tibcoDeploymentFolder = new File(tibcoDeploymentPath);
        String[] tibcoExtension = {tibcoFileExtension};


        for(String tibcoDomain:tibcoDomainsList){
            try {
                File domainDeploymentPath = new File(tibcoDeploymentFolder.getCanonicalFile() + fileSeparator + tibcoDomain + fileSeparator);
                List<File> filesListed = (List<File>) FileUtils.listFiles(domainDeploymentPath,tibcoExtension,true);
                for (File fileListed:filesListed){
                    //Incluye los ficheros starter
                    if(fileListed.getName().startsWith("Starter_")){
                        tibcoServicesFiles.add(fileListed.getCanonicalPath());
                    }
                    else if (fileListed.getCanonicalPath().contains("BackEnd")){
                        tibcoServicesFiles.add(fileListed.getCanonicalPath());
                    }
                    //Busca en el resto de ficheros .process si existe la etiqueta destination e incluye su ruta en la lista
                    else{
                        List<String> fileContent = FileUtils.readLines(fileListed);
                        for(String line:fileContent){
                            if(line.contains("com.tibco.plugin.jms")){
                                tibcoServicesFiles.add(fileListed.getCanonicalPath());
                            }
                        }
                    }
                }
            }
            catch (Exception e){
                logger.error(e.toString());
            }
        }
        return tibcoServicesFiles;
    }

    public String seekJMSConf(String applicationPath,String confValue){
        String varValue = "";
        File applicationConfPath;
        String fileSeparator = System.getProperty("file.separator");

        //Se separa el nombre de la variable de la ruta
        String[] confValueList = confValue.split("/");
        String varName = confValueList[confValueList.length-1];

        String varPath = "";
        for (int i=0;i<confValueList.length -1; i++){
            varPath = varPath + confValueList[i] + fileSeparator;
        }

        //Se forma la ruta completa para leer el XSD de configuracion
        if(!applicationPath.endsWith(fileSeparator)){
            applicationConfPath = new File(applicationPath + fileSeparator + "defaultVars" + fileSeparator + varPath + "defaultVars.substvar");
        }else{
            applicationConfPath = new File(applicationPath + "defaultVars" + fileSeparator + varPath + "defaultVars.substvar");
        }

        try {
            if(applicationConfPath.isFile()){
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
                Document document = dbBuilder.parse(applicationConfPath);

                document.getDocumentElement().normalize();
                NodeList nodeList = document.getElementsByTagName("globalVariable");

                for (int temp = 0; temp < nodeList.getLength();temp++){
                    Node nNode = nodeList.item(temp);
                    Element eElement = (Element) nNode;

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        String nameTag = "";
                        String valueTag = "";
                        try {
                            nameTag = eElement.getElementsByTagName("name").item(0).getTextContent();
                            valueTag = eElement.getElementsByTagName("value").item(0).getTextContent();
                            if (nameTag.equals(varName)) {
                                varValue = valueTag;
                            }
                        }
                        catch (Exception e){
                            logger.error(e.toString());
                        }
                    }

                }
            }
        } catch (Exception e) {
            logger.error(applicationPath + " " + e.toString());
        }
        if (varValue == ""||varValue == null){
            varValue = "Unknown";
            logger.warn("No ha sido posible obtener el valor de la variable para " + applicationConfPath + ".");
        }
        return varValue;
    }

    public EMSResourceModel getEMSConnection(String applicationPath,String confPath) throws JAXBException {

        File emsConfFile = new File(applicationPath + confPath);
        String fileContent = "";
        EMSResourceModel emsResourceModel = null;

        try {
            fileContent = FileUtils.readFileToString(emsConfFile);

        JAXBContext jaxbContext = JAXBContext.newInstance(EMSResourceModel.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        StringReader stringReader = new StringReader(fileContent);
        emsResourceModel = (EMSResourceModel) unmarshaller.unmarshal(stringReader);

        }
        catch (Exception ex){
            logger.error("There was an error while reading EMS configuration file. \n" + ex.toString());
        }

        return emsResourceModel;

    }

    //Metodo que forma el nombre de la cola obtenido desde el fichero .process
    public String getQueueName(String applicationPath,String destinationPath){
        String queueHeader ="";
        String queueEnv = "";
        String queueEnd = "";
        String queueName = "";
        if (!destinationPath.isEmpty()){
            queueName = destinationPath;
        }
        else {
            queueName = "Empty";
        }


        //Un caso muy especifico de KJOS
        /**if(destinationPath.contains("concat")){
            destinationPath = destinationPath.replace("concat(","");
            destinationPath = destinationPath.replace(")","");
            if(destinationPath.contains("ns8")){
                destinationPath = destinationPath.replace("$_globalVariables/ns8:GlobalVariables","");
            }
            else if(destinationPath.contains("ns6")){
                destinationPath = destinationPath.replace("$_globalVariables/ns6:GlobalVariables","");
            }
            destinationPath = destinationPath.replace(",","");
            String separator = Pattern.quote("'");
            String[] destinationPathList = destinationPath.split(separator);

            queueHeader = destinationPathList[1];
            String cfgPath = destinationPathList[2];
            queueEnv = seekJMSConf(applicationPath,cfgPath);
            queueEnd = destinationPathList[3];
            queueName = queueHeader + queueEnv + queueEnd;
        }**/
        if(destinationPath.startsWith("concat") || destinationPath.contains("$_globalVariables")){
            destinationPath = destinationPath.replace("concat(","");
            destinationPath = destinationPath.replace(")","");
            destinationPath = destinationPath.replaceAll("\n","");
            destinationPath = destinationPath.replaceAll(",","");
            destinationPath = destinationPath.replaceAll("\"","");
            destinationPath = destinationPath.replaceAll("'","");
            if (destinationPath.contains("ns6")) {
                destinationPath = destinationPath.replace("$_globalVariables/ns6:GlobalVariables", "");
            }
            else if (destinationPath.contains("ns8")){
                destinationPath = destinationPath.replace("$_globalVariables/ns8:GlobalVariables", "");
            }
            else if(destinationPath.contains("gv:")){
                destinationPath = destinationPath.replace("$_globalVariables/gv:GlobalVariables", "");
            }
            else {
                destinationPath = destinationPath.replace("$_globalVariables/GlobalVariables", "");
            }


            if (destinationPath.contains(".")){
                String separator = Pattern.quote(".");
                String[] destinationPathList = destinationPath.split(separator);
                if (destinationPathList[0].startsWith("GLB") && destinationPathList[0].contains(".")){
                    queueHeader = destinationPathList[0];
                }
                else{
                    queueHeader = seekJMSConf(applicationPath,destinationPathList[0]);
                }

                if (destinationPathList[1].contains("/")){
                    queueEnv = seekJMSConf(applicationPath,destinationPathList[1]);
                }
                else{
                    queueEnv = destinationPathList[1];
                }

                if(destinationPathList[2].startsWith(".") && !destinationPathList[2].contains("/")){
                    queueEnd = destinationPathList[2];
                }
                else{
                    queueEnd = seekJMSConf(applicationPath,destinationPathList[2]);
                }
                queueName = queueHeader + "." + queueEnv + "." + queueEnd;
            }
            else {
                queueName = seekJMSConf(applicationPath,destinationPath);
            }

        }
        else if(destinationPath.startsWith("%%") && destinationPath.contains(".")){
            String[] destinationPathList = destinationPath.split("%%");
            for (String item:destinationPathList){
                //1 - Variable que apunta a configuracion
                if(item.contains("/")){
                    String varValue = seekJMSConf(applicationPath,item);
                    if(varValue.startsWith("GLB")){
                        queueHeader = varValue;
                    }
                    else if(varValue.length() <= 3){
                        queueEnv = "." + varValue + ".";
                    }
                    else if(!varValue.startsWith("GLB") && varValue.length() > 3){
                        queueEnd = varValue;
                    }
                }
                //2 - Variables literales
                else{
                    if (item.startsWith("GLB") && item.contains(".")){
                        queueHeader = item;
                    }
                    else if (item.startsWith(".") && item.endsWith(".") && item.length() > 1){
                        queueEnv = item;
                    }
                    else if(item.startsWith(".") && !item.endsWith(".")){
                        queueEnd = item;
                    }
                }
            }
            if (!queueHeader.isEmpty() && !queueEnv.isEmpty() && !queueEnd.isEmpty()){
                queueName = queueHeader + queueEnv + queueEnd;
            }
            else{
                queueName = destinationPath;
            }

        }
        else if(destinationPath.startsWith("%%") && !destinationPath.contains(".")){
            destinationPath = destinationPath.replace("%%","");
            String value = seekJMSConf(applicationPath,destinationPath);
            queueName = value;
        }

        //CORRECCION DE OTROS CASOS RAROS
        if(queueName.contains("%")){
            String[] queueNameSplit = queueName.split("%%");
            for (String item:queueNameSplit){
                item = item.replace("%","");
                if(item.contains("/")){
                    item = item.replace(".","");
                    item = seekJMSConf(applicationPath,item);
                }
                if (item.startsWith("GLB") && item.contains(".")){
                    queueHeader = item;
                    if (!queueHeader.endsWith(".")){
                        queueHeader = queueHeader + ".";
                    }
                }
                else if(!item.startsWith(".") && item.length() == 2){
                    queueEnv = item;
                }
                else if(item.startsWith(".") && !item.endsWith(".")){
                    queueEnd = item;
                }
                else if(!item.startsWith("GLB") && item.length()>3){
                    queueEnd = "." + item;
                }

            }
            queueName = queueHeader + queueEnv + queueEnd;
        }

        if(queueName.contains("..")){
            queueName = queueName.replace("..",".");
        }
        return queueName;
    }

}
