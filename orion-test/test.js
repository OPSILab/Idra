var fs = require('fs');
var should = require('should')
var assert = require('assert');
var request = require('supertest');

let room = JSON.parse(fs.readFileSync('room_entity.json'));
let orion_catalogue = JSON.parse(fs.readFileSync('orion_catalogue.json'));
let orionBaseURL='http://localhost:1026'
let idraBaseUrl='http://localhost:8080'

let adminToken=null;
let orionCatalogueID=null;
let datasetId = null;
let accessUrl = null;

describe('Create Orion Entity', function() {
    it("should return 201 status code", function(done){
       request(`${orionBaseURL}`)
        .post('/v2/entities')
        .set('Content-Type','application/json')
        .send(room)
        .expect(201)
        .end(function(err,res){
            if(err) done(err);
            else done()
        })
    })
});

describe('IDRA ', function() {
    it("IDRA - LOGIN should return administrator token", function(done){
       request(`${idraBaseUrl}`)
        .post('/Idra/api/v1/administration/login')
        .set('Content-Type','application/json')
        .send({"username":"admin","password":"21232f297a57a5a743894a0e4a801fc3"})
        .expect(200)
        .end(function(err,res){
            if(err) done(err);
            else{
              adminToken = res.text  
              done()
            } 
        })
    })

    it("IDRA - should create a Catalogue", function(done){

        request(`${idraBaseUrl}`)
        .post('/Idra/api/v1/administration/catalogues')
        .set('Content-Type','multipart/form-data;')
        .set('Authorization', 'Bearer '+adminToken)
        .field("node",JSON.stringify(orion_catalogue))
        .expect(200)
        .end(function(err,res){
            if(err) done(err);
            else  done() 
        })
    })

    it("IDRA - should read all of the Catalogues", function(done){
        
        request(`${idraBaseUrl}`)
        .get('/Idra/api/v1/administration/catalogues?withImage=false')
        .set('Authorization', 'Bearer '+adminToken)
        .expect(200)
        .end(function(err,res){
            if(err) done(err);
            else {
                assert.ok(res.body.length==1,"Catalogues length = 1")
                orionCatalogueID = res.body[0].id
                done()
            }  
        })
    })

    it("IDRA - should read the Orion Catalogue", function(done){
        
        request(`${idraBaseUrl}`)
        .get(`/Idra/api/v1/administration/catalogues/${orionCatalogueID}?withImage=false`)
        .set('Authorization', 'Bearer '+adminToken)
        .expect(200)
        .end(function(err,res){
            if(err) done(err);
            else {
                assert.ok(res.body.nodeType == 'ORION',"Catalogue Type = ORION")
                assert.ok(res.body.host,orionBaseURL == orionBaseURL, "HOST = "+orionBaseURL)
                assert.ok(res.body.datasetCount==1,"DatasetCount = 1")
                done()
            }  
        })
    })

    it("IDRA - should retrieve the Orion Catalogue's datasets", function(done){
        
        request(`${idraBaseUrl}`)
        .get(`/Idra/api/v1/client/catalogues/${orionCatalogueID}/datasets`)
        .expect(200)
        .end(function(err,res){
            if(err) done(err);
            else {
                assert.ok(res.body.count==1,"Count = 1")
                assert.ok(res.body.results.length==1,"Results lenght = 1")
                datasetId = res.body.results[0].id;
                done()
            }  
        })
    })

    it("IDRA - should retrieve the dataset by ID", function(done){
        
        request(`${idraBaseUrl}`)
        .get(`/Idra/api/v1/client/catalogues/${orionCatalogueID}/datasets/${datasetId}`)
        .expect(200)
        .end(function(err,res){
            if(err) done(err);
            else {
                assert.ok(res.body.id == datasetId, "Dataset id returned ok")
                assert.ok(res.body.nodeID == orionCatalogueID, "Catalogue id returned ok")
                assert.ok(res.body.distributions.length==1, "Distribution Length = 1")
                let distribution = res.body.distributions[0]
                assert.ok(distribution.format == 'fiware-ngsi','Distribution Format = fiware-ngsi')
                assert.ok(distribution.accessURL == `${orionBaseURL}/v2/entities?type=${room.type}`,'Access URL ok')
                accessUrl = distribution.accessURL
                done()
            }  
        })
    })

    it("ORION - should retrieve the entites from distribution's accessURL", function(done){
        request('')
        .get(accessUrl)
        .expect(200)
        .end(function(err,res){
            if(err) done(err);
            else done()  
        })
    })

    it("IDRA - should delete the Orion Catalogue", function(done){
        
        request(`${idraBaseUrl}`)
        .delete(`/Idra/api/v1/administration/catalogues/${orionCatalogueID}`)
        .set('Authorization', 'Bearer '+adminToken)
        .expect(200)
        .end(function(err,res){
            if(err) done(err);
            else done()  
        })
    })

});