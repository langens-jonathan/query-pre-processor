package query_service.query_pre_processor.web;

import query_service.query_pre_processor.query.DifferenceTriples;
import query_service.query_pre_processor.query.QueryService;
import query_service.query_pre_processor.query.Triple;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class RootController {

  @Inject
  private QueryService queryService;

  @RequestMapping("/test")
  public ResponseEntity<String> getTestObject(){
    return new ResponseEntity<String>("CHECK", HttpStatus.OK);
  }

  @RequestMapping(value = "/query")
  public ResponseEntity<String> preProcessQuery(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String body)
  {
    DifferenceTriples diff = this.queryService.getDifferenceTriples(body);

    String resp = "";

    resp += "new INSERTS:\n";

    for(Triple t : diff.getInsertTriples())
    {
      resp += "    " + "<" + t.getSubject() + "> <" + t.getPredicate() + "> ";
      resp += "\"" + t.getObjectString() + "\"";
      if(t.getObjectType() != null) resp += "^^" + t.getObjectType();
      resp += "\n";
    }

    resp += "new DELETES:\n";

    for(Triple t : diff.getDeleteTriples())
    {
      resp += "    " + "<" + t.getSubject() + "> <" + t.getPredicate() + "> ";
      resp += "\"" + t.getObjectString() + "\"";
      if(t.getObjectType() != null) resp += "^^" + t.getObjectType();
      resp += "\n";
    }

    return ResponseEntity.ok(resp);
  }
}
