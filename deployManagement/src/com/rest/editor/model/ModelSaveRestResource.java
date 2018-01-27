
package com.rest.editor.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import sun.misc.BASE64Encoder;

/**
 * @author Tijs Rademakers
 */
@RestController
public class ModelSaveRestResource implements ModelDataJsonConstants {

  protected static final Logger LOGGER = LoggerFactory.getLogger(ModelSaveRestResource.class);

  @Autowired
  private RepositoryService repositoryService;

  @Autowired
  private ObjectMapper objectMapper;

  @RequestMapping(value="/service/model/{modelId}/save", method = RequestMethod.PUT)
  @ResponseStatus(value = HttpStatus.OK)
  public void saveModel(@PathVariable String modelId, @RequestBody MultiValueMap<String, String> values) {
    try {

      Model model = repositoryService.getModel(modelId);

      ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
      modelJson.put(MODEL_NAME, values.getFirst("name"));
      modelJson.put(MODEL_DESCRIPTION, values.getFirst("description"));
      model.setMetaInfo(modelJson.toString());

      model.setName(values.getFirst("name"));

      repositoryService.saveModel(model);   //如果模型存在就会更新，否则就会new

      //把接受到的json，手动转化为bpmn格式的数据
     /* byte[] json_xml = null;
      byte[] bytes = values.getFirst("json_xml").getBytes("utf-8");
      JsonNode editNode = new ObjectMapper().readTree(bytes);
      BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
      BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editNode);
      json_xml = new BpmnXMLConverter().convertToXML(bpmnModel);*/

      repositoryService.addModelEditorSource(model.getId(),values.getFirst("json_xml").getBytes("utf-8"));  //插入模型数据的xml数据

      InputStream svgStream = new ByteArrayInputStream(values.getFirst("svg_xml").getBytes("utf-8"));
      TranscoderInput input = new TranscoderInput(svgStream);

      PNGTranscoder transcoder = new PNGTranscoder();
      // Setup output
      ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      TranscoderOutput output = new TranscoderOutput(outStream);

      // Do the transformation
      transcoder.transcode(input, output);
      final byte[] result = outStream.toByteArray();
      repositoryService.addModelEditorSourceExtra(model.getId(), result); //插入模型的svg图片

      outStream.close();


    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.error("Error saving model", e);
      throw new ActivitiException("Error saving model", e);
    }
  }
}
