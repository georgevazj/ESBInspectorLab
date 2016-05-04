package com.bbva.mmap.jobs.tibco.processor;

import com.bbva.mmap.common.utils.FileSeeker;
import com.bbva.mmap.jobs.tibco.model.input.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.regex.Pattern;


/**
 * Created by Jorge on 14/4/16.
 */
public class TibcoJobProcessor implements ItemProcessor<ProcessDefinitionModel,ProcessDefinitionModel> {

    Logger logger = LoggerFactory.getLogger(TibcoJobProcessor.class);

    @Autowired
    private FileSeeker fileSeeker;
    @Autowired
    private String tibcoDeploymentPath;
    @Autowired
    private String pathSeparator;

    @Override
    public ProcessDefinitionModel process(ProcessDefinitionModel processDefinitionModel) throws Exception {
        String applicationName = processDefinitionModel.getApplicationName();
        String[] applicationNameSplit = applicationName.split("_");
        String domain ="";
        String uuaa ="";

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

        //Obtencion de los nombres de cola
        String applicationPath = tibcoDeploymentPath + domain + pathSeparator + "datafiles" + pathSeparator + applicationName + "_root" + pathSeparator;
        List<StarterModel> starterModels = processDefinitionModel.getStarterModels();
        for (StarterModel starterModel:starterModels){
            List<StarterConfigModel> starterConfigModels = starterModel.getStarterConfigModels();
            for(StarterConfigModel starterConfigModel:starterConfigModels){
                List<SessionAttributesModel> sessionAttributesModels = starterConfigModel.getSessionAttributesModels();
                for (SessionAttributesModel sessionAttributesModel:sessionAttributesModels){
                    String destinationConfig = sessionAttributesModel.getDestination();
                    String destination = getQueueName(applicationPath,destinationConfig);
                    sessionAttributesModel.setDestination(destination);
                }
            }
        }
        try{
            List<ActivityModel> activityModels = processDefinitionModel.getActivityModels();
            if (activityModels.size() > 0){
                for(ActivityModel activityModel:activityModels){
                    List<ActivityConfigModel> activityConfigModels = activityModel.getActivityConfigModels();
                    for(ActivityConfigModel activityConfigModel:activityConfigModels){
                        List<SessionAttributesModel> sessionAttributesModels1 = activityConfigModel.getSessionAttributesModels();
                        if (!sessionAttributesModels1.isEmpty()){
                            for (SessionAttributesModel sessionAttributesModel1:sessionAttributesModels1){
                                String destinationConfig1 = sessionAttributesModel1.getDestination();
                                String destination1 = getQueueName(applicationPath,destinationConfig1);
                                sessionAttributesModel1.setDestination(destination1);
                            }
                        }
                    }
                }
            }
            List<GroupModel> groupModels = processDefinitionModel.getGroupModels();
            if (groupModels.size()>0){
                for (GroupModel groupModel:groupModels){
                    List<ActivityModel> activityModels1 = groupModel.getActivityModels();
                    if (activityModels1.size() > 0){
                        for(ActivityModel activityModel:activityModels1){
                            List<ActivityConfigModel> activityConfigModels = activityModel.getActivityConfigModels();
                            for(ActivityConfigModel activityConfigModel:activityConfigModels){
                                List<SessionAttributesModel> sessionAttributesModels1 = activityConfigModel.getSessionAttributesModels();
                                if (!sessionAttributesModels1.isEmpty()){
                                    for (SessionAttributesModel sessionAttributesModel1:sessionAttributesModels1){
                                        String destinationConfig1 = sessionAttributesModel1.getDestination();
                                        String destination1 = getQueueName(applicationPath,destinationConfig1);
                                        sessionAttributesModel1.setDestination(destination1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e){
            logger.error("Error on " + applicationName + " path " + applicationPath + " \nError: " + e.toString());
        }
        return processDefinitionModel;
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