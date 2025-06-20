package it.eng.idra.beans.dcat;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

public class ELI {
    
        private static final Model m = ModelFactory.createDefaultModel();
    
    public static final String NS = "http://data.europa.eu/eli/ontology#";
    public static final Resource NAMESPACE = m.createResource(NS);

    public static String getURI() { return NS; }

    // Class: eli:LegalResource
    public static final Resource LegalResource = m.createResource(NS + "LegalResource");
}
