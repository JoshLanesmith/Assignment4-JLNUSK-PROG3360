INSERT_API_KEY=user:7c794a771bd4296b0d9b623d9a2246943de23ae78e3eff83c1c1da26

curl --location --request POST 'http://localhost:4242/api/admin/projects/default/features' \
    --header 'Authorization: $INSERT_API_KEY' \
    --header 'Content-Type: application/json' \
    --data-raw '{
  "type": "release",
  "name": "premium-pricing",
  "description": "",
  "impressionData": false
}'

curl --location --request POST 'http://localhost:4242/api/admin/projects/default/features' \
    --header 'Authorization: $INSERT_API_KEY' \
    --header 'Content-Type: application/json' \
    --data-raw '{
  "type": "release",
  "name": "order-notifications",
  "description": "",
  "impressionData": false
}'

curl --location --request POST 'http://localhost:4242/api/admin/projects/default/features' \
    --header 'Authorization: $INSERT_API_KEY' \
    --header 'Content-Type: application/json' \
    --data-raw '{
  "type": "release",
  "name": "bulk-order-discount",
  "description": "",
  "impressionData": false
}'