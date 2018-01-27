HOST=${HOST:-http://localhost:8080}

API="/iot/fleet"
DATA='{ "name": "my fleet 1", "id": "773870b3-f8cd-4fc9-988e-c1c0f081f6a7" }'
curl -k -H "Content-Type: application/json" -X POST -d "$DATA" "$HOST$API"
echo ""

API="/iot/location"
DATA='{ "name": "naviland", "id": "2ddc00b2-5425-47a8-bb87-911942c49886", "fleet": "773870b3-f8cd-4fc9-988e-c1c0f081f6a7", "latitude": 0.1, "longitude": 0.2 }'
curl -k -H "Content-Type: application/json" -X POST -d "$DATA" "$HOST$API"
echo ""

API="/iot/location"
DATA='{ "name": "son of naviland", "id": "3ddc00b2-5425-47a8-bb87-911942c49881", "fleet": "773870b3-f8cd-4fc9-988e-c1c0f081f6a7", "latitude": 0.2, "longitude": 0.3 }'
curl -k -H "Content-Type: application/json" -X POST -d "$DATA" "$HOST$API"
echo ""

API="/iot/device"
DATA='{ "name": "my device 1", "id": "814a41d9-6778-4b14-bac6-2dec2d5f4085", "location": "2ddc00b2-5425-47a8-bb87-911942c49886", "desc": "important gadget" }'
curl -k -H "Content-Type: application/json" -X POST -d "$DATA" "$HOST$API"
echo ""

API="/iot/device"
DATA='{ "name": "my device 2", "id": "479d2b95-0b62-41ff-8c79-f1d13442a687", "kind": 99, "location": "2ddc00b2-5425-47a8-bb87-911942c49886", "desc": "another important gadget" }'
curl -k -H "Content-Type: application/json" -X POST -d "$DATA" "$HOST$API"
echo ""

