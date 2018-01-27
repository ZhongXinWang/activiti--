/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rest.editor.model;

import com.alibaba.fastjson.JSON;
import com.demo.util.R;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.converter.util.InputStreamProvider;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.util.io.InputStreamSource;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;

/**
 * @author Tijs Rademakers
 */
@RestController
public class ModelEditorJsonRestResource implements ModelDataJsonConstants {
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(ModelEditorJsonRestResource.class);
  
  @Autowired
  private RepositoryService repositoryService;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @RequestMapping(value="/service/model/{modelId}/json", method = RequestMethod.GET, produces = "application/json")
  public  ObjectNode getEditorJson(@PathVariable String modelId) {
    ObjectNode modelNode = null;
    Model model = repositoryService.getModel(modelId);

    if (model != null) {
      try {
        if (StringUtils.isNotEmpty(model.getMetaInfo())) {
          modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
        } else {
          modelNode = objectMapper.createObjectNode();
          modelNode.put(MODEL_NAME, model.getName());
        }
        modelNode.put(MODEL_ID, model.getId());

        //把xml转化为json
       /* byte[] modelEditorSource = repositoryService.getModelEditorSource(model.getId());

        InputStreamProvider inputStreamProvider = new InputStreamSource(new ByteArrayInputStream(modelEditorSource)); //构建流对象
        BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();//创建装换器
        BpmnModel bpmnModel = bpmnXMLConverter.convertToBpmnModel(inputStreamProvider,false,false);
        ObjectNode jsonNodes = new BpmnJsonConverter().convertToJson(bpmnModel);*/

        //构建
        ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(
            new String(repositoryService.getModelEditorSource(model.getId()), "utf-8"));

        modelNode.put("model", editorJsonNode);
        
      } catch (Exception e) {
        LOGGER.error("Error creating model JSON", e);
        throw new ActivitiException("Error creating model JSON", e);
      }
    }

    return modelNode;
  }
}
