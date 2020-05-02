# VertTerm
DV-Projekt Verteilte- Termin und Ressourcenplanung

# Run only frontend
/ReactApp> npm start


# Test frontend with backend
change port in ReactApp/webpack.config.js from 8080 to 8081

Run frontend: /ReactApp> npm start

FOR BETTER PERFORMANCE:
    create a copy of /pom.xml -> /pom_old.xml
    delete in <build> the whole <execution> with the <id>: "npm install" and "webpack-build"

Run backend: /> mvn spring-boot:run

After Testing:
    DON'T FORGET TO UNDO YOUR CHANGE!!!


