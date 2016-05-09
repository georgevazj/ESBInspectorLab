package com.bbva.mmap.jobs.tibco.processor;

import com.bbva.mmap.common.utils.FileSeeker;
import com.bbva.mmap.jobs.tibco.model.configs.DefaultVarsModel;
import com.bbva.mmap.jobs.tibco.model.configs.GlobalVariableModel;
import com.bbva.mmap.jobs.tibco.model.configs.QueuesFileListModel;
import com.bbva.mmap.jobs.tibco.model.configs.QueuesFileModel;
import com.bbva.mmap.jobs.tibco.model.input.*;
import com.bbva.mmap.jobs.tibco.model.output.TibcoOutput;
import com.bbva.mmap.jobs.tibco.model.output.TibcoOutputActivity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import sun.reflect.generics.reflectiveObjects.LazyReflectiveObjectGenerator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by jorge on 04/05/2016.
 */
public class TibcoOutputProcessor implements ItemProcessor<ProcessDefinitionModel,TibcoOutput> {

    private static final Logger logger = LoggerFactory.getLogger(TibcoOutputProcessor.class);

    @Autowired
    private FileSeeker fileSeeker;
    @Autowired
    private String tibcoDeploymentPath;
    @Autowired
    private String pathSeparator;


    @Override
    public TibcoOutput process(ProcessDefinitionModel processDefinitionModel) throws Exception {
        TibcoOutput tibcoOutput = new TibcoOutput();
        String applicationName = processDefinitionModel.getApplicationName();
        String[] applicationNameSplit = applicationName.split("_");
        String serviceNameFromModel = processDefinitionModel.getName();

        String serviceName = "";
        String domain = "";
        String uuaa = "";

        //Correccion de algunos nombres de dominio y uuaa erroneos
        try{
            domain = applicationNameSplit[0];
            uuaa = applicationNameSplit[1];
            if (domain.equals("S") || uuaa.equals("TB")){
                domain = "GMA";
            }
            else if (domain.equals("KVMZ") && uuaa.equals("BW")){
                domain ="GMA";
                uuaa = "KVMZ";
            }
            else if (domain.equals("CIB") && uuaa.equals("KYFD")){
                domain ="GMA";
            }
            else if(domain.equals("Lanzador") && uuaa.equals("Eventos")){
                domain = "CIB";
                uuaa = "EYFW";
            }
            else if(domain.equals("Pruebas") && uuaa.equals("EMS")){
                domain = "CIB";
                uuaa = "EYFW";
            }
            else if(domain.contains("-")){
                String[] domainSplit = domain.split("-");
                domain = domainSplit[0];
                uuaa = domainSplit[1];
            }
            else if(domain.equals("TB")){
                domain = "GMA";
            }
            else if(domain.equals("FW") && uuaa.equals("Visor")){
                domain = "CIB";
                uuaa = "EYFW";
            }
        }
        catch (ArrayIndexOutOfBoundsException e){
            domain = "Unknown";
            uuaa = "Unknown";
        }

        //RUTAS DE INTERES PARA LAS APLICACIONES

        //Ruta completa de despliegue de la aplicacion
        String applicationPath = tibcoDeploymentPath + domain + pathSeparator + "datafiles" + pathSeparator + applicationName + "_root" + pathSeparator;
        //Ruta de los ficheros de configuracion para Queues.xml
        File defaultVarsFile = new File(applicationPath + pathSeparator + "defaultVars" + pathSeparator + "CFG" + pathSeparator + "ARCHITECTURE" + pathSeparator + "ENVIRONMENT" + pathSeparator + "defaultVars.substvar");

        //OBTENCION DEL NOMBRE DEL SERVICIO
        String[] serviceNameSplit = serviceNameFromModel.split("/");
        for (int i = 0; i < serviceNameSplit.length; i++){
            if (serviceNameSplit[i].equals("Business") && serviceNameSplit[i+1].startsWith("S_")){
                serviceName = serviceNameSplit[i+1];
            }
        }

        List<StarterModel> starterModels = processDefinitionModel.getStarterModels();
        try {
            //SERVICIOS IA, IP y SS (FUNCIONAN DE LA MISMA MANERA)
            if (serviceName.contains("_SS_") || serviceName.contains("_IA_") || serviceName.contains("_IP_")) {
                //STARTER > CONSUMIDOR
                for (StarterModel starterModel : starterModels) {
                    if (starterModel.getType().contains("jms")) {
                        List<StarterConfigModel> starterConfigModels = starterModel.getStarterConfigModels();
                        for (StarterConfigModel starterConfigModel : starterConfigModels) {
                            List<SessionAttributesModel> sessionAttributesModels = starterConfigModel.getSessionAttributesModels();
                            for (SessionAttributesModel sessionAttributesModel : sessionAttributesModels) {
                                String destination = getQueueName(applicationPath, sessionAttributesModel.getDestination());

                                TibcoOutputActivity tibcoOutputActivity = new TibcoOutputActivity();
                                tibcoOutputActivity.setApplication(applicationName);
                                tibcoOutputActivity.setDestination(destination);
                                tibcoOutputActivity.setService(serviceName);
                                tibcoOutputActivity.setType("Consumer");
                                tibcoOutputActivity.setDomain(domain);
                                tibcoOutputActivity.setUuaa(uuaa);

                                tibcoOutput.getTibcoOutputActivities().add(tibcoOutputActivity);
                            }
                        }

                        //SE BUSCAN LOS FICHEROS DE BACKEND PARA LOS PRODUCTORES
                        File dir = new File(applicationPath);
                        List<File> files = (List<File>) FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
                        for (File file : files) {

                            if (!applicationName.contains("RDR_SERVICE") && file.getCanonicalPath().contains(serviceName) && file.getName().contains("BackEnd")) {
                                JAXBContext jaxbContext = JAXBContext.newInstance(ProcessDefinitionModel.class);
                                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                                String textContent = FileUtils.readFileToString(file);
                                String newText = textContent.replace("pd:", "");
                                newText = newText.replace("ns1:", "");
                                newText = newText.replace("ns2:", "");
                                newText = newText.replace("<ns:", "<");
                                newText = newText.replace("</ns:", "</");
                                newText = newText.replace("xsl:", "");
                                newText = newText.replace("pfx:2", "");
                                newText = newText.replace("pfx:", "");

                                StringReader stringReader = new StringReader(newText);
                                ProcessDefinitionModel processDefinitionModel1 = (ProcessDefinitionModel) unmarshaller.unmarshal(stringReader);

                                //PRODUCTORES SIN BUCLE
                                List<ActivityModel> activityModels1 = processDefinitionModel1.getActivityModels();
                                for (ActivityModel activityModel : activityModels1) {
                                    String destination = "";
                                    if (activityModel.getType().contains("jms")) {
                                        List<ActivityConfigModel> activityConfigModels = activityModel.getActivityConfigModels();
                                        for (ActivityConfigModel activityConfigModel : activityConfigModels) {
                                            List<SessionAttributesModel> sessionAttributesModels = activityConfigModel.getSessionAttributesModels();

                                            for (SessionAttributesModel sessionAttributesModel : sessionAttributesModels) {
                                                if (!sessionAttributesModel.getDestination().isEmpty()) {
                                                    destination = getQueueName(applicationPath, sessionAttributesModel.getDestination());
                                                    TibcoOutputActivity tibcoOutputActivity = new TibcoOutputActivity();
                                                    tibcoOutputActivity.setApplication(applicationName);
                                                    tibcoOutputActivity.setDestination(destination);
                                                    tibcoOutputActivity.setService(serviceName);
                                                    tibcoOutputActivity.setType("Producer");
                                                    tibcoOutputActivity.setDomain(domain);
                                                    tibcoOutputActivity.setUuaa(uuaa);

                                                    tibcoOutput.getTibcoOutputActivities().add(tibcoOutputActivity);

                                                }
                                            }
                                        }

                                        if (destination.isEmpty()){
                                            ActivityInputBindingsModel activityInputBindingsModel = activityModel.getActivityInputBindingsModel();
                                            ActivityInputModel activityInputModel = activityInputBindingsModel.getActivityInputModel();
                                            DestinationQueueModel destinationQueueModel = activityInputModel.getDestinationQueueModel();
                                            DestinationValueOfModel destinationValueOfModel = destinationQueueModel.getValueOfModel();

                                            destination = getQueueName(applicationPath,destinationValueOfModel.getSelect());

                                            TibcoOutputActivity tibcoOutputActivity = new TibcoOutputActivity();
                                            tibcoOutputActivity.setApplication(applicationName);
                                            tibcoOutputActivity.setDestination(destination);
                                            tibcoOutputActivity.setService(serviceName);
                                            tibcoOutputActivity.setType("Producer");
                                            tibcoOutputActivity.setDomain(domain);
                                            tibcoOutputActivity.setUuaa(uuaa);

                                            tibcoOutput.getTibcoOutputActivities().add(tibcoOutputActivity);
                                        }

                                    }
                                }

                                //PRODUCTORES INVOCADOS EN BUCLE
                                List<GroupModel> groupModels1 = processDefinitionModel1.getGroupModels();
                                for (GroupModel groupModel : groupModels1) {
                                    List<ActivityModel> groupActivityModels = groupModel.getActivityModels();
                                    for (ActivityModel activityModel : groupActivityModels) {
                                        if (activityModel.getType().contains("jms")) {
                                            List<ActivityConfigModel> activityConfigModels = activityModel.getActivityConfigModels();
                                            for (ActivityConfigModel activityConfigModel : activityConfigModels) {
                                                List<SessionAttributesModel> sessionAttributesModels = activityConfigModel.getSessionAttributesModels();


                                                for (SessionAttributesModel sessionAttributesModel : sessionAttributesModels) {
                                                    if (!sessionAttributesModel.getDestination().isEmpty()) {
                                                        String destination = getQueueName(applicationPath, sessionAttributesModel.getDestination());

                                                        TibcoOutputActivity tibcoOutputActivity = new TibcoOutputActivity();
                                                        tibcoOutputActivity.setApplication(applicationName);
                                                        tibcoOutputActivity.setDestination(destination);
                                                        tibcoOutputActivity.setService(serviceName);
                                                        tibcoOutputActivity.setType("Producer");
                                                        tibcoOutputActivity.setDomain(domain);
                                                        tibcoOutputActivity.setUuaa(uuaa);

                                                        tibcoOutput.getTibcoOutputActivities().add(tibcoOutputActivity);

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                            //CASO ESPECIFICO PARA RDR_SERVICE
                            else if (applicationName.contains("RDR_SERVICE") && file.getCanonicalPath().contains(serviceName) && file.getName().contains("RequestRDR")) {
                                JAXBContext jaxbContext = JAXBContext.newInstance(ProcessDefinitionModel.class);
                                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                                String textContent = FileUtils.readFileToString(file);
                                String newText = textContent.replace("pd:", "");
                                newText = newText.replace("ns1:", "");
                                newText = newText.replace("ns2:", "");
                                newText = newText.replace("<ns:", "<");
                                newText = newText.replace("</ns:", "</");
                                newText = newText.replace("xsl:", "");
                                newText = newText.replace("pfx:2", "");
                                newText = newText.replace("pfx:", "");

                                StringReader stringReader = new StringReader(newText);
                                ProcessDefinitionModel processDefinitionModel1 = (ProcessDefinitionModel) unmarshaller.unmarshal(stringReader);

                                //PRODUCTORES SIN BUCLE
                                List<ActivityModel> activityModels1 = processDefinitionModel1.getActivityModels();
                                for (ActivityModel activityModel : activityModels1) {
                                    String destination = "";
                                    if (activityModel.getType().contains("jms")) {
                                        List<ActivityConfigModel> activityConfigModels = activityModel.getActivityConfigModels();
                                        for (ActivityConfigModel activityConfigModel : activityConfigModels) {
                                            List<SessionAttributesModel> sessionAttributesModels = activityConfigModel.getSessionAttributesModels();

                                            for (SessionAttributesModel sessionAttributesModel : sessionAttributesModels) {
                                                if (!sessionAttributesModel.getDestination().isEmpty()) {
                                                    destination = getQueueName(applicationPath, sessionAttributesModel.getDestination());

                                                    TibcoOutputActivity tibcoOutputActivity = new TibcoOutputActivity();
                                                    tibcoOutputActivity.setApplication(applicationName);
                                                    tibcoOutputActivity.setDestination(destination);
                                                    tibcoOutputActivity.setService(serviceName);
                                                    tibcoOutputActivity.setType("Producer");
                                                    tibcoOutputActivity.setDomain(domain);
                                                    tibcoOutputActivity.setUuaa(uuaa);

                                                    tibcoOutput.getTibcoOutputActivities().add(tibcoOutputActivity);

                                                }
                                            }
                                        }
                                    }
                                }

                                //PRODUCTORES INVOCADOS EN BUCLE
                                List<GroupModel> groupModels1 = processDefinitionModel1.getGroupModels();
                                for (GroupModel groupModel : groupModels1) {
                                    List<ActivityModel> groupActivityModels = groupModel.getActivityModels();
                                    for (ActivityModel activityModel : groupActivityModels) {
                                        if (activityModel.getType().contains("jms")) {
                                            List<ActivityConfigModel> activityConfigModels = activityModel.getActivityConfigModels();
                                            for (ActivityConfigModel activityConfigModel : activityConfigModels) {
                                                List<SessionAttributesModel> sessionAttributesModels = activityConfigModel.getSessionAttributesModels();


                                                for (SessionAttributesModel sessionAttributesModel : sessionAttributesModels) {
                                                    if (!sessionAttributesModel.getDestination().isEmpty()) {
                                                        String destination = getQueueName(applicationPath, sessionAttributesModel.getDestination());

                                                        TibcoOutputActivity tibcoOutputActivity = new TibcoOutputActivity();
                                                        tibcoOutputActivity.setApplication(applicationName);
                                                        tibcoOutputActivity.setDestination(destination);
                                                        tibcoOutputActivity.setService(serviceName);
                                                        tibcoOutputActivity.setType("Producer");
                                                        tibcoOutputActivity.setDomain(domain);
                                                        tibcoOutputActivity.setUuaa(uuaa);

                                                        tibcoOutput.getTibcoOutputActivities().add(tibcoOutputActivity);

                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            } //FINAL DE SERVICIOS SS, IA Y IP

            //OBTENCION DE SERVICIOS CA
            else if (serviceName.contains("_CA_") || serviceName.contains("_FT_")){
                if (defaultVarsFile.exists() &&  serviceNameFromModel.contains("Starter")){
                    List<ActivityModel> activityModels = processDefinitionModel.getActivityModels();

                    for (StarterModel starterModel:starterModels){
                        List<StarterConfigModel> starterConfigModels = starterModel.getStarterConfigModels();
                        for (StarterConfigModel starterConfigModel : starterConfigModels) {
                            List<SessionAttributesModel> sessionAttributesModels = starterConfigModel.getSessionAttributesModels();
                            for (SessionAttributesModel sessionAttributesModel : sessionAttributesModels) {
                                String destination = getQueueName(applicationPath, sessionAttributesModel.getDestination());

                                TibcoOutputActivity tibcoOutputActivity = new TibcoOutputActivity();
                                tibcoOutputActivity.setApplication(applicationName);
                                tibcoOutputActivity.setDestination(destination);
                                tibcoOutputActivity.setService(serviceName);
                                tibcoOutputActivity.setType("Consumer");
                                tibcoOutputActivity.setDomain(domain);
                                tibcoOutputActivity.setUuaa(uuaa);

                                tibcoOutput.getTibcoOutputActivities().add(tibcoOutputActivity);
                            }
                        }
                    }

                    for (ActivityModel activityModel:activityModels){
                        if (activityModel.getType().contains("CallProcessActivity")){

                            //Servicio con el que comunica enviando mensajes JMS
                            String targetServiceId = "";
                            if (serviceName.contains("_CA_")){
                                targetServiceId = activityModel.getActivityInputBindingsModel().getActivityInputBindingsRootElement().getActivityInputBindingsRootAdapterChannel().getTargetServiceModelIDModel().getDestinationValueOfModel().getSelect();
                            }
                            else if(serviceName.contains("_FT_")){
                                logger.info(applicationName + " SERVICE FT >>>> " + serviceName);
                                targetServiceId = activityModel.getActivityInputBindingsModel().getActivityInputBindingsInputElement().getActivityInputBindingsFilterAdapterElement().getTargetServiceModelIDModel().getDestinationValueOfModel().getSelect();
                            }

                            String queuesFilePath = "";
                            String queuesEnv = "";
                            //SE LEE EL CONTENIDO DEL FICHERO DE CONFIGURACION PARA OBTENER LA RUTA DEL QUEUES XML Y EL ENTORNO
                            try{
                                JAXBContext jaxbContext = JAXBContext.newInstance(DefaultVarsModel.class);
                                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                                String defaultVarsFileContent = FileUtils.readFileToString(defaultVarsFile);
                                defaultVarsFileContent = defaultVarsFileContent.replace(":xsi","");
                                defaultVarsFileContent = defaultVarsFileContent.replace("xmlns = ","");
                                defaultVarsFileContent = defaultVarsFileContent.replace("\"http://www.w3.org/2001/XMLSchema-instance\"","");
                                defaultVarsFileContent = defaultVarsFileContent.replace("\"http://www.tibco.com/xmlns/repo/types/2002\"","");

                                StringReader stringReader = new StringReader(defaultVarsFileContent);

                                DefaultVarsModel defaultVarsModel = (DefaultVarsModel) unmarshaller.unmarshal(stringReader);
                                List<GlobalVariableModel> globalVariableModels = defaultVarsModel.getGlobalVariablesModel().getGlobalVariableModels();


                                for (GlobalVariableModel globalVariableModel:globalVariableModels){
                                    if (globalVariableModel.getName().equals("CFG_ENVIRONMENT_PREFIX")){
                                        queuesEnv = globalVariableModel.getValue();
                                    }
                                    else if (globalVariableModel.getName().equals("CFG_PATH")){
                                        queuesFilePath = globalVariableModel.getValue();
                                    }

                                }
                                String projectPath = System.getProperty("user.dir");
                                //ONLY FOR TESTING
                                //File queuesFile = new File(queuesFilePath + "Queues.xml");
                                File queuesFile = new File(projectPath + pathSeparator + "src" + pathSeparator + "main" + pathSeparator + "resources" + pathSeparator + queuesFilePath + "Queues.xml");
                                if (queuesFile.exists()){
                                    if (targetServiceId.contains("globalVariables")){
                                        String[] targetServiceIdSplit = targetServiceId.split("/");
                                        targetServiceId = targetServiceIdSplit[targetServiceIdSplit.length-1];
                                    }

                                    //ES NECESARIO BUSCAR EL CONTENIDO DE TARGETSERVICEID EN EL FICHERO DE CONFIGURACION EN COMMON/GLB/SERVICEIDS
                                    File serviceIdConfFile = new File(applicationPath + pathSeparator + "defaultVars" + pathSeparator + "COMMON" + pathSeparator + "GLB" + pathSeparator + "ServiceIDs" + pathSeparator + "defaultVars.substvar");
                                    if (serviceIdConfFile.exists()){
                                        JAXBContext jaxbContextServiceId = JAXBContext.newInstance(DefaultVarsModel.class);
                                        Unmarshaller unmarshallerServiceId = jaxbContextServiceId.createUnmarshaller();

                                        String serviceIdFileContent = FileUtils.readFileToString(serviceIdConfFile);
                                        serviceIdFileContent = serviceIdFileContent.replace(":xsi","");
                                        serviceIdFileContent = serviceIdFileContent.replace("xmlns = ","");
                                        serviceIdFileContent = serviceIdFileContent.replace("\"http://www.w3.org/2001/XMLSchema-instance\"","");
                                        serviceIdFileContent = serviceIdFileContent.replace("\"http://www.tibco.com/xmlns/repo/types/2002\"","");

                                        StringReader stringReaderServiceId = new StringReader(serviceIdFileContent);

                                        DefaultVarsModel defaultVarsModelServiceId = (DefaultVarsModel) unmarshallerServiceId.unmarshal(stringReaderServiceId);
                                        List<GlobalVariableModel> globalVariableModelsServiceId = defaultVarsModelServiceId.getGlobalVariablesModel().getGlobalVariableModels();

                                        for (GlobalVariableModel globalVariableModel:globalVariableModelsServiceId){
                                            if (globalVariableModel.getName().equals(targetServiceId)){
                                                targetServiceId = globalVariableModel.getValue();
                                            }
                                        }
                                    }

                                    //SE BUSCA EL SERVICIO DESTINO EN EL QUEUES.XML
                                    List<String> queuesFileContent = FileUtils.readLines(queuesFile);
                                    for (String line:queuesFileContent){
                                        if(line.contains(targetServiceId)){
                                            String[] lineSplit = line.split(">");
                                            String destination = lineSplit[1];
                                            destination = destination.replace("</eai:Destination","");

                                            if (destination.contains("%ENV%")){
                                                destination = destination.replace("%ENV%",queuesEnv);
                                            }
                                            if(destination.contains(".." + queuesEnv + "..")){
                                                destination = destination.replace(".." + queuesEnv + "..","." + queuesEnv + ".");
                                            }

                                            TibcoOutputActivity tibcoOutputActivity = new TibcoOutputActivity();
                                            tibcoOutputActivity.setApplication(applicationName);
                                            tibcoOutputActivity.setDestination(destination);
                                            tibcoOutputActivity.setService(serviceName);
                                            tibcoOutputActivity.setType("Producer");
                                            tibcoOutputActivity.setDomain(domain);
                                            tibcoOutputActivity.setUuaa(uuaa);

                                            tibcoOutput.getTibcoOutputActivities().add(tibcoOutputActivity);



                                        }
                                    }
                                }
                            }
                            catch (JAXBException ex){
                                logger.error(ex.toString());
                            }

                        }
                    }
                }
            }
        }

        //LO ERRORES NULLPOINTER SE OMITEN DADO QUE SON PRODUCIDOS CUANDO UN PROCESO NO TIENE STARTER, ACTIVITIES, ETC... NO SON FALLOS A TENER EN CUENTA EN ESTE CASO
        catch (NullPointerException ex){
        }

        return tibcoOutput;
    }

    //Metodo que forma el nombre de la cola obtenido desde el fichero .process
    private String getQueueName(String applicationPath,String destinationPath){
        String queueHeader ="";
        String queueEnv = "";
        String queueEnd = "";
        String queueName = destinationPath;

        //Un caso muy especifico de KJOS
        if(destinationPath.contains("concat")){
            try{

                destinationPath = destinationPath.replace("concat(","");
                destinationPath = destinationPath.replace(")","");
                destinationPath = destinationPath.replace("$_globalVariables/GlobalVariables","");
                destinationPath = destinationPath.replace(",","");
                destinationPath = destinationPath.replace("\"","");

                String separator = Pattern.quote(".");

                String[] destinationPathList = destinationPath.split(separator);

                for (String cfgPath:destinationPathList){
                    String varValue = fileSeeker.seekJMSConf(applicationPath,cfgPath);
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

                queueName = queueHeader + queueEnv + queueEnd;
            }
            catch (IndexOutOfBoundsException ex){
                queueName = "QUEUE_ERROR";
                logger.error("There was an error while getting the queue name for " + applicationPath);
            }
        }
        else{
            String[] destinationPathList = destinationPath.split("%%");
            for (String item:destinationPathList){
                //1 - Variable que apunta a configuracion
                if(item.contains("/")){
                    String varValue = fileSeeker.seekJMSConf(applicationPath,item);
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
                    else if(item.startsWith(".") && !item.endsWith(".")) {
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
        if (queueName.startsWith("%%")){
            logger.warn(queueName);
        }
        return queueName;
    }


}
