package query_service.query_pre_processor.query;

import org.openrdf.model.Value;

/**
 * Created by langens-jonathan on 31.05.16.
 *
 * This is a datastructure representing a triple. It consists of 3 private string
 * objects. All other code is autogenerated with exception of the setObject function
 * that also sets the type and value.
 */
public class Triple
{
    private String subject;
    private String predicate;
    private Value object = null;

    private String objectString;
    private String objectType;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Value getObject() {
        return object;
    }

    public void setObject(Value object) {
        this.object = object;
        this.objectString = object.stringValue();
        this.objectType = object.toString().substring(0, this.objectString.length());
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public void setObjectString(String objectString){this.objectString = objectString;}

    public String getObjectString() {return this.objectString;}

    public void setObjectType(String objectType) { this.objectType = objectType;}

    public String getObjectType() {return this.objectType;}
}
