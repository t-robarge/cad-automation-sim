# CAD/PLM Rules Automation Simulator (Java/Spring + Drools)

## Quickstart
1) Install JDK 21, Docker, Maven
2) Build API:
   ```bash
   cd backend
   mvn -q package
   ```
3) Run stack:
   ```bash
   cd ../ops
   docker-compose up --build
   ```
4) Swagger UI: http://localhost:8081/swagger-ui.html

## Demo
- Run automation on seed part (id=1):
  ```bash
  curl -s -X POST http://localhost:8081/api/automation/run/1 | jq '.annotations | length, .topSvg | length'
  ```
- Create a new part:
  ```bash
  curl -s -X POST http://localhost:8081/api/parts     -H 'Content-Type: application/json'     -d '{
      "partNumber":"P-2001","name":"Mounting Plate","unit":"MM",
      "features":[
        {"type":"HOLE","x":25,"y":30,"d1":6,"d2":0},
        {"type":"FACE","x":10,"y":10,"d1":100,"d2":50}
      ]
    }' | jq .id
  ```
  Then run automation on the returned id and preview SVG in your browser by saving the `topSvg` string to a `.svg` file.

## Notes
- Uses RAM Postgres via Docker compose; adjust ports if 5433 is busy.
- Lombok reduces boilerplate; enable annotation processing in IDE.
