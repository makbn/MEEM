### Data Vertex CSV


| index  | name | type | description |
| -------| ---- | ---- | ----------- |
| 0 | id | number (int) | id of city on map |
| 1 | lat | number (double) | latitude of city |
| 2 | lon | number (double) | longitude of city |
| 3 | date | string (date ex.: 2017-06-14) | date of parcel delivery |
| 4 | time | string (time ex.: 18:11:34.577) | time of parcel delivery |
| 5 | postManId | number (int) | id of the postman | 
| 6 | pCity | string | persian name of the city |
| 7 | delivered | boolean | parcel delivered to the dest or just moving in the middle nodes |
| 8 | cityId | number (int) | id of the city in city.json file |
| 9 | pState | string | Persian name of the state |
| 10 | eState | string | English name of the state |
| 11 | stateId | number (int) | id of state in city.json file |
| 12 | eCity | string | English name of the city |


### Data Edges CSV

| index  | name | type | description |
| -------| ---- | ---- | ----------- |
| 0 | src | number (int) | id of the parcel's source city on map |
| 1 | dst | number (int) | id of the parcel's destination city on map |
| 2 | w | number (int) | count of parcels between two cities |
| 3 | isDirected | boolean | if the path between these cities are directed on the map or not |


