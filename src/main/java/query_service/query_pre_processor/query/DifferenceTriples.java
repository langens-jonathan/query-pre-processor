package query_service.query_pre_processor.query;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by langens-jonathan on 31.05.16.
 *
 * This is a data structure
 *
 * The class difference triples holds a set of all triples that will
 * be updated and one set for all triples that will be deleted on a
 * certain data set.
 */
public class DifferenceTriples
{
    // a set with all triples that will be updated in the store
    private Set<Triple> insertTriples;

    // a set with all triples that will be deleted in the store
    private Set<Triple> deleteTriples;

    /**
     * default constructor
     */
    public DifferenceTriples()
    {
        this.deleteTriples = new HashSet<Triple>();
        this.insertTriples = new HashSet<Triple>();
    }

    /**
     * adds the given update triple to the set of update triples
     * @param triple
     */
    public void addInsertTriple(Triple triple)
    {
        this.insertTriples.add(triple);
    }

    /**
     * adds the given delete triple to the set of delete triples
     * @param triple
     */
    public void addDeleteTripel(Triple triple)
    {
        this.deleteTriples.add(triple);
    }

    /**
     * @return the set of update triples
     */
    public Set<Triple> getInsertTriples()
    {
        return this.insertTriples;
    }

    /**
     * @return the set of delete triples
     */
    public Set<Triple> getDeleteTriples()
    {
        return this.deleteTriples;
    }

    public void setDeleteTriples(Set<Triple> deleteTriples){this.deleteTriples = deleteTriples;}

    public void setInsertTriples(Set<Triple> insertTriples){this.insertTriples = insertTriples;}
}
