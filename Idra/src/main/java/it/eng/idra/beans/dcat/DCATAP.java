package it.eng.idra.beans.dcat;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class DCATAP {

    private static final Model m = ModelFactory.createDefaultModel();

    public static final String NS = "http://data.europa.eu/r5r/";
    public static final Resource NAMESPACE = m.createResource(NS);
    
    public static String getURI() { return NS; }

    public static final Property applicableLegislation = m.createProperty(NS + "applicableLegislation");
    public static final Property availability = m.createProperty(NS + "availability");
    public static final Property hvdCategory = m.createProperty(NS + "hvdCategory");

}
