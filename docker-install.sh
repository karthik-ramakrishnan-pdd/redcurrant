export env=uat

#ACR Login

export ADAL_PYTHON_SSL_NO_VERIFY=1
export AZURE_CLI_DISABLE_CONNECTION_VERIFICATION=1

az acr login --name luluorgacr


#Stop & Remove Current Image
docker stop redcurrant-platform
docker rm redcurrant-platform

docker rmi luluorgacr.azurecr.io/redcurrant-platform:$env --force


#Run Latest Image
docker pull luluorgacr.azurecr.io/redcurrant-platform:$env

docker run --name redcurrant-platform -d -p 8080:15511  luluorgacr.azurecr.io/redcurrant-platform:$env  --platform linux/amd64

# docker buildx build  --platform linux/amd64 -t redcurrant-platform:$env   .