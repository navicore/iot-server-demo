HOST=${HOST:-http://localhost:8080}

echo "["
curl -k -H "Content-Type: application/json" $HOST$API/iot/fleet/773870b3-f8cd-4fc9-988e-c1c0f081f6a7
echo ","
curl -k -H "Content-Type: application/json" $HOST$API/iot/fleet/773870b3-f8cd-4fc9-988e-c1c0f081f6a7/locations
echo ","
curl -k -H "Content-Type: application/json" $HOST$API/iot/location/2ddc00b2-5425-47a8-bb87-911942c49886
echo ","
curl -k -H "Content-Type: application/json" $HOST$API/iot/location/2ddc00b2-5425-47a8-bb87-911942c49886/devices
echo ","
curl -k -H "Content-Type: application/json" $HOST$API/iot/device/479d2b95-0b62-41ff-8c79-f1d13442a687
echo "]"
