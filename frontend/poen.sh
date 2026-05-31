npx openapi-generator-cli generate -i http://localhost:8080/v3/api-docs -g typescript-angular -o ../frontend/lib/src/ --additional-properties=providedInRoot=true --remove-operation-id-prefix
