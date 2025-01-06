#ACR Login

export ADAL_PYTHON_SSL_NO_VERIFY=1
export AZURE_CLI_DISABLE_CONNECTION_VERIFICATION=1

az acr login --name luluorgacr


#Stop & Remove Current Image
docker stop redcurrant-platform
docker rm redcurrant-platform

docker rmi luluorgacr.azurecr.io/redcurrent-platform:stg --force


#Run Latest Image
docker pull luluorgacr.azurecr.io/redcurrent-platform:stg

docker run --name redcurrant-platform -d -p 15511:8080  luluorgacr.azurecr.io/redcurrent-platform:stg