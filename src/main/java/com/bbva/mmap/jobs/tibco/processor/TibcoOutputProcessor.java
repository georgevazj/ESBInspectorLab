package com.bbva.mmap.jobs.tibco.processor;

import com.bbva.mmap.common.utils.FileSeeker;
import com.bbva.mmap.jobs.tibco.model.input.*;
import com.bbva.mmap.jobs.tibco.model.output.TibcoOutput;
import com.bbva.mmap.jobs.tibco.model.output.TibcoOutputActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

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

        if (serviceName.contains("BackEnd")){
            logger.info("Application > " + applicationName +
                    "\n\t\t Service > " + serviceName);
        }


        String applicationPath = tibcoDeploymentPath + domain + pathSeparator + "datafiles" + pathSeparator + applicationName + "_root" + pathSeparator;
        //OBTENCION DEL NOMBRE DEL SERVICIO
        String[] serviceNameSplit = serviceNameFromModel.split("/");
        for (int i = 0; i < serviceNameSplit.length; i++){
            if (serviceNameSplit[i].equals("Business") && serviceNameSplit[i+1].startsWith("S_")){
                serviceName = serviceNameSplit[i+1];
            }
        }

        List<StarterModel> starterModels = processDefinitionModel.getStarterModels();
        List<ActivityModel> activityModels = processDefinitionModel.getActivityModels();
        List<GroupModel> groupModels = processDefinitionModel.getGroupModels();

        try{
            //SERVICIOS IA Y SS (FUNCIONAN DE LA MISMA MANERA)
            if (serviceName.contains("_SS_") || serviceName.contains("_IA_")|| serviceNameFromModel.contains("BackEnd")){
                //STARTER > CONSUMIDOR
                for (StarterModel starterModel:starterModels){

                    if(starterModel.getType().contains("jms")){
                        List<StarterConfigModel> starterConfigModels = starterModel.getStarterConfigModels();
                        for (StarterConfigModel starterConfigModel:starterConfigModels){
                            List<SessionAttributesModel> sessionAttributesModels = starterConfigModel.getSessionAttributesModels();
                            for (SessionAttributesModel sessionAttributesModel:sessionAttributesModels){
                                String destination = getQueueName(applicationPath,sessionAttributesModel.getDestination());

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
                }
                //ACTIVIDAD Y ACTIVIDADES EN BUCLE (GROUP) > PRODUCTOR
                for (ActivityModel activityModel:activityModels){
                    if (activityModel.getType().contains("jms")){
                        List<ActivityConfigModel> activityConfigModels = activityModel.getActivityConfigModels();
                        for (ActivityConfigModel activityConfigModel:activityConfigModels){
                            List<SessionAttributesModel> sessionAttributesModels = activityConfigModel.getSessionAttributesModels();

                            int cont = 0;
                            for (SessionAttributesModel sessionAttributesModel:sessionAttributesModels){
                                if (!sessionAttributesModel.getDestination().isEmpty()){
                                    String destination = getQueueName(applicationPath,sessionAttributesModel.getDestination());

                                    cont = cont + 1;

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
                for (GroupModel groupModel:groupModels){
                    List<ActivityModel> groupActivityModels = groupModel.getActivityModels();
                    for (ActivityModel activityModel:groupActivityModels){
                        if (activityModel.getType().contains("jms")){
                            List<ActivityConfigModel> activityConfigModels = activityModel.getActivityConfigModels();
                            for (ActivityConfigModel activityConfigModel:activityConfigModels){
                                List<SessionAttributesModel> sessionAttributesModels = activityConfigModel.getSessionAttributesModels();

                                int cont = 0;

                                for (SessionAttributesModel sessionAttributesModel:sessionAttributesModels){
                                    if (!sessionAttributesModel.getDestination().isEmpty()){
                                        String destination = getQueueName(applicationPath,sessionAttributesModel.getDestination());

                                        cont = cont + 1;

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
            destinationPath = destinationPath.replace("concat(","");
            destinationPath = destinationPath.replace(")","");
            destinationPath = destinationPath.replace("$_globalVariables/ns8:GlobalVariables","");
            destinationPath = destinationPath.replace(",","");
            String separator = Pattern.quote("'");
            String[] destinationPathList = destinationPath.split(separator);

            queueHeader = destinationPathList[1];
            String cfgPath = destinationPathList[2];
            queueEnv = fileSeeker.seekJMSConf(applicationPath,cfgPath);
            queueEnd = destinationPathList[3];
            queueName = queueHeader + queueEnv + queueEnd;
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
        if (queueName.startsWith("%%")){
            logger.warn(queueName);
        }
        return queueName;
    }


}
