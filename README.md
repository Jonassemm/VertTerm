# VertTerm
DV-Projekt Verteilte- Termin und Ressourcenplanung

#Prerequisites
MongoDb,
Maven,
JavaSDK 11, 
recommended: an IDE like IntelliJ or Eclipse

The needed structures for the MongoDB are self-initializing.

# Compile and run backend
To compile and run the program, run "mvn spring-boot:run".

#Run backend
If a new compile is unnecessary, the application can be started with "mvn spring.boot:start".

# Run only frontend
/ReactApp> npm start

# Test frontend with backend
Run frontend: /ReactApp> npm start

FOR BETTER PERFORMANCE:
    create a copy of /pom.xml -> /pom_old.xml
    delete in <build> the whole <execution> with the <id>: "npm install" and "webpack-build"

Run backend: /> mvn spring-boot:run

After Testing:
    DON'T FORGET TO UNDO YOUR CHANGE AT POM!!!


