LOCK TABLES `configuration` WRITE;
/*!40000 ALTER TABLE `configuration` DISABLE KEYS */;
INSERT INTO `configuration` VALUES (1,'refresh_period','3600'),(2,'rdf_max_dimension','10'),(3,'rdf_undefined_content_length','true'),(4,'rdf_undefined_dimension','true');
/*!40000 ALTER TABLE `configuration` ENABLE KEYS */;
UNLOCK TABLES;


LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin@gmail.com','Admin',NULL,'Admin','21232f297a57a5a743894a0e4a801fc3','2015-10-16','admin');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
