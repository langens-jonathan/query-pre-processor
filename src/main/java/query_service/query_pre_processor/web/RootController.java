package query_service.query_pre_processor.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenforce.semtech.SPARQLParser.SPARQL.InvalidSPARQLException;
import com.tenforce.semtech.SPARQLParser.SPARQL.SPARQLQuery;
import query_service.query_pre_processor.callback.CallBack;
import query_service.query_pre_processor.callback.CallBackService;
import query_service.query_pre_processor.callback.CallBackSetNotFoundException;
import query_service.query_pre_processor.query.DifferenceTriples;
import query_service.query_pre_processor.query.QueryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
public class RootController {

  @Inject
  private QueryService queryService;

  @Inject
  private CallBackService callBackService;

  /**
   * initializes the callback service with 2 call back sets (allDifferences and effectiveDifferences)
   */
  @PostConstruct
  public void init()
  {
    this.callBackService.addCallBackSet("allDifferences");
    this.callBackService.addCallBackSet("effectiveDifferences");
  }

  /**
   * Auto wired web entry point
   *
   * expects a body in the form
   * {
   *     "callback":"<CALLBACKLOCATION>"
   * }
   *
   * a Call Back object with this location is instantiated and added to the all differences set
   * @param request
   * @param response
   * @param body
     * @return
     */
  @RequestMapping(value = "/registerForAllDifferences")
  public ResponseEntity<String> registerAD(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String body)
  {
    Map<String, Object> jsonMap;
    try {
      ObjectMapper mapper = new ObjectMapper();
      jsonMap = mapper.readValue(body, Map.class);
      String callbackString = (String)jsonMap.get("callback");
      CallBack callback = new CallBack();
      callback.setUrl(callbackString);
      this.callBackService.addCallBack("allDifferences", callback);
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    catch(CallBackSetNotFoundException cb)
    {
      cb.printStackTrace();
    }

    return new ResponseEntity<String>("OK", HttpStatus.OK);
  }

  /**
   * Auto wired web entry point
   *
   * expects a body in the form
   * {
   *     "callback":"<CALLBACKLOCATION>"
   * }
   *
   * a Call Back object with this location is instantiated and added to the effective differences set
   * @param request
   * @param response
   * @param body
   * @return
   */
  @RequestMapping(value = "/registerForEffectiveDifferences")
  public ResponseEntity<String> registerED(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String body)
  {
    Map<String, Object> jsonMap;
    try {
      ObjectMapper mapper = new ObjectMapper();
      jsonMap = mapper.readValue(body, Map.class);
      String callbackString = (String)jsonMap.get("callback");
      CallBack callback = new CallBack();
      callback.setUrl(callbackString);
      this.callBackService.addCallBack("effectiveDifferences", callback);
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    catch(CallBackSetNotFoundException cb)
    {
      cb.printStackTrace();
    }

    return new ResponseEntity<String>("OK", HttpStatus.OK);
  }



  @RequestMapping(value = "/sparql")
  public ResponseEntity<String> preProcessQuery(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) String body) throws InvalidSPARQLException
  {
    try {
        String queryString;

        if (request.getParameterMap().containsKey("query")) {
            queryString = request.getParameter("query");
            try {
                queryString = URLDecoder.decode(queryString, "UTF-8");
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            queryString = URLDecoder.decode(body, "UTF-8");
            if(queryString.toLowerCase().startsWith("update="))
            {
                queryString = queryString.substring(7, queryString.length());
            }
            if(queryString.toLowerCase().startsWith("query="))
            {
                queryString = queryString.substring(6, queryString.length());
            }
        }
        SPARQLQuery parsedQuery = new SPARQLQuery(queryString);

        if(parsedQuery.getType().equals(SPARQLQuery.Type.UPDATE)) {
            Map<String, DifferenceTriples> diff = this.queryService.getDifferenceTriples(parsedQuery);

            //String resp = diff.getAllChangesAsJSON();

            String allJson = "[";//diff.getAllChangesAsJSON();
            String effectiveJson = "[";// diff.getEffectiveChangesAsJSON();

            for(String g : diff.keySet())
            {
                allJson += "{\"graph\":\"" + g + "\",\"delta\":" + diff.get(g).getAllChangesAsJSON() + "},";
                effectiveJson += "{\"graph\":\"" + g + "\",\"delta\":" + diff.get(g).getEffectiveChangesAsJSON() + "},";
            }

            if(!diff.keySet().isEmpty())
            {
                allJson = allJson.substring(0, allJson.length() - 1);
                effectiveJson = effectiveJson.substring(0, effectiveJson.length() - 1);
            }

            allJson += "]";
            effectiveJson += "]";

            try {
                this.callBackService.notifyCallBacks("effectiveDifferences", effectiveJson);
                this.callBackService.notifyCallBacks("allDifferences", allJson);
            } catch (CallBackSetNotFoundException cb) {
                cb.printStackTrace();
            }
        }
        String url = "http://localhost:8890/sparql";

        Map<String, String> headers = new HashMap<String, String>();
        Enumeration<String> henum = request.getHeaderNames();
        while(henum.hasMoreElements())
        {
            String headerName = henum.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }

        if(parsedQuery.getType().equals(SPARQLQuery.Type.UPDATE)) {
            String qrp = this.queryService.sparqlService.postSPARQLResponse(url, queryString, headers);
            return new ResponseEntity<String>(qrp , HttpStatus.OK);
        }
        else
        {
            String qrp = this.queryService.sparqlService.getSPARQLResponse(url + "?query=" + URLEncoder.encode(queryString, "UTF-8"), headers);
            return new ResponseEntity<String>(qrp, HttpStatus.OK);
        }

    }catch(InvalidSPARQLException e)
    {
      e.printStackTrace();
    } catch (MalformedURLException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }

      return ResponseEntity.ok("");
  }


}
